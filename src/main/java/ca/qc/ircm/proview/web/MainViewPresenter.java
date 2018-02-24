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
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.user.web.SigninView;
import ca.qc.ircm.utils.MessageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * Main presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SERVICES_DESCRIPTION = "servicesDescription";
  public static final String SERVICES = "services";
  public static final String BIOMARKER_SITE = "biomarkerSite";
  public static final String ANALYSES = "analyses";
  public static final String RESPONSABILITIES = "responsabilities";
  public static final String SIGNIN = "signin";
  private static final Logger logger = LoggerFactory.getLogger(MainViewPresenter.class);
  private MainView view;
  private MainViewDesign design;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  public MainViewPresenter() {
  }

  protected MainViewPresenter(AuthorizationService authorizationService, String applicationName) {
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Initialize presenter.
   *
   * @param view
   *          view
   */
  public void init(MainView view) {
    logger.debug("Main view");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.servicesDescription.addStyleName(SERVICES_DESCRIPTION);
    design.servicesDescription.setValue(resources.message(SERVICES_DESCRIPTION));
    design.services.addStyleName(SERVICES);
    design.services.setValue(resources.message(SERVICES));
    design.biomarkerSite.addStyleName(BIOMARKER_SITE);
    design.biomarkerSite.setCaption(resources.message(BIOMARKER_SITE));
    design.analyses.addStyleName(ANALYSES);
    design.analyses.setValue(resources.message(ANALYSES));
    design.responsabilities.addStyleName(RESPONSABILITIES);
    design.responsabilities.setValue(resources.message(RESPONSABILITIES));
    design.signin.addStyleName(SIGNIN);
    design.signin.setCaption(resources.message(SIGNIN));
    design.signin.addClickListener(e -> view.navigateTo(SigninView.VIEW_NAME));
  }

  /**
   * Go to submissions view if user is signed.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (authorizationService.isUser()) {
      view.navigateTo(SubmissionsView.VIEW_NAME);
    }
  }
}
