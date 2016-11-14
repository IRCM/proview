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

package ca.qc.ircm.proview.test.config;

import static ca.qc.ircm.proview.web.Menu.HOME_STYLE;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Additional functions for TestBenchTestCase.
 */
public abstract class AbstractTestBenchTestCase extends TestBenchTestCase {
  private static final long MAX_WAIT = 500;
  private static final long SINGLE_WAIT = 100;
  @Value("http://localhost:${local.server.port}")
  protected String baseUrl;

  protected String viewUrl(String view) {
    return baseUrl + "/#!" + view;
  }

  /**
   * This method loads a page and fixes the issue with get method returning too quickly.
   *
   * @param view
   *          view to load
   */
  public void openView(String view) {
    String url = viewUrl(view);
    if (url.equals(getDriver().getCurrentUrl())) {
      getDriver().navigate().refresh();
    } else {
      getDriver().get(url);
    }
    waitForPageLoad();
  }

  protected Locale currentLocale() {
    MenuBarElement menuBar = $(MenuBarElement.class).first();
    WebElement home = menuBar.findElement(className("v-menubar-menuitem-" + HOME_STYLE));
    Set<Locale> locales = WebConstants.getLocales();
    return locales.stream().filter(locale -> new MessageResource(Menu.class, locale)
        .message(HOME_STYLE).equals(home.getText())).findAny().orElse(Locale.ENGLISH);
  }

  protected MessageResource resources(Class<?> baseClass) {
    return new MessageResource(baseClass, currentLocale());
  }

  public void waitForPageLoad() {
    findElement(By.className("v-loading-indicator"));
  }

  public void waitForNotificationCaption(NotificationElement notification) {
    waitFor(() -> !notification.getCaption().isEmpty());
  }

  private void waitFor(Supplier<Boolean> condition) {
    long totalWait = 0;
    try {
      while (!condition.get() && totalWait < MAX_WAIT) {
        Thread.sleep(SINGLE_WAIT);
        totalWait += SINGLE_WAIT;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  protected Optional<WebElement> findOptionalElement(By by) {
    try {
      return Optional.of(findElement(by));
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }

  protected Optional<WebElement> optionalElement(Supplier<WebElement> supplier) {
    try {
      return Optional.of(supplier.get());
    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }

  protected boolean getCheckBoxValue(CheckBoxElement field) {
    String value = field.getValue();
    return value.equals("checked");
  }

  protected void setCheckBoxValue(CheckBoxElement field, boolean value) {
    if (value != getCheckBoxValue(field)) {
      field.findElement(tagName("label")).click();
    }
  }

  protected void setOptionValue(OptionGroupElement field, String value) {
    Optional<WebElement> valueField = field.findElements(className("v-select-option")).stream()
        .map(option -> option.findElement(tagName("label")))
        .filter(label -> value.equals(label.getText())).findFirst();
    if (valueField.isPresent()) {
      valueField.get().click();
    }
  }
}
