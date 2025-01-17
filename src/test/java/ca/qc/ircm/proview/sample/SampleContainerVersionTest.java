package ca.qc.ircm.proview.sample;

import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Tests for {@link SampleContainer} version.
 */
@ServiceTestAnnotations
public class SampleContainerVersionTest extends AbstractServiceTestCase {
  @Autowired
  private SampleContainerRepository repository;
  @Autowired
  private SampleRepository sampleRepository;

  @Test
  public void update_FailVersion() {
    final Sample sample1 = sampleRepository.findById(442L).orElseThrow();
    final Sample sample2 = sampleRepository.findById(443L).orElseThrow();
    SampleContainer sampleContainer1 = repository.findById(130L).orElseThrow();
    SampleContainer sampleContainer2 = repository.findById(130L).orElseThrow();
    detach(sampleContainer1, sampleContainer2);
    sampleContainer1.setSample(sample1);
    sampleContainer2.setSample(sample2);
    repository.saveAndFlush(sampleContainer1);
    assertThrows(ObjectOptimisticLockingFailureException.class,
        () -> repository.save(sampleContainer2));
  }
}
