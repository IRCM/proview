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

package ca.qc.ircm.proview.digestion.web;

import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.COMMENT;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DELETED;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DIGESTIONS_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.DOWN;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.HEADER;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.REMOVE;
import static ca.qc.ircm.proview.digestion.web.DigestionViewPresenter.SAVE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class DigestionViewPageObject extends AbstractTestBenchTestCase {
  private static final int COMMENT_COLUMN = 2;

  protected void open() {
    openView(DigestionView.VIEW_NAME);
  }

  protected void openWithDigestion() {
    openView(DigestionView.VIEW_NAME, "195");
  }

  protected void openWithWells() {
    openView(DigestionView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(DigestionView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement deleted() {
    return wrap(LabelElement.class, findElement(className(DELETED)));
  }

  protected PanelElement protocolPanel() {
    return wrap(PanelElement.class, findElement(className(PROTOCOL_PANEL)));
  }

  protected ComboBoxElement protocol() {
    return wrap(ComboBoxElement.class, findElement(className(PROTOCOL)));
  }

  protected void setProtocol(String name) {
    protocol().selectByText(name);
    save().focus();
  }

  protected PanelElement digestionsPanel() {
    return wrap(PanelElement.class, findElement(className(DIGESTIONS_PANEL)));
  }

  protected GridElement digestions() {
    return wrap(GridElement.class, findElement(className(DIGESTIONS)));
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        digestions().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
    field.setValue(comment);
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

  protected CheckBoxElement banContainers() {
    return wrap(CheckBoxElement.class, findElement(className(BAN_CONTAINERS)));
  }
}
