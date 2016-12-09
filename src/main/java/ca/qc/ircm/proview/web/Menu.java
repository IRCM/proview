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

import ca.qc.ircm.proview.security.AuthenticationService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.user.web.AccessView;
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.user.web.SignasView;
import ca.qc.ircm.proview.user.web.SignoutFilter;
import ca.qc.ircm.proview.user.web.UserView;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.proview.web.component.BaseComponent;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Menu.
 */
public class Menu extends CustomComponent implements BaseComponent {
  public static final String HOME = "home";
  public static final String SUBMISSION = "submission";
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
  public static final String HELP = "help";
  private static final long serialVersionUID = 4442788596052318607L;
  private static final Logger logger = LoggerFactory.getLogger(Menu.class);
  private MenuBar menu = new MenuBar();
  private MenuItem home;
  private MenuItem submission;
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
  private MenuItem help;
  @Inject
  private AuthorizationService authorizationService;
  @Inject
  private AuthenticationService authenticationService;

  /**
   * Creates navigation menu.
   */
  public Menu() {
    setCompositionRoot(menu);
    home = menu.addItem("Home", item -> changeView(MainView.VIEW_NAME));
    submission = menu.addItem("Submission", item -> changeView(SubmissionView.VIEW_NAME));
    submission.setVisible(false);
    profile = menu.addItem("Profile", item -> changeView(UserView.VIEW_NAME));
    profile.setVisible(false);
    signout = menu.addItem("Sign out", item -> signout());
    signout.setVisible(false);
    changeLanguage = menu.addItem("Change language", item -> changeLanguage());
    manager = menu.addItem("Manager", null);
    manager.setVisible(false);
    validateUsers = manager.addItem("Validate users", item -> changeView(ValidateView.VIEW_NAME));
    validateUsers.setVisible(false);
    access = manager.addItem("Users access", item -> changeView(AccessView.VIEW_NAME));
    access.setVisible(false);
    signas = manager.addItem("Sign as", item -> changeView(SignasView.VIEW_NAME));
    signas.setVisible(false);
    register = manager.addItem("Register user", item -> changeView(RegisterView.VIEW_NAME));
    register.setVisible(false);
    stopSignas = manager.addItem("Stop sign as", item -> stopSignas());
    stopSignas.setVisible(false);
    contact = menu.addItem("Help", item -> changeView(ContactView.VIEW_NAME));
    help = menu.addItem("Help", item -> changeView(MainView.VIEW_NAME));
  }

  @Override
  public void attach() {
    super.attach();
    injectBeans();
    prepareComponents();
    updateVisible();
  }

  private void injectBeans() {
    UI ui = getUI();
    if (ui instanceof MainUi) {
      WebApplicationContext context =
          WebApplicationContextUtils.getWebApplicationContext(((MainUi) ui).getServletContext());
      if (context != null) {
        context.getAutowireCapableBeanFactory().autowireBean(this);
      }
    }
  }

  private void prepareComponents() {
    MessageResource resources = getResources();
    home.setStyleName(HOME);
    home.setText(resources.message(HOME));
    submission.setStyleName(SUBMISSION);
    submission.setText(resources.message(SUBMISSION));
    profile.setStyleName(PROFILE);
    profile.setText(resources.message(PROFILE));
    signout.setStyleName(SIGNOUT);
    signout.setText(resources.message(SIGNOUT));
    changeLanguage.setStyleName(CHANGE_LANGUAGE);
    changeLanguage.setText(resources.message(CHANGE_LANGUAGE));
    manager.setStyleName(MANAGER);
    manager.setText(resources.message(MANAGER));
    validateUsers.setStyleName(VALIDATE_USERS);
    validateUsers.setText(resources.message(VALIDATE_USERS));
    access.setStyleName(ACCESS);
    access.setText(resources.message(ACCESS));
    signas.setStyleName(SIGN_AS);
    signas.setText(resources.message(SIGN_AS));
    register.setStyleName(REGISTER);
    register.setText(resources.message(REGISTER));
    stopSignas.setStyleName(STOP_SIGN_AS);
    stopSignas.setText(resources.message(STOP_SIGN_AS));
    contact.setStyleName(CONTACT);
    contact.setText(resources.message(CONTACT));
    help.setStyleName(HELP);
    help.setText(resources.message(HELP));
  }

  private void updateVisible() {
    if (authorizationService != null) {
      if (authorizationService.isUser()) {
        profile.setVisible(true);
        signout.setVisible(true);
      }
      if (authorizationService.hasUserRole()) {
        submission.setVisible(true);
      }
      if (authorizationService.isRunAs()) {
        manager.setVisible(true);
        stopSignas.setVisible(true);
      }
      if (authorizationService.hasManagerRole()) {
        manager.setVisible(true);
        validateUsers.setVisible(true);
        access.setVisible(true);
      }
      if (authorizationService.hasAdminRole()) {
        manager.setVisible(true);
        validateUsers.setVisible(true);
        access.setVisible(true);
        signas.setVisible(true);
        register.setVisible(true);
      }
    }
  }

  private void changeView(String viewName) {
    logger.debug("Navigate to {}", viewName);
    getUI().getNavigator().navigateTo(viewName);
  }

  private void stopSignas() {
    if (authenticationService != null && authenticationService != null) {
      logger.debug("Stop sign as user {}", authorizationService.getCurrentUser());
      authenticationService.stopRunAs();
      changeView(MainView.VIEW_NAME);
    }
  }

  private void signout() {
    if (authenticationService != null) {
      logger.debug("Signout user {}", authorizationService.getCurrentUser());
      UI ui = getUI();
      if (ui instanceof MainUi) {
        String signoutUrl = ((MainUi) ui).getServletContext().getContextPath();
        signoutUrl += SignoutFilter.SIGNOUT_URL;
        getUI().getPage().setLocation(signoutUrl);
      }
    } else {
      logger.warn("Signout called without an AuthenticationService instance");
      changeView(AccessDeniedView.VIEW_NAME);
    }
  }

  private void changeLanguage() {
    Locale newLocale = Locale.ENGLISH;
    Locale locale = getLocale();
    if (locale != null && locale.getLanguage().equals("en")) {
      newLocale = Locale.FRENCH;
    }
    logger.debug("Change language from {} to {}", locale, newLocale);
    getUI().getSession().setLocale(newLocale);
    getUI().setLocale(newLocale);
    getUI().getPage().reload();
  }
}
