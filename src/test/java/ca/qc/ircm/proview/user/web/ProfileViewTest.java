package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.web.ProfileView.HEADER;
import static ca.qc.ircm.proview.user.web.ProfileView.ID;
import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.DefaultAddressConfiguration;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link ProfileView}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ProfileViewTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(ProfileView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private ProfileView view;
  @MockBean
  private UserService service;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  @Mock
  private User user;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(ProfileView.class);
  }

  @Test
  public void formUser() {
    assertEquals(10L, view.form.getUser().getId());
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void save() {
    view.form = mock(UserForm.class);
    String password = "test_password";
    when(view.form.isValid()).thenReturn(true);
    when(view.form.getPassword()).thenReturn(password);
    when(view.form.getUser()).thenReturn(user);

    test(view.save).click();

    verify(view.form).isValid();
    verify(service).save(eq(user), eq(password));
    assertTrue($(ProfileView.class).exists());
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED), test(notification).getText());
  }

  @Test
  public void save_Invalid() {
    view.form = mock(UserForm.class);

    test(view.save).click();

    verify(view.form).isValid();
    verify(service, never()).save(any(), any());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }
}
