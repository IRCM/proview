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
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.sample.Control;
import ca.qc.ircm.proview.sample.ControlService;
import ca.qc.ircm.proview.sample.ControlType;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.server.UserError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Locale;
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
  public static final String TYPE = control.type.getMetadata().getName();
  public static final String QUANTITY = control.quantity.getMetadata().getName();
  public static final String VOLUME = control.volume.getMetadata().getName();
  public static final String CONTROL_TYPE = control.controlType.getMetadata().getName();
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String EXAMPLE = "example";
  public static final String EXPLANATION_PANEL = "explanationPanel";
  public static final String EXPLANATION = "explanation";
  public static final String SAVE = "save";
  public static final String SAVED = "saved";
  private static final Logger logger = LoggerFactory.getLogger(ControlFormPresenter.class);
  private ControlForm view;
  private ControlFormDesign design;
  private boolean readOnly = false;
  private Binder<Control> sampleBinder = new BeanValidationBinder<>(Control.class);
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
    design.standardsPanel.addStyleName(STANDARDS_PANEL);
    design.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
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
    design.type.addStyleName(TYPE);
    design.type.setCaption(resources.message(TYPE));
    design.type.setItems(SampleType.values());
    design.type.setItemCaptionGenerator(support -> support.getLabel(locale));
    sampleBinder.forField(design.type).asRequired(generalResources.message(REQUIRED)).bind(TYPE);
    design.quantityField.addStyleName(QUANTITY);
    design.quantityField.setCaption(resources.message(QUANTITY));
    design.quantityField.setPlaceholder(resources.message(property(QUANTITY, EXAMPLE)));
    sampleBinder.forField(design.quantityField).withNullRepresentation("").bind(QUANTITY);
    design.volumeField.addStyleName(VOLUME);
    design.volumeField.setCaption(resources.message(VOLUME));
    sampleBinder.forField(design.volumeField).withNullRepresentation("").bind(VOLUME);
    design.controlTypeField.addStyleName(CONTROL_TYPE);
    design.controlTypeField.setCaption(resources.message(CONTROL_TYPE));
    design.controlTypeField.setItems(ControlType.values());
    design.controlTypeField.setItemCaptionGenerator(type -> type.getLabel(locale));
    sampleBinder.forField(design.controlTypeField).asRequired(generalResources.message(REQUIRED))
        .bind(CONTROL_TYPE);
  }

  private void updateReadOnly() {
    design.nameField.setReadOnly(readOnly);
    design.quantityField.setReadOnly(readOnly);
    design.volumeField.setReadOnly(readOnly);
    design.type.setReadOnly(readOnly);
    design.controlTypeField.setReadOnly(readOnly);
    view.standardsForm.setReadOnly(readOnly);
    design.saveButton.setVisible(!readOnly);
    if (!newControl()) {
      design.explanationPanel.setVisible(!readOnly);
    }
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
      if (controlService.exists(value)) {
        if (sampleBinder.getBean().getId() == null || !controlService
            .get(sampleBinder.getBean().getId()).getName().equalsIgnoreCase(value)) {
          return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
        }
      }
      return ValidationResult.ok();
    };
  }

  private boolean validate() {
    boolean valid = true;
    valid &= validate(sampleBinder);
    valid &= view.standardsForm.validate();
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
    for (Standard standard : view.standardsForm.getValue()) {
      Standard copy = new Standard();
      copy.setName(standard.getName());
      copy.setQuantity(standard.getQuantity());
      copy.setComment(standard.getComment());
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
      control.setType(SampleType.SOLUTION);
      control.setControlType(ControlType.NEGATIVE_CONTROL);
      control.setStandards(new ArrayList<>());
    }
    view.standardsForm.setValue(control.getStandards());
    sampleBinder.setBean(control);
    updateReadOnly();
  }
}
