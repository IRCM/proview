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

package ca.qc.ircm.proview.plate;

import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.test.config.NonTransactionalTestAnnotations;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@NonTransactionalTestAnnotations
public class PlateSpotTest {
  @Test
  public void getName() {
    assertEquals("C-4", new PlateSpot(2, 3).getName());
    assertEquals("Z-4", new PlateSpot(25, 3).getName());
    assertEquals("AA-31", new PlateSpot(26, 30).getName());
    assertEquals("AE-31", new PlateSpot(30, 30).getName());
    assertEquals("BA-31", new PlateSpot(52, 30).getName());
    assertEquals("ZZ-31", new PlateSpot(701, 30).getName());
    assertEquals("AAA-31", new PlateSpot(702, 30).getName());
    assertEquals("ZZZ-31", new PlateSpot(18277, 30).getName());
    assertEquals("AAAA-31", new PlateSpot(18278, 30).getName());
  }

  @Test
  public void getFullName() {
    Plate plate = new Plate(1L, "test_plate");
    plate.setColumnCount(31);
    plate.setRowCount(18279);
    plate.initSpots();
    assertEquals("test_plate (C-4)", plate.spot(2, 3).getFullName(Locale.ENGLISH));
    assertEquals("test_plate (Z-31)", plate.spot(25, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (AA-31)", plate.spot(26, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (AE-31)", plate.spot(30, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (BA-31)", plate.spot(52, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (ZZ-31)", plate.spot(701, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (AAA-31)", plate.spot(702, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (ZZZ-31)", plate.spot(18277, 30).getFullName(Locale.FRENCH));
    assertEquals("test_plate (AAAA-31)", plate.spot(18278, 30).getFullName(Locale.FRENCH));
  }
}
