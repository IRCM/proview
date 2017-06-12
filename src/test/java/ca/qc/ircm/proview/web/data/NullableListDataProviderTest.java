package ca.qc.ircm.proview.web.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

public class NullableListDataProviderTest {
  private NullableListDataProvider<String> nullableListDataProvider;

  @Before
  public void beforeTest() {
    nullableListDataProvider = new NullableListDataProvider<>(Arrays.asList(null, "a", "b"));
  }

  @Test
  public void getId() {
    assertEquals(Optional.empty(), nullableListDataProvider.getId(null));
    assertEquals("a", nullableListDataProvider.getId("a"));
    assertEquals("b", nullableListDataProvider.getId("b"));
  }
}
