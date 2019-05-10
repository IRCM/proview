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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.ui.UI;
import javax.annotation.security.RolesAllowed;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ShiroViewAccessControlTest {
  private static final String USER = ca.qc.ircm.proview.user.UserRole.USER;
  private static final String ADMIN = ca.qc.ircm.proview.user.UserRole.ADMIN;
  private ShiroViewAccessControl accessControl;
  @Mock
  private ApplicationContext applicationContext;
  @Mock
  private UI ui;

  @Before
  public void beforeTest() {
    accessControl = new ShiroViewAccessControl(applicationContext);
  }

  private Subject getSubject() {
    return SecurityUtils.getSubject();
  }

  @Test
  public void isAccessGranted_NoRolesDefined() {
    String beanname = NoRoles.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> NoRoles.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject(), never()).hasRole(any());
  }

  @Test
  public void isAccessGranted_User_True() {
    String beanname = UserRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserRole.class);
    when(getSubject().hasRole(USER)).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(USER);
  }

  @Test
  public void isAccessGranted_User_False() {
    String beanname = UserRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(USER);
  }

  @Test
  public void isAccessGranted_Admin_True() {
    String beanname = AdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> AdminRole.class);
    when(getSubject().hasRole(ADMIN)).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(ADMIN);
  }

  @Test
  public void isAccessGranted_Admin_False() {
    String beanname = AdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> AdminRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(ADMIN);
  }

  @Test
  public void isAccessGranted_UserOrAdmin_UserTrue() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);
    when(getSubject().hasRole(USER)).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(USER);
  }

  @Test
  public void isAccessGranted_UserOrAdmin_AdminTrue() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);
    when(getSubject().hasRole(ADMIN)).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(USER);
    verify(getSubject()).hasRole(ADMIN);
  }

  @Test
  public void isAccessGranted_UserOrAdmin_False() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole(USER);
    verify(getSubject()).hasRole(ADMIN);
  }

  public static class NoRoles {
  }

  @RolesAllowed(USER)
  public static class UserRole {
  }

  @RolesAllowed(ADMIN)
  public static class AdminRole {
  }

  @RolesAllowed({ USER, ADMIN })
  public static class UserOrAdminRole {
  }
}
