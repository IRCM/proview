package ca.qc.ircm.proview.sample;

/**
 * Service class for Sample.
 */
public interface SampleService {
  /**
   * Selects sample from database.
   *
   * @param id
   *          database identifier of sample
   * @return sample
   */
  public Sample get(Long id);

  /**
   * Returns true if sample is linked to some results, false otherwise.
   *
   * @param sample
   *          sample
   * @return true if sample is linked to some results, false otherwise
   */
  public boolean linkedToResults(Sample sample);
}
