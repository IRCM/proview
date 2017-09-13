package ca.qc.ircm.proview.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Converts time instance to other time instance.
 */
public interface TimeConverter {
  default Instant toInstant(LocalDateTime dateTime) {
    return dateTime.atZone(ZoneId.systemDefault()).toInstant();
  }

  default LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }
}
