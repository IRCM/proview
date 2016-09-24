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

package ca.qc.ircm.proview.plate.render;

import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Sample;

/**
 * Default implementation of {@link LocalizedPlate}.
 */
public class LocalizedPlateDefault implements LocalizedPlate {

  private final FractionationService fractionationService;

  public LocalizedPlateDefault(FractionationService fractionationService) {
    this.fractionationService = fractionationService;
  }

  @Override
  public String getHeader(Plate plate) {
    return plate.getName() + " (" + plate.getType() + ")";
  }

  @Override
  public String getSpot(PlateSpot spot) {
    return spot.getName();
  }

  @Override
  public String getSampleName(PlateSpot spot) {
    if (spot.getSample() != null) {
      // Special case if sample if a fraction.
      FractionationDetail detail = fractionationService.find(spot);
      if (detail != null) {
        if (detail.getName() != null) {
          return detail.getName();
        } else {
          return detail.getLims();
        }
      }

      // Return sample's tag.
      Sample sample = spot.getSample();
      if (sample.getName() != null) {
        return sample.getName();
      } else {
        return sample.getLims();
      }
    } else {
      return null;
    }
  }
}
