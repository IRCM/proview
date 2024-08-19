package ca.qc.ircm.proview.msanalysis;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Source for mass spectrometer.
 */
public enum MassDetectionInstrumentSource {
  ESI(true), NSI(true), LDTD(false);

  public final boolean available;

  MassDetectionInstrumentSource(boolean available) {
    this.available = available;
  }

  public static List<MassDetectionInstrumentSource> availables() {
    return Stream.of(MassDetectionInstrumentSource.values()).filter(source -> source.available)
        .collect(Collectors.toList());
  }
}
