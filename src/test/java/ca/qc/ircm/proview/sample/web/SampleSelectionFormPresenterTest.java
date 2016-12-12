package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CLEAR;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS_COLUMNS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_COLUMNS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SELECT;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Panel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleSelectionFormPresenterTest {
  private SampleSelectionFormPresenter presenter;
  @Mock
  private SampleSelectionForm view;
  @Mock
  private ControlService controlService;
  @PersistenceContext
  private EntityManager entityManager;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleSelectionForm.class, locale);

  @Before
  public void beforeTest() {
    presenter = new SampleSelectionFormPresenter(controlService);
    view.samplesPanel = new Panel();
    view.samplesGrid = new Grid();
    view.controlsPanel = new Panel();
    view.controlsGrid = new Grid();
    view.clearButton = new Button();
    view.selectButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(view.samplesGrid.getStyleName().contains(SAMPLES));
    assertTrue(view.controlsPanel.getStyleName().contains(CONTROLS_PANEL));
    assertTrue(view.controlsGrid.getStyleName().contains(CONTROLS));
    assertTrue(view.selectButton.getStyleName().contains(SELECT));
    assertTrue(view.clearButton.getStyleName().contains(CLEAR));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    for (Object propertyId : SAMPLES_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.samplesGrid.getColumn(propertyId).getHeaderCaption());
    }
    assertEquals(resources.message(CONTROLS_PANEL), view.controlsPanel.getCaption());
    for (Object propertyId : CONTROLS_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.controlsGrid.getColumn(propertyId).getHeaderCaption());
    }
    assertEquals(resources.message(SELECT), view.selectButton.getCaption());
    assertEquals(resources.message(CLEAR), view.clearButton.getCaption());
  }

  @Test
  public void samplesGridColumns() {
    presenter.init(view);

    List<Column> columns = view.samplesGrid.getColumns();

    assertTrue(view.samplesGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertEquals(NAME, columns.get(0).getPropertyId());
    assertEquals(EXPERIENCE, columns.get(1).getPropertyId());
    assertEquals(STATUS, columns.get(2).getPropertyId());
    assertEquals(1, view.samplesGrid.getFrozenColumnCount());
  }

  @Test
  public void controlsGridColumns() {
    presenter.init(view);

    List<Column> columns = view.controlsGrid.getColumns();

    assertTrue(view.controlsGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertEquals(NAME, columns.get(0).getPropertyId());
    assertEquals(1, view.controlsGrid.getFrozenColumnCount());
  }
}
