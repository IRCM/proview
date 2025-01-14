package ca.qc.ircm.proview.tube;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import ca.qc.ircm.proview.sample.Sample;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for sample tubes.
 */
@Service
@Transactional
public class TubeService {
  private final TubeRepository repository;

  @Autowired
  protected TubeService(TubeRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects tube from database.
   *
   * @param id
   *          database identifier of tube
   * @return tube
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get().sample, 'read')")
  public Optional<Tube> get(long id) {
    return repository.findById(id);
  }

  /**
   * Returns true if name is available in database, false otherwise.
   *
   * @param name
   *          tube's name
   * @return true if name is available in database, false otherwise
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public boolean nameAvailable(String name) {
    return repository.countByName(name) == 0;
  }

  /**
   * Returns tubes used for sample.
   *
   * @param sample
   *          sample.
   * @return digestion tubes used for sample.
   */
  @PreAuthorize("hasPermission(#sample, 'read')")
  public List<Tube> all(Sample sample) {
    return repository.findBySample(sample);
  }
}
