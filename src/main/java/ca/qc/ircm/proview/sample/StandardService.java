package ca.qc.ircm.proview.sample;

/**
 * Service for standards.
 */
public interface StandardService {
  /**
   * Selects standard from database.
   *
   * @param id
   *          database identifier of standard
   * @return standard
   */
  public Standard get(Long id);
}
