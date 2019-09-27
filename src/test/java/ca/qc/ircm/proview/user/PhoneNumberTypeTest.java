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

package ca.qc.ircm.proview.user;

import static ca.qc.ircm.proview.user.PhoneNumberType.FAX;
import static ca.qc.ircm.proview.user.PhoneNumberType.MOBILE;
import static ca.qc.ircm.proview.user.PhoneNumberType.WORK;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.junit.Assert.assertEquals;

import ca.qc.ircm.proview.AppResources;
import org.junit.Test;

public class PhoneNumberTypeTest {
  AppResources englishResource = new AppResources(PhoneNumberType.class, ENGLISH);
  AppResources frenchResource = new AppResources(PhoneNumberType.class, FRENCH);

  @Test
  public void getLabel_WorkEnglish() {
    assertEquals(englishResource.message(WORK.name()), WORK.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_WorkFrench() {
    assertEquals(frenchResource.message(WORK.name()), WORK.getLabel(FRENCH));
  }

  @Test
  public void getLabel_MobileEnglish() {
    assertEquals(englishResource.message(MOBILE.name()), MOBILE.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_MobileFrench() {
    assertEquals(frenchResource.message(MOBILE.name()), MOBILE.getLabel(FRENCH));
  }

  @Test
  public void getLabel_FaxEnglish() {
    assertEquals(englishResource.message(FAX.name()), FAX.getLabel(ENGLISH));
  }

  @Test
  public void getLabel_FaxFrench() {
    assertEquals(frenchResource.message(FAX.name()), FAX.getLabel(FRENCH));
  }
}
