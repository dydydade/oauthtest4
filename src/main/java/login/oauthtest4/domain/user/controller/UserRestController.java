package login.oauthtest4.domain.user.controller;

import jakarta.validation.Valid;
import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.service.UserService;
import login.oauthtest4.global.exception.user.NicknameAlreadyInUseException;
import login.oauthtest4.global.response.ResultCode;
import login.oauthtest4.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<?> findUserByEmail(@RequestParam String email) {
        FindUserResponse body = userService.findUserByEmail(email);
        ResultResponse result = ResultResponse.of(ResultCode.EMAIL_ALREADY_EXISTS, body);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * [일반 회원 가입]
     * @param userSignUpRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody @Valid UserSignUpRequest userSignUpRequest) {
        UserSignUpResponse userSignUpResponse = userService.signUp(userSignUpRequest);
        ResultResponse result = ResultResponse.of(ResultCode.NORMAL_REGISTER_SUCCESS, userSignUpResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * [소셜 회원 가입]
     * @param userSocialSignUpRequest
     * @return
     */
    @PostMapping("/signup/social")
    public ResponseEntity<ResultResponse> socialSignUp(@RequestBody @Valid UserSocialSignUpRequest userSocialSignUpRequest) {
        UserSignUpResponse userSignUpResponse = userService.socialSignUp(userSocialSignUpRequest);
        ResultResponse result = ResultResponse.of(ResultCode.SOCIAL_REGISTER_SUCCESS, userSignUpResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * 회원 탈퇴
     * @param userId      탈퇴하려는 계정의 ID
     * @param currentUser 로그인 사용자 정보 (UserDetails)
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResultResponse> signOff(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        UserSignOffResponse userSignOffResponse = userService.signOff(userId, currentUser);
        ResultResponse result = ResultResponse.of(ResultCode.MEMBER_WITHDRAWAL_SUCCESS, userSignOffResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/nicknames")
    public ResponseEntity<ResultResponse> checkNicknameAvailability(@RequestParam("nickname") String nickname) {
        boolean nicknameAvailable = userService.checkNicknameAvailability(nickname);

        if (nicknameAvailable) {
            ResultResponse result = ResultResponse.of(ResultCode.NICKNAME_AVAILABLE_SUCCESS, nicknameAvailable);
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
        }

        throw new NicknameAlreadyInUseException();
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ResultResponse> setUserPassword(
            @RequestBody PasswordChangeRequest passwordChangeRequest
    ) {
        verifyTempToken(passwordChangeRequest);

        userService.setUserPassword(passwordChangeRequest);
        ResultResponse result = ResultResponse.of(ResultCode.PASSWORD_SET_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    private void verifyTempToken(PasswordChangeRequest passwordChangeRequest) {
        // TODO: 이메일 인증 성공 시점에 발급한 임시 토큰 검증
    }
}
