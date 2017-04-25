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
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.web.Menu;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;

import java.util.stream.IntStream;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * View for a plate.
 */
@SpringView(name = PlateView.VIEW_NAME)
@RolesAllowed("USER")
public class PlateView extends PlateViewDesign implements BaseView {
  public static final String VIEW_NAME = "plate";
  private static final long serialVersionUID = -7006664525905554582L;
  private Menu menu = new Menu();
  private PlateLayout plateLayout = new PlateLayout(12, 8);
  @Inject
  private PlateComponent plateComponent;
  @Inject
  private transient PlateService plateService;

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    menuLayout.addComponent(menu);
    plateLayoutContainer.addComponent(plateLayout);
    IntStream.range(0, 12).forEach(i -> IntStream.range(0, 8)
        .forEach(j -> plateLayout.addComponent(new Label("Sample name"), i, j)));
    plateComponentPanel.setContent(plateComponent);
  }

  @Override
  public void attach() {
    super.attach();
    Plate plate = plateService.get(1L);
    plateComponent.getPresenter().setPlate(plate);
    plateComponent.getPresenter().setMultiSelect(true);
    plateComponentPanel.setCaption(plate.getName());
  }
}
