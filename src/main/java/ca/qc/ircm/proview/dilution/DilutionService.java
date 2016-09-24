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

package ca.qc.ircm.proview.dilution;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Services for dilutions.
 */
public interface DilutionService {
  /**
   * Selects dilution from database.
   *
   * @param id
   *          database identifier of dilution
   * @return dilution
   */
  public Dilution get(Long id);

  /**
   * Returns all dilutions where sample was diluted.
   *
   * @param sample
   *          sample
   * @return all dilutions where sample was diluted
   */
  public List<Dilution> all(Sample sample);

  /**
   * Insert a dilution in database.
   *
   * @param dilution
   *          dilution to insert
   */
  public void insert(Dilution dilution);

  /**
   * Undo erroneous dilution that never actually occurred. This method is usually called shortly
   * after action was inserted into the database. The user realises that the samples checked for
   * dilution are not the right ones. So, in practice, the dilution never actually occurred.
   *
   * @param dilution
   *          erroneous dilution to undo
   * @param justification
   *          explanation of what was incorrect with the dilution
   */
  public void undoErroneous(Dilution dilution, String justification);

  /**
   * Report that a problem occurred during dilution causing it to fail. Problems usually occur
   * because of an experimental error. In this case, the dilution was done but the incorrect
   * dilution could only be detected later in the sample processing. Thus the dilution is not undone
   * but flagged as having failed.
   *
   * @param dilution
   *          dilution to flag as having failed
   * @param failedDescription
   *          description of the problem that occurred
   * @param banContainers
   *          true if containers used in dilution should be banned, this will also ban any container
   *          were samples were transfered after dilution
   */
  public void undoFailed(Dilution dilution, String failedDescription, boolean banContainers);
}
