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
import ca.qc.ircm.proview.plate.WellLocation;
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
  public static final String DESTINATION_WELL_IN_USE = DESTINATION_WELL + ".inUse";
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
  private TransferViewDesign design;
  private List<Sample> samples = new ArrayList<>();
  private Map<Sample, Binder<SampleTransfer>> transferBinders = new HashMap<>();
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
    design = view.design;
    prepareComponents();
    addListeners();
    design.type.setValue(TransferType.PLATE_TO_PLATE);
    updateVisibility();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message("title", applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    design.headerLabel.setValue(resources.message(HEADER));
    design.typePanel.addStyleName(TRANSFER_TYPE_PANEL);
    design.typePanel.addStyleName(REQUIRED);
    design.typePanel.setCaption(resources.message(TRANSFER_TYPE_PANEL));
    design.type.addStyleName(TRANSFER_TYPE);
    design.type.setItemCaptionGenerator(type -> type.getLabel(locale));
    design.type.setItems(TransferType.values());
    design.type.addValueChangeListener(e -> updateType());
    design.transfersPanel.addStyleName(TRANSFERS_PANEL);
    design.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    prepareTransfersGrid();
    design.source.addStyleName(SOURCE);
    design.source.setCaption(resources.message(SOURCE));
    design.sourcePlatesField.addStyleName(SOURCE_PLATES);
    design.sourcePlatesField.setCaption(resources.message(SOURCE_PLATES));
    design.sourcePlatesField.setEmptySelectionAllowed(false);
    design.sourcePlatesField.setItemCaptionGenerator(Plate::getName);
    design.sourcePlatesField.setRequiredIndicatorVisible(true);
    design.sourcePlatePanel.addStyleName(SOURCE_PLATE_PANEL);
    design.sourcePlatePanel.setVisible(false);
    view.sourcePlateForm.addStyleName(SOURCE_PLATE);
    view.sourcePlateForm.setMultiSelect(true);
    design.destination.addStyleName(DESTINATION);
    design.destination.setCaption(resources.message(DESTINATION));
    design.destinationPlatesField.addStyleName(DESTINATION_PLATES);
    design.destinationPlatesField.setRequiredIndicatorVisible(true);
    design.destinationPlatesField.setCaption(resources.message(DESTINATION_PLATES));
    design.destinationPlatesField.setItemCaptionGenerator(Plate::getName);
    design.destinationPlatesField.setEmptySelectionAllowed(false);
    design.destinationPlatesField.setNewItemHandler(name -> {
      Plate plate = new Plate(null, name);
      plate.initWells();
      design.destinationPlatesField.setValue(plate);
    });
    design.destinationPlatesField.setItems(plateService.all(new PlateFilterBuilder().build()));
    design.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    design.destinationPlatePanel.setVisible(false);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    design.test.addStyleName(TEST);
    design.test.setCaption(resources.message(TEST));
    design.test.addClickListener(e -> test());
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
  }

  private void prepareTransfersGrid() {
    final MessageResource resources = view.getResources();
    design.transfers.addStyleName(TRANSFERS);
    design.transfers.addStyleName(COMPONENTS);
    design.transfers.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.transfers.addColumn(ts -> sourceField(ts), new ComponentRenderer()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER));
    design.transfers.addColumn(ts -> destinationTube(ts), new ComponentRenderer())
        .setId(DESTINATION_TUBE).setCaption(resources.message(DESTINATION_TUBE));
    design.transfers.addColumn(ts -> destinationWell(ts), new ComponentRenderer())
        .setId(DESTINATION_WELL).setCaption(resources.message(DESTINATION_WELL));
  }

  private Binder<SampleTransfer> binder(SampleTransfer ts) {
    if (transferBinders.get(ts.getSample()) != null) {
      return transferBinders.get(ts.getSample());
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      final Sample sample = ts.getSample();
      Binder<SampleTransfer> binder = new BeanValidationBinder<>(SampleTransfer.class);
      binder.setBean(ts);
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
        ts.setContainer(tubes.get(0));
      }
      binder.forField(container).asRequired(generalResources.message(REQUIRED)).bind(CONTAINER);
      transferBinders.put(ts.getSample(), binder);
      return binder;
    }
  }

  @SuppressWarnings("unchecked")
  private ComboBox<Tube> sourceField(SampleTransfer ts) {
    if (transferBinders.get(ts.getSample()) == null) {
      binder(ts);
    }
    return (ComboBox<Tube>) transferBinders.get(ts.getSample()).getBinding(CONTAINER).get()
        .getField();
  }

  private ComboBox<Tube> destinationTube(SampleTransfer ts) {
    if (destinationTubes.get(ts) != null) {
      return destinationTubes.get(ts);
    } else {
      ComboBox<Tube> field = new ComboBox<>();
      field.setItems(Collections.emptyList());
      field.setItemCaptionGenerator(Tube::getName);
      field.setNewItemHandler(name -> field.setValue(new Tube(null, name)));
      field.addStyleName(DESTINATION_TUBE);
      destinationTubes.put(ts, field);
      return field;
    }
  }

  private ComboBox<Well> destinationWell(SampleTransfer ts) {
    if (destinationWells.get(ts) != null) {
      return destinationWells.get(ts);
    } else {
      ComboBox<Well> field = new ComboBox<>();
      field.addStyleName(DESTINATION_CONTAINER);
      field.setEmptySelectionAllowed(false);
      field.setItemCaptionGenerator(well -> well.getName());
      field.setItems(design.destinationPlatesField.getValue() != null
          ? design.destinationPlatesField.getValue().getWells()
          : Collections.emptyList());
      destinationWells.put(ts, field);
      return field;
    }
  }

  private void addListeners() {
    design.sourcePlatesField
        .addValueChangeListener(e -> updateSourcePlate(design.sourcePlatesField.getValue()));
    design.destinationPlatesField.addValueChangeListener(e -> updateDestinationPlate());
    design.saveButton.addClickListener(e -> save());
  }

  private void updateType() {
    final MessageResource generalResources = view.getGeneralResources();
    final SampleContainerType destinationType = design.type.getValue().destinationType;
    design.transfers.getDataProvider().refreshAll();
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
    final SampleContainerType sourceType = design.type.getValue().sourceType;
    final SampleContainerType destinationType = design.type.getValue().destinationType;
    design.transfersPanel.setVisible(sourceType == TUBE);
    design.transfers.getColumn(DESTINATION_TUBE).setHidden(destinationType != TUBE);
    design.transfers.getColumn(DESTINATION_WELL).setHidden(destinationType != WELL);
    design.source.setVisible(sourceType == WELL);
    design.destination.setVisible(destinationType == WELL);
  }

  private void updateSourcePlate(Plate plate) {
    design.sourcePlatePanel.setVisible(plate != null);
    if (plate != null) {
      design.sourcePlatePanel.setCaption(plate.getName());
      List<Well> wells =
          samples.stream().flatMap(sample -> wellService.location(sample, plate).stream())
              .collect(Collectors.toList());
      view.sourcePlateForm.setValue(plate);
      view.sourcePlateForm.setSelectedWells(wells.stream()
          .filter(w -> w.getPlate().getId().equals(plate.getId())).collect(Collectors.toList()));
    }
  }

  private void updateDestinationPlate() {
    Plate plate = design.destinationPlatesField.getValue();
    design.destinationPlatePanel.setVisible(plate != null);
    if (plate != null) {
      design.destinationPlatePanel.setCaption(plate.getName());
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
    design.transfers.setItems(transfers);
    transfers.stream().forEach(ts -> {
      binder(ts);
      destinationTube(ts);
      destinationWell(ts);
    });
    List<Plate> plates = plateService.all(new PlateFilterBuilder().containsAnySamples(
        samples.stream().filter(s -> s instanceof SubmissionSample).collect(Collectors.toList()))
        .build());
    design.sourcePlatesField.setItems(plates);
    if (!plates.isEmpty()) {
      design.sourcePlatesField.setValue(plates.get(0));
    }
    samples.stream().map(s -> wellService.last(s)).filter(well -> well != null).findFirst()
        .ifPresent(w -> {
          plates.stream().filter(p -> p.getId().equals(w.getPlate().getId())).findAny()
              .ifPresent(p -> {
                design.sourcePlatesField.setValue(p);
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
        final MessageResource resources = view.getResources();
        return ValidationResult
            .error(resources.message(DESTINATION_WELL_IN_USE, container.getName()));
      }
    }
    return ValidationResult.ok();
  }

  private boolean validate() {
    logger.trace("Validate transfer");
    boolean valid = true;
    if (design.type.getValue().sourceType == TUBE) {
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
      logger.debug("Validation error: {}", result.getErrorMessage());
      return false;
    } else {
      return true;
    }
  }

  private ValidationResult validateDestinations() {
    final MessageResource resources = view.getResources();
    if (design.type.getValue().sourceType == TUBE) {
      Set<String> containerNames = new HashSet<>();
      for (Sample sample : samples) {
        Binder<SampleTransfer> binder = transferBinders.get(sample);
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
          return ValidationResult.error(message);
        }
      }
    } else {
      design.destinationPlatesField.setComponentError(null);
      Plate plate = view.destinationPlateForm.getValue();
      Well well = view.destinationPlateForm.getSelectedWell();
      if (well == null) {
        String message = resources.message(DESTINATION_PLATE_NO_SELECTION);
        design.destinationPlatesField.setComponentError(new UserError(message));
        return ValidationResult.error(message);
      }
      List<SampleContainer> sources = sources();
      List<Well> wells = plate.wells(new WellLocation(well.getRow(), well.getColumn()),
          new WellLocation(plate.getRowCount() - 1, plate.getColumnCount() - 1));
      Collections.sort(wells, new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
      for (int i = 0; i < sources.size(); i++) {
        if (i >= wells.size()) {
          String message =
              resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
          design.destinationPlatesField.setComponentError(new UserError(message));
          return ValidationResult.error(message);
        }
        Well destination = wells.get(i);
        if (destination.getSample() != null) {
          String message =
              resources.message(DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE, samples.size());
          design.destinationPlatesField.setComponentError(new UserError(message));
          return ValidationResult.error(message);
        }
      }
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateSourcePlate() {
    design.sourcePlatesField.setComponentError(null);
    MessageResource resources = view.getResources();
    Collection<Well> selectedWells = view.sourcePlateForm.getSelectedWells();
    if (selectedWells.isEmpty()) {
      String message = resources.message(SOURCE_PLATE_EMPTY);
      design.sourcePlatesField.setComponentError(new UserError(message));
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
      String message = resources.message(SOURCE_PLATE_SAMPLE_NOT_SELECTED, sample.getName());
      design.sourcePlatesField.setComponentError(new UserError(message));
      return ValidationResult.error(message);
    }
    return ValidationResult.ok();
  }

  private List<SampleContainer> sources() {
    if (design.type.getValue().sourceType == TUBE) {
      return new ArrayList<>(samples.stream().map(sample -> transferBinders.get(sample))
          .map(binder -> binder.getBean().getContainer()).collect(Collectors.toList()));
    } else {
      return new ArrayList<>(
          view.sourcePlateForm.getSelectedWells().stream().filter(well -> well.getSample() != null)
              .sorted(new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN))
              .collect(Collectors.toList()));
    }
  }

  private List<SampleContainer> destinations() {
    if (design.type.getValue().sourceType == TUBE) {
      return new ArrayList<>(samples.stream().map(sample -> transferBinders.get(sample))
          .map(binder -> binder.getBean().getDestinationContainer()).collect(Collectors.toList()));
    } else {
      Plate plate = view.destinationPlateForm.getValue();
      Well well = view.destinationPlateForm.getSelectedWell();
      int column = well.getColumn();
      int row = well.getRow();
      List<SampleContainer> destinations = new ArrayList<>();
      List<SampleContainer> sources = sources();
      for (int i = 0; i < sources.size(); i++) {
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
      if (design.type.getValue().destinationType == WELL) {
        // Reset samples.
        Plate database = plateService.get(design.destinationPlatesField.getValue() != null
            ? design.destinationPlatesField.getValue().getId()
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
