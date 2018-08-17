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

package ca.qc.ircm.proview.plate.web;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.web.view.BaseView;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import java.util.Collection;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Plates selection component.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlatesSelectionComponent extends CustomComponent implements BaseView {
  public static final String VIEW_NAME = "plates";
  private static final long serialVersionUID = -3581153300579000271L;
  protected Grid<Plate> plates = new Grid<>();
  @Inject
  private transient PlatesSelectionComponentPresenter presenter;

  protected PlatesSelectionComponent() {
  }

  protected PlatesSelectionComponent(PlatesSelectionComponentPresenter presenter) {
    this.presenter = presenter;
  }

  /**
   * Initializes view.
   */
  @PostConstruct
  public void init() {
    setCompositionRoot(plates);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
  }

  public void setSelectionMode(SelectionMode selectionMode) {
    plates.setSelectionMode(selectionMode);
  }

  public Set<Plate> getSelectedItems() {
    return plates.getSelectedItems();
  }

  /**
   * Sets selected items.
   *
   * @param plates
   *          selected plates
   */
  public void setSelectedItems(Collection<Plate> plates) {
    this.plates.deselectAll();
    if (plates != null) {
      plates.forEach(plate -> this.plates.select(plate));
    }
  }

  public boolean isExcludeSubmissionPlates() {
    return presenter.isExcludeSubmissionPlates();
  }

  public void setExcludeSubmissionPlates(boolean excludeSubmissionPlates) {
    presenter.setExcludeSubmissionPlates(excludeSubmissionPlates);
  }
}
