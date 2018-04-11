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

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.APPROVED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.time.TimeConverter.toInstant;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Stream;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionFilterTest {
  private SubmissionFilter filter;
  private Locale locale = Locale.FRENCH;
  @Mock
  private JPAQuery<?> query;
  @Captor
  private ArgumentCaptor<OrderSpecifier<?>[]> orderSpecifierArrayCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    filter = new SubmissionFilter(locale);
  }

  @Test
  public void addConditions_ExperimentContains() throws Exception {
    filter.experimentContains = "test";

    filter.addConditions(query);

    verify(query).where(submission.experiment.contains("test"));
  }

  @Test
  public void addConditions_UserContains() throws Exception {
    filter.userContains = "test";

    filter.addConditions(query);

    verify(query)
        .where(submission.user.email.contains("test").or(submission.user.name.contains("test")));
  }

  @Test
  public void addConditions_DirectorContains() throws Exception {
    filter.directorContains = "test";

    filter.addConditions(query);

    verify(query).where(submission.laboratory.director.contains("test"));
  }

  @Test
  public void addConditions_AnySampleNameContains() throws Exception {
    filter.anySampleNameContains = "test";

    filter.addConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
  }

  @Test
  public void addConditions_GoalContains() throws Exception {
    filter.goalContains = "test";

    filter.addConditions(query);

    verify(query).where(submission.goal.contains("test"));
  }

  @Test
  public void addConditions_AnySampleStatus() throws Exception {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addConditions(query);

    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addConditions_DateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addConditions_DateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.openClosed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closedOpen(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addConditions_DateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.atLeast(start);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
  }

  @Test
  public void addConditions_DateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.greaterThan(start);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.atMost(end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.lessThan(end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addConditions_Results_True() throws Exception {
    filter.results = true;

    filter.addConditions(query);

    verify(query).where(submission.samples.any().status.in(SampleStatus.analysedStatuses()));
  }

  @Test
  public void addConditions_Results_False() throws Exception {
    filter.results = false;

    filter.addConditions(query);

    verify(query).where(submission.samples.any().status.notIn(SampleStatus.analysedStatuses()));
  }

  @Test
  public void addConditions_Hidden_True() throws Exception {
    filter.hidden = true;

    filter.addConditions(query);

    verify(query).where(submission.hidden.eq(true));
  }

  @Test
  public void addConditions_Hidden_False() throws Exception {
    filter.hidden = false;

    filter.addConditions(query);

    verify(query).where(submission.hidden.eq(false));
  }

  @Test
  public void addConditions_AnySampleNameContainsAndAnySampleStatus() throws Exception {
    filter.anySampleNameContains = "test";
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addConditions_Sort_UserAsc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.asc());

    filter.addConditions(query);

    verify(query).orderBy(new OrderSpecifier[] { submission.user.name.asc() });
  }

  @Test
  public void addConditions_Sort_UserDesc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.desc());

    filter.addConditions(query);

    verify(query).orderBy(new OrderSpecifier[] { submission.user.name.desc() });
  }

  @Test
  public void addConditions_Sort_UserAscAndExperimentDesc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.asc(), submission.experiment.desc());

    filter.addConditions(query);

    verify(query).orderBy(submission.user.name.asc(), submission.experiment.desc());
  }

  @Test
  public void addConditions_Offset() throws Exception {
    filter.offset = 10;

    filter.addConditions(query);

    verify(query).offset(10);
  }

  @Test
  public void addConditions_Limit() throws Exception {
    filter.limit = 10;

    filter.addConditions(query);

    verify(query).limit(10);
  }

  @Test
  public void addConditions_OffsetAndlimit() throws Exception {
    filter.offset = 10;
    filter.limit = 20;

    filter.addConditions(query);

    verify(query).offset(10);
    verify(query).limit(20);
  }

  @Test
  public void addCountConditions_ExperimentContains() throws Exception {
    filter.experimentContains = "test";

    filter.addCountConditions(query);

    verify(query).where(submission.experiment.contains("test"));
  }

  @Test
  public void addCountConditions_UserContains() throws Exception {
    filter.userContains = "test";

    filter.addCountConditions(query);

    verify(query)
        .where(submission.user.email.contains("test").or(submission.user.name.contains("test")));
  }

  @Test
  public void addCountConditions_DirectorContains() throws Exception {
    filter.directorContains = "test";

    filter.addCountConditions(query);

    verify(query).where(submission.laboratory.director.contains("test"));
  }

  @Test
  public void addCountConditions_AnySampleNameContains() throws Exception {
    filter.anySampleNameContains = "test";

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
  }

  @Test
  public void addCountConditions_GoalContains() throws Exception {
    filter.goalContains = "test";

    filter.addCountConditions(query);

    verify(query).where(submission.goal.contains("test"));
  }

  @Test
  public void addCountConditions_AnySampleStatus() throws Exception {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addCountConditions_DateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addCountConditions_DateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.openClosed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closedOpen(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addCountConditions_DateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.atLeast(start);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start)));
  }

  @Test
  public void addCountConditions_DateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.greaterThan(start);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toInstant(start.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.atMost(end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.lessThan(end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.before(toInstant(end)));
  }

  @Test
  public void addCountConditions_Results_True() throws Exception {
    filter.results = true;

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().status.in(SampleStatus.analysedStatuses()));
  }

  @Test
  public void addCountConditions_Results_False() throws Exception {
    filter.results = false;

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().status.notIn(SampleStatus.analysedStatuses()));
  }

  @Test
  public void addCountConditions_AnySampleNameContainsAndAnySampleStatus() throws Exception {
    filter.anySampleNameContains = "test";
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addCountConditions_Sort_UserAsc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.asc());

    filter.addCountConditions(query);

    verify(query, never()).orderBy(orderSpecifierArrayCaptor.capture());
  }

  @Test
  public void addCountConditions_Sort_UserDesc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.desc());

    filter.addCountConditions(query);

    verify(query, never()).orderBy(orderSpecifierArrayCaptor.capture());
  }

  @Test
  public void addCountConditions_Sort_UserAscAndExperimentDesc() throws Exception {
    filter.sortOrders = Arrays.asList(submission.user.name.asc(), submission.experiment.desc());

    filter.addCountConditions(query);

    verify(query, never()).orderBy(orderSpecifierArrayCaptor.capture());
  }

  @Test
  public void addCountConditions_Offset() throws Exception {
    filter.offset = 10;

    filter.addCountConditions(query);

    verify(query, never()).offset(anyInt());
  }

  @Test
  public void addCountConditions_Limit() throws Exception {
    filter.limit = 10;

    filter.addCountConditions(query);

    verify(query, never()).limit(anyInt());
  }

  @Test
  public void addCountConditions_OffsetAndlimit() throws Exception {
    filter.offset = 10;
    filter.limit = 20;

    filter.addCountConditions(query);

    verify(query, never()).offset(anyInt());
    verify(query, never()).limit(anyInt());
  }

  private Submission experiment(String experiment) {
    return experiment(new Submission(), experiment);
  }

  private Submission experiment(Submission submission, String experiment) {
    submission.setExperiment(experiment);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission user(String email, String userName) {
    Submission submission = new Submission();
    User user = new User(null, email);
    user.setName(userName);
    submission.setUser(user);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission director(String userName) {
    Submission submission = new Submission();
    Laboratory laboratory = new Laboratory();
    laboratory.setDirector(userName);
    submission.setLaboratory(laboratory);
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

  @Test
  public void test_experimentContains() {
    filter.experimentContains = "test";

    assertTrue(filter.test(experiment("My test")));
    assertTrue(filter.test(experiment("Test")));
    assertFalse(filter.test(experiment("My experiment")));
  }

  @Test
  public void test_experimentContains_Null() {
    filter.experimentContains = null;

    assertTrue(filter.test(experiment("My test")));
    assertTrue(filter.test(experiment("Test")));
    assertTrue(filter.test(experiment("My experiment")));
  }

  @Test
  public void test_userContains() {
    filter.userContains = "test";

    assertTrue(filter.test(user("My test", "My name")));
    assertTrue(filter.test(user("Test", "Name")));
    assertTrue(filter.test(user("My email", "My test")));
    assertTrue(filter.test(user("Email", "Test")));
    assertFalse(filter.test(user("My experiment", "My name")));
  }

  @Test
  public void test_userContains_Null() {
    filter.userContains = null;

    assertTrue(filter.test(user("My test", "My name")));
    assertTrue(filter.test(user("Test", "Name")));
    assertTrue(filter.test(user("My email", "My test")));
    assertTrue(filter.test(user("Email", "Test")));
    assertTrue(filter.test(user("My experiment", "My name")));
  }

  @Test
  public void test_directorContains() {
    filter.directorContains = "test";

    assertTrue(filter.test(director("My test")));
    assertTrue(filter.test(director("Test")));
    assertFalse(filter.test(director("My name")));
  }

  @Test
  public void test_directorContains_Null() {
    filter.directorContains = null;

    assertTrue(filter.test(director("My test")));
    assertTrue(filter.test(director("Test")));
    assertTrue(filter.test(director("My name")));
  }

  @Test
  public void test_goalContains() {
    filter.goalContains = "test";

    assertTrue(filter.test(goal("My test")));
    assertTrue(filter.test(goal("Test")));
    assertFalse(filter.test(goal("My experiment")));
  }

  @Test
  public void test_goalContains_Null() {
    filter.goalContains = null;

    assertTrue(filter.test(goal("My test")));
    assertTrue(filter.test(goal("Test")));
    assertTrue(filter.test(goal("My experiment")));
  }

  @Test
  public void test_anySampleNameContains() {
    filter.anySampleNameContains = "test";

    assertTrue(filter.test(sampleNames("abc", "sample_test")));
    assertTrue(filter.test(sampleNames("Test", "abc")));
    assertFalse(filter.test(sampleNames("my_sample")));
    assertFalse(filter.test(sampleNames("my_sample", "abc")));
  }

  @Test
  public void test_anySampleNameContains_Null() {
    filter.anySampleNameContains = null;

    assertTrue(filter.test(sampleNames("abc", "sample_test")));
    assertTrue(filter.test(sampleNames("Test", "abc")));
    assertTrue(filter.test(sampleNames("my_sample")));
    assertTrue(filter.test(sampleNames("my_sample", "abc")));
  }

  @Test
  public void test_anySampleStatus() {
    filter.anySampleStatus = ANALYSED;

    assertTrue(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertFalse(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertFalse(filter.test(sampleStatuses(RECEIVED, DIGESTED, APPROVED)));
  }

  @Test
  public void test_anySampleStatus_Null() {
    filter.anySampleStatus = null;

    assertTrue(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, DIGESTED, APPROVED)));
  }

  @Test
  public void test_dateRange() {
    LocalDate from = LocalDate.of(2011, 1, 2);
    LocalDate to = LocalDate.of(2011, 10, 9);
    filter.dateRange = Range.closed(from, to);

    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertFalse(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void test_dateRange_Null() {
    filter.dateRange = null;

    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertTrue(filter.test(date(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void test_result_True() {
    filter.results = true;

    assertTrue(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertFalse(filter.test(sampleStatuses(RECEIVED, DIGESTED, APPROVED)));
  }

  @Test
  public void test_result_False() {
    filter.results = false;

    assertFalse(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertFalse(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertFalse(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, DIGESTED, APPROVED)));
  }

  @Test
  public void test_result_Null() {
    filter.results = null;

    assertTrue(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertTrue(filter.test(sampleStatuses(DATA_ANALYSIS, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, DIGESTED, APPROVED)));
  }

  @Test
  public void test_experimentContainsAndAnySampleNameContains() {
    filter.experimentContains = "test";
    filter.anySampleNameContains = "test";

    assertTrue(filter.test(sampleNames(experiment("test"), "abc", "sample_test")));
    assertFalse(filter.test(sampleNames(experiment("test"), "my_sample", "abc")));
    assertFalse(filter.test(sampleNames(experiment("abc"), "my_sample", "test")));
    assertFalse(filter.test(sampleNames(experiment("abc"), "my_sample", "abc")));
  }
}
