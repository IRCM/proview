package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.ID;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.SAMPLES_TYPE;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.VOLUME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.IntactProteinSubmissionForm.id;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link IntactProteinSubmissionForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class IntactProteinSubmissionFormTest extends SpringUIUnitTest {
  private static final String INJECTION_TYPE_PREFIX = messagePrefix(InjectionType.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private IntactProteinSubmissionForm form;
  @MockBean
  private SubmissionSampleService sampleService;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private SubmissionRepository repository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
  private AppResources submissionResources = new AppResources(Submission.class, locale);
  private AppResources sampleResources = new AppResources(Sample.class, locale);
  private AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private String experiment = "my experiment";
  private String goal = "my goal";
  private String taxonomy = "my taxon";
  private String protein = "my protein";
  private Double molecularWeight = 12.3;
  private String postTranslationModification = "glyco";
  private SampleType sampleType = SampleType.SOLUTION;
  private int samplesCount = 2;
  private String sampleName1 = "my sample 1";
  private String sampleName2 = "my sample 2";
  private String samplesNames = sampleName1 + ", " + sampleName2;
  private String quantity = "13g";
  private String volume = "9 ml";
  private InjectionType injection = InjectionType.LC_MS;
  private MassDetectionInstrumentSource source = MassDetectionInstrumentSource.LDTD;
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    SubmissionView view = navigate(SubmissionView.class);
    test(test(view).find(Tabs.class).id(SERVICE)).select(INTACT_PROTEIN.getLabel(locale));
    form = test(view).find(IntactProteinSubmissionForm.class).id(ID);
  }

  private Submission submission() {
    Submission submission = new Submission();
    submission.setExperiment(experiment);
    submission.setGoal(goal);
    submission.setTaxonomy(taxonomy);
    submission.setProtein(protein);
    submission.setSamples(new ArrayList<>());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().get(0).setMolecularWeight(molecularWeight);
    submission.setPostTranslationModification(postTranslationModification);
    submission.getSamples().get(0).setType(sampleType);
    submission.getSamples().get(0).setName(sampleName1);
    submission.getSamples().get(1).setName(sampleName2);
    submission.getSamples().get(0).setQuantity(quantity);
    submission.getSamples().get(0).setVolume(volume);
    submission.setInjectionType(injection);
    submission.setSource(source);
    submission.setInstrument(instrument);
    return submission;
  }

  private void setFields() {
    form.experiment.setValue(experiment);
    form.goal.setValue(goal);
    form.taxonomy.setValue(taxonomy);
    form.protein.setValue(protein);
    form.molecularWeight.setValue(String.valueOf(molecularWeight));
    form.postTranslationModification.setValue(postTranslationModification);
    form.sampleType.setValue(sampleType);
    form.samplesCount.setValue(String.valueOf(samplesCount));
    form.samplesNames.setValue(samplesNames);
    form.quantity.setValue(quantity);
    form.volume.setValue(volume);
    form.injection.setValue(injection);
    form.source.setValue(source);
    form.instrument.setValue(instrument);
  }

  @Test
  public void styles() {
    assertEquals(ID, form.getId().orElse(""));
    assertEquals(id(GOAL), form.goal.getId().orElse(""));
    assertEquals(id(TAXONOMY), form.taxonomy.getId().orElse(""));
    assertEquals(id(PROTEIN), form.protein.getId().orElse(""));
    assertEquals(id(MOLECULAR_WEIGHT), form.molecularWeight.getId().orElse(""));
    assertEquals(id(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getId().orElse(""));
    assertEquals(id(SAMPLES_TYPE), form.sampleType.getId().orElse(""));
    assertEquals(id(SAMPLES_COUNT), form.samplesCount.getId().orElse(""));
    assertEquals(id(SAMPLES_NAMES), form.samplesNames.getId().orElse(""));
    assertEquals(id(QUANTITY), form.quantity.getId().orElse(""));
    assertEquals(id(VOLUME), form.volume.getId().orElse(""));
    assertEquals(id(INJECTION_TYPE), form.injection.getId().orElse(""));
    assertEquals(id(SOURCE), form.source.getId().orElse(""));
    assertEquals(id(INSTRUMENT), form.instrument.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(submissionResources.message(GOAL), form.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), form.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), form.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), form.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), form.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), form.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), form.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), form.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), form.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), form.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), form.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(IntactProteinSubmissionForm.class, locale);
    final AppResources submissionResources = new AppResources(Submission.class, locale);
    final AppResources sampleResources = new AppResources(Sample.class, locale);
    final AppResources submissionSampleResources = new AppResources(SubmissionSample.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(submissionResources.message(GOAL), form.goal.getLabel());
    assertEquals(submissionResources.message(TAXONOMY), form.taxonomy.getLabel());
    assertEquals(submissionResources.message(PROTEIN), form.protein.getLabel());
    assertEquals(submissionSampleResources.message(MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(submissionResources.message(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(resources.message(SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(resources.message(SAMPLES_COUNT), form.samplesCount.getLabel());
    assertEquals(resources.message(SAMPLES_NAMES), form.samplesNames.getLabel());
    assertEquals(sampleResources.message(QUANTITY), form.quantity.getLabel());
    assertEquals(resources.message(QUANTITY_PLACEHOLDER), form.quantity.getPlaceholder());
    assertEquals(sampleResources.message(VOLUME), form.volume.getLabel());
    assertEquals(resources.message(VOLUME_PLACEHOLDER), form.volume.getPlaceholder());
    assertEquals(submissionResources.message(INJECTION_TYPE), form.injection.getLabel());
    assertEquals(submissionResources.message(SOURCE), form.source.getLabel());
    assertEquals(submissionResources.message(INSTRUMENT), form.instrument.getLabel());
  }

  @Test
  public void required() {
    assertTrue(form.experiment.isRequiredIndicatorVisible());
    assertFalse(form.goal.isRequiredIndicatorVisible());
    assertTrue(form.taxonomy.isRequiredIndicatorVisible());
    assertFalse(form.protein.isRequiredIndicatorVisible());
    assertFalse(form.molecularWeight.isRequiredIndicatorVisible());
    assertFalse(form.postTranslationModification.isRequiredIndicatorVisible());
    assertTrue(form.sampleType.isRequiredIndicatorVisible());
    assertTrue(form.samplesCount.isRequiredIndicatorVisible());
    assertTrue(form.samplesNames.isRequiredIndicatorVisible());
    assertTrue(form.quantity.isRequiredIndicatorVisible());
    assertTrue(form.volume.isRequiredIndicatorVisible());
    assertTrue(form.injection.isRequiredIndicatorVisible());
    assertTrue(form.source.isRequiredIndicatorVisible());
    assertFalse(form.instrument.isRequiredIndicatorVisible());
  }

  @Test
  public void enabled() {
    assertTrue(form.experiment.isEnabled());
    assertTrue(form.goal.isEnabled());
    assertTrue(form.taxonomy.isEnabled());
    assertTrue(form.protein.isEnabled());
    assertTrue(form.molecularWeight.isEnabled());
    assertTrue(form.postTranslationModification.isEnabled());
    assertTrue(form.sampleType.isEnabled());
    assertTrue(form.samplesCount.isEnabled());
    assertTrue(form.samplesNames.isEnabled());
    assertTrue(form.quantity.isEnabled());
    assertTrue(form.source.isEnabled());
    assertTrue(form.injection.isEnabled());
    assertTrue(form.instrument.isEnabled());
  }

  @Test
  public void enabled_Solution() {
    form.sampleType.setValue(SampleType.SOLUTION);
    assertTrue(form.volume.isEnabled());
  }

  @Test
  public void enabled_Dry() {
    form.sampleType.setValue(SampleType.DRY);
    assertFalse(form.volume.isEnabled());
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(form.sampleType);
    assertEquals(2, items.size());
    for (SampleType value : new SampleType[] { DRY, SOLUTION }) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void injectionTypes() {
    List<InjectionType> items = items(form.injection);
    assertEquals(InjectionType.values().length, items.size());
    for (InjectionType value : InjectionType.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(INJECTION_TYPE_PREFIX + value.name()),
          form.injection.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void sources() {
    List<MassDetectionInstrumentSource> items = items(form.source);
    assertEquals(MassDetectionInstrumentSource.availables().size(), items.size());
    for (MassDetectionInstrumentSource value : MassDetectionInstrumentSource.availables()) {
      assertTrue(items.contains(value));
      assertEquals(value.getLabel(locale),
          form.source.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void instruments() {
    List<MassDetectionInstrument> items = items(form.instrument);
    assertEquals(MassDetectionInstrument.userChoices().size(), items.size());
    for (MassDetectionInstrument value : MassDetectionInstrument.userChoices()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + value.name()),
          form.instrument.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void isValid_EmptyExperiment() {
    setFields();
    form.experiment.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.experiment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyTaxonomy() {
    setFields();
    form.taxonomy.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.taxonomy);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyMolecularWeight() {
    setFields();
    form.molecularWeight.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_InvalidMolecularWeight() {
    setFields();
    form.molecularWeight.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.molecularWeight);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_NUMBER)), error.getMessage());
  }

  @Test
  public void isValid_EmptySampleType() {
    setFields();
    form.sampleType.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleType);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySamplesCount() {
    setFields();
    form.samplesCount.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_InvalidSamplesCount() {
    setFields();
    form.samplesCount.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(INVALID_INTEGER)), error.getMessage());
  }

  @Test
  public void isValid_SamplesNamesCommaSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesCommaSpaceSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + ", " + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + ";" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSpaceSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "; " + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesTabSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "\t" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesNewlineSeparator() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "\n" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_EmptySamplesNames() {
    setFields();
    form.samplesNames.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyFirstSampleName() {
    setFields();
    form.samplesNames.setValue(", abc");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_EmptySecondSampleName() {
    setFields();
    form.samplesNames.setValue("abc, ");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_LastEmptySamplesNames() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",");

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_DuplicatedSamplesNames() {
    setFields();
    form.samplesCount.setValue("3");
    form.samplesNames.setValue(
        Stream.of(sampleName1, sampleName2, sampleName2).collect(Collectors.joining(", ")));

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_DUPLICATES, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_WrongNumberOfSamplesNames_Below() {
    setFields();
    form.samplesNames.setValue(sampleName1);

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_WrongNumberOfSamplesNames_Above() {
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",other_sample_name");

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_WRONG_COUNT, 3, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNames() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    setFields();

    assertFalse(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(resources.message(SAMPLES_NAMES_EXISTS, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNamesInSubmission() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    SubmissionSample sample = new SubmissionSample();
    sample.setName(sampleName2);
    form.getSubmission().getSamples().add(sample);
    form.setSubmission(form.getSubmission());
    setFields();

    assertTrue(form.isValid());
    BinderValidationStatus<IntactProteinSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyQuantity() {
    setFields();
    form.quantity.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantity);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyVolume_Solution() {
    setFields();
    form.volume.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.volume);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyVolume_Dry() {
    setFields();
    form.sampleType.setValue(SampleType.DRY);
    form.volume.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_Injection() {
    setFields();
    form.injection.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.injection);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_Source() {
    setFields();
    form.source.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.source);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid() {
    setFields();

    assertTrue(form.isValid());
    assertTrue(form.validateSubmission().isOk());
    assertTrue(form.validateFirstSample().isOk());
    assertTrue(form.validateSamples().isOk());
  }

  @Test
  public void getSubmission_NoChanges() {
    Submission database = repository.findById(34L).orElse(null);
    database.setInjectionType(InjectionType.DIRECT_INFUSION);
    database.setSource(MassDetectionInstrumentSource.ESI);
    form.setSubmission(database);

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
    assertEquals(database.getId(), submission.getId());
    assertEquals(database.getExperiment(), submission.getExperiment());
    assertEquals(database.getGoal(), submission.getGoal());
    assertEquals(database.getTaxonomy(), submission.getTaxonomy());
    assertEquals(database.getProtein(), submission.getProtein());
    assertEquals(database.getSamples().get(0).getMolecularWeight(),
        submission.getSamples().get(0).getMolecularWeight());
    assertEquals(database.getPostTranslationModification(),
        submission.getPostTranslationModification());
    assertEquals(database.getSamples().get(0).getType(), submission.getSamples().get(0).getType());
    assertEquals(database.getSamples().size(), submission.getSamples().size());
    for (int i = 0; i < database.getSamples().size(); i++) {
      assertEquals(database.getSamples().get(i).getId(), submission.getSamples().get(i).getId());
      assertEquals(database.getSamples().get(i).getName(),
          submission.getSamples().get(i).getName());
    }
    assertEquals(database.getSamples().get(0).getQuantity(),
        submission.getSamples().get(0).getQuantity());
    assertEquals(database.getSamples().get(0).getVolume(),
        submission.getSamples().get(0).getVolume());
    assertEquals(database.getInjectionType(), submission.getInjectionType());
    assertEquals(database.getSource(), submission.getSource());
    assertEquals(database.getInstrument(), submission.getInstrument());
  }

  @Test
  public void getSubmission_ModifiedFields() {
    setFields();

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(samplesCount, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(sampleType, submission.getSamples().get(i).getType());
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(quantity, submission.getSamples().get(0).getQuantity());
      assertEquals(volume, submission.getSamples().get(0).getVolume());
    }
    assertEquals(injection, submission.getInjectionType());
    assertEquals(source, submission.getSource());
    assertEquals(instrument, submission.getInstrument());
  }

  @Test
  public void setSubmission() {
    form.setSubmission(submission());

    assertEquals(experiment, form.experiment.getValue());
    assertFalse(form.experiment.isReadOnly());
    assertEquals(goal, form.goal.getValue());
    assertFalse(form.goal.isReadOnly());
    assertEquals(taxonomy, form.taxonomy.getValue());
    assertFalse(form.taxonomy.isReadOnly());
    assertEquals(protein, form.protein.getValue());
    assertFalse(form.protein.isReadOnly());
    assertEquals(molecularWeight, Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertFalse(form.molecularWeight.isReadOnly());
    assertEquals(postTranslationModification, form.postTranslationModification.getValue());
    assertFalse(form.postTranslationModification.isReadOnly());
    assertEquals(sampleType, form.sampleType.getValue());
    assertFalse(form.sampleType.isReadOnly());
    assertEquals(samplesCount, Integer.parseInt(form.samplesCount.getValue()));
    assertFalse(form.samplesCount.isReadOnly());
    assertEquals(samplesNames, form.samplesNames.getValue());
    assertFalse(form.samplesNames.isReadOnly());
    assertEquals(quantity, form.quantity.getValue());
    assertFalse(form.quantity.isReadOnly());
    assertEquals(volume, form.volume.getValue());
    assertFalse(form.volume.isReadOnly());
    assertEquals(injection, form.injection.getValue());
    assertFalse(form.injection.isReadOnly());
    assertEquals(source, form.source.getValue());
    assertFalse(form.source.isReadOnly());
    assertEquals(instrument, form.instrument.getValue());
    assertFalse(form.instrument.isReadOnly());
  }

  @Test
  public void setSubmission_ReadOnly() {
    Submission submission = repository.findById(32L).get();
    submission.setProtein(protein);
    submission.getSamples().get(0).setMolecularWeight(molecularWeight);
    submission.setPostTranslationModification(postTranslationModification);

    form.setSubmission(submission);

    assertEquals(submission.getExperiment(), form.experiment.getValue());
    assertTrue(form.experiment.isReadOnly());
    assertEquals(submission.getGoal(), form.goal.getValue());
    assertTrue(form.goal.isReadOnly());
    assertEquals(submission.getTaxonomy(), form.taxonomy.getValue());
    assertTrue(form.taxonomy.isReadOnly());
    assertEquals(submission.getProtein(), form.protein.getValue());
    assertTrue(form.protein.isReadOnly());
    assertEquals(submission.getSamples().get(0).getMolecularWeight(),
        Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertTrue(form.molecularWeight.isReadOnly());
    assertEquals(submission.getPostTranslationModification(),
        form.postTranslationModification.getValue());
    assertTrue(form.postTranslationModification.isReadOnly());
    assertEquals(submission.getSamples().get(0).getType(), form.sampleType.getValue());
    assertTrue(form.sampleType.isReadOnly());
    assertEquals(submission.getSamples().size(), Integer.parseInt(form.samplesCount.getValue()));
    assertTrue(form.samplesCount.isReadOnly());
    assertEquals(
        submission.getSamples().stream().map(Sample::getName).collect(Collectors.joining(", ")),
        form.samplesNames.getValue());
    assertTrue(form.samplesNames.isReadOnly());
    assertEquals(submission.getSamples().get(0).getQuantity(), form.quantity.getValue());
    assertTrue(form.quantity.isReadOnly());
    assertEquals(submission.getSamples().get(0).getVolume(), form.volume.getValue());
    assertTrue(form.volume.isReadOnly());
    assertEquals(submission.getInjectionType(), form.injection.getValue());
    assertTrue(form.injection.isReadOnly());
    assertEquals(submission.getSource(), form.source.getValue());
    assertTrue(form.source.isReadOnly());
    assertEquals(submission.getInstrument(), form.instrument.getValue());
    assertTrue(form.instrument.isReadOnly());
  }

  @Test
  public void setSubmission_NullInstrument() {
    Submission submission = submission();
    submission.setInstrument(null);
    form.setSubmission(submission);

    assertEquals(MassDetectionInstrument.NULL, form.instrument.getValue());
  }
}
