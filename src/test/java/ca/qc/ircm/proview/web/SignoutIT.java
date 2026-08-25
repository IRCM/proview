package ca.qc.ircm.proview.web;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewComponent;
import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.SeleniumTestAnnotations;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SignoutView}.
 */
@SeleniumTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SignoutIT extends AbstractSeleniumTestCase {

  @Test
  public void signout() {
    openView(SubmissionsView.VIEW_NAME);
    waitUntil(ViewLayoutComponent.find());
    openView(SignoutView.VIEW_NAME);
    waitUntil(SigninViewComponent.find());
  }

  @Test
  @WithAnonymousUser
  public void signout_clear_rememberme() {
    openView(SigninView.VIEW_NAME);
    SigninViewComponent view = waitUntil(SigninViewComponent.find());
    view.username().sendKeys("christopher.anderson@ircm.qc.ca");
    view.password().sendKeys("password");
    view.signin().click();
    waitUntil(SubmissionsViewComponent.find());
    assertNotNull(driver.manage().getCookieNamed("remember-me"));
    openView(SignoutView.VIEW_NAME);
    waitUntil(SigninViewComponent.find());
    assertNull(driver.manage().getCookieNamed("remember-me"));
  }

  @Test
  public void signout_sidenav() {
    openView(SubmissionsView.VIEW_NAME);
    ViewLayoutComponent view = waitUntil(ViewLayoutComponent.find());
    view = waitUntil(view.openDrawer());
    view.signout().click();
    waitUntil(SigninViewComponent.find());
  }

  @Test
  @WithAnonymousUser
  public void signout_sidenav_clear_rememberme() {
    openView(SigninView.VIEW_NAME);
    SigninViewComponent view = waitUntil(SigninViewComponent.find());
    view.username().sendKeys("christopher.anderson@ircm.qc.ca");
    view.password().sendKeys("password");
    view.signin().click();
    waitUntil(SubmissionsViewComponent.find());
    assertNotNull(driver.manage().getCookieNamed("remember-me"));
    ViewLayoutComponent layout = waitUntil(ViewLayoutComponent.find());
    layout = waitUntil(layout.openDrawer());
    layout.signout().click();
    waitUntil(SigninViewComponent.find());
    assertNull(driver.manage().getCookieNamed("remember-me"));
  }
}
