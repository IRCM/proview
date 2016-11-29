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

import static ca.qc.ircm.proview.user.web.SignasViewPresenter.COLUMNS;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.EMAIL;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.HEADER;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.SIGN_AS;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.USERS_GRID;
import static ca.qc.ircm.proview.user.web.SignasViewPresenter.VIEW;
import static ca.qc.ircm.proview.web.Menu.MANAGER_STYLE;
import static ca.qc.ircm.proview.web.Menu.STOP_SIGN_AS_STYLE;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.user.web.SignasView;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class SignasPageObject extends AbstractTestBenchTestCase {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SignasPageObject.class);
  private static final int EMAIL_COLUMN = gridColumnIndex(EMAIL);
  private static final int VIEW_COLUMN = gridColumnIndex(VIEW);
  private static final int SIGN_AS_COLUMN = gridColumnIndex(SIGN_AS);

  protected void open() {
    openView(SignasView.VIEW_NAME);
  }

  private static int gridColumnIndex(String property) {
    for (int i = 0; i < COLUMNS.length; i++) {
      if (property.equals(COLUMNS[i])) {
        return i;
      }
    }
    return -1;
  }

  protected LabelElement headerLabel() {
    return wrap(LabelElement.class, findElement(className(HEADER)));
  }

  protected GridElement usersGrid() {
    return wrap(GridElement.class, findElement(className(USERS_GRID)));
  }

  private void processUsersGridRow(String email, Consumer<Integer> consumer) {
    GridElement usersGrid = usersGrid();
    processGridRows(usersGrid, row -> {
      GridCellElement emailCell = usersGrid.getCell(row, EMAIL_COLUMN);
      try {
        if (email.equals(emailCell.getText())) {
          consumer.accept(row);
        }
      } catch (RuntimeException e) {
        throw e;
      }
    });
  }

  protected void clickViewUser(String email) {
    GridElement usersGrid = usersGrid();
    processUsersGridRow(email, row -> {
      usersGrid.scrollLeft(usersGrid.getRect().getX() + usersGrid.getRect().getWidth());
      waitForPageLoad();
      GridCellElement buttonCell = usersGrid.getCell(row, VIEW_COLUMN);
      buttonCell.click();
    });
  }

  protected void clickSignas(String email) {
    GridElement usersGrid = usersGrid();
    processUsersGridRow(email, row -> {
      usersGrid.scrollLeft(usersGrid.getRect().getX() + usersGrid.getRect().getWidth());
      waitForPageLoad();
      GridCellElement buttonCell = usersGrid.getCell(row, SIGN_AS_COLUMN);
      buttonCell.click();
    });
  }

  protected List<String> getUserEmails() {
    GridElement usersGrid = usersGrid();
    List<String> selectedFiles = new ArrayList<>();
    processGridRows(usersGrid, row -> {
      GridCellElement cell = usersGrid.getCell(row, EMAIL_COLUMN);
      selectedFiles.add(cell.getText());
    });
    return selectedFiles;
  }

  private WebElement menuItemByStyle(String className) {
    return findElement(className("v-menubar-menuitem-" + className));
  }

  protected WebElement managerMenuItem() {
    return menuItemByStyle(MANAGER_STYLE);
  }

  protected void clickManager() {
    managerMenuItem().click();
  }

  protected WebElement stopSignasMenuItem() {
    return menuItemByStyle(STOP_SIGN_AS_STYLE);
  }
}
