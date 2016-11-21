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

package ca.qc.ircm.proview.web.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class MenuTest extends MenuPageObject {
  protected MessageResource resources(Locale locale) {
    return new MessageResource(MainView.class, locale);
  }

  @Test
  @WithSubject(anonymous = true)
  public void fieldsExistence_Anonymous() throws Throwable {
    openView(MainView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertFalse(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertFalse(optional(() -> managerMenuItem()).isPresent());
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 10)
  public void fieldsExistence_User() throws Throwable {
    openView(MainView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertFalse(optional(() -> managerMenuItem()).isPresent());
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 3)
  public void fieldsExistence_Manager() throws Throwable {
    openView(MainView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 1)
  public void fieldsExistence_Admin() throws Throwable {
    openView(MainView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  public void home() throws Throwable {
    openView(RegisterView.VIEW_NAME);

    clickHome();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void changeLanguage() throws Throwable {
    open();
    Locale currentLocale = currentLocale();

    clickChangeLanguage();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
    Locale newLocale = Locale.FRENCH;
    if (currentLocale == Locale.FRENCH) {
      newLocale = Locale.ENGLISH;
    }
    assertEquals(newLocale, currentLocale());
  }

  @Test
  @WithSubject
  public void submission() throws Throwable {
    open();

    clickSubmission();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void validateUsers() throws Throwable {
    open();

    clickValidateUsers();

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @Ignore("not programmed yet")
  public void help() throws Throwable {
    open();

    clickHelp();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
