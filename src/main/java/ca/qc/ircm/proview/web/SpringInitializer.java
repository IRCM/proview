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

import ca.qc.ircm.proview.SpringConfiguration;
import ca.qc.ircm.proview.logging.Log4jConfiguration;
import ca.qc.ircm.proview.logging.NdcFilter;
import ca.qc.ircm.proview.security.web.ShiroWebEnvironmentListener;
import ca.qc.ircm.proview.user.SignedProvider;
import ca.qc.ircm.proview.user.web.SignedFilter;
import com.vaadin.spring.server.SpringVaadinServlet;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import org.springframework.web.util.IntrospectorCleanupListener;

import java.util.EnumSet;

import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Initializes Spring.
 */
public class SpringInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
  private static final String VAADIN_PRODUCTION_MODE = "productionMode";

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[] { SpringConfiguration.class, SignedProvider.class };
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class[] { SpringWebConfiguration.class };
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] {};
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    servletContext.addListener(IntrospectorCleanupListener.class);
    servletContext.addListener(Log4jConfiguration.class);

    super.onStartup(servletContext);

    registerVaadinServlet(servletContext);
    servletContext.addListener(ShiroWebEnvironmentListener.class);
    servletContext.addListener(RequestContextListener.class);
    servletContext.addFilter(ShiroFilter.class.getSimpleName(), ShiroFilter.class)
        .addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    registerServletFilter(servletContext, new DelegatingFilterProxy(SignedFilter.BEAN_NAME))
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    servletContext.addFilter(NdcFilter.class.getSimpleName(), NdcFilter.class)
        .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  private void registerVaadinServlet(ServletContext servletContext) {
    servletContext.setInitParameter(VAADIN_PRODUCTION_MODE,
        Boolean.toString(getVaadinProductionMode()));
    SpringVaadinServlet servlet = new SpringVaadinServlet();
    servlet.setServiceUrlPath(null);
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet("vaadin", servlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.setAsyncSupported(true);
    dispatcher.addMapping("/*");
  }

  private boolean getVaadinProductionMode() {
    JndiObjectFactoryBean factory = new JndiObjectFactoryBean();
    factory.setJndiName("vaadin-debug");
    factory.setResourceRef(true);
    try {
      factory.afterPropertiesSet();
      Object value = factory.getObject();
      return !Boolean.valueOf(String.valueOf(value));
    } catch (IllegalArgumentException | NamingException e) {
      // True by default.
      return true;
    }
  }
}
