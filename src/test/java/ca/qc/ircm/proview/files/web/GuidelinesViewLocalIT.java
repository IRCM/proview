package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.files.web.GuidelinesView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.AbstractLocalBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.testbench.BrowserTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link GuidelinesView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class GuidelinesViewLocalIT extends AbstractLocalBrowserTestCase {

  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;

  private void open() {
    openView(VIEW_NAME);
  }

  @BrowserTest
  public void download() throws Throwable {
    open();
    Files.createDirectories(downloadHome);
    Guideline guideline = guidelinesConfiguration.categories(currentLocale()).get(0).getGuidelines()
        .get(0);
    Path downloaded = downloadHome.resolve(guideline.getPath().getFileName().toString());
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(
        Objects.requireNonNull(getClass().getResource("/structure1.png")).toURI());
    Files.createDirectories(guideline.getPath().getParent());
    Files.copy(source, guideline.getPath(), StandardCopyOption.REPLACE_EXISTING);

    open();

    GuidelinesViewElement view = $(GuidelinesViewElement.class).waitForFirst();
    AnchorElement guidelineElement = view.categories().get(0).guidelines().get(0);
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
