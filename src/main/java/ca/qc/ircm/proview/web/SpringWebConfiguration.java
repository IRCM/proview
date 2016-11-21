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

package ca.qc.ircm.proview.web;

import ca.qc.ircm.proview.logging.MdcFilter;
import ca.qc.ircm.proview.security.web.ShiroWebEnvironmentListener;
import ca.qc.ircm.proview.security.web.SignoutFilter;
import ca.qc.ircm.proview.user.SignedShiro;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.user.web.SignedFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.util.IntrospectorCleanupListener;

import javax.inject.Inject;

/**
 * Enable Spring Web MVC for REST services.
 */
@Configuration
public class SpringWebConfiguration extends WebMvcConfigurerAdapter {
  public static final String SHIRO_FILTER_NAME = "ShiroFilter";
  @Inject
  private UserService userService;

  @Bean(name = SHIRO_FILTER_NAME)
  public ShiroFilter shiroFilter() {
    return new ShiroFilter();
  }

  @Bean(name = SignedFilter.BEAN_NAME)
  public SignedFilter signedFilter() {
    return new SignedFilter();
  }

  @Bean(name = MdcFilter.BEAN_NAME)
  public MdcFilter ndcFilter() {
    return new MdcFilter();
  }

  @Bean(name = SignoutFilter.BEAN_NAME)
  public SignoutFilter signoutFilter() {
    return new SignoutFilter();
  }

  @Bean
  public ServletListenerRegistrationBean<IntrospectorCleanupListener>
      introspectorCleanupListener() {
    return new ServletListenerRegistrationBean<>(new IntrospectorCleanupListener());
  }

  @Bean
  public ServletListenerRegistrationBean<RequestContextListener> requestContextListener() {
    return new ServletListenerRegistrationBean<>(new RequestContextListener());
  }

  @Bean
  public ServletListenerRegistrationBean<ShiroWebEnvironmentListener>
      shiroWebEnvironmentListener() {
    return new ServletListenerRegistrationBean<>(new ShiroWebEnvironmentListener());
  }

  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
  public SignedShiro getSigned() {
    return new SignedShiro(userService);
  }
}
