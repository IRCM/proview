package ca.qc.ircm.proview.submission;

/**
 * Protein content of samples.
 */
public enum ProteinContent {
  SMALL(1, 4), MEDIUM(5, 10), LARGE(10, 20), XLARGE(20, Integer.MAX_VALUE);

  /**
   * Start of interval.
   */
  private final int start;
  /**
   * End of interval.
   */
  private final int end;
  ProteinContent(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }
}