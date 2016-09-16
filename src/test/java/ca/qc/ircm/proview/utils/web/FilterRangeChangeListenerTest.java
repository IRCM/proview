package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FilterRangeChangeListenerTest {
  private FilterRangeChangeListener listener;
  @Mock
  private Container.Filterable container;
  @Mock
  private ValueChangeEvent event;
  @Mock
  private Property<Range<Integer>> eventProperty;
  @Captor
  private ArgumentCaptor<Filter> filterCaptor;
  private String propertyId = "testPropertyId";

  @Before
  public void beforeTest() {
    listener = new FilterRangeChangeListener(container, propertyId);
    when(event.getProperty()).thenReturn(eventProperty);
  }

  @Test
  public void valueChange_NullRange() {
    Range<Integer> range = null;
    when(eventProperty.getValue()).thenReturn(range);

    listener.valueChange(event);

    verify(container, never()).addContainerFilter(filterCaptor.capture());
  }

  @Test
  public void valueChange_NullRangeId() {
    Range<Integer> nullId = Range.all();
    listener = new FilterRangeChangeListener(container, propertyId, nullId);
    when(eventProperty.getValue()).thenReturn(nullId);

    listener.valueChange(event);

    verify(container, never()).addContainerFilter(filterCaptor.capture());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void valueChange_NullRangeId_DifferentType() {
    Integer nullId = -1;
    Property<Object> eventProperty = mock(Property.class);
    when(event.getProperty()).thenReturn(eventProperty);
    listener = new FilterRangeChangeListener(container, propertyId, nullId);
    when(eventProperty.getValue()).thenReturn(nullId);

    listener.valueChange(event);

    verify(container, never()).addContainerFilter(filterCaptor.capture());
  }

  @Test
  public void valueChange_Range() {
    Range<Integer> range = Range.open(10, 20);
    when(eventProperty.getValue()).thenReturn(range);

    listener.valueChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof RangeFilter);
    RangeFilter<?> rangeFilter = (RangeFilter<?>) filter;
    assertEquals(range, rangeFilter.getRange());
  }

  @Test
  public void valueChange_RemoveFilters_NullRange() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    Range<Integer> range = null;
    when(eventProperty.getValue()).thenReturn(range);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));

    listener.valueChange(event);

    assertTrue(listener instanceof RemovePropertyFiltersValueChangeListener);
    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
  }

  @Test
  public void valueChange_RemoveFilters_Range() {
    Filter filter1 = mock(Filter.class);
    when(filter1.appliesToProperty(propertyId)).thenReturn(true);
    Filter filter2 = mock(Filter.class);
    when(filter2.appliesToProperty(propertyId)).thenReturn(false);
    Filter filter3 = mock(Filter.class);
    when(filter3.appliesToProperty(propertyId)).thenReturn(true);
    Range<Integer> range = Range.open(10, 20);
    when(eventProperty.getValue()).thenReturn(range);
    when(container.getContainerFilters()).thenReturn(Arrays.asList(filter1, filter2, filter3));

    listener.valueChange(event);

    assertTrue(listener instanceof RemovePropertyFiltersValueChangeListener);
    verify(container).removeContainerFilter(filter1);
    verify(container).removeContainerFilter(filter3);
  }
}
