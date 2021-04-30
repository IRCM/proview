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

import static ca.qc.ircm.proview.sample.ControlType.NEGATIVE_CONTROL;
import static ca.qc.ircm.proview.sample.ControlType.POSITIVE_CONTROL;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

/**
 * Tests for {@link ControlType}.
 */
public class ControlTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Other", ControlType.getNullLabel(Locale.ENGLISH));
    assertEquals("Autre", ControlType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Negative() {
    assertEquals("Negative control", NEGATIVE_CONTROL.getLabel(Locale.ENGLISH));
    assertEquals("Contrôle négatif", NEGATIVE_CONTROL.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Positive() {
    assertEquals("Positive control", POSITIVE_CONTROL.getLabel(Locale.ENGLISH));
    assertEquals("Contrôle positif", POSITIVE_CONTROL.getLabel(Locale.FRENCH));
  }
}
