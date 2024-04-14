package login.oauthtest4.domain.terms.service;

import jakarta.transaction.Transactional;
import login.oauthtest4.domain.terms.model.AgreementHistory;
import login.oauthtest4.domain.terms.repository.AgreementHistoryRepository;
import login.oauthtest4.domain.user.dto.UserSignUpTermsAgreementDto;
import login.oauthtest4.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgreementHistoryService {

    private final AgreementHistoryRepository agreementHistoryRepository;
    private final TermsService termsService;

    /**
     * [회원가입 시 약관 동의 정보 저장 메서드]
     */
    @Transactional
    public void saveAgreementHistory(UserSignUpTermsAgreementDto termsAgreementDto, User user) {
        List<AgreementHistory> agreementHistories = termsAgreementDto.getAgreements().stream()
                .map(termsAgreement -> AgreementHistory.builder()
                        .user(user)
                        .terms(termsService.findById(termsAgreement.getTermsId()))
                        .consentGiven(termsAgreement.isConsentGiven())
                        .ipAddress(termsAgreement.getIpAddress())
                        .deviceInformation(termsAgreement.getDeviceInformation())
                        .build())
                .collect(Collectors.toList());

        agreementHistoryRepository.saveAllAndFlush(agreementHistories);
    }


}
