package ca.qc.ircm.proview.sample;

/**
 * Services for contaminants.
 */
public interface ContaminantService {
  /**
   * Selects contaminant from database.
   *
   * @param id
   *          database identifier of contaminant
   * @return contaminant
   */
  public Contaminant get(Long id);
}
