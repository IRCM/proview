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
  @Autowired
  private SampleContainerRepository repository;

  protected SampleContainerService() {
  }

  /**
   * Selects sample container from database.
   *
   * @param id
   *          database identifier of sample container
   * @return sample container
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get().sample, 'read')")
  public Optional<SampleContainer> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample
   *          sample
   * @return last sample container in which sample was
   */
  @PreAuthorize("hasPermission(#sample, 'read')")
  public Optional<SampleContainer> last(Sample sample) {
    if (sample == null) {
      return Optional.empty();
    }

    return repository.findFirstBySampleOrderByTimestampDesc(sample);
  }
}
