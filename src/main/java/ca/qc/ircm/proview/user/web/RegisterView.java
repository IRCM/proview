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

import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Registers user view.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends RegisterDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/register";
  private static final long serialVersionUID = 7586918222688019429L;
  private static final Logger logger = LoggerFactory.getLogger(RegisterView.class);
  @Inject
  private RegisterPresenter presenter;

  @Override
  public void attach() {
    logger.debug("Register user view");
    super.attach();
    presenter.init(this);
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  /**
   * Redirect to {@link MainView}.
   */
  public void afterSuccessfulRegister() {
    final MessageResource resources = getResources();
    Notification.show(resources.message("done", emailField.getValue()),
        Notification.Type.TRAY_NOTIFICATION);
    getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
  }

  public TextField getEmailField() {
    return emailField;
  }

  public TextField getNameField() {
    return nameField;
  }

  public PasswordField getPasswordField() {
    return passwordField;
  }

  public PasswordField getConfirmPasswordField() {
    return confirmPasswordField;
  }

  public CheckBox getNewLaboratoryField() {
    return newLaboratoryField;
  }

  public TextField getOrganizationField() {
    return organizationField;
  }

  public TextField getLaboratoryNameField() {
    return laboratoryNameField;
  }

  public TextField getManagerEmailField() {
    return managerEmailField;
  }

  public TextField getAddressField() {
    return addressField;
  }

  public TextField getAddressSecondField() {
    return addressSecondField;
  }

  public TextField getTownField() {
    return townField;
  }

  public TextField getStateField() {
    return stateField;
  }

  public TextField getCountryField() {
    return countryField;
  }

  public TextField getPostalCodeField() {
    return postalCodeField;
  }

  public Button getClearAddressButton() {
    return clearAddressButton;
  }

  public TextField getPhoneNumberField() {
    return phoneNumberField;
  }

  public TextField getPhoneExtensionField() {
    return phoneExtensionField;
  }

  public Button getRegisterButton() {
    return registerButton;
  }

  public Label getHeaderLabel() {
    return headerLabel;
  }

  public Label getAddressHeaderLabel() {
    return addressHeaderLabel;
  }

  public Label getPhoneNumberHeaderLabel() {
    return phoneNumberHeaderLabel;
  }

  public Label getRegisterHeaderLabel() {
    return registerHeaderLabel;
  }

  public Label getRequiredLabel() {
    return requiredLabel;
  }

  public Label getLaboratoryHeaderLabel() {
    return laboratoryHeaderLabel;
  }
}
