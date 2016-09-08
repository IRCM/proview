package ca.qc.ircm.proview.digestion;

import static ca.qc.ircm.proview.digestion.QDigestionProtocol.digestionProtocol;
import static ca.qc.ircm.proview.treatment.QProtocol.protocol;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.treatment.ProtocolActivityService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of digestion protocol services.
 */
@Service
@Transactional
public class DigestionProtocolServiceImpl implements DigestionProtocolService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private ProtocolActivityService protocolActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DigestionProtocolServiceImpl() {
  }

  protected DigestionProtocolServiceImpl(EntityManager entityManager,
      JPAQueryFactory queryFactory, ProtocolActivityService protocolActivityService,
      ActivityService activityService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.protocolActivityService = protocolActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public DigestionProtocol get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(DigestionProtocol.class, id);
  }

  @Override
  public List<DigestionProtocol> all() {
    authorizationService.checkAdminRole();

    JPAQuery<DigestionProtocol> query = queryFactory.select(digestionProtocol);
    query.from(digestionProtocol);
    return query.fetch();
  }

  @Override
  public boolean availableName(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkAdminRole();

    JPAQuery<Long> query = queryFactory.select(protocol.id);
    query.from(protocol);
    query.where(protocol.name.eq(name));
    return query.fetchCount() == 0;
  }

  @Override
  public void insert(DigestionProtocol protocol) {
    authorizationService.checkAdminRole();

    entityManager.persist(protocol);
    entityManager.flush();

    // Log insertion of protocol.
    Activity activity = protocolActivityService.insert(protocol);
    activityService.insert(activity);
  }
}