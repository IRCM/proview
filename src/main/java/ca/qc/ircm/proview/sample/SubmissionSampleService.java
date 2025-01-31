package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Sample.
 */
@Service
@Transactional
public class SubmissionSampleService {

  private final SubmissionSampleRepository repository;
  private final SubmissionRepository submissionRepository;
  private final SampleActivityService sampleActivityService;
  private final ActivityService activityService;
  private final AuthenticatedUser authenticatedUser;

  @Autowired
  protected SubmissionSampleService(SubmissionSampleRepository repository,
      SubmissionRepository submissionRepository, SampleActivityService sampleActivityService,
      ActivityService activityService, AuthenticatedUser authenticatedUser) {
    this.repository = repository;
    this.submissionRepository = submissionRepository;
    this.sampleActivityService = sampleActivityService;
    this.activityService = activityService;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Selects submitted sample from database.
   *
   * @param id database identifier of submitted sample
   * @return submitted sample
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<SubmissionSample> get(long id) {
    return repository.findById(id);
  }

  /**
   * Returns true if a sample with this name is already in database for current user, false
   * otherwise.
   *
   * @param name name of sample
   * @return true if a sample with this name is already in database for current user, false
   * otherwise
   */
  @PreAuthorize("hasAuthority('" + USER + "')")
  public boolean exists(String name) {
    User currentUser = authenticatedUser.getUser().orElseThrow();

    BooleanExpression predicate =
        submissionSample.name.eq(name).and(submissionSample.submission.user.eq(currentUser));
    return repository.count(predicate) > 0;
  }

  /**
   * Update many sample's status.
   *
   * @param samples samples containing new status
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void updateStatus(Collection<? extends SubmissionSample> samples) {
    for (SubmissionSample sample : samples) {
      SampleStatus status = sample.getStatus();
      sample = repository.findById(sample.getId()).orElseThrow();
      sample.setStatus(status);
      // Log changes.
      sampleActivityService.updateStatus(sample).ifPresent(activityService::insert);

      if (SampleStatus.RECEIVED.equals(sample.getStatus())
          && sample.getSubmission().getSampleDeliveryDate() == null) {
        Submission submission = sample.getSubmission();
        submission.setSampleDeliveryDate(LocalDate.now());
        submissionRepository.save(submission);
      }
      if (SampleStatus.DIGESTED.equals(sample.getStatus())
          && sample.getSubmission().getDigestionDate() == null) {
        Submission submission = sample.getSubmission();
        submission.setDigestionDate(LocalDate.now());
        submissionRepository.save(submission);
      }
      if (SampleStatus.ANALYSED.equals(sample.getStatus())
          && sample.getSubmission().getAnalysisDate() == null) {
        Submission submission = sample.getSubmission();
        submission.setAnalysisDate(LocalDate.now());
        submissionRepository.save(submission);
      }
    }
  }
}
