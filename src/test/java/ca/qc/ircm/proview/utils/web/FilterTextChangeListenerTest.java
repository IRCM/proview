package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class FilterTextChangeListenerTest {
  private FilterTextChangeListener listener;
  @Mock
  private Container.Filterable container;
  @Mock
  private TextChangeEvent event;
  @Captor
  private ArgumentCaptor<Filter> filterCaptor;
  private String propertyId = "testPropertyId";

  @Before
  public void beforeTest() {
  }

  @Test
  public void addNonNullFilter() {
    boolean ignoreCase = false;
    boolean onlyMatchPrefix = false;
    listener = new FilterTextChangeListener(container, propertyId, ignoreCase, onlyMatchPrefix);
    String text = "10";
    when(event.getText()).thenReturn(text);

    listener.textChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(propertyId, stringFilter.getPropertyId());
    assertEquals(text, stringFilter.getFilterString());
    assertEquals(ignoreCase, stringFilter.isIgnoreCase());
    assertEquals(onlyMatchPrefix, stringFilter.isOnlyMatchPrefix());
  }

  @Test
  public void addNonNullFilter_IgnoreCase() {
    boolean ignoreCase = true;
    boolean onlyMatchPrefix = false;
    listener = new FilterTextChangeListener(container, propertyId, ignoreCase, onlyMatchPrefix);
    String text = "10";
    when(event.getText()).thenReturn(text);

    listener.textChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(propertyId, stringFilter.getPropertyId());
    assertEquals(text, stringFilter.getFilterString());
    assertEquals(ignoreCase, stringFilter.isIgnoreCase());
    assertEquals(onlyMatchPrefix, stringFilter.isOnlyMatchPrefix());
  }

  @Test
  public void addNonNullFilter_OnlyMatchPrefix() {
    boolean ignoreCase = false;
    boolean onlyMatchPrefix = true;
    listener = new FilterTextChangeListener(container, propertyId, ignoreCase, onlyMatchPrefix);
    String text = "10";
    when(event.getText()).thenReturn(text);

    listener.textChange(event);

    verify(container).addContainerFilter(filterCaptor.capture());
    Filter filter = filterCaptor.getValue();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(propertyId, stringFilter.getPropertyId());
    assertEquals(text, stringFilter.getFilterString());
    assertEquals(ignoreCase, stringFilter.isIgnoreCase());
    assertEquals(onlyMatchPrefix, stringFilter.isOnlyMatchPrefix());
  }
}
