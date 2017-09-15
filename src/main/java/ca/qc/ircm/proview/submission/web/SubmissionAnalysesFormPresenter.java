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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.dataanalysis.QDataAnalysis.dataAnalysis;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Submission results form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionAnalysesFormPresenter {
  public static final String ANALYSIS = msAnalysis.getMetadata().getName();
  public static final String ACQUISITIONS = msAnalysis.acquisitions.getMetadata().getName();
  public static final String SAMPLE = acquisition.sample.getMetadata().getName();
  public static final String NAME = SAMPLE + "." + acquisition.sample.name.getMetadata().getName();
  public static final String ACQUISITION_FILE = acquisition.acquisitionFile.getMetadata().getName();
  public static final String ACQUISITION_INDEX = acquisition.listIndex.getMetadata().getName();
  public static final String DATA_ANALYSES_PANEL = "dataAnalysesPanel";
  public static final String DATA_ANALYSES = "dataAnalyses";
  public static final String DATA_ANALYSIS = dataAnalysis.getMetadata().getName();
  public static final String PROTEIN =
      DATA_ANALYSIS + "." + dataAnalysis.protein.getMetadata().getName();
  public static final String PEPTIDE =
      DATA_ANALYSIS + "." + dataAnalysis.peptide.getMetadata().getName();
  public static final String DATA_ANALYSIS_TYPE =
      DATA_ANALYSIS + "." + dataAnalysis.type.getMetadata().getName();
  public static final String MAX_WORK_TIME =
      DATA_ANALYSIS + "." + dataAnalysis.maxWorkTime.getMetadata().getName();
  public static final String SCORE =
      DATA_ANALYSIS + "." + dataAnalysis.score.getMetadata().getName();
  public static final String WORK_TIME =
      DATA_ANALYSIS + "." + dataAnalysis.workTime.getMetadata().getName();
  public static final String STATUS =
      DATA_ANALYSIS + "." + dataAnalysis.status.getMetadata().getName();
  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(SubmissionAnalysesFormPresenter.class);
  private SubmissionAnalysesForm view;
  private Submission submission;
  @Inject
  private MsAnalysisService msAnalysisService;
  @Inject
  private DataAnalysisService dataAnalysisService;

  protected SubmissionAnalysesFormPresenter() {
  }

  protected SubmissionAnalysesFormPresenter(MsAnalysisService msAnalysisService,
      DataAnalysisService dataAnalysisService) {
    this.msAnalysisService = msAnalysisService;
    this.dataAnalysisService = dataAnalysisService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionAnalysesForm view) {
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.dataAnalysesPanel.addStyleName(DATA_ANALYSES_PANEL);
    view.dataAnalysesPanel.setCaption(resources.message(DATA_ANALYSES_PANEL));
    view.dataAnalyses.addStyleName(DATA_ANALYSES);
    view.dataAnalyses.addColumn(da -> da.getSample().getName()).setId(NAME)
        .setCaption(resources.message(NAME));
    view.dataAnalyses.addColumn(da -> da.getProtein()).setId(PROTEIN)
        .setCaption(resources.message(PROTEIN));
    view.dataAnalyses.addColumn(da -> da.getPeptide()).setId(PEPTIDE)
        .setCaption(resources.message(PEPTIDE));
    view.dataAnalyses.addColumn(da -> da.getType().getLabel(locale)).setId(DATA_ANALYSIS_TYPE)
        .setCaption(resources.message(DATA_ANALYSIS_TYPE));
    view.dataAnalyses.addColumn(da -> da.getMaxWorkTime()).setId(MAX_WORK_TIME)
        .setCaption(resources.message(MAX_WORK_TIME));
    view.dataAnalyses.addColumn(da -> da.getScore()).setId(SCORE)
        .setCaption(resources.message(SCORE));
    view.dataAnalyses.addColumn(da -> da.getWorkTime()).setId(WORK_TIME)
        .setCaption(resources.message(WORK_TIME));
    view.dataAnalyses.addColumn(da -> da.getStatus().getLabel(locale)).setId(STATUS)
        .setCaption(resources.message(STATUS));
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private void createAnalysisPanel(MsAnalysis analysis) {
    final MessageResource resources = view.getResources();
    Panel panel = new Panel();
    view.analysesLayout.addComponent(panel);
    VerticalLayout layout = new VerticalLayout();
    layout.setSizeFull();
    panel.setContent(layout);
    panel.addStyleName(ANALYSIS);
    panel.setSizeFull();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    panel.setCaption(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))));
    Grid<Acquisition> grid = new Grid<>();
    grid.addStyleName(ACQUISITIONS);
    grid.setSizeFull();
    grid.setItems(analysis.getAcquisitions());
    grid.addColumn(acquisition -> acquisition.getSample().getName()).setId(NAME)
        .setCaption(resources.message(NAME));
    grid.addColumn(acquisition -> acquisition.getAcquisitionFile()).setId(ACQUISITION_FILE)
        .setCaption(resources.message(ACQUISITION_FILE));
    grid.addColumn(acquisition -> acquisition.getListIndex()).setId(ACQUISITION_INDEX)
        .setHidden(true);
    grid.sort(ACQUISITION_INDEX);
    layout.addComponent(grid);
  }

  Submission getBean() {
    return submission;
  }

  void setBean(Submission submission) {
    this.submission = submission;
    List<MsAnalysis> analyses = msAnalysisService.all(submission);
    view.analysesLayout.removeAllComponents();
    analyses.forEach(analysis -> {
      createAnalysisPanel(analysis);
    });
    List<DataAnalysis> dataAnalyses = dataAnalysisService.all(submission);
    view.dataAnalyses.setItems(dataAnalyses);
    view.dataAnalysesPanel.setVisible(!dataAnalyses.isEmpty());
  }
}
