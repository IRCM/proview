package ca.qc.ircm.proview.sample;

/**
 * All statuses of a sample.
 */
public enum SampleStatus {
  /**
   * Waiting to receive sample.
   */
  WAITING,
  /**
   * Sample was received and awaits treatments and analysis.
   */
  RECEIVED,
  /**
   * Sample was digested.
   */
  DIGESTED,
  /**
   * Sample was enriched.
   */
  ENRICHED,
  /**
   * Sample analysis was cancelled.
   */
  CANCELLED,
  /**
   * Sample was analysed and have results.
   */
  ANALYSED;

  public static SampleStatus[] analysedStatuses() {
    return new SampleStatus[] { CANCELLED, ANALYSED };
  }
}