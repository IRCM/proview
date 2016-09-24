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

package ca.qc.ircm.proview.transfer;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.sample.SampleContainer;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Transfer} that can be recorded.
 */
public interface TransferActivityService {
  /**
   * Creates an activity about insertion of transfer.
   *
   * @param transfer
   *          inserted transfer
   * @return an activity about insertion of transfer
   */
  @CheckReturnValue
  public Activity insert(Transfer transfer);

  /**
   * Creates an activity about transfer being marked as erroneous.
   *
   * @param transfer
   *          erroneous transfer that was undone
   * @param justification
   *          explanation of what was incorrect with the transfer
   * @param samplesRemoved
   *          containers were sample was removed
   * @return activity about transfer being marked as erroneous
   */
  @CheckReturnValue
  public Activity undoErroneous(Transfer transfer, String justification,
      Collection<SampleContainer> samplesRemoved);

  /**
   * Creates an activity about transfer being marked as failed.
   *
   * @param transfer
   *          failed transfer that was undone
   * @param failedDescription
   *          description of the problem that occurred
   * @param bannedContainers
   *          containers that were banned
   * @return activity about transfer being marked as failed
   */
  @CheckReturnValue
  public Activity undoFailed(Transfer transfer, String failedDescription,
      Collection<SampleContainer> bannedContainers);
}
