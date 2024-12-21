package ca.qc.ircm.proview.test.utils;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.DataNullableId;
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
  public static <D extends Data> Optional<D> findData(Collection<D> datas, long id) {
    return datas.stream().filter(data -> data.getId() == id).findFirst();
  }

  public static <D extends DataNullableId> Optional<D> find(Collection<D> datas, long id) {
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
