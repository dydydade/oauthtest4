package login.oauthtest4.global.auth.oauth2.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.oauthtest4.global.exception.ErrorResponse;
import login.oauthtest4.global.exception.user.RegisteredUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static login.oauthtest4.global.exception.ErrorCode.REGISTERED_USER_NOT_FOUND;

@RequiredArgsConstructor
public class OAuth2ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RegisteredUserNotFoundException ex) {
            final ErrorResponse errorResponse = ErrorResponse.of(REGISTERED_USER_NOT_FOUND);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
