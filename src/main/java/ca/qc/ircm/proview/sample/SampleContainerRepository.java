package ca.qc.ircm.proview.sample;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Sample container repository.
 */
public interface SampleContainerRepository extends JpaRepository<SampleContainer, Long> {

  Optional<SampleContainer> findFirstBySampleOrderByTimestampDesc(Sample sample);
}
