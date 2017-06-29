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

import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;

import java.time.Instant;
import java.util.Collection;

/**
 * Simple implementation of {@link SampleFilter}.
 */
public class SampleFilterBuilder {
  private static class SampleFilterDefault implements SampleFilter {
    private String experienceContains;
    private String laboratoryContains;
    private Laboratory laboratory;
    private String limsContains;
    private Instant minimalSubmissionDate;
    private Instant maximalSubmissionDate;
    private String nameContains;
    private String projectContains;
    private Collection<SampleStatus> statuses;
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
    public Collection<SampleStatus> getStatuses() {
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

  private final SampleFilterDefault parameters = new SampleFilterDefault();

  public SampleFilterBuilder experienceContains(String experienceContains) {
    parameters.experienceContains = experienceContains;
    return this;
  }

  public SampleFilterBuilder laboratoryContains(String laboratoryContains) {
    parameters.laboratoryContains = laboratoryContains;
    return this;
  }

  public SampleFilterBuilder laboratory(Laboratory laboratory) {
    parameters.laboratory = laboratory;
    return this;
  }

  public SampleFilterBuilder limsContains(String limsContains) {
    parameters.limsContains = limsContains;
    return this;
  }

  /**
   * Sets minimal submission date.
   *
   * @param minimalSubmissionDate
   *          minimal submission date
   * @return filter
   */
  public SampleFilterBuilder minimalSubmissionDate(Instant minimalSubmissionDate) {
    parameters.minimalSubmissionDate = minimalSubmissionDate;
    return this;
  }

  /**
   * Sets maximal submission date.
   *
   * @param maximalSubmissionDate
   *          maximal submission date
   * @return filter
   */
  public SampleFilterBuilder maximalSubmissionDate(Instant maximalSubmissionDate) {
    parameters.maximalSubmissionDate = maximalSubmissionDate;
    return this;
  }

  public SampleFilterBuilder nameContains(String nameContains) {
    parameters.nameContains = nameContains;
    return this;
  }

  public SampleFilterBuilder projectContains(String projectContains) {
    parameters.projectContains = projectContains;
    return this;
  }

  public SampleFilterBuilder statuses(Collection<SampleStatus> statuses) {
    parameters.statuses = statuses;
    return this;
  }

  public SampleFilterBuilder support(SubmissionSampleService.Support support) {
    parameters.support = support;
    return this;
  }

  public SampleFilterBuilder userContains(String userContains) {
    parameters.userContains = userContains;
    return this;
  }

  public SampleFilterBuilder user(User user) {
    parameters.user = user;
    return this;
  }

  public SampleFilter build() {
    return parameters;
  }
}
