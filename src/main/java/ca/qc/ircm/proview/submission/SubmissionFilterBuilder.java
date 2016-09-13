package ca.qc.ircm.proview.submission;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.Collection;

public class SubmissionFilterBuilder {
  private static final class SubmissionFilterImpl implements SubmissionFilter {
    private String experienceContains;
    private String laboratoryContains;
    private Laboratory laboratory;
    private String limsContains;
    private Instant minimalSubmissionDate;
    private Instant maximalSubmissionDate;
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

    @Override
    public String getLaboratoryContains() {
      return laboratoryContains;
    }

    @Override
    public Laboratory getLaboratory() {
      return laboratory;
    }

    @Override
    public String getLimsContains() {
      return limsContains;
    }

    @Override
    public Instant getMinimalSubmissionDate() {
      return minimalSubmissionDate;
    }

    @Override
    public Instant getMaximalSubmissionDate() {
      return maximalSubmissionDate;
    }

    @Override
    public String getNameContains() {
      return nameContains;
    }

    @Override
    public String getProjectContains() {
      return projectContains;
    }

    @Override
    public Collection<Status> getStatuses() {
      return statuses;
    }

    @Override
    public SubmissionSampleService.Support getSupport() {
      return support;
    }

    @Override
    public String getUserContains() {
      return userContains;
    }

    @Override
    public User getUser() {
      return user;
    }
  }

  private final SubmissionFilterImpl filter = new SubmissionFilterImpl();

  public SubmissionFilter build() {
    return filter;
  }

  public SubmissionFilterBuilder experienceContains(String experienceContains) {
    filter.experienceContains = experienceContains;
    return this;
  }

  public SubmissionFilterBuilder laboratoryContains(String laboratoryContains) {
    filter.laboratoryContains = laboratoryContains;
    return this;
  }

  public SubmissionFilterBuilder laboratory(Laboratory laboratory) {
    filter.laboratory = laboratory;
    return this;
  }

  public SubmissionFilterBuilder limsContains(String limsContains) {
    filter.limsContains = limsContains;
    return this;
  }

  public SubmissionFilterBuilder minimalSubmissionDate(Instant minimalSubmissionDate) {
    filter.minimalSubmissionDate = minimalSubmissionDate;
    return this;
  }

  public SubmissionFilterBuilder maximalSubmissionDate(Instant maximalSubmissionDate) {
    filter.maximalSubmissionDate = maximalSubmissionDate;
    return this;
  }

  public SubmissionFilterBuilder nameContains(String nameContains) {
    filter.nameContains = nameContains;
    return this;
  }

  public SubmissionFilterBuilder projectContains(String projectContains) {
    filter.projectContains = projectContains;
    return this;
  }

  public SubmissionFilterBuilder statuses(Collection<Status> statuses) {
    filter.statuses = statuses;
    return this;
  }

  public SubmissionFilterBuilder support(SubmissionSampleService.Support support) {
    filter.support = support;
    return this;
  }

  public SubmissionFilterBuilder userContains(String userContains) {
    filter.userContains = userContains;
    return this;
  }

  public SubmissionFilterBuilder user(User user) {
    filter.user = user;
    return this;
  }
}
