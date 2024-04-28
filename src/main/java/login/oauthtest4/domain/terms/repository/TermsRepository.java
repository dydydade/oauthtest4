package login.oauthtest4.domain.terms.repository;

import login.oauthtest4.domain.terms.model.Terms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;


public interface TermsRepository extends JpaRepository<Terms, Long>, TermsRepositoryCustom {

    public List<Terms> findLatestVersionOfEachTermsType();

    public Set<Long> findRequiredTermsIds();

}
