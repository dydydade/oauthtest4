package login.oauthtest4.global.auth.dto;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    // ApiResponse 생성자
    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    // 성공 응답을 생성하는 메소드
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "SUCCESS");
    }

    // 성공 응답을 생성하는 메소드, 데이터 없음
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, "SUCCESS");
    }

    // 실패 응답을 생성하는 메소드
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, null, message);
    }

    // Getter 메소드
    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    // Setter 메소드 (필요한 경우)
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}