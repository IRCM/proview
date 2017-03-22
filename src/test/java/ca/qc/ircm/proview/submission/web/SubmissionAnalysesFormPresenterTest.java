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
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
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
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
  @Captor
  private ArgumentCaptor<Panel> panelCaptor;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionAnalysesForm.class, locale);
  private Submission submission;
  private List<MsAnalysis> analyses = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionAnalysesFormPresenter(msAnalysisService);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    submission = entityManager.find(Submission.class, 1L);
    analyses.add(entityManager.find(MsAnalysis.class, 20L));
    analyses.add(entityManager.find(MsAnalysis.class, 21L));
    when(msAnalysisService.all(any(Submission.class))).thenReturn(analyses);
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  private List<Panel> viewPanels() {
    verify(view, atLeastOnce()).addComponent(panelCaptor.capture());
    return panelCaptor.getAllValues();
  }

  @SuppressWarnings("unchecked")
  private List<Grid<Acquisition>> viewGrids() {
    return viewPanels().stream().map(panel -> (VerticalLayout) panel.getContent())
        .map(layout -> (Grid<Acquisition>) layout.getComponent(0)).collect(Collectors.toList());
  }

  @Test
  public void components() {
    presenter.init(view);
    presenter.setBean(submission);

    verify(msAnalysisService).all(submission);
    verify(view).removeAllComponents();
    verify(view, times(2)).addComponent(any(Panel.class));
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setBean(submission);

    for (Panel analysisPanel : viewPanels()) {
      assertTrue(analysisPanel.getStyleName().contains(ANALYSIS));
    }
    for (Grid<Acquisition> acquisitionsGrid : viewGrids()) {
      assertTrue(acquisitionsGrid.getStyleName().contains(ACQUISITIONS));
    }
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setBean(submission);

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    List<Panel> panels = viewPanels();
    for (int i = 0; i < analyses.size(); i++) {
      Panel analysisPanel = panels.get(i);
      MsAnalysis analysis = analyses.get(i);
      assertEquals(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))),
          analysisPanel.getCaption());
    }
  }

  @Test
  public void grids() {
    presenter.init(view);
    presenter.setBean(submission);

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
}
