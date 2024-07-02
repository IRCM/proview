package ca.qc.ircm.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link MessageResource}.
 */
public class MessageResourceTest {
  @Test
  public void message() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.CANADA);

    String message = messageResource.message("message");

    assertEquals("This is a test", message);
  }

  @Test
  public void message_NoReplacements() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.CANADA);

    String message = messageResource.message("replacements");

    assertEquals("This is a test {0} {1} {2} {3}", message);
  }

  @Test
  public void message_Replacements() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.CANADA);

    String message = messageResource.message("replacements", "test", 1, 0.32,
        new GregorianCalendar(2015, 7, 24, 15, 23, 45).getTime());

    assertEquals("This is a test test second 32% 2015-08-24T15:23:45", message);
  }

  @Test
  public void message_Class() {
    MessageResource messageResource = new MessageResource(MessageResourceTest.class, Locale.CANADA);

    String message = messageResource.message("message");

    assertEquals("This is a test", message);
  }

  @Test
  public void message_Class_NoReplacements() {
    MessageResource messageResource = new MessageResource(MessageResourceTest.class, Locale.CANADA);

    String message = messageResource.message("replacements");

    assertEquals("This is a test {0} {1} {2} {3}", message);
  }

  @Test
  public void message_Class_Replacements() {
    MessageResource messageResource = new MessageResource(MessageResourceTest.class, Locale.CANADA);

    String message = messageResource.message("replacements", "test", 1, 0.32,
        new GregorianCalendar(2015, 7, 24, 15, 23, 45).getTime());

    assertEquals("This is a test test second 32% 2015-08-24T15:23:45", message);
  }

  @Test
  public void message_French() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.FRENCH);

    String message = messageResource.message("message");

    assertEquals("Ceci est un test", message);
  }

  @Test
  public void message_French_NoReplacements() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.FRENCH);

    String message = messageResource.message("replacements");

    assertEquals("Ceci est un test {0} {1} {2} {3}", message);
  }

  @Test
  public void message_French_Replacements() {
    MessageResource messageResource =
        new MessageResource(MessageResourceTest.class.getName(), Locale.FRENCH);

    String message = messageResource.message("replacements", "test", 1, 0.32,
        new GregorianCalendar(2015, 7, 24, 15, 23, 45).getTime());

    assertEquals("Ceci est un test test second 32% 2015-08-24T15:23:45", message);
  }
}
