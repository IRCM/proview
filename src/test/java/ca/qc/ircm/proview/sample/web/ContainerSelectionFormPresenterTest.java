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

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.CLEAR;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.CONTAINER_TUBE;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.PLATES;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.PLATES_PANEL;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.PLATE_PANEL;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.PLATE_SAMPLE_NOT_SELECTED;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.SAMPLE;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.SELECT;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.TUBES;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.TUBES_PANEL;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.TYPE;
import static ca.qc.ircm.proview.sample.web.ContainerSelectionFormPresenter.TYPE_PANEL;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellLocation;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class ContainerSelectionFormPresenterTest {
  private ContainerSelectionFormPresenter presenter;
  @Mock
  private ContainerSelectionForm view;
  @Mock
  private TubeService tubeService;
  @Mock
  private WellService wellService;
  @Mock
  private PlateService plateService;
  @Mock
  private SampleService sampleService;
  @Captor
  private ArgumentCaptor<List<SampleContainer>> containersCaptor;
  @Captor
  private ArgumentCaptor<List<Well>> wellsCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  private ContainerSelectionFormDesign design = new ContainerSelectionFormDesign();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(ContainerSelectionForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private Map<Sample, List<Tube>> sourceTubes = new HashMap<>();
  private List<Plate> sourcePlates = new ArrayList<>();
  private Map<Sample, Map<Plate, List<Well>>> sourceWells = new LinkedHashMap<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new ContainerSelectionFormPresenter(tubeService, plateService, wellService);
    view.design = design;
    view.plateComponent = mock(PlateComponent.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    samples.add(entityManager.find(Sample.class, 559L));
    samples.add(entityManager.find(Sample.class, 560L));
    samples.add(entityManager.find(Sample.class, 444L));
    samples.forEach(s -> sourceTubes.put(s, generateTubes(s, 3)));
    when(tubeService.all(any())).thenAnswer(i -> sourceTubes.get(i.getArguments()[0]));
    sourcePlates.add(entityManager.find(Plate.class, 26L));
    sourcePlates.add(entityManager.find(Plate.class, 107L));
    sourcePlates.forEach(plate -> plate.initWells());
    when(plateService.all(any())).thenReturn(new ArrayList<>(sourcePlates));
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      Map<Plate, List<Well>> wellsMap = new HashMap<>();
      List<Well> wells = Arrays.asList(sourcePlates.get(0).well(i, 0));
      wells.stream().forEach(well -> well.setSample(sample));
      wellsMap.put(sourcePlates.get(0), wells);
      wells = Arrays.asList(sourcePlates.get(1).well(i, 1), sourcePlates.get(1).well(i, 3));
      wells.stream().forEach(well -> well.setSample(sample));
      wellsMap.put(sourcePlates.get(1), wells);
      sourceWells.put(sample, wellsMap);
    });
    when(wellService.location(any(), any())).thenAnswer(i -> i.getArguments()[0] != null
        ? sourceWells.get(i.getArguments()[0]).get(i.getArguments()[1])
        : null);
    when(wellService.last(any()))
        .thenAnswer(i -> i.getArguments()[0] != null && !sourcePlates.isEmpty()
            ? sourceWells.get(i.getArguments()[0]).get(sourcePlates.get(0)).get(0)
            : null);
    when(sampleService.get(any())).thenAnswer(i -> {
      Long id = i.getArgumentAt(0, Long.class);
      return id != null ? entityManager.find(Sample.class, id) : null;
    });
  }

  private List<Tube> generateTubes(Sample sample, int count) {
    List<Tube> tubes = new ArrayList<>();
    Instant timestamp = Instant.now();
    for (int i = 0; i < count; i++) {
      Tube tube = new Tube();
      tube.setId(sample.getId() * 100 + i);
      tube.setName(sample.getName() + "_" + i);
      tube.setSample(sample);
      tube.setTimestamp(timestamp.minus(count - i, ChronoUnit.DAYS));
      tubes.add(tube);
    }
    return tubes;
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.typePanel.getStyleName().contains(TYPE_PANEL));
    assertTrue(design.typePanel.getStyleName().contains(REQUIRED));
    assertTrue(design.type.getStyleName().contains(TYPE));
    assertTrue(design.tubesPanel.getStyleName().contains(TUBES_PANEL));
    assertTrue(design.tubes.getStyleName().contains(TUBES));
    assertTrue(design.platesPanel.getStyleName().contains(PLATES_PANEL));
    assertTrue(design.plates.getStyleName().contains(PLATES));
    assertTrue(design.platePanel.getStyleName().contains(PLATE_PANEL));
    assertTrue(design.select.getStyleName().contains(SELECT));
    assertTrue(design.clear.getStyleName().contains(CLEAR));
  }

  @Test
  public void caption() {
    presenter.init(view);

    assertEquals(resources.message(TYPE_PANEL), design.typePanel.getCaption());
    for (SampleContainerType type : SampleContainerType.values()) {
      assertEquals(type.getLabel(locale), design.type.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(TUBES_PANEL), design.tubesPanel.getCaption());
    assertEquals(resources.message(PLATES_PANEL), design.platesPanel.getCaption());
    assertEquals(resources.message(PLATES), design.plates.getCaption());
    for (Plate plate : sourcePlates) {
      assertEquals(plate.getName(), design.plates.getItemCaptionGenerator().apply(plate));
    }
    assertEquals(resources.message(SELECT), design.select.getCaption());
    assertEquals(resources.message(CLEAR), design.clear.getCaption());
  }

  @Test
  public void typeItems() {
    presenter.init(view);

    assertEquals(WELL, design.type.getValue());
    ListDataProvider<SampleContainerType> dataProvider = dataProvider(design.type);
    assertEquals(SampleContainerType.values().length, dataProvider.getItems().size());
    for (SampleContainerType type : SampleContainerType.values()) {
      assertTrue(dataProvider.getItems().contains(type));
    }
  }

  @Test
  public void changeType() {
    presenter.init(view);
    presenter.setSamples(samples);
    assertFalse(design.tubesPanel.isVisible());
    assertTrue(design.platesPanel.isVisible());

    design.type.setValue(TUBE);
    assertTrue(design.tubesPanel.isVisible());
    assertFalse(design.platesPanel.isVisible());

    design.type.setValue(WELL);
    assertFalse(design.tubesPanel.isVisible());
    assertTrue(design.platesPanel.isVisible());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void tubes() {
    presenter.init(view);
    presenter.setSamples(samples);

    assertEquals(2, design.tubes.getColumns().size());
    assertEquals(SAMPLE, design.tubes.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.tubes.getColumn(SAMPLE).getCaption());
    for (Sample sample : samples) {
      assertEquals(sample.getName(),
          design.tubes.getColumn(SAMPLE).getValueProvider().apply(sample));
    }
    assertEquals(CONTAINER_TUBE, design.tubes.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.tubes.getColumns().get(1).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(CONTAINER_TUBE),
        design.tubes.getColumn(CONTAINER_TUBE).getCaption());
    assertFalse(design.tubes.getColumn(CONTAINER_TUBE).isSortable());
    for (Sample sample : samples) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) design.tubes.getColumn(CONTAINER_TUBE).getValueProvider().apply(sample);
      assertTrue(field.isRequiredIndicatorVisible());
      assertEquals(sourceTubes.get(sample).get(0), field.getValue());
      ListDataProvider<Tube> dataProvider = dataProvider(field);
      assertEquals(sourceTubes.get(sample).size(), dataProvider.getItems().size());
      for (Tube tube : sourceTubes.get(sample)) {
        assertTrue(dataProvider.getItems().contains(tube));
        assertEquals(tube.getName(), field.getItemCaptionGenerator().apply(tube));
      }
    }
    ListDataProvider<Sample> dataProvider = dataProvider(design.tubes);
    for (Sample sample : samples) {
      assertTrue(dataProvider.getItems().contains(sample));
    }
  }

  @Test
  public void plates() {
    presenter.init(view);
    presenter.setSamples(samples);

    Plate plate = sourcePlates.get(0);
    assertEquals(plate, design.plates.getValue());
    assertTrue(design.plates.isRequiredIndicatorVisible());
    verify(view.plateComponent).setMultiSelect(true);
    verify(view.plateComponent).setValue(plate);
    verify(view.plateComponent).setSelectedWells(wellsCaptor.capture());
    List<Well> wells =
        samples.stream().flatMap(sample -> sourceWells.get(sample).get(plate).stream())
            .collect(Collectors.toList());
    assertEquals(wells, wellsCaptor.getValue());
    assertEquals(plate.getName(), design.platePanel.getCaption());
    ListDataProvider<Plate> dataProvider = dataProvider(design.plates);
    assertEquals(sourcePlates.size(), dataProvider.getItems().size());
    for (Plate contain : sourcePlates) {
      assertTrue(dataProvider.getItems().contains(contain));
    }
  }

  @Test
  public void selectPlate() {
    presenter.init(view);
    presenter.setSamples(samples);

    Plate plate = sourcePlates.get(1);
    design.plates.setValue(plate);
    assertEquals(plate, design.plates.getValue());
    verify(view.plateComponent).setValue(plate);
    verify(view.plateComponent, atLeastOnce()).setSelectedWells(wellsCaptor.capture());
    List<Well> wells =
        samples.stream().flatMap(sample -> sourceWells.get(sample).get(plate).stream())
            .collect(Collectors.toList());
    assertEquals(wells, wellsCaptor.getValue());
    assertEquals(plate.getName(), design.platePanel.getCaption());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void select_TubeEmpty() {
    Sample sample = samples.get(0);
    sourceTubes.put(sample, new ArrayList<>());
    presenter.init(view);
    presenter.setSamples(samples);
    design.type.setValue(TUBE);
    ComboBox<Tube> field =
        (ComboBox<Tube>) design.tubes.getColumn(CONTAINER_TUBE).getValueProvider().apply(sample);

    design.select.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(view, never()).fireSaveEvent(any());
  }

  @Test
  public void select_PlateEmpty() {
    when(plateService.all(any())).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.setSamples(samples);
    when(view.plateComponent.getSelectedWells()).thenReturn(Collections.emptyList());

    design.select.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.plates.getErrorMessage().getFormattedHtmlMessage());
    verify(view, never()).fireSaveEvent(any());
  }

  @Test
  public void select_PlateNotAllSamples() {
    presenter.init(view);
    presenter.setSamples(samples);
    when(view.plateComponent.getSelectedWells())
        .thenReturn(sourceWells.get(samples.get(0)).get(sourcePlates.get(0)));

    design.select.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(PLATE_SAMPLE_NOT_SELECTED, samples.get(1).getName())),
        design.plates.getErrorMessage().getFormattedHtmlMessage());
    verify(view, never()).fireSaveEvent(any());
  }

  @Test
  public void select_Tubes() {
    presenter.init(view);
    presenter.setSamples(samples);
    design.type.setValue(TUBE);

    design.select.click();

    verify(view, never()).showError(any());
    verify(view).fireSaveEvent(containersCaptor.capture());
    List<SampleContainer> containers = containersCaptor.getValue();
    assertEquals(samples.size(), containers.size());
    for (Sample sample : samples) {
      assertTrue(containers.contains(sourceTubes.get(sample).get(0)));
    }
  }

  @Test
  public void select_Wells() {
    presenter.init(view);
    presenter.setSamples(samples);
    Plate plate = sourcePlates.get(0);
    when(view.plateComponent.getSelectedWells())
        .thenReturn(plate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));

    design.select.click();

    verify(view, never()).showError(any());
    verify(view).fireSaveEvent(containersCaptor.capture());
    List<SampleContainer> containers = containersCaptor.getValue();
    assertEquals(samples.size(), containers.size());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0))) {
      assertTrue(containers.contains(well));
    }
  }

  @Test
  public void select_WellsWithEmpty() {
    presenter.init(view);
    presenter.setSamples(samples);
    Plate plate = sourcePlates.get(0);
    when(view.plateComponent.getSelectedWells())
        .thenReturn(plate.wells(new WellLocation(0, 0), new WellLocation(samples.size(), 0)));

    design.select.click();

    verify(view, never()).showError(any());
    verify(view).fireSaveEvent(containersCaptor.capture());
    List<SampleContainer> containers = containersCaptor.getValue();
    assertEquals(samples.size(), containers.size());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0))) {
      assertTrue(containers.contains(well));
    }
  }

  @Test
  public void select_WellsWithOtherSample() {
    Plate plate = sourcePlates.get(0);
    plate.well(samples.size(), 0).setSample(new SubmissionSample(2000L, "test"));
    presenter.init(view);
    presenter.setSamples(samples);
    when(view.plateComponent.getSelectedWells())
        .thenReturn(plate.wells(new WellLocation(0, 0), new WellLocation(samples.size(), 0)));

    design.select.click();

    verify(view, never()).showError(any());
    verify(view).fireSaveEvent(containersCaptor.capture());
    List<SampleContainer> containers = containersCaptor.getValue();
    assertEquals(samples.size(), containers.size());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0))) {
      assertTrue(containers.contains(well));
    }
  }

  @Test
  public void select_MultipleWellsPerSample() {
    presenter.init(view);
    presenter.setSamples(samples);
    Plate plate = sourcePlates.get(0);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(plate).stream()).collect(Collectors.toList());
    when(view.plateComponent.getSelectedWells()).thenReturn(sourceWells);

    design.select.click();

    verify(view, never()).showError(any());
    verify(view).fireSaveEvent(containersCaptor.capture());
    List<SampleContainer> containers = containersCaptor.getValue();
    assertEquals(samples.size(), containers.size());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0))) {
      assertTrue(containers.contains(well));
    }
  }

  @Test
  public void clear() {
    presenter.init(view);
  }
}
