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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService.MsAnalysisAggregate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.Sample;
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
  private ArgumentCaptor<MsAnalysisAggregate> msAnalysisAggregateCaptor;
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
  public void get_Acquisition() {
    MsAnalysis msAnalysis = msAnalysisService.get(new Acquisition(1L));

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
  public void get_Acquisition_Null() {
    MsAnalysis msAnalysis = msAnalysisService.get((Acquisition) null);

    assertNull(msAnalysis);
  }

  @Test
  public void all() {
    SubmissionSample sample = new SubmissionSample(442L);

    List<MsAnalysis> msAnalyses = msAnalysisService.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(1, msAnalyses.size());
    assertTrue(find(msAnalyses, 12).isPresent());
  }

  @Test
  public void all_SampleNull() {
    List<MsAnalysis> msAnalyses = msAnalysisService.all((Sample) null);

    assertEquals(0, msAnalyses.size());
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
    acquisition.setComments("unit_test_comments");
    List<Acquisition> acquisitions = new ArrayList<>();
    acquisitions.add(acquisition);
    final MsAnalysis finalMsAnalysis = msAnalysis;
    final List<Acquisition> finalAcquisitions = acquisitions;
    MsAnalysisAggregate insertAggregate = new MsAnalysisAggregate() {
      @Override
      public MsAnalysis getMsAnalysis() {
        return finalMsAnalysis;
      }

      @Override
      public List<Acquisition> getAcquisitions() {
        return finalAcquisitions;
      }
    };
    when(msAnalysisActivityService.insert(any(MsAnalysisAggregate.class))).thenReturn(activity);

    try {
      msAnalysisService.insert(insertAggregate);
    } catch (SamplesFromMultipleUserException e) {
      fail("SamplesFromMultipleUserException not expected");
    }

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).insert(msAnalysisAggregateCaptor.capture());
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
    assertEquals("unit_test_comments", acquisition.getComments());
    SubmissionSample sampleStatus = entityManager.find(SubmissionSample.class, sample.getId());
    assertEquals(SampleStatus.ANALYSED, sampleStatus.getStatus());
    MsAnalysisAggregate msAnalysisAggregate = msAnalysisAggregateCaptor.getValue();
    assertEquals(msAnalysis.getId(), msAnalysisAggregate.getMsAnalysis().getId());
    acquisitions = msAnalysisAggregate.getAcquisitions();
    assertEquals(1, acquisitions.size());
    acquisition = acquisitions.get(0);
    assertEquals((Long) 3L, acquisition.getContainer().getId());
    assertEquals((Long) 443L, acquisition.getSample().getId());
    assertEquals(new Integer(1), acquisition.getNumberOfAcquisition());
    assertEquals("unit_test_sample_list", acquisition.getSampleListName());
    assertEquals("XL_20100614_COU_09", acquisition.getAcquisitionFile());
    assertEquals((Integer) 0, acquisition.getListIndex());
    assertEquals("unit_test_comments", acquisition.getComments());
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
    acquisition.setComments("unit_test_comments");
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
    acquisition.setComments("unit_test_comments");
    acquisitions.add(acquisition);
    MsAnalysisAggregate insertAggregate = new MsAnalysisAggregate() {
      @Override
      public MsAnalysis getMsAnalysis() {
        return msAnalysis;
      }

      @Override
      public List<Acquisition> getAcquisitions() {
        return acquisitions;
      }
    };

    try {
      msAnalysisService.insert(insertAggregate);
      fail("Expected SamplesFromMultipleUserException");
    } catch (SamplesFromMultipleUserException e) {
      // Ignore.
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
    acquisition.setComments("unit_test_comments");
    acquisitions.add(acquisition);
    acquisition = new Acquisition();
    acquisition.setContainer(tube2);
    acquisition.setSample(tube2.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComments("unit_test_comments");
    acquisitions.add(acquisition);

    // Insert MS analysis.
    MsAnalysisAggregate insertAggregate = new MsAnalysisAggregate() {
      @Override
      public MsAnalysis getMsAnalysis() {
        return msAnalysis;
      }

      @Override
      public List<Acquisition> getAcquisitions() {
        return acquisitions;
      }
    };
    try {
      msAnalysisService.insert(insertAggregate);
    } catch (SamplesFromMultipleUserException e) {
      fail("SamplesFromMultipleUserException not expected");
    }
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
