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

package ca.qc.ircm.proview.standard;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link StandardAddition} that can be recorded.
 */
public interface StandardAdditionActivityService {
  /**
   * Creates an activity about insertion of addition of standards.
   *
   * @param standardAddition
   *          inserted addition of standards
   * @return activity about insertion of addition of standards
   */
  @CheckReturnValue
  public Activity insert(StandardAddition standardAddition);

  /**
   * Creates an activity about addition of standards being marked as erroneous.
   *
   * @param standardAddition
   *          erroneous addition of standards that was undone
   * @param justification
   *          explanation of what was incorrect with the addition of standards
   * @return activity about addition of standards being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(StandardAddition standardAddition, String justification);

  /**
   * Creates an activity about addition of standards being marked as failed.
   *
   * @param standardAddition
   *          failed addition of standards that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about addition of standards being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(StandardAddition standardAddition, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
