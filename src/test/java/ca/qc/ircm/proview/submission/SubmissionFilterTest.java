package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.WAITING;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.data.domain.Range;
import org.springframework.data.domain.Range.Bound;
import org.springframework.lang.Nullable;

/**
 * Tests for {@link SubmissionFilter}.
 */
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
  @BeforeEach
  public void beforeTest() {
    filter = new SubmissionFilter();
  }

  @Test
  public void predicate_ExperimentContains() {
    filter.experimentContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.experiment.contains("test"));
  }

  @Test
  public void predicate_UserContains() {
    filter.userContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate,
        submission.user.email.contains("test").or(submission.user.name.contains("test")));
  }

  @Test
  public void predicate_DirectorContains() {
    filter.directorContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.laboratory.director.contains("test"));
  }

  @Test
  public void predicate_Service() {
    filter.service = Service.LC_MS_MS;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.service.eq(Service.LC_MS_MS));
  }

  @Test
  public void predicate_AnySampleNameContains() {
    filter.anySampleNameContains = "test";

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().name.contains("test"));
  }

  @Test
  public void predicate_AnySampleStatus() {
    filter.anySampleStatus = SampleStatus.RECEIVED;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().status.eq(SampleStatus.RECEIVED));
  }

  @Test
  public void predicate_Instrument() {
    filter.instrument = MassDetectionInstrument.LTQ_ORBI_TRAP;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.instrument.eq(MassDetectionInstrument.LTQ_ORBI_TRAP));
  }

  @Test
  public void predicate_InstrumentForceNull() {
    filter.instrument = MassDetectionInstrument.NULL;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.instrument.isNull());
  }

  @Test
  public void predicate_DateRange_OpenRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.open(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start))
        .and(submission.submissionDate.before(toLocalDateTime(end))));
  }

  @Test
  public void predicate_DateRange_ClosedRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.closed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start.minusDays(1)))
        .and(submission.submissionDate.before(toLocalDateTime(end.plusDays(1)))));
  }

  @Test
  public void predicate_DateRange_OpenClosedRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.leftOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start))
        .and(submission.submissionDate.before(toLocalDateTime(end.plusDays(1)))));
  }

  @Test
  public void predicate_DateRange_ClosedOpenRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.rightOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start.minusDays(1)))
        .and(submission.submissionDate.before(toLocalDateTime(end))));
  }

  @Test
  public void predicate_DateRange_AtLeast() {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.rightUnbounded(Bound.inclusive(start));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start.minusDays(1))));
  }

  @Test
  public void predicate_DateRange_GreaterThan() {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dateRange = Range.rightUnbounded(Bound.exclusive(start));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.gt(toLocalDateTime(start)));
  }

  @Test
  public void predicate_DateRange_AtMost() {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.leftUnbounded(Bound.inclusive(end));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.before(toLocalDateTime(end.plusDays(1))));
  }

  @Test
  public void predicate_DateRange_LessThan() {
    LocalDate end = LocalDate.now();
    filter.dateRange = Range.leftUnbounded(Bound.exclusive(end));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.submissionDate.before(toLocalDateTime(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_OpenRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.open(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate,
        submission.dataAvailableDate.gt(start).and(submission.dataAvailableDate.before(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_ClosedRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.closed(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.gt(start.minusDays(1))
        .and(submission.dataAvailableDate.before(end.plusDays(1))));
  }

  @Test
  public void predicate_DataAvailableDateRange_OpenClosedRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.leftOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.gt(start)
        .and(submission.dataAvailableDate.before(end.plusDays(1))));
  }

  @Test
  public void predicate_DataAvailableDateRange_ClosedOpenRange() {
    LocalDate start = LocalDate.now().minusDays(10);
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.rightOpen(start, end);

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.gt(start.minusDays(1))
        .and(submission.dataAvailableDate.before(end)));
  }

  @Test
  public void predicate_DataAvailableDateRange_AtLeast() {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.rightUnbounded(Bound.inclusive(start));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.gt(start.minusDays(1)));
  }

  @Test
  public void predicate_DataAvailableDateRange_GreaterThan() {
    LocalDate start = LocalDate.now().minusDays(10);
    filter.dataAvailableDateRange = Range.rightUnbounded(Bound.exclusive(start));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.gt(start));
  }

  @Test
  public void predicate_DataAvailableDateRange_AtMost() {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.leftUnbounded(Bound.inclusive(end));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.before(end.plusDays(1)));
  }

  @Test
  public void predicate_DataAvailableDateRange_LessThan() {
    LocalDate end = LocalDate.now();
    filter.dataAvailableDateRange = Range.leftUnbounded(Bound.exclusive(end));

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.dataAvailableDate.before(end));
  }

  @Test
  public void predicate_Hidden_True() {
    filter.hidden = true;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.hidden.eq(true));
  }

  @Test
  public void predicate_Hidden_False() {
    filter.hidden = false;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.hidden.eq(false));
  }

  @Test
  public void predicate_AnySampleNameContainsAndAnySampleStatus() {
    filter.anySampleNameContains = "test";
    filter.anySampleStatus = SampleStatus.RECEIVED;

    Predicate predicate = filter.predicate();

    assertEquals(predicate, submission.samples.any().name.contains("test")
        .and(submission.samples.any().status.eq(SampleStatus.RECEIVED)));
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
    User user = new User(0, email);
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

  private Submission dataAvailableDate(@Nullable LocalDate date) {
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
  }

  @Test
  public void test_Instrument_OnlyNull() {
    filter.instrument = MassDetectionInstrument.NULL;

    assertFalse(filter.test(instrument(MassDetectionInstrument.LTQ_ORBI_TRAP)));
    assertFalse(filter.test(instrument(MassDetectionInstrument.VELOS)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.NULL)));
  }

  @Test
  public void test_Instrument_Null() {
    filter.instrument = null;

    assertTrue(filter.test(instrument(MassDetectionInstrument.LTQ_ORBI_TRAP)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.VELOS)));
    assertTrue(filter.test(instrument(MassDetectionInstrument.NULL)));
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
