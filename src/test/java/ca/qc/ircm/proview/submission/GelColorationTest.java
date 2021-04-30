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

import static ca.qc.ircm.proview.submission.GelColoration.COOMASSIE;
import static ca.qc.ircm.proview.submission.GelColoration.OTHER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER;
import static ca.qc.ircm.proview.submission.GelColoration.SILVER_INVITROGEN;
import static ca.qc.ircm.proview.submission.GelColoration.SYPRO;
import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.junit.Test;

/**
 * Tests for {@link GelColoration}.
 */
public class GelColorationTest {
  @Test
  public void getNullLabel() {
    assertEquals("None", GelColoration.getNullLabel(Locale.ENGLISH));
    assertEquals("Aucune", GelColoration.getNullLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Coomassie() {
    assertEquals("Coomassie", COOMASSIE.getLabel(Locale.ENGLISH));
    assertEquals("Coomassie", COOMASSIE.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Sypro() {
    assertEquals("Sypro", SYPRO.getLabel(Locale.ENGLISH));
    assertEquals("Sypro", SYPRO.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Silver() {
    assertEquals("Silver", SILVER.getLabel(Locale.ENGLISH));
    assertEquals("Silver", SILVER.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_SilverInvitrogen() {
    assertEquals("Silver (Invitrogen)", SILVER_INVITROGEN.getLabel(Locale.ENGLISH));
    assertEquals("Silver (Invitrogen)", SILVER_INVITROGEN.getLabel(Locale.FRENCH));
  }

  @Test
  public void getLabel_Other() {
    assertEquals("Other coloration", OTHER.getLabel(Locale.ENGLISH));
    assertEquals("Autre coloration", OTHER.getLabel(Locale.FRENCH));
  }
}
