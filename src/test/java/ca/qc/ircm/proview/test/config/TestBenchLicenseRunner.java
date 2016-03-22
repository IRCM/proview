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

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.Statement;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * Skips tests if no test bench license is found.
 */
public class TestBenchLicenseRunner extends BlockJUnit4ClassRunner {
  private static final String LICENSE_ERROR_MESSAGE =
      "License for Vaadin TestBench not found. Skipping test class {0} .";
  private static final String[] LICENSE_PATHS =
      new String[] { "vaadin.testbench.developer.license", ".vaadin.testbench.developer.license" };
  private static final String LICENSE_SYSTEM_PROPERTY = "vaadin.testbench.developer.license";
  private final Class<?> testClass;

  public TestBenchLicenseRunner(Class<?> testClass) throws Throwable {
    super(testClass);
    this.testClass = testClass;
  }

  @Override
  protected Statement classBlock(RunNotifier notifier) {
    before();
    return super.classBlock(notifier);
  }

  protected void before() {
    boolean licenseFileExists = false;
    for (String licencePath : LICENSE_PATHS) {
      licenseFileExists |=
          Files.exists(Paths.get(System.getProperty("user.home")).resolve(licencePath));
    }
    assumeTrue(MessageFormat.format(LICENSE_ERROR_MESSAGE, testClass.getName()),
        licenseFileExists || System.getProperty(LICENSE_SYSTEM_PROPERTY) != null);
  }
}
