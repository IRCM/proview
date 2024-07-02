package ca.qc.ircm.proview;

import java.util.Locale;
import org.springframework.context.MessageSource;

/**
 * Friendlier version of {@link MessageSource}.
 */
public class Messages {
  private final String prefix;
  private final Locale locale;
  private final MessageSource messageSource;

  /**
   * Creates an instance of Messages.
   * 
   * @param prefix
   *          prefix to add to keys in {@link #message(String, Object...)}
   * @param locale
   *          locale
   * @param messageSource
   *          {@link MessageSource} from which to get messages
   */
  public Messages(String prefix, Locale locale, MessageSource messageSource) {
    this.prefix = prefix;
    this.locale = locale;
    this.messageSource = messageSource;
  }

  /**
   * Returns message from resource bundle with replacements. <br>
   * If key does not exists, returns <code>!{key}!</code>.
   *
   * @param key
   *          message's key
   * @param replacements
   *          message's replacements
   * @return message, or <code>!{key}!</code> if key does not exists
   */
  public String message(String key, Object... replacements) {
    return messageSource.getMessage(prefix + "." + key, replacements,
        "'!{" + locale + ":" + prefix + "." + key + "}!'", locale);
  }
}
