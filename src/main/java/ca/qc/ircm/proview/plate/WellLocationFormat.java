package ca.qc.ircm.proview.plate;

/**
 * Formats a {@link WellLocation well location} into a string of type <code>a-1</code>.
 */
public class WellLocationFormat {
  public String format(WellLocation wellLocation) {
    return ((char) ('a' + wellLocation.getRow())) + "-" + (wellLocation.getColumn() + 1);
  }
}
