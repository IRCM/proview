package ca.qc.ircm.proview.submission;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Submission repository.
 */
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
