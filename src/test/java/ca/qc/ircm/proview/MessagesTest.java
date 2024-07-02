package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link MessagesFactory}.
 */
@NonTransactionalTestAnnotations
public class MessagesTest {
  @Autowired
  private MessageSource messageSource;

  @Test
  public void messageSource_getMessage() {
    assertEquals("ProView",
        messageSource.getMessage("Constants.application.name", null, Locale.ENGLISH));
  }

  @Test
  public void message() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("Save", messages.message("save"));
  }

  @Test
  public void message_French() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("Sauvegarder", messages.message("save"));
  }

  @Test
  public void message_Replacements() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("out of the range 2 to 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ReplacementsFrench() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("doit être compris entre 2 et 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_Missing() {
    Messages messages = new Messages("Constants", Locale.ENGLISH, messageSource);
    assertEquals("!{en:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_MissingFrench() {
    Messages messages = new Messages("Constants", Locale.FRENCH, messageSource);
    assertEquals("!{fr:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_UserEmail() {
    Messages messages = new Messages("user.User", Locale.ENGLISH, messageSource);
    assertEquals("Email", messages.message("email"));
  }

  @Test
  public void message_UserEmailFrench() {
    Messages messages = new Messages("user.User", Locale.FRENCH, messageSource);
    assertEquals("Courriel", messages.message("email"));
  }
}
