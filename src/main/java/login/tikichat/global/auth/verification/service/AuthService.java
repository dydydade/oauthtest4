package login.tikichat.global.auth.verification.service;

import login.tikichat.global.auth.verification.dao.AuthTokenDao;
import login.tikichat.global.auth.verification.dto.EmailCertificationResponse;
import login.tikichat.global.auth.verification.CertificationGenerator;
import login.tikichat.global.auth.verification.dao.CertificationNumberDao;
import login.tikichat.global.component.EmailSender;
import login.tikichat.global.exception.auth.EmailNotFoundException;
import login.tikichat.global.exception.auth.InvalidEmailVerificationCodeException;
import login.tikichat.global.exception.auth.InvalidVerificationTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MAIL_TITLE_CERTIFICATION = "인증번호 발송";
    private final CertificationNumberDao certificationNumberDao;
    private final CertificationGenerator generator;
    private final EmailSender emailSender;
    private final AuthTokenDao authTokenDao;

    @Async("async-executor")
    public CompletableFuture<EmailCertificationResponse> sendEmailForCertification(String email)
            throws NoSuchAlgorithmException {
        String certificationNumber = generator.createCertificationNumber();
        String content = "인증번호: " + certificationNumber;
        certificationNumberDao.saveCertificationNumber(email, certificationNumber);

        this.emailSender.sendEmail(email, MAIL_TITLE_CERTIFICATION, content);

        return CompletableFuture.completedFuture(new EmailCertificationResponse(email, certificationNumber));
    }

    public String verifyEmail(String email, String certificationNumber) {
        if (!isVerify(email, certificationNumber)) {
            throw new InvalidEmailVerificationCodeException();
        }

        final var token = UUID.randomUUID().toString();

        this.authTokenDao.saveToken(token);

        certificationNumberDao.removeCertificationNumber(email);

        return token;
    }

    public void verifyToken(String token) {
        if (!this.authTokenDao.hasToken(token)) {
            throw new InvalidVerificationTokenException();
        }

        this.authTokenDao.removeToken(token);
    }

    private boolean isVerify(String email, String certificationNumber) {
        boolean validatedEmail = isEmailExists(email);
        if (!isEmailExists(email)) {
            throw new EmailNotFoundException();
        }
        return (validatedEmail &&
                certificationNumberDao.getCertificationNumber(email).equals(certificationNumber));
    }

    private boolean isEmailExists(String email) {
        return certificationNumberDao.hasKey(email);
    }
}