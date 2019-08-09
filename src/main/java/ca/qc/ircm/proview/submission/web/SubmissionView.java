package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.FORMULA;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MONOISOTOPIC_MASS;
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
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SOURCE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TOXICITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.PLACEHOLDER;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleProperties;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.user.UserRole;
import ca.qc.ircm.proview.web.ViewLayout;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Submission view.
 */
@Route(value = SubmissionView.VIEW_NAME, layout = ViewLayout.class)
@RolesAllowed({ UserRole.USER })
public class SubmissionView extends VerticalLayout
    implements HasDynamicTitle, HasUrlParameter<Long>, LocaleChangeObserver {
  public static final String VIEW_NAME = "submission";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String SAMPLE = "sample";
  public static final String SAMPLE_NAME = property(SAMPLE, SampleProperties.NAME);
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  public static final String DEVELOPMENT_TIME_PLACEHOLDER = property(DEVELOPMENT_TIME, PLACEHOLDER);
  public static final String WEIGHT_MARKER_QUANTITY_PLACEHOLDER =
      property(WEIGHT_MARKER_QUANTITY, PLACEHOLDER);
  public static final String PROTEIN_QUANTITY_PLACEHOLDER = property(PROTEIN_QUANTITY, PLACEHOLDER);
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionView.class);
  protected H2 header = new H2();
  protected Tabs service = new Tabs();
  protected Tab lcmsms = new Tab();
  protected Tab smallMolecule = new Tab();
  protected Tab intactProtein = new Tab();
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
  protected TextField solvent = new TextField();
  protected TextField sampleName = new TextField();
  protected TextField formula = new TextField();
  protected TextField monoisotopicMass = new TextField();
  protected TextField averageMass = new TextField();
  protected TextField toxicity = new TextField();
  protected Checkbox lightSensitive = new Checkbox();
  protected RadioButtonGroup<StorageTemperature> storageTemperature = new RadioButtonGroup<>();
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
  protected RadioButtonGroup<InjectionType> injection = new RadioButtonGroup<>();
  protected RadioButtonGroup<MassDetectionInstrumentSource> source = new RadioButtonGroup<>();
  protected ComboBox<MassDetectionInstrument> instrument = new ComboBox<>();
  protected RadioButtonGroup<ProteinIdentification> identification = new RadioButtonGroup<>();
  protected TextField identificationLink = new TextField();
  protected ComboBox<Quantification> quantification = new ComboBox<>();
  protected TextArea quantificationComment = new TextArea();
  protected RadioButtonGroup<Boolean> highResolution = new RadioButtonGroup<>();
  // TODO Create Custom field for solvents.
  protected Checkbox solventAcetonitrile = new Checkbox();
  protected Checkbox solventMethanol = new Checkbox();
  protected Checkbox solventChcl3 = new Checkbox();
  protected Checkbox solventOther = new Checkbox();
  protected TextArea comment = new TextArea();
  private SubmissionViewPresenter presenter;

  @Autowired
  protected SubmissionView(SubmissionViewPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    logger.debug("Submission view");
    setId(ID);
    FormLayout formLayout = new FormLayout();
    formLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3), new ResponsiveStep("15em", 4));
    formLayout.add(
        new FormLayout(service, experiment, goal, taxonomy, protein, molecularWeight,
            postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume, separation,
            thickness, coloration, otherColoration, developmentTime, destained,
            weightMarkerQuantity),
        new FormLayout(digestion, usedDigestion, otherDigestion, proteinContent, injection, source,
            instrument, identification, identificationLink, quantification, quantificationComment,
            highResolution),
        new FormLayout(comment));
    add(header, service, formLayout);
    header.setId(HEADER);
    service.setId(SERVICE);
    service.add(lcmsms, smallMolecule, intactProtein);
    lcmsms.setId(LC_MS_MS.name());
    smallMolecule.setId(SMALL_MOLECULE.name());
    intactProtein.setId(INTACT_PROTEIN.name());
    experiment.setId(EXPERIMENT);
    goal.setId(GOAL);
    taxonomy.setId(TAXONOMY);
    protein.setId(PROTEIN);
    molecularWeight.setId(MOLECULAR_WEIGHT);
    postTranslationModification.setId(POST_TRANSLATION_MODIFICATION);
    quantity.setId(QUANTITY);
    volume.setId(VOLUME);
    sampleType.setId(SAMPLES_TYPE);
    sampleType.setItems(SampleType.values());
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    samplesCount.setId(SAMPLES_COUNT);
    samplesNames.setId(SAMPLES_NAMES);
    samplesNames.setMinHeight("10em");
    sampleName.setId(SAMPLE_NAME);
    formula.setId(FORMULA);
    monoisotopicMass.setId(MONOISOTOPIC_MASS);
    averageMass.setId(AVERAGE_MASS);
    toxicity.setId(TOXICITY);
    lightSensitive.setId(LIGHT_SENSITIVE);
    storageTemperature.setId(STORAGE_TEMPERATURE);
    separation.setId(SEPARATION);
    separation.setItems(GelSeparation.values());
    separation.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    thickness.setId(THICKNESS);
    thickness.setItems(GelThickness.values());
    thickness.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    coloration.setId(COLORATION);
    coloration.setItems(GelColoration.values());
    coloration.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    otherColoration.setId(OTHER_COLORATION);
    developmentTime.setId(DEVELOPMENT_TIME);
    destained.setId(DECOLORATION);
    weightMarkerQuantity.setId(WEIGHT_MARKER_QUANTITY);
    proteinQuantity.setId(PROTEIN_QUANTITY);
    digestion.setId(DIGESTION);
    digestion.setItems(ProteolyticDigestion.values());
    digestion.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    usedDigestion.setId(USED_DIGESTION);
    otherDigestion.setId(OTHER_DIGESTION);
    proteinContent.setId(PROTEIN_CONTENT);
    proteinContent.setItems(ProteinContent.values());
    proteinContent.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    proteinContent.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    injection.setId(INJECTION_TYPE);
    injection.setItems(InjectionType.values());
    injection.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    injection.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    source.setId(SOURCE);
    source.setItems(MassDetectionInstrumentSource.availables());
    source.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    source.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.setId(INSTRUMENT);
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    identification.setId(IDENTIFICATION);
    identification.setItems(ProteinIdentification.availables());
    identification.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    identificationLink.setId(IDENTIFICATION_LINK);
    quantification.setId(QUANTIFICATION);
    quantification.setItems(Quantification.values());
    quantification.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    quantificationComment.setId(QUANTIFICATION_COMMENT);
    highResolution.setId(HIGH_RESOLUTION);
    highResolution.setItems(false, true);
    highResolution
        .setRenderer(new TextRenderer<>(value -> new MessageResource(Submission.class, getLocale())
            .message(property(HIGH_RESOLUTION, value))));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    comment.setId(COMMENT);
    comment.setMinHeight("20em");
    presenter.init(this);
  }

  @Override
  public void localeChange(LocaleChangeEvent event) {
    final MessageResource resources = new MessageResource(SubmissionView.class, getLocale());
    final MessageResource submissionResources = new MessageResource(Submission.class, getLocale());
    final MessageResource sampleResources = new MessageResource(Sample.class, getLocale());
    final MessageResource submissionSampleResources =
        new MessageResource(SubmissionSample.class, getLocale());
    header.setText(resources.message(HEADER));
    lcmsms.setLabel(LC_MS_MS.getLabel(getLocale()));
    smallMolecule.setLabel(SMALL_MOLECULE.getLabel(getLocale()));
    intactProtein.setLabel(INTACT_PROTEIN.getLabel(getLocale()));
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
    sampleName.setLabel(sampleResources.message(SampleProperties.NAME));
    formula.setLabel(submissionResources.message(FORMULA));
    monoisotopicMass.setLabel(submissionResources.message(MONOISOTOPIC_MASS));
    averageMass.setLabel(submissionResources.message(AVERAGE_MASS));
    toxicity.setLabel(submissionResources.message(TOXICITY));
    lightSensitive.setLabel(submissionResources.message(LIGHT_SENSITIVE));
    storageTemperature.setLabel(submissionResources.message(STORAGE_TEMPERATURE));
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
    injection.setLabel(submissionResources.message(INJECTION_TYPE));
    source.setLabel(submissionResources.message(SOURCE));
    instrument.setLabel(submissionResources.message(INSTRUMENT));
    identification.setLabel(submissionResources.message(IDENTIFICATION));
    identificationLink.setLabel(submissionResources.message(IDENTIFICATION_LINK));
    quantification.setLabel(submissionResources.message(QUANTIFICATION));
    quantificationComment.setLabel(submissionResources.message(QUANTIFICATION_COMMENT));
    highResolution.setLabel(submissionResources.message(HIGH_RESOLUTION));
    comment.setLabel(submissionResources.message(COMMENT));
    presenter.localeChange(getLocale());
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter Long parameter) {
    presenter.setParameter(parameter);
  }
}
