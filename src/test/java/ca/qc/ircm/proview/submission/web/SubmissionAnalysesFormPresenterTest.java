package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ACQUISITIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ACQUISITION_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.ANALYSIS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.LIMS;
import static ca.qc.ircm.proview.submission.web.SubmissionAnalysesFormPresenter.NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Panel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    view.analysisPanels = new ArrayList<>();
    view.acquisitionsGrids = new ArrayList<>();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    submission = entityManager.find(Submission.class, 1L);
    analyses.add(entityManager.find(MsAnalysis.class, 20L));
    analyses.add(entityManager.find(MsAnalysis.class, 21L));
    analyses.forEach(analysis -> {
      view.analysisPanels.add(new Panel());
      view.acquisitionsGrids.add(new Grid());
    });
    when(msAnalysisService.all(any(Submission.class))).thenReturn(analyses);
  }

  private LocalDate date(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
  }

  @Test
  public void components() {
    presenter.init(view);
    presenter.setItemDataSource(new BeanItem<>(submission));

    verify(msAnalysisService).all(submission);
    verify(view).removeAllComponents();
    verify(view, times(2)).createAnalysisPanel();
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setItemDataSource(new BeanItem<>(submission));

    for (Panel analysisPanel : view.analysisPanels) {
      assertTrue(analysisPanel.getStyleName().contains(ANALYSIS));
    }
    for (Grid acquisitionsGrid : view.acquisitionsGrids) {
      assertTrue(acquisitionsGrid.getStyleName().contains(ACQUISITIONS));
    }
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setItemDataSource(new BeanItem<>(submission));

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    for (int i = 0; i < analyses.size(); i++) {
      Panel analysisPanel = view.analysisPanels.get(i);
      MsAnalysis analysis = analyses.get(i);
      assertEquals(resources.message(ANALYSIS, formatter.format(date(analysis.getInsertTime()))),
          analysisPanel.getCaption());
    }
  }

  @Test
  public void grids() {
    presenter.init(view);
    presenter.setItemDataSource(new BeanItem<>(submission));

    for (Grid acquisitionsGrid : view.acquisitionsGrids) {
      List<Column> columns = acquisitionsGrid.getColumns();

      assertEquals(LIMS, columns.get(0).getPropertyId());
      assertEquals(resources.message(LIMS), columns.get(0).getHeaderCaption());
      assertEquals(NAME, columns.get(1).getPropertyId());
      assertEquals(resources.message(NAME), columns.get(1).getHeaderCaption());
      assertEquals(ACQUISITION_FILE, columns.get(2).getPropertyId());
      assertEquals(resources.message(ACQUISITION_FILE), columns.get(2).getHeaderCaption());
    }
  }
}
