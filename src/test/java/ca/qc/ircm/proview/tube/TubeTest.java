package ca.qc.ircm.proview.tube;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Tube}.
 */
@NonTransactionalTestAnnotations
public class TubeTest {
  @Test
  public void getName() {
    assertEquals("test_tube", new Tube(1L, "test_tube").getName());
  }

  @Test
  public void getFullName() {
    assertEquals("test_tube", new Tube(1L, "test_tube").getFullName());
    assertEquals("test_tube", new Tube(1L, "test_tube").getFullName());
  }
}
