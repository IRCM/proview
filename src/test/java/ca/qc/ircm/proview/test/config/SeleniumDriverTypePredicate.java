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

import com.vaadin.testbench.TestBenchDriverProxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Test selenium driver type.
 */
public class SeleniumDriverTypePredicate {
  /**
   * Returns actual selenium driver.
   *
   * @param driver
   *          driver
   * @return actual selenium driver
   */
  public static WebDriver actualDriver(WebDriver driver) {
    if (driver instanceof TestBenchDriverProxy) {
      driver = ((TestBenchDriverProxy) driver).getActualDriver();
    }
    return driver;
  }

  public static boolean isPhantomjsDriver(WebDriver driver) {
    return actualDriver(driver) instanceof PhantomJSDriver;
  }

  public static boolean isChromeDriver(WebDriver driver) {
    return actualDriver(driver) instanceof ChromeDriver;
  }

  public static boolean isFirefoxDriver(WebDriver driver) {
    return actualDriver(driver) instanceof FirefoxDriver;
  }
}
