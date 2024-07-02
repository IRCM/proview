package ca.qc.ircm.proview.sample;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Sample repository.
 */
public interface SampleRepository extends JpaRepository<Sample, Long> {
}
