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

package ca.qc.ircm.proview.security.web;

import ca.qc.ircm.proview.ApplicationConfiguration;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.ShiroRealm;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * Creates Shiro's environment.
 */
public class ShiroWebEnvironmentListener extends EnvironmentLoaderListener {
  private static final Logger logger = LoggerFactory.getLogger(ShiroWebEnvironmentListener.class);
  @Inject
  private AuthenticationService authenticationService;
  @Inject
  private ApplicationConfiguration applicationConfiguration;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    WebApplicationContext context =
        WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext());
    context.getAutowireCapableBeanFactory().autowireBean(this);
    super.contextInitialized(sce);
  }

  @Override
  protected Class<?> determineWebEnvironmentClass(ServletContext servletContext) {
    return ShiroWebEnvironment.class;
  }

  @Override
  protected void customizeEnvironment(WebEnvironment environment) {
    Realm realm = createRealm();
    logger.debug("Set realm {} in web environment", realm);
    if (!(environment instanceof ShiroWebEnvironment)) {
      throw new IllegalStateException(
          WebEnvironment.class.getSimpleName() + " must be mutable to use custom realm");
    }

    ((ShiroWebEnvironment) environment).setRealm(realm);
    ((ShiroWebEnvironment) environment).setCipherKey(applicationConfiguration.getCipherKey());
  }

  private Realm createRealm() {
    CacheManager shiroCacheManager = new MemoryConstrainedCacheManager();
    PermissionResolver permissionResolver = new WildcardPermissionResolver();
    Realm realm = new ShiroRealm(authenticationService, shiroCacheManager, permissionResolver,
        applicationConfiguration.getRealmName());
    return realm;
  }
}
