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
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ACQUISITION_INDEX;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSES;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.DATA_ANALYSIS_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.MAX_WORK_TIME;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.PEPTIDE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.PROTEIN;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.SCORE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.WORK_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.dataanalysis.DataAnalysisService;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
  @Captor
  private ArgumentCaptor<Panel> panelCaptor;
  private SubmissionAnalysesFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionAnalysesForm.class, locale);
  private Submission submission;
  private List<MsAnalysis> analyses = new ArrayList<>();
  private List<DataAnalysis> dataAnalyses = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionAnalysesFormPresenter(msAnalysisService, dataAnalysisService);
    design = new SubmissionAnalysesFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    submission = entityManager.find(Submission.class, 1L);
    analyses.add(entityManager.find(MsAnalysis.class, 20L));
    analyses.add(entityManager.find(MsAnalysis.class, 21L));
    when(msAnalysisService.all(any(Submission.class))).thenReturn(analyses);
    dataAnalyses.add(entityManager.find(DataAnalysis.class, 3L));
    dataAnalyses.add(entityManager.find(DataAnalysis.class, 4L));
    when(dataAnalysisService.all(any(Submission.class))).thenReturn(dataAnalyses);
  }

  @SuppressWarnings("unchecked")
  private <T> ListDataProvider<T> dataProvider(Grid<T> grid) {
    return (ListDataProvider<T>) grid.getDataProvider();
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private List<Panel> viewPanels() {
    return IntStream.range(0, design.analysesLayout.getComponentCount())
        .mapToObj(i -> (Panel) design.analysesLayout.getComponent(i)).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private List<Grid<Acquisition>> viewGrids() {
    return viewPanels().stream().map(panel -> (VerticalLayout) panel.getContent())
        .map(layout -> (Grid<Acquisition>) layout.getComponent(0)).collect(Collectors.toList());
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
      assertEquals(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))),
          analysisPanel.getCaption());
    }
    assertEquals(resources.message(DATA_ANALYSES_PANEL), design.dataAnalysesPanel.getCaption());
  }

  @Test
  public void msAnalysisGrids() {
    presenter.init(view);
    presenter.setValue(submission);

    for (Grid<Acquisition> acquisitionsGrid : viewGrids()) {
      List<Column<Acquisition, ?>> columns = acquisitionsGrid.getColumns();

      assertEquals(NAME, columns.get(0).getId());
      assertEquals(resources.message(NAME), columns.get(0).getCaption());
      assertEquals(ACQUISITION_FILE, columns.get(1).getId());
      assertEquals(resources.message(ACQUISITION_FILE), columns.get(1).getCaption());
      assertEquals(ACQUISITION_INDEX, columns.get(2).getId());
      assertEquals(true, columns.get(2).isHidden());
    }
  }

  @Test
  public void dataAnalysesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    final DataAnalysis dataAnalysis1 = dataAnalyses.get(0);
    final DataAnalysis dataAnalysis2 = dataAnalyses.get(1);
    assertEquals(8, design.dataAnalyses.getColumns().size());
    assertEquals(NAME, design.dataAnalyses.getColumns().get(0).getId());
    assertEquals(resources.message(NAME), design.dataAnalyses.getColumn(NAME).getCaption());
    assertEquals(dataAnalysis1.getSample().getName(),
        design.dataAnalyses.getColumn(NAME).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getSample().getName(),
        design.dataAnalyses.getColumn(NAME).getValueProvider().apply(dataAnalysis2));
    assertEquals(PROTEIN, design.dataAnalyses.getColumns().get(1).getId());
    assertEquals(resources.message(PROTEIN), design.dataAnalyses.getColumn(PROTEIN).getCaption());
    assertEquals(dataAnalysis1.getProtein(),
        design.dataAnalyses.getColumn(PROTEIN).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getProtein(),
        design.dataAnalyses.getColumn(PROTEIN).getValueProvider().apply(dataAnalysis2));
    assertEquals(PEPTIDE, design.dataAnalyses.getColumns().get(2).getId());
    assertEquals(resources.message(PEPTIDE), design.dataAnalyses.getColumn(PEPTIDE).getCaption());
    assertEquals(dataAnalysis1.getPeptide(),
        design.dataAnalyses.getColumn(PEPTIDE).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getPeptide(),
        design.dataAnalyses.getColumn(PEPTIDE).getValueProvider().apply(dataAnalysis2));
    assertEquals(DATA_ANALYSIS_TYPE, design.dataAnalyses.getColumns().get(3).getId());
    assertEquals(resources.message(DATA_ANALYSIS_TYPE),
        design.dataAnalyses.getColumn(DATA_ANALYSIS_TYPE).getCaption());
    assertEquals(dataAnalysis1.getType().getLabel(locale),
        design.dataAnalyses.getColumn(DATA_ANALYSIS_TYPE).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getType().getLabel(locale),
        design.dataAnalyses.getColumn(DATA_ANALYSIS_TYPE).getValueProvider().apply(dataAnalysis2));
    assertEquals(MAX_WORK_TIME, design.dataAnalyses.getColumns().get(4).getId());
    assertEquals(resources.message(MAX_WORK_TIME),
        design.dataAnalyses.getColumn(MAX_WORK_TIME).getCaption());
    assertEquals(dataAnalysis1.getMaxWorkTime(),
        design.dataAnalyses.getColumn(MAX_WORK_TIME).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getMaxWorkTime(),
        design.dataAnalyses.getColumn(MAX_WORK_TIME).getValueProvider().apply(dataAnalysis2));
    assertEquals(SCORE, design.dataAnalyses.getColumns().get(5).getId());
    assertEquals(resources.message(SCORE), design.dataAnalyses.getColumn(SCORE).getCaption());
    assertEquals(dataAnalysis1.getScore(),
        design.dataAnalyses.getColumn(SCORE).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getScore(),
        design.dataAnalyses.getColumn(SCORE).getValueProvider().apply(dataAnalysis2));
    assertEquals(WORK_TIME, design.dataAnalyses.getColumns().get(6).getId());
    assertEquals(resources.message(WORK_TIME),
        design.dataAnalyses.getColumn(WORK_TIME).getCaption());
    assertEquals(dataAnalysis1.getWorkTime(),
        design.dataAnalyses.getColumn(WORK_TIME).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getWorkTime(),
        design.dataAnalyses.getColumn(WORK_TIME).getValueProvider().apply(dataAnalysis2));
    assertEquals(STATUS, design.dataAnalyses.getColumns().get(7).getId());
    assertEquals(resources.message(STATUS), design.dataAnalyses.getColumn(STATUS).getCaption());
    assertEquals(dataAnalysis1.getStatus().getLabel(locale),
        design.dataAnalyses.getColumn(STATUS).getValueProvider().apply(dataAnalysis1));
    assertEquals(dataAnalysis2.getStatus().getLabel(locale),
        design.dataAnalyses.getColumn(STATUS).getValueProvider().apply(dataAnalysis2));

    assertTrue(design.dataAnalysesPanel.isVisible());
    Collection<DataAnalysis> dataAnalyses = dataProvider(design.dataAnalyses).getItems();
    assertEquals(2, dataAnalyses.size());
    assertTrue(dataAnalyses.contains(dataAnalysis1));
    assertTrue(dataAnalyses.contains(dataAnalysis2));
  }

  @Test
  public void dataAnalysesGrid_Empty() {
    when(dataAnalysisService.all(any(Submission.class))).thenReturn(Collections.emptyList());

    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.dataAnalysesPanel.isVisible());
  }
}
