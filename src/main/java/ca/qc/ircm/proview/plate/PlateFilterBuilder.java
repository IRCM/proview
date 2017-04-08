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

import ca.qc.ircm.proview.sample.Sample;

import java.util.Arrays;
import java.util.List;

public class PlateFilterBuilder {
  private static class Filter implements PlateFilter {
    private PlateType type;
    private List<Sample> containsAnySamples;

    @Override
    public PlateType type() {
      return type;
    }

    @Override
    public List<Sample> containsAnySamples() {
      return containsAnySamples;
    }
  }

  private Filter filter = new Filter();

  public PlateFilterBuilder type(PlateType type) {
    filter.type = type;
    return this;
  }

  public PlateFilterBuilder containsAnySamples(Sample... samples) {
    return containsAnySamples(Arrays.asList(samples));
  }

  public PlateFilterBuilder containsAnySamples(List<Sample> samples) {
    filter.containsAnySamples = samples;
    return this;
  }

  public PlateFilter build() {
    return filter;
  }
}
