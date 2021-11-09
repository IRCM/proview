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

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.grid.testbench.GridTHTDElement;
import com.vaadin.testbench.elementsbase.Element;

/**
 * {@link SubmissionsView} element.
 */
@Element("vaadin-grid")
public class SubmissionsViewSubmissionsElement extends GridElement {
  private static final int EXPERIMENT_COLUMN = 1;
  private static final int VISIBLE_COLUMN = 11;

  public GridTHTDElement experimentCell(int row) {
    return getCell(row, EXPERIMENT_COLUMN);
  }

  public ButtonElement visible(int row) {
    return getCell(row, VISIBLE_COLUMN).$(ButtonElement.class).first();
  }

  public ButtonElement view(int row) {
    return getCell(row, 0).$(ButtonElement.class).first();
  }
}
