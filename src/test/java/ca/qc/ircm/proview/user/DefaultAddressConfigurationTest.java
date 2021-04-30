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

package ca.qc.ircm.proview.user;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link DefaultAddressConfiguration}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class DefaultAddressConfigurationTest {
  @Autowired
  private DefaultAddressConfiguration defaultAddressConfiguration;

  @Test
  public void defaultProperties() throws Throwable {
    assertEquals("110 avenue des Pins Ouest", defaultAddressConfiguration.getLine());
    assertEquals("Montreal", defaultAddressConfiguration.getTown());
    assertEquals("Quebec", defaultAddressConfiguration.getState());
    assertEquals("Canada", defaultAddressConfiguration.getCountry());
    assertEquals("H2W 1R7", defaultAddressConfiguration.getPostalCode());
    Address address = defaultAddressConfiguration.getAddress();
    assertEquals("110 avenue des Pins Ouest", address.getLine());
    assertEquals("Montreal", address.getTown());
    assertEquals("Quebec", address.getState());
    assertEquals("Canada", address.getCountry());
    assertEquals("H2W 1R7", address.getPostalCode());
  }
}
