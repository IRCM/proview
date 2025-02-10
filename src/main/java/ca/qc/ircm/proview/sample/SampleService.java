package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Sample.
 */
@Service
@Transactional
public class SampleService {

  private final SampleRepository repository;

  @Autowired
  @UsedBy(SPRING)
  protected SampleService(SampleRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects sample from database.
   *
   * @param id database identifier of sample
   * @return sample
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<Sample> get(long id) {
    return repository.findById(id);
  }
}
