package ca.qc.ircm.proview.sample;

/**
 * Services for {@link EluateSample submitted eluate sample}.
 */
public interface EluateSampleService {
  /**
   * Selects sample having this id.
   *
   * @param id
   *          sample's database identifier
   * @return sample having this id
   */
  public EluateSample get(Long id);

  /**
   * Updates sample's information in database.
   *
   * @param sample
   *          sample containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(EluateSample sample, String justification);
}
