package login.tikichat.global.auth.jwt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.tikichat.global.exception.filter.MissingDeviceIdException;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String BEARER = "Bearer ";
    private static final String DEVICE_ID_HEADER_KEY = "Device-ID";

    private final ObjectMapper objectMapper;


    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return extractToken(request, accessHeader);
    }

    /**
     * 헤더에서 RefreshToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return extractToken(request, refreshHeader);
    }

    public Optional<String> extractToken(HttpServletRequest request, String header) {
        return Optional.ofNullable(request.getHeader(header))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    /**
     * AccessToken 헤더에 실어서 보내기
     */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, BEARER + accessToken);
        log.debug("재발급된 Access Token : {}", accessToken);
    }

    /**
     * AccessToken + RefreshToken 헤더에 실어서 보내기
     */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken, ResultResponse resultResponse) throws IOException {
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(resultResponse.getStatus());
        response.getWriter().write(objectMapper.writeValueAsString(resultResponse));
        log.debug("Access Token, Refresh Token 헤더 응답 완료");
    }

    /**
     * AccessToken + RefreshToken 헤더에 싣기
     */
    public void setAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken, ResultResponse resultResponse) throws IOException {
        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(resultResponse.getStatus());
        log.debug("Access Token, Refresh Token 헤더 설정 완료");
    }

    /**
     * AccessToken 헤더 설정
     */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, BEARER + accessToken);
    }

    /**
     * RefreshToken 헤더 설정
     */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, BEARER + refreshToken);
    }

    /**
     * request 헤더에서 Device-Id 를 찾아서 반환하는 메서드
     * 없으면 MissingDeviceIdException 예외를 던짐
     * @param request
     * @return
     */
    public String extractDeviceIdFromHeader(HttpServletRequest request) {

        String deviceId = request.getHeader(DEVICE_ID_HEADER_KEY);

        if (deviceId == null) {
            throw new MissingDeviceIdException();
        }

        return deviceId;
    }
}
