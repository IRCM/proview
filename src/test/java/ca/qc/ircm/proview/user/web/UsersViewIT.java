package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UsersView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewIT extends SpringUIUnitTest {

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    navigate(VIEW_NAME, AccessDeniedView.class);
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    navigate(UsersView.class);
  }

  @Test
  public void security_Admin() {
    navigate(UsersView.class);
  }

  @Test
  public void edit() {
    UsersView view = navigate(UsersView.class);
    test(view.users).select(0);

    test(view.edit).click();

    UserDialog dialog = $(UserDialog.class).single();
    assertEquals(1, dialog.getUserId());
  }

  @Test
  public void add() {
    UsersView view = navigate(UsersView.class);

    test(view.add).click();

    $(UserDialog.class).single();
  }

  @Test
  public void view_Laboratory() {
    UsersView view = navigate(UsersView.class);
    test(view.users).select(0);

    test(view.viewLaboratory).click();

    $(LaboratoryDialog.class).single();
  }
}
