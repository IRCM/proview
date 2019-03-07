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

package ca.qc.ircm.proview.solubilisation;

import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellRepository;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeRepository;
import ca.qc.ircm.proview.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SolubilisationServiceTest extends AbstractServiceTestCase {
  @Inject
  private SolubilisationService service;
  @Inject
  private SolubilisationRepository repository;
  @Inject
  private TubeRepository tubeRepository;
  @Inject
  private WellRepository wellRepository;
  @MockBean
  private SolubilisationActivityService solubilisationActivityService;
  @MockBean
  private ActivityService activityService;
  @MockBean
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
    user = new User(4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void get() {
    Solubilisation solubilisation = service.get(1L);

    verify(authorizationService).checkAdminRole();
    assertNotNull(solubilisation);
    assertEquals((Long) 1L, solubilisation.getId());
    assertEquals(TreatmentType.SOLUBILISATION, solubilisation.getType());
    assertEquals((Long) 4L, solubilisation.getUser().getId());
    assertEquals(
        LocalDateTime.of(2011, 10, 13, 11, 45, 0).atZone(ZoneId.systemDefault()).toInstant(),
        solubilisation.getInsertTime());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionExplanation());
    List<TreatedSample> treatedSamples = solubilisation.getTreatedSamples();
    assertEquals(1, treatedSamples.size());
    TreatedSample treatedSample = treatedSamples.get(0);
    assertEquals(solubilisation, treatedSample.getTreatment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    assertEquals(null, treatedSample.getComment());
    assertEquals("Methanol", treatedSample.getSolvent());
    assertEquals(20.0, treatedSample.getSolventVolume(), 0.01);
  }

  @Test
  public void get_Null() {
    Solubilisation solubilisation = service.get(null);

    assertNull(solubilisation);
  }

  @Test
  public void insert_Tube() {
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    Sample sample = new SubmissionSample(1L);
    Tube tube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(tube);
    treatedSample.setSolvent("Methanol");
    treatedSample.setSolventVolume(20.0);
    treatedSamples.add(treatedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatedSamples(treatedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    service.insert(solubilisation);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = service.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionExplanation());
    assertEquals(user.getId(), solubilisation.getUser().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatedSamples().size());
    treatedSample = solubilisation.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.TUBE, treatedSample.getContainer().getType());
    assertEquals((Long) 1L, treatedSample.getContainer().getId());
    assertEquals("Methanol", treatedSample.getSolvent());
    assertEquals((Double) 20.0, treatedSample.getSolventVolume());
  }

  @Test
  public void insert_Well() {
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    SubmissionSample sample = new SubmissionSample(1L);
    Well well = new Well(128L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setComment("unit test");
    treatedSample.setSample(sample);
    treatedSample.setContainer(well);
    treatedSample.setSolvent("Methanol");
    treatedSample.setSolventVolume(20.0);
    treatedSamples.add(treatedSample);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatedSamples(treatedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    service.insert(solubilisation);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).insert(eq(solubilisation));
    verify(activityService).insert(eq(activity));
    assertNotNull(solubilisation.getId());
    solubilisation = service.get(solubilisation.getId());
    assertEquals(false, solubilisation.isDeleted());
    assertEquals(null, solubilisation.getDeletionExplanation());
    assertEquals(user.getId(), solubilisation.getUser().getId());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(solubilisation.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(solubilisation.getInsertTime()));
    assertEquals(1, solubilisation.getTreatedSamples().size());
    treatedSample = solubilisation.getTreatedSamples().get(0);
    assertEquals("unit test", treatedSample.getComment());
    assertEquals((Long) 1L, treatedSample.getSample().getId());
    assertEquals(SampleContainerType.WELL, treatedSample.getContainer().getType());
    assertEquals((Long) 128L, treatedSample.getContainer().getId());
    assertEquals("Methanol", treatedSample.getSolvent());
    assertEquals((Double) 20.0, treatedSample.getSolventVolume());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    final Tube tube1 = tubeRepository.findOne(3L);
    final Tube tube2 = tubeRepository.findOne(8L);
    TreatedSample treatedSample1 = new TreatedSample();
    treatedSample1.setComment("unit test");
    treatedSample1.setSample(tube1.getSample());
    treatedSample1.setContainer(tube1);
    treatedSample1.setSolvent("Methanol");
    treatedSample1.setSolventVolume(20.0);
    treatedSamples.add(treatedSample1);
    TreatedSample treatedSample2 = new TreatedSample();
    treatedSample2.setComment("unit test");
    treatedSample2.setSample(tube2.getSample());
    treatedSample2.setContainer(tube2);
    treatedSample2.setSolvent("Methanol");
    treatedSample2.setSolventVolume(20.0);
    treatedSamples.add(treatedSample2);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatedSamples(treatedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    try {
      service.insert(solubilisation);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    final List<TreatedSample> treatedSamples = new ArrayList<>();
    final Tube tube1 = tubeRepository.findOne(3L);
    final Tube tube2 = tubeRepository.findOne(4L);
    TreatedSample treatedSample1 = new TreatedSample();
    treatedSample1.setComment("unit test");
    treatedSample1.setSample(tube1.getSample());
    treatedSample1.setContainer(tube1);
    treatedSample1.setSolvent("Methanol");
    treatedSample1.setSolventVolume(20.0);
    treatedSamples.add(treatedSample1);
    TreatedSample treatedSample2 = new TreatedSample();
    treatedSample2.setComment("unit test");
    treatedSample2.setSample(tube2.getSample());
    treatedSample2.setContainer(tube2);
    treatedSample2.setSolvent("Methanol");
    treatedSample2.setSolventVolume(20.0);
    treatedSamples.add(treatedSample2);
    Solubilisation solubilisation = new Solubilisation();
    solubilisation.setTreatedSamples(treatedSamples);
    when(solubilisationActivityService.insert(any(Solubilisation.class))).thenReturn(activity);

    try {
      service.insert(solubilisation);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void update() {
    Solubilisation solubilisation = repository.findOne(236L);
    detach(solubilisation);
    solubilisation.getTreatedSamples().stream().forEach(ts -> detach(ts));
    solubilisation.getTreatedSamples().get(0).setSolvent("ch3oh");
    solubilisation.getTreatedSamples().get(0).setSolventVolume(7.0);
    solubilisation.getTreatedSamples().get(0).setComment("test update");
    solubilisation.getTreatedSamples().get(0).setContainer(new Well(248L));
    solubilisation.getTreatedSamples().get(0).setSample(new Control(444L));
    when(solubilisationActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(solubilisation, "test explanation");

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).update(eq(solubilisation), eq("test explanation"));
    verify(activityService).insert(activity);
    solubilisation = repository.findOne(236L);
    assertNotNull(solubilisation);
    assertEquals((Long) 248L, solubilisation.getTreatedSamples().get(0).getContainer().getId());
    assertEquals((Long) 444L, solubilisation.getTreatedSamples().get(0).getSample().getId());
    assertEquals("ch3oh", solubilisation.getTreatedSamples().get(0).getSolvent());
    assertEquals(7.0, solubilisation.getTreatedSamples().get(0).getSolventVolume(), 0.0001);
    assertEquals("test update", solubilisation.getTreatedSamples().get(0).getComment());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveTreatedSample() {
    Solubilisation solubilisation = repository.findOne(236L);
    detach(solubilisation);
    solubilisation.getTreatedSamples().stream().forEach(ts -> detach(ts));
    solubilisation.getTreatedSamples().remove(1);
    when(solubilisationActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    service.update(solubilisation, "test explanation");
  }

  @Test
  public void undo_NoBan() {
    Solubilisation solubilisation = repository.findOne(1L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", false);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = service.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undo_Ban() {
    Solubilisation solubilisation = repository.findOne(236L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = service.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Well well = wellRepository.findOne(992L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1004L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 992L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1004L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer() {
    Solubilisation solubilisation = repository.findOne(237L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = service.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(44L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(43L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(993L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1005L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 44L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 43L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 993L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1005L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation() {
    Solubilisation solubilisation = repository.findOne(238L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    Solubilisation test = service.get(solubilisation.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(45L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(46L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(994L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1006L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1018L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1030L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(6, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 45L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 46L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 994L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1006L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1018L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1030L).isPresent());
  }

  @Test
  public void undo_Ban_Transfer_Fractionation() {
    Solubilisation solubilisation = repository.findOne(239L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = service.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(47L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(48L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(995L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1007L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1088L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1100L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1112L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1124L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(8, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 47L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 48L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 995L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1007L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1088L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1100L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1112L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1124L).isPresent());
  }

  @Test
  public void undo_Ban_Fractionation_Transfer() {
    Solubilisation solubilisation = repository.findOne(240L);
    detach(solubilisation);
    when(solubilisationActivityService.undoFailed(any(Solubilisation.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    service.undo(solubilisation, "fail unit test", true);

    repository.flush();
    verify(authorizationService).checkAdminRole();
    verify(solubilisationActivityService).undoFailed(eq(solubilisation), eq("fail unit test"),
        containersCaptor.capture());
    verify(activityService).insert(eq(activity));
    solubilisation = service.get(solubilisation.getId());
    assertNotNull(solubilisation);
    assertEquals(true, solubilisation.isDeleted());
    assertEquals("fail unit test", solubilisation.getDeletionExplanation());
    Tube tube = tubeRepository.findOne(49L);
    assertEquals(true, tube.isBanned());
    tube = tubeRepository.findOne(50L);
    assertEquals(true, tube.isBanned());
    Well well = wellRepository.findOne(996L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1008L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1020L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1032L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1089L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1101L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1113L);
    assertEquals(true, well.isBanned());
    well = wellRepository.findOne(1125L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = containersCaptor.getValue();
    assertEquals(10, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 49L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 50L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 996L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1008L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1020L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1032L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1089L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1101L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1113L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1125L).isPresent());
  }
}
