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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.CANCEL;
import static ca.qc.ircm.proview.Constants.SAVE;
import static ca.qc.ircm.proview.user.LaboratoryProperties.NAME;
import static ca.qc.ircm.proview.user.web.LaboratoryDialog.id;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link LaboratoryDialog} element.
 */
@Element("vaadin-dialog")
@Attribute(name = "id", value = LaboratoryDialog.ID)
public class LaboratoryDialogElement extends DialogElement {
  public H2Element header() {
    return $(H2Element.class).first();
  }

  public TextFieldElement name() {
    return $(TextFieldElement.class).id(id(NAME));
  }

  public ButtonElement save() {
    return $(ButtonElement.class).id(id(SAVE));
  }

  public ButtonElement cancel() {
    return $(ButtonElement.class).id(id(CANCEL));
  }
}
