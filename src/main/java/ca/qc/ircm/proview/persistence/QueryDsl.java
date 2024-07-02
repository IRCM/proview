package ca.qc.ircm.proview.persistence;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.ComparableExpressionBase;

/**
 * Utilities for Query DSL.
 */
public class QueryDsl {
  public static String qname(Path<?> path) {
    return path.getMetadata().getName();
  }

  /**
   * Returns {@link OrderSpecifier} for specified path and order.
   *
   * @param <T>
   *          path type
   * @param path
   *          path
   * @param descending
   *          true if descending, false for ascending
   * @return {@link OrderSpecifier} for specified path and order
   */
  @SuppressWarnings("rawtypes")
  public static <T extends Comparable> OrderSpecifier<T> direction(ComparableExpressionBase<T> path,
      boolean descending) {
    if (descending) {
      return path.desc();
    } else {
      return path.asc();
    }
  }
}
