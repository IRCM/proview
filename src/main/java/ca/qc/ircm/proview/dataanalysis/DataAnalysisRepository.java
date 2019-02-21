package ca.qc.ircm.proview.dataanalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Data analysis repository.
 */
public interface DataAnalysisRepository
    extends JpaRepository<DataAnalysis, Long>, QueryDslPredicateExecutor<DataAnalysis> {
}
