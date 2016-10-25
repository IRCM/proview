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
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.utils.web.MessageResourcesView;
import ca.qc.ircm.proview.web.Menu;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;

import java.util.stream.IntStream;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * View for a plate.
 */
@SpringView(name = PlateView.VIEW_NAME)
@RolesAllowed("USER")
public class PlateView extends PlateViewDesign implements MessageResourcesView {
  public static final String VIEW_NAME = "plate";
  private static final long serialVersionUID = -7006664525905554582L;
  private Menu menu = new Menu();
  private PlateLayout plateLayout = new PlateLayout(12, 8);
  private PlateComponent plateComponent = new PlateComponent();
  @Inject
  private PlateService plateService;
  @Inject
  private PlateComponentPresenter plateComponentPresenter;

  public PlateView() {
    menuLayout.addComponent(menu);
    plateLayoutContainer.addComponent(plateLayout);
    IntStream.range(0, 12).forEach(i -> IntStream.range(0, 8)
        .forEach(j -> plateLayout.addWellComponent(new Label("Sample name"), i, j)));
  }

  @Override
  public void attach() {
    Plate plate = plateService.get(1L);
    plateComponentPresenter.setPlate(plate);
    plateComponentPresenter.setMultiSelect(true);
    plateComponent.setPresenter(plateComponentPresenter);
    super.attach();
    plateComponentPanel.setCaption(plate.getName());
    plateComponentPanel.setContent(plateComponent);
  }
}
