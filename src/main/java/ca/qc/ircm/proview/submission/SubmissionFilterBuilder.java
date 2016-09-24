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

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SampleStatus;
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

  public SubmissionFilterBuilder statuses(Collection<SampleStatus> statuses) {
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
