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
    header.setText(category.name());
    category.guidelines().forEach(guide -> {
      Anchor link = new Anchor();
      link.setText(guide.name());
      link.setHref(new StreamResource(guide.path().getFileName().toString(),
          new PathStreamResourceWriter(guide.path().toFile())));
      add(link);
    });
  }
}
