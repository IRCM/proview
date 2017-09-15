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

package ca.qc.ircm.proview.submission.web;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.submission.Submission;
import com.vaadin.server.SerializablePredicate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

/**
 * Filters submissions.
 */
public class SubmissionWebFilter implements SerializablePredicate<Submission> {
  private static final long serialVersionUID = -5902082214544061745L;
  public String experienceContains;
  public String emailContains;
  public String anySampleNameContains;
  public String goalContains;
  public SampleStatus anySampleStatus;
  public Range<LocalDate> dateRange;
  public Boolean results;
  private final Locale locale;

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
    if (emailContains != null) {
      test &= submission.getUser().getEmail().toLowerCase(locale)
          .contains(emailContains.toLowerCase(locale));
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
      test &= dateRange
          .contains(submission.getSubmissionDate().atZone(ZoneId.systemDefault()).toLocalDate());
    }
    if (results != null) {
      boolean analysed = submission.getSamples().isEmpty() || submission.getSamples().stream()
          .anyMatch(sample -> SampleStatus.ANALYSED.compareTo(sample.getStatus()) <= 0);
      test &= submission.getSamples().isEmpty() || results ? analysed : !analysed;
    }
    return test;
  }
}
