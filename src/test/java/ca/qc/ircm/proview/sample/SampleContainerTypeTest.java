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

import static ca.qc.ircm.proview.sample.SampleContainerType.SPOT;
import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class SampleContainerTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleContainerType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleContainerType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Tube() {
    assertEquals("To approve", TUBE.getLabel(Locale.ENGLISH));
    assertEquals("À approuver", TUBE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Spot() {
    assertEquals("To receive", SPOT.getLabel(Locale.ENGLISH));
    assertEquals("À recevoir", SPOT.getLabel(Locale.FRENCH));
  }
}