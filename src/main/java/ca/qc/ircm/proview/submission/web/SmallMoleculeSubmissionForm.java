package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.text.Strings.property;

import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
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
 * Submission form for {@link Service#SMALL_MOLECULE}.
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SmallMoleculeSubmissionForm extends FormLayout implements LocaleChangeObserver {
  public static final String CLASS_NAME = "smallMoleculeSubmissionForm";
  public static final String SAMPLE = "sample";
  public static final String SAMPLE_TYPE = SAMPLE + "Type";
  public static final String SAMPLE_NAME = SAMPLE + "Name";
  private static final long serialVersionUID = 7704703308278059432L;
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
  // TODO Create Custom field for solvents.
  protected Checkbox solventAcetonitrile = new Checkbox();
  protected Checkbox solventMethanol = new Checkbox();
  protected Checkbox solventChcl3 = new Checkbox();
  protected Checkbox solventOther = new Checkbox();
  private SmallMoleculeSubmissionFormPresenter presenter;

  @Autowired
  protected SmallMoleculeSubmissionForm(SmallMoleculeSubmissionFormPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    addClassName(CLASS_NAME);
    setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3));
    add(new FormLayout(sampleType, sampleName, solvent, formula),
        new FormLayout(monoisotopicMass, averageMass, toxicity, lightSensitive, storageTemperature),
        new FormLayout(highResolution));
    sampleType.addClassName(SAMPLE_TYPE);
    sampleType.setItems(DRY, SOLUTION);
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    sampleName.addClassName(SAMPLE_NAME);
    solvent.addClassName(SOLUTION_SOLVENT);
    formula.addClassName(FORMULA);
    monoisotopicMass.addClassName(MONOISOTOPIC_MASS);
    averageMass.addClassName(AVERAGE_MASS);
    toxicity.addClassName(TOXICITY);
    lightSensitive.addClassName(LIGHT_SENSITIVE);
    storageTemperature.addClassName(STORAGE_TEMPERATURE);
    storageTemperature.setItems(StorageTemperature.values());
    storageTemperature.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    storageTemperature.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    highResolution.addClassName(HIGH_RESOLUTION);
    highResolution.setItems(false, true);
    highResolution
        .setRenderer(new TextRenderer<>(value -> new MessageResource(Submission.class, getLocale())
            .message(property(HIGH_RESOLUTION, value))));
    highResolution.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(SmallMoleculeSubmissionForm.class,
        getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
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
    presenter.localeChange(getLocale());
  }

  public Submission getSubmission() {
    return presenter.getSubmission();
  }

  public void setSubmission(Submission submission) {
    presenter.setSubmission(submission);
  }
}
