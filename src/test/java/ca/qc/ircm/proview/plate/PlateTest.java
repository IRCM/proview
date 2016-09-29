package ca.qc.ircm.proview.plate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.plate.PlateSpotService.SimpleSpotLocation;
import ca.qc.ircm.proview.sample.SubmissionSample;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlateTest {
  private Plate createPlate(Plate.Type type) {
    Plate plate = new Plate();
    plate.setType(type);
    List<PlateSpot> spots = new ArrayList<>();
    plate.setSpots(spots);
    for (int i = 0; i < type.getColumnCount(); i++) {
      for (int j = 0; j < type.getRowCount(); j++) {
        PlateSpot spot = new PlateSpot(j, i);
        spot.setPlate(plate);
        spots.add(spot);
      }
    }
    return plate;
  }

  private Optional<PlateSpot> find(List<PlateSpot> spots, int row, int column) {
    return spots.stream().filter(s -> s.getRow() == row && s.getColumn() == column).findAny();
  }

  @Test
  public void getRowCount() {
    assertEquals(Plate.Type.A.getRowCount(), createPlate(Plate.Type.A).getRowCount());
    assertEquals(Plate.Type.G.getRowCount(), createPlate(Plate.Type.G).getRowCount());
    assertEquals(Plate.Type.PM.getRowCount(), createPlate(Plate.Type.PM).getRowCount());
  }

  @Test
  public void getColumnCount() {
    assertEquals(Plate.Type.A.getRowCount(), createPlate(Plate.Type.A).getRowCount());
    assertEquals(Plate.Type.G.getRowCount(), createPlate(Plate.Type.G).getRowCount());
    assertEquals(Plate.Type.PM.getRowCount(), createPlate(Plate.Type.PM).getRowCount());
  }

  @Test
  public void spot_A() {
    Plate plate = createPlate(Plate.Type.A);

    PlateSpot spot = plate.spot(0, 0);
    assertEquals(0, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(0, 1);
    assertEquals(0, spot.getRow());
    assertEquals(1, spot.getColumn());
    spot = plate.spot(1, 0);
    assertEquals(1, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(Plate.Type.A.getRowCount() - 1, Plate.Type.A.getColumnCount() - 1);
    assertEquals(Plate.Type.A.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.A.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(Plate.Type.A.getRowCount() - 1, Plate.Type.A.getColumnCount() - 2);
    assertEquals(Plate.Type.A.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.A.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(Plate.Type.A.getRowCount() - 2, Plate.Type.A.getColumnCount() - 1);
    assertEquals(Plate.Type.A.getRowCount() - 2, spot.getRow());
    assertEquals(Plate.Type.A.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spot_G() {
    Plate plate = createPlate(Plate.Type.G);

    PlateSpot spot = plate.spot(0, 0);
    assertEquals(0, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(0, 1);
    assertEquals(0, spot.getRow());
    assertEquals(1, spot.getColumn());
    spot = plate.spot(1, 0);
    assertEquals(1, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(Plate.Type.G.getRowCount() - 1, Plate.Type.G.getColumnCount() - 1);
    assertEquals(Plate.Type.G.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.G.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(Plate.Type.G.getRowCount() - 1, Plate.Type.G.getColumnCount() - 2);
    assertEquals(Plate.Type.G.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.G.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(Plate.Type.G.getRowCount() - 2, Plate.Type.G.getColumnCount() - 1);
    assertEquals(Plate.Type.G.getRowCount() - 2, spot.getRow());
    assertEquals(Plate.Type.G.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spot_Pm() {
    Plate plate = createPlate(Plate.Type.PM);

    PlateSpot spot = plate.spot(0, 0);
    assertEquals(0, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(0, 1);
    assertEquals(0, spot.getRow());
    assertEquals(1, spot.getColumn());
    spot = plate.spot(1, 0);
    assertEquals(1, spot.getRow());
    assertEquals(0, spot.getColumn());
    spot = plate.spot(Plate.Type.PM.getRowCount() - 1, Plate.Type.PM.getColumnCount() - 1);
    assertEquals(Plate.Type.PM.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.PM.getColumnCount() - 1, spot.getColumn());
    spot = plate.spot(Plate.Type.PM.getRowCount() - 1, Plate.Type.PM.getColumnCount() - 2);
    assertEquals(Plate.Type.PM.getRowCount() - 1, spot.getRow());
    assertEquals(Plate.Type.PM.getColumnCount() - 2, spot.getColumn());
    spot = plate.spot(Plate.Type.PM.getRowCount() - 2, Plate.Type.PM.getColumnCount() - 1);
    assertEquals(Plate.Type.PM.getRowCount() - 2, spot.getRow());
    assertEquals(Plate.Type.PM.getColumnCount() - 1, spot.getColumn());
  }

  @Test
  public void spots_A() {
    Plate plate = createPlate(Plate.Type.A);

    List<PlateSpot> spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 1));
    assertEquals(Plate.Type.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0),
        new SimpleSpotLocation(Plate.Type.A.getRowCount(), Plate.Type.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SimpleSpotLocation(6, 2), new SimpleSpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SimpleSpotLocation(2, 6), new SimpleSpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void spots_G() {
    Plate plate = createPlate(Plate.Type.G);

    List<PlateSpot> spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 1));
    assertEquals(Plate.Type.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0),
        new SimpleSpotLocation(Plate.Type.A.getRowCount(), Plate.Type.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SimpleSpotLocation(6, 2), new SimpleSpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SimpleSpotLocation(2, 6), new SimpleSpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void spots_Pm() {
    Plate plate = createPlate(Plate.Type.PM);

    List<PlateSpot> spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 0));
    assertEquals(1, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(1, 0));
    assertEquals(2, spots.size());
    assertTrue(find(spots, 0, 0).isPresent());
    assertTrue(find(spots, 1, 0).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0), new SimpleSpotLocation(0, 1));
    assertEquals(Plate.Type.A.getRowCount() + 1, spots.size());
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 0).isPresent());
    }
    assertTrue(find(spots, 0, 1).isPresent());
    spots = plate.spots(new SimpleSpotLocation(0, 0),
        new SimpleSpotLocation(Plate.Type.A.getRowCount(), Plate.Type.A.getColumnCount()));
    assertEquals(plate.getSpots().size(), spots.size());
    spots = plate.spots(new SimpleSpotLocation(6, 2), new SimpleSpotLocation(2, 5));
    assertEquals(21, spots.size());
    for (int i = 6; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 2).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 3).isPresent());
    }
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      assertTrue(find(spots, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(spots, i, 5).isPresent());
    }
    spots = plate.spots(new SimpleSpotLocation(2, 6), new SimpleSpotLocation(5, 2));
    assertEquals(0, spots.size());
  }

  @Test
  public void getEmptySpotCount() {
    Plate plate = createPlate(Plate.Type.A);

    assertEquals(Plate.Type.A.getRowCount() * Plate.Type.A.getColumnCount(),
        plate.getEmptySpotCount());
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(Plate.Type.A.getRowCount() * (Plate.Type.A.getColumnCount() - 1),
        plate.getEmptySpotCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(Plate.Type.A.getRowCount() * (Plate.Type.A.getColumnCount() - 1) - 5,
        plate.getEmptySpotCount());
  }

  @Test
  public void getSampleCount() {
    Plate plate = createPlate(Plate.Type.A);

    assertEquals(0, plate.getSampleCount());
    for (int i = 0; i < Plate.Type.A.getRowCount(); i++) {
      find(plate.getSpots(), i, 3).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(Plate.Type.A.getRowCount(), plate.getSampleCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getSpots(), i, 0).orElse(null).setSample(new SubmissionSample());
    }
    assertEquals(Plate.Type.A.getRowCount() + 5, plate.getSampleCount());
  }
}
