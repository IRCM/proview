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

package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link ApplicationConfiguration}.
 */
@NonTransactionalTestAnnotations
public class ApplicationConfigurationTest {
  @Autowired
  private ApplicationConfiguration applicationConfiguration;

  @Test
  public void getLogFile() {
    assertEquals(Paths.get(System.getProperty("user.dir"), "test.log"),
        applicationConfiguration.getLogFile());
  }

  @Test
  public void getUrl() {
    assertEquals("http://localhost:8080/myurl/subpath?param1=abc",
        applicationConfiguration.getUrl("/myurl/subpath?param1=abc"));
  }
}
