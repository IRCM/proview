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

package ca.qc.ircm.proview.files;

import jakarta.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Guidelines' configuration.
 */
@ConfigurationProperties(prefix = GuidelinesConfiguration.PREFIX)
public class GuidelinesConfiguration {
  public static final String PREFIX = "guidelines";
  public static final String DEFAULT_GUIDELINES = "default";
  private Path home;
  private Map<String, List<Category>> categories;

  /**
   * Returns guideline categories for locale.
   *
   * @param locale
   *          locale
   * @return guideline categories for locale
   */
  public List<Category> categories(Locale locale) {
    if (locale != null && categories.containsKey(locale.getLanguage())) {
      return new ArrayList<>(categories.get(locale.getLanguage()));
    }
    return new ArrayList<>(categories.get(DEFAULT_GUIDELINES));
  }

  @PostConstruct
  void init() {
    categories.values().stream().flatMap(cats -> cats.stream())
        .flatMap(cat -> cat.getGuidelines().stream())
        .forEach(gui -> gui.setPath(home.resolve(gui.getPath())));
  }

  Path getHome() {
    return home;
  }

  void setHome(Path home) {
    this.home = home;
  }

  Map<String, List<Category>> getCategories() {
    return categories;
  }

  void setCategories(Map<String, List<Category>> categories) {
    this.categories = categories;
  }
}
