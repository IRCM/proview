package ca.qc.ircm.proview.test.utils;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.treatment.Solvent;

import java.util.Collection;
import java.util.Optional;

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

  public static Optional<SampleSolvent> findSampleSolvent(Collection<SampleSolvent> solvents,
      Solvent solvent) {
    return solvents.stream().filter(so -> so.getSolvent() == solvent).findFirst();
  }

  public static <V> boolean containsInstanceOf(Collection<V> values, Class<? extends V> clazz) {
    return values.stream().filter(extension -> clazz.isInstance(extension)).findAny().isPresent();
  }
}
