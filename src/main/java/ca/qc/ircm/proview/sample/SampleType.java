package ca.qc.ircm.proview.sample;

/**
 * Sample support.
 */
public enum SampleType {
  /**
   * Sample is dry.
   */
  DRY,
  /**
   * Sample is in solution.
   */
  SOLUTION,
  /**
   * Sample is in a Gel.
   */
  GEL, BIOID_BEADS, MAGNETIC_BEADS, AGAROSE_BEADS;

  public boolean isDry() {
    return this == DRY;
  }

  public boolean isSolution() {
    return this == SOLUTION || this == BIOID_BEADS || this == MAGNETIC_BEADS
        || this == AGAROSE_BEADS;
  }

  public boolean isGel() {
    return this == GEL;
  }

  public boolean isBeads() {
    return this == BIOID_BEADS || this == MAGNETIC_BEADS || this == AGAROSE_BEADS;
  }
}
