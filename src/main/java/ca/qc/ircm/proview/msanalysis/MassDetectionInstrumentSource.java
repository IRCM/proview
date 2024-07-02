package ca.qc.ircm.proview.msanalysis;

import ca.qc.ircm.proview.AppResources;
import java.util.List;
import java.util.Locale;
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

  private static AppResources getResources(Locale locale) {
    return new AppResources(MassDetectionInstrumentSource.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    AppResources resources = getResources(locale);
    return resources.message(name());
  }
}
