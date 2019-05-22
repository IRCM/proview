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

package ca.qc.ircm.proview.security.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import com.vaadin.ui.UI;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SpringViewAccessControlTest {
  private static final String USER = ca.qc.ircm.proview.user.UserRole.USER;
  @Inject
  private SpringViewAccessControl accessControl;
  @MockBean
  private AuthorizationService authorizationService;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private UI ui;
  private User user = new User(1L, "proview@ircm.qc.ca");

  @Before
  public void beforeTest() {
    accessControl.setApplicationContext(applicationContext);
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void isAccessGranted_NoRolesDefinedTrue() {
    String beanname = NoRoles.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> NoRoles.class);
    when(authorizationService.isAuthorized(any())).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(authorizationService).isAuthorized(NoRoles.class);
  }

  @Test
  public void isAccessGranted_NoRolesDefinedFalse() {
    String beanname = NoRoles.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> NoRoles.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(authorizationService).isAuthorized(NoRoles.class);
  }

  @Test
  public void isAccessGranted_User_True() {
    String beanname = UserRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserRole.class);
    when(authorizationService.isAuthorized(any())).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(authorizationService).isAuthorized(UserRole.class);
  }

  @Test
  public void isAccessGranted_User_False() {
    String beanname = UserRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(authorizationService).isAuthorized(UserRole.class);
  }

  public static class NoRoles {
  }

  @RolesAllowed(USER)
  public static class UserRole {
  }
}
