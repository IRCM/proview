package ca.qc.ircm.proview.security.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.ui.UI;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.security.RolesAllowed;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ShiroViewAccessControlTest {
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
    when(getSubject().hasRole("USER")).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("USER");
  }

  @Test
  public void isAccessGranted_User_False() {
    String beanname = UserRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("USER");
  }

  @Test
  public void isAccessGranted_Admin_True() {
    String beanname = AdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> AdminRole.class);
    when(getSubject().hasRole("ADMIN")).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("ADMIN");
  }

  @Test
  public void isAccessGranted_Admin_False() {
    String beanname = AdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> AdminRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("ADMIN");
  }

  @Test
  public void isAccessGranted_UserOrAdmin_UserTrue() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);
    when(getSubject().hasRole("USER")).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("USER");
  }

  @Test
  public void isAccessGranted_UserOrAdmin_AdminTrue() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);
    when(getSubject().hasRole("ADMIN")).thenReturn(true);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertTrue(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("USER");
    verify(getSubject()).hasRole("ADMIN");
  }

  @Test
  public void isAccessGranted_UserOrAdmin_False() {
    String beanname = UserOrAdminRole.class.getName();
    when(applicationContext.getType(any())).thenAnswer(i -> UserOrAdminRole.class);

    boolean granted = accessControl.isAccessGranted(ui, beanname);

    assertFalse(granted);
    verify(applicationContext).getType(beanname);
    verify(getSubject()).hasRole("USER");
    verify(getSubject()).hasRole("ADMIN");
  }

  public static class NoRoles {
  }

  @RolesAllowed("USER")
  public static class UserRole {
  }

  @RolesAllowed("ADMIN")
  public static class AdminRole {
  }

  @RolesAllowed({ "USER", "ADMIN" })
  public static class UserOrAdminRole {
  }
}
