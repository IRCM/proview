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

import ca.qc.ircm.proview.security.AuthorizationService;
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
  public static final String CHANGE_PROJECT_STYLE = "changeProject";
  private static final long serialVersionUID = 4442788596052318607L;
  private static final Logger logger = LoggerFactory.getLogger(Menu.class);
  private MenuBar menu = new MenuBar();
  private MenuItem home;
  private MenuItem changeLanguage;
  private MenuItem manager;
  private MenuItem validateUsers;
  private MenuItem help;
  @Inject
  private AuthorizationService authorizationService;

  /**
   * Creates navigation menu.
   */
  public Menu() {
    setCompositionRoot(menu);
    home = menu.addItem("Home", new ChangeViewCommand(MainView.VIEW_NAME));
    changeLanguage = menu.addItem("Change language", new ChangeLanguageCommand());
    manager = menu.addItem("Manager", null);
    manager.setVisible(false);
    validateUsers =
        manager.addItem("Validate users", new ChangeViewCommand(ValidateView.VIEW_NAME));
    help = menu.addItem("Help", new ChangeViewCommand(MainView.VIEW_NAME));
  }

  @Override
  public void attach() {
    super.attach();
    setStyles();
    setCaptions();
    injectBeans();
    if (authorizationService != null) {
      if (authorizationService.hasManagerRole() || authorizationService.hasAdminRole()) {
        manager.setVisible(true);
      }
    }
  }

  private void setStyles() {
  }

  private void setCaptions() {
    MessageResource resources = getResources();
    home.setText(resources.message("home"));
    changeLanguage.setText(resources.message("changeLanguage"));
    manager.setText(resources.message("manager"));
    validateUsers.setText(resources.message("validateUsers"));
    help.setText(resources.message("help"));
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

  private class ChangeViewCommand implements MenuBar.Command {
    private static final long serialVersionUID = -4625560173980983731L;
    private final String viewName;

    ChangeViewCommand(String viewName) {
      this.viewName = viewName;
    }

    @Override
    public void menuSelected(MenuItem selectedItem) {
      logger.debug("Navigate to {}", viewName);
      getUI().getNavigator().navigateTo(viewName);
    }
  }

  private class ChangeLanguageCommand implements MenuBar.Command {
    private static final long serialVersionUID = 6785281901439260013L;

    @Override
    public void menuSelected(MenuItem selectedItem) {
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
}
