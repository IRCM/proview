package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.history.ActionType.DELETE;
import static ca.qc.ircm.proview.history.ActionType.INSERT;
import static ca.qc.ircm.proview.history.ActionType.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link ActionType}.
 */
@NonTransactionalTestAnnotations
public class ActionTypeTest {
  private static final String ACTION_TYPE_PREFIX = messagePrefix(ActionType.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(ACTION_TYPE_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(ACTION_TYPE_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Insert() {
    assertEquals("Insert",
        messageSource.getMessage(ACTION_TYPE_PREFIX + INSERT.name(), null, Locale.ENGLISH));
    assertEquals("Insertion",
        messageSource.getMessage(ACTION_TYPE_PREFIX + INSERT.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Update() {
    assertEquals("Update",
        messageSource.getMessage(ACTION_TYPE_PREFIX + UPDATE.name(), null, Locale.ENGLISH));
    assertEquals("Mise-à-jour",
        messageSource.getMessage(ACTION_TYPE_PREFIX + UPDATE.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Delete() {
    assertEquals("Delete",
        messageSource.getMessage(ACTION_TYPE_PREFIX + DELETE.name(), null, Locale.ENGLISH));
    assertEquals("Délétion",
        messageSource.getMessage(ACTION_TYPE_PREFIX + DELETE.name(), null, Locale.FRENCH));
  }
}
