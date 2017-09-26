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

import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.FILL_STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXPLANATION;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COMMENTS;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_COUNT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_NAME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARD_QUANTITY;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SUPPORT;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.VOLUME;
import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.HEADER;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class ControlViewPageObject extends AbstractTestBenchTestCase {
  private static final int NAME_COLUMN = 0;
  private static final int QUANTITY_COLUMN = 1;
  private static final int COMMENTS_COLUMN = 2;

  protected void open() {
    openView(ControlView.VIEW_NAME);
  }

  protected void open(String parameters) {
    openView(ControlView.VIEW_NAME, parameters);
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected TextFieldElement nameField() {
    return wrap(TextFieldElement.class, findElement(className(NAME)));
  }

  protected String getName() {
    return nameField().getValue();
  }

  protected void setName(String value) {
    nameField().setValue(value);
  }

  protected ComboBoxElement supportField() {
    return wrap(ComboBoxElement.class, findElement(className(SUPPORT)));
  }

  protected String getSupport() {
    return supportField().getValue();
  }

  protected void setSupport(String value) {
    supportField().selectByText(value);
  }

  protected TextFieldElement quantityField() {
    return wrap(TextFieldElement.class, findElement(className(QUANTITY)));
  }

  protected String getQuantity() {
    return quantityField().getValue();
  }

  protected void setQuantity(String value) {
    quantityField().setValue(value);
  }

  protected TextFieldElement volumeField() {
    return wrap(TextFieldElement.class, findElement(className(VOLUME)));
  }

  protected String getVolume() {
    return volumeField().getValue();
  }

  protected void setVolume(String value) {
    volumeField().setValue(value);
  }

  protected ComboBoxElement controlTypeField() {
    return wrap(ComboBoxElement.class, findElement(className(CONTROL_TYPE)));
  }

  protected String getControlType() {
    return controlTypeField().getValue();
  }

  protected void setControlType(String value) {
    controlTypeField().selectByText(value);
  }

  protected TextFieldElement standardCountField() {
    return wrap(TextFieldElement.class, findElement(className(STANDARD_COUNT)));
  }

  protected String getStandardCount() {
    return standardCountField().getValue();
  }

  protected void setStandardCount(String value) {
    standardCountField().setValue(value);
  }

  protected GridElement standardsGrid() {
    return wrap(GridElement.class, findElement(className(STANDARDS)));
  }

  protected TextFieldElement standardNameField(int row) {
    return wrap(TextFieldElement.class, standardsGrid().getCell(row, NAME_COLUMN)
        .findElement(className(STANDARDS + "." + STANDARD_NAME)));
  }

  protected String getStandardName(int row) {
    return standardNameField(row).getValue();
  }

  protected void setStandardName(int row, String name) {
    standardNameField(row).setValue(name);
  }

  protected TextFieldElement standardQuantityField(int row) {
    return wrap(TextFieldElement.class, standardsGrid().getCell(row, QUANTITY_COLUMN)
        .findElement(className(STANDARDS + "." + STANDARD_QUANTITY)));
  }

  protected String getStandardQuantity(int row) {
    return standardQuantityField(row).getValue();
  }

  protected void setStandardQuantity(int row, String quantity) {
    standardQuantityField(row).setValue(quantity);
  }

  protected TextFieldElement standardCommentsField(int row) {
    return wrap(TextFieldElement.class, standardsGrid().getCell(row, COMMENTS_COLUMN)
        .findElement(className(STANDARDS + "." + STANDARD_COMMENTS)));
  }

  protected String getStandardComments(int row) {
    return standardCommentsField(row).getValue();
  }

  protected void setStandardComments(int row, String comment) {
    standardCommentsField(row).setValue(comment);
  }

  protected ButtonElement fillStandardsButton() {
    return wrap(ButtonElement.class, findElement(className(FILL_STANDARDS)));
  }

  protected TextFieldElement explanationField() {
    return wrap(TextFieldElement.class, findElement(className(EXPLANATION)));
  }

  protected String getExplanation() {
    return explanationField().getValue();
  }

  protected void setExplanation(String value) {
    explanationField().setValue(value);
  }

  protected ButtonElement saveButton() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    saveButton().click();
  }
}
