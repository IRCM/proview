package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.laboratory.QLaboratory.laboratory;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.mail.HtmlEmailDefault;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.user.User;
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
import java.util.ArrayList;
import java.util.Date;
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
  @Deprecated
  public List<GelImage> gelImages(Submission submission) {
    if (submission == null) {
      return new ArrayList<>();
    }
    authorizationService.checkSubmissionReadPermission(submission);

    return submission.getGelImages();
  }

  @Override
  public void insert(Submission submission) {
    authorizationService.checkUserRole();
    User user = authorizationService.getCurrentUser();
    Laboratory laboratory = user.getLaboratory();

    submission.setLaboratory(laboratory);
    submission.setUser(user);
    Set<String> otherSampleLims = new HashSet<String>();
    Set<String> otherTubeNames = new HashSet<String>();
    for (SubmissionSample sample : submission.getSamples()) {
      generateLims(sample, laboratory, otherSampleLims);
      sample.setSubmission(submission);
      sample.setStatus(Status.TO_APPROVE);
      sample.setPrice(pricingEvaluator.computePrice(sample, submission.getSubmissionDate()));
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
