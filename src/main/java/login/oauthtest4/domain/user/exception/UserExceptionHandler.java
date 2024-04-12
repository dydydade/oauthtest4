package login.oauthtest4.domain.user.exception;

import login.oauthtest4.global.auth.verification.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(AlreadySignedUpUserException.class)
    public ResponseEntity<Object> handleAlreadySignedUpUserException(AlreadySignedUpUserException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(RegisteredUserNotFoundException.class)
    public ResponseEntity<Object> handleRegisteredUserNotFoundException(RegisteredUserNotFoundException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.failure(ex.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(UnauthorizedAccountAttemptException.class)
    public ResponseEntity<Object> handleUnauthorizedAccountAttemptException(UnauthorizedAccountAttemptException ex) {
        ApiResponse<Object> apiResponse = ApiResponse.error(ex.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.append(error.getDefaultMessage()).append("; ");
        }
        ApiResponse<Object> apiResponse = ApiResponse.failure(errors.toString().replaceAll(";\\s*$", ""));
        return ResponseEntity.status(BAD_REQUEST).body(apiResponse);
    }
}
