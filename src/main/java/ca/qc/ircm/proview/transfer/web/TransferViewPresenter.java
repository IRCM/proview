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
import ca.qc.ircm.proview.plate.PlateType;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.server.UserError;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
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
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Sample transfer view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TransferViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SOURCE = "source";
  public static final String SOURCE_TYPE = "sourceType";
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
  static final PlateType[] DESTINATION_PLATE_TYPES =
      new PlateType[] { PlateType.A, PlateType.G, PlateType.PM };
  private static final Logger logger = LoggerFactory.getLogger(TransferViewPresenter.class);
  private TransferView view;
  private List<Sample> samples = new ArrayList<>();
  private Map<Object, ComboBox<Tube>> sourceTubeFields = new HashMap<>();
  private Map<Object, TextField> destinationTubeFields = new HashMap<>();
  private Map<Sample, Binder<SampleSourceTube>> sourceTubeBinders = new HashMap<>();
  private Map<Sample, Binder<Tube>> destinationTubeBinders = new HashMap<>();
  private Binder<Plate> destinationPlateBinder = new BeanValidationBinder<>(Plate.class);
  @Inject
  private TransferService transferService;
  @Inject
  private TubeService tubeService;
  @Inject
  private WellService wellService;
  @Inject
  private PlateService plateService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected TransferViewPresenter() {
  }

  protected TransferViewPresenter(TransferService transferService, TubeService tubeService,
      WellService wellService, PlateService plateService, String applicationName) {
    this.transferService = transferService;
    this.tubeService = tubeService;
    this.wellService = wellService;
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
    destinationPlateBinder.setBean(new Plate());
    prepareComponents();
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
    view.source.addStyleName(SOURCE);
    view.source.setCaption(resources.message(SOURCE));
    view.sourceType.addStyleName(SOURCE_TYPE);
    view.sourceType.setItems(SampleContainerType.values());
    view.sourceType.setItemCaptionGenerator(type -> type.getLabel(locale));
    prepareSourceTubesGrid();
    view.sourcePlatesField.addStyleName(SOURCE_PLATES);
    view.sourcePlatesField.setCaption(resources.message(SOURCE_PLATES));
    view.sourcePlatesField.setEmptySelectionAllowed(false);
    view.sourcePlatesField.setItemCaptionGenerator(Plate::getName);
    view.sourcePlatePanel.addStyleName(SOURCE_PLATE_PANEL);
    view.sourcePlatePanel.setVisible(false);
    view.sourcePlateForm.addStyleName(SOURCE_PLATE);
    view.sourcePlateForm.setMultiSelect(true);
    view.destination.addStyleName(DESTINATION);
    view.destination.setCaption(resources.message(DESTINATION));
    view.destinationType.addStyleName(DESTINATION_TABS);
    prepareDestinationTubesGrid();
    view.destinationPlatesTypeField.setEmptySelectionAllowed(false);
    view.destinationPlatesTypeField.addStyleName(DESTINATION_PLATES_TYPE);
    view.destinationPlatesTypeField.setCaption(resources.message(DESTINATION_PLATES_TYPE));
    view.destinationPlatesTypeField.setItems(DESTINATION_PLATE_TYPES);
    view.destinationPlatesTypeField.setItemCaptionGenerator(type -> type.getLabel(locale));
    destinationPlateBinder.forField(view.destinationPlatesTypeField)
        .asRequired(generalResources.message(REQUIRED)).bind(Plate::getType, Plate::setType);
    view.destinationPlatesTypeField.setRequiredIndicatorVisible(true);
    view.destinationPlatesField.addStyleName(DESTINATION_PLATES);
    view.destinationPlatesField.setCaption(resources.message(DESTINATION_PLATES));
    view.destinationPlatesField.setEmptySelectionAllowed(false);
    view.destinationPlatesField.setNewItemHandler(name -> {
      destinationPlateBinder.getBean().setName(name);
    });
    destinationPlateBinder.forField(view.destinationPlatesField)
        .asRequired(generalResources.message(REQUIRED)).bind(Plate::getName, Plate::setName);
    view.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    view.destinationPlatePanel.setVisible(false);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    Plate destinationPlate = new Plate();
    destinationPlate.setType(PlateType.A);
    destinationPlateBinder.setBean(destinationPlate);
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void prepareSourceTubesGrid() {
    final MessageResource resources = view.getResources();
    view.sourceTubesGrid.addStyleName(SOURCE_TUBES);
    view.sourceTubesGrid.addStyleName(COMPONENTS);
    view.sourceTubesGrid.addColumn(Sample::getName).setId(NAME).setCaption(resources.message(NAME));
    view.sourceTubesGrid.addColumn(sample -> sourceTubeComboBox(sample), new ComponentRenderer())
        .setId(TUBE).setCaption(resources.message(TUBE));
  }

  private ComboBox<Tube> sourceTubeComboBox(Sample sample) {
    if (sourceTubeFields.containsKey(sample)) {
      return sourceTubeFields.get(sample);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      ComboBox<Tube> comboBox = new ComboBox<>();
      comboBox.addStyleName(TUBE);
      comboBox.setEmptySelectionAllowed(false);
      List<Tube> tubes = tubeService.all(sample);
      comboBox.setItems(tubes);
      comboBox.setItemCaptionGenerator(Tube::getName);
      comboBox.setRequiredIndicatorVisible(true);
      Binder<SampleSourceTube> binder = new BeanValidationBinder<>(SampleSourceTube.class);
      binder.setBean(new SampleSourceTube(tubes.isEmpty() ? null : tubes.get(0)));
      binder.forField(comboBox).asRequired(generalResources.message(REQUIRED))
          .bind(SampleSourceTube::getTube, SampleSourceTube::setTube);
      sourceTubeBinders.put(sample, binder);
      sourceTubeFields.put(sample, comboBox);
      return comboBox;
    }
  }

  private void prepareDestinationTubesGrid() {
    final MessageResource resources = view.getResources();
    view.destinationTubesGrid.addStyleName(DESTINATION_TUBES);
    view.destinationTubesGrid.addStyleName(COMPONENTS);
    view.destinationTubesGrid.addColumn(Sample::getName).setId(DESTINATION_SAMPLE_NAME)
        .setCaption(resources.message(DESTINATION_SAMPLE_NAME));
    view.destinationTubesGrid
        .addColumn(sample -> destinationTubeNameField(sample), new ComponentRenderer())
        .setId(DESTINATION_TUBE_NAME).setCaption(resources.message(DESTINATION_TUBE_NAME));
  }

  private TextField destinationTubeNameField(Sample sample) {
    if (destinationTubeFields.containsKey(sample)) {
      return destinationTubeFields.get(sample);
    } else {
      MessageResource generalResources = view.getGeneralResources();
      TextField textField = new TextField();
      textField.addStyleName(DESTINATION_TUBE_NAME);
      Binder<Tube> binder = new BeanValidationBinder<>(Tube.class);
      binder.setBean(new Tube());
      binder.forField(textField).asRequired(generalResources.message(REQUIRED))
          .withValidator((value, context) -> tubeService.get(value) != null
              ? ValidationResult.error(generalResources.message(ALREADY_EXISTS))
              : ValidationResult.ok())
          .bind(Tube::getName, Tube::setName);
      destinationTubeBinders.put(sample, binder);
      destinationTubeFields.put(sample, textField);
      return textField;
    }
  }

  private void addListeners() {
    view.sourcePlatesField
        .addValueChangeListener(e -> updateSourcePlate(view.sourcePlatesField.getValue()));
    view.destinationPlatesTypeField.addValueChangeListener(e -> updateDestinationPlateType());
    view.destinationPlatesField.addValueChangeListener(e -> updateDestinationPlate());
    view.saveButton.addClickListener(e -> save());
  }

  private void updateSourcePlate(Plate plate) {
    view.sourcePlatePanel.setVisible(plate != null);
    if (plate != null) {
      view.sourcePlatePanel.setCaption(plate.getName());
      List<Well> wells =
          samples.stream().flatMap(sample -> wellService.location(sample, plate).stream())
              .collect(Collectors.toList());
      view.sourcePlateForm.setPlate(plate);
      view.sourcePlateForm.setSelectedWells(wells.stream()
          .filter(w -> w.getPlate().getId().equals(plate.getId())).collect(Collectors.toList()));
    }
  }

  private void updateDestinationPlateType() {
    PlateType type = view.destinationPlatesTypeField.getValue();
    List<Plate> plates = plateService.all(new PlateFilterBuilder().type(type).build());
    view.destinationPlatesField.setItems(plates.stream().map(plate -> plate.getName()));
  }

  private void updateDestinationPlate() {
    String name = view.destinationPlatesField.getValue();
    Plate plate = plateService.get(name);
    if (plate == null) {
      plate = new Plate();
      plate.setName(name);
      plate.setType(view.destinationPlatesTypeField.getValue());
      plate.initWells();
    }

    view.destinationPlatePanel.setVisible(plate != null);
    view.destinationPlatePanel.setCaption(plate.getName());
    view.destinationPlateForm.setPlate(plate);
  }

  private void updateSamples() {
    view.sourceTubesGrid.setItems(samples);
    List<Plate> plates = plateService.all(new PlateFilterBuilder().containsAnySamples(
        samples.stream().filter(s -> s instanceof SubmissionSample).collect(Collectors.toList()))
        .build());
    view.sourcePlatesField.setItems(plates);
    if (!plates.isEmpty()) {
      view.sourcePlatesField.setValue(plates.get(0));
    }
    samples.stream().map(s -> wellService.last(s)).filter(well -> well != null).findFirst()
        .ifPresent(w -> {
          plates.stream().filter(p -> p.getId().equals(w.getPlate().getId())).findAny()
              .ifPresent(p -> {
                view.sourcePlatesField.setValue(p);
              });
        });
    view.destinationTubesGrid.setItems(samples);
  }

  private boolean isTubeSource() {
    return view.sourceType.getValue() == SampleContainerType.TUBE;
  }

  private boolean isTubeDestination() {
    return view.destinationType.getValue() == SampleContainerType.WELL;
  }

  private boolean validate() {
    logger.trace("Validate transfer");
    boolean valid = true;
    if (isTubeSource()) {
      for (Sample sample : samples) {
        valid &= validate(sourceTubeBinders.get(sample));
      }
    } else {
      valid &= validate(() -> validateSourcePlate());
    }
    if (isTubeDestination()) {
      for (Sample sample : samples) {
        valid &= validate(destinationTubeBinders.get(sample));
      }
    } else {
      valid &= validate(() -> validateDestinationPlate());
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Transfer validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private boolean validate(Supplier<ValidationResult> validator) {
    ValidationResult result = validator.get();
    if (result.isError()) {
      logger.trace("Validation error {}", result.getErrorMessage());
      view.showError(result.getErrorMessage());
      return false;
    } else {
      return true;
    }
  }

  private ValidationResult validateSourcePlate() {
    view.sourcePlatesField.setComponentError(null);
    MessageResource resources = view.getResources();
    Collection<Well> selectedWells = view.sourcePlateForm.getSelectedWells();
    if (selectedWells.isEmpty()) {
      logger.debug("No samples to transfer");
      String message = resources.message(SOURCE_PLATE_EMPTY);
      view.sourcePlatesField.setComponentError(new UserError(message));
      return ValidationResult.error(message);
    }
    Map<Sample, Boolean> sampleSelection =
        samples.stream().collect(Collectors.toMap(s -> s, s -> false));
    for (Well well : selectedWells) {
      if (well.getSample() == null) {
        logger.debug("A selected well {} does not have a sample", well);
        String message = resources.message(SOURCE_PLATE_EMPTY_WELL, well.getName());
        view.sourcePlatesField.setComponentError(new UserError(message));
        return ValidationResult.error(message);
      }
      sampleSelection.put(well.getSample(), true);
    }
    Optional<Map.Entry<Sample, Boolean>> emptySelectionEntry =
        sampleSelection.entrySet().stream().filter(e -> !e.getValue()).findAny();
    if (emptySelectionEntry.isPresent()) {
      Sample sample = emptySelectionEntry.orElse(null).getKey();
      logger.debug("A selected well {} does not have a sample", sample);
      String message = resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, sample.getName());
      view.sourcePlatesField.setComponentError(new UserError(message));
      return ValidationResult.error(message);
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateDestinationPlate() {
    view.destinationPlatesField.setComponentError(null);
    MessageResource resources = view.getResources();
    Plate plate = view.destinationPlateForm.getPlate();
    Well well = view.destinationPlateForm.getSelectedWell();
    if (well == null) {
      logger.debug("No selection in destination plate");
      String message = resources.message(DESTINATION_PLATE_NO_SELECTION);
      view.destinationPlatesField.setComponentError(new UserError(message));
      return ValidationResult.error(message);
    }
    int column = well.getColumn();
    int row = well.getRow();
    for (int i = 0; i < sources().size(); i++) {
      if (plate.well(row, column).getSample() != null) {
        logger.debug("Not enough free wells in destination plate starting from selection");
        String message = resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
        view.destinationPlatesField.setComponentError(new UserError(message));
        return ValidationResult.error(message);
      }
      row++;
      if (row >= plate.getRowCount()) {
        row = 0;
        column++;
      }
      if (column >= plate.getColumnCount()) {
        logger.debug("Not enough free wells in destination plate starting from selection");
        String message = resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
        view.destinationPlatesField.setComponentError(new UserError(message));
        return ValidationResult.error(message);
      }
    }
    return ValidationResult.ok();
  }

  private List<SampleContainer> sources() {
    if (isTubeSource()) {
      List<Tube> sources =
          samples.stream().map(sample -> sourceTubeBinders.get(sample).getBean().getTube())
              .collect(Collectors.toList());
      return new ArrayList<>(sources);
    } else {
      return new ArrayList<>(view.sourcePlateForm.getSelectedWells());
    }
  }

  private List<SampleContainer> destinations() {
    if (isTubeDestination()) {
      List<Tube> destinations = samples.stream()
          .map(sample -> destinationTubeBinders.get(sample).getBean()).collect(Collectors.toList());
      return new ArrayList<>(destinations);
    } else {
      Plate plate = view.destinationPlateForm.getPlate();
      Well well = view.destinationPlateForm.getSelectedWell();
      int column = well.getColumn();
      int row = well.getRow();
      List<SampleContainer> destinations = new ArrayList<>();
      for (int i = 0; i < sources().size(); i++) {
        destinations.add(plate.well(row, column));
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
    samples = view.savedSamples();
    updateSamples();
  }

  public static class SampleSourceTube {
    private Tube tube;

    private SampleSourceTube(Tube tube) {
      this.tube = tube;
    }

    public Tube getTube() {
      return tube;
    }

    public void setTube(Tube tube) {
      this.tube = tube;
    }
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
