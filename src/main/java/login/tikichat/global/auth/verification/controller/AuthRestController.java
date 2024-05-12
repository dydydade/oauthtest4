package login.tikichat.global.auth.verification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import login.tikichat.global.auth.verification.dto.EmailCertificationRequest;
import login.tikichat.global.auth.verification.dto.NormalLoginRequest;
import login.tikichat.global.auth.verification.service.MailSendService;
import login.tikichat.global.auth.verification.service.MailVerifyService;
import login.tikichat.global.response.ResultCode;
import login.tikichat.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@Tag(name = "Auth API", description = "인증 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final MailSendService mailSendService;
    private final MailVerifyService mailVerifyService;

    @PostMapping("/send-certification")
    @Operation(summary = "이메일 인증코드 발송", description = "이메일 인증코드 발송 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 코드를 메일로 발송하였습니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "이메일을 찾지 못했습니다.")
    })
    public ResponseEntity<ResultResponse> sendCertificationNumber(
            @Validated @RequestBody EmailCertificationRequest request
    ) throws MessagingException, NoSuchAlgorithmException {
        // 비동기 메서드 호출
        mailSendService.sendEmailForCertification(request.getEmail())
                .exceptionally(ex -> {
                    // 인증 코드 발생 간 오류 발생 시, 클라이언트에 오류를 알릴 수 있는 코드 구현(필요 시)
                    alertClientAboutEmailFailure(ex);
                    return null;
                });
        // 인증 코드 발송 응답은 즉시 반환(비동기)
        ResultResponse result = ResultResponse.of(ResultCode.AUTH_CODE_SENT_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    private void alertClientAboutEmailFailure(Throwable ex) {
        // 구현 필요
    }

    @GetMapping("/verify")
    @Operation(summary = "이메일 인증코드 검증", description = "이메일 인증코드 검증 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증에 성공하였습니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 인증코드입니다.")
    })
    public ResponseEntity<ResultResponse> verifyCertificationNumber(
            @Parameter(description = "인증할 이메일")
            @RequestParam(name = "email") String email,
            @Parameter(description = "인증코드")
            @RequestParam(name = "certificationNumber") String certificationNumber
    ) {
        mailVerifyService.verifyEmail(email, certificationNumber);
        ResultResponse result = ResultResponse.of(ResultCode.EMAIL_VERIFICATION_SUCCESS, null);
        return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
    }

    /**
     * 스웨거 적용을 위한 더미 메서드(실제 /api/v1/login 경로에 대한 요청은 필터 체인을 통해 수행)
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "일반 로그인 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인에 성공하였습니다.", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이메일 또는 비밀번호를 다시 확인해주세요.")
    })
    public ResponseEntity<ResultResponse> login(
            @Parameter(name = "Device-ID", description = "로그인 기기 식별 정보", required = true, in = ParameterIn.HEADER)
            @Param("Device-ID") String deviceId,
            @RequestBody NormalLoginRequest loginRequest
    ) {
        return ResponseEntity.ok().build();
    }
}