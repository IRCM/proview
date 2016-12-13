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
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS_COLUMNS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROLS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.CONTROL_TYPE;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.ORIGINAL_CONTAINER_NAME;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_COLUMNS;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.SELECT;
import static ca.qc.ircm.proview.sample.web.SampleSelectionFormPresenter.STATUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionModel;
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
  private ControlService controlService;
  @Mock
  private ValueChangeListener valueChangeListener;
  @Captor
  private ArgumentCaptor<ValueChangeEvent> valueChangeEventCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SampleSelectionForm.class, locale);
  private List<SubmissionSample> selectedSamples = new ArrayList<>();
  private List<SubmissionSample> allSamples = new ArrayList<>();
  private List<Control> controls = new ArrayList<>();

  /**
   * Before test.
   */
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
    selectedSamples.add(entityManager.find(SubmissionSample.class, 442L));
    selectedSamples.add(entityManager.find(SubmissionSample.class, 627L));
    allSamples =
        selectedSamples.stream().flatMap(sample -> sample.getSubmission().getSamples().stream())
            .collect(Collectors.toList());
    controls.add(entityManager.find(Control.class, 444L));
    controls.add(entityManager.find(Control.class, 448L));
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
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);

    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    for (Object propertyId : SAMPLES_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.samplesGrid.getColumn(propertyId).getHeaderCaption());
    }
    Container.Indexed container = view.samplesGrid.getContainerDataSource();
    SubmissionSample sample = selectedSamples.get(0);
    Object statusValue = container.getItem(sample).getItemProperty(STATUS).getValue();
    assertEquals(sample.getStatus().getLabel(locale), statusValue);
    assertEquals(resources.message(CONTROLS_PANEL), view.controlsPanel.getCaption());
    for (Object propertyId : CONTROLS_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.controlsGrid.getColumn(propertyId).getHeaderCaption());
    }
    container = view.controlsGrid.getContainerDataSource();
    Control control = controls.get(0);
    Object controlTypeValue = container.getItem(control).getItemProperty(CONTROL_TYPE).getValue();
    assertEquals(control.getControlType().getLabel(locale), controlTypeValue);
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
    assertEquals(CONTROL_TYPE, columns.get(1).getPropertyId());
    assertEquals(ORIGINAL_CONTAINER_NAME, columns.get(2).getPropertyId());
    assertEquals(1, view.controlsGrid.getFrozenColumnCount());
  }

  @Test
  public void defaultSamples() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    presenter.init(view);

    Container.Indexed container = view.samplesGrid.getContainerDataSource();

    assertEquals(allSamples.size(), container.size());
    assertTrue(allSamples.containsAll(container.getItemIds()));
    assertTrue(container.getItemIds().containsAll(allSamples));
    Collection<Object> selection = view.samplesGrid.getSelectedRows();
    assertEquals(selectedSamples.size(), selection.size());
    assertTrue(selectedSamples.containsAll(selection));
    assertTrue(selection.containsAll(selectedSamples));
  }

  @Test
  public void defaultControls() {
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);

    Container.Indexed container = view.controlsGrid.getContainerDataSource();

    assertEquals(controls.size(), container.size());
    assertTrue(controls.containsAll(container.getItemIds()));
    assertTrue(container.getItemIds().containsAll(controls));
    Collection<Object> selection = view.controlsGrid.getSelectedRows();
    assertEquals(0, selection.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void select_Samples() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    presenter.selectedSamplesProperty().addValueChangeListener(valueChangeListener);
    allSamples.forEach(sample -> view.samplesGrid.select(sample));

    view.selectButton.click();

    verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
    ValueChangeEvent valueChangeEvent = valueChangeEventCaptor.getValue();
    assertTrue(valueChangeEvent.getProperty().getValue() instanceof List);
    List<Sample> samples = (List<Sample>) valueChangeEvent.getProperty().getValue();
    assertEquals(this.allSamples.size(), samples.size());
    assertTrue(this.allSamples.containsAll(samples));
    assertTrue(samples.containsAll(this.allSamples));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void select_Controls() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    presenter.selectedSamplesProperty().addValueChangeListener(valueChangeListener);
    view.samplesGrid.deselectAll();
    controls.forEach(sample -> view.controlsGrid.select(sample));

    view.selectButton.click();

    verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
    ValueChangeEvent valueChangeEvent = valueChangeEventCaptor.getValue();
    assertTrue(valueChangeEvent.getProperty().getValue() instanceof List);
    List<Sample> samples = (List<Sample>) valueChangeEvent.getProperty().getValue();
    assertEquals(this.controls.size(), samples.size());
    assertTrue(this.controls.containsAll(samples));
    assertTrue(samples.containsAll(this.controls));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void select_SamplesAndControls() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    presenter.selectedSamplesProperty().addValueChangeListener(valueChangeListener);
    view.controlsGrid.select(controls.get(0));

    view.selectButton.click();

    verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
    ValueChangeEvent valueChangeEvent = valueChangeEventCaptor.getValue();
    assertTrue(valueChangeEvent.getProperty().getValue() instanceof List);
    List<Sample> samples = (List<Sample>) valueChangeEvent.getProperty().getValue();
    assertEquals(selectedSamples.size() + 1, samples.size());
    assertTrue(samples.containsAll(this.selectedSamples));
    assertTrue(samples.contains(this.controls.get(0)));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void select_None() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    presenter.selectedSamplesProperty().addValueChangeListener(valueChangeListener);
    view.samplesGrid.deselectAll();

    view.selectButton.click();

    verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
    ValueChangeEvent valueChangeEvent = valueChangeEventCaptor.getValue();
    assertTrue(valueChangeEvent.getProperty().getValue() instanceof List);
    List<Sample> samples = (List<Sample>) valueChangeEvent.getProperty().getValue();
    assertEquals(0, samples.size());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void clear() {
    presenter.setSelectedSamples(new ArrayList<>(selectedSamples));
    when(controlService.all()).thenReturn(controls);
    presenter.init(view);
    presenter.selectedSamplesProperty().addValueChangeListener(valueChangeListener);
    view.controlsGrid.select(controls.get(0));

    view.clearButton.click();

    verify(valueChangeListener).valueChange(valueChangeEventCaptor.capture());
    ValueChangeEvent valueChangeEvent = valueChangeEventCaptor.getValue();
    assertTrue(valueChangeEvent.getProperty().getValue() instanceof List);
    List<Sample> samples = (List<Sample>) valueChangeEvent.getProperty().getValue();
    assertEquals(0, samples.size());
  }
}
