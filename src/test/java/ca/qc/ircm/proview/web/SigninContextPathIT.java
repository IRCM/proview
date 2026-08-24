package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.SigninView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.qc.ircm.proview.submission.web.SubmissionsViewComponent;
import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link SigninView} using Selenium and a non-empty context path.
 */
@TestBenchTestAnnotations
@ActiveProfiles({"integration-test", "context-path"})
@WithAnonymousUser
public class SigninContextPathIT extends AbstractSeleniumTestCase {

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void sign() {
    open();
    SigninViewComponent view = waitUntil(SigninViewComponent.find());
    view.username().sendKeys("christopher.anderson@ircm.qc.ca");
    view.password().sendKeys("password");
    view.signin().click();
    waitUntil(SubmissionsViewComponent.find());
    Cookie rememberMeCookie = driver.manage().getCookieNamed("remember-me");
    assertNotNull(rememberMeCookie);
    assertEquals(contextPath, rememberMeCookie.getPath());
    assertNotEquals("pass1", rememberMeCookie.getValue());
  }
}
