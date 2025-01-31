package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.ENRICHED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.WAITING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Tests for {@link SampleStatus}.
 */
@NonTransactionalTestAnnotations
public class SampleStatusTest {

  private static final String SAMPLE_STATUS_PREFIX = messagePrefix(SampleStatus.class);
  @Autowired
  private MessageSource messageSource;

  @Test
  public void analysedStatuses() {
    List<SampleStatus> analysedStatuses = Arrays.asList(SampleStatus.analysedStatuses());
    assertTrue(analysedStatuses.contains(ANALYSED));
    assertTrue(analysedStatuses.contains(CANCELLED));
  }

  @Test
  public void getNullLabel() {
    assertEquals("Undetermined",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + "NULL", null, Locale.ENGLISH));
    assertEquals("Indéterminé",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + "NULL", null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Waiting() {
    assertEquals("Waiting for samples",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + WAITING.name(), null, Locale.ENGLISH));
    assertEquals("Échantillons en attente",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + WAITING.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Received() {
    assertEquals("Samples received",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + RECEIVED.name(), null, Locale.ENGLISH));
    assertEquals("Échantillons reçus",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + RECEIVED.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Digest() {
    assertEquals("Digested",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + DIGESTED.name(), null, Locale.ENGLISH));
    assertEquals("Digéré",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + DIGESTED.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Enrich() {
    assertEquals("Enriched",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + ENRICHED.name(), null, Locale.ENGLISH));
    assertEquals("Enrichit",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + ENRICHED.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Analysed() {
    assertEquals("Analyzed",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + ANALYSED.name(), null, Locale.ENGLISH));
    assertEquals("Analysé",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + ANALYSED.name(), null, Locale.FRENCH));
  }

  @Test
  public void getLabel_Cancelled() {
    assertEquals("Cancelled",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + CANCELLED.name(), null, Locale.ENGLISH));
    assertEquals("Annulé",
        messageSource.getMessage(SAMPLE_STATUS_PREFIX + CANCELLED.name(), null, Locale.FRENCH));
  }
}
