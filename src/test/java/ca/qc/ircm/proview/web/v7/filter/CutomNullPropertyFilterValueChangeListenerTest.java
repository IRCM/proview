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

package ca.qc.ircm.proview.web.v7.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.v7.filter.CutomNullPropertyFilterValueChangeListener;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.function.Consumer;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class CutomNullPropertyFilterValueChangeListenerTest {
  private CutomNullPropertyFilterValueChangeListener listener;
  @Mock
  private Container.Filterable container;
  @Mock
  private ValueChangeEvent event;
  @Mock
  private Property<Integer> eventProperty;
  @Mock
  private Consumer<Object> addNonNullFilter;
  private String propertyId = "testPropertyId";
  private Integer acceptAllId = 20;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    listener =
        new DefaultCutomNullPropertyFilterValueChangeListener(container, propertyId, acceptAllId);
    when(event.getProperty()).thenReturn(eventProperty);
  }

  @Test
  public void addFilter_NullValue() {
    when(eventProperty.getValue()).thenReturn(null);

    listener.valueChange(event);

    verify(addNonNullFilter, never()).accept(any());
  }

  @Test
  public void addFilter_AcceptAllId() {
    when(eventProperty.getValue()).thenReturn(acceptAllId);

    listener.valueChange(event);

    verify(addNonNullFilter, never()).accept(any());
  }

  @Test
  public void addFilter_Other() {
    Integer otherId = acceptAllId + 1;
    when(eventProperty.getValue()).thenReturn(otherId);

    listener.valueChange(event);

    verify(addNonNullFilter).accept(otherId);
  }

  @Test
  public void addFilter_OtherDifferentType() {
    listener = new DefaultCutomNullPropertyFilterValueChangeListener(container, propertyId, 2.0);
    Integer otherId = acceptAllId + 1;
    when(eventProperty.getValue()).thenReturn(otherId);

    listener.valueChange(event);

    verify(addNonNullFilter).accept(otherId);
  }

  @SuppressWarnings("serial")
  private class DefaultCutomNullPropertyFilterValueChangeListener
      extends CutomNullPropertyFilterValueChangeListener {
    DefaultCutomNullPropertyFilterValueChangeListener(Container.Filterable container,
        Object propertyId) {
      super(container, propertyId);
    }

    DefaultCutomNullPropertyFilterValueChangeListener(Container.Filterable container,
        Object propertyId, Object acceptAllId) {
      super(container, propertyId, acceptAllId);
    }

    @Override
    protected void addNonNullFilter(Object propertyValue) {
      addNonNullFilter.accept(propertyValue);
    }
  }
}
