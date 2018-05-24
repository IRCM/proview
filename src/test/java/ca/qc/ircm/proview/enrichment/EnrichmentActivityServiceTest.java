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
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    final Protocol protocol = new Protocol(2L);
    SubmissionSample sample = new SubmissionSample(1L);
    sample.setStatus(SampleStatus.ENRICHED);
    Tube sourceTube = new Tube(1L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setSample(sample);
    treatedSample.setContainer(sourceTube);
    List<TreatedSample> treatedSamples = new ArrayList<>();
    treatedSamples.add(treatedSample);
    Enrichment enrichment = new Enrichment();
    enrichment.setId(123456L);
    enrichment.setProtocol(protocol);
    enrichment.setTreatedSamples(treatedSamples);

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
    enrichment.getTreatedSamples().forEach(ts -> entityManager.detach(ts));
    enrichment.setProtocol(entityManager.find(Protocol.class, 4L));
    enrichment.getTreatedSamples().get(0).setContainer(new Well(248L));
    enrichment.getTreatedSamples().get(0).setSample(new Control(444L));
    enrichment.getTreatedSamples().get(0).setComment("test");
    TreatedSample newTreatedSample = new TreatedSample();
    newTreatedSample.setId(400L);
    newTreatedSample.setContainer(new Tube(14L));
    newTreatedSample.setSample(new SubmissionSample(562L));
    enrichment.getTreatedSamples().add(newTreatedSample);

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
    UpdateActivity newTreatedSampleActivity = new UpdateActivity();
    newTreatedSampleActivity.setActionType(ActionType.INSERT);
    newTreatedSampleActivity.setTableName("treatedsample");
    newTreatedSampleActivity.setRecordId(400L);
    expecteds.add(newTreatedSampleActivity);
    UpdateActivity updateTreatedSampleSampleActivity = new UpdateActivity();
    updateTreatedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSampleActivity.setTableName("treatedsample");
    updateTreatedSampleSampleActivity.setRecordId(272L);
    updateTreatedSampleSampleActivity.setColumn("sampleId");
    updateTreatedSampleSampleActivity.setOldValue("579");
    updateTreatedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateTreatedSampleSampleActivity);
    UpdateActivity updateTreatedSampleContainerActivity = new UpdateActivity();
    updateTreatedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleContainerActivity.setTableName("treatedsample");
    updateTreatedSampleContainerActivity.setRecordId(272L);
    updateTreatedSampleContainerActivity.setColumn("containerId");
    updateTreatedSampleContainerActivity.setOldValue("800");
    updateTreatedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateTreatedSampleContainerActivity);
    UpdateActivity updateTreatedSampleCommentActivity = new UpdateActivity();
    updateTreatedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleCommentActivity.setTableName("treatedsample");
    updateTreatedSampleCommentActivity.setRecordId(272L);
    updateTreatedSampleCommentActivity.setColumn("comment");
    updateTreatedSampleCommentActivity.setOldValue(null);
    updateTreatedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateTreatedSampleCommentActivity);
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
}
