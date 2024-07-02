package ca.qc.ircm.proview.history;

import ca.qc.ircm.proview.history.DatabaseLogUtil.DatabaseBoolean;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts an object to a string value to save in database.
 */
public class DatabaseConverter {
  /**
   * Converts an object to a string value to save in database.
   *
   * @param value
   *          object to convert to string
   * @return string value to save in database
   */
  public Optional<String> convert(Object value) {
    if (value == null) {
      return Optional.empty();
    }

    String converterValue;
    if (value instanceof Boolean) {
      converterValue = DatabaseBoolean.get((Boolean) value).databaseValue;
    } else if (value instanceof Date) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      converterValue = dateFormat.format((Date) value);
    } else if (value instanceof Instant) {
      DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
      converterValue =
          instantFormatter.format(LocalDateTime.ofInstant((Instant) value, ZoneId.systemDefault()));
    } else if (value instanceof LocalDateTime) {
      DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
      converterValue = localDateTimeFormatter.format((LocalDateTime) value);
    } else if (value instanceof LocalDate) {
      DateTimeFormatter localDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
      converterValue = localDateFormatter.format((LocalDate) value);
    } else if (value instanceof Collection) {
      Collection<?> collection = ((Collection<?>) value);
      if (collection.isEmpty()) {
        return Optional.empty();
      }
      converterValue =
          collection.stream().map(o -> Objects.toString(o, "")).collect(Collectors.joining(","));
    } else {
      converterValue = String.valueOf(value);
    }
    converterValue = reduceLength(converterValue, 255);
    return Optional.of(converterValue);
  }

  /**
   * Reduces the length of input to the number to bytes specified (byteCount) using UTF-8 encoding.
   * If input already fits in the number to bytes specified, then input is returned.
   *
   * @param input
   *          input string to reduce to specified number to bytes
   * @param byteCount
   *          number to bytes the input string must be reduced to
   * @return input reduced to the number to bytes specified
   */
  private String reduceLength(String input, int byteCount) {
    try {
      while (input.getBytes("UTF-8").length > byteCount) {
        input = input.substring(0, input.length() - 1);
      }
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError(
          "UTF-8 is a required charset, but is unkown to this version of Java");
    }
    return input;
  }
}
