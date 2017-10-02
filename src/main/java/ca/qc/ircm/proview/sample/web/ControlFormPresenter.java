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
 * Control form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ControlFormPresenter implements BinderValidator {
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
  public static final String EXPLANATION_PANEL = "explanationPanel";
  public static final String EXPLANATION = "explanation";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  private static final int MAX_STANDARD_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(ControlFormPresenter.class);
  private ControlForm view;
  private ControlFormDesign design;
  private boolean readOnly = false;
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

  protected ControlFormPresenter() {
  }

  protected ControlFormPresenter(ControlService controlService) {
    this.controlService = controlService;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(ControlForm view) {
    logger.debug("Control form");
    this.view = view;
    design = view.design;
    sampleBinder.setBean(new Control());
    final MessageResource resources = view.getResources();
    prepareSamplesComponents();
    prepareStandardsComponents();
    updateStandardsTable(design.standardCountField.getValue());
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.addStyleName(REQUIRED);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanationPanel.setVisible(false);
    design.explanation.addStyleName(EXPLANATION);
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
    design.saveButton.addClickListener(e -> save());
    updateReadOnly();
  }

  private void prepareSamplesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.samplePanel.addStyleName(SAMPLE_PANEL);
    design.samplePanel.setCaption(resources.message(SAMPLE_PANEL));
    design.nameField.addStyleName(NAME);
    design.nameField.setCaption(resources.message(NAME));
    sampleBinder.forField(design.nameField).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("").withValidator(validateSampleName()).bind(NAME);
    design.supportField.addStyleName(SUPPORT);
    design.supportField.setCaption(resources.message(SUPPORT));
    design.supportField.setItems(SampleSupport.values());
    design.supportField.setItemCaptionGenerator(support -> support.getLabel(locale));
    sampleBinder.forField(design.supportField).asRequired(generalResources.message(REQUIRED))
        .bind(SUPPORT);
    design.quantityField.addStyleName(QUANTITY);
    design.quantityField.setCaption(resources.message(QUANTITY));
    design.quantityField.setPlaceholder(resources.message(QUANTITY + "." + EXAMPLE));
    sampleBinder.forField(design.quantityField).withNullRepresentation("").bind(QUANTITY);
    design.volumeField.addStyleName(VOLUME);
    design.volumeField.setCaption(resources.message(VOLUME));
    sampleBinder.forField(design.volumeField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(VOLUME);
    design.controlTypeField.addStyleName(CONTROL_TYPE);
    design.controlTypeField.setCaption(resources.message(CONTROL_TYPE));
    design.controlTypeField.setItems(ControlType.values());
    design.controlTypeField.setItemCaptionGenerator(type -> type.getLabel(locale));
    sampleBinder.forField(design.controlTypeField).asRequired(generalResources.message(REQUIRED))
        .bind(CONTROL_TYPE);
  }

  private void prepareStandardsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.standardsPanel.addStyleName(STANDARDS_PANEL);
    design.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    design.standardCountField.addStyleName(STANDARD_COUNT);
    design.standardCountField.setCaption(resources.message(STANDARD_COUNT));
    standardCountBinder.forField(design.standardCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_STANDARD_COUNT), 0, MAX_STANDARD_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(design.standardCountField.getValue()));
    design.standardsGrid.addStyleName(STANDARD);
    design.standardsGrid.addStyleName(COMPONENTS);
    design.standardsGrid.setDataProvider(standardsDataProvider);
    design.standardsGrid
        .addColumn(standard -> standardNameTextField(standard), new ComponentRenderer())
        .setId(STANDARD_NAME).setCaption(resources.message(STANDARD + "." + STANDARD_NAME));
    design.standardsGrid
        .addColumn(standard -> standardQuantityTextField(standard), new ComponentRenderer())
        .setId(STANDARD_QUANTITY).setCaption(resources.message(STANDARD + "." + STANDARD_QUANTITY));
    design.standardsGrid
        .addColumn(standard -> standardCommentsTextField(standard), new ComponentRenderer())
        .setId(STANDARD_COMMENTS).setCaption(resources.message(STANDARD + "." + STANDARD_COMMENTS));
    design.fillStandardsButton.addStyleName(FILL_STANDARDS);
    design.fillStandardsButton.addStyleName(FILL_BUTTON_STYLE);
    design.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS));
    design.fillStandardsButton.setIcon(VaadinIcons.ARROW_DOWN);
    design.fillStandardsButton.addClickListener(e -> fillStandards());
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
      field.setReadOnly(readOnly);
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
      field.setReadOnly(readOnly);
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
      field.setReadOnly(readOnly);
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
      design.standardsTableLayout.setVisible(count > 0);
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

  private void updateReadOnly() {
    design.nameField.setReadOnly(readOnly);
    design.quantityField.setReadOnly(readOnly);
    design.volumeField.setReadOnly(readOnly);
    design.supportField.setReadOnly(readOnly);
    design.controlTypeField.setReadOnly(readOnly);
    design.standardCountField.setReadOnly(readOnly);
    design.fillStandardsButton.setVisible(!readOnly);
    design.saveButton.setVisible(!readOnly);
    if (!newControl()) {
      design.explanationPanel.setVisible(!readOnly);
    }
    standardBinders.values().forEach(binder -> {
      binder.getBinding(STANDARD_NAME)
          .ifPresent(binding -> binding.getField().setReadOnly(readOnly));
      binder.getBinding(STANDARD_QUANTITY)
          .ifPresent(binding -> binding.getField().setReadOnly(readOnly));
      binder.getBinding(STANDARD_COMMENTS)
          .ifPresent(binding -> binding.getField().setReadOnly(readOnly));
    });
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
    if (!newControl() && design.explanation.getValue().isEmpty()) {
      logger.trace("Explanation field is required");
      final MessageResource generalResources = view.getGeneralResources();
      design.explanation.setComponentError(new UserError(generalResources.message(REQUIRED)));
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
        controlService.update(control, design.explanation.getValue());
      }
      final MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message(SAVED, control.getName()));
      view.fireSaveEvent(control);
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

  boolean isReadOnly() {
    return readOnly;
  }

  void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    updateReadOnly();
  }

  Control getValue() {
    return sampleBinder.getBean();
  }

  void setValue(Control control) {
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
    updateReadOnly();
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
