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

import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.treatment.Solvent.OTHER;

import ca.qc.ircm.proview.treatment.Solvent;
import com.vaadin.flow.component.checkbox.testbench.CheckboxElement;
import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-custom-field")
public class SolventsFieldElement extends FormLayoutElement {
  public CheckboxElement solvent(Solvent solvent) {
    return $(CheckboxElement.class).attribute("class", solvent.name()).first();
  }

  public CheckboxElement methanol() {
    return $(CheckboxElement.class).attribute("class", METHANOL.name()).first();
  }

  public CheckboxElement acetonitrile() {
    return $(CheckboxElement.class).attribute("class", ACETONITRILE.name()).first();
  }

  public CheckboxElement chcl3() {
    return $(CheckboxElement.class).attribute("class", CHCL3.name()).first();
  }

  public CheckboxElement other() {
    return $(CheckboxElement.class).attribute("class", OTHER.name()).first();
  }
}
