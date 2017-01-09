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

package ca.qc.ircm.proview.sample.web.integration;

import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.HEADER;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.NEW_STATUS;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAVE;
import static ca.qc.ircm.proview.web.component.ConfirmDialogComponent.CONFIRM_DIALOG;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.WindowElement;

import java.util.Locale;

public abstract class SampleStatusPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(SampleStatusView.VIEW_NAME);
  }

  protected void openWithSubmissions() {
    openView(SampleStatusView.VIEW_NAME, "442,443");
  }

  private int gridColumnIndex(String property) {
    Object[] columns = SampleStatusViewPresenter.getSamplesColumns();
    for (int i = 0; i < columns.length; i++) {
      if (property.equals(columns[i])) {
        return i;
      }
    }
    return -1;
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected GridElement samplesGrid() {
    return wrap(GridElement.class, findElement(className(SAMPLES)));
  }

  protected ComboBoxElement sampleStatusComboBox(int row) {
    GridElement samplesGrid = samplesGrid();
    return wrap(ComboBoxElement.class,
        samplesGrid.getCell(row, gridColumnIndex(NEW_STATUS)).findElement(className(NEW_STATUS)));
  }

  protected SampleStatus getSampleStatus(int row) {
    String valueLabel = sampleStatusComboBox(row).getValue();
    Locale locale = currentLocale();
    for (SampleStatus status : SampleStatus.values()) {
      if (status.getLabel(locale).equals(valueLabel)) {
        return status;
      }
    }
    return null;
  }

  protected void setSampleStatus(int row, SampleStatus status) {
    sampleStatusComboBox(row).selectByText(status.getLabel(currentLocale()));
  }

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    saveButton().click();
  }

  protected WindowElement confirmWindow() {
    return wrap(WindowElement.class, findElement(className(CONFIRM_DIALOG)));
  }
}
