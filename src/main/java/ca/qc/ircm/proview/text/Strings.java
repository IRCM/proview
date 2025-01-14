package ca.qc.ircm.proview.text;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utilities for String.
 */
public class Strings {
  /**
   * Concatenates properties separating them by dots.
   *
   * @param names
   *          property names
   * @return properties separated by dots
   */
  public static String property(Object... names) {
    return Arrays.asList(names).stream().map(name -> String.valueOf(name))
        .collect(Collectors.joining("."));
  }

  /**
   * Concatenates names to create a valid CSS class name.
   *
   * @param names
   *          class names
   * @return valid CSS class name based on names
   */
  public static String styleName(Object... names) {
    return Arrays.asList(names).stream().map(name -> String.valueOf(name))
        .map(name -> name.replaceAll("\\.", "-")).collect(Collectors.joining("-"));
  }

  /**
   * Removes accents from characters.
   *
   * @param value
   *          value
   * @return value without accents
   */
  public static String normalize(String value) {
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
  }
}
