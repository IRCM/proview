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

import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
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
public class DigestionServiceTest {
  private DigestionService digestionService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private DigestionProtocolService digestionProtocolService;
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
    digestionService = new DigestionService(entityManager, queryFactory, digestionProtocolService,
        digestionActivityService, activityService, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Digestion digestion = digestionService.get(6L);

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
    assertEquals(null, digestion.getDeletionExplanation());
    List<DigestedSample> digestedSamples = digestion.getTreatmentSamples();
    assertEquals(1, digestedSamples.size());
    DigestedSample digestedSample = digestedSamples.get(0);
    assertEquals(digestion, digestedSample.getDigestion());
    assertEquals((Long) 444L, digestedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, digestedSample.getContainer().getType());
    assertEquals((Long) 4L, digestedSample.getContainer().getId());
    assertEquals(null, digestedSample.getComments());
  }

  @Test
  public void get_Null() {
    Digestion digestion = digestionService.get(null);

    assertNull(digestion);
  }

  @Test
  public void all() {
    Submission submission = entityManager.find(Submission.class, 147L);

    List<Digestion> digestions = digestionService.all(submission);

    verify(authorizationService).checkAdminRole();
    assertEquals(1, digestions.size());
    Digestion digestion = digestions.get(0);
    assertEquals((Long) 195L, digestion.getId());
  }

  @Test
  public void all_Null() {
    List<Digestion> digestions = digestionService.all(null);

    assertTrue(digestions.isEmpty());
  }

  @Test
  public void insert_Tube() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(new DigestionProtocol(1L));
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    entityManager.detach(sample);
    Tube tube = new Tube(1L);
    final List<DigestedSample> digestedSamples = new ArrayList<>();
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setComments("unit test");
    digestedSample.setSample(sample);
    digestedSample.setContainer(tube);
    digestedSamples.add(digestedSample);
    digestion.setTreatmentSamples(digestedSamples);
    when(digestionActivityService.insert(any(Digestion.class))).thenReturn(activity);

    digestionService.insert(digestion);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = entityManager.find(Digestion.class, digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionExplanation());
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
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test
  public void insert_Well() {
    Digestion digestion = new Digestion();
    digestion.setProtocol(new DigestionProtocol(1L));
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    entityManager.detach(sample);
    Well well = new Well(128L);
    final List<DigestedSample> digestedSamples = new ArrayList<>();
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setComments("unit test");
    digestedSample.setSample(sample);
    digestedSample.setContainer(well);
    digestedSamples.add(digestedSample);
    digestion.setTreatmentSamples(digestedSamples);
    when(digestionActivityService.insert(any(Digestion.class))).thenReturn(activity);

    digestionService.insert(digestion);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = digestionService.get(digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionExplanation());
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
    assertEquals(SampleContainerType.WELL, digestedSample.getContainer().getType());
    assertEquals((Long) 128L, digestedSample.getContainer().getId());
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test
  public void insert_NewProtocol() {
    Digestion digestion = new Digestion();
    DigestionProtocol protocol = new DigestionProtocol(null, "test protocol");
    digestion.setProtocol(protocol);
    SubmissionSample sample = entityManager.find(SubmissionSample.class, 1L);
    entityManager.detach(sample);
    Tube tube = new Tube(1L);
    final List<DigestedSample> digestedSamples = new ArrayList<>();
    DigestedSample digestedSample = new DigestedSample();
    digestedSample.setComments("unit test");
    digestedSample.setSample(sample);
    digestedSample.setContainer(tube);
    digestedSamples.add(digestedSample);
    digestion.setTreatmentSamples(digestedSamples);
    doAnswer(i -> {
      entityManager.persist(i.getArgumentAt(0, any()));
      return null;
    }).when(digestionProtocolService).insert(any());
    when(digestionActivityService.insert(any(Digestion.class))).thenReturn(activity);

    digestionService.insert(digestion);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionProtocolService).insert(eq(protocol));
    verify(digestionActivityService).insert(eq(digestion));
    verify(activityService).insert(activity);
    assertNotNull(digestion.getId());
    digestion = entityManager.find(Digestion.class, digestion.getId());
    assertEquals(false, digestion.isDeleted());
    assertEquals(null, digestion.getDeletionType());
    assertEquals(null, digestion.getDeletionExplanation());
    assertEquals(user, digestion.getUser());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(digestion.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(digestion.getInsertTime()));
    assertNotNull(digestion.getProtocol().getId());
    assertEquals(protocol.getName(), digestion.getProtocol().getName());
    assertEquals(1, digestion.getTreatmentSamples().size());
    digestedSample = digestion.getTreatmentSamples().get(0);
    assertEquals("unit test", digestedSample.getComments());
    assertEquals((Long) 1L, digestedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, digestedSample.getContainer().getType());
    assertEquals((Long) 1L, digestedSample.getContainer().getId());
    sample = entityManager.find(SubmissionSample.class, 1L);
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
  }

  @Test
  public void undoErroneous() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoErroneous(any(Digestion.class), any(String.class)))
        .thenReturn(activity);

    digestionService.undoErroneous(digestion, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoErroneous(eq(digestion), eq("undo unit test"));
    verify(activityService).insert(activity);
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.ERRONEOUS, digestion.getDeletionType());
    assertEquals("undo unit test", digestion.getDeletionExplanation());
  }

  @Test
  public void undoFailed_NoBan() {
    Digestion digestion = entityManager.find(Digestion.class, 195L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionService.undoFailed(digestion, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
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

    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Well well = entityManager.find(Well.class, 224L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 236L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 224L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 236L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    Digestion digestion = entityManager.find(Digestion.class, 196L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = digestionService.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 13L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 14L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 320L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 332L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 13L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 14L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 320L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 332L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    Digestion digestion = entityManager.find(Digestion.class, 197L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    Digestion test = digestionService.get(digestion.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 15L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 16L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 322L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 334L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 346L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 358L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 15L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 16L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 322L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 334L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 346L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 358L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    Digestion digestion = entityManager.find(Digestion.class, 198L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 17L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 18L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 321L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 333L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 416L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 428L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 440L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 452L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 17L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 18L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 321L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 333L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 416L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 428L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 440L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 452L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    Digestion digestion = entityManager.find(Digestion.class, 199L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(activity);
    // Test that digestion failed.
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 19L);
    assertEquals(true, tube.isBanned());
    tube = entityManager.find(Tube.class, 20L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 323L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 335L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 347L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 359L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 417L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 429L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 441L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 453L);
    assertEquals(true, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 19L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 20L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 323L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 335L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 347L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 359L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 417L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 429L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 441L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 453L).isPresent());
  }

  @Test
  public void undoFailed_NotBanErroneousTransfer() {
    Digestion digestion = entityManager.find(Digestion.class, 321L);
    entityManager.detach(digestion);
    when(digestionActivityService.undoFailed(any(Digestion.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    // Digestion failed.
    digestionService.undoFailed(digestion, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(digestionActivityService).undoFailed(eq(digestion), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    digestion = digestionService.get(digestion.getId());
    assertNotNull(digestion);
    assertEquals(true, digestion.isDeleted());
    assertEquals(Treatment.DeletionType.FAILED, digestion.getDeletionType());
    assertEquals("fail unit test", digestion.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 2279L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1570L);
    assertEquals(false, well.isBanned());
    // Test log.
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 2279L).isPresent());
  }
}
