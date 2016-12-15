package ca.qc.ircm.proview.plate;

import ca.qc.ircm.proview.sample.Sample;

import java.util.Arrays;
import java.util.List;

public class PlateFilterBuilder {
  private static class Filter implements PlateFilter {
    private Plate.Type type;
    private List<Sample> containsAnySamples;

    @Override
    public Plate.Type type() {
      return type;
    }

    @Override
    public List<Sample> containsAnySamples() {
      return containsAnySamples;
    }
  }

  private Filter filter = new Filter();

  public PlateFilterBuilder type(Plate.Type type) {
    filter.type = type;
    return this;
  }

  public PlateFilterBuilder containsAnySamples(Sample... samples) {
    return containsAnySamples(Arrays.asList(samples));
  }

  public PlateFilterBuilder containsAnySamples(List<Sample> samples) {
    filter.containsAnySamples = samples;
    return this;
  }

  public PlateFilter build() {
    return filter;
  }
}
