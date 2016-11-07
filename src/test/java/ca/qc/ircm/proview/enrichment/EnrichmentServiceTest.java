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

package ca.qc.ircm.proview.enrichment;

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
public class EnrichmentServiceTest {
  private EnrichmentService enrichmentServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private EnrichmentActivityService enrichmentActivityService;
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
    enrichmentServiceImpl = new EnrichmentService(entityManager, queryFactory,
        enrichmentActivityService, activityService, authorizationService);
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
    Enrichment enrichment = enrichmentServiceImpl.get(7L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(enrichment);
    assertEquals((Long) 7L, enrichment.getId());
    assertEquals(Treatment.Type.ENRICHMENT, enrichment.getType());
    assertEquals((Long) 2L, enrichment.getUser().getId());
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(
        LocalDateTime.of(2011, 11, 9, 15, 20, 21).atZone(ZoneId.systemDefault()).toInstant(),
        enrichment.getInsertTime());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionType());
    assertEquals(null, enrichment.getDeletionJustification());
    List<EnrichedSample> enrichedSamples = enrichment.getTreatmentSamples();
    assertEquals(1, enrichedSamples.size());
    EnrichedSample enrichedSample = enrichedSamples.get(0);
    assertEquals((Long) 444L, enrichedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, enrichedSample.getContainer().getType());
    assertEquals((Long) 4L, enrichedSample.getContainer().getId());
    assertEquals(null, enrichedSample.getComments());
  }

  @Test
  public void get_Null() {
    Enrichment enrichment = enrichmentServiceImpl.get(null);

    assertNull(enrichment);
  }

  @Test
  public void all_Tube() {
    Sample sample = new SubmissionSample(444L);

    List<Enrichment> enrichments = enrichmentServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, enrichments.size());
    Enrichment enrichment = enrichments.get(0);
    assertEquals((Long) 7L, enrichment.getId());
  }

  @Test
  public void all_Spot() {
    Sample sample = new SubmissionSample(581L);

    List<Enrichment> enrichments = enrichmentServiceImpl.all(sample);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, enrichments.size());
    Enrichment enrichment = enrichments.get(0);
    assertEquals((Long) 225L, enrichment.getId());
  }

  @Test
  public void all_Null() {
    List<Enrichment> enrichments = enrichmentServiceImpl.all(null);

    assertEquals(0, enrichments.size());
  }

  @Test
  public void insert_Tube() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new EnrichmentProtocol(2L));
    final List<EnrichedSample> enrichedSamples = new ArrayList<EnrichedSample>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    EnrichedSample enrichedSample = new EnrichedSample();
    enrichedSample.setComments("unit test");
    enrichedSample.setSample(sample);
    enrichedSample.setContainer(tube);
    enrichedSamples.add(enrichedSample);
    enrichment.setTreatmentSamples(enrichedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    enrichmentServiceImpl.insert(enrichment);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionType());
    assertEquals(null, enrichment.getDeletionJustification());
    assertEquals(user, enrichment.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(enrichment.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(enrichment.getInsertTime()));
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(1, enrichment.getTreatmentSamples().size());
    enrichedSample = enrichment.getTreatmentSamples().get(0);
    assertEquals("unit test", enrichedSample.getComments());
    assertEquals((Long) 1L, enrichedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, enrichedSample.getContainer().getType());
    assertEquals((Long) 1L, enrichedSample.getContainer().getId());
  }

  @Test
  public void insert_Spot() {
    Enrichment enrichment = new Enrichment();
    enrichment.setProtocol(new EnrichmentProtocol(2L));
    final List<EnrichedSample> enrichedSamples = new ArrayList<EnrichedSample>();
    SubmissionSample sample = new SubmissionSample(1L);
    PlateSpot spot = new PlateSpot(128L);
    EnrichedSample enrichedSample = new EnrichedSample();
    enrichedSample.setComments("unit test");
    enrichedSample.setSample(sample);
    enrichedSample.setContainer(spot);
    enrichedSamples.add(enrichedSample);
    enrichment.setTreatmentSamples(enrichedSamples);
    when(enrichmentActivityService.insert(any(Enrichment.class))).thenReturn(activity);

    enrichmentServiceImpl.insert(enrichment);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).insert(eq(enrichment));
    verify(activityService).insert(eq(activity));
    assertNotNull(enrichment.getId());
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertEquals(false, enrichment.isDeleted());
    assertEquals(null, enrichment.getDeletionType());
    assertEquals(null, enrichment.getDeletionJustification());
    assertEquals(user, enrichment.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(enrichment.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(enrichment.getInsertTime()));
    assertEquals((Long) 2L, enrichment.getProtocol().getId());
    assertEquals(1, enrichment.getTreatmentSamples().size());
    enrichedSample = enrichment.getTreatmentSamples().get(0);
    assertEquals("unit test", enrichedSample.getComments());
    assertEquals((Long) 1L, enrichedSample.getSample().getId());
    assertEquals(SampleContainerType.SPOT, enrichedSample.getContainer().getType());
    assertEquals((Long) 128L, enrichedSample.getContainer().getId());
  }

  @Test
  public void undo_Erroneous() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoErroneous(any(Enrichment.class), any(String.class)))
        .thenReturn(activity);

    enrichmentServiceImpl.undoErroneous(enrichment, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoErroneous(eq(enrichment), eq("undo unit test"));
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, enrichment.getDeletionType());
    assertEquals("undo unit test", enrichment.getDeletionJustification());
  }

  @Test
  public void undo_Failed_NoBan() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Failed_Ban() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 800L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 812L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 800L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 812L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 225L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 801L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 813L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 896L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 908L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 801L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 813L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 896L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 908L));
  }

  @Test
  public void undo_Failed_Ban_Fractionation() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 226L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 825L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 837L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 897L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 909L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 921L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 933L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 825L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 837L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 897L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 909L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 921L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 933L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 227L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 849L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 861L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 898L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 910L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 803L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 815L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 827L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 839L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 849L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 861L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 898L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 910L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 803L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 815L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 827L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 839L));
  }

  @Test
  public void undo_Failed_Ban_Fractionation_Transfer() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 228L);
    entityManager.detach(enrichment);
    when(enrichmentActivityService.undoFailed(any(Enrichment.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    enrichmentServiceImpl.undoFailed(enrichment, "fail unit test", true);

    entityManager.flush();
    verify(enrichmentActivityService).undoFailed(eq(enrichment), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    enrichment = enrichmentServiceImpl.get(enrichment.getId());
    assertNotNull(enrichment);
    assertEquals(true, enrichment.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, enrichment.getDeletionType());
    assertEquals("fail unit test", enrichment.getDeletionJustification());
    PlateSpot spot = entityManager.find(PlateSpot.class, 873L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 885L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 899L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 911L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 923L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 935L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 802L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 814L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 826L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 838L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 873L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 885L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 899L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 911L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 923L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 935L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 802L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 814L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 826L));
    assertNotNull(findContainer(bannedContainers, SampleContainerType.SPOT, 838L));
  }
}
