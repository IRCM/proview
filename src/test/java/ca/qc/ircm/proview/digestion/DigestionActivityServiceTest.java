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

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.QSubmissionSample;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.ProtocolRepository;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class DigestionActivityServiceTest extends AbstractServiceTestCase {
  private static final QSubmission qsubmission = QSubmission.submission;
  private static final QSubmissionSample qsubmissionSample = QSubmissionSample.submissionSample;
  @Inject
  private DigestionActivityService digestionActivityService;
  @Inject
  private DigestionRepository repository;
  @Inject
  private ProtocolRepository protocolRepository;
  @MockBean
  private AuthorizationService authorizationService;
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
  public void insert() {
    final Protocol protocol = new Protocol(1L);
    SubmissionSample sample = new SubmissionSample(1L);
    sample.setStatus(SampleStatus.DIGESTED);
    Submission submission = new Submission(1L);
    LocalDate date = LocalDate.now();
    submission.setDigestionDate(date);
    sample.setSubmission(submission);
    Tube sourceTube = new Tube(352L);
    TreatedSample treatedSample = new TreatedSample();
    treatedSample.setSample(sample);
    treatedSample.setContainer(sourceTube);
    List<TreatedSample> treatedSamples = new ArrayList<>();
    treatedSamples.add(treatedSample);
    Digestion digestion = new Digestion();
    digestion.setId(123456L);
    digestion.setProtocol(protocol);
    digestion.setTreatedSamples(treatedSamples);

    Activity activity = digestionActivityService.insert(digestion);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(Treatment.TABLE_NAME, activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity sampleStatusActivity = new UpdateActivity();
    sampleStatusActivity.setActionType(ActionType.UPDATE);
    sampleStatusActivity.setTableName(Sample.TABLE_NAME);
    sampleStatusActivity.setRecordId(sample.getId());
    sampleStatusActivity.setColumn(qname(qsubmissionSample.status));
    sampleStatusActivity.setOldValue(SampleStatus.ANALYSED.name());
    sampleStatusActivity.setNewValue(SampleStatus.DIGESTED.name());
    expectedUpdateActivities.add(sampleStatusActivity);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    UpdateActivity submissionDigestionDateActivity = new UpdateActivity();
    submissionDigestionDateActivity.setActionType(ActionType.UPDATE);
    submissionDigestionDateActivity.setTableName(Submission.TABLE_NAME);
    submissionDigestionDateActivity.setRecordId(submission.getId());
    submissionDigestionDateActivity.setColumn(qname(qsubmission.digestionDate));
    submissionDigestionDateActivity.setOldValue("2010-12-11");
    submissionDigestionDateActivity.setNewValue(dateFormatter.format(date));
    expectedUpdateActivities.add(submissionDigestionDateActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update() {
    Digestion digestion = repository.findOne(195L);
    detach(digestion);
    digestion.getTreatedSamples().forEach(ts -> detach(ts));
    digestion.setProtocol(protocolRepository.findOne(3L));
    digestion.getTreatedSamples().get(0).setContainer(new Well(248L));
    digestion.getTreatedSamples().get(0).setSample(new Control(444L));
    digestion.getTreatedSamples().get(0).setComment("test");
    TreatedSample newTreatedSample = new TreatedSample();
    newTreatedSample.setId(400L);
    newTreatedSample.setContainer(new Tube(14L));
    newTreatedSample.setSample(new SubmissionSample(562L));
    digestion.getTreatedSamples().add(newTreatedSample);

    Optional<Activity> optionalActivity =
        digestionActivityService.update(digestion, "test explanation");

    assertTrue(optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("test explanation", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expecteds = new HashSet<>();
    UpdateActivity protocolActivity = new UpdateActivity();
    protocolActivity.setActionType(ActionType.UPDATE);
    protocolActivity.setTableName("treatment");
    protocolActivity.setRecordId(digestion.getId());
    protocolActivity.setColumn("protocol");
    protocolActivity.setOldValue("1");
    protocolActivity.setNewValue("3");
    expecteds.add(protocolActivity);
    UpdateActivity newTreatedSampleActivity = new UpdateActivity();
    newTreatedSampleActivity.setActionType(ActionType.INSERT);
    newTreatedSampleActivity.setTableName("treatedsample");
    newTreatedSampleActivity.setRecordId(400L);
    expecteds.add(newTreatedSampleActivity);
    UpdateActivity updateTreatedSampleSampleActivity = new UpdateActivity();
    updateTreatedSampleSampleActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleSampleActivity.setTableName("treatedsample");
    updateTreatedSampleSampleActivity.setRecordId(196L);
    updateTreatedSampleSampleActivity.setColumn("sampleId");
    updateTreatedSampleSampleActivity.setOldValue("559");
    updateTreatedSampleSampleActivity.setNewValue("444");
    expecteds.add(updateTreatedSampleSampleActivity);
    UpdateActivity updateTreatedSampleContainerActivity = new UpdateActivity();
    updateTreatedSampleContainerActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleContainerActivity.setTableName("treatedsample");
    updateTreatedSampleContainerActivity.setRecordId(196L);
    updateTreatedSampleContainerActivity.setColumn("containerId");
    updateTreatedSampleContainerActivity.setOldValue("224");
    updateTreatedSampleContainerActivity.setNewValue("248");
    expecteds.add(updateTreatedSampleContainerActivity);
    UpdateActivity updateTreatedSampleCommentActivity = new UpdateActivity();
    updateTreatedSampleCommentActivity.setActionType(ActionType.UPDATE);
    updateTreatedSampleCommentActivity.setTableName("treatedsample");
    updateTreatedSampleCommentActivity.setRecordId(196L);
    updateTreatedSampleCommentActivity.setColumn("comment");
    updateTreatedSampleCommentActivity.setOldValue(null);
    updateTreatedSampleCommentActivity.setNewValue("test");
    expecteds.add(updateTreatedSampleCommentActivity);
    LogTestUtils.validateUpdateActivities(expecteds, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Digestion digestion = repository.findOne(6L);
    detach(digestion);

    Optional<Activity> optionalActivity =
        digestionActivityService.update(digestion, "test explanation");

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void undoErroneous() {
    Digestion digestion = new Digestion(6L);

    Activity activity = digestionActivityService.undoErroneous(digestion, "unit_test");

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_NoBan() {
    Digestion digestion = new Digestion(6L);

    Activity activity = digestionActivityService.undoFailed(digestion, "unit_test", null);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void undoFailed_Ban() {
    Digestion digestion = new Digestion(6L);
    Tube sourceTube = new Tube(4L);
    Well well = new Well(130L);
    Collection<SampleContainer> bannedContainers = new ArrayList<>();
    bannedContainers.add(sourceTube);
    bannedContainers.add(well);

    Activity activity =
        digestionActivityService.undoFailed(digestion, "unit_test", bannedContainers);

    assertEquals(ActionType.DELETE, activity.getActionType());
    assertEquals("treatment", activity.getTableName());
    assertEquals(digestion.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
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
