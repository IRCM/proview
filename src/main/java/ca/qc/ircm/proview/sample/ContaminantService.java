package ca.qc.ircm.proview.sample;

import java.util.List;

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

  /**
   * Selects all sample's contaminants.
   *
   * @param sample
   *          sample
   * @return all sample's contaminants
   */
  @Deprecated
  public List<Contaminant> all(Sample sample);
}
