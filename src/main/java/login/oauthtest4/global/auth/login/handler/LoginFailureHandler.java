package login.oauthtest4.global.auth.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.oauthtest4.global.auth.verification.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * JWT 로그인 실패 시 처리하는 핸들러
 * SimpleUrlAuthenticationFailureHandler를 상속받아서 구현
 */
@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.failure("이메일 또는 비밀번호를 다시 확인해주세요.");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        log.debug("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}
