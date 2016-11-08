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

package ca.qc.ircm.proview.submission;

import static ca.qc.ircm.proview.submission.StorageTemperature.LOW;
import static ca.qc.ircm.proview.submission.StorageTemperature.MEDIUM;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class StorageTemperatureTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", StorageTemperature.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminée", StorageTemperature.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Medium() {
    assertEquals("4 °C", MEDIUM.getLabel(Locale.ENGLISH));
    assertEquals("4 °C", MEDIUM.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Low() {
    assertEquals("-20 °C", LOW.getLabel(Locale.ENGLISH));
    assertEquals("-20 °C", LOW.getLabel(Locale.FRENCH));
  }
}
