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
import static ca.qc.ircm.proview.sample.SampleStatus.RECEIVED;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_ANALYSE;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_APPROVE;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_DIGEST;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_ENRICH;
import static ca.qc.ircm.proview.sample.SampleStatus.TO_RECEIVE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class SampleStatusTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", SampleStatus.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", SampleStatus.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Toapprove() {
    assertEquals("To approve", TO_APPROVE.getLabel(Locale.ENGLISH));
    assertEquals("À approuver", TO_APPROVE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Toreceive() {
    assertEquals("To receive", TO_RECEIVE.getLabel(Locale.ENGLISH));
    assertEquals("À recevoir", TO_RECEIVE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Received() {
    assertEquals("Received", RECEIVED.getLabel(Locale.ENGLISH));
    assertEquals("Reçu", RECEIVED.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Todigest() {
    assertEquals("To digest", TO_DIGEST.getLabel(Locale.ENGLISH));
    assertEquals("À digérer", TO_DIGEST.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_ToEnrich() {
    assertEquals("To enrich", TO_ENRICH.getLabel(Locale.ENGLISH));
    assertEquals("À enrichir", TO_ENRICH.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Toanalyse() {
    assertEquals("To analyse", TO_ANALYSE.getLabel(Locale.ENGLISH));
    assertEquals("À analyser", TO_ANALYSE.getLabel(Locale.FRENCH));
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
