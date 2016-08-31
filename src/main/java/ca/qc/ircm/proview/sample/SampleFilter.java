package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.user.User;

import java.util.Collection;
import java.util.Date;

/**
 * Filters samples.
 */
public interface SampleFilter {
  public String getExperienceContains();

  public String getLaboratoryContains();

  public Laboratory getLaboratory();

  public String getLimsContains();

  public Date getMinimalSubmissionDate();

  public Date getMaximalSubmissionDate();

  public String getNameContains();

  public String getProjectContains();

  public Collection<Status> getStatuses();

  public SubmissionSampleService.Support getSupport();

  public String getUserContains();

  public User getUser();
}
