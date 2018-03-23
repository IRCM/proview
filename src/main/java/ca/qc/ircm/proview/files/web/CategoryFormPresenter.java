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
import ca.qc.ircm.proview.files.Guideline;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Button;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Form containing one category of guidelines.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CategoryFormPresenter {
  public static final String CATEGORY = "category";
  public static final String GUIDELINE = "guideline";
  private CategoryForm view;
  private CategoryFormDesign design;
  private Category category;

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(CategoryForm view) {
    this.view = view;
    design = view.design;
  }

  private void prepareComponents() {
    view.setVisible(category != null);
    design.category.addStyleName(CATEGORY);
    if (category != null) {
      design.category.setCaption(category.name());
      design.guidelines.removeAllComponents();
      category.guidelines().forEach(guide -> design.guidelines.addComponent(button(guide)));
    }
  }

  private Button button(Guideline guideline) {
    Button button = new Button();
    button.addStyleName(GUIDELINE);
    button.setCaption(guideline.name());
    button.setIcon(VaadinIcons.DOWNLOAD);
    FileResource resource = new FileResource(guideline.path().toFile());
    FileDownloader fileDownloader = new FileDownloader(resource);
    fileDownloader.extend(button);
    return button;
  }

  Category getValue() {
    return category;
  }

  void setValue(Category category) {
    this.category = category;
    prepareComponents();
  }
}
