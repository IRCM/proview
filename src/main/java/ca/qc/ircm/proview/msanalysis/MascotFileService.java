/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
