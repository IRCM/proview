package ca.qc.ircm.proview.text;

import java.text.Normalizer;

public class Strings {
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
