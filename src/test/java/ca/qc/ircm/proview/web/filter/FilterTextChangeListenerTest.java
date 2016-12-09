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

package ca.qc.ircm.proview.web.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.filter.FilterTextChangeListener;
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
