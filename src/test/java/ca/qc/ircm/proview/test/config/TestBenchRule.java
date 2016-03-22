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
import org.junit.rules.ExternalResource;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Rule for integration tests using Vaadin's test bench.
 */
public class TestBenchRule extends ExternalResource {
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
  protected void before() throws Throwable {
    target.setDriver(new FirefoxDriver());
  }

  @Override
  protected void after() {
    if (quitAfterTest) {
      target.getDriver().quit();
    }
  }
}
