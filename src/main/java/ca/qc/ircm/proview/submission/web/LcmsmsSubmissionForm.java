package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.PLACEHOLDER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.SpotbugsJustifications.INNER_CLASS_EI_EXPOSE_REP;
import static ca.qc.ircm.proview.UsedBy.VAADIN;
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

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.UsedBy;
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
import java.io.Serial;
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
  private static final String MESSAGES_PREFIX = messagePrefix(LcmsmsSubmissionForm.class);
  private static final String SAMPLE_PREFIX = messagePrefix(Sample.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String PROTEIN_IDENTIFICATION_PREFIX =
      messagePrefix(ProteinIdentification.class);
  private static final String PROTEOLYTIC_DIGESTION_PREFIX =
      messagePrefix(ProteolyticDigestion.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String GEL_COLORATION_PREFIX = messagePrefix(GelColoration.class);
  private static final String GEL_SEPARATION_PREFIX = messagePrefix(GelSeparation.class);
  private static final String GEL_THICKNESS_PREFIX = messagePrefix(GelThickness.class);
  private static final String PROTEIN_CONTENT_PREFIX = messagePrefix(ProteinContent.class);
  private static final String QUANTIFICATION_PREFIX = messagePrefix(Quantification.class);
  @Serial
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
  private final Binder<Submission> binder = new BeanValidationBinder<>(Submission.class);
  private final Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private final Binder<Samples> samplesBinder = new BeanValidationBinder<>(Samples.class);
  private final transient SubmissionSampleService sampleService;
  private final transient AuthenticatedUser authenticatedUser;

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
    separation.setItemLabelGenerator(value -> getTranslation(GEL_SEPARATION_PREFIX + value.name()));
    thickness.setId(id(THICKNESS));
    thickness.setItems(GelThickness.values());
    thickness.setItemLabelGenerator(value -> getTranslation(GEL_THICKNESS_PREFIX + value.name()));
    coloration.setId(id(COLORATION));
    coloration.setItems(GelColoration.values());
    coloration.setItemLabelGenerator(value -> getTranslation(GEL_COLORATION_PREFIX + value.name()));
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
    proteinContent.setRenderer(
        new TextRenderer<>(value -> getTranslation(PROTEIN_CONTENT_PREFIX + value.name())));
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
    quantification
        .setItemLabelGenerator(value -> getTranslation(QUANTIFICATION_PREFIX + value.name()));
    quantification.addValueChangeListener(e -> updateQuantificationComment());
    quantification.addValueChangeListener(e -> quantificationChanged());
    quantificationComment.setId(id(QUANTIFICATION_COMMENT));
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    Locale locale = getLocale();
    experiment.setLabel(getTranslation(SUBMISSION_PREFIX + EXPERIMENT));
    goal.setLabel(getTranslation(SUBMISSION_PREFIX + GOAL));
    taxonomy.setLabel(getTranslation(SUBMISSION_PREFIX + TAXONOMY));
    protein.setLabel(getTranslation(SUBMISSION_PREFIX + PROTEIN));
    molecularWeight.setLabel(getTranslation(SUBMISSION_SAMPLE_PREFIX + MOLECULAR_WEIGHT));
    postTranslationModification
        .setLabel(getTranslation(SUBMISSION_PREFIX + POST_TRANSLATION_MODIFICATION));
    quantity.setLabel(getTranslation(SAMPLE_PREFIX + QUANTITY));
    quantity.setPlaceholder(getTranslation(MESSAGES_PREFIX + QUANTITY_PLACEHOLDER));
    volume.setLabel(getTranslation(SAMPLE_PREFIX + VOLUME));
    volume.setPlaceholder(getTranslation(MESSAGES_PREFIX + VOLUME_PLACEHOLDER));
    contaminants.setLabel(getTranslation(SUBMISSION_PREFIX + CONTAMINANTS));
    contaminants.setPlaceholder(getTranslation(MESSAGES_PREFIX + CONTAMINANTS_PLACEHOLDER));
    standards.setLabel(getTranslation(SUBMISSION_PREFIX + STANDARDS));
    standards.setPlaceholder(getTranslation(MESSAGES_PREFIX + STANDARDS_PLACEHOLDER));
    sampleType.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_TYPE));
    samplesCount.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT));
    samplesNames.setLabel(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES));
    samplesNames.setPlaceholder(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_PLACEHOLDER));
    samplesNames.getElement().setAttribute(TITLE,
        getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_TITLE));
    separation.setLabel(getTranslation(SUBMISSION_PREFIX + SEPARATION));
    thickness.setLabel(getTranslation(SUBMISSION_PREFIX + THICKNESS));
    coloration.setLabel(getTranslation(SUBMISSION_PREFIX + COLORATION));
    otherColoration.setLabel(getTranslation(SUBMISSION_PREFIX + OTHER_COLORATION));
    developmentTime.setLabel(getTranslation(SUBMISSION_PREFIX + DEVELOPMENT_TIME));
    developmentTime.setPlaceholder(getTranslation(MESSAGES_PREFIX + DEVELOPMENT_TIME_PLACEHOLDER));
    destained.setLabel(getTranslation(SUBMISSION_PREFIX + DECOLORATION));
    weightMarkerQuantity.setLabel(getTranslation(SUBMISSION_PREFIX + WEIGHT_MARKER_QUANTITY));
    weightMarkerQuantity
        .setPlaceholder(getTranslation(MESSAGES_PREFIX + WEIGHT_MARKER_QUANTITY_PLACEHOLDER));
    proteinQuantity.setLabel(getTranslation(SUBMISSION_PREFIX + PROTEIN_QUANTITY));
    proteinQuantity.setPlaceholder(getTranslation(MESSAGES_PREFIX + PROTEIN_QUANTITY_PLACEHOLDER));
    digestion.setLabel(getTranslation(SUBMISSION_PREFIX + DIGESTION));
    usedDigestion.setLabel(getTranslation(SUBMISSION_PREFIX + USED_DIGESTION));
    otherDigestion.setLabel(getTranslation(SUBMISSION_PREFIX + OTHER_DIGESTION));
    proteinContent.setLabel(getTranslation(SUBMISSION_PREFIX + PROTEIN_CONTENT));
    instrument.setLabel(getTranslation(SUBMISSION_PREFIX + INSTRUMENT));
    identification.setLabel(getTranslation(SUBMISSION_PREFIX + IDENTIFICATION));
    identificationLink.setLabel(getTranslation(SUBMISSION_PREFIX + IDENTIFICATION_LINK));
    quantification.setLabel(getTranslation(SUBMISSION_PREFIX + QUANTIFICATION));
    quantificationComment.setLabel(getTranslation(SUBMISSION_PREFIX + QUANTIFICATION_COMMENT));
    quantificationComment
        .setPlaceholder(getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER));
    binder.forField(experiment).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(EXPERIMENT);
    binder.forField(goal).withNullRepresentation("").bind(GOAL);
    binder.forField(taxonomy).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").bind(TAXONOMY);
    binder.forField(protein).withNullRepresentation("").bind(PROTEIN);
    firstSampleBinder.forField(molecularWeight).withNullRepresentation("")
        .withConverter(
            new StringToDoubleConverter(getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)))
        .bind(MOLECULAR_WEIGHT);
    binder.forField(postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    firstSampleBinder.forField(sampleType).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(TYPE);
    samplesBinder.forField(samplesCount).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("")
        .withConverter(
            new StringToIntegerConverter(getTranslation(CONSTANTS_PREFIX + INVALID_INTEGER)))
        .bind(SAMPLES_COUNT);
    samplesBinder.forField(samplesNames).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .withNullRepresentation("").withConverter(new SamplesNamesConverter())
        .withValidator(samplesNamesDuplicates(locale)).withValidator(samplesNamesExists(locale))
        .withValidator(samplesNamesCount(locale)).bind(SAMPLES_NAMES);
    quantity.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(quantity)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").bind(QUANTITY);
    volume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(volume)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .withNullRepresentation("").bind(VOLUME);
    separation.setRequiredIndicatorVisible(true);
    binder.forField(separation)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(SEPARATION);
    thickness.setRequiredIndicatorVisible(true);
    binder.forField(thickness)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(THICKNESS);
    coloration.setRequiredIndicatorVisible(true);
    binder.forField(coloration)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(COLORATION);
    otherColoration.setRequiredIndicatorVisible(true);
    binder.forField(otherColoration)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(OTHER_COLORATION);
    binder.forField(developmentTime).bind(DEVELOPMENT_TIME);
    binder.forField(destained).bind(DECOLORATION);
    binder.forField(weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new IgnoreConversionIfDisabledConverter<>(
            new StringToDoubleConverter(getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER))))
        .bind(WEIGHT_MARKER_QUANTITY);
    binder.forField(proteinQuantity).bind(PROTEIN_QUANTITY);
    binder.forField(digestion).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(DIGESTION);
    usedDigestion.setRequiredIndicatorVisible(true);
    binder.forField(usedDigestion)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(USED_DIGESTION);
    otherDigestion.setRequiredIndicatorVisible(true);
    binder.forField(otherDigestion)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(OTHER_DIGESTION);
    binder.forField(proteinContent).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(PROTEIN_CONTENT);
    binder.forField(instrument).withNullRepresentation(MassDetectionInstrument.NULL)
        .bind(INSTRUMENT);
    binder.forField(identification).asRequired(getTranslation(CONSTANTS_PREFIX + REQUIRED))
        .bind(IDENTIFICATION);
    identificationLink.setRequiredIndicatorVisible(true);
    binder.forField(identificationLink)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(IDENTIFICATION_LINK);
    binder.forField(quantification).withNullRepresentation(Quantification.NULL)
        .bind(QUANTIFICATION);
    quantificationComment.setRequiredIndicatorVisible(true);
    binder.forField(quantificationComment)
        .withValidator(
            new RequiredIfEnabledValidator<>(getTranslation(CONSTANTS_PREFIX + REQUIRED)))
        .bind(QUANTIFICATION_COMMENT);
    sampleTypeChanged();
    digestionChanged();
    identificationChanged();
    quantificationChanged();
    setReadOnly();
  }

  private void updateQuantificationComment() {
    if (quantification.getValue() == Quantification.TMT) {
      quantificationComment
          .setPlaceholder(getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER_TMT));
    } else {
      quantificationComment
          .setPlaceholder(getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER));
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
      return duplicate
          .map(du -> ValidationResult
              .error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_DUPLICATES, du)))
          .orElse(ValidationResult.ok());
    };
  }

  private Validator<List<String>> samplesNamesExists(Locale locale) {
    return (values, context) -> {
      Set<String> oldNames =
          binder.getBean().getSamples().stream().map(Sample::getName).collect(Collectors.toSet());
      Optional<String> exists = values.stream()
          .filter(name -> sampleService.exists(name) && !oldNames.contains(name)).findFirst();
      return exists.map(
              ex -> ValidationResult.error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_EXISTS, ex)))
          .orElse(ValidationResult.ok());
    };
  }

  private Validator<List<String>> samplesNamesCount(Locale locale) {
    return (values, context) -> {
      Optional<Integer> samplesCount = samplesCount();
      if (samplesCount.isPresent() && samplesCount.get() != values.size()) {
        return ValidationResult.error(getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT,
            values.size(), samplesCount.get()));
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
        logger.warn("firstSampleBinder validation passed, but failed when writing to sample {}",
            sample);
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
    if (submission.getSamples().isEmpty()) {
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

    @UsedBy(VAADIN)
    public int getSamplesCount() {
      return samplesCount;
    }

    @UsedBy(VAADIN)
    public void setSamplesCount(int samplesCount) {
      this.samplesCount = samplesCount;
    }

    @UsedBy(VAADIN)
    public List<String> getSamplesNames() {
      return samplesNames;
    }

    @UsedBy(VAADIN)
    public void setSamplesNames(List<String> samplesNames) {
      this.samplesNames = samplesNames;
    }
  }

  private static class SamplesNamesConverter implements Converter<String, List<String>> {

    @Serial
    private static final long serialVersionUID = 8024859234735628305L;

    @Override
    public Result<List<String>> convertToModel(String value, ValueContext context) {
      return Result.ok(Arrays.stream(value.split("\\s*[,;\\t\\n]\\s*"))
          .filter(val -> !val.isEmpty()).collect(Collectors.toList()));
    }

    @Override
    public String convertToPresentation(List<String> value, ValueContext context) {
      return value.stream().map(val -> Objects.toString(val, "")).collect(Collectors.joining(", "));
    }
  }
}
