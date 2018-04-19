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

package ca.qc.ircm.proview.fractionation.web;

import static ca.qc.ircm.proview.treatment.QTreatmentSample.treatmentSample;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.treatment.TreatmentSample;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Fractionation view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FractionationViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String TYPE_PANEL = "typePanel";
  public static final String TYPE = "type";
  public static final String FRACTIONS_PANEL = "fractionsPanel";
  public static final String FRACTIONS = "fractions";
  public static final String SAMPLE = treatmentSample.sample.getMetadata().getName();
  public static final String CONTAINER = treatmentSample.container.getMetadata().getName();
  public static final String DESTINATION =
      treatmentSample.destinationContainer.getMetadata().getName();
  public static final String NUMBER = treatmentSample.number.getMetadata().getName();
  public static final String PI_INTERVAL = treatmentSample.piInterval.getMetadata().getName();
  public static final String COMMENT = treatmentSample.comment.getMetadata().getName();
  public static final String INVALID_FRACTIONATION = "fractionation.invalid";
  private static final Logger logger = LoggerFactory.getLogger(FractionationViewPresenter.class);
  private FractionationView view;
  private FractionationViewDesign design;
  private List<TreatmentSample> fractions = new ArrayList<>();
  @Inject
  private FractionationService fractionationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected FractionationViewPresenter() {
  }

  protected FractionationViewPresenter(FractionationService fractionationService,
      String applicationName) {
    this.fractionationService = fractionationService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(FractionationView view) {
    logger.debug("Fractionation view");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.addStyleName(ValoTheme.LABEL_H1);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.typePanel.addStyleName(TYPE_PANEL);
    design.typePanel.addStyleName(REQUIRED);
    design.typePanel.setCaption(resources.message(TYPE_PANEL));
    design.type.addStyleName(TYPE);
    design.fractionsPanel.addStyleName(FRACTIONS_PANEL);
    design.fractionsPanel.setCaption(resources.message(FRACTIONS_PANEL));
    design.fractions.addStyleName(FRACTIONS);
    design.fractions.addStyleName(COMPONENTS);
    design.fractions.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.fractions
        .addColumn(ts -> ts.getContainer() != null ? ts.getContainer().getFullName() : "")
        .setId(CONTAINER).setCaption(resources.message(CONTAINER));
    design.fractions.addColumn(
        ts -> ts.getDestinationContainer() != null ? ts.getDestinationContainer().getFullName()
            : "")
        .setId(DESTINATION).setCaption(resources.message(DESTINATION));
    design.fractions.addColumn(ts -> ts.getNumber()).setId(NUMBER)
        .setCaption(resources.message(NUMBER));
    design.fractions.addColumn(ts -> ts.getPiInterval()).setId(PI_INTERVAL)
        .setCaption(resources.message(PI_INTERVAL)).setHidden(true);
    design.fractions.addColumn(ts -> ts.getComment()).setId(COMMENT)
        .setCaption(resources.message(COMMENT));
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    final Locale locale = view.getLocale();
    if (parameters == null || parameters.isEmpty()) {
      view.showWarning(view.getResources().message(INVALID_FRACTIONATION));
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set fractionation {}", id);
        Fractionation fractionation = fractionationService.get(id);
        if (fractionation != null) {
          fractions = fractionation.getTreatmentSamples();
          design.deleted.setVisible(fractionation.isDeleted());
          design.type.setValue(fractionation.getFractionationType().getLabel(locale));
          design.fractions.getColumn(NUMBER)
              .setHidden(fractionation.getFractionationType() != FractionationType.MUDPIT);
          design.fractions.getColumn(PI_INTERVAL)
              .setHidden(fractionation.getFractionationType() != FractionationType.PI);
        } else {
          view.showWarning(view.getResources().message(INVALID_FRACTIONATION));
        }
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_FRACTIONATION));
      }
    }

    design.fractions.setItems(fractions);
  }
}
