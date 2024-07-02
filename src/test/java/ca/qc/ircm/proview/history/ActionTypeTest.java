package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.history.ActionType.DELETE;
import static ca.qc.ircm.proview.history.ActionType.INSERT;
import static ca.qc.ircm.proview.history.ActionType.UPDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ActionType}.
 */
public class ActionTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", ActionType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", ActionType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Insert() {
    assertEquals("Insert", INSERT.getLabel(Locale.ENGLISH));
    assertEquals("Insertion", INSERT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Update() {
    assertEquals("Update", UPDATE.getLabel(Locale.ENGLISH));
    assertEquals("Mise-à-jour", UPDATE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Delete() {
    assertEquals("Delete", DELETE.getLabel(Locale.ENGLISH));
    assertEquals("Délétion", DELETE.getLabel(Locale.FRENCH));
  }
}
