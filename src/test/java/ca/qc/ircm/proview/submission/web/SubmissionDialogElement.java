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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.EDIT;
import static ca.qc.ircm.proview.Constants.PRINT;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.id;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.combobox.testbench.ComboBoxElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class SubmissionDialogElement extends DialogElement {
  public H3Element header() {
    return $(H3Element.class).id(id(HEADER));
  }

  public ComboBoxElement instrument() {
    return $(ComboBoxElement.class).id(id(INSTRUMENT));
  }

  public DatePickerElement dataAvailableDate() {
    return $(DatePickerElement.class).id(id(DATA_AVAILABLE_DATE));
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(id(SAVE));
  }

  public void clickSave() {
    save().click();
  }

  public ButtonElement print() {
    return $(ButtonElement.class).id(id(PRINT));
  }

  public void clickPrint() {
    print().click();
  }

  public ButtonElement edit() {
    return $(ButtonElement.class).id(id(EDIT));
  }

  public void clickEdit() {
    edit().click();
  }
}
