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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.google.common.collect.Range;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateFilterTest {
  private PlateFilter filter = new PlateFilter();
  @Mock
  private JPAQuery<?> query;

  private Plate name(String name) {
    return new Plate(null, name);
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

  private Plate nameAndSubmission(String name, boolean submission) {
    Plate plate = mock(Plate.class);
    when(plate.getName()).thenReturn(name);
    when(plate.isSubmission()).thenReturn(submission);
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
  public void test_NameContainsAndEmptyCount() {
    filter.nameContains = "test";
    filter.submission = true;

    assertTrue(filter.test(nameAndSubmission("test", true)));
    assertFalse(filter.test(nameAndSubmission("test", false)));
    assertTrue(filter.test(nameAndSubmission("TEST", true)));
    assertFalse(filter.test(nameAndSubmission("TEST", false)));
    assertFalse(filter.test(nameAndSubmission("abc", true)));
    assertFalse(filter.test(nameAndSubmission("abc", false)));
    assertFalse(filter.test(nameAndSubmission("ABC", true)));
    assertFalse(filter.test(nameAndSubmission("ABC", false)));
    assertTrue(filter.test(nameAndSubmission("abctest", true)));
    assertFalse(filter.test(nameAndSubmission("abctest", false)));
    assertTrue(filter.test(nameAndSubmission("testabc", true)));
    assertFalse(filter.test(nameAndSubmission("testabc", false)));
    assertTrue(filter.test(nameAndSubmission("abctestdef", true)));
    assertFalse(filter.test(nameAndSubmission("abctestdef", false)));
    assertTrue(filter.test(nameAndSubmission("ABCTEST", true)));
    assertFalse(filter.test(nameAndSubmission("ABCTEST", false)));
    assertTrue(filter.test(nameAndSubmission("TESTABC", true)));
    assertFalse(filter.test(nameAndSubmission("TESTABC", false)));
    assertTrue(filter.test(nameAndSubmission("ABCTESTDEF", true)));
    assertFalse(filter.test(nameAndSubmission("ABCTESTDEF", false)));
  }

  @Test
  public void predicate_NameContains() throws Exception {
    filter.nameContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(plate.name.contains("test"), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.open(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.goe(toInstant(start.plusDays(1)))
        .and(plate.insertTime.before(toInstant(end))), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.closed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.goe(toInstant(start))
        .and(plate.insertTime.before(toInstant(end.plusDays(1)))), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.openClosed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.goe(toInstant(start.plusDays(1)))
        .and(plate.insertTime.before(toInstant(end.plusDays(1)))), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.closedOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(
        plate.insertTime.goe(toInstant(start)).and(plate.insertTime.before(toInstant(end))),
        predicate);
  }

  @Test
  public void predicate_InsertTimeRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.insertTimeRange = Range.atLeast(start);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.goe(toInstant(start)), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.insertTimeRange = Range.greaterThan(start);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.goe(toInstant(start.plusDays(1))), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.atMost(end);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.before(toInstant(end.plusDays(1))), predicate);
  }

  @Test
  public void predicate_InsertTimeRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.insertTimeRange = Range.lessThan(end);

    Predicate predicate = filter.predicate();

    assertEquals(plate.insertTime.before(toInstant(end)), predicate);
  }

  @Test
  public void predicate_Submission_True() throws Exception {
    filter.submission = true;

    Predicate predicate = filter.predicate();

    assertEquals(plate.submission.eq(true), predicate);
  }

  @Test
  public void predicate_Submission_False() throws Exception {
    filter.submission = false;

    Predicate predicate = filter.predicate();

    assertEquals(plate.submission.eq(false), predicate);
  }

  @Test
  public void predicate_NameContainsAndSubmission() {
    filter.nameContains = "test";
    filter.submission = true;

    Predicate predicate = filter.predicate();

    assertEquals(plate.name.contains("test").and(plate.submission.eq(true)), predicate);
  }
}
