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
import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.user.QUser.user;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.mail.EmailService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateRepository;
import ca.qc.ircm.proview.plate.QPlate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeRepository;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.utils.MessageResource;
import com.google.common.collect.Lists;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for submission.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionService {
  private final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
  @Inject
  private SubmissionRepository repository;
  @Inject
  private SubmissionSampleRepository sampleRepository;
  @Inject
  private PlateRepository plateRepository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private UserRepository userRepository;
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

  /**
   * Selects submission from database.
   *
   * @param id
   *          database identifier of submission
   * @return submission
   */
  @PostAuthorize("returnObject == null || hasPermission(returnObject, 'read')")
  public Submission get(Long id) {
    if (id == null) {
      return null;
    }

    return repository.findOne(id);
  }

  /**
   * Selects submission related to this plate.
   *
   * @param plate
   *          plate
   * @return submission related to this plate
   */
  @PreAuthorize("hasPermission(#plate, 'read')")
  public Submission get(Plate plate) {
    if (plate == null) {
      return null;
    }

    QPlate qplate = QPlate.plate;
    JPAQuery<Submission> query = queryFactory.select(submissionSample.submission);
    query.from(qplate, submissionSample);
    query.where(qplate.eq(plate));
    query.where(qplate.submission.eq(true));
    query.where(submissionSample.originalContainer.in(qplate.wells));
    return query.fetchFirst();
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
  @PreAuthorize("hasAuthority('" + USER + "')")
  public List<Submission> all(SubmissionFilter filter) {
    JPAQuery<Submission> query = queryFactory.select(submission);
    initializeAllQuery(query);
    if (filter != null) {
      query.where(filter.predicate());
      if (filter.sortOrders != null) {
        query.orderBy(filter.sortOrders.toArray(new OrderSpecifier[0]));
      }
      if (filter.offset != null) {
        query.offset(filter.offset);
      }
      if (filter.limit != null) {
        query.limit(filter.limit);
      }
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
  @PreAuthorize("hasAuthority('" + USER + "')")
  public int count(SubmissionFilter filter) {
    JPAQuery<Long> query = queryFactory.select(submission.id.countDistinct());
    initializeAllQuery(query);
    if (filter != null) {
      query.where(filter.predicate());
    }
    return query.fetchFirst().intValue();
  }

  private void initializeAllQuery(JPAQuery<?> query) {
    final User currentUser = authorizationService.getCurrentUser();
    final Laboratory currentLaboratory = currentUser.getLaboratory();

    query.from(submission);
    if (!authorizationService.hasRole(UserRole.ADMIN)) {
      query.where(submission.hidden.eq(false));
      if (authorizationService.hasPermission(currentLaboratory, BasePermission.WRITE)) {
        query.where(submission.laboratory.eq(currentLaboratory));
      } else {
        query.where(submission.user.eq(currentUser));
      }
    }
  }

  /**
   * Returns printable version of submission in HTML.
   *
   * @param submission
   *          submission
   * @param locale
   *          user's locale
   * @return printable version of submission in HTML
   */
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
  @PreAuthorize("hasAuthority('" + USER + "')")
  public void insert(Submission submission) {
    User user = authorizationService.getCurrentUser();
    Laboratory laboratory = user.getLaboratory();

    submission.setLaboratory(laboratory);
    submission.setUser(user);
    submission.setSubmissionDate(Instant.now());
    submission.setPrice(pricingEvaluator.computePrice(submission, submission.getSubmissionDate()));
    Plate plate = plate(submission);
    submission.getSamples().forEach(sample -> {
      sample.setSubmission(submission);
      sample.setStatus(SampleStatus.WAITING);
    });

    repository.save(submission);
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

    repository.flush();
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
    plateRepository.save(plate);
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
    tubeRepository.save(tube);
  }

  private List<User> adminUsers() {
    BooleanExpression predicate =
        user.admin.eq(true).and(user.valid.eq(true)).and(user.active.eq(true)).and(user.id.ne(1L));
    return Lists.newArrayList(userRepository.findAll(predicate));
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
   * <strong>Will only work if all samples status are {@link SampleStatus#WAITING}</strong>
   *
   * @param submission
   *          submission with new information
   * @param explanation
   *          explanation for changes made to submission
   * @throws IllegalArgumentException
   *           samples don't all have {@link SampleStatus#WAITING} status
   */
  @PreAuthorize("hasPermission(#submission, 'write')")
  public void update(Submission submission, String explanation) throws IllegalArgumentException {
    validateUpdateSubmission(submission);
    if (!authorizationService.hasRole(UserRole.ADMIN)
        && anyStatusGreaterOrEquals(submission, SampleStatus.RECEIVED)) {
      Submission userSupplied = submission;
      submission = repository.findOne(submission.getId());
      for (int i = 0; i < submission.getSamples().size(); i++) {
        SubmissionSample sample = submission.getSamples().get(i);
        sample.setName(userSupplied.getSamples().get(i).getName());
        if (sample.getOriginalContainer() instanceof Tube) {
          Tube tube = (Tube) sample.getOriginalContainer();
          tube.setName(userSupplied.getSamples().get(i).getOriginalContainer().getName());
        } else {
          Plate plate = ((Well) sample.getOriginalContainer()).getPlate();
          Plate userSuppliedPlate =
              ((Well) userSupplied.getSamples().get(i).getOriginalContainer()).getPlate();
          plate.setName(userSuppliedPlate.getName());
        }
      }
    }

    doUpdate(submission);

    Optional<Activity> activity = submissionActivityService.update(submission, explanation);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
      repository.flush();

      if (!authorizationService.hasRole(UserRole.ADMIN)) {
        // Send email to admin users to inform them of the changes in submission.
        try {
          this.sendSubmissionToAdmins(submission, ActionType.UPDATE);
        } catch (MessagingException e) {
          logger.error("Could not send email containing new submitted samples", e);
        }
      }
    }
  }

  private void validateUpdateSubmission(Submission submission) {
    if (!authorizationService.hasRole(UserRole.ADMIN)) {
      Submission old = repository.findOne(submission.getId());
      if (!old.getUser().getId().equals(submission.getUser().getId())) {
        throw new IllegalArgumentException("Cannot update submission's owner");
      }

      if (anyStatusGreaterOrEquals(submission, SampleStatus.RECEIVED)) {
        throw new IllegalArgumentException("Cannot update submission if samples don't have "
            + SampleStatus.WAITING.name() + " status or less");
      }
    }
  }

  private boolean anyStatusGreaterOrEquals(Submission submission, SampleStatus status) {
    return submission.getSamples().stream()
        .filter(sample -> sample.getStatus() != null && status.compareTo(sample.getStatus()) <= 0)
        .findAny().isPresent();
  }

  private void doUpdate(Submission submission) throws IllegalArgumentException {
    Submission old = repository.findOne(submission.getId());
    submission.setLaboratory(submission.getUser().getLaboratory());

    removeUnusedContainers(submission, old);
    submission.getSamples().forEach(sample -> {
      sample.setSubmission(submission);
      if (sample.getId() == null) {
        sample.setStatus(SampleStatus.WAITING);
        sampleRepository.save(sample);
      }
    });

    repository.save(submission);
    Plate plate = plate(submission);
    if (plate != null) {
      if (plate.getId() == null) {
        persistPlate(submission, plate);
      } else {
        plateRepository.save(plate);
      }
    } else {
      Set<String> otherTubeNames = new HashSet<>();
      for (SubmissionSample sample : submission.getSamples()) {
        Tube tube = (Tube) sample.getOriginalContainer();
        if (tube == null || tube.getId() == null) {
          persistTube(sample, otherTubeNames);
        } else {
          tube.setName(sample.getName());
          tubeRepository.save(tube);
        }
      }
    }
  }

  private void removeUnusedContainers(Submission submission, Submission old) {
    Plate plate = plate(submission);
    Plate oldPlate = plate(old);
    if (oldPlate != null && (plate == null || !oldPlate.getId().equals(plate.getId()))) {
      plateRepository.delete(plate);
    }
    Set<Long> sampleIds = submission.getSamples().stream().filter(sample -> sample.getId() != null)
        .map(sample -> sample.getId()).collect(Collectors.toSet());
    old.getSamples().stream().filter(sample -> !sampleIds.contains(sample.getId())
        && sample.getOriginalContainer().getType() == TUBE).forEach(sample -> {
          logger.debug("Remove tube {}", sample.getOriginalContainer());
          tubeRepository.delete((Tube) sample.getOriginalContainer());
        });
    // Populate tube ids.
    submission.getSamples().stream()
        .filter(sample -> sample.getId() != null && sample.getOriginalContainer().getType() == TUBE)
        .forEach(sample -> {
          old.getSamples().stream().filter(oldSample -> oldSample.getId().equals(sample.getId()))
              .findAny().ifPresent(oldSample -> {
                sample.getOriginalContainer().setId(oldSample.getOriginalContainer().getId());
              });
        });
  }

  /**
   * Hide submission.
   *
   * @param submission
   *          submission
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void hide(Submission submission) {
    submission = repository.findOne(submission.getId());
    submission.setHidden(true);
    repository.save(submission);

    Optional<Activity> activity = submissionActivityService.update(submission, null);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }
  }

  /**
   * Make submission visible, if it is hidden.
   *
   * @param submission
   *          submission
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void show(Submission submission) {
    submission = repository.findOne(submission.getId());
    submission.setHidden(false);
    repository.save(submission);

    Optional<Activity> activity = submissionActivityService.update(submission, null);
    if (activity.isPresent()) {
      activityService.insert(activity.get());
    }
  }
}
