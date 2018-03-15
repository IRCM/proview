/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.FORGOTTED_PASSWORD;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.FORGOT_PASSWORD;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.FORGOT_PASSWORD_BUTTON;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.FORGOT_PASSWORD_EMAIL;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.REGISTER_BUTTON;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.SIGN_BUTTON;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.SIGN_PANEL;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.SIGN_PASSWORD;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.SIGN_USERNAME;
import static ca.qc.ircm.proview.user.web.SigninViewPresenter.TITLE;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_EMAIL;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.LdapConfiguration;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.ForgotPasswordWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.CustomLoginForm;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.shiro.authc.AuthenticationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class SigninViewPresenterTest {
  private SigninViewPresenter presenter;
  @Mock
  private SigninView view;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private UserService userService;
  @Mock
  private ForgotPasswordService forgotPasswordService;
  @Mock
  private LdapConfiguration ldapConfiguration;
  @Mock
  private CustomLoginForm signForm;
  @Mock
  private User user;
  @Mock
  private ForgotPassword forgotPassword;
  @Captor
  private ArgumentCaptor<LoginListener> loginListenerCaptor;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<ForgotPasswordWebContext> forgotPasswordWebContextCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private SigninViewDesign design;
  private TextField signFormUsername = new TextField();
  private PasswordField signFormPassword = new PasswordField();
  private Button signButton = new Button();
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SigninView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String username = "proview@ircm.qc.ca";
  private String password = "password";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SigninViewPresenter(authenticationService, authorizationService, userService,
        forgotPasswordService, ldapConfiguration, applicationName);
    design = new SigninViewDesign();
    view.design = design;
    view.signForm = signForm;
    when(signForm.getUserNameField()).thenReturn(signFormUsername);
    when(signForm.getPasswordField()).thenReturn(signFormPassword);
    when(signForm.getLoginButton()).thenReturn(signButton);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.header.getStyleName().contains(HEADER));
    assertTrue(design.signPanel.getStyleName().contains(SIGN_PANEL));
    assertTrue(signFormUsername.getStyleName().contains(SIGN_USERNAME));
    assertTrue(signFormPassword.getStyleName().contains(SIGN_PASSWORD));
    assertTrue(signButton.getStyleName().contains(SIGN_BUTTON));
    assertTrue(signButton.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
    assertTrue(design.forgotPasswordPanel.getStyleName().contains(FORGOT_PASSWORD));
    assertTrue(design.forgotPasswordEmailField.getStyleName().contains(FORGOT_PASSWORD_EMAIL));
    assertTrue(design.forgotPasswordButton.getStyleName().contains(FORGOT_PASSWORD_BUTTON));
    assertTrue(design.registerButton.getStyleName().contains(REGISTER_BUTTON));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.header.getValue());
    assertEquals(resources.message(SIGN_PANEL), design.signPanel.getCaption());
    assertEquals(resources.message(SIGN_USERNAME), signFormUsername.getCaption());
    assertEquals(resources.message(SIGN_PASSWORD), signFormPassword.getCaption());
    assertEquals(resources.message(SIGN_BUTTON), signButton.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD), design.forgotPasswordPanel.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD_EMAIL),
        design.forgotPasswordEmailField.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD_BUTTON),
        design.forgotPasswordButton.getCaption());
    assertEquals(resources.message(REGISTER_BUTTON), design.registerButton.getCaption());
  }

  private void clickLoginButton() {
    verify(signForm).addLoginListener(loginListenerCaptor.capture());
    List<LoginListener> listeners = loginListenerCaptor.getAllValues();
    for (LoginListener listener : listeners) {
      listener.onLogin(null);
    }
  }

  @Test
  public void sign_User() {
    when(authorizationService.isUser()).thenReturn(true);
    presenter.init(view);
    signFormUsername.setValue(username);
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(authenticationService).sign(username, password, true);
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void sign_EmailEmpty() {
    presenter.init(view);
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        signFormUsername.getErrorMessage().getFormattedHtmlMessage());
    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
  }

  @Test
  public void sign_EmailInvalid() {
    presenter.init(view);
    signFormUsername.setValue("aaa");
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        signFormUsername.getErrorMessage().getFormattedHtmlMessage());
    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
  }

  @Test
  public void sign_EmailInvalid_Ldap() {
    when(ldapConfiguration.enabled()).thenReturn(true);
    presenter.init(view);
    signFormUsername.setValue("aaa");
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(view, never()).showError(stringCaptor.capture());
    verify(authenticationService).sign("aaa", password, true);
  }

  @Test
  public void sign_PasswordEmpty() {
    presenter.init(view);
    signFormUsername.setValue(username);

    clickLoginButton();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        signFormPassword.getErrorMessage().getFormattedHtmlMessage());
    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
  }

  @Test
  public void sign_AuthenticationException() {
    when(authorizationService.isUser()).thenThrow(new AuthenticationException());
    presenter.init(view);
    signFormUsername.setValue(username);
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message("sign.fail"), stringCaptor.getValue());
    verify(authenticationService).sign(username, password, true);
  }

  @Test
  public void forgotPassword() {
    when(userService.exists(any())).thenReturn(true);
    presenter.init(view);
    design.forgotPasswordEmailField.setValue(username);
    when(view.getUrl(any())).thenAnswer(context -> context.getArgumentAt(0, String.class));
    when(forgotPasswordService.insert(any(), any())).thenReturn(forgotPassword);
    long id = 357604839027601809L;
    int confirmNumber = 135495343;
    when(forgotPassword.getId()).thenReturn(id);
    when(forgotPassword.getConfirmNumber()).thenReturn(confirmNumber);

    design.forgotPasswordButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(username);
    verify(forgotPasswordService).insert(eq(username), forgotPasswordWebContextCaptor.capture());
    String url = forgotPasswordWebContextCaptor.getValue()
        .getChangeForgottenPasswordUrl(forgotPassword, locale);
    assertEquals(ForgotPasswordView.VIEW_NAME + "/" + id + "/" + confirmNumber, url);
    verify(view).showWarning(resources.message(FORGOTTED_PASSWORD));
  }

  @Test
  public void forgotPassword_EmailNoUser() {
    when(userService.exists(any())).thenReturn(false);
    presenter.init(view);
    design.forgotPasswordEmailField.setValue(username);

    design.forgotPasswordButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(username);
    verify(forgotPasswordService, never()).insert(any(), any());
    verify(view).showWarning(resources.message(FORGOTTED_PASSWORD));
  }

  @Test
  public void forgotPassword_EmailEmpty() {
    presenter.init(view);
    design.forgotPasswordButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.forgotPasswordEmailField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).insert(any(), any());
  }

  @Test
  public void forgotPassword_EmailInvalid() {
    presenter.init(view);
    design.forgotPasswordEmailField.setValue("aaa");

    design.forgotPasswordButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        design.forgotPasswordEmailField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).insert(any(), any());
  }

  @Test
  public void register() {
    presenter.init(view);
    design.registerButton.click();

    verify(view).navigateTo(RegisterView.VIEW_NAME);
  }

  @Test
  public void enter_NotSigned() {
    when(authorizationService.isUser()).thenReturn(false);
    presenter.init(view);

    presenter.enter("");

    verify(view, never()).navigateTo(any());
  }

  @Test
  public void enter_User() {
    when(authorizationService.isUser()).thenReturn(true);
    presenter.init(view);

    presenter.enter("");

    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }
}
