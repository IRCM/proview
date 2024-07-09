package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.PLACEHOLDER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.SpotbugsJustifications.INNER_CLASS_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.TYPE;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.CONTAMINANTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STANDARDS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.web.IgnoreConversionIfDisabledConverter;
import ca.qc.ircm.proview.web.RequiredIfEnabledValidator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#LC_MS_MS}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LcmsmsSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String ID = "lcmsms-submission-form";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String SAMPLES_NAMES_PLACEHOLDER = property(SAMPLES_NAMES, PLACEHOLDER);
  public static final String SAMPLES_NAMES_TITLE = property(SAMPLES_NAMES, TITLE);
  public static final String SAMPLES_NAMES_DUPLICATES = property(SAMPLES + "Names", "duplicate");
  public static final String SAMPLES_NAMES_EXISTS = property(SAMPLES + "Names", "exists");
  public static final String SAMPLES_NAMES_WRONG_COUNT = property(SAMPLES + "Names", "wrongCount");
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  public static final String CONTAMINANTS_PLACEHOLDER = property(CONTAMINANTS, PLACEHOLDER);
  public static final String STANDARDS_PLACEHOLDER = property(STANDARDS, PLACEHOLDER);
  public static final String DEVELOPMENT_TIME_PLACEHOLDER = property(DEVELOPMENT_TIME, PLACEHOLDER);
  public static final String WEIGHT_MARKER_QUANTITY_PLACEHOLDER =
      property(WEIGHT_MARKER_QUANTITY, PLACEHOLDER);
  public static final String PROTEIN_QUANTITY_PLACEHOLDER = property(PROTEIN_QUANTITY, PLACEHOLDER);
  public static final String QUANTIFICATION_COMMENT_PLACEHOLDER =
      property(QUANTIFICATION_COMMENT, PLACEHOLDER);
  public static final String QUANTIFICATION_COMMENT_PLACEHOLDER_TMT =
      property(QUANTIFICATION_COMMENT, PLACEHOLDER, Quantification.TMT.name());
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String PROTEIN_IDENTIFICATION_PREFIX =
      messagePrefix(ProteinIdentification.class);
  private static final String PROTEOLYTIC_DIGESTION_PREFIX =
      messagePrefix(ProteolyticDigestion.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final long serialVersionUID = 1460183864073097086L;
  private static final Logger logger = LoggerFactory.getLogger(LcmsmsSubmissionForm.class);
  protected TextField experiment = new TextField();
  protected TextField goal = new TextField();
  protected TextField taxonomy = new TextField();
  protected TextField protein = new TextField();
  protected TextField molecularWeight = new TextField();
  protected TextField postTranslationModification = new TextField();
  protected RadioButtonGroup<SampleType> sampleType = new RadioButtonGroup<>();
  protected TextField samplesCount = new TextField();
  protected TextArea samplesNames = new TextArea();
  protected TextField quantity = new TextField();
  protected TextField volume = new TextField();
  protected TextArea contaminants = new TextArea();
  protected TextArea standards = new TextArea();
  protected ComboBox<GelSeparation> separation = new ComboBox<>();
  protected ComboBox<GelThickness> thickness = new ComboBox<>();
  protected ComboBox<GelColoration> coloration = new ComboBox<>();
  protected TextField otherColoration = new TextField();
  protected TextField developmentTime = new TextField();
  protected Checkbox destained = new Checkbox();
  protected TextField weightMarkerQuantity = new TextField();
  protected TextField proteinQuantity = new TextField();
  protected ComboBox<ProteolyticDigestion> digestion = new ComboBox<>();
  protected TextField usedDigestion = new TextField();
  protected TextField otherDigestion = new TextField();
  protected RadioButtonGroup<ProteinContent> proteinContent = new RadioButtonGroup<>();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  protected RadioButtonGroup<ProteinIdentification> identification = new RadioButtonGroup<>();
  protected TextField identificationLink = new TextField();
  protected ComboBox<Quantification> quantification = new ComboBox<>();
  protected TextArea quantificationComment = new TextArea();
  private Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);
  private transient SubmissionSampleService sampleService;
  private transient AuthenticatedUser authenticatedUser;

  @Autowired
  protected LcmsmsSubmissionForm(SubmissionSampleService sampleService,
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
    add(new FormLayout(experiment, goal, taxonomy, protein, molecularWeight,
        postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume, separation,
            thickness, coloration, otherColoration, developmentTime, destained,
            weightMarkerQuantity, proteinQuantity),
        new FormLayout(digestion, usedDigestion, otherDigestion, proteinContent, instrument,
            identification, identificationLink, quantification, quantificationComment));
    experiment.setId(id(EXPERIMENT));
    goal.setId(id(GOAL));
    taxonomy.setId(id(TAXONOMY));
    protein.setId(id(PROTEIN));
    molecularWeight.setId(id(MOLECULAR_WEIGHT));
    postTranslationModification.setId(id(POST_TRANSLATION_MODIFICATION));
    quantity.setId(id(QUANTITY));
    volume.setId(id(VOLUME));
    contaminants.setId(id(CONTAMINANTS));
    standards.setId(id(STANDARDS));
    sampleType.setId(id(SAMPLES_TYPE));
    sampleType.setItems(SampleType.values());
    sampleType.setRenderer(
        new TextRenderer<>(value -> getTranslation(SAMPLE_TYPE_PREFIX + value.name())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleType.addValueChangeListener(e -> sampleTypeChanged());
    samplesCount.setId(id(SAMPLES_COUNT));
    samplesNames.setId(id(SAMPLES_NAMES));
    samplesNames.setMinHeight("10em");
    separation.setId(id(SEPARATION));
    separation.setItems(GelSeparation.values());
    separation.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    thickness.setId(id(THICKNESS));
    thickness.setItems(GelThickness.values());
    thickness.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    coloration.setId(id(COLORATION));
    coloration.setItems(GelColoration.values());
    coloration.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    coloration.addValueChangeListener(e -> colorationChanged());
    otherColoration.setId(id(OTHER_COLORATION));
    developmentTime.setId(id(DEVELOPMENT_TIME));
    destained.setId(id(DECOLORATION));
    weightMarkerQuantity.setId(id(WEIGHT_MARKER_QUANTITY));
    proteinQuantity.setId(id(PROTEIN_QUANTITY));
    digestion.setId(id(DIGESTION));
    digestion.setItems(ProteolyticDigestion.values());
    digestion.setItemLabelGenerator(
        value -> getTranslation(PROTEOLYTIC_DIGESTION_PREFIX + value.name()));
    digestion.addValueChangeListener(e -> digestionChanged());
    usedDigestion.setId(id(USED_DIGESTION));
    otherDigestion.setId(id(OTHER_DIGESTION));
    proteinContent.setId(id(PROTEIN_CONTENT));
    proteinContent.setItems(ProteinContent.values());
    proteinContent.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    proteinContent.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.setId(id(INSTRUMENT));
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(
        value -> getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + value.name()));
    identification.setId(id(IDENTIFICATION));
    identification.setItems(ProteinIdentification.availables());
    identification.setRenderer(
        new TextRenderer<>(value -> getTranslation(PROTEIN_IDENTIFICATION_PREFIX + value.name())));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    identification.addValueChangeListener(e -> identificationChanged());
    identificationLink.setId(id(IDENTIFICATION_LINK));
    quantification.setId(id(QUANTIFICATION));
    quantification.setItems(Quantification.values());
    quantification.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    quantification.addValueChangeListener(e -> updateQuantificationComment());
    quantification.addValueChangeListener(e -> quantificationChanged());
    quantificationComment.setId(id(QUANTIFICATION_COMMENT));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    Locale locale = getLocale();
    final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    final AppResources sampleResources = new AppResources(Sample.class, locale);
    final AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    experiment.setLabel(submissionResources.message(EXPERIMENT));
    goal.setLabel(submissionResources.message(GOAL));
    taxonomy.setLabel(submissionResources.message(TAXONOMY));
    protein.setLabel(submissionResources.message(PROTEIN));
    molecularWeight.setLabel(submissionSampleResources.message(MOLECULAR_WEIGHT));
    postTranslationModification
        .setLabel(submissionResources.message(POST_TRANSLATION_MODIFICATION));
    quantity.setLabel(sampleResources.message(QUANTITY));
    quantity.setPlaceholder(resources.message(QUANTITY_PLACEHOLDER));
    volume.setLabel(sampleResources.message(VOLUME));
    volume.setPlaceholder(resources.message(VOLUME_PLACEHOLDER));
    contaminants.setLabel(submissionResources.message(CONTAMINANTS));
    contaminants.setPlaceholder(resources.message(CONTAMINANTS_PLACEHOLDER));
    standards.setLabel(submissionResources.message(STANDARDS));
    standards.setPlaceholder(resources.message(STANDARDS_PLACEHOLDER));
    sampleType.setLabel(resources.message(SAMPLES_TYPE));
    samplesCount.setLabel(resources.message(SAMPLES_COUNT));
    samplesNames.setLabel(resources.message(SAMPLES_NAMES));
    samplesNames.setPlaceholder(resources.message(SAMPLES_NAMES_PLACEHOLDER));
    samplesNames.getElement().setAttribute(TITLE, resources.message(SAMPLES_NAMES_TITLE));
    separation.setLabel(submissionResources.message(SEPARATION));
    thickness.setLabel(submissionResources.message(THICKNESS));
    coloration.setLabel(submissionResources.message(COLORATION));
    otherColoration.setLabel(submissionResources.message(OTHER_COLORATION));
    developmentTime.setLabel(submissionResources.message(DEVELOPMENT_TIME));
    developmentTime.setPlaceholder(resources.message(DEVELOPMENT_TIME_PLACEHOLDER));
    destained.setLabel(submissionResources.message(DECOLORATION));
    weightMarkerQuantity.setLabel(submissionResources.message(WEIGHT_MARKER_QUANTITY));
    weightMarkerQuantity.setPlaceholder(resources.message(WEIGHT_MARKER_QUANTITY_PLACEHOLDER));
    proteinQuantity.setLabel(submissionResources.message(PROTEIN_QUANTITY));
    proteinQuantity.setPlaceholder(resources.message(PROTEIN_QUANTITY_PLACEHOLDER));
    digestion.setLabel(submissionResources.message(DIGESTION));
    usedDigestion.setLabel(submissionResources.message(USED_DIGESTION));
    otherDigestion.setLabel(submissionResources.message(OTHER_DIGESTION));
    proteinContent.setLabel(submissionResources.message(PROTEIN_CONTENT));
    instrument.setLabel(submissionResources.message(INSTRUMENT));
    identification.setLabel(submissionResources.message(IDENTIFICATION));
    identificationLink.setLabel(submissionResources.message(IDENTIFICATION_LINK));
    quantification.setLabel(submissionResources.message(QUANTIFICATION));
    quantificationComment.setLabel(submissionResources.message(QUANTIFICATION_COMMENT));
    quantificationComment.setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER));
    binder.forField(experiment).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(goal).withNullRepresentation("").bind(GOAL);
    binder.forField(taxonomy).asRequired(webResources.message(REQUIRED)).withNullRepresentation("")
        .bind(TAXONOMY);
    binder.forField(protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(molecularWeight).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(webResources.message(INVALID_NUMBER)))
        .bind(MOLECULAR_WEIGHT);
    binder.forField(postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(sampleType).asRequired(webResources.message(REQUIRED)).bind(TYPE);
    samplesBinder.forField(samplesCount).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("")
        .withConverter(new StringToIntegerConverter(webResources.message(INVALID_INTEGER)))
        .bind(SAMPLES_COUNT);
    samplesBinder.forField(samplesNames).asRequired(webResources.message(REQUIRED))
        .withNullRepresentation("").withConverter(new SamplesNamesConverter())
        .withValidator(samplesNamesDuplicates(locale)).withValidator(samplesNamesExists(locale))
        .withValidator(samplesNamesCount(locale)).bind(SAMPLES_NAMES);
    quantity.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(quantity)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(QUANTITY);
    volume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(volume)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .withNullRepresentation("").bind(VOLUME);
    separation.setRequiredIndicatorVisible(true);
    binder.forField(separation)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(SEPARATION);
    thickness.setRequiredIndicatorVisible(true);
    binder.forField(thickness)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(THICKNESS);
    coloration.setRequiredIndicatorVisible(true);
    binder.forField(coloration)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(COLORATION);
    otherColoration.setRequiredIndicatorVisible(true);
    binder.forField(otherColoration)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_COLORATION);
    binder.forField(developmentTime).bind(DEVELOPMENT_TIME);
    binder.forField(destained).bind(DECOLORATION);
    binder.forField(weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new IgnoreConversionIfDisabledConverter<>(
            new StringToDoubleConverter(webResources.message(INVALID_NUMBER))))
        .bind(WEIGHT_MARKER_QUANTITY);
    binder.forField(proteinQuantity).bind(PROTEIN_QUANTITY);
    binder.forField(digestion).asRequired(webResources.message(REQUIRED)).bind(DIGESTION);
    usedDigestion.setRequiredIndicatorVisible(true);
    binder.forField(usedDigestion)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(USED_DIGESTION);
    otherDigestion.setRequiredIndicatorVisible(true);
    binder.forField(otherDigestion)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(OTHER_DIGESTION);
    binder.forField(proteinContent).asRequired(webResources.message(REQUIRED))
        .bind(PROTEIN_CONTENT);
    binder.forField(instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    binder.forField(identification).asRequired(webResources.message(REQUIRED)).bind(IDENTIFICATION);
    identificationLink.setRequiredIndicatorVisible(true);
    binder.forField(identificationLink)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(IDENTIFICATION_LINK);
    binder.forField(quantification).withNullRepresentation(Quantification.NULL)
        .bind(QUANTIFICATION);
    quantificationComment.setRequiredIndicatorVisible(true);
    binder.forField(quantificationComment)
        .withValidator(new RequiredIfEnabledValidator<>(webResources.message(REQUIRED)))
        .bind(QUANTIFICATION_COMMENT);
    sampleTypeChanged();
    digestionChanged();
    identificationChanged();
    quantificationChanged();
    setReadOnly();
  }

  private void updateQuantificationComment() {
    final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, getLocale());
    if (quantification.getValue() == Quantification.TMT) {
      quantificationComment
          .setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER_TMT));
    } else {
      quantificationComment.setPlaceholder(resources.message(QUANTIFICATION_COMMENT_PLACEHOLDER));
    }
  }

  private void sampleTypeChanged() {
    SampleType type = sampleType.getValue();
    quantity.setEnabled(type != GEL);
    volume.setEnabled(type != GEL && type != DRY);
    separation.setEnabled(type == GEL);
    thickness.setEnabled(type == GEL);
    coloration.setEnabled(type == GEL);
    developmentTime.setEnabled(type == GEL);
    destained.setEnabled(type == GEL);
    weightMarkerQuantity.setEnabled(type == GEL);
    proteinQuantity.setEnabled(type == GEL);
    colorationChanged();
  }

  private void colorationChanged() {
    SampleType type = sampleType.getValue();
    GelColoration coloration = this.coloration.getValue();
    otherColoration.setEnabled(type == SampleType.GEL && coloration == GelColoration.OTHER);
  }

  private void digestionChanged() {
    ProteolyticDigestion digestion = this.digestion.getValue();
    usedDigestion.setEnabled(digestion == ProteolyticDigestion.DIGESTED);
    otherDigestion.setEnabled(digestion == ProteolyticDigestion.OTHER);
  }

  private void identificationChanged() {
    ProteinIdentification identification = this.identification.getValue();
    identificationLink.setEnabled(identification == ProteinIdentification.OTHER);
  }

  private void quantificationChanged() {
    Quantification quantification = this.quantification.getValue();
    quantificationComment
        .setEnabled(quantification == Quantification.SILAC || quantification == Quantification.TMT);
  }

  private Optional<Integer> samplesCount() {
    try {
      return Optional.of(Integer.parseInt(samplesCount.getValue()));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private Validator<List<String>> samplesNamesDuplicates(Locale locale) {
    return (values, context) -> {
      Set<String> duplicates = new HashSet<>();
      Optional<String> duplicate =
          values.stream().filter(name -> !duplicates.add(name)).findFirst();
      if (duplicate.isPresent()) {
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
        return ValidationResult.error(resources.message(SAMPLES_NAMES_DUPLICATES, duplicate.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesExists(Locale locale) {
    return (values, context) -> {
      Set<String> oldNames =
          binder.getBean().getSamples().stream().map(Sample::getName).collect(Collectors.toSet());
      Optional<String> exists = values.stream()
          .filter(name -> sampleService.exists(name) && !oldNames.contains(name)).findFirst();
      if (exists.isPresent()) {
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
        return ValidationResult.error(resources.message(SAMPLES_NAMES_EXISTS, exists.get()));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<List<String>> samplesNamesCount(Locale locale) {
    return (values, context) -> {
      Optional<Integer> samplesCount = samplesCount();
      if (samplesCount.isPresent() && samplesCount.get() != values.size()) {
        final AppResources resources = new AppResources(LcmsmsSubmissionForm.class, locale);
        return ValidationResult
            .error(resources.message(SAMPLES_NAMES_WRONG_COUNT, values.size(), samplesCount.get()));
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

  BinderValidationStatus<Samples> validateSamples() {
    return samplesBinder.validate();
  }

  boolean isValid() {
    boolean valid = validateSubmission().isOk();
    valid = validateFirstSample().isOk() && valid;
    valid = validateSamples().isOk() && valid;
    if (valid) {
      try {
        updateSubmissionSamples();
      } catch (ValidationException e) {
        return false;
      }
    }
    return valid;
  }

  private void updateSubmissionSamples() throws ValidationException {
    Submission submission = binder.getBean();
    Samples samples = samplesBinder.getBean();
    submission.getSamples().get(0).setName(samples.getSamplesNames().get(0));
    while (submission.getSamples().size() < samples.samplesCount) {
      submission.getSamples().add(new SubmissionSample());
    }
    while (submission.getSamples().size() > samples.samplesCount) {
      submission.getSamples().remove(submission.getSamples().size() - 1);
    }
    for (int i = 0; i < samples.samplesCount; i++) {
      SubmissionSample sample = submission.getSamples().get(i);
      try {
        firstSampleBinder.writeBean(sample);
      } catch (ValidationException e) {
        logger.warn(
            "firstSampleBinder validation passed, but failed when writing to sample " + sample);
        throw e;
      }
      submission.getSamples().get(i).setName(samples.getSamplesNames().get(i));
    }
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
    Samples samples = new Samples();
    samples.setSamplesCount(submission.getSamples().size());
    samples.setSamplesNames(
        submission.getSamples().stream().map(Sample::getName).collect(Collectors.toList()));
    samplesBinder.setBean(samples);
    setReadOnly();
  }

  private void setReadOnly() {
    boolean readOnly = !authenticatedUser.hasPermission(binder.getBean(), WRITE);
    binder.setReadOnly(readOnly);
    firstSampleBinder.setReadOnly(readOnly);
    samplesBinder.setReadOnly(readOnly);
  }

  /**
   * Represents a list of sample names.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = INNER_CLASS_EI_EXPOSE_REP)
  protected static class Samples {
    private int samplesCount;
    private List<String> samplesNames;

    public int getSamplesCount() {
      return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
      this.samplesCount = samplesCount;
    }

    public List<String> getSamplesNames() {
      return samplesNames;
    }

    public void setSamplesNames(List<String> samplesNames) {
      this.samplesNames = samplesNames;
    }
  }

  private static class SamplesNamesConverter implements Converter<String, List<String>> {
    private static final long serialVersionUID = 8024859234735628305L;

    @Override
    public Result<List<String>> convertToModel(String value, ValueContext context) {
      return Result.ok(Arrays.asList(value.split("\\s*[,;\\t\\n]\\s*")).stream()
          .filter(val -> !val.isEmpty()).collect(Collectors.toList()));
    }

    @Override
    public String convertToPresentation(List<String> value, ValueContext context) {
      return value.stream().map(val -> Objects.toString(val, "")).collect(Collectors.joining(", "));
    }
  }
}
