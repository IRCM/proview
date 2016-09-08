package ca.qc.ircm.proview.msanalysis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.VerificationType;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService.MsAnalysisAggregate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MsAnalysisActivityServiceImplTest {
  private MsAnalysisActivityServiceImpl msAnalysisActivityServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;
  private MsAnalysisVerificationService realMsAnalysisVerificationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    msAnalysisActivityServiceImpl =
        new MsAnalysisActivityServiceImpl(entityManager, authorizationService);
    realMsAnalysisVerificationService = new MsAnalysisVerificationServiceImpl(true);
  }

  @Test
  public void insert() {
    final SubmissionSample sample = new EluateSample(443L);
    final Tube sourceTube = new Tube(348L);
    final MsAnalysis msAnalysis = new MsAnalysis();
    msAnalysis.setId(123456L);
    msAnalysis.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    msAnalysis.setSource(MsAnalysis.Source.LDTD);
    Acquisition acquisition = new Acquisition();
    acquisition.setAcquisitionFile("unit_test_acquisition_file");
    acquisition.setListIndex(1);
    acquisition.setNumberOfAcquisition(1);
    acquisition.setSample(sample);
    acquisition.setSampleListName("unit_test_sample_list_name");
    acquisition.setContainer(sourceTube);
    final List<Acquisition> acquisitions = new ArrayList<Acquisition>();
    acquisitions.add(acquisition);
    final Map<VerificationType, Map<String, Boolean>> verifications =
        new HashMap<VerificationType, Map<String, Boolean>>();
    Map<VerificationType, Set<String>> expectedVerifications = realMsAnalysisVerificationService
        .verifications(MassDetectionInstrument.LTQ_ORBI_TRAP, MsAnalysis.Source.LDTD);
    for (VerificationType verificationType : expectedVerifications.keySet()) {
      verifications.put(verificationType, new HashMap<String, Boolean>());
      for (String verification : expectedVerifications.get(verificationType)) {
        verifications.get(verificationType).put(verification, true);
      }
    }
    sample.setStatus(SubmissionSample.Status.ANALYSED);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityServiceImpl.insert(new MsAnalysisAggregate() {
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
    });

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity sampleStatusActivity = new UpdateActivity();
    sampleStatusActivity.setActionType(ActionType.UPDATE);
    sampleStatusActivity.setTableName("sample");
    sampleStatusActivity.setRecordId(sample.getId());
    sampleStatusActivity.setColumn("status");
    sampleStatusActivity.setOldValue(SubmissionSample.Status.TO_APPROVE.name());
    sampleStatusActivity.setNewValue(SubmissionSample.Status.ANALYSED.name());
    expectedUpdateActivities.add(sampleStatusActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void undoErroneous() {
    MsAnalysis msAnalysis = new MsAnalysis(1L);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityServiceImpl.undoErroneous(msAnalysis, "unit_test");

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    MsAnalysis msAnalysis = new MsAnalysis(1L);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity = msAnalysisActivityServiceImpl.undoFailed(msAnalysis, "unit_test", null);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    final MsAnalysis msAnalysis = new MsAnalysis(1L);
    Tube sourceTube = new Tube(1L);
    PlateSpot spot = new PlateSpot(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(spot);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity =
        msAnalysisActivityServiceImpl.undoFailed(msAnalysis, "unit_test", bannedContainers);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("msanalysis", activity.getTableName());
    assertEquals(msAnalysis.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals((Long) 1L, activity.getUser().getId());
    final Collection<UpdateActivity> expecteds = new HashSet<UpdateActivity>();
    UpdateActivity bannedTubeActivity = new UpdateActivity();
    bannedTubeActivity.setActionType(ActionType.UPDATE);
    bannedTubeActivity.setTableName("samplecontainer");
    bannedTubeActivity.setRecordId(sourceTube.getId());
    bannedTubeActivity.setColumn("banned");
    bannedTubeActivity.setOldValue("0");
    bannedTubeActivity.setNewValue("1");
    expecteds.add(bannedTubeActivity);
    UpdateActivity bannedSpotActivity = new UpdateActivity();
    bannedSpotActivity.setActionType(ActionType.UPDATE);
    bannedSpotActivity.setTableName("samplecontainer");
    bannedSpotActivity.setRecordId(spot.getId());
    bannedSpotActivity.setColumn("banned");
    bannedSpotActivity.setOldValue("0");
    bannedSpotActivity.setNewValue("1");
    expecteds.add(bannedSpotActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undoFailed_LongDescription() throws Throwable {
    MsAnalysis msAnalysis = new MsAnalysis(1L);
    Tube sourceTube = new Tube(1L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Activity activity =
        msAnalysisActivityServiceImpl.undoFailed(msAnalysis, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(255, activity.getJustification().length());
    assertEquals(reasonCutAt255Bytes, activity.getJustification());
  }
}