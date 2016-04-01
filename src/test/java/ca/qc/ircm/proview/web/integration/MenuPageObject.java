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

import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.MenuBarElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.Locale;

public abstract class MenuPageObject extends TestBenchTestCase {
  private Locale[] locales = new Locale[] { Locale.ENGLISH, Locale.FRENCH };

  protected abstract String getBaseUrl();

  protected void open() {
    getDriver().get(getBaseUrl() + "#!" + MainView.VIEW_NAME);
  }

  protected void open(String view) {
    getDriver().get(getBaseUrl() + "#!" + view);
  }

  private MessageResource getResources(Locale locale) {
    return new MessageResource(Menu.class, locale);
  }

  private void clickMenuItem(String itemKey) {
    try {
      for (Locale locale : locales) {
        String item = getResources(locale).message(itemKey);
        try {
          menu().clickItem(item);
        } catch (NoSuchElementException e) {
          // Wrong locale.
        }
      }
    } catch (StaleElementReferenceException e) {
      // Thrown when page changes.
    }
  }

  protected MenuBarElement menu() {
    return $(MenuBarElement.class).first();
  }

  protected void clickHome() {
    clickMenuItem("home");
  }

  protected void clickChangeLanguage() {
    clickMenuItem("changeLanguage");
  }

  protected void clickValidateUsers() {
    clickMenuItem("manager");
    clickMenuItem("validateUsers");
  }

  protected void clickHelp() {
    clickMenuItem("help");
  }
}
