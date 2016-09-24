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

package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.history.Activity;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

/**
 * Creates activities about {@link Plate} that can be recorded.
 */
public interface PlateActivityService {
  /**
   * Creates an activity about insertion of plate.
   *
   * @param plate
   *          inserted plate
   * @return activity about insertion of plate
   */
  @CheckReturnValue
  public Activity insert(Plate plate);

  /**
   * Creates an activity about spots being marked as banned.
   *
   * @param spots
   *          spots that were banned
   * @param justification
   *          justification for banning spots
   * @return activity about spots being marked as banned
   */
  @CheckReturnValue
  public Activity ban(Collection<PlateSpot> spots, String justification);

  /**
   * Creates an activity about spots being marked as reactivated.
   *
   * @param spots
   *          spots that were reactivated
   * @param justification
   *          justification for reactivating spots
   * @return activity about spots being marked as reactivated
   */
  @CheckReturnValue
  public Activity activate(Collection<PlateSpot> spots, String justification);
}
