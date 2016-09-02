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
