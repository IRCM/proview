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

package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.HEADER;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.treatment.web.TreatmentViewPresenter.TREATED_SAMPLES_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;

public class TreatmentViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(TreatmentView.VIEW_NAME);
  }

  protected void openWithDigestion() {
    openView(TreatmentView.VIEW_NAME, "195");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected PanelElement protocolPanel() {
    return wrap(PanelElement.class, findElement(className(PROTOCOL_PANEL)));
  }

  protected LabelElement protocol() {
    return wrap(LabelElement.class, findElement(className(PROTOCOL)));
  }

  protected PanelElement treatedSamplesPanel() {
    return wrap(PanelElement.class, findElement(className(TREATED_SAMPLES_PANEL)));
  }

  protected GridElement treatedSamples() {
    return wrap(GridElement.class, findElement(className(TREATED_SAMPLES)));
  }
}
