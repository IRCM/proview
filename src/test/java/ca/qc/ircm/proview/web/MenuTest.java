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
import static ca.qc.ircm.proview.web.Menu.CONTROL;
import static ca.qc.ircm.proview.web.Menu.DIGESTION;
import static ca.qc.ircm.proview.web.Menu.DILUTION;
import static ca.qc.ircm.proview.web.Menu.ENRICHMENT;
import static ca.qc.ircm.proview.web.Menu.HOME;
import static ca.qc.ircm.proview.web.Menu.MANAGER;
import static ca.qc.ircm.proview.web.Menu.MS_ANALYSIS;
import static ca.qc.ircm.proview.web.Menu.PLATE;
import static ca.qc.ircm.proview.web.Menu.PROFILE;
import static ca.qc.ircm.proview.web.Menu.REGISTER;
import static ca.qc.ircm.proview.web.Menu.SIGNIN;
import static ca.qc.ircm.proview.web.Menu.SIGNOUT;
import static ca.qc.ircm.proview.web.Menu.SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.SOLUBILISATION;
import static ca.qc.ircm.proview.web.Menu.STANDARD_ADDITION;
import static ca.qc.ircm.proview.web.Menu.STOP_SIGN_AS;
import static ca.qc.ircm.proview.web.Menu.SUBMISSION;
import static ca.qc.ircm.proview.web.Menu.TRANSFER;
import static ca.qc.ircm.proview.web.Menu.TREATMENT;
import static ca.qc.ircm.proview.web.Menu.VALIDATE_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.plate.web.PlateView;
import ca.qc.ircm.proview.sample.web.ControlView;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.user.web.AccessView;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.SignasView;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.utils.MessageResource;
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
    assertFalse(optional(() -> treatmentMenuItem()).isPresent());
    assertFalse(optional(() -> transferMenuItem()).isPresent());
    assertFalse(optional(() -> digestionMenuItem()).isPresent());
    assertFalse(optional(() -> enrichmentMenuItem()).isPresent());
    assertFalse(optional(() -> solubilisationMenuItem()).isPresent());
    assertFalse(optional(() -> dilutionMenuItem()).isPresent());
    assertFalse(optional(() -> standardAdditionMenuItem()).isPresent());
    assertFalse(optional(() -> msAnalysisMenuItem()).isPresent());
    assertFalse(optional(() -> controlMenuItem()).isPresent());
    assertFalse(optional(() -> plateMenuItem()).isPresent());
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
    assertTrue(optional(() -> signin()).isPresent());
  }

  @Test
  @WithSubject(userId = 10)
  public void fieldsExistence_User() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertFalse(optional(() -> treatmentMenuItem()).isPresent());
    assertFalse(optional(() -> transferMenuItem()).isPresent());
    assertFalse(optional(() -> digestionMenuItem()).isPresent());
    assertFalse(optional(() -> enrichmentMenuItem()).isPresent());
    assertFalse(optional(() -> solubilisationMenuItem()).isPresent());
    assertFalse(optional(() -> dilutionMenuItem()).isPresent());
    assertFalse(optional(() -> standardAdditionMenuItem()).isPresent());
    assertFalse(optional(() -> msAnalysisMenuItem()).isPresent());
    assertFalse(optional(() -> controlMenuItem()).isPresent());
    assertFalse(optional(() -> plateMenuItem()).isPresent());
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
    assertFalse(optional(() -> signin()).isPresent());
  }

  @Test
  @WithSubject(userId = 3)
  public void fieldsExistence_Manager() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertFalse(optional(() -> treatmentMenuItem()).isPresent());
    assertFalse(optional(() -> transferMenuItem()).isPresent());
    assertFalse(optional(() -> digestionMenuItem()).isPresent());
    assertFalse(optional(() -> enrichmentMenuItem()).isPresent());
    assertFalse(optional(() -> solubilisationMenuItem()).isPresent());
    assertFalse(optional(() -> dilutionMenuItem()).isPresent());
    assertFalse(optional(() -> standardAdditionMenuItem()).isPresent());
    assertFalse(optional(() -> msAnalysisMenuItem()).isPresent());
    assertFalse(optional(() -> controlMenuItem()).isPresent());
    assertFalse(optional(() -> plateMenuItem()).isPresent());
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
    assertFalse(optional(() -> signin()).isPresent());
  }

  @Test
  @WithSubject(userId = 1)
  public void fieldsExistence_Admin() throws Throwable {
    openView(ContactView.VIEW_NAME);

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertTrue(optional(() -> treatmentMenuItem()).isPresent());
    clickTreatment();
    assertTrue(optional(() -> transferMenuItem()).isPresent());
    assertTrue(optional(() -> digestionMenuItem()).isPresent());
    assertTrue(optional(() -> enrichmentMenuItem()).isPresent());
    assertTrue(optional(() -> solubilisationMenuItem()).isPresent());
    assertTrue(optional(() -> dilutionMenuItem()).isPresent());
    assertTrue(optional(() -> standardAdditionMenuItem()).isPresent());
    assertTrue(optional(() -> msAnalysisMenuItem()).isPresent());
    assertTrue(optional(() -> controlMenuItem()).isPresent());
    assertTrue(optional(() -> plateMenuItem()).isPresent());
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
    assertFalse(optional(() -> signin()).isPresent());
  }

  @Test
  @WithSubject(userId = 1)
  public void fieldsExistence_Admin_SignedAs() throws Throwable {
    openView(ContactView.VIEW_NAME);

    signas("christopher.anderson@ircm.qc.ca");

    assertTrue(optional(() -> homeMenuItem()).isPresent());
    assertTrue(optional(() -> submissionMenuItem()).isPresent());
    assertFalse(optional(() -> treatmentMenuItem()).isPresent());
    assertFalse(optional(() -> transferMenuItem()).isPresent());
    assertFalse(optional(() -> digestionMenuItem()).isPresent());
    assertFalse(optional(() -> enrichmentMenuItem()).isPresent());
    assertFalse(optional(() -> solubilisationMenuItem()).isPresent());
    assertFalse(optional(() -> dilutionMenuItem()).isPresent());
    assertFalse(optional(() -> standardAdditionMenuItem()).isPresent());
    assertFalse(optional(() -> msAnalysisMenuItem()).isPresent());
    assertFalse(optional(() -> controlMenuItem()).isPresent());
    assertFalse(optional(() -> plateMenuItem()).isPresent());
    assertTrue(optional(() -> profileMenuItem()).isPresent());
    assertTrue(optional(() -> signoutMenuItem()).isPresent());
    assertTrue(optional(() -> changeLanguageMenuItem()).isPresent());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertFalse(optional(() -> validateUsersMenuItem()).isPresent());
    assertFalse(optional(() -> accessMenuItem()).isPresent());
    assertFalse(optional(() -> signasMenuItem()).isPresent());
    assertFalse(optional(() -> registerMenuItem()).isPresent());
    assertTrue(optional(() -> stopSignasMenuItem()).isPresent());
    assertTrue(optional(() -> contactMenuItem()).isPresent());
    assertFalse(optional(() -> signin()).isPresent());
  }

  @Test
  @WithSubject
  public void captions() throws Throwable {
    openView(ContactView.VIEW_NAME);

    MessageResource resources = resources(Menu.class);
    assertEquals(resources.message(HOME), homeMenuItem().getText());
    assertEquals(resources.message(SUBMISSION), submissionMenuItem().getText());
    assertEquals(resources.message(TREATMENT), treatmentMenuItem().getText());
    clickTreatment();
    assertEquals(resources.message(TRANSFER), transferMenuItem().getText());
    assertEquals(resources.message(DIGESTION), digestionMenuItem().getText());
    assertEquals(resources.message(ENRICHMENT), enrichmentMenuItem().getText());
    assertEquals(resources.message(SOLUBILISATION), solubilisationMenuItem().getText());
    assertEquals(resources.message(DILUTION), dilutionMenuItem().getText());
    assertEquals(resources.message(STANDARD_ADDITION), standardAdditionMenuItem().getText());
    assertEquals(resources.message(MS_ANALYSIS), msAnalysisMenuItem().getText());
    clickTreatment();
    assertEquals(resources.message(CONTROL), controlMenuItem().getText());
    assertEquals(resources.message(PLATE), plateMenuItem().getText());
    assertEquals(resources.message(PROFILE), profileMenuItem().getText());
    assertEquals(resources.message(SIGNOUT), signoutMenuItem().getText());
    assertEquals(resources.message(CHANGE_LANGUAGE), changeLanguageMenuItem().getText());
    assertEquals(resources.message(MANAGER), managerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(VALIDATE_USERS), validateUsersMenuItem().getText());
    assertEquals(resources.message(ACCESS), accessMenuItem().getText());
    assertEquals(resources.message(SIGN_AS), signasMenuItem().getText());
    assertEquals(resources.message(REGISTER), registerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(CONTACT), contactMenuItem().getText());
    signas("christopher.anderson@ircm.qc.ca");
    clickManager();
    assertEquals(resources.message(STOP_SIGN_AS), stopSignasMenuItem().getText());
    clickHome();
    clickStopSignas();

    clickChangeLanguage();

    resources = resources(Menu.class);
    assertEquals(resources.message(HOME), homeMenuItem().getText());
    assertEquals(resources.message(SUBMISSION), submissionMenuItem().getText());
    assertEquals(resources.message(TREATMENT), treatmentMenuItem().getText());
    clickTreatment();
    assertEquals(resources.message(TRANSFER), transferMenuItem().getText());
    assertEquals(resources.message(DIGESTION), digestionMenuItem().getText());
    assertEquals(resources.message(ENRICHMENT), enrichmentMenuItem().getText());
    assertEquals(resources.message(SOLUBILISATION), solubilisationMenuItem().getText());
    assertEquals(resources.message(DILUTION), dilutionMenuItem().getText());
    assertEquals(resources.message(STANDARD_ADDITION), standardAdditionMenuItem().getText());
    assertEquals(resources.message(MS_ANALYSIS), msAnalysisMenuItem().getText());
    clickTreatment();
    assertEquals(resources.message(CONTROL), controlMenuItem().getText());
    assertEquals(resources.message(PLATE), plateMenuItem().getText());
    assertEquals(resources.message(PROFILE), profileMenuItem().getText());
    assertEquals(resources.message(SIGNOUT), signoutMenuItem().getText());
    assertEquals(resources.message(CHANGE_LANGUAGE), changeLanguageMenuItem().getText());
    assertEquals(resources.message(MANAGER), managerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(VALIDATE_USERS), validateUsersMenuItem().getText());
    assertEquals(resources.message(ACCESS), accessMenuItem().getText());
    assertEquals(resources.message(SIGN_AS), signasMenuItem().getText());
    assertEquals(resources.message(REGISTER), registerMenuItem().getText());
    clickManager();
    assertEquals(resources.message(CONTACT), contactMenuItem().getText());
    signas("christopher.anderson@ircm.qc.ca");
    clickManager();
    assertEquals(resources.message(STOP_SIGN_AS), stopSignasMenuItem().getText());
  }

  @Test
  @WithSubject(anonymous = true)
  public void captions_Anonymous() throws Throwable {
    openView(ContactView.VIEW_NAME);

    MessageResource resources = resources(Menu.class);
    assertEquals(resources.message(SIGNIN), signin().getText());
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
  public void transfer() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickTransfer();

    assertEquals(viewUrl(TransferView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void digestion() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickDigestion();

    assertEquals(viewUrl(DigestionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void enrichment() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickEnrichment();

    assertEquals(viewUrl(EnrichmentView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void solubilisation() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickSolubilisation();

    assertEquals(viewUrl(SolubilisationView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void dilution() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickDilution();

    assertEquals(viewUrl(DilutionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void standardAddition() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickStandardAddition();

    assertEquals(viewUrl(StandardAdditionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void msAnalysis() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickTreatment();
    clickMsAnalysis();

    assertEquals(viewUrl(MsAnalysisView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void control() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickControl();

    assertEquals(viewUrl(ControlView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void plate() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickPlate();

    assertEquals(viewUrl(PlateView.VIEW_NAME), getDriver().getCurrentUrl());
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
  @WithSubject(anonymous = true)
  public void signinMenu() throws Throwable {
    openView(ContactView.VIEW_NAME);

    clickSignin();

    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
