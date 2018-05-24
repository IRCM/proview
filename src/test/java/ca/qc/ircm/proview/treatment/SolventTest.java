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

package ca.qc.ircm.proview.treatment;

import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.treatment.Solvent.OTHER;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

public class SolventTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", Solvent.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", Solvent.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Acetonitrile() {
    assertEquals("Acetonitrile", ACETONITRILE.getLabel(Locale.ENGLISH));
    assertEquals("Acétonitrile", ACETONITRILE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Methanol() {
    assertEquals("Methanol", METHANOL.getLabel(Locale.ENGLISH));
    assertEquals("Méthanol", METHANOL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Chcl3() {
    assertEquals("CHCl<sub>3</sub>", CHCL3.getLabel(Locale.ENGLISH));
    assertEquals("CHCl<sub>3</sub>", CHCL3.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
