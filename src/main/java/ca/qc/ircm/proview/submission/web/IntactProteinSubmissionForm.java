package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.web.WebConstants.PLACEHOLDER;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.spring.annotation.SpringComponent;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

/**
 * Submission form for {@link Service#INTACT_PROTEIN}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IntactProteinSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String CLASS_NAME = "intactProteinSubmissionForm";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String SAMPLES_NAMES_DUPLICATES = property(SAMPLES + "Names", "duplicate");
  public static final String SAMPLES_NAMES_EXISTS = property(SAMPLES + "Names", "exists");
  public static final String SAMPLES_NAMES_WRONG_COUNT = property(SAMPLES + "Names", "wrongCount");
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  public static final String DEVELOPMENT_TIME_PLACEHOLDER = property(DEVELOPMENT_TIME, PLACEHOLDER);
  public static final String WEIGHT_MARKER_QUANTITY_PLACEHOLDER =
      property(WEIGHT_MARKER_QUANTITY, PLACEHOLDER);
  public static final String PROTEIN_QUANTITY_PLACEHOLDER = property(PROTEIN_QUANTITY, PLACEHOLDER);
  private static final long serialVersionUID = 7704703308278059432L;
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
  protected RadioButtonGroup<InjectionType> injection = new RadioButtonGroup<>();
  protected RadioButtonGroup<MassDetectionInstrumentSource> source = new RadioButtonGroup<>();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  private IntactProteinSubmissionFormPresenter presenter;

  @Autowired
  protected IntactProteinSubmissionForm(IntactProteinSubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    addClassName(CLASS_NAME);
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(experiment, goal, taxonomy, protein, molecularWeight,
        postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume),
        new FormLayout(injection, source, instrument));
    experiment.addClassName(EXPERIMENT);
    goal.addClassName(GOAL);
    taxonomy.addClassName(TAXONOMY);
    protein.addClassName(PROTEIN);
    molecularWeight.addClassName(MOLECULAR_WEIGHT);
    postTranslationModification.addClassName(POST_TRANSLATION_MODIFICATION);
    quantity.addClassName(QUANTITY);
    volume.addClassName(VOLUME);
    sampleType.addClassName(SAMPLES_TYPE);
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    samplesCount.addClassName(SAMPLES_COUNT);
    samplesNames.addClassName(SAMPLES_NAMES);
    samplesNames.setMinHeight("10em");
    injection.addClassName(INJECTION_TYPE);
    injection.setItems(InjectionType.values());
    injection.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    injection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    source.addClassName(SOURCE);
    source.setItems(MassDetectionInstrumentSource.availables());
    source.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    source.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.addClassName(INSTRUMENT);
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources =
        new MessageResource(IntactProteinSubmissionForm.class, getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource sampleResources = new MessageResource(Sample.class, getLocale());
    final MessageResource submissionSampleResources =
        new MessageResource(SubmissionSample.class, getLocale());
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
    sampleType.setLabel(resources.message(SAMPLES_TYPE));
    samplesCount.setLabel(resources.message(SAMPLES_COUNT));
    samplesNames.setLabel(resources.message(SAMPLES_NAMES));
    injection.setLabel(submissionResources.message(INJECTION_TYPE));
    source.setLabel(submissionResources.message(SOURCE));
    instrument.setLabel(submissionResources.message(INSTRUMENT));
    presenter.localeChange(getLocale());
  }

  public boolean isValid() {
    return presenter.isValid();
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
  }
}
