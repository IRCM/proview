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

import static ca.qc.ircm.proview.user.web.SignasViewPresenter.USERS_GRID;
import static ca.qc.ircm.proview.web.Menu.ACCESS;
import static ca.qc.ircm.proview.web.Menu.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.Menu.CONTACT;
import static ca.qc.ircm.proview.web.Menu.HELP;
import static ca.qc.ircm.proview.web.Menu.HOME;
import static ca.qc.ircm.proview.web.Menu.MANAGER;
import static ca.qc.ircm.proview.web.Menu.PROFILE;
import static ca.qc.ircm.proview.web.Menu.REGISTER;
import static ca.qc.ircm.proview.web.Menu.SIGNOUT;
import static ca.qc.ircm.proview.web.Menu.SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.STOP_SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.SUBMISSION;
import static ca.qc.ircm.proview.web.Menu.VALIDATE_USERS;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.SignasView;
import ca.qc.ircm.proview.user.web.SignasViewPresenter;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.MenuBarElement;
import org.openqa.selenium.WebElement;

public abstract class MenuPageObject extends AbstractTestBenchTestCase {
  private static final int EMAIL_COLUMN = 0;
  private static final int SIGN_AS_COLUMN = 4;

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

  protected WebElement managerMenuItem() {
    return menuItemByStyle(MANAGER);
  }

  protected void clickManager() {
    managerMenuItem().click();
  }

  protected WebElement validateUsersMenuItem() {
    return menuItemByStyle(VALIDATE_USERS);
  }

  protected void clickValidateUsers() {
    managerMenuItem().click();
    validateUsersMenuItem().click();
  }

  protected WebElement accessMenuItem() {
    return menuItemByStyle(ACCESS);
  }

  protected void clickAccess() {
    managerMenuItem().click();
    accessMenuItem().click();
  }

  protected WebElement signasMenuItem() {
    return menuItemByStyle(SIGN_AS);
  }

  protected void clickSignas() {
    managerMenuItem().click();
    signasMenuItem().click();
  }

  protected WebElement registerMenuItem() {
    return menuItemByStyle(REGISTER);
  }

  protected void clickRegister() {
    managerMenuItem().click();
    registerMenuItem().click();
  }

  protected WebElement stopSignasMenuItem() {
    return menuItemByStyle(STOP_SIGN_AS);
  }

  protected void clickStopSignas() {
    managerMenuItem().click();
    stopSignasMenuItem().click();
  }

  protected WebElement contactMenuItem() {
    return menuItemByStyle(CONTACT);
  }

  protected void clickContact() {
    contactMenuItem().click();
  }

  protected WebElement helpMenuItem() {
    return menuItemByStyle(HELP);
  }

  protected void clickHelp() {
    helpMenuItem().click();
  }

  protected void signas(String email) {
    openView(SignasView.VIEW_NAME);
    GridElement usersGrid = wrap(GridElement.class, findElement(className(USERS_GRID)));
    processGridRows(usersGrid, row -> {
      GridCellElement emailCell = usersGrid.getCell(row, EMAIL_COLUMN);
      try {
        if (email.equals(emailCell.getText())) {
          usersGrid.getCell(row, SIGN_AS_COLUMN);
          ButtonElement button = wrap(ButtonElement.class,
              usersGrid.getRow(row).findElement(className(SignasViewPresenter.SIGN_AS)));
          button.click();
        }
      } catch (RuntimeException e) {
        throw e;
      }
    });
  }
}
