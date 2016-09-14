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

import static ca.qc.ircm.proview.user.web.UserFormPresenter.CONFIRM_PASSWORD_PROPERTY;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.EMAIL_PROPERTY;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.NAME_PROPERTY;
import static ca.qc.ircm.proview.user.web.UserFormPresenter.PASSWORD_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserFormPresenterTest {
  private UserFormPresenter presenter;
  @Mock
  private UserService userService;
  private UserForm view = new UserForm();
  private User user = new User();
  private BeanItem<User> item = new BeanItem<>(user);
  private PropertysetItem passwordItem = new PropertysetItem();
  private ObjectProperty<String> passwordProperty = new ObjectProperty<>(null, String.class);
  private ObjectProperty<String> confirmPasswordProperty = new ObjectProperty<>(null, String.class);
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources;
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private String email = "unit.test@ircm.qc.ca";
  private String name = "Unit Test";
  private String password = "unittestpassword";

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new UserFormPresenter(userService);
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
    presenter.attach();
    passwordItem.addItemProperty(UserFormPresenter.PASSWORD_PROPERTY, passwordProperty);
    passwordItem.addItemProperty(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY,
        confirmPasswordProperty);
  }

  private void setFields() {
    view.emailField.setValue(email);
    view.nameField.setValue(name);
    view.passwordField.setValue(password);
    view.confirmPasswordField.setValue(password);
  }

  private CompositeErrorMessage error(String message) {
    return new CompositeErrorMessage(new UserError(message));
  }

  @Test
  public void setPresenterInView() {
    UserForm view = mock(UserForm.class);
    presenter.init(view);

    verify(view).setPresenter(presenter);
  }

  @Test
  public void styles() {
    assertTrue(view.emailField.getStyleName().contains(EMAIL_PROPERTY));
    assertTrue(view.nameField.getStyleName().contains(NAME_PROPERTY));
    assertTrue(view.passwordField.getStyleName().contains(PASSWORD_PROPERTY));
    assertTrue(view.confirmPasswordField.getStyleName().contains(CONFIRM_PASSWORD_PROPERTY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(EMAIL_PROPERTY), view.emailField.getCaption());
    assertEquals(resources.message(NAME_PROPERTY), view.nameField.getCaption());
    assertEquals(resources.message(PASSWORD_PROPERTY), view.passwordField.getCaption());
    assertEquals(resources.message(CONFIRM_PASSWORD_PROPERTY),
        view.confirmPasswordField.getCaption());
  }

  @Test
  public void required_Default() {
    assertTrue(view.emailField.isRequired());
    assertEquals(generalResources.message("required", view.emailField.getCaption()),
        view.emailField.getRequiredError());
    assertTrue(view.nameField.isRequired());
    assertEquals(generalResources.message("required", view.nameField.getCaption()),
        view.nameField.getRequiredError());
    assertTrue(view.passwordField.isRequired());
    assertEquals(generalResources.message("required", view.passwordField.getCaption()),
        view.passwordField.getRequiredError());
    assertTrue(view.confirmPasswordField.isRequired());
    assertEquals(generalResources.message("required", view.confirmPasswordField.getCaption()),
        view.confirmPasswordField.getRequiredError());
  }

  @Test
  public void required_NewUser() {
    presenter.setItemDataSource(item);

    assertTrue(view.emailField.isRequired());
    assertEquals(generalResources.message("required", view.emailField.getCaption()),
        view.emailField.getRequiredError());
    assertTrue(view.nameField.isRequired());
    assertEquals(generalResources.message("required", view.nameField.getCaption()),
        view.nameField.getRequiredError());
    assertTrue(view.passwordField.isRequired());
    assertEquals(generalResources.message("required", view.passwordField.getCaption()),
        view.passwordField.getRequiredError());
    assertTrue(view.confirmPasswordField.isRequired());
    assertEquals(generalResources.message("required", view.confirmPasswordField.getCaption()),
        view.confirmPasswordField.getRequiredError());
  }

  @Test
  public void required_ExistingUser() {
    user.setId(1L);
    presenter.setItemDataSource(item);

    assertTrue(view.emailField.isRequired());
    assertEquals(generalResources.message("required", view.emailField.getCaption()),
        view.emailField.getRequiredError());
    assertTrue(view.nameField.isRequired());
    assertEquals(generalResources.message("required", view.nameField.getCaption()),
        view.nameField.getRequiredError());
    assertFalse(view.passwordField.isRequired());
    assertFalse(view.confirmPasswordField.isRequired());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.emailField.isReadOnly());
    assertTrue(view.emailField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.nameField.isReadOnly());
    assertTrue(view.nameField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.passwordField.isVisible());
    assertFalse(view.confirmPasswordField.isVisible());
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.emailField.isReadOnly());
    assertFalse(view.emailField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.nameField.isReadOnly());
    assertFalse(view.nameField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.passwordField.isVisible());
    assertTrue(view.confirmPasswordField.isVisible());
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.emailField.isReadOnly());
    assertTrue(view.emailField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.nameField.isReadOnly());
    assertTrue(view.nameField.getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.passwordField.isVisible());
    assertFalse(view.confirmPasswordField.isVisible());
  }

  @Test
  public void allFieldsValid() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertTrue(presenter.isValid());
    presenter.commit();
    assertEquals(email, user.getEmail());
    assertEquals(name, user.getName());
    assertEquals(password, passwordProperty.getValue());
    assertEquals(password, confirmPasswordProperty.getValue());
  }

  @Test
  public void email_Empty() {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.emailField.setValue("");

    assertFalse(view.emailField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void email_Invalid() {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.emailField.setValue("abc");

    assertFalse(view.emailField.isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".invalid")).getFormattedHtmlMessage(),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void email_AlreadyExists_NewUser() {
    when(userService.exists(any())).thenReturn(true);
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertFalse(view.emailField.isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".exists")).getFormattedHtmlMessage(),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void email_AlreadyExists_ExistingUser() {
    user.setId(1L);
    User otherUser = new User();
    otherUser.setEmail("other@email.com");
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(otherUser);
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertFalse(view.emailField.isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".exists")).getFormattedHtmlMessage(),
        view.emailField.getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void email_AlreadyExists_ExistingUser_DatabaseEmail() throws Throwable {
    user.setId(1L);
    user.setEmail(email);
    when(userService.exists(any())).thenReturn(true);
    when(userService.get(any(Long.class))).thenReturn(user);
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertTrue(view.emailField.isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void name_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.nameField.setValue("");

    assertFalse(view.nameField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void password_Empty_NewUser() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("");

    assertFalse(view.passwordField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void confirmPassword_Empty_NewUser() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.confirmPasswordField.setValue("");

    assertFalse(view.confirmPasswordField.isValid());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void passwords_Empty_ExistingUser() throws Throwable {
    user.setId(1L);
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.passwordField.setValue("");
    view.confirmPasswordField.setValue("");

    assertTrue(view.passwordField.isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void passwords_Match() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertTrue(view.passwordField.isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void passwords_DontMatch_ConfirmPasswordChanged() {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    assertTrue(presenter.isValid());
    view.confirmPasswordField.setValue("password2");

    assertFalse(view.passwordField.isValid());
    assertEquals(
        error(resources.message(PASSWORD_PROPERTY + ".notMatch")).getFormattedHtmlMessage(),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }

  @Test
  public void passwords_DontMatch_PasswordChanged() {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    assertTrue(presenter.isValid());
    view.passwordField.setValue("password2");

    assertFalse(view.passwordField.isValid());
    assertEquals(
        error(resources.message(PASSWORD_PROPERTY + ".notMatch")).getFormattedHtmlMessage(),
        view.passwordField.getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
