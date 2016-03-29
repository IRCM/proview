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

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides server's URL for integration tests.
 */
public class IntegrationTestUrlRule implements TestRule {
  private static final Logger logger = LoggerFactory.getLogger(IntegrationTestUrlRule.class);
  private static final String BASE_URL_PROPERTY = "base.url";
  private static final String BASE_URL_FALLBACK = "http://localhost:8080/proview";
  private static final Pattern CONTEXT_PATH_PATTERN = Pattern.compile("http://[^/]+(/[^/\\?]+).*");

  public IntegrationTestUrlRule() {
    initBaseUrl();
  }

  private void initBaseUrl() {
    if (System.getProperty(BASE_URL_PROPERTY) == null) {
      logger.warn("{} system property is not defined, fallback to {}", BASE_URL_PROPERTY,
          BASE_URL_FALLBACK);
      System.setProperty(BASE_URL_PROPERTY, BASE_URL_FALLBACK);
    }
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return base;
  }

  public String getBaseUrl() {
    return System.getProperty(BASE_URL_PROPERTY);
  }

  /**
   * Returns context path of base URL.
   *
   * @return context path of base URL
   */
  public String getContextPath() {
    Matcher matcher = CONTEXT_PATH_PATTERN.matcher(getBaseUrl());
    if (matcher.matches()) {
      return matcher.group(1);
    } else {
      return null;
    }
  }
}
