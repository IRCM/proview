package ca.qc.ircm.proview.plate.render;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;

/**
 * Get localised plate information for plate PDF.
 */
public interface LocalizedPlate {

  /**
   * Get header for table containing plate information.
   * 
   * @param plate
   *          Plate.
   * @return Header.
   */
  public String getHeader(Plate plate);

  /**
   * Get spot information.
   * 
   * @param spot
   *          Spot.
   * @return Spot information.
   */
  public String getSpot(PlateSpot spot);

  /**
   * Get spot's sample name.
   * 
   * @param spot
   *          spot
   * @return spot's sample name
   */
  public String getSampleName(PlateSpot spot);
}
