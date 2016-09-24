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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class ServiceTest {
  @Test
  public void getLabel() {
    Locale locale = Locale.CANADA;

    assertEquals("LC/MS/MS", Service.LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", Service.TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", Service.MALDI_MS.getLabel(locale));
    assertEquals("Small molecule", Service.SMALL_MOLECULE.getLabel(locale));
    assertEquals("Intact protein", Service.INTACT_PROTEIN.getLabel(locale));
  }

  @Test
  public void getLabel_French() {
    Locale locale = Locale.CANADA_FRENCH;

    assertEquals("LC/MS/MS", Service.LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", Service.TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", Service.MALDI_MS.getLabel(locale));
    assertEquals("Petite molecule", Service.SMALL_MOLECULE.getLabel(locale));
    assertEquals("Protéine intacte", Service.INTACT_PROTEIN.getLabel(locale));
  }
}
