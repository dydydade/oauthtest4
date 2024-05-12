package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BaseUserSignUpRequest {

    @Schema(description = "회원가입 요청 이메일")
    @NotBlank(message = "요청에는 이메일을 반드시 포함하여야 합니다.")
    @Email(message = "올바른 형식의 이메일을 입력해 주세요.")
    private String email;    // 유저가 입력한 email

    @Schema(description = "회원가입 요청 닉네임")
    private String nickname;

    @Schema(description = "약관 동의 정보")
    @NotNull(message = "요청에는 약관 동의 정보를 반드시 포함하여야 합니다.")
    private UserSignUpTermsAgreementDto termsAgreementDto;
}
