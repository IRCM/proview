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
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

/**
 * Creates entity manager for tests.
 */
public class EntityManagerRule implements TestRule {
  private static final Logger logger = LoggerFactory.getLogger(EntityManagerRule.class);
  private javax.persistence.EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;
  private DataSource dataSource;

  /**
   * Creates entity manager for tests.
   *
   * @param dataSource
   *          data source
   */
  public EntityManagerRule(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        logger.trace("Creating entity manager");
        LocalContainerEntityManagerFactoryBean factory;
        factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("proview");
        factory.setDataSource(dataSource);
        factory.afterPropertiesSet();
        entityManagerFactory = factory.getObject();
        entityManager = entityManagerFactory.createEntityManager();
        try {
          EntityTransaction transaction = entityManager.getTransaction();
          transaction.begin();
          try {
            base.evaluate();
            try {
              entityManager.flush();
            } catch (PersistenceException e) {
              // Ignore.
            }
          } finally {
            if (transaction.getRollbackOnly()) {
              transaction.rollback();
            } else {
              transaction.commit();
            }
          }
        } finally {
          entityManager.close();
          factory.destroy();
        }
      }
    };
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }
}
