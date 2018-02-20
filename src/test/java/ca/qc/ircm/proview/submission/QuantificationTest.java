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

import static ca.qc.ircm.proview.submission.Quantification.LABEL_FREE;
import static ca.qc.ircm.proview.submission.Quantification.NULL;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Quantification.TMT;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class QuantificationTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", Quantification.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucune", Quantification.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Null() {
    assertEquals("None", NULL.getLabel(Locale.ENGLISH));
    assertEquals("Aucune", NULL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_LabelFree() {
    assertEquals("Label-free", LABEL_FREE.getLabel(Locale.ENGLISH));
    assertEquals("Label-free", LABEL_FREE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Silac() {
    assertEquals("Silac", SILAC.getLabel(Locale.ENGLISH));
    assertEquals("Silac", SILAC.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Tmt() {
    assertEquals("Tandem mass tags", TMT.getLabel(Locale.ENGLISH));
    assertEquals("Tandem mass tags", TMT.getLabel(Locale.FRENCH));
  }
}
