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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CLEAR;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.ORIGINAL_CONTAINER_NAME;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_LAST_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SELECT;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Panel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleSelectionFormPresenterTest {
  private SampleSelectionFormPresenter presenter;
  @Mock
  private SampleSelectionForm view;
  @Mock
  private SampleContainerService sampleContainerService;
  @Mock
  private ControlService controlService;
  @Mock
  private SaveListener<List<Sample>> saveListener;
  @Mock
  private Registration registration;
  @Captor
  private ArgumentCaptor<List<Sample>> samplesCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleSelectionForm.class, locale);
  private List<SubmissionSample> selectedSamples;
  private List<SubmissionSample> allSamples;
  private List<Control> controls;
  private List<SampleContainer> lastContainers;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SampleSelectionFormPresenter(sampleContainerService, controlService);
    view.samplesPanel = new Panel();
    view.samplesGrid = new Grid<>();
    view.controlsPanel = new Panel();
    view.controlsGrid = new Grid<>();
    view.clearButton = new Button();
    view.selectButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    selectedSamples = new ArrayList<>();
    selectedSamples.add(entityManager.find(SubmissionSample.class, 442L));
    selectedSamples.add(entityManager.find(SubmissionSample.class, 627L));
    Well well = new Well(1, 2);
    well.setPlate(new Plate(10L, "test_plate"));
    lastContainers = new ArrayList<>();
    lastContainers.add(well);
    lastContainers.add(new Tube(10L, "test_tube"));
    when(sampleContainerService.last(selectedSamples.get(0))).thenReturn(lastContainers.get(0));
    when(sampleContainerService.last(selectedSamples.get(1))).thenReturn(lastContainers.get(1));
    allSamples =
        selectedSamples.stream().flatMap(sample -> sample.getSubmission().getSamples().stream())
            .collect(Collectors.toList());
    controls = new ArrayList<>();
    controls.add(entityManager.find(Control.class, 444L));
    controls.add(entityManager.find(Control.class, 448L));
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> gridDataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
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
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);

    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    assertEquals(resources.message(CONTROLS_PANEL), view.controlsPanel.getCaption());
    Control control = controls.get(0);
    assertEquals(resources.message(NAME), view.controlsGrid.getColumn(NAME).getCaption());
    assertEquals(control.getName(),
        view.controlsGrid.getColumn(NAME).getValueProvider().apply(control));
    assertEquals(resources.message(CONTROL_TYPE),
        view.controlsGrid.getColumn(CONTROL_TYPE).getCaption());
    assertEquals(control.getControlType().getLabel(locale),
        view.controlsGrid.getColumn(CONTROL_TYPE).getValueProvider().apply(control));
    assertEquals(resources.message(ORIGINAL_CONTAINER_NAME),
        view.controlsGrid.getColumn(ORIGINAL_CONTAINER_NAME).getCaption());
    assertEquals(control.getOriginalContainer().getName(),
        view.controlsGrid.getColumn(ORIGINAL_CONTAINER_NAME).getValueProvider().apply(control));
    assertEquals(resources.message(SELECT), view.selectButton.getCaption());
    assertEquals(resources.message(CLEAR), view.clearButton.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);

    List<Column<SubmissionSample, ?>> columns = view.samplesGrid.getColumns();

    assertTrue(view.samplesGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertEquals(NAME, columns.get(0).getId());
    assertEquals(EXPERIENCE, columns.get(1).getId());
    assertEquals(STATUS, columns.get(2).getId());
    assertEquals(1, view.samplesGrid.getFrozenColumnCount());
    assertEquals(resources.message(NAME), view.samplesGrid.getColumn(NAME).getCaption());
    assertEquals(resources.message(EXPERIENCE),
        view.samplesGrid.getColumn(EXPERIENCE).getCaption());
    assertEquals(resources.message(STATUS), view.samplesGrid.getColumn(STATUS).getCaption());
    assertEquals(resources.message(SAMPLES_LAST_CONTAINER),
        view.samplesGrid.getColumn(SAMPLES_LAST_CONTAINER).getCaption());
    SubmissionSample sample = selectedSamples.get(0);
    assertEquals(sample.getName(),
        view.samplesGrid.getColumn(NAME).getValueProvider().apply(sample));
    assertEquals(sample.getSubmission().getExperience(),
        view.samplesGrid.getColumn(EXPERIENCE).getValueProvider().apply(sample));
    assertEquals(sample.getStatus().getLabel(locale),
        view.samplesGrid.getColumn(STATUS).getValueProvider().apply(sample));
    assertEquals(lastContainers.get(0).getFullName(),
        view.samplesGrid.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample));
    sample = selectedSamples.get(1);
    assertEquals(sample.getName(),
        view.samplesGrid.getColumn(NAME).getValueProvider().apply(sample));
    assertEquals(sample.getSubmission().getExperience(),
        view.samplesGrid.getColumn(EXPERIENCE).getValueProvider().apply(sample));
    assertEquals(sample.getStatus().getLabel(locale),
        view.samplesGrid.getColumn(STATUS).getValueProvider().apply(sample));
    assertEquals(lastContainers.get(1).getFullName(),
        view.samplesGrid.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample));
  }

  @Test
  public void controlsGridColumns() {
    presenter.init(view);

    List<Column<Control, ?>> columns = view.controlsGrid.getColumns();

    assertTrue(view.controlsGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertEquals(NAME, columns.get(0).getId());
    assertEquals(CONTROL_TYPE, columns.get(1).getId());
    assertEquals(ORIGINAL_CONTAINER_NAME, columns.get(2).getId());
    assertEquals(1, view.controlsGrid.getFrozenColumnCount());
  }

  @Test
  public void defaultSamples() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    presenter.init(view);

    ListDataProvider<SubmissionSample> dataProvider = gridDataProvider(view.samplesGrid);

    assertEquals(allSamples.size(), dataProvider.getItems().size());
    assertTrue(allSamples.containsAll(dataProvider.getItems()));
    assertTrue(dataProvider.getItems().containsAll(allSamples));
    Collection<SubmissionSample> selection = view.samplesGrid.getSelectedItems();
    assertEquals(selectedSamples.size(), selection.size());
    assertTrue(selectedSamples.containsAll(selection));
    assertTrue(selection.containsAll(selectedSamples));
  }

  @Test
  public void defaultControls() {
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);

    ListDataProvider<Control> dataProvider = gridDataProvider(view.controlsGrid);

    assertEquals(controls.size(), dataProvider.getItems().size());
    assertTrue(controls.containsAll(dataProvider.getItems()));
    assertTrue(dataProvider.getItems().containsAll(controls));
    Collection<Control> selection = view.controlsGrid.getSelectedItems();
    assertEquals(0, selection.size());
  }

  @Test
  public void select_Samples() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    allSamples.forEach(sample -> view.samplesGrid.select(sample));

    view.selectButton.click();

    verify(view).fireSaveEvent(samplesCaptor.capture());
    assertEquals(allSamples, samplesCaptor.getValue());
  }

  @Test
  public void select_Controls() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    view.samplesGrid.deselectAll();
    controls.forEach(sample -> view.controlsGrid.select(sample));

    view.selectButton.click();

    verify(view).fireSaveEvent(samplesCaptor.capture());
    assertEquals(controls, samplesCaptor.getValue());
  }

  @Test
  public void select_SamplesAndControls() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    view.controlsGrid.select(controls.get(0));

    view.selectButton.click();

    verify(view).fireSaveEvent(samplesCaptor.capture());
    List<Sample> samples = samplesCaptor.getValue();
    assertEquals(selectedSamples.size() + 1, samples.size());
    assertTrue(samples.containsAll(this.selectedSamples));
    assertTrue(samples.contains(this.controls.get(0)));
  }

  @Test
  public void select_None() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    view.samplesGrid.deselectAll();

    view.selectButton.click();

    verify(view).fireSaveEvent(samplesCaptor.capture());
    assertTrue(samplesCaptor.getValue().isEmpty());
  }

  @Test
  public void clear() {
    presenter.setItems(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    view.controlsGrid.select(controls.get(0));

    view.clearButton.click();

    verify(view).fireSaveEvent(samplesCaptor.capture());
    assertTrue(samplesCaptor.getValue().isEmpty());
  }
}
