package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.user.web.Passwords.NOT_MATCH;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsProperties.PASSWORD;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.HEADER;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.ID;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.INVALID;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.MESSAGE;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.UseForgotPasswordView.SEPARATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link UseForgotPasswordView}.
 */
@ServiceTestAnnotations
@WithAnonymousUser
public class UseForgotPasswordViewTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(UseForgotPasswordView.class);
  private static final String PASSWORDS_PREFIX = messagePrefix(Passwords.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private UseForgotPasswordView view;
  @MockitoBean
  private ForgotPasswordService service;
  @Mock
  private BeforeEvent beforeEvent;
  @Mock
  private ForgotPassword forgotPassword;
  private final Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    view = navigate(UseForgotPasswordView.class);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(MESSAGE, view.message.getId().orElse(""));
    assertEquals(PASSWORD, view.password.getId().orElse(""));
    assertTrue(view.password.isRequiredIndicatorVisible());
    assertEquals(CONFIRM_PASSWORD, view.confirmPassword.getId().orElse(""));
    assertTrue(view.confirmPassword.isRequiredIndicatorVisible());
    assertEquals(SAVE, view.save.getId().orElse(""));
    assertTrue(view.save.hasThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName()));
  }

  @Test
  public void labels() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(PASSWORDS_PREFIX + PASSWORD), view.password.getLabel());
    assertEquals(view.getTranslation(PASSWORDS_PREFIX + CONFIRM_PASSWORD),
        view.confirmPassword.getLabel());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
    validateIcon(VaadinIcon.CHECK.create(), view.save.getIcon());
  }

  @Test
  public void localeChange() {
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER), view.header.getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + MESSAGE), view.message.getText());
    assertEquals(view.getTranslation(PASSWORDS_PREFIX + PASSWORD), view.password.getLabel());
    assertEquals(view.getTranslation(PASSWORDS_PREFIX + CONFIRM_PASSWORD),
        view.confirmPassword.getLabel());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + SAVE), view.save.getText());
  }

  @Test
  public void save_EmptyPassword() {
    when(service.get(anyLong(), any())).thenReturn(Optional.of(forgotPassword));
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view = navigate(UseForgotPasswordView.class, parameter);
    view.confirmPassword.setValue("new_password");

    test(view.save).click();

    verify(service, never()).updatePassword(any(), any());
    BinderValidationStatus<Passwords> status = view.validate();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.password);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(view.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void save_EmptyConfirmPassword() {
    when(service.get(anyLong(), any())).thenReturn(Optional.of(forgotPassword));
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view = navigate(UseForgotPasswordView.class, parameter);
    view.password.setValue("new_password");

    test(view.save).click();

    verify(service, never()).updatePassword(any(), any());
    BinderValidationStatus<Passwords> status = view.validate();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.confirmPassword);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(view.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void save_PasswordsNotMatch() {
    when(service.get(anyLong(), any())).thenReturn(Optional.of(forgotPassword));
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view = navigate(UseForgotPasswordView.class, parameter);
    view.password.setValue("new_password");
    view.confirmPassword.setValue("different_password");

    test(view.save).click();

    verify(service, never()).updatePassword(any(), any());
    BinderValidationStatus<Passwords> status = view.validate();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, view.password);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(view.getTranslation(PASSWORDS_PREFIX + NOT_MATCH)),
        error.getMessage());
  }

  @Test
  public void save() {
    when(service.get(anyLong(), any())).thenReturn(Optional.of(forgotPassword));
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view = navigate(UseForgotPasswordView.class, parameter);
    String password = "test_password";
    view.password.setValue(password);
    view.confirmPassword.setValue(password);

    test(view.save).click();

    verify(service).updatePassword(eq(forgotPassword), eq(password));
    assertTrue($(SigninView.class).exists());
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + SAVED), test(notification).getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void setParameter() {
    when(service.get(anyLong(), any())).thenReturn(Optional.of(forgotPassword));
    view.form = mock(PasswordsForm.class);
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view.setParameter(beforeEvent, parameter);
    verify(service, atLeastOnce()).get(id, confirmNumber);
    assertTrue(view.save.isEnabled());
    verify(view.form, never()).setEnabled(false);
  }

  @Test
  public void setParameter_IdNotNumber() {
    view.form = mock(PasswordsForm.class);
    String parameter = "A434GS" + SEPARATOR + "feafet23ts";
    view.setParameter(beforeEvent, parameter);
    verify(service, never()).get(anyLong(), any());
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + INVALID), test(notification).getText());
    assertFalse(view.save.isEnabled());
    verify(view.form).setEnabled(false);
  }

  @Test
  public void setParameter_MissingConfirm() {
    view.form = mock(PasswordsForm.class);
    String parameter = "34925";
    view.setParameter(beforeEvent, parameter);
    verify(service, never()).get(anyLong(), any());
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + INVALID), test(notification).getText());
    assertFalse(view.save.isEnabled());
    verify(view.form).setEnabled(false);
  }

  @Test
  public void setParameter_NullForgotPassword() {
    view.form = mock(PasswordsForm.class);
    long id = 34925;
    String confirmNumber = "feafet23ts";
    String parameter = id + SEPARATOR + confirmNumber;
    view.setParameter(beforeEvent, parameter);
    verify(service).get(id, confirmNumber);
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + INVALID), test(notification).getText());
    assertFalse(view.save.isEnabled());
    verify(view.form).setEnabled(false);
  }

  @Test
  public void setParameter_Empty() {
    view.form = mock(PasswordsForm.class);
    view.setParameter(beforeEvent, "");
    verify(service, never()).get(anyLong(), any());
    Notification notification = $(Notification.class).last();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + INVALID), test(notification).getText());
    assertFalse(view.save.isEnabled());
    verify(view.form).setEnabled(false);
  }
}
