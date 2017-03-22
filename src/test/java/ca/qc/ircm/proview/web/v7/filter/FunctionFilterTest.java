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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.web.v7.filter.FunctionFilter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.function.BiFunction;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class FunctionFilterTest {
  private FunctionFilter filter;
  private String propertyId = "property";
  private String value = "value";
  @Mock
  private BiFunction<Object, Item, Object> itemValueFunction;
  @Mock
  private BiFunction<Object, Object, Boolean> compareFunction;
  @Mock
  private Object itemId;
  @Mock
  private Item item;

  @Before
  public void beforeTest() {
    filter = new FunctionFilter(propertyId, value, itemValueFunction);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void itemValueFunction() {
    BiFunction<Object, Item, Object> itemValueFunction =
        FunctionFilter.itemValueFunction(propertyId);
    Property<Object> property = mock(Property.class);
    when(item.getItemProperty(any())).thenReturn(property);
    when(property.getValue()).thenReturn(value);

    Object value = itemValueFunction.apply(itemId, item);

    assertEquals(this.value, value);
    verify(item).getItemProperty(propertyId);
    verify(property).getValue();
  }

  @Test
  public void defaultValueFunction() {
    when(itemValueFunction.apply(any(), any())).thenReturn(value, "otherValue");

    assertTrue(filter.passesFilter(itemId, item));
    assertFalse(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
  }

  @Test
  public void passesFilter_NullValue() {
    filter = new FunctionFilter(propertyId, null, itemValueFunction);
    when(itemValueFunction.apply(any(), any())).thenReturn(null, value);

    assertTrue(filter.passesFilter(itemId, item));
    assertFalse(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
  }

  @Test
  public void passesFilter_NullItemValue() {
    when(itemValueFunction.apply(any(), any())).thenReturn(null, value);

    assertFalse(filter.passesFilter(itemId, item));
    assertTrue(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
  }

  @Test
  public void passesFilter_CompareFunction() {
    filter = new FunctionFilter(propertyId, value, itemValueFunction, compareFunction);
    String itemValue = "itemValue";
    when(itemValueFunction.apply(any(), any())).thenReturn(itemValue);
    when(compareFunction.apply(any(), any())).thenReturn(true, false);

    assertTrue(filter.passesFilter(itemId, item));
    assertFalse(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
    verify(compareFunction, times(2)).apply(itemValue, value);
  }

  @Test
  public void passesFilter_CompareFunction_NullValue() {
    filter = new FunctionFilter(propertyId, null, itemValueFunction, compareFunction);
    String itemValue = "itemValue";
    when(itemValueFunction.apply(any(), any())).thenReturn(itemValue);
    when(compareFunction.apply(any(), any())).thenReturn(true, false);

    assertTrue(filter.passesFilter(itemId, item));
    assertFalse(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
    verify(compareFunction, times(2)).apply(itemValue, null);
  }

  @Test
  public void passesFilter_CompareFunction_NullItemValue() {
    filter = new FunctionFilter(propertyId, value, itemValueFunction, compareFunction);
    when(itemValueFunction.apply(any(), any())).thenReturn(null);
    when(compareFunction.apply(any(), any())).thenReturn(true, false);

    assertTrue(filter.passesFilter(itemId, item));
    assertFalse(filter.passesFilter(itemId, item));

    verify(itemValueFunction, times(2)).apply(itemId, item);
    verify(compareFunction, times(2)).apply(null, value);
  }

  @Test
  public void appliesToProperty() {
    assertTrue(filter.appliesToProperty(propertyId));
    assertFalse(filter.appliesToProperty("other_property"));
  }

  @Test
  public void getPropertyId() {
    assertEquals(propertyId, filter.getPropertyId());
  }

  @Test
  public void getValue() {
    assertEquals(value, filter.getValue());
  }

  @Test
  public void getItemValueFunction() {
    assertEquals(itemValueFunction, filter.getItemValueFunction());
  }

  @Test
  public void getComparisonFunction_DefaultCompare() {
    BiFunction<Object, Object, Boolean> comparisonFunction = filter.getComparisonFunction();

    assertNotNull(comparisonFunction);
    assertTrue(comparisonFunction.apply(null, null));
    assertFalse(comparisonFunction.apply(null, new Object()));
    assertFalse(comparisonFunction.apply(new Object(), null));
    assertTrue(comparisonFunction.apply(item, item));
    assertFalse(comparisonFunction.apply(item, "test"));
    assertTrue(comparisonFunction.apply("test", "test"));
  }

  @Test
  public void getComparisonFunction_Custom() {
    filter = new FunctionFilter(propertyId, value, itemValueFunction, compareFunction);

    assertEquals(compareFunction, filter.getComparisonFunction());
  }
}
