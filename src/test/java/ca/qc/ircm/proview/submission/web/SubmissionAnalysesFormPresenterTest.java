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

import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ACQUISITION_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSES;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSIS_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.PEPTIDE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.PROTEIN;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.SCORE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.VIEW;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.WORK_TIME;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.gridStartEdit;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisStatus;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionAnalysesFormPresenterTest {
  private SubmissionAnalysesFormPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private SubmissionAnalysesForm view;
  @Mock
  private MsAnalysisService msAnalysisService;
  @Mock
  private DataAnalysisService dataAnalysisService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private UI ui;
  @Mock
  private ConnectorTracker connectorTracker;
  @Mock
  private VaadinSession vaadinSession;
  @Captor
  private ArgumentCaptor<Panel> panelCaptor;
  private SubmissionAnalysesFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionAnalysesForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private Submission submission;
  private List<MsAnalysis> analyses = new ArrayList<>();
  private List<DataAnalysis> dataAnalyses = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionAnalysesFormPresenter(msAnalysisService, dataAnalysisService,
        authorizationService);
    design = new SubmissionAnalysesFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    submission = entityManager.find(Submission.class, 1L);
    analyses.add(entityManager.find(MsAnalysis.class, 20L));
    analyses.add(entityManager.find(MsAnalysis.class, 21L));
    when(msAnalysisService.all(any(Submission.class))).thenReturn(analyses);
    dataAnalyses.add(entityManager.find(DataAnalysis.class, 3L));
    dataAnalyses.add(entityManager.find(DataAnalysis.class, 4L));
    when(dataAnalysisService.all(any(Submission.class))).thenReturn(dataAnalyses);
    when(view.getUI()).thenReturn(ui);
    when(view.getParent()).thenReturn(ui);
    when(ui.getConnectorTracker()).thenReturn(connectorTracker);
    design.setParent(view);
    when(ui.getSession()).thenReturn(vaadinSession);
    when(vaadinSession.hasLock()).thenReturn(true);
  }

  private List<Panel> viewPanels() {
    return IntStream.range(0, design.analysesLayout.getComponentCount())
        .mapToObj(i -> (Panel) design.analysesLayout.getComponent(i)).collect(Collectors.toList());
  }

  private List<Button> viewAnalyses() {
    return viewPanels().stream().map(panel -> (VerticalLayout) panel.getContent())
        .map(layout -> (Button) layout.getComponent(0)).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Grid<Acquisition>> viewGrids() {
    return viewPanels().stream().map(panel -> (VerticalLayout) panel.getContent())
        .map(layout -> (Grid<Acquisition>) layout.getComponent(1)).collect(Collectors.toList());
  }

  @Test
  public void components() {
    presenter.init(view);
    presenter.setValue(submission);

    verify(msAnalysisService).all(submission);
    assertEquals(2, design.analysesLayout.getComponentCount());
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setValue(submission);

    for (Panel analysisPanel : viewPanels()) {
      assertTrue(analysisPanel.getStyleName().contains(ANALYSIS));
    }
    for (Grid<Acquisition> acquisitionsGrid : viewGrids()) {
      assertTrue(acquisitionsGrid.getStyleName().contains(ACQUISITIONS));
    }
    assertTrue(design.dataAnalysesPanel.getStyleName().contains(DATA_ANALYSES_PANEL));
    assertTrue(design.dataAnalyses.getStyleName().contains(DATA_ANALYSES));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setValue(submission);

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    List<Panel> panels = viewPanels();
    for (int i = 0; i < analyses.size(); i++) {
      Panel analysisPanel = panels.get(i);
      MsAnalysis analysis = analyses.get(i);
      assertEquals(
          resources.message(ANALYSIS, formatter.format(toLocalDate(analysis.getInsertTime()))),
          analysisPanel.getCaption());
    }
    assertEquals(resources.message(DATA_ANALYSES_PANEL), design.dataAnalysesPanel.getCaption());
  }

  @Test
  public void msAnalysisPanels() {
    presenter.init(view);
    presenter.setValue(submission);

    for (int i = 0; i < analyses.size(); i++) {
      final MsAnalysis analysis = analyses.get(i);
      Button button = viewAnalyses().get(i);
      assertTrue(button.getStyleName().contains(VIEW));
      assertEquals(resources.message(VIEW), button.getCaption());
      button.click();
      verify(view).navigateTo(MsAnalysisView.VIEW_NAME, analysis.getId().toString());
    }
    for (Grid<Acquisition> acquisitionsGrid : viewGrids()) {
      List<Column<Acquisition, ?>> columns = acquisitionsGrid.getColumns();

      assertEquals(NAME, columns.get(0).getId());
      assertEquals(resources.message(NAME), columns.get(0).getCaption());
      assertEquals(ACQUISITION_FILE, columns.get(1).getId());
      assertEquals(resources.message(ACQUISITION_FILE), columns.get(1).getCaption());
    }
  }

  @Test
  public void dataAnalysesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    final List<DataAnalysis> analyses =
        new ArrayList<>(dataProvider(design.dataAnalyses).getItems());
    assertEquals(7, design.dataAnalyses.getColumns().size());
    assertEquals(NAME, design.dataAnalyses.getColumns().get(0).getId());
    assertEquals(resources.message(NAME), design.dataAnalyses.getColumn(NAME).getCaption());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getSample().getName(),
          design.dataAnalyses.getColumn(NAME).getValueProvider().apply(analysis));
    }
    assertEquals(PROTEIN, design.dataAnalyses.getColumns().get(1).getId());
    assertEquals(resources.message(PROTEIN), design.dataAnalyses.getColumn(PROTEIN).getCaption());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getProtein(),
          design.dataAnalyses.getColumn(PROTEIN).getValueProvider().apply(analysis));
      assertEquals(resources.message(PROTEIN + "." + DESCRIPTION),
          design.dataAnalyses.getColumn(PROTEIN).getDescriptionGenerator().apply(analysis));
    }
    assertEquals(PEPTIDE, design.dataAnalyses.getColumns().get(2).getId());
    assertEquals(resources.message(PEPTIDE), design.dataAnalyses.getColumn(PEPTIDE).getCaption());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getPeptide(),
          design.dataAnalyses.getColumn(PEPTIDE).getValueProvider().apply(analysis));
      assertEquals(resources.message(PEPTIDE + "." + DESCRIPTION),
          design.dataAnalyses.getColumn(PEPTIDE).getDescriptionGenerator().apply(analysis));
    }
    assertEquals(DATA_ANALYSIS_TYPE, design.dataAnalyses.getColumns().get(3).getId());
    assertEquals(resources.message(DATA_ANALYSIS_TYPE),
        design.dataAnalyses.getColumn(DATA_ANALYSIS_TYPE).getCaption());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getType().getLabel(locale),
          design.dataAnalyses.getColumn(DATA_ANALYSIS_TYPE).getValueProvider().apply(analysis));
    }
    assertEquals(SCORE, design.dataAnalyses.getColumns().get(4).getId());
    assertEquals(resources.message(SCORE), design.dataAnalyses.getColumn(SCORE).getCaption());
    assertNull(design.dataAnalyses.getColumn(SCORE).getEditorBinding());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getScore(),
          design.dataAnalyses.getColumn(SCORE).getValueProvider().apply(analysis));
      assertEquals(analysis.getScore(),
          design.dataAnalyses.getColumn(SCORE).getDescriptionGenerator().apply(analysis));
    }
    assertEquals(WORK_TIME, design.dataAnalyses.getColumns().get(5).getId());
    assertEquals(resources.message(WORK_TIME),
        design.dataAnalyses.getColumn(WORK_TIME).getCaption());
    assertNull(design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding());
    for (DataAnalysis analysis : analyses) {
      assertEquals(
          resources.message(WORK_TIME + "." + VALUE, Objects.toString(analysis.getWorkTime(), "0"),
              analysis.getMaxWorkTime()),
          design.dataAnalyses.getColumn(WORK_TIME).getValueProvider().apply(analysis));
      assertEquals(
          resources.message(WORK_TIME + "." + DESCRIPTION,
              Objects.toString(analysis.getWorkTime(), "0"), analysis.getMaxWorkTime()),
          design.dataAnalyses.getColumn(WORK_TIME).getDescriptionGenerator().apply(analysis));
    }
    assertEquals(STATUS, design.dataAnalyses.getColumns().get(6).getId());
    assertEquals(resources.message(STATUS), design.dataAnalyses.getColumn(STATUS).getCaption());
    assertNull(design.dataAnalyses.getColumn(STATUS).getEditorBinding());
    for (DataAnalysis analysis : analyses) {
      assertEquals(analysis.getStatus().getLabel(locale),
          design.dataAnalyses.getColumn(STATUS).getValueProvider().apply(analysis));
    }
    assertTrue(design.dataAnalysesPanel.isVisible());
    assertEquals(dataAnalyses.size(), analyses.size());
    for (DataAnalysis analysis : dataAnalyses) {
      assertTrue(analyses.contains(analysis));
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);

    assertNotNull(design.dataAnalyses.getColumn(SCORE).getEditorBinding());
    assertTrue(
        design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField() instanceof TextArea);
    TextArea scoreEditor =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    assertTrue(scoreEditor.getStyleName().contains(SCORE));
    assertEquals(3, scoreEditor.getRows());
    assertNotNull(design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding());
    assertTrue(design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding()
        .getField() instanceof TextField);
    TextField workTimeEditor =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    assertTrue(workTimeEditor.getStyleName().contains(WORK_TIME));
    assertNotNull(design.dataAnalyses.getColumn(STATUS).getEditorBinding());
    assertTrue(
        design.dataAnalyses.getColumn(STATUS).getEditorBinding().getField() instanceof ComboBox);
    ComboBox<DataAnalysisStatus> statusEditor = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    assertTrue(statusEditor.getStyleName().contains(STATUS));
    assertFalse(statusEditor.isEmptySelectionAllowed());
    List<DataAnalysisStatus> statusEditorValues = items(statusEditor);
    assertEquals(DataAnalysisStatus.values().length, statusEditorValues.size());
    for (DataAnalysisStatus value : DataAnalysisStatus.values()) {
      assertTrue(statusEditorValues.contains(value));
    }
  }

  @Test
  public void dataAnalysesGrid_WorkTimeInvalid() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);

    TextField field =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    field.setValue("a");
    BindingValidationStatus<?> validation =
        design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(INVALID_NUMBER), validation.getMessage().get());
  }

  @Test
  public void dataAnalysesGrid_WorkTimeBelowMinimum() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);

    TextField field =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    field.setValue("-1,0");
    BindingValidationStatus<?> validation =
        design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertNotNull(validation.getMessage().get());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_SaveAnalysedNoScore() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("1,25");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.ANALYSED);
    design.dataAnalyses.getEditor().save();

    verify(dataAnalysisService, never()).update(any(), any());
    BindingValidationStatus<?> validation =
        design.dataAnalyses.getColumn(SCORE).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(REQUIRED), validation.getMessage().get());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_SaveCancelledNoScore() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("1,25");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.CANCELLED);
    design.dataAnalyses.getEditor().save();

    verify(dataAnalysisService).update(any(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_SaveAnalysedNoWorkTime() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("Test");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.ANALYSED);
    design.dataAnalyses.getEditor().save();

    verify(dataAnalysisService, never()).update(any(), any());
    BindingValidationStatus<?> validation =
        design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().validate();
    assertTrue(validation.isError());
    assertEquals(generalResources.message(REQUIRED), validation.getMessage().get());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_SaveCancelledNoWorkTime() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("Test");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.CANCELLED);
    design.dataAnalyses.getEditor().save();

    verify(dataAnalysisService).update(any(), any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_Save() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("Test");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("1,25");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.ANALYSED);
    design.dataAnalyses.getEditor().save();

    verify(dataAnalysisService).update(dataAnalysis, null);
    assertEquals("Test", dataAnalysis.getScore());
    assertEquals(1.25, dataAnalysis.getWorkTime(), 0.000001);
    assertEquals(DataAnalysisStatus.ANALYSED, dataAnalysis.getStatus());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void dataAnalysesGrid_Cancel() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    DataAnalysis dataAnalysis = dataAnalyses.get(0);
    gridStartEdit(design.dataAnalyses, dataAnalysis);
    TextArea scoreField =
        (TextArea) design.dataAnalyses.getColumn(SCORE).getEditorBinding().getField();
    scoreField.setValue("Test");
    TextField workTimeField =
        (TextField) design.dataAnalyses.getColumn(WORK_TIME).getEditorBinding().getField();
    workTimeField.setValue("1,25");
    ComboBox<DataAnalysisStatus> statusField = (ComboBox<DataAnalysisStatus>) design.dataAnalyses
        .getColumn(STATUS).getEditorBinding().getField();
    statusField.setValue(DataAnalysisStatus.ANALYSED);
    design.dataAnalyses.getEditor().cancel();

    verify(dataAnalysisService, never()).update(any(), any());
  }

  @Test
  public void dataAnalysesGrid_Empty() {
    when(dataAnalysisService.all(any(Submission.class))).thenReturn(Collections.emptyList());

    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.dataAnalysesPanel.isVisible());
  }
}
