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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class GeneratedPropertyContainerFilterTest {
  private GeneratedPropertyContainerFilter filter;
  @Mock
  private Filter originalFilter;
  @Mock
  private GeneratedPropertyContainer container;
  @Mock
  private Item generatedItem;
  @Mock
  private Item item;
  private Object itemId = "testItemId";
  private Object propertyId = "testPropertyId";

  @Before
  public void beforeTest() {
    filter = new GeneratedPropertyContainerFilter(originalFilter, container);
  }

  @Test
  public void passesFilter_True() {
    when(originalFilter.passesFilter(any(), any())).thenReturn(true);
    when(container.getItem(itemId)).thenReturn(item);

    boolean value = filter.passesFilter(itemId, generatedItem);

    assertTrue(value);
    verify(container).getItem(itemId);
    verify(originalFilter).passesFilter(itemId, item);
  }

  @Test
  public void passesFilter_False() {
    when(originalFilter.passesFilter(any(), any())).thenReturn(false);
    when(container.getItem(itemId)).thenReturn(item);

    boolean value = filter.passesFilter(itemId, generatedItem);

    assertFalse(value);
    verify(container).getItem(itemId);
    verify(originalFilter).passesFilter(itemId, item);
  }

  @Test
  public void appliesToProperty_True() {
    when(originalFilter.appliesToProperty(any())).thenReturn(true);

    assertTrue(filter.appliesToProperty(propertyId));

    verify(originalFilter).appliesToProperty(propertyId);
  }

  @Test
  public void appliesToProperty_False() {
    when(originalFilter.appliesToProperty(any())).thenReturn(false);

    assertFalse(filter.appliesToProperty(propertyId));

    verify(originalFilter).appliesToProperty(propertyId);
  }
}
