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

import static ca.qc.ircm.proview.web.MenuPresenter.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.MenuPresenter.CONTACT;
import static ca.qc.ircm.proview.web.MenuPresenter.CONTROL;
import static ca.qc.ircm.proview.web.MenuPresenter.GUIDELINES;
import static ca.qc.ircm.proview.web.MenuPresenter.HOME;
import static ca.qc.ircm.proview.web.MenuPresenter.PLATE;
import static ca.qc.ircm.proview.web.MenuPresenter.PROFILE;
import static ca.qc.ircm.proview.web.MenuPresenter.SIGNIN;
import static ca.qc.ircm.proview.web.MenuPresenter.SIGNOUT;
import static ca.qc.ircm.proview.web.MenuPresenter.STOP_SIGN_AS;
import static ca.qc.ircm.proview.web.MenuPresenter.SUBMISSION;
import static ca.qc.ircm.proview.web.MenuPresenter.USERS;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.proview.user.web.UsersViewPresenter;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.MenuBarElement;
import java.util.stream.IntStream;
import org.openqa.selenium.WebElement;

public abstract class MenuPageObject extends AbstractTestBenchTestCase {
  private static final int EMAIL_COLUMN = 0;

  private WebElement menuItemByStyle(String className) {
    return findElement(className("v-menubar-menuitem-" + className));
  }

  protected MenuBarElement menu() {
    return $(MenuBarElement.class).first();
  }

  protected WebElement homeMenuItem() {
    return menuItemByStyle(HOME);
  }

  protected void clickHome() {
    homeMenuItem().click();
  }

  protected WebElement submissionMenuItem() {
    return menuItemByStyle(SUBMISSION);
  }

  protected void clickSubmission() {
    submissionMenuItem().click();
  }

  protected WebElement plateMenuItem() {
    return menuItemByStyle(PLATE);
  }

  protected void clickPlate() {
    plateMenuItem().click();
  }

  protected WebElement controlMenuItem() {
    return menuItemByStyle(CONTROL);
  }

  protected void clickControl() {
    controlMenuItem().click();
  }

  protected WebElement profileMenuItem() {
    return menuItemByStyle(PROFILE);
  }

  protected void clickProfile() {
    profileMenuItem().click();
  }

  protected WebElement signoutMenuItem() {
    return menuItemByStyle(SIGNOUT);
  }

  protected void clickSignout() {
    signoutMenuItem().click();
  }

  protected WebElement changeLanguageMenuItem() {
    return menuItemByStyle(CHANGE_LANGUAGE);
  }

  protected void clickChangeLanguage() {
    changeLanguageMenuItem().click();
  }

  protected WebElement usersMenuItem() {
    return menuItemByStyle(USERS);
  }

  protected void clickManager() {
    usersMenuItem().click();
  }

  protected WebElement contactMenuItem() {
    return menuItemByStyle(CONTACT);
  }

  protected void clickContact() {
    contactMenuItem().click();
  }

  protected WebElement guidelines() {
    return menuItemByStyle(GUIDELINES);
  }

  protected void clickGuidelines() {
    guidelines().click();
  }

  protected WebElement signin() {
    return menuItemByStyle(SIGNIN);
  }

  protected void clickSignin() {
    signin().click();
  }

  protected void signas(String email) {
    openView(UsersView.VIEW_NAME);
    GridElement usersGrid =
        wrap(GridElement.class, mainContent().findElement(className(UsersViewPresenter.USERS)));
    IntStream.range(0, (int) usersGrid.getRowCount())
        .filter(row -> email.equals(usersGrid.getCell(row, EMAIL_COLUMN).getText())).findFirst()
        .ifPresent(row -> {
          usersGrid.getRow(row).click();
        });
    ButtonElement button =
        wrap(ButtonElement.class, findElement(className(UsersViewPresenter.SWITCH_USER)));
    button.click();
  }

  protected WebElement stopSignasMenuItem() {
    return menuItemByStyle(STOP_SIGN_AS);
  }

  protected void clickStopSignas() {
    stopSignasMenuItem().click();
  }
}
