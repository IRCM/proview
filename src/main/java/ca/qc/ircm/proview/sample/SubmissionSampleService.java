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

package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.USER;

import ca.qc.ircm.proview.history.Activity;
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
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Sample.
 */
@org.springframework.stereotype.Service
@Transactional
public class SubmissionSampleService {
  @Autowired
  private SubmissionSampleRepository repository;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Autowired
  private SampleActivityService sampleActivityService;
  @Autowired
  private ActivityService activityService;
  @Autowired
  private AuthenticatedUser authenticatedUser;

  protected SubmissionSampleService() {
  }

  /**
   * Selects submitted sample from database.
   *
   * @param id
   *          database identifier of submitted sample
   * @return submitted sample
   */
  @PostAuthorize("!returnObject.isPresent() || hasPermission(returnObject.get(), 'read')")
  public Optional<SubmissionSample> get(Long id) {
    if (id == null) {
      return Optional.empty();
    }

    return repository.findById(id);
  }

  /**
   * Returns true if a sample with this name is already in database for current user, false
   * otherwise.
   *
   * @param name
   *          name of sample
   * @return true if a sample with this name is already in database for current user, false
   *         otherwise
   */
  @PreAuthorize("hasAuthority('" + USER + "')")
  public boolean exists(String name) {
    if (name == null) {
      return false;
    }
    User currentUser = authenticatedUser.getCurrentUser().orElse(null);

    BooleanExpression predicate =
        submissionSample.name.eq(name).and(submissionSample.submission.user.eq(currentUser));
    return repository.count(predicate) > 0;
  }

  /**
   * Update many sample's status.
   *
   * @param samples
   *          samples containing new status
   */
  @PreAuthorize("hasAuthority('" + ADMIN + "')")
  public void updateStatus(Collection<? extends SubmissionSample> samples) {
    for (SubmissionSample sample : samples) {
      SampleStatus status = sample.getStatus();
      sample = repository.findById(sample.getId()).orElse(null);
      sample.setStatus(status);
      // Log changes.
      Optional<Activity> activity = sampleActivityService.updateStatus(sample);
      if (activity.isPresent()) {
        activityService.insert(activity.get());
      }

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
