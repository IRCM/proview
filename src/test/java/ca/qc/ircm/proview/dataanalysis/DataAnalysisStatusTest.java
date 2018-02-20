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

package ca.qc.ircm.proview.dataanalysis;

import static ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus.ANALYSED;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus.CANCELLED;
import static ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus.TO_DO;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class DataAnalysisStatusTest {
  @Test
  public void getNullLabel() {
    assertEquals("Not determined", DataAnalysisStatus.getNullLabel(Locale.ENGLISH));
    assertEquals("Non déterminé", DataAnalysisStatus.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Todo() {
    assertEquals("To do", TO_DO.getLabel(Locale.ENGLISH));
    assertEquals("À faire", TO_DO.getLabel(Locale.FRENCH));
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
