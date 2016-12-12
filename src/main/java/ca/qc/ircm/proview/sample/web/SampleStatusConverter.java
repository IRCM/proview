package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.SampleStatus;
import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * Converter for sample's status.
 */
public class SampleStatusConverter implements Converter<String, SampleStatus> {
  private static final long serialVersionUID = 5154656880871887291L;

  @Override
  public SampleStatus convertToModel(String value, Class<? extends SampleStatus> targetType,
      Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
    if (value == null || value.isEmpty()) {
      return null;
    }

    for (SampleStatus status : SampleStatus.values()) {
      if (status.getLabel(locale).equals(value)) {
        return status;
      }
    }
    throw new ConversionException(
        "Could not convert " + value + " to " + SampleStatus.class.getName());
  }

  @Override
  public String convertToPresentation(SampleStatus value, Class<? extends String> targetType,
      Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
    return value != null ? value.getLabel(locale) : "";
  }

  @Override
  public Class<SampleStatus> getModelType() {
    return SampleStatus.class;
  }

  @Override
  public Class<String> getPresentationType() {
    return String.class;
  }
}
