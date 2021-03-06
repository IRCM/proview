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

package ca.qc.ircm.proview.test.utils;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Search utilities.
 */
public class SearchUtils {
  public static <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(data -> data.getId() == id).findFirst();
  }

  public static <N extends Named> Optional<N> find(Collection<N> values, String name) {
    return values.stream().filter(data -> name.equals(data.getName())).findFirst();
  }

  public static <C extends SampleContainer> Optional<C> findContainer(Collection<C> containers,
      SampleContainerType type, long id) {
    return containers.stream().filter(sc -> sc.getId() == id && sc.getType() == type).findFirst();
  }

  public static <V> boolean containsInstanceOf(Collection<V> values, Class<? extends V> clazz) {
    return values.stream().filter(extension -> clazz.isInstance(extension)).findAny().isPresent();
  }

  @SuppressWarnings("unchecked")
  public static <V, R extends V> Optional<R> findInstanceOf(Collection<V> values, Class<R> clazz) {
    return values.stream().filter(extension -> clazz.isInstance(extension))
        .map(extension -> (R) extension).findAny();
  }

  /**
   * Returns last element of a list.
   *
   * @param elements
   *          list
   * @return last element of a list
   */
  public static <E> Optional<E> last(List<E> elements) {
    if (elements.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(elements.get(elements.size() - 1));
  }
}
