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

package ca.qc.ircm.proview.user.web;

import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Validate users view.
 */
@SpringView(name = ValidateView.VIEW_NAME)
@RolesAllowed({ "ADMIN", "MANAGER" })
public class ValidateView extends ValidateViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/validate";
  private static final long serialVersionUID = -1956061543048432065L;
  @Inject
  private ValidateViewPresenter presenter;
  @Inject
  private Provider<ViewUserWindow> userWindowProvider;
  protected Menu menu = new Menu();

  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    presenter.init(this);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.attach();
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  /**
   * Open view user window.
   *
   * @param user
   *          user to view
   */
  public void viewUser(User user) {
    ViewUserWindow userWindow = userWindowProvider.get();
    userWindow.center();
    getUI().addWindow(userWindow);
    userWindow.setUser(user);
  }

  public void afterSuccessfulValidate(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
  }
}
