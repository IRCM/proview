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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DELETED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MS_ANALYSIS_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;

public class MsAnalysisViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(MsAnalysisView.VIEW_NAME);
  }

  protected void openWithMsAnalysis() {
    openView(MsAnalysisView.VIEW_NAME, "14");
  }

  protected void openWithWells() {
    openView(MsAnalysisView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(MsAnalysisView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement deleted() {
    return wrap(LabelElement.class, findElement(className(DELETED)));
  }

  protected PanelElement msAnalysisPanel() {
    return wrap(PanelElement.class, findElement(className(MS_ANALYSIS_PANEL)));
  }

  protected LabelElement massDetectionInstrument() {
    return wrap(LabelElement.class, findElement(className(MASS_DETECTION_INSTRUMENT)));
  }

  protected LabelElement source() {
    return wrap(LabelElement.class, findElement(className(SOURCE)));
  }

  protected PanelElement acquisitionsPanel() {
    return wrap(PanelElement.class, findElement(className(ACQUISITIONS_PANEL)));
  }

  protected GridElement acquisitions() {
    return wrap(GridElement.class, findElement(className(ACQUISITIONS)));
  }

  protected PanelElement explanationPanel() {
    return wrap(PanelElement.class, findElement(className(EXPLANATION_PANEL)));
  }

  protected LabelElement explanation() {
    return wrap(LabelElement.class, findElement(className(EXPLANATION)));
  }
}
