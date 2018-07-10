/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.sample;

import static ca.qc.ircm.proview.sample.SampleStatus.ANALYSED;
import static ca.qc.ircm.proview.sample.SampleStatus.CANCELLED;
import static ca.qc.ircm.proview.sample.SampleStatus.DATA_ANALYSIS;
import static ca.qc.ircm.proview.sample.SampleStatus.DIGESTED;
import static ca.qc.ircm.proview.sample.SampleStatus.ENRICHED;
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.WAITING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.Test;

public class SampleStatusTest {
  @Test
  public void analysedStatuses() {
    List<SampleStatus> analysedStatuses = Arrays.asList(SampleStatus.analysedStatuses());
    assertTrue(analysedStatuses.contains(ANALYSED));
    assertTrue(analysedStatuses.contains(CANCELLED));
    assertTrue(analysedStatuses.contains(DATA_ANALYSIS));
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
  public void getLabel_Dataanalysis() {
    assertEquals("Data analysis", DATA_ANALYSIS.getLabel(Locale.ENGLISH));
    assertEquals("Analyse de données", DATA_ANALYSIS.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Analysed() {
    assertEquals("Analysed", ANALYSED.getLabel(Locale.ENGLISH));
    assertEquals("Analysé", ANALYSED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Cancelled() {
    assertEquals("Cancelled", CANCELLED.getLabel(Locale.ENGLISH));
    assertEquals("Annulé", CANCELLED.getLabel(Locale.FRENCH));
  }
}
