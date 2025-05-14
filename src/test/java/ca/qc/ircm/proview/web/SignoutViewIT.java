package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SignoutView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SignoutViewIT extends AbstractBrowserTestCase {

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    openView(SignoutView.VIEW_NAME);

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void security_User() {
    openView(SignoutView.VIEW_NAME);

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void signout() {
    openView(SubmissionsView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    openView(SignoutView.VIEW_NAME);
    $(SigninViewElement.class).waitForFirst();
  }
}
