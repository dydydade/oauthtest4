package login.tikichat.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpResponse {

    @Schema(description = "가입이 승인된 회원 ID")
    private Long userId;

    @Schema(description = "가입이 승인된 회원 이메일")
    private String email;    // 유저가 입력한 email

    @Schema(description = "가입이 승인된 회원 닉네임")
    private String nickname;
}
