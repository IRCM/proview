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
import com.vaadin.server.AbstractExtension;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;

/**
 * Closes a window when view is changed.
 */
public class CloseWindowOnViewChange extends AbstractExtension {
  private static final long serialVersionUID = -3954332401718155291L;
  private Window window;
  private Registration registration;

  public CloseWindowOnViewChange() {
  }

  public CloseWindowOnViewChange(Window window) {
    extend(window);
  }

  public void extend(Window window) {
    super.extend(window);
    this.window = window;
    registration = window.getUI().getNavigator().addViewChangeListener(listener());
  }

  @Override
  public void remove() {
    super.remove();
    registration.remove();
    window.close();
  }

  private ViewChangeListener listener() {
    return new CloseWindowOnViewChangeListener(this);
  }

  public static CloseWindowOnViewChange closeWindowOnViewChange(Window window) {
    CloseWindowOnViewChange instance = new CloseWindowOnViewChange();
    instance.extend(window);
    return instance;
  }

  public static class CloseWindowOnViewChangeListener implements ViewChangeListener {
    private static final long serialVersionUID = -7398291644106825356L;
    private CloseWindowOnViewChange extension;

    private CloseWindowOnViewChangeListener(CloseWindowOnViewChange extension) {
      this.extension = extension;
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
      extension.remove();
      return true;
    }
  }
}
