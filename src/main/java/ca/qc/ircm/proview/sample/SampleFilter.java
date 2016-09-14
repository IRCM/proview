package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.Collection;

/**
 * Filters samples.
 */
public interface SampleFilter {
  public String getExperienceContains();

  public String getLaboratoryContains();

  public Laboratory getLaboratory();

  public String getLimsContains();

  public Instant getMinimalSubmissionDate();

  public Instant getMaximalSubmissionDate();

  public String getNameContains();

  public String getProjectContains();

  public Collection<SampleStatus> getStatuses();

  public SubmissionSampleService.Support getSupport();

  public String getUserContains();

  public User getUser();
}
