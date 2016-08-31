package ca.qc.ircm.proview.treatment;

/**
 * Services for treatments.
 */
public interface TreatmentService {
  /**
   * Selects treatment from database.
   *
   * @param id
   *          database identifier of treatment
   * @return treatment
   */
  public Treatment<?> get(Long id);
}
