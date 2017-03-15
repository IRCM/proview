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
  private Plate createPlate(PlateType type) {
    Plate plate = new Plate();
    plate.setType(type);
    return plate;
  }

  private Optional<PlateSpot> find(List<PlateSpot> spots, int row, int column) {
    return spots.stream().filter(s -> s.getRow() == row && s.getColumn() == column).findAny();
  }

  @Test
  public void getRowCount() {
    assertEquals(PlateType.A.getRowCount(), createPlate(PlateType.A).getRowCount());
    assertEquals(PlateType.G.getRowCount(), createPlate(PlateType.G).getRowCount());
    assertEquals(PlateType.PM.getRowCount(), createPlate(PlateType.PM).getRowCount());
  }

  @Test
  public void getColumnCount() {
    assertEquals(PlateType.A.getRowCount(), createPlate(PlateType.A).getRowCount());
    assertEquals(PlateType.G.getRowCount(), createPlate(PlateType.G).getRowCount());
    assertEquals(PlateType.PM.getRowCount(), createPlate(PlateType.PM).getRowCount());
  }

  @Test
  public void initSpots() {
    Plate plate = createPlate(PlateType.A);

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
  public void spot_A() {
    Plate plate = createPlate(PlateType.A);
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
    spot = plate.spot(PlateType.A.getRowCount() - 1, PlateType.A.getColumnCount() - 1);
    assertEquals(PlateType.A.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.A.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(PlateType.A.getRowCount() - 1, PlateType.A.getColumnCount() - 2);
    assertEquals(PlateType.A.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.A.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(PlateType.A.getRowCount() - 2, PlateType.A.getColumnCount() - 1);
    assertEquals(PlateType.A.getRowCount() - 2, spot.getRow());
    assertEquals(PlateType.A.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spot_G() {
    Plate plate = createPlate(PlateType.G);
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
    spot = plate.spot(PlateType.G.getRowCount() - 1, PlateType.G.getColumnCount() - 1);
    assertEquals(PlateType.G.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.G.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(PlateType.G.getRowCount() - 1, PlateType.G.getColumnCount() - 2);
    assertEquals(PlateType.G.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.G.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(PlateType.G.getRowCount() - 2, PlateType.G.getColumnCount() - 1);
    assertEquals(PlateType.G.getRowCount() - 2, spot.getRow());
    assertEquals(PlateType.G.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spot_Pm() {
    Plate plate = createPlate(PlateType.PM);
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
    spot = plate.spot(PlateType.PM.getRowCount() - 1, PlateType.PM.getColumnCount() - 1);
    assertEquals(PlateType.PM.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.PM.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(PlateType.PM.getRowCount() - 1, PlateType.PM.getColumnCount() - 2);
    assertEquals(PlateType.PM.getRowCount() - 1, spot.getRow());
    assertEquals(PlateType.PM.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(PlateType.PM.getRowCount() - 2, PlateType.PM.getColumnCount() - 1);
    assertEquals(PlateType.PM.getRowCount() - 2, spot.getRow());
    assertEquals(PlateType.PM.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spots_A() {
    Plate plate = createPlate(PlateType.A);
    plate.initSpots();

    List<PlateSpot> spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 1));
    assertEquals(PlateType.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SpotLocation(0, 0),
        new SpotLocation(PlateType.A.getRowCount(), PlateType.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SpotLocation(6, 2), new SpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SpotLocation(2, 6), new SpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void spots_G() {
    Plate plate = createPlate(PlateType.G);
    plate.initSpots();

    List<PlateSpot> spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 1));
    assertEquals(PlateType.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SpotLocation(0, 0),
        new SpotLocation(PlateType.A.getRowCount(), PlateType.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SpotLocation(6, 2), new SpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SpotLocation(2, 6), new SpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void spots_Pm() {
    Plate plate = createPlate(PlateType.PM);
    plate.initSpots();

    List<PlateSpot> spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SpotLocation(0, 0), new SpotLocation(0, 1));
    assertEquals(PlateType.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SpotLocation(0, 0),
        new SpotLocation(PlateType.A.getRowCount(), PlateType.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SpotLocation(6, 2), new SpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
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
    Plate plate = createPlate(PlateType.A);
    plate.initSpots();

    assertEquals(PlateType.A.getRowCount() * PlateType.A.getColumnCount(),
        plate.getEmptySpotCount());
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(PlateType.A.getRowCount() * (PlateType.A.getColumnCount() - 1),
        plate.getEmptySpotCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(PlateType.A.getRowCount() * (PlateType.A.getColumnCount() - 1) - 5,
        plate.getEmptySpotCount());
  }

  @Test
  public void getSampleCount() {
    Plate plate = createPlate(PlateType.A);
    plate.initSpots();

    assertEquals(0, plate.getSampleCount());
    for (int i = 0; i < PlateType.A.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(PlateType.A.getRowCount(), plate.getSampleCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(PlateType.A.getRowCount() + 5, plate.getSampleCount());
  }
}
