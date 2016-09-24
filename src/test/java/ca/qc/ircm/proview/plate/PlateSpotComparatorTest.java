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

import ca.qc.ircm.proview.plate.PlateSpotComparator.Compare;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class PlateSpotComparatorTest {
  @Test
  public void compare_Location_Row() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(2, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_Row_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_Column() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(1, 2);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_Column_Reverse() {
    PlateSpot spot1 = new PlateSpot(1, 2);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_RowColumn() {
    PlateSpot spot1 = new PlateSpot(1, 2);
    PlateSpot spot2 = new PlateSpot(2, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Location_RowColumn_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 2);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Location_Same() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.LOCATION);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare == 0);
  }

  @Test
  public void compare_Timestamp() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(2, 1);
    spot1.setTimestamp(
        LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
    spot2.setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_Timestamp_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    spot1.setTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    spot2.setTimestamp(
        LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_Timestamp_Same() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    spot1.setTimestamp(
        LocalDateTime.of(2015, 5, 20, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    spot2.setTimestamp(
        LocalDateTime.of(2015, 5, 20, 0, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.TIME_STAMP);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare == 0);
  }

  @Test
  public void compare_SampleAssign_Row() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(2, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Row_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_Column() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(2, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Column_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_RowColumn() {
    PlateSpot spot1 = new PlateSpot(1, 2);
    PlateSpot spot2 = new PlateSpot(2, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare > 0);
  }

  @Test
  public void compare_SampleAssign_RowColumn_Reverse() {
    PlateSpot spot1 = new PlateSpot(2, 1);
    PlateSpot spot2 = new PlateSpot(1, 2);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare < 0);
  }

  @Test
  public void compare_SampleAssign_Same() {
    PlateSpot spot1 = new PlateSpot(1, 1);
    PlateSpot spot2 = new PlateSpot(1, 1);
    PlateSpotComparator comparator = new PlateSpotComparator(Compare.SAMPLE_ASSIGN);

    int compare = comparator.compare(spot1, spot2);

    assertTrue(compare == 0);
  }
}
