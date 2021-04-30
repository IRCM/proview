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

package ca.qc.ircm.proview.msanalysis;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.LDTD;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.NSI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Locale;
import org.junit.Test;

/**
 * Tests for {@link MassDetectionInstrumentSource}.
 */
public class MassDetectionInstrumentSourceTest {
  @Test
  public void availables() {
    assertTrue(ESI.available);
    assertTrue(NSI.available);
    assertFalse(LDTD.available);

    List<MassDetectionInstrumentSource> availables = MassDetectionInstrumentSource.availables();
    assertEquals(2, availables.size());
    assertEquals(ESI, availables.get(0));
    assertEquals(NSI, availables.get(1));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", MassDetectionInstrumentSource.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", MassDetectionInstrumentSource.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Esi() {
    assertEquals("ESI", ESI.getLabel(Locale.ENGLISH));
    assertEquals("ESI", ESI.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Nsi() {
    assertEquals("NSI", NSI.getLabel(Locale.ENGLISH));
    assertEquals("NSI", NSI.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Ldtd() {
    assertEquals("LDTD", LDTD.getLabel(Locale.ENGLISH));
    assertEquals("LDTD", LDTD.getLabel(Locale.FRENCH));
  }
}
