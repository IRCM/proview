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
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.ui.CustomComponent;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Form containing one category of guidelines.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CategoryForm extends CustomComponent implements BaseComponent {
  private static final long serialVersionUID = -2957789867158175076L;
  protected CategoryFormDesign design = new CategoryFormDesign();
  @Inject
  private transient CategoryFormPresenter presenter;
  private Category category;

  protected CategoryForm() {
  }

  protected CategoryForm(CategoryFormPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(design);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
    presenter.setValue(category);
  }

  /**
   * Sets category.
   * 
   * @param category
   *          category
   */
  public void setValue(Category category) {
    this.category = category;
    if (isAttached()) {
      presenter.setValue(category);
    }
  }
}
