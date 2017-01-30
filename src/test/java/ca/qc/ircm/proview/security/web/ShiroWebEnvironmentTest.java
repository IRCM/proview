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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.config.Ini;
import org.apache.shiro.mgt.AbstractRememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ShiroWebEnvironmentTest {
  private ShiroWebEnvironment shiroWebEnvironment;
  @Mock
  private SecurityConfiguration securityConfiguration;
  @Mock
  private Realm realm;
  private byte[] cipherKey = Base64.decode("AcEG7RqLxcP6enoSBJKNjA==");

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    shiroWebEnvironment = new ShiroWebEnvironment();
    shiroWebEnvironment.setSecurityConfiguration(securityConfiguration);
    when(securityConfiguration.getCipherKeyBytes()).thenReturn(cipherKey);
    when(securityConfiguration.shiroRealm()).thenReturn(realm);
  }

  @Test
  public void iniConfiguration() {
    shiroWebEnvironment.init();

    Ini ini = shiroWebEnvironment.getIni();

    assertEquals("/user/sign", ini.getSectionProperty("main", "authc.loginUrl"));
  }

  @Test
  public void createWebSecurityManager() {
    WebSecurityManager rawManager = shiroWebEnvironment.createWebSecurityManager();

    verify(securityConfiguration, atLeastOnce()).shiroRealm();
    verify(securityConfiguration, atLeastOnce()).getCipherKeyBytes();
    assertTrue(rawManager instanceof DefaultWebSecurityManager);
    DefaultWebSecurityManager manager = (DefaultWebSecurityManager) rawManager;
    assertTrue(manager.getCacheManager() instanceof MemoryConstrainedCacheManager);
    assertEquals(1, manager.getRealms().size());
    assertEquals(realm, manager.getRealms().iterator().next());
    assertTrue(manager.getRememberMeManager() instanceof AbstractRememberMeManager);
    AbstractRememberMeManager rememberMeManager =
        (AbstractRememberMeManager) manager.getRememberMeManager();
    assertArrayEquals(cipherKey, rememberMeManager.getCipherKey());
  }
}
