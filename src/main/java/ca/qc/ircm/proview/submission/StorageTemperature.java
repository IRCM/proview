package ca.qc.ircm.proview.submission;

/**
 * Available storage temperatures.
 */
public enum StorageTemperature {
  MEDIUM(4), LOW(-20);

  StorageTemperature(int temperature) {
    this.temperature = temperature;
  }

  /**
   * Real temperature of this enum.
   */
  private int temperature;

  public int getTemperature() {
    return temperature;
  }
}