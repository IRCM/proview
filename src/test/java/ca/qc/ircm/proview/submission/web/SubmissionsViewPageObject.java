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

import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.ADD_SUBMISSION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HELP;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HISTORY;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

public abstract class SubmissionsViewPageObject extends AbstractTestBenchTestCase {
  private static final int EXPERIMENT_COLUMN = 0;
  private static final int USER_COLUMN = 1;
  private static final int LINKED_TO_RESULTS_COLUMN = 11;
  private static final int HISTORY_COLUMN = 13;

  protected void open() {
    openView(SubmissionsView.VIEW_NAME);
  }

  protected abstract boolean isAdmin();

  protected abstract boolean isManager();

  private int gridColumnIndex(int column) {
    if (!isAdmin() && !isManager() && column >= USER_COLUMN) {
      column--; // Director column is hidden.
    }
    if (!isAdmin() && column >= USER_COLUMN) {
      column--; // User column is hidden.
    }
    return column;
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected ButtonElement help() {
    return wrap(ButtonElement.class, findElement(className(HELP)));
  }

  protected void clickHelp() {
    help().click();
  }

  protected GridElement submissionsGrid() {
    return wrap(GridElement.class, findElement(className(SUBMISSIONS)));
  }

  protected String experimentByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    ButtonElement button = wrap(ButtonElement.class, submissionsGrid
        .getCell(row, gridColumnIndex(EXPERIMENT_COLUMN)).findElement(className(EXPERIMENT)));
    return button.getCaption();
  }

  protected void selectSubmission(int row) {
    submissionsGrid().getCell(row, 1).click();
  }

  protected void clickViewSubmissionByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(EXPERIMENT_COLUMN))
        .findElement(className(EXPERIMENT)).click();
  }

  protected void clickViewSubmissionResultsByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(LINKED_TO_RESULTS_COLUMN));
    submissionsGrid.getRow(row).findElement(className(LINKED_TO_RESULTS)).click();
  }

  protected void clickViewSubmissionHistoryByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(HISTORY_COLUMN));
    submissionsGrid.getRow(row).findElement(className(HISTORY)).click();
  }

  protected ButtonElement addSubmissionButton() {
    return wrap(ButtonElement.class, findElement(className(ADD_SUBMISSION)));
  }

  protected void clickAddSubmissionButton() {
    addSubmissionButton().click();
  }

  protected ButtonElement updateStatusButton() {
    return wrap(ButtonElement.class, findElement(className(UPDATE_STATUS)));
  }

  protected void clickUpdateStatusButton() {
    updateStatusButton().click();
  }
}
