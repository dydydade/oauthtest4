package login.oauthtest4.global.exception.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import login.oauthtest4.global.exception.BusinessException;
import login.oauthtest4.global.exception.ErrorResponse;
import login.oauthtest4.global.exception.user.RegisteredUserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static login.oauthtest4.global.exception.ErrorCode.*;

@RequiredArgsConstructor
public class GlobalExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidJsonWebTokenException ex) {
            final ErrorResponse errorResponse = ErrorResponse.of(INVALID_JSON_WEB_TOKEN);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (MissingDeviceIdException ex) {
            final ErrorResponse errorResponse = ErrorResponse.of(MISSING_DEVICE_ID);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (RegisteredUserNotFoundException ex) {
            final ErrorResponse errorResponse = ErrorResponse.of(REGISTERED_USER_NOT_FOUND);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (BusinessException ex) { // 비즈니스 예외 발생 시 여기서 처리
            final ErrorResponse errorResponse = ErrorResponse.of(ex.getErrorCode(), ex.getErrors());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception ex) { // 그 밖의 모든 예외 INTERNAL_SERVER_ERROR
            final ErrorResponse errorResponse = ErrorResponse.of(INTERNAL_SERVER_ERROR);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}