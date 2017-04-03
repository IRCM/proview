package ca.qc.ircm.proview.submission.web;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.server.SerializablePredicate;

import java.time.Instant;
import java.util.Locale;

/**
 * Filters submissions.
 */
public class SubmissionWebFilter implements SerializablePredicate<Submission> {
  private static final long serialVersionUID = -5902082214544061745L;
  private String experienceContains;
  private String anySampleNameContains;
  private String goalContains;
  private SampleStatus anySampleStatus;
  private Range<Instant> dateRange;
  private Boolean results;
  private Locale locale;

  public SubmissionWebFilter(Locale locale) {
    this.locale = locale;
  }

  @Override
  public boolean test(Submission submission) {
    boolean test = true;
    if (experienceContains != null) {
      test &= submission.getExperience().toLowerCase(locale)
          .contains(experienceContains.toLowerCase(locale));
    }
    if (anySampleNameContains != null) {
      test &= submission.getSamples().isEmpty()
          || submission.getSamples().stream().anyMatch(sample -> sample.getName()
              .toLowerCase(locale).contains(anySampleNameContains.toLowerCase(locale)));
    }
    if (goalContains != null) {
      test &= submission.getGoal().toLowerCase(locale).contains(goalContains.toLowerCase(locale));
    }
    if (anySampleStatus != null) {
      test &= submission.getSamples().isEmpty() || submission.getSamples().stream()
          .anyMatch(sample -> anySampleStatus.equals(sample.getStatus()));
    }
    if (dateRange != null) {
      test &= dateRange.contains(submission.getSubmissionDate());
    }
    if (results != null) {
      boolean analysed = submission.getSamples().isEmpty() || submission.getSamples().stream()
          .anyMatch(sample -> SampleStatus.ANALYSED.compareTo(sample.getStatus()) <= 0);
      test &= submission.getSamples().isEmpty() || results ? analysed : !analysed;
    }
    return test;
  }

  public String getExperienceContains() {
    return experienceContains;
  }

  public void setExperienceContains(String experienceContains) {
    this.experienceContains = experienceContains;
  }

  public String getAnySampleNameContains() {
    return anySampleNameContains;
  }

  public void setAnySampleNameContains(String anySampleNameContains) {
    this.anySampleNameContains = anySampleNameContains;
  }

  public String getGoalContains() {
    return goalContains;
  }

  public void setGoalContains(String goalContains) {
    this.goalContains = goalContains;
  }

  public SampleStatus getAnySampleStatus() {
    return anySampleStatus;
  }

  public void setAnySampleStatus(SampleStatus anySampleStatus) {
    this.anySampleStatus = anySampleStatus;
  }

  public Range<Instant> getDateRange() {
    return dateRange;
  }

  public void setDateRange(Range<Instant> dateRange) {
    this.dateRange = dateRange;
  }

  public Boolean getResults() {
    return results;
  }

  public void setResults(Boolean results) {
    this.results = results;
  }
}
