package login.oauthtest4.global.auth.controller;

import jakarta.mail.MessagingException;
import login.oauthtest4.global.auth.dto.ApiResponse;
import login.oauthtest4.global.auth.dto.EmailCertificationRequest;
import login.oauthtest4.global.auth.service.MailSendService;
import login.oauthtest4.global.auth.service.MailVerifyService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<?> sendCertificationNumber(
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
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 메일로 발송되었습니다."));
    }

    private void alertClientAboutEmailFailure(Throwable ex) {
        // 구현 필요
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCertificationNumber(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "certificationNumber") String certificationNumber
    ) {
        mailVerifyService.verifyEmail(email, certificationNumber);
        return ResponseEntity.ok(ApiResponse.success("이메일 인증에 성공하였습니다."));
    }
}