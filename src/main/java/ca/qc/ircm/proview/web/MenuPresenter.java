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

import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.plate.web.PlatesView;
import ca.qc.ircm.proview.sample.web.ControlView;
import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.user.web.AccessView;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.SignasView;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.proview.user.web.SignoutFilter;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Menu presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuPresenter {
  public static final String HOME = "home";
  public static final String SUBMISSION = "submission";
  public static final String TREATMENT = "treatment";
  public static final String TRANSFER = "transfer";
  public static final String DIGESTION = "digestion";
  public static final String ENRICHMENT = "enrichment";
  public static final String SOLUBILISATION = "solubilisation";
  public static final String DILUTION = "dilution";
  public static final String STANDARD_ADDITION = "standardAddition";
  public static final String MS_ANALYSIS = "msAnalysis";
  public static final String CONTROL = "control";
  public static final String PLATE = "plate";
  public static final String PROFILE = "profile";
  public static final String SIGNOUT = "signout";
  public static final String CHANGE_LANGUAGE = "changeLanguage";
  public static final String MANAGER = "manager";
  public static final String VALIDATE_USERS = "validateUsers";
  public static final String ACCESS = "access";
  public static final String SIGN_AS = "signas";
  public static final String REGISTER = "register";
  public static final String STOP_SIGN_AS = "stopSignas";
  public static final String CONTACT = "contact";
  public static final String GUIDELINES = "guidelines";
  public static final String SIGNIN = "signin";
  public static final String ABOUT = "about";
  private static final Logger logger = LoggerFactory.getLogger(MenuPresenter.class);
  private Menu view;
  private MenuItem home;
  private MenuItem submission;
  private MenuItem treatment;
  private MenuItem transfer;
  private MenuItem digestion;
  private MenuItem enrichment;
  private MenuItem solubilisation;
  private MenuItem dilution;
  private MenuItem standardAddition;
  private MenuItem msAnalysis;
  private MenuItem control;
  private MenuItem plate;
  private MenuItem profile;
  private MenuItem signout;
  private MenuItem changeLanguage;
  private MenuItem manager;
  private MenuItem validateUsers;
  private MenuItem access;
  private MenuItem signas;
  private MenuItem register;
  private MenuItem stopSignas;
  private MenuItem contact;
  private MenuItem guidelines;
  private MenuItem signin;
  private MenuItem about;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private AuthenticationService authenticationService;

  protected MenuPresenter() {
  }

  protected MenuPresenter(AuthorizationService authorizationService,
      AuthenticationService authenticationService) {
    this.authorizationService = authorizationService;
    this.authenticationService = authenticationService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(Menu view) {
    this.view = view;
    prepareComponents();
    updateVisible();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    home = view.menu.addItem(resources.message(HOME), item -> changeView(MainView.VIEW_NAME));
    home.setStyleName(HOME);
    submission = view.menu.addItem(resources.message(SUBMISSION),
        item -> changeView(SubmissionView.VIEW_NAME));
    submission.setStyleName(SUBMISSION);
    submission.setVisible(false);
    treatment = view.menu.addItem(resources.message(TREATMENT), null);
    treatment.setStyleName(TREATMENT);
    treatment.setVisible(false);
    transfer =
        treatment.addItem(resources.message(TRANSFER), item -> changeView(TransferView.VIEW_NAME));
    transfer.setStyleName(TRANSFER);
    digestion = treatment.addItem(resources.message(DIGESTION),
        item -> changeView(DigestionView.VIEW_NAME));
    digestion.setStyleName(DIGESTION);
    enrichment = treatment.addItem(resources.message(ENRICHMENT),
        item -> changeView(EnrichmentView.VIEW_NAME));
    enrichment.setStyleName(ENRICHMENT);
    solubilisation = treatment.addItem(resources.message(SOLUBILISATION),
        item -> changeView(SolubilisationView.VIEW_NAME));
    solubilisation.setStyleName(SOLUBILISATION);
    dilution =
        treatment.addItem(resources.message(DILUTION), item -> changeView(DilutionView.VIEW_NAME));
    dilution.setStyleName(DILUTION);
    standardAddition = treatment.addItem(resources.message(STANDARD_ADDITION),
        item -> changeView(StandardAdditionView.VIEW_NAME));
    standardAddition.setStyleName(STANDARD_ADDITION);
    msAnalysis = treatment.addItem(resources.message(MS_ANALYSIS),
        item -> changeView(MsAnalysisView.VIEW_NAME));
    msAnalysis.setStyleName(MS_ANALYSIS);
    control =
        view.menu.addItem(resources.message(CONTROL), item -> changeView(ControlView.VIEW_NAME));
    control.setStyleName(CONTROL);
    control.setVisible(false);
    plate = view.menu.addItem(resources.message(PLATE), item -> changeView(PlatesView.VIEW_NAME));
    plate.setStyleName(PLATE);
    plate.setVisible(false);
    profile = view.menu.addItem(resources.message(PROFILE), item -> changeView(UserView.VIEW_NAME));
    profile.setStyleName(PROFILE);
    profile.setVisible(false);
    signout = view.menu.addItem(resources.message(SIGNOUT), item -> signout());
    signout.setStyleName(SIGNOUT);
    signout.setVisible(false);
    changeLanguage =
        view.menu.addItem(resources.message(CHANGE_LANGUAGE), item -> changeLanguage());
    changeLanguage.setStyleName(CHANGE_LANGUAGE);
    manager = view.menu.addItem(resources.message(MANAGER), null);
    manager.setStyleName(MANAGER);
    manager.setVisible(false);
    validateUsers = manager.addItem(resources.message(VALIDATE_USERS),
        item -> changeView(ValidateView.VIEW_NAME));
    validateUsers.setStyleName(VALIDATE_USERS);
    validateUsers.setVisible(false);
    access = manager.addItem(resources.message(ACCESS), item -> changeView(AccessView.VIEW_NAME));
    access.setStyleName(ACCESS);
    access.setVisible(false);
    signas = manager.addItem(resources.message(SIGN_AS), item -> changeView(SignasView.VIEW_NAME));
    signas.setStyleName(SIGN_AS);
    signas.setVisible(false);
    register =
        manager.addItem(resources.message(REGISTER), item -> changeView(RegisterView.VIEW_NAME));
    register.setStyleName(REGISTER);
    register.setVisible(false);
    stopSignas = manager.addItem(resources.message(STOP_SIGN_AS), item -> stopSignas());
    stopSignas.setStyleName(STOP_SIGN_AS);
    stopSignas.setVisible(false);
    contact =
        view.menu.addItem(resources.message(CONTACT), item -> changeView(ContactView.VIEW_NAME));
    contact.setStyleName(CONTACT);
    guidelines = view.menu.addItem(resources.message(GUIDELINES),
        item -> changeView(GuidelinesView.VIEW_NAME));
    guidelines.setStyleName(GUIDELINES);
    guidelines.setVisible(false);
    signin = view.menu.addItem(resources.message(SIGNIN), item -> changeView(SigninView.VIEW_NAME));
    signin.setStyleName(SIGNIN);
    signin.setVisible(false);
    about = view.menu.addItem(resources.message(ABOUT), item -> changeView(AboutView.VIEW_NAME));
    about.setStyleName(ABOUT);
  }

  private void updateVisible() {
    submission.setVisible(authorizationService.hasUserRole());
    treatment.setVisible(authorizationService.hasAdminRole());
    transfer.setVisible(authorizationService.hasAdminRole());
    digestion.setVisible(authorizationService.hasAdminRole());
    enrichment.setVisible(authorizationService.hasAdminRole());
    solubilisation.setVisible(authorizationService.hasAdminRole());
    dilution.setVisible(authorizationService.hasAdminRole());
    standardAddition.setVisible(authorizationService.hasAdminRole());
    msAnalysis.setVisible(authorizationService.hasAdminRole());
    control.setVisible(authorizationService.hasAdminRole());
    plate.setVisible(authorizationService.hasAdminRole());
    profile.setVisible(authorizationService.isUser());
    signout.setVisible(authorizationService.isUser());
    manager.setVisible(authorizationService.isRunAs() || authorizationService.hasManagerRole()
        || authorizationService.hasAdminRole());
    validateUsers
        .setVisible(authorizationService.hasManagerRole() || authorizationService.hasAdminRole());
    access.setVisible(authorizationService.hasManagerRole() || authorizationService.hasAdminRole());
    signas.setVisible(authorizationService.hasAdminRole());
    register.setVisible(authorizationService.hasAdminRole());
    stopSignas.setVisible(authorizationService.isRunAs());
    guidelines.setVisible(authorizationService.hasUserRole());
    signin.setVisible(!authorizationService.isUser());
  }

  private void changeView(String viewName) {
    logger.debug("Navigate to {}", viewName);
    view.navigateTo(viewName);
  }

  private void stopSignas() {
    logger.debug("Stop sign as user {}", authorizationService.getCurrentUser());
    authenticationService.stopRunAs();
    changeView(MainView.VIEW_NAME);
  }

  private void signout() {
    logger.debug("Signout user {}", authorizationService.getCurrentUser());
    UI ui = view.getUI();
    if (ui instanceof MainUi) {
      String signoutUrl = ((MainUi) ui).getServletContext().getContextPath();
      signoutUrl += SignoutFilter.SIGNOUT_URL;
      ui.getPage().setLocation(signoutUrl);
    }
  }

  private void changeLanguage() {
    Locale newLocale = Locale.ENGLISH;
    Locale locale = view.getLocale();
    if (locale != null && locale.getLanguage().equals("en")) {
      newLocale = Locale.FRENCH;
    }
    logger.debug("Change language from {} to {}", locale, newLocale);
    view.getUI().getSession().setLocale(newLocale);
    view.getUI().setLocale(newLocale);
    view.getUI().getPage().reload();
  }

  void viewChange() {
    updateVisible();
  }
}
