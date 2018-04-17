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

import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.text.Strings;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filters plate search.
 */
public class PlateFilter implements Predicate<Plate> {
  public String nameContains;
  public Integer minimumEmptyCount;
  public Range<LocalDate> insertTimeRange;
  public Boolean submission;
  public List<Sample> containsAnySamples;

  @Override
  public boolean test(Plate plate) {
    boolean test = true;
    if (nameContains != null) {
      String nameContainsNormalized = Strings.normalize(nameContains).toLowerCase();
      String name = Strings.normalize(plate.getName()).toLowerCase();
      test &= name.contains(nameContainsNormalized);
    }
    if (insertTimeRange != null) {
      test &= insertTimeRange.contains(toLocalDate(plate.getInsertTime()));
    }
    if (submission != null) {
      test &= submission == plate.isSubmission();
    }
    if (minimumEmptyCount != null) {
      test &= plate.getEmptyWellCount() >= minimumEmptyCount;
    }
    if (containsAnySamples != null) {
      Set<Long> sampleIds =
          containsAnySamples.stream().map(sample -> sample.getId()).collect(Collectors.toSet());
      test &= plate.getWells().stream().filter(well -> well.getSample() != null)
          .map(well -> well.getSample().getId()).filter(id -> sampleIds.contains(id)).findAny()
          .isPresent();
    }
    return test;
  }
}
