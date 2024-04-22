package login.oauthtest4.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserSignUpRequest {

    @Schema(description = "회원가입 요청 이메일")
    @NotBlank(message = "요청에는 이메일을 반드시 포함하여야 합니다.")
    @Email(message = "올바른 형식의 이메일을 입력해 주세요.")
    private String email;    // 유저가 입력한 email

    @Schema(description = "회원가입 요청 비밀번호")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$",
            message = "비밀번호에는 문자, 숫자, 특수 문자가 각각 1개 이상 포함되어야 합니다. 특수 문자는 !@#$%^&* 만 사용할 수 있으며, 길이는 8~20자여야 합니다.")
    @NotBlank(message = "요청에는 비밀번호를 반드시 포함하여야 합니다.")
    private String password; // 유저가 입력한 password

    @Schema(description = "회원가입 요청 닉네임")
    private String nickname;

    @Schema(description = "약관 동의 정보")
    @NotNull(message = "요청에는 약관 동의 정보를 반드시 포함하여야 합니다.")
    private UserSignUpTermsAgreementDto termsAgreementDto;
}


