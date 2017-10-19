package ca.qc.ircm.proview.dilution.web;

import static ca.qc.ircm.proview.dilution.QDilutedSample.dilutedSample;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.TextField;
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
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Dilution view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DilutionViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DILUTIONS_PANEL = "dilutionsPanel";
  public static final String DILUTIONS = "dilutions";
  public static final String SAMPLE = dilutedSample.sample.getMetadata().getName();
  public static final String CONTAINER = dilutedSample.container.getMetadata().getName();
  public static final String SOURCE_VOLUME = dilutedSample.sourceVolume.getMetadata().getName();
  public static final String SOLVENT = dilutedSample.solvent.getMetadata().getName();
  public static final String SOLVENT_VOLUME = dilutedSample.solventVolume.getMetadata().getName();
  public static final String DOWN = "down";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_DILUTION = "dilution.invalid";
  private static final Logger logger = LoggerFactory.getLogger(DilutionViewPresenter.class);
  private DilutionView view;
  private DilutionViewDesign design;
  private boolean readOnly;
  private Binder<Dilution> binder = new BeanValidationBinder<>(Dilution.class);
  private List<DilutedSample> dilutions = new ArrayList<>();
  private Map<DilutedSample, Binder<DilutedSample>> dilutionBinders = new HashMap<>();
  private Map<DilutedSample, TextField> sourceVolumeFields = new HashMap<>();
  private Map<DilutedSample, TextField> solventFields = new HashMap<>();
  private Map<DilutedSample, TextField> solventVolumeFields = new HashMap<>();
  @Inject
  private DilutionService dilutionService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected DilutionViewPresenter() {
  }

  protected DilutionViewPresenter(DilutionService dilutionService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.dilutionService = dilutionService;
    this.sampleContainerService = sampleContainerService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(DilutionView view) {
    logger.debug("Dilution view");
    this.view = view;
    design = view.design;
    binder.setBean(new Dilution());
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.dilutionsPanel.addStyleName(DILUTIONS_PANEL);
    design.dilutionsPanel.setCaption(resources.message(DILUTIONS_PANEL));
    design.dilutions.addStyleName(DILUTIONS);
    design.dilutions.addStyleName(COMPONENTS);
    design.dilutions.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.dilutions.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER));
    design.dilutions.addColumn(ts -> sourceVolumeField(ts), new ComponentRenderer())
        .setId(SOURCE_VOLUME).setCaption(resources.message(SOURCE_VOLUME));
    design.dilutions.addColumn(ts -> solventField(ts), new ComponentRenderer()).setId(SOLVENT)
        .setCaption(resources.message(SOLVENT));
    design.dilutions.addColumn(ts -> solventVolumeField(ts), new ComponentRenderer())
        .setId(SOLVENT_VOLUME).setCaption(resources.message(SOLVENT_VOLUME));
    design.down.addStyleName(DOWN);
    design.down.addStyleName(BUTTON_SKIP_ROW);
    design.down.setCaption(resources.message(DOWN));
    design.down.setIcon(VaadinIcons.ARROW_DOWN);
    design.down.addClickListener(e -> down());
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
  }

  private Binder<DilutedSample> binder(DilutedSample ts) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<DilutedSample> binder = new BeanValidationBinder<>(DilutedSample.class);
    binder.setBean(ts);
    dilutionBinders.put(ts, binder);
    binder.forField(sourceVolumeField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SOURCE_VOLUME);
    binder.forField(solventField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").bind(SOLVENT);
    binder.forField(solventVolumeField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SOLVENT_VOLUME);
    return binder;
  }

  private TextField sourceVolumeField(DilutedSample ts) {
    if (sourceVolumeFields.get(ts) != null) {
      return sourceVolumeFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOURCE_VOLUME);
      field.setRequiredIndicatorVisible(true);
      sourceVolumeFields.put(ts, field);
      return field;
    }
  }

  private TextField solventField(DilutedSample ts) {
    if (solventFields.get(ts) != null) {
      return solventFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOLVENT);
      field.setRequiredIndicatorVisible(true);
      solventFields.put(ts, field);
      return field;
    }
  }

  private TextField solventVolumeField(DilutedSample ts) {
    if (solventVolumeFields.get(ts) != null) {
      return solventVolumeFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(SOLVENT_VOLUME);
      field.setRequiredIndicatorVisible(true);
      solventVolumeFields.put(ts, field);
      return field;
    }
  }

  private void updateReadOnly() {
    sourceVolumeFields.values().forEach(field -> field.setReadOnly(readOnly));
    solventFields.values().forEach(field -> field.setReadOnly(readOnly));
    solventVolumeFields.values().forEach(field -> field.setReadOnly(readOnly));
    design.down.setVisible(!readOnly);
    design.save.setVisible(!readOnly);
  }

  private void down() {
    if (!dilutions.isEmpty()) {
      String sourceVolume = sourceVolumeFields.get(dilutions.get(0)).getValue();
      String solvent = solventFields.get(dilutions.get(0)).getValue();
      String solventVolume = solventVolumeFields.get(dilutions.get(0)).getValue();
      sourceVolumeFields.values().forEach(field -> field.setValue(sourceVolume));
      solventFields.values().forEach(field -> field.setValue(solvent));
      solventVolumeFields.values().forEach(field -> field.setValue(solventVolume));
    }
  }

  private boolean validate() {
    logger.trace("Validate dilution");
    final MessageResource resources = view.getResources();
    if (dilutions.isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    for (Binder<DilutedSample> binder : dilutionBinders.values()) {
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Dilution validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving new dilution");
      Dilution dilution = binder.getBean();
      dilution.setTreatmentSamples(dilutions);
      dilutionService.insert(dilution);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(SAVED,
          dilutions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(DilutionView.VIEW_NAME, String.valueOf(dilution.getId()));
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
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      dilutions = view.savedContainers().stream().map(container -> {
        DilutedSample ts = new DilutedSample();
        ts.setSample(container.getSample());
        ts.setContainer(container);
        return ts;
      }).collect(Collectors.toList());
    } else if (parameters.startsWith("containers/")) {
      parameters = parameters.substring("containers/".length());
      logger.trace("Parsing containers from parameters");
      dilutions = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          DilutedSample ts = new DilutedSample();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          dilutions.add(ts);
        }
      } else {
        view.showWarning(view.getResources().message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set dilution {}", id);
        Dilution dilution = dilutionService.get(id);
        binder.setBean(dilution);
        if (dilution != null) {
          dilutions = dilution.getTreatmentSamples();
          readOnly = true;
        } else {
          view.showWarning(view.getResources().message(INVALID_DILUTION));
        }
      } catch (NumberFormatException e) {
        view.showWarning(view.getResources().message(INVALID_DILUTION));
      }
    }

    design.dilutions.setItems(dilutions);
    dilutions.stream().forEach(ts -> {
      binder(ts);
    });
    updateReadOnly();
  }
}
