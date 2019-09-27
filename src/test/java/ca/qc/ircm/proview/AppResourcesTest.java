package ca.qc.ircm.proview;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.web.WebConstants;
import java.util.Locale;
import org.junit.Test;

public class AppResourcesTest {
  private Locale locale = Locale.ENGLISH;

  @Test
  public void message_Name() {
    AppResources resources = new AppResources(WebConstants.class.getName(), locale);
    assertEquals("ProView", resources.message("application.name"));
  }

  @Test
  public void message_NameEnglish() {
    AppResources resources = new AppResources(WebConstants.class.getName(), Locale.ENGLISH);
    assertEquals("Save", resources.message("save"));
  }

  @Test
  public void message_NameFrench() {
    AppResources resources = new AppResources(WebConstants.class.getName(), Locale.FRENCH);
    assertEquals("Sauvegarder", resources.message("save"));
  }

  @Test
  public void message_Class() {
    AppResources resources = new AppResources(WebConstants.class, locale);
    assertEquals("ProView", resources.message("application.name"));
  }

  @Test
  public void message_ClassEnglish() {
    AppResources resources = new AppResources(WebConstants.class, Locale.ENGLISH);
    assertEquals("Save", resources.message("save"));
  }

  @Test
  public void message_ClassFrench() {
    AppResources resources = new AppResources(WebConstants.class, Locale.FRENCH);
    assertEquals("Sauvegarder", resources.message("save"));
  }
}
