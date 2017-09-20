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

package ca.qc.ircm.proview.plate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class PlateActivityServiceTest {
  private PlateActivityService plateActivityService;
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
    plateActivityService = new PlateActivityService(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void insert() {
    Plate plate = new Plate();
    plate.setId(123456L);
    plate.setName("unit_test_plate_123456");
    plate.setType(PlateType.A);

    Activity activity = plateActivityService.insert(plate);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("plate", activity.getTableName());
    assertEquals(plate.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void ban() {
    Plate plate = new Plate(26L);
    List<Well> bans = new ArrayList<Well>();
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
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
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
    List<Well> bans = new ArrayList<Well>();
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

    try {
      plateActivityService.ban(bans, "unit_test");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }

  @Test
  public void activate() {
    Plate plate = new Plate(26L);
    List<Well> wells = new ArrayList<Well>();
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
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
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
    List<Well> wells = new ArrayList<Well>();
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

    try {
      plateActivityService.activate(wells, "unit_test");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Ignore.
    }
  }
}
