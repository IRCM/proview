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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class UserFormPresenterTest {
  @InjectMocks
  private UserFormPresenter presenter = new UserFormPresenter();
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
    view.setLocale(locale);
    resources = view.getResources();
    presenter.init(view);
    presenter.attach();
    passwordItem.addItemProperty(UserFormPresenter.PASSWORD_PROPERTY, passwordProperty);
    passwordItem.addItemProperty(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY,
        confirmPasswordProperty);
  }

  private void setFields() {
    view.getEmailField().setValue(email);
    view.getNameField().setValue(name);
    view.getPasswordField().setValue(password);
    view.getConfirmPasswordField().setValue(password);
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
    assertTrue(view.getEmailField().getStyleName().contains(EMAIL_PROPERTY));
    assertTrue(view.getNameField().getStyleName().contains(NAME_PROPERTY));
    assertTrue(view.getPasswordField().getStyleName().contains(PASSWORD_PROPERTY));
    assertTrue(view.getConfirmPasswordField().getStyleName().contains(CONFIRM_PASSWORD_PROPERTY));
  }

  @Test
  public void captions() {
    assertEquals(resources.message(EMAIL_PROPERTY), view.getEmailField().getCaption());
    assertEquals(resources.message(NAME_PROPERTY), view.getNameField().getCaption());
    assertEquals(resources.message(PASSWORD_PROPERTY), view.getPasswordField().getCaption());
    assertEquals(resources.message(CONFIRM_PASSWORD_PROPERTY),
        view.getConfirmPasswordField().getCaption());
  }

  @Test
  public void required_Default() {
    assertTrue(view.getEmailField().isRequired());
    assertEquals(generalResources.message("required", view.getEmailField().getCaption()),
        view.getEmailField().getRequiredError());
    assertTrue(view.getNameField().isRequired());
    assertEquals(generalResources.message("required", view.getNameField().getCaption()),
        view.getNameField().getRequiredError());
    assertTrue(view.getPasswordField().isRequired());
    assertEquals(generalResources.message("required", view.getPasswordField().getCaption()),
        view.getPasswordField().getRequiredError());
    assertTrue(view.getConfirmPasswordField().isRequired());
    assertEquals(generalResources.message("required", view.getConfirmPasswordField().getCaption()),
        view.getConfirmPasswordField().getRequiredError());
  }

  @Test
  public void required_NewUser() {
    presenter.setItemDataSource(item);

    assertTrue(view.getEmailField().isRequired());
    assertEquals(generalResources.message("required", view.getEmailField().getCaption()),
        view.getEmailField().getRequiredError());
    assertTrue(view.getNameField().isRequired());
    assertEquals(generalResources.message("required", view.getNameField().getCaption()),
        view.getNameField().getRequiredError());
    assertTrue(view.getPasswordField().isRequired());
    assertEquals(generalResources.message("required", view.getPasswordField().getCaption()),
        view.getPasswordField().getRequiredError());
    assertTrue(view.getConfirmPasswordField().isRequired());
    assertEquals(generalResources.message("required", view.getConfirmPasswordField().getCaption()),
        view.getConfirmPasswordField().getRequiredError());
  }

  @Test
  public void required_ExistingUser() {
    user.setId(1L);
    presenter.setItemDataSource(item);

    assertTrue(view.getEmailField().isRequired());
    assertEquals(generalResources.message("required", view.getEmailField().getCaption()),
        view.getEmailField().getRequiredError());
    assertTrue(view.getNameField().isRequired());
    assertEquals(generalResources.message("required", view.getNameField().getCaption()),
        view.getNameField().getRequiredError());
    assertFalse(view.getPasswordField().isRequired());
    assertFalse(view.getConfirmPasswordField().isRequired());
  }

  @Test
  public void editable_Default() {
    assertTrue(view.getEmailField().isReadOnly());
    assertTrue(view.getEmailField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getNameField().isReadOnly());
    assertTrue(view.getNameField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getPasswordField().isVisible());
    assertFalse(view.getConfirmPasswordField().isVisible());
  }

  @Test
  public void editable_True() {
    presenter.setEditable(true);

    assertFalse(view.getEmailField().isReadOnly());
    assertFalse(view.getEmailField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getNameField().isReadOnly());
    assertFalse(view.getNameField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getPasswordField().isVisible());
    assertTrue(view.getConfirmPasswordField().isVisible());
  }

  @Test
  public void editable_False() {
    presenter.setEditable(false);

    assertTrue(view.getEmailField().isReadOnly());
    assertTrue(view.getEmailField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertTrue(view.getNameField().isReadOnly());
    assertTrue(view.getNameField().getStyleName().contains(ValoTheme.TEXTFIELD_BORDERLESS));
    assertFalse(view.getPasswordField().isVisible());
    assertFalse(view.getConfirmPasswordField().isVisible());
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
    view.getEmailField().setValue("");

    assertFalse(view.getEmailField().isValid());
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
    view.getEmailField().setValue("abc");

    assertFalse(view.getEmailField().isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".invalid")).getFormattedHtmlMessage(),
        view.getEmailField().getErrorMessage().getFormattedHtmlMessage());
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

    assertFalse(view.getEmailField().isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".exists")).getFormattedHtmlMessage(),
        view.getEmailField().getErrorMessage().getFormattedHtmlMessage());
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

    assertFalse(view.getEmailField().isValid());
    assertEquals(error(resources.message(EMAIL_PROPERTY + ".exists")).getFormattedHtmlMessage(),
        view.getEmailField().getErrorMessage().getFormattedHtmlMessage());
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

    assertTrue(view.getEmailField().isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void name_Empty() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();
    view.getNameField().setValue("");

    assertFalse(view.getNameField().isValid());
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
    view.getPasswordField().setValue("");

    assertFalse(view.getPasswordField().isValid());
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
    view.getConfirmPasswordField().setValue("");

    assertFalse(view.getConfirmPasswordField().isValid());
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
    view.getPasswordField().setValue("");
    view.getConfirmPasswordField().setValue("");

    assertTrue(view.getPasswordField().isValid());
    assertTrue(presenter.isValid());
    presenter.commit();
  }

  @Test
  public void passwords_Match() throws Throwable {
    presenter.setItemDataSource(item);
    presenter.setPasswordItemDataSource(passwordItem);
    presenter.setEditable(true);
    setFields();

    assertTrue(view.getPasswordField().isValid());
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
    view.getConfirmPasswordField().setValue("password2");

    assertFalse(view.getPasswordField().isValid());
    assertEquals(
        error(resources.message(PASSWORD_PROPERTY + ".notMatch")).getFormattedHtmlMessage(),
        view.getPasswordField().getErrorMessage().getFormattedHtmlMessage());
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
    view.getPasswordField().setValue("password2");

    assertFalse(view.getPasswordField().isValid());
    assertEquals(
        error(resources.message(PASSWORD_PROPERTY + ".notMatch")).getFormattedHtmlMessage(),
        view.getPasswordField().getErrorMessage().getFormattedHtmlMessage());
    assertFalse(presenter.isValid());
    try {
      presenter.commit();
      fail("Expected CommitException");
    } catch (CommitException e) {
      // Success.
    }
  }
}
