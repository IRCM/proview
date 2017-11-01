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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class EnrichmentActivityServiceTest {
  private EnrichmentActivityService enrichmentActivityService;
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
    enrichmentActivityService = new EnrichmentActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    final EnrichmentProtocol protocol = new EnrichmentProtocol(2L);
    SubmissionSample sample = new SubmissionSample(1L);
    sample.setStatus(SampleStatus.ENRICHED);
    Tube sourceTube = new Tube(1L);
    EnrichedSample enrichedSample = new EnrichedSample();
    enrichedSample.setSample(sample);
    enrichedSample.setContainer(sourceTube);
    List<EnrichedSample> enrichedSamples = new ArrayList<>();
    enrichedSamples.add(enrichedSample);
    Enrichment enrichment = new Enrichment();
    enrichment.setId(123456L);
    enrichment.setProtocol(protocol);
    enrichment.setTreatmentSamples(enrichedSamples);

    Activity activity = enrichmentActivityService.insert(enrichment);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity sampleStatusActivity = new UpdateActivity();
    sampleStatusActivity.setActionType(ActionType.UPDATE);
    sampleStatusActivity.setTableName("sample");
    sampleStatusActivity.setRecordId(sample.getId());
    sampleStatusActivity.setColumn("status");
    sampleStatusActivity.setOldValue(SampleStatus.ANALYSED.name());
    sampleStatusActivity.setNewValue(SampleStatus.ENRICHED.name());
    expectedUpdateActivities.add(sampleStatusActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 223L);
    entityManager.detach(enrichment);
    enrichment.getTreatmentSamples().forEach(ts -> entityManager.detach(ts));
    enrichment.setProtocol(entityManager.find(EnrichmentProtocol.class, 4L));
    enrichment.getTreatmentSamples().get(0).setContainer(new Well(248L));
    enrichment.getTreatmentSamples().get(0).setSample(new Control(444L));
    enrichment.getTreatmentSamples().get(0).setComment("test");
    EnrichedSample newEnrichedSample = new EnrichedSample();
    newEnrichedSample.setId(400L);
    newEnrichedSample.setContainer(new Tube(14L));
    newEnrichedSample.setSample(new SubmissionSample(562L));
    enrichment.getTreatmentSamples().add(newEnrichedSample);

    Optional<Activity> optionalActivity =
        enrichmentActivityService.update(enrichment, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity protocolActivity = new UpdateActivity();
    protocolActivity.setActionType(ActionType.UPDATE);
    protocolActivity.setTableName("treatment");
    protocolActivity.setRecordId(enrichment.getId());
    protocolActivity.setColumn("protocol");
    protocolActivity.setOldValue("2");
    protocolActivity.setNewValue("4");
    expecteds.add(protocolActivity);
    UpdateActivity newEnrichedSampleActivity = new UpdateActivity();
    newEnrichedSampleActivity.setActionType(ActionType.INSERT);
    newEnrichedSampleActivity.setTableName("treatmentsample");
    newEnrichedSampleActivity.setRecordId(400L);
    expecteds.add(newEnrichedSampleActivity);
    UpdateActivity updateEnrichedSampleSampleActivity = new UpdateActivity();
    updateEnrichedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateEnrichedSampleSampleActivity.setTableName("treatmentsample");
    updateEnrichedSampleSampleActivity.setRecordId(272L);
    updateEnrichedSampleSampleActivity.setColumn("sampleId");
    updateEnrichedSampleSampleActivity.setOldValue("579");
    updateEnrichedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateEnrichedSampleSampleActivity);
    UpdateActivity updateEnrichedSampleContainerActivity = new UpdateActivity();
    updateEnrichedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateEnrichedSampleContainerActivity.setTableName("treatmentsample");
    updateEnrichedSampleContainerActivity.setRecordId(272L);
    updateEnrichedSampleContainerActivity.setColumn("containerId");
    updateEnrichedSampleContainerActivity.setOldValue("800");
    updateEnrichedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateEnrichedSampleContainerActivity);
    UpdateActivity updateEnrichedSampleCommentActivity = new UpdateActivity();
    updateEnrichedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateEnrichedSampleCommentActivity.setTableName("treatmentsample");
    updateEnrichedSampleCommentActivity.setRecordId(272L);
    updateEnrichedSampleCommentActivity.setColumn("comment");
    updateEnrichedSampleCommentActivity.setOldValue(null);
    updateEnrichedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateEnrichedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Enrichment enrichment = entityManager.find(Enrichment.class, 7L);
    entityManager.detach(enrichment);

    Optional<Activity> optionalActivity =
        enrichmentActivityService.update(enrichment, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    Enrichment enrichment = new Enrichment(7L);

    Activity activity = enrichmentActivityService.undoErroneous(enrichment, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Enrichment enrichment = new Enrichment(7L);

    Activity activity = enrichmentActivityService.undoFailed(enrichment, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Enrichment enrichment = new Enrichment(7L);
    Tube sourceTube = new Tube(4L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity =
        enrichmentActivityService.undoFailed(enrichment, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(enrichment.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity bannedTubeActivity = new UpdateActivity();
    bannedTubeActivity.setActionType(ActionType.UPDATE);
    bannedTubeActivity.setTableName("samplecontainer");
    bannedTubeActivity.setRecordId(sourceTube.getId());
    bannedTubeActivity.setColumn("banned");
    bannedTubeActivity.setOldValue("0");
    bannedTubeActivity.setNewValue("1");
    expecteds.add(bannedTubeActivity);
    UpdateActivity bannedWellActivity = new UpdateActivity();
    bannedWellActivity.setActionType(ActionType.UPDATE);
    bannedWellActivity.setTableName("samplecontainer");
    bannedWellActivity.setRecordId(well.getId());
    bannedWellActivity.setColumn("banned");
    bannedWellActivity.setOldValue("0");
    bannedWellActivity.setNewValue("1");
    expecteds.add(bannedWellActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void undoFailed_LongDescription() throws Throwable {
    Enrichment enrichment = new Enrichment(7L);
    Tube sourceTube = new Tube(4L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    String reason = "long reason having more than 255 characters "
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        + "AAAAAAAAAAAAAAAAAAAAAAAAAAA";

    Activity activity = enrichmentActivityService.undoFailed(enrichment, reason, bannedContainers);

    StringBuilder builder = new StringBuilder(reason);
    while (builder.toString().getBytes("UTF-8").length > 255) {
      builder.deleteCharAt(builder.length() - 1);
    }
    String reasonCutAt255Bytes = builder.toString();
    assertEquals(255, activity.getExplanation().length());
    assertEquals(reasonCutAt255Bytes, activity.getExplanation());
  }
}
