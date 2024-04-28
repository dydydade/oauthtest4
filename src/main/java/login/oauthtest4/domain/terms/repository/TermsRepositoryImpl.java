package login.oauthtest4.domain.terms.repository;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import login.oauthtest4.domain.terms.model.QTerms;
import login.oauthtest4.domain.terms.model.Terms;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class TermsRepositoryImpl implements TermsRepositoryCustom {

    private final EntityManager entityManager;

    private QTerms terms = QTerms.terms;
    private QTerms termsSub = new QTerms("termsSub");

    @Override
    public List<Terms> findLatestVersionOfEachTermsType() {
        JPAQuery<Terms> query = new JPAQuery<>(entityManager);

        List<Terms> result = query.select(terms)
                .from(terms)
                .where(terms.version.eq(
                        select(termsSub.version.max())
                                .from(termsSub)
                                .where(termsSub.termsType.eq(terms.termsType))
                                .groupBy(termsSub.termsType)
                ))
                .fetch();

        return result;
    }

    @Override
    public Set<Long> findRequiredTermsIds() {
        JPAQuery<Terms> query = new JPAQuery<>(entityManager);

        List<Long> result = query.select(terms.id)
                .from(terms)
                .where(terms.version.eq(
                        select(termsSub.version.max())
                                .from(termsSub)
                                .where(termsSub.termsType.eq(terms.termsType)
                                        .and(terms.mandatory.eq(true)))
                                .groupBy(termsSub.termsType)
                ))
                .fetch();

        return result.stream().collect(Collectors.toSet());
    }
}
