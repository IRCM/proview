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
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.WAITING;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.google.common.collect.Range;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SubmissionFilterTest {
  private SubmissionFilter filter;
  @Mock
  private JPAQuery<?> query;
  @Captor
  private ArgumentCaptor<OrderSpecifier<?>[]> orderSpecifierArrayCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    filter = new SubmissionFilter();
  }

  @Test
  public void predicate_ExperimentContains() throws Exception {
    filter.experimentContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.experiment.contains("test"));
  }

  @Test
  public void predicate_UserContains() throws Exception {
    filter.userContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate,
        submission.user.email.contains("test").or(submission.user.name.contains("test")));
  }

  @Test
  public void predicate_DirectorContains() throws Exception {
    filter.directorContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.laboratory.director.contains("test"));
  }

  @Test
  public void predicate_Service() throws Exception {
    filter.service = Service.LC_MS_MS;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.service.eq(Service.LC_MS_MS));
  }

  @Test
  public void predicate_AnySampleNameContains() throws Exception {
    filter.anySampleNameContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().name.contains("test"));
  }

  @Test
  public void predicate_AnySampleStatus() throws Exception {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void predicate_Instrument() throws Exception {
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.instrument.eq(MassDetectionInstrument.LTQ_ORBI_TRAP));
  }

  @Test
  public void predicate_InstrumentForceNull() throws Exception {
    filter.instrument = MassDetectionInstrument.NULL;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.instrument.isNull());
  }

  @Test
  public void predicate_DateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start.plusDays(1)))
        .and(submission.submissionDate.before(toLocalDateTime(end))));
  }

  @Test
  public void predicate_DateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start))
        .and(submission.submissionDate.before(toLocalDateTime(end.plusDays(1)))));
  }

  @Test
  public void predicate_DateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.openClosed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start.plusDays(1)))
        .and(submission.submissionDate.before(toLocalDateTime(end.plusDays(1)))));
  }

  @Test
  public void predicate_DateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closedOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start))
        .and(submission.submissionDate.before(toLocalDateTime(end))));
  }

  @Test
  public void predicate_DateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.atLeast(start);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start)));
  }

  @Test
  public void predicate_DateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.greaterThan(start);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
  }

  @Test
  public void predicate_DateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.atMost(end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void predicate_DateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.lessThan(end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.open(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.goe(start.plusDays(1))
        .and(submission.dataAvailableDate.before(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.goe(start)
        .and(submission.dataAvailableDate.before(end.plusDays(1))));
  }

  @Test
  public void predicate_DataAvailableDateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.openClosed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.goe(start.plusDays(1))
        .and(submission.dataAvailableDate.before(end.plusDays(1))));
  }

  @Test
  public void predicate_DataAvailableDateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closedOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate,
        submission.dataAvailableDate.goe(start).and(submission.dataAvailableDate.before(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.atLeast(start);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.goe(start));
  }

  @Test
  public void predicate_DataAvailableDateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.greaterThan(start);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.goe(start.plusDays(1)));
  }

  @Test
  public void predicate_DataAvailableDateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.atMost(end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void predicate_DataAvailableDateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.lessThan(end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.before(end));
  }

  @Test
  public void predicate_Hidden_True() throws Exception {
    filter.hidden = true;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.hidden.eq(true));
  }

  @Test
  public void predicate_Hidden_False() throws Exception {
    filter.hidden = false;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.hidden.eq(false));
  }

  @Test
  public void predicate_AnySampleNameContainsAndAnySampleStatus() throws Exception {
    filter.anySampleNameContains = "test";
    filter.anySampleStatus = SampleStatus.RECEIVED;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().name.contains("test")
        .and(submission.samples.any().status.eq(SampleStatus.RECEIVED)));
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
  public void addConditions_Service() throws Exception {
    filter.service = Service.LC_MS_MS;

    filter.addConditions(query);

    verify(query).where(submission.service.eq(Service.LC_MS_MS));
  }

  @Test
  public void addConditions_AnySampleNameContains() throws Exception {
    filter.anySampleNameContains = "test";

    filter.addConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
  }

  @Test
  public void addConditions_AnySampleStatus() throws Exception {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addConditions(query);

    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addConditions_Instrument() throws Exception {
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    filter.addConditions(query);

    verify(query).where(submission.instrument.eq(MassDetectionInstrument.LTQ_ORBI_TRAP));
  }

  @Test
  public void addConditions_InstrumentForceNull() throws Exception {
    filter.instrument = MassDetectionInstrument.NULL;

    filter.addConditions(query);

    verify(query).where(submission.instrument.isNull());
  }

  @Test
  public void addConditions_DateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addConditions_DateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.openClosed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closedOpen(start, end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addConditions_DateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.atLeast(start);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
  }

  @Test
  public void addConditions_DateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.greaterThan(start);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.atMost(end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addConditions_DateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.lessThan(end);

    filter.addConditions(query);

    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addConditions_DataAvailableDateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.open(start, end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
    verify(query).where(submission.dataAvailableDate.before(end));
  }

  @Test
  public void addConditions_DataAvailableDateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addConditions_DataAvailableDateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.openClosed(start, end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addConditions_DataAvailableDateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closedOpen(start, end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
    verify(query).where(submission.dataAvailableDate.before(end));
  }

  @Test
  public void addConditions_DataAvailableDateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.atLeast(start);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
  }

  @Test
  public void addConditions_DataAvailableDateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.greaterThan(start);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
  }

  @Test
  public void addConditions_DataAvailableDateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.atMost(end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addConditions_DataAvailableDateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.lessThan(end);

    filter.addConditions(query);

    verify(query).where(submission.dataAvailableDate.before(end));
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
  public void addCountConditions_Service() throws Exception {
    filter.service = Service.LC_MS_MS;

    filter.addConditions(query);

    verify(query).where(submission.service.eq(Service.LC_MS_MS));
  }

  @Test
  public void addCountConditions_AnySampleNameContains() throws Exception {
    filter.anySampleNameContains = "test";

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().name.contains("test"));
  }

  @Test
  public void addCountConditions_AnySampleStatus() throws Exception {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    filter.addCountConditions(query);

    verify(query).where(submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void addCountConditions_Instrument() throws Exception {
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    filter.addCountConditions(query);

    verify(query).where(submission.instrument.eq(MassDetectionInstrument.LTQ_ORBI_TRAP));
  }

  @Test
  public void addCountConditions_InstrumentForceNull() throws Exception {
    filter.instrument = MassDetectionInstrument.NULL;

    filter.addCountConditions(query);

    verify(query).where(submission.instrument.isNull());
  }

  @Test
  public void addCountConditions_DateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addCountConditions_DateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.openClosed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closedOpen(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addCountConditions_DateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.atLeast(start);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start)));
  }

  @Test
  public void addCountConditions_DateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.greaterThan(start);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.goe(toLocalDateTime(start.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.atMost(end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void addCountConditions_DateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.lessThan(end);

    filter.addCountConditions(query);

    verify(query).where(submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_OpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.open(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
    verify(query).where(submission.dataAvailableDate.before(end));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_ClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_OpenClosedRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.openClosed(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_ClosedOpenRange() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closedOpen(start, end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
    verify(query).where(submission.dataAvailableDate.before(end));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_AtLeast() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.atLeast(start);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_GreaterThan() throws Exception {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.greaterThan(start);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.goe(start.plusDays(1)));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_AtMost() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.atMost(end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void addCountConditions_DataAvailableDateRange_LessThan() throws Exception {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.lessThan(end);

    filter.addCountConditions(query);

    verify(query).where(submission.dataAvailableDate.before(end));
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

  private Submission service(Service service) {
    Submission submission = mock(Submission.class);
    when(submission.getService()).thenReturn(service);
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

  private Submission instrument(MassDetectionInstrument instrument) {
    Submission submission = mock(Submission.class);
    when(submission.getInstrument()).thenReturn(instrument);
    return submission;
  }

  private Submission date(LocalDateTime date) {
    Submission submission = new Submission();
    submission.setSubmissionDate(date);
    submission.setSamples(Collections.emptyList());
    return submission;
  }

  private Submission dataAvailableDate(LocalDate date) {
    Submission submission = mock(Submission.class);
    when(submission.getDataAvailableDate()).thenReturn(date);
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
  public void test_experimentContains_French() {
    filter.experimentContains = "pépin";

    assertTrue(filter.test(experiment("My pépin")));
    assertTrue(filter.test(experiment("My pepin")));
    assertTrue(filter.test(experiment("Pépin")));
    assertTrue(filter.test(experiment("Pepin")));
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
  public void test_userContains_French() {
    filter.userContains = "pépin";

    assertTrue(filter.test(user("My pépin", "My name")));
    assertTrue(filter.test(user("My pepin", "My name")));
    assertTrue(filter.test(user("Pépin", "Name")));
    assertTrue(filter.test(user("Pepin", "Name")));
    assertTrue(filter.test(user("My email", "My pépin")));
    assertTrue(filter.test(user("My email", "My pepin")));
    assertTrue(filter.test(user("Email", "Pépin")));
    assertTrue(filter.test(user("Email", "Pepin")));
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
  public void test_directorContains_French() {
    filter.directorContains = "pépin";

    assertTrue(filter.test(director("My pépin")));
    assertTrue(filter.test(director("My pepin")));
    assertTrue(filter.test(director("Pépin")));
    assertTrue(filter.test(director("Pepin")));
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
  public void test_service() {
    filter.service = Service.LC_MS_MS;

    assertTrue(filter.test(service(Service.LC_MS_MS)));
    assertFalse(filter.test(service(Service.INTACT_PROTEIN)));
    assertFalse(filter.test(service(Service.MALDI_MS)));
    assertFalse(filter.test(service(Service.SMALL_MOLECULE)));
    assertFalse(filter.test(service(Service.TWO_DIMENSION_LC_MS_MS)));
  }

  @Test
  public void test_service_Null() {
    filter.service = null;

    assertTrue(filter.test(service(Service.LC_MS_MS)));
    assertTrue(filter.test(service(Service.INTACT_PROTEIN)));
    assertTrue(filter.test(service(Service.MALDI_MS)));
    assertTrue(filter.test(service(Service.SMALL_MOLECULE)));
    assertTrue(filter.test(service(Service.TWO_DIMENSION_LC_MS_MS)));
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
  public void test_anySampleNameContains_French() {
    filter.anySampleNameContains = "pépin";

    assertTrue(filter.test(sampleNames("abc", "sample_pépin")));
    assertTrue(filter.test(sampleNames("abc", "sample_pepin")));
    assertTrue(filter.test(sampleNames("Pépin", "abc")));
    assertTrue(filter.test(sampleNames("Pepin", "abc")));
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
    assertFalse(filter.test(sampleStatuses(DIGESTED, CANCELLED)));
    assertFalse(filter.test(sampleStatuses(RECEIVED, DIGESTED, WAITING)));
  }

  @Test
  public void test_anySampleStatus_Null() {
    filter.anySampleStatus = null;

    assertTrue(filter.test(sampleStatuses(RECEIVED, ANALYSED)));
    assertTrue(filter.test(sampleStatuses(ANALYSED, ANALYSED, DIGESTED)));
    assertTrue(filter.test(sampleStatuses(DIGESTED, CANCELLED)));
    assertTrue(filter.test(sampleStatuses(RECEIVED, DIGESTED, WAITING)));
  }

  @Test
  public void test_Instrument() {
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    assertTrue(filter.test(instrument(MassDetectionInstrument.LTQ_ORBI_TRAP)));
    assertFalse(filter.test(instrument(MassDetectionInstrument.VELOS)));
    assertFalse(filter.test(instrument(MassDetectionInstrument.NULL)));
    assertFalse(filter.test(instrument(null)));
  }

  @Test
  public void test_Instrument_OnlyNull() {
    filter.instrument = MassDetectionInstrument.NULL;

    assertFalse(filter.test(instrument(MassDetectionInstrument.LTQ_ORBI_TRAP)));
    assertFalse(filter.test(instrument(MassDetectionInstrument.VELOS)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.NULL)));
    assertTrue(filter.test(instrument(null)));
  }

  @Test
  public void test_Instrument_Null() {
    filter.instrument = null;

    assertTrue(filter.test(instrument(MassDetectionInstrument.LTQ_ORBI_TRAP)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.VELOS)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.NULL)));
    assertTrue(filter.test(instrument(null)));
  }

  @Test
  public void test_dateRange() {
    LocalDate from = LocalDate.of(2011, 1, 2);
    LocalDate to = LocalDate.of(2011, 10, 9);
    filter.dateRange = Range.closed(from, to);

    assertFalse(filter.test(date(LocalDateTime.of(2011, 1, 1, 9, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 1, 2, 9, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 10, 8, 23, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 10, 9, 23, 40))));
    assertFalse(filter.test(date(LocalDateTime.of(2011, 12, 1, 0, 0))));
    assertFalse(filter.test(date(LocalDateTime.of(2011, 1, 1, 0, 0))));
  }

  @Test
  public void test_dateRange_Null() {
    filter.dateRange = null;

    assertTrue(filter.test(date(LocalDateTime.of(2011, 1, 1, 9, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 1, 2, 9, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 10, 8, 23, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 10, 9, 23, 40))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 12, 1, 0, 0))));
    assertTrue(filter.test(date(LocalDateTime.of(2011, 1, 1, 0, 0))));
  }

  @Test
  public void test_DataAvailableDateRange() {
    LocalDate from = LocalDate.of(2011, 1, 2);
    LocalDate to = LocalDate.of(2011, 10, 9);
    filter.dataAvailableDateRange = Range.closed(from, to);

    assertFalse(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 1))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 2))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 10, 8))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 10, 9))));
    assertFalse(filter.test(dataAvailableDate(LocalDate.of(2011, 12, 1))));
    assertFalse(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 1))));
    assertFalse(filter.test(dataAvailableDate(null)));
  }

  @Test
  public void test_DataAvailableDateRange_Null() {
    filter.dataAvailableDateRange = null;

    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 1))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 2))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 10, 8))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 10, 9))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 12, 1))));
    assertTrue(filter.test(dataAvailableDate(LocalDate.of(2011, 1, 1))));
    assertTrue(filter.test(dataAvailableDate(null)));
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
