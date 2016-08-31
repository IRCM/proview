package ca.qc.ircm.proview.sample;

/**
 * Service class for submitted molecule samples.
 */
public interface MoleculeSampleService {
  /**
   * Selects sample having this id.
   *
   * @param id
   *          sample's database identifier
   * @return sample having this id
   */
  public MoleculeSample get(Long id);

  /**
   * Updates sample's information in database.
   *
   * @param sample
   *          sample containing new information
   * @param justification
   *          justification for changes made to sample
   * @throws SaveStructureException
   *           new structure could not be saved
   */
  public void update(MoleculeSample sample, String justification) throws SaveStructureException;
}
