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

import static ca.qc.ircm.proview.sample.SampleType.AGAROSE_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.BIOID_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SampleType.MAGNETIC_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Locale;

public class SampleTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Dry() {
    assertEquals("Dry", DRY.getLabel(Locale.ENGLISH));
    assertEquals("Sec", DRY.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Solution() {
    assertEquals("Solution", SOLUTION.getLabel(Locale.ENGLISH));
    assertEquals("Solution", SOLUTION.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Gel() {
    assertEquals("Gel", GEL.getLabel(Locale.ENGLISH));
    assertEquals("Gel", GEL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_BioidBeads() {
    assertEquals("BioID beads", BIOID_BEADS.getLabel(Locale.ENGLISH));
    assertEquals("Billes BioID", BIOID_BEADS.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_MagneticBeads() {
    assertEquals("Magnetic beads", MAGNETIC_BEADS.getLabel(Locale.ENGLISH));
    assertEquals("Billes magnétiques", MAGNETIC_BEADS.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_AgaroseBeads() {
    assertEquals("Agarose beads", AGAROSE_BEADS.getLabel(Locale.ENGLISH));
    assertEquals("Billes agaroses", AGAROSE_BEADS.getLabel(Locale.FRENCH));
  }

  @Test
  public void isDry() {
    assertTrue(DRY.isDry());
    assertFalse(SOLUTION.isDry());
    assertFalse(GEL.isDry());
    assertFalse(BIOID_BEADS.isDry());
    assertFalse(MAGNETIC_BEADS.isDry());
    assertFalse(AGAROSE_BEADS.isDry());
  }

  @Test
  public void isSolution() {
    assertFalse(DRY.isSolution());
    assertTrue(SOLUTION.isSolution());
    assertFalse(GEL.isSolution());
    assertTrue(BIOID_BEADS.isSolution());
    assertTrue(MAGNETIC_BEADS.isSolution());
    assertTrue(AGAROSE_BEADS.isSolution());
  }

  @Test
  public void isGel() {
    assertFalse(DRY.isGel());
    assertFalse(SOLUTION.isGel());
    assertTrue(GEL.isGel());
    assertFalse(BIOID_BEADS.isGel());
    assertFalse(MAGNETIC_BEADS.isGel());
    assertFalse(AGAROSE_BEADS.isGel());
  }

  @Test
  public void isBeads() {
    assertFalse(DRY.isBeads());
    assertFalse(SOLUTION.isBeads());
    assertFalse(GEL.isBeads());
    assertTrue(BIOID_BEADS.isBeads());
    assertTrue(MAGNETIC_BEADS.isBeads());
    assertTrue(AGAROSE_BEADS.isBeads());
  }
}
