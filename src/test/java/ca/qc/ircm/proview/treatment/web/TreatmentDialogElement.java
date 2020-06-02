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

import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.DELETED;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.INSERT_TIME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.HEADER;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.flow.component.html.testbench.H4Element;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-dialog")
public class TreatmentDialogElement extends DialogElement {
  public H3Element header() {
    return $(H3Element.class).attributeContains("class", HEADER).first();
  }

  public DivElement deleted() {
    return $(DivElement.class).attributeContains("class", DELETED).first();
  }

  public DivElement protocol() {
    return $(DivElement.class).attributeContains("class", PROTOCOL).first();
  }

  public DivElement fractionationType() {
    return $(DivElement.class).attributeContains("class", FRACTIONATION_TYPE).first();
  }

  public DivElement date() {
    return $(DivElement.class).attributeContains("class", INSERT_TIME).first();
  }

  public H4Element samplesHeader() {
    return $(H4Element.class).attributeContains("class", styleName(TREATED_SAMPLES, HEADER))
        .first();
  }

  public GridElement samples() {
    return $(GridElement.class).attributeContains("class", TREATED_SAMPLES).first();
  }
}
