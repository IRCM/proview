package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.text.Strings.normalize;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.data.domain.Range;
import org.springframework.lang.Nullable;

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
  public Range<LocalDate> dataAvailableDateRange;
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
      if (instrument == MassDetectionInstrument.NULL) {
        test &= submission.getInstrument() == null || instrument == submission.getInstrument();
      } else {
        test &= instrument == submission.getInstrument();
      }
    }
    if (dateRange != null) {
      test &= dateRange.contains(submission.getSubmissionDate().toLocalDate(),
          Comparator.naturalOrder());
    }
    if (dataAvailableDateRange != null) {
      test &= submission.getDataAvailableDate() != null && dataAvailableDateRange.contains(
          submission.getDataAvailableDate(), Comparator.naturalOrder());
    }
    return test;
  }

  /**
   * Returns QueryDSL predicate matching filter.
   *
   * @return QueryDSL predicate matching filter
   */
  @Nullable
  public com.querydsl.core.types.Predicate predicate() {
    BooleanBuilder predicate = new BooleanBuilder();
    if (experimentContains != null) {
      predicate.and(submission.experiment.contains(experimentContains));
    }
    if (userContains != null) {
      predicate.and(submission.user.email.contains(userContains)
          .or(submission.user.name.contains(userContains)));
    }
    if (directorContains != null) {
      predicate.and(submission.laboratory.director.contains(directorContains));
    }
    if (service != null) {
      predicate.and(submission.service.eq(service));
    }
    if (anySampleNameContains != null) {
      predicate.and(submission.samples.any().name.contains(anySampleNameContains));
    }
    if (anySampleStatus != null) {
      predicate.and(submission.samples.any().status.eq(anySampleStatus));
    }
    if (instrument != null) {
      if (instrument == MassDetectionInstrument.NULL) {
        predicate.and(submission.instrument.isNull());
      } else {
        predicate.and(submission.instrument.eq(instrument));
      }
    }
    if (dateRange != null) {
      if (dateRange.getLowerBound().isBounded()) {
        LocalDate date = dateRange.getLowerBound().getValue().orElseThrow();
        if (dateRange.getLowerBound().isInclusive()) {
          date = date.minusDays(1);
        }
        predicate.and(submission.submissionDate.after(toLocalDateTime(date)));
      }
      if (dateRange.getUpperBound().isBounded()) {
        LocalDate date = dateRange.getUpperBound().getValue().orElseThrow();
        if (dateRange.getUpperBound().isInclusive()) {
          date = date.plusDays(1);
        }
        predicate.and(submission.submissionDate.before(toLocalDateTime(date)));
      }
    }
    if (dataAvailableDateRange != null) {
      if (dataAvailableDateRange.getLowerBound().isBounded()) {
        LocalDate date = dataAvailableDateRange.getLowerBound().getValue().orElseThrow();
        if (dataAvailableDateRange.getLowerBound().isInclusive()) {
          date = date.minusDays(1);
        }
        predicate.and(submission.dataAvailableDate.after(date));
      }
      if (dataAvailableDateRange.getUpperBound().isBounded()) {
        LocalDate date = dataAvailableDateRange.getUpperBound().getValue().orElseThrow();
        if (dataAvailableDateRange.getUpperBound().isInclusive()) {
          date = date.plusDays(1);
        }
        predicate.and(submission.dataAvailableDate.before(date));
      }
    }
    if (hidden != null) {
      predicate.and(submission.hidden.eq(hidden));
    }
    return predicate.getValue();
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
      if (instrument == MassDetectionInstrument.NULL) {
        query.where(submission.instrument.isNull());
      } else {
        query.where(submission.instrument.eq(instrument));
      }
    }
    if (dateRange != null) {
      if (dateRange.getLowerBound().isBounded()) {
        LocalDate date = dateRange.getLowerBound().getValue().orElseThrow();
        if (dateRange.getLowerBound().isInclusive()) {
          date = date.minusDays(1);
        }
        query.where(submission.submissionDate.after(toLocalDateTime(date)));
      }
      if (dateRange.getUpperBound().isBounded()) {
        LocalDate date = dateRange.getUpperBound().getValue().orElseThrow();
        if (dateRange.getUpperBound().isInclusive()) {
          date = date.plusDays(1);
        }
        query.where(submission.submissionDate.before(toLocalDateTime(date)));
      }
    }
    if (dataAvailableDateRange != null) {
      if (dataAvailableDateRange.getLowerBound().isBounded()) {
        LocalDate date = dataAvailableDateRange.getLowerBound().getValue().orElseThrow();
        if (dataAvailableDateRange.getLowerBound().isInclusive()) {
          date = date.minusDays(1);
        }
        query.where(submission.dataAvailableDate.after(date));
      }
      if (dataAvailableDateRange.getUpperBound().isBounded()) {
        LocalDate date = dataAvailableDateRange.getUpperBound().getValue().orElseThrow();
        if (dataAvailableDateRange.getUpperBound().isInclusive()) {
          date = date.plusDays(1);
        }
        query.where(submission.dataAvailableDate.before(date));
      }
    }
    if (hidden != null) {
      query.where(submission.hidden.eq(hidden));
    }
  }

  public void addCountConditions(JPAQuery<?> query) {
    addFilterConditions(query);
  }

  /**
   * Adds conditions to query to match filters.
   *
   * @param query database query
   */
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
