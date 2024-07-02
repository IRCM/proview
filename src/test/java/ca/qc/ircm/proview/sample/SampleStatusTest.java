package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.ENRICHED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.WAITING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link SampleStatus}.
 */
public class SampleStatusTest {
  @Test
  public void analysedStatuses() {
    List<SampleStatus> analysedStatuses = Arrays.asList(SampleStatus.analysedStatuses());
    assertTrue(analysedStatuses.contains(ANALYSED));
    assertTrue(analysedStatuses.contains(CANCELLED));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleStatus.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleStatus.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Waiting() {
    assertEquals("Waiting for samples", WAITING.getLabel(Locale.ENGLISH));
    assertEquals("Échantillons en attente", WAITING.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Received() {
    assertEquals("Samples received", RECEIVED.getLabel(Locale.ENGLISH));
    assertEquals("Échantillons reçus", RECEIVED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Digest() {
    assertEquals("Digested", DIGESTED.getLabel(Locale.ENGLISH));
    assertEquals("Digéré", DIGESTED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Enrich() {
    assertEquals("Enriched", ENRICHED.getLabel(Locale.ENGLISH));
    assertEquals("Enrichit", ENRICHED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Analysed() {
    assertEquals("Analyzed", ANALYSED.getLabel(Locale.ENGLISH));
    assertEquals("Analysé", ANALYSED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Cancelled() {
    assertEquals("Cancelled", CANCELLED.getLabel(Locale.ENGLISH));
    assertEquals("Annulé", CANCELLED.getLabel(Locale.FRENCH));
  }
}
