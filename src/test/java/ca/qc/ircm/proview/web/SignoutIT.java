package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import org.junit.jupiter.api.Disabled;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SignoutView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SignoutIT extends AbstractBrowserTestCase {

  @BrowserTest
  public void signout() {
    openView(SubmissionsView.VIEW_NAME);
    $(ViewLayoutElement.class).waitForFirst();
    openView(SignoutView.VIEW_NAME);
    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithAnonymousUser
  public void signout_clear_rememberme() {
    openView(SigninView.VIEW_NAME);
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    $(SubmissionsViewElement.class).waitForFirst();
    assertNotNull(getDriver().manage().getCookieNamed("remember-me"));
    openView(SignoutView.VIEW_NAME);
    $(SigninViewElement.class).waitForFirst();
    assertNull(getDriver().manage().getCookieNamed("remember-me"));
  }

  @BrowserTest
  public void signout_sidenav() {
    openView(SubmissionsView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.signout().click();
    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithAnonymousUser
  @Disabled("ViewLayoutElement is not responsive in this test for some reason")
  public void signout_sidenav_clear_rememberme() {
    // TODO Fix this test.
    openView(SigninView.VIEW_NAME);
    SigninViewElement view = $(SigninViewElement.class).waitForFirst();
    view.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    view.getPasswordField().setValue("password");
    view.getSubmitButton().click();
    $(SubmissionsViewElement.class).waitForFirst();
    assertNotNull(getDriver().manage().getCookieNamed("remember-me"));
    ViewLayoutElement layout = $(ViewLayoutElement.class).waitForFirst();
    layout.signout().click();
    $(SigninViewElement.class).waitForFirst();
    assertNull(getDriver().manage().getCookieNamed("remember-me"));
  }
}
