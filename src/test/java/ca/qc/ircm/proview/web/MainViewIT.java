package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainView.VIEW_NAME;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link MainView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class MainViewIT extends SpringBrowserlessTest {

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME, SigninView.class);
  }

  @Test
  public void userRedirected() {
    navigate(VIEW_NAME, SubmissionsView.class);
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void managerRedirected() {
    navigate(VIEW_NAME, SubmissionsView.class);
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void adminRedirected() {
    navigate(VIEW_NAME, SubmissionsView.class);
  }
}
