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

package ca.qc.ircm.proview.plate.web;

import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CustomComponent;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * Plate view.
 */
@SpringView(name = PlateView.VIEW_NAME)
@RolesAllowed("USER")
public class PlateView extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "plate";
  private static final long serialVersionUID = -7006664525905554582L;
  protected PlateViewDesign design = new PlateViewDesign();
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  private transient PlateViewPresenter presenter;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
    design.plateComponentPanel.setContent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    presenter.enter(event.getParameters());
  }
}
