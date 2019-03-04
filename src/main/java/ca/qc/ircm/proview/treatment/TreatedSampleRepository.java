package ca.qc.ircm.proview.treatment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Treated sample repository.
 */
public interface TreatedSampleRepository
    extends JpaRepository<TreatedSample, Long>, QueryDslPredicateExecutor<TreatedSample> {
}
