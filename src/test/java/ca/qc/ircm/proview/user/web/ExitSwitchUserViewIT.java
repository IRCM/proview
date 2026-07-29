package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.ExitSwitchUserView.VIEW_NAME;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ExitSwitchUserView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class ExitSwitchUserViewIT extends SpringUIUnitTest {

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
    navigate(VIEW_NAME, AccessDeniedView.class);
  }

  @Test
  public void security_Admin() {
    navigate(VIEW_NAME, AccessDeniedView.class);
  }
}
