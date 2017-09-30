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

package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.CONTAINER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_CONTAINER_DUPLICATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_NO_SELECTION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_WELL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_WELL_IN_USE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.HEADER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAVE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_EMPTY;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_SAMPLE_NOT_SELECTED;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TEST;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TITLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE_PANEL;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellComparator;
import ca.qc.ircm.proview.plate.WellLocation;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class TransferViewPresenterTest {
  private TransferViewPresenter presenter;
  @Mock
  private TransferView view;
  @Mock
  private TransferService transferService;
  @Mock
  private TubeService tubeService;
  @Mock
  private PlateService plateService;
  @Mock
  private WellService wellService;
  @Captor
  private ArgumentCaptor<Collection<Well>> wellsCaptor;
  @Captor
  private ArgumentCaptor<Transfer> transferCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(TransferView.class, locale);
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
    presenter = new TransferViewPresenter(transferService, tubeService, wellService, plateService,
        applicationName);
    view.headerLabel = new Label();
    view.typePanel = new Panel();
    view.type = new RadioButtonGroup<>();
    view.transfersPanel = new Panel();
    view.transfers = new Grid<>();
    view.source = new Panel();
    view.sourcePlateLayout = new VerticalLayout();
    view.sourcePlatesField = new ComboBox<>();
    view.sourcePlatePanel = new Panel();
    view.sourcePlateFormLayout = new VerticalLayout();
    view.sourcePlateForm = mock(PlateComponent.class);
    view.destination = new Panel();
    view.destinationPlateLayout = new VerticalLayout();
    view.destinationPlatesField = new ComboBox<>();
    view.destinationPlatePanel = new Panel();
    view.destinationPlateFormLayout = new VerticalLayout();
    view.destinationPlateForm = mock(PlateComponent.class);
    view.test = new Button();
    view.saveButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(view.savedSamples()).thenReturn(samples);
    samples.add(entityManager.find(Sample.class, 559L));
    samples.add(entityManager.find(Sample.class, 560L));
    samples.add(entityManager.find(Sample.class, 444L));
    samples.forEach(s -> sourceTubes.put(s, generateTubes(s, 3)));
    when(tubeService.all(any())).thenAnswer(i -> sourceTubes.get(i.getArguments()[0]));
    sourcePlates.add(entityManager.find(Plate.class, 26L));
    sourcePlates.add(entityManager.find(Plate.class, 107L));
    sourcePlates.forEach(plate -> plate.initWells());
    when(plateService.all(any())).thenReturn(sourcePlates);
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
        ? sourceWells.get(i.getArguments()[0]).get(i.getArguments()[1]) : null);
    when(wellService.last(any()))
        .thenAnswer(i -> i.getArguments()[0] != null && !sourcePlates.isEmpty()
            ? sourceWells.get(i.getArguments()[0]).get(sourcePlates.get(0)).get(0) : null);
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

  private <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(d -> d.getId() == id).findFirst();
  }

  private <V> boolean containsInstanceOf(Collection<V> extensions, Class<? extends V> clazz) {
    return extensions.stream().filter(extension -> clazz.isInstance(extension)).findAny()
        .isPresent();
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(ComboBox<V> comboBox) {
    return (ListDataProvider<V>) comboBox.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(RadioButtonGroup<V> radioButtonGroup) {
    return (ListDataProvider<V>) radioButtonGroup.getDataProvider();
  }

  private List<SampleTransfer> all(Collection<SampleTransfer> datas, Sample sample) {
    return datas.stream().filter(d -> sample.equals(d.getSample())).collect(Collectors.toList());
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(view.typePanel.getStyleName().contains(TRANSFER_TYPE_PANEL));
    assertTrue(view.type.getStyleName().contains(TRANSFER_TYPE));
    assertTrue(view.transfersPanel.getStyleName().contains(TRANSFERS_PANEL));
    assertTrue(view.transfers.getStyleName().contains(TRANSFERS));
    assertTrue(view.transfers.getStyleName().contains(COMPONENTS));
    assertTrue(view.source.getStyleName().contains(SOURCE));
    assertTrue(view.sourcePlatesField.getStyleName().contains(SOURCE_PLATES));
    assertTrue(view.sourcePlatePanel.getStyleName().contains(SOURCE_PLATE_PANEL));
    verify(view.sourcePlateForm).addStyleName(SOURCE_PLATE);
    assertTrue(view.destination.getStyleName().contains(DESTINATION));
    assertTrue(view.destinationPlatesField.getStyleName().contains(DESTINATION_PLATES));
    assertTrue(view.destinationPlatePanel.getStyleName().contains(DESTINATION_PLATE_PANEL));
    verify(view.destinationPlateForm).addStyleName(DESTINATION_PLATE);
    assertTrue(view.test.getStyleName().contains(TEST));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(TRANSFER_TYPE_PANEL), view.typePanel.getCaption());
    assertEquals(resources.message(TRANSFERS_PANEL), view.transfersPanel.getCaption());
    assertEquals(resources.message(SOURCE), view.source.getCaption());
    for (TransferType type : TransferType.values()) {
      assertEquals(type.getLabel(locale), view.type.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(SOURCE_PLATES), view.sourcePlatesField.getCaption());
    assertEquals(resources.message(DESTINATION), view.destination.getCaption());
    assertEquals(resources.message(DESTINATION_PLATES), view.destinationPlatesField.getCaption());
    assertEquals(resources.message(TEST), view.test.getCaption());
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
  }

  @Test
  public void typeValues() {
    presenter.init(view);
    presenter.enter("");

    ListDataProvider<TransferType> dataProvider = dataProvider(view.type);
    assertEquals(TransferType.values().length, dataProvider.getItems().size());
    for (TransferType type : TransferType.values()) {
      assertTrue(dataProvider.getItems().contains(type));
    }
  }

  @Test
  public void changeType() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(view.transfersPanel.isVisible());
    assertTrue(view.source.isVisible());
    assertTrue(view.destination.isVisible());

    view.type.setValue(TransferType.TUBES_TO_PLATE);
    assertTrue(view.transfersPanel.isVisible());
    assertFalse(view.source.isVisible());
    assertTrue(view.destination.isVisible());

    view.type.setValue(TransferType.TUBES_TO_TUBES);
    assertTrue(view.transfersPanel.isVisible());
    assertFalse(view.source.isVisible());
    assertFalse(view.destination.isVisible());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void transfers() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(SAMPLE, view.transfers.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), view.transfers.getColumn(SAMPLE).getCaption());
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      assertEquals(ts.getSample().getName(),
          view.transfers.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, view.transfers.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(view.transfers.getColumn(CONTAINER).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(CONTAINER), view.transfers.getColumn(CONTAINER).getCaption());
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      ListDataProvider<Tube> dataProvider = dataProvider(field);
      assertEquals(sourceTubes.get(ts.getSample()), dataProvider.getItems());
      assertEquals(sourceTubes.get(ts.getSample()).get(0), field.getValue());
    }
    assertEquals(DESTINATION_TUBE, view.transfers.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(view.transfers.getColumn(DESTINATION_TUBE).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_TUBE),
        view.transfers.getColumn(DESTINATION_TUBE).getCaption());
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      ListDataProvider<Tube> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
    }
    assertEquals(DESTINATION_WELL, view.transfers.getColumns().get(3).getId());
    assertTrue(containsInstanceOf(view.transfers.getColumn(DESTINATION_WELL).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_WELL),
        view.transfers.getColumn(DESTINATION_WELL).getCaption());
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
    }
    Plate plate = new Plate();
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertEquals(plate.getWells(), dataProvider.getItems());
    }
  }

  @Test
  public void sourcePlatesFieldItems() {
    presenter.init(view);
    presenter.enter("");

    Collection<Plate> plates = dataProvider(view.sourcePlatesField).getItems();
    assertEquals(sourcePlates.size(), plates.size());
    assertTrue(sourcePlates.containsAll(plates));
    assertTrue(plates.containsAll(sourcePlates));
    for (Plate plate : sourcePlates) {
      assertEquals(plate.getName(), view.sourcePlatesField.getItemCaptionGenerator().apply(plate));
    }
    assertEquals(sourcePlates.get(0), view.sourcePlatesField.getValue());
  }

  @Test
  public void sourcePlatesFieldItems_NoLastWell() {
    when(wellService.last(any())).thenReturn(null);

    presenter.init(view);
    presenter.enter("");

    Collection<Plate> plates = dataProvider(view.sourcePlatesField).getItems();
    assertEquals(sourcePlates.size(), plates.size());
    assertTrue(sourcePlates.containsAll(plates));
    assertTrue(plates.containsAll(sourcePlates));
    for (Plate plate : sourcePlates) {
      assertEquals(plate.getName(), view.sourcePlatesField.getItemCaptionGenerator().apply(plate));
    }
    assertEquals(sourcePlates.get(0), view.sourcePlatesField.getValue());
  }

  @Test
  public void selectSourcePlate() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = sourcePlates.get(1);

    view.sourcePlatesField.setValue(plate);

    assertEquals(plate.getName(), view.sourcePlatePanel.getCaption());
    verify(view.sourcePlateForm, atLeastOnce()).setMultiSelect(true);
    verify(view.sourcePlateForm, atLeastOnce()).setValue(plate);
    verify(view.sourcePlateForm, atLeastOnce()).setSelectedWells(wellsCaptor.capture());
    Collection<Well> wells = wellsCaptor.getValue();
    assertEquals(samples.size() * 2, wells.size());
    for (Map<Plate, List<Well>> sampleWells : sourceWells.values()) {
      for (Well well : sampleWells.get(plate)) {
        assertTrue(find(wells, well.getId()).isPresent());
      }
    }
  }

  @Test
  public void defaultSourcePlate() {
    presenter.init(view);
    presenter.enter("");

    Plate plate = sourcePlates.get(0);
    assertEquals(plate.getName(), view.sourcePlatePanel.getCaption());
    verify(view.sourcePlateForm, atLeastOnce()).setMultiSelect(true);
    verify(view.sourcePlateForm, atLeastOnce()).setValue(plate);
    verify(view.sourcePlateForm, atLeastOnce()).setSelectedWells(wellsCaptor.capture());
    Collection<Well> wells = wellsCaptor.getValue();
    assertEquals(samples.size(), wells.size());
    for (Map<Plate, List<Well>> sampleWells : sourceWells.values()) {
      for (Well well : sampleWells.get(plate)) {
        assertTrue(find(wells, well.getId()).isPresent());
      }
    }
  }

  @Test
  public void destinationPlatesField() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(view.destinationPlatesField.isEmptySelectionAllowed());
    assertNotNull(view.destinationPlatesField.getNewItemHandler() != null);
    assertTrue(view.destinationPlatesField.isRequiredIndicatorVisible());
    Collection<Plate> plates = dataProvider(view.destinationPlatesField).getItems();
    assertEquals(sourcePlates.size(), plates.size());
    assertTrue(plates.containsAll(sourcePlates));
    assertTrue(sourcePlates.containsAll(plates));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_TubeToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Map<Sample, Tube> sources = new HashMap<>();
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    transfers.forEach(ts -> {
      ComboBox<Tube> comboBox =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      sources.put(ts.getSample(), comboBox.getValue());
    });
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 0));
    }
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 0).getSample());
    assertEquals(samples.get(1), plate.well(1, 0).getSample());
    assertEquals(samples.get(2), plate.well(2, 0).getSample());
    for (Well well : plate.wells(new WellLocation(3, 0), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    count = 2;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 3));
    }
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertNull(well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(5, 3), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_TubeToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Map<Sample, Tube> sources = new HashMap<>();
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    transfers.forEach(ts -> {
      ComboBox<Tube> comboBox =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      sources.put(ts.getSample(), comboBox.getValue());
    });
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    Plate plateCopy = new Plate();
    plateCopy.setName(plate.getName());
    plateCopy.setColumnCount(plate.getColumnCount());
    plateCopy.setRowCount(plate.getRowCount());
    plateCopy.initWells();
    plateCopy.getWells().stream()
        .forEach(well -> well.setSample(plate.well(well.getRow(), well.getColumn()).getSample()));
    when(plateService.get(any(Long.class))).thenReturn(plateCopy);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 2));
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 2));
    }
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 2).getSample());
    assertEquals(samples.get(1), plate.well(1, 2).getSample());
    assertEquals(samples.get(2), plate.well(2, 2).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(7, 1))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(3, 2), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    count = 2;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 3));
    }
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(5, 3), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
  }

  @Test
  public void test_PlateToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 0).getSample());
    assertEquals(samples.get(1), plate.well(1, 0).getSample());
    assertEquals(samples.get(2), plate.well(2, 0).getSample());
    for (Well well : plate.wells(new WellLocation(3, 0), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertNull(well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(5, 3), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }
  }

  @Test
  public void test_PlateToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    Plate plateCopy = new Plate();
    plateCopy.setName(plate.getName());
    plateCopy.setColumnCount(plate.getColumnCount());
    plateCopy.setRowCount(plate.getRowCount());
    plateCopy.initWells();
    plateCopy.getWells().stream()
        .forEach(well -> well.setSample(plate.well(well.getRow(), well.getColumn()).getSample()));
    when(plateService.get(any(Long.class))).thenReturn(plateCopy);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 2));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 2).getSample());
    assertEquals(samples.get(1), plate.well(1, 2).getSample());
    assertEquals(samples.get(2), plate.well(2, 2).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(7, 1))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(3, 2), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(5, 3), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
  }

  @Test
  public void test_PlateToNewPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(1);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(sourceWells);
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 0).getSample());
    assertEquals(samples.get(1), plate.well(1, 0).getSample());
    assertEquals(samples.get(2), plate.well(2, 0).getSample());
    assertEquals(samples.get(0), plate.well(3, 0).getSample());
    assertEquals(samples.get(1), plate.well(4, 0).getSample());
    assertEquals(samples.get(2), plate.well(5, 0).getSample());
    for (Well well : plate.wells(new WellLocation(6, 0), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    assertEquals(samples.get(0), plate.well(5, 3).getSample());
    assertEquals(samples.get(1), plate.well(6, 3).getSample());
    assertEquals(samples.get(2), plate.well(7, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertNull(well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(0, 4), new WellLocation(7, 11))) {
      assertNull(well.getSample());
    }
  }

  @Test
  public void test_PlateToExistingPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(1);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(sourceWells);
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    Plate plateCopy = new Plate();
    plateCopy.setName(plate.getName());
    plateCopy.setColumnCount(plate.getColumnCount());
    plateCopy.setRowCount(plate.getRowCount());
    plateCopy.initWells();
    plateCopy.getWells().stream()
        .forEach(well -> well.setSample(plate.well(well.getRow(), well.getColumn()).getSample()));
    when(plateService.get(any(Long.class))).thenReturn(plateCopy);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 2));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(0, 2).getSample());
    assertEquals(samples.get(1), plate.well(1, 2).getSample());
    assertEquals(samples.get(2), plate.well(2, 2).getSample());
    assertEquals(samples.get(0), plate.well(3, 2).getSample());
    assertEquals(samples.get(1), plate.well(4, 2).getSample());
    assertEquals(samples.get(2), plate.well(5, 2).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(7, 1))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(6, 2), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(2, 3));
    view.test.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    verify(view.destinationPlateForm, atLeastOnce()).setValue(plate);
    assertEquals(samples.get(0), plate.well(2, 3).getSample());
    assertEquals(samples.get(1), plate.well(3, 3).getSample());
    assertEquals(samples.get(2), plate.well(4, 3).getSample());
    assertEquals(samples.get(0), plate.well(5, 3).getSample());
    assertEquals(samples.get(1), plate.well(6, 3).getSample());
    assertEquals(samples.get(2), plate.well(7, 3).getSample());
    for (Well well : plate.wells(new WellLocation(0, 0), new WellLocation(1, 3))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
    for (Well well : plate.wells(new WellLocation(0, 4), new WellLocation(7, 11))) {
      assertEquals(plateCopy.well(well.getRow(), well.getColumn()).getSample(), well.getSample());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_NoSourceTube() {
    Sample sample = samples.get(0);
    sourceTubes.put(sample, new ArrayList<>());
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_TUBES);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> comboBox = (ComboBox<Tube>) view.transfers.getColumn(CONTAINER)
        .getValueProvider().apply(transfers.iterator().next());

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        comboBox.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_NoSourceWells() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate destination = new Plate(null, "test");
    destination.initWells();
    view.destinationPlatesField.setValue(destination);

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(resources.message(SOURCE_PLATE_EMPTY)),
        view.sourcePlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_SourcePlateSelectedWellEmpty() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate plate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells())
        .thenReturn(plate.wells(new WellLocation(0, 0), new WellLocation(samples.size(), 0)));
    Plate destination = new Plate(null, "test");
    destination.initWells();
    view.destinationPlatesField.setValue(destination);
    when(view.destinationPlateForm.getValue()).thenReturn(destination);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(destination.well(0, 0));

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(any());
  }

  @Test
  public void save_SourcePlateMissingOneSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate plate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells())
        .thenReturn(plate.wells(new WellLocation(1, 0), new WellLocation(samples.size() - 1, 0)));
    Plate destination = new Plate(null, "test");
    destination.initWells();
    view.destinationPlatesField.setValue(destination);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, samples.get(0).getName())),
        view.sourcePlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_NoDestinationTube() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_TUBES);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field = (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.iterator().next());
    field.setValue(null);

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationTubeExists() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_TUBES);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field = (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.iterator().next());
    field.setValue(new Tube(null, "test"));
    when(tubeService.get("test")).thenReturn(new Tube(null, "test"));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, "test")),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
    verify(tubeService, atLeastOnce()).get("test");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationTubeDuplicate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_TUBES);
    List<SampleTransfer> transfers = new ArrayList<>(dataProvider(view.transfers).getItems());
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field1 = (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.get(0));
    field1.setValue(new Tube(null, "test"));
    ComboBox<Tube> field2 = (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.get(1));
    field2.setValue(new Tube(null, "test"));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_CONTAINER_DUPLICATE, TUBE.ordinal(), "test")),
        field2.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationWellUsed() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Plate destination = new Plate();
    destination.initWells();
    view.destinationPlatesField.setValue(destination);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(destination.well(count++, 0));
    }
    destination.well(0, 0).setId(2000L);
    Well used = new Well(0, 0);
    used.setSample(new SubmissionSample());
    when(wellService.get(2000L)).thenReturn(used);
    ComboBox<Well> field = (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.iterator().next());

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_WELL_IN_USE, field.getValue().getName())),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
    verify(wellService).get(2000L);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_NoDestinationWell() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Plate destination = new Plate();
    destination.initWells();
    view.destinationPlatesField.setValue(destination);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    ComboBox<Well> field = (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.iterator().next());

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationWellDuplicate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Plate destination = new Plate();
    destination.initWells();
    view.destinationPlatesField.setValue(destination);
    List<SampleTransfer> transfers = new ArrayList<>(dataProvider(view.transfers).getItems());
    for (SampleTransfer ts : transfers) {
      view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
    }
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(destination.well(count++, 0));
    }
    ComboBox<Well> field1 = (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.get(0));
    ComboBox<Well> field2 = (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.get(1));
    field2.setValue(field1.getValue());

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(resources.message(DESTINATION_CONTAINER_DUPLICATE, WELL.ordinal(),
        field2.getValue().getName())), field2.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNoSelectedWell() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(resources.message(DESTINATION_PLATE_NO_SELECTION)),
        view.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNotEnoughFreeSpace_Overflow() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell())
        .thenReturn(plate.well(plate.getRowCount() - 2, plate.getColumnCount() - 1));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size())),
        view.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNotEnoughFreeSpace_WellHasSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = new Plate(null, "test");
    plate.initWells();
    plate.well(1, 0).setSample(samples.get(0));
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size())),
        view.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToTube() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_TUBES);
    Map<Sample, Tube> sources = new HashMap<>();
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> comboBox =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      sources.put(ts.getSample(), comboBox.getValue());
    }
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sources.get(sample), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals(sample.getName() + "_destination",
          sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Map<Sample, Tube> sources = new HashMap<>();
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> comboBox =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      sources.put(ts.getSample(), comboBox.getValue());
    }
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 0));
    }

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sources.get(sample), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.TUBES_TO_PLATE);
    Map<Sample, Tube> sources = new HashMap<>();
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> comboBox =
          (ComboBox<Tube>) view.transfers.getColumn(CONTAINER).getValueProvider().apply(ts);
      sources.put(ts.getSample(), comboBox.getValue());
    }
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    int count = 0;
    for (SampleTransfer ts : transfers) {
      ComboBox<Well> field =
          (ComboBox<Well>) view.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 4));
    }

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sources.get(sample), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }

  @Test
  @Ignore("Not available for now")
  @SuppressWarnings("unchecked")
  public void save_PlateToTube() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.well(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + i, sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  public void save_PlateToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.well(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  public void save_PlateToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(
        sourcePlate.wells(new WellLocation(0, 0), new WellLocation(samples.size() - 1, 0)));
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.well(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }

  @Test
  @Ignore("Not available for now")
  @SuppressWarnings("unchecked")
  public void save_PlateToTube_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(1);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(sourceWells);
    Collection<SampleTransfer> transfers = dataProvider(view.transfers).getItems();
    for (SampleTransfer ts : transfers) {
      ComboBox<Tube> field =
          (ComboBox<Tube>) view.transfers.getColumn(DESTINATION_TUBE).getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), sourceWells.size());
    int count = 0;
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<SampleTransfer> sampleTransfers = all(transfer.getTreatmentSamples(), sample);
      assertEquals(2, sampleTransfers.size());
      SampleTransfer sampleTransfer = sampleTransfers.get(0);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.well(i, 1), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, sampleTransfer.getDestinationContainer().getName());
      sampleTransfer = sampleTransfers.get(1);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.well(i, 3), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  public void save_PlateToNewPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(1);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(sourceWells);
    Plate plate = new Plate(null, "test");
    plate.initWells();
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), sourceWells.size());
    Collections.sort(sourceWells, new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
    int count = 0;
    for (int i = 0; i < sourceWells.size(); i++) {
      Well source = sourceWells.get(i);
      Sample sample = source.getSample();
      SampleTransfer sampleTransfer = transfer.getTreatmentSamples().get(i);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(source, sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  public void save_PlateToExistingPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    Plate sourcePlate = sourcePlates.get(1);
    List<Well> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateForm.getSelectedWells()).thenReturn(sourceWells);
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getValue()).thenReturn(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));

    view.saveButton.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), sourceWells.size());
    Collections.sort(sourceWells, new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
    int count = 0;
    for (int i = 0; i < sourceWells.size(); i++) {
      Well source = sourceWells.get(i);
      Sample sample = source.getSample();
      SampleTransfer sampleTransfer = transfer.getTreatmentSamples().get(i);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(source, sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }
}
