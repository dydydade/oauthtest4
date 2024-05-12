package login.tikichat.domain.terms.service;

import jakarta.transaction.Transactional;
import login.tikichat.domain.terms.dto.LatestTermsDto;
import login.tikichat.domain.terms.dto.LatestTermsResponse;
import login.tikichat.domain.terms.dto.TermsCreateRequest;
import login.tikichat.domain.terms.dto.TermsCreateResponse;
import login.tikichat.domain.terms.model.Terms;
import login.tikichat.domain.terms.repository.TermsRepository;
import login.tikichat.domain.user.dto.UserSignUpTermsAgreementDto;
import login.tikichat.global.exception.terms.RequiredTermsNotAgreedException;
import login.tikichat.global.exception.terms.TermsNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;

    /**
     * [이용약관 생성 메서드]
     * @param termsCreateRequest
     * @return
     */
    @Transactional
    public TermsCreateResponse createTerms(TermsCreateRequest termsCreateRequest) {
        Terms terms = Terms.builder()
                .termsType(termsCreateRequest.getTermsType())
                .version(termsCreateRequest.getVersion())
                .content(termsCreateRequest.getContent())
                .mandatory(termsCreateRequest.isMandatory())
                .effectiveAt(termsCreateRequest.getEffectiveDate())
                .build();

        terms = termsRepository.saveAndFlush(terms);

        return TermsCreateResponse.builder()
                .id(terms.getId())
                .termsType(terms.getTermsType())
                .version(terms.getVersion())
                .mandatory(terms.isMandatory())
                .effectiveDate(terms.getEffectiveAt())
                .build();
    }

    /**
     * [각 이용약관 타입별 최신 버전만 조회하는 메서드]
     * ex) 개인정보처리방침(v2.0), 서비스 이용약관(v3.0), 마케팅 활용 동의(v2.2)
     */
    @Transactional
    public LatestTermsResponse findLatestVersionOfEachTermsType() {
        return new LatestTermsResponse(termsRepository.findLatestVersionOfEachTermsType().stream()
                .map(terms -> {
                    return LatestTermsDto.builder()
                            .id(terms.getId())
                            .termsType(terms.getTermsType())
                            .version(terms.getVersion())
                            .content(terms.getContent())
                            .mandatory(terms.isMandatory())
                            .effectiveAt(terms.getEffectiveAt())
                            .build();
                })
                .collect(Collectors.toList()));
    }

    /**
     * [사용자가 회원가입 시 모든 필수 약관에 동의했는지 검증하는 메서드]
     * @param termsAgreementDto
     */
    @Transactional
    public void validateRequiredTermsConsents(UserSignUpTermsAgreementDto termsAgreementDto) {
        Set<Long> requiredTermsIds = this.findRequiredTermsIds();

        Set<Long> agreedTermsIds = termsAgreementDto.getAgreements().stream()
                .filter(UserSignUpTermsAgreementDto.TermsAgreement::isConsentGiven)
                .map(UserSignUpTermsAgreementDto.TermsAgreement::getTermsId)
                .collect(Collectors.toSet());

        if (!agreedTermsIds.containsAll(requiredTermsIds)) {
            throw new RequiredTermsNotAgreedException();
        }
    }

    /**
     * [모든 최신 필수 약관 목록을 조회하는 메서드]
     * @return
     */
    @Transactional
    public Set<Long> findRequiredTermsIds() {
        return termsRepository.findRequiredTermsIds();
    }

    public Terms findById(Long id) {
        return termsRepository.findById(id)
                .orElseThrow(() -> new TermsNotFoundException());
    }
}
