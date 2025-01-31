package ca.qc.ircm.proview.tube;

import ca.qc.ircm.proview.sample.Sample;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Tube repository.
 */
public interface TubeRepository extends JpaRepository<Tube, Long> {

  long countByName(String name);

  List<Tube> findBySample(Sample sample);
}
