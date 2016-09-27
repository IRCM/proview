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

package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class SampleSupportTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleSupport.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleSupport.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Dry() {
    assertEquals("Dry", DRY.getLabel(Locale.ENGLISH));
    assertEquals("Sec", DRY.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Solution() {
    assertEquals("Solution", SOLUTION.getLabel(Locale.ENGLISH));
    assertEquals("Solution", SOLUTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Gel() {
    assertEquals("Gel", GEL.getLabel(Locale.ENGLISH));
    assertEquals("Gel", GEL.getLabel(Locale.FRENCH));
  }
}
