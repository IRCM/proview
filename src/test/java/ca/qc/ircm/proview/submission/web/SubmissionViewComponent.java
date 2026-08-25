package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;
import static ca.qc.ircm.proview.submission.web.SubmissionView.ID;

import ca.qc.ircm.proview.test.config.SeleniumComponent;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link SubmissionView}.
 */
public class SubmissionViewComponent extends SeleniumComponent {

  public SubmissionViewComponent(WebElement element) {
    super(element);
    assert ID.equals(element.getAttribute("id"));
  }

  public static Function<WebDriver, SubmissionViewComponent> find() {
    return d -> new SubmissionViewComponent(d.findElement(By.id(ID)));
  }

  public SubmissionFilesGridComponent files() {
    return new SubmissionFilesGridComponent(element.findElement(By.id(FILES)));
  }
}
