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
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Main view.
 */
@SpringView(name = MainView.VIEW_NAME)
public class MainView extends MainDesign implements MessageResourcesView {
  private static final long serialVersionUID = -2537732272999926530L;
  public static final String VIEW_NAME = "";
  private static final Logger logger = LoggerFactory.getLogger(MainView.class);
  @Inject
  private MainPresenter presenter;
  @Inject
  private AuthorizationService authorizationService;

  @Override
  public void attach() {
    logger.debug("Main view");
    super.attach();
    presenter.init(this);
  }

  public void setTitle(String title) {
    getUI().getPage().setTitle(title);
  }

  public void showError(String error) {
    Notification.show(error, Notification.Type.ERROR_MESSAGE);
  }

  /**
   * User signed successfully.
   */
  public void afterSuccessfulSign() {
    // TODO Replace by actual views.
    if (authorizationService.hasProteomicRole()) {
      getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
    } else {
      getUI().getNavigator().navigateTo(MainView.VIEW_NAME);
    }
  }

  public void afterSuccessfulForgotPassword(String message) {
    Notification.show(message, Notification.Type.WARNING_MESSAGE);
  }

  public void navigateToRegister() {
    getUI().getNavigator().navigateTo(RegisterView.VIEW_NAME);
  }

  public Label getHeader() {
    return header;
  }

  public LoginFormDefault getSignForm() {
    return signForm;
  }

  public Label getForgotPasswordHeader() {
    return forgotPasswordHeader;
  }

  public TextField getForgotPasswordEmailField() {
    return forgotPasswordEmailField;
  }

  public Button getForgotPasswordButton() {
    return forgotPasswordButton;
  }

  public Label getRegisterHeader() {
    return registerHeader;
  }

  public Button getRegisterButton() {
    return registerButton;
  }
}
