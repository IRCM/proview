package ca.qc.ircm.proview.enrichment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.GelSample;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
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
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EnrichmentActivityServiceImplTest {
  private EnrichmentActivityServiceImpl enrichmentActivityServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    enrichmentActivityServiceImpl =
        new EnrichmentActivityServiceImpl(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    final EnrichmentProtocol protocol = new EnrichmentProtocol(2L);
    Sample sample = new GelSample(1L);
    Tube sourceTube = new Tube(1L);
    EnrichedSample enrichedSample = new EnrichedSample();
    enrichedSample.setSample(sample);
    enrichedSample.setContainer(sourceTube);
    List<EnrichedSample> enrichedSamples = new ArrayList<EnrichedSample>();
    enrichedSamples.add(enrichedSample);
    Enrichment enrichment = new Enrichment();
    enrichment.setId(123456L);
    enrichment.setProtocol(protocol);
    enrichment.setTreatmentSamples(enrichedSamples);

    Activity activity = enrichmentActivityServiceImpl.insert(enrichment);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoErroneous() {
    Enrichment enrichment = new Enrichment(7L);

    Activity activity = enrichmentActivityServiceImpl.undoErroneous(enrichment, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Enrichment enrichment = new Enrichment(7L);

    Activity activity = enrichmentActivityServiceImpl.undoFailed(enrichment, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Enrichment enrichment = new Enrichment(7L);
    Tube sourceTube = new Tube(4L);
    PlateSpot spot = new PlateSpot(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(spot);

    Activity activity =
        enrichmentActivityServiceImpl.undoFailed(enrichment, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
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
    Enrichment enrichment = new Enrichment(7L);
    Tube sourceTube = new Tube(4L);
    Collection<SampleContainer> bannedContainers = new ArrayList<SampleContainer>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity =
        enrichmentActivityServiceImpl.undoFailed(enrichment, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getJustification().length());
    assertEquals(reasonCutAt255Bytes, activity.getJustification());
  }
}
