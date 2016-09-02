package ca.qc.ircm.proview.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.GelSample;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class StandardAdditionServiceImplTest {
  private StandardAdditionServiceImpl standardAdditionServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private StandardAdditionActivityService standardAdditionActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<Collection<SampleContainer>> containersCaptor;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    standardAdditionServiceImpl = new StandardAdditionServiceImpl(entityManager, queryFactory,
        standardAdditionActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private SampleContainer findContainer(Collection<SampleContainer> containers,
      SampleContainer.Type type, long id) {
    for (SampleContainer container : containers) {
      if (container.getId() == id && container.getType() == type) {
        return container;
      }
    }
    return null;
  }

  @Test
  public void get() {
    StandardAddition standardAddition = standardAdditionServiceImpl.get(5L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(standardAddition);
    assertEquals((Long) 5L, standardAddition.getId());
    assertEquals(Treatment.Type.STANDARD_ADDITION, standardAddition.getType());
    assertEquals((Long) 2L, standardAddition.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 12, 2).atZone(ZoneId.systemDefault()).toInstant(),
        standardAddition.getInsertTime());
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionType());
    assertEquals(null, standardAddition.getDeletionJustification());
    List<AddedStandard> addedStandards = standardAddition.getTreatmentSamples();
    assertEquals(1, addedStandards.size());
    AddedStandard addedStandard = addedStandards.get(0);
    assertEquals((Long) 444L, addedStandard.getSample().getId());
    assertEquals(SampleContainer.Type.TUBE, addedStandard.getContainer().getType());
    assertEquals((Long) 4L, addedStandard.getContainer().getId());
    assertEquals(null, addedStandard.getComments());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0", addedStandard.getQuantity());
    assertEquals(AddedStandard.QuantityUnit.MICRO_GRAMS, addedStandard.getQuantityUnit());
  }

  @Test
  public void get_Null() {
    StandardAddition standardAddition = standardAdditionServiceImpl.get(null);

    assertNull(standardAddition);
  }

  @Test
  public void all_Tube() {
    Sample sample = new EluateSample(444L);

    List<StandardAddition> standardAdditions = standardAdditionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, standardAdditions.size());
    StandardAddition standardAddition = standardAdditions.get(0);
    assertEquals((Long) 5L, standardAddition.getId());
  }

  @Test
  public void all_Spot() {
    Sample sample = new EluateSample(599L);

    List<StandardAddition> standardAdditions = standardAdditionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, standardAdditions.size());
    StandardAddition standardAddition = standardAdditions.get(0);
    assertEquals((Long) 248L, standardAddition.getId());
  }

  @Test
  public void all_Null() {
    List<StandardAddition> standardAdditions = standardAdditionServiceImpl.all(null);

    assertEquals(0, standardAdditions.size());
  }

  @Test
  public void insert_Tube() {
    final List<AddedStandard> addedStandards = new ArrayList<AddedStandard>();
    Sample sample = new GelSample(1L);
    Tube tube = new Tube(1L);
    AddedStandard addedStandard = new AddedStandard();
    addedStandard.setComments("unit test");
    addedStandard.setSample(sample);
    addedStandard.setContainer(tube);
    addedStandard.setName("unit_test_added_standard");
    addedStandard.setQuantity("20.0");
    addedStandard.setQuantityUnit(AddedStandard.QuantityUnit.MICRO_GRAMS);
    addedStandards.add(addedStandard);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    standardAdditionServiceImpl.insert(standardAddition);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).insert(eq(standardAddition));
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionType());
    assertEquals(null, standardAddition.getDeletionJustification());
    assertEquals(user, standardAddition.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(standardAddition.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(standardAddition.getInsertTime()));
    assertEquals(1, standardAddition.getTreatmentSamples().size());
    addedStandard = standardAddition.getTreatmentSamples().get(0);
    assertEquals("unit test", addedStandard.getComments());
    assertEquals((Long) 1L, addedStandard.getSample().getId());
    assertEquals(SampleContainer.Type.TUBE, addedStandard.getContainer().getType());
    assertEquals((Long) 1L, addedStandard.getContainer().getId());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0", addedStandard.getQuantity());
    assertEquals(AddedStandard.QuantityUnit.MICRO_GRAMS, addedStandard.getQuantityUnit());
  }

  @Test
  public void insert_Spot() {
    final List<AddedStandard> addedStandards = new ArrayList<AddedStandard>();
    GelSample sample = new GelSample(1L);
    PlateSpot spot = new PlateSpot(128L);
    AddedStandard addedStandard = new AddedStandard();
    addedStandard.setComments("unit test");
    addedStandard.setSample(sample);
    addedStandard.setContainer(spot);
    addedStandard.setName("unit_test_added_standard");
    addedStandard.setQuantity("20.0");
    addedStandard.setQuantityUnit(AddedStandard.QuantityUnit.MICRO_GRAMS);
    addedStandards.add(addedStandard);
    StandardAddition standardAddition = new StandardAddition();
    standardAddition.setTreatmentSamples(addedStandards);
    when(standardAdditionActivityService.insert(any(StandardAddition.class))).thenReturn(activity);

    standardAdditionServiceImpl.insert(standardAddition);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).insert(eq(standardAddition));
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(false, standardAddition.isDeleted());
    assertEquals(null, standardAddition.getDeletionType());
    assertEquals(null, standardAddition.getDeletionJustification());
    assertEquals(user, standardAddition.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(standardAddition.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(standardAddition.getInsertTime()));
    assertEquals(1, standardAddition.getTreatmentSamples().size());
    addedStandard = standardAddition.getTreatmentSamples().get(0);
    assertEquals("unit test", addedStandard.getComments());
    assertEquals((Long) 1L, addedStandard.getSample().getId());
    assertEquals(SampleContainer.Type.SPOT, addedStandard.getContainer().getType());
    assertEquals((Long) 128L, addedStandard.getContainer().getId());
    assertEquals("unit_test_added_standard", addedStandard.getName());
    assertEquals("20.0", addedStandard.getQuantity());
    assertEquals(AddedStandard.QuantityUnit.MICRO_GRAMS, addedStandard.getQuantityUnit());
  }

  @Test
  public void undoErroneous() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoErroneous(any(StandardAddition.class),
        any(String.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoErroneous(standardAddition, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoErroneous(eq(standardAddition),
        eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, standardAddition.getDeletionType());
    assertEquals("undo unit test", standardAddition.getDeletionJustification());
  }

  @Test
  public void undoFailed_NoBan() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 5L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, standardAddition.getDeletionType());
    assertEquals("fail unit test", standardAddition.getDeletionJustification());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 248L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, standardAddition.getDeletionType());
    assertEquals("fail unit test", standardAddition.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 997L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1009L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 997L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1009L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 249L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, standardAddition.getDeletionType());
    assertEquals("fail unit test", standardAddition.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 53L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 54L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 998L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1010L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 53L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 54L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 998L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1010L));
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 250L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    StandardAddition test = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 55L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 56L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 999L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1011L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1023L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1035L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 55L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 56L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 999L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1011L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1023L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1035L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 251L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, standardAddition.getDeletionType());
    assertEquals("fail unit test", standardAddition.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 57L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 58L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1000L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1012L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1090L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1102L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1114L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1126L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 57L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 58L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1000L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1012L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1090L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1102L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1114L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1126L));
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    StandardAddition standardAddition = entityManager.find(StandardAddition.class, 252L);
    entityManager.detach(standardAddition);
    when(standardAdditionActivityService.undoFailed(any(StandardAddition.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    standardAdditionServiceImpl.undoFailed(standardAddition, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(standardAdditionActivityService).undoFailed(eq(standardAddition), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    standardAddition = standardAdditionServiceImpl.get(standardAddition.getId());
    assertNotNull(standardAddition);
    assertEquals(true, standardAddition.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, standardAddition.getDeletionType());
    assertEquals("fail unit test", standardAddition.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 60L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 59L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1001L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1013L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1025L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1037L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1091L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1103L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1115L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1127L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 60L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 59L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1001L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1013L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1025L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1037L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1091L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1103L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1115L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1127L));
  }
}
