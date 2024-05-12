package login.tikichat.global.auth.verification.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import login.tikichat.global.auth.verification.dto.EmailCertificationResponse;
import login.tikichat.global.auth.verification.CertificationGenerator;
import login.tikichat.global.auth.verification.dao.CertificationNumberDao;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
@RequiredArgsConstructor
public class MailSendService {

    private static final String MAIL_TITLE_CERTIFICATION = "인증번호 발송";
    private static final String DOMAIN_NAME = "http://localhost:8080";
    private final JavaMailSender mailSender;
    private final CertificationNumberDao certificationNumberDao;
    private final CertificationGenerator generator;

    @Async
    public CompletableFuture<EmailCertificationResponse> sendEmailForCertification(String email) throws NoSuchAlgorithmException, MessagingException {
        String certificationNumber = generator.createCertificationNumber();
        String content = String.format("%s/api/v1/auth/verify?certificationNumber=%s&email=%s   링크를 3분 이내에 클릭해주세요.", DOMAIN_NAME, certificationNumber, email);
        certificationNumberDao.saveCertificationNumber(email, certificationNumber);
        sendMail(email, content);
        return CompletableFuture.completedFuture(new EmailCertificationResponse(email, certificationNumber));
    }

    private void sendMail(String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject(MAIL_TITLE_CERTIFICATION);
        helper.setText(content);
        mailSender.send(mimeMessage);
    }
}