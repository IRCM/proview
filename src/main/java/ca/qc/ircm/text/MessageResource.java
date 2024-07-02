package ca.qc.ircm.text;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * {@link ResourceBundle} that formats messages using {@link MessageFormat}.
 */
public class MessageResource {
  private final ResourceBundle resources;

  public MessageResource(String baseName, Locale locale) {
    resources = ResourceBundle.getBundle(baseName, locale);
  }

  public MessageResource(Class<?> baseClass, Locale locale) {
    resources = ResourceBundle.getBundle(baseClass.getName(), locale);
  }

  /**
   * Returns message.
   *
   * @param key
   *          message's key
   * @param replacements
   *          message's replacements
   * @return message
   */
  public String message(String key, Object... replacements) {
    return MessageFormat.format(resources.getString(key), replacements);
  }
}
