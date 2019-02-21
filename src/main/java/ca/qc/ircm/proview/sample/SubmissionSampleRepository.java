package ca.qc.ircm.proview.sample;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Submission sample repository.
 */
public interface SubmissionSampleRepository
    extends JpaRepository<SubmissionSample, Long>, QueryDslPredicateExecutor<SubmissionSample> {
}
