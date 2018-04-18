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

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.text.Strings;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;

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

  private void addFilterConditions(JPAQuery<?> query) {
    if (nameContains != null) {
      query.where(plate.name.contains(nameContains));
    }
    if (insertTimeRange != null) {
      if (insertTimeRange.hasLowerBound()) {
        LocalDate date = insertTimeRange.lowerEndpoint();
        if (insertTimeRange.lowerBoundType() == BoundType.OPEN) {
          date = date.plusDays(1);
        }
        query.where(plate.insertTime.goe(toInstant(date)));
      }
      if (insertTimeRange.hasUpperBound()) {
        LocalDate date = insertTimeRange.upperEndpoint();
        if (insertTimeRange.upperBoundType() == BoundType.CLOSED) {
          date = date.plusDays(1);
        }
        query.where(plate.insertTime.before(toInstant(date)));
      }
    }
    if (submission != null) {
      query.where(plate.submission.eq(submission));
    }
    if (minimumEmptyCount != null) {
      QWell mecW = new QWell("mecW");
      query.where(plate.columnCount.multiply(plate.rowCount).subtract(minimumEmptyCount)
          .goe(JPAExpressions.select(mecW.sample.count()).from(plate.wells, mecW)));
    }
    if (containsAnySamples != null) {
      query.where(plate.wells.any().sample.in(containsAnySamples));
    }
  }

  public void addConditions(JPAQuery<?> query) {
    addFilterConditions(query);
  }
}
