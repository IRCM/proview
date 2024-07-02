package ca.qc.ircm.proview.submission;

/**
 * Available MudPit fractions.
 */
public enum MudPitFraction {
  EIGHT(8), TWELVE(12), SIXTEEN(16);

  MudPitFraction(int number) {
    this.number = number;
  }

  /**
   * Number of fractions for this enum.
   */
  private int number;

  public int getNumber() {
    return number;
  }
}