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
import static ca.qc.ircm.proview.sample.SampleType.DRY;
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
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#SMALL_MOLECULE}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String ID = "small-molecule-submission-form";
  public static final String SAMPLE = "sample";
  public static final String SAMPLE_TYPE = SAMPLE + "Type";
  public static final String SAMPLE_NAME = SAMPLE + "Name";
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SmallMoleculeSubmissionForm.class);
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField sampleName = new TextField();
  protected TextField solvent = new TextField();
  protected TextField formula = new TextField();
  protected TextField monoisotopicMass = new TextField();
  protected TextField averageMass = new TextField();
  protected TextField toxicity = new TextField();
  protected Checkbox lightSensitive = new Checkbox();
  protected RadioButtonGroup<StorageTemperature> storageTemperature = new RadioButtonGroup<>();
  protected RadioButtonGroup<Boolean> highResolution = new RadioButtonGroup<>();
  protected CheckboxGroup<Solvent> solvents = new CheckboxGroup<>();
  protected TextField otherSolvent = new TextField();
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private SubmissionSampleService sampleService;
  private AuthenticatedUser authenticatedUser;

  @Autowired
  protected SmallMoleculeSubmissionForm(SubmissionSampleService sampleService,
      AuthenticatedUser authenticatedUser) {
    this.sampleService = sampleService;
    this.authenticatedUser = authenticatedUser;
  }

  public static String id(String baseId) {
    return styleName(ID, baseId);
  }

  @PostConstruct
  void init() {
    setId(ID);
    setMaxWidth("80em");
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(sampleType, sampleName, solvent, formula),
        new FormLayout(monoisotopicMass, averageMass, toxicity, lightSensitive, storageTemperature),
        new FormLayout(highResolution, solvents, otherSolvent));
    sampleType.setId(id(SAMPLE_TYPE));
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleType.addValueChangeListener(e -> sampleTypeChanged());
    sampleName.setId(id(SAMPLE_NAME));
    solvent.setId(id(SOLUTION_SOLVENT));
    formula.setId(id(FORMULA));
    monoisotopicMass.setId(id(MONOISOTOPIC_MASS));
    averageMass.setId(id(AVERAGE_MASS));
    toxicity.setId(id(TOXICITY));
    lightSensitive.setId(id(LIGHT_SENSITIVE));
    storageTemperature.setId(id(STORAGE_TEMPERATURE));
    storageTemperature.setItems(StorageTemperature.values());
    storageTemperature.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    storageTemperature.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    highResolution.setId(id(HIGH_RESOLUTION));
    highResolution.setItems(false, true);
    highResolution
        .setRenderer(new TextRenderer<>(value -> new AppResources(Submission.class, getLocale())
            .message(property(HIGH_RESOLUTION, value))));
    highResolution.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    solvents.setId(id(SOLVENTS));
    solvents.setItems(Solvent.values());
    solvents.setItemLabelGenerator(solvent -> solvent.getLabel(getLocale()));
    solvents.addValueChangeListener(e -> solventsChanged());
    otherSolvent.setId(id(OTHER_SOLVENT));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    Locale locale = event.getLocale();
    final AppResources resources = new AppResources(SmallMoleculeSubmissionForm.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    sampleType.setLabel(resources.message(SAMPLE_TYPE));
    sampleName.setLabel(resources.message(SAMPLE_NAME));
    solvent.setLabel(submissionResources.message(SOLUTION_SOLVENT));
    formula.setLabel(submissionResources.message(FORMULA));
    monoisotopicMass.setLabel(submissionResources.message(MONOISOTOPIC_MASS));
    averageMass.setLabel(submissionResources.message(AVERAGE_MASS));
    toxicity.setLabel(submissionResources.message(TOXICITY));
    lightSensitive.setLabel(submissionResources.message(LIGHT_SENSITIVE));
    storageTemperature.setLabel(submissionResources.message(STORAGE_TEMPERATURE));
    highResolution.setLabel(submissionResources.message(HIGH_RESOLUTION));
    solvents.setLabel(submissionResources.message(SOLVENTS));
    otherSolvent.setLabel(submissionResources.message(OTHER_SOLVENT));
    firstSampleBinder.forField(sampleType).asRequired(webResources.message(REQUIRED)).bind(TYPE);
    firstSampleBinder.forField(sampleName).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").withValidator(sampleNameExists(locale)).bind(NAME);
    solvent.setRequiredIndicatorVisible(true);
    binder.forField(solvent)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(SOLUTION_SOLVENT);
    binder.forField(formula).asRequired(webResources.message(REQUIRED)).withNullRepresentation("")
        .bind(FORMULA);
    binder.forField(monoisotopicMass).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS);
    binder.forField(averageMass).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(AVERAGE_MASS);
    binder.forField(toxicity).withNullRepresentation("").bind(TOXICITY);
    binder.forField(lightSensitive).bind(LIGHT_SENSITIVE);
    binder.forField(storageTemperature).asRequired(webResources.message(REQUIRED))
        .bind(STORAGE_TEMPERATURE);
    binder.forField(highResolution).asRequired(webResources.message(REQUIRED))
        .bind(HIGH_RESOLUTION);
    binder.forField(solvents).asRequired(webResources.message(REQUIRED))
        .withConverter(value -> value != null ? (List<Solvent>) new ArrayList<>(value) : null,
            value -> value != null ? new HashSet<>(value) : new HashSet<>())
        .withValidator(solventsNotEmpty(locale)).bind(SOLVENTS);
    otherSolvent.setRequiredIndicatorVisible(true);
    binder.forField(otherSolvent)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_SOLVENT);
    sampleTypeChanged();
    solventsChanged();
    setReadOnly();
  }

  private void sampleTypeChanged() {
    SampleType type = sampleType.getValue();
    solvent.setEnabled(type == SOLUTION);
  }

  private void solventsChanged() {
    Set<Solvent> solvents = this.solvents.getValue();
    otherSolvent.setEnabled(solvents != null && solvents.contains(Solvent.OTHER));
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
