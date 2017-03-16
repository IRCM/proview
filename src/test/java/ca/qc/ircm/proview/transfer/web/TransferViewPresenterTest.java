package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES_TYPE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_NO_SELECTION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_TYPES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_SAMPLE_NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TABS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE_COLUMNS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE_NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.HEADER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SAVE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_EMPTY;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_EMPTY_WELL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_SAMPLE_NOT_SELECTED;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TABS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TUBES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TUBE_COLUMNS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TITLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TUBE;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.plate.PlateSpotService;
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.plate.SpotLocation;
import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.plate.web.PlateComponentPresenter;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.TextField;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
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
  private PlateSpotService plateSpotService;
  @Captor
  private ArgumentCaptor<Collection<PlateSpot>> wellsCaptor;
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
  private Map<Sample, Map<Plate, List<PlateSpot>>> sourceWells = new LinkedHashMap<>();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new TransferViewPresenter(transferService, tubeService, plateSpotService,
        plateService, applicationName);
    view.headerLabel = new Label();
    view.sourceHeaderLabel = new Label();
    view.sourceTabs = new TabSheet();
    view.sourceTubesGrid = new Grid();
    view.sourceTabs.addComponent(view.sourceTubesGrid);
    view.sourcePlateLayout = new VerticalLayout();
    view.sourceTabs.addComponent(view.sourcePlateLayout);
    view.sourcePlatesField = new ComboBox();
    view.sourcePlatePanel = new Panel();
    view.sourcePlateFormLayout = new VerticalLayout();
    view.sourcePlateForm = new PlateComponent();
    view.sourcePlateFormPresenter = mock(PlateComponentPresenter.class);
    view.destinationHeaderLabel = new Label();
    view.destinationTabs = new TabSheet();
    view.destinationTubesGrid = new Grid();
    view.destinationTabs.addComponent(view.destinationTubesGrid);
    view.destinationPlateLayout = new VerticalLayout();
    view.destinationTabs.addComponent(view.destinationPlateLayout);
    view.destinationPlatesField = new ComboBox();
    view.destinationPlatesTypeField = new ComboBox();
    view.destinationPlatePanel = new Panel();
    view.destinationPlateFormLayout = new VerticalLayout();
    view.destinationPlateForm = new PlateComponent();
    view.destinationPlateFormPresenter = mock(PlateComponentPresenter.class);
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
    sourcePlates.forEach(plate -> plate.initSpots());
    when(plateService.all(any())).thenReturn(sourcePlates);
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      Map<Plate, List<PlateSpot>> wellsMap = new HashMap<>();
      List<PlateSpot> wells = Arrays.asList(sourcePlates.get(0).spot(i, 0));
      wells.stream().forEach(well -> well.setSample(sample));
      wellsMap.put(sourcePlates.get(0), wells);
      wells = Arrays.asList(sourcePlates.get(1).spot(i, 1), sourcePlates.get(1).spot(i, 3));
      wells.stream().forEach(well -> well.setSample(sample));
      wellsMap.put(sourcePlates.get(1), wells);
      sourceWells.put(sample, wellsMap);
    });
    when(plateSpotService.location(any(), any())).thenAnswer(i -> i.getArguments()[0] != null
        ? sourceWells.get(i.getArguments()[0]).get(i.getArguments()[1]) : null);
    when(plateSpotService.last(any()))
        .thenAnswer(i -> i.getArguments()[0] != null && !sourcePlates.isEmpty()
            ? sourceWells.get(i.getArguments()[0]).get(sourcePlates.get(0)).get(0) : null);
  }

  private List<Tube> generateTubes(Sample sample, int count) {
    List<Tube> tubes = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Tube tube = new Tube();
      tube.setId(sample.getId() * 100 + i);
      tube.setName(sample.getName() + "_" + i);
      tube.setSample(sample);
      tubes.add(tube);
    }
    return tubes;
  }

  private <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(d -> d.getId() == id).findFirst();
  }

  private List<SampleTransfer> all(Collection<SampleTransfer> datas, Sample sample) {
    return datas.stream().filter(d -> sample.equals(d.getSample())).collect(Collectors.toList());
  }

  private String errorMessage(String message) {
    return new CompositeErrorMessage(new UserError(message)).getFormattedHtmlMessage();
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.headerLabel.getStyleName().contains(ValoTheme.LABEL_H1));
    assertTrue(view.sourceHeaderLabel.getStyleName().contains(SOURCE));
    assertTrue(view.sourceHeaderLabel.getStyleName().contains(ValoTheme.LABEL_H2));
    assertTrue(view.sourceTabs.getStyleName().contains(SOURCE_TABS));
    assertTrue(view.sourceTabs.getStyleName().contains(ValoTheme.TABSHEET_FRAMED));
    assertTrue(view.sourceTabs.getStyleName().contains(ValoTheme.TABSHEET_PADDED_TABBAR));
    assertTrue(view.sourceTubesGrid.getStyleName().contains(SOURCE_TUBES));
    assertTrue(view.sourceTubesGrid.getStyleName().contains(COMPONENTS));
    assertTrue(view.sourcePlatesField.getStyleName().contains(SOURCE_PLATES));
    assertTrue(view.sourcePlatePanel.getStyleName().contains(SOURCE_PLATE_PANEL));
    assertTrue(view.sourcePlateForm.getStyleName().contains(SOURCE_PLATE));
    assertTrue(view.destinationHeaderLabel.getStyleName().contains(DESTINATION));
    assertTrue(view.destinationHeaderLabel.getStyleName().contains(ValoTheme.LABEL_H2));
    assertTrue(view.destinationTabs.getStyleName().contains(DESTINATION_TABS));
    assertTrue(view.destinationTabs.getStyleName().contains(ValoTheme.TABSHEET_FRAMED));
    assertTrue(view.destinationTabs.getStyleName().contains(ValoTheme.TABSHEET_PADDED_TABBAR));
    assertTrue(view.destinationTubesGrid.getStyleName().contains(DESTINATION_TUBES));
    assertTrue(view.destinationPlatesTypeField.getStyleName().contains(DESTINATION_PLATES_TYPE));
    assertTrue(view.destinationPlatesField.getStyleName().contains(DESTINATION_PLATES));
    assertTrue(view.destinationPlatePanel.getStyleName().contains(DESTINATION_PLATE_PANEL));
    assertTrue(view.destinationPlateForm.getStyleName().contains(DESTINATION_PLATE));
    assertTrue(view.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    assertEquals(resources.message(SOURCE), view.sourceHeaderLabel.getValue());
    assertEquals(resources.message(SOURCE_TUBES),
        view.sourceTabs.getTab(view.sourceTubesGrid).getCaption());
    for (Object propertyId : SOURCE_TUBE_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.sourceTubesGrid.getColumn(propertyId).getHeaderCaption());
    }
    assertEquals(resources.message(SOURCE_PLATE),
        view.sourceTabs.getTab(view.sourcePlateLayout).getCaption());
    assertEquals(resources.message(SOURCE_PLATES), view.sourcePlatesField.getCaption());
    assertEquals(resources.message(DESTINATION), view.destinationHeaderLabel.getValue());
    assertEquals(resources.message(DESTINATION_TUBES),
        view.destinationTabs.getTab(view.destinationTubesGrid).getCaption());
    for (Object propertyId : DESTINATION_TUBE_COLUMNS) {
      assertEquals(resources.message((String) propertyId),
          view.destinationTubesGrid.getColumn(propertyId).getHeaderCaption());
    }
    assertEquals(resources.message(DESTINATION_PLATE),
        view.destinationTabs.getTab(view.destinationPlateLayout).getCaption());
    assertEquals(resources.message(DESTINATION_PLATES_TYPE),
        view.destinationPlatesTypeField.getCaption());
    for (PlateType type : DESTINATION_PLATE_TYPES) {
      assertEquals(type.getLabel(locale), view.destinationPlatesTypeField.getItemCaption(type));
    }
    assertEquals(resources.message(DESTINATION_PLATES), view.destinationPlatesField.getCaption());
    assertEquals(resources.message(SAVE), view.saveButton.getCaption());
  }

  @Test
  public void sourceTubesColumns() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(NAME, view.sourceTubesGrid.getColumns().get(0).getPropertyId());
    assertEquals(TUBE, view.sourceTubesGrid.getColumns().get(1).getPropertyId());
    assertTrue(view.sourceTubesGrid.getColumns().get(1).getRenderer() instanceof ComponentRenderer);
    Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
    Sample sample = samples.get(0);
    ComboBox comboBox = (ComboBox) container.getItem(sample).getItemProperty(TUBE).getValue();
    assertEquals(sourceTubes.get(sample).size(), comboBox.getItemIds().size());
    assertTrue(sourceTubes.get(sample).containsAll(comboBox.getItemIds()));
    assertTrue(comboBox.getItemIds().containsAll(sourceTubes.get(sample)));
    for (Tube tube : sourceTubes.get(sample)) {
      assertEquals(tube.getName(), comboBox.getItemCaption(tube));
    }
  }

  @Test
  public void sourcePlatesFieldItems() {
    presenter.init(view);
    presenter.enter("");

    Collection<?> itemIds = view.sourcePlatesField.getItemIds();
    assertEquals(sourcePlates.size(), itemIds.size());
    assertTrue(sourcePlates.containsAll(itemIds));
    assertTrue(itemIds.containsAll(sourcePlates));
    for (Plate plate : sourcePlates) {
      assertEquals(plate.getName(), view.sourcePlatesField.getItemCaption(plate));
    }
    assertEquals(sourcePlates.get(0), view.sourcePlatesField.getValue());
  }

  @Test
  public void sourcePlatesFieldItems_NoLastWell() {
    when(plateSpotService.last(any())).thenReturn(null);

    presenter.init(view);
    presenter.enter("");

    Collection<?> itemIds = view.sourcePlatesField.getItemIds();
    assertEquals(sourcePlates.size(), itemIds.size());
    assertTrue(sourcePlates.containsAll(itemIds));
    assertTrue(itemIds.containsAll(sourcePlates));
    for (Plate plate : sourcePlates) {
      assertEquals(plate.getName(), view.sourcePlatesField.getItemCaption(plate));
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
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setMultiSelect(true);
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setPlate(plate);
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setSelectedSpots(wellsCaptor.capture());
    Collection<PlateSpot> wells = wellsCaptor.getValue();
    assertEquals(samples.size() * 2, wells.size());
    for (Map<Plate, List<PlateSpot>> sampleWells : sourceWells.values()) {
      for (PlateSpot well : sampleWells.get(plate)) {
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
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setMultiSelect(true);
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setPlate(plate);
    verify(view.sourcePlateFormPresenter, atLeastOnce()).setSelectedSpots(wellsCaptor.capture());
    Collection<PlateSpot> wells = wellsCaptor.getValue();
    assertEquals(samples.size(), wells.size());
    for (Map<Plate, List<PlateSpot>> sampleWells : sourceWells.values()) {
      for (PlateSpot well : sampleWells.get(plate)) {
        assertTrue(find(wells, well.getId()).isPresent());
      }
    }
  }

  @Test
  public void destinationTubesColumns() {
    presenter.init(view);
    presenter.enter("");

    assertEquals(DESTINATION_SAMPLE_NAME,
        view.destinationTubesGrid.getColumns().get(0).getPropertyId());
    assertEquals(DESTINATION_TUBE_NAME,
        view.destinationTubesGrid.getColumns().get(1).getPropertyId());
    assertTrue(
        view.destinationTubesGrid.getColumns().get(1).getRenderer() instanceof ComponentRenderer);
    Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
    Object itemId = container.getIdByIndex(0);
    Object rawTubeNameField =
        container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();
    assertTrue(rawTubeNameField instanceof TextField);
    TextField tubeNameField = (TextField) rawTubeNameField;
    assertTrue(tubeNameField.isRequired());
    assertEquals(generalResources.message(REQUIRED), tubeNameField.getRequiredError());
  }

  @Test
  public void destinationPlateTypeField() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(view.destinationPlatesTypeField.isNullSelectionAllowed());
    assertFalse(view.destinationPlatesTypeField.isNewItemsAllowed());
    Collection<?> itemIds = view.destinationPlatesTypeField.getItemIds();
    assertEquals(DESTINATION_PLATE_TYPES.length, itemIds.size());
    for (PlateType type : DESTINATION_PLATE_TYPES) {
      assertTrue(itemIds.contains(type));
    }
    assertEquals(PlateType.A, view.destinationPlatesTypeField.getValue());
    assertTrue(view.destinationPlatesTypeField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.destinationPlatesTypeField.getRequiredError());
  }

  @Test
  public void destinationPlateTypeField_Change() {
    presenter.init(view);
    presenter.enter("");
    sourcePlates.clear();
    sourcePlates.add(entityManager.find(Plate.class, 109L));
    sourcePlates.add(entityManager.find(Plate.class, 110L));

    view.destinationPlatesTypeField.setValue(PlateType.G);

    Collection<?> itemIds = view.destinationPlatesField.getItemIds();
    List<String> plateNames =
        sourcePlates.stream().map(p -> p.getName()).collect(Collectors.toList());
    assertEquals(sourcePlates.size(), itemIds.size());
    assertTrue(itemIds.containsAll(plateNames));
    assertTrue(plateNames.containsAll(itemIds));
  }

  @Test
  public void destinationPlatesField() {
    presenter.init(view);
    presenter.enter("");

    assertFalse(view.destinationPlatesField.isNullSelectionAllowed());
    assertTrue(view.destinationPlatesField.isNewItemsAllowed());
    assertTrue(view.destinationPlatesField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.destinationPlatesField.getRequiredError());
    Collection<?> itemIds = view.destinationPlatesField.getItemIds();
    List<String> plateNames =
        sourcePlates.stream().map(p -> p.getName()).collect(Collectors.toList());
    assertEquals(sourcePlates.size(), itemIds.size());
    assertTrue(itemIds.containsAll(plateNames));
    assertTrue(plateNames.containsAll(itemIds));
  }

  @Test
  public void save_NoSourceTube() {
    Sample sample = samples.get(0);
    sourceTubes.put(sample, new ArrayList<>());
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
    ComboBox comboBox = (ComboBox) container.getItem(sample).getItemProperty(TUBE).getValue();

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
    view.sourceTabs.setSelectedTab(1);

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
    view.sourceTabs.setSelectedTab(1);
    Plate plate = sourcePlates.get(0);
    when(view.sourcePlateFormPresenter.getSelectedSpots())
        .thenReturn(plate.spots(new SpotLocation(0, 2), new SpotLocation(0, 3)));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(SOURCE_PLATE_EMPTY_WELL, plate.spot(0, 2).getName())),
        view.sourcePlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_SourcePlateMissingOneSample() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate plate = sourcePlates.get(0);
    when(view.sourcePlateFormPresenter.getSelectedSpots())
        .thenReturn(plate.spots(new SpotLocation(1, 0), new SpotLocation(samples.size() - 1, 0)));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, samples.get(0).getName())),
        view.sourcePlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_NoDestinationTube() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
    Object itemId = container.getIdByIndex(0);
    TextField textField =
        (TextField) container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        textField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationTubeExists() {
    presenter.init(view);
    presenter.enter("");
    Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
    Object itemId = container.getIdByIndex(0);
    TextField textField =
        (TextField) container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();
    textField.setValue("test");
    when(tubeService.get("test")).thenReturn(new Tube(null, "test"));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        textField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_DestinationPlateNoSelectedWell() {
    presenter.init(view);
    presenter.enter("");
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      container.getItem(sample).getItemProperty(TUBE).getValue();
    });
    view.destinationTabs.setSelectedTab(1);
    view.destinationPlatesField.addItem("test");
    view.destinationPlatesField.setValue("test");

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
    view.destinationTabs.setSelectedTab(1);
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      container.getItem(sample).getItemProperty(TUBE).getValue();
    });
    Plate plate = new Plate(null, "test");
    plate.setType(PlateType.A);
    plate.initSpots();
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot())
        .thenReturn(plate.spot(plate.getRowCount() - 2, plate.getColumnCount() - 1));

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
    view.destinationTabs.setSelectedTab(1);
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      container.getItem(sample).getItemProperty(TUBE).getValue();
    });
    Plate plate = new Plate(null, "test");
    plate.setType(PlateType.A);
    plate.initSpots();
    plate.spot(1, 0).setSample(samples.get(0));
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 0));

    view.saveButton.click();

    verify(view).showError(generalResources.message(FIELD_NOTIFICATION));
    assertEquals(
        errorMessage(resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size())),
        view.destinationPlatesField.getErrorMessage().getFormattedHtmlMessage());
    verify(transferService, never()).insert(any());
  }

  @Test
  public void save_TubeToTube() {
    presenter.init(view);
    presenter.enter("");
    Map<Sample, Tube> sources = new HashMap<>();
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      ComboBox comboBox = (ComboBox) container.getItem(sample).getItemProperty(TUBE).getValue();
      sources.put(sample, (Tube) comboBox.getValue());
    });
    IntStream.range(0, samples.size()).forEach(i -> {
      Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
      Object itemId = container.getIdByIndex(i);
      TextField textField =
          (TextField) container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();
      textField.setValue("test" + i);
    });

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
      assertEquals("test" + i, sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  public void save_TubeToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    Map<Sample, Tube> sources = new HashMap<>();
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      ComboBox comboBox = (ComboBox) container.getItem(sample).getItemProperty(TUBE).getValue();
      sources.put(sample, (Tube) comboBox.getValue());
    });
    view.destinationTabs.setSelectedTab(1);
    Plate plate = new Plate(null, "test");
    plate.setType(PlateType.A);
    plate.initSpots();
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 0));

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
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  public void save_TubeToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    Map<Sample, Tube> sources = new HashMap<>();
    samples.forEach(sample -> {
      Container.Indexed container = view.sourceTubesGrid.getContainerDataSource();
      ComboBox comboBox = (ComboBox) container.getItem(sample).getItemProperty(TUBE).getValue();
      sources.put(sample, (Tube) comboBox.getValue());
    });
    view.destinationTabs.setSelectedTab(1);
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 4));

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
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }

  @Test
  public void save_PlateToTube() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(
        sourcePlate.spots(new SpotLocation(0, 0), new SpotLocation(samples.size() - 1, 0)));
    IntStream.range(0, samples.size()).forEach(i -> {
      Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
      Object itemId = container.getIdByIndex(i);
      TextField textField =
          (TextField) container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();
      textField.setValue("test" + i);
    });

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
      assertEquals(sourcePlate.spot(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + i, sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  public void save_PlateToNewPlate() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(
        sourcePlate.spots(new SpotLocation(0, 0), new SpotLocation(samples.size() - 1, 0)));
    view.destinationTabs.setSelectedTab(1);
    Plate plate = new Plate(null, "test");
    plate.setType(PlateType.A);
    plate.initSpots();
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 0));

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
      assertEquals(sourcePlate.spot(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  public void save_PlateToExistingPlate() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(0);
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(
        sourcePlate.spots(new SpotLocation(0, 0), new SpotLocation(samples.size() - 1, 0)));
    view.destinationTabs.setSelectedTab(1);
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 4));

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
      assertEquals(sourcePlate.spot(i, 0), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(i, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }

  @Test
  @Ignore("Fix this test later since we never transfer from plate to tubes")
  public void save_PlateToTube_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(1);
    List<PlateSpot> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(sourceWells);
    IntStream.range(0, samples.size()).forEach(i -> {
      Container.Indexed container = view.destinationTubesGrid.getContainerDataSource();
      Object itemId = container.getIdByIndex(i);
      TextField textField =
          (TextField) container.getItem(itemId).getItemProperty(DESTINATION_TUBE_NAME).getValue();
      textField.setValue("test" + i);
    });

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
      assertEquals(sourcePlate.spot(i, 1), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, sampleTransfer.getDestinationContainer().getName());
      sampleTransfer = sampleTransfers.get(1);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.spot(i, 3), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof Tube);
      assertEquals("test" + count++, sampleTransfer.getDestinationContainer().getName());
    }
  }

  @Test
  public void save_PlateToNewPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(1);
    List<PlateSpot> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(sourceWells);
    view.destinationTabs.setSelectedTab(1);
    Plate plate = new Plate(null, "test");
    plate.setType(PlateType.A);
    plate.initSpots();
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 0));

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
      assertEquals(sourcePlate.spot(i, 1), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
      sampleTransfer = sampleTransfers.get(1);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.spot(i, 3), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(0, destinationWell.getColumn());
    }
  }

  @Test
  public void save_PlateToExistingPlate_MultipleWellPerSample() {
    presenter.init(view);
    presenter.enter("");
    view.sourceTabs.setSelectedTab(1);
    Plate sourcePlate = sourcePlates.get(1);
    List<PlateSpot> sourceWells = this.sourceWells.values().stream()
        .flatMap(map -> map.get(sourcePlate).stream()).collect(Collectors.toList());
    when(view.sourcePlateFormPresenter.getSelectedSpots()).thenReturn(sourceWells);
    view.destinationTabs.setSelectedTab(1);
    Plate plate = sourcePlates.get(0);
    view.destinationPlatesField.addItem(plate.getName());
    view.destinationPlatesField.setValue(plate.getName());
    when(view.destinationPlateFormPresenter.getPlate()).thenReturn(plate);
    when(view.destinationPlateFormPresenter.getSelectedSpot()).thenReturn(plate.spot(0, 4));

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
      assertEquals(sourcePlate.spot(i, 1), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      PlateSpot destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
      sampleTransfer = sampleTransfers.get(1);
      assertEquals(sample, sampleTransfer.getSample());
      assertEquals(sourcePlate.spot(i, 3), sampleTransfer.getContainer());
      assertTrue(sampleTransfer.getDestinationContainer() instanceof PlateSpot);
      destinationWell = (PlateSpot) sampleTransfer.getDestinationContainer();
      assertEquals(plate, destinationWell.getPlate());
      assertEquals(count++, destinationWell.getRow());
      assertEquals(4, destinationWell.getColumn());
    }
  }
}
