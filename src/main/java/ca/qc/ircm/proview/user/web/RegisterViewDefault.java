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

import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Registers user.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterViewDefault extends RegisterDesign implements RegisterView {
  private static final long serialVersionUID = 7586918222688019429L;
  private static final Logger logger = LoggerFactory.getLogger(RegisterViewDefault.class);
  @Inject
  private RegisterPresenter presenter;

  @Override
  public void attach() {
    logger.debug("Register user view");
    super.attach();
    presenter.init(this);
  }

  @Override
  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  @Override
  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  @Override
  public void afterSuccessfulRegister() {
    final MessageResource resources = getResources();
    Notification.show(resources.message("done", emailField.getValue()),
        Notification.Type.TRAY_NOTIFICATION);
    getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
  }

  @Override
  public TextField getEmailField() {
    return emailField;
  }

  @Override
  public TextField getNameField() {
    return nameField;
  }

  @Override
  public PasswordField getPasswordField() {
    return passwordField;
  }

  @Override
  public PasswordField getConfirmPasswordField() {
    return confirmPasswordField;
  }

  @Override
  public CheckBox getNewLaboratoryField() {
    return newLaboratoryField;
  }

  @Override
  public TextField getOrganizationField() {
    return organizationField;
  }

  @Override
  public TextField getLaboratoryNameField() {
    return laboratoryNameField;
  }

  @Override
  public TextField getManagerEmailField() {
    return managerEmailField;
  }

  @Override
  public TextField getAddressField() {
    return addressField;
  }

  @Override
  public TextField getAddressSecondField() {
    return addressSecondField;
  }

  @Override
  public TextField getTownField() {
    return townField;
  }

  @Override
  public TextField getStateField() {
    return stateField;
  }

  @Override
  public ComboBox getCountryField() {
    return countryField;
  }

  @Override
  public TextField getPostalCodeField() {
    return postalCodeField;
  }

  @Override
  public Button getClearAddressButton() {
    return clearAddressButton;
  }

  @Override
  public TextField getPhoneNumberField() {
    return phoneNumberField;
  }

  @Override
  public TextField getPhoneExtensionField() {
    return phoneExtensionField;
  }

  @Override
  public Button getRegisterButton() {
    return registerButton;
  }

  @Override
  public Label getHeaderLabel() {
    return headerLabel;
  }

  @Override
  public Label getAddressHeaderLabel() {
    return addressHeaderLabel;
  }

  @Override
  public Label getPhoneNumberHeaderLabel() {
    return phoneNumberHeaderLabel;
  }

  @Override
  public Label getRegisterHeaderLabel() {
    return registerHeaderLabel;
  }

  @Override
  public Label getRequiredLabel() {
    return requiredLabel;
  }

  @Override
  public Label getLaboratoryHeaderLabel() {
    return laboratoryHeaderLabel;
  }
}
