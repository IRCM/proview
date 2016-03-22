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

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.ApplicationConfigurationBean;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Provides server's URL for integration tests.
 */
public class IntegrationTestConfigurationRule implements TestRule {
  private static final Logger logger =
      LoggerFactory.getLogger(IntegrationTestConfigurationRule.class);
  private static final String USER_HOME_PROPERTY = "integration.user.home";
  private String userHome;

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        processUserHome();
        base.evaluate();
      }
    };
  }

  private void processUserHome() {
    userHome = System.getProperty("user.home");
    if (System.getProperty(USER_HOME_PROPERTY) == null) {
      logger.warn("{} system property is not defined, fallback to {}", USER_HOME_PROPERTY,
          userHome);
    } else {
      userHome = System.getProperty(USER_HOME_PROPERTY);
    }
  }

  public String getUserHome() {
    return userHome;
  }

  /**
   * Returns {@link ApplicationConfiguration} of the web container.
   *
   * @return {@link ApplicationConfiguration} of the web container
   * @throws ConfigurationException
   *           could not initialize ApplicationConfiguration
   * @throws IOException
   *           could not initialize ApplicationConfiguration
   * @throws URISyntaxException
   *           could not initialize ApplicationConfiguration
   */
  public ApplicationConfiguration getApplicationConfiguration()
      throws ConfigurationException, IOException, URISyntaxException {
    String realUserHome = System.getProperty("user.home");
    try {
      System.setProperty("user.home", userHome);
      return new ApplicationConfigurationBean(null);
    } finally {
      System.setProperty("user.home", realUserHome);
    }
  }
}
