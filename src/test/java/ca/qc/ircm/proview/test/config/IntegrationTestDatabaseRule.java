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
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Connects to H2 database before test.
 */
public class IntegrationTestDatabaseRule implements TestRule {
  private static final Logger logger = LoggerFactory.getLogger(IntegrationTestDatabaseRule.class);
  private static final String DRIVER = "org.h2.Driver";
  private static final String URL_PROPERTY = "database.url";
  private static final String URL_FALLBACK = "jdbc:h2:tcp://localhost/mem:proview";
  private static final String USERNAME = "sa";
  private static final String PASSWORD = "";
  private String databaseUrl;
  private EntityManagerRule entityManagerRule;

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        findDatabaseUrl();
        DataSource dataSource =
            TestDataSourceFactory.createDataSource(DRIVER, databaseUrl, USERNAME, PASSWORD);
        cleanDatabase(dataSource);
        entityManagerRule = new EntityManagerRule(dataSource);
        final Statement entityManagerStatement = entityManagerRule.apply(base, description);
        entityManagerStatement.evaluate();
      }
    };
  }

  private void findDatabaseUrl() {
    if (System.getProperty(URL_PROPERTY) != null) {
      databaseUrl = System.getProperty(URL_PROPERTY);
    } else {
      logger.warn("{} system property is not defined, fallback to {}", URL_PROPERTY, URL_FALLBACK);
      databaseUrl = URL_FALLBACK;
    }
  }

  private void cleanDatabase(DataSource dataSource) throws SQLException {
    logger.debug("Cleaning database at {}", databaseUrl);
    try (Connection connection = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(connection, new ClassPathResource("database.sql"));
    }
  }

  public EntityManager getEntityManager() {
    return entityManagerRule.getEntityManager();
  }
}
