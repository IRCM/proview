package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Services for Mascot file.
 */
public interface MascotFileService {
  /**
   * Returns link between acquisistion and mascot file.
   *
   * @param id
   *          link identifier
   * @return link between acquisistion and mascot file
   */
  public AcquisitionMascotFile get(Long id);

  /**
   * Returns all links Mascot files linked to acquisition.
   *
   * @param acquisition
   *          acquisition
   * @return all Mascot files linked to acquisition
   */
  public List<AcquisitionMascotFile> all(Acquisition acquisition);

  /**
   * Returns true if sample is linked to at least one visible Mascot file, false otherwise.
   *
   * @param sample
   *          sample
   * @return true if sample is linked to at least one visible Mascot file, false otherwise
   */
  public boolean exists(Sample sample);

  /**
   * Updates link between acquisistion and mascot file.
   *
   * @param acquisitionMascotFile
   *          link between acquisistion and mascot file
   */
  public void update(AcquisitionMascotFile acquisitionMascotFile);
}
