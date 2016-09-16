package ca.qc.ircm.proview.utils.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class RangeFilterTest {
  private RangeFilter<Integer> filter;
  @Mock
  private Item item;
  @Mock
  private Property<Integer> itemProperty;

  @Test
  public void passesFilter_True() {
    String propertyId = "testPropertyId";
    Range<Integer> range = Range.singleton(20);
    filter = new RangeFilter<>(propertyId, range);
    when(item.getItemProperty(any())).thenReturn(itemProperty);
    when(itemProperty.getValue()).thenReturn(20);

    assertTrue(filter.passesFilter(propertyId, item));

    verify(item).getItemProperty(propertyId);
    verify(itemProperty).getValue();
  }

  @Test
  public void passesFilter_False() {
    String propertyId = "testPropertyId";
    Range<Integer> range = Range.singleton(10);
    filter = new RangeFilter<>(propertyId, range);
    when(item.getItemProperty(any())).thenReturn(itemProperty);
    when(itemProperty.getValue()).thenReturn(20);

    assertFalse(filter.passesFilter(propertyId, item));

    verify(item).getItemProperty(propertyId);
    verify(itemProperty).getValue();
  }

  @Test
  public void appliesToProperty() {
    String propertyId = "testPropertyId";
    Range<Integer> range = Range.all();
    filter = new RangeFilter<>(propertyId, range);
    assertTrue(filter.appliesToProperty(propertyId));
    assertFalse(filter.appliesToProperty("otherPropertyId"));

    propertyId = "anotherPropertyId";
    filter = new RangeFilter<>(propertyId, range);
    assertTrue(filter.appliesToProperty(propertyId));
    assertFalse(filter.appliesToProperty("otherPropertyId"));
  }

  @Test
  public void getPropertyId() {
    String propertyId = "testPropertyId";
    Range<Integer> range = Range.all();
    filter = new RangeFilter<>(propertyId, range);

    assertEquals(propertyId, filter.getPropertyId());
  }

  @Test
  public void getRange() {
    String propertyId = "testPropertyId";
    Range<Integer> range = Range.all();
    filter = new RangeFilter<>(propertyId, range);

    assertEquals(range, filter.getRange());
  }
}
