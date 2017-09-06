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

package ca.qc.ircm.proview.tube;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class TubeTest {
  @Test
  public void getName() {
    assertEquals("test_tube", new Tube(1L, "test_tube").getName());
  }

  @Test
  public void getFullName() {
    assertEquals("test_tube", new Tube(1L, "test_tube").getFullName(Locale.ENGLISH));
    assertEquals("test_tube", new Tube(1L, "test_tube").getFullName(Locale.FRENCH));
  }
}
