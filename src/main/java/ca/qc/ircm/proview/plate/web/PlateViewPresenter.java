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
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.render.PlateImageRenderer;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Plate view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PlateViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String PLATE_PANEL = "platePanel";
  public static final String PLATE = "plate";
  public static final String PRINT = "print";
  public static final String INVALID_PLATE = "plate.invalid";
  public static final String PRINT_TYPE = "png";
  public static final String PRINT_FILENAME = "%s." + PRINT_TYPE;
  public static final String PRINT_NULL_NAME = "template";
  public static final String PRINT_MIME = "image/" + PRINT_TYPE;
  public static final String PRINT_EXCEPTION = "print.exception";
  private static final Logger logger = LoggerFactory.getLogger(PlateViewPresenter.class);
  private PlateView view;
  private PlateViewDesign design;
  @Inject
  private PlateService plateService;
  @Inject
  private PlateImageRenderer plateImageRenderer;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected PlateViewPresenter() {
  }

  protected PlateViewPresenter(PlateService plateService, PlateImageRenderer plateImageRenderer,
      AuthorizationService authorizationService, String applicationName) {
    this.plateService = plateService;
    this.plateImageRenderer = plateImageRenderer;
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(PlateView view) {
    logger.debug("Plate view");
    this.view = view;
    design = view.design;
    prepareComponents();
    updatePlate(null);
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.plate.addStyleName(PLATE);
    design.plate.setVisible(authorizationService.hasAdminRole());
    design.plate.setEmptySelectionAllowed(false);
    design.plate.setItemCaptionGenerator(plate -> plate.getName());
    if (authorizationService.hasAdminRole()) {
      design.plate.setItems(plateService.all(null));
    } else {
      design.plate.setItems();
    }
    design.plate.addValueChangeListener(e -> updatePlate(e.getValue()));
    design.plateComponentPanel.addStyleName(PLATE_PANEL);
    design.print.addStyleName(PRINT);
    design.print.setCaption(resources.message(PRINT));
  }

  private void updatePlate(Plate plate) {
    if (plate != null) {
      design.plateComponentPanel.setCaption(plate.getName());
      view.plateComponent.setValue(plate);
    } else {
      design.plateComponentPanel.setCaption("");
    }
    preparePrint(plate);
  }

  private void preparePrint(Plate plate) {
    final Locale locale = view.getLocale();
    try {
      byte[] image = plateImageRenderer.render(plate, locale, PRINT_TYPE);
      String filename =
          String.format(PRINT_FILENAME, plate != null ? plate.getName() : PRINT_NULL_NAME);
      new ArrayList<>(design.print.getExtensions()).stream().forEach(ext -> ext.remove());
      StreamResource printResource =
          new StreamResource(() -> new ByteArrayInputStream(image), filename);
      printResource.setMIMEType(PRINT_MIME);
      BrowserWindowOpener opener = new BrowserWindowOpener(printResource);
      opener.extend(design.print);
    } catch (IOException e) {
      MessageResource resources = view.getResources();
      view.showWarning(resources.message(PRINT_EXCEPTION, plate != null ? plate.getName() : ""));
    }
  }

  /**
   * Called when view is entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    if (parameters != null && !parameters.isEmpty()) {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set plate {}", id);
        design.plate.setValue(plateService.get(id));
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_PLATE));
      }
    }
  }
}
