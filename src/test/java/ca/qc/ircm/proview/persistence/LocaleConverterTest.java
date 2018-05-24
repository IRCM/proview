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

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Before;
import org.junit.Test;

public class LocaleConverterTest {
  private LocaleConverter localeConverter;

  @Before
  public void beforeTest() {
    localeConverter = new LocaleConverter();
  }

  @Test
  public void convertToDatabaseColumn_Language() {
    assertEquals("en", localeConverter.convertToDatabaseColumn(Locale.ENGLISH));
    assertEquals("fr", localeConverter.convertToDatabaseColumn(Locale.FRENCH));
  }

  @Test
  public void convertToDatabaseColumn_Country() {
    assertEquals("en_US", localeConverter.convertToDatabaseColumn(Locale.US));
    assertEquals("fr_CA", localeConverter.convertToDatabaseColumn(Locale.CANADA_FRENCH));
  }

  @Test
  public void convertToDatabaseColumn_Variant() {
    assertEquals("en_US_usvariant",
        localeConverter.convertToDatabaseColumn(new Locale("en", "us", "usvariant")));
    assertEquals("fr_CA_frvariant",
        localeConverter.convertToDatabaseColumn(new Locale("fr", "ca", "frvariant")));
  }

  @Test
  public void convertToDatabaseColumn_MultipleVariants() {
    assertEquals("en_US_variant1_variant2",
        localeConverter.convertToDatabaseColumn(new Locale("en", "us", "variant1_variant2")));
    assertEquals("fr_CA_variant1_variant2",
        localeConverter.convertToDatabaseColumn(new Locale("fr", "ca", "variant1_variant2")));
  }

  @Test
  public void convertToEntityAttribute_Language() {
    assertEquals(Locale.ENGLISH, localeConverter.convertToEntityAttribute("en"));
    assertEquals(Locale.FRENCH, localeConverter.convertToEntityAttribute("fr"));
  }

  @Test
  public void convertToEntityAttribute_Country() {
    assertEquals(Locale.US, localeConverter.convertToEntityAttribute("en_US"));
    assertEquals(Locale.CANADA_FRENCH, localeConverter.convertToEntityAttribute("fr_CA"));
  }

  @Test
  public void convertToEntityAttribute_CountryLowerCase() {
    assertEquals(Locale.US, localeConverter.convertToEntityAttribute("en_us"));
    assertEquals(Locale.CANADA_FRENCH, localeConverter.convertToEntityAttribute("fr_ca"));
  }

  @Test
  public void convertToEntityAttribute_Variant() {
    assertEquals(new Locale("en", "US", "usvariant"),
        localeConverter.convertToEntityAttribute("en_US_usvariant"));
    assertEquals(new Locale("fr", "CA", "frvariant"),
        localeConverter.convertToEntityAttribute("fr_CA_frvariant"));
  }

  @Test
  public void convertToEntityAttribute_MultipleVariants() {
    assertEquals(new Locale("en", "US", "variant1_variant2"),
        localeConverter.convertToEntityAttribute("en_US_variant1_variant2"));
    assertEquals(new Locale("fr", "CA", "variant1_variant2"),
        localeConverter.convertToEntityAttribute("fr_CA_variant1_variant2"));
  }
}
