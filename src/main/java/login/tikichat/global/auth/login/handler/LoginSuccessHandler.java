package login.tikichat.global.auth.login.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.tikichat.domain.user.service.UserRefreshTokenService;
import login.tikichat.global.auth.jwt.util.JwtUtils;
import login.tikichat.global.auth.login.dto.LoginSuccessResponse;
import login.tikichat.global.exception.filter.MissingDeviceIdException;
import login.tikichat.global.auth.jwt.service.JwtService;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

import static login.tikichat.global.response.ResultCode.LOGIN_SUCCESS;


@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final UserRefreshTokenService userRefreshTokenService;
    private static final String DEVICE_ID_HEADER_KEY = "Device-ID";

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String deviceId = request.getHeader(DEVICE_ID_HEADER_KEY);

        if (deviceId == null) {
            throw new MissingDeviceIdException();
        }

        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출
        String accessToken = jwtService.createAccessToken(email); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        LoginSuccessResponse loginSuccessResponse = LoginSuccessResponse.builder()
                .email(email)
                .build();

        final ResultResponse resultResponse = ResultResponse.of(LOGIN_SUCCESS, loginSuccessResponse);

        jwtUtils.sendAccessAndRefreshToken(response, accessToken, refreshToken, resultResponse);

        userRefreshTokenService.findAndUpdateUserRefreshToken(email, deviceId, refreshToken);


        log.debug("로그인에 성공하였습니다. 이메일 : {}", email);
        log.debug("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.debug("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
