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

import static org.junit.Assume.assumeTrue;

import com.vaadin.testbench.TestBenchTestCase;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Rule for integration tests using Vaadin's test bench.
 */
@Order(TestBenchTestExecutionListener.ORDER)
public class TestBenchTestExecutionListener extends AbstractTestExecutionListener {
  public static final int ORDER = 0;
  private static final String LICENSE_ERROR_MESSAGE =
      "License for Vaadin TestBench not found. Skipping test class {0} .";
  private static final String[] LICENSE_PATHS =
      new String[] { "vaadin.testbench.developer.license", ".vaadin.testbench.developer.license" };
  private static final String LICENSE_SYSTEM_PROPERTY = "vaadin.testbench.developer.license";
  private static final Logger logger =
      LoggerFactory.getLogger(TestBenchTestExecutionListener.class);

  @Override
  public void beforeTestClass(TestContext testContext) throws Exception {
    if (isTestBenchTest(testContext)) {
      boolean licenseFileExists = false;
      for (String licencePath : LICENSE_PATHS) {
        licenseFileExists |=
            Files.exists(Paths.get(System.getProperty("user.home")).resolve(licencePath));
      }
      String message =
          MessageFormat.format(LICENSE_ERROR_MESSAGE, testContext.getTestClass().getName());
      logger.info(message);
      assumeTrue(message, licenseFileExists || System.getProperty(LICENSE_SYSTEM_PROPERTY) != null);
    }
  }

  @Override
  public void beforeTestMethod(TestContext testContext) throws Exception {
    if (isTestBenchTest(testContext)) {
      WebDriver driver = new FirefoxDriver();
      TestBenchTestCase target = getInstance(testContext);
      target.setDriver(driver);
    }
  }

  @Override
  public void afterTestMethod(TestContext testContext) throws Exception {
    if (isTestBenchTest(testContext)) {
      TestBenchTestCase target = getInstance(testContext);
      target.getDriver().manage().deleteAllCookies();
      target.getDriver().quit();
    }
  }

  private boolean isTestBenchTest(TestContext testContext) {
    return TestBenchTestCase.class.isAssignableFrom(testContext.getTestClass());
  }

  private TestBenchTestCase getInstance(TestContext testContext) {
    return (TestBenchTestCase) testContext.getTestInstance();
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
