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

import static ca.qc.ircm.proview.Constants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLVENTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form presenter for {@link Service#LC_MS_MS}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionFormPresenter {
  @SuppressWarnings("unused")
  private static final Logger logger =
      LoggerFactory.getLogger(SmallMoleculeSubmissionFormPresenter.class);
  private SmallMoleculeSubmissionForm form;
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private SubmissionSampleService sampleService;
  private AuthenticatedUser authenticatedUser;

  @Autowired
  protected SmallMoleculeSubmissionFormPresenter(SubmissionSampleService sampleService,
      AuthenticatedUser authenticatedUser) {
    this.sampleService = sampleService;
    this.authenticatedUser = authenticatedUser;
  }

  /**
   * Initializes presenter.
   *
   * @param form
   *          form
   */
  void init(SmallMoleculeSubmissionForm form) {
    this.form = form;
    form.sampleType.addValueChangeListener(e -> sampleTypeChanged());
    form.solvents.addValueChangeListener(e -> solventsChanged());
  }

  void localeChange(Locale locale) {
    final AppResources webResources = new AppResources(Constants.class, locale);
    firstSampleBinder.forField(form.sampleType).asRequired(webResources.message(REQUIRED))
        .bind(TYPE);
    firstSampleBinder.forField(form.sampleName).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").withValidator(sampleNameExists(locale)).bind(NAME);
    form.solvent.setRequiredIndicatorVisible(true);
    binder.forField(form.solvent)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(SOLUTION_SOLVENT);
    binder.forField(form.formula).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(FORMULA);
    binder.forField(form.monoisotopicMass).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS);
    binder.forField(form.averageMass).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(AVERAGE_MASS);
    binder.forField(form.toxicity).withNullRepresentation("").bind(TOXICITY);
    binder.forField(form.lightSensitive).bind(LIGHT_SENSITIVE);
    binder.forField(form.storageTemperature).asRequired(webResources.message(REQUIRED))
        .bind(STORAGE_TEMPERATURE);
    binder.forField(form.highResolution).asRequired(webResources.message(REQUIRED))
        .bind(HIGH_RESOLUTION);
    binder.forField(form.solvents).asRequired(webResources.message(REQUIRED))
        .withValidator(solventsNotEmpty(locale)).bind(SOLVENTS);
    form.otherSolvent.setRequiredIndicatorVisible(true);
    binder.forField(form.otherSolvent)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_SOLVENT);
    sampleTypeChanged();
    solventsChanged();
    setReadOnly();
  }

  private void sampleTypeChanged() {
    SampleType type = form.sampleType.getValue();
    form.solvent.setEnabled(type == SOLUTION);
  }

  private void solventsChanged() {
    List<Solvent> solvents = form.solvents.getValue();
    form.otherSolvent.setEnabled(solvents != null && solvents.contains(Solvent.OTHER));
  }

  private Validator<String> sampleNameExists(Locale locale) {
    return (value, context) -> {
      if (sampleService.exists(value)) {
        final AppResources resources = new AppResources(Constants.class, locale);
        return ValidationResult.error(resources.message(ALREADY_EXISTS, value));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<Solvent>> solventsNotEmpty(Locale locale) {
    return (value, context) -> {
      if (value.isEmpty()) {
        final AppResources resources = new AppResources(Constants.class, locale);
        return ValidationResult.error(resources.message(REQUIRED, value));
      }
      return ValidationResult.ok();
    };
  }

  BinderValidationStatus<Submission> validateSubmission() {
    return binder.validate();
  }

  BinderValidationStatus<SubmissionSample> validateFirstSample() {
    return firstSampleBinder.validate();
  }

  boolean isValid() {
    boolean valid = true;
    logger.debug("solvents: {}", binder.getBean().getSolvents());
    valid = validateSubmission().isOk() && valid;
    valid = validateFirstSample().isOk() && valid;
    return valid;
  }

  Submission getSubmission() {
    return binder.getBean();
  }

  void setSubmission(Submission submission) {
    Objects.requireNonNull(submission);
    if (submission.getSamples() == null || submission.getSamples().isEmpty()) {
      throw new IllegalArgumentException("submission must contain at least one sample");
    }
    binder.setBean(submission);
    firstSampleBinder.setBean(submission.getSamples().get(0));
    setReadOnly();
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    firstSampleBinder.setReadOnly(readOnly);
  }
}
