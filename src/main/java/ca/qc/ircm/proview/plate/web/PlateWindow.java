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

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.Window;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Plate window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateWindow extends Window implements BaseComponent {
  private static final long serialVersionUID = 3546891648320023942L;
  protected PlateWindowDesign design = new PlateWindowDesign();
  private Plate plate;
  @Inject
  protected PlateComponent plateComponent;
  @Inject
  private transient PlateWindowPresenter presenter;

  @PostConstruct
  protected void init() {
    setContent(design);
    design.plateLayout.setContent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
    if (plate != null) {
      presenter.setValue(plate);
    }
  }

  /**
   * Sets plate.
   *
   * @param plate
   *          plate
   */
  public void setValue(Plate plate) {
    this.plate = plate;
    if (isAttached()) {
      presenter.setValue(plate);
    }
  }
}
