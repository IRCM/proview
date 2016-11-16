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

import static ca.qc.ircm.proview.web.Menu.CHANGE_LANGUAGE_STYLE;
import static ca.qc.ircm.proview.web.Menu.HELP_STYLE;
import static ca.qc.ircm.proview.web.Menu.HOME_STYLE;
import static ca.qc.ircm.proview.web.Menu.MANAGER_STYLE;
import static ca.qc.ircm.proview.web.Menu.SUBMISSION_STYLE;
import static ca.qc.ircm.proview.web.Menu.VALIDATE_USERS_STYLE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.testbench.elements.MenuBarElement;

public abstract class MenuPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MainView.VIEW_NAME);
  }

  private void clickMenuItemByStyle(String className) {
    findElement(className("v-menubar-menuitem-" + className)).click();
  }

  protected MenuBarElement menu() {
    return $(MenuBarElement.class).first();
  }

  protected void clickHome() {
    clickMenuItemByStyle(HOME_STYLE);
  }

  protected void clickSubmission() {
    clickMenuItemByStyle(SUBMISSION_STYLE);
  }

  protected void clickChangeLanguage() {
    clickMenuItemByStyle(CHANGE_LANGUAGE_STYLE);
  }

  protected void clickValidateUsers() {
    clickMenuItemByStyle(MANAGER_STYLE);
    clickMenuItemByStyle(VALIDATE_USERS_STYLE);
  }

  protected void clickHelp() {
    clickMenuItemByStyle(HELP_STYLE);
  }
}
