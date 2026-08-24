package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesView.ID;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import java.util.List;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link GuidelinesView}.
 */
public class GuidelinesViewComponent extends SeleniumComponent {

  public GuidelinesViewComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, GuidelinesViewComponent> find() {
    return d -> new GuidelinesViewComponent(d.findElement(By.id(ID)));
  }

  public List<WebElement> guidelines() {
    return element.findElements(By.cssSelector("a"));
  }
}
