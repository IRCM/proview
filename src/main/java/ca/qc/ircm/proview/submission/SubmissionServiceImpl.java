/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QAcquisitionMascotFile.acquisitionMascotFile;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.mail.HtmlEmailDefault;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Default implementation of submission services.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {
  private static class ReportDefault implements Report {
    private List<Submission> submissions;
    private Map<Submission, Boolean> linkedToResults;

    private ReportDefault(List<Submission> submissions, Map<Submission, Boolean> linkedToResults) {
      this.submissions = submissions;
      this.linkedToResults = linkedToResults;
    }

    @Override
    public List<Submission> getSubmissions() {
      return submissions;
    }

    @Override
    public Map<Submission, Boolean> getLinkedToResults() {
      return linkedToResults;
    }
  }

  private static final String LIMS_RANDOM_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
  private final Logger logger = LoggerFactory.getLogger(SubmissionServiceImpl.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Inject
  private SubmissionActivityService submissionActivityService;
  @Inject
  private ActivityService activityService;
  @Inject
  private PricingEvaluator pricingEvaluator;
  @Inject
  private TemplateEngine templateEngine;
  @Inject
  private TubeService tubeService;
  @Inject
  private EmailService emailService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionServiceImpl() {
  }

  protected SubmissionServiceImpl(EntityManager entityManager, JPAQueryFactory queryFactory,
      SubmissionActivityService submissionActivityService, ActivityService activityService,
      PricingEvaluator pricingEvaluator, TemplateEngine templateEngine, TubeService tubeService,
      EmailService emailService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.submissionActivityService = submissionActivityService;
    this.activityService = activityService;
    this.pricingEvaluator = pricingEvaluator;
    this.templateEngine = templateEngine;
    this.tubeService = tubeService;
    this.emailService = emailService;
    this.authorizationService = authorizationService;
  }

  @Override
  public Submission get(Long id) {
    if (id == null) {
      return null;
    }

    Submission submission = entityManager.find(Submission.class, id);
    authorizationService.checkSubmissionReadPermission(submission);
    return submission;
  }

  @Override
  public Report report(SubmissionFilter filter) {
    authorizationService.checkUserRole();
    return report(filter, false);
  }

  private Report report(SubmissionFilter filter, boolean admin) {
    if (filter == null) {
      filter = new SubmissionFilterBuilder().build();
    }

    final List<Submission> submissions = fetchReportSubmissions(filter, admin);

    List<Tuple> tuples;
    if (!submissions.isEmpty()) {
      JPAQuery<Tuple> query = queryFactory.select(submission.id, acquisitionMascotFile.count());
      query.from(submission);
      query.join(submission.samples, submissionSample);
      query.join(acquisition);
      query.join(acquisitionMascotFile);
      query.where(submission.in(submissions));
      query.where(acquisition.sample.eq(submissionSample._super));
      query.where(acquisitionMascotFile.acquisition.eq(acquisition));
      if (!admin) {
        query.where(acquisitionMascotFile.visible.eq(true));
      }
      query.groupBy(submission.id);
      tuples = query.fetch();
    } else {
      tuples = Collections.emptyList();
    }
    final Map<Submission, Boolean> linkedToResults = new HashMap<>();
    final Map<Long, Submission> submissionsById = new HashMap<>();
    for (Submission submission : submissions) {
      submissionsById.put(submission.getId(), submission);
      linkedToResults.put(submission, false);
    }
    for (Tuple tuple : tuples) {
      Submission actualSubmission = submissionsById.get(tuple.get(submission.id));
      linkedToResults.put(actualSubmission, tuple.get(acquisitionMascotFile.count()) > 0);
    }
    return new ReportDefault(submissions, linkedToResults);
  }

  private List<Submission> fetchReportSubmissions(SubmissionFilter filter, boolean admin) {
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

    JPAQuery<Submission> query = queryFactory.select(submission);
    query.from(submission);
    query.join(submission.samples, submissionSample).fetch();
    query.join(submission.laboratory, laboratory);
    query.join(submission.user, user);
    if (filter.getExperienceContains() != null) {
      query.where(submission.experience.contains(filter.getExperienceContains()));
    }
    if (filter.getLaboratoryContains() != null) {
      query.where(laboratory.organization.eq(filter.getLaboratoryContains()));
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
      query.where(submission.project.contains(filter.getProjectContains()));
    }
    if (filter.getStatuses() != null) {
      query.where(submissionSample.status.in(filter.getStatuses()));
    }
    if (filter.getSupport() != null) {
      if (filter.getSupport() == SubmissionSampleService.Support.SOLUTION) {
        query.where(submissionSample.support.in(Sample.Support.SOLUTION, Sample.Support.DRY));
        query.where(submission.service.notIn(Service.INTACT_PROTEIN, Service.SMALL_MOLECULE));
      } else if (filter.getSupport() == SubmissionSampleService.Support.GEL) {
        query.where(submissionSample.support.eq(Sample.Support.GEL));
        query.where(submission.service.ne(Service.INTACT_PROTEIN));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_HIGH) {
        query.where(submission.service.eq(Service.SMALL_MOLECULE));
        query.where(submission.highResolution.eq(true));
      } else if (filter.getSupport() == SubmissionSampleService.Support.MOLECULE_LOW) {
        query.where(submission.service.eq(Service.SMALL_MOLECULE));
        query.where(submission.highResolution.eq(false));
      } else if (filter.getSupport() == SubmissionSampleService.Support.INTACT_PROTEIN) {
        query.where(submission.service.eq(Service.INTACT_PROTEIN));
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
  public Report adminReport(SubmissionFilter filter) {
    authorizationService.checkAdminRole();
    return report(filter, true);
  }

  @Override
  public void insert(Submission submission) {
    authorizationService.checkUserRole();
    User user = authorizationService.getCurrentUser();
    Laboratory laboratory = user.getLaboratory();

    submission.setLaboratory(laboratory);
    submission.setUser(user);
    submission.setPrice(pricingEvaluator.computePrice(submission, submission.getSubmissionDate()));
    Set<String> otherSampleLims = new HashSet<String>();
    Set<String> otherTubeNames = new HashSet<String>();
    for (SubmissionSample sample : submission.getSamples()) {
      generateLims(sample, laboratory, otherSampleLims);
      sample.setSubmission(submission);
      sample.setStatus(SampleStatus.TO_APPROVE);
      Tube tube = new Tube();
      tube.setSample(sample);
      tube.setName(tubeService.generateTubeName(sample, otherTubeNames));
      tube.setTimestamp(Instant.now());
      otherTubeNames.add(tube.getName());
      sample.setOriginalContainer(tube);
    }

    entityManager.persist(submission);
    for (SubmissionSample sample : submission.getSamples()) {
      entityManager.persist(sample);
      entityManager.persist(sample.getOriginalContainer());
    }

    logger.info("Submission {} added to database", submission);

    // Send email to protemic users to inform them of the submission.
    try {
      Map<SubmissionSample, Tube> samplesWithTubes = new LinkedHashMap<SubmissionSample, Tube>();
      for (SubmissionSample submissionSample : submission.getSamples()) {
        samplesWithTubes.put(submissionSample, (Tube) submissionSample.getOriginalContainer());
      }
      this.sendSubmissionToProteomicUsers(samplesWithTubes, user);
    } catch (EmailException e) {
      logger.error("Could not send email containing new submitted samples", e);
    }

    entityManager.flush();
    // Log insertion to database.
    Activity activity = submissionActivityService.insert(submission);
    activityService.insert(activity);
  }

  private boolean existsByLims(String lims) {
    JPAQuery<Long> query = queryFactory.select(sample.id);
    query.from(sample);
    query.where(sample.lims.eq(lims));
    return query.fetchCount() > 0;
  }

  private void generateLims(SubmissionSample sample, Laboratory laboratory,
      Set<String> otherSampleLims) {
    String organization = laboratory.getOrganization().substring(0,
        Math.min(4, laboratory.getOrganization().length()));
    String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String random =
        RandomStringUtils.randomNumeric(1) + RandomStringUtils.random(3, LIMS_RANDOM_CHARACTERS);
    String lims = organization + date + "_" + random;
    while (otherSampleLims.contains(lims) || existsByLims(lims)) {
      random =
          RandomStringUtils.randomNumeric(1) + RandomStringUtils.random(3, LIMS_RANDOM_CHARACTERS);
      lims = organization + date + "_" + random;
    }
    otherSampleLims.add(lims);
    sample.setLims(lims);
  }

  private List<User> adminUsers() {
    JPAQuery<User> query = queryFactory.select(user);
    query.from(user);
    query.join(user.laboratory, laboratory);
    query.where(user.admin.eq(true));
    query.where(user.valid.eq(true));
    query.where(user.id.ne(1L));
    return query.fetch();
  }

  /**
   * Sends an email to all proteomic users containing a summary of submission.
   *
   * @param detailedSubmission
   *          submission
   * @param user
   *          user who submitted samples
   */
  private void sendSubmissionToProteomicUsers(Map<SubmissionSample, Tube> samplesWithTubes,
      User user) throws EmailException {
    Context context = new Context();
    context.setVariable("tab", "\t");
    context.setVariable("user", user);
    context.setVariable("sampleCount", samplesWithTubes.keySet().size());
    context.setVariable("samples", samplesWithTubes.keySet());
    context.setVariable("tubes", samplesWithTubes);

    final List<User> proteomicUsers = adminUsers();
    HtmlEmailDefault email = new HtmlEmailDefault();
    email.setSubject("New samples were submitted");
    String htmlTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.html";
    String htmlEmail = templateEngine.process(htmlTemplateLocation, context);
    email.setHtmlMessage(htmlEmail);
    String textTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.txt";
    String textEmail = templateEngine.process(textTemplateLocation, context);
    email.setTextMessage(textEmail);
    for (User proteomicUser : proteomicUsers) {
      email.addReceiver(proteomicUser.getEmail());
    }
    emailService.sendHtmlEmail(email);
  }

  @Override
  public void update(Submission submission, User owner, String justification) {
    authorizationService.checkAdminRole();

    if (owner != null) {
      submission.setUser(owner);
      submission.setLaboratory(owner.getLaboratory());
    } else {
      // Make sure owner is not updated.
      Submission oldSubmission = entityManager.find(Submission.class, submission.getId());
      entityManager.detach(oldSubmission);
      submission.setUser(oldSubmission.getUser());
      submission.setLaboratory(oldSubmission.getLaboratory());
    }

    // Log update to database.
    Optional<Activity> activity = submissionActivityService.update(submission, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(submission);
  }
}
