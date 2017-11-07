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
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
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
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DOWN;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.HEADER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.INVALID_CONTAINERS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.INVALID_TRANSFER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.NO_CONTAINERS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAMPLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAVE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAVED;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TEST;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TITLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TRANSFER_TYPE_PANEL;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellComparator;
import ca.qc.ircm.proview.plate.WellLocation;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.transfer.TransferedSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.ComboBox;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
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
  private WellService wellService;
  @Mock
  private PlateService plateService;
  @Mock
  private SampleContainerService sampleContainerService;
  @Captor
  private ArgumentCaptor<Collection<Well>> wellsCaptor;
  @Captor
  private ArgumentCaptor<Transfer> transferCaptor;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private TubeService realTubeService;
  @Inject
  private WellService realWellService;
  @Inject
  private PlateService realPlateService;
  @Value("${spring.application.name}")
  private String applicationName;
  private TransferViewDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(TransferView.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private List<Sample> samples = new ArrayList<>();
  private List<Tube> sourceTubes = new ArrayList<>();
  private List<Well> sourceWells = new ArrayList<>();
  private List<Plate> destinationPlates = new ArrayList<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new TransferViewPresenter(transferService, tubeService, wellService, plateService,
        sampleContainerService, applicationName);
    design = new TransferViewDesign();
    view.design = design;
    view.destinationPlateForm = mock(PlateComponent.class);
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    samples.add(entityManager.find(Sample.class, 559L));
    samples.add(entityManager.find(Sample.class, 560L));
    samples.add(entityManager.find(Sample.class, 444L));
    sourceTubes = samples.stream().map(sample -> {
      Tube tube = new Tube(sample.getId(), sample.getName());
      tube.setSample(sample);
      return tube;
    }).collect(Collectors.toList());
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceWells));
    destinationPlates.add(entityManager.find(Plate.class, 26L));
    destinationPlates.add(entityManager.find(Plate.class, 107L));
    when(plateService.all(any())).thenReturn(new ArrayList<>(destinationPlates));
  }

  private void sourceTubes() {
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceTubes));
  }

  private List<TransferedSample> all(Collection<TransferedSample> datas, Sample sample) {
    return datas.stream().filter(d -> sample.equals(d.getSample())).collect(Collectors.toList());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(design.typePanel.getStyleName().contains(TRANSFER_TYPE_PANEL));
    assertTrue(design.type.getStyleName().contains(TRANSFER_TYPE));
    assertTrue(design.transfersPanel.getStyleName().contains(TRANSFERS_PANEL));
    assertTrue(design.transfers.getStyleName().contains(TRANSFERS));
    assertTrue(design.transfers.getStyleName().contains(COMPONENTS));
    assertTrue(design.down.getStyleName().contains(DOWN));
    assertTrue(design.down.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.destination.getStyleName().contains(DESTINATION));
    assertTrue(design.destinationPlatesField.getStyleName().contains(DESTINATION_PLATES));
    assertTrue(design.destinationPlatePanel.getStyleName().contains(DESTINATION_PLATE_PANEL));
    verify(view.destinationPlateForm).addStyleName(DESTINATION_PLATE);
    assertTrue(design.test.getStyleName().contains(TEST));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    assertEquals(resources.message(TRANSFER_TYPE_PANEL), design.typePanel.getCaption());
    for (SampleContainerType type : SampleContainerType.values()) {
      assertEquals(type.getLabel(locale), design.type.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(TRANSFERS_PANEL), design.transfersPanel.getCaption());
    assertEquals(resources.message(DOWN), design.down.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.down.getIcon());
    assertEquals(resources.message(DESTINATION), design.destination.getCaption());
    assertEquals(resources.message(DESTINATION_PLATES), design.destinationPlatesField.getCaption());
    assertEquals(resources.message(TEST), design.test.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
  }

  @Test
  public void typeValues() {
    presenter.init(view);
    presenter.enter("");

    ListDataProvider<SampleContainerType> dataProvider = dataProvider(design.type);
    assertEquals(SampleContainerType.values().length, dataProvider.getItems().size());
    for (SampleContainerType type : SampleContainerType.values()) {
      assertTrue(dataProvider.getItems().contains(type));
    }
  }

  @Test
  public void changeType_Tube() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");

    assertTrue(design.type.getItemEnabledProvider().test(TUBE));
    assertTrue(design.type.getItemEnabledProvider().test(WELL));
    assertTrue(design.typePanel.isVisible());
    assertTrue(design.transfersPanel.isVisible());
    assertTrue(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    assertFalse(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    assertTrue(design.destination.isVisible());
    assertTrue(design.down.isVisible());

    design.type.setValue(TUBE);
    assertTrue(design.type.getItemEnabledProvider().test(TUBE));
    assertTrue(design.type.getItemEnabledProvider().test(WELL));
    assertTrue(design.typePanel.isVisible());
    assertTrue(design.transfersPanel.isVisible());
    assertFalse(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    assertTrue(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    assertFalse(design.destination.isVisible());
    assertFalse(design.down.isVisible());

    design.type.setValue(WELL);
    assertTrue(design.type.getItemEnabledProvider().test(TUBE));
    assertTrue(design.type.getItemEnabledProvider().test(WELL));
    assertTrue(design.typePanel.isVisible());
    assertTrue(design.transfersPanel.isVisible());
    assertTrue(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    assertFalse(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    assertTrue(design.destination.isVisible());
    assertTrue(design.down.isVisible());
  }

  @Test
  public void changeType_Well() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.type.getItemEnabledProvider().test(TUBE));
    assertTrue(design.type.getItemEnabledProvider().test(WELL));
    assertTrue(design.typePanel.isVisible());
    assertTrue(design.transfersPanel.isVisible());
    assertTrue(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    assertTrue(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    assertTrue(design.destination.isVisible());
    assertFalse(design.down.isVisible());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void transfers() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(5, design.transfers.getColumns().size());
    assertEquals(SAMPLE, design.transfers.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.transfers.getColumn(SAMPLE).getCaption());
    assertFalse(design.transfers.getColumn(SAMPLE).isHidden());
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      assertEquals(ts.getSample().getName(),
          design.transfers.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.transfers.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.transfers.getColumn(CONTAINER).getCaption());
    assertFalse(design.transfers.getColumn(CONTAINER).isHidden());
    for (TransferedSample ts : transfers) {
      assertEquals(ts.getContainer().getFullName(),
          design.transfers.getColumn(CONTAINER).getValueProvider().apply(ts));
      assertEquals(ts.getContainer().isBanned() ? BANNED : "",
          design.transfers.getColumn(CONTAINER).getStyleGenerator().apply(ts));
    }
    assertEquals(DESTINATION, design.transfers.getColumns().get(2).getId());
    assertEquals(resources.message(DESTINATION),
        design.transfers.getColumn(DESTINATION).getCaption());
    assertTrue(design.transfers.getColumn(DESTINATION).isHidden());
    for (TransferedSample ts : transfers) {
      assertEquals("", design.transfers.getColumn(DESTINATION).getValueProvider().apply(ts));
    }
    assertEquals(DESTINATION_TUBE, design.transfers.getColumns().get(3).getId());
    assertTrue(containsInstanceOf(design.transfers.getColumn(DESTINATION_TUBE).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_TUBE),
        design.transfers.getColumn(DESTINATION_TUBE).getCaption());
    assertTrue(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    assertFalse(design.transfers.getColumn(DESTINATION_TUBE).isSortable());
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(DESTINATION_TUBE));
      ListDataProvider<Tube> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
      assertNotNull(field.getNewItemHandler());
      assertFalse(field.isEmptySelectionAllowed());
      assertTrue(field.isRequiredIndicatorVisible());
      field.getNewItemHandler().accept("test new tube");
      assertNull(field.getValue().getId());
      assertEquals("test new tube", field.getValue().getName());
      dataProvider = dataProvider(field);
      assertEquals(1, dataProvider.getItems().size());
      assertTrue(find(dataProvider.getItems(), "test new tube").isPresent());
    }
    assertEquals(DESTINATION_WELL, design.transfers.getColumns().get(4).getId());
    assertTrue(containsInstanceOf(design.transfers.getColumn(DESTINATION_WELL).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_WELL),
        design.transfers.getColumn(DESTINATION_WELL).getCaption());
    assertTrue(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    assertFalse(design.transfers.getColumn(DESTINATION_WELL).isSortable());
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(DESTINATION_WELL));
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
      assertNull(field.getNewItemHandler());
      assertFalse(field.isEmptySelectionAllowed());
      assertTrue(field.isRequiredIndicatorVisible());
    }
    Plate plate = new Plate();
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertEquals(plate.getWells(), dataProvider.getItems());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void transfers_Transfer() {
    presenter = new TransferViewPresenter(transferService, realTubeService, realWellService,
        realPlateService, sampleContainerService, applicationName);
    Transfer transfer = entityManager.find(Transfer.class, 3L);
    when(transferService.get(any())).thenReturn(transfer);
    presenter.init(view);
    presenter.enter("3");

    assertEquals(5, design.transfers.getColumns().size());
    assertEquals(SAMPLE, design.transfers.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLE), design.transfers.getColumn(SAMPLE).getCaption());
    assertFalse(design.transfers.getColumn(SAMPLE).isHidden());
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      assertEquals(ts.getSample().getName(),
          design.transfers.getColumn(SAMPLE).getValueProvider().apply(ts));
    }
    assertEquals(CONTAINER, design.transfers.getColumns().get(1).getId());
    assertEquals(resources.message(CONTAINER), design.transfers.getColumn(CONTAINER).getCaption());
    assertFalse(design.transfers.getColumn(CONTAINER).isHidden());
    for (TransferedSample ts : transfers) {
      assertEquals(ts.getContainer().getFullName(),
          design.transfers.getColumn(CONTAINER).getValueProvider().apply(ts));
      assertEquals(ts.getContainer().isBanned() ? BANNED : "",
          design.transfers.getColumn(CONTAINER).getStyleGenerator().apply(ts));
    }
    assertEquals(DESTINATION, design.transfers.getColumns().get(2).getId());
    assertEquals(resources.message(DESTINATION),
        design.transfers.getColumn(DESTINATION).getCaption());
    assertFalse(design.transfers.getColumn(DESTINATION).isHidden());
    for (TransferedSample ts : transfers) {
      assertEquals(ts.getDestinationContainer().getFullName(),
          design.transfers.getColumn(DESTINATION).getValueProvider().apply(ts));
    }
    assertEquals(DESTINATION_TUBE, design.transfers.getColumns().get(3).getId());
    assertTrue(containsInstanceOf(design.transfers.getColumn(DESTINATION_TUBE).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_TUBE),
        design.transfers.getColumn(DESTINATION_TUBE).getCaption());
    assertTrue(design.transfers.getColumn(DESTINATION_TUBE).isHidden());
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(DESTINATION_TUBE));
      ListDataProvider<Tube> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
      assertNotNull(field.getNewItemHandler());
      assertFalse(field.isEmptySelectionAllowed());
      assertTrue(field.isRequiredIndicatorVisible());
      field.getNewItemHandler().accept("test new tube");
      assertNull(field.getValue().getId());
      assertEquals("test new tube", field.getValue().getName());
      dataProvider = dataProvider(field);
      assertEquals(1, dataProvider.getItems().size());
      assertTrue(find(dataProvider.getItems(), "test new tube").isPresent());
    }
    assertEquals(DESTINATION_WELL, design.transfers.getColumns().get(4).getId());
    assertTrue(containsInstanceOf(design.transfers.getColumn(DESTINATION_WELL).getExtensions(),
        ComponentRenderer.class));
    assertEquals(resources.message(DESTINATION_WELL),
        design.transfers.getColumn(DESTINATION_WELL).getCaption());
    assertTrue(design.transfers.getColumn(DESTINATION_WELL).isHidden());
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertTrue(field.getStyleName().contains(DESTINATION_WELL));
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertTrue(dataProvider.getItems().isEmpty());
      assertNull(field.getNewItemHandler());
      assertFalse(field.isEmptySelectionAllowed());
      assertTrue(field.isRequiredIndicatorVisible());
    }
    Plate plate = new Plate();
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      ListDataProvider<Well> dataProvider = dataProvider(field);
      assertEquals(plate.getWells(), dataProvider.getItems());
    }
  }

  @Test
  public void destinationPlatesField() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(design.destinationPlatesField.isEmptySelectionAllowed());
    assertNotNull(design.destinationPlatesField.getNewItemHandler());
    assertTrue(design.destinationPlatesField.isRequiredIndicatorVisible());
    Collection<Plate> plates = dataProvider(design.destinationPlatesField).getItems();
    assertEquals(destinationPlates.size(), plates.size());
    assertTrue(plates.containsAll(destinationPlates));
    assertTrue(destinationPlates.containsAll(plates));
    design.destinationPlatesField.getNewItemHandler().accept("test new plate");
    assertEquals(null, design.destinationPlatesField.getValue().getId());
    assertEquals("test new plate", design.destinationPlatesField.getValue().getName());
    assertEquals(12 * 8, design.destinationPlatesField.getValue().getWells().size());
    plates = dataProvider(design.destinationPlatesField).getItems();
    assertEquals(destinationPlates.size() + 1, plates.size());
    assertTrue(find(plates, "test new plate").isPresent());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void down() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers) {
      design.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
    }

    ((ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL).getValueProvider()
        .apply(transfers.get(0))).setValue(plate.well(0, 0));
    design.down.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    int count = 0;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertEquals(plate.well(count++, 0), field.getValue());
    }

    ((ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL).getValueProvider()
        .apply(transfers.get(0))).setValue(plate.well(2, 3));
    design.down.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    count = 2;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertEquals(plate.well(count++, 3), field.getValue());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void down_NoWell() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers) {
      design.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
    }

    design.down.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertEquals(null, field.getValue());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void down_OrderedBySampleDesc() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);
    design.transfers.sort(SAMPLE, SortDirection.DESCENDING);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers) {
      design.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
    }

    ((ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL).getValueProvider()
        .apply(transfers.get(2))).setValue(plate.well(0, 0));
    design.down.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    int count = 0;
    for (TransferedSample ts : VaadinUtils.gridItems(design.transfers)
        .collect(Collectors.toList())) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertEquals(plate.well(count++, 0), field.getValue());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void down_OrderedBySourceDesc() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers) {
      design.transfers.getColumn(DESTINATION_WELL).getValueProvider().apply(ts);
    }
    design.transfers.sort(CONTAINER, SortDirection.DESCENDING);

    ((ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL).getValueProvider()
        .apply(transfers.get(2))).setValue(plate.well(0, 0));
    design.down.click();
    verify(view, never()).showError(any());
    verify(transferService, never()).insert(any());
    int count = 0;
    for (TransferedSample ts : VaadinUtils.gridItems(design.transfers)
        .collect(Collectors.toList())) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      assertEquals(plate.well(count++, 0), field.getValue());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void test_TubeToNewPlate() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    int count = 0;
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 0));
    }
    design.test.click();
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
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 3));
    }
    design.test.click();
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
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = sourceWells.get(0).getPlate();
    design.destinationPlatesField.setValue(plate);
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
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 2));
    }
    design.test.click();
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
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 3));
    }
    design.test.click();
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
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    design.test.click();
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
    design.test.click();
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
    Plate plate = destinationPlates.get(0);
    design.destinationPlatesField.setValue(plate);
    Plate plateCopy = new Plate();
    plateCopy.setName(plate.getName());
    plateCopy.setColumnCount(plate.getColumnCount());
    plateCopy.setRowCount(plate.getRowCount());
    plateCopy.initWells();
    plateCopy.getWells().stream()
        .forEach(well -> well.setSample(plate.well(well.getRow(), well.getColumn()).getSample()));
    when(plateService.get(any(Long.class))).thenReturn(plateCopy);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 2));
    design.test.click();
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
    design.test.click();
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
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    sourceWells.clear();
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0), sourcePlate.well(i, 1));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceWells));
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(plateService.get(any(Long.class))).thenReturn(null);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    design.test.click();
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
    design.test.click();
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
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    sourceWells.clear();
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0), sourcePlate.well(i, 1));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceWells));
    presenter.init(view);
    presenter.enter("");
    Plate plate = destinationPlates.get(0);
    design.destinationPlatesField.setValue(plate);
    Plate plateCopy = new Plate();
    plateCopy.setName(plate.getName());
    plateCopy.setColumnCount(plate.getColumnCount());
    plateCopy.setRowCount(plate.getRowCount());
    plateCopy.initWells();
    plateCopy.getWells().stream()
        .forEach(well -> well.setSample(plate.well(well.getRow(), well.getColumn()).getSample()));
    when(plateService.get(any(Long.class))).thenReturn(plateCopy);

    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 2));
    design.test.click();
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
    design.test.click();
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
  public void save_NoContainers() {
    when(view.savedContainers()).thenReturn(new ArrayList<>());
    presenter.init(view);
    presenter.enter("");

    design.save.click();

    verify(view).showError(resources.message(NO_CONTAINERS));
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_NoDestinationTube() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers.subList(1, transfers.size())) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.get(0));

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationTubeExists() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.iterator().next());
    field.setValue(new Tube(null, "test"));
    when(tubeService.get("test")).thenReturn(new Tube(null, "test"));

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, "test")),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
    verify(tubeService, atLeastOnce()).get("test");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationTubeDuplicate() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    ComboBox<Tube> field1 = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.get(0));
    field1.setValue(new Tube(null, "test"));
    ComboBox<Tube> field2 = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
        .getValueProvider().apply(transfers.get(1));
    field2.setValue(new Tube(null, "test"));

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_CONTAINER_DUPLICATE, TUBE.ordinal(), "test")),
        field2.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationWellUsed() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate destination = new Plate();
    destination.initWells();
    design.destinationPlatesField.setValue(destination);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    int count = 0;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(destination.well(count++, 0));
    }
    destination.well(0, 0).setId(2000L);
    Well used = new Well(0, 0);
    used.setSample(new SubmissionSample());
    when(wellService.get(2000L)).thenReturn(used);
    ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.iterator().next());

    design.save.click();

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
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate destination = new Plate();
    destination.initWells();
    design.destinationPlatesField.setValue(destination);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.iterator().next());

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        field.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_DestinationWellDuplicate() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate destination = new Plate();
    destination.initWells();
    design.destinationPlatesField.setValue(destination);
    List<TransferedSample> transfers = new ArrayList<>(dataProvider(design.transfers).getItems());
    int count = 0;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(destination.well(count++, 0));
    }
    ComboBox<Well> field1 = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.get(0));
    ComboBox<Well> field2 = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
        .getValueProvider().apply(transfers.get(1));
    field2.setValue(field1.getValue());

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(resources.message(DESTINATION_CONTAINER_DUPLICATE, WELL.ordinal(),
        field2.getValue().getName())), field2.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNoSelectedWell() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(resources.message(DESTINATION_PLATE_NO_SELECTION)),
        design.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNotEnoughFreeSpace_Overflow() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell())
        .thenReturn(plate.well(plate.getRowCount() - 2, plate.getColumnCount() - 1));

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size())),
        design.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNotEnoughFreeSpace_WellHasSample() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    plate.well(1, 0).setSample(samples.get(0));
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));

    design.save.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size())),
        design.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToTube() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(456L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceTubes.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Tube);
      assertEquals(sample.getName() + "_destination",
          transferedSample.getDestinationContainer().getName());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToNewPlate() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    int count = 0;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 0));
    }
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(457L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceTubes.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_TubeToExistingPlate() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    Plate plate = destinationPlates.get(0);
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    int count = 0;
    for (TransferedSample ts : transfers) {
      ComboBox<Well> field = (ComboBox<Well>) design.transfers.getColumn(DESTINATION_WELL)
          .getValueProvider().apply(ts);
      field.setValue(plate.well(count++, 4));
    }

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertNull(transfer.getId());
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceTubes.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  @Ignore("Not available for now")
  @SuppressWarnings("unchecked")
  public void save_PlateToTube() {
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(458L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceWells.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Tube);
      assertEquals("test" + i, transferedSample.getDestinationContainer().getName());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  public void save_PlateToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(459L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceWells.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  public void save_PlateToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    Plate plate = destinationPlates.get(0);
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(460L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), samples.size());
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(1, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourceWells.get(i), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  @Ignore("Not available for now")
  @SuppressWarnings("unchecked")
  public void save_PlateToTube_MultipleWellPerSample() {
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0), sourcePlate.well(i, 1));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(461L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(transfer.getTreatmentSamples().size(), sourceWells.size());
    int count = 0;
    for (int i = 0; i < samples.size(); i++) {
      Sample sample = samples.get(i);
      List<TransferedSample> transferedSamples = all(transfer.getTreatmentSamples(), sample);
      assertEquals(2, transferedSamples.size());
      TransferedSample transferedSample = transferedSamples.get(0);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourcePlate.well(i, 1), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, transferedSample.getDestinationContainer().getName());
      transferedSample = transferedSamples.get(1);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(sourcePlate.well(i, 3), transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, transferedSample.getDestinationContainer().getName());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  public void save_PlateToNewPlate_MultipleWellPerSample() {
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    sourceWells.clear();
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0), sourcePlate.well(i, 1));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceWells));
    presenter.init(view);
    presenter.enter("");
    Plate plate = new Plate(null, "test");
    plate.initWells();
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 0));
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(462L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(samples.size() * 2, transfer.getTreatmentSamples().size());
    Collections.sort(sourceWells, new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
    int count = 0;
    for (int i = 0; i < sourceWells.size(); i++) {
      Well source = sourceWells.get(i);
      Sample sample = source.getSample();
      TransferedSample transferedSample = transfer.getTreatmentSamples().get(i);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(source, transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  public void save_PlateToExistingPlate_MultipleWellPerSample() {
    Plate sourcePlate = entityManager.find(Plate.class, 26L);
    sourceWells.clear();
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      List<Well> wells = Arrays.asList(sourcePlate.well(i, 0), sourcePlate.well(i, 1));
      wells.stream().forEach(well -> well.setSample(sample));
      sourceWells.addAll(wells);
    });
    when(view.savedContainers()).thenReturn(new ArrayList<>(sourceWells));
    presenter.init(view);
    presenter.enter("");
    Plate plate = destinationPlates.get(0);
    design.destinationPlatesField.setValue(plate);
    when(view.destinationPlateForm.getSelectedWell()).thenReturn(plate.well(0, 4));
    doAnswer(i -> {
      Transfer transfer = i.getArgumentAt(0, Transfer.class);
      assertNull(transfer.getId());
      transfer.setId(463L);
      return null;
    }).when(transferService).insert(any());

    design.save.click();

    verify(view, never()).showError(any());
    verify(transferService).insert(transferCaptor.capture());
    Transfer transfer = transferCaptor.getValue();
    assertEquals(samples.size() * 2, transfer.getTreatmentSamples().size());
    Collections.sort(sourceWells, new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
    int count = 0;
    for (int i = 0; i < sourceWells.size(); i++) {
      Well source = sourceWells.get(i);
      Sample sample = source.getSample();
      TransferedSample transferedSample = transfer.getTreatmentSamples().get(i);
      assertEquals(sample, transferedSample.getSample());
      assertEquals(source, transferedSample.getContainer());
      assertTrue(transferedSample.getDestinationContainer() instanceof Well);
      Well destinationWell = (Well) transferedSample.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
    verify(view).showTrayNotification(resources.message(SAVED, samples.size()));
    verify(view).saveContainers(transfer.getTreatmentSamples().stream()
        .map(ts -> ts.getDestinationContainer()).collect(Collectors.toList()));
    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void save_IllegalArgumentException() {
    sourceTubes();
    presenter.init(view);
    presenter.enter("");
    design.type.setValue(TUBE);
    Collection<TransferedSample> transfers = dataProvider(design.transfers).getItems();
    for (TransferedSample ts : transfers) {
      ComboBox<Tube> field = (ComboBox<Tube>) design.transfers.getColumn(DESTINATION_TUBE)
          .getValueProvider().apply(ts);
      field.setValue(new Tube(null, ts.getSample().getName() + "_destination"));
    }
    doThrow(new IllegalArgumentException()).when(transferService).insert(any());

    design.save.click();

    verify(view).showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    verify(view, never()).showTrayNotification(any());
    verify(view, never()).navigateTo(any(), any());
  }

  @Test
  public void enter_Empty() {
    presenter.init(view);
    presenter.enter("");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    assertEquals(samples.size(), tss.getItems().size());
    for (Sample sample : samples) {
      assertTrue(tss.getItems().stream().filter(ts -> sample.equals(ts.getSample())).findAny()
          .isPresent());
    }
  }

  @Test
  public void enter_SavedContainersFromMultipleUsers() {
    when(view.savedContainersFromMultipleUsers()).thenReturn(true);
    presenter.init(view);
    presenter.enter("");

    verify(view).showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    assertEquals(samples.size(), tss.getItems().size());
    for (Sample sample : samples) {
      assertTrue(tss.getItems().stream().filter(ts -> sample.equals(ts.getSample())).findAny()
          .isPresent());
    }
  }

  @Test
  public void enter_Transfer() {
    presenter = new TransferViewPresenter(transferService, realTubeService, realWellService,
        realPlateService, sampleContainerService, applicationName);
    Transfer transfer = entityManager.find(Transfer.class, 3L);
    when(transferService.get(any())).thenReturn(transfer);
    presenter.init(view);
    presenter.enter("3");

    verify(transferService).get(3L);
    assertFalse(design.typePanel.isVisible());
    assertTrue(design.transfersPanel.isVisible());
    assertFalse(design.destination.isVisible());
    assertFalse(design.down.isVisible());
    assertFalse(design.save.isVisible());
    List<TransferedSample> tss = new ArrayList<>(dataProvider(design.transfers).getItems());
    assertEquals(transfer.getTreatmentSamples().size(), tss.size());
    for (int i = 0; i < transfer.getTreatmentSamples().size(); i++) {
      assertEquals(transfer.getTreatmentSamples().get(i), tss.get(i));
    }
  }

  @Test
  public void enter_TransferNotId() {
    presenter.init(view);
    presenter.enter("a");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    verify(view).showWarning(resources.message(INVALID_TRANSFER));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_TransferIdNotExists() {
    presenter.init(view);
    presenter.enter("3");

    verify(transferService).get(3L);
    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    verify(view).showWarning(resources.message(INVALID_TRANSFER));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_Containers() {
    when(sampleContainerService.get(any())).thenAnswer(i -> {
      Long id = i.getArgumentAt(0, Long.class);
      return id != null ? entityManager.find(SampleContainer.class, id) : null;
    });
    List<SampleContainer> containers = new ArrayList<>();
    containers.add(entityManager.find(SampleContainer.class, 11L));
    containers.add(entityManager.find(SampleContainer.class, 12L));
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    assertEquals(containers.size(), tss.getItems().size());
    for (SampleContainer container : containers) {
      assertTrue(tss.getItems().stream().filter(ts -> container == ts.getContainer()).findAny()
          .isPresent());
    }
  }

  @Test
  public void enter_ContainersEmpty() {
    presenter.init(view);
    presenter.enter("containers/");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersNotId() {
    presenter.init(view);
    presenter.enter("containers/11,a");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }

  @Test
  public void enter_ContainersIdNotExists() {
    when(sampleContainerService.get(any())).thenReturn(null);
    presenter.init(view);
    presenter.enter("containers/11,12");

    ListDataProvider<TransferedSample> tss = dataProvider(design.transfers);
    verify(view).showWarning(resources.message(INVALID_CONTAINERS));
    assertTrue(tss.getItems().isEmpty());
  }
}
