package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.user.User;

import java.util.Collection;
import java.util.Date;

/**
 * Simple implementation of {@link SampleFilter}.
 */
public class SampleFilterBean implements SampleFilter {
  private String experienceContains;
  private String laboratoryContains;
  private Laboratory laboratory;
  private String limsContains;
  private Date minimalSubmissionDate;
  private Date maximalSubmissionDate;
  private String nameContains;
  private String projectContains;
  private Collection<Status> statuses;
  private SubmissionSampleService.Support support;
  private String userContains;
  private User user;

  @Override
  public String getExperienceContains() {
    return experienceContains;
  }

  public SampleFilterBean experienceContains(String experienceContains) {
    this.experienceContains = experienceContains;
    return this;
  }

  @Override
  public String getLaboratoryContains() {
    return laboratoryContains;
  }

  public SampleFilterBean laboratoryContains(String laboratoryContains) {
    this.laboratoryContains = laboratoryContains;
    return this;
  }

  @Override
  public Laboratory getLaboratory() {
    return laboratory;
  }

  public SampleFilterBean laboratory(Laboratory laboratory) {
    this.laboratory = laboratory;
    return this;
  }

  @Override
  public String getLimsContains() {
    return limsContains;
  }

  public SampleFilterBean limsContains(String limsContains) {
    this.limsContains = limsContains;
    return this;
  }

  @Override
  public Date getMinimalSubmissionDate() {
    return minimalSubmissionDate != null ? (Date) minimalSubmissionDate.clone() : null;
  }

  /**
   * Sets minimal submission date.
   *
   * @param minimalSubmissionDate
   *          minimal submission date
   * @return filter
   */
  public SampleFilterBean minimalSubmissionDate(Date minimalSubmissionDate) {
    this.minimalSubmissionDate =
        minimalSubmissionDate != null ? (Date) minimalSubmissionDate.clone() : null;
    return this;
  }

  @Override
  public Date getMaximalSubmissionDate() {
    return maximalSubmissionDate != null ? (Date) maximalSubmissionDate.clone() : null;
  }

  /**
   * Sets maximal submission date.
   *
   * @param maximalSubmissionDate
   *          maximal submission date
   * @return filter
   */
  public SampleFilterBean maximalSubmissionDate(Date maximalSubmissionDate) {
    this.maximalSubmissionDate =
        maximalSubmissionDate != null ? (Date) maximalSubmissionDate.clone() : null;
    return this;
  }

  @Override
  public String getNameContains() {
    return nameContains;
  }

  public SampleFilterBean nameContains(String nameContains) {
    this.nameContains = nameContains;
    return this;
  }

  @Override
  public String getProjectContains() {
    return projectContains;
  }

  public SampleFilterBean projectContains(String projectContains) {
    this.projectContains = projectContains;
    return this;
  }

  @Override
  public Collection<Status> getStatuses() {
    return statuses;
  }

  public SampleFilterBean statuses(Collection<Status> statuses) {
    this.statuses = statuses;
    return this;
  }

  @Override
  public SubmissionSampleService.Support getSupport() {
    return support;
  }

  public SampleFilterBean support(SubmissionSampleService.Support support) {
    this.support = support;
    return this;
  }

  @Override
  public String getUserContains() {
    return userContains;
  }

  public SampleFilterBean userContains(String userContains) {
    this.userContains = userContains;
    return this;
  }

  @Override
  public User getUser() {
    return user;
  }

  public SampleFilterBean user(User user) {
    this.user = user;
    return this;
  }
}
