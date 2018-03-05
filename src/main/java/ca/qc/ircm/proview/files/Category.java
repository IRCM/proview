package ca.qc.ircm.proview.files;

import java.util.List;

/**
 * Guideline's category.
 */
public interface Category {
  /**
   * Returns category's name.
   *
   * @return category's name
   */
  public String name();

  /**
   * Returns category's guidelines.
   *
   * @return category's guidelines
   */
  public List<Guideline> guidelines();
}
