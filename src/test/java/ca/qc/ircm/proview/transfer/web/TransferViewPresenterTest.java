package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATES_TYPE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_SAMPLE_NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TABS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE_COLUMNS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.DESTINATION_TUBE_NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.HEADER;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.NAME;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_PLATE_PANEL;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TABS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TUBES;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.SOURCE_TUBE_COLUMNS;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TITLE;
import static ca.qc.ircm.proview.transfer.web.TransferViewPresenter.TUBE;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.plate.PlateSpotService;
import ca.qc.ircm.proview.plate.web.PlateComponent;
import ca.qc.ircm.proview.plate.web.PlateComponentPresenter;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.junit.Before;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
  private TubeService tubeService;
  @Mock
  private PlateService plateService;
  @Mock
  private PlateSpotService plateSpotService;
  @Captor
  private ArgumentCaptor<Collection<PlateSpot>> wellsCaptor;
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
  private Map<Sample, Map<Plate, List<PlateSpot>>> sourceWells = new HashMap<>();

  @Before
  public void beforeTest() {
    presenter =
        new TransferViewPresenter(tubeService, plateSpotService, plateService, applicationName);
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
    when(plateService.all(any())).thenReturn(sourcePlates);
    IntStream.range(0, samples.size()).forEach(i -> {
      Sample sample = samples.get(i);
      Map<Plate, List<PlateSpot>> wells = new HashMap<>();
      wells.put(sourcePlates.get(0), Arrays.asList(sourcePlates.get(0).spot(i, 0)));
      wells.put(sourcePlates.get(1),
          Arrays.asList(sourcePlates.get(1).spot(i, 1), sourcePlates.get(1).spot(i, 3)));
      sourceWells.put(sample, wells);
    });
    when(plateSpotService.location(any(), any()))
        .thenAnswer(i -> sourceWells.get(i.getArguments()[0]).get(i.getArguments()[1]));
    when(plateSpotService.last(any()))
        .thenAnswer(i -> sourceWells.get(i.getArguments()[0]).get(sourcePlates.get(0)).get(0));
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
    assertEquals(resources.message(DESTINATION_PLATES), view.destinationPlatesField.getCaption());
    assertEquals(resources.message(DESTINATION_PLATES_TYPE),
        view.destinationPlatesTypeField.getCaption());
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
}
