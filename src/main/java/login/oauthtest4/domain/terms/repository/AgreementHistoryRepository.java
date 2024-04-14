package login.oauthtest4.domain.terms.repository;

import login.oauthtest4.domain.terms.model.AgreementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementHistoryRepository extends JpaRepository<AgreementHistory, Long> {

}
