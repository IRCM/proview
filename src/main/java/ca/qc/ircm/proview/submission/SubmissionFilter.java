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

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.time.TimeConverter.toInstant;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Filters submissions.
 */
public class SubmissionFilter implements Predicate<Submission> {
  public String experimentContains;
  public String userContains;
  public String directorContains;
  public String anySampleNameContains;
  public SampleStatus anySampleStatus;
  public Range<LocalDate> dateRange;
  public Boolean results;
  public Boolean hidden;
  public List<OrderSpecifier<?>> sortOrders;
  public Integer offset;
  public Integer limit;

  @Override
  public boolean test(Submission submission) {
    boolean test = true;
    if (experimentContains != null) {
      String experimentContainsNormalized = normalize(experimentContains).toLowerCase();
      String experiment = normalize(submission.getExperiment()).toLowerCase();
      test &= experiment.contains(experimentContainsNormalized);
    }
    if (userContains != null) {
      String userContainsNormalized = normalize(userContains).toLowerCase();
      String email = normalize(submission.getUser().getEmail()).toLowerCase();
      String name = normalize(submission.getUser().getName()).toLowerCase();
      test &= email.contains(userContainsNormalized) || name.contains(userContainsNormalized);
    }
    if (directorContains != null) {
      String directorContainsNormalized = normalize(directorContains).toLowerCase();
      String director = normalize(submission.getLaboratory().getDirector()).toLowerCase();
      test &= director.contains(directorContainsNormalized);
    }
    if (anySampleNameContains != null) {
      String nameContainsNormalized = normalize(anySampleNameContains).toLowerCase();
      test &= submission.getSamples().isEmpty() || submission.getSamples().stream().anyMatch(
          sample -> normalize(sample.getName()).toLowerCase().contains(nameContainsNormalized));
    }
    if (anySampleStatus != null) {
      test &= submission.getSamples().isEmpty() || submission.getSamples().stream()
          .anyMatch(sample -> anySampleStatus.equals(sample.getStatus()));
    }
    if (dateRange != null) {
      test &= dateRange.contains(toLocalDate(submission.getSubmissionDate()));
    }
    if (results != null) {
      Set<SampleStatus> analysedStatuses =
          new HashSet<>(Arrays.asList(SampleStatus.analysedStatuses()));
      boolean analysed = submission.getSamples().isEmpty() || submission.getSamples().stream()
          .anyMatch(sample -> analysedStatuses.contains(sample.getStatus()));
      test &= submission.getSamples().isEmpty() || results ? analysed : !analysed;
    }
    return test;
  }

  private void addFilterConditions(JPAQuery<?> query) {
    if (experimentContains != null) {
      query.where(submission.experiment.contains(experimentContains));
    }
    if (userContains != null) {
      query.where(submission.user.email.contains(userContains)
          .or(submission.user.name.contains(userContains)));
    }
    if (directorContains != null) {
      query.where(submission.laboratory.director.contains(directorContains));
    }
    if (anySampleNameContains != null) {
      query.where(submission.samples.any().name.contains(anySampleNameContains));
    }
    if (anySampleStatus != null) {
      query.where(submission.samples.any().status.eq(anySampleStatus));
    }
    if (dateRange != null) {
      if (dateRange.hasLowerBound()) {
        LocalDate date = dateRange.lowerEndpoint();
        if (dateRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(submission.submissionDate.goe(toInstant(date)));
      }
      if (dateRange.hasUpperBound()) {
        LocalDate date = dateRange.upperEndpoint();
        if (dateRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(submission.submissionDate.before(toInstant(date)));
      }
    }
    if (results != null) {
      if (results) {
        query.where(submission.samples.any().status.in(SampleStatus.analysedStatuses()));
      } else {
        query.where(submission.samples.any().status.notIn(SampleStatus.analysedStatuses()));
      }
    }
    if (hidden != null) {
      query.where(submission.hidden.eq(hidden));
    }
  }

  public void addCountConditions(JPAQuery<?> query) {
    addFilterConditions(query);
  }

  public void addConditions(JPAQuery<?> query) {
    addFilterConditions(query);
    if (sortOrders != null) {
      query.orderBy(sortOrders.toArray(new OrderSpecifier[0]));
    }
    if (offset != null) {
      query.offset(offset);
    }
    if (limit != null) {
      query.limit(limit);
    }
  }
}
