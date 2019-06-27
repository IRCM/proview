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

package ca.qc.ircm.proview.submission;

import ca.qc.ircm.text.MessageResource;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Protemoics analysis Services.
 */
public enum Service {
  /**
   * LC/MS/MS analysis.
   */
  LC_MS_MS(true),
  /**
   * 2D-LC/MS/MS analysis.
   */
  TWO_DIMENSION_LC_MS_MS(false),
  /**
   * Maldi/MS analysis.
   */
  MALDI_MS(false),
  /**
   * Small molecule analysis.
   */
  SMALL_MOLECULE(true),
  /**
   * Intact protein analysis.
   */
  INTACT_PROTEIN(true);

  public final boolean available;

  Service(boolean available) {
    this.available = available;
  }

  public static List<Service> availables() {
    return Stream.of(Service.values()).filter(service -> service.available)
        .collect(Collectors.toList());
  }

  private static MessageResource getResources(Locale locale) {
    return new MessageResource(Service.class, locale);
  }

  public static String getNullLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message("NULL");
  }

  public String getLabel(Locale locale) {
    MessageResource resources = getResources(locale);
    return resources.message(name());
  }
}
