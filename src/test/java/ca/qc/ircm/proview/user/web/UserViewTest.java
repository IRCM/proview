package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.web.UserView.HEADER;
import static ca.qc.ircm.proview.user.web.UserView.ID;
import static ca.qc.ircm.proview.user.web.UserView.SAVED;
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
import ca.qc.ircm.proview.user.LaboratoryService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link UserView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UserViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(UserView.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private UserView view;
  @MockitoBean
  private UserService service;
  @MockitoBean
  private LaboratoryService laboratoryService;
  @Mock
  private BeforeEvent beforeEvent;
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(UserView.class);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    view.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void setParameter() {
    view.form = mock(UserForm.class);
    User user = mock(User.class);
    when(view.form.getUser()).thenReturn(user);
    when(user.getId()).thenReturn(12L);
    String name = "Christian Poitras";
    when(user.getName()).thenReturn(name);
    when(service.get(any(Long.class))).thenReturn(Optional.of(user));

    view.setParameter(beforeEvent, 12L);

    verify(view.form).setUser(user);
    verify(service).get(12L);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 1, name),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
  }

  @Test
  public void setParameter_Null() {
    view.form = mock(UserForm.class);
    when(view.form.getUser()).thenReturn(new User());

    view.setParameter(beforeEvent, null);

    verify(view.form, never()).setUser(any());
    verify(service, never()).get(any(Long.class));
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, 0),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
  }

  @Test
  public void save_Invalid() {
    view.form = mock(UserForm.class);

    test(view.save).click();

    verify(view.form).isValid();
    verify(service, never()).save(any(), any());
  }

  @Test
  public void save() {
    String password = "test_password";
    User user = mock(User.class);
    view.form = mock(UserForm.class);
    when(view.form.isValid()).thenReturn(true);
    when(view.form.getPassword()).thenReturn(password);
    when(view.form.getUser()).thenReturn(user);

    test(view.save).click();

    verify(view.form).isValid();
    verify(service).save(eq(user), eq(password));
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, user.getName()),
        test(notification).getText());
    assertTrue($(UsersView.class).exists());
  }
}
