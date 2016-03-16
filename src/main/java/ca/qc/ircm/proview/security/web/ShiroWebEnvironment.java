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

import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.env.WebEnvironment;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom {@link WebEnvironment} for Shiro.
 */
public class ShiroWebEnvironment extends IniWebEnvironment {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ShiroWebEnvironment.class);
  private Collection<Realm> realms;
  private String cipherKey;

  @Override
  protected WebSecurityManager createWebSecurityManager() {
    // Must call super to process main section.
    DefaultWebSecurityManager manager =
        (DefaultWebSecurityManager) super.createWebSecurityManager();
    manager.setRealms(realms);
    AbstractRememberMeManager rememberMeManager =
        (AbstractRememberMeManager) manager.getRememberMeManager();
    rememberMeManager.setCipherKey(Base64.decode(cipherKey));
    return manager;
  }

  /**
   * Sets Shiro's realm.
   *
   * @param realm
   *          realm
   */
  public void setRealm(final Realm realm) {
    Collection<Realm> realms = new ArrayList<Realm>(1);
    realms.add(realm);
    this.setRealms(realms);
  }

  public void setCipherKey(String cipherKey) {
    this.cipherKey = cipherKey;
  }

  public void setRealms(Collection<Realm> realms) {
    this.realms = realms;
  }
}
