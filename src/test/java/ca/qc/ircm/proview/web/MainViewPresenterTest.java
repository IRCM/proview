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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD;
import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD_EMAIL;
import static ca.qc.ircm.proview.web.MainViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.MainViewPresenter.REGISTER;
import static ca.qc.ircm.proview.web.MainViewPresenter.REGISTER_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_PANEL;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_PASSWORD;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_USERNAME;
import static ca.qc.ircm.proview.web.MainViewPresenter.TITLE;
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
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.user.ForgotPasswordWebContext;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.utils.MessageResource;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
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
@ServiceTestAnnotations
public class MainViewPresenterTest {
  private MainViewPresenter presenter;
  @Mock
  private MainView view;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private UserService userService;
  @Mock
  private ForgotPasswordService forgotPasswordService;
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
  private TextField signFormUsername = new TextField();
  private PasswordField signFormPassword = new PasswordField();
  private Button signButton = new Button();
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(MainView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String username = "proview@ircm.qc.ca";
  private String password = "password";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new MainViewPresenter(authenticationService, authorizationService, userService,
        forgotPasswordService, applicationName);
    view.menu = new Menu();
    view.header = new Label();
    view.signPanel = new Panel();
    view.signForm = signForm;
    view.forgotPasswordPanel = new Panel();
    view.forgotPasswordEmailField = new TextField();
    view.forgotPasswordButton = new Button();
    view.registerPanel = new Panel();
    view.registerButton = new Button();
    when(signForm.getUserNameField()).thenReturn(signFormUsername);
    when(signForm.getPasswordField()).thenReturn(signFormPassword);
    when(signForm.getLoginButton()).thenReturn(signButton);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    presenter.init(view);
  }

  private String errorMessage(String message) {
    return new CompositeErrorMessage(new UserError(message)).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    assertTrue(view.header.getStyleName().contains(HEADER));
    assertTrue(view.signPanel.getStyleName().contains(SIGN_PANEL));
    assertTrue(signFormUsername.getStyleName().contains(SIGN_USERNAME));
    assertTrue(signFormPassword.getStyleName().contains(SIGN_PASSWORD));
    assertTrue(signButton.getStyleName().contains(SIGN_BUTTON));
    assertTrue(view.forgotPasswordPanel.getStyleName().contains(FORGOT_PASSWORD));
    assertTrue(view.forgotPasswordEmailField.getStyleName().contains(FORGOT_PASSWORD_EMAIL));
    assertTrue(view.forgotPasswordButton.getStyleName().contains(FORGOT_PASSWORD_BUTTON));
    assertTrue(view.registerPanel.getStyleName().contains(REGISTER));
    assertTrue(view.registerButton.getStyleName().contains(REGISTER_BUTTON));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(HEADER), view.header.getValue());
    assertEquals(resources.message(SIGN_PANEL), view.signPanel.getCaption());
    assertEquals(resources.message(SIGN_USERNAME), signFormUsername.getCaption());
    assertEquals(resources.message(SIGN_PASSWORD), signFormPassword.getCaption());
    assertEquals(resources.message(SIGN_BUTTON), signButton.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD), view.forgotPasswordPanel.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD_EMAIL),
        view.forgotPasswordEmailField.getCaption());
    assertEquals(resources.message(FORGOT_PASSWORD_BUTTON), view.forgotPasswordButton.getCaption());
    assertEquals(resources.message(REGISTER), view.registerPanel.getCaption());
    assertEquals(resources.message(REGISTER_BUTTON), view.registerButton.getCaption());
  }

  private String requiredError(String caption) {
    return generalResources.message("required", caption);
  }

  @Test
  public void requiredFields() {
    assertTrue(signFormUsername.isRequired());
    assertEquals(requiredError(resources.message("sign.username")),
        signFormUsername.getRequiredError());
    assertTrue(signFormPassword.isRequired());
    assertEquals(requiredError(resources.message("sign.password")),
        signFormPassword.getRequiredError());
    assertTrue(view.forgotPasswordEmailField.isRequired());
    assertEquals(requiredError(resources.message("forgotPassword.email")),
        view.forgotPasswordEmailField.getRequiredError());
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
  }

  @Test
  public void required() {
    assertTrue(signFormUsername.isRequired());
    assertEquals(generalResources.message(REQUIRED), signFormUsername.getRequiredError());
    assertTrue(signFormPassword.isRequired());
    assertEquals(generalResources.message(REQUIRED), signFormPassword.getRequiredError());
    assertTrue(view.forgotPasswordEmailField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.forgotPasswordEmailField.getRequiredError());
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
    signFormUsername.setValue(username);
    signFormPassword.setValue(password);
    when(authorizationService.isUser()).thenReturn(true);

    clickLoginButton();

    verify(authenticationService).sign(username, password, true);
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void sign_EmailEmpty() {
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
  public void sign_PasswordEmpty() {
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
    signFormUsername.setValue(username);
    signFormPassword.setValue(password);
    when(authorizationService.isUser()).thenThrow(new AuthenticationException());

    clickLoginButton();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message("sign.fail"), stringCaptor.getValue());
    verify(authenticationService).sign(username, password, true);
  }

  @Test
  public void forgotPassword() {
    when(userService.exists(any())).thenReturn(true);
    view.forgotPasswordEmailField.setValue(username);
    String forgotPasswordUrl = "/proview/forgotpassword";
    when(view.getUrl(any())).thenReturn(forgotPasswordUrl);
    when(forgotPasswordService.insert(any(), any())).thenReturn(forgotPassword);
    long id = 357604839027601809L;
    int confirmNumber = 135495343;
    when(forgotPassword.getId()).thenReturn(id);
    when(forgotPassword.getConfirmNumber()).thenReturn(confirmNumber);

    view.forgotPasswordButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(username);
    verify(forgotPasswordService).insert(eq(username), forgotPasswordWebContextCaptor.capture());
    String url = forgotPasswordWebContextCaptor.getValue()
        .getChangeForgottenPasswordUrl(forgotPassword, locale);
    assertEquals(forgotPasswordUrl + "/" + id + "/" + confirmNumber, url);
    verify(view).showWarning(resources.message(FORGOT_PASSWORD + ".done"));
  }

  @Test
  public void forgotPassword_EmailNoUser() {
    when(userService.exists(any())).thenReturn(false);
    view.forgotPasswordEmailField.setValue(username);

    view.forgotPasswordButton.click();

    verify(view, never()).showError(any());
    verify(userService).exists(username);
    verify(forgotPasswordService, never()).insert(any(), any());
    verify(view).showWarning(resources.message(FORGOT_PASSWORD + ".done"));
  }

  @Test
  public void forgotPassword_EmailEmpty() {
    view.forgotPasswordButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.forgotPasswordEmailField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).insert(any(), any());
  }

  @Test
  public void forgotPassword_EmailInvalid() {
    view.forgotPasswordEmailField.setValue("aaa");

    view.forgotPasswordButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_EMAIL)),
        view.forgotPasswordEmailField.getErrorMessage().getFormattedHtmlMessage());
    verify(forgotPasswordService, never()).insert(any(), any());
  }

  @Test
  public void register() {
    view.registerButton.click();

    verify(view).navigateTo(RegisterView.VIEW_NAME);
  }

  @Test
  public void enter_NotSigned() {
    when(authorizationService.isUser()).thenReturn(false);

    presenter.enter("");

    verify(view, never()).navigateTo(any());
  }

  @Test
  public void enter_User() {
    when(authorizationService.isUser()).thenReturn(true);

    presenter.enter("");

    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }
}
