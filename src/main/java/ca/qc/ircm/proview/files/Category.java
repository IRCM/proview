package ca.qc.ircm.proview.files;

import static ca.qc.ircm.proview.UsedBy.SPRING;

import ca.qc.ircm.proview.UsedBy;
import java.util.ArrayList;
import java.util.List;

/**
 * Guideline's category.
 */
public class Category {

  /**
   * Name.
   */
  private String name;
  /**
   * Guidelines of this category.
   */
  private List<Guideline> guidelines;

  public String getName() {
    return name;
  }

  @UsedBy(SPRING)
  void setName(String name) {
    this.name = name;
  }

  public List<Guideline> getGuidelines() {
    return new ArrayList<>(guidelines);
  }

  @UsedBy(SPRING)
  void setGuidelines(List<Guideline> guidelines) {
    this.guidelines = guidelines;
  }
}
