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

package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.gridItems;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

/**
 * Dilution view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MsAnalysisViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String MS_ANALYSIS_PANEL = "msAnalysisPanel";
  public static final String MASS_DETECTION_INSTRUMENT =
      msAnalysis.massDetectionInstrument.getMetadata().getName();
  public static final String SOURCE = msAnalysis.source.getMetadata().getName();
  public static final String CONTAINERS_PANEL = "containersPanel";
  public static final String CONTAINERS = "containers";
  public static final String ACQUISITIONS_PANEL = "acquisitionsPanel";
  public static final String ACQUISITIONS = "acquisitions";
  public static final String SAMPLE = acquisition.sample.getMetadata().getName();
  public static final String CONTAINER = acquisition.container.getMetadata().getName();
  public static final String ACQUISITION_COUNT = acquisition.getMetadata().getName() + "Count";
  public static final String ACQUISITION_FILE = acquisition.acquisitionFile.getMetadata().getName();
  public static final String SAMPLE_LIST_NAME = acquisition.sampleListName.getMetadata().getName();
  public static final String COMMENT = acquisition.comment.getMetadata().getName();
  public static final String DOWN = "down";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String SAVE_ACQUISITION_REMOVED = "save.acquisitionRemoved";
  public static final String REMOVE = "remove";
  public static final String REMOVED = "removed";
  public static final String BAN_CONTAINERS = "banContainers";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_MS_ANALYSIS = "msAnalysis.invalid";
  private static final Logger logger = LoggerFactory.getLogger(MsAnalysisViewPresenter.class);
  private MsAnalysisView view;
  private MsAnalysisViewDesign design;
  private Binder<MsAnalysis> binder = new BeanValidationBinder<>(MsAnalysis.class);
  private ListDataProvider<SampleContainer> containersDataProvider = DataProvider.ofItems();
  private Map<SampleContainer, Binder<ItemCount>> acquisitionCountBinders = new HashMap<>();
  private Map<SampleContainer, TextField> acquisitionCountFields = new HashMap<>();
  private ListDataProvider<Acquisition> acquisitionsDataProvider = DataProvider.ofItems();
  private Map<Acquisition, Binder<Acquisition>> acquisitionBinders = new HashMap<>();
  private Map<Acquisition, TextField> acquisitionFileFields = new HashMap<>();
  private Map<Acquisition, TextField> sampleListNameFields = new HashMap<>();
  private Map<Acquisition, TextField> commentFields = new HashMap<>();
  private Map<Acquisition, Button> downButtons = new HashMap<>();
  @Inject
  private MsAnalysisService msAnalysisService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected MsAnalysisViewPresenter() {
  }

  protected MsAnalysisViewPresenter(MsAnalysisService msAnalysisService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.msAnalysisService = msAnalysisService;
    this.sampleContainerService = sampleContainerService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(MsAnalysisView view) {
    logger.debug("MS Analysis view");
    this.view = view;
    design = view.design;
    MsAnalysis defaultMsAnalysis = new MsAnalysis();
    defaultMsAnalysis.setMassDetectionInstrument(MassDetectionInstrument.VELOS);
    defaultMsAnalysis.setSource(MassDetectionInstrumentSource.ESI);
    binder.setBean(defaultMsAnalysis);
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    final Locale locale = view.getLocale();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.msAnalysisPanel.addStyleName(MS_ANALYSIS_PANEL);
    design.msAnalysisPanel.setCaption(resources.message(MS_ANALYSIS_PANEL));
    design.massDetectionInstrument.addStyleName(MASS_DETECTION_INSTRUMENT);
    design.massDetectionInstrument.setCaption(resources.message(MASS_DETECTION_INSTRUMENT));
    design.massDetectionInstrument.setEmptySelectionAllowed(false);
    design.massDetectionInstrument.setItems(MassDetectionInstrument.platformChoices());
    design.massDetectionInstrument
        .setItemCaptionGenerator(instrument -> instrument.getLabel(locale));
    binder.forField(design.massDetectionInstrument).asRequired(generalResources.message(REQUIRED))
        .bind(MASS_DETECTION_INSTRUMENT);
    design.source.addStyleName(SOURCE);
    design.source.setCaption(resources.message(SOURCE));
    design.source.setEmptySelectionAllowed(false);
    design.source.setItems(MassDetectionInstrumentSource.availables());
    design.source.setItemCaptionGenerator(source -> source.getLabel(locale));
    binder.forField(design.source).asRequired(generalResources.message(REQUIRED)).bind(SOURCE);
    design.containersPanel.addStyleName(CONTAINERS_PANEL);
    design.containersPanel.setCaption(resources.message(CONTAINERS_PANEL));
    design.containers.addStyleName(CONTAINERS);
    design.containers.addStyleName(COMPONENTS);
    design.containers.setDataProvider(containersDataProvider);
    design.containers.addColumn(container -> container.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.containers.addColumn(container -> container.getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER))
        .setStyleGenerator(container -> container.isBanned() ? BANNED : "");
    design.containers
        .addColumn(container -> acquisitionCountField(container), new ComponentRenderer())
        .setId(ACQUISITION_COUNT).setCaption(resources.message(ACQUISITION_COUNT))
        .setSortable(false);
    design.acquisitionsPanel.addStyleName(ACQUISITIONS_PANEL);
    design.acquisitionsPanel.setCaption(resources.message(ACQUISITIONS_PANEL));
    design.acquisitions.addStyleName(ACQUISITIONS);
    design.acquisitions.addStyleName(COMPONENTS);
    design.acquisitions.setDataProvider(acquisitionsDataProvider);
    design.acquisitions.addColumn(acquisition -> acquisition.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> acquisition.getContainer().getFullName())
        .setId(CONTAINER).setCaption(resources.message(CONTAINER))
        .setStyleGenerator(acquisition -> acquisition.getContainer().isBanned() ? BANNED : "")
        .setSortable(false);
    design.acquisitions
        .addColumn(acquisition -> sampleListNameField(acquisition), new ComponentRenderer())
        .setId(SAMPLE_LIST_NAME).setCaption(resources.message(SAMPLE_LIST_NAME)).setSortable(false);
    design.acquisitions
        .addColumn(acquisition -> acquisitionFileField(acquisition), new ComponentRenderer())
        .setId(ACQUISITION_FILE).setCaption(resources.message(ACQUISITION_FILE)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> commentField(acquisition), new ComponentRenderer())
        .setId(COMMENT).setCaption(resources.message(COMMENT)).setSortable(false);
    design.acquisitions.addColumn(acquisition -> downButton(acquisition), new ComponentRenderer())
        .setId(DOWN).setCaption(resources.message(DOWN)).setSortable(false);
    new GridRowDragger<>(design.acquisitions);
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
    design.banContainers.addStyleName(BAN_CONTAINERS);
    design.banContainers.setCaption(resources.message(BAN_CONTAINERS));
  }

  private Binder<ItemCount> binder(SampleContainer container) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<ItemCount> binder = new BeanValidationBinder<>(ItemCount.class);
    binder.setBean(new ItemCount((int) acquisitionsDataProvider.getItems().stream()
        .filter(ac -> container.getId().equals(ac.getContainer().getId())).count()));
    acquisitionCountBinders.put(container, binder);
    binder.forField(acquisitionCountField(container)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 1, Integer.MAX_VALUE), 1, Integer.MAX_VALUE))
        .bind(ItemCount::getCount, ItemCount::setCount);
    return binder;
  }

  private Binder<Acquisition> binder(Acquisition acquisition) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<Acquisition> binder = new BeanValidationBinder<>(Acquisition.class);
    binder.setBean(acquisition);
    acquisitionBinders.put(acquisition, binder);
    binder.forField(sampleListNameField(acquisition)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").bind(SAMPLE_LIST_NAME);
    binder.forField(acquisitionFileField(acquisition))
        .asRequired(generalResources.message(REQUIRED)).withNullRepresentation("")
        .bind(ACQUISITION_FILE);
    binder.forField(commentField(acquisition)).withNullRepresentation("").bind(COMMENT);
    return binder;
  }

  private TextField acquisitionCountField(SampleContainer container) {
    if (acquisitionCountFields.get(container) != null) {
      return acquisitionCountFields.get(container);
    } else {
      TextField field = new TextField();
      field.addStyleName(ACQUISITION_COUNT);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      field.setValue(Integer.toString(1));
      field.addValueChangeListener(e -> updateAcquisitionCount(container, e.getValue()));
      acquisitionCountFields.put(container, field);
      return field;
    }
  }

  private TextField sampleListNameField(Acquisition acquisition) {
    if (sampleListNameFields.get(acquisition) != null) {
      return sampleListNameFields.get(acquisition);
    } else {
      TextField field = new TextField();
      field.addStyleName(SAMPLE_LIST_NAME);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      sampleListNameFields.put(acquisition, field);
      return field;
    }
  }

  private TextField acquisitionFileField(Acquisition acquisition) {
    if (acquisitionFileFields.get(acquisition) != null) {
      return acquisitionFileFields.get(acquisition);
    } else {
      TextField field = new TextField();
      field.addStyleName(ACQUISITION_FILE);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      acquisitionFileFields.put(acquisition, field);
      return field;
    }
  }

  private TextField commentField(Acquisition acquisition) {
    if (commentFields.get(acquisition) != null) {
      return commentFields.get(acquisition);
    } else {
      TextField field = new TextField();
      field.addStyleName(COMMENT);
      field.setReadOnly(binder.getBean().isDeleted());
      commentFields.put(acquisition, field);
      return field;
    }
  }

  private Button downButton(Acquisition acquisition) {
    if (downButtons.get(acquisition) != null) {
      return downButtons.get(acquisition);
    } else {
      final MessageResource resources = view.getResources();
      Button button = new Button();
      button.addStyleName(DOWN);
      button.setIcon(VaadinIcons.ARROW_DOWN);
      button.setIconAlternateText(resources.message(DOWN));
      button.addClickListener(e -> down(acquisition));
      downButtons.put(acquisition, button);
      return button;
    }
  }

  private void down(Acquisition acquisition) {
    boolean copy = false;
    String sampleListName = sampleListNameFields.get(acquisition).getValue();
    String acquisitionFile = acquisitionFileFields.get(acquisition).getValue();
    String comment = commentFields.get(acquisition).getValue();
    for (Acquisition other : VaadinUtils.gridItems(design.acquisitions)
        .collect(Collectors.toList())) {
      if (acquisition.equals(other)) {
        copy = true;
      } else if (copy) {
        acquisitionFile = Named.incrementLastNumber(acquisitionFile);
        sampleListNameFields.get(other).setValue(sampleListName);
        acquisitionFileFields.get(other).setValue(acquisitionFile);
        commentFields.get(other).setValue(comment);
      }
    }
  }

  private void updateAcquisitionCount(SampleContainer container, String value) {
    int count;
    try {
      count = Math.max(Integer.parseInt(value), 1);
    } catch (NumberFormatException e) {
      // Ignore, user is typing.
      return;
    }
    int currentCount = (int) acquisitionsDataProvider.getItems().stream()
        .filter(ac -> container.getId().equals(ac.getContainer().getId())).count();
    if (count < currentCount) {
      List<Acquisition> remove = acquisitionsDataProvider.getItems().stream()
          .filter(ac -> container.getId().equals(ac.getContainer().getId())).skip(count)
          .collect(Collectors.toList());
      remove.stream().forEach(ac -> acquisitionsDataProvider.getItems().remove(ac));
    } else {
      IntStream.range(currentCount, count).forEach(i -> {
        Acquisition acquisition = new Acquisition();
        acquisition.setSample(container.getSample());
        acquisition.setContainer(container);
        acquisitionsDataProvider.getItems().add(acquisition);
        binder(acquisition);
      });
    }
    acquisitionsDataProvider.refreshAll();
  }

  private boolean validate() {
    logger.trace("Validate MS analysis");
    final MessageResource resources = view.getResources();
    if (acquisitionsDataProvider.getItems().isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    for (Binder<ItemCount> binder : acquisitionCountBinders.values()) {
      valid &= validate(binder);
    }
    for (Acquisition acquisition : acquisitionsDataProvider.getItems()) {
      Binder<Acquisition> binder = acquisitionBinders.get(acquisition);
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("MS analysis validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving new MS analysis");
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      MsAnalysis msAnalysis = binder.getBean();
      msAnalysis.setAcquisitions(gridItems(design.acquisitions).collect(Collectors.toList()));
      for (Acquisition acquisition : msAnalysis.getAcquisitions()) {
        acquisition.setNumberOfAcquisition(
            Integer.valueOf(acquisitionCountFields.get(acquisition.getContainer()).getValue()));
      }
      if (msAnalysis.getId() != null) {
        try {
          msAnalysisService.update(msAnalysis, design.explanation.getValue());
        } catch (IllegalArgumentException e) {
          view.showError(resources.message(SAVE_ACQUISITION_REMOVED));
          return;
        }
      } else {
        try {
          msAnalysisService.insert(msAnalysis);
        } catch (IllegalArgumentException e) {
          view.showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
          return;
        }
      }
      view.showTrayNotification(resources.message(SAVED, msAnalysis.getAcquisitions().stream()
          .map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(MsAnalysisView.VIEW_NAME, String.valueOf(msAnalysis.getId()));
    }
  }

  private boolean validateRemove() {
    logger.trace("Validate remove MS analysis {}", binder.getBean());
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
      logger.debug("Removing MS analysis {}", binder.getBean());
      MsAnalysis msAnalysis = binder.getBean();
      msAnalysisService.undo(msAnalysis, design.explanation.getValue(),
          design.banContainers.getValue());
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(REMOVED, msAnalysis.getAcquisitions().stream()
          .map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(MsAnalysisView.VIEW_NAME, String.valueOf(msAnalysis.getId()));
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
    List<SampleContainer> containers = new ArrayList<>();
    List<Acquisition> acquisitions = new ArrayList<>();
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      containers = view.savedContainers();
      acquisitions = view.savedContainers().stream().map(container -> {
        Acquisition acquisition = new Acquisition();
        acquisition.setSample(container.getSample());
        acquisition.setContainer(container);
        return acquisition;
      }).collect(Collectors.toList());
      if (view.savedContainersFromMultipleUsers()) {
        view.showWarning(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
      }
    } else if (parameters.startsWith("containers/")) {
      parameters = parameters.substring("containers/".length());
      logger.trace("Parsing containers from parameters");
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          containers.add(container);
          Acquisition acquisition = new Acquisition();
          acquisition.setSample(container.getSample());
          acquisition.setContainer(container);
          acquisitions.add(acquisition);
        }
      } else {
        view.showWarning(resources.message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set MS analysis {}", id);
        MsAnalysis msAnalysis = msAnalysisService.get(id);
        binder.setBean(msAnalysis);
        if (msAnalysis != null) {
          acquisitions = msAnalysis.getAcquisitions();
          containers = acquisitions.stream().map(ac -> ac.getContainer()).distinct()
              .collect(Collectors.toList());
          design.massDetectionInstrument.setReadOnly(msAnalysis.isDeleted());
          design.source.setReadOnly(msAnalysis.isDeleted());
          design.deleted.setVisible(msAnalysis.isDeleted());
          design.explanationPanel.setVisible(!msAnalysis.isDeleted());
          design.save.setVisible(!msAnalysis.isDeleted());
          design.removeLayout.setVisible(!msAnalysis.isDeleted());
        } else {
          view.showWarning(resources.message(INVALID_MS_ANALYSIS));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_MS_ANALYSIS));
      }
    }

    containersDataProvider.getItems().addAll(containers);
    containersDataProvider.refreshAll();
    acquisitionsDataProvider.getItems().clear();
    acquisitionsDataProvider.getItems().addAll(acquisitions);
    acquisitionsDataProvider.refreshAll();
    containers.stream().forEach(sc -> {
      binder(sc);
    });
    acquisitions.stream().forEach(ac -> {
      binder(ac);
    });
  }

  private static class ItemCount {
    private Integer count;

    private ItemCount() {
    }

    private ItemCount(Integer count) {
      this.count = count;
    }

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }
  }
}
