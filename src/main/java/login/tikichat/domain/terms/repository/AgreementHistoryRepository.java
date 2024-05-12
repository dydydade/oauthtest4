package login.tikichat.domain.terms.repository;

import login.tikichat.domain.terms.model.AgreementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgreementHistoryRepository extends JpaRepository<AgreementHistory, Long> {

}
