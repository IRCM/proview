package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.security.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service class for Acquisition.
 */
@Service
@Transactional
public class AcquisitionServiceImpl implements AcquisitionService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private AuthorizationService authorizationService;

  protected AcquisitionServiceImpl() {
  }

  protected AcquisitionServiceImpl(EntityManager entityManager,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.authorizationService = authorizationService;
  }

  @Override
  public Acquisition get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkRobotRole();

    return entityManager.find(Acquisition.class, id);
  }
}
