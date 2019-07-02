package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.files.Category;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

/**
 * Category component.
 */
public class CategoryComponent extends VerticalLayout {
  public static final String CATEGORY = "category";
  private static final long serialVersionUID = -6619674234077142003L;
  protected H2 header = new H2();

  /**
   * Creates a category component for specified category.
   *
   * @param category
   *          category
   */
  public CategoryComponent(Category category) {
    addClassName(CATEGORY);
    setPadding(false);
    add(header);
    header.setText(category.name());
    category.guidelines().forEach(guide -> {
      Anchor link = new Anchor();
      link.setText(guide.name());
      link.setHref(new StreamResource(guide.path().getFileName().toString(),
          new PathStreamResourceWriter(guide.path())));
      add(link);
    });
  }
}
