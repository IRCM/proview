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

import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.OTHER;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

/**
 * Tests for {@link ProteolyticDigestion}.
 */
public class ProteolyticDigestionTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", ProteolyticDigestion.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucun", ProteolyticDigestion.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Trypsine() {
    assertEquals("Trypsin", TRYPSIN.getLabel(Locale.ENGLISH));
    assertEquals("Trypsine", TRYPSIN.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Digested() {
    assertEquals("Already digested", DIGESTED.getLabel(Locale.ENGLISH));
    assertEquals("Déjà digérées", DIGESTED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
