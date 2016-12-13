/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
