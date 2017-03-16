package ca.qc.ircm.proview.transfer.web;

import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.tube.QTube.tube;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilterBuilder;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.plate.PlateSpotService;
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.BeanFieldGroup;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertyValueGenerator;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Sample transfer view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferViewPresenter {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SOURCE = "source";
  public static final String SOURCE_TABS = "sourceTabs";
  public static final String SOURCE_TUBES = "sourceTubes";
  public static final String SOURCE_PLATES = "sourcePlates";
  public static final String SOURCE_PLATE_PANEL = "sourcePlatePanel";
  public static final String SOURCE_PLATE = "sourcePlate";
  public static final String SOURCE_PLATE_EMPTY = "sourcePlate.empty";
  public static final String SOURCE_PLATE_EMPTY_WELL = "sourcePlate.emptyWell";
  public static final String SOURCE_PLATE_SAMPLE_NOT_SELECTED = "sourcePlate.sampleNotSelected";
  public static final String DESTINATION = "destination";
  public static final String DESTINATION_TABS = "destinationTabs";
  public static final String DESTINATION_TUBES = "destinationTubes";
  public static final String DESTINATION_PLATES = "destinationPlates";
  public static final String DESTINATION_PLATES_TYPE = "destinationPlatesType";
  public static final String DESTINATION_PLATE_PANEL = "destinationPlatePanel";
  public static final String DESTINATION_PLATE = "destinationPlate";
  public static final String DESTINATION_PLATE_NO_SELECTION = "destinationPlate.noSelection";
  public static final String DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE =
      "destinationPlate.notEnoughFreeSpace";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String NAME = sample.name.getMetadata().getName();
  public static final String TUBE = tube.getMetadata().getName();
  public static final String PLATE = plate.getMetadata().getName();
  public static final String PLATE_TYPE = plate.type.getMetadata().getName();
  public static final String PLATE_NAME = plate.name.getMetadata().getName();
  public static final String DESTINATION_SAMPLE = sample.getMetadata().getName();
  public static final String DESTINATION_SAMPLE_NAME =
      DESTINATION_SAMPLE + "." + sample.name.getMetadata().getName();
  public static final String DESTINATION_TUBE_NAME = "tubeName";
  static final Object[] SOURCE_TUBE_COLUMNS = new Object[] { NAME, TUBE };
  static final Object[] DESTINATION_TUBE_COLUMNS =
      new Object[] { DESTINATION_SAMPLE_NAME, DESTINATION_TUBE_NAME };
  static final PlateType[] DESTINATION_PLATE_TYPES =
      new PlateType[] { PlateType.A, PlateType.G, PlateType.PM };
  private static final Logger logger = LoggerFactory.getLogger(TransferViewPresenter.class);
  private TransferView view;
  private ObjectProperty<List<Sample>> samplesProperty = new ObjectProperty<>(new ArrayList<>());
  private Map<Object, ComboBox> sourceTubeFields = new HashMap<>();
  private Map<Object, TextField> destinationTubeFields = new HashMap<>();
  private BeanFieldGroup<Plate> destinationPlateFieldGroup = new BeanFieldGroup<>(Plate.class);
  private BeanItemContainer<Sample> sourceTubesContainer = new BeanItemContainer<>(Sample.class);
  private GeneratedPropertyContainer sourceTubesGeneratedContainer =
      new GeneratedPropertyContainer(sourceTubesContainer);
  private BeanItemContainer<DestinationTube> destinationTubesContainer =
      new BeanItemContainer<>(DestinationTube.class);
  private GeneratedPropertyContainer destinationTubesGeneratedContainer =
      new GeneratedPropertyContainer(destinationTubesContainer);
  @Inject
  private TransferService transferService;
  @Inject
  private TubeService tubeService;
  @Inject
  private PlateSpotService plateSpotService;
  @Inject
  private PlateService plateService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected TransferViewPresenter() {
  }

  protected TransferViewPresenter(TransferService transferService, TubeService tubeService,
      PlateSpotService plateSpotService, PlateService plateService, String applicationName) {
    this.transferService = transferService;
    this.tubeService = tubeService;
    this.plateSpotService = plateSpotService;
    this.plateService = plateService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(TransferView view) {
    logger.debug("Transfer view");
    this.view = view;
    prepareComponents();
    bindFields();
    addListeners();
    updateDestinationPlateType();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message("title", applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
    view.sourceHeaderLabel.addStyleName(SOURCE);
    view.sourceHeaderLabel.addStyleName(ValoTheme.LABEL_H2);
    view.sourceHeaderLabel.setValue(resources.message(SOURCE));
    view.sourceTabs.addStyleName(SOURCE_TABS);
    view.sourceTabs.addStyleName(ValoTheme.TABSHEET_FRAMED);
    view.sourceTabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    view.sourceTabs.getTab(view.sourceTubesGrid).setCaption(resources.message(SOURCE_TUBES));
    view.sourceTabs.getTab(view.sourcePlateLayout).setCaption(resources.message(SOURCE_PLATE));
    prepareSourceTubesGrid();
    view.sourcePlatesField.addStyleName(SOURCE_PLATES);
    view.sourcePlatesField.setCaption(resources.message(SOURCE_PLATES));
    view.sourcePlatesField.setNullSelectionAllowed(false);
    view.sourcePlatePanel.addStyleName(SOURCE_PLATE_PANEL);
    view.sourcePlatePanel.setVisible(false);
    view.sourcePlateForm.addStyleName(SOURCE_PLATE);
    view.sourcePlateFormPresenter.setMultiSelect(true);
    view.destinationHeaderLabel.addStyleName(DESTINATION);
    view.destinationHeaderLabel.addStyleName(ValoTheme.LABEL_H2);
    view.destinationHeaderLabel.setValue(resources.message(DESTINATION));
    view.destinationTabs.addStyleName(DESTINATION_TABS);
    view.destinationTabs.addStyleName(ValoTheme.TABSHEET_FRAMED);
    view.destinationTabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
    view.destinationTabs.getTab(view.destinationTubesGrid)
        .setCaption(resources.message(DESTINATION_TUBES));
    view.destinationTabs.getTab(view.destinationPlateLayout)
        .setCaption(resources.message(DESTINATION_PLATE));
    prepareDestinationTubesGrid();
    view.destinationPlatesTypeField.setNullSelectionAllowed(false);
    view.destinationPlatesTypeField.setNewItemsAllowed(false);
    view.destinationPlatesTypeField.addStyleName(DESTINATION_PLATES_TYPE);
    view.destinationPlatesTypeField.setCaption(resources.message(DESTINATION_PLATES_TYPE));
    for (PlateType type : DESTINATION_PLATE_TYPES) {
      view.destinationPlatesTypeField.addItem(type);
      view.destinationPlatesTypeField.setItemCaption(type, type.getLabel(locale));
    }
    view.destinationPlatesTypeField.setRequired(true);
    view.destinationPlatesTypeField.setRequiredError(generalResources.message(REQUIRED));
    view.destinationPlatesField.addStyleName(DESTINATION_PLATES);
    view.destinationPlatesField.setCaption(resources.message(DESTINATION_PLATES));
    view.destinationPlatesField.setNullSelectionAllowed(false);
    view.destinationPlatesField.setNewItemsAllowed(true);
    view.destinationPlatesField.setRequired(true);
    view.destinationPlatesField.setRequiredError(generalResources.message(REQUIRED));
    view.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    view.destinationPlatePanel.setVisible(false);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    Plate destinationPlate = new Plate();
    destinationPlate.setType(PlateType.A);
    destinationPlateFieldGroup.setItemDataSource(destinationPlate);
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  @SuppressWarnings("serial")
  private void prepareSourceTubesGrid() {
    final MessageResource resources = view.getResources();
    sourceTubesGeneratedContainer.addGeneratedProperty(TUBE,
        new PropertyValueGenerator<ComboBox>() {
          @Override
          public ComboBox getValue(Item item, Object itemId, Object propertyId) {
            Sample sample = (Sample) itemId;
            ComboBox comboBox = prepareSourceTubeComboBox(sample);
            return comboBox;
          }

          @Override
          public Class<ComboBox> getType() {
            return ComboBox.class;
          }
        });
    view.sourceTubesGrid.addStyleName(SOURCE_TUBES);
    view.sourceTubesGrid.addStyleName(COMPONENTS);
    view.sourceTubesGrid.setContainerDataSource(sourceTubesGeneratedContainer);
    view.sourceTubesGrid.setColumns(SOURCE_TUBE_COLUMNS);
    view.sourceTubesGrid.getColumns().forEach(
        column -> column.setHeaderCaption(resources.message((String) column.getPropertyId())));
    view.sourceTubesGrid.getColumn(TUBE).setRenderer(new ComponentRenderer());
  }

  private ComboBox prepareSourceTubeComboBox(Sample sample) {
    final MessageResource generalResources = view.getGeneralResources();
    ComboBox comboBox = new ComboBox();
    comboBox.addStyleName(TUBE);
    comboBox.setNullSelectionAllowed(false);
    comboBox.setNewItemsAllowed(false);
    tubeService.all(sample).forEach(tube -> {
      comboBox.addItem(tube);
      comboBox.setItemCaption(tube, tube.getName());
    });
    comboBox.setRequired(true);
    comboBox.setRequiredError(generalResources.message(REQUIRED));
    if (!comboBox.getItemIds().isEmpty()) {
      comboBox.setValue(comboBox.getItemIds().iterator().next());
    }
    sourceTubeFields.put(sample, comboBox);
    return comboBox;
  }

  private void prepareDestinationTubesGrid() {
    final MessageResource resources = view.getResources();
    destinationTubesContainer.addNestedContainerBean(DESTINATION_SAMPLE);
    view.destinationTubesGrid.addStyleName(DESTINATION_TUBES);
    view.destinationTubesGrid.addStyleName(COMPONENTS);
    view.destinationTubesGrid.setContainerDataSource(destinationTubesGeneratedContainer);
    view.destinationTubesGrid.setColumns(DESTINATION_TUBE_COLUMNS);
    view.destinationTubesGrid.getColumns().forEach(
        column -> column.setHeaderCaption(resources.message((String) column.getPropertyId())));
    view.destinationTubesGrid.getColumn(DESTINATION_TUBE_NAME).setRenderer(new ComponentRenderer());
  }

  private TextField prepareDestinationTubeNameField(Sample sample) {
    MessageResource generalResources = view.getGeneralResources();
    TextField textField = new TextField();
    textField.addStyleName(DESTINATION_TUBE_NAME);
    textField.setRequired(true);
    textField.setRequiredError(generalResources.message(REQUIRED));
    textField.addValidator(value -> {
      if (tubeService.get((String) value) != null) {
        throw new InvalidValueException(generalResources.message(ALREADY_EXISTS));
      }
    });
    destinationTubeFields.put(sample, textField);
    return textField;
  }

  private void bindFields() {
    destinationPlateFieldGroup.bind(view.destinationPlatesTypeField, PLATE_TYPE);
    destinationPlateFieldGroup.bind(view.destinationPlatesField, PLATE_NAME);
  }

  private void addListeners() {
    view.sourcePlatesField
        .addValueChangeListener(e -> updateSourcePlate((Plate) view.sourcePlatesField.getValue()));
    view.destinationPlatesTypeField.addValueChangeListener(e -> updateDestinationPlateType());
    view.destinationPlatesField.addValueChangeListener(e -> updateDestinationPlate());
    samplesProperty.addValueChangeListener(e -> updateSamples());
    view.saveButton.addClickListener(e -> save());
  }

  private void updateSourcePlate(Plate plate) {
    view.sourcePlatePanel.setVisible(plate != null);
    if (plate != null) {
      view.sourcePlatePanel.setCaption(plate.getName());
      List<Sample> samples = samplesProperty.getValue();
      List<PlateSpot> wells =
          samples.stream().flatMap(sample -> plateSpotService.location(sample, plate).stream())
              .collect(Collectors.toList());
      view.sourcePlateFormPresenter.setPlate(plate);
      view.sourcePlateFormPresenter.setSelectedSpots(wells.stream()
          .filter(w -> w.getPlate().getId().equals(plate.getId())).collect(Collectors.toList()));
    }
  }

  private void updateDestinationPlateType() {
    PlateType type = (PlateType) view.destinationPlatesTypeField.getValue();
    List<Plate> plates = plateService.all(new PlateFilterBuilder().type(type).build());
    view.destinationPlatesField.removeAllItems();
    plates.forEach(plate -> {
      view.destinationPlatesField.addItem(plate.getName());
      view.destinationPlatesField.setItemCaption(plate.getName(), plate.getName());
    });
  }

  private void updateDestinationPlate() {
    String name = (String) view.destinationPlatesField.getValue();
    Plate plate = plateService.get(name);
    if (plate == null) {
      plate = new Plate();
      plate.setName(name);
      plate.setType((PlateType) view.destinationPlatesTypeField.getValue());
      plate.initSpots();
    }

    view.destinationPlatePanel.setVisible(plate != null);
    view.destinationPlatePanel.setCaption(plate.getName());
    view.destinationPlateFormPresenter.setPlate(plate);
  }

  private void updateSamples() {
    List<Sample> samples = samplesProperty.getValue();
    sourceTubesContainer.removeAllItems();
    sourceTubesContainer.addAll(samples);
    List<Plate> plates = plateService.all(new PlateFilterBuilder().containsAnySamples(
        samples.stream().filter(s -> s instanceof SubmissionSample).collect(Collectors.toList()))
        .build());
    view.sourcePlatesField.removeAllItems();
    plates.forEach(plate -> {
      view.sourcePlatesField.addItem(plate);
      view.sourcePlatesField.setItemCaption(plate, plate.getName());
    });
    if (!plates.isEmpty()) {
      view.sourcePlatesField.setValue(plates.get(0));
    }
    samples.stream().map(s -> plateSpotService.last(s)).filter(well -> well != null).findFirst()
        .ifPresent(w -> {
          plates.stream().filter(p -> p.getId().equals(w.getPlate().getId())).findAny()
              .ifPresent(p -> {
                view.sourcePlatesField.setValue(p);
              });
        });
    destinationTubesContainer.removeAllItems();
    destinationTubesContainer.addAll(
        samples.stream().map(s -> new DestinationTube(s, prepareDestinationTubeNameField(s)))
            .collect(Collectors.toList()));
  }

  private boolean isTubeSource() {
    return view.sourceTabs.getSelectedTab() == view.sourceTubesGrid;
  }

  private boolean isTubeDestination() {
    return view.destinationTabs.getSelectedTab() == view.destinationTubesGrid;
  }

  private boolean validate() {
    logger.trace("Validate transfer");
    boolean valid = true;
    try {
      if (isTubeSource()) {
        validateSourceTubes();
      } else {
        validateSourcePlate();
      }
      if (isTubeDestination()) {
        validateDestinationTubes();
      } else {
        validateDestinationPlate();
      }
    } catch (InvalidValueException e) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Validation value failed with message {}", e.getMessage(), e);
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      valid = false;
    }
    return valid;
  }

  private void validateSourceTubes() {
    sourceTubeFields.values().forEach(t -> t.validate());
  }

  private void validateSourcePlate() {
    view.sourcePlatesField.setComponentError(null);
    MessageResource resources = view.getResources();
    Collection<PlateSpot> selectedWells = view.sourcePlateFormPresenter.getSelectedSpots();
    if (selectedWells.isEmpty()) {
      logger.debug("No samples to transfer");
      String message = resources.message(SOURCE_PLATE_EMPTY);
      view.sourcePlatesField.setComponentError(new UserError(message));
      throw new InvalidValueException(message);
    }
    Map<Sample, Boolean> sampleSelection =
        samplesProperty.getValue().stream().collect(Collectors.toMap(s -> s, s -> false));
    for (PlateSpot well : selectedWells) {
      if (well.getSample() == null) {
        logger.debug("A selected well {} does not have a sample", well);
        String message = resources.message(SOURCE_PLATE_EMPTY_WELL, well.getName());
        view.sourcePlatesField.setComponentError(new UserError(message));
        throw new InvalidValueException(message);
      }
      sampleSelection.put(well.getSample(), true);
    }
    sampleSelection.entrySet().stream().filter(e -> !e.getValue()).findAny().ifPresent(e -> {
      logger.debug("A selected well {} does not have a sample", e.getKey());
      String message = resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, e.getKey().getName());
      view.sourcePlatesField.setComponentError(new UserError(message));
      throw new InvalidValueException(message);
    });
  }

  private void validateDestinationTubes() {
    destinationTubeFields.values().forEach(t -> t.validate());
  }

  private void validateDestinationPlate() {
    view.destinationPlatesField.setComponentError(null);
    MessageResource resources = view.getResources();
    Plate plate = view.destinationPlateFormPresenter.getPlate();
    PlateSpot spot = view.destinationPlateFormPresenter.getSelectedSpot();
    if (spot == null) {
      logger.debug("No selection in destination plate");
      String message = resources.message(DESTINATION_PLATE_NO_SELECTION);
      view.destinationPlatesField.setComponentError(new UserError(message));
      throw new InvalidValueException(message);
    }
    int column = spot.getColumn();
    int row = spot.getRow();
    for (int i = 0; i < sources().size(); i++) {
      if (plate.spot(row, column).getSample() != null) {
        logger.debug("Not enough free wells in destination plate starting from selection");
        String message = resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE,
            samplesProperty.getValue().size());
        view.destinationPlatesField.setComponentError(new UserError(message));
        throw new InvalidValueException(message);
      }
      row++;
      if (row >= plate.getRowCount()) {
        row = 0;
        column++;
      }
      if (column >= plate.getColumnCount()) {
        logger.debug("Not enough free wells in destination plate starting from selection");
        String message = resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE,
            samplesProperty.getValue().size());
        view.destinationPlatesField.setComponentError(new UserError(message));
        throw new InvalidValueException(message);
      }
    }
  }

  private List<SampleContainer> sources() {
    if (isTubeSource()) {
      List<Sample> ids = sourceTubesContainer.getItemIds();
      return ids.stream().map(sample -> (Tube) sourceTubeFields.get(sample).getValue())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>(view.sourcePlateFormPresenter.getSelectedSpots());
    }
  }

  private List<SampleContainer> destinations() {
    if (isTubeDestination()) {
      List<DestinationTube> ids = destinationTubesContainer.getItemIds();
      return ids.stream().map(dtube -> new Tube(null, dtube.getTubeName().getValue()))
          .collect(Collectors.toList());
    } else {
      Plate plate = view.destinationPlateFormPresenter.getPlate();
      PlateSpot spot = view.destinationPlateFormPresenter.getSelectedSpot();
      int column = spot.getColumn();
      int row = spot.getRow();
      List<SampleContainer> destinations = new ArrayList<>();
      for (int i = 0; i < sources().size(); i++) {
        destinations.add(plate.spot(row, column));
        row++;
        if (row >= plate.getRowCount()) {
          row = 0;
          column++;
        }
      }
      return destinations;
    }
  }

  private void save() {
    if (validate()) {
      List<SampleContainer> sources = sources();
      List<SampleContainer> destinations = destinations();
      List<SampleTransfer> sampleTransfers = new ArrayList<>();
      for (int i = 0; i < sources.size(); i++) {
        SampleContainer source = sources.get(i);
        SampleContainer destination = destinations.get(i);
        SampleTransfer sampleTransfer = new SampleTransfer();
        sampleTransfer.setSample(source.getSample());
        sampleTransfer.setContainer(source);
        sampleTransfer.setDestinationContainer(destination);
        sampleTransfers.add(sampleTransfer);
      }
      Transfer transfer = new Transfer();
      transfer.setTreatmentSamples(sampleTransfers);
      transferService.insert(transfer);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(SAVED));
      view.navigateTo(TransferView.VIEW_NAME + "/" + transfer.getId());
    }
  }

  public void enter(String parameters) {
    samplesProperty.setValue(view.savedSamples());
  }

  public static class DestinationTube {
    private Sample sample;
    private TextField tubeName;

    private DestinationTube(Sample sample, TextField tubeNameField) {
      this.sample = sample;
      this.tubeName = tubeNameField;
    }

    public Sample getSample() {
      return sample;
    }

    public void setSample(Sample sample) {
      this.sample = sample;
    }

    public TextField getTubeName() {
      return tubeName;
    }

    public void setTubeName(TextField tubeName) {
      this.tubeName = tubeName;
    }
  }
}
