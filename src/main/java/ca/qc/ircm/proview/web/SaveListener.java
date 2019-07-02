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

import com.vaadin.flow.internal.ReflectTools;
import java.lang.reflect.Method;

/**
 * Interface for listening for a {@link SavedEvent} fired by a {@link Component}.
 */
public interface SaveListener<V> {
  public static final Method SAVED_METHOD = ReflectTools.findMethod(SaveListener.class, "saved",
      SavedEvent.class);

  /**
   * Called when an object has been saved. A reference to the saved object is given by
   * {@link SavedEvent#getSavedObject()}.
   *
   * @param event
   *          an event containing information about the save
   */
  public void saved(SavedEvent<V> event);
}
