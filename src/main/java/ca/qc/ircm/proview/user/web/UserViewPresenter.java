/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.user.web.UserView.SAVED;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Users dialog presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(UserViewPresenter.class);
  private UserView view;
  private UserService service;

  @Autowired
  protected UserViewPresenter(UserService service) {
    this.service = service;
  }

  void init(UserView view) {
    this.view = view;
  }

  void save(Locale locale) {
    if (view.form.isValid()) {
      User user = view.form.getUser();
      String password = view.form.getPassword();
      logger.debug("save user {} in laboratory {}", user, user.getLaboratory());
      service.save(user, password);
      final AppResources resources = new AppResources(UserView.class, locale);
      view.showNotification(resources.message(SAVED, user.getName()));
      UI.getCurrent().navigate(UsersView.class);
    }
  }

  void setParameter(Long parameter) {
    if (parameter != null) {
      view.form.setUser(service.get(parameter));
    }
  }
}
