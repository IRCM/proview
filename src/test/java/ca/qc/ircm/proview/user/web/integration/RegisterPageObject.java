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

import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.ADDRESS_FORM_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.CLEAR_ADDRESS_BUTTON_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.LABORATORY_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.LABORATORY_NAME_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.MANAGER_EMAIL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.NEW_LABORATORY_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.ORGANIZATION_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.PHONE_NUMBER_FORM_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REGISTER_BUTTON_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REGISTER_HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.REQUIRED_LABEL_ID;
import static ca.qc.ircm.proview.user.web.RegisterViewPresenter.USER_FORM_ID;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.AddressFormPresenter;
import ca.qc.ircm.proview.user.web.PhoneNumberFormPresenter;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.UserFormPresenter;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import org.openqa.selenium.WebElement;

public abstract class RegisterPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(RegisterView.VIEW_NAME);
  }

  private boolean getCheckBoxValue(CheckBoxElement field) {
    String value = field.getValue();
    return value.equals("checked");
  }

  private void setCheckBoxValue(CheckBoxElement field, boolean value) {
    if (value != getCheckBoxValue(field)) {
      field.findElement(tagName("label")).click();
    }
  }

  protected LabelElement headerLabel() {
    return $(LabelElement.class).id(HEADER_LABEL_ID);
  }

  protected PanelElement userPanel() {
    return $(PanelElement.class).id(USER_FORM_ID);
  }

  private WebElement userFormElement(String className) {
    return findElement(org.openqa.selenium.By.id(USER_FORM_ID))
        .findElement(org.openqa.selenium.By.className(className));
  }

  protected TextFieldElement emailField() {
    return wrap(TextFieldElement.class, userFormElement(UserFormPresenter.EMAIL_PROPERTY));
  }

  protected String getEmail() {
    return emailField().getValue();
  }

  protected void setEmail(String value) {
    emailField().setValue(value);
  }

  protected TextFieldElement nameField() {
    return wrap(TextFieldElement.class, userFormElement(UserFormPresenter.NAME_PROPERTY));
  }

  protected String getName() {
    return nameField().getValue();
  }

  protected void setName(String value) {
    nameField().setValue(value);
  }

  protected PasswordFieldElement passwordField() {
    return wrap(PasswordFieldElement.class, userFormElement(UserFormPresenter.PASSWORD_PROPERTY));
  }

  protected String getPassword() {
    return passwordField().getValue();
  }

  protected void setPassword(String value) {
    passwordField().setValue(value);
  }

  protected PasswordFieldElement confirmPasswordField() {
    return wrap(PasswordFieldElement.class,
        userFormElement(UserFormPresenter.CONFIRM_PASSWORD_PROPERTY));
  }

  protected String getConfirmPassword() {
    return confirmPasswordField().getValue();
  }

  protected void setConfirmPassword(String value) {
    confirmPasswordField().setValue(value);
  }

  protected PanelElement laboratoryPanel() {
    return $(PanelElement.class).id(LABORATORY_ID);
  }

  protected CheckBoxElement newLaboratoryField() {
    return $(CheckBoxElement.class).id(NEW_LABORATORY_ID);
  }

  protected boolean isNewLaboratory() {
    return getCheckBoxValue(newLaboratoryField());
  }

  protected void setNewLaboratory(boolean value) {
    setCheckBoxValue(newLaboratoryField(), value);
  }

  protected TextFieldElement organizationField() {
    return $(TextFieldElement.class).id(ORGANIZATION_ID);
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
    return $(TextFieldElement.class).id(LABORATORY_NAME_ID);
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
    return $(TextFieldElement.class).id(MANAGER_EMAIL_ID);
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
    return $(PanelElement.class).id(ADDRESS_FORM_ID);
  }

  private WebElement addressFormElement(String className) {
    return findElement(org.openqa.selenium.By.id(ADDRESS_FORM_ID))
        .findElement(org.openqa.selenium.By.className(className));
  }

  protected TextFieldElement addressLineField() {
    return wrap(TextFieldElement.class, addressFormElement(AddressFormPresenter.LINE_PROPERTY));
  }

  protected String getAddressLine() {
    return addressLineField().getValue();
  }

  protected void setAddressLine(String value) {
    addressLineField().setValue(value);
  }

  protected TextFieldElement addressSecondLineField() {
    return wrap(TextFieldElement.class,
        addressFormElement(AddressFormPresenter.SECOND_LINE_PROPERTY));
  }

  protected String getAddressSecondLine() {
    return addressSecondLineField().getValue();
  }

  protected void setAddressSecondLine(String value) {
    addressSecondLineField().setValue(value);
  }

  protected TextFieldElement townField() {
    return wrap(TextFieldElement.class, addressFormElement(AddressFormPresenter.TOWN_PROPERTY));
  }

  protected String getTown() {
    return townField().getValue();
  }

  protected void setTown(String value) {
    townField().setValue(value);
  }

  protected TextFieldElement stateField() {
    return wrap(TextFieldElement.class, addressFormElement(AddressFormPresenter.STATE_PROPERTY));
  }

  protected String getState() {
    return stateField().getValue();
  }

  protected void setState(String value) {
    stateField().setValue(value);
  }

  protected TextFieldElement countryField() {
    return wrap(TextFieldElement.class, addressFormElement(AddressFormPresenter.COUNTRY_PROPERTY));
  }

  protected String getCountry() {
    return countryField().getValue();
  }

  protected void setCountry(String value) {
    countryField().setValue(value);
  }

  protected TextFieldElement postalCodeField() {
    return wrap(TextFieldElement.class,
        addressFormElement(AddressFormPresenter.POSTAL_CODE_PROPERTY));
  }

  protected String getPostalCode() {
    return postalCodeField().getValue();
  }

  protected void setPostalCode(String value) {
    postalCodeField().setValue(value);
  }

  protected ButtonElement clearAddressButton() {
    return $(ButtonElement.class).id(CLEAR_ADDRESS_BUTTON_ID);
  }

  protected void clickClearAddress() {
    clearAddressButton().click();
  }

  protected PanelElement phoneNumberPanel() {
    return $(PanelElement.class).id(PHONE_NUMBER_FORM_ID);
  }

  private WebElement phoneNumberFormElement(String className) {
    return findElement(org.openqa.selenium.By.id(PHONE_NUMBER_FORM_ID))
        .findElement(org.openqa.selenium.By.className(className));
  }

  protected TextFieldElement phoneTypeField() {
    return wrap(TextFieldElement.class,
        phoneNumberFormElement(PhoneNumberFormPresenter.TYPE_PROPERTY));
  }

  protected String getPhoneType() {
    return phoneNumberField().getValue();
  }

  protected void setPhoneType(String value) {
    phoneNumberField().setValue(value);
  }

  protected TextFieldElement phoneNumberField() {
    return wrap(TextFieldElement.class,
        phoneNumberFormElement(PhoneNumberFormPresenter.NUMBER_PROPERTY));
  }

  protected String getPhoneNumber() {
    return phoneNumberField().getValue();
  }

  protected void setPhoneNumber(String value) {
    phoneNumberField().setValue(value);
  }

  protected TextFieldElement phoneExtensionField() {
    return wrap(TextFieldElement.class,
        phoneNumberFormElement(PhoneNumberFormPresenter.EXTENSION_PROPERTY));
  }

  protected String getPhoneExtension() {
    return phoneExtensionField().getValue();
  }

  protected void setPhoneExtension(String value) {
    phoneExtensionField().setValue(value);
  }

  protected LabelElement registerHeaderLabel() {
    return $(LabelElement.class).id(REGISTER_HEADER_LABEL_ID);
  }

  protected ButtonElement registerButton() {
    return $(ButtonElement.class).id(REGISTER_BUTTON_ID);
  }

  protected void clickRegister() {
    registerButton().click();
  }

  protected LabelElement requiredLabel() {
    return $(LabelElement.class).id(REQUIRED_LABEL_ID);
  }
}
