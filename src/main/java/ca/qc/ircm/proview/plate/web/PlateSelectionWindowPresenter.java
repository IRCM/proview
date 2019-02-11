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

import static ca.qc.ircm.proview.plate.PlateProperties.NAME;
import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import java.util.Collections;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Plate window presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateSelectionWindowPresenter {
  public static final String WINDOW_STYLE = "plates-selection-window";
  public static final String TITLE = "title";
  public static final String SELECT_NEW = "selectNew";
  public static final String SELECT = "select";
  private static final Logger logger = LoggerFactory.getLogger(PlateSelectionWindowPresenter.class);
  private PlateSelectionWindow view;
  private PlateSelectionWindowDesign design;
  private Binder<Plate> binder = new BeanValidationBinder<>(Plate.class);
  @Inject
  private PlateService plateService;

  protected PlateSelectionWindowPresenter() {
    binder.setBean(new Plate());
  }

  protected PlateSelectionWindowPresenter(PlateService plateService) {
    this();
    this.plateService = plateService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateSelectionWindow view) {
    logger.debug("Plates selection window");
    this.view = view;
    design = view.design;
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.setHeight("750px");
    view.setWidth("1150px");
    view.addStyleName(WINDOW_STYLE);
    view.setCaption(resources.message(TITLE, ""));
    closeWindowOnViewChange(view);
    design.name.addStyleName(NAME);
    design.name.setCaption(resources.message(NAME));
    binder.forField(design.name).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("")
        .withValidator((name, context) -> validateNewPlateName(name, context)).bind(NAME);
    design.selectNew.addStyleName(SELECT_NEW);
    design.selectNew.setCaption(resources.message(SELECT_NEW));
    design.selectNew.addClickListener(e -> selectNewPlate());
    design.select.addStyleName(SELECT);
    design.select.setCaption(resources.message(SELECT));
    design.select.addClickListener(e -> select());
  }

  private ValidationResult validateNewPlateName(String name, ValueContext context) {
    if (name != null && !plateService.nameAvailable(name)) {
      MessageResource generalResources = view.getGeneralResources();
      return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
    }
    return ValidationResult.ok();
  }

  private void selectNewPlate() {
    if (binder.validate().isOk()) {
      view.fireSaveEvent(binder.getBean());
    }
  }

  private void select() {
    if (!view.platesSelection.getSelectedItems().isEmpty()) {
      view.fireSaveEvent(view.platesSelection.getSelectedItems().iterator().next());
    } else {
      view.fireSaveEvent(null);
    }
  }

  void setValue(Plate plate) {
    if (plate == null) {
      throw new NullPointerException("plate cannot be null");
    }
    if (plate.getId() != null) {
      view.platesSelection.setSelectedItems(Collections.nCopies(1, plate));
    } else {
      binder.setBean(plate);
    }
  }
}
