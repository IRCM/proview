package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleType.AGAROSE_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.BIOID_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SampleType.MAGNETIC_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link SampleType}.
 */
@NonTransactionalTestAnnotations
public class SampleTypeTest {

  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Dry() {
    assertEquals("Dry",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + DRY.name(), null, Locale.ENGLISH));
    assertEquals("Sec",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + DRY.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Solution() {
    assertEquals("Solution",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + SOLUTION.name(), null, Locale.ENGLISH));
    assertEquals("Solution",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + SOLUTION.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Gel() {
    assertEquals("Gel",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + GEL.name(), null, Locale.ENGLISH));
    assertEquals("Gel",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + GEL.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_BioidBeads() {
    assertEquals("BioID beads",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + BIOID_BEADS.name(), null, Locale.ENGLISH));
    assertEquals("Billes BioID",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + BIOID_BEADS.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_MagneticBeads() {
    assertEquals("Magnetic beads",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + MAGNETIC_BEADS.name(), null, Locale.ENGLISH));
    assertEquals("Billes magnétiques",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + MAGNETIC_BEADS.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_AgaroseBeads() {
    assertEquals("Agarose beads",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + AGAROSE_BEADS.name(), null, Locale.ENGLISH));
    assertEquals("Billes agaroses",
        messageSource.getMessage(SAMPLE_TYPE_PREFIX + AGAROSE_BEADS.name(), null, Locale.FRENCH));
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
