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
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DATA_ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DILUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.ENRICHMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HISTORY;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.MS_ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_CONTAINERS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_CONTAINERS_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SOLUBILISATION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.STANDARD_ADDITION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TRANSFER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TREATMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.tagName;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class SubmissionsViewPageObject extends AbstractTestBenchTestCase {
  private static final int EXPERIENCE_COLUMN = 1;
  private static final int USER_COLUMN = 2;
  private static final int LINKED_TO_RESULTS_COLUMN = 8;
  private static final int TREATMENTS_COLUMN = 9;
  private static final int HISTORY_COLUMN = 10;

  protected void open() {
    openView(SubmissionsView.VIEW_NAME);
  }

  protected abstract boolean isAdmin();

  protected abstract boolean isManager();

  private int gridColumnIndex(int column) {
    if (!isAdmin() && !isManager() && column >= USER_COLUMN) {
      column--; // User column is hidden.
    }
    if (!isAdmin()) {
      column--; // Select column is hidden.
    }
    return column;
  }

  protected LabelElement header() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected GridElement submissionsGrid() {
    return wrap(GridElement.class, findElement(className(SUBMISSIONS)));
  }

  protected String experienceByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    ButtonElement button = wrap(ButtonElement.class, submissionsGrid
        .getCell(row, gridColumnIndex(EXPERIENCE_COLUMN)).findElement(className(EXPERIENCE)));
    return button.getCaption();
  }

  protected void selectSubmissions(int... rows) {
    GridElement grid = submissionsGrid();
    if (isAdmin()) {
      Set<Integer> rowsSet = IntStream.of(rows).mapToObj(v -> v).collect(Collectors.toSet());
      IntStream.range(0, (int) grid.getRowCount()).forEach(row -> {
        if (rowsSet.contains(row)) {
          grid.getCell(row, 0).findElement(tagName("input")).click();
        }
      });
    } else if (rows.length > 0) {
      grid.getRow(rows[0]).getCell(1).click();
    }
  }

  protected void clickViewSubmissionByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(EXPERIENCE_COLUMN))
        .findElement(className(EXPERIENCE)).click();
  }

  protected void clickViewSubmissionResultsByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(LINKED_TO_RESULTS_COLUMN));
    submissionsGrid.getRow(row).findElement(className(LINKED_TO_RESULTS)).click();
  }

  protected void clickViewSubmissionTreatmentsByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(TREATMENTS_COLUMN));
    submissionsGrid.getRow(row).findElement(className(TREATMENTS)).click();
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

  protected ButtonElement selectSamplesButton() {
    return wrap(ButtonElement.class, findElement(className(SELECT_SAMPLES)));
  }

  protected void clickSelectSamplesButton() {
    selectSamplesButton().click();
  }

  protected LabelElement selectedSamplesLabel() {
    return wrap(LabelElement.class, findElement(className(SELECT_SAMPLES_LABEL)));
  }

  protected ButtonElement selectContainersButton() {
    return wrap(ButtonElement.class, findElement(className(SELECT_CONTAINERS)));
  }

  protected void clickSelectContainersButton() {
    selectContainersButton().click();
  }

  protected LabelElement selectedContainersLabel() {
    return wrap(LabelElement.class, findElement(className(SELECT_CONTAINERS_LABEL)));
  }

  protected ButtonElement updateStatusButton() {
    return wrap(ButtonElement.class, findElement(className(UPDATE_STATUS)));
  }

  protected void clickUpdateStatusButton() {
    updateStatusButton().click();
  }

  protected ButtonElement transferButton() {
    return wrap(ButtonElement.class, findElement(className(TRANSFER)));
  }

  protected void clickTransferButton() {
    transferButton().click();
  }

  protected ButtonElement digestionButton() {
    return wrap(ButtonElement.class, findElement(className(DIGESTION)));
  }

  protected void clickDigestionButton() {
    digestionButton().click();
  }

  protected ButtonElement enrichmentButton() {
    return wrap(ButtonElement.class, findElement(className(ENRICHMENT)));
  }

  protected void clickEnrichmentButton() {
    enrichmentButton().click();
  }

  protected ButtonElement solubilisationButton() {
    return wrap(ButtonElement.class, findElement(className(SOLUBILISATION)));
  }

  protected void clickSolubilisationButton() {
    solubilisationButton().click();
  }

  protected ButtonElement dilutionButton() {
    return wrap(ButtonElement.class, findElement(className(DILUTION)));
  }

  protected void clickDilutionButton() {
    dilutionButton().click();
  }

  protected ButtonElement standardAdditionButton() {
    return wrap(ButtonElement.class, findElement(className(STANDARD_ADDITION)));
  }

  protected void clickStandardAdditionButton() {
    standardAdditionButton().click();
  }

  protected ButtonElement msAnalysisButton() {
    return wrap(ButtonElement.class, findElement(className(MS_ANALYSIS)));
  }

  protected void clickMsAnalysisButton() {
    msAnalysisButton().click();
  }

  protected ButtonElement dataAnalysisButton() {
    return wrap(ButtonElement.class, findElement(className(DATA_ANALYSIS)));
  }

  protected void clickDataAnalysisButton() {
    dataAnalysisButton().click();
  }
}
