package ca.qc.ircm.proview.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Submission sample repository.
 */
public interface SubmissionSampleRepository
    extends JpaRepository<SubmissionSample, Long>, QuerydslPredicateExecutor<SubmissionSample> {
}
