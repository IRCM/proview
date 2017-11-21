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

package ca.qc.ircm.proview.fractionation.web;

import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.DELETED;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.FRACTIONS;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.FRACTIONS_PANEL;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.HEADER;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.TYPE;
import static ca.qc.ircm.proview.fractionation.web.FractionationViewPresenter.TYPE_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;

public class FractionationViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(FractionationView.VIEW_NAME);
  }

  protected void openWithFractionation() {
    openView(FractionationView.VIEW_NAME, "203");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement deleted() {
    return wrap(LabelElement.class, findElement(className(DELETED)));
  }

  protected PanelElement typePanel() {
    return wrap(PanelElement.class, findElement(className(TYPE_PANEL)));
  }

  protected LabelElement type() {
    return wrap(LabelElement.class, findElement(className(TYPE)));
  }

  protected PanelElement fractionsPanel() {
    return wrap(PanelElement.class, findElement(className(FRACTIONS_PANEL)));
  }

  protected GridElement fractions() {
    return wrap(GridElement.class, findElement(className(FRACTIONS)));
  }
}
