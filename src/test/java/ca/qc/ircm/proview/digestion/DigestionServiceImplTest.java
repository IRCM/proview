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

package ca.qc.ircm.proview.digestion;

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
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SampleContainerType;
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
public class DigestionServiceImplTest {
  private DigestionServiceImpl digestionServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private DigestionActivityService digestionActivityService;
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
    digestionServiceImpl = new DigestionServiceImpl(entityManager, queryFactory,
        digestionActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private SampleContainer findContainer(Collection<SampleContainer> containers,
      SampleContainerType type, long id) {
    for (SampleContainer container : containers) {
      if (container.getId() == id && container.getType() == type) {
        return container;
      }
    }
    return null;
  }

  @Test
  public void get() {
    Digestion digestion = digestionServiceImpl.get(6L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(digestion);
    assertEquals((Long) 6L, digestion.getId());
    assertEquals(Treatment.Type.DIGESTION, digestion.getType());
    assertEquals((Long) 2L, digestion.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 15, 20).atZone(ZoneId.systemDefault()).toInstant(),
        digestion.getInsertTime());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionJustification());
    List<DigestedSample> digestedSamples = digestion.getTreatmentSamples();
    assertEquals(1, digestedSamples.size());
    DigestedSample digestedSample = digestedSamples.get(0);
    assertEquals((Long) 444L, digestedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, digestedSample.getContainer().getType());
    assertEquals((Long) 4L, digestedSample.getContainer().getId());
    assertEquals(null, digestedSample.getComments());
  }

  @Test
  public void get_Null() {
    Digestion digestion = digestionServiceImpl.get(null);

    assertNull(digestion);
  }

  @Test
  public void all_Tube() {
    Sample sample = new SubmissionSample(444L);

    List<Digestion> digestions = digestionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, digestions.size());
    Digestion digestion = digestions.get(0);
    assertEquals((Long) 6L, digestion.getId());
  }

  @Test
  public void all_Spot() {
    Sample sample = new SubmissionSample(559L);

    List<Digestion> digestions = digestionServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, digestions.size());
    Digestion digestion = digestions.get(0);
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void all_Null() {
    List<Digestion> digestions = digestionServiceImpl.all(null);

    assertTrue(digestions.isEmpty());
  }

  @Test
  public void insert_Tube() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(new DigestionProtocol(1L));
    SubmissionSample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    final List<DigestedSample> digestedSamples = new ArrayList<DigestedSample>();
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setComments("unit test");
    digestedSample.setSample(sample);
    digestedSample.setContainer(tube);
    digestedSamples.add(digestedSample);
    digestion.setTreatmentSamples(digestedSamples);
    when(digestionActivityService.insert(any(Digestion.class))).thenReturn(activity);

    digestionServiceImpl.insert(digestion);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = entityManager.find(Digestion.class, digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionJustification());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertEquals((Long) 1L, digestion.getProtocol().getId());
    assertEquals(1, digestion.getTreatmentSamples().size());
    digestedSample = digestion.getTreatmentSamples().get(0);
    assertEquals("unit test", digestedSample.getComments());
    assertEquals((Long) 1L, digestedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, digestedSample.getContainer().getType());
    assertEquals((Long) 1L, digestedSample.getContainer().getId());
  }

  @Test
  public void insert_Spot() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(new DigestionProtocol(1L));
    SubmissionSample sample = new SubmissionSample(1L);
    PlateSpot spot = new PlateSpot(128L);
    final List<DigestedSample> digestedSamples = new ArrayList<DigestedSample>();
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setComments("unit test");
    digestedSample.setSample(sample);
    digestedSample.setContainer(spot);
    digestedSamples.add(digestedSample);
    digestion.setTreatmentSamples(digestedSamples);
    when(digestionActivityService.insert(any(Digestion.class))).thenReturn(activity);

    digestionServiceImpl.insert(digestion);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = digestionServiceImpl.get(digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionJustification());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertEquals((Long) 1L, digestion.getProtocol().getId());
    assertEquals(1, digestion.getTreatmentSamples().size());
    digestedSample = digestion.getTreatmentSamples().get(0);
    assertEquals("unit test", digestedSample.getComments());
    assertEquals((Long) 1L, digestedSample.getSample().getId());
    assertEquals(SampleContainerType.SPOT, digestedSample.getContainer().getType());
    assertEquals((Long) 128L, digestedSample.getContainer().getId());
  }

  @Test
  public void undoErroneous() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoErroneous(any(Digestion.class), any(String.class)))
        .thenReturn(activity);

    digestionServiceImpl.undoErroneous(digestion, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoErroneous(eq(digestion), eq("undo unit test"));
    verify(activityService).insert(activity);
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, digestion.getDeletionType());
    assertEquals("undo unit test", digestion.getDeletionJustification());
  }

  @Test
  public void undoFailed_NoBan() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionJustification());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 224L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 236L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 224L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 236L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Digestion digestion = entityManager.find(Digestion.class, 196L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = digestionServiceImpl.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 13L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 14L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 320L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 332L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 13L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 14L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 320L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 332L));
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    Digestion digestion = entityManager.find(Digestion.class, 197L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = digestionServiceImpl.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 15L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 16L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 322L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 334L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 346L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 358L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 15L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 16L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 322L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 334L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 346L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 358L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Digestion digestion = entityManager.find(Digestion.class, 198L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 17L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 18L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 321L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 333L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 416L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 428L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 440L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 452L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 17L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 18L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 321L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 333L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 416L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 428L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 440L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 452L));
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    Digestion digestion = entityManager.find(Digestion.class, 199L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 19L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 20L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 323L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 335L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 347L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 359L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 417L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 429L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 441L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 453L);
    assertEquals(true, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 19L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 20L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 323L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 335L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 347L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 359L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 417L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 429L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 441L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 453L));
  }

  @Test
  public void undoFailed_NotBanErroneousTransfer() {
    Digestion digestion = entityManager.find(Digestion.class, 321L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Digestion failed.
    digestionServiceImpl.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    digestion = digestionServiceImpl.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 2279L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1570L);
    assertEquals(false, spot.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.TUBE, 2279L));
  }
}
