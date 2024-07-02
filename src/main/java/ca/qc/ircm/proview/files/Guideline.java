package ca.qc.ircm.proview.files;

import java.nio.file.Path;

/**
 * Guideline.
 */
public class Guideline {
  /**
   * Name.
   */
  private String name;
  /**
   * Guideline's file.
   */
  private Path path;

  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  public Path getPath() {
    return path;
  }

  void setPath(Path path) {
    this.path = path;
  }
}
