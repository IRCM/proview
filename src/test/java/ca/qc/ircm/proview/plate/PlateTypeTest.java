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

package ca.qc.ircm.proview.plate;

import static ca.qc.ircm.proview.plate.PlateType.A;
import static ca.qc.ircm.proview.plate.PlateType.G;
import static ca.qc.ircm.proview.plate.PlateType.PM;
import static ca.qc.ircm.proview.plate.PlateType.SUBMISSION;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class PlateTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Other", PlateType.getNullLabel(Locale.ENGLISH));
    assertEquals("Autre", PlateType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_A() {
    assertEquals("A", A.getLabel(Locale.ENGLISH));
    assertEquals("A", A.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_G() {
    assertEquals("G", G.getLabel(Locale.ENGLISH));
    assertEquals("G", G.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Pm() {
    assertEquals("Small molecules", PM.getLabel(Locale.ENGLISH));
    assertEquals("Petites molécules", PM.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Submission() {
    assertEquals("User submission", SUBMISSION.getLabel(Locale.ENGLISH));
    assertEquals("Soumission d'un utilisateur", SUBMISSION.getLabel(Locale.FRENCH));
  }
}
