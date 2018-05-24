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

import static ca.qc.ircm.proview.web.CloseWindowOnViewChange.closeWindowOnViewChange;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
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
public class PlateWindowPresenter {
  public static final String WINDOW_STYLE = "plate-window";
  public static final String TITLE = "title";
  public static final String PRINT = "print";
  public static final String PRINT_FILENAME = "plate-print-%s.html";
  public static final String PRINT_MIME = "text/html";
  private static final Logger logger = LoggerFactory.getLogger(PlateWindowPresenter.class);
  private PlateWindow view;
  private PlateWindowDesign design;
  @Inject
  private PlateService plateService;

  protected PlateWindowPresenter() {
  }

  protected PlateWindowPresenter(PlateService plateService) {
    this.plateService = plateService;
  }

  /**
   * Initializes presenter.
   * 
   * @param view
   *          view
   */
  public void init(PlateWindow view) {
    logger.debug("Plate window");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.setHeight("750px");
    view.setWidth("1150px");
    view.addStyleName(WINDOW_STYLE);
    view.setCaption(resources.message(TITLE, ""));
    design.print.addStyleName(PRINT);
    design.print.setCaption(resources.message(PRINT));
    closeWindowOnViewChange(view);
  }

  private void preparePrint(Plate plate) {
    final Locale locale = view.getLocale();
    String content = plateService.print(plate, locale);
    String filename = String.format(PRINT_FILENAME, plate != null ? plate.getName() : "");
    new ArrayList<>(design.print.getExtensions()).stream().forEach(ext -> ext.remove());
    StreamResource printResource = new StreamResource(
        () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), filename);
    printResource.setMIMEType(PRINT_MIME);
    printResource.setCacheTime(0);
    BrowserWindowOpener opener = new BrowserWindowOpener(printResource);
    opener.extend(design.print);
  }

  void setValue(Plate plate) {
    MessageResource resources = view.getResources();
    view.setCaption(resources.message(TITLE, plate.getName()));
    design.plateLayout.setCaption(plate.getName());
    view.plateComponent.setValue(plate);
    preparePrint(plate);
  }
}
