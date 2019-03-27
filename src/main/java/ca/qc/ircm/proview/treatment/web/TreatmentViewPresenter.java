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

package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.COMMENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.NAME;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.NUMBER;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.PI_INTERVAL;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.QUANTITY;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SAMPLE;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOLVENT_VOLUME;
import static ca.qc.ircm.proview.treatment.TreatedSampleProperties.SOURCE_VOLUME;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.PROTOCOL;
import static ca.qc.ircm.proview.treatment.TreatmentProperties.TREATED_SAMPLES;
import static ca.qc.ircm.proview.treatment.TreatmentType.DILUTION;
import static ca.qc.ircm.proview.treatment.TreatmentType.FRACTIONATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.SOLUBILISATION;
import static ca.qc.ircm.proview.treatment.TreatmentType.STANDARD_ADDITION;
import static ca.qc.ircm.proview.treatment.TreatmentType.TRANSFER;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.treatment.FractionationType;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.utils.MessageResource;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Treatment view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TreatmentViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String PROTOCOL_PANEL = "protocolPanel";
  public static final String FRACTIONATION_TYPE_PANEL = "fractionationTypePanel";
  public static final String TREATED_SAMPLES_PANEL = "treatedSamplesPanel";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String INVALID_TREATMENT = "treatment.invalid";
  private static final Logger logger = LoggerFactory.getLogger(TreatmentViewPresenter.class);
  private TreatmentView view;
  private TreatmentViewDesign design;
  @Inject
  private TreatmentService treatmentService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected TreatmentViewPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(TreatmentView view) {
    logger.debug("Treatment view");
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.protocolPanel.addStyleName(PROTOCOL_PANEL);
    design.protocolPanel.setCaption(resources.message(PROTOCOL_PANEL));
    design.protocolPanel.setVisible(false);
    design.protocol.addStyleName(PROTOCOL);
    design.fractionationTypePanel.addStyleName(FRACTIONATION_TYPE_PANEL);
    design.fractionationTypePanel.setCaption(resources.message(FRACTIONATION_TYPE_PANEL));
    design.fractionationTypePanel.setVisible(false);
    design.fractionationType.addStyleName(FRACTIONATION_TYPE);
    design.treatedSamplesPanel.addStyleName(TREATED_SAMPLES_PANEL);
    design.treatedSamplesPanel.setCaption(resources.message(TREATED_SAMPLES_PANEL));
    design.treatedSamples.addStyleName(TREATED_SAMPLES);
    design.treatedSamples.addStyleName(COMPONENTS);
    design.treatedSamples.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.treatedSamples.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER))
        .setStyleGenerator(ts -> ts.getContainer().isBanned() ? BANNED : "");
    design.treatedSamples.addColumn(ts -> ts.getSourceVolume()).setId(SOURCE_VOLUME)
        .setCaption(resources.message(SOURCE_VOLUME)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getSolvent()).setId(SOLVENT)
        .setCaption(resources.message(SOLVENT)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getSolventVolume()).setId(SOLVENT_VOLUME)
        .setCaption(resources.message(SOLVENT_VOLUME)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getName()).setId(NAME)
        .setCaption(resources.message(NAME)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getQuantity()).setId(QUANTITY)
        .setCaption(resources.message(QUANTITY)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(DESTINATION_CONTAINER).setCaption(resources.message(DESTINATION_CONTAINER))
        .setStyleGenerator(ts -> ts.getDestinationContainer().isBanned() ? BANNED : "")
        .setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getNumber()).setId(NUMBER)
        .setCaption(resources.message(NUMBER)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getPiInterval()).setId(PI_INTERVAL)
        .setCaption(resources.message(PI_INTERVAL)).setHidden(true);
    design.treatedSamples.addColumn(ts -> ts.getComment()).setId(COMMENT)
        .setCaption(resources.message(COMMENT)).setSortable(false);
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanationPanel.setVisible(false);
    design.explanation.addStyleName(EXPLANATION);
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    final MessageResource resources = view.getResources();
    if (parameters != null && !parameters.isEmpty()) {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set digestion {}", id);
        Treatment treatment = treatmentService.get(id);
        if (treatment != null) {
          design.deleted.setVisible(treatment.isDeleted());
          design.protocolPanel.setVisible(treatment.getProtocol() != null);
          design.protocol
              .setValue(treatment.getProtocol() != null ? treatment.getProtocol().getName() : "");
          design.fractionationTypePanel.setVisible(treatment.getType() == FRACTIONATION);
          design.fractionationType.setValue(treatment.getFractionationType() != null
              ? treatment.getFractionationType().getLabel(view.getLocale())
              : "");
          design.treatedSamples.setItems(treatment.getTreatedSamples());
          design.treatedSamples.getColumn(SOURCE_VOLUME).setHidden(treatment.getType() != DILUTION);
          design.treatedSamples.getColumn(SOLVENT)
              .setHidden(treatment.getType() != DILUTION && treatment.getType() != SOLUBILISATION);
          design.treatedSamples.getColumn(SOLVENT_VOLUME)
              .setHidden(treatment.getType() != DILUTION && treatment.getType() != SOLUBILISATION);
          design.treatedSamples.getColumn(NAME).setHidden(treatment.getType() != STANDARD_ADDITION);
          design.treatedSamples.getColumn(QUANTITY)
              .setHidden(treatment.getType() != STANDARD_ADDITION);
          design.treatedSamples.getColumn(DESTINATION_CONTAINER)
              .setHidden(treatment.getType() != TRANSFER && treatment.getType() != FRACTIONATION);
          design.treatedSamples.getColumn(NUMBER).setHidden(treatment.getType() != FRACTIONATION
              || treatment.getFractionationType() != FractionationType.MUDPIT);
          design.treatedSamples.getColumn(PI_INTERVAL)
              .setHidden(treatment.getType() != FRACTIONATION
                  || treatment.getFractionationType() != FractionationType.PI);
          design.explanationPanel.setVisible(treatment.isDeleted());
          design.explanation.setValue(treatment.getDeletionExplanation());
        } else {
          view.showWarning(resources.message(INVALID_TREATMENT));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_TREATMENT));
      }
    }
  }
}
