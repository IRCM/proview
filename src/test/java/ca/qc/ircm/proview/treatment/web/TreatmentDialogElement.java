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
import static ca.qc.ircm.proview.treatment.web.TreatmentDialog.id;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H3Element;
import com.vaadin.flow.component.html.testbench.H4Element;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link TreatmentDialog} element.
 */
@Element("vaadin-dialog")
public class TreatmentDialogElement extends DialogElement {
  public H3Element header() {
    return $(H3Element.class).id(id(HEADER));
  }

  public DivElement deleted() {
    return $(DivElement.class).id(id(DELETED));
  }

  public DivElement protocol() {
    return $(DivElement.class).id(id(PROTOCOL));
  }

  public DivElement fractionationType() {
    return $(DivElement.class).id(id(FRACTIONATION_TYPE));
  }

  public DivElement date() {
    return $(DivElement.class).id(id(INSERT_TIME));
  }

  public H4Element samplesHeader() {
    return $(H4Element.class).id(id(styleName(TREATED_SAMPLES, HEADER)));
  }

  public GridElement samples() {
    return $(GridElement.class).id(id(TREATED_SAMPLES));
  }
}
