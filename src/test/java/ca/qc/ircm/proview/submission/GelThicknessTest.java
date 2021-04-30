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

import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.GelThickness.ONE_HALF;
import static ca.qc.ircm.proview.submission.GelThickness.TWO;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

/**
 * Tests for {@link GelThickness}.
 */
public class GelThicknessTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", GelThickness.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", GelThickness.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_One() {
    assertEquals("1", ONE.getLabel(Locale.ENGLISH));
    assertEquals("1", ONE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_OneHalf() {
    assertEquals("1.5", ONE_HALF.getLabel(Locale.ENGLISH));
    assertEquals("1.5", ONE_HALF.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Two() {
    assertEquals("2", TWO.getLabel(Locale.ENGLISH));
    assertEquals("2", TWO.getLabel(Locale.FRENCH));
  }
}
