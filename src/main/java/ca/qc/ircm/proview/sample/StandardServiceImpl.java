package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QStandard.standard;

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
 * Service for Standard.
 */
@Service
@Transactional
public class StandardServiceImpl implements StandardService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected StandardServiceImpl() {
  }

  protected StandardServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  private Sample getSample(Standard standard) {
    JPAQuery<Sample> query = queryFactory.select(sample);
    query.from(sample);
    query.where(sample.standards.contains(standard));
    return query.fetchOne();
  }

  @Override
  public Standard get(Long id) {
    if (id == null) {
      return null;
    }

    Standard standard = entityManager.find(Standard.class, id);
    if (standard != null) {
      Sample sample = getSample(standard);
      authorizationService.checkSampleReadPermission(sample);
    }
    return standard;
  }

  @Override
  @Deprecated
  public List<Standard> all(Sample sampleParam) {
    if (sampleParam == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sampleParam);

    JPAQuery<Standard> query = queryFactory.select(standard);
    query.from(sample);
    query.join(sample.standards, standard);
    query.where(sample.eq(sampleParam));
    return query.fetch();
  }
}
