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

import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.OptionGroupElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
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

  /**
   * Returns message specified by key in all locales.
   *
   * @param resources
   *          return resources for locale
   * @param key
   *          message key
   * @param replacements
   *          replacements
   */
  protected Set<String> message(Function<Locale, MessageResource> resources, String key,
      Object... replacements) {
    Set<Locale> locales = WebConstants.getLocales();
    Set<String> messages = new HashSet<>();
    for (Locale locale : locales) {
      messages.add(resources.apply(locale).message(key, replacements));
    }
    return messages;
  }

  protected Function<Locale, MessageResource> resources(Class<?> baseClass) {
    return (locale) -> new MessageResource(baseClass, locale);
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

  protected void setOptionValue(OptionGroupElement field, String value) {
    // TODO Fix API to support multi-language.
    Optional<WebElement> valueField = field.findElements(className("v-select-option")).stream()
        .map(option -> option.findElement(tagName("label")))
        .filter(label -> value.equals(label.getText())).findFirst();
    if (valueField.isPresent()) {
      valueField.get().click();
    }
  }
}
