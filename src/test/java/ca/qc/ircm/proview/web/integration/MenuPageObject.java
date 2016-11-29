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

import static ca.qc.ircm.proview.web.Menu.ACCESS_STYLE;
import static ca.qc.ircm.proview.web.Menu.CHANGE_LANGUAGE_STYLE;
import static ca.qc.ircm.proview.web.Menu.HELP_STYLE;
import static ca.qc.ircm.proview.web.Menu.HOME_STYLE;
import static ca.qc.ircm.proview.web.Menu.MANAGER_STYLE;
import static ca.qc.ircm.proview.web.Menu.PROFILE_STYLE;
import static ca.qc.ircm.proview.web.Menu.SIGNOUT_STYLE;
import static ca.qc.ircm.proview.web.Menu.SUBMISSION_STYLE;
import static ca.qc.ircm.proview.web.Menu.VALIDATE_USERS_STYLE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.testbench.elements.MenuBarElement;
import org.openqa.selenium.WebElement;

public abstract class MenuPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MainView.VIEW_NAME);
  }

  private WebElement menuItemByStyle(String className) {
    return findElement(className("v-menubar-menuitem-" + className));
  }

  protected MenuBarElement menu() {
    return $(MenuBarElement.class).first();
  }

  protected WebElement homeMenuItem() {
    return menuItemByStyle(HOME_STYLE);
  }

  protected void clickHome() {
    homeMenuItem().click();
  }

  protected WebElement submissionMenuItem() {
    return menuItemByStyle(SUBMISSION_STYLE);
  }

  protected void clickSubmission() {
    submissionMenuItem().click();
  }

  protected WebElement profileMenuItem() {
    return menuItemByStyle(PROFILE_STYLE);
  }

  protected void clickProfile() {
    profileMenuItem().click();
  }

  protected WebElement signoutMenuItem() {
    return menuItemByStyle(SIGNOUT_STYLE);
  }

  protected void clickSignout() {
    signoutMenuItem().click();
  }

  protected WebElement changeLanguageMenuItem() {
    return menuItemByStyle(CHANGE_LANGUAGE_STYLE);
  }

  protected void clickChangeLanguage() {
    changeLanguageMenuItem().click();
  }

  protected WebElement managerMenuItem() {
    return menuItemByStyle(MANAGER_STYLE);
  }

  protected void clickManager() {
    managerMenuItem().click();
  }

  protected WebElement validateUsersMenuItem() {
    return menuItemByStyle(VALIDATE_USERS_STYLE);
  }

  protected void clickValidateUsers() {
    managerMenuItem().click();
    validateUsersMenuItem().click();
  }

  protected WebElement accessMenuItem() {
    return menuItemByStyle(ACCESS_STYLE);
  }

  protected void clickAccess() {
    managerMenuItem().click();
    accessMenuItem().click();
  }

  protected WebElement helpMenuItem() {
    return menuItemByStyle(HELP_STYLE);
  }

  protected void clickHelp() {
    helpMenuItem().click();
  }
}
