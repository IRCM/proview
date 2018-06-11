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
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.component.BaseComponent;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Window;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Plates selection window.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateSelectionWindow extends Window implements BaseComponent {
  private static final long serialVersionUID = 3449238156530889834L;
  protected PlateSelectionWindowDesign design = new PlateSelectionWindowDesign();
  @Inject
  protected PlatesSelectionComponent platesSelection;
  private Plate plate;
  @Inject
  private transient PlateSelectionWindowPresenter presenter;

  protected PlateSelectionWindow() {
  }

  protected PlateSelectionWindow(PlateSelectionWindowPresenter presenter,
      PlatesSelectionComponent platesSelection) {
    this.presenter = presenter;
    this.platesSelection = platesSelection;
  }

  @PostConstruct
  protected void init() {
    setContent(design);
    design.platesSelectionLayout.setContent(platesSelection);
  }

  @Override
  public void attach() {
    super.attach();
    presenter.init(this);
    if (plate != null) {
      presenter.setValue(plate);
    }
  }

  public Registration addSaveListener(SaveListener<Plate> listener) {
    return addListener(SaveEvent.class, listener, SaveListener.SAVED_METHOD);
  }

  protected void fireSaveEvent(Plate plate) {
    fireEvent(new SaveEvent<>(this, plate));
  }

  /**
   * Sets plate.
   *
   * @param plate
   *          plate
   */
  public void setValue(Plate plate) {
    this.plate = plate;
    if (isAttached()) {
      presenter.setValue(plate);
    }
  }
}
