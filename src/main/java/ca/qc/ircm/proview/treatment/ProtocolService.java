package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for protocols.
 */
@Service
@Transactional
public class ProtocolService {
  private final ProtocolRepository repository;

  @Autowired
  protected ProtocolService(ProtocolRepository repository) {
    this.repository = repository;
  }

  /**
   * Selects protocol from database.
   *
   * @param id
   *          database identifier of protocol
   * @return protocol
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Protocol> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Returns all protocols of specified type.
   *
   * @param type
   *          protocol type
   * @return all protocols of specified type
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public List<Protocol> all(Protocol.Type type) {
    return repository.findByType(type);
  }
}
