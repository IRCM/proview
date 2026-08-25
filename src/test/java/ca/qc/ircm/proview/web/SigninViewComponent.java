package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.SigninView.ID;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link SigninView}.
 */
public class SigninViewComponent extends SeleniumComponent {

  public SigninViewComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, SigninViewComponent> find() {
    return d -> new SigninViewComponent(d.findElement(By.id(ID)));
  }

  public WebElement username() {
    return element.findElement(By.id("vaadinLoginUsername"));
  }

  public WebElement password() {
    return element.findElement(By.id("vaadinLoginPassword"));
  }

  public WebElement signin() {
    return element.findElement(By.cssSelector("vaadin-button[slot='submit']"));
  }

  public WebElement errorMessageDescription() {
    WebElement loginForm = element.getShadowRoot()
        .findElement(By.cssSelector("vaadin-login-form-wrapper"));
    return loginForm.getShadowRoot()
        .findElement(By.cssSelector("div[part='error-message-description']"));
  }

  public WebElement forgotPassword() {
    return element.findElement(By.cssSelector("vaadin-button[slot='forgot-password']"));
  }
}
