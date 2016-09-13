package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.Collection;

/**
 * Filters submissions to show.
 */
public interface SubmissionFilter {
  public String getExperienceContains();

  public String getLaboratoryContains();

  public Laboratory getLaboratory();

  public String getLimsContains();

  public Instant getMinimalSubmissionDate();

  public Instant getMaximalSubmissionDate();

  public String getNameContains();

  public String getProjectContains();

  public Collection<Status> getStatuses();

  public SubmissionSampleService.Support getSupport();

  public String getUserContains();

  public User getUser();
}
