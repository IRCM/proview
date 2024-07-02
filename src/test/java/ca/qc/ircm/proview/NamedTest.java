package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Named}.
 */
public class NamedTest {
  @Test
  public void incrementLastNumber() {
    assertEquals("abc2", Named.incrementLastNumber("abc1"));
    assertEquals("abc3", Named.incrementLastNumber("abc2"));
    assertEquals("abc10", Named.incrementLastNumber("abc9"));
    assertEquals("abc02", Named.incrementLastNumber("abc01"));
    assertEquals("abc10", Named.incrementLastNumber("abc09"));
    assertEquals("abc010", Named.incrementLastNumber("abc009"));
    assertEquals("abc2a", Named.incrementLastNumber("abc1a"));
    assertEquals("abc3a", Named.incrementLastNumber("abc2a"));
    assertEquals("abc10a", Named.incrementLastNumber("abc9a"));
    assertEquals("abc02a", Named.incrementLastNumber("abc01a"));
    assertEquals("abc10a", Named.incrementLastNumber("abc09a"));
    assertEquals("abc010a", Named.incrementLastNumber("abc009a"));
    assertEquals("abc_2", Named.incrementLastNumber("abc_1"));
    assertEquals("abc_3", Named.incrementLastNumber("abc_2"));
    assertEquals("abc_10", Named.incrementLastNumber("abc_9"));
    assertEquals("abc_02", Named.incrementLastNumber("abc_01"));
    assertEquals("abc_10", Named.incrementLastNumber("abc_09"));
    assertEquals("abc_010", Named.incrementLastNumber("abc_009"));
    assertEquals("abc_2_a", Named.incrementLastNumber("abc_1_a"));
    assertEquals("abc_3_a", Named.incrementLastNumber("abc_2_a"));
    assertEquals("abc_10_a", Named.incrementLastNumber("abc_9_a"));
    assertEquals("abc_02_a", Named.incrementLastNumber("abc_01_a"));
    assertEquals("abc_10_a", Named.incrementLastNumber("abc_09_a"));
    assertEquals("abc_010_a", Named.incrementLastNumber("abc_009_a"));
  }
}
