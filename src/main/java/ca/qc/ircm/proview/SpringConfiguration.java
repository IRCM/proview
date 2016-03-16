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

package ca.qc.ircm.proview;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * Configuration for Spring.
 */
@Configuration
@ComponentScan(
    basePackages = { "ca.qc.ircm.proview", "ca.qc.ircm.proview.*" },
    excludeFilters = @Filter(Controller.class) )
@EnableTransactionManagement
public class SpringConfiguration {
  /**
   * Returns configuration file location, if any.
   *
   * @return configuration file location, or null for default location
   */
  @Bean(name = "configuration")
  public String configuration() {
    JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
    factory.setJndiName("proteus.configuration");
    factory.setResourceRef(true);
    try {
      factory.afterPropertiesSet();
      return (String) factory.getObject();
    } catch (IllegalArgumentException | NamingException e) {
      return null;
    }
  }

  /**
   * Creates data source.
   *
   * @return data source
   */
  @Bean
  public DataSource dataSource() {
    JndiDataSourceLookup lookup = new JndiDataSourceLookup();
    lookup.setResourceRef(true);
    DataSource dataSource = lookup.getDataSource("proview");
    return dataSource;
  }

  /**
   * Creates entity manager factory.
   *
   * @return entity manager factory
   */
  @Bean
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
    factory.setPersistenceUnitName("proview");
    factory.setDataSource(dataSource());
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Bean
  public PlatformTransactionManager txManager() {
    return new JpaTransactionManager(entityManagerFactory());
  }
}
