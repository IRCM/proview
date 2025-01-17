package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainView.VIEW_NAME;

import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link MainView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class MainViewItTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void userRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void managerRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void adminRedirected() {
    open();

    $(SubmissionsViewElement.class).waitForFirst();
  }
}
