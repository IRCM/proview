package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.SampleType.AGAROSE_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.BIOID_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SampleType.MAGNETIC_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SampleType}.
 */
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
