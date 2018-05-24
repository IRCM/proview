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
   * @param path
   *          path
   * @param descending
   *          true if descending, false for ascending
   * @return {@link OrderSpecifier} for specified path and order
   */
  public static <T extends Comparable<?>> OrderSpecifier<T>
      direction(ComparableExpressionBase<T> path, boolean descending) {
    if (descending) {
      return path.desc();
    } else {
      return path.asc();
    }
  }
}
