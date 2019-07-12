package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SAMPLES;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_PROTEOLYTIC_DIGESTION_METHOD;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.text.Strings.styleName;
import static ca.qc.ircm.proview.web.WebConstants.APPLICATION_NAME;
import static ca.qc.ircm.proview.web.WebConstants.PLACEHOLDER;
import static ca.qc.ircm.proview.web.WebConstants.TITLE;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
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
    implements HasDynamicTitle, LocaleChangeObserver {
  public static final String VIEW_NAME = "submission";
  public static final String ID = styleName(VIEW_NAME, "view");
  public static final String HEADER = "header";
  public static final String SAMPLES_TYPE = SAMPLES + "Type";
  public static final String SAMPLES_COUNT = SAMPLES + "Count";
  public static final String SAMPLES_NAMES = SAMPLES + "Names";
  public static final String QUANTITY_PLACEHOLDER = property(QUANTITY, PLACEHOLDER);
  public static final String VOLUME_PLACEHOLDER = property(VOLUME, PLACEHOLDER);
  public static final String DEVELOPMENT_TIME_PLACEHOLDER = property(DEVELOPMENT_TIME, PLACEHOLDER);
  public static final String WEIGHT_MARKER_QUANTITY_PLACEHOLDER =
      property(WEIGHT_MARKER_QUANTITY, PLACEHOLDER);
  public static final String PROTEIN_QUANTITY_PLACEHOLDER = property(PROTEIN_QUANTITY, PLACEHOLDER);
  private static final long serialVersionUID = 7704703308278059432L;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionView.class);
  protected H2 header = new H2();
  protected RadioButtonGroup<Service> service = new RadioButtonGroup<>();
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
  protected RadioButtonGroup<MassDetectionInstrument> instrument = new RadioButtonGroup<>();
  protected RadioButtonGroup<ProteinIdentification> identification = new RadioButtonGroup<>();
  protected TextField identificationLink = new TextField();
  protected ComboBox<Quantification> quantification = new ComboBox<>();
  protected TextArea quantificationComment = new TextArea();
  protected TextArea comment = new TextArea();
  private SubmissionViewPresenter presenter;

  @Autowired
  protected SubmissionView(SubmissionViewPresenter presenter) {
    this.presenter = presenter;
  }

  @PostConstruct
  void init() {
    logger.debug("Submission dialog");
    setId(ID);
    VerticalLayout layout = new VerticalLayout();
    layout.setMaxWidth("90em");
    layout.setMinWidth("22em");
    add(layout);
    FormLayout formLayout = new FormLayout();
    formLayout.setResponsiveSteps(new ResponsiveStep("15em", 1), new ResponsiveStep("15em", 2),
        new ResponsiveStep("15em", 3), new ResponsiveStep("15em", 4));
    formLayout.add(
        new FormLayout(service, experiment, goal, taxonomy, protein, molecularWeight,
            postTranslationModification),
        new FormLayout(sampleType, samplesCount, samplesNames, quantity, volume, separation,
            thickness, coloration, otherColoration, developmentTime, destained,
            weightMarkerQuantity),
        new FormLayout(digestion, usedDigestion, otherDigestion, proteinContent, instrument,
            identification, identificationLink, quantification, quantificationComment),
        new FormLayout(comment));
    layout.add(header, formLayout);
    header.addClassName(HEADER);
    service.addClassName(SERVICE);
    service.setItems(Service.availables());
    service.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    service.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    experiment.addClassName(EXPERIMENT);
    goal.addClassName(GOAL);
    taxonomy.addClassName(TAXONOMY);
    protein.addClassName(PROTEIN);
    molecularWeight.addClassName(MOLECULAR_WEIGHT);
    postTranslationModification.addClassName(POST_TRANSLATION_MODIFICATION);
    quantity.addClassName(QUANTITY);
    volume.addClassName(VOLUME);
    sampleType.addClassName(SAMPLES_TYPE);
    sampleType.setItems(SampleType.values());
    sampleType.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    sampleType.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    samplesCount.addClassName(SAMPLES_COUNT);
    samplesNames.addClassName(SAMPLES_NAMES);
    samplesNames.setMinHeight("10em");
    separation.addClassName(SEPARATION);
    separation.setItems(GelSeparation.values());
    separation.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    thickness.addClassName(THICKNESS);
    thickness.setItems(GelThickness.values());
    thickness.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    coloration.addClassName(COLORATION);
    coloration.setItems(GelColoration.values());
    coloration.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    otherColoration.addClassName(OTHER_COLORATION);
    developmentTime.addClassName(DEVELOPMENT_TIME);
    destained.addClassName(DECOLORATION);
    weightMarkerQuantity.addClassName(WEIGHT_MARKER_QUANTITY);
    proteinQuantity.addClassName(PROTEIN_QUANTITY);
    digestion.addClassName(PROTEOLYTIC_DIGESTION_METHOD);
    digestion.setItems(ProteolyticDigestion.values());
    digestion.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    usedDigestion.addClassName(USED_PROTEOLYTIC_DIGESTION_METHOD);
    otherDigestion.addClassName(OTHER_PROTEOLYTIC_DIGESTION_METHOD);
    proteinContent.addClassName(PROTEIN_CONTENT);
    proteinContent.setItems(ProteinContent.values());
    proteinContent.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    proteinContent.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    instrument.addClassName(MASS_DETECTION_INSTRUMENT);
    instrument.setItems(MassDetectionInstrument.userChoices());
    instrument.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    instrument.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    identification.addClassName(PROTEIN_IDENTIFICATION);
    identification.setItems(ProteinIdentification.availables());
    identification.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    identification.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL);
    identificationLink.addClassName(PROTEIN_IDENTIFICATION_LINK);
    quantification.addClassName(QUANTIFICATION);
    quantification.setItems(Quantification.values());
    quantification.setRenderer(new TextRenderer<>(value -> value.getLabel(getLocale())));
    quantification.setItemLabelGenerator(value -> value.getLabel(getLocale()));
    quantificationComment.addClassName(QUANTIFICATION_COMMENT);
    comment.addClassName(COMMENT);
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
    service.setLabel(submissionResources.message(SERVICE));
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
    digestion.setLabel(submissionResources.message(PROTEOLYTIC_DIGESTION_METHOD));
    usedDigestion.setLabel(submissionResources.message(USED_PROTEOLYTIC_DIGESTION_METHOD));
    otherDigestion.setLabel(submissionResources.message(OTHER_PROTEOLYTIC_DIGESTION_METHOD));
    proteinContent.setLabel(submissionResources.message(PROTEIN_CONTENT));
    instrument.setLabel(submissionResources.message(MASS_DETECTION_INSTRUMENT));
    identification.setLabel(submissionResources.message(PROTEIN_IDENTIFICATION));
    identificationLink.setLabel(submissionResources.message(PROTEIN_IDENTIFICATION_LINK));
    quantification.setLabel(submissionResources.message(QUANTIFICATION));
    quantificationComment.setLabel(submissionResources.message(QUANTIFICATION_COMMENT));
    comment.setLabel(submissionResources.message(COMMENT));
    presenter.localeChange(getLocale());
  }

  @Override
  public String getPageTitle() {
    final MessageResource resources = new MessageResource(getClass(), getLocale());
    final MessageResource generalResources = new MessageResource(WebConstants.class, getLocale());
    return resources.message(TITLE, generalResources.message(APPLICATION_NAME));
  }
}
