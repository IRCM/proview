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
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Main presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MainViewPresenter {
  private static final Logger logger = LoggerFactory.getLogger(MainViewPresenter.class);
  private MainView view;
  @Inject
  private AuthorizationService authorizationService;

  public MainViewPresenter() {
  }

  protected MainViewPresenter(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
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
  }

  /**
   * Go to appropriate view depending if user is signed.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (!authorizationService.isAnonymous()) {
      view.navigateTo(SubmissionsView.VIEW_NAME);
    } else {
      view.navigateTo(SigninView.VIEW_NAME);
    }
  }
}
