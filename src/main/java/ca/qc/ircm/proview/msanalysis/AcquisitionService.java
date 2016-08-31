package ca.qc.ircm.proview.msanalysis;

/**
 * Service class for acquisitions.
 */
public interface AcquisitionService {
  /**
   * Selects acquisition from database.
   *
   * @param id
   *          database identifier of acquisition
   * @return acquisition
   */
  public Acquisition get(Long id);
}
