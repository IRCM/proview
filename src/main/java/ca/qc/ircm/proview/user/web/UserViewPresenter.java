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

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * User view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RolesAllowed({ "USER" })
public class UserViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String INVALID_USER = "user.invalid";
  private static final Logger logger = LoggerFactory.getLogger(UserViewPresenter.class);
  private UserView view;
  @Inject
  private UserService userService;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected UserViewPresenter() {
  }

  protected UserViewPresenter(UserService userService, AuthorizationService authorizationService,
      String applicationName) {
    this.userService = userService;
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(UserView view) {
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    view.setTitle(view.getResources().message(TITLE, applicationName));
    view.header.addStyleName(HEADER);
    view.header.addStyleName("h1");
    view.header.setValue(view.getResources().message(HEADER));
  }

  /**
   * Called by view when view is entered.
   *
   * @param parameters
   *          parameters
   */
  public void enter(String parameters) {
    User user;
    if (parameters != null && !parameters.isEmpty()) {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set user {}", id);
        user = userService.get(id);
      } catch (NumberFormatException e) {
        user = null;
      }
    } else {
      user = authorizationService.getCurrentUser();
    }

    if (user == null) {
      view.showWarning(view.getResources().message(INVALID_USER));
    } else {
      view.userFormPresenter.setBean(user);
      view.userFormPresenter.setEditable(editable(user));
    }
  }

  private boolean editable(User user) {
    return authorizationService.hasUserWritePermission(user);
  }
}
