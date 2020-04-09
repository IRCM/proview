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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ALL;
import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.HEADER;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.text.Strings.styleName;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class SamplesStatusDialogElement extends DialogElement {
  private static final int STATUS_INDEX = 1;

  public H2Element header() {
    return $(H2Element.class).attribute("class", HEADER).first();
  }

  public GridElement samples() {
    return $(GridElement.class).attribute("class", SAMPLES).first();
  }

  public ComboBoxElement allStatus() {
    return $(ComboBoxElement.class).attribute("class", styleName(STATUS, ALL)).first();
  }

  public ComboBoxElement status(int row) {
    return samples().getCell(row, STATUS_INDEX).$(ComboBoxElement.class).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).attribute("class", SAVE).first();
  }

  public ButtonElement cancel() {
    return $(ButtonElement.class).attribute("class", CANCEL).first();
  }
}
