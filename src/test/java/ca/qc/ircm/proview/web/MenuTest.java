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
import static ca.qc.ircm.proview.web.Menu.ACCESS;
import static ca.qc.ircm.proview.web.Menu.CHANGE_LANGUAGE;
import static ca.qc.ircm.proview.web.Menu.CONTACT;
import static ca.qc.ircm.proview.web.Menu.HELP;
import static ca.qc.ircm.proview.web.Menu.HOME;
import static ca.qc.ircm.proview.web.Menu.MANAGER;
import static ca.qc.ircm.proview.web.Menu.PROFILE;
import static ca.qc.ircm.proview.web.Menu.REGISTER;
import static ca.qc.ircm.proview.web.Menu.SIGNOUT;
import static ca.qc.ircm.proview.web.Menu.SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.STOP_SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.SUBMISSION;
import static ca.qc.ircm.proview.web.Menu.VALIDATE_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.AccessView;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.SignasView;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Cookie;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class MenuTest extends MenuPageObject {
  protected MessageResource resources(Locale locale) {
    return new MessageResource(MainView.class, locale);
  }

  @Test
  @WithSubject(anonymous = true)
  public void fieldsExistence_Anonymous() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertFalse(optional(() -> submissionMenuItem()).isPresent());
    assertFalse(optional(() -> profileMenuItem()).isPresent());
    assertFalse(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertFalse(optional(() -> managerMenuItem()).isPresent());
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertFalse(optional(() -> accessMenuItem()).isPresent());
    assertFalse(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> registerMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 10)
  public void fieldsExistence_User() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> profileMenuItem()).isPresent());
    assertTrue(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertFalse(optional(() -> managerMenuItem()).isPresent());
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertFalse(optional(() -> accessMenuItem()).isPresent());
    assertFalse(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> registerMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 3)
  public void fieldsExistence_Manager() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> profileMenuItem()).isPresent());
    assertTrue(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> accessMenuItem()).isPresent());
    assertFalse(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> registerMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 1)
  public void fieldsExistence_Admin() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> profileMenuItem()).isPresent());
    assertTrue(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> accessMenuItem()).isPresent());
    assertTrue(optional(() -> signasMenuItem()).isPresent());
    assertTrue(optional(() -> registerMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject(userId = 1)
  public void fieldsExistence_Admin_SignedAs() throws Throwable {
    openView(ContactView.VIEW_NAME);

    signas("christopher.anderson@ircm.qc.ca");

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> profileMenuItem()).isPresent());
    assertTrue(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    Thread.sleep(1000);
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertFalse(optional(() -> accessMenuItem()).isPresent());
    assertFalse(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> registerMenuItem()).isPresent());
    assertTrue(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertTrue(optional(() -> helpMenuItem()).isPresent());
  }

  @Test
  @WithSubject
  public void captions() throws Throwable {
    openView(ContactView.VIEW_NAME);

    MessageResource resources = resources(Menu.class);
    assertEquals(resources.message(HOME), homeMenuItem().getText());
    assertEquals(resources.message(SUBMISSION), submissionMenuItem().getText());
    assertEquals(resources.message(PROFILE), profileMenuItem().getText());
    assertEquals(resources.message(SIGNOUT), signoutMenuItem().getText());
    assertEquals(resources.message(CHANGE_LANGUAGE), changeLanguageMenuItem().getText());
    assertEquals(resources.message(MANAGER), managerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(VALIDATE_USERS), validateUsersMenuItem().getText());
    assertEquals(resources.message(ACCESS), accessMenuItem().getText());
    assertEquals(resources.message(SIGN_AS), signasMenuItem().getText());
    assertEquals(resources.message(REGISTER), registerMenuItem().getText());
    assertEquals(resources.message(CONTACT), contactMenuItem().getText());
    assertEquals(resources.message(HELP), helpMenuItem().getText());
    signas("christopher.anderson@ircm.qc.ca");
    clickManager();
    assertEquals(resources.message(STOP_SIGN_AS), stopSignasMenuItem().getText());
    clickHome();
    clickStopSignas();

    clickChangeLanguage();

    resources = resources(Menu.class);
    assertEquals(resources.message(HOME), homeMenuItem().getText());
    assertEquals(resources.message(SUBMISSION), submissionMenuItem().getText());
    assertEquals(resources.message(PROFILE), profileMenuItem().getText());
    assertEquals(resources.message(SIGNOUT), signoutMenuItem().getText());
    assertEquals(resources.message(CHANGE_LANGUAGE), changeLanguageMenuItem().getText());
    assertEquals(resources.message(MANAGER), managerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(VALIDATE_USERS), validateUsersMenuItem().getText());
    assertEquals(resources.message(ACCESS), accessMenuItem().getText());
    assertEquals(resources.message(SIGN_AS), signasMenuItem().getText());
    assertEquals(resources.message(REGISTER), registerMenuItem().getText());
    assertEquals(resources.message(CONTACT), contactMenuItem().getText());
    assertEquals(resources.message(HELP), helpMenuItem().getText());
    signas("christopher.anderson@ircm.qc.ca");
    clickManager();
    assertEquals(resources.message(STOP_SIGN_AS), stopSignasMenuItem().getText());
  }

  @Test
  public void home() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickHome();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void submission() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSubmission();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void profile() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickProfile();

    assertEquals(viewUrl(UserView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void signout() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSignout();

    Thread.sleep(50); // Wait for redirection.
    assertEquals(homeUrl(), getDriver().getCurrentUrl());
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
  public void validateUsers() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickValidateUsers();

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void access() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickAccess();

    assertEquals(viewUrl(AccessView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void signas() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSignas();

    assertEquals(viewUrl(SignasView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void register() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickRegister();

    assertEquals(viewUrl(RegisterView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void stopSignas() throws Throwable {
    openView(ContactView.VIEW_NAME);
    signas("christopher.anderson@ircm.qc.ca");

    clickStopSignas();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> validateUsersMenuItem()).isPresent());
    assertTrue(optional(() -> accessMenuItem()).isPresent());
    assertTrue(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> stopSignasMenuItem()).isPresent());
  }

  @Test
  public void contact() throws Throwable {
    openView(RegisterView.VIEW_NAME);

    clickContact();

    assertEquals(viewUrl(ContactView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @Ignore("not programmed yet")
  public void help() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickHelp();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
