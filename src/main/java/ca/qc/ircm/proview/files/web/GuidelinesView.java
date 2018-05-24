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

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Guidelines view.
 */
@SpringView(name = GuidelinesView.VIEW_NAME)
@RolesAllowed({ "USER" })
public class GuidelinesView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "guidelines";
  private static final long serialVersionUID = -2957789867158175076L;
  protected GuidelinesViewDesign design = new GuidelinesViewDesign();
  @Inject
  protected GuidelinesForm guidelinesForm;
  @Inject
  private transient GuidelinesViewPresenter presenter;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
    design.guidelinesLayout.addComponent(guidelinesForm);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
