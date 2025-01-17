package ca.qc.ircm.proview.plate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Plate}.
 */
public class PlateTest {
  private Optional<Well> find(List<Well> wells, int row, int column) {
    return wells.stream().filter(s -> s.getRow() == row && s.getColumn() == column).findAny();
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
  public void initWells() {
    Plate plate = new Plate();

    plate.initWells();

    assertEquals(plate.getRowCount() * plate.getColumnCount(), plate.getWells().size());
    boolean[][] wellLocations = new boolean[plate.getRowCount()][plate.getColumnCount()];
    for (Well well : plate.getWells()) {
      assertEquals(plate, well.getPlate());
      wellLocations[well.getRow()][well.getColumn()] = true;
    }
    for (boolean[] wellLocation : wellLocations) {
      for (boolean b : wellLocation) {
        assertTrue(b);
      }
    }
  }

  @Test
  public void well() {
    Plate plate = new Plate();
    plate.initWells();

    Well well = plate.well(0, 0);
    assertEquals(0, well.getRow());
    assertEquals(0, well.getColumn());
    well = plate.well(0, 1);
    assertEquals(0, well.getRow());
    assertEquals(1, well.getColumn());
    well = plate.well(1, 0);
    assertEquals(1, well.getRow());
    assertEquals(0, well.getColumn());
    well = plate.well(plate.getRowCount() - 1, plate.getColumnCount() - 1);
    assertEquals(plate.getRowCount() - 1, well.getRow());
    assertEquals(plate.getColumnCount() - 1, well.getColumn());
    well = plate.well(plate.getRowCount() - 1, plate.getColumnCount() - 2);
    assertEquals(plate.getRowCount() - 1, well.getRow());
    assertEquals(plate.getColumnCount() - 2, well.getColumn());
    well = plate.well(plate.getRowCount() - 2, plate.getColumnCount() - 1);
    assertEquals(plate.getRowCount() - 2, well.getRow());
    assertEquals(plate.getColumnCount() - 1, well.getColumn());
  }

  @Test
  public void wells() {
    Plate plate = new Plate();
    plate.initWells();

    List<Well> wells = plate.wells(new WellLocation(0, 0), new WellLocation(0, 0));
    assertEquals(1, wells.size());
    assertTrue(find(wells, 0, 0).isPresent());
    wells = plate.wells(new WellLocation(0, 0), new WellLocation(1, 0));
    assertEquals(2, wells.size());
    assertTrue(find(wells, 0, 0).isPresent());
    assertTrue(find(wells, 1, 0).isPresent());
    wells = plate.wells(new WellLocation(0, 0), new WellLocation(0, 1));
    assertEquals(plate.getRowCount() + 1, wells.size());
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(wells, i, 0).isPresent());
    }
    assertTrue(find(wells, 0, 1).isPresent());
    wells = plate.wells(new WellLocation(0, 0),
        new WellLocation(plate.getRowCount(), plate.getColumnCount()));
    assertEquals(plate.getWells().size(), wells.size());
    wells = plate.wells(new WellLocation(6, 2), new WellLocation(2, 5));
    assertEquals(21, wells.size());
    for (int i = 6; i < plate.getRowCount(); i++) {
      assertTrue(find(wells, i, 2).isPresent());
    }
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(wells, i, 3).isPresent());
    }
    for (int i = 0; i < plate.getRowCount(); i++) {
      assertTrue(find(wells, i, 4).isPresent());
    }
    for (int i = 0; i < 2; i++) {
      assertTrue(find(wells, i, 5).isPresent());
    }
    wells = plate.wells(new WellLocation(2, 6), new WellLocation(5, 2));
    assertEquals(0, wells.size());
  }

  @Test
  public void wellsContainingSample() {
    Plate plate = new Plate();
    plate.initWells();
    Sample sample1 = new SubmissionSample(564L);
    Sample sample2 = new SubmissionSample(565L);
    plate.well(0, 0).setSample(sample1);
    plate.well(0, 1).setSample(sample1);
    plate.well(1, 1).setSample(sample1);
    plate.well(1, 0).setSample(sample2);
    plate.well(2, 1).setSample(sample2);

    List<Well> wells = plate.wellsContainingSample(sample1);

    assertEquals(3, wells.size());
    assertTrue(wells.contains(plate.well(0, 0)));
    assertTrue(wells.contains(plate.well(0, 1)));
    assertTrue(wells.contains(plate.well(1, 1)));
  }

  @Test
  public void getEmptyWellCount() {
    Plate plate = new Plate();
    plate.initWells();

    assertEquals(plate.getRowCount() * plate.getColumnCount(), plate.getEmptyWellCount());
    for (int i = 0; i < plate.getRowCount(); i++) {
      find(plate.getWells(), i, 3).orElseThrow().setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount() * (plate.getColumnCount() - 1), plate.getEmptyWellCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getWells(), i, 0).orElseThrow().setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount() * (plate.getColumnCount() - 1) - 5, plate.getEmptyWellCount());
  }

  @Test
  public void getSampleCount() {
    Plate plate = new Plate();
    plate.initWells();

    assertEquals(0, plate.getSampleCount());
    for (int i = 0; i < plate.getRowCount(); i++) {
      find(plate.getWells(), i, 3).orElseThrow().setSample(new SubmissionSample());
    }
    assertEquals(plate.getRowCount(), plate.getSampleCount());
    for (int i = 0; i < 5; i++) {
      find(plate.getWells(), i, 0).orElseThrow().setSample(new SubmissionSample());
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
