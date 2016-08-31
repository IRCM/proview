package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample;
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
 * Services for Mascot file.
 */
@Service
@Transactional
public class MascotFileServiceImpl implements MascotFileService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private MascotFileActivityService mascotFileActivityService;
  @Inject
  private MsAnalysisService msAnalysisService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected MascotFileServiceImpl() {
  }

  protected MascotFileServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      MascotFileActivityService mascotFileActivityService, MsAnalysisService msAnalysisService,
      ActivityService activityService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.mascotFileActivityService = mascotFileActivityService;
    this.msAnalysisService = msAnalysisService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public AcquisitionMascotFile get(Long id) {
    if (id == null) {
      return null;
    }
    authorizationService.checkAdminRole();

    return entityManager.find(AcquisitionMascotFile.class, id);
  }

  @Override
  public List<AcquisitionMascotFile> all(Acquisition acquisition) {
    if (acquisition == null) {
      return new ArrayList<AcquisitionMascotFile>();
    }
    MsAnalysis msAnalysis = msAnalysisService.get(acquisition);
    authorizationService.checkMsAnalysisReadPermission(msAnalysis);

    JPAQuery<AcquisitionMascotFile> query = queryFactory.select(acquisitionMascotFile);
    query.from(acquisitionMascotFile);
    query.where(acquisitionMascotFile.acquisition.eq(acquisition));
    return query.fetch();
  }

  @Override
  public boolean exists(Sample sample) {
    if (sample == null) {
      return false;
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<Long> query = queryFactory.select(acquisitionMascotFile.id);
    query.from(acquisitionMascotFile);
    query.where(acquisitionMascotFile.acquisition.sample.eq(sample));
    if (!authorizationService.hasAdminRole()) {
      query.where(acquisitionMascotFile.visible.eq(true));
    }
    return query.fetchCount() > 0;
  }

  @Override
  public void update(AcquisitionMascotFile acquisitionMascotFile) {
    authorizationService.checkAdminRole();

    // Log change in database.
    Optional<Activity> activity = mascotFileActivityService.update(acquisitionMascotFile);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(acquisitionMascotFile);
  }
}
