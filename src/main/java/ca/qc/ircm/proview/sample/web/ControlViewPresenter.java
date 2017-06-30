package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.QControl.control;
import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.UserError;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Add control view presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControlViewPresenter implements BinderValidator {
  public static final String TITLE = "title";
  public static final String HEADER = "header";
  public static final String SAMPLE_PANEL = "samplePanel";
  public static final String NAME = control.name.getMetadata().getName();
  public static final String SUPPORT = control.support.getMetadata().getName();
  public static final String QUANTITY = control.quantity.getMetadata().getName();
  public static final String VOLUME = control.volume.getMetadata().getName();
  public static final String CONTROL_TYPE = control.controlType.getMetadata().getName();
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String STANDARD_COUNT = "standardCount";
  public static final String STANDARDS = "standards";
  public static final String STANDARD = control.standards.getMetadata().getName();
  public static final int STANDARDS_TABLE_LENGTH = 4;
  public static final String STANDARD_NAME = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENTS = standard.comments.getMetadata().getName();
  public static final String FILL_STANDARDS = "fillStandards";
  public static final String EXAMPLE = "example";
  public static final String FILL_BUTTON_STYLE = "skip-row";
  public static final String JUSTIFICATION = "justification";
  public static final String SAVE = "save";
  public static final String INVALID_SAMPLE = "sample.invalid";
  private static final int MAX_STANDARD_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(ControlViewPresenter.class);
  private ControlView view;
  private boolean editable = false;
  private Binder<Control> sampleBinder = new BeanValidationBinder<>(Control.class);
  private Binder<ItemCount> standardCountBinder = new Binder<>(ItemCount.class);
  private ListDataProvider<Standard> standardsDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<Standard, Binder<Standard>> standardBinders = new HashMap<>();
  private Map<Standard, TextField> standardNameFields = new HashMap<>();
  private Map<Standard, TextField> standardQuantityFields = new HashMap<>();
  private Map<Standard, TextField> standardCommentsFields = new HashMap<>();
  @Inject
  private ControlService controlService;
  @Inject
  private AuthorizationService authorizationService;
  @Value("${spring.application.name}")
  private String applicationName;

  protected ControlViewPresenter() {
  }

  protected ControlViewPresenter(ControlService controlService,
      AuthorizationService authorizationService, String applicationName) {
    this.controlService = controlService;
    this.authorizationService = authorizationService;
    this.applicationName = applicationName;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ControlView view) {
    logger.debug("Add control view");
    this.view = view;
    editable = authorizationService.hasAdminRole();
    final MessageResource resources = view.getResources();
    view.setTitle(resources.message(TITLE, applicationName));
    view.headerLabel.addStyleName(HEADER);
    view.headerLabel.addStyleName(ValoTheme.LABEL_H1);
    view.headerLabel.setValue(resources.message(HEADER));
    prepareSamplesComponents();
    prepareStandardsComponents();
    updateStandardsTable(view.standardCountField.getValue());
    view.justificationLayout.setVisible(false);
    view.justificationField.addStyleName(JUSTIFICATION);
    view.justificationField.setCaption(resources.message(JUSTIFICATION));
    view.justificationField.setRequiredIndicatorVisible(true);
    view.saveButton.addStyleName(SAVE);
    view.saveButton.setCaption(resources.message(SAVE));
    view.saveButton.setVisible(editable);
    view.saveButton.addClickListener(e -> save());
  }

  private void prepareSamplesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.samplePanel.addStyleName(SAMPLE_PANEL);
    view.samplePanel.setCaption(resources.message(SAMPLE_PANEL));
    view.nameField.addStyleName(NAME);
    view.nameField.setCaption(resources.message(NAME));
    view.nameField.setReadOnly(!editable);
    sampleBinder.forField(view.nameField).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").withValidator(validateSampleName()).bind(NAME);
    view.supportField.addStyleName(SUPPORT);
    view.supportField.setCaption(resources.message(SUPPORT));
    view.supportField.setItems(SampleSupport.values());
    view.supportField.setItemCaptionGenerator(support -> support.getLabel(locale));
    view.supportField.setReadOnly(!editable);
    sampleBinder.forField(view.supportField).asRequired(generalResources.message(REQUIRED))
        .bind(SUPPORT);
    view.quantityField.addStyleName(QUANTITY);
    view.quantityField.setCaption(resources.message(QUANTITY));
    view.quantityField.setPlaceholder(resources.message(QUANTITY + "." + EXAMPLE));
    view.quantityField.setReadOnly(!editable);
    sampleBinder.forField(view.quantityField).withNullRepresentation("").bind(QUANTITY);
    view.volumeField.addStyleName(VOLUME);
    view.volumeField.setCaption(resources.message(VOLUME));
    view.volumeField.setReadOnly(!editable);
    sampleBinder.forField(view.volumeField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(VOLUME);
    view.controlTypeField.addStyleName(CONTROL_TYPE);
    view.controlTypeField.setCaption(resources.message(CONTROL_TYPE));
    view.controlTypeField.setItems(ControlType.values());
    view.controlTypeField.setItemCaptionGenerator(type -> type.getLabel(locale));
    view.controlTypeField.setReadOnly(!editable);
    sampleBinder.forField(view.controlTypeField).asRequired(generalResources.message(REQUIRED))
        .bind(CONTROL_TYPE);
  }

  private void prepareStandardsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.standardsPanel.addStyleName(STANDARDS_PANEL);
    view.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    view.standardCountField.addStyleName(STANDARD_COUNT);
    view.standardCountField.setCaption(resources.message(STANDARD_COUNT));
    view.standardCountField.setReadOnly(!editable);
    standardCountBinder.forField(view.standardCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_STANDARD_COUNT), 0, MAX_STANDARD_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    view.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(view.standardCountField.getValue()));
    view.standardsGrid.addStyleName(STANDARD);
    view.standardsGrid.addStyleName(COMPONENTS);
    view.standardsGrid.setDataProvider(standardsDataProvider);
    view.standardsGrid
        .addColumn(standard -> standardNameTextField(standard), new ComponentRenderer())
        .setId(STANDARD_NAME).setCaption(resources.message(STANDARD + "." + STANDARD_NAME));
    view.standardsGrid
        .addColumn(standard -> standardQuantityTextField(standard), new ComponentRenderer())
        .setId(STANDARD_QUANTITY).setCaption(resources.message(STANDARD + "." + STANDARD_QUANTITY));
    view.standardsGrid
        .addColumn(standard -> standardCommentsTextField(standard), new ComponentRenderer())
        .setId(STANDARD_COMMENTS).setCaption(resources.message(STANDARD + "." + STANDARD_COMMENTS));
    view.fillStandardsButton.addStyleName(FILL_STANDARDS);
    view.fillStandardsButton.addStyleName(FILL_BUTTON_STYLE);
    view.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS));
    view.fillStandardsButton.setIcon(VaadinIcons.ARROW_DOWN);
    view.fillStandardsButton.setVisible(editable);
    view.fillStandardsButton.addClickListener(e -> fillStandards());
  }

  private TextField standardNameTextField(Standard standard) {
    if (standardNameFields.containsKey(standard)) {
      return standardNameFields.get(standard);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(STANDARD + "." + STANDARD_NAME);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(!editable);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_NAME);
      standardBinders.put(standard, binder);
      standardNameFields.put(standard, field);
      return field;
    }
  }

  private TextField standardQuantityTextField(Standard standard) {
    if (standardQuantityFields.containsKey(standard)) {
      return standardQuantityFields.get(standard);
    } else {
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(STANDARD + "." + STANDARD_QUANTITY);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(!editable);
      field.setPlaceholder(resources.message(STANDARD + "." + STANDARD_QUANTITY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_QUANTITY);
      standardBinders.put(standard, binder);
      standardQuantityFields.put(standard, field);
      return field;
    }
  }

  private TextField standardCommentsTextField(Standard standard) {
    if (standardCommentsFields.containsKey(standard)) {
      return standardCommentsFields.get(standard);
    } else {
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(STANDARD + "." + STANDARD_COMMENTS);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(!editable);
      binder.forField(field).withNullRepresentation("").bind(STANDARD_COMMENTS);
      standardBinders.put(standard, binder);
      standardCommentsFields.put(standard, field);
      return field;
    }
  }

  private void updateStandardsTable(String countValue) {
    if (standardCountBinder.isValid()) {
      int count;
      try {
        count = Math.max(Integer.parseInt(countValue), 0);
      } catch (NumberFormatException e) {
        count = 0;
      }
      while (standardsDataProvider.getItems().size() > count) {
        Standard remove = standardsDataProvider.getItems().stream()
            .skip(standardsDataProvider.getItems().size() - 1).findFirst().orElse(null);
        standardsDataProvider.getItems().remove(remove);
      }
      while (standardsDataProvider.getItems().size() < count) {
        standardsDataProvider.getItems().add(new Standard());
      }
      view.standardsTableLayout.setVisible(count > 0);
      standardsDataProvider.refreshAll();
    }
  }

  private void fillStandards() {
    Standard first = standardsDataProvider.getItems().iterator().next();
    String name = first.getName();
    String quantity = first.getQuantity();
    String comments = first.getComments();
    standardsDataProvider.getItems().forEach(standard -> {
      standard.setName(name);
      standard.setQuantity(quantity);
      standard.setComments(comments);
      standardBinders.get(standard).setBean(standard);
    });
    standardsDataProvider.refreshAll();
  }

  private Validator<String> validateSampleName() {
    return (value, context) -> {
      if (value == null || value.isEmpty()) {
        return ValidationResult.ok();
      }
      MessageResource generalResources = view.getGeneralResources();
      if (!Pattern.matches("\\w*", value)) {
        return ValidationResult.error(generalResources.message(ONLY_WORDS));
      }
      return ValidationResult.ok();
    };
  }

  private boolean validate() {
    boolean valid = true;
    valid &= validate(sampleBinder);
    valid &= validate(standardCountBinder);
    for (Standard standard : standardsDataProvider.getItems()) {
      valid &= validate(standardBinders.get(standard));
    }
    if (!newControl() && view.justificationField.getValue().isEmpty()) {
      logger.trace("Justification field is required");
      final MessageResource generalResources = view.getGeneralResources();
      view.justificationField.setComponentError(new UserError(generalResources.message(REQUIRED)));
      valid = false;
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Control field validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private boolean newControl() {
    return sampleBinder.getBean().getId() == null;
  }

  private void save() {
    if (validate()) {
      Control control = sampleBinder.getBean();
      copyStandardsFromTableToSample(control);
      logger.debug("Save control {}", control);
      if (newControl()) {
        controlService.insert(control);
      } else {
        controlService.update(control, view.justificationField.getValue());
      }
    }
  }

  private void copyStandardsFromTableToSample(Control sample) {
    sample.setStandards(new ArrayList<>());
    for (Standard standard : standardsDataProvider.getItems()) {
      Standard copy = new Standard();
      copy.setName(standard.getName());
      copy.setQuantity(standard.getQuantity());
      copy.setComments(standard.getComments());
      sample.getStandards().add(copy);
    }
  }

  private boolean validateParameters(String parameters) {
    boolean valid = true;
    try {
      Long id = Long.valueOf(parameters);
      if (controlService.get(id) == null) {
        valid = false;
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
    Control control = null;
    if (parameters != null && !parameters.isEmpty()) {
      if (validateParameters(parameters)) {
        Long id = Long.valueOf(parameters);
        control = controlService.get(id);
      } else {
        view.showWarning(view.getResources().message(INVALID_SAMPLE));
      }
    }

    if (control == null) {
      control = new Control();
      control.setSupport(SampleSupport.SOLUTION);
      control.setControlType(ControlType.NEGATIVE_CONTROL);
      control.setStandards(new ArrayList<>());
    }
    standardCountBinder.setBean(new ItemCount(control.getStandards().size()));
    standardsDataProvider.getItems().clear();
    standardsDataProvider.getItems().addAll(control.getStandards());
    standardsDataProvider.refreshAll();
    sampleBinder.setBean(control);
    if (!newControl() && authorizationService.hasAdminRole()) {
      view.justificationLayout.setVisible(true);
    }
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
