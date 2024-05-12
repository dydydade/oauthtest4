package login.tikichat.domain.terms.repository;

import login.tikichat.domain.terms.model.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;


public interface TermsRepository extends JpaRepository<Terms, Long>, TermsRepositoryCustom {

    public List<Terms> findLatestVersionOfEachTermsType();

    public Set<Long> findRequiredTermsIds();

}
