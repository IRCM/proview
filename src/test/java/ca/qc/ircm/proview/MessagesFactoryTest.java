package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import ca.qc.ircm.proview.user.User;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link MessagesFactory}.
 */
@NonTransactionalTestAnnotations
public class MessagesFactoryTest {
  @Autowired
  private MessagesFactory messagesFactory;
  @Autowired
  private MessageSource messageSource;

  @Test
  public void messageSource_getMessage() {
    assertEquals("ProView",
        messageSource.getMessage("Constants.application.name", null, Locale.ENGLISH));
  }

  @Test
  public void message_Class() {
    Messages messages = messagesFactory.with(Constants.class, Locale.ENGLISH);
    assertEquals("Save", messages.message("save"));
  }

  @Test
  public void message_ClassFrench() {
    Messages messages = messagesFactory.with(Constants.class, Locale.FRENCH);
    assertEquals("Sauvegarder", messages.message("save"));
  }

  @Test
  public void message_ClassReplacements() {
    Messages messages = messagesFactory.with(Constants.class, Locale.ENGLISH);
    assertEquals("out of the range 2 to 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ClassReplacementsFrench() {
    Messages messages = messagesFactory.with(Constants.class, Locale.FRENCH);
    assertEquals("doit être compris entre 2 et 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ClassMissing() {
    Messages messages = messagesFactory.with(Constants.class, Locale.ENGLISH);
    assertEquals("!{en:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_ClassMissingFrench() {
    Messages messages = messagesFactory.with(Constants.class, Locale.FRENCH);
    assertEquals("!{fr:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_Name() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.ENGLISH);
    assertEquals("ProView", messages.message("application.name"));
  }

  @Test
  public void message_NameFrench() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.FRENCH);
    assertEquals("Sauvegarder", messages.message("save"));
  }

  @Test
  public void message_NameReplacements() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.ENGLISH);
    assertEquals("out of the range 2 to 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_NameReplacementsFrench() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.FRENCH);
    assertEquals("doit être compris entre 2 et 5", messages.message("outOfRange", 2, 5));
  }

  @Test
  public void message_NameMissing() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.ENGLISH);
    assertEquals("!{en:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_NameMissingFrench() {
    Messages messages = messagesFactory.with(Constants.class.getName(), Locale.FRENCH);
    assertEquals("!{fr:Constants.missing_name}!", messages.message("missing_name"));
  }

  @Test
  public void message_UserEmail() {
    Messages messages = messagesFactory.with(User.class, Locale.ENGLISH);
    assertEquals("Email", messages.message("email"));
  }

  @Test
  public void message_UserEmailFrench() {
    Messages messages = messagesFactory.with(User.class, Locale.FRENCH);
    assertEquals("Courriel", messages.message("email"));
  }

  @Test
  public void message_UserBasenameEmail() {
    Messages messages = messagesFactory.with(User.class.getName(), Locale.ENGLISH);
    assertEquals("Email", messages.message("email"));
  }

  @Test
  public void message_UserBasenameEmailFrench() {
    Messages messages = messagesFactory.with(User.class.getName(), Locale.FRENCH);
    assertEquals("Courriel", messages.message("email"));
  }
}
