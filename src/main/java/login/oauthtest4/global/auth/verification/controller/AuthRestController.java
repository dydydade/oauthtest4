package login.oauthtest4.global.auth.verification.controller;

import jakarta.mail.MessagingException;
import login.oauthtest4.global.auth.verification.dto.EmailCertificationRequest;
import login.oauthtest4.global.auth.verification.service.MailSendService;
import login.oauthtest4.global.auth.verification.service.MailVerifyService;
import login.oauthtest4.global.response.ResultCode;
import login.oauthtest4.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {

    private final MailSendService mailSendService;
    private final MailVerifyService mailVerifyService;

    @PostMapping("/send-certification")
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
    public ResponseEntity<ResultResponse> verifyCertificationNumber(
            @RequestParam(name = "email") String email,
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
    public ResponseEntity<ResultResponse> login() {
        return ResponseEntity.ok().build();
    }
}