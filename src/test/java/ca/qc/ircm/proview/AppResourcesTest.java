package ca.qc.ircm.proview;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.qc.ircm.proview.user.User;
import java.util.Locale;
import java.util.MissingResourceException;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AppResources}.
 */
public class AppResourcesTest {
  private Locale locale = Locale.ENGLISH;

  @Test
  public void message_Name() {
    AppResources resources = new AppResources(Constants.class.getName(), locale);
    assertEquals("ProView", resources.message("application.name"));
  }

  @Test
  public void message_NameEnglish() {
    AppResources resources = new AppResources(Constants.class.getName(), Locale.ENGLISH);
    assertEquals("Save", resources.message("save"));
  }

  @Test
  public void message_NameFrench() {
    AppResources resources = new AppResources(Constants.class.getName(), Locale.FRENCH);
    assertEquals("Sauvegarder", resources.message("save"));
  }

  @Test
  public void message_NameReplacements() {
    AppResources resources = new AppResources(Constants.class.getName(), locale);
    assertEquals("out of the range 2 to 5", resources.message("outOfRange", 2, 5));
  }

  @Test
  public void message_NameReplacementsFrench() {
    AppResources resources = new AppResources(Constants.class.getName(), Locale.FRENCH);
    assertEquals("doit être compris entre 2 et 5", resources.message("outOfRange", 2, 5));
  }

  @Test
  public void message_NameMissing() {
    AppResources resources = new AppResources(Constants.class.getName(), locale);
    assertThrows(MissingResourceException.class, () -> {
      assertEquals("!{en:Constants.missing_name}!", resources.message("missing_name"));
    });
  }

  @Test
  public void message_NameMissingFrench() {
    AppResources resources = new AppResources(Constants.class.getName(), Locale.FRENCH);
    assertThrows(MissingResourceException.class, () -> {
      assertEquals("!{fr:Constants.missing_name}!", resources.message("missing_name"));
    });
  }

  @Test
  public void message_Class() {
    AppResources resources = new AppResources(Constants.class, locale);
    assertEquals("ProView", resources.message("application.name"));
  }

  @Test
  public void message_ClassEnglish() {
    AppResources resources = new AppResources(Constants.class, Locale.ENGLISH);
    assertEquals("Save", resources.message("save"));
  }

  @Test
  public void message_ClassFrench() {
    AppResources resources = new AppResources(Constants.class, Locale.FRENCH);
    assertEquals("Sauvegarder", resources.message("save"));
  }

  @Test
  public void message_ClassReplacements() {
    AppResources resources = new AppResources(Constants.class, locale);
    assertEquals("out of the range 2 to 5", resources.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ClassReplacementsFrench() {
    AppResources resources = new AppResources(Constants.class, Locale.FRENCH);
    assertEquals("doit être compris entre 2 et 5", resources.message("outOfRange", 2, 5));
  }

  @Test
  public void message_ClassMissing() {
    AppResources resources = new AppResources(Constants.class, locale);
    assertThrows(MissingResourceException.class, () -> {
      assertEquals("!{en:Constants.missing_name}!", resources.message("missing_name"));
    });
  }

  @Test
  public void message_ClassMissingFrench() {
    AppResources resources = new AppResources(Constants.class, Locale.FRENCH);
    assertThrows(MissingResourceException.class, () -> {
      assertEquals("!{fr:Constants.missing_name}!", resources.message("missing_name"));
    });
  }

  @Test
  public void message_UserEmail() {
    AppResources resources = new AppResources(User.class, locale);
    assertEquals("Email", resources.message("email"));
  }

  @Test
  public void message_UserEmailFrench() {
    AppResources resources = new AppResources(User.class, Locale.FRENCH);
    assertEquals("Courriel", resources.message("email"));
  }

  @Test
  public void message_UserBasenameEmail() {
    AppResources resources = new AppResources(User.class, locale);
    assertEquals("Email", resources.message("email"));
  }

  @Test
  public void message_UserBasenameEmailFrench() {
    AppResources resources = new AppResources(User.class, Locale.FRENCH);
    assertEquals("Courriel", resources.message("email"));
  }
}
