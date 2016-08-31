package ca.qc.ircm.proview.sample;

import java.util.List;

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

  /**
   * Selects all sample's standards added by user before sample is submitted for analysis.
   *
   * @param sample
   *          sample
   * @return all sample's standards added by user before sample is submitted for analysis
   */
  @Deprecated
  public List<Standard> all(Sample sample);
}
