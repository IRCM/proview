package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.ViewLayout.CONTACT;
import static ca.qc.ircm.proview.web.ViewLayout.EXIT_SWITCH_USER;
import static ca.qc.ircm.proview.web.ViewLayout.GUIDELINES;
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.ViewLayout.NAV;
import static ca.qc.ircm.proview.web.ViewLayout.PROFILE;
import static ca.qc.ircm.proview.web.ViewLayout.SIGNOUT;
import static ca.qc.ircm.proview.web.ViewLayout.SUBMISSIONS;
import static ca.qc.ircm.proview.web.ViewLayout.USERS;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Web element for {@link ViewLayout}.
 */
public class ViewLayoutComponent extends SeleniumComponent {

  private static final Logger logger = LoggerFactory.getLogger(ViewLayoutComponent.class);

  public ViewLayoutComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, ViewLayoutComponent> find() {
    return d -> new ViewLayoutComponent(d.findElement(By.id(ID)));
  }

  @CheckReturnValue
  public Function<WebDriver, WebElement> openDrawer() {
    WebElement drawerToggle = element.findElement(By.cssSelector("vaadin-drawer-toggle"));
    if (!"true".equals(drawerToggle.getAttribute("aria-expanded"))) {
      drawerToggle.click();
    }
    return d -> {
      d.findElement(By.id(styleName(PROFILE, NAV)));
      return d.findElement(By.cssSelector("vaadin-drawer-toggle[aria-expanded='true']"));
    };
  }

  public WebElement submissions() {
    return element.findElement(By.id(styleName(SUBMISSIONS, NAV)));
  }

  public WebElement profile() {
    return element.findElement(By.id(styleName(PROFILE, NAV)));
  }

  public WebElement users() {
    return element.findElement(By.id(styleName(USERS, NAV)));
  }

  public WebElement exitSwitchUser() {
    return element.findElement(By.id(styleName(EXIT_SWITCH_USER, NAV)));
  }

  public WebElement signout() {
    return element.findElement(By.id(styleName(SIGNOUT, NAV)));
  }

  public WebElement contact() {
    return element.findElement(By.id(styleName(CONTACT, NAV)));
  }

  public WebElement guidelines() {
    return element.findElement(By.id(styleName(GUIDELINES, NAV)));
  }
}
