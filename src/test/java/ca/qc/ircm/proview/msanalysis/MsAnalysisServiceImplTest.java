package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MsAnalysis.Source.LDTD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService.MsAnalysisAggregate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisServiceImplTest {
  private MsAnalysisServiceImpl msAnalysisServiceImpl;
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
    msAnalysisServiceImpl = new MsAnalysisServiceImpl(entityManager, queryFactory,
        msAnalysisActivityService, activityService, authorizationService);
  }

  private <D extends Data> D find(Collection<D> datas, long id) {
    for (D data : datas) {
      if (data.getId() == id) {
        return data;
      }
    }
    return null;
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
    MsAnalysis msAnalysis = msAnalysisServiceImpl.get(1L);

    verify(authorizationService).checkMsAnalysisReadPermission(msAnalysis);
    assertNotNull(msAnalysis);
    assertEquals((Long) 1L, msAnalysis.getId());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(MsAnalysis.Source.NSI, msAnalysis.getSource());
    assertEquals(
        LocalDateTime.of(2010, 12, 13, 14, 10, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        msAnalysis.getInsertTime());
    assertEquals(false, msAnalysis.isDeleted());
    assertEquals(null, msAnalysis.getDeletionType());
    assertEquals(null, msAnalysis.getDeletionJustification());
  }

  @Test
  public void get_Null() {
    MsAnalysis msAnalysis = msAnalysisServiceImpl.get((Long) null);

    assertNull(msAnalysis);
  }

  @Test
  public void get_Acquisition() {
    MsAnalysis msAnalysis = msAnalysisServiceImpl.get(new Acquisition(1L));

    verify(authorizationService).checkMsAnalysisReadPermission(msAnalysis);
    assertNotNull(msAnalysis);
    assertEquals((Long) 1L, msAnalysis.getId());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, msAnalysis.getMassDetectionInstrument());
    assertEquals(MsAnalysis.Source.NSI, msAnalysis.getSource());
    assertEquals(
        LocalDateTime.of(2010, 12, 13, 14, 10, 27, 0).atZone(ZoneId.systemDefault()).toInstant(),
        msAnalysis.getInsertTime());
    assertEquals(false, msAnalysis.isDeleted());
    assertEquals(null, msAnalysis.getDeletionType());
    assertEquals(null, msAnalysis.getDeletionJustification());
  }

  @Test
  public void get_Acquisition_Null() {
    MsAnalysis msAnalysis = msAnalysisServiceImpl.get((Acquisition) null);

    assertNull(msAnalysis);
  }

  @Test
  public void all() {
    SubmissionSample sample = new EluateSample(442L);

    List<MsAnalysis> msAnalyses = msAnalysisServiceImpl.all(sample);

    verify(authorizationService).checkSampleReadPermission(sample);
    assertEquals(1, msAnalyses.size());
    assertNotNull(find(msAnalyses, 12));
  }

  @Test
  public void all_Null() {
    List<MsAnalysis> msAnalyses = msAnalysisServiceImpl.all(null);

    assertEquals(0, msAnalyses.size());
  }

  @Test
  public void verifications() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);

    Map<VerificationType, Map<String, Boolean>> checks =
        msAnalysisServiceImpl.verifications(msAnalysis);

    verify(authorizationService).checkMsAnalysisReadPermission(msAnalysis);
    assertTrue(checks.get(VerificationType.SAMPLE).get("acquisitionFile"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("nitrogenQuantity"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("calibration"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("heliumQuantity"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("plaquePositionOnAutoSampler"));
    assertTrue(checks.get(VerificationType.SAMPLE).get("sampleVsSpot"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("lcPumpPressure"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("coolerTemperature"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("diskSpace"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("sonicatedPlaque"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("mobilePhaseQuantity"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("vacuum"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("drainingVialVolume"));
    assertTrue(checks.get(VerificationType.INSTRUMENT).get("qcPassed"));
    Boolean check = checks.get(VerificationType.INSTRUMENT).get("argonQuantity");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("openGaz");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("collisionEnergy");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("mcp");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("uncheckedAutoCid");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("checkedGhz");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("clMethod");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("msMethod");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("volume");
    assertFalse(check != null && check);
    check = checks.get(VerificationType.INSTRUMENT).get("spray");
    assertFalse(check != null && check);
  }

  @Test
  public void verifications_Null() {
    Map<VerificationType, Map<String, Boolean>> checks = msAnalysisServiceImpl.verifications(null);

    assertEquals(0, checks.size());
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
    Map<String, Boolean> instrumentVerifications = new HashMap<String, Boolean>();
    instrumentVerifications.put("nitrogenQuantity", true);
    instrumentVerifications.put("calibration", true);
    instrumentVerifications.put("heliumQuantity", true);
    instrumentVerifications.put("plaquePositionOnAutoSampler", true);
    instrumentVerifications.put("lcPumpPressure", true);
    instrumentVerifications.put("coolerTemperature", true);
    instrumentVerifications.put("diskSpace", true);
    instrumentVerifications.put("sonicatedPlaque", true);
    instrumentVerifications.put("mobilePhaseQuantity", true);
    instrumentVerifications.put("vacuum", true);
    instrumentVerifications.put("drainingVialVolume", true);
    instrumentVerifications.put("qcPassed", true);
    Map<String, Boolean> sampleVerifications = new HashMap<String, Boolean>();
    sampleVerifications.put("sampleVsSpot", true);
    sampleVerifications.put("acquisitionFile", true);
    Map<VerificationType, Map<String, Boolean>> verifications =
        new HashMap<VerificationType, Map<String, Boolean>>();
    verifications.put(VerificationType.INSTRUMENT, instrumentVerifications);
    verifications.put(VerificationType.SAMPLE, sampleVerifications);
    Acquisition acquisition = new Acquisition();
    acquisition.setContainer(tube);
    acquisition.setSample(tube.getSample());
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSampleListName("unit_test_sample_list");
    acquisition.setAcquisitionFile("XL_20100614_COU_09");
    acquisition.setListIndex(0);
    acquisition.setComments("unit_test_comments");
    List<Acquisition> acquisitions = new ArrayList<Acquisition>();
    acquisitions.add(acquisition);
    final MsAnalysis finalMsAnalysis = msAnalysis;
    final List<Acquisition> finalAcquisitions = acquisitions;
    final Map<VerificationType, Map<String, Boolean>> finalVerifications = verifications;
    MsAnalysisAggregate insertAggregate = new MsAnalysisAggregate() {
      @Override
      public MsAnalysis getMsAnalysis() {
        return finalMsAnalysis;
      }

      @Override
      public List<Acquisition> getAcquisitions() {
        return finalAcquisitions;
      }

      @Override
      public Map<VerificationType, Map<String, Boolean>> getVerifications() {
        return finalVerifications;
      }
    };
    when(msAnalysisActivityService.insert(any(MsAnalysisAggregate.class))).thenReturn(activity);

    try {
      msAnalysisServiceImpl.insert(insertAggregate);
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
    verifications = msAnalysisServiceImpl.verifications(msAnalysis);
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("nitrogenQuantity"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("calibration"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("heliumQuantity"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("plaquePositionOnAutoSampler"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("lcPumpPressure"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("coolerTemperature"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("diskSpace"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("sonicatedPlaque"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("mobilePhaseQuantity"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("vacuum"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("drainingVialVolume"));
    assertTrue(verifications.get(VerificationType.INSTRUMENT).get("qcPassed"));
    assertTrue(verifications.get(VerificationType.SAMPLE).get("sampleVsSpot"));
    assertTrue(verifications.get(VerificationType.SAMPLE).get("acquisitionFile"));
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
    assertEquals(verifications, msAnalysisAggregate.getVerifications());
  }

  @Test
  public void insert_SamplesFromMultipleUser() {
    final SubmissionSample sample1 = entityManager.find(SubmissionSample.class, 443L);
    final SubmissionSample sample2 = entityManager.find(SubmissionSample.class, 446L);
    final MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setMassDetectionInstrument(LTQ_ORBI_TRAP);
    msAnalysis.setSource(LDTD);
    final Map<VerificationType, Map<String, Boolean>> verifications =
        new HashMap<VerificationType, Map<String, Boolean>>();
    Map<String, Boolean> instrumentVerifications = new HashMap<String, Boolean>();
    instrumentVerifications.put("nitrogenQuantity", true);
    instrumentVerifications.put("calibration", true);
    instrumentVerifications.put("heliumQuantity", true);
    instrumentVerifications.put("plaquePositionOnAutoSampler", true);
    instrumentVerifications.put("lcPumpPressure", true);
    instrumentVerifications.put("coolerTemperature", true);
    instrumentVerifications.put("diskSpace", true);
    instrumentVerifications.put("sonicatedPlaque", true);
    instrumentVerifications.put("mobilePhaseQuantity", true);
    instrumentVerifications.put("vacuum", true);
    instrumentVerifications.put("drainingVialVolume", true);
    instrumentVerifications.put("qcPassed", true);
    Map<String, Boolean> sampleVerifications = new HashMap<String, Boolean>();
    sampleVerifications.put("sampleVsSpot", true);
    sampleVerifications.put("acquisitionFile", true);
    verifications.put(VerificationType.INSTRUMENT, instrumentVerifications);
    verifications.put(VerificationType.SAMPLE, sampleVerifications);
    Tube tube1 = new Tube(3L);
    tube1.setSample(sample1);
    final List<Acquisition> acquisitions = new ArrayList<Acquisition>();
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

      @Override
      public Map<VerificationType, Map<String, Boolean>> getVerifications() {
        return verifications;
      }
    };

    try {
      msAnalysisServiceImpl.insert(insertAggregate);
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
    final Map<VerificationType, Map<String, Boolean>> verifications =
        new HashMap<VerificationType, Map<String, Boolean>>();
    Map<String, Boolean> instrumentVerifications = new HashMap<String, Boolean>();
    instrumentVerifications.put("nitrogenQuantity", true);
    instrumentVerifications.put("calibration", true);
    instrumentVerifications.put("heliumQuantity", true);
    instrumentVerifications.put("plaquePositionOnAutoSampler", true);
    instrumentVerifications.put("lcPumpPressure", true);
    instrumentVerifications.put("coolerTemperature", true);
    instrumentVerifications.put("diskSpace", true);
    instrumentVerifications.put("sonicatedPlaque", true);
    instrumentVerifications.put("mobilePhaseQuantity", true);
    instrumentVerifications.put("vacuum", true);
    instrumentVerifications.put("drainingVialVolume", true);
    instrumentVerifications.put("qcPassed", true);
    Map<String, Boolean> sampleVerifications = new HashMap<String, Boolean>();
    sampleVerifications.put("sampleVsSpot", true);
    sampleVerifications.put("acquisitionFile", true);
    verifications.put(VerificationType.INSTRUMENT, instrumentVerifications);
    verifications.put(VerificationType.SAMPLE, sampleVerifications);
    final List<Acquisition> acquisitions = new ArrayList<Acquisition>();
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

      @Override
      public Map<VerificationType, Map<String, Boolean>> getVerifications() {
        return verifications;
      }
    };
    try {
      msAnalysisServiceImpl.insert(insertAggregate);
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

    msAnalysisServiceImpl.undoErroneous(msAnalysis, "undo unit test");

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoErroneous(eq(msAnalysis), eq("undo unit test"));
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.ERRONEOUS, msAnalysis.getDeletionType());
    assertEquals("undo unit test", msAnalysis.getDeletionJustification());
  }

  @Test
  public void undoFailed_NoBan() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", false);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionJustification());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(true, bannedContainers.isEmpty());
  }

  @Test
  public void undoFailed_Ban() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 12L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 2L);
    assertEquals(true, tube.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(1, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 2L));
  }

  @Test
  public void undoFailed_Ban_Transfer() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 22L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 85L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1472L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(2, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 85L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1472L));
  }

  @Test
  public void undoFailed_Ban_Fractionation() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 23L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 86L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1473L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1485L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(3, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 86L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1473L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1485L));
  }

  @Test
  public void undoFailed_Ban_Transfer_Fractionation() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 24L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(authorizationService).checkAdminRole();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    msAnalysis = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(msAnalysis);
    assertEquals(true, msAnalysis.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, msAnalysis.getDeletionType());
    assertEquals("fail unit test", msAnalysis.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 87L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1474L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1568L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1580L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(4, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 87L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1474L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1568L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1580L));
  }

  @Test
  public void undoFailed_Ban_Fractionation_Transfer() {
    MsAnalysis msAnalysis = entityManager.find(MsAnalysis.class, 25L);
    entityManager.detach(msAnalysis);
    when(msAnalysisActivityService.undoFailed(any(MsAnalysis.class), any(String.class),
        anyCollectionOf(SampleContainer.class))).thenReturn(activity);

    msAnalysisServiceImpl.undoFailed(msAnalysis, "fail unit test", true);

    entityManager.flush();
    verify(msAnalysisActivityService).undoFailed(eq(msAnalysis), eq("fail unit test"),
        sampleContainersCaptor.capture());
    verify(activityService).insert(activity);
    MsAnalysis test = entityManager.find(MsAnalysis.class, msAnalysis.getId());
    assertNotNull(test);
    assertEquals(true, test.isDeleted());
    assertEquals(MsAnalysis.DeletionType.FAILED, test.getDeletionType());
    assertEquals("fail unit test", test.getDeletionJustification());
    Tube tube = entityManager.find(Tube.class, 88L);
    assertEquals(true, tube.isBanned());
    PlateSpot spot = entityManager.find(PlateSpot.class, 1475L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1487L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1569L);
    assertEquals(true, spot.isBanned());
    spot = entityManager.find(PlateSpot.class, 1581L);
    assertEquals(true, spot.isBanned());
    Collection<SampleContainer> bannedContainers = sampleContainersCaptor.getValue();
    assertEquals(5, bannedContainers.size());
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.TUBE, 88L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1475L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1487L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1569L));
    assertNotNull(findContainer(bannedContainers, SampleContainer.Type.SPOT, 1581L));
  }
}
