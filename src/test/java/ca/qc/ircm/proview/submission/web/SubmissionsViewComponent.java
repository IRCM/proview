package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionsView.ID;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link SubmissionsView}.
 */
public class SubmissionsViewComponent extends SeleniumComponent {

  public SubmissionsViewComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, SubmissionsViewComponent> find() {
    return d -> new SubmissionsViewComponent(d.findElement(By.id(ID)));
  }
}
