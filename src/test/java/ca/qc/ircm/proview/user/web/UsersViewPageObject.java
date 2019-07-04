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
import static ca.qc.ircm.proview.user.web.UsersView.HEADER;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_FAILED;
import static ca.qc.ircm.proview.user.web.UsersView.SWITCH_USER;

import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.html.testbench.DivElement;
import com.vaadin.flow.component.html.testbench.H2Element;

public class UsersViewPageObject extends AbstractTestBenchTestCase {
  protected void open() {
    openView(UsersView.VIEW_NAME);
  }

  protected H2Element header() {
    return $(H2Element.class).id(HEADER);
  }

  protected GridElement users() {
    return $(GridElement.class).first();
  }

  protected void clickUser(int row) {
    users().getCell(row, 0).click();
  }

  protected void doubleClickUser(int row) {
    users().getCell(row, 0).doubleClick();
  }

  protected DivElement switchFailed() {
    return $(DivElement.class).id(SWITCH_FAILED);
  }

  protected ButtonElement addButton() {
    return $(ButtonElement.class).id(ADD);
  }

  protected void clickAdd() {
    addButton().click();
  }

  protected ButtonElement switchUserButton() {
    return $(ButtonElement.class).id(SWITCH_USER);
  }

  protected void clickSwitchUser() {
    switchUserButton().click();
  }

  protected DialogElement userDialog() {
    return $(DialogElement.class).id(UserDialog.CLASS_NAME);
  }
}
