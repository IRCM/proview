package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.OTHER;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class ProteolyticDigestionTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", ProteolyticDigestion.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucun", ProteolyticDigestion.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Trypsine() {
    assertEquals("Trypsin", TRYPSIN.getLabel(Locale.ENGLISH));
    assertEquals("Trypsine", TRYPSIN.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Digested() {
    assertEquals("Already digested", DIGESTED.getLabel(Locale.ENGLISH));
    assertEquals("Déjà digéré", DIGESTED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre", OTHER.getLabel(Locale.FRENCH));
  }
}
