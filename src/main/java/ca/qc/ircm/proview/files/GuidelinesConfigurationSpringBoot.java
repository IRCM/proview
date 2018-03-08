package ca.qc.ircm.proview.files;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

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
