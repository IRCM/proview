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

import static ca.qc.ircm.proview.submission.SubmissionProperties.ANALYSIS_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DATA_AVAILABLE_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION_DATE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLE_DELIVERY_DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.HEADER;
import static ca.qc.ircm.proview.web.WebConstants.EDIT;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;
import static ca.qc.ircm.proview.web.WebConstants.SAVE;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.datepicker.testbench.DatePickerElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class SubmissionDialogElement extends DialogElement {
  public H2Element header() {
    return $(H2Element.class).attributeContains("class", HEADER).first();
  }

  public DatePickerElement sampleDeliveryDate() {
    return $(DatePickerElement.class).attributeContains("class", SAMPLE_DELIVERY_DATE).first();
  }

  public DatePickerElement digestionDate() {
    return $(DatePickerElement.class).attributeContains("class", DIGESTION_DATE).first();
  }

  public DatePickerElement analysisDate() {
    return $(DatePickerElement.class).attributeContains("class", ANALYSIS_DATE).first();
  }

  public DatePickerElement dataAvailableDate() {
    return $(DatePickerElement.class).attributeContains("class", DATA_AVAILABLE_DATE).first();
  }

  public ButtonElement save() {
    return $(ButtonElement.class).attributeContains("class", SAVE).first();
  }

  public void clickSave() {
    save().click();
  }

  public ButtonElement print() {
    return $(ButtonElement.class).attributeContains("class", PRINT).first();
  }

  public void clickPrint() {
    print().click();
  }

  public ButtonElement edit() {
    return $(ButtonElement.class).attributeContains("class", EDIT).first();
  }

  public void clickEdit() {
    edit().click();
  }
}
