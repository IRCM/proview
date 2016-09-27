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

package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.laboratory.Laboratory;
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
      SubmissionSample sample = new SubmissionSample();
      sample.setSubmission(submission);
      sample.setId(1L);
      sample.setLims("IRCM_20111018_01");
      sample.setName("CAP_20111018_01");
      sample.setStatus(SampleStatus.TO_DIGEST);
      sample.setSupport(SampleSupport.GEL);
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
      SubmissionSample sample = new SubmissionSample();
      sample.setSubmission(submission);
      sample.setId(2L);
      sample.setLims("MCGI_20111018_01");
      sample.setName("MLA_20111018_01");
      sample.setStatus(SampleStatus.TO_ANALYSE);
      sample.setSupport(SampleSupport.DRY);
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
      SubmissionSample sample = new SubmissionSample();
      sample.setSubmission(submission);
      sample.setId(3L);
      sample.setLims("UDEM_20111018_01");
      sample.setName("JZA_20111018_01");
      sample.setStatus(SampleStatus.ANALYSED);
      sample.setSupport(SampleSupport.SOLUTION);
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
