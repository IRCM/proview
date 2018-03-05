package ca.qc.ircm.proview.files;

import java.nio.file.Path;

/**
 * Guideline.
 */
public interface Guideline {
  /**
   * Returns guideline's name.
   * 
   * @return guideline's name
   */
  public String name();

  /**
   * Returns guideline's path.
   * 
   * @return guideline's path
   */
  public Path path();
}
