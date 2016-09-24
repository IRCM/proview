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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

public class StringToInstantConverterTest {
  private StringToInstantConverter stringToInstantConverter;
  private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
  private Locale locale = Locale.getDefault();

  @Before
  public void beforeTest() {
    stringToInstantConverter = new StringToInstantConverter(formatter);
  }

  private LocalDateTime toLocalDateTime(Instant instant) {
    return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  private Instant toInstant(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
  }

  @Test
  public void convertToModel() {
    Instant expected = Instant.now();

    Instant date = stringToInstantConverter.convertToModel(
        formatter.withLocale(locale).format(toLocalDateTime(expected)), Instant.class, locale);

    assertEquals(expected, date);
  }

  @Test
  public void convertToModel_DateFormatter() {
    formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    Instant date = stringToInstantConverter.convertToModel(
        formatter.withLocale(locale).format(toLocalDateTime(expected).toLocalDate()), Instant.class,
        locale);

    assertEquals(toInstant(toLocalDateTime(expected).toLocalDate().atTime(0, 0, 0)), date);
  }

  @Test
  public void convertToModel_CustomDateTimeFormatter() {
    formatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(" abc ").append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    Instant date = stringToInstantConverter.convertToModel(
        formatter.withLocale(locale).format(toLocalDateTime(expected)), Instant.class, locale);

    assertEquals(expected, date);
  }

  @Test
  public void convertToModel_French() {
    formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
    locale = Locale.FRENCH;
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    Instant date = stringToInstantConverter.convertToModel(
        formatter.withLocale(locale).format(toLocalDateTime(expected)), Instant.class, locale);

    assertEquals(toInstant(toLocalDateTime(expected).with(ChronoField.NANO_OF_SECOND, 0)), date);
  }

  @Test
  public void convertToPresentation() {
    Instant expected = Instant.now();

    String value = stringToInstantConverter.convertToPresentation(expected, String.class, locale);

    assertEquals(formatter.withLocale(locale).format(toLocalDateTime(expected)), value);
  }

  @Test
  public void convertToPresentation_DateFormatter() {
    formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    String value = stringToInstantConverter.convertToPresentation(expected, String.class, locale);

    assertEquals(formatter.withLocale(locale).format(toLocalDateTime(expected).toLocalDate()),
        value);
  }

  @Test
  public void convertToPresentation_CustomDateTimeFormatter() {
    formatter = new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral(" abc ").append(DateTimeFormatter.ISO_LOCAL_TIME).toFormatter();
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    String value = stringToInstantConverter.convertToPresentation(expected, String.class, locale);

    assertEquals(formatter.withLocale(locale).format(toLocalDateTime(expected)), value);
  }

  @Test
  public void convertToPresentation_French() {
    formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM);
    locale = Locale.FRENCH;
    stringToInstantConverter = new StringToInstantConverter(formatter);
    Instant expected = Instant.now();

    String value = stringToInstantConverter.convertToPresentation(expected, String.class, locale);

    assertEquals(formatter.withLocale(locale).format(toLocalDateTime(expected)), value);
  }

  @Test
  public void getModelType() {
    Class<Instant> modelType = stringToInstantConverter.getModelType();

    assertEquals(Instant.class, modelType);
  }

  @Test
  public void getPresentationType() {
    Class<String> modelType = stringToInstantConverter.getPresentationType();

    assertEquals(String.class, modelType);
  }
}
