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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
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
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class MascotFileActivityServiceTest {
  private MascotFileActivityService mascotFileActivityService;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    mascotFileActivityService =
        new MascotFileActivityService(entityManager, authorizationService);
  }

  @Test
  public void update() {
    AcquisitionMascotFile acquisitionMascotFile = new AcquisitionMascotFile(1L);
    acquisitionMascotFile.setVisible(false);
    acquisitionMascotFile.setComments("test_new_comments");
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        mascotFileActivityService.update(acquisitionMascotFile);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals((Long) 1L, activity.getUser().getId());
    assertEquals("acquisition_to_mascotfile", activity.getTableName());
    assertEquals((Long) 1L, activity.getRecordId());
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(null, activity.getJustification());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity visibleUpdateActivity = activity.getUpdates().get(0);
    visibleUpdateActivity.setTableName("acquisition_to_mascotfile");
    visibleUpdateActivity.setRecordId(456789456L);
    visibleUpdateActivity.setActionType(ActionType.UPDATE);
    visibleUpdateActivity.setColumn("visible");
    visibleUpdateActivity.setOldValue("1");
    visibleUpdateActivity.setNewValue("0");
    expectedUpdateActivities.add(visibleUpdateActivity);
    UpdateActivity commentsUpdateActivity = activity.getUpdates().get(0);
    commentsUpdateActivity.setTableName("acquisition_to_mascotfile");
    commentsUpdateActivity.setRecordId(1L);
    commentsUpdateActivity.setActionType(ActionType.UPDATE);
    commentsUpdateActivity.setColumn("comments");
    commentsUpdateActivity.setOldValue("complete report");
    commentsUpdateActivity.setNewValue("test_new_comments");
    expectedUpdateActivities.add(commentsUpdateActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    AcquisitionMascotFile acquisitionMascotFile =
        entityManager.find(AcquisitionMascotFile.class, 1L);
    User user = new User(1L);
    when(authorizationService.getCurrentUser()).thenReturn(user);

    Optional<Activity> optionalActivity =
        mascotFileActivityService.update(acquisitionMascotFile);

    verify(authorizationService, atLeastOnce()).getCurrentUser();
    assertEquals(false, optionalActivity.isPresent());
  }
}
