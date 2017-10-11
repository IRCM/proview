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

package ca.qc.ircm.proview.web.component;

import static ca.qc.ircm.proview.web.WebConstants.SAVED_CONTAINERS;

import ca.qc.ircm.proview.sample.SampleContainer;
import com.vaadin.ui.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Gets/sets saved containers in session.
 */
public interface SavedContainersComponent extends Component {
  default void saveContainers(Collection<SampleContainer> containers) {
    getUI().getSession().setAttribute(SAVED_CONTAINERS, containers);
  }

  /**
   * Returns saved containers.
   *
   * @return saved containers, never null
   */
  @SuppressWarnings("unchecked")
  default List<SampleContainer> savedContainers() {
    Collection<SampleContainer> containers =
        (Collection<SampleContainer>) getUI().getSession().getAttribute(SAVED_CONTAINERS);
    return containers == null ? new ArrayList<>() : new ArrayList<>(containers);
  }
}
