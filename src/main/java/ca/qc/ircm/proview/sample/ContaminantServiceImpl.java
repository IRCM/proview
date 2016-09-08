package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QEluateSample.eluateSample;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of contaminant services.
 */
@Service
@Transactional
public class ContaminantServiceImpl implements ContaminantService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected ContaminantServiceImpl() {
  }

  protected ContaminantServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  private Sample getSample(Contaminant contaminant) {
    JPAQuery<EluateSample> query = queryFactory.select(eluateSample);
    query.from(eluateSample);
    query.where(eluateSample.contaminants.contains(contaminant));
    return query.fetchOne();
  }

  @Override
  public Contaminant get(Long id) {
    if (id == null) {
      return null;
    }

    Contaminant contaminant = entityManager.find(Contaminant.class, id);
    if (contaminant != null) {
      Sample sample = getSample(contaminant);
      authorizationService.checkSampleReadPermission(sample);
    }
    return contaminant;
  }

  @Override
  @Deprecated
  public List<Contaminant> all(Sample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Contaminant> query = queryFactory.select(contaminant);
    query.from(eluateSample);
    query.join(eluateSample.contaminants, contaminant);
    query.where(eluateSample._super._super._super.eq(sample));
    return query.fetch();
  }
}