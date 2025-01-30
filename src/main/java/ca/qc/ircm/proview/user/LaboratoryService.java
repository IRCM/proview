package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Laboratory service class.
 */
@Service
@Transactional
public class LaboratoryService {

  private LaboratoryRepository repository;

  @Autowired
  protected LaboratoryService(LaboratoryRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects user from database.
   *
   * @param id database identifier of user
   * @return user
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<Laboratory> get(long id) {
    return repository.findById(id);
  }

  /**
   * Returns all laboratories.
   *
   * @return all laboratories
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Laboratory> all() {
    return Lists.newArrayList(repository.findAll());
  }

  /**
   * Saves laboratory into the database.
   *
   * @param laboratory laboratory
   */
  @PreAuthorize("hasPermission(#laboratory, 'write')")
  public void save(Laboratory laboratory) {
    repository.save(laboratory);
  }
}
