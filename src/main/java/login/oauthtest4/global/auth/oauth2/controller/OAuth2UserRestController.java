package login.oauthtest4.global.auth.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.oauthtest4.domain.user.service.UserRefreshTokenService;
import login.oauthtest4.global.auth.jwt.service.JwtService;
import login.oauthtest4.global.auth.oauth2.dto.OAuth2UserDto;
import login.oauthtest4.global.auth.oauth2.dto.UserDto;
import login.oauthtest4.global.auth.oauth2.service.CustomOAuth2UserService;
import login.oauthtest4.global.response.ResultCode;
import login.oauthtest4.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;


@Tag(name = "Social Login API", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
public class OAuth2UserRestController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtService jwtService;

    private final UserRefreshTokenService userRefreshTokenService;
    private static final String DEVICE_ID_HEADER_KEY = "Device-ID";
    private static final String STATE = "yongchan-1234";

    /**
     * [네이버/카카오/구글 소셜 로그인 api] 클라이언트로부터 OAuth2 AccessToken 전달받아
     * 소셜 사용자 정보 조회 후, 응답으로 JWT AccessToken, RefreshToken 반환
     * @param registrationId
     * @param body
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @Operation(summary = "소셜 로그인", description = "네이버, 카카오, 구글 소셜 로그인 api 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 로그인을 완료하였습니다..",
                    content = {@Content(schema = @Schema(implementation = OAuth2UserDto.class))}
            ),
            @ApiResponse(responseCode = "303", description = "기존에 회원으로 존재하지 않는 소셜 이메일입니다. 소셜 회원가입을 진행해 주세요."),
            @ApiResponse(responseCode = "400", description = "요청 헤더에 Device-ID 정보가 포함되지 않았습니다.\t\n소셜 계정은 일반 로그인을 할 수 없습니다.\t\n이메일 또는 비밀번호를 다시 확인해주세요.")
    })
    @GetMapping("/api/v1/auth/social/login/{registrationId}")
    public ResultResponse socialLogin(@PathVariable String registrationId, @RequestBody Map<String, Object> body, HttpServletRequest request, HttpServletResponse response) {

        String oauth2AccessTokenStr = (String) body.get("oauth2AccessTokenStr");

        OAuth2UserDto oAuth2UserDto = customOAuth2UserService.socialLogin(registrationId, oauth2AccessTokenStr);

        UserDto user = oAuth2UserDto.getUser();

        if (user == null) {
            return ResultResponse.of(ResultCode.SOCIAL_EMAIL_NOT_REGISTERED, oAuth2UserDto);
        }

        loginSuccess(request, response, oAuth2UserDto);

        return ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, oAuth2UserDto);
    }

    /**
     * [테스트용 임시 메서드] 클라이언트에서 AccessToken 보내주지 않아도 테스트할 수 있도록
     * 백엔드에서 AccessToken 받아오는 메서드
     * @param registrationId
     * @param response
     * @throws IOException
     */
    @GetMapping("/login/oauth2/{registrationId}")
    public void redirectToAuthorizationProvider(@PathVariable String registrationId, HttpServletResponse response) throws IOException {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        String redirectUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getAuthorizationUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", clientRegistration.getScopes()))
                .queryParam("state", STATE)
                .toUriString();

        response.sendRedirect(redirectUri);
    }

    /**
     * [테스트용 임시 메서드] 클라이언트에서 AccessToken 보내주지 않아도 테스트할 수 있도록
     * 백엔드에서 AccessToken 받아오는 메서드
     * @param registrationId
     * @param response
     * @throws IOException
     */
    @GetMapping("/login/oauth2/code/{registrationId}")
    public void test(@PathVariable String registrationId, @RequestParam String code, @RequestParam String state, HttpServletResponse response) throws IllegalAccessException, IOException {
        // state 값이 일치하는지 검증합니다.
        if (!STATE.equals(state)) {
            throw new IllegalAccessException();
        }

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        URI tokenUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getTokenUri())
                .build()
                .toUri();

        String body = UriComponentsBuilder.newInstance()
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("client_secret", clientRegistration.getClientSecret())
                .build()
                .toUriString()
                .substring(1); // Remove the leading '?' from the query string

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> result = restTemplate.exchange(
                tokenUri,
                HttpMethod.POST,
                entity,
                Map.class
        );

        String redirectUri = "/api/v1/auth/social/login/test/" + registrationId + "?oauth2AccessTokenStr=" + result.getBody().get("access_token");

        response.sendRedirect(redirectUri);
    }

    /**
     * [테스트용 임시 메서드] 클라이언트에서 AccessToken 보내주지 않아도 테스트할 수 있도록
     * 백엔드에서 AccessToken 받아오는 메서드
     * @param registrationId
     * @param response
     * @throws IOException
     */
    @GetMapping("/api/v1/auth/social/login/test/{registrationId}")
    public ResultResponse login(@PathVariable String registrationId, @RequestParam String oauth2AccessTokenStr, HttpServletRequest request, HttpServletResponse response) throws IOException {

        OAuth2UserDto oAuth2UserDto = customOAuth2UserService.socialLogin(registrationId, oauth2AccessTokenStr);

        UserDto user = oAuth2UserDto.getUser();

        if (user == null) {
            return ResultResponse.of(ResultCode.SOCIAL_EMAIL_NOT_REGISTERED, oAuth2UserDto);
        }

        loginSuccess(request, response, oAuth2UserDto);

        return ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, oAuth2UserDto);
    }


    private void loginSuccess(HttpServletRequest request, HttpServletResponse response, OAuth2UserDto oAuth2UserDto) {
        String deviceId = request.getHeader(DEVICE_ID_HEADER_KEY);

        if (deviceId == null) {
            // TODO: 삭제(테스트용)
            deviceId = "123";
//            throw new MissingDeviceIdException();
        }

        String accessToken = jwtService.createAccessToken(oAuth2UserDto.getUser().getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        userRefreshTokenService.findAndUpdateUserRefreshToken(oAuth2UserDto.getUser().getEmail(), deviceId, refreshToken);
    }
}
