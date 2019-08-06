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

import static ca.qc.ircm.proview.submission.web.SubmissionsView.ADD;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.HEADER;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.H2Element;
import com.vaadin.flow.component.orderedlayout.testbench.VerticalLayoutElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("vaadin-vertical-layout")
public class SubmissionsViewElement extends VerticalLayoutElement {
  private static final int EXPERIMENT_COLUMN = 0;
  private static final int VISIBLE_COLUMN = 13;

  public H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  public GridElement submissions() {
    return $(GridElement.class).first();
  }

  public void clickSubmission(int row) {
    submissions().getCell(row, 0).click();
  }

  public ButtonElement visible(int row) {
    return submissions().getCell(row, VISIBLE_COLUMN).$(ButtonElement.class).first();
  }

  public void clickVisible(int row) {
    visible(row).click();
  }

  public void doubleClickSubmission(int row) {
    submissions().getCell(row, 0).doubleClick();
  }

  public String experiment(int row) {
    return submissions().getCell(row, EXPERIMENT_COLUMN).getText();
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public void clickAdd() {
    add().click();
  }
}
