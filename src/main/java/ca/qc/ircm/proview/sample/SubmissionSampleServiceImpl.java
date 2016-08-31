package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;
import static ca.qc.ircm.proview.sample.QMoleculeSample.moleculeSample;
import static ca.qc.ircm.proview.sample.QProteicSample.proteicSample;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.DATA_ANALYSIS;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.RECEIVED;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.TO_ANALYSE;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.TO_APPROVE;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.TO_DIGEST;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.TO_ENRICH;
import static ca.qc.ircm.proview.sample.SubmissionSample.Status.TO_RECEIVE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QUser.user;

import com.google.common.base.Optional;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service class for Sample.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionSampleServiceImpl implements SubmissionSampleService {
  private static class ReportDefault implements Report {
    private List<SubmissionSample> samples;
    private Map<SubmissionSample, Boolean> linkedToResults;

    private ReportDefault(List<SubmissionSample> samples,
        Map<SubmissionSample, Boolean> linkedToResults) {
      this.samples = samples;
      this.linkedToResults = linkedToResults;
    }

    @Override
    public List<SubmissionSample> getSamples() {
      return samples;
    }

    @Override
    public Map<SubmissionSample, Boolean> getLinkedToResults() {
      return linkedToResults;
    }
  }

  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SampleActivityService sampleActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private PricingEvaluator pricingEvaluator;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionSampleServiceImpl() {
  }

  protected SubmissionSampleServiceImpl(EntityManager entityManager,
      JPAQueryFactory queryFactory, SampleActivityService sampleActivityService,
      ActivityService activityService, PricingEvaluator pricingEvaluator,
      AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.pricingEvaluator = pricingEvaluator;
    this.authorizationService = authorizationService;
  }

  @Override
  public SubmissionSample get(Long id) {
    if (id == null) {
      return null;
    }

    SubmissionSample sample = entityManager.find(SubmissionSample.class, id);
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  @Override
  public SubmissionSample getSubmission(String name) {
    if (name == null) {
      return null;
    }

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.where(submissionSample.name.eq(name));
    SubmissionSample sample = query.fetchOne();
    authorizationService.checkSampleReadPermission(sample);
    return sample;
  }

  @Override
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }
    authorizationService.checkUserRole();

    JPAQuery<Long> query = queryFactory.select(submissionSample.id);
    query.from(submissionSample);
    query.where(submissionSample.name.eq(name));
    return query.fetchCount() > 0;
  }

  @Override
  public Report report(SampleFilter filter) {
    authorizationService.checkUserRole();

    return report(filter, false);
  }

  private Report report(SampleFilter filter, boolean admin) {
    if (filter == null) {
      filter = new SampleFilterBean();
    }

    final List<SubmissionSample> samples = fetchReportSamples(filter, admin);

    List<Tuple> tuples;
    if (!samples.isEmpty()) {
      JPAQuery<Tuple> query = queryFactory.select(sample.id, acquisitionMascotFile.count());
      query.from(sample);
      query.join(acquisition);
      query.join(acquisitionMascotFile);
      query.where(sample.in(samples));
      query.where(acquisition.sample.eq(sample));
      query.where(acquisitionMascotFile.acquisition.eq(acquisition));
      if (!admin) {
        query.where(acquisitionMascotFile.visible.eq(true));
      }
      query.groupBy(sample.id);
      tuples = query.fetch();
    } else {
      tuples = Collections.emptyList();
    }
    final Map<SubmissionSample, Boolean> linkedToResults = new HashMap<>();
    final Map<Long, SubmissionSample> samplesById = new HashMap<>();
    for (SubmissionSample sample : samples) {
      samplesById.put(sample.getId(), sample);
      linkedToResults.put(sample, false);
    }
    for (Tuple tuple : tuples) {
      SubmissionSample actualSample = samplesById.get(tuple.get(sample.id));
      linkedToResults.put(actualSample, tuple.get(acquisitionMascotFile.count()) > 0);
    }
    return new ReportDefault(samples, linkedToResults);
  }

  private List<SubmissionSample> fetchReportSamples(SampleFilter filter, boolean admin) {
    final User _user;
    final Laboratory _laboratory;
    boolean manager;
    if (admin) {
      _user = null;
      _laboratory = null;
      manager = false;
    } else {
      _user = authorizationService.getCurrentUser();
      _laboratory = _user.getLaboratory();
      manager = authorizationService.hasLaboratoryManagerPermission(_laboratory);
    }

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.join(submissionSample.submission, submission).fetch();
    query.join(submission.laboratory, laboratory);
    query.join(submission.user, user);
    if (filter.getExperienceContains() != null) {
      query.where(submissionSample.instanceOfAny(GelSample.class, EluateSample.class));
      query.from(proteicSample);
      query.where(proteicSample.eq(submissionSample));
      query.where(proteicSample.experience.contains(filter.getExperienceContains()));
    }
    if (filter.getLaboratoryContains() != null) {
      query.where(laboratory.organization.contains(filter.getLaboratoryContains()));
    }
    if (filter.getLaboratory() != null) {
      query.where(laboratory.eq(filter.getLaboratory()));
    }
    if (filter.getLimsContains() != null) {
      query.where(submissionSample.lims.contains(filter.getLimsContains()));
    }
    if (filter.getMinimalSubmissionDate() != null) {
      query.where(submission.submissionDate.goe(filter.getMinimalSubmissionDate()));
    }
    if (filter.getMaximalSubmissionDate() != null) {
      query.where(submission.submissionDate.loe(filter.getMaximalSubmissionDate()));
    }
    if (filter.getNameContains() != null) {
      query.where(submissionSample.name.contains(filter.getNameContains()));
    }
    if (filter.getProjectContains() != null) {
      query.where(submissionSample.instanceOfAny(GelSample.class, EluateSample.class));
      query.from(proteicSample);
      query.where(proteicSample.eq(submissionSample));
      query.where(proteicSample.project.contains(filter.getProjectContains()));
    }
    if (filter.getStatuses() != null) {
      query.where(submissionSample.status.in(filter.getStatuses()));
    }
    if (filter.getSupport() != null) {
      if (filter.getSupport() == SubmissionSampleService.Support.SOLUTION) {
        query.where(submissionSample.instanceOf(EluateSample.class));
        query.where(submissionSample.service.ne(Service.INTACT_PROTEIN));
      } else if (filter.getSupport() == SubmissionSampleService.Support.GEL) {
        query.where(submissionSample.instanceOf(GelSample.class));
        query.where(submissionSample.service.ne(Service.INTACT_PROTEIN));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_HIGH) {
        query.where(submissionSample.instanceOf(MoleculeSample.class));
        query.from(moleculeSample);
        query.where(moleculeSample.eq(submissionSample));
        query.where(moleculeSample.highResolution.eq(true));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_LOW) {
        query.where(submissionSample.instanceOf(MoleculeSample.class));
        query.from(moleculeSample);
        query.where(moleculeSample.eq(submissionSample));
        query.where(moleculeSample.highResolution.eq(false));
      } else if (filter.getSupport() == SubmissionSampleService.Support.INTACT_PROTEIN) {
        query.where(submissionSample.service.eq(Service.INTACT_PROTEIN));
      }
    }
    if (filter.getUserContains() != null) {
      query.where(user.name.contains(filter.getUserContains()));
    }
    if (filter.getUser() != null) {
      query.where(user.eq(filter.getUser()));
    }
    if (!admin) {
      if (manager) {
        query.where(laboratory.eq(_laboratory));
      } else {
        query.where(user.eq(_user));
      }
    }
    return query.fetch();
  }

  @Override
  public Report adminReport(SampleFilter filter) {
    authorizationService.checkAdminRole();

    return report(filter, true);
  }

  @Override
  public List<SubmissionSample> sampleMonitoring() {
    authorizationService.checkAdminRole();

    JPAQuery<SubmissionSample> query = queryFactory.select(submissionSample);
    query.from(submissionSample);
    query.join(submissionSample.submission, submission).fetch();
    query.join(submission.laboratory, laboratory).fetch();
    query.join(submission.user, user).fetch();
    query.join(submissionSample.originalContainer).fetch();
    query.where(submissionSample.status.in(TO_APPROVE, TO_RECEIVE, RECEIVED, TO_DIGEST, TO_ENRICH,
        TO_ANALYSE, DATA_ANALYSIS));
    return query.fetch();
  }

  @Override
  public List<String> projects() {
    authorizationService.checkUserRole();
    User user = authorizationService.getCurrentUser();

    JPAQuery<String> query = queryFactory.select(proteicSample.project);
    query.from(proteicSample);
    query.join(proteicSample.submission, submission);
    query.where(submission.user.eq(user));
    return query.distinct().fetch();
  }

  @Override
  public void updateStatus(Collection<? extends SubmissionSample> samples) {
    authorizationService.checkAdminRole();

    for (SubmissionSample sample : samples) {
      // Log changes.
      Optional<Activity> activity = sampleActivityService.update(sample, null);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }

      entityManager.merge(sample);
    }
  }

  @Override
  public BigDecimal computePrice(SubmissionSample sample, Date date) {
    if (sample == null || date == null) {
      return null;
    }

    return pricingEvaluator.computePrice(sample, date);
  }
}
