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

package ca.qc.ircm.proview.dataanalysis;

import static ca.qc.ircm.proview.dataanalysis.DataAnalysisType.PEPTIDE;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisType.PROTEIN;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisType.PROTEIN_PEPTIDE;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

public class DataAnalysisTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Not determined", DataAnalysisType.getNullLabel(Locale.ENGLISH));
    assertEquals("Non déterminé", DataAnalysisType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Protein() {
    assertEquals("Protein", PROTEIN.getLabel(Locale.ENGLISH));
    assertEquals("Protéine", PROTEIN.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Peptide() {
    assertEquals("Peptide", PEPTIDE.getLabel(Locale.ENGLISH));
    assertEquals("Peptide", PEPTIDE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_ProteinPeptide() {
    assertEquals("Protein and peptide", PROTEIN_PEPTIDE.getLabel(Locale.ENGLISH));
    assertEquals("Protéine et peptide", PROTEIN_PEPTIDE.getLabel(Locale.FRENCH));
  }
}
