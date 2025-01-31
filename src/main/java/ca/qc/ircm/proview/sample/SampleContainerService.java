package ca.qc.ircm.proview.sample;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sample containers.
 */
@Service
@Transactional
public class SampleContainerService {

  private final SampleContainerRepository repository;

  @Autowired
  protected SampleContainerService(SampleContainerRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects sample container from database.
   *
   * @param id database identifier of sample container
   * @return sample container
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get().sample, 'read')")
  public Optional<SampleContainer> get(long id) {
    return repository.findById(id);
  }

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample sample
   * @return last sample container in which sample was
   */
  @PreAuthorize("hasPermission(#sample, 'read')")
  public Optional<SampleContainer> last(Sample sample) {
    return repository.findFirstBySampleOrderByTimestampDesc(sample);
  }
}
