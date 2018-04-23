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
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
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
import java.util.Objects;

import javax.inject.Inject;

/**
 * Submission results form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionAnalysesFormPresenter {
  public static final String ANALYSIS = msAnalysis.getMetadata().getName();
  public static final String VIEW = "view";
  public static final String ACQUISITIONS = msAnalysis.acquisitions.getMetadata().getName();
  public static final String SAMPLE = acquisition.sample.getMetadata().getName();
  public static final String NAME =
      property(SAMPLE, acquisition.sample.name.getMetadata().getName());
  public static final String ACQUISITION_FILE = acquisition.acquisitionFile.getMetadata().getName();
  public static final String DATA_ANALYSES_PANEL = "dataAnalysesPanel";
  public static final String DATA_ANALYSES = "dataAnalyses";
  public static final String PROTEIN = dataAnalysis.protein.getMetadata().getName();
  public static final String PEPTIDE = dataAnalysis.peptide.getMetadata().getName();
  public static final String DATA_ANALYSIS_TYPE = dataAnalysis.type.getMetadata().getName();
  public static final String MAX_WORK_TIME = dataAnalysis.maxWorkTime.getMetadata().getName();
  public static final String SCORE = dataAnalysis.score.getMetadata().getName();
  public static final String WORK_TIME = dataAnalysis.workTime.getMetadata().getName();
  public static final String STATUS = dataAnalysis.status.getMetadata().getName();
  public static final String DESCRIPTION = "description";
  public static final String VALUE = "value";

  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(SubmissionAnalysesFormPresenter.class);
  private SubmissionAnalysesForm view;
  private SubmissionAnalysesFormDesign design;
  private Submission submission;
  @Inject
  private MsAnalysisService msAnalysisService;
  @Inject
  private DataAnalysisService dataAnalysisService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionAnalysesFormPresenter() {
  }

  protected SubmissionAnalysesFormPresenter(MsAnalysisService msAnalysisService,
      DataAnalysisService dataAnalysisService, AuthorizationService authorizationService) {
    this.msAnalysisService = msAnalysisService;
    this.dataAnalysisService = dataAnalysisService;
    this.authorizationService = authorizationService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionAnalysesForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    final Locale locale = view.getLocale();
    design.dataAnalysesPanel.addStyleName(DATA_ANALYSES_PANEL);
    design.dataAnalysesPanel.setCaption(resources.message(DATA_ANALYSES_PANEL));
    design.dataAnalyses.addStyleName(DATA_ANALYSES);
    design.dataAnalyses.addColumn(da -> da.getSample().getName()).setId(NAME)
        .setCaption(resources.message(NAME));
    design.dataAnalyses.addColumn(da -> da.getProtein()).setId(PROTEIN)
        .setCaption(resources.message(PROTEIN))
        .setDescriptionGenerator(da -> resources.message(property(PROTEIN, DESCRIPTION)));
    design.dataAnalyses.addColumn(da -> da.getPeptide()).setId(PEPTIDE)
        .setCaption(resources.message(PEPTIDE))
        .setDescriptionGenerator(da -> resources.message(property(PEPTIDE, DESCRIPTION)));
    design.dataAnalyses.addColumn(da -> da.getType().getLabel(locale)).setId(DATA_ANALYSIS_TYPE)
        .setCaption(resources.message(DATA_ANALYSIS_TYPE));
    design.dataAnalyses.addColumn(da -> da.getScore()).setId(SCORE)
        .setCaption(resources.message(SCORE)).setDescriptionGenerator(da -> da.getScore())
        .setExpandRatio(3);
    design.dataAnalyses.addColumn(da -> workTime(da)).setId(WORK_TIME)
        .setCaption(resources.message(WORK_TIME))
        .setDescriptionGenerator(da -> workTimeDescription(da)).setExpandRatio(1);
    design.dataAnalyses.addColumn(da -> da.getStatus().getLabel(locale)).setId(STATUS)
        .setCaption(resources.message(STATUS)).setExpandRatio(1);
    if (authorizationService.hasAdminRole()) {
      design.dataAnalyses.setSelectionMode(SelectionMode.NONE);
      design.dataAnalyses.getEditor().setEnabled(true);
      design.dataAnalyses.getEditor().addSaveListener(e -> {
        dataAnalysisService.update(e.getBean(), null);
      });
      Binder<DataAnalysis> binder = new BeanValidationBinder<>(DataAnalysis.class);
      design.dataAnalyses.getEditor().setBinder(binder);
      ComboBox<DataAnalysisStatus> statusEditor = new ComboBox<>();
      statusEditor.addStyleName(STATUS);
      statusEditor.setEmptySelectionAllowed(false);
      statusEditor.setItems(DataAnalysisStatus.values());
      statusEditor.setItemCaptionGenerator(type -> type.getLabel(view.getLocale()));
      design.dataAnalyses.getColumn(STATUS).setEditorBinding(binder.forField(statusEditor)
          .asRequired(generalResources.message(REQUIRED)).bind(STATUS));
      TextArea scoreEditor = new TextArea();
      scoreEditor.addStyleName(SCORE);
      scoreEditor.setRows(3);
      design.dataAnalyses.getColumn(SCORE)
          .setEditorBinding(binder.forField(scoreEditor).withNullRepresentation("")
              .withValidator(
                  (value, context) -> validateScore(value, context, statusEditor.getValue()))
              .bind(SCORE));
      TextField workTimeEditor = new TextField();
      workTimeEditor.addStyleName(WORK_TIME);
      design.dataAnalyses.getColumn(WORK_TIME)
          .setEditorBinding(binder.forField(workTimeEditor).withNullRepresentation("")
              .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
              .withValidator(
                  (value, context) -> validateWorkTime(value, context, statusEditor.getValue()))
              .bind(WORK_TIME));
    }
  }

  private String workTime(DataAnalysis dataAnalysis) {
    final MessageResource resources = view.getResources();
    return resources.message(property(WORK_TIME, VALUE),
        Objects.toString(dataAnalysis.getWorkTime(), "0"), dataAnalysis.getMaxWorkTime());
  }

  private String workTimeDescription(DataAnalysis dataAnalysis) {
    final MessageResource resources = view.getResources();
    return resources.message(property(WORK_TIME, DESCRIPTION),
        Objects.toString(dataAnalysis.getWorkTime(), "0"), dataAnalysis.getMaxWorkTime());
  }

  private ValidationResult validateScore(String value, ValueContext context,
      DataAnalysisStatus status) {
    logger.debug("validateScore, value={}, status={}", value, status);
    if (value == null && status == DataAnalysisStatus.ANALYSED) {
      MessageResource generalResources = view.getGeneralResources();
      return ValidationResult.error(generalResources.message(REQUIRED));
    } else {
      return ValidationResult.ok();
    }
  }

  private ValidationResult validateWorkTime(Double value, ValueContext context,
      DataAnalysisStatus status) {
    logger.debug("validateWorkTime, value={}, status={}", value, status);
    if (value == null && status == DataAnalysisStatus.ANALYSED) {
      MessageResource generalResources = view.getGeneralResources();
      return ValidationResult.error(generalResources.message(REQUIRED));
    } else {
      return ValidationResult.ok();
    }
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private void createAnalysisPanel(MsAnalysis analysis) {
    final MessageResource resources = view.getResources();
    Panel panel = new Panel();
    design.analysesLayout.addComponent(panel);
    VerticalLayout layout = new VerticalLayout();
    layout.setSizeFull();
    panel.setContent(layout);
    panel.addStyleName(ANALYSIS);
    panel.setSizeFull();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    panel.setCaption(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))));
    Button view = new Button();
    view.addStyleName(VIEW);
    view.setCaption(resources.message(VIEW));
    view.addClickListener(e -> view(analysis));
    layout.addComponent(view);
    Grid<Acquisition> grid = new Grid<>();
    grid.addStyleName(ACQUISITIONS);
    grid.setSizeFull();
    grid.setItems(analysis.getAcquisitions());
    grid.addColumn(acquisition -> acquisition.getSample().getName()).setId(NAME)
        .setCaption(resources.message(NAME));
    grid.addColumn(acquisition -> acquisition.getAcquisitionFile()).setId(ACQUISITION_FILE)
        .setCaption(resources.message(ACQUISITION_FILE));
    layout.addComponent(grid);
  }

  private void view(MsAnalysis analysis) {
    view.navigateTo(MsAnalysisView.VIEW_NAME, String.valueOf(analysis.getId()));
  }

  Submission getValue() {
    return submission;
  }

  void setValue(Submission submission) {
    this.submission = submission;
    List<MsAnalysis> analyses = msAnalysisService.all(submission);
    design.analysesLayout.removeAllComponents();
    analyses.forEach(analysis -> {
      createAnalysisPanel(analysis);
    });
    List<DataAnalysis> dataAnalyses = dataAnalysisService.all(submission);
    design.dataAnalyses.setItems(dataAnalyses);
    design.dataAnalysesPanel.setVisible(!dataAnalyses.isEmpty());
  }
}
