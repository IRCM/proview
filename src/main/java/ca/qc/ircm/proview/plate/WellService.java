package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.user.UserRole.ADMIN;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Services for plate's wells.
 */
@Service
@Transactional
public class WellService {
  @Autowired
  private WellRepository wellRepository;

  protected WellService() {
  }

  /**
   * Selects well from database.
   *
   * @param id
   *          database identifier of well
   * @return well
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Well> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return wellRepository.findById(id);
  }
}
