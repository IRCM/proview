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

package ca.qc.ircm.proview.test.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Finds annotations on method or class.
 */
public interface AnnotationFinder {
  /**
   * Returns annotation if present class or any superclass, or null if annotation is not present.
   *
   * @param object
   *          object on which to search for annotation
   * @param annotationClass
   *          annotation to look for
   * @return annotation if present class or any superclass, or null if annotation is not present
   */
  public default <A extends Annotation> A findAnnotation(Object object, Class<A> annotationClass) {
    return findAnnotation(object, null, annotationClass);
  }

  /**
   * Returns annotation if present on method, class or superclass, or null if annotation is not
   * present.
   *
   * @param object
   *          object on which to search for annotation
   * @param method
   *          method on which to search for annotation
   * @param annotationClass
   *          annotation to look for
   * @return annotation if present on method, class or superclass, or null if annotation is not
   *         present
   */
  public default <A extends Annotation> A findAnnotation(Object object, Method method,
      Class<A> annotationClass) {
    A annotation = method != null ? method.getAnnotation(annotationClass) : null;
    if (annotation == null) {
      Class<?> clazz = object.getClass();
      while (annotation == null && clazz != null) {
        annotation = clazz.getAnnotation(annotationClass);
        clazz = clazz.getSuperclass();
      }
    }
    return annotation;
  }
}