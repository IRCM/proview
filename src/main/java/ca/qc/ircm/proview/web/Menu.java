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
import ca.qc.ircm.proview.user.web.SignoutFilter;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
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
public class Menu extends CustomComponent implements MessageResourcesComponent {
  public static final String HOME_STYLE = "home";
  public static final String SUBMISSION_STYLE = "submission";
  public static final String SIGNOUT_STYLE = "signout";
  public static final String CHANGE_LANGUAGE_STYLE = "changeLanguage";
  public static final String MANAGER_STYLE = "manager";
  public static final String VALIDATE_USERS_STYLE = "validateUsers";
  public static final String HELP_STYLE = "help";
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
    profile = menu.addItem("Profile", item -> changeView(SubmissionView.VIEW_NAME));
    profile.setVisible(false);
    signout = menu.addItem("Sign out", item -> signout());
    signout.setVisible(false);
    changeLanguage = menu.addItem("Change language", item -> changeLanguage());
    manager = menu.addItem("Manager", null);
    manager.setVisible(false);
    validateUsers = manager.addItem("Validate users", item -> changeView(ValidateView.VIEW_NAME));
    help = menu.addItem("Help", item -> changeView(MainView.VIEW_NAME));
  }

  @Override
  public void attach() {
    super.attach();
    setStyles();
    setCaptions();
    injectBeans();
    if (authorizationService != null) {
      if (authorizationService.isUser()) {
        signout.setVisible(true);
      }
      if (authorizationService.hasUserRole()) {
        submission.setVisible(true);
      }
      if (authorizationService.hasManagerRole() || authorizationService.hasAdminRole()) {
        manager.setVisible(true);
      }
    }
  }

  private void setStyles() {
    home.setStyleName(HOME_STYLE);
    submission.setStyleName(SUBMISSION_STYLE);
    signout.setStyleName(SIGNOUT_STYLE);
    changeLanguage.setStyleName(CHANGE_LANGUAGE_STYLE);
    manager.setStyleName(MANAGER_STYLE);
    validateUsers.setStyleName(VALIDATE_USERS_STYLE);
    help.setStyleName(HELP_STYLE);
  }

  private void setCaptions() {
    MessageResource resources = getResources();
    home.setText(resources.message(HOME_STYLE));
    submission.setText(resources.message(SUBMISSION_STYLE));
    signout.setText(resources.message(SIGNOUT_STYLE));
    changeLanguage.setText(resources.message(CHANGE_LANGUAGE_STYLE));
    manager.setText(resources.message(MANAGER_STYLE));
    validateUsers.setText(resources.message(VALIDATE_USERS_STYLE));
    help.setText(resources.message(HELP_STYLE));
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

  private void changeView(String viewName) {
    logger.debug("Navigate to {}", viewName);
    getUI().getNavigator().navigateTo(viewName);
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
