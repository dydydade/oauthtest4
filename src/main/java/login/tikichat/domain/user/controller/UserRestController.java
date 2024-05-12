package login.tikichat.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import login.tikichat.domain.user.dto.*;
import login.tikichat.domain.user.service.UserService;
import login.tikichat.global.exception.user.NicknameAlreadyInUseException;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User API", description = "사용자 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(summary = "이메일로 회원 조회", description = "이메일로 회원 정보를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회에 성공하였습니다.",
            content = {@Content(schema = @Schema(implementation = FindUserResponse.class))}
            ),
            @ApiResponse(responseCode = "404", description = "가입된 계정을 찾을 수 없습니다.")
    })
    public ResponseEntity<ResultResponse> findUserByEmail(@RequestParam String email) {
        FindUserResponse body = userService.findUserByEmail(email);
        ResultResponse result = ResultResponse.of(ResultCode.FIND_USER_INFO_SUCCESS, body);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * [일반 회원 가입]
     * @param userNormalSignUpRequest
     * @return
     */
    @PostMapping("/signup/normal")
    @Operation(summary = "일반 계정 회원 가입", description = "일반 계정 회원 가입 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일반 회원 가입을 완료하였습니다.",
                content = {@Content(schema = @Schema(implementation = UserSignUpResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "요청에 필수 약관 정보를 모두 포함시켜 주세요."),
            @ApiResponse(responseCode = "404", description = "대상 약관을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "같은 이메일로 이미 가입한 계정이 있습니다.\t\n이미 사용 중인 닉네임입니다."),
    })
    public ResponseEntity<ResultResponse> signUp(@RequestBody @Valid UserNormalSignUpRequest userNormalSignUpRequest) {
        UserSignUpResponse userSignUpResponse = userService.signUp(userNormalSignUpRequest);
        ResultResponse result = ResultResponse.of(ResultCode.NORMAL_REGISTER_SUCCESS, userSignUpResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * [소셜 회원 가입]
     * @param userSocialSignUpRequest
     * @return
     */
    @PostMapping("/signup/social")
    @Operation(summary = "소셜 계정 회원 가입", description = "소셜 계정 회원 가입 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 가입을 완료하였습니다.",
                    content = {@Content(schema = @Schema(implementation = UserSignUpResponse.class))}
            ),
            @ApiResponse(responseCode = "400", description = "요청에 필수 약관 정보를 모두 포함시켜 주세요.\t\n소셜 회원가입 시 소셜 계정 연동 정보를 반드시 포함하여야 합니다.\t\n계정의 이메일과 소셜 연동 정보의 이메일이 일치하지 않습니다."),
            @ApiResponse(responseCode = "404", description = "대상 약관을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "같은 이메일로 이미 가입한 계정이 있습니다.\t\n이미 사용 중인 닉네임입니다."),
    })
    public ResponseEntity<ResultResponse> socialSignUp(@RequestBody @Valid UserSocialSignUpRequest userSocialSignUpRequest, HttpServletRequest request, HttpServletResponse response) {
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
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴를 완료하였습니다.",
                    content = {@Content(schema = @Schema(implementation = UserSignOffResponse.class))}
            ),
            @ApiResponse(responseCode = "403", description = "회원 탈퇴를 요청한 계정과 로그인 사용자가 일치하지 않습니다.")
    })
    public ResponseEntity<ResultResponse> signOff(
            @PathVariable
            @Schema(description = "탈퇴할 회원 ID")
            Long userId,
            @AuthenticationPrincipal UserDetails currentUser
    ) {
        UserSignOffResponse userSignOffResponse = userService.signOff(userId, currentUser);
        ResultResponse result = ResultResponse.of(ResultCode.MEMBER_WITHDRAWAL_SUCCESS, userSignOffResponse);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    @GetMapping("/nicknames")
    @Operation(summary = "닉네임 사용 가능 여부 조회", description = "닉네임 사용 가능 여부 조회 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임입니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임입니다.")
    })
    public ResponseEntity<ResultResponse> checkNicknameAvailability(
            @Parameter(description = "중복 조회할 닉네임", example = "레모네이드13")
            @RequestParam("nickname") String nickname
    ) {
        boolean nicknameAvailable = userService.checkNicknameAvailability(nickname);

        if (nicknameAvailable) {
            ResultResponse result = ResultResponse.of(ResultCode.NICKNAME_AVAILABLE_SUCCESS, null);
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
        }

        throw new NicknameAlreadyInUseException();
    }

    @PutMapping("/{userId}/password")
    @SecurityRequirement(name = "JWT")
    @Operation(summary = "회원 비밀번호 설정", description = "회원 비밀번호 설정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 설정을 완료하였습니다.", useReturnTypeSchema = true),
    })
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
