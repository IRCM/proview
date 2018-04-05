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

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QLaboratory.laboratory;
import static ca.qc.ircm.proview.user.QUser.user;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
  private TemplateEngine emailTemplateEngine;
  @Inject
  private EmailService emailService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionService() {
  }

  protected SubmissionService(EntityManager entityManager, JPAQueryFactory queryFactory,
      SubmissionActivityService submissionActivityService, ActivityService activityService,
      PricingEvaluator pricingEvaluator, TemplateEngine emailTemplateEngine,
      EmailService emailService, AuthorizationService authorizationService) {
    this.entityManager = entityManager;
    this.queryFactory = queryFactory;
    this.submissionActivityService = submissionActivityService;
    this.activityService = activityService;
    this.pricingEvaluator = pricingEvaluator;
    this.emailTemplateEngine = emailTemplateEngine;
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
    return all(null);
  }

  /**
   * Returns current user's submissions.<br>
   * For managers, returns all submissions made a user of his laboratory<br>
   * For administrators, returns all submissions.
   *
   * @param filter
   *          filter submissions to return
   * @return current user's submissions or more for managers / administrators
   */
  public List<Submission> all(SubmissionFilter filter) {
    authorizationService.checkUserRole();

    JPAQuery<Submission> query = queryFactory.select(submission);
    initializeAllQuery(query);
    if (filter != null) {
      filter.addConditions(query);
    }
    return query.distinct().fetch();
  }

  /**
   * Returns number of current user's submissions.<br>
   * For managers, returns number of submissions made a user of his laboratory<br>
   * For administrators, returns number of submissions.
   *
   * @param filter
   *          filter submissions to return
   * @return current user's submissions or more for managers / administrators
   */
  public int count(SubmissionFilter filter) {
    authorizationService.checkUserRole();

    JPAQuery<Long> query = queryFactory.select(submission.id.countDistinct());
    initializeAllQuery(query);
    if (filter != null) {
      filter.addCountConditions(query);
    }
    return query.fetchFirst().intValue();
  }

  private void initializeAllQuery(JPAQuery<?> query) {
    final User currentUser = authorizationService.getCurrentUser();
    final Laboratory currentLaboratory = currentUser.getLaboratory();

    query.from(submission);
    if (!authorizationService.hasAdminRole()) {
      if (authorizationService.hasLaboratoryManagerPermission(currentLaboratory)) {
        query.where(submission.laboratory.eq(currentLaboratory));
      } else {
        query.where(submission.user.eq(currentUser));
      }
    }
  }

  public String print(Submission submission, Locale locale) {
    if (submission == null || submission.getService() == null || submission.getSamples() == null
        || submission.getSamples().isEmpty() || submission.getSamples().get(0) == null
        || submission.getSamples().get(0).getType() == null || locale == null) {
      return "";
    }

    Context context = new Context();
    context.setLocale(locale);
    context.setVariable("submission", submission);
    context.setVariable("submissionDate",
        submission.getSubmissionDate() != null ? Date.from(submission.getSubmissionDate()) : null);
    context.setVariable("locale", locale);
    context.setVariable("user", submission.getUser());
    context.setVariable("laboratory", submission.getLaboratory());
    if (submission.getSamples() != null && !submission.getSamples().isEmpty()) {
      SubmissionSample sample = submission.getSamples().get(0);
      context.setVariable("sample", sample);
      if (sample.getOriginalContainer() != null
          && sample.getOriginalContainer().getType() == SampleContainerType.WELL) {
        Plate plate = ((Well) sample.getOriginalContainer()).getPlate();
        context.setVariable("plate", plate);
      }
    }

    String templateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Print.html";
    String content = emailTemplateEngine.process(templateLocation, context);
    return content;
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
    Plate plate = plate(submission);
    submission.getSamples().forEach(sample -> {
      sample.setSubmission(submission);
      sample.setStatus(SampleStatus.TO_APPROVE);
    });

    entityManager.persist(submission);
    if (plate != null) {
      persistPlate(submission, plate);
    } else {
      persistTubes(submission);
    }

    logger.info("Submission {} added to database", submission);

    // Send email to admin users to inform them of the submission.
    try {
      this.sendSubmissionToAdmins(submission, ActionType.INSERT);
    } catch (MessagingException e) {
      logger.error("Could not send email containing new submitted samples", e);
    }

    entityManager.flush();
    // Log insertion to database.
    Activity activity = submissionActivityService.insert(submission);
    activityService.insert(activity);
  }

  private Plate plate(Submission submission) {
    return submission.getSamples().stream()
        .filter(sample -> sample.getOriginalContainer() != null
            && sample.getOriginalContainer().getType() == SampleContainerType.WELL)
        .findAny().map(sample -> ((Well) sample.getOriginalContainer()).getPlate()).orElse(null);
  }

  private void persistPlate(Submission submission, Plate plate) {
    plate.setInsertTime(Instant.now());
    plate.setSubmission(true);
    plate.getWells().forEach(well -> well.setTimestamp(Instant.now()));
    submission.getSamples().forEach(sample -> {
      sample.getOriginalContainer().setSample(sample);
    });
    entityManager.persist(plate);
  }

  private void persistTubes(Submission submission) {
    Set<String> otherTubeNames = new HashSet<>();
    submission.getSamples().forEach(sample -> persistTube(sample, otherTubeNames));
  }

  private void persistTube(SubmissionSample sample, Set<String> otherTubeNames) {
    Tube tube = new Tube();
    tube.setSample(sample);
    tube.setName(sample.getName());
    tube.setTimestamp(Instant.now());
    otherTubeNames.add(tube.getName());
    sample.setOriginalContainer(tube);
    entityManager.persist(tube);
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
   * Sends an email to all admisn containing a summary of submission.
   *
   * @param submission
   *          submission
   * @param type
   *          type
   */
  private void sendSubmissionToAdmins(Submission submission, ActionType type)
      throws MessagingException {
    Context context = new Context();
    context.setVariable("tab", "\t");
    context.setVariable("type", type.name());
    context.setVariable("submission", submission);
    context.setVariable("user", submission.getUser());
    context.setVariable("sampleCount", submission.getSamples().size());
    context.setVariable("samples", submission.getSamples());
    context.setVariable("containerType",
        submission.getSamples().get(0).getOriginalContainer().getType());

    MessageResource resource = new MessageResource(SubmissionService.class, Locale.ENGLISH);
    final List<User> proteomicUsers = adminUsers();
    MimeMessageHelper email = emailService.htmlEmail();
    email.setSubject(resource.message("subject." + type.name()));
    String htmlTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.html";
    String htmlEmail = emailTemplateEngine.process(htmlTemplateLocation, context);
    String textTemplateLocation =
        "/" + SubmissionService.class.getName().replace(".", "/") + "_Email.txt";
    String textEmail = emailTemplateEngine.process(textTemplateLocation, context);
    email.setText(textEmail, htmlEmail);
    for (User proteomicUser : proteomicUsers) {
      email.addTo(proteomicUser.getEmail());
    }
    emailService.send(email);
  }

  /**
   * Updates submission. <br>
   * <strong>Will only work if all samples status are {@link SampleStatus#TO_APPROVE}</strong>
   *
   * @param submission
   *          submission with new information
   * @throws IllegalArgumentException
   *           samples don't all have {@link SampleStatus#TO_APPROVE} status
   */
  public void update(Submission submission) throws IllegalArgumentException {
    if (submission.getSamples().stream()
        .filter(
            sample -> sample.getStatus() != null && sample.getStatus() != SampleStatus.TO_APPROVE)
        .findAny().isPresent()) {
      throw new IllegalArgumentException("Cannot update submission if samples don't have "
          + SampleStatus.TO_APPROVE.name() + " status");
    }
    authorizationService.checkSubmissionWritePermission(submission);

    Submission old = entityManager.find(Submission.class, submission.getId());
    submission.setUser(old.getUser());
    submission.setLaboratory(old.getLaboratory());
    submission.setSubmissionDate(old.getSubmissionDate());
    submission.setPrice(pricingEvaluator.computePrice(submission, submission.getSubmissionDate()));
    updateNoSecurity(submission);

    Activity activity = submissionActivityService.update(submission);
    activityService.insert(activity);

    // Send email to admin users to inform them of the changes in submission.
    try {
      this.sendSubmissionToAdmins(submission, ActionType.UPDATE);
    } catch (MessagingException e) {
      logger.error("Could not send email containing new submitted samples", e);
    }
  }

  private void updateNoSecurity(Submission submission) throws IllegalArgumentException {
    Submission old = entityManager.find(Submission.class, submission.getId());
    removeUnusedContainers(submission, old);
    submission.getSamples().forEach(sample -> {
      sample.setSubmission(submission);
      if (sample.getId() == null) {
        sample.setStatus(SampleStatus.TO_APPROVE);
        entityManager.persist(sample);
      }
    });

    entityManager.merge(submission);
    Plate plate = plate(submission);
    if (plate != null) {
      if (plate.getId() == null) {
        persistPlate(submission, plate);
      } else {
        entityManager.merge(plate);
      }
    } else {
      Set<String> otherTubeNames = new HashSet<>();
      for (SubmissionSample sample : submission.getSamples()) {
        Tube tube = (Tube) sample.getOriginalContainer();
        if (tube == null || tube.getId() == null) {
          persistTube(sample, otherTubeNames);
        } else {
          tube.setName(sample.getName());
          entityManager.merge(tube);
        }
      }
    }
  }

  private void removeUnusedContainers(Submission submission, Submission old) {
    Plate plate = plate(submission);
    Plate oldPlate = plate(old);
    if (oldPlate != null && (plate == null || !oldPlate.getId().equals(plate.getId()))) {
      entityManager.remove(plate);
    }
    Set<Long> sampleIds = submission.getSamples().stream().filter(sample -> sample.getId() != null)
        .map(sample -> sample.getId()).collect(Collectors.toSet());
    old.getSamples().stream().filter(sample -> !sampleIds.contains(sample.getId())
        && sample.getOriginalContainer().getType() == TUBE).forEach(sample -> {
          entityManager.remove(sample.getOriginalContainer());
        });
    // Populate tube ids.
    submission.getSamples().stream()
        .filter(sample -> sample.getId() != null && sample.getOriginalContainer().getType() == TUBE)
        .forEach(sample -> {
          old.getSamples().stream().filter(oldSample -> oldSample.getId() == sample.getId())
              .findAny().ifPresent(oldSample -> {
                sample.getOriginalContainer().setId(oldSample.getId());
              });
        });
  }

  /**
   * Forces submission update.
   *
   * @param submission
   *          submission with new information
   * @param explanation
   *          explanation for changes made to submission
   */
  public void forceUpdate(Submission submission, String explanation) {
    authorizationService.checkAdminRole();

    final Submission old = entityManager.find(Submission.class, submission.getId());
    entityManager.detach(old);

    submission.setLaboratory(submission.getUser().getLaboratory());
    submission.setPrice(pricingEvaluator.computePrice(submission, submission.getSubmissionDate()));
    updateNoSecurity(submission);
    entityManager.flush();

    // Log update to database.
    Optional<Activity> activity =
        submissionActivityService.forceUpdate(submission, explanation, old);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }
  }

  /**
   * Approve analysis of all samples in submissions.
   *
   * @param submissions
   *          submissions
   */
  public void approve(Collection<Submission> submissions) {
    authorizationService.checkApproverRole();

    for (Submission submission : submissions) {
      submission.getSamples().stream()
          .filter(sample -> sample.getStatus() == SampleStatus.TO_APPROVE)
          .forEach(sample -> sample.setStatus(SampleStatus.APPROVED));
      entityManager.merge(submission);
      Optional<Activity> activity = submissionActivityService.approve(submission);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }
    }
  }
}
