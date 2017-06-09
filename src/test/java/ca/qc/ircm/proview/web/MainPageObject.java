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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD;
import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.FORGOT_PASSWORD_EMAIL;
import static ca.qc.ircm.proview.web.MainViewPresenter.HEADER;
import static ca.qc.ircm.proview.web.MainViewPresenter.REGISTER_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_BUTTON;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_PANEL;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_PASSWORD;
import static ca.qc.ircm.proview.web.MainViewPresenter.SIGN_USERNAME;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextFieldElement;

public abstract class MainPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MainView.VIEW_NAME);
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement signPanel() {
    return wrap(PanelElement.class, findElement(className(SIGN_PANEL)));
  }

  protected TextFieldElement signUsernameField() {
    return wrap(TextFieldElement.class, findElement(className(SIGN_USERNAME)));
  }

  protected String getSignUsername() {
    return signUsernameField().getValue();
  }

  protected void setSignUsername(String value) {
    signUsernameField().setValue(value);
  }

  protected TextFieldElement signPasswordField() {
    return wrap(TextFieldElement.class, findElement(className(SIGN_PASSWORD)));
  }

  protected String getSignPassword() {
    return signPasswordField().getValue();
  }

  protected void setSignPassword(String value) {
    signPasswordField().setValue(value);
  }

  protected ButtonElement signButton() {
    return wrap(ButtonElement.class, findElement(className(SIGN_BUTTON)));
  }

  protected void clickSignButton() {
    signButton().click();
  }

  protected LabelElement forgotPasswordPanel() {
    return wrap(LabelElement.class, findElement(className(FORGOT_PASSWORD)));
  }

  protected TextFieldElement forgotPasswordEmailField() {
    return wrap(TextFieldElement.class, findElement(className(FORGOT_PASSWORD_EMAIL)));
  }

  protected String getForgotPasswordEmail() {
    return forgotPasswordEmailField().getValue();
  }

  protected void setForgotPasswordEmail(String value) {
    forgotPasswordEmailField().setValue(value);
  }

  protected ButtonElement forgotPasswordButton() {
    return wrap(ButtonElement.class, findElement(className(FORGOT_PASSWORD_BUTTON)));
  }

  protected void clickForgotPasswordButton() {
    forgotPasswordButton().click();
  }

  protected ButtonElement registerButton() {
    return wrap(ButtonElement.class, findElement(className(REGISTER_BUTTON)));
  }

  protected void clickRegisterButton() {
    registerButton().click();
  }
}
