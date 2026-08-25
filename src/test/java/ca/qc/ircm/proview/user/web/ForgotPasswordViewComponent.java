package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.ForgotPasswordView.ID;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link ForgotPasswordView}.
 */
public class ForgotPasswordViewComponent extends SeleniumComponent {

  public ForgotPasswordViewComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, ForgotPasswordViewComponent> find() {
    return d -> new ForgotPasswordViewComponent(d.findElement(By.id(ID)));
  }
}
