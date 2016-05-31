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
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Registers user view.
 */
@SpringView(name = RegisterView.VIEW_NAME)
public class RegisterView extends RegisterViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/register";
  private static final long serialVersionUID = 7586918222688019429L;
  @Inject
  private RegisterViewPresenter presenter;

  @PostConstruct
  public void init() {
    presenter.init(this);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  /**
   * Redirect to {@link MainView}.
   *
   * @param message
   *          message to show in notification
   */
  public void afterSuccessfulRegister(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
    getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
  }

  public Panel getLaboratoryPanel() {
    return laboratoryPanel;
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

  public Button getRegisterButton() {
    return registerButton;
  }

  public Label getHeaderLabel() {
    return headerLabel;
  }

  public Label getRegisterHeaderLabel() {
    return registerHeaderLabel;
  }

  public Label getRequiredLabel() {
    return requiredLabel;
  }

  public Panel getUserPanel() {
    return userPanel;
  }

  public UserForm getUserForm() {
    return userForm;
  }

  public Panel getAddressPanel() {
    return addressPanel;
  }

  public AddressForm getAddressForm() {
    return addressForm;
  }

  public Button getClearAddressButton() {
    return clearAddressButton;
  }

  public Panel getPhoneNumberPanel() {
    return phoneNumberPanel;
  }

  public PhoneNumberForm getPhoneNumberForm() {
    return phoneNumberForm;
  }
}
