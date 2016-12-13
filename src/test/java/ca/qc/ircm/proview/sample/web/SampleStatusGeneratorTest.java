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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class SampleStatusGeneratorTest {
  private static final String STATUS = submissionSample.status.getMetadata().getName();
  private SampleStatusGenerator generator;
  private SampleStatus status;
  private SubmissionSample sample;
  private BeanItem<SubmissionSample> sampleItem;
  private Locale locale = Locale.ENGLISH;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    generator = new SampleStatusGenerator(() -> locale);
    sample = new SubmissionSample();
    status = ANALYSED;
    sample.setStatus(status);
    sampleItem = new BeanItem<>(sample);
  }

  @Test
  public void getValue() {
    String status = generator.getValue(sampleItem, sample, STATUS);

    assertEquals(this.status.getLabel(locale), status);
  }

  @Test
  public void getValue_Received() {
    SampleStatus status = RECEIVED;
    sample.setStatus(status);

    String statusValue = generator.getValue(sampleItem, sample, STATUS);

    assertEquals(status.getLabel(locale), statusValue);
  }

  @Test
  public void getValue_French() {
    Locale locale = Locale.FRENCH;
    generator = new SampleStatusGenerator(() -> locale);

    String status = generator.getValue(sampleItem, sample, STATUS);

    assertEquals(this.status.getLabel(locale), status);
  }

  @Test
  public void getValue_Null() {
    sample.setStatus(null);

    String status = generator.getValue(sampleItem, sample, STATUS);

    assertEquals(SampleStatus.getNullLabel(locale), status);
  }

  @Test(expected = ClassCastException.class)
  public void getValue_Invalid() {
    PropertysetItem item = new PropertysetItem();
    ObjectProperty<String> statusProperty = new ObjectProperty<>("invalid_status");
    item.addItemProperty(STATUS, statusProperty);

    generator.getValue(item, sample, STATUS);
  }

  @Test
  public void getType() {
    assertEquals(String.class, generator.getType());
  }
}
