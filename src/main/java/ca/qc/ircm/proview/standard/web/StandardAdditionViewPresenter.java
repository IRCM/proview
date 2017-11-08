package ca.qc.ircm.proview.standard.web;

import static ca.qc.ircm.proview.standard.QAddedStandard.addedStandard;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.gridItems;
import static ca.qc.ircm.proview.web.WebConstants.BANNED;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.SAVED_SAMPLE_FROM_MULTIPLE_USERS;

import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.standard.StandardAdditionService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
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
 * StandardAddition view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StandardAdditionViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String DELETED = "deleted";
  public static final String STANDARD_ADDITIONS_PANEL = "standardAdditionsPanel";
  public static final String STANDARD_ADDITIONS = "standardAdditions";
  public static final String SAMPLE = addedStandard.sample.getMetadata().getName();
  public static final String CONTAINER = addedStandard.container.getMetadata().getName();
  public static final String NAME = addedStandard.name.getMetadata().getName();
  public static final String QUANTITY = addedStandard.quantity.getMetadata().getName();
  public static final String COMMENT = addedStandard.comment.getMetadata().getName();
  public static final String DOWN = "down";
  public static final String EXPLANATION = "explanation";
  public static final String EXPLANATION_PANEL = EXPLANATION + "Panel";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  public static final String REMOVE = "remove";
  public static final String REMOVED = "removed";
  public static final String BAN_CONTAINERS = "banContainers";
  public static final String NO_CONTAINERS = "containers.empty";
  public static final String INVALID_CONTAINERS = "containers.invalid";
  public static final String SPLIT_CONTAINER_PARAMETERS = ",";
  public static final String INVALID_STANDARD_ADDITION = "standardAddition.invalid";
  private static final Logger logger = LoggerFactory.getLogger(StandardAdditionViewPresenter.class);
  private StandardAdditionView view;
  private StandardAdditionViewDesign design;
  private Binder<StandardAddition> binder = new BeanValidationBinder<>(StandardAddition.class);
  private List<AddedStandard> standardAdditions = new ArrayList<>();
  private Map<AddedStandard, Binder<AddedStandard>> standardAdditionBinders = new HashMap<>();
  private Map<AddedStandard, TextField> nameFields = new HashMap<>();
  private Map<AddedStandard, TextField> quantityFields = new HashMap<>();
  private Map<AddedStandard, TextField> commentFields = new HashMap<>();
  @Inject
  private StandardAdditionService standardAdditionService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected StandardAdditionViewPresenter() {
  }

  protected StandardAdditionViewPresenter(StandardAdditionService standardAdditionService,
      SampleContainerService sampleContainerService, String applicationName) {
    this.standardAdditionService = standardAdditionService;
    this.sampleContainerService = sampleContainerService;
    this.applicationName = applicationName;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(StandardAdditionView view) {
    logger.debug("StandardAddition view");
    this.view = view;
    design = view.design;
    binder.setBean(new StandardAddition());
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    design.header.addStyleName(HEADER);
    design.header.setValue(resources.message(HEADER));
    design.deleted.addStyleName(DELETED);
    design.deleted.setValue(resources.message(DELETED));
    design.deleted.setVisible(false);
    design.standardAdditionsPanel.addStyleName(STANDARD_ADDITIONS_PANEL);
    design.standardAdditionsPanel.setCaption(resources.message(STANDARD_ADDITIONS_PANEL));
    design.standardAdditions.addStyleName(STANDARD_ADDITIONS);
    design.standardAdditions.addStyleName(COMPONENTS);
    design.standardAdditions.addColumn(ts -> ts.getSample().getName()).setId(SAMPLE)
        .setCaption(resources.message(SAMPLE));
    design.standardAdditions.addColumn(ts -> ts.getContainer().getFullName()).setId(CONTAINER)
        .setCaption(resources.message(CONTAINER))
        .setStyleGenerator(ts -> ts.getContainer().isBanned() ? BANNED : "");
    design.standardAdditions.addColumn(ts -> nameField(ts), new ComponentRenderer()).setId(NAME)
        .setCaption(resources.message(NAME)).setSortable(false);
    design.standardAdditions.addColumn(ts -> quantityField(ts), new ComponentRenderer())
        .setId(QUANTITY).setCaption(resources.message(QUANTITY)).setSortable(false);
    design.standardAdditions.addColumn(ts -> commentField(ts), new ComponentRenderer())
        .setId(COMMENT).setCaption(resources.message(COMMENT)).setSortable(false);
    design.down.addStyleName(DOWN);
    design.down.addStyleName(BUTTON_SKIP_ROW);
    design.down.setCaption(resources.message(DOWN));
    design.down.setIcon(VaadinIcons.ARROW_DOWN);
    design.down.addClickListener(e -> down());
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

  private Binder<AddedStandard> binder(AddedStandard ts) {
    final MessageResource generalResources = view.getGeneralResources();
    Binder<AddedStandard> binder = new BeanValidationBinder<>(AddedStandard.class);
    binder.setBean(ts);
    standardAdditionBinders.put(ts, binder);
    binder.forField(nameField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").bind(NAME);
    binder.forField(quantityField(ts)).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").bind(QUANTITY);
    binder.forField(commentField(ts)).withNullRepresentation("").bind(COMMENT);
    return binder;
  }

  private TextField nameField(AddedStandard ts) {
    if (nameFields.get(ts) != null) {
      return nameFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(NAME);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      nameFields.put(ts, field);
      return field;
    }
  }

  private TextField quantityField(AddedStandard ts) {
    if (quantityFields.get(ts) != null) {
      return quantityFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(QUANTITY);
      field.setRequiredIndicatorVisible(true);
      field.setReadOnly(binder.getBean().isDeleted());
      quantityFields.put(ts, field);
      return field;
    }
  }

  private TextField commentField(AddedStandard ts) {
    if (commentFields.get(ts) != null) {
      return commentFields.get(ts);
    } else {
      TextField field = new TextField();
      field.addStyleName(COMMENT);
      field.setReadOnly(binder.getBean().isDeleted());
      commentFields.put(ts, field);
      return field;
    }
  }

  private void down() {
    if (!standardAdditions.isEmpty()) {
      AddedStandard first = gridItems(design.standardAdditions).findFirst().orElse(null);
      String name = nameFields.get(first).getValue();
      String quantity = quantityFields.get(first).getValue();
      String comment = commentFields.get(first).getValue();
      nameFields.values().forEach(field -> field.setValue(name));
      quantityFields.values().forEach(field -> field.setValue(quantity));
      commentFields.values().forEach(field -> field.setValue(comment));
    }
  }

  private boolean validate() {
    logger.trace("Validate standard addition");
    final MessageResource resources = view.getResources();
    if (standardAdditions.isEmpty()) {
      String message = resources.message(NO_CONTAINERS);
      logger.debug("Validation error: {}", message);
      view.showError(message);
      return false;
    }
    boolean valid = true;
    valid &= validate(binder);
    for (Binder<AddedStandard> binder : standardAdditionBinders.values()) {
      valid &= validate(binder);
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Standard addition validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private void save() {
    if (validate()) {
      logger.debug("Saving new standard addition");
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      StandardAddition standardAddition = binder.getBean();
      standardAddition.setTreatmentSamples(standardAdditions);
      if (standardAddition.getId() != null) {
        standardAdditionService.update(standardAddition, design.explanation.getValue());
      } else {
        try {
          standardAdditionService.insert(standardAddition);
        } catch (IllegalArgumentException e) {
          view.showError(generalResources.message(SAVED_SAMPLE_FROM_MULTIPLE_USERS));
          return;
        }
      }
      view.showTrayNotification(resources.message(SAVED,
          standardAdditions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(StandardAdditionView.VIEW_NAME, String.valueOf(standardAddition.getId()));
    }
  }

  private boolean validateRemove() {
    logger.trace("Validate remove standard addition {}", binder.getBean());
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
      logger.debug("Removing standard addition {}", binder.getBean());
      StandardAddition standardAddition = binder.getBean();
      standardAdditionService.undo(standardAddition, design.explanation.getValue(),
          design.banContainers.getValue());
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(REMOVED,
          standardAdditions.stream().map(ts -> ts.getSample().getId()).distinct().count()));
      view.navigateTo(StandardAdditionView.VIEW_NAME, String.valueOf(standardAddition.getId()));
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
    if (parameters == null || parameters.isEmpty()) {
      logger.trace("Recovering containers from session");
      standardAdditions = view.savedContainers().stream().map(container -> {
        AddedStandard ts = new AddedStandard();
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
      standardAdditions = new ArrayList<>();
      if (validateContainersParameters(parameters)) {
        String[] rawIds = parameters.split(SPLIT_CONTAINER_PARAMETERS, -1);
        for (String rawId : rawIds) {
          Long id = Long.valueOf(rawId);
          SampleContainer container = sampleContainerService.get(id);
          AddedStandard ts = new AddedStandard();
          ts.setSample(container.getSample());
          ts.setContainer(container);
          standardAdditions.add(ts);
        }
      } else {
        view.showWarning(resources.message(INVALID_CONTAINERS));
      }
    } else {
      try {
        Long id = Long.valueOf(parameters);
        logger.debug("Set standardAddition {}", id);
        StandardAddition standardAddition = standardAdditionService.get(id);
        binder.setBean(standardAddition);
        if (standardAddition != null) {
          standardAdditions = standardAddition.getTreatmentSamples();
          design.deleted.setVisible(standardAddition.isDeleted());
          design.explanationPanel.setVisible(!standardAddition.isDeleted());
          design.save.setVisible(!standardAddition.isDeleted());
          design.removeLayout.setVisible(!standardAddition.isDeleted());
        } else {
          view.showWarning(resources.message(INVALID_STANDARD_ADDITION));
        }
      } catch (NumberFormatException e) {
        view.showWarning(resources.message(INVALID_STANDARD_ADDITION));
      }
    }

    design.standardAdditions.setItems(standardAdditions);
    standardAdditions.stream().forEach(ts -> {
      binder(ts);
    });
  }
}
