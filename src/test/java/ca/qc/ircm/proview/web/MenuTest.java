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

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.test.config.ShiroTestExecutionListener.REMEMBER_ME_COOKIE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.SigninView;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class MenuTest extends MenuPageObject {
  @Test
  @WithSubject
  public void submission() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSubmission();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void signout() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSignout();

    Thread.sleep(50); // Wait for redirection.
    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
    Set<Cookie> cookies = driver.manage().getCookies();
    assertFalse(cookies.stream().filter(cookie -> cookie.getName().equals(REMEMBER_ME_COOKIE_NAME))
        .findAny().isPresent());
  }

  @Test
  public void changeLanguage() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale currentLocale = currentLocale();

    clickChangeLanguage();

    assertEquals(viewUrl(ContactView.VIEW_NAME), getDriver().getCurrentUrl());
    Locale newLocale = Locale.FRENCH;
    if (currentLocale == Locale.FRENCH) {
      newLocale = Locale.ENGLISH;
    }
    assertEquals(newLocale, currentLocale());
  }

  @Test
  @WithSubject
  public void stopSignas() throws Throwable {
    openView(ContactView.VIEW_NAME);
    signas("christopher.anderson@ircm.qc.ca");

    clickStopSignas();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
    assertTrue(optional(() -> usersMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
  }
}
