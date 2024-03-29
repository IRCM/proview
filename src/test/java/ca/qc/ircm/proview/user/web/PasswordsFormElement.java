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

import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD;
import static ca.qc.ircm.proview.user.web.PasswordsForm.PASSWORD_CONFIRM;

import com.vaadin.flow.component.formlayout.testbench.FormLayoutElement;
import com.vaadin.flow.component.textfield.testbench.PasswordFieldElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link PasswordsForm} element.
 */
@Element("vaadin-form-layout")
@Attribute(name = "class", value = PasswordsForm.CLASS_NAME)
public class PasswordsFormElement extends FormLayoutElement {
  public PasswordFieldElement password() {
    return $(PasswordFieldElement.class).attributeContains("class", PASSWORD).first();
  }

  public PasswordFieldElement passwordConfirm() {
    return $(PasswordFieldElement.class).attributeContains("class", PASSWORD_CONFIRM).first();
  }
}
