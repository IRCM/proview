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

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.plate.web.PlatesView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.WebSecurityConfiguration;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.MenuBar.MenuItem;
import java.util.Locale;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.stereotype.Controller;

/**
 * Menu presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MenuPresenter {
  public static final String HOME = "home";
  public static final String SUBMISSION = "submission";
  public static final String PLATE = "plate";
  public static final String PROFILE = "profile";
  public static final String SIGNOUT = "signout";
  public static final String CHANGE_LANGUAGE = "changeLanguage";
  public static final String USERS = "users";
  public static final String CONTACT = "contact";
  public static final String GUIDELINES = "guidelines";
  public static final String SIGNIN = "signin";
  public static final String STOP_SIGN_AS = "stopSignas";
  private static final Logger logger = LoggerFactory.getLogger(MenuPresenter.class);
  private Menu view;
  private MenuItem home;
  private MenuItem submission;
  private MenuItem plate;
  private MenuItem profile;
  private MenuItem signout;
  private MenuItem changeLanguage;
  private MenuItem users;
  private MenuItem stopSignas;
  private MenuItem contact;
  private MenuItem guidelines;
  private MenuItem signin;
  @Inject
  private AuthorizationService authorizationService;

  protected MenuPresenter() {
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
    users = view.menu.addItem(resources.message(USERS), item -> changeView(UsersView.VIEW_NAME));
    users.setStyleName(USERS);
    users.setVisible(false);
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
    stopSignas = view.menu.addItem(resources.message(STOP_SIGN_AS), item -> stopSignas());
    stopSignas.setStyleName(STOP_SIGN_AS);
    stopSignas.setVisible(false);
  }

  private void updateVisible() {
    submission.setVisible(authorizationService.hasRole(UserRole.USER));
    plate.setVisible(authorizationService.hasRole(UserRole.ADMIN));
    profile.setVisible(!authorizationService.isAnonymous());
    signout.setVisible(!authorizationService.isAnonymous());
    users.setVisible(authorizationService.hasRole(UserRole.MANAGER)
        || authorizationService.hasRole(UserRole.ADMIN));
    guidelines.setVisible(authorizationService.hasRole(UserRole.USER));
    signin.setVisible(authorizationService.isAnonymous());
    stopSignas
        .setVisible(authorizationService.hasRole(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR));
  }

  private void changeView(String viewName) {
    logger.debug("Navigate to {}", viewName);
    view.navigateTo(viewName);
  }

  private void stopSignas() {
    logger.debug("Stop sign as user {}", authorizationService.getCurrentUser());
    view.getUI().getPage().setLocation(WebSecurityConfiguration.SWITCH_USER_EXIT_URL);
  }

  private void signout() {
    logger.debug("Signout user {}", authorizationService.getCurrentUser());
    view.getUI().getPage().setLocation(WebSecurityConfiguration.SIGNOUT_URL);
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
