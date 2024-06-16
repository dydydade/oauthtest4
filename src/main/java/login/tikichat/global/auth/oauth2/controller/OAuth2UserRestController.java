package login.tikichat.global.auth.oauth2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.tikichat.domain.user.service.UserRefreshTokenService;
import login.tikichat.global.auth.jwt.service.JwtService;
import login.tikichat.global.auth.jwt.util.JwtUtils;
import login.tikichat.global.auth.oauth2.dto.OAuth2UserDto;
import login.tikichat.global.auth.oauth2.dto.SocialLoginRequest;
import login.tikichat.global.auth.oauth2.dto.UserDto;
import login.tikichat.global.auth.oauth2.service.CustomOAuth2UserService;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@Tag(name = "Social Login API", description = "소셜 로그인 API")
@RestController
@RequiredArgsConstructor
public class OAuth2UserRestController {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final JwtService jwtService;

    private final JwtUtils jwtUtils;

    private final UserRefreshTokenService userRefreshTokenService;
    private static final String DEVICE_ID_HEADER_KEY = "Device-ID";
    private static final String STATE = "yongchan-1234";


    /**
     * [네이버/카카오/구글 소셜 로그인 api] 클라이언트로부터 OAuth2 AccessToken 전달받아
     * 소셜 사용자 정보 조회 후, 응답으로 JWT AccessToken, RefreshToken 반환
     * @param registrationId
     * @param socialLoginRequest
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
    @PostMapping("/api/v1/auth/social/login/{registrationId}")
    public ResponseEntity<Object> socialLogin(@PathVariable String registrationId, @RequestBody SocialLoginRequest socialLoginRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String deviceId = jwtUtils.extractDeviceIdFromHeader(request);

        OAuth2UserDto oAuth2UserDto = customOAuth2UserService.socialLogin(registrationId, socialLoginRequest.getOauth2AccessToken());
        UserDto user = oAuth2UserDto.getUser();

        if (user == null) {
            ResultResponse result = ResultResponse.of(ResultCode.SOCIAL_EMAIL_NOT_REGISTERED, oAuth2UserDto);
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
        }

        ResultResponse result = ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, oAuth2UserDto);

        String accessToken = jwtService.createAccessToken(oAuth2UserDto.getUser().getEmail());
        String refreshToken = jwtService.createRefreshToken();

        jwtUtils.setAccessAndRefreshToken(response, accessToken, refreshToken, result);
        userRefreshTokenService.findAndUpdateUserRefreshToken(oAuth2UserDto.getUser().getEmail(), deviceId, refreshToken);

        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }


    /**
     * 추후 소셜 로그인 추가 연동 시 작성
     */
//    @PostMapping("/api/v1/auth/social/link/{registrationId}")
//    public ResponseEntity<Object> linkSocialProfile(@PathVariable String registrationId, @RequestParam String oauth2AccessToken, HttpServletRequest request, HttpServletResponse response) throws IOException {
//
//        String deviceId = jwtUtils.extractDeviceIdFromHeader(request);
//
//        OAuth2UserDto oAuth2UserDto = customOAuth2UserService.socialLogin(registrationId, oauth2AccessToken);
//        UserDto user = oAuth2UserDto.getUser();
//
//        if (!oAuth2UserDto.isUserExists()) {
//            ResultResponse result = ResultResponse.of(ResultCode.SOCIAL_EMAIL_NOT_REGISTERED, oAuth2UserDto);
//            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
//        }
//
//        ResultResponse result = ResultResponse.of(ResultCode.SOCIAL_LOGIN_SUCCESS, oAuth2UserDto);
//
//        String accessToken = jwtService.createAccessToken(oAuth2UserDto.getUser().getEmail());
//        String refreshToken = jwtService.createRefreshToken();
//
//        jwtUtils.setAccessAndRefreshToken(response, accessToken, refreshToken, result);
//        userRefreshTokenService.findAndUpdateUserRefreshToken(oAuth2UserDto.getUser().getEmail(), deviceId, refreshToken);
//
//        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
//    }
}
