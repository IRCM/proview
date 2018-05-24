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

package ca.qc.ircm.proview.history;

import static ca.qc.ircm.proview.history.ActionType.DELETE;
import static ca.qc.ircm.proview.history.ActionType.INSERT;
import static ca.qc.ircm.proview.history.ActionType.UPDATE;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

public class ActionTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Not determined", ActionType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", ActionType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Insert() {
    assertEquals("Insert", INSERT.getLabel(Locale.ENGLISH));
    assertEquals("Insertion", INSERT.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Update() {
    assertEquals("Update", UPDATE.getLabel(Locale.ENGLISH));
    assertEquals("Mise-à-jour", UPDATE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Delete() {
    assertEquals("Delete", DELETE.getLabel(Locale.ENGLISH));
    assertEquals("Délétion", DELETE.getLabel(Locale.FRENCH));
  }
}
