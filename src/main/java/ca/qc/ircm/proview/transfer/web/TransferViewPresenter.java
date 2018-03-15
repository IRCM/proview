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
import static ca.qc.ircm.proview.transfer.QTransferedSample.transferedSample;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.gridItems;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;

import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateFilter;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.WellComparator;
import ca.qc.ircm.proview.plate.WellLocation;
import ca.qc.ircm.proview.plate.WellService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.transfer.TransferedSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.tube.TubeService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
  public static final String DELETED = "deleted";
  public static final String TRANSFER_TYPE_PANEL = "typePanel";
  public static final String TRANSFER_TYPE = "type";
  public static final String TRANSFERS_PANEL = "transfersPanel";
  public static final String TRANSFERS = "transfers";
  public static final String SAMPLE = transferedSample.sample.getMetadata().getName();
  public static final String CONTAINER = transferedSample.container.getMetadata().getName();
  public static final String DESTINATION_CONTAINER =
      transferedSample.destinationContainer.getMetadata().getName();
  public static final String DESTINATION_CONTAINER_DUPLICATE = DESTINATION_CONTAINER + ".duplicate";
  public static final String DESTINATION_COUNT = DESTINATION_CONTAINER + "Count";
  public static final String DESTINATION_COUNT_FIELD_WIDTH = "60px";
  public static final String DESTINATION_TUBE = DESTINATION_CONTAINER + "Tube";
  public static final String DESTINATION_WELL = DESTINATION_CONTAINER + "Well";
  public static final String DESTINATION_WELL_IN_USE = DESTINATION_WELL + ".inUse";
  public static final String DESTINATION = "destination";
  public static final String DESTINATION_PLATES = "destinationPlates";
  public static final String DESTINATION_PLATE_PANEL = "destinationPlatePanel";
  public static final String DESTINATION_PLATE = "destinationPlate";
  public static final String DESTINATION_PLATE_NO_SELECTION = "destinationPlate.noSelection";
  public static final String DESTINATION_PLATE_NOT_ENOUGH_FREE_SPACE =
      "destinationPlate.notEnoughFreeSpace";
  public static final String TEST = "test";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String DESTINATION_SAMPLE = sample.getMetadata().getName();
  public static final String DESTINATION_SAMPLE_NAME =
      DESTINATION_SAMPLE + "." + sample.name.getMetadata().getName();
  public static final String DOWN = "down";
  public static final String REMOVE = "remove";
  public static final String REMOVED = "removed";
  public static final String REMOVE_SAMPLES_FROM_CONTAINERS = "removeSamplesFromContainers";
  public static final String CONTAINERS_MODIFICATION = "containersModification";
  public static final String BAN_CONTAINERS = "banContainers";
  public static final String REMOVE_NOTHING = "removeNothing";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_TRANSFER = "transfer.invalid";
  public static final String DESTINATION_IN_USE = "destinationInUse";
  private static final Logger logger = LoggerFactory.getLogger(TransferViewPresenter.class);
  private TransferView view;
  private TransferViewDesign design;
  private Binder<Transfer> binder = new BeanValidationBinder<>(Transfer.class);
  private Map<SampleContainer, TransferedSample> firstTransfers = new HashMap<>();
  private ListDataProvider<TransferedSample> transfersDataProvider = DataProvider.ofItems();
  private Map<TransferedSample, Binder<TransferedSample>> transferBinders = new HashMap<>();
  private Map<TransferedSample, TextField> destinationCounts = new HashMap<>();
  private Map<TransferedSample, ComboBox<Tube>> destinationTubes = new HashMap<>();
  private Map<TransferedSample, ComboBox<Well>> destinationWells = new HashMap<>();
  private Map<TransferedSample, Button> downs = new HashMap<>();
  @Inject
  private TransferService transferService;
  @Inject
  private TubeService tubeService;
  @Inject
  private WellService wellService;
  @Inject
  private PlateService plateService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected TransferViewPresenter() {
  }

  protected TransferViewPresenter(TransferService transferService, TubeService tubeService,
      WellService wellService, PlateService plateService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.transferService = transferService;
    this.tubeService = tubeService;
    this.wellService = wellService;
    this.plateService = plateService;
    this.sampleContainerService = sampleContainerService;
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
    binder.setBean(new Transfer());
    prepareComponents();
    design.type.setValue(WELL);
    updateVisibility();
  }

  @SuppressWarnings("unchecked")
  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message(TITLE, applicationName));
    design.headerLabel.addStyleName(HEADER);
    design.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    design.headerLabel.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.typePanel.addStyleName(TRANSFER_TYPE_PANEL);
    design.typePanel.addStyleName(REQUIRED);
    design.typePanel.setCaption(resources.message(TRANSFER_TYPE_PANEL));
    design.type.addStyleName(TRANSFER_TYPE);
    design.type.setItemCaptionGenerator(type -> type.getLabel(locale));
    design.type.setItems(SampleContainerType.values());
    design.type.addValueChangeListener(e -> updateType());
    design.transfersPanel.addStyleName(TRANSFERS_PANEL);
    design.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    prepareTransfersGrid();
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
      ((ListDataProvider<Plate>) design.destinationPlatesField.getDataProvider()).getItems()
          .add(plate);
      design.destinationPlatesField.getDataProvider().refreshItem(plate);
      design.destinationPlatesField.setValue(plate);
    });
    design.destinationPlatesField
        .setItems(plateService.all(new PlateFilter().onlyProteomicPlates()));
    design.destinationPlatesField.addValueChangeListener(e -> updateDestinationPlate());
    design.destinationPlatePanel.addStyleName(DESTINATION_PLATE_PANEL);
    design.destinationPlatePanel.setVisible(false);
    view.destinationPlateForm.addStyleName(DESTINATION_PLATE);
    design.test.addStyleName(TEST);
    design.test.setCaption(resources.message(TEST));
    design.test.addClickListener(e -> test());
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanationPanel.setVisible(false);
    design.explanation.addStyleName(EXPLANATION);
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
    design.removeLayout.setVisible(false);
    design.remove.addStyleName(REMOVE);
    design.remove.setCaption(resources.message(REMOVE));
    design.remove.addClickListener(e -> remove());
    design.containersModification.addStyleName(CONTAINERS_MODIFICATION);
    design.containersModification.setItemCaptionGenerator(item -> resources.message(item));
    design.containersModification.setItems(REMOVE_SAMPLES_FROM_CONTAINERS, BAN_CONTAINERS,
        REMOVE_NOTHING);
    design.containersModification.setValue(REMOVE_SAMPLES_FROM_CONTAINERS);
  }

  private void prepareTransfersGrid() {
    final MessageResource resources = view.getResources();
    design.transfers.addStyleName(TRANSFERS);
    design.transfers.addStyleName(COMPONENTS);
    design.transfers.setDataProvider(transfersDataProvider);
    design.transfers.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.transfers
        .addColumn(ts -> ts.getContainer() != null ? ts.getContainer().getFullName() : "")
        .setId(CONTAINER).setCaption(resources.message(CONTAINER))
        .setStyleGenerator(ts -> ts.getContainer().isBanned() ? BANNED : "");
    design.transfers.addColumn(
        ts -> ts.getDestinationContainer() != null ? ts.getDestinationContainer().getFullName()
            : "")
        .setId(DESTINATION).setCaption(resources.message(DESTINATION)).setHidden(true);
    design.transfers.addColumn(ts -> destinationCount(ts), new ComponentRenderer())
        .setId(DESTINATION_COUNT).setCaption(resources.message(DESTINATION_COUNT))
        .setSortable(false);
    design.transfers.addColumn(ts -> destinationTube(ts), new ComponentRenderer())
        .setId(DESTINATION_TUBE).setCaption(resources.message(DESTINATION_TUBE)).setSortable(false);
    design.transfers.addColumn(ts -> destinationWell(ts), new ComponentRenderer())
        .setId(DESTINATION_WELL).setCaption(resources.message(DESTINATION_WELL)).setSortable(false);
    design.transfers.addColumn(ts -> downButton(ts), new ComponentRenderer()).setId(DOWN)
        .setCaption(resources.message(DOWN)).setSortable(false);
  }

  private Binder<TransferedSample> binder(TransferedSample ts) {
    Binder<TransferedSample> binder = new BeanValidationBinder<>(TransferedSample.class);
    binder.setBean(ts);
    transferBinders.put(ts, binder);
    return binder;
  }

  private TextField destinationCount(TransferedSample ts) {
    if (destinationCounts.get(ts) != null) {
      return destinationCounts.get(ts);
    } else if (firstTransfers.get(ts.getContainer()).equals(ts)) {
      TextField field = new TextField();
      field.addStyleName(DESTINATION_COUNT);
      field.setValue("1");
      field.setWidth(DESTINATION_COUNT_FIELD_WIDTH);
      field.addValueChangeListener(e -> updateCount(ts, e.getValue()));
      destinationCounts.put(ts, field);
      return field;
    } else {
      return null;
    }
  }

  private void updateCount(TransferedSample ts, String rawCount) {
    int count;
    try {
      count = Math.max(Integer.parseInt(rawCount), 1);
    } catch (NumberFormatException e) {
      count = 1;
    }

    SampleContainer container = ts.getContainer();
    List<TransferedSample> others = transfersDataProvider.getItems().stream()
        .filter(other -> other.getContainer().equals(container)).collect(Collectors.toList());
    int countDifference = count - others.size();
    while (countDifference > 0) {
      TransferedSample newTs = new TransferedSample();
      newTs.setSample(container.getSample());
      newTs.setContainer(container);
      transfersDataProvider.getItems().add(newTs);
      countDifference--;
    }
    while (countDifference < 0) {
      others.stream().filter(other -> !other.equals(firstTransfers.get(container))).findAny()
          .ifPresent(other -> {
            transfersDataProvider.getItems().remove(other);
            others.remove(other);
            transferBinders.remove(other);
            destinationCounts.remove(other);
            destinationTubes.remove(other);
            destinationWells.remove(other);
            downs.remove(other);
          });
      countDifference++;
    }
    transfersDataProvider.refreshAll();
    updateBinders();
  }

  private ComboBox<Tube> destinationTube(TransferedSample ts) {
    if (destinationTubes.get(ts) != null) {
      return destinationTubes.get(ts);
    } else {
      ComboBox<Tube> field = new ComboBox<>();
      field.addStyleName(DESTINATION_TUBE);
      field.setItems(Collections.emptyList());
      field.setItemCaptionGenerator(Tube::getName);
      field.setEmptySelectionAllowed(false);
      field.setRequiredIndicatorVisible(true);
      field.setNewItemHandler(name -> {
        Tube tube = new Tube(null, name);
        field.setItems(tube);
        field.setValue(tube);
      });
      destinationTubes.put(ts, field);
      return field;
    }
  }

  private ComboBox<Well> destinationWell(TransferedSample ts) {
    if (destinationWells.get(ts) != null) {
      return destinationWells.get(ts);
    } else {
      ComboBox<Well> field = new ComboBox<>();
      field.addStyleName(DESTINATION_WELL);
      field.setEmptySelectionAllowed(false);
      field.setRequiredIndicatorVisible(true);
      field.setItemCaptionGenerator(well -> well.getName());
      field.setItems(design.destinationPlatesField.getValue() != null
          ? design.destinationPlatesField.getValue().getWells()
          : Collections.emptyList());
      destinationWells.put(ts, field);
      return field;
    }
  }

  private Button downButton(TransferedSample ts) {
    if (downs.get(ts) != null) {
      return downs.get(ts);
    } else {
      MessageResource resources = view.getResources();
      Button down = new Button();
      down.addStyleName(DOWN);
      down.setIcon(VaadinIcons.ARROW_DOWN);
      down.setIconAlternateText(resources.message(DOWN));
      down.addClickListener(e -> down(ts));
      downs.put(ts, down);
      return down;
    }
  }

  private void updateType() {
    transfersDataProvider.refreshAll();
    updateBinders();
    updateVisibility();
  }

  private void updateBinders() {
    final MessageResource generalResources = view.getGeneralResources();
    final SampleContainerType destinationType = design.type.getValue();
    if (binder.getBean().getId() == null) {
      transfersDataProvider.getItems().forEach(ts -> {
        HasValue<? extends SampleContainer> destination =
            destinationType == WELL ? destinationWell(ts) : destinationTube(ts);
        ts.setDestinationContainer(destination.getValue());
        binder(ts).forField(destination).asRequired(generalResources.message(REQUIRED))
            .withValidator((container, context) -> validateDestinationContainer(container))
            .bind(DESTINATION_CONTAINER);
      });
    }
  }

  private void updateVisibility() {
    final SampleContainerType destinationType = design.type.getValue();
    design.transfers.getColumn(DESTINATION_COUNT)
        .setHidden(binder.getBean().getId() != null || destinationType != WELL);
    design.transfers.getColumn(DESTINATION_TUBE)
        .setHidden(binder.getBean().getId() != null || destinationType != TUBE);
    design.transfers.getColumn(DESTINATION_WELL)
        .setHidden(binder.getBean().getId() != null || destinationType != WELL);
    design.transfers.getColumn(DOWN)
        .setHidden(binder.getBean().getId() != null || destinationType != WELL);
    design.destination.setVisible(binder.getBean().getId() == null && destinationType == WELL);
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

  private void down(TransferedSample ts) {
    List<TransferedSample> tss = gridItems(design.transfers).collect(Collectors.toList());
    Well firstWell = destinationWells.get(ts).getValue();
    if (firstWell != null) {
      Plate plate = design.destinationPlatesField.getValue();
      List<Well> wells = plate.wells(new WellLocation(firstWell.getRow(), firstWell.getColumn()),
          new WellLocation(plate.getRowCount() - 1, plate.getColumnCount() - 1));
      wells.sort(new WellComparator(WellComparator.Compare.SAMPLE_ASSIGN));
      boolean copy = false;
      int index = 0;
      for (TransferedSample other : tss) {
        if (ts.equals(other)) {
          copy = true;
        }
        if (copy) {
          ComboBox<Well> field = destinationWells.get(other);
          field.setValue(wells.get(index++));
        }
      }
    }
  }

  private ValidationResult validateDestinationContainer(SampleContainer container) {
    if (container instanceof Tube) {
      if (!tubeService.nameAvailable(container.getName())) {
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
    final MessageResource resources = view.getResources();
    if (transfersDataProvider.getItems().isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    for (Binder<TransferedSample> binder : transferBinders.values()) {
      valid &= validate(binder);
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
    Set<String> containerNames = new HashSet<>();
    for (TransferedSample ts : transfersDataProvider.getItems()) {
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
    return ValidationResult.ok();
  }

  private List<TransferedSample> transfers() {
    return new ArrayList<>(transfersDataProvider.getItems().stream().collect(Collectors.toList()));
  }

  private void test() {
    if (validate()) {
      if (design.type.getValue() == WELL) {
        // Reset samples.
        Plate database = plateService.get(design.destinationPlatesField.getValue() != null
            ? design.destinationPlatesField.getValue().getId()
            : null);
        if (database == null) {
          design.destinationPlatesField.getValue().getWells().forEach(well -> well.setSample(null));
        } else {
          design.destinationPlatesField.getValue().getWells().forEach(
              well -> well.setSample(database.well(well.getRow(), well.getColumn()).getSample()));
        }
        // Set samples.
        List<TransferedSample> transfers = transfers();
        transfers.forEach(ts -> ts.getDestinationContainer().setSample(ts.getSample()));
        view.destinationPlateForm.setValue(design.destinationPlatesField.getValue());
      }
    }
  }

  private void save() {
    if (validate()) {
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      List<TransferedSample> transferedSamples = transfers();
      Transfer transfer = new Transfer();
      transfer.setTreatmentSamples(transferedSamples);
      try {
        transferService.insert(transfer);
      } catch (IllegalArgumentException e) {
        view.showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
        return;
      }
      view.showTrayNotification(resources.message(SAVED, transfersDataProvider.getItems().stream()
          .map(ts -> ts.getSample().getId()).distinct().count()));
      view.saveContainers(transferedSamples.stream().map(ts -> ts.getDestinationContainer())
          .collect(Collectors.toList()));
      view.navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
    }
  }

  private boolean validateRemove() {
    logger.trace("Validate remove digestion {}", binder.getBean());
    if (design.explanation.getValue().isEmpty()) {
      final MessageResource generalResources = view.getGeneralResources();
      String message = generalResources.message(REQUIRED);
      logger.debug("Validation error: {}", message);
      design.explanation.setComponentError(new UserError(message));
      view.showError(generalResources.message(FIELD_NOTIFICATION));
      return false;
    }
    return true;
  }

  private void remove() {
    if (validateRemove()) {
      logger.debug("Removing digestion {}", binder.getBean());
      final MessageResource resources = view.getResources();
      Transfer transfer = binder.getBean();
      try {
        transferService.undo(transfer, design.explanation.getValue(),
            design.containersModification.getValue() == REMOVE_SAMPLES_FROM_CONTAINERS,
            design.containersModification.getValue() == BAN_CONTAINERS);
      } catch (IllegalArgumentException e) {
        view.showError(resources.message(DESTINATION_IN_USE));
        return;
      }
      view.showTrayNotification(resources.message(REMOVED, transfersDataProvider.getItems().stream()
          .map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(TransferView.VIEW_NAME, String.valueOf(transfer.getId()));
    }
  }

  private boolean validateContainersParameters(String parameters) {
    boolean valid = true;
    String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
    if (rawIds.length < 1) {
      valid = false;
    }
    try {
      for (String rawId : rawIds) {
        Long id = Long.valueOf(rawId);
        if (sampleContainerService.get(id) == null) {
          valid = false;
        }
      }
    } catch (NumberFormatException e) {
      valid = false;
    }
    return valid;
  }

  /**
   * Called by view when entered.
   *
   * @param parameters
   *          view parameters
   */
  public void enter(String parameters) {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    List<TransferedSample> transfers = new ArrayList<>();
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      transfers = view.savedContainers().stream().map(container -> {
        TransferedSample ts = new TransferedSample();
        ts.setSample(container.getSample());
        ts.setContainer(container);
        return ts;
      }).collect(Collectors.toList());
      if (view.savedContainersFromMultipleUsers()) {
        view.showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
      }
    } else if (parameters.startsWith("containers/")) {
      parameters = parameters.substring("containers/".length());
      logger.trace("Parsing containers from parameters");
      transfers = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          TransferedSample ts = new TransferedSample();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          transfers.add(ts);
        }
      } else {
        view.showWarning(resources.message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set transfer {}", id);
        Transfer transfer = transferService.get(id);
        if (transfer != null) {
          binder.setBean(transfer);
          transfers = transfer.getTreatmentSamples();
          design.typePanel.setVisible(false);
          design.transfers.getColumn(DESTINATION).setHidden(false);
          design.deleted.setVisible(transfer.isDeleted());
          design.explanationPanel.setVisible(!transfer.isDeleted());
          design.save.setVisible(false);
          design.removeLayout.setVisible(!transfer.isDeleted());
        } else {
          view.showWarning(resources.message(INVALID_TRANSFER));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_TRANSFER));
      }
    }

    firstTransfers = transfers.stream()
        .collect(Collectors.toMap(ts -> ts.getContainer(), ts -> ts, (ts1, ts2) -> ts1));
    transfersDataProvider.getItems().addAll(transfers);
    transfersDataProvider.refreshAll();
    transfers.stream().forEach(ts -> {
      binder(ts);
      destinationTube(ts);
      destinationWell(ts);
    });
    updateType();
    updateVisibility();
  }
}
