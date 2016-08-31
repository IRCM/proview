package ca.qc.ircm.proview.sample;

import ca.qc.ircm.proview.plate.PlateSpot;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for sample containers.
 */
public class SampleContainerComparator implements Comparator<SampleContainer>, Serializable {
  private static final long serialVersionUID = -1704857519143636051L;
  private final Locale locale;

  public SampleContainerComparator(Locale locale) {
    this.locale = locale;
  }

  @Override
  public int compare(SampleContainer o1, SampleContainer o2) {
    Collator collator = Collator.getInstance(locale);
    switch (o1.getType()) {
      case TUBE:
        switch (o2.getType()) {
          case TUBE:
            return collator.compare(o1.getName(), o2.getName());
          case SPOT:
            return -1;
          default:
        }
        break;
      case SPOT:
        switch (o2.getType()) {
          case TUBE:
            return 1;
          case SPOT:
            PlateSpot spot1 = (PlateSpot) o1;
            PlateSpot spot2 = (PlateSpot) o2;
            int compare = Integer.valueOf(spot1.getColumn()).compareTo(spot2.getColumn());
            compare =
                compare == 0 ? Integer.valueOf(spot1.getRow()).compareTo(spot2.getRow()) : compare;
            return compare;
          default:
        }
        break;
      default:
    }
    throw new UnsupportedOperationException("type of one container has an unknown value");
  }
}
