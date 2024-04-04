package login.oauthtest4.global.auth.controller;

import jakarta.mail.MessagingException;
import login.oauthtest4.domain.user.dto.NormalLoginRequest;
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
    public ResponseEntity<String> sendCertificationNumber(
            @Validated @RequestBody EmailCertificationRequest request
    ) throws MessagingException, NoSuchAlgorithmException {
        // 비동기 메서드 호출
        mailSendService.sendEmailForCertification(request.getEmail())
                .thenApply(response -> ResponseEntity.ok(ApiResponse.success(response)))
                .exceptionally(ex -> ResponseEntity.badRequest().body(ApiResponse.failure("이메일 인증 처리 중 에러 발생")));
        return ResponseEntity.ok("인증 코드가 메일로 발송되었습니다.");
    }

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyCertificationNumber(
            @RequestParam(name = "email") String email,
            @RequestParam(name = "certificationNumber") String certificationNumber
    ) {
        mailVerifyService.verifyEmail(email, certificationNumber);
        return ResponseEntity.ok(ApiResponse.success());
    }


    // TODO: 완성시키기
    @PostMapping("/login")
    public String login(@RequestBody NormalLoginRequest request) {
        return "login";
    }

    // TODO: 완성시키기
    @PostMapping("/logout")
    public String logout(@RequestBody NormalLoginRequest request) {
        return "logout";
    }
}