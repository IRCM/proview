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

package ca.qc.ircm.proview.text;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NormalizedComparatorTest {
  private NormalizedComparator comparator = new NormalizedComparator();

  @Test
  public void normalize() {
    assertTrue(comparator.compare("bateau", "bàteau") == 0);
    assertTrue(comparator.compare("bateau", "bàteau") == 0);
    assertTrue(comparator.compare("bateau", "BÀTEAU") == 0);
    assertTrue(comparator.compare("BATEAU", "BÀTEAU") == 0);
    assertTrue(comparator.compare("bateau", "bâteau") == 0);
    assertTrue(comparator.compare("BATEAU", "BÂTEAU") == 0);
    assertTrue(comparator.compare("bateau", "bäteau") == 0);
    assertTrue(comparator.compare("BATEAU", "BÄTEAU") == 0);
    assertTrue(comparator.compare("pepin", "pépin") == 0);
    assertTrue(comparator.compare("pepin", "pèpin") == 0);
    assertTrue(comparator.compare("pepin", "pêpin") == 0);
    assertTrue(comparator.compare("pepin", "pëpin") == 0);
    assertTrue(comparator.compare("pepin", "pepîn") == 0);
    assertTrue(comparator.compare("pepin", "pepïn") == 0);
    assertTrue(comparator.compare("pepin", "PÉPIN") == 0);
    assertTrue(comparator.compare("pepon", "pepôn") == 0);
    assertTrue(comparator.compare("pepon", "pepön") == 0);
    assertTrue(comparator.compare("pepun", "pepùn") == 0);
    assertTrue(comparator.compare("pepun", "pepûn") == 0);
    assertTrue(comparator.compare("pepun", "pepün") == 0);
    assertTrue(comparator.compare("pepin", "pépîn") == 0);
    assertTrue(comparator.compare("pepin", "peqin") < 0);
    assertTrue(comparator.compare("peqin", "pepin") > 0);
    assertTrue(comparator.compare("pepin", "pepin1") < 0);
    assertTrue(comparator.compare("pepin1", "pepin") > 0);
    assertTrue(comparator.compare("pepin", "péqîn") < 0);
    assertTrue(comparator.compare("peqin", "pépîn") > 0);
    assertTrue(comparator.compare(null, "pepin") < 0);
    assertTrue(comparator.compare("pepin", null) > 0);
    assertTrue(comparator.compare("", "pepin") < 0);
    assertTrue(comparator.compare("pepin", "") > 0);
    // Test Polish, out of curiosity.
    assertTrue(comparator.compare("a", "ą") == 0);
    assertTrue(comparator.compare("c", "ć") == 0);
    assertTrue(comparator.compare("e", "ę") == 0);
    // Doesn't work because ł in Unicode is not l with a slash, but its own character.
    //assertTrue(comparator.compare("l", "ł") == 0);
    assertTrue(comparator.compare("n", "ń") == 0);
    assertTrue(comparator.compare("o", "ó") == 0);
    assertTrue(comparator.compare("s", "ś") == 0);
    assertTrue(comparator.compare("z", "ź") == 0);
    assertTrue(comparator.compare("z", "ż") == 0);
  }
}
