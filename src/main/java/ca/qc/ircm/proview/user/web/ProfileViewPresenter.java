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

import static ca.qc.ircm.proview.user.web.ProfileView.SAVED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import ca.qc.ircm.proview.web.MainView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Profile view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProfileViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(ProfileViewPresenter.class);
  private ProfileView view;
  private UserService service;
  private AuthorizationService authorizationService;

  @Autowired
  protected ProfileViewPresenter(UserService service, AuthorizationService authorizationService) {
    this.service = service;
    this.authorizationService = authorizationService;
  }

  void init(ProfileView view) {
    this.view = view;
    view.form.setUser(authorizationService.getCurrentUser().orElse(null));
  }

  void save(Locale locale) {
    if (view.form.isValid()) {
      User user = view.form.getUser();
      String password = view.form.getPassword();
      logger.debug("save user {}", user);
      service.save(user, password);
      final AppResources resources = new AppResources(ProfileView.class, locale);
      view.showNotification(resources.message(SAVED));
      UI.getCurrent().navigate(MainView.class);
    }
  }
}
