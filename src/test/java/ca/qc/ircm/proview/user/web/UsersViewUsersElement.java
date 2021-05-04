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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.user.web.UsersView.ADD;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;

/**
 * {@link UsersView} users element.
 */
@Element("vaadin-grid")
public class UsersViewUsersElement extends GridElement {
  private static final int EMAIL_COLUMN = 0;
  private static final int LABORATORY_COLUMN = 2;
  private static final int EDIT_COLUMN = 4;

  public GridTHTDElement emailCell(int row) {
    return getCell(row, EMAIL_COLUMN);
  }

  public GridTHTDElement laboratoryCell(int row) {
    return getCell(row, LABORATORY_COLUMN);
  }

  public String email(int row) {
    return getCell(row, EMAIL_COLUMN).getText();
  }

  public ButtonElement edit(int row) {
    return getCell(row, EDIT_COLUMN).$(ButtonElement.class).first();
  }

  public DivElement switchFailed() {
    return $(DivElement.class).id(SWITCH_FAILED);
  }

  public ButtonElement add() {
    return $(ButtonElement.class).id(ADD);
  }

  public ButtonElement switchUser() {
    return $(ButtonElement.class).id(SWITCH_USER);
  }

  public UserDialogElement dialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(UserDialog.ID)))
        .wrap(UserDialogElement.class);
  }

  public LaboratoryDialogElement laboratoryDialog() {
    return ((TestBenchElement) getDriver().findElement(By.id(LaboratoryDialog.ID)))
        .wrap(LaboratoryDialogElement.class);
  }
}
