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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Service for submission.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionService {
  private final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
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

  protected SubmissionService() {
  }

  protected SubmissionService(EntityManager entityManager, JPAQueryFactory queryFactory,
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

  /**
   * Selects submission from database.
   *
   * @param id
   *          database identifier of submission
   * @return submission
   */
  public Submission get(Long id) {
    if (id == null) {
      return null;
    }

    Submission submission = entityManager.find(Submission.class, id);
    authorizationService.checkSubmissionReadPermission(submission);
    return submission;
  }

  /**
   * Returns current user's submissions.<br>
   * For managers, returns all submissions made a user of his laboratory<br>
   * For administrators, returns all submissions.
   *
   * @return current user's submissions or more for managers / administrators
   */
  public List<Submission> all() {
    authorizationService.checkUserRole();
    final User currentUser = authorizationService.getCurrentUser();
    final Laboratory currentLaboratory = currentUser.getLaboratory();

    JPAQuery<Submission> query = queryFactory.select(submission);
    query.from(submission);
    query.join(submission.samples, submissionSample);
    query.join(submission.laboratory, laboratory);
    query.join(submission.user, user);
    if (!authorizationService.hasAdminRole()) {
      if (authorizationService.hasLaboratoryManagerPermission(currentLaboratory)) {
        query.where(laboratory.eq(currentLaboratory));
      } else {
        query.where(user.eq(currentUser));
      }
    }
    return query.distinct().fetch();
  }

  /**
   * Add a submission to database.<br>
   * Submission's date should not be older than yesterday.
   *
   * @param submission
   *          submission
   */
  public void insert(Submission submission) {
    authorizationService.checkUserRole();
    User user = authorizationService.getCurrentUser();
    Laboratory laboratory = user.getLaboratory();

    submission.setLaboratory(laboratory);
    submission.setUser(user);
    submission.setSubmissionDate(Instant.now());
    submission.setPrice(pricingEvaluator.computePrice(submission, submission.getSubmissionDate()));
    Set<String> otherTubeNames = new HashSet<>();
    Plate plate = null;
    for (SubmissionSample sample : submission.getSamples()) {
      sample.setSubmission(submission);
      sample.setStatus(SampleStatus.TO_APPROVE);
      if (sample.getOriginalContainer() == null) {
        Tube tube = new Tube();
        tube.setSample(sample);
        tube.setName(tubeService.generateTubeName(sample, otherTubeNames));
        tube.setTimestamp(Instant.now());
        otherTubeNames.add(tube.getName());
        sample.setOriginalContainer(tube);
      } else if (sample.getOriginalContainer().getType() == SampleContainerType.WELL) {
        if (plate == null) {
          plate = createSubmissionPlate(submission);
        }
        Well sourceWell = (Well) sample.getOriginalContainer();
        Well well = plate.well(sourceWell.getRow(), sourceWell.getColumn());
        well.setSample(sample);
        sample.setOriginalContainer(well);
      }
    }

    entityManager.persist(submission);
    if (plate != null) {
      entityManager.persist(plate);
    }
    for (SubmissionSample sample : submission.getSamples()) {
      entityManager.persist(sample);
      if (sample.getOriginalContainer().getType() == SampleContainerType.TUBE) {
        entityManager.persist(sample.getOriginalContainer());
      }
    }

    logger.info("Submission {} added to database", submission);

    // Send email to protemic users to inform them of the submission.
    try {
      this.sendSubmissionToProteomicUsers(submission.getSamples(), user);
    } catch (MessagingException e) {
      logger.error("Could not send email containing new submitted samples", e);
    }

    entityManager.flush();
    // Log insertion to database.
    Activity activity = submissionActivityService.insert(submission);
    activityService.insert(activity);
  }

  private Plate createSubmissionPlate(Submission submission) {
    Plate plate = new Plate();
    plate.setType(PlateType.SUBMISSION);
    plate.setName(submission.getExperience());
    plate.setInsertTime(Instant.now());
    plate.initWells();
    plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
    return plate;
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
  private void sendSubmissionToProteomicUsers(List<SubmissionSample> samples, User user)
      throws MessagingException {
    Context context = new Context();
    context.setVariable("tab", "\t");
    context.setVariable("user", user);
    context.setVariable("sampleCount", samples.size());
    context.setVariable("samples", samples);
    context.setVariable("containerType", samples.get(0).getOriginalContainer().getType());

    final List<User> proteomicUsers = adminUsers();
    MimeMessageHelper email = emailService.htmlEmail();
    email.setSubject("New samples were submitted");
    String htmlTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.html";
    String htmlEmail = templateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.txt";
    String textEmail = templateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);
    for (User proteomicUser : proteomicUsers) {
      email.addTo(proteomicUser.getEmail());
    }
    emailService.send(email);
  }

  /**
   * Forces submission update.
   *
   * @param submission
   *          submission with new information
   * @param justification
   *          justification for changes made to submission
   */
  public void forceUpdate(Submission submission, String justification) {
    authorizationService.checkAdminRole();

    submission.setLaboratory(submission.getUser().getLaboratory());

    // Log update to database.
    Optional<Activity> activity = submissionActivityService.update(submission, justification);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }

    entityManager.merge(submission);
  }
}
