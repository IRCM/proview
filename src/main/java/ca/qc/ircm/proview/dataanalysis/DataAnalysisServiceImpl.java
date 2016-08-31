package ca.qc.ircm.proview.dataanalysis;

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of data analysis services.
 */
@Service
@Transactional
public class DataAnalysisServiceImpl implements DataAnalysisService {
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private DataAnalysisActivityService dataAnalysisActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private AuthorizationService authorizationService;

  protected DataAnalysisServiceImpl() {
  }

  protected DataAnalysisServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      DataAnalysisActivityService dataAnalysisActivityService, ActivityService activityService,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.dataAnalysisActivityService = dataAnalysisActivityService;
    this.activityService = activityService;
    this.authorizationService = authorizationService;
  }

  @Override
  public DataAnalysis get(Long id) {
    if (id == null) {
      return null;
    }
    DataAnalysis dataAnalysis = entityManager.find(DataAnalysis.class, id);

    authorizationService.checkDataAnalysisReadPermission(dataAnalysis);

    return dataAnalysis;
  }

  @Override
  public List<DataAnalysis> all(SubmissionSample sample) {
    if (sample == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSampleReadPermission(sample);

    JPAQuery<DataAnalysis> query = queryFactory.select(dataAnalysis);
    query.from(dataAnalysis);
    query.where(dataAnalysis.sample.eq(sample));
    return query.fetch();
  }

  @Override
  public void insert(Collection<DataAnalysis> dataAnalyses) {
    for (DataAnalysis dataAnalysis : dataAnalyses) {
      authorizationService.checkSampleReadPermission(dataAnalysis.getSample());

      // Update sample status.
      dataAnalysis.getSample().setStatus(SubmissionSample.Status.DATA_ANALYSIS);

      // Insert data analysis.
      dataAnalysis.setStatus(DataAnalysis.Status.TO_DO);
      entityManager.persist(dataAnalysis);
      entityManager.flush();

      // Log insertion of data analysis.
      Activity activity = dataAnalysisActivityService.insert(dataAnalysis);
      activityService.insert(activity);

      entityManager.merge(dataAnalysis.getSample());
    }
  }

  @Override
  public void analyse(Collection<DataAnalysis> dataAnalyses) {
    authorizationService.checkAdminRole();

    // Update analysed DataAnalysis.
    for (DataAnalysis dataAnalysis : dataAnalyses) {
      // Update sample status.
      if (!existsTodoBySampleWithExclude(dataAnalysis)) {
        dataAnalysis.getSample().setStatus(SubmissionSample.Status.ANALYSED);
      }

      // Log update of data analysis.
      Optional<Activity> activity = dataAnalysisActivityService.update(dataAnalysis, null);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }

      entityManager.merge(dataAnalysis);
      entityManager.merge(dataAnalysis.getSample());
    }
  }

  private boolean existsTodoBySampleWithExclude(DataAnalysis dataAnalysisParam) {
    JPAQuery<Long> query = queryFactory.select(dataAnalysis.id);
    query.from(dataAnalysis);
    query.where(dataAnalysis.sample.eq(dataAnalysisParam.getSample()));
    query.where(dataAnalysis.status.eq(DataAnalysis.Status.TO_DO));
    query.where(dataAnalysis.ne(dataAnalysisParam));
    return query.fetchCount() > 0;
  }

  @Override
  public void update(DataAnalysis dataAnalysis, String justification) {
    authorizationService.checkAdminRole();

    // Update sample status.
    if (dataAnalysis.getStatus() != DataAnalysis.Status.TO_DO
        && !existsTodoBySampleWithExclude(dataAnalysis)) {
      dataAnalysis.getSample().setStatus(SubmissionSample.Status.ANALYSED);
    } else {
      dataAnalysis.getSample().setStatus(SubmissionSample.Status.DATA_ANALYSIS);
    }

    // Log update of data analysis.
    Optional<Activity> activity = dataAnalysisActivityService.update(dataAnalysis, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(dataAnalysis);
    entityManager.merge(dataAnalysis.getSample());
  }
}
