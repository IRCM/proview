package ca.qc.ircm.proview.security.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.HEADER;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.HOME;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.MESSAGE;
import static ca.qc.ircm.proview.security.web.AccessDeniedView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.web.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link AccessDeniedView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class AccessDeniedViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(AccessDeniedView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private AccessDeniedView view;
  private Locale locale = Locale.ENGLISH;

  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    assertThrows(IllegalArgumentException.class, () -> navigate(UsersView.class));
    view = $(AccessDeniedView.class).first();
  }

  @Test
  public void styles() {
    assertEquals(VIEW_NAME, view.getContent().getId().orElse(""));
    assertTrue(view.header.hasClassName(HEADER));
    assertTrue(view.message.hasClassName(MESSAGE));
    assertTrue(view.home.hasClassName(HOME));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HOME), view.home.getText());
  }

  @Test
  public void localeChange() {
    Locale locale = Locale.FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HOME), view.home.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void home() {
    test(view.home).click();
    assertTrue($(SubmissionsView.class).exists());
  }
}
