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

package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.transfer.web.TransferType.PLATE_TO_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferType.TUBES_TO_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferType.TUBES_TO_TUBES;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Locale;

public class TransferTypeTest {
  @Test
  public void getNullLabel() {
    assertEquals("Undetermined", TransferType.getNullLabel(Locale.ENGLISH));
    assertEquals("Indéterminé", TransferType.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_PlateToPlate() {
    assertEquals("Plate to plate", PLATE_TO_PLATE.getLabel(Locale.ENGLISH));
    assertEquals("Plaque à plaque", PLATE_TO_PLATE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_TubesToPlate() {
    assertEquals("Tubes to plate", TUBES_TO_PLATE.getLabel(Locale.ENGLISH));
    assertEquals("Tubes à plaque", TUBES_TO_PLATE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_TubesToTubes() {
    assertEquals("Tubes to tubes", TUBES_TO_TUBES.getLabel(Locale.ENGLISH));
    assertEquals("Tubes à tubes", TUBES_TO_TUBES.getLabel(Locale.FRENCH));
  }
}
