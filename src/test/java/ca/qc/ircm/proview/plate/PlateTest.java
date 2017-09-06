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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.sample.SubmissionSample;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class PlateTest {
  private Optional<PlateSpot> find(List<PlateSpot> spots, int row, int column) {
    return spots.stream().filter(s -> s.getRow() == row && s.getColumn() == column).findAny();
  }

  @Test
  public void getRowCount() {
    Plate plate = new Plate();
    assertEquals(8, plate.getRowCount());
    plate.setRowCount(10);
    assertEquals(10, plate.getRowCount());
  }

  @Test
  public void getColumnCount() {
    Plate plate = new Plate();
    assertEquals(12, plate.getColumnCount());
    plate.setColumnCount(10);
    assertEquals(10, plate.getColumnCount());
  }

  @Test
  public void initSpots() {
    Plate plate = new Plate();

    plate.initSpots();

    assertEquals(plate.getRowCount() * plate.getColumnCount(), plate.getSpots().size());
    boolean[][] spotLocations = new boolean[plate.getRowCount()][plate.getColumnCount()];
    for (PlateSpot spot : plate.getSpots()) {
      assertEquals(plate, spot.getPlate());
      spotLocations[spot.getRow()][spot.getColumn()] = true;
    }
    for (int row = 0; row < spotLocations.length; row++) {
      for (int column = 0; column < spotLocations[row].length; column++) {
        assertTrue(spotLocations[row][column]);
      }
    }
  }

  @Test
  public void spot() {
    Plate plate = new Plate();
    plate.initSpots();

    PlateSpot spot = plate.spot(0, 0);
    assertEquals(0, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(0, 1);
    assertEquals(0, spot.getRow());
    assertEquals(1, spot.getColumn());
    spot = plate.spot(1, 0);
    assertEquals(1, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(plate.getRowCount() - 1, plate.getColumnCount() - 1);
    assertEquals(plate.getRowCount() - 1, spot.getRow());
    assertEquals(plate.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(plate.getRowCount() - 1, plate.getColumnCount() - 2);
    assertEquals(plate.getRowCount() - 1, spot.getRow());
    assertEquals(plate.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(plate.getRowCount() - 2, plate.getColumnCount() - 1);
    assertEquals(plate.getRowCount() - 2, spot.getRow());
    assertEquals(plate.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spots() {
    Plate plate = new Plate();
    plate.initSpots();

    List<PlateSpot> spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 1));
    assertEquals(plate.getRowCount() + 1, spots.size());
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SpotLocation(0, 0),
        new SpotLocation(plate.getRowCount(), plate.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SpotLocation(6, 2), new SpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < plate.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SpotLocation(2, 6), new SpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void getEmptySpotCount() {
    Plate plate = new Plate();
    plate.initSpots();

    assertEquals(plate.getRowCount() * plate.getColumnCount(), plate.getEmptySpotCount());
    for (int i = 0; i < plate.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount() * (plate.getColumnCount() - 1), plate.getEmptySpotCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount() * (plate.getColumnCount() - 1) - 5, plate.getEmptySpotCount());
  }

  @Test
  public void getSampleCount() {
    Plate plate = new Plate();
    plate.initSpots();

    assertEquals(0, plate.getSampleCount());
    for (int i = 0; i < plate.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount(), plate.getSampleCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount() + 5, plate.getSampleCount());
  }

  @Test
  public void rowLabel() {
    assertEquals("A", Plate.rowLabel(0));
    assertEquals("Z", Plate.rowLabel(25));
    assertEquals("AA", Plate.rowLabel(26));
    assertEquals("AE", Plate.rowLabel(30));
    assertEquals("BA", Plate.rowLabel(52));
    assertEquals("ZZ", Plate.rowLabel(701));
    assertEquals("AAA", Plate.rowLabel(702));
    assertEquals("ZZZ", Plate.rowLabel(18277));
    assertEquals("AAAA", Plate.rowLabel(18278));
  }

  @Test
  public void columnLabel() {
    assertEquals("1", Plate.columnLabel(0));
    assertEquals("26", Plate.columnLabel(25));
    assertEquals("27", Plate.columnLabel(26));
    assertEquals("31", Plate.columnLabel(30));
    assertEquals("53", Plate.columnLabel(52));
    assertEquals("702", Plate.columnLabel(701));
    assertEquals("703", Plate.columnLabel(702));
    assertEquals("18278", Plate.columnLabel(18277));
    assertEquals("18279", Plate.columnLabel(18278));
  }
}
