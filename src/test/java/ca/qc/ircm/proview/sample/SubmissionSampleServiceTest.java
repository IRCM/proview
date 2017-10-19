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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.pricing.PricingEvaluator;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionSampleServiceTest {
  private SubmissionSampleService submissionSampleService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SampleActivityService sampleActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private PricingEvaluator pricingEvaluator;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    submissionSampleService = new SubmissionSampleService(entityManager, queryFactory,
        sampleActivityService, activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get_Gel() throws Throwable {
    SubmissionSample sample = submissionSampleService.get(1L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample gelSample = sample;
    assertEquals((Long) 1L, gelSample.getId());
    assertEquals("FAM119A_band_01", gelSample.getName());
    assertEquals(true, gelSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 1L, gelSample.getOriginalContainer().getId());
    assertEquals(SampleSupport.GEL, gelSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, gelSample.getType());
    assertEquals(SampleStatus.ANALYSED, gelSample.getStatus());
    assertEquals(null, gelSample.getNumberProtein());
    assertEquals(null, gelSample.getMolecularWeight());
    assertEquals((Long) 1L, gelSample.getSubmission().getId());
  }

  @Test
  public void get() throws Throwable {
    SubmissionSample sample = submissionSampleService.get(442L);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertTrue(sample instanceof SubmissionSample);
    SubmissionSample eluateSample = sample;
    assertEquals((Long) 442L, eluateSample.getId());
    assertEquals("CAP_20111013_01", eluateSample.getName());
    assertEquals(true, eluateSample.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 2L, eluateSample.getOriginalContainer().getId());
    assertEquals(SampleSupport.SOLUTION, eluateSample.getSupport());
    assertEquals(Sample.Type.SUBMISSION, eluateSample.getType());
    assertEquals(SampleStatus.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals("1.5 μg", eluateSample.getQuantity());
    assertEquals((Double) 50.0, eluateSample.getVolume());
    assertEquals(null, eluateSample.getNumberProtein());
    assertEquals(null, eluateSample.getMolecularWeight());
  }

  @Test
  public void get_NullId() throws Throwable {
    SubmissionSample sample = submissionSampleService.get((Long) null);

    assertNull(sample);
  }

  @Test
  public void exists_True() throws Throwable {
    boolean exists = submissionSampleService.exists("CAP_20111013_05");

    verify(authorizationService).checkUserRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_False() throws Throwable {
    boolean exists = submissionSampleService.exists("CAP_20111013_80");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Null() throws Throwable {
    boolean exists = submissionSampleService.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void projects() throws Throwable {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    List<String> projects = submissionSampleService.projects();

    verify(authorizationService).checkUserRole();
    assertEquals(2, projects.size());
    assertEquals(true, projects.contains("cap_project"));
    assertEquals(true, projects.contains("Coulombe"));
    assertEquals(false, projects.contains("some_random_string"));
  }

  @Test
  public void update() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(sample);
    sample.setName("new_solution_tag_0001");
    sample.setSupport(SampleSupport.DRY);
    sample.setQuantity("12 pmol");
    sample.setVolume(70.0);
    sample.setNumberProtein(10);
    sample.setMolecularWeight(120.0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals("new_solution_tag_0001", test.getName());
    assertEquals(SampleSupport.DRY, test.getSupport());
    assertEquals("12 pmol", test.getQuantity());
    assertEquals((Double) 70.0, test.getVolume());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertEquals("new_solution_tag_0001", newSubmissionSample.getName());
    assertEquals(SampleSupport.DRY, newSubmissionSample.getSupport());
    assertEquals("12 pmol", newSubmissionSample.getQuantity());
    assertEquals((Double) 70.0, newSubmissionSample.getVolume());
    assertEquals((Integer) 10, newSubmissionSample.getNumberProtein());
    assertEquals((Double) 120.0, newSubmissionSample.getMolecularWeight());
  }

  @Test
  public void update_AddContaminant() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(sample);
    Contaminant insert = new Contaminant();
    insert.setName("my_new_contaminant");
    insert.setQuantity("3 μg");
    insert.setComment("some_comment");
    sample.getContaminants().add(insert);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant insertion.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getContaminants().size());
    Contaminant testContaminant = test.getContaminants().get(0);
    assertEquals("my_new_contaminant", testContaminant.getName());
    assertEquals("3 μg", testContaminant.getQuantity());
    assertEquals("some_comment", testContaminant.getComment());
    // Validate activity log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertEquals(1, newSubmissionSample.getContaminants().size());
    testContaminant = newSubmissionSample.getContaminants().get(0);
    assertEquals("my_new_contaminant", testContaminant.getName());
    assertEquals("3 μg", testContaminant.getQuantity());
    assertEquals("some_comment", testContaminant.getComment());
  }

  @Test
  public void update_UpdateContaminant() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample);
    for (Contaminant contaminant : sample.getContaminants()) {
      entityManager.detach(contaminant);
    }
    Contaminant update = sample.getContaminants().get(0);
    update.setName("new_contaminant_name");
    update.setQuantity("1 pmol");
    update.setComment("new_comment");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant update.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getContaminants().size());
    Contaminant testContaminant = test.getContaminants().get(0);
    assertEquals("new_contaminant_name", testContaminant.getName());
    assertEquals("1 pmol", testContaminant.getQuantity());
    assertEquals("new_comment", testContaminant.getComment());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertTrue(newSubmissionSample.getContaminants().size() == 1);
    testContaminant = newSubmissionSample.getContaminants().get(0);
    assertEquals("new_contaminant_name", testContaminant.getName());
    assertEquals("1 pmol", testContaminant.getQuantity());
    assertEquals("new_comment", testContaminant.getComment());
  }

  @Test
  public void update_RemoveContaminant() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample);
    sample.getContaminants().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    // Update sample.
    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate contaminant deletion.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(0, test.getContaminants().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertEquals(0, newSubmissionSample.getContaminants().size());
  }

  @Test
  public void update_AddStandard() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 442L);
    entityManager.detach(sample);
    Standard standard = new Standard();
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComment("some_comment");
    sample.getStandards().add(standard);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    // Update sample.
    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard insertion.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3 μg", testStandard.getQuantity());
    assertEquals("some_comment", testStandard.getComment());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertEquals(1, newSubmissionSample.getStandards().size());
    testStandard = newSubmissionSample.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3 μg", testStandard.getQuantity());
    assertEquals("some_comment", testStandard.getComment());
  }

  @Test
  public void update_UpdateStandard() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample);
    for (Standard standard : sample.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = sample.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComment("new_comment");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard update.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1 pmol", testStandard.getQuantity());
    assertEquals("new_comment", testStandard.getComment());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertTrue(newSubmissionSample.getStandards().size() == 1);
    testStandard = newSubmissionSample.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1 pmol", testStandard.getQuantity());
    assertEquals("new_comment", testStandard.getComment());
  }

  @Test
  public void update_RemoveStandard() {
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample);
    sample.getStandards().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.update(sample, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard deletion.
    SubmissionSample test = entityManager.find(SubmissionSample.class, sample.getId());
    entityManager.refresh(test);
    assertEquals(0, test.getStandards().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof SubmissionSample);
    SubmissionSample newSubmissionSample = (SubmissionSample) newSample;
    assertEquals(0, newSubmissionSample.getStandards().size());
  }

  @Test
  public void updateStatus() throws Throwable {
    SubmissionSample sample1 = entityManager.find(SubmissionSample.class, 443L);
    entityManager.detach(sample1);
    sample1.setStatus(SampleStatus.DIGESTED);
    SubmissionSample sample2 = entityManager.find(SubmissionSample.class, 445L);
    entityManager.detach(sample2);
    sample2.setStatus(SampleStatus.RECEIVED);
    Collection<SubmissionSample> samples = new LinkedList<>();
    samples.add(sample1);
    samples.add(sample2);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    submissionSampleService.updateStatus(samples);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    SubmissionSample testSample1 = entityManager.find(SubmissionSample.class, 443L);
    SubmissionSample testSample2 = entityManager.find(SubmissionSample.class, 445L);
    assertEquals(SampleStatus.DIGESTED, testSample1.getStatus());
    assertEquals(SampleStatus.RECEIVED, testSample2.getStatus());
    verify(sampleActivityService, times(2)).update(sampleCaptor.capture(), isNull(String.class));
    verify(activityService, times(2)).insert(activity);
    SubmissionSample newTestSample1 = (SubmissionSample) sampleCaptor.getAllValues().get(0);
    assertEquals(SampleStatus.DIGESTED, newTestSample1.getStatus());
    SubmissionSample newTestSample2 = (SubmissionSample) sampleCaptor.getAllValues().get(1);
    assertEquals(SampleStatus.RECEIVED, newTestSample2.getStatus());
  }
}
