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

package ca.qc.ircm.proview.user.web.integration;

import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.USERS_GRID_ID;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.VALIDATE_SELECTED_BUTTON_ID;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.proview.user.web.ValidateViewPresenter;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import org.openqa.selenium.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ValidatePageObject extends AbstractTestBenchTestCase {
  private static final Logger logger = LoggerFactory.getLogger(ValidatePageObject.class);
  private static final int SELECT_COLUMN = 0;
  private static final int EMAIL_COLUMN = gridColumnIndex(ValidateViewPresenter.EMAIL);
  private static final int VALIDATE_COLUMN = gridColumnIndex(ValidateViewPresenter.VALIDATE);

  protected void open() {
    openView(ValidateView.VIEW_NAME);
  }

  private static int gridColumnIndex(String property) {
    String[] columns = ValidateViewPresenter.getColumns();
    for (int i = 0; i < columns.length; i++) {
      if (property.equals(columns[i])) {
        return i + 1; // +1 because of select column.
      }
    }
    return -1;
  }

  protected LabelElement headerLabel() {
    return $(LabelElement.class).id(HEADER_LABEL_ID);
  }

  protected GridElement usersGrid() {
    return $(GridElement.class).id(USERS_GRID_ID);
  }

  private void processUsersGridRows(Consumer<Integer> consumer) {
    GridElement usersGrid = usersGrid();
    int row = 0;
    try {
      while (true) {
        usersGrid.getRow(row);
        consumer.accept(row);
        row++;
      }
    } catch (NoSuchElementException e) {
      // No more rows.
    }
  }

  protected void clickValidateUser(String email) {
    logger.debug("clickValidateUser for user {}", email);
    GridElement usersGrid = usersGrid();
    processUsersGridRows(row -> {
      GridCellElement emailCell = usersGrid.getCell(row, EMAIL_COLUMN);
      try {
        if (email.equals(emailCell.getText())) {
          GridCellElement buttonCell = usersGrid.getCell(row, VALIDATE_COLUMN);
          buttonCell.click();
        }
      } catch (RuntimeException e) {
        throw e;
      }
    });
  }

  protected void selectUsers(String... emails) {
    Set<String> emailsSet = new HashSet<>(Arrays.asList(emails));
    GridElement usersGrid = usersGrid();
    processUsersGridRows(row -> {
      GridCellElement emailCell = usersGrid.getCell(row, EMAIL_COLUMN);
      if (emailsSet.contains(emailCell.getText())) {
        GridCellElement checkboxCell = usersGrid.getCell(row, SELECT_COLUMN);
        checkboxCell.click();
      }
    });
  }

  protected List<String> getUserEmails() {
    GridElement usersGrid = usersGrid();
    List<String> selectedFiles = new ArrayList<>();
    processUsersGridRows(row -> {
      GridCellElement cell = usersGrid.getCell(row, EMAIL_COLUMN);
      selectedFiles.add(cell.getText());
    });
    return selectedFiles;
  }

  protected ButtonElement validateSelectedButton() {
    return $(ButtonElement.class).id(VALIDATE_SELECTED_BUTTON_ID);
  }

  protected void clickValidateSelected() {
    validateSelectedButton().click();
  }
}
