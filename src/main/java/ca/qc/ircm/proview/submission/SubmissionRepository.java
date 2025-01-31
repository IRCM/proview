package ca.qc.ircm.proview.submission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Submission repository.
 */
public interface SubmissionRepository
    extends JpaRepository<Submission, Long>, QuerydslPredicateExecutor<Submission> {

}
