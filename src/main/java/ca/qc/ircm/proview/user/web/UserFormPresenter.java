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

import ca.qc.ircm.proview.user.QUser;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * User form.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserFormPresenter {
  public static final String ID_PROPERTY = QUser.user.id.getMetadata().getName();
  public static final String EMAIL_PROPERTY = QUser.user.email.getMetadata().getName();
  public static final String NAME_PROPERTY = QUser.user.name.getMetadata().getName();
  public static final String PASSWORD_PROPERTY = "password";
  public static final String CONFIRM_PASSWORD_PROPERTY = "confirmPassword";
  public static final String LABORATORY_PROPERTY = QUser.user.laboratory.getMetadata().getName();
  public static final String ADDRESSES_PROPERTY = QUser.user.address.getMetadata().getName();
  public static final String PHONE_NUMBERS_PROPERTY =
      QUser.user.phoneNumbers.getMetadata().getName();
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<User> userFieldGroup = new BeanFieldGroup<>(User.class);
  private FieldGroup passwordFieldGroup = new FieldGroup();
  private Long userId;
  private UserForm view;
  @Inject
  private UserService userService;

  public UserFormPresenter() {
  }

  public UserFormPresenter(UserService userService) {
    this.userService = userService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(UserForm view) {
    this.view = view;
    view.setPresenter(this);
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    setStyles();
    bindFields();
    addFieldListeners();
    updateEditable();
    setCaptions();
    setRequired();
    addValidators();
  }

  private void setStyles() {
    view.emailField.setStyleName(EMAIL_PROPERTY);
    view.nameField.setStyleName(NAME_PROPERTY);
    view.passwordField.setStyleName(PASSWORD_PROPERTY);
    view.confirmPasswordField.setStyleName(CONFIRM_PASSWORD_PROPERTY);
  }

  private void bindFields() {
    userFieldGroup.setItemDataSource(new BeanItem<>(new User()));
    userFieldGroup.bind(view.emailField, EMAIL_PROPERTY);
    userFieldGroup.bind(view.nameField, NAME_PROPERTY);
    passwordFieldGroup.bind(view.passwordField, PASSWORD_PROPERTY);
    passwordFieldGroup.bind(view.confirmPasswordField, CONFIRM_PASSWORD_PROPERTY);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    view.confirmPasswordField.addValueChangeListener(e -> {
      view.passwordField.isValid();
      view.passwordField.markAsDirty();
    });
  }

  private void updateEditable() {
    boolean editable = editableProperty.getValue();
    view.emailField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.nameField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      view.emailField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.nameField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    view.emailField.setReadOnly(!editable);
    view.nameField.setReadOnly(!editable);
    view.passwordField.setVisible(editable);
    view.confirmPasswordField.setVisible(editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.emailField.setCaption(resources.message(EMAIL_PROPERTY));
    view.nameField.setCaption(resources.message(NAME_PROPERTY));
    view.passwordField.setCaption(resources.message(PASSWORD_PROPERTY));
    view.confirmPasswordField.setCaption(resources.message(CONFIRM_PASSWORD_PROPERTY));
  }

  private void setRequired() {
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.emailField.setRequired(true);
    view.emailField
        .setRequiredError(generalResources.message("required", view.emailField.getCaption()));
    view.nameField.setRequired(true);
    view.nameField
        .setRequiredError(generalResources.message("required", view.nameField.getCaption()));
    view.passwordField
        .setRequiredError(generalResources.message("required", view.passwordField.getCaption()));
    view.confirmPasswordField.setRequiredError(
        generalResources.message("required", view.confirmPasswordField.getCaption()));
    view.passwordField.setRequired(userId == null);
    view.confirmPasswordField.setRequired(userId == null);
  }

  private void addValidators() {
    MessageResource resources = view.getResources();
    view.emailField
        .addValidator(new EmailValidator(resources.message(EMAIL_PROPERTY + ".invalid")));
    view.emailField.addValidator((value) -> {
      String email = view.emailField.getValue();
      if (userService.exists(email)
          && (userId == null || !userService.get(userId).getEmail().equals(email))) {
        throw new InvalidValueException(resources.message(EMAIL_PROPERTY + ".exists"));
      }
    });
    view.passwordField.addValidator((value) -> {
      String password = view.passwordField.getValue();
      String confirmPassword = view.confirmPasswordField.getValue();
      if (password != null && !password.isEmpty() && confirmPassword != null
          && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
        throw new InvalidValueException(resources.message(PASSWORD_PROPERTY + ".notMatch"));
      }
    });
  }

  /**
   * Commits all changes done to the bound fields.
   *
   * @throws CommitException
   *           if the commit was aborted
   */
  public void commit() throws CommitException {
    userFieldGroup.commit();
    passwordFieldGroup.commit();
  }

  public boolean isValid() {
    return userFieldGroup.isValid() && passwordFieldGroup.isValid();
  }

  public Item getItemDataSource() {
    return userFieldGroup.getItemDataSource();
  }

  /**
   * Sets user as an item.
   *
   * @param item
   *          user as an item
   */
  public void setItemDataSource(Item item) {
    userFieldGroup.setItemDataSource(item);
    userId = (Long) item.getItemProperty(ID_PROPERTY).getValue();
    setRequired();
  }

  public Item getPasswordItemDataSource() {
    return passwordFieldGroup.getItemDataSource();
  }

  /**
   * Sets password item.
   *
   * @param item
   *          password item
   */
  public void setPasswordItemDataSource(Item item) {
    passwordFieldGroup.setItemDataSource(item);
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
