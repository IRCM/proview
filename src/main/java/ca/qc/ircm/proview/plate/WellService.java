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

  private final WellRepository wellRepository;

  @Autowired
  protected WellService(WellRepository wellRepository) {
    this.wellRepository = wellRepository;
  }

  /**
   * Selects well from database.
   *
   * @param id database identifier of well
   * @return well
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public Optional<Well> get(long id) {
    return wellRepository.findById(id);
  }
}
