package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.FILES;

import ca.qc.ircm.proview.test.config.GridComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Web element for {@link SubmissionsView}.
 */
public class SubmissionFilesGridComponent extends GridComponent {

  private static final int FILENAME_COLUMN = 0;

  public SubmissionFilesGridComponent(WebElement element) {
    super(element);
    assert FILES.equals(element.getAttribute("id"));
  }

  public WebElement filename(int row) {
    WebElement cell = cell(row, FILENAME_COLUMN);
    return cell.findElement(By.cssSelector("a"));
  }
}
