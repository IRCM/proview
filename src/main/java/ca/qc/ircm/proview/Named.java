package ca.qc.ircm.proview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Objects implementing this interface have a name that is easy to understand by for users.
 * <p>
 * Object name does not need to be unique.
 * </p>
 */
public interface Named {

  /**
   * Returns object's name.
   *
   * @return name.
   */
  String getName();

  /**
   * Increments last number in name.
   *
   * @param name
   *          original name
   * @return name with last number increased
   */
  static String incrementLastNumber(String name) {
    Pattern pattern = Pattern.compile("(.*\\D)?(\\d+)(\\D*)");
    Matcher matcher = pattern.matcher(name);
    if (matcher.matches()) {
      try {
        StringBuilder builder = new StringBuilder();
        builder.append(matcher.group(1) != null ? matcher.group(1) : "");
        int number = Integer.parseInt(matcher.group(2));
        int length = matcher.group(2).length();
        StringBuilder newNumber = new StringBuilder(String.valueOf(number + 1));
        while (newNumber.length() < length) {
          newNumber.insert(0, "0");
        }
        builder.append(newNumber);
        builder.append(matcher.group(3));
        return builder.toString();
      } catch (NumberFormatException e) {
        return name;
      }
    } else {
      return name;
    }
  }
}
