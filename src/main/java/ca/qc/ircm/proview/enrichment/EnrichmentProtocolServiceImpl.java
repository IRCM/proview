package ca.qc.ircm.proview.enrichment;

import static ca.qc.ircm.proview.enrichment.QEnrichmentProtocol.enrichmentProtocol;
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
 * Default implementation of enrichment protocol services.
 */
@Service
@Transactional
public class EnrichmentProtocolServiceImpl implements EnrichmentProtocolService {
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

  protected EnrichmentProtocolServiceImpl() {
  }

  protected EnrichmentProtocolServiceImpl(EntityManager entityManager,
      JPAQueryFactory queryFactory, ProtocolActivityService protocolActivityService,
      ActivityService activityService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.protocolActivityService = protocolActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public EnrichmentProtocol get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(EnrichmentProtocol.class, id);
  }

  @Override
  public List<EnrichmentProtocol> all() {
    authorizationService.checkAdminRole();

    JPAQuery<EnrichmentProtocol> query = queryFactory.select(enrichmentProtocol);
    query.from(enrichmentProtocol);
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
  public void insert(EnrichmentProtocol protocol) {
    authorizationService.checkAdminRole();

    entityManager.persist(protocol);

    // Log insertion of protocol.
    entityManager.flush();
    Activity activity = protocolActivityService.insert(protocol);
    activityService.insert(activity);
  }
}