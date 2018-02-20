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

package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelSeparation.TWO_DIMENSION;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class GelSeparationTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", GelSeparation.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", GelSeparation.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_OneDimension() {
    assertEquals("1D", ONE_DIMENSION.getLabel(Locale.ENGLISH));
    assertEquals("1D", ONE_DIMENSION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_TwoDimension() {
    assertEquals("2D", TWO_DIMENSION.getLabel(Locale.ENGLISH));
    assertEquals("2D", TWO_DIMENSION.getLabel(Locale.FRENCH));
  }
}
