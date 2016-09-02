package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.sample.SubmissionSample.Status;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SubmissionSampleComparatorTest {
  private List<SubmissionSample> samples = new ArrayList<SubmissionSample>();

  /**
   * Before test.
   */
  @Before
  public void populateSamples() {
    {
      Laboratory laboratory = new Laboratory();
      laboratory.setOrganization("IRCM");
      User user = new User();
      user.setEmail("christian.poitras@ircm.qc.ca");
      Submission submission = new Submission();
      submission.setLaboratory(laboratory);
      submission.setUser(user);
      submission.setSubmissionDate(
          LocalDateTime.of(2011, 10, 18, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
      GelSample sample = new GelSample();
      sample.setSubmission(submission);
      sample.setId(1L);
      sample.setLims("IRCM_20111018_01");
      sample.setName("CAP_20111018_01");
      sample.setService(Service.LC_MS_MS);
      sample.setStatus(Status.TO_DIGEST);
      sample.setProject("cap_project");
      sample.setExperience("cap_experience");
      samples.add(sample);
    }
    {
      Laboratory laboratory = new Laboratory();
      laboratory.setOrganization("McGill");
      User user = new User();
      user.setEmail("mathieu.lavallée@mcgill.ca");
      Submission submission = new Submission();
      submission.setLaboratory(laboratory);
      submission.setUser(user);
      submission.setSubmissionDate(
          LocalDateTime.of(2011, 10, 19, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
      EluateSample sample = new EluateSample();
      sample.setSubmission(submission);
      sample.setId(2L);
      sample.setLims("MCGI_20111018_01");
      sample.setName("MLA_20111018_01");
      sample.setStatus(Status.TO_ANALYSE);
      sample.setProject("caé_project");
      sample.setExperience("mla_experience");
      sample.setService(Service.TWO_DIMENSION_LC_MS_MS);
      sample.setSupport(Sample.Support.DRY);
      samples.add(sample);
    }
    {
      Laboratory laboratory = new Laboratory();
      laboratory.setOrganization("UdeM");
      User user = new User();
      user.setEmail("jean.labbé@mcgill.ca");
      Submission submission = new Submission();
      submission.setLaboratory(laboratory);
      submission.setUser(user);
      submission.setSubmissionDate(
          LocalDateTime.of(2011, 10, 20, 0, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
      MoleculeSample sample = new MoleculeSample();
      sample.setSubmission(submission);
      sample.setId(3L);
      sample.setLims("UDEM_20111018_01");
      sample.setName("JZA_20111018_01");
      sample.setStatus(Status.ANALYSED);
      sample.setService(Service.SMALL_MOLECULE);
      sample.setSupport(Sample.Support.SOLUTION);
      samples.add(sample);
    }
  }

  @Test
  public void compareByLaboratory() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.LABORATORY, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareByUser() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.USER, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(1), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(1), testSamples.get(2));
  }

  @Test
  public void compareByService() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.SERVICE, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareBySubmission() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.SUBMISSION, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareByLims() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.LIMS, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareByName() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.NAME, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(1), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(1), testSamples.get(2));
  }

  @Test
  public void compareByStatus() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.STATUS, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareByProject() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.PROJECT, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(0), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(0), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));

    comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.PROJECT, Locale.CANADA_FRENCH);
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(0), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(0), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareByExperience() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.EXPERIENCE, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(0), testSamples.get(0));
    assertEquals(samples.get(1), testSamples.get(1));
    assertEquals(samples.get(2), testSamples.get(2));
  }

  @Test
  public void compareBySupport() {
    SubmissionSampleComparator comparator =
        new SubmissionSampleComparator(SubmissionSampleService.Sort.SUPPORT, Locale.CANADA);
    List<SubmissionSample> testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(0), testSamples.get(2));
    testSamples = new ArrayList<SubmissionSample>(samples);
    Collections.reverse(testSamples);
    Collections.sort(testSamples, comparator);
    assertEquals(samples.get(1), testSamples.get(0));
    assertEquals(samples.get(2), testSamples.get(1));
    assertEquals(samples.get(0), testSamples.get(2));
  }
}
