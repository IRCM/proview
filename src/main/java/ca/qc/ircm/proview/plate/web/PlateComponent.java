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
import ca.qc.ircm.proview.plate.PlateSpot;
import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.ui.CustomComponent;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

/**
 * Plate component that allows selection.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateComponent extends CustomComponent {
  private static final long serialVersionUID = -5886354033312877270L;
  private static final Logger logger = LoggerFactory.getLogger(PlateComponent.class);
  @Inject
  private transient PlateComponentPresenter presenter;
  protected Spreadsheet spreadsheet;

  /**
   * Creates a plate component.
   */
  public PlateComponent() {
    try {
      spreadsheet =
          new Spreadsheet(new XSSFWorkbook(getClass().getResourceAsStream("/Plate-Template.xlsx")));
    } catch (IOException e) {
      logger.error("Could not load plate-template");
      spreadsheet = new Spreadsheet();
    }
    setCompositionRoot(spreadsheet);
  }

  public PlateComponent(PlateComponentPresenter presenter) {
    this();
    this.presenter = presenter;
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public boolean isMultiSelect() {
    return presenter.isMultiSelect();
  }

  public void setMultiSelect(boolean multiSelect) {
    presenter.setMultiSelect(multiSelect);
  }

  /**
   * Returns selected spot.
   *
   * @return selected spot
   */
  public PlateSpot getSelectedSpot() {
    return presenter.getSelectedSpot();
  }

  /**
   * Returns select spots.
   *
   * @return select spots
   */
  public Collection<PlateSpot> getSelectedSpots() {
    return presenter.getSelectedSpots();
  }

  /**
   * Set selected wells.
   * <p>
   * Since only ranges can be selected, all wells in range from min / max row to min / max column of
   * all wells.
   * </p>
   *
   * @param selectedSpots
   *          selected wells
   */
  public void setSelectedSpots(Collection<PlateSpot> selectedSpots) {
    presenter.setSelectedSpots(selectedSpots);
  }

  public Plate getPlate() {
    return presenter.getPlate();
  }

  /**
   * Sets plate, cannot be null.
   *
   * @param plate
   *          plate, cannot be null
   */
  public void setPlate(Plate plate) {
    presenter.setPlate(plate);
  }

  @Override
  public boolean isReadOnly() {
    return presenter.isReadOnly();
  }

  @Override
  public void setReadOnly(boolean readOnly) {
    presenter.setReadOnly(readOnly);
  }
}
