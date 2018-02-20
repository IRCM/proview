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

import static ca.qc.ircm.proview.treatment.TreatmentType.DIGESTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.DILUTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.ENRICHMENT;
import static ca.qc.ircm.proview.treatment.TreatmentType.FRACTIONATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.SOLUBILISATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.STANDARD_ADDITION;
import static ca.qc.ircm.proview.treatment.TreatmentType.TRANSFER;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class TreatmentTypeTest {
  @Test
  public void getLabel_Digestion() {
    assertEquals("Digestion", DIGESTION.getLabel(Locale.ENGLISH));
    assertEquals("Digestion", DIGESTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Dilution() {
    assertEquals("Dilution", DILUTION.getLabel(Locale.ENGLISH));
    assertEquals("Dilution", DILUTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Enrichment() {
    assertEquals("Enrichment", ENRICHMENT.getLabel(Locale.ENGLISH));
    assertEquals("Enrichissement", ENRICHMENT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Fractionation() {
    assertEquals("Fractionation", FRACTIONATION.getLabel(Locale.ENGLISH));
    assertEquals("Fractionnement", FRACTIONATION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Solubilisation() {
    assertEquals("Solubilisation", SOLUBILISATION.getLabel(Locale.ENGLISH));
    assertEquals("Solubilisation", SOLUBILISATION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_StandardAddition() {
    assertEquals("Standard addition", STANDARD_ADDITION.getLabel(Locale.ENGLISH));
    assertEquals("Addition de standard", STANDARD_ADDITION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Transfer() {
    assertEquals("Transfer", TRANSFER.getLabel(Locale.ENGLISH));
    assertEquals("Transfert", TRANSFER.getLabel(Locale.FRENCH));
  }
}
