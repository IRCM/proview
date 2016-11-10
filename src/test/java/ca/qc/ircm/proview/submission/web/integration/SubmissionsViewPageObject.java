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

package ca.qc.ircm.proview.submission.web.integration;

import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER_ID;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS_PROPERTY;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

public abstract class SubmissionsViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(SubmissionsView.VIEW_NAME);
  }

  protected LabelElement header() {
    return $(LabelElement.class).id(HEADER_ID);
  }

  protected GridElement submissionsGrid() {
    return $(GridElement.class).id(SUBMISSIONS_PROPERTY);
  }

  protected String experienceByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    ButtonElement button = wrap(ButtonElement.class,
        submissionsGrid.getCell(row, 2).findElement(className("v-button")));
    return button.getCaption();
  }

  protected void clickViewSubmissionByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, 2).click();
  }

  protected void clickViewSubmissionResultsByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, 8).click();
  }
}
