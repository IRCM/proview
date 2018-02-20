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

package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.DatabaseLogUtil.DatabaseBoolean;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Converts an object to a string value to save in database.
 */
public class DatabaseConverter {
  /**
   * Converts an object to a string value to save in database.
   *
   * @param value
   *          object to convert to string
   * @return string value to save in database
   */
  public String convert(Object value) {
    if (value == null) {
      return null;
    }

    String converterValue;
    if (value instanceof Boolean) {
      converterValue = DatabaseBoolean.get((Boolean) value).databaseValue;
    } else if (value instanceof Date) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      converterValue = dateFormat.format((Date) value);
    } else if (value instanceof Instant) {
      DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
      converterValue =
          instantFormatter.format(LocalDateTime.ofInstant((Instant) value, ZoneId.systemDefault()));
    } else {
      converterValue = String.valueOf(value);
    }
    converterValue = DatabaseLogUtil.reduceLength(converterValue, 255);
    return converterValue;
  }
}
