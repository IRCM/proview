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

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.time.TimeConverter.toInstant;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateFilterTest {
  private PlateFilter filter = new PlateFilter();
  @Mock
  private JPAQuery<?> query;

  private Plate name(String name) {
    return new Plate(null, name);
  }

  private Plate emptyCount(int emptyCount) {
    Plate plate = mock(Plate.class);
    when(plate.getEmptyWellCount()).thenReturn(emptyCount);
    return plate;
  }

  private Plate insertTime(Instant instant) {
    Plate plate = mock(Plate.class);
    when(plate.getInsertTime()).thenReturn(instant);
    return plate;
  }

  private Plate submission(boolean submission) {
    Plate plate = mock(Plate.class);
    when(plate.isSubmission()).thenReturn(submission);
    return plate;
  }

  private Plate samples(Long... ids) {
    Plate plate = new Plate();
    plate.initWells();
    IntStream.range(0, ids.length)
        .forEach(i -> plate.getWells().get(i).setSample(new SubmissionSample(ids[i])));
    return plate;
  }

  private Plate controls(Long... ids) {
    Plate plate = new Plate();
    plate.initWells();
    IntStream.range(0, ids.length)
        .forEach(i -> plate.getWells().get(i).setSample(new Control(ids[i])));
    return plate;
  }

  private Plate nameAndEmptyCount(String name, int emptyCount) {
    Plate plate = mock(Plate.class);
    when(plate.getName()).thenReturn(name);
    when(plate.getEmptyWellCount()).thenReturn(emptyCount);
    return plate;
  }

  @Test
  public void test_All() {
    assertTrue(filter.test(new Plate()));
    assertTrue(filter.test(new Plate()));
  }

  @Test
  public void test_NameContains() {
    filter.nameContains = "test";

    assertTrue(filter.test(name("test")));
    assertTrue(filter.test(name("TEST")));
    assertFalse(filter.test(name("abc")));
    assertFalse(filter.test(name("ABC")));
    assertTrue(filter.test(name("abctest")));
    assertTrue(filter.test(name("testabc")));
    assertTrue(filter.test(name("abctestdef")));
    assertTrue(filter.test(name("ABCTEST")));
    assertTrue(filter.test(name("TESTABC")));
    assertTrue(filter.test(name("ABCTESTDEF")));
  }

  @Test
  public void test_NameContains_French() {
    filter.nameContains = "pépin";

    assertTrue(filter.test(name("pépin")));
    assertTrue(filter.test(name("pepin")));
    assertTrue(filter.test(name("PÉPIN")));
    assertTrue(filter.test(name("PEPIN")));
    assertFalse(filter.test(name("abc")));
    assertFalse(filter.test(name("ABC")));
    assertTrue(filter.test(name("abcpépin")));
    assertTrue(filter.test(name("abcpepin")));
    assertTrue(filter.test(name("pépinabc")));
    assertTrue(filter.test(name("pepinabc")));
    assertTrue(filter.test(name("abcpépindef")));
    assertTrue(filter.test(name("abcpepindef")));
    assertTrue(filter.test(name("ABCPÉPIN")));
    assertTrue(filter.test(name("ABCPEPIN")));
    assertTrue(filter.test(name("PÉPINABC")));
    assertTrue(filter.test(name("PEPINABC")));
    assertTrue(filter.test(name("ABCPÉPINDEF")));
    assertTrue(filter.test(name("ABCPEPINDEF")));
  }

  @Test
  public void test_NameContains_Null() {
    filter.nameContains = null;

    assertTrue(filter.test(name("test")));
    assertTrue(filter.test(name("TEST")));
    assertTrue(filter.test(name("abc")));
    assertTrue(filter.test(name("abctest")));
    assertTrue(filter.test(name("testabc")));
    assertTrue(filter.test(name("abctestdef")));
    assertTrue(filter.test(name("ABCTEST")));
    assertTrue(filter.test(name("TESTABC")));
    assertTrue(filter.test(name("ABCTESTDEF")));
  }

  @Test
  public void test_MinimumEmptyCount() {
    filter.minimumEmptyCount = 40;

    assertTrue(filter.test(emptyCount(40)));
    assertTrue(filter.test(emptyCount(41)));
    assertTrue(filter.test(emptyCount(96)));
    assertFalse(filter.test(emptyCount(39)));
    assertFalse(filter.test(emptyCount(0)));
  }

  @Test
  public void test_MinimumEmptyCount_Null() {
    filter.minimumEmptyCount = null;

    assertTrue(filter.test(emptyCount(40)));
    assertTrue(filter.test(emptyCount(41)));
    assertTrue(filter.test(emptyCount(96)));
    assertTrue(filter.test(emptyCount(39)));
    assertTrue(filter.test(emptyCount(0)));
  }

  @Test
  public void test_InsertTimeRange() {
    LocalDate from = LocalDate.of(2011, 1, 2);
    LocalDate to = LocalDate.of(2011, 10, 9);
    filter.insertTimeRange = Range.closed(from, to);

    assertFalse(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertFalse(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertFalse(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void test_InsertTimeRange_Null() {
    filter.insertTimeRange = null;

    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 1, 9, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 2, 9, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 10, 8, 23, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 10, 9, 23, 40)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 12, 1, 0, 0)))));
    assertTrue(filter.test(insertTime(toInstant(LocalDateTime.of(2011, 1, 1, 0, 0)))));
  }

  @Test
  public void test_Submission_True() {
    filter.submission = true;

    assertTrue(filter.test(submission(true)));
    assertFalse(filter.test(submission(false)));
  }

  @Test
  public void test_Submission_False() {
    filter.submission = false;

    assertFalse(filter.test(submission(true)));
    assertTrue(filter.test(submission(false)));
  }

  @Test
  public void test_Submission_Null() {
    filter.submission = null;

    assertTrue(filter.test(submission(true)));
    assertTrue(filter.test(submission(false)));
  }

  @Test
  public void test_ContainsAnySamples() {
    filter.containsAnySamples =
        Arrays.asList(new SubmissionSample(5L), new SubmissionSample(8L), new Control(12L));

    assertTrue(filter.test(samples(5L, 8L, 12L)));
    assertTrue(filter.test(controls(5L, 8L, 12L)));
    assertTrue(filter.test(samples(5L, 10L, 14L)));
    assertTrue(filter.test(samples(7L, 8L, 14L)));
    assertTrue(filter.test(samples(7L, 10L, 12L)));
    assertFalse(filter.test(samples(7L, 10L, 14L)));
    assertTrue(filter.test(controls(5L, 10L, 14L)));
    assertTrue(filter.test(controls(7L, 8L, 14L)));
    assertTrue(filter.test(controls(7L, 10L, 12L)));
    assertFalse(filter.test(controls(7L, 10L, 14L)));
  }

  @Test
  public void test_ContainsAnySamples_Null() {
    filter.containsAnySamples = null;

    assertTrue(filter.test(samples(5L, 8L, 12L)));
    assertTrue(filter.test(controls(5L, 8L, 12L)));
    assertTrue(filter.test(samples(5L, 10L, 14L)));
    assertTrue(filter.test(samples(7L, 8L, 14L)));
    assertTrue(filter.test(samples(7L, 10L, 12L)));
    assertTrue(filter.test(samples(7L, 10L, 14L)));
    assertTrue(filter.test(controls(5L, 10L, 14L)));
    assertTrue(filter.test(controls(7L, 8L, 14L)));
    assertTrue(filter.test(controls(7L, 10L, 12L)));
    assertTrue(filter.test(controls(7L, 10L, 14L)));
  }

  @Test
  public void test_NameContainsAndEmptyCount() {
    filter.nameContains = "test";
    filter.minimumEmptyCount = 40;

    assertTrue(filter.test(nameAndEmptyCount("test", 40)));
    assertTrue(filter.test(nameAndEmptyCount("test", 41)));
    assertTrue(filter.test(nameAndEmptyCount("test", 96)));
    assertFalse(filter.test(nameAndEmptyCount("test", 39)));
    assertFalse(filter.test(nameAndEmptyCount("test", 0)));
    assertTrue(filter.test(nameAndEmptyCount("TEST", 40)));
    assertTrue(filter.test(nameAndEmptyCount("TEST", 41)));
    assertTrue(filter.test(nameAndEmptyCount("TEST", 96)));
    assertFalse(filter.test(nameAndEmptyCount("TEST", 39)));
    assertFalse(filter.test(nameAndEmptyCount("TEST", 0)));
    assertFalse(filter.test(nameAndEmptyCount("abc", 40)));
    assertFalse(filter.test(nameAndEmptyCount("abc", 41)));
    assertFalse(filter.test(nameAndEmptyCount("abc", 96)));
    assertFalse(filter.test(nameAndEmptyCount("abc", 39)));
    assertFalse(filter.test(nameAndEmptyCount("abc", 0)));
    assertFalse(filter.test(nameAndEmptyCount("ABC", 40)));
    assertFalse(filter.test(nameAndEmptyCount("ABC", 41)));
    assertFalse(filter.test(nameAndEmptyCount("ABC", 96)));
    assertFalse(filter.test(nameAndEmptyCount("ABC", 39)));
    assertFalse(filter.test(nameAndEmptyCount("ABC", 0)));
    assertTrue(filter.test(nameAndEmptyCount("abctest", 40)));
    assertTrue(filter.test(nameAndEmptyCount("abctest", 41)));
    assertTrue(filter.test(nameAndEmptyCount("abctest", 96)));
    assertFalse(filter.test(nameAndEmptyCount("abctest", 39)));
    assertFalse(filter.test(nameAndEmptyCount("abctest", 0)));
    assertTrue(filter.test(nameAndEmptyCount("testabc", 40)));
    assertTrue(filter.test(nameAndEmptyCount("testabc", 41)));
    assertTrue(filter.test(nameAndEmptyCount("testabc", 96)));
    assertFalse(filter.test(nameAndEmptyCount("testabc", 39)));
    assertFalse(filter.test(nameAndEmptyCount("testabc", 0)));
    assertTrue(filter.test(nameAndEmptyCount("abctestdef", 40)));
    assertTrue(filter.test(nameAndEmptyCount("abctestdef", 41)));
    assertTrue(filter.test(nameAndEmptyCount("abctestdef", 96)));
    assertFalse(filter.test(nameAndEmptyCount("abctestdef", 39)));
    assertFalse(filter.test(nameAndEmptyCount("abctestdef", 0)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTEST", 40)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTEST", 41)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTEST", 96)));
    assertFalse(filter.test(nameAndEmptyCount("ABCTEST", 39)));
    assertFalse(filter.test(nameAndEmptyCount("ABCTEST", 0)));
    assertTrue(filter.test(nameAndEmptyCount("TESTABC", 40)));
    assertTrue(filter.test(nameAndEmptyCount("TESTABC", 41)));
    assertTrue(filter.test(nameAndEmptyCount("TESTABC", 96)));
    assertFalse(filter.test(nameAndEmptyCount("TESTABC", 39)));
    assertFalse(filter.test(nameAndEmptyCount("TESTABC", 0)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTESTDEF", 40)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTESTDEF", 41)));
    assertTrue(filter.test(nameAndEmptyCount("ABCTESTDEF", 96)));
    assertFalse(filter.test(nameAndEmptyCount("ABCTESTDEF", 39)));
    assertFalse(filter.test(nameAndEmptyCount("ABCTESTDEF", 0)));
  }

  @Test
  public void addConditions_NameContains() throws Exception {
    filter.nameContains = "test";

    filter.addConditions(query);

    verify(query).where(plate.name.contains("test"));
  }

  @Test
  public void addConditions_MinimumEmptyCount() throws Exception {
    filter.minimumEmptyCount = 40;

    filter.addConditions(query);

    QWell mecW = new QWell("mecW");
    verify(query).where(plate.columnCount.multiply(plate.rowCount).subtract(40)
        .goe(JPAExpressions.select(mecW.sample.count()).from(plate.wells, mecW)));
  }

  @Test
  public void addConditions_InsertTimeRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.open(start, end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start.plusDays(1))));
    verify(query).where(plate.insertTime.before(toInstant(end)));
  }

  @Test
  public void addConditions_InsertTimeRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.closed(start, end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start)));
    verify(query).where(plate.insertTime.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_InsertTimeRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.openClosed(start, end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start.plusDays(1))));
    verify(query).where(plate.insertTime.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_InsertTimeRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.closedOpen(start, end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start)));
    verify(query).where(plate.insertTime.before(toInstant(end)));
  }

  @Test
  public void addConditions_InsertTimeRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.insertTimeRange = Range.atLeast(start);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start)));
  }

  @Test
  public void addConditions_InsertTimeRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.insertTimeRange = Range.greaterThan(start);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.goe(toInstant(start.plusDays(1))));
  }

  @Test
  public void addConditions_InsertTimeRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.atMost(end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.before(toInstant(end.plusDays(1))));
  }

  @Test
  public void addConditions_InsertTimeRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.lessThan(end);

    filter.addConditions(query);

    verify(query).where(plate.insertTime.before(toInstant(end)));
  }

  @Test
  public void addConditions_Submission_True() throws Exception {
    filter.submission = true;

    filter.addConditions(query);

    verify(query).where(plate.submission.eq(true));
  }

  @Test
  public void addConditions_Submission_False() throws Exception {
    filter.submission = false;

    filter.addConditions(query);

    verify(query).where(plate.submission.eq(false));
  }

  @Test
  public void addConditions_ContainsAnySample() throws Exception {
    filter.containsAnySamples =
        Arrays.asList(new SubmissionSample(5L), new SubmissionSample(8L), new Control(12L));

    filter.addConditions(query);

    verify(query).where(plate.wells.any().sample.in(filter.containsAnySamples));
  }

  @Test
  public void addConditions_NameContainsAndSubmission() {
    filter.nameContains = "test";
    filter.submission = true;

    filter.addConditions(query);

    verify(query).where(plate.name.contains("test"));
    verify(query).where(plate.submission.eq(true));
  }
}
