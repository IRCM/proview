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
import static org.mockito.Mockito.verify;

import ca.qc.ircm.proview.security.SecurityConfiguration;
import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import javax.servlet.ServletContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ShiroWebEnvironmentListenerTest {
  private ShiroWebEnvironmentListener listener;
  @Mock
  private ServletContext servletContext;
  @Mock
  private ShiroWebEnvironment shiroWebEnvironment;
  @Mock
  private SecurityConfiguration securityConfiguration;

  @Before
  public void beforeTest() {
    listener = new ShiroWebEnvironmentListener(securityConfiguration);
  }

  @Test
  public void determineWebEnvironmentClass() {
    Class<?> clazz = listener.determineWebEnvironmentClass(servletContext);

    assertEquals(ShiroWebEnvironment.class, clazz);
  }

  @Test
  public void customizeEnvironment() {
    listener.customizeEnvironment(shiroWebEnvironment);

    verify(shiroWebEnvironment).setSecurityConfiguration(securityConfiguration);
  }
}
