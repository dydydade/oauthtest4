package login.tikichat.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindUserResponse {

    @Schema(description = "회원을 조회한 이메일", example = "dydydade@gmail.com")
    private String email;

    @Schema(description = "비밀번호 존재 여부(비밀번호 없을 경우 소셜 ONLY 계정)", example = "true")
    private boolean passwordExists;
}
