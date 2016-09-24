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

package ca.qc.ircm.proview.treatment;

import ca.qc.ircm.proview.history.Activity;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Protocol} that can be recorded.
 */
public interface ProtocolActivityService {
  /**
   * Creates an activity about insertion of protocol.
   *
   * @param protocol
   *          inserted protocol
   * @return activity about insertion of protocol
   */
  @CheckReturnValue
  public Activity insert(Protocol protocol);
}
