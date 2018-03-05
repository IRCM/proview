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

package ca.qc.ircm.proview.files.web;

import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Guidelines window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GuidelinesWindowPresenter {
  public static final String WINDOW_STYLE = "guidelines-window";
  public static final String TITLE = "title";
  private static final Logger logger = LoggerFactory.getLogger(GuidelinesWindowPresenter.class);

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(GuidelinesWindow view) {
    logger.debug("Guidelines window");
    view.addStyleName(WINDOW_STYLE);
    view.setCaption(view.getResources().message(TITLE));
    closeWindowOnViewChange(view);
  }
}