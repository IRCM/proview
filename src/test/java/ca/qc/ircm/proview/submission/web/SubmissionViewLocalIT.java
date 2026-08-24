package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.AbstractSeleniumTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionViewLocalIT extends AbstractSeleniumTestCase {

  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionViewLocalIT.class);

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void downloadFile() throws Throwable {
    Files.createDirectories(downloadHome);
    Path downloaded = downloadHome.resolve("protocol.txt");
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(
        Objects.requireNonNull(getClass().getResource("/submissionfile1.txt")).toURI());
    openView(VIEW_NAME, "1");
    SubmissionViewComponent view = waitUntil(SubmissionViewComponent.find());
    WebElement filename = view.files().filename(0);
    scrollIntoView(filename);
    filename.click();

    // Wait for file to download.
    Thread.sleep(2000);
    assertTrue(Files.exists(downloaded));
    try {
      assertArrayEquals(Files.readAllBytes(source), Files.readAllBytes(downloaded));
    } finally {
      Files.delete(downloaded);
    }
  }
}
