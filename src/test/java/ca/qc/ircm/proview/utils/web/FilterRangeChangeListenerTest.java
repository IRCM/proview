package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
  public void addNonNullFilter() {
    Range<Integer> range = Range.open(10, 20);
    when(eventProperty.getValue()).thenReturn(range);

    listener.valueChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof RangeFilter);
    RangeFilter<?> rangeFilter = (RangeFilter<?>) filter;
    assertEquals(propertyId, rangeFilter.getPropertyId());
    assertEquals(range, rangeFilter.getRange());
  }
}
