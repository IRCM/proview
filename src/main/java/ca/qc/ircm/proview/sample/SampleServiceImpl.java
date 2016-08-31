package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;

import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of sample services.
 */
@Service
@Transactional
public class SampleServiceImpl implements SampleService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private AuthorizationService authorizationService;

  protected SampleServiceImpl() {
  }

  protected SampleServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.authorizationService = authorizationService;
  }

  @Override
  public Sample get(Long id) {
    if (id == null) {
      return null;
    }

    Sample sample = entityManager.find(Sample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  @Override
  public boolean linkedToResults(Sample sample) {
    if (sample == null) {
      return false;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Long> query = queryFactory.select(acquisition.id);
    query.from(acquisition);
    query.from(acquisitionMascotFile);
    query.where(acquisitionMascotFile.acquisition.eq(acquisition));
    query.where(acquisition.sample.eq(sample));
    if (!authorizationService.hasAdminRole()) {
      query.where(acquisitionMascotFile.visible.eq(true));
    }
    return query.fetchCount() > 0;
  }
}
