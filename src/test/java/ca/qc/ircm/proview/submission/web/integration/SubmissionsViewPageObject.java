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

import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class SubmissionsViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(SubmissionsView.VIEW_NAME);
  }

  protected abstract boolean isAdmin();

  private int gridColumnIndex(String property) {
    Object[] columns = SubmissionsViewPresenter.getColumns();
    for (int i = 0; i < columns.length; i++) {
      if (property.equals(columns[i])) {
        return i + (isAdmin() ? 1 : 0); // +1 because of select column.
      }
    }
    return -1;
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
        .getCell(row, gridColumnIndex(EXPERIENCE)).findElement(className("v-button")));
    return button.getCaption();
  }

  protected void selectSubmissions(int... rows) {
    Set<Integer> rowsSet =
        IntStream.range(0, rows.length).mapToObj(i -> rows[i]).collect(Collectors.toSet());
    GridElement grid = submissionsGrid();
    gridRows(grid).filter(r -> rowsSet.contains(r)).forEach(r -> {
      grid.getCell(r, 0).click();
    });
  }

  protected void clickViewSubmissionByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(EXPERIENCE)).click();
  }

  protected void clickViewSubmissionResultsByRow(int row) {
    GridElement submissionsGrid = submissionsGrid();
    submissionsGrid.getCell(row, gridColumnIndex(LINKED_TO_RESULTS)).click();
  }

  protected ButtonElement selectSamplesButton() {
    return wrap(ButtonElement.class, findElement(className(SELECT_SAMPLES)));
  }

  protected void clickSelectSamplesButton() {
    selectSamplesButton().click();
  }

  protected ButtonElement updateStatusButton() {
    return wrap(ButtonElement.class, findElement(className(UPDATE_STATUS)));
  }

  protected void clickUpdateStatusButton() {
    updateStatusButton().click();
  }
}
