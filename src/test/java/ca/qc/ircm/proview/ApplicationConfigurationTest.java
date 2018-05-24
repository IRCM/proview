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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class ApplicationConfigurationTest {
  @Inject
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

  @Test
  public void getPlateTemplate() throws Throwable {
    InputStream expectedInput = getClass().getResourceAsStream("/Plate-Template.xlsx");
    InputStream actualInput = applicationConfiguration.getPlateTemplate();
    ByteArrayOutputStream expectedOutput = new ByteArrayOutputStream();
    ByteArrayOutputStream actualOutput = new ByteArrayOutputStream();
    IOUtils.copy(expectedInput, expectedOutput);
    IOUtils.copy(actualInput, actualOutput);
    assertArrayEquals(expectedOutput.toByteArray(), actualOutput.toByteArray());
  }
}
