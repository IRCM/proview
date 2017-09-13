package ca.qc.ircm.proview.time;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeConverterTest {
  private TimeConverter testTimeConverter = new TestTimeConverter();

  @Test
  public void toInstant_LocalDateTime() {
    LocalDateTime dateTime1 = LocalDateTime.now();
    LocalDateTime dateTime2 = LocalDateTime.now().minusMinutes(10);

    assertEquals(dateTime1.atZone(ZoneId.systemDefault()).toInstant(),
        testTimeConverter.toInstant(dateTime1));
    assertEquals(dateTime2.atZone(ZoneId.systemDefault()).toInstant(),
        testTimeConverter.toInstant(dateTime2));
  }

  @Test
  public void toLocalDateTime_Instant() {
    Instant instant1 = Instant.now();
    Instant instant2 = Instant.now().minus(10, ChronoUnit.MINUTES);

    assertEquals(LocalDateTime.ofInstant(instant1, ZoneId.systemDefault()),
        testTimeConverter.toLocalDateTime(instant1));
    assertEquals(LocalDateTime.ofInstant(instant2, ZoneId.systemDefault()),
        testTimeConverter.toLocalDateTime(instant2));
  }

  private static class TestTimeConverter implements TimeConverter {
  }
}
