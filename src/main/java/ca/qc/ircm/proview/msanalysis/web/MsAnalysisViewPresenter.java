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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.ACQUISITION_FILE;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.COMMENT;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.CONTAINER;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE;
import static ca.qc.ircm.proview.msanalysis.AcquisitionProperties.SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.msanalysis.MsAnalysisProperties.SOURCE;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Dilution view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsAnalysisViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String MS_ANALYSIS_PANEL = "msAnalysisPanel";
  public static final String ACQUISITIONS_PANEL = "acquisitionsPanel";
  public static final String ACQUISITIONS = "acquisitions";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String INVALID_MS_ANALYSIS = "msAnalysis.invalid";
  private static final Logger logger = LoggerFactory.getLogger(MsAnalysisViewPresenter.class);
  private MsAnalysisView view;
  private MsAnalysisViewDesign design;
  @Inject
  private MsAnalysisService msAnalysisService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected MsAnalysisViewPresenter() {
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(MsAnalysisView view) {
    logger.debug("MS Analysis view");
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
    design.msAnalysisPanel.addStyleName(MS_ANALYSIS_PANEL);
    design.msAnalysisPanel.setCaption(resources.message(MS_ANALYSIS_PANEL));
    design.massDetectionInstrument.addStyleName(MASS_DETECTION_INSTRUMENT);
    design.massDetectionInstrument.setCaption(resources.message(MASS_DETECTION_INSTRUMENT));
    design.massDetectionInstrument.setValue("");
    design.source.addStyleName(SOURCE);
    design.source.setCaption(resources.message(SOURCE));
    design.source.setValue("");
    design.acquisitionsPanel.addStyleName(ACQUISITIONS_PANEL);
    design.acquisitionsPanel.setCaption(resources.message(ACQUISITIONS_PANEL));
    design.acquisitions.addStyleName(ACQUISITIONS);
    design.acquisitions.addStyleName(COMPONENTS);
    design.acquisitions.addColumn(acquisition -> acquisition.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> acquisition.getContainer().getFullName())
        .setId(CONTAINER).setCaption(resources.message(CONTAINER))
        .setStyleGenerator(acquisition -> acquisition.getContainer().isBanned() ? BANNED : "")
        .setSortable(false);
    design.acquisitions.addColumn(acquisition -> acquisition.getSampleListName())
        .setId(SAMPLE_LIST_NAME).setCaption(resources.message(SAMPLE_LIST_NAME)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> acquisition.getAcquisitionFile())
        .setId(ACQUISITION_FILE).setCaption(resources.message(ACQUISITION_FILE)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> acquisition.getComment()).setId(COMMENT)
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
        logger.debug("Set MS analysis {}", id);
        MsAnalysis msAnalysis = msAnalysisService.get(id);
        if (msAnalysis != null) {
          design.deleted.setVisible(msAnalysis.isDeleted());
          design.massDetectionInstrument.setValue(msAnalysis.getMassDetectionInstrument() != null
              ? msAnalysis.getMassDetectionInstrument().getLabel(view.getLocale())
              : "");
          design.source.setValue(
              msAnalysis.getSource() != null ? msAnalysis.getSource().getLabel(view.getLocale())
                  : "");
          design.acquisitions.setItems(msAnalysis.getAcquisitions());
          design.explanationPanel.setVisible(msAnalysis.isDeleted());
          design.explanation.setValue(msAnalysis.getDeletionExplanation());
        } else {
          view.showWarning(resources.message(INVALID_MS_ANALYSIS));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_MS_ANALYSIS));
      }
    }
  }
}
