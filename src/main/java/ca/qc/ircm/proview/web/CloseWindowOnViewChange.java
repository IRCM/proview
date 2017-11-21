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

package ca.qc.ircm.proview.web;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;

/**
 * Closes a window when view is changed.
 */
public class CloseWindowOnViewChange {
  private final Window window;
  private Registration viewChangeListenerRegistration;

  private CloseWindowOnViewChange(Window window) {
    this.window = window;
    viewChangeListenerRegistration =
        window.getUI().getNavigator().addViewChangeListener(listener());
  }

  private ViewChangeListener listener() {
    return e -> {
      viewChangeListenerRegistration.remove();
      window.close();
      return true;
    };
  }

  public static void closeWindowOnViewChange(Window window) {
    new CloseWindowOnViewChange(window);
  }
}
