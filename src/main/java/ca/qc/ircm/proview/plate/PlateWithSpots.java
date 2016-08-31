package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.plate.PlateSpotService.SpotLocation;

import java.util.List;

/**
 * Aggregate of a plate and it's spots.
 */
@Deprecated
public interface PlateWithSpots {

  /**
   * Returns plate.
   *
   * @return plate
   */
  public Plate getPlate();

  /**
   * Returns plate's spots.
   *
   * @return plate's spots
   */
  public List<PlateSpot> getSpots();

  /**
   * Returns number of empty spots on plate.
   *
   * @return number of empty spots on plate
   */
  public Integer getEmptySpotCount();

  /**
   * Returns number of spots containing a sample on plate.
   *
   * @return number of spots containing a sample on plate
   */
  public Integer getSampleCount();

  /**
   * Returns spots going from <code>from parameter</code> (inclusive) up to
   * <code>to parameter</code> (inclusive). For examples, if <code>from</code> is <code>c-7</code>
   * and <code>to</code> is <code>d-8</code>, returned spots will be
   * <code>c-7, d-7, e-7, f-7, g-7, h-7, a-8, b-8, c-8, d-8</code>
   *
   * @param from
   *          first location of returned spots
   * @param to
   *          final location of returned spots
   * @return spots going from <code>from parameter</code> (inclusive) up to
   *         <code>to parameter</code> (inclusive).
   */
  public List<PlateSpot> spots(SpotLocation from, SpotLocation to);

  /**
   * <p>
   * Returns spot at location row-column.
   * </p>
   * <p>
   * row must be lower then plate's number of rows (see {@link Plate#getRowCount()})
   * </p>
   * <p>
   * column must be lower then plate's number of columns (see {@link Plate#getColumnCount()})
   * </p>
   *
   * @param row
   *          row of spot.
   * @param column
   *          column of spot.
   * @return spot at location row-column.
   */
  public PlateSpot spot(int row, int column);
}
