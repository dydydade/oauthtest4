package login.oauthtest4.domain.user.controller;

import login.oauthtest4.domain.user.dto.*;
import login.oauthtest4.domain.user.service.UserService;
import login.oauthtest4.global.auth.dto.ApiResponse;
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
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(body, "회원으로 존재하는 이메일 계정입니다."));
    }

    /**
     * 회원 가입
     *
     * @param userSignUpRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody UserSignUpRequest userSignUpRequest) {
        UserSignUpResponse userSignUpResponse = userService.signUp(userSignUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userSignUpResponse, "회원 가입이 정상적으로 완료되었습니다."));
    }

    /**
     * 회원 탈퇴
     *
     * @param userId      탈퇴하려는 계정의 ID
     * @param currentUser 로그인 사용자 정보 (UserDetails)
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> signOff(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        UserSignOffResponse userSignOffResponse = userService.signOff(userId, currentUser);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(userSignOffResponse, "회원 탈퇴가 정상적으로 완료되었습니다."));
    }

    @GetMapping("/nicknames")
    public ResponseEntity<?> checkNicknameAvailability(@RequestParam("nickname") String nickname) {
        boolean nicknameAvailable = userService.checkNicknameAvailability(nickname);

        if (nicknameAvailable) {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("사용 가능한 닉네임입니다."));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.failure("이미 사용 중인 닉네임입니다."));
    }

//    @PutMapping("/{userId}/password")
//    public ResponseEntity<?> changePassword(
//            @PathVariable("userId") Long userId,
//            @RequestBody PasswordChangeRequest passwordChangeRequest,
//            @AuthenticationPrincipal UserDetails currentUser
//    ) {
//
//    }
}
