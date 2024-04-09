package login.oauthtest4.global.auth.verification.dto;

public class ApiResponse<T> {
    private ResponseStatus status;
    private T data;
    private String message;

    // ApiResponse 생성자
    private ApiResponse(ResponseStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // 성공 응답을 생성하는 메소드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, data, "SUCCESS");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, data, message);
    }

    // 성공 응답을 생성하는 메소드, 데이터 없음
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ResponseStatus.SUCCESS, null, "SUCCESS");
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(ResponseStatus.SUCCESS, null, message);
    }

    // 실패 응답을 생성하는 메소드
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(ResponseStatus.FAIL, null, message);
    }

    // 오류 응답을 생성하는 메소드
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResponseStatus.ERROR, null, message);
    }

    public static <T> ApiResponse<T> error(T data, String message) {
        return new ApiResponse<>(ResponseStatus.ERROR, data, message);
    }

    // Getter 메소드
    public ResponseStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    // Setter 메소드 (필요한 경우)
    public void setSuccess(ResponseStatus status) {
        this.status = status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}