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

package ca.qc.ircm.proview.sample.web;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Control view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControlViewPresenter implements BinderValidator, SaveListener<Control> {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String INVALID_SAMPLE = "sample.invalid";
  private static final Logger logger = LoggerFactory.getLogger(ControlViewPresenter.class);
  private ControlView view;
  private ControlViewDesign design;
  @Inject
  private ControlService controlService;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ControlViewPresenter() {
  }

  protected ControlViewPresenter(ControlService controlService,
      AuthorizationService authorizationService, String applicationName) {
    this.controlService = controlService;
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ControlView view) {
    logger.debug("Control view");
    this.view = view;
    design = view.design;
    view.form.setReadOnly(!authorizationService.hasAdminRole());
    view.form.addSaveListener(this);
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.setValue(resources.message(HEADER));
  }

  private boolean validateParameters(String parameters) {
    boolean valid = true;
    try {
      Long id = Long.valueOf(parameters);
      if (controlService.get(id) == null) {
        valid = false;
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    Control control = null;
    if (parameters != null && !parameters.isEmpty()) {
      if (validateParameters(parameters)) {
        Long id = Long.valueOf(parameters);
        control = controlService.get(id);
        view.form.setValue(control);
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLE));
      }
    }
  }

  @Override
  public void saved(SaveEvent<Control> event) {
    Control control = event.getSavedObject();
    view.navigateTo(ControlView.VIEW_NAME + "/" + control.getId());
  }
}
