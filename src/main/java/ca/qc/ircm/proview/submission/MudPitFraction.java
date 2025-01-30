package ca.qc.ircm.proview.submission;

/**
 * Available MudPit fractions.
 */
public enum MudPitFraction {
  EIGHT(8), TWELVE(12), SIXTEEN(16);

  /**
   * Number of fractions for this enum.
   */
  private final int number;

  MudPitFraction(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }
}