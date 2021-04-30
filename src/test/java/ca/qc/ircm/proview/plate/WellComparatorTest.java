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

import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.plate.WellComparator.Compare;
import java.time.LocalDateTime;
import org.junit.Test;

/**
 * Tests for {@link WellComparator}.
 */
public class WellComparatorTest {
  @Test
  public void compare_Location_Row() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(2, 1);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_Row_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_Column() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(1, 2);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_Column_Reverse() {
    Well well1 = new Well(1, 2);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_RowColumn() {
    Well well1 = new Well(1, 2);
    Well well2 = new Well(2, 1);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_RowColumn_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 2);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_Same() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.LOCATION);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare == 0);
  }

  @Test
  public void compare_Timestamp() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(2, 1);
    well1.setTimestamp(LocalDateTime.now().minusMinutes(1));
    well2.setTimestamp(LocalDateTime.now());
    WellComparator comparator = new WellComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Timestamp_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 1);
    well1.setTimestamp(LocalDateTime.now());
    well2.setTimestamp(LocalDateTime.now().minusMinutes(1));
    WellComparator comparator = new WellComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Timestamp_Same() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(1, 1);
    well1.setTimestamp(LocalDateTime.of(2015, 5, 20, 0, 0, 0));
    well2.setTimestamp(LocalDateTime.of(2015, 5, 20, 0, 0, 0));
    WellComparator comparator = new WellComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare == 0);
  }

  @Test
  public void compare_SampleAssign_Row() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(2, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Row_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_Column() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(2, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Column_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_RowColumn() {
    Well well1 = new Well(1, 2);
    Well well2 = new Well(2, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_RowColumn_Reverse() {
    Well well1 = new Well(2, 1);
    Well well2 = new Well(1, 2);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Same() {
    Well well1 = new Well(1, 1);
    Well well2 = new Well(1, 1);
    WellComparator comparator = new WellComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(well1, well2);

    assertTrue(compare == 0);
  }
}
