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

import static com.vaadin.testbench.By.vaadin;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.user.web.RegisterView;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import org.openqa.selenium.WebElement;

import java.util.Optional;

public abstract class RegisterPageObject extends TestBenchTestCase {
  protected abstract String getBaseUrl();

  protected void open() {
    getDriver().get(getBaseUrl() + "/#!" + RegisterView.VIEW_NAME);
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
    return $(LabelElement.class).id("headerLabel");
  }

  protected TextFieldElement emailField() {
    return $(TextFieldElement.class).id("email");
  }

  protected String getEmail() {
    return emailField().getValue();
  }

  protected void setEmail(String value) {
    emailField().setValue(value);
  }

  protected TextFieldElement nameField() {
    return $(TextFieldElement.class).id("name");
  }

  protected String getName() {
    return nameField().getValue();
  }

  protected void setName(String value) {
    nameField().setValue(value);
  }

  protected PasswordFieldElement passwordField() {
    return $(PasswordFieldElement.class).id("password");
  }

  protected String getPassword() {
    return passwordField().getValue();
  }

  protected void setPassword(String value) {
    passwordField().setValue(value);
  }

  protected PasswordFieldElement confirmPasswordField() {
    return $(PasswordFieldElement.class).id("confirmPassword");
  }

  protected String getConfirmPassword() {
    return confirmPasswordField().getValue();
  }

  protected void setConfirmPassword(String value) {
    confirmPasswordField().setValue(value);
  }

  protected LabelElement laboratoryHeaderLabel() {
    return $(LabelElement.class).id("laboratoryHeaderLabel");
  }

  protected CheckBoxElement newLaboratoryField() {
    return $(CheckBoxElement.class).id("newLaboratory");
  }

  protected boolean isNewLaboratory() {
    return getCheckBoxValue(newLaboratoryField());
  }

  protected void setNewLaboratory(boolean value) {
    setCheckBoxValue(newLaboratoryField(), value);
  }

  protected TextFieldElement organizationField() {
    return $(TextFieldElement.class).id("organization");
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
    return $(TextFieldElement.class).id("laboratoryName");
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
    return $(TextFieldElement.class).id("managerEmail");
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

  protected LabelElement addressHeaderLabel() {
    return $(LabelElement.class).id("addressHeaderLabel");
  }

  protected TextFieldElement addressField() {
    return $(TextFieldElement.class).id("address");
  }

  protected String getAddress() {
    return addressField().getValue();
  }

  protected void setAddress(String value) {
    addressField().setValue(value);
  }

  protected TextFieldElement addressSecondField() {
    return $(TextFieldElement.class).id("addressSecond");
  }

  protected String getAddressSecond() {
    return addressSecondField().getValue();
  }

  protected void setAddressSecond(String value) {
    addressSecondField().setValue(value);
  }

  protected TextFieldElement townField() {
    return $(TextFieldElement.class).id("town");
  }

  protected String getTown() {
    return townField().getValue();
  }

  protected void setTown(String value) {
    townField().setValue(value);
  }

  protected TextFieldElement stateField() {
    return $(TextFieldElement.class).id("state");
  }

  protected String getState() {
    return stateField().getValue();
  }

  protected void setState(String value) {
    stateField().setValue(value);
  }

  protected ComboBoxElement countryField() {
    return $(ComboBoxElement.class).id("country");
  }

  protected String getCountry() {
    return countryField().getValue();
  }

  /**
   * Sets country.<br>
   * Export format must be a country defined in proview.ini file or field will not change.
   *
   * @param value
   *          country
   */
  protected void setCountry(String value) {
    ComboBoxElement field = countryField();
    field.openPopup();
    WebElement popup = field.findElement(vaadin("#popup"));
    Optional<WebElement> valueField =
        popup.findElements(tagName("td")).stream().map(td -> td.findElement(tagName("span")))
            .filter(span -> value.equals(span.getText())).findFirst();
    if (valueField.isPresent()) {
      valueField.get().click();
    }
  }

  protected TextFieldElement postalCodeField() {
    return $(TextFieldElement.class).id("postalCode");
  }

  protected String getPostalCode() {
    return postalCodeField().getValue();
  }

  protected void setPostalCode(String value) {
    postalCodeField().setValue(value);
  }

  protected ButtonElement clearAddressButton() {
    return $(ButtonElement.class).id("clearAddress");
  }

  protected void clickClearAddress() {
    clearAddressButton().click();
  }

  protected LabelElement phoneNumberHeaderLabel() {
    return $(LabelElement.class).id("phoneNumberHeaderLabel");
  }

  protected TextFieldElement phoneNumberField() {
    return $(TextFieldElement.class).id("phoneNumber");
  }

  protected String getPhoneNumber() {
    return phoneNumberField().getValue();
  }

  protected void setPhoneNumber(String value) {
    phoneNumberField().setValue(value);
  }

  protected TextFieldElement phoneExtensionField() {
    return $(TextFieldElement.class).id("phoneExtension");
  }

  protected String getPhoneExtension() {
    return phoneExtensionField().getValue();
  }

  protected void setPhoneExtension(String value) {
    phoneExtensionField().setValue(value);
  }

  protected LabelElement registerHeaderLabel() {
    return $(LabelElement.class).id("registerHeaderLabel");
  }

  protected ButtonElement registerButton() {
    return $(ButtonElement.class).id("register");
  }

  protected void clickRegister() {
    registerButton().click();
  }

  protected LabelElement requiredLabel() {
    return $(LabelElement.class).id("requiredLabel");
  }
}
