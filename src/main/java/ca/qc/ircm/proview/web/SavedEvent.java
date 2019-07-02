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

import com.vaadin.flow.component.Component;
import java.util.EventObject;

/**
 * Save event.
 */
public class SaveEvent<V> extends EventObject {
  private static final long serialVersionUID = 7709868652458561869L;
  private V savedObject;

  public SaveEvent(Component source) {
    super(source);
  }

  public SaveEvent(Component source, V savedObject) {
    super(source);
    this.savedObject = savedObject;
  }

  public V getSavedObject() {
    return savedObject;
  }

  public void setSavedObject(V savedObject) {
    this.savedObject = savedObject;
  }
}
