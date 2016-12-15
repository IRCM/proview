package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.sample.Sample;

import java.util.List;

/**
 * Filters plate search.
 */
public interface PlateFilter {
  public Plate.Type type();

  public List<Sample> containsAnySamples();
}
