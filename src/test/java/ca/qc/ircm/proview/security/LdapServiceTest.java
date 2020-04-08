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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class LdapServiceTest {
  private LdapService ldapService;
  @Autowired
  private LdapTemplate ldapTemplate;
  @Autowired
  private LdapConfiguration ldapConfiguration;

  @Before
  public void beforeTest() {
    ldapService = new LdapService(ldapTemplate, ldapConfiguration);
  }

  @Test
  public void isPasswordValid_True() {
    assertTrue(ldapService.isPasswordValid("poitrasc", "secret"));
  }

  @Test
  public void isPasswordValid_InvalidUser() {
    assertFalse(ldapService.isPasswordValid("invalid", "secret"));
  }

  @Test
  public void isPasswordValid_InvalidPassword() {
    assertFalse(ldapService.isPasswordValid("poitrasc", "secret2"));
  }

  @Test
  public void getEmail() {
    assertEquals("christian.poitras@ircm.qc.ca", ldapService.getEmail("poitrasc"));
  }

  @Test
  public void getEmail_Invalid() {
    assertEquals(null, ldapService.getEmail("invalid"));
  }

  @Test
  public void getUsername() {
    assertEquals("poitrasc", ldapService.getUsername("christian.poitras@ircm.qc.ca"));
  }
}
