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

package ca.qc.ircm.proview.solubilisation;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Services for solubilisation.
 */
public interface SolubilisationService {
  /**
   * Selects solubilisation from database.
   *
   * @param id
   *          solubilisation's database identifier
   * @return solubilisation
   */
  public Solubilisation get(Long id);

  /**
   * Returns solubilisations where sample was solubilized.
   *
   * @param sample
   *          sample
   * @return solubilisations where sample was solubilized
   */
  public List<Solubilisation> all(Sample sample);

  /**
   * Inserts solubilisation into database.
   *
   * @param solubilisation
   *          solubilisation
   */
  public void insert(Solubilisation solubilisation);

  /**
   * Undo erroneous solubilisation that never actually occurred. This method is usually called
   * shortly after action was inserted into the database. The user realises that the samples checked
   * for solubilisation are not the right ones. So, in practice, the solubilisation never actually
   * occurred.
   *
   * @param solubilisation
   *          erroneous solubilisation to undo
   * @param justification
   *          explanation of what was incorrect with the solubilisation
   */
  public void undoErroneous(Solubilisation solubilisation, String justification);

  /**
   * Report that a problem occurred during solubilisation causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the solubilisation was done but the incorrect
   * solubilisation could only be detected later in the sample processing. Thus the solubilisation
   * is not undone but flagged as having failed.
   *
   * @param solubilisation
   *          solubilisation to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in solubilisation should be banned, this will also ban any
   *          container were samples were transfered after solubilisation
   */
  public void undoFailed(Solubilisation solubilisation, String failedDescription,
      boolean banContainers);
}
