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

package ca.qc.ircm.proview.web.integration;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.FormElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;

public abstract class MainPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MainView.VIEW_NAME);
  }

  protected LabelElement header() {
    return $(LabelElement.class).id("header");
  }

  protected FormElement signForm() {
    return $(FormElement.class).id("signForm");
  }

  protected LabelElement signFormHeader() {
    return $(LabelElement.class).id("loginForm-header");
  }

  protected TextFieldElement signFormUsernameField() {
    return wrap(TextFieldElement.class,
        findElement(org.openqa.selenium.By.id("loginForm-userNameField")));
  }

  protected String getSignFormUsername() {
    return signFormUsernameField().getValue();
  }

  protected void setSignFormUsername(String value) {
    signFormUsernameField().setValue(value);
  }

  protected TextFieldElement signFormPasswordField() {
    return wrap(TextFieldElement.class,
        findElement(org.openqa.selenium.By.id("loginForm-passwordField")));
  }

  protected String getSignFormPassword() {
    return signFormPasswordField().getValue();
  }

  protected void setSignFormPassword(String value) {
    signFormPasswordField().setValue(value);
  }

  protected ButtonElement signFormSignButton() {
    return $(ButtonElement.class).id("loginForm-loginButton");
  }

  protected void clickSignFormSignButton() {
    signFormSignButton().click();
  }

  protected LabelElement forgotPasswordHeader() {
    return $(LabelElement.class).id("forgotPasswordHeader");
  }

  protected TextFieldElement forgotPasswordEmailField() {
    return $(TextFieldElement.class).id("forgotPasswordEmailField");
  }

  protected String getForgotPasswordEmail() {
    return forgotPasswordEmailField().getValue();
  }

  protected void setForgotPasswordEmail(String value) {
    forgotPasswordEmailField().setValue(value);
  }

  protected ButtonElement forgotPasswordButton() {
    return $(ButtonElement.class).id("forgotPasswordButton");
  }

  protected void clickForgotPasswordButton() {
    forgotPasswordButton().click();
  }

  protected LabelElement registerHeader() {
    return $(LabelElement.class).id("registerHeader");
  }

  protected ButtonElement registerButton() {
    return $(ButtonElement.class).id("registerButton");
  }

  protected void clickRegisterButton() {
    registerButton().click();
  }
}
