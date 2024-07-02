package ca.qc.ircm.proview.treatment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Treatment repository.
 */
public interface TreatmentRepository
    extends JpaRepository<Treatment, Long>, QuerydslPredicateExecutor<Treatment> {
}
