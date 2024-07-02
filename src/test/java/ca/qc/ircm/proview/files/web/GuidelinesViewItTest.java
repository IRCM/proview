package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.files.web.GuidelinesView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.Download;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link GuidelinesView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class GuidelinesViewItTest extends AbstractTestBenchTestCase {
  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;
  @Value("${spring.application.name}")
  private String applicationName;
  @Value("${download-home}")
  protected Path downloadHome;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(GuidelinesView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    GuidelinesViewElement view = $(GuidelinesViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    List<Category> categories = guidelinesConfiguration.categories(currentLocale());
    assertEquals(categories.size(), view.categories().size());
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponentElement categoryElement = view.categories().get(i);
      assertTrue(optional(() -> categoryElement.header()).isPresent());
      assertEquals(category.getGuidelines().size(), categoryElement.guidelines().size());
    }
  }

  @Test
  @Download
  public void download() throws Throwable {
    Files.createDirectories(downloadHome);
    Guideline guideline =
        guidelinesConfiguration.categories(currentLocale()).get(0).getGuidelines().get(0);
    Path downloaded = downloadHome.resolve(guideline.getPath().getFileName().toString());
    Files.deleteIfExists(downloaded);
    Path source = Paths.get(getClass().getResource("/structure1.png").toURI());
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
