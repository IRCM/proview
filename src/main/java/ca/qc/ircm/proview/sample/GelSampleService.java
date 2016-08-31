package ca.qc.ircm.proview.sample;

/**
 * Services for {@link GelSample submitted gel sample}.
 */
public interface GelSampleService {
  /**
   * Selects sample having this id.
   *
   * @param id
   *          sample's database identifier
   * @return sample having this id
   */
  public GelSample get(Long id);

  /**
   * Updates sample's information in database.
   *
   * @param sample
   *          sample containing new information
   * @param justification
   *          justification for changes made to sample
   */
  public void update(GelSample sample, String justification);
}
