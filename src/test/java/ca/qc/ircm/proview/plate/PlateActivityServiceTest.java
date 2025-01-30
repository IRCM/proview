package ca.qc.ircm.proview.plate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link PlateActivityService}.
 */
@ServiceTestAnnotations
public class PlateActivityServiceTest extends AbstractServiceTestCase {

  @Autowired
  private PlateActivityService plateActivityService;
  @Autowired
  private PlateRepository repository;
  @MockitoBean
  private AuthenticatedUser authenticatedUser;
  private User user;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    user = new User(4L);
    when(authenticatedUser.getUser()).thenReturn(Optional.of(user));
  }

  @Test
  public void insert() {
    Plate plate = new Plate();
    plate.setId(123456L);
    plate.setName("unit_test_plate_123456");

    Activity activity = plateActivityService.insert(plate);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertNull(activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Plate plate = repository.findById(26L).orElseThrow();
    detach(plate);
    plate.setName("unit_test");
    plate.setColumnCount(13);
    plate.setRowCount(9);
    plate.setInsertTime(LocalDateTime.now());
    plate.setSubmission(new Submission(123L));

    Optional<Activity> optionalActivity = plateActivityService.update(plate);

    assertTrue(optionalActivity.isPresent());
    final DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Plate.TABLE_NAME, activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertNull(activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity nameUpdate = new UpdateActivity();
    nameUpdate.setTableName(Plate.TABLE_NAME);
    nameUpdate.setRecordId(plate.getId());
    nameUpdate.setActionType(ActionType.UPDATE);
    nameUpdate.setColumn("name");
    nameUpdate.setOldValue("A_20111108");
    nameUpdate.setNewValue("unit_test");
    expectedUpdateActivities.add(nameUpdate);
    UpdateActivity columnCountUpdate = new UpdateActivity();
    columnCountUpdate.setTableName(Plate.TABLE_NAME);
    columnCountUpdate.setRecordId(plate.getId());
    columnCountUpdate.setActionType(ActionType.UPDATE);
    columnCountUpdate.setColumn("columnCount");
    columnCountUpdate.setOldValue("12");
    columnCountUpdate.setNewValue("13");
    expectedUpdateActivities.add(columnCountUpdate);
    UpdateActivity rowCountUpdate = new UpdateActivity();
    rowCountUpdate.setTableName(Plate.TABLE_NAME);
    rowCountUpdate.setRecordId(plate.getId());
    rowCountUpdate.setActionType(ActionType.UPDATE);
    rowCountUpdate.setColumn("rowCount");
    rowCountUpdate.setOldValue("8");
    rowCountUpdate.setNewValue("9");
    expectedUpdateActivities.add(rowCountUpdate);
    UpdateActivity insertTimeUpdate = new UpdateActivity();
    insertTimeUpdate.setTableName(Plate.TABLE_NAME);
    insertTimeUpdate.setRecordId(plate.getId());
    insertTimeUpdate.setActionType(ActionType.UPDATE);
    insertTimeUpdate.setColumn("insertTime");
    insertTimeUpdate.setOldValue("2011-11-08T13:33:21");
    insertTimeUpdate.setNewValue(instantFormatter.format(plate.getInsertTime()));
    expectedUpdateActivities.add(insertTimeUpdate);
    UpdateActivity submissionUpdate = new UpdateActivity();
    submissionUpdate.setTableName(Plate.TABLE_NAME);
    submissionUpdate.setRecordId(plate.getId());
    submissionUpdate.setActionType(ActionType.UPDATE);
    submissionUpdate.setColumn("submission");
    submissionUpdate.setOldValue(null);
    submissionUpdate.setNewValue("123");
    expectedUpdateActivities.add(submissionUpdate);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChanges() {
    Plate plate = repository.findById(26L).orElseThrow();
    detach(plate);

    Optional<Activity> optionalActivity = plateActivityService.update(plate);

    assertFalse(optionalActivity.isPresent());
  }

  @Test
  public void ban() {
    Plate plate = new Plate(26L);
    List<Well> bans = new ArrayList<>();
    Well well = new Well(130L);
    well.setPlate(plate);
    bans.add(well);
    well = new Well(131L);
    well.setPlate(plate);
    bans.add(well);

    Activity activity = plateActivityService.ban(bans, "unit_test");

    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    for (Well banned : bans) {
      UpdateActivity banActivity = new UpdateActivity();
      banActivity.setActionType(ActionType.UPDATE);
      banActivity.setTableName("samplecontainer");
      banActivity.setRecordId(banned.getId());
      banActivity.setColumn("banned");
      banActivity.setOldValue("0");
      banActivity.setNewValue("1");
      expectedUpdateActivities.add(banActivity);
    }
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void ban_MultiplePlates() {
    final Plate plate1 = new Plate(26L);
    final Plate plate2 = new Plate(107L);
    List<Well> bans = new ArrayList<>();
    Well well = new Well(130L);
    well.setPlate(plate1);
    bans.add(well);
    well = new Well(131L);
    well.setPlate(plate1);
    bans.add(well);
    well = new Well(231L);
    well.setPlate(plate2);
    bans.add(well);
    well = new Well(232L);
    well.setPlate(plate2);
    bans.add(well);

    assertThrows(IllegalArgumentException.class, () -> plateActivityService.ban(bans, "unit_test"));
  }

  @Test
  public void activate() {
    Plate plate = new Plate(26L);
    List<Well> wells = new ArrayList<>();
    Well well = new Well(199L);
    well.setPlate(plate);
    well.setBanned(true);
    wells.add(well);
    well = new Well(211L);
    well.setPlate(plate);
    well.setBanned(true);
    wells.add(well);

    Activity activity = plateActivityService.activate(wells, "unit_test");

    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    for (Well activated : wells) {
      UpdateActivity activateActivity = new UpdateActivity();
      activateActivity.setActionType(ActionType.UPDATE);
      activateActivity.setTableName("samplecontainer");
      activateActivity.setRecordId(activated.getId());
      activateActivity.setColumn("banned");
      activateActivity.setOldValue("1");
      activateActivity.setNewValue("0");
      expectedUpdateActivities.add(activateActivity);
    }
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void activate_MultiplePlates() {
    final Plate plate1 = new Plate(26L);
    final Plate plate2 = new Plate(107L);
    List<Well> wells = new ArrayList<>();
    Well well = new Well(199L);
    well.setPlate(plate1);
    well.setBanned(true);
    wells.add(well);
    well = new Well(211L);
    well.setPlate(plate1);
    well.setBanned(true);
    wells.add(well);
    well = new Well(307L);
    well.setPlate(plate2);
    well.setBanned(true);
    wells.add(well);
    well = new Well(319L);
    well.setPlate(plate2);
    well.setBanned(true);
    wells.add(well);

    assertThrows(IllegalArgumentException.class,
        () -> plateActivityService.activate(wells, "unit_test"));
  }
}
