package ca.qc.ircm.proview.submission;

/**
 * Available storage temperatures.
 */
public enum StorageTemperature {
  MEDIUM(4), LOW(-20);

  /**
   * Real temperature of this enum.
   */
  private final int temperature;

  StorageTemperature(int temperature) {
    this.temperature = temperature;
  }

  public int getTemperature() {
    return temperature;
  }
}