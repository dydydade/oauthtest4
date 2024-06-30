package login.tikichat.domain.terms.repository;

import login.tikichat.domain.terms.model.QTerms;
import login.tikichat.domain.terms.model.Terms;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;

@Repository
public class TermsRepositoryImpl extends QuerydslRepositorySupport implements TermsRepositoryCustom {
    public TermsRepositoryImpl() {
        super(Terms.class);
    }

    @Override
    public List<Terms> findLatestVersionOfEachTermsType() {
        final var termsQ = QTerms.terms;
        final var termsSubQ = new QTerms("termsSubQ");
        final var query = super.from(termsQ);

        List<Terms> result = query
                .where(termsQ.version.eq(
                        select(termsSubQ.version.max())
                                .from(termsSubQ)
                                .where(termsSubQ.termsType.eq(termsQ.termsType))
                                .groupBy(termsSubQ.termsType)
                ))
                .fetch();

        return result;
    }

    @Override
    public Set<Long> findRequiredTermsIds() {
        final var termsQ = QTerms.terms;
        final var termsSubQ = new QTerms("termsSubQ");
        final var query = super.from(termsQ);

        List<Long> result = query
                .select(termsQ.id)
                .where(termsQ.version.eq(
                        select(termsSubQ.version.max())
                                .from(termsSubQ)
                                .where(termsSubQ.termsType.eq(termsQ.termsType)
                                        .and(termsSubQ.mandatory.eq(true)))
                                .groupBy(termsSubQ.termsType)
                ))
                .fetch();

        return result.stream().collect(Collectors.toSet());
    }
}
