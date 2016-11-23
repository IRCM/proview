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

package ca.qc.ircm.proview.user.web.integration;

import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_COUNTRY;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_LINE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_POSTAL_CODE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_STATE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADDRESS_TOWN;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.ADD_PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.CLEAR_ADDRESS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY_NAME;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.LABORATORY_ORGANIZATION;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.MANAGER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.NAME;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.NEW_LABORATORY;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PASSWORD;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBERS;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_EXTENSION;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.PHONE_NUMBER_TYPE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.REGISTER_WARNING;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.REMOVE_PHONE_NUMBER;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.SAVE;
import static ca.qc.ircm.proview.user.web.NewUserFormPresenter.USER;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.HEADER;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.PhoneNumberType;
import ca.qc.ircm.proview.user.web.RegisterView;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;

public abstract class RegisterPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(RegisterView.VIEW_NAME);
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement userPanel() {
    return wrap(PanelElement.class, findElement(className(USER)));
  }

  protected TextFieldElement emailField() {
    return wrap(TextFieldElement.class, findElement(className(EMAIL)));
  }

  protected String getEmail() {
    return emailField().getValue();
  }

  protected void setEmail(String value) {
    emailField().setValue(value);
  }

  protected TextFieldElement nameField() {
    return wrap(TextFieldElement.class, findElement(className(NAME)));
  }

  protected String getName() {
    return nameField().getValue();
  }

  protected void setName(String value) {
    nameField().setValue(value);
  }

  protected PasswordFieldElement passwordField() {
    return wrap(PasswordFieldElement.class, findElement(className(PASSWORD)));
  }

  protected String getPassword() {
    return passwordField().getValue();
  }

  protected void setPassword(String value) {
    passwordField().setValue(value);
  }

  protected PasswordFieldElement confirmPasswordField() {
    return wrap(PasswordFieldElement.class, findElement(className(CONFIRM_PASSWORD)));
  }

  protected String getConfirmPassword() {
    return confirmPasswordField().getValue();
  }

  protected void setConfirmPassword(String value) {
    confirmPasswordField().setValue(value);
  }

  protected PanelElement laboratoryPanel() {
    return wrap(PanelElement.class, findElement(className(LABORATORY)));
  }

  protected CheckBoxElement newLaboratoryField() {
    return wrap(CheckBoxElement.class, findElement(className(NEW_LABORATORY)));
  }

  protected boolean isNewLaboratory() {
    return getCheckBoxValue(newLaboratoryField());
  }

  protected void setNewLaboratory(boolean value) {
    setCheckBoxValue(newLaboratoryField(), value);
  }

  protected TextFieldElement organizationField() {
    return wrap(TextFieldElement.class, findElement(className(LABORATORY_ORGANIZATION)));
  }

  protected String getOrganization() {
    return organizationField().getValue();
  }

  protected boolean isOrganizationVisible() {
    return organizationField().isDisplayed();
  }

  protected void setOrganization(String value) {
    organizationField().setValue(value);
  }

  protected TextFieldElement laboratoryNameField() {
    return wrap(TextFieldElement.class, findElement(className(LABORATORY + "-" + LABORATORY_NAME)));
  }

  protected String getLaboratoryName() {
    return laboratoryNameField().getValue();
  }

  protected boolean isLaboratoryNameVisible() {
    return laboratoryNameField().isDisplayed();
  }

  protected void setLaboratoryName(String value) {
    laboratoryNameField().setValue(value);
  }

  protected TextFieldElement managerEmailField() {
    return wrap(TextFieldElement.class, findElement(className(MANAGER)));
  }

  protected String getManagerEmail() {
    return managerEmailField().getValue();
  }

  protected boolean isManagerEmailVisible() {
    return managerEmailField().isDisplayed();
  }

  protected void setManagerEmail(String value) {
    managerEmailField().setValue(value);
  }

  protected PanelElement addressPanel() {
    return wrap(PanelElement.class, findElement(className(ADDRESS)));
  }

  protected TextFieldElement addressLineField() {
    return wrap(TextFieldElement.class, findElement(className(ADDRESS_LINE)));
  }

  protected String getAddressLine() {
    return addressLineField().getValue();
  }

  protected void setAddressLine(String value) {
    addressLineField().setValue(value);
  }

  protected TextFieldElement townField() {
    return wrap(TextFieldElement.class, findElement(className(ADDRESS_TOWN)));
  }

  protected String getTown() {
    return townField().getValue();
  }

  protected void setTown(String value) {
    townField().setValue(value);
  }

  protected TextFieldElement stateField() {
    return wrap(TextFieldElement.class, findElement(className(ADDRESS_STATE)));
  }

  protected String getState() {
    return stateField().getValue();
  }

  protected void setState(String value) {
    stateField().setValue(value);
  }

  protected TextFieldElement countryField() {
    return wrap(TextFieldElement.class, findElement(className(ADDRESS_COUNTRY)));
  }

  protected String getCountry() {
    return countryField().getValue();
  }

  protected void setCountry(String value) {
    countryField().setValue(value);
  }

  protected TextFieldElement postalCodeField() {
    return wrap(TextFieldElement.class, findElement(className(ADDRESS_POSTAL_CODE)));
  }

  protected String getPostalCode() {
    return postalCodeField().getValue();
  }

  protected void setPostalCode(String value) {
    postalCodeField().setValue(value);
  }

  protected ButtonElement clearAddressButton() {
    return wrap(ButtonElement.class, findElement(className(CLEAR_ADDRESS)));
  }

  protected void clickClearAddress() {
    clearAddressButton().click();
  }

  protected PanelElement phoneNumberPanel() {
    return wrap(PanelElement.class, findElement(className(PHONE_NUMBERS)));
  }

  protected ComboBoxElement phoneTypeField() {
    return wrap(ComboBoxElement.class, findElement(className(PHONE_NUMBER_TYPE)));
  }

  protected PhoneNumberType getPhoneType() {
    return Enum.valueOf(PhoneNumberType.class, phoneTypeField().getValue());
  }

  protected void setPhoneType(PhoneNumberType value) {
    phoneTypeField().selectByText(value.getLabel(currentLocale()));
  }

  protected TextFieldElement phoneNumberField() {
    return wrap(TextFieldElement.class, findElement(className(PHONE_NUMBER_NUMBER)));
  }

  protected String getPhoneNumber() {
    return phoneNumberField().getValue();
  }

  protected void setPhoneNumber(String value) {
    phoneNumberField().setValue(value);
  }

  protected TextFieldElement phoneExtensionField() {
    return wrap(TextFieldElement.class, findElement(className(PHONE_NUMBER_EXTENSION)));
  }

  protected String getPhoneExtension() {
    return phoneExtensionField().getValue();
  }

  protected void setPhoneExtension(String value) {
    phoneExtensionField().setValue(value);
  }

  protected ButtonElement removePhoneNumberButton() {
    return wrap(ButtonElement.class, findElement(className(REMOVE_PHONE_NUMBER)));
  }

  protected ButtonElement addPhoneNumberButton() {
    return wrap(ButtonElement.class, findElement(className(ADD_PHONE_NUMBER)));
  }

  protected LabelElement registerWarningLabel() {
    return wrap(LabelElement.class, findElement(className(REGISTER_WARNING)));
  }

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    saveButton().click();
  }
}
