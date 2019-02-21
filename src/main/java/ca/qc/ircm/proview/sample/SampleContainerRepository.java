package ca.qc.ircm.proview.sample;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Sample container repository.
 */
public interface SampleContainerRepository extends JpaRepository<SampleContainer, Long> {
  SampleContainer findFirstBySampleOrderByTimestampDesc(Sample sample);
}
