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
    assertEquals(SampleType.GEL, gelSample.getType());
    assertEquals(Sample.Category.SUBMISSION, gelSample.getCategory());
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
    assertEquals(SampleType.SOLUTION, eluateSample.getType());
    assertEquals(Sample.Category.SUBMISSION, eluateSample.getCategory());
    assertEquals(SampleStatus.DATA_ANALYSIS, eluateSample.getStatus());
    assertEquals((Long) 32L, eluateSample.getSubmission().getId());
    assertEquals("1.5 μg", eluateSample.getQuantity());
    assertEquals("50 μl", eluateSample.getVolume());
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
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    boolean exists = submissionSampleService.exists("CAP_20111013_05");

    verify(authorizationService).checkUserRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_False() throws Throwable {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    boolean exists = submissionSampleService.exists("CAP_20111013_80");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_OtherUSer() throws Throwable {
    User user = new User(10L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    boolean exists = submissionSampleService.exists("CAP_20111013_05");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_ControlName() throws Throwable {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    boolean exists = submissionSampleService.exists("control_01");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Null() throws Throwable {
    User user = new User(3L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    boolean exists = submissionSampleService.exists(null);

    assertEquals(false, exists);
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
