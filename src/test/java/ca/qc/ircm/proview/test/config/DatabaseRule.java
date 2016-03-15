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
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * Cleans and rollback database when needed. <br>
 * TODO Remove tube and platespot tables from test database.
 */
public class DatabaseRule implements TestRule {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseRule.class);
  private static final String DRIVER = "org.h2.Driver";
  private static final String URL = "jdbc:h2:mem:testdb";
  private static final String USERNAME = "sa";
  private static final String PASSWORD = "";
  private EntityManagerRule entityManagerRule;

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        logger.trace("Creating database");
        EmbeddedDatabase database = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript("database.sql").build();
        DataSource dataSource =
            TestDataSourceFactory.createDataSource(DRIVER, URL, USERNAME, PASSWORD);
        entityManagerRule = new EntityManagerRule(dataSource);
        final Statement entityManagerStatement = entityManagerRule.apply(base, description);
        try {
          entityManagerStatement.evaluate();
        } finally {
          logger.trace("Dropping database");
          database.shutdown();
        }
      }
    };
  }

  public EntityManager getEntityManager() {
    return entityManagerRule.getEntityManager();
  }
}
