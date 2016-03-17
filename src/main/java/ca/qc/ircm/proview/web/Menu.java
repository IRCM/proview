/*
 * Copyright (c) 2010 Institut de recherches cliniques de Montreal (IRCM)
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

import ca.qc.ircm.proview.utils.web.MessageResourcesComponent;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Menu.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Menu extends CustomComponent implements MessageResourcesComponent {
  private static final long serialVersionUID = 4442788596052318607L;
  private static final Logger logger = LoggerFactory.getLogger(Menu.class);
  private MenuBar menu = new MenuBar();
  private MenuItem home;
  private MenuItem help;

  /**
   * Creates navigation menu.
   */
  public Menu() {
    setCompositionRoot(menu);
    home = menu.addItem("Home", new ChangeViewCommand(MainView.VIEW_NAME));
    help = menu.addItem("Help", new ChangeViewCommand(MainView.VIEW_NAME));
  }

  @Override
  public void attach() {
    super.attach();
    MessageResource resources = getResources();
    home.setText(resources.message("home"));
    help.setText(resources.message("help"));
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
      UI.getCurrent().getNavigator().navigateTo(viewName);
    }
  }
}
