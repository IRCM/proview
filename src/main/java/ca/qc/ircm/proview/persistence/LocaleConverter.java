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

package ca.qc.ircm.proview.persistence;

import java.util.Locale;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocaleConverter implements AttributeConverter<Locale, String> {
  private static final String SEPARATOR = "_";

  @Override
  public String convertToDatabaseColumn(Locale value) {
    if (value == null) {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    builder.append(value.getLanguage());
    builder.append(SEPARATOR);
    if (value.getCountry() != null && !value.getCountry().isEmpty()) {
      builder.append(value.getCountry());
      builder.append(SEPARATOR);
      if (value.getVariant() != null && !value.getVariant().isEmpty()) {
        builder.append(value.getVariant());
        builder.append(SEPARATOR);
      }
    }
    builder.delete(builder.length() - SEPARATOR.length(), builder.length());
    return builder.toString();
  }

  @Override
  public Locale convertToEntityAttribute(String value) {
    if (value == null || value.isEmpty()) {
      return null;
    }

    String[] values = value.split(SEPARATOR, 3);
    String language = values[0];
    String country = "";
    if (values.length > 1) {
      country = values[1];
    }
    String variant = "";
    if (values.length > 2) {
      variant = values[2];
    }
    return new Locale(language, country, variant);
  }
}
