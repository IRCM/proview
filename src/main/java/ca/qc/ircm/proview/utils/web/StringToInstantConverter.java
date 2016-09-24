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

package ca.qc.ircm.proview.utils.web;

import com.vaadin.data.util.converter.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Vaadin converter for instant.
 */
public class StringToInstantConverter implements Converter<String, Instant> {
  private static final long serialVersionUID = -4450445401185867742L;
  private final transient DateTimeFormatter formatter;

  public StringToInstantConverter(DateTimeFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public Instant convertToModel(String value, Class<? extends Instant> targetType, Locale locale)
      throws ConversionException {
    try {
      return value != null ? LocalDateTime.parse(value, formatter.withLocale(locale))
          .atZone(ZoneId.systemDefault()).toInstant() : null;
    } catch (DateTimeParseException e) {
      try {
        return LocalDate.parse(value, formatter.withLocale(locale)).atTime(0, 0)
            .atZone(ZoneId.systemDefault()).toInstant();
      } catch (DateTimeParseException e2) {
        throw new ConversionException(
            "Could not convert " + value + " to " + targetType.getSimpleName(), e);
      }
    }
  }

  @Override
  public String convertToPresentation(Instant value, Class<? extends String> targetType,
      Locale locale) throws ConversionException {
    return value != null ? value.atZone(ZoneId.systemDefault()).toLocalDateTime()
        .format(formatter.withLocale(locale)) : null;
  }

  @Override
  public Class<Instant> getModelType() {
    return Instant.class;
  }

  @Override
  public Class<String> getPresentationType() {
    return String.class;
  }
}
