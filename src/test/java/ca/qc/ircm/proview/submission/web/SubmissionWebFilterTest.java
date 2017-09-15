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

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_ANALYSE;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_DIGEST;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionWebFilterTest {
  private SubmissionWebFilter filter;
  private Locale locale = Locale.FRENCH;

  @Before
  public void beforeTest() {
    filter = new SubmissionWebFilter(locale);
  }

  private Submission experience(String experience) {
    return experience(new Submission(), experience);
  }

  private Submission experience(Submission submission, String experience) {
    submission.setExperience(experience);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission email(String email) {
    Submission submission = new Submission();
    submission.setUser(new User(null, email));
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission goal(String goal) {
    Submission submission = new Submission();
    submission.setGoal(goal);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission sampleNames(String... names) {
    return sampleNames(new Submission(), names);
  }

  private Submission sampleNames(Submission submission, String... names) {
    submission.setSamples(new ArrayList<>());
    Stream.of(names).forEach(name -> {
      SubmissionSample sample = new SubmissionSample();
      sample.setName(name);
      submission.getSamples().add(sample);
    });
    return submission;
  }

  private Submission sampleStatuses(SampleStatus... statuses) {
    Submission submission = new Submission();
    submission.setSamples(new ArrayList<>());
    Stream.of(statuses).forEach(status -> {
      SubmissionSample sample = new SubmissionSample();
      sample.setStatus(status);
      submission.getSamples().add(sample);
    });
    return submission;
  }

  private Submission date(Instant date) {
    Submission submission = new Submission();
    submission.setSubmissionDate(date);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Instant toInstant(LocalDateTime date) {
    return date.atZone(ZoneId.systemDefault()).toInstant();
  }

  @Test
  public void experienceContains() {
    filter.experienceContains = Optional.of("test");

    assertTrue(filter.test(experience("My test")));
    assertTrue(filter.test(experience("Test")));
    assertFalse(filter.test(experience("My experience")));
  }

  @Test
  public void experienceContains_Empty() {
    filter.experienceContains = Optional.empty();

    assertTrue(filter.test(experience("My test")));
    assertTrue(filter.test(experience("Test")));
    assertTrue(filter.test(experience("My experience")));
  }

  @Test
  public void emailContains() {
    filter.emailContains = Optional.of("test");

    assertTrue(filter.test(email("My test")));
    assertTrue(filter.test(email("Test")));
    assertFalse(filter.test(email("My experience")));
  }

  @Test
  public void emailContains_Empty() {
    filter.emailContains = Optional.empty();

    assertTrue(filter.test(email("My test")));
    assertTrue(filter.test(email("Test")));
    assertTrue(filter.test(email("My experience")));
  }

  @Test
  public void goalContains() {
    filter.goalContains = Optional.of("test");

    assertTrue(filter.test(goal("My test")));
    assertTrue(filter.test(goal("Test")));
    assertFalse(filter.test(goal("My experience")));
  }

  @Test
  public void goalContains_Empty() {
    filter.goalContains = Optional.empty();

    assertTrue(filter.test(goal("My test")));
    assertTrue(filter.test(goal("Test")));
    assertTrue(filter.test(goal("My experience")));
  }

  @Test
  public void anySampleNameContains() {
    filter.anySampleNameContains = Optional.of("test");

    assertTrue(filter.test(sampleNames("abc", "sample_test")));
    assertTrue(filter.test(sampleNames("Test", "abc")));
    assertFalse(filter.test(sampleNames("my_sample")));
    assertFalse(filter.test(sampleNames("my_sample", "abc")));
  }

  @Test
  public void anySampleNameContains_Empty() {
    filter.anySampleNameContains = Optional.empty();

    assertTrue(filter.test(sampleNames("abc", "sample_test")));
    assertTrue(filter.test(sampleNames("Test", "abc")));
    assertTrue(filter.test(sampleNames("my_sample")));
    assertTrue(filter.test(sampleNames("my_sample", "abc")));
  }

  @Test
  public void anySampleStatus() {
    filter.anySampleStatus = Optional.of(ANALYSED);

    assertTrue(filter.test(sampleStatuses(TO_ANALYSE, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, TO_DIGEST)));
    assertFalse(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertFalse(filter.test(sampleStatuses(RECEIVED, TO_DIGEST, TO_ANALYSE)));
  }

  @Test
  public void anySampleStatus_Empty() {
    filter.anySampleStatus = Optional.empty();

    assertTrue(filter.test(sampleStatuses(TO_ANALYSE, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, TO_DIGEST)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, TO_DIGEST, TO_ANALYSE)));
  }

  @Test
  public void dateRange() {
    LocalDate from = LocalDate.of(2011, 1, 2);
    LocalDate to = LocalDate.of(2011, 10, 9);
    filter.dateRange = Optional.of(Range.closed(from, to));

    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void dateRange_Empty() {
    filter.dateRange = Optional.empty();

    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void result_True() {
    filter.results = Optional.of(true);

    assertTrue(filter.test(sampleStatuses(TO_ANALYSE, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, TO_DIGEST)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertFalse(filter.test(sampleStatuses(RECEIVED, TO_DIGEST, TO_ANALYSE)));
  }

  @Test
  public void result_False() {
    filter.results = Optional.of(false);

    assertFalse(filter.test(sampleStatuses(TO_ANALYSE, ANALYSED)));
    assertFalse(filter.test(sampleStatuses(ANALYSED, ANALYSED, TO_DIGEST)));
    assertFalse(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, TO_DIGEST, TO_ANALYSE)));
  }

  @Test
  public void result_Empty() {
    filter.results = Optional.empty();

    assertTrue(filter.test(sampleStatuses(TO_ANALYSE, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, TO_DIGEST)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, TO_DIGEST, TO_ANALYSE)));
  }

  @Test
  public void experienceContainsAndAnySampleNameContains() {
    filter.experienceContains = Optional.of("test");
    filter.anySampleNameContains = Optional.of("test");

    assertTrue(filter.test(sampleNames(experience("test"), "abc", "sample_test")));
    assertFalse(filter.test(sampleNames(experience("test"), "my_sample", "abc")));
    assertFalse(filter.test(sampleNames(experience("abc"), "my_sample", "test")));
    assertFalse(filter.test(sampleNames(experience("abc"), "my_sample", "abc")));
  }
}
