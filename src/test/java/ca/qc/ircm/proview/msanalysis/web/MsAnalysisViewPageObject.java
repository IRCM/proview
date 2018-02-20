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

import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITIONS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITION_COUNT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.ACQUISITION_FILE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.COMMENT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.CONTAINERS;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.CONTAINERS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DELETED;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.DOWN;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.HEADER;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.MS_ANALYSIS_PANEL;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.REMOVE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SAVE;
import static ca.qc.ircm.proview.msanalysis.web.MsAnalysisViewPresenter.SOURCE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class MsAnalysisViewPageObject extends AbstractTestBenchTestCase {
  private static final int ACQUISITION_COUNT_COLUMN = 2;
  private static final int SAMPLE_LIST_NAME_COLUMN = 2;
  private static final int ACQUISITION_FILE_COLUMN = 3;
  private static final int COMMENT_COLUMN = 4;

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

  protected ComboBoxElement massDetectionInstrument() {
    return wrap(ComboBoxElement.class, findElement(className(MASS_DETECTION_INSTRUMENT)));
  }

  protected void setMassDetectionInstrument(MassDetectionInstrument instrument) {
    massDetectionInstrument().selectByText(instrument.getLabel(currentLocale()));
    save().focus();
  }

  protected ComboBoxElement source() {
    return wrap(ComboBoxElement.class, findElement(className(SOURCE)));
  }

  protected void setSource(MassDetectionInstrumentSource source) {
    source().selectByText(source.getLabel(currentLocale()));
    save().focus();
  }

  protected PanelElement containersPanel() {
    return wrap(PanelElement.class, findElement(className(CONTAINERS_PANEL)));
  }

  protected GridElement containers() {
    return wrap(GridElement.class, findElement(className(CONTAINERS)));
  }

  protected void setAcquisitionCount(int row, String acquisitionCount) {
    TextFieldElement field = wrap(TextFieldElement.class, containers().getRow(row)
        .getCell(ACQUISITION_COUNT_COLUMN).findElement(className(ACQUISITION_COUNT)));
    field.setValue(acquisitionCount);
    massDetectionInstrument().focus();
  }

  protected PanelElement acquisitionsPanel() {
    return wrap(PanelElement.class, findElement(className(ACQUISITIONS_PANEL)));
  }

  protected GridElement acquisitions() {
    return wrap(GridElement.class, findElement(className(ACQUISITIONS)));
  }

  protected void setSampleListName(int row, String sampleListName) {
    TextFieldElement field = wrap(TextFieldElement.class, acquisitions().getRow(row)
        .getCell(SAMPLE_LIST_NAME_COLUMN).findElement(className(SAMPLE_LIST_NAME)));
    field.setValue(sampleListName);
  }

  protected void setAcquisitionFile(int row, String acquisitionFile) {
    TextFieldElement field = wrap(TextFieldElement.class, acquisitions().getRow(row)
        .getCell(ACQUISITION_FILE_COLUMN).findElement(className(ACQUISITION_FILE)));
    field.setValue(acquisitionFile);
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        acquisitions().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
    field.setValue(comment);
  }

  protected ButtonElement down() {
    return wrap(ButtonElement.class, findElement(className(DOWN)));
  }

  protected void clickDown() {
    down().click();
  }

  protected PanelElement explanationPanel() {
    return wrap(PanelElement.class, findElement(className(EXPLANATION_PANEL)));
  }

  protected TextAreaElement explanation() {
    return wrap(TextAreaElement.class, findElement(className(EXPLANATION)));
  }

  protected ButtonElement save() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    save().click();
  }

  protected ButtonElement remove() {
    return wrap(ButtonElement.class, findElement(className(REMOVE)));
  }

  protected CheckBoxElement banContainers() {
    return wrap(CheckBoxElement.class, findElement(className(BAN_CONTAINERS)));
  }
}
