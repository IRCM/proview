package ca.qc.ircm.proview.files.web;

import ca.qc.ircm.proview.files.Category;
import ca.qc.ircm.proview.web.PathStreamResourceWriter;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;

/**
 * Category component.
 */
public class CategoryComponent extends VerticalLayout {
  public static final String CATEGORY = "category";
  private static final long serialVersionUID = -6619674234077142003L;
  protected H3 header = new H3();

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
    header.setText(category.getName());
    category.getGuidelines().forEach(guide -> {
      Anchor link = new Anchor();
      link.setText(guide.getName());
      link.setHref(new StreamResource(guide.getPath().getFileName().toString(),
          new PathStreamResourceWriter(guide.getPath().toFile())));
      add(link);
    });
  }
}
