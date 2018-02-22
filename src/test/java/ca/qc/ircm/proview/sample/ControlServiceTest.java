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

import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlServiceTest {
  private ControlService controlService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SampleActivityService sampleActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Control> controlCaptor;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    controlService = new ControlService(entityManager, queryFactory, sampleActivityService,
        activityService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get_Id() {
    Control control = controlService.get(444L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 444L, control.getId());
    assertEquals("control_01", control.getName());
    assertEquals(ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertEquals(SampleType.GEL, control.getType());
    assertEquals(null, control.getVolume());
    assertEquals(null, control.getQuantity());
    assertEquals(true, control.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 4L, control.getOriginalContainer().getId());
  }

  @Test
  public void get_NullId() {
    Control control = controlService.get(null);

    assertNull(control);
  }

  @Test
  public void all() {
    List<Control> controls = controlService.all();

    verify(authorizationService).checkAdminRole();
    assertEquals(2, controls.size());
    assertTrue(find(controls, 444).isPresent());
    assertTrue(find(controls, 448).isPresent());
  }

  @Test
  public void exists_True() throws Throwable {
    boolean exists = controlService.exists("control_01");

    verify(authorizationService).checkUserRole();
    assertEquals(true, exists);
  }

  @Test
  public void exists_False() throws Throwable {
    boolean exists = controlService.exists("control_AB");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_SubmissionSampleName() throws Throwable {
    boolean exists = controlService.exists("CAP_20111013_05");

    verify(authorizationService).checkUserRole();
    assertEquals(false, exists);
  }

  @Test
  public void exists_Null() throws Throwable {
    boolean exists = controlService.exists(null);

    assertEquals(false, exists);
  }

  @Test
  public void insert() {
    Control control = new Control();
    control.setName("nc_test_000001");
    control.setControlType(ControlType.NEGATIVE_CONTROL);
    control.setType(SampleType.GEL);
    control.setVolume("20.0 μl");
    control.setQuantity("12.0 μg");
    control.setStandards(new ArrayList<>());
    Standard standard1 = new Standard();
    standard1.setName("std1");
    standard1.setQuantity("1 ug");
    standard1.setComment("com1");
    control.getStandards().add(standard1);
    Standard standard2 = new Standard();
    standard2.setName("std2");
    standard2.setQuantity("2 ug");
    standard2.setComment("com2");
    control.getStandards().add(standard2);
    when(sampleActivityService.insertControl(any(Control.class))).thenReturn(activity);

    controlService.insert(control);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).insertControl(controlCaptor.capture());
    verify(activityService).insert(activity);
    Control testControl = controlService.get(control.getId());
    assertEquals("nc_test_000001", testControl.getName());
    assertEquals(SampleType.GEL, testControl.getType());
    assertEquals("20.0 μl", testControl.getVolume());
    assertEquals("12.0 μg", testControl.getQuantity());
    assertEquals(2, testControl.getStandards().size());
    Standard standard = testControl.getStandards().get(0);
    assertNotNull(standard.getId());
    assertEquals("std1", standard.getName());
    assertEquals("1 ug", standard.getQuantity());
    assertEquals("com1", standard.getComment());
    standard = testControl.getStandards().get(1);
    assertNotNull(standard.getId());
    assertEquals("std2", standard.getName());
    assertEquals("2 ug", standard.getQuantity());
    assertEquals("com2", standard.getComment());
    // Validate log.
    testControl = controlCaptor.getValue();
    assertEquals("nc_test_000001", testControl.getName());
    assertEquals(SampleType.GEL, testControl.getType());
    assertEquals("20.0 μl", testControl.getVolume());
    assertEquals("12.0 μg", testControl.getQuantity());
    assertEquals(2, testControl.getStandards().size());
    standard = testControl.getStandards().get(0);
    assertNotNull(standard.getId());
    assertEquals("std1", standard.getName());
    assertEquals("1 ug", standard.getQuantity());
    assertEquals("com1", standard.getComment());
    standard = testControl.getStandards().get(1);
    assertNotNull(standard.getId());
    assertEquals("std2", standard.getName());
    assertEquals("2 ug", standard.getQuantity());
    assertEquals("com2", standard.getComment());
  }

  @Test
  public void update() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    control.setName("nc_test_000001");
    control.setControlType(ControlType.POSITIVE_CONTROL);
    control.setType(SampleType.SOLUTION);
    control.setVolume("2.0 μl");
    control.setQuantity("40 μg");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlService.update(control, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    Control test = entityManager.find(Control.class, control.getId());
    entityManager.refresh(test);
    assertEquals("nc_test_000001", test.getName());
    assertEquals(ControlType.POSITIVE_CONTROL, test.getControlType());
    assertEquals(SampleType.SOLUTION, test.getType());
    assertEquals("2.0 μl", test.getVolume());
    assertEquals("40 μg", test.getQuantity());
    assertEquals(0, test.getStandards().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals("nc_test_000001", newControl.getName());
    assertEquals(ControlType.POSITIVE_CONTROL, newControl.getControlType());
    assertEquals(SampleType.SOLUTION, newControl.getType());
    assertEquals("2.0 μl", newControl.getVolume());
    assertEquals("40 μg", newControl.getQuantity());
    assertEquals(0, newControl.getStandards().size());
  }

  @Test
  public void update_AddStandard() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    Standard standard = new Standard();
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComment("some_comment");
    control.getStandards().add(standard);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlService.update(control, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate new standard.
    Control test = entityManager.find(Control.class, control.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3 μg", testStandard.getQuantity());
    assertEquals("some_comment", testStandard.getComment());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals(1, newControl.getStandards().size());
    testStandard = newControl.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3 μg", testStandard.getQuantity());
    assertEquals("some_comment", testStandard.getComment());
  }

  @Test
  public void update_UpdateStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    for (Standard standard : control.getStandards()) {
      entityManager.detach(standard);
    }
    // Change standard.
    Standard standard = control.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComment("new_comment");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlService.update(control, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard update.
    Control test = entityManager.find(Control.class, control.getId());
    entityManager.refresh(test);
    assertEquals(1, test.getStandards().size());
    Standard testStandard = test.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1 pmol", testStandard.getQuantity());
    assertEquals("new_comment", testStandard.getComment());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals(1, newControl.getStandards().size());
    testStandard = newControl.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1 pmol", testStandard.getQuantity());
    assertEquals("new_comment", testStandard.getComment());
  }

  @Test
  public void update_RemoveStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    control.getStandards().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlService.update(control, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    // Validate standard deletion.
    Control test = entityManager.find(Control.class, control.getId());
    entityManager.refresh(test);
    assertEquals(0, test.getStandards().size());
    // Validate activity log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals(0, newControl.getStandards().size());
  }
}
