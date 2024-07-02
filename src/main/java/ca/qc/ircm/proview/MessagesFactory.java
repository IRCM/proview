package ca.qc.ircm.proview;

import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Creates instances of {@link Messages}.
 */
@Component
public class MessagesFactory {
  /**
   * Strip this key from class name, if it matches.
   */
  private static final String STRIP_KEY =
      Pattern.quote(AppResources.class.getPackage().getName() + ".");
  /**
   * Message source.
   */
  private final MessageSource messageSource;

  @Autowired
  protected MessagesFactory(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public Messages with(String baseName, Locale locale) {
    String prefix = baseName.replaceFirst(STRIP_KEY, "");
    return new Messages(prefix, locale, messageSource);
  }

  public Messages with(Class<?> baseClass, Locale locale) {
    String prefix = baseClass.getName().replaceFirst(STRIP_KEY, "");
    return new Messages(prefix, locale, messageSource);
  }

}
