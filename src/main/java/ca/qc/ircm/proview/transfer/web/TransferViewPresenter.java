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

import static ca.qc.ircm.proview.sample.QSample.sample;
import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilterBuilder;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellComparator;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeComparator;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
  public static final String TRANSFER_TYPE_PANEL = "typePanel";
  public static final String TRANSFER_TYPE = "type";
  public static final String TRANSFERS_PANEL = "transfersPanel";
  public static final String TRANSFERS = "transfers";
  public static final String SAMPLE = sampleTransfer.sample.getMetadata().getName();
  public static final String CONTAINER = sampleTransfer.container.getMetadata().getName();
  public static final String DESTINATION_CONTAINER =
      sampleTransfer.destinationContainer.getMetadata().getName();
  public static final String DESTINATION_CONTAINER_DUPLICATE = DESTINATION_CONTAINER + ".duplicate";
  public static final String DESTINATION_TUBE = DESTINATION_CONTAINER + "Tube";
  public static final String DESTINATION_WELL = DESTINATION_CONTAINER + "Well";
  public static final String DESTINATION_WELL_IN_USE = DESTINATION_CONTAINER + ".inUse";
  public static final String SOURCE = "source";
  public static final String SOURCE_PLATES = "sourcePlates";
  public static final String SOURCE_PLATE_PANEL = "sourcePlatePanel";
  public static final String SOURCE_PLATE = "sourcePlate";
  public static final String SOURCE_PLATE_EMPTY = "sourcePlate.empty";
  public static final String SOURCE_PLATE_EMPTY_WELL = "sourcePlate.emptyWell";
  public static final String SOURCE_PLATE_SAMPLE_NOT_SELECTED = "sourcePlate.sampleNotSelected";
  public static final String DESTINATION = "destination";
  public static final String DESTINATION_PLATES = "destinationPlates";
  public static final String DESTINATION_PLATE_PANEL = "destinationPlatePanel";
  public static final String DESTINATION_PLATE = "destinationPlate";
  public static final String DESTINATION_PLATE_NO_SELECTION = "destinationPlate.noSelection";
  public static final String DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE =
      "destinationPlate.notEnoughFreeSpace";
  public static final String TEST = "test";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String DESTINATION_SAMPLE = sample.getMetadata().getName();
  public static final String DESTINATION_SAMPLE_NAME =
      DESTINATION_SAMPLE + "." + sample.name.getMetadata().getName();
  private static final Logger logger = LoggerFactory.getLogger(TransferViewPresenter.class);
  private TransferView view;
  private List<Sample> samples = new ArrayList<>();
  private Map<SampleTransfer, Binder<SampleTransfer>> transferBinders = new HashMap<>();
  private Map<SampleTransfer, ComboBox<Tube>> destinationTubes = new HashMap<>();
  private Map<SampleTransfer, ComboBox<Well>> destinationWells = new HashMap<>();
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
    prepareComponents();
    addListeners();
    view.type.setValue(TransferType.PLATE_TO_PLATE);
    updateVisibility();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message("title", applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
    view.typePanel.addStyleName(TRANSFER_TYPE_PANEL);
    view.typePanel.addStyleName(REQUIRED);
    view.typePanel.setCaption(resources.message(TRANSFER_TYPE_PANEL));
    view.type.addStyleName(TRANSFER_TYPE);
    view.type.setItemCaptionGenerator(type -> type.getLabel(locale));
    view.type.setItems(TransferType.values());
    view.type.addValueChangeListener(e -> updateType());
    view.transfersPanel.addStyleName(TRANSFERS_PANEL);
    view.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    prepareTransfersGrid();
    view.source.addStyleName(SOURCE);
    view.source.setCaption(resources.message(SOURCE));
    view.sourcePlatesField.addStyleName(SOURCE_PLATES);
    view.sourcePlatesField.setCaption(resources.message(SOURCE_PLATES));
    view.sourcePlatesField.setEmptySelectionAllowed(false);
    view.sourcePlatesField.setItemCaptionGenerator(Plate::getName);
    view.sourcePlatesField.setRequiredIndicatorVisible(true);
    view.sourcePlatePanel.addStyleName(SOURCE_PLATE_PANEL);
    view.sourcePlatePanel.setVisible(false);
    view.sourcePlateForm.addStyleName(SOURCE_PLATE);
    view.sourcePlateForm.setMultiSelect(true);
    view.destination.addStyleName(DESTINATION);
    view.destination.setCaption(resources.message(DESTINATION));
    view.destinationPlatesField.addStyleName(DESTINATION_PLATES);
    view.destinationPlatesField.setRequiredIndicatorVisible(true);
    view.destinationPlatesField.setCaption(resources.message(DESTINATION_PLATES));
    view.destinationPlatesField.setItemCaptionGenerator(Plate::getName);
    view.destinationPlatesField.setEmptySelectionAllowed(false);
    view.destinationPlatesField.setNewItemHandler(name -> {
      Plate plate = new Plate(null, name);
      plate.initWells();
      view.destinationPlatesField.setValue(plate);
    });
    view.destinationPlatesField.setItems(plateService.all(new PlateFilterBuilder().build()));
    view.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    view.destinationPlatePanel.setVisible(false);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    view.test.addStyleName(TEST);
    view.test.setCaption(resources.message(TEST));
    view.test.addClickListener(e -> test());
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
  }

  private void prepareTransfersGrid() {
    final MessageResource resources = view.getResources();
    view.transfers.addStyleName(TRANSFERS);
    view.transfers.addStyleName(COMPONENTS);
    view.transfers.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    view.transfers.addColumn(ts -> sourceField(ts), new ComponentRenderer()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER));
    view.transfers.addColumn(ts -> destinationTube(ts), new ComponentRenderer())
        .setId(DESTINATION_TUBE).setCaption(resources.message(DESTINATION_TUBE));
    view.transfers.addColumn(ts -> destinationWell(ts), new ComponentRenderer())
        .setId(DESTINATION_WELL).setCaption(resources.message(DESTINATION_WELL));
  }

  private Binder<SampleTransfer> binder(SampleTransfer sampleTransfer) {
    if (transferBinders.get(sampleTransfer) != null) {
      return transferBinders.get(sampleTransfer);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      final Sample sample = sampleTransfer.getSample();
      Binder<SampleTransfer> binder = new BeanValidationBinder<>(SampleTransfer.class);
      binder.setBean(sampleTransfer);
      ComboBox<Tube> container = new ComboBox<>();
      container.addStyleName(CONTAINER);
      container.setEmptySelectionAllowed(false);
      List<Tube> tubes = tubeService.all(sample);
      Collections.sort(tubes,
          new TubeComparator(view.getLocale(), TubeComparator.Compare.TIME_STAMP));
      Collections.reverse(tubes);
      container.setItems(tubes);
      container.setItemCaptionGenerator(Tube::getName);
      if (!tubes.isEmpty()) {
        sampleTransfer.setContainer(tubes.get(0));
      }
      binder.forField(container).asRequired(generalResources.message(REQUIRED)).bind(CONTAINER);
      transferBinders.put(sampleTransfer, binder);
      return binder;
    }
  }

  @SuppressWarnings("unchecked")
  private ComboBox<Tube> sourceField(SampleTransfer sampleTransfer) {
    if (transferBinders.get(sampleTransfer) == null) {
      binder(sampleTransfer);
    }
    return (ComboBox<Tube>) transferBinders.get(sampleTransfer).getBinding(CONTAINER).get()
        .getField();
  }

  private ComboBox<Tube> destinationTube(SampleTransfer sampleTransfer) {
    if (destinationTubes.get(sampleTransfer) != null) {
      return destinationTubes.get(sampleTransfer);
    } else {
      ComboBox<Tube> field = new ComboBox<>();
      field.setItems(Collections.emptyList());
      field.setItemCaptionGenerator(Tube::getName);
      field.setNewItemHandler(name -> field.setValue(new Tube(null, name)));
      field.addStyleName(DESTINATION_TUBE);
      destinationTubes.put(sampleTransfer, field);
      return field;
    }
  }

  private ComboBox<Well> destinationWell(SampleTransfer sampleTransfer) {
    if (destinationWells.get(sampleTransfer) != null) {
      return destinationWells.get(sampleTransfer);
    } else {
      ComboBox<Well> field = new ComboBox<>();
      field.addStyleName(DESTINATION_CONTAINER);
      field.setEmptySelectionAllowed(false);
      field.setItemCaptionGenerator(well -> well.getName());
      field.setItems(view.destinationPlatesField.getValue() != null
          ? view.destinationPlatesField.getValue().getWells()
          : Collections.emptyList());
      destinationWells.put(sampleTransfer, field);
      return field;
    }
  }

  private void addListeners() {
    view.sourcePlatesField
        .addValueChangeListener(e -> updateSourcePlate(view.sourcePlatesField.getValue()));
    view.destinationPlatesField.addValueChangeListener(e -> updateDestinationPlate());
    view.saveButton.addClickListener(e -> save());
  }

  private void updateType() {
    final MessageResource generalResources = view.getGeneralResources();
    final SampleContainerType destinationType = view.type.getValue().destinationType;
    view.transfers.getDataProvider().refreshAll();
    transferBinders.values().forEach(binder -> {
      SampleTransfer ts = binder.getBean();
      HasValue<? extends SampleContainer> destination =
          destinationType == WELL ? destinationWell(ts) : destinationTube(ts);
      ts.setDestinationContainer(destination.getValue());
      binder.forField(destination).asRequired(generalResources.message(REQUIRED))
          .withValidator((container, context) -> validateDestinationContainer(container))
          .bind(DESTINATION_CONTAINER);
    });
    updateVisibility();
  }

  private void updateVisibility() {
    final SampleContainerType sourceType = view.type.getValue().sourceType;
    final SampleContainerType destinationType = view.type.getValue().destinationType;
    view.transfersPanel.setVisible(sourceType == TUBE);
    view.transfers.getColumn(DESTINATION_TUBE).setHidden(destinationType != TUBE);
    view.transfers.getColumn(DESTINATION_WELL).setHidden(destinationType != WELL);
    view.source.setVisible(sourceType == WELL);
    view.destination.setVisible(destinationType == WELL);
  }

  private void updateSourcePlate(Plate plate) {
    view.sourcePlatePanel.setVisible(plate != null);
    if (plate != null) {
      view.sourcePlatePanel.setCaption(plate.getName());
      List<Well> wells =
          samples.stream().flatMap(sample -> wellService.location(sample, plate).stream())
              .collect(Collectors.toList());
      view.sourcePlateForm.setValue(plate);
      view.sourcePlateForm.setSelectedWells(wells.stream()
          .filter(w -> w.getPlate().getId().equals(plate.getId())).collect(Collectors.toList()));
    }
  }

  private void updateDestinationPlate() {
    Plate plate = view.destinationPlatesField.getValue();
    view.destinationPlatePanel.setVisible(plate != null);
    if (plate != null) {
      view.destinationPlatePanel.setCaption(plate.getName());
      view.destinationPlateForm.setValue(plate);
      destinationWells.values().forEach(dw -> dw.setItems(plate.getWells()));
    }
  }

  private void updateSamples() {
    List<SampleTransfer> transfers = samples.stream().map(sample -> {
      SampleTransfer ts = new SampleTransfer();
      ts.setSample(sample);
      return ts;
    }).collect(Collectors.toList());
    view.transfers.setItems(transfers);
    transfers.stream().forEach(ts -> {
      binder(ts);
      destinationTube(ts);
      destinationWell(ts);
    });
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
  }

  private ValidationResult validateDestinationContainer(SampleContainer container) {
    if (container instanceof Tube) {
      if (tubeService.get(container.getName()) != null) {
        final MessageResource generalResources = view.getGeneralResources();
        return ValidationResult
            .error(generalResources.message(ALREADY_EXISTS, container.getName()));
      }
    } else if (container instanceof Well) {
      Well database = wellService.get(container.getId());
      if (database != null && database.getSample() != null) {
        final MessageResource generalResources = view.getGeneralResources();
        return ValidationResult
            .error(generalResources.message(DESTINATION_WELL_IN_USE, container.getName()));
      }
    }
    return ValidationResult.ok();
  }

  private boolean validate() {
    logger.trace("Validate transfer");
    boolean valid = true;
    if (view.type.getValue().sourceType == TUBE) {
      for (Binder<SampleTransfer> binder : this.transferBinders.values()) {
        valid &= validate(binder);
      }
    } else {
      valid &= validate(() -> validateSourcePlate());
    }
    valid &= validate(() -> validateDestinations());
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
      return false;
    } else {
      return true;
    }
  }

  private ValidationResult validateDestinations() {
    final MessageResource resources = view.getResources();
    if (view.type.getValue().sourceType == TUBE) {
      destinationTubes.values().forEach(field -> field.setComponentError(null));
      Set<String> containerNames = new HashSet<>();
      for (Binder<SampleTransfer> binder : transferBinders.values()) {
        SampleTransfer ts = binder.getBean();
        if (ts.getDestinationContainer() != null
            && !containerNames.add(ts.getDestinationContainer().getName())) {
          String message = resources.message(DESTINATION_CONTAINER_DUPLICATE,
              ts.getDestinationContainer().getType().ordinal(),
              ts.getDestinationContainer().getName());
          if (ts.getDestinationContainer() instanceof Tube) {
            destinationTubes.get(ts).setComponentError(new UserError(message));
          } else {
            destinationWells.get(ts).setComponentError(new UserError(message));
          }
          logger.debug("Validation error: {}", message);
          return ValidationResult.error(message);
        }
      }
    } else {
      view.destinationPlatesField.setComponentError(null);
      Plate plate = view.destinationPlateForm.getValue();
      Well well = view.destinationPlateForm.getSelectedWell();
      if (well == null) {
        String message = resources.message(DESTINATION_PLATE_NO_SELECTION);
        logger.debug("Validation error: {}", message);
        view.destinationPlatesField.setComponentError(new UserError(message));
        return ValidationResult.error(message);
      }
      int column = well.getColumn();
      int row = well.getRow();
      for (int i = 0; i < sources().size(); i++) {
        if (plate.well(row, column).getSample() != null) {
          String message =
              resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
          logger.debug("Validation error: {}", message);
          view.destinationPlatesField.setComponentError(new UserError(message));
          return ValidationResult.error(message);
        }
        row++;
        if (row >= plate.getRowCount()) {
          row = 0;
          column++;
        }
        if (column >= plate.getColumnCount()) {
          String message =
              resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
          logger.debug("Validation error: {}", message);
          view.destinationPlatesField.setComponentError(new UserError(message));
          return ValidationResult.error(message);
        }
      }
    }
    return ValidationResult.ok();
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
    Map<Long, Boolean> sampleSelection =
        samples.stream().collect(Collectors.toMap(s -> s.getId(), s -> false));
    for (Well well : selectedWells) {
      if (well.getSample() != null) {
        sampleSelection.put(well.getSample().getId(), true);
      }
    }
    Optional<Map.Entry<Long, Boolean>> emptySelectionEntry =
        sampleSelection.entrySet().stream().filter(e -> !e.getValue()).findAny();
    if (emptySelectionEntry.isPresent()) {
      Sample sample =
          samples.stream().filter(sa -> emptySelectionEntry.get().getKey().equals(sa.getId()))
              .findAny().orElse(null);
      logger.debug("A selected sample {} does not have a well", sample);
      String message = resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, sample.getName());
      view.sourcePlatesField.setComponentError(new UserError(message));
      return ValidationResult.error(message);
    }
    return ValidationResult.ok();
  }

  private List<SampleContainer> sources() {
    if (view.type.getValue().sourceType == TUBE) {
      return new ArrayList<>(transferBinders.values().stream()
          .map(binder -> binder.getBean().getContainer()).collect(Collectors.toList()));
    } else {
      return new ArrayList<>(
          view.sourcePlateForm.getSelectedWells().stream().filter(well -> well.getSample() != null)
              .sorted(new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN))
              .collect(Collectors.toList()));
    }
  }

  private List<SampleContainer> destinations() {
    if (view.type.getValue().sourceType == TUBE) {
      return new ArrayList<>(transferBinders.values().stream()
          .map(binder -> binder.getBean().getDestinationContainer()).collect(Collectors.toList()));
    } else {
      Plate plate = view.destinationPlateForm.getValue();
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

  private void test() {
    if (validate()) {
      if (view.type.getValue().destinationType == WELL) {
        // Reset samples.
        Plate database = plateService.get(view.destinationPlatesField.getValue() != null
            ? view.destinationPlatesField.getValue().getId()
            : null);
        if (database == null) {
          view.destinationPlateForm.getValue().getWells().forEach(well -> well.setSample(null));
        } else {
          view.destinationPlateForm.getValue().getWells().forEach(
              well -> well.setSample(database.well(well.getRow(), well.getColumn()).getSample()));
        }
        // Set samples.
        List<SampleContainer> sources = sources();
        List<SampleContainer> destinations = destinations();
        for (int i = 0; i < sources.size(); i++) {
          SampleContainer source = sources.get(i);
          SampleContainer destination = destinations.get(i);
          destination.setSample(source.getSample());
        }
        view.destinationPlateForm.setValue(view.destinationPlateForm.getValue());
      }
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
        SampleTransfer ts = new SampleTransfer();
        ts.setSample(source.getSample());
        ts.setContainer(source);
        ts.setDestinationContainer(destination);
        sampleTransfers.add(ts);
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
