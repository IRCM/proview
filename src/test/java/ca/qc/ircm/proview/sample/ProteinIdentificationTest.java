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

package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.ProteinIdentification.MSDB_ID;
import static ca.qc.ircm.proview.sample.ProteinIdentification.NCBINR;
import static ca.qc.ircm.proview.sample.ProteinIdentification.OTHER;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ProteinIdentification}.
 */
public class ProteinIdentificationTest {
  @Test
  public void availables() {
    assertTrue(REFSEQ.available);
    assertTrue(UNIPROT.available);
    assertFalse(NCBINR.available);
    assertFalse(MSDB_ID.available);
    assertTrue(OTHER.available);

    List<ProteinIdentification> identifications = ProteinIdentification.availables();
    assertEquals(3, identifications.size());
    assertEquals(REFSEQ, identifications.get(0));
    assertEquals(UNIPROT, identifications.get(1));
    assertEquals(OTHER, identifications.get(2));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", ProteinIdentification.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", ProteinIdentification.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Refseq() {
    assertEquals("NCBI (RefSeq)", REFSEQ.getLabel(Locale.ENGLISH));
    assertEquals("NCBI (RefSeq)", REFSEQ.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Uniprot() {
    assertEquals("UniProt", UNIPROT.getLabel(Locale.ENGLISH));
    assertEquals("UniProt", UNIPROT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Ncbinr() {
    assertEquals("NCBInr", NCBINR.getLabel(Locale.ENGLISH));
    assertEquals("NCBInr", NCBINR.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Msdb() {
    assertEquals("MSDB_ID", MSDB_ID.getLabel(Locale.ENGLISH));
    assertEquals("MSDB_ID", MSDB_ID.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
