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

import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.HEADER;
import static ca.qc.ircm.proview.plate.web.PlateViewPresenter.PLATE_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;

public class PlateViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(PlateView.VIEW_NAME);
  }

  protected void openWithPlate() {
    openView(PlateView.VIEW_NAME, "26");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement platePanel() {
    return wrap(PanelElement.class, findElement(className(PLATE_PANEL)));
  }
}
