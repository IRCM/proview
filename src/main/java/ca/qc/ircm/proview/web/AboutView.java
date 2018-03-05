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

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.inject.Inject;

/**
 * About view.
 */
@SpringView(name = AboutView.VIEW_NAME)
public class AboutView extends CustomComponent implements BaseView {
  private static final long serialVersionUID = -2537732272999926530L;
  public static final String VIEW_NAME = "about";
  protected AboutViewDesign design = new AboutViewDesign();
  @Inject
  private transient AboutViewPresenter presenter;

  /**
   * Creates introduction view.
   */
  public AboutView() {
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}