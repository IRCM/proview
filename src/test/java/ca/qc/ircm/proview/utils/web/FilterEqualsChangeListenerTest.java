package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.filter.Compare;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FilterEqualsChangeListenerTest {
  private FilterEqualsChangeListener listener;
  @Mock
  private Container.Filterable container;
  @Mock
  private ValueChangeEvent event;
  @Mock
  private Property<Integer> eventProperty;
  @Captor
  private ArgumentCaptor<Filter> filterCaptor;
  private Object propertyId = "testPropertyId";
  private Integer acceptAllId = -1;

  @Before
  public void beforeTest() {
    listener = new FilterEqualsChangeListener(container, propertyId, acceptAllId);
    when(event.getProperty()).thenReturn(eventProperty);
  }

  @Test
  public void addNonNullFilter() {
    Integer value = acceptAllId + 1;
    when(eventProperty.getValue()).thenReturn(value);

    listener.valueChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof Compare.Equal);
    Compare.Equal equalsFilter = (Compare.Equal) filter;
    assertEquals(propertyId, equalsFilter.getPropertyId());
    assertEquals(value, equalsFilter.getValue());
  }
}
