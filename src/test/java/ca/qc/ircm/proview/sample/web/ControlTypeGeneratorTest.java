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

import static ca.qc.ircm.proview.sample.ControlType.NEGATIVE_CONTROL;
import static ca.qc.ircm.proview.sample.ControlType.POSITIVE_CONTROL;
import static ca.qc.ircm.proview.sample.QControl.control;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlType;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class ControlTypeGeneratorTest {
  private static final String CONTROL_TYPE = control.controlType.getMetadata().getName();
  private ControlTypeGenerator generator;
  private ControlType type;
  private Control sample;
  private BeanItem<Control> sampleItem;
  private Locale locale = Locale.ENGLISH;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    generator = new ControlTypeGenerator(() -> locale);
    sample = new Control();
    type = NEGATIVE_CONTROL;
    sample.setControlType(type);
    sampleItem = new BeanItem<>(sample);
  }

  @Test
  public void getValue() {
    String status = generator.getValue(sampleItem, sample, CONTROL_TYPE);

    assertEquals(this.type.getLabel(locale), status);
  }

  @Test
  public void getValue_Received() {
    ControlType type = POSITIVE_CONTROL;
    sample.setControlType(type);

    String status = generator.getValue(sampleItem, sample, CONTROL_TYPE);

    assertEquals(type.getLabel(locale), status);
  }

  @Test
  public void getValue_French() {
    Locale locale = Locale.FRENCH;
    generator = new ControlTypeGenerator(() -> locale);

    String status = generator.getValue(sampleItem, sample, CONTROL_TYPE);

    assertEquals(this.type.getLabel(locale), status);
  }

  @Test
  public void getValue_Null() {
    sample.setControlType(null);

    String status = generator.getValue(sampleItem, sample, CONTROL_TYPE);

    assertEquals(ControlType.getNullLabel(locale), status);
  }

  @Test(expected = ClassCastException.class)
  public void getValue_Invalid() {
    PropertysetItem item = new PropertysetItem();
    ObjectProperty<String> statusProperty = new ObjectProperty<>("invalid_status");
    item.addItemProperty(CONTROL_TYPE, statusProperty);

    generator.getValue(item, sample, CONTROL_TYPE);
  }

  @Test
  public void getType() {
    assertEquals(String.class, generator.getType());
  }
}
