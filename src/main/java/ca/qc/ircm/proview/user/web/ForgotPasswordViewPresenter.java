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

import static ca.qc.ircm.proview.user.web.ForgotPasswordView.INVALID;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SAVED;
import static ca.qc.ircm.proview.user.web.ForgotPasswordView.SEPARATOR;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.user.ForgotPassword;
import ca.qc.ircm.proview.user.ForgotPasswordService;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Forgot password view presenter.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ForgotPasswordViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordViewPresenter.class);
  private ForgotPasswordView view;
  private ForgotPassword forgotPassword;
  private ForgotPasswordService service;

  @Autowired
  protected ForgotPasswordViewPresenter(ForgotPasswordService service) {
    this.service = service;
  }

  void init(ForgotPasswordView view) {
    this.view = view;
  }

  void save(Locale locale) {
    if (view.form.isValid()) {
      String password = view.form.getPassword();
      logger.debug("save new password for user {}", forgotPassword.getUser());
      service.updatePassword(forgotPassword, password);
      final AppResources resources = new AppResources(ForgotPasswordView.class, locale);
      view.showNotification(resources.message(SAVED));
      UI.getCurrent().navigate(SigninView.class);
    }
  }

  private boolean validateParameter(String parameter, Locale locale) {
    final AppResources resources = new AppResources(ForgotPasswordView.class, locale);
    if (parameter == null) {
      view.showNotification(resources.message(INVALID));
      return false;
    }

    String[] parameters = parameter.split(SEPARATOR, -1);
    boolean valid = true;
    if (parameters.length < 2) {
      valid = false;
    } else {
      try {
        long id = Long.parseLong(parameters[0]);
        String confirmNumber = parameters[1];
        if (service.get(id, confirmNumber) == null) {
          valid = false;
        }
      } catch (NumberFormatException e) {
        valid = false;
      }
    }
    if (!valid) {
      view.showNotification(resources.message(INVALID));
    }
    return valid;
  }

  void setParameter(String parameter, Locale locale) {
    if (validateParameter(parameter, locale)) {
      String[] parameters = parameter.split(SEPARATOR, -1);
      long id = Long.parseLong(parameters[0]);
      String confirmNumber = parameters[1];
      forgotPassword = service.get(id, confirmNumber);
    } else {
      view.save.setEnabled(false);
      view.form.setEnabled(false);
    }
  }
}
