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

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.time.TimeConverter.toInstant;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;

import ca.qc.ircm.proview.text.Strings;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.querydsl.core.BooleanBuilder;
import java.time.LocalDate;
import java.util.function.Predicate;

/**
 * Filters plate search.
 */
public class PlateFilter implements Predicate<Plate> {
  public String nameContains;
  public Range<LocalDate> insertTimeRange;
  public Boolean submission;

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
    return test;
  }

  /**
   * Returns QueryDSL predicate matching filter.
   *
   * @return QueryDSL predicate matching filter
   */
  public com.querydsl.core.types.Predicate predicate() {
    BooleanBuilder predicate = new BooleanBuilder();
    if (nameContains != null) {
      predicate.and(plate.name.contains(nameContains));
    }
    if (insertTimeRange != null) {
      if (insertTimeRange.hasLowerBound()) {
        LocalDate date = insertTimeRange.lowerEndpoint();
        if (insertTimeRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        predicate.and(plate.insertTime.goe(toInstant(date)));
      }
      if (insertTimeRange.hasUpperBound()) {
        LocalDate date = insertTimeRange.upperEndpoint();
        if (insertTimeRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        predicate.and(plate.insertTime.before(toInstant(date)));
      }
    }
    if (submission != null) {
      predicate.and(plate.submission.eq(submission));
    }
    return predicate.getValue();
  }
}
