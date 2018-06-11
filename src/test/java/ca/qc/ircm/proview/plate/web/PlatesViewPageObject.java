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

import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.NAME;
import static ca.qc.ircm.proview.plate.web.PlatesSelectionComponentPresenter.PLATES;
import static ca.qc.ircm.proview.plate.web.PlatesViewPresenter.HEADER;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

public class PlatesViewPageObject extends AbstractTestBenchTestCase {
  private static final int NAME_COLUMN = 0;

  protected void open() {
    openView(PlatesView.VIEW_NAME);
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected GridElement plates() {
    return wrap(GridElement.class, findElement(className(PLATES)));
  }

  protected String nameByRow(int row) {
    GridElement platesGrid = plates();
    ButtonElement button = wrap(ButtonElement.class,
        platesGrid.getCell(row, NAME_COLUMN).findElement(className(NAME)));
    return button.getCaption();
  }

  protected void clickViewPlateByRow(int row) {
    GridElement platesGrid = plates();
    platesGrid.getCell(row, NAME_COLUMN).findElement(className(NAME)).click();
  }
}
