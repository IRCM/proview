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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.LDTD;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.SearchUtils.findContainer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisServiceTest {
  private MsAnalysisService msAnalysisService;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private MsAnalysisActivityService msAnalysisActivityService;
  @Mock
  private ActivityService activityService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private Activity activity;
  @Captor
  private ArgumentCaptor<MsAnalysis> msAnalysisCaptor;
  @Captor
  private ArgumentCaptor<Collection<SampleContainer>> sampleContainersCaptor;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    msAnalysisService = new MsAnalysisService(entityManager, queryFactory,
        msAnalysisActivityService, activityService, authorizationService);
  }

  @Test
  public void get() {
    MsAnalysis msAnalysis = msAnalysisService.get(1L);

    verify(authorizationService).checkMsAnalysisReadPermission(msAnalysis);
    assertNotNull(msAnalysis);
    assertEquals((Long) 1L, msAnalysis.getId());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.NSI, msAnalysis.getSource());
    assertEquals(
        LocalDateTime.of(2010, 12, 13, 14, 10, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        msAnalysis.getInsertTime());
    assertEquals(false, msAnalysis.isDeleted());
    assertEquals(null, msAnalysis.getDeletionType());
    assertEquals(null, msAnalysis.getDeletionExplanation());
  }

  @Test
  public void get_Null() {
    MsAnalysis msAnalysis = msAnalysisService.get((Long) null);

    assertNull(msAnalysis);
  }

  @Test
  public void all_Submission() {
    Submission submission = new Submission(155L);

    List<MsAnalysis> msAnalyses = msAnalysisService.all(submission);

    verify(authorizationService).checkSubmissionReadPermission(submission);
    assertEquals(1, msAnalyses.size());
    assertTrue(find(msAnalyses, 21).isPresent());
  }

  @Test
  public void all_SubmissionNull() {
    List<MsAnalysis> msAnalyses = msAnalysisService.all((Submission) null);

    assertEquals(0, msAnalyses.size());
  }

  @Test
  public void insert() {
    Tube tube = entityManager.find(Tube.class, 3L);
    entityManager.detach(tube);
    SubmissionSample sample = (SubmissionSample) tube.getSample();
    entityManager.detach(sample);
    MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setMassDetectionInstrument(LTQ_ORBI_TRAP);
    msAnalysis.setSource(LDTD);
    Acquisition acquisition = new Acquisition();
    acquisition.setContainer(tube);
    acquisition.setSample(tube.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComment("unit_test_comment");
    List<Acquisition> acquisitions = new ArrayList<>();
    acquisitions.add(acquisition);
    msAnalysis.setAcquisitions(acquisitions);
    when(msAnalysisActivityService.insert(any())).thenReturn(activity);

    msAnalysisService.insert(msAnalysis);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).insert(eq(msAnalysis));
    verify(activityService).insert(activity);
    assertNotNull(msAnalysis.getId());
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    entityManager.refresh(msAnalysis);
    assertEquals(LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(LDTD, msAnalysis.getSource());
    Instant before = LocalDateTime.now().minusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(before.isBefore(msAnalysis.getInsertTime()));
    Instant after = LocalDateTime.now().plusMinutes(2).atZone(ZoneId.systemDefault()).toInstant();
    assertTrue(after.isAfter(msAnalysis.getInsertTime()));
    acquisitions = msAnalysis.getAcquisitions();
    assertEquals(1, acquisitions.size());
    acquisition = acquisitions.get(0);
    assertEquals((Long) 3L, acquisition.getContainer().getId());
    assertEquals((Long) 443L, acquisition.getSample().getId());
    assertEquals(new Integer(1), acquisition.getNumberOfAcquisition());
    assertEquals("unit_test_sample_list", acquisition.getSampleListName());
    assertEquals("XL_20100614_COU_09", acquisition.getAcquisitionFile());
    assertEquals((Integer) 0, acquisition.getListIndex());
    assertEquals("unit_test_comment", acquisition.getComment());
    SubmissionSample sampleStatus = entityManager.find(SubmissionSample.class, sample.getId());
    assertEquals(SampleStatus.ANALYSED, sampleStatus.getStatus());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final SubmissionSample sample1 = entityManager.find(SubmissionSample.class, 443L);
    final SubmissionSample sample2 = entityManager.find(SubmissionSample.class, 446L);
    final MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setMassDetectionInstrument(LTQ_ORBI_TRAP);
    msAnalysis.setSource(LDTD);
    Tube tube1 = new Tube(3L);
    tube1.setSample(sample1);
    final List<Acquisition> acquisitions = new ArrayList<>();
    Acquisition acquisition = new Acquisition();
    acquisition.setContainer(tube1);
    acquisition.setSample(tube1.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComment("unit_test_comment");
    acquisitions.add(acquisition);
    Tube tube2 = new Tube(8L);
    tube2.setSample(sample2);
    acquisition = new Acquisition();
    acquisition.setContainer(tube2);
    acquisition.setSample(tube2.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComment("unit_test_comment");
    acquisitions.add(acquisition);
    msAnalysis.setAcquisitions(acquisitions);

    try {
      msAnalysisService.insert(msAnalysis);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Success.
    }
  }

  @Test
  public void insert_SamplesFromOneUserAndControl() {
    Tube tube1 = entityManager.find(Tube.class, 3L);
    entityManager.detach(tube1);
    SubmissionSample sample1 = (SubmissionSample) tube1.getSample();
    entityManager.detach(sample1);
    Tube tube2 = entityManager.find(Tube.class, 4L);
    entityManager.detach(tube2);
    Control sample2 = (Control) tube2.getSample();
    entityManager.detach(sample2);
    final MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setMassDetectionInstrument(LTQ_ORBI_TRAP);
    msAnalysis.setSource(LDTD);
    final List<Acquisition> acquisitions = new ArrayList<>();
    Acquisition acquisition = new Acquisition();
    acquisition.setContainer(tube1);
    acquisition.setSample(tube1.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComment("unit_test_comment");
    acquisitions.add(acquisition);
    acquisition = new Acquisition();
    acquisition.setContainer(tube2);
    acquisition.setSample(tube2.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComment("unit_test_comment");
    acquisitions.add(acquisition);
    msAnalysis.setAcquisitions(acquisitions);

    try {
      msAnalysisService.insert(msAnalysis);
    } catch (IllegalArgumentException e) {
      fail("IllegalArgumentException not expected");
    }
  }

  @Test
  public void update() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    entityManager.detach(msAnalysis);
    msAnalysis.getAcquisitions().stream().forEach(ts -> entityManager.detach(ts));
    msAnalysis.setMassDetectionInstrument(MassDetectionInstrument.ORBITRAP_FUSION);
    msAnalysis.setSource(MassDetectionInstrumentSource.ESI);
    msAnalysis.getAcquisitions().get(0).setSampleListName("new_sample_list");
    msAnalysis.getAcquisitions().get(0).setAcquisitionFile("new_acquisition_file");
    msAnalysis.getAcquisitions().get(0).setComment("test update");
    msAnalysis.getAcquisitions().get(0).setContainer(new Tube(8L));
    msAnalysis.getAcquisitions().get(0).setSample(new SubmissionSample(446L));
    msAnalysis.getAcquisitions().get(1).setNumberOfAcquisition(2);
    Acquisition acquisition = new Acquisition();
    acquisition.setSampleListName("XL_20111115_01");
    acquisition.setAcquisitionFile("XL_20111115_COU_03");
    acquisition.setComment("test update new");
    acquisition.setSample(new SubmissionSample(445L));
    acquisition.setContainer(new Tube(5L));
    acquisition.setNumberOfAcquisition(2);
    acquisition.setListIndex(2);
    msAnalysis.getAcquisitions().add(acquisition);
    when(msAnalysisActivityService.update(any(), any())).thenReturn(Optional.of(activity));

    msAnalysisService.update(msAnalysis, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).update(eq(msAnalysis), eq("test explanation"));
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    assertNotNull(msAnalysis);
    assertEquals(MassDetectionInstrument.ORBITRAP_FUSION, msAnalysis.getMassDetectionInstrument());
    assertEquals(MassDetectionInstrumentSource.ESI, msAnalysis.getSource());
    assertEquals((Long) 8L, msAnalysis.getAcquisitions().get(0).getContainer().getId());
    assertEquals((Long) 446L, msAnalysis.getAcquisitions().get(0).getSample().getId());
    assertEquals("new_sample_list", msAnalysis.getAcquisitions().get(0).getSampleListName());
    assertEquals("new_acquisition_file", msAnalysis.getAcquisitions().get(0).getAcquisitionFile());
    assertEquals("test update", msAnalysis.getAcquisitions().get(0).getComment());
    assertEquals(new Integer(1), msAnalysis.getAcquisitions().get(0).getNumberOfAcquisition());
    assertEquals((Integer) 0, msAnalysis.getAcquisitions().get(0).getListIndex());
    assertEquals((Integer) 2, msAnalysis.getAcquisitions().get(0).getPosition());
    assertEquals(new Integer(2), msAnalysis.getAcquisitions().get(1).getNumberOfAcquisition());
    assertEquals((Integer) 1, msAnalysis.getAcquisitions().get(1).getListIndex());
    assertEquals((Integer) 1, msAnalysis.getAcquisitions().get(1).getPosition());
    assertEquals((Long) 5L, msAnalysis.getAcquisitions().get(2).getContainer().getId());
    assertEquals((Long) 445L, msAnalysis.getAcquisitions().get(2).getSample().getId());
    assertEquals("XL_20111115_01", msAnalysis.getAcquisitions().get(2).getSampleListName());
    assertEquals("XL_20111115_COU_03", msAnalysis.getAcquisitions().get(2).getAcquisitionFile());
    assertEquals("test update new", msAnalysis.getAcquisitions().get(2).getComment());
    assertEquals(new Integer(2), msAnalysis.getAcquisitions().get(2).getNumberOfAcquisition());
    assertEquals((Integer) 2, msAnalysis.getAcquisitions().get(2).getListIndex());
    assertEquals((Integer) 2, msAnalysis.getAcquisitions().get(2).getPosition());
  }

  @Test(expected = IllegalArgumentException.class)
  public void update_RemoveAcquisition() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    entityManager.detach(msAnalysis);
    msAnalysis.getAcquisitions().stream().forEach(ts -> entityManager.detach(ts));
    msAnalysis.getAcquisitions().remove(1);

    msAnalysisService.update(msAnalysis, "test explanation");
  }

  @Test
  public void update_NoActivity() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.update(any(), any())).thenReturn(Optional.empty());

    msAnalysisService.update(msAnalysis, "test explanation");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).update(eq(msAnalysis), eq("test explanation"));
    verify(activityService, never()).insert(any());
    msAnalysis = entityManager.find(MsAnalysis.class, 14L);
    assertNotNull(msAnalysis);
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
  }

  @Test
  public void undoErroneous() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoErroneous(any(MsAnalysis.class), any(String.class)))
        .thenReturn(activity);

    msAnalysisService.undoErroneous(msAnalysis, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoErroneous(eq(msAnalysis), eq("undo unit test"));
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.ERRONEOUS, msAnalysis.getDeletionType());
    assertEquals("undo unit test", msAnalysis.getDeletionExplanation());
  }

  @Test
  public void undoFailed_NoBan() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionExplanation());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 2L);
    assertEquals(true, tube.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 2L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 22L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 85L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1472L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 85L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1472L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 23L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 86L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1473L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1485L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(3, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 86L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1473L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1485L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 24L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 87L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1474L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1568L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1580L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 87L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1474L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1568L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1580L).isPresent());
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 25L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisService.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    MsAnalysis test = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionExplanation());
    Tube tube = entityManager.find(Tube.class, 88L);
    assertEquals(true, tube.isBanned());
    Well well = entityManager.find(Well.class, 1475L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1487L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1569L);
    assertEquals(true, well.isBanned());
    well = entityManager.find(Well.class, 1581L);
    assertEquals(true, well.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(5, bannedContainers.size());
    assertTrue(findContainer(bannedContainers, SampleContainerType.TUBE, 88L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1475L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1487L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1569L).isPresent());
    assertTrue(findContainer(bannedContainers, SampleContainerType.WELL, 1581L).isPresent());
  }
}
