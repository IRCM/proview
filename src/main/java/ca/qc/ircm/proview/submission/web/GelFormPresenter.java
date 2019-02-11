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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.ui.AbstractTextField;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Submission form presenter for gel properties.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GelFormPresenter implements BinderValidator {
  public static final String EXAMPLE = "example";
  private static final Logger logger = LoggerFactory.getLogger(GelFormPresenter.class);
  private GelForm view;
  private GelFormDesign design;
  private boolean readOnly = false;
  private Binder<Submission> submissionBinder = new BeanValidationBinder<>(Submission.class);

  protected GelFormPresenter() {
    submissionBinder.setBean(new Submission());
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(GelForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.separation.addStyleName(SEPARATION);
    design.separation.setCaption(resources.message(SEPARATION));
    design.separation.setEmptySelectionAllowed(false);
    design.separation.setItems(GelSeparation.values());
    design.separation.setItemCaptionGenerator(separation -> separation.getLabel(locale));
    design.separation.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.separation).asRequired(generalResources.message(REQUIRED))
        .bind(SEPARATION);
    design.thickness.addStyleName(THICKNESS);
    design.thickness.setCaption(resources.message(THICKNESS));
    design.thickness.setEmptySelectionAllowed(false);
    design.thickness.setItems(GelThickness.values());
    design.thickness.setItemCaptionGenerator(thickness -> thickness.getLabel(locale));
    design.thickness.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.thickness).asRequired(generalResources.message(REQUIRED))
        .bind(THICKNESS);
    design.coloration.addStyleName(COLORATION);
    design.coloration.setCaption(resources.message(COLORATION));
    design.coloration.setEmptySelectionAllowed(true);
    design.coloration.setEmptySelectionCaption(GelColoration.getNullLabel(locale));
    design.coloration.setItems(GelColoration.values());
    design.coloration.setItemCaptionGenerator(coloration -> coloration.getLabel(locale));
    design.coloration.addValueChangeListener(e -> {
      design.otherColoration.setVisible(e.getValue() == GelColoration.OTHER);
      if (e.getValue() != GelColoration.OTHER) {
        design.otherColoration.setValue("");
      }
    });
    submissionBinder.forField(design.coloration).bind(COLORATION);
    design.otherColoration.addStyleName(OTHER_COLORATION);
    design.otherColoration.setCaption(resources.message(OTHER_COLORATION));
    design.otherColoration.setRequiredIndicatorVisible(true);
    design.otherColoration.setVisible(design.coloration.getValue() == GelColoration.OTHER);
    submissionBinder.forField(design.otherColoration)
        .withValidator(requiredTextIfVisible(design.otherColoration)).withNullRepresentation("")
        .bind(OTHER_COLORATION);
    design.developmentTime.addStyleName(DEVELOPMENT_TIME);
    design.developmentTime.setCaption(resources.message(DEVELOPMENT_TIME));
    design.developmentTime.setPlaceholder(resources.message(property(DEVELOPMENT_TIME, EXAMPLE)));
    submissionBinder.forField(design.developmentTime).withNullRepresentation("")
        .bind(DEVELOPMENT_TIME);
    design.decoloration.addStyleName(DECOLORATION);
    design.decoloration.setCaption(resources.message(DECOLORATION));
    submissionBinder.forField(design.decoloration).bind(DECOLORATION);
    design.weightMarkerQuantity.addStyleName(WEIGHT_MARKER_QUANTITY);
    design.weightMarkerQuantity.setCaption(resources.message(WEIGHT_MARKER_QUANTITY));
    design.weightMarkerQuantity
        .setPlaceholder(resources.message(property(WEIGHT_MARKER_QUANTITY, EXAMPLE)));
    submissionBinder.forField(design.weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY);
    design.proteinQuantity.addStyleName(PROTEIN_QUANTITY);
    design.proteinQuantity.setCaption(resources.message(PROTEIN_QUANTITY));
    design.proteinQuantity.setPlaceholder(resources.message(property(PROTEIN_QUANTITY, EXAMPLE)));
    submissionBinder.forField(design.proteinQuantity).withNullRepresentation("")
        .bind(PROTEIN_QUANTITY);
    submissionBinder.setReadOnly(readOnly);
  }

  private Validator<String> requiredTextIfVisible(AbstractTextField field) {
    final MessageResource generalResources = view.getGeneralResources();
    return (value, context) -> {
      if (field.isVisible() && value.isEmpty()) {
        String error = generalResources.message(REQUIRED);
        logger.debug("validation error on {}: {}", field.getStyleName(), error);
        return ValidationResult.error(error);
      }
      return ValidationResult.ok();
    };
  }

  boolean validate() {
    logger.trace("Validate submission gel properties");
    return validate(submissionBinder);
  }

  Submission getValue() {
    return submissionBinder.getBean();
  }

  void setValue(Submission submission) {
    if (submission == null) {
      submission = new Submission();
    }

    submissionBinder.setBean(submission);
  }

  boolean isReadOnly() {
    return readOnly;
  }

  void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    submissionBinder.setReadOnly(readOnly);
  }
}
