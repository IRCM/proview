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

import com.vaadin.testbench.TestBenchTestCase;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rule for integration tests using Vaadin's test bench.
 */
public class TestBenchRule extends IntegrationTestUrlRule {
  private static final Logger logger = LoggerFactory.getLogger(TestBenchRule.class);
  private TestBenchTestCase target;
  private boolean quitAfterTest;

  public TestBenchRule(TestBenchTestCase target) {
    this(target, true);
  }

  public TestBenchRule(TestBenchTestCase target, boolean quitAfterTest) {
    this.target = target;
    this.quitAfterTest = quitAfterTest;
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        WebDriver driver = new FirefoxDriver();
        long userId = 1L;
        boolean anonymous = false;
        WithSubject withSubject = null;
        if (description.getAnnotation(WithSubject.class) != null) {
          withSubject = description.getAnnotation(WithSubject.class);
        } else if (description.getTestClass().getAnnotation(WithSubject.class) != null) {
          withSubject = description.getTestClass().getAnnotation(WithSubject.class);
        }
        if (withSubject != null) {
          userId = withSubject.userId();
          anonymous = withSubject.anonymous();
        }
        logger.debug("Set {} as user", userId);
        try {
          target.setDriver(driver);
          if (!anonymous) {
            driver.get(getBaseUrl());
            Cookie cookie = new Cookie("rememberMe", SubjectRule.rememberCookie(userId));
            driver.manage().addCookie(cookie);
          }
          base.evaluate();
        } finally {
          driver.manage().deleteAllCookies();
          if (quitAfterTest) {
            driver.quit();
          }
        }
      }
    };
  }
}
