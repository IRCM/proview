package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link GuidelinesView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class GuidelinesViewLocalIT extends AbstractSeleniumTestCase {

  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  public void download() throws Throwable {
    open();
    Files.createDirectories(downloadHome);
    Guideline guideline = guidelinesConfiguration.categories(currentLocale()).getFirst()
        .getGuidelines().getFirst();
    Path downloaded = downloadHome.resolve(guideline.getPath().getFileName().toString());
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(
        Objects.requireNonNull(getClass().getResource("/structure1.png")).toURI());
    Files.createDirectories(guideline.getPath().getParent());
    Files.copy(source, guideline.getPath(), StandardCopyOption.REPLACE_EXISTING);

    open();

    GuidelinesViewComponent view = waitUntil(GuidelinesViewComponent.find());
    WebElement guidelineElement = view.guidelines().getFirst();
    guidelineElement.click();
    // Wait for file to download.
    Thread.sleep(2000);
    assertTrue(Files.exists(downloaded));
    try {
      assertArrayEquals(Files.readAllBytes(guideline.getPath()), Files.readAllBytes(downloaded));
    } finally {
      Files.delete(downloaded);
    }
  }
}
