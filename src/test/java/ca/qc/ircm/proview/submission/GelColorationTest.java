package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.GelColoration.COOMASSIE;
import static ca.qc.ircm.proview.submission.GelColoration.OTHER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER_INVITROGEN;
import static ca.qc.ircm.proview.submission.GelColoration.SYPRO;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link GelColoration}.
 */
public class GelColorationTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", GelColoration.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucune", GelColoration.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Coomassie() {
    assertEquals("Coomassie", COOMASSIE.getLabel(Locale.ENGLISH));
    assertEquals("Coomassie", COOMASSIE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Sypro() {
    assertEquals("Sypro", SYPRO.getLabel(Locale.ENGLISH));
    assertEquals("Sypro", SYPRO.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Silver() {
    assertEquals("Silver", SILVER.getLabel(Locale.ENGLISH));
    assertEquals("Silver", SILVER.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_SilverInvitrogen() {
    assertEquals("Silver (Invitrogen)", SILVER_INVITROGEN.getLabel(Locale.ENGLISH));
    assertEquals("Silver (Invitrogen)", SILVER_INVITROGEN.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other coloration", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre coloration", OTHER.getLabel(Locale.FRENCH));
  }
}
