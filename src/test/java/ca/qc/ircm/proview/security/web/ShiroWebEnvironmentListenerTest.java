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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.security.ShiroRealm;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.apache.shiro.authz.permission.WildcardPermissionResolver;
import org.apache.shiro.realm.Realm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.ServletContext;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ShiroWebEnvironmentListenerTest {
  private ShiroWebEnvironmentListener listener;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ShiroWebEnvironment shiroWebEnvironment;
  @Mock
  private AuthenticationService authenticationService;
  @Mock
  private SecurityConfiguration securityConfiguration;
  @Captor
  private ArgumentCaptor<Realm> realmCaptor;
  private byte[] cipherKey = new byte[4096];

  @Before
  public void beforeTest() {
    listener = new ShiroWebEnvironmentListener(authenticationService, securityConfiguration);
  }

  @Test
  public void determineWebEnvironmentClass() {
    Class<?> clazz = listener.determineWebEnvironmentClass(servletContext);

    assertEquals(ShiroWebEnvironment.class, clazz);
  }

  @Test
  public void customizeEnvironment() {
    when(securityConfiguration.getCipherKeyBytes()).thenReturn(cipherKey);

    listener.customizeEnvironment(shiroWebEnvironment);

    verify(shiroWebEnvironment).setRealm(realmCaptor.capture());
    Realm rawRealm = realmCaptor.getValue();
    assertTrue(rawRealm instanceof ShiroRealm);
    ShiroRealm realm = (ShiroRealm) rawRealm;
    assertTrue(realm.getPermissionResolver() instanceof WildcardPermissionResolver);
    verify(shiroWebEnvironment).setCipherKey(cipherKey);
  }
}
