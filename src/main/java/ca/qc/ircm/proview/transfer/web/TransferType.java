package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;

import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.utils.MessageResource;

import java.util.Locale;

/**
 * Types of transfer.
 */
public enum TransferType {
  PLATE_TO_PLATE(WELL, WELL), TUBES_TO_PLATE(TUBE, WELL), TUBES_TO_TUBES(TUBE, TUBE);

  public final SampleContainerType sourceType;
  public final SampleContainerType destinationType;

  TransferType(SampleContainerType sourceType, SampleContainerType destinationType) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
  }

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(TransferType.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
