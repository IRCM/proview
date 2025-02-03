package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.submission.Submission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * Plate repository.
 */
public interface PlateRepository
    extends JpaRepository<Plate, Long>, QuerydslPredicateExecutor<Plate> {

  Optional<Plate> findBySubmission(Submission submission);
}
