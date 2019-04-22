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

import static ca.qc.ircm.proview.treatment.FractionationType.MUDPIT;
import static ca.qc.ircm.proview.treatment.FractionationType.PI;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.treatment.FractionationType;
import java.util.Locale;
import org.junit.Test;

public class FractionationTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Not applicable", FractionationType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", FractionationType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Mudpit() {
    assertEquals("Ions exchange column", MUDPIT.getLabel(Locale.ENGLISH));
    assertEquals("Colonne d'échange d'ions", MUDPIT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Pi() {
    assertEquals("pI", PI.getLabel(Locale.ENGLISH));
    assertEquals("pI", PI.getLabel(Locale.FRENCH));
  }
}