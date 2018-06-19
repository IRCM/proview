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

import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;

import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Registers user presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RegisterViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  private static final Logger logger = LoggerFactory.getLogger(RegisterViewPresenter.class);
  private RegisterView view;
  private RegisterViewDesign design;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  public RegisterViewPresenter() {
  }

  protected RegisterViewPresenter(AuthorizationService authorizationService,
      String applicationName) {
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(RegisterView view) {
    logger.debug("Register user view");
    this.view = view;
    design = view.design;
    prepareComponents();
    addFieldListeners();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
    if (authorizationService.hasAdminRole()) {
      view.setTitle(resources.message(property(TITLE, "admin"), applicationName));
      design.headerLabel.setValue(resources.message(property(HEADER, "admin")));
    }
  }

  private void addFieldListeners() {
    view.userForm.addSaveListener(e -> view.navigateTo(MainView.VIEW_NAME));
  }
}
