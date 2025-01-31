package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.INVALID_EMAIL;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.UserProperties.EMAIL;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.HEADER;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.ID;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.MESSAGE;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SAVED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.ForgotPasswordWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link ForgotPasswordView}.
 */
@ServiceTestAnnotations
@WithAnonymousUser
public class ForgotPasswordViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(ForgotPasswordView.class);
  private static final String USER_PREFIX = messagePrefix(User.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private ForgotPasswordView view;
  @MockitoBean
  private ForgotPasswordService service;
  @MockitoBean
  private UserService userService;
  @Captor
  private ArgumentCaptor<ForgotPasswordWebContext> webContextCaptor;
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(ForgotPasswordView.class);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(MESSAGE, view.message.getId().orElse(""));
    assertEquals(EMAIL, view.email.getId().orElse(""));
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), view.email.getLabel());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(USER_PREFIX + EMAIL), view.email.getLabel());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmailEmtpy() {
    view.email.setValue("");

    test(view.save).click();

    BinderValidationStatus<User> status = view.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(view.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
    verify(service, never()).insert(any(), any());
    assertTrue($(ForgotPasswordView.class).exists());
    assertFalse($(Notification.class).exists());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_EmailInvalid() {
    view.email.setValue("test");

    test(view.save).click();

    BinderValidationStatus<User> status = view.validateUser();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.email);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(view.getTranslation(CONSTANTS_PREFIX + INVALID_EMAIL)),
        error.getMessage());
    verify(service, never()).insert(any(), any());
    assertTrue($(ForgotPasswordView.class).exists());
    assertFalse($(Notification.class).exists());
  }

  @Test
  public void save_EmailNotExists() {
    String email = "test@ircm.qc.ca";
    test(view.email).setValue(email);

    test(view.save).click();

    verify(userService).exists(email);
    verify(service, never()).insert(any(), any());
    assertTrue($(SigninView.class).exists());
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, email), test(notification).getText());
  }

  @Test
  public void save() {
    when(userService.exists(any())).thenReturn(true);
    String email = "test@ircm.qc.ca";
    test(view.email).setValue(email);

    test(view.save).click();

    verify(userService).exists(email);
    verify(service).insert(eq(email), webContextCaptor.capture());
    ForgotPasswordWebContext webContext = webContextCaptor.getValue();
    ForgotPassword forgotPassword = new ForgotPassword();
    forgotPassword.setId(34925L);
    forgotPassword.setConfirmNumber("feafet23ts");
    String url = webContext.getChangeForgottenPasswordUrl(forgotPassword, locale);
    assertEquals("/" + UseForgotPasswordView.VIEW_NAME + "/" + forgotPassword.getId()
        + UseForgotPasswordView.SEPARATOR + forgotPassword.getConfirmNumber(), url);
    assertTrue($(SigninView.class).exists());
    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED, email), test(notification).getText());
  }
}
