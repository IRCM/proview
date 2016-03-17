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
import ca.qc.ircm.proview.user.web.RegisterView;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
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
  private AuthenticationService authenticationService;
  @Inject
  private UI ui;

  /**
   * Initialize view.
   */
  @PostConstruct
  public void init() {
    sign.getHeader().setStyleName("h2");
    sign.addLoginListener(e -> {
      MessageResource resources = getResources();
      String username = e.getUserName();
      String password = e.getPassword();
      try {
        authenticationService.sign(username, password, true);
        //ui.getNavigator().navigateTo(SomeView.VIEW_NAME);
        Notification.show("Success", Notification.Type.TRAY_NOTIFICATION);
      } catch (AuthenticationException ae) {
        Notification.show(resources.message("sign.fail"), Notification.Type.ERROR_MESSAGE);
      }
    });
    forgotPassword.addClickListener(e -> {
      Notification.show("Forgot password", Notification.Type.TRAY_NOTIFICATION);
    });
    register.addClickListener(e -> {
      ui.getNavigator().navigateTo(RegisterView.VIEW_NAME);
    });
  }

  @Override
  public void attach() {
    super.attach();
    logger.debug("Main view");
    MessageResource resources = getResources();
    setCaption(resources.message("title"));
    ui.getPage().setTitle(getCaption());
    header.setValue(resources.message("header"));
    sign.getHeader().setValue(resources.message("sign"));
    forgotPasswordHeader.setValue(resources.message("forgotPassword"));
    email.setCaption(resources.message("forgotPassword.email"));
    forgotPassword.setCaption(resources.message("forgotPassword.button"));
    registerHeader.setValue(resources.message("register"));
    register.setCaption(resources.message("register.button"));
  }
}
