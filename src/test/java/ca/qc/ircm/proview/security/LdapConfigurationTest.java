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

package ca.qc.ircm.proview.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class LdapConfigurationTest {
  @Inject
  private LdapConfiguration ldapConfiguration;
  @Inject
  private LdapTemplate ldapTemplate;

  @Test
  public void defaultProperties() throws Throwable {
    LdapContextSource contextSource = (LdapContextSource) ldapTemplate.getContextSource();
    assertTrue(ldapConfiguration.enabled());
    assertEquals(contextSource.getUrls()[0], ldapConfiguration.url());
    assertEquals("uid={0},ou=people,dc=mycompany,dc=com", ldapConfiguration.userDnTemplate());
    assertEquals("dc=mycompany,dc=com", ldapConfiguration.base());
    assertEquals("uid={0}", ldapConfiguration.userFilter());
    assertEquals("uid", ldapConfiguration.idAttribute());
    assertEquals("mail", ldapConfiguration.mailAttribute());
  }
}
