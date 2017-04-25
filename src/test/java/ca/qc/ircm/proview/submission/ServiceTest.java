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

import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.MALDI_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.Service.TWO_DIMENSION_LC_MS_MS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;
import java.util.Locale;

public class ServiceTest {
  @Test
  public void availables() {
    assertTrue(LC_MS_MS.available);
    assertFalse(TWO_DIMENSION_LC_MS_MS.available);
    assertFalse(MALDI_MS.available);
    assertTrue(SMALL_MOLECULE.available);
    assertTrue(INTACT_PROTEIN.available);

    List<Service> availables = Service.availables();
    assertEquals(3, availables.size());
    assertEquals(LC_MS_MS, availables.get(0));
    assertEquals(SMALL_MOLECULE, availables.get(1));
    assertEquals(INTACT_PROTEIN, availables.get(2));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", Service.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", Service.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel() {
    Locale locale = Locale.CANADA;

    assertEquals("LC/MS/MS", LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", MALDI_MS.getLabel(locale));
    assertEquals("Small molecule", SMALL_MOLECULE.getLabel(locale));
    assertEquals("Intact protein", INTACT_PROTEIN.getLabel(locale));
  }

  @Test
  public void getLabel_French() {
    Locale locale = Locale.CANADA_FRENCH;

    assertEquals("LC/MS/MS", LC_MS_MS.getLabel(locale));
    assertEquals("2D-LC/MS/MS (MudPit)", TWO_DIMENSION_LC_MS_MS.getLabel(locale));
    assertEquals("MALDI/MS", MALDI_MS.getLabel(locale));
    assertEquals("Petite molecule", SMALL_MOLECULE.getLabel(locale));
    assertEquals("Protéine intacte", INTACT_PROTEIN.getLabel(locale));
  }
}
