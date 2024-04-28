package login.oauthtest4.domain.terms.repository;

import login.oauthtest4.domain.terms.model.Terms;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TermsRepositoryCustom {

    @Query("SELECT t FROM Terms t WHERE t.version IN (SELECT MAX(t2.version) FROM Terms t2 WHERE t2.termsType = t.termsType GROUP BY t2.termsType)")
    public List<Terms> findLatestVersionOfEachTermsType();

    @Query("SELECT t.id FROM Terms t WHERE t.version IN (SELECT MAX(t2.version) FROM Terms t2 WHERE t2.termsType = t.termsType AND t.mandatory = true GROUP BY t2.termsType)")
    public Set<Long> findRequiredTermsIds();
}
