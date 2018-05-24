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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Guidelines' configuration.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = GuidelinesConfigurationSpringBoot.PREFIX)
public class GuidelinesConfigurationSpringBoot implements GuidelinesConfiguration {
  public static final String PREFIX = "guidelines";
  public static final String DEFAULT_GUIDELINES = "default";
  private Path home;
  private Map<String, List<CategoryStringBoot>> categories;

  @Override
  public List<Category> categories(Locale locale) {
    if (locale != null && categories.containsKey(locale.getLanguage())) {
      return new ArrayList<>(categories.get(locale.getLanguage()));
    }
    return new ArrayList<>(categories.get(DEFAULT_GUIDELINES));
  }

  @PostConstruct
  public void init() {
    categories.values().stream().flatMap(cats -> cats.stream())
        .flatMap(cat -> cat.guidelines.stream()).forEach(gui -> gui.home = home);
  }

  public Path getHome() {
    return home;
  }

  public void setHome(Path home) {
    this.home = home;
  }

  public Map<String, List<CategoryStringBoot>> getCategories() {
    return categories;
  }

  public void setCategories(Map<String, List<CategoryStringBoot>> categories) {
    this.categories = categories;
  }

  public static class CategoryStringBoot implements Category {
    private String name;
    private List<GuidelineSpringBoot> guidelines;

    @Override
    public String name() {
      return name;
    }

    @Override
    public List<Guideline> guidelines() {
      return new ArrayList<>(guidelines);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<GuidelineSpringBoot> getGuidelines() {
      return guidelines;
    }

    public void setGuidelines(List<GuidelineSpringBoot> guidelines) {
      this.guidelines = guidelines;
    }
  }

  public static class GuidelineSpringBoot implements Guideline {
    private String name;
    private Path home;
    private Path path;

    @Override
    public String name() {
      return name;
    }

    @Override
    public Path path() {
      return home.resolve(path);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Path getPath() {
      return path;
    }

    public void setPath(Path path) {
      this.path = path;
    }
  }
}
