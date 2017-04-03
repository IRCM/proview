package ca.qc.ircm.proview.submission.web;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.server.SerializablePredicate;

import java.time.Instant;
import java.util.Locale;
import java.util.Set;

/**
 * Filters submissions.
 */
public class SubmissionWebFilter implements SerializablePredicate<Submission> {
  private static final long serialVersionUID = -5902082214544061745L;
  private String experienceContains;
  private String firstSampleNameContains;
  private String goalContains;
  private Set<SampleStatus> anySampleStatuses;
  private Range<Instant> dateRange;
  private Boolean results;
  private Locale locale;

  public SubmissionWebFilter(Locale locale) {
    this.locale = locale;
  }

  @Override
  public boolean test(Submission submission) {
    // TODO Auto-generated method stub
    return true;
  }

  public String getExperienceContains() {
    return experienceContains;
  }

  public void setExperienceContains(String experienceContains) {
    this.experienceContains = experienceContains;
  }

  public String getFirstSampleNameContains() {
    return firstSampleNameContains;
  }

  public void setFirstSampleNameContains(String firstSampleNameContains) {
    this.firstSampleNameContains = firstSampleNameContains;
  }

  public String getGoalContains() {
    return goalContains;
  }

  public void setGoalContains(String goalContains) {
    this.goalContains = goalContains;
  }

  public Set<SampleStatus> getAnySampleStatuses() {
    return anySampleStatuses;
  }

  public void setAnySampleStatuses(Set<SampleStatus> anySampleStatuses) {
    this.anySampleStatuses = anySampleStatuses;
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
