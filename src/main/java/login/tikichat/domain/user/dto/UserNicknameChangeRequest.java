package login.tikichat.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNicknameChangeRequest {

    @Schema(description = "새로운 닉네임", example = "나는호두야")
    private String newNickname;
}
