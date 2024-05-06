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

package ca.qc.ircm.proview;

/**
 * Explanations for ignoring findbugs warnings.
 */
public class FindbugsExplanations {
  public static final String ENTITY_EI_EXPOSE_REP =
      "Entities should expose internal representation like objects and lists to allow modification";
  public static final String SPRING_BOOT_EI_EXPOSE_REP =
      "Exposed internal representation for objects created by Spring Boot is acceptable";
  public static final String INNER_CLASS_EI_EXPOSE_REP =
      "Exposed internal representation for inner classes is acceptable";
  public static final String CHILD_COMPONENT_EI_EXPOSE_REP =
      "Exposed internal representation for some sub components fields is acceptable";
  public static final String DESIGNER_NP_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD =
      "Vaadin designer sets Design fields after parsing HTML file";
}
