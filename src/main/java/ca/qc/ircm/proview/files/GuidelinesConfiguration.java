package ca.qc.ircm.proview.files;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import jakarta.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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
   * @param locale locale
   * @return guideline categories for locale
   */
  public List<Category> categories(Locale locale) {
    if (categories.containsKey(locale.getLanguage())) {
      return new ArrayList<>(categories.get(locale.getLanguage()));
    }
    return new ArrayList<>(categories.get(DEFAULT_GUIDELINES));
  }

  @PostConstruct
  void init() {
    categories.values().stream().flatMap(Collection::stream)
        .flatMap(cat -> cat.getGuidelines().stream())
        .forEach(gui -> gui.setPath(home.resolve(gui.getPath())));
  }

  Path getHome() {
    return home;
  }

  @UsedBy(SPRING)
  void setHome(Path home) {
    this.home = home;
  }

  Map<String, List<Category>> getCategories() {
    return categories;
  }

  @UsedBy(SPRING)
  void setCategories(Map<String, List<Category>> categories) {
    this.categories = categories;
  }
}
