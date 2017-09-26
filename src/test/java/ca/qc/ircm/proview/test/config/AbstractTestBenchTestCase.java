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

import static ca.qc.ircm.proview.web.Menu.HOME;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import com.google.common.base.Predicate;

import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.MenuBarElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Additional functions for TestBenchTestCase.
 */
public abstract class AbstractTestBenchTestCase extends TestBenchTestCase
    implements SeleniumDriverTypePredicate {
  private static final Logger logger = LoggerFactory.getLogger(AbstractTestBenchTestCase.class);
  @Value("http://localhost:${local.server.port}")
  protected String baseUrl;

  protected String homeUrl() {
    return baseUrl + "/";
  }

  protected String viewUrl(String view) {
    return baseUrl + "/#!" + view;
  }

  protected String viewUrl(String view, String parameters) {
    return baseUrl + "/#!" + view + "/" + parameters;
  }

  protected void openView(String view) {
    openView(view, null);
  }

  protected void openView(String view, String parameters) {
    String url = viewUrl(view);
    if (parameters != null && !parameters.isEmpty()) {
      url += "/" + parameters;
    }
    if (url.equals(getDriver().getCurrentUrl())) {
      getDriver().navigate().refresh();
    } else {
      getDriver().get(url);
    }
  }

  protected <T> void waitFor(Supplier<T> supplier) {
    waitFor(supplier, 5);
  }

  protected <T> void waitFor(Supplier<T> supplier, long maxWaitMillis) {
    new WebDriverWait(getDriver(), maxWaitMillis)
        .until((Predicate<WebDriver>) driver -> optional(() -> supplier.get()).isPresent());
  }

  protected Locale currentLocale() {
    MenuBarElement menuBar = $(MenuBarElement.class).first();
    WebElement home = menuBar.findElement(className("v-menubar-menuitem-" + HOME));
    Set<Locale> locales = WebConstants.getLocales();
    return locales.stream()
        .filter(
            locale -> new MessageResource(Menu.class, locale).message(HOME).equals(home.getText()))
        .findAny().orElse(Locale.ENGLISH);
  }

  protected MessageResource resources(Class<?> baseClass) {
    return new MessageResource(baseClass, currentLocale());
  }

  protected <T> Optional<T> optional(Supplier<T> supplier) {
    try {
      return Optional.of(supplier.get());
    } catch (Throwable e) {
      return Optional.empty();
    }
  }

  protected void uploadFile(WebElement uploader, Path file) {
    logger.debug("Uploading file {} to uploader {} with class {}", file,
        uploader.getAttribute("id"), uploader.getAttribute("class"));
    if (isPhantomjsDriver(driver)) {
      String selector =
          "." + uploader.getAttribute("class").replaceAll(" ", ".") + " .gwt-FileUpload";
      ((PhantomJSDriver) actualDriver(driver)).executePhantomJS(
          "var page = this; page.uploadFile('" + selector + "', '" + file.toAbsolutePath() + "');");
    } else {
      WebElement input = uploader.findElement(className("gwt-FileUpload"));
      input.sendKeys(file.toAbsolutePath().toString());
    }
  }

  protected IntStream gridRows(GridElement grid) {
    int row = 0;
    try {
      while (true) {
        grid.getRow(row);
        row++;
      }
    } catch (NoSuchElementException e) {
      // No more rows.
    }
    return IntStream.range(0, row);
  }

  protected void processGridRows(GridElement grid, Consumer<Integer> consumer) {
    int row = 0;
    try {
      while (true) {
        grid.getRow(row);
        consumer.accept(row);
        row++;
      }
    } catch (NoSuchElementException e) {
      // No more rows.
    }
  }

  // Workaround for Vaadin referring to wrong element when doing isSelected test.
  protected boolean getCheckBoxValue(CheckBoxElement field) {
    String value = field.getValue();
    return value.equals("checked");
  }

  // Workaround for Vaadin referring to wrong element when doing click.
  protected void setCheckBoxValue(CheckBoxElement field, boolean value) {
    if (value != getCheckBoxValue(field)) {
      field.findElement(tagName("label")).click();
    }
  }
}
