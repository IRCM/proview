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

import ca.qc.ircm.platelayout.PlateLayout;
import com.vaadin.ui.CustomComponent;

/**
 * Plate component that allows selection and drag and drop.
 */
public class PlateComponent extends CustomComponent {
  private static final long serialVersionUID = -5886354033312877270L;
  private transient PlateComponentPresenter presenter;
  protected PlateLayout plateLayout;

  public PlateComponent() {
    plateLayout = new PlateLayout();
    setCompositionRoot(plateLayout);
  }

  public void setPresenter(PlateComponentPresenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }
}
