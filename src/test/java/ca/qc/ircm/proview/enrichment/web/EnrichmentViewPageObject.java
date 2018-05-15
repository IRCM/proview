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

package ca.qc.ircm.proview.enrichment.web;

import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.COMMENT;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.DELETED;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.DOWN;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.HEADER;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.PROTOCOL_PANEL;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.REMOVE;
import static ca.qc.ircm.proview.enrichment.web.EnrichmentViewPresenter.SAVE;
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

public class EnrichmentViewPageObject extends AbstractTestBenchTestCase {
  private static final int COMMENT_COLUMN = 2;
  private static final int DOWN_COLUMN = 3;

  protected void open() {
    openView(EnrichmentView.VIEW_NAME);
  }

  protected void openWithEnrichment() {
    openView(EnrichmentView.VIEW_NAME, "223");
  }

  protected void openWithWells() {
    openView(EnrichmentView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(EnrichmentView.VIEW_NAME, "containers/11,12,4");
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
  }

  protected PanelElement enrichmentsPanel() {
    return wrap(PanelElement.class, findElement(className(ENRICHMENTS_PANEL)));
  }

  protected GridElement enrichments() {
    return wrap(GridElement.class, findElement(className(ENRICHMENTS)));
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        enrichments().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
    field.setValue(comment);
  }

  protected void clickDown(int row) {
    wrap(ButtonElement.class,
        enrichments().getRow(row).getCell(DOWN_COLUMN).findElement(className(DOWN))).click();
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
