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

package ca.qc.ircm.proview.solubilisation.web;

import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.BAN_CONTAINERS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.COMMENT;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.DELETED;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.DOWN;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.EXPLANATION;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.HEADER;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.REMOVE;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SAVE;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLUBILISATIONS;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLUBILISATIONS_PANEL;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLVENT;
import static ca.qc.ircm.proview.solubilisation.web.SolubilisationViewPresenter.SOLVENT_VOLUME;
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

public class SolubilisationViewPageObject extends AbstractTestBenchTestCase {
  private static final int SOLVENT_COLUMN = 2;
  private static final int SOLVENT_VOLUME_COLUMN = 3;
  private static final int COMMENT_COLUMN = 4;

  protected void open() {
    openView(SolubilisationView.VIEW_NAME);
  }

  protected void openWithSolubilisation() {
    openView(SolubilisationView.VIEW_NAME, "236");
  }

  protected void openWithWells() {
    openView(SolubilisationView.VIEW_NAME, "containers/224,236,248");
  }

  protected void openWithTubes() {
    openView(SolubilisationView.VIEW_NAME, "containers/11,12,4");
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected LabelElement deleted() {
    return wrap(LabelElement.class, findElement(className(DELETED)));
  }

  protected PanelElement solubilisationsPanel() {
    return wrap(PanelElement.class, findElement(className(SOLUBILISATIONS_PANEL)));
  }

  protected GridElement solubilisations() {
    return wrap(GridElement.class, findElement(className(SOLUBILISATIONS)));
  }

  protected void setSolvent(int row, String solvent) {
    TextFieldElement field = wrap(TextFieldElement.class,
        solubilisations().getRow(row).getCell(SOLVENT_COLUMN).findElement(className(SOLVENT)));
    field.setValue(solvent);
  }

  protected void setSolventVolume(int row, Double solventVolume) {
    TextFieldElement field = wrap(TextFieldElement.class, solubilisations().getRow(row)
        .getCell(SOLVENT_VOLUME_COLUMN).findElement(className(SOLVENT_VOLUME)));
    field.setValue(Objects.toString(solventVolume, ""));
  }

  protected void setComment(int row, String comment) {
    TextFieldElement field = wrap(TextFieldElement.class,
        solubilisations().getRow(row).getCell(COMMENT_COLUMN).findElement(className(COMMENT)));
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
