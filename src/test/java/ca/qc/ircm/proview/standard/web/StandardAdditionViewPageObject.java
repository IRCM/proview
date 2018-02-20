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

package ca.qc.ircm.proview.standard.web;

import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.COMMENT;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.DELETED;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.DOWN;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.HEADER;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.NAME;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.QUANTITY;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.REMOVE;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.SAVE;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.STANDARD_ADDITIONS;
import static ca.qc.ircm.proview.standard.web.StandardAdditionViewPresenter.STANDARD_ADDITIONS_PANEL;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;

import java.util.Objects;

public class StandardAdditionViewPageObject extends AbstractTestBenchTestCase {
  private static final int NAME_COLUMN = 2;
  private static final int QUANTITY_COLUMN = 3;
  private static final int COMMENT_COLUMN = 4;

  protected void open() {
    openView(StandardAdditionView.VIEW_NAME);
  }

  protected void openWithStandardAddition() {
    openView(StandardAdditionView.VIEW_NAME, "248");
  }

  protected void openWithWells() {
    openView(StandardAdditionView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(StandardAdditionView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement deleted() {
    return wrap(LabelElement.class, findElement(className(DELETED)));
  }

  protected PanelElement standardAdditionsPanel() {
    return wrap(PanelElement.class, findElement(className(STANDARD_ADDITIONS_PANEL)));
  }

  protected GridElement standardAdditions() {
    return wrap(GridElement.class, findElement(className(STANDARD_ADDITIONS)));
  }

  protected void setName(int row, String name) {
    TextFieldElement field = wrap(TextFieldElement.class,
        standardAdditions().getRow(row).getCell(NAME_COLUMN).findElement(className(NAME)));
    field.setValue(Objects.toString(name, ""));
  }

  protected void setQuantity(int row, String quantity) {
    TextFieldElement field = wrap(TextFieldElement.class,
        standardAdditions().getRow(row).getCell(QUANTITY_COLUMN).findElement(className(QUANTITY)));
    field.setValue(quantity);
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        standardAdditions().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
    field.setValue(Objects.toString(comment, ""));
  }

  protected ButtonElement down() {
    return wrap(ButtonElement.class, findElement(className(DOWN)));
  }

  protected void clickDown() {
    down().click();
  }

  protected PanelElement explanationPanel() {
    return wrap(PanelElement.class, findElement(className(EXPLANATION_PANEL)));
  }

  protected TextAreaElement explanation() {
    return wrap(TextAreaElement.class, findElement(className(EXPLANATION)));
  }

  protected ButtonElement save() {
    return wrap(ButtonElement.class, findElement(className(SAVE)));
  }

  protected void clickSave() {
    save().click();
  }

  protected ButtonElement remove() {
    return wrap(ButtonElement.class, findElement(className(REMOVE)));
  }

  protected void clickRemove() {
    remove().click();
  }

  protected CheckBoxElement banContainers() {
    return wrap(CheckBoxElement.class, findElement(className(BAN_CONTAINERS)));
  }
}
