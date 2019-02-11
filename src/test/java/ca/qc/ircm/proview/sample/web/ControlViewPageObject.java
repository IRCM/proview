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

import static ca.qc.ircm.proview.sample.ControlProperties.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.STANDARDS;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXPLANATION;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.ControlFormPresenter.STANDARDS_CONTAINER;
import static ca.qc.ircm.proview.sample.web.ControlViewPresenter.HEADER;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.COUNT;
import static ca.qc.ircm.proview.sample.web.StandardsFormPresenter.DOWN;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.sample.StandardProperties;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class ControlViewPageObject extends AbstractTestBenchTestCase {
  private static final String STANDARD_NAME = StandardProperties.NAME;
  private static final String STANDARD_QUANTITY = StandardProperties.QUANTITY;
  private static final String STANDARD_COMMENT = StandardProperties.COMMENT;
  private static final int NAME_COLUMN = 0;
  private static final int QUANTITY_COLUMN = 1;
  private static final int COMMENT_COLUMN = 2;

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

  protected ComboBoxElement type() {
    return wrap(ComboBoxElement.class, findElement(className(TYPE)));
  }

  protected String getType() {
    return type().getValue();
  }

  protected void setType(String value) {
    type().selectByText(value);
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

  protected PanelElement standardPanel() {
    return wrap(PanelElement.class, findElement(className(STANDARDS_CONTAINER)));
  }

  protected TextFieldElement standardCountField() {
    return wrap(TextFieldElement.class, standardPanel().findElement(className(COUNT)));
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
    return wrap(TextFieldElement.class,
        standardsGrid().getCell(row, NAME_COLUMN).findElement(className(STANDARD_NAME)));
  }

  protected String getStandardName(int row) {
    return standardNameField(row).getValue();
  }

  protected void setStandardName(int row, String name) {
    standardNameField(row).setValue(name);
  }

  protected TextFieldElement standardQuantityField(int row) {
    return wrap(TextFieldElement.class,
        standardsGrid().getCell(row, QUANTITY_COLUMN).findElement(className(STANDARD_QUANTITY)));
  }

  protected String getStandardQuantity(int row) {
    return standardQuantityField(row).getValue();
  }

  protected void setStandardQuantity(int row, String quantity) {
    standardQuantityField(row).setValue(quantity);
  }

  protected TextFieldElement standardCommentField(int row) {
    return wrap(TextFieldElement.class,
        standardsGrid().getCell(row, COMMENT_COLUMN).findElement(className(STANDARD_COMMENT)));
  }

  protected String getStandardComment(int row) {
    return standardCommentField(row).getValue();
  }

  protected void setStandardComment(int row, String comment) {
    standardCommentField(row).setValue(comment);
  }

  protected ButtonElement fillStandardsButton() {
    return wrap(ButtonElement.class, standardPanel().findElement(className(DOWN)));
  }

  protected PanelElement explanationPanel() {
    return wrap(PanelElement.class, findElement(className(EXPLANATION_PANEL)));
  }

  protected TextAreaElement explanationField() {
    return wrap(TextAreaElement.class, findElement(className(EXPLANATION)));
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
