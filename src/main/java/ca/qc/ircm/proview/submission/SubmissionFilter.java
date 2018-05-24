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

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
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
  public Service service;
  public String anySampleNameContains;
  public SampleStatus anySampleStatus;
  public MassDetectionInstrument instrument;
  public Range<LocalDate> dateRange;
  public Range<LocalDate> sampleDeliveryDateRange;
  public Range<LocalDate> digestionDateRange;
  public Range<LocalDate> analysisDateRange;
  public Range<LocalDate> dataAvailableDateRange;
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
    if (service != null) {
      test &= submission.getService() == service;
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
    if (instrument != null) {
      test &= instrument == submission.getMassDetectionInstrument();
    }
    if (dateRange != null) {
      test &= dateRange.contains(toLocalDate(submission.getSubmissionDate()));
    }
    if (sampleDeliveryDateRange != null) {
      test &= submission.getSampleDeliveryDate() != null
          && sampleDeliveryDateRange.contains(submission.getSampleDeliveryDate());
    }
    if (digestionDateRange != null) {
      test &= submission.getDigestionDate() != null
          && digestionDateRange.contains(submission.getDigestionDate());
    }
    if (analysisDateRange != null) {
      test &= submission.getAnalysisDate() != null
          && analysisDateRange.contains(submission.getAnalysisDate());
    }
    if (dataAvailableDateRange != null) {
      test &= submission.getDataAvailableDate() != null
          && dataAvailableDateRange.contains(submission.getDataAvailableDate());
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
    if (service != null) {
      query.where(submission.service.eq(service));
    }
    if (anySampleNameContains != null) {
      query.where(submission.samples.any().name.contains(anySampleNameContains));
    }
    if (anySampleStatus != null) {
      query.where(submission.samples.any().status.eq(anySampleStatus));
    }
    if (instrument != null) {
      query.where(submission.massDetectionInstrument.eq(instrument));
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
    if (digestionDateRange != null) {
      if (digestionDateRange.hasLowerBound()) {
        LocalDate date = digestionDateRange.lowerEndpoint();
        if (digestionDateRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(submission.digestionDate.goe(date));
      }
      if (digestionDateRange.hasUpperBound()) {
        LocalDate date = digestionDateRange.upperEndpoint();
        if (digestionDateRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(submission.digestionDate.before(date));
      }
    }
    if (sampleDeliveryDateRange != null) {
      if (sampleDeliveryDateRange.hasLowerBound()) {
        LocalDate date = sampleDeliveryDateRange.lowerEndpoint();
        if (sampleDeliveryDateRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(submission.sampleDeliveryDate.goe(date));
      }
      if (sampleDeliveryDateRange.hasUpperBound()) {
        LocalDate date = sampleDeliveryDateRange.upperEndpoint();
        if (sampleDeliveryDateRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(submission.sampleDeliveryDate.before(date));
      }
    }
    if (analysisDateRange != null) {
      if (analysisDateRange.hasLowerBound()) {
        LocalDate date = analysisDateRange.lowerEndpoint();
        if (analysisDateRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(submission.analysisDate.goe(date));
      }
      if (analysisDateRange.hasUpperBound()) {
        LocalDate date = analysisDateRange.upperEndpoint();
        if (analysisDateRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(submission.analysisDate.before(date));
      }
    }
    if (dataAvailableDateRange != null) {
      if (dataAvailableDateRange.hasLowerBound()) {
        LocalDate date = dataAvailableDateRange.lowerEndpoint();
        if (dataAvailableDateRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(submission.dataAvailableDate.goe(date));
      }
      if (dataAvailableDateRange.hasUpperBound()) {
        LocalDate date = dataAvailableDateRange.upperEndpoint();
        if (dataAvailableDateRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(submission.dataAvailableDate.before(date));
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
