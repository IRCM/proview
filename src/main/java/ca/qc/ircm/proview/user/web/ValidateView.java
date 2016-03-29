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
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Validate users view.
 */
@SpringView(name = ValidateView.VIEW_NAME)
@RolesAllowed({ "PROTEOMIC", "MANAGER" })
public class ValidateView extends ValidateDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "user/validate";
  private static final long serialVersionUID = -1956061543048432065L;
  private static final Logger logger = LoggerFactory.getLogger(ValidateView.class);
  @Inject
  private ValidatePresenter presenter;

  @Override
  public void attach() {
    logger.debug("Validate users view");
    super.attach();
    presenter.init(this);
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String message) {
    Notification.show(message, Notification.Type.ERROR_MESSAGE);
  }

  public void viewUser(User user) {
    Notification.show("View user clicked for user " + user.getEmail());
  }

  public void afterSuccessfulValidate(String message) {
    Notification.show(message, Notification.Type.TRAY_NOTIFICATION);
  }

  public Label getHeaderLabel() {
    return headerLabel;
  }

  public Grid getUsersGrid() {
    return usersGrid;
  }

  public Button getValidateSelectedButton() {
    return validateSelectedButton;
  }
}
