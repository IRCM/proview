package ca.qc.ircm.proview.plate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Well}.
 */
@NonTransactionalTestAnnotations
public class WellTest {

  @Test
  public void getName() {
    assertEquals("C-4", new Well(2, 3).getName());
    assertEquals("Z-4", new Well(25, 3).getName());
    assertEquals("AA-31", new Well(26, 30).getName());
    assertEquals("AE-31", new Well(30, 30).getName());
    assertEquals("BA-31", new Well(52, 30).getName());
    assertEquals("ZZ-31", new Well(701, 30).getName());
    assertEquals("AAA-31", new Well(702, 30).getName());
    assertEquals("ZZZ-31", new Well(18277, 30).getName());
    assertEquals("AAAA-31", new Well(18278, 30).getName());
  }

  @Test
  public void getFullName() {
    Plate plate = new Plate(1L, "test_plate");
    plate.setColumnCount(31);
    plate.setRowCount(18279);
    plate.initWells();
    assertEquals("test_plate (C-4)", plate.well(2, 3).getFullName());
    assertEquals("test_plate (Z-31)", plate.well(25, 30).getFullName());
    assertEquals("test_plate (AA-31)", plate.well(26, 30).getFullName());
    assertEquals("test_plate (AE-31)", plate.well(30, 30).getFullName());
    assertEquals("test_plate (BA-31)", plate.well(52, 30).getFullName());
    assertEquals("test_plate (ZZ-31)", plate.well(701, 30).getFullName());
    assertEquals("test_plate (AAA-31)", plate.well(702, 30).getFullName());
    assertEquals("test_plate (ZZZ-31)", plate.well(18277, 30).getFullName());
    assertEquals("test_plate (AAAA-31)", plate.well(18278, 30).getFullName());
  }
}
