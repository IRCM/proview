package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.plate.PlateSpotService.SpotLocation;

/**
 * Formats a {@link SpotLocation spot location} into a string of type <code>a-1</code>.
 */
public class SpotLocationFormat {
  public String format(SpotLocation spotLocation) {
    return ((char) ('a' + spotLocation.getRow())) + "-" + (spotLocation.getColumn() + 1);
  }
}
