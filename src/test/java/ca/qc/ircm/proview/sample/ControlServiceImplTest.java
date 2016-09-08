package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.sample.Standard.QuantityUnit;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ControlServiceImplTest {
  private ControlServiceImpl controlServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SampleActivityService sampleActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private TubeService tubeService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Control> controlCaptor;
  @Captor
  private ArgumentCaptor<Sample> sampleCaptor;
  @Captor
  private ArgumentCaptor<Collection<String>> stringsCaptor;
  private Optional<Activity> optionalActivity;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    controlServiceImpl = new ControlServiceImpl(entityManager, queryFactory, sampleActivityService,
        activityService, tubeService, authorizationService);
    optionalActivity = Optional.of(activity);
  }

  @Test
  public void get_Id() {
    Control control = controlServiceImpl.get(444L);

    verify(authorizationService).checkAdminRole();
    assertEquals((Long) 444L, control.getId());
    assertEquals("CONTROL.1", control.getLims());
    assertEquals("control_01", control.getName());
    assertEquals(Control.ControlType.NEGATIVE_CONTROL, control.getControlType());
    assertEquals(null, control.getComments());
    assertEquals(Sample.Support.GEL, control.getSupport());
    assertEquals(null, control.getVolume());
    assertEquals(null, control.getQuantity());
    assertEquals(true, control.getOriginalContainer() instanceof Tube);
    assertEquals((Long) 4L, control.getOriginalContainer().getId());
  }

  @Test
  public void get_NullId() {
    Control control = controlServiceImpl.get(null);

    assertNull(control);
  }

  @Test
  public void insert() {
    Control control = new Control();
    control.setName("nc_test_000001");
    control.setControlType(Control.ControlType.NEGATIVE_CONTROL);
    control.setSupport(Support.GEL);
    control.setVolume(20.0);
    control.setQuantity("12.0 mg");
    control.setComments("unit_test_comments");
    when(tubeService.generateTubeName(any(Sample.class), anyCollectionOf(String.class)))
        .thenReturn("nc_test_000001");
    when(sampleActivityService.insertControl(any(Control.class))).thenReturn(activity);

    controlServiceImpl.insert(control);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).insertControl(controlCaptor.capture());
    verify(tubeService).generateTubeName(eq(control), stringsCaptor.capture());
    assertEquals(true, stringsCaptor.getValue().isEmpty());
    verify(activityService).insert(activity);
    Control testControl = controlServiceImpl.get(control.getId());
    assertEquals(true, testControl.getLims().toUpperCase().startsWith("CONTROL"));
    assertEquals("nc_test_000001", testControl.getName());
    assertEquals(Support.GEL, testControl.getSupport());
    assertEquals((Double) 20.0, testControl.getVolume());
    assertEquals("12.0 mg", testControl.getQuantity());
    assertEquals("unit_test_comments", testControl.getComments());
    assertEquals(0, testControl.getStandards().size());
    // Validate log.
    testControl = controlCaptor.getValue();
    assertEquals("nc_test_000001", testControl.getName());
    assertEquals(Support.GEL, testControl.getSupport());
    assertEquals((Double) 20.0, testControl.getVolume());
    assertEquals("12.0 mg", testControl.getQuantity());
    assertEquals("unit_test_comments", testControl.getComments());
    assertEquals(true, testControl.getStandards() == null || testControl.getStandards().isEmpty());
  }

  @Test
  public void update() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    control.setComments("my_new_comment");
    control.setName("nc_test_000001");
    control.setControlType(Control.ControlType.POSITIVE_CONTROL);
    control.setSupport(Support.SOLUTION);
    control.setVolume(2.0);
    control.setQuantity("40 mg");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlServiceImpl.update(control, "test changes");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(sampleActivityService).update(sampleCaptor.capture(), eq("test changes"));
    verify(activityService).insert(activity);
    Control test = entityManager.find(Control.class, control.getId());
    entityManager.refresh(test);
    assertEquals("my_new_comment", test.getComments());
    assertEquals("nc_test_000001", test.getName());
    assertEquals(Control.ControlType.POSITIVE_CONTROL, test.getControlType());
    assertEquals(Support.SOLUTION, test.getSupport());
    assertEquals((Double) 2.0, test.getVolume());
    assertEquals("40 mg", test.getQuantity());
    assertEquals(0, test.getStandards().size());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals("my_new_comment", newControl.getComments());
    assertEquals("nc_test_000001", newControl.getName());
    assertEquals(Control.ControlType.POSITIVE_CONTROL, newControl.getControlType());
    assertEquals(Support.SOLUTION, newControl.getSupport());
    assertEquals((Double) 2.0, newControl.getVolume());
    assertEquals("40 mg", newControl.getQuantity());
    assertEquals(0, newControl.getStandards().size());
  }

  @Test
  public void update_AddStandard() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    Standard standard = new Standard();
    standard.setName("my_new_standard");
    standard.setQuantity("3");
    standard.setQuantityUnit(QuantityUnit.MICRO_GRAMS);
    standard.setComments("some_comments");
    control.getStandards().add(standard);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlServiceImpl.update(control, "test changes");

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
    assertEquals("3", testStandard.getQuantity());
    assertEquals(QuantityUnit.MICRO_GRAMS, testStandard.getQuantityUnit());
    assertEquals("some_comments", testStandard.getComments());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals(1, newControl.getStandards().size());
    testStandard = newControl.getStandards().get(0);
    assertEquals("my_new_standard", testStandard.getName());
    assertEquals("3", testStandard.getQuantity());
    assertEquals(QuantityUnit.MICRO_GRAMS, testStandard.getQuantityUnit());
    assertEquals("some_comments", testStandard.getComments());
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
    standard.setQuantity("1");
    standard.setQuantityUnit(QuantityUnit.PICO_MOL);
    standard.setComments("new_comments");
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlServiceImpl.update(control, "test changes");

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
    assertEquals("1", testStandard.getQuantity());
    assertEquals(QuantityUnit.PICO_MOL, testStandard.getQuantityUnit());
    assertEquals("new_comments", testStandard.getComments());
    // Validate log.
    Sample newSample = sampleCaptor.getValue();
    assertTrue(newSample instanceof Control);
    Control newControl = (Control) newSample;
    assertEquals(1, newControl.getStandards().size());
    testStandard = newControl.getStandards().get(0);
    assertEquals("new_standard_name", testStandard.getName());
    assertEquals("1", testStandard.getQuantity());
    assertEquals(QuantityUnit.PICO_MOL, testStandard.getQuantityUnit());
    assertEquals("new_comments", testStandard.getComments());
  }

  @Test
  public void update_RemoveStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    control.getStandards().remove(0);
    when(sampleActivityService.update(any(Sample.class), any(String.class)))
        .thenReturn(optionalActivity);

    controlServiceImpl.update(control, "test changes");

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
