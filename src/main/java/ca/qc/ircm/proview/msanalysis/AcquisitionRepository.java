package ca.qc.ircm.proview.msanalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Acquisition repository.
 */
public interface AcquisitionRepository
    extends JpaRepository<Acquisition, Long>, QueryDslPredicateExecutor<Acquisition> {
}
