package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.files.web.GuidelinesView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.files.Guideline;
import ca.qc.ircm.proview.files.GuidelinesConfiguration;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.Download;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.html.testbench.AnchorElement;
import com.vaadin.testbench.BrowserTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link GuidelinesView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class GuidelinesViewIT extends AbstractBrowserTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(GuidelinesView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Value("${download-home}")
  protected Path downloadHome;
  @Autowired
  private GuidelinesConfiguration guidelinesConfiguration;
  @Value("${spring.application.name}")
  private String applicationName;
  @Autowired
  private MessageSource messageSource;

  private void open() {
    openView(VIEW_NAME);
  }

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName = messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null,
        locale);
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @BrowserTest
  public void fieldsExistence() {
    open();

    GuidelinesViewElement view = $(GuidelinesViewElement.class).waitForFirst();
    List<Category> categories = guidelinesConfiguration.categories(currentLocale());
    Assertions.assertEquals(categories.size(), view.categories().size());
    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      CategoryComponentElement categoryElement = view.categories().get(i);
      assertTrue(optional(categoryElement::header).isPresent());
      Assertions.assertEquals(category.getGuidelines().size(), categoryElement.guidelines().size());
    }
  }

  @BrowserTest
  @Download
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
