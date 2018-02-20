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

import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.CONFIRM_PASSWORD;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.PASSWORD;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.PASSWORD_PANEL;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.SAVE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.ForgotPasswordView;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.PasswordFieldElement;

public abstract class ForgotPasswordPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(ForgotPasswordView.VIEW_NAME + "/9/174407008");
  }

  protected MenuBarElement menu() {
    return $(MenuBarElement.class).first();
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement passwordPanel() {
    return wrap(PanelElement.class, findElement(className(PASSWORD_PANEL)));
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

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    saveButton().click();
  }
}
