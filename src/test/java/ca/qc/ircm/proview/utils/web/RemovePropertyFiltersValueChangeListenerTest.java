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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.function.Consumer;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class RemovePropertyFiltersValueChangeListenerTest {
  private RemovePropertyFiltersValueChangeListener listener;
  @Mock
  private Container.Filterable container;
  @Mock
  private ValueChangeEvent event;
  @Mock
  private Property<Integer> eventProperty;
  @Mock
  private TextChangeEvent textChangeEvent;
  @Mock
  private Consumer<Object> addFilter;
  private String propertyId = "testPropertyId";

  @Before
  public void beforeTest() {
    listener = new DefaultRemovePropertyFiltersValueChangeListener(container, propertyId);
    when(event.getProperty()).thenReturn(eventProperty);
  }

  @Test
  public void valueChange_SimpleFilterable() {
    IndexedContainer container = mock(IndexedContainer.class);
    listener = new DefaultRemovePropertyFiltersValueChangeListener(container, propertyId);
    Integer propertyValue = 20;
    when(eventProperty.getValue()).thenReturn(propertyValue);

    listener.valueChange(event);

    verify(container).removeContainerFilters(propertyId);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void valueChange_SimpleFilterable_NullValue() {
    IndexedContainer container = mock(IndexedContainer.class);
    listener = new DefaultRemovePropertyFiltersValueChangeListener(container, propertyId);
    Integer propertyValue = null;
    when(eventProperty.getValue()).thenReturn(propertyValue);

    listener.valueChange(event);

    verify(container).removeContainerFilters(propertyId);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void valueChange_Other() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));
    Integer propertyValue = 20;
    when(eventProperty.getValue()).thenReturn(propertyValue);

    listener.valueChange(event);

    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void valueChange_Other_NullValue() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));
    Integer propertyValue = null;
    when(eventProperty.getValue()).thenReturn(propertyValue);

    listener.valueChange(event);

    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void textChange_SimpleFilterable() {
    IndexedContainer container = mock(IndexedContainer.class);
    listener = new DefaultRemovePropertyFiltersValueChangeListener(container, propertyId);
    String propertyValue = "test";
    when(textChangeEvent.getText()).thenReturn(propertyValue);

    listener.textChange(textChangeEvent);

    verify(container).removeContainerFilters(propertyId);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void textChange_SimpleFilterable_NullValue() {
    IndexedContainer container = mock(IndexedContainer.class);
    listener = new DefaultRemovePropertyFiltersValueChangeListener(container, propertyId);
    String propertyValue = null;
    when(textChangeEvent.getText()).thenReturn(propertyValue);

    listener.textChange(textChangeEvent);

    verify(container).removeContainerFilters(propertyId);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void textChange_Other() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));
    String propertyValue = "test";
    when(textChangeEvent.getText()).thenReturn(propertyValue);

    listener.textChange(textChangeEvent);

    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
    verify(addFilter).accept(propertyValue);
  }

  @Test
  public void textChange_Other_NullValue() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));
    String propertyValue = null;
    when(textChangeEvent.getText()).thenReturn(propertyValue);

    listener.textChange(textChangeEvent);

    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
    verify(addFilter).accept(propertyValue);
  }

  @SuppressWarnings("serial")
  private class DefaultRemovePropertyFiltersValueChangeListener
      extends RemovePropertyFiltersValueChangeListener {
    DefaultRemovePropertyFiltersValueChangeListener(Container.Filterable container,
        Object propertyId) {
      super(container, propertyId);
    }

    @Override
    protected void addFilter(Object propertyValue) {
      addFilter.accept(propertyValue);
    }
  }
}
