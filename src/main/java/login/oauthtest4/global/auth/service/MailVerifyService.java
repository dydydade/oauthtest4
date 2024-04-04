package login.oauthtest4.global.auth.service;

import login.oauthtest4.global.auth.dao.CertificationNumberDao;
import login.oauthtest4.global.auth.exception.EmailNotFoundException;
import login.oauthtest4.global.auth.exception.InvalidCertificationNumberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailVerifyService {

    private final CertificationNumberDao certificationNumberDao;

    public void verifyEmail(String email, String certificationNumber) {
        if (!isVerify(email, certificationNumber)) {
            throw new InvalidCertificationNumberException();
        }
        certificationNumberDao.removeCertificationNumber(email);
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