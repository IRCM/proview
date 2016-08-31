package ca.qc.ircm.proview.sample;

import java.util.List;

/**
 * Service for sample containers.
 */
public interface SampleContainerService {
  /**
   * Selects sample container from database.
   *
   * @param id
   *          database identifier of sample container
   * @return sample container
   */
  public SampleContainer get(Long id);

  /**
   * Selects last sample container in which sample was.
   *
   * @param sample
   *          sample
   * @return last sample container in which sample was
   */
  public SampleContainer last(Sample sample);

  /**
   * Returns sample containers containing sample.
   *
   * @param sample
   *          sample
   * @return sample containers containing sample
   */
  public List<SampleContainer> all(Sample sample);
}
