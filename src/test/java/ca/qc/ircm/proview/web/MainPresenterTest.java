package ca.qc.ircm.proview.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.utils.MessageResource;
import com.ejt.vaadin.loginform.LoginForm.LoginListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Locale;

public class MainPresenterTest {
  @Rule
  public RuleChain rules = Rules.defaultRules(this);
  @InjectMocks
  private MainPresenter mainPresenter = new MainPresenter();
  @Mock
  private MainView view;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private UserService userService;
  @Mock
  private LoginFormDefault signForm;
  @Mock
  private User user;
  @Captor
  private ArgumentCaptor<LoginListener> loginListenerCaptor;
  private Label header = new Label();
  private Label signHeader = new Label();
  private TextField signFormUsername = new TextField();
  private PasswordField signFormPassword = new PasswordField();
  private Button signButton = new Button();
  private Label forgorPasswordHeader = new Label();
  private TextField forgotPasswordEmailField = new TextField();
  private Button forgotPasswordButton = new Button();
  private Label registerHeader = new Label();
  private Button registerButton = new Button();
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
    when(view.getHeader()).thenReturn(header);
    when(view.getSignForm()).thenReturn(signForm);
    when(signForm.getHeader()).thenReturn(signHeader);
    when(signForm.getUserNameField()).thenReturn(signFormUsername);
    when(signForm.getPasswordField()).thenReturn(signFormPassword);
    when(signForm.getLoginButton()).thenReturn(signButton);
    when(view.getForgotPasswordHeader()).thenReturn(forgorPasswordHeader);
    when(view.getForgotPasswordEmailField()).thenReturn(forgotPasswordEmailField);
    when(view.getForgotPasswordButton()).thenReturn(forgotPasswordButton);
    when(view.getRegisterHeader()).thenReturn(registerHeader);
    when(view.getRegisterButton()).thenReturn(registerButton);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    mainPresenter.init(view);
  }

  @Test
  public void captions() {
    assertEquals(resources.message("header"), header.getValue());
    assertEquals(resources.message("sign"), signHeader.getValue());
    assertEquals(resources.message("sign.username"), signFormUsername.getCaption());
    assertEquals(resources.message("sign.password"), signFormPassword.getCaption());
    assertEquals(resources.message("sign.button"), signButton.getCaption());
    assertEquals(resources.message("forgotPassword"), forgorPasswordHeader.getValue());
    assertEquals(resources.message("forgotPassword.email"), forgotPasswordEmailField.getCaption());
    assertEquals(resources.message("forgotPassword.button"), forgotPasswordButton.getCaption());
    assertEquals(resources.message("register"), registerHeader.getValue());
    assertEquals(resources.message("register.button"), registerButton.getCaption());
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
    assertTrue(forgotPasswordEmailField.isRequired());
    assertEquals(requiredError(resources.message("forgotPassword.email")),
        forgotPasswordEmailField.getRequiredError());
  }

  @Test
  public void title() {
    verify(view).setTitle(resources.message("title"));
  }

  @Test
  public void signFormUsername_Required() {
    assertFalse(signFormUsername.isValid());
  }

  @Test
  public void signFormUsername_EmailValidator() {
    signFormUsername.setValue("aaa");

    assertFalse(signFormUsername.isValid());
  }

  @Test
  public void signFormPassword_Required() {
    assertFalse(signFormPassword.isValid());
  }

  @Test
  public void forgotPasswordEmailField_Required() {
    assertFalse(forgotPasswordEmailField.isValid());
  }

  @Test
  public void forgotPasswordEmailField_EmailValidator() {
    forgotPasswordEmailField.setValue("aaa");

    assertFalse(forgotPasswordEmailField.isValid());
  }

  private void clickLoginButton() {
    verify(signForm).addLoginListener(loginListenerCaptor.capture());
    List<LoginListener> listeners = loginListenerCaptor.getAllValues();
    for (LoginListener listener : listeners) {
      listener.onLogin(null);
    }
  }

  @Test
  public void sign() {
    signFormUsername.setValue(username);
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(authenticationService).sign(username, password, true);
  }

  @Test
  public void sign_EmailEmpty() {
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
    verify(view).showError(any());
  }

  @Test
  public void sign_EmailInvalid() {
    signFormUsername.setValue("aaa");
    signFormPassword.setValue(password);

    clickLoginButton();

    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
    verify(view).showError(any());
  }

  @Test
  public void sign_PasswordEmpty() {
    signFormUsername.setValue(username);

    clickLoginButton();

    verify(authenticationService, never()).sign(any(), any(), anyBoolean());
    verify(view).showError(any());
  }

  @Test
  public void forgotPassword() {
    when(userService.get(anyString())).thenReturn(user);
    forgotPasswordEmailField.setValue(username);

    forgotPasswordButton.click();

    // TODO Add test for forgot password creation.
  }

  @Test
  public void forgotPassword_EmailEmpty() {
    forgotPasswordButton.click();

    // TODO Add test for forgot password creation (never).
    verify(view).showError(any());
  }

  @Test
  public void forgotPassword_EmailInvalid() {
    forgotPasswordEmailField.setValue("aaa");

    forgotPasswordButton.click();

    // TODO Add test for forgot password creation (never).
    verify(view).showError(any());
  }

  @Test
  public void register() {
    registerButton.click();

    verify(view).navigateToRegister();
  }
}
