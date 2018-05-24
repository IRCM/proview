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

import static ca.qc.ircm.proview.web.WebConstants.DEFAULT_LOCALE;

import ca.qc.ircm.proview.logging.web.MdcFilter;
import ca.qc.ircm.proview.security.web.ShiroWebEnvironmentListener;
import ca.qc.ircm.proview.user.web.SignoutFilter;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.IntrospectorCleanupListener;

/**
 * Enable Spring Web MVC for REST services.
 */
@Configuration
public class SpringWebConfiguration extends WebMvcConfigurerAdapter {
  public static final String SHIRO_FILTER_NAME = "ShiroFilter";

  @Bean(name = SHIRO_FILTER_NAME)
  public ShiroFilter shiroFilter() {
    return new ShiroFilter();
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

  /**
   * Returns {@link LocaleResolver} instance.
   * 
   * @return {@link LocaleResolver} instance
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(DEFAULT_LOCALE);
    return slr;
  }

  /**
   * Returns {@link LocaleChangeInterceptor} instance.
   * 
   * @return {@link LocaleChangeInterceptor} instance
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
    lci.setParamName("lang");
    return lci;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }
}
