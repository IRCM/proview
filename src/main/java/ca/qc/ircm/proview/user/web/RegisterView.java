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
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.util.Locale;

/**
 * Registers user.
 */
public interface RegisterView extends MessageResourcesView {
  public static final String VIEW_NAME = "user/register";

  public void setTitle(String title);

  public void showError(String message);

  public void afterSuccessfulRegister();

  public Label getHeaderLabel();

  public TextField getEmailField();

  public TextField getNameField();

  public PasswordField getPasswordField();

  public PasswordField getConfirmPasswordField();

  public Label getLaboratoryHeaderLabel();

  public CheckBox getNewLaboratoryField();

  public TextField getOrganizationField();

  public TextField getLaboratoryNameField();

  public TextField getManagerEmailField();

  public Label getAddressHeaderLabel();

  public TextField getAddressField();

  public TextField getAddressSecondField();

  public TextField getTownField();

  public TextField getStateField();

  public ComboBox getCountryField();

  public TextField getPostalCodeField();

  public Button getClearAddressButton();

  public Label getPhoneNumberHeaderLabel();

  public TextField getPhoneNumberField();

  public TextField getPhoneExtensionField();

  public Label getRegisterHeaderLabel();

  public Button getRegisterButton();

  public Label getRequiredLabel();

  @Override
  default MessageResource getResources() {
    return MessageResourcesView.super.getResources(RegisterView.class);
  }

  @Override
  default MessageResource getResources(Locale locale) {
    return MessageResourcesView.super.getResources(RegisterView.class, locale);
  }
}
