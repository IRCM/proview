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

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import ca.qc.ircm.proview.sample.SampleStatus;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SampleStatusConverterTest {
  private SampleStatusConverter converter;
  private Locale locale = Locale.ENGLISH;

  @Before
  public void beforeTest() {
    converter = new SampleStatusConverter();
  }

  @Test
  public void convertToModel() {
    SampleStatus status =
        converter.convertToModel(ANALYSED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(ANALYSED, status);
  }

  @Test
  public void convertToModel_Received() {
    SampleStatus status =
        converter.convertToModel(RECEIVED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(RECEIVED, status);
  }

  @Test
  public void convertToModel_French() {
    Locale locale = Locale.FRENCH;

    SampleStatus status =
        converter.convertToModel(ANALYSED.getLabel(locale), SampleStatus.class, locale);

    assertEquals(ANALYSED, status);
  }

  @Test
  public void convertToModel_Null() {
    SampleStatus status = converter.convertToModel(null, SampleStatus.class, locale);

    assertNull(status);
  }

  @Test
  public void convertToModel_Empty() {
    SampleStatus status = converter.convertToModel("", SampleStatus.class, locale);

    assertNull(status);
  }

  @Test(expected = ConversionException.class)
  public void convertToModel_Invalid() {
    converter.convertToModel("invalid status", SampleStatus.class, locale);
  }

  @Test
  public void convertToPresentation() {
  }

  @Test
  public void getModelType() {
    assertEquals(SampleStatus.class, converter.getModelType());
  }

  @Test
  public void getPresentationType() {
    assertEquals(String.class, converter.getPresentationType());
  }
}
