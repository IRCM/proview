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

import ca.qc.ircm.proview.security.SecurityConfiguration;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link WebEnvironment} for Shiro.
 */
public class ShiroWebEnvironment extends IniWebEnvironment {
  private static final Logger logger = LoggerFactory.getLogger(ShiroWebEnvironment.class);
  private SecurityConfiguration securityConfiguration;

  @Override
  protected WebSecurityManager createWebSecurityManager() {
    // Must call super to process main section.
    DefaultWebSecurityManager manager =
        (DefaultWebSecurityManager) super.createWebSecurityManager();
    manager.setCacheManager(new MemoryConstrainedCacheManager());
    logger.debug("Set realm {} in web environment", securityConfiguration.shiroRealm());
    manager.setRealm(securityConfiguration.shiroRealm());
    AbstractRememberMeManager rememberMeManager =
        (AbstractRememberMeManager) manager.getRememberMeManager();
    rememberMeManager.setCipherKey(securityConfiguration.getCipherKeyBytes());
    return manager;
  }

  public SecurityConfiguration getSecurityConfiguration() {
    return securityConfiguration;
  }

  public void setSecurityConfiguration(SecurityConfiguration securityConfiguration) {
    this.securityConfiguration = securityConfiguration;
  }
}
