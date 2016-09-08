package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.LTQ_ORBI_TRAP;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.Q_EXACTIVE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.TSQ_VANTAGE;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.QEluateSample.eluateSample;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Eluate sample submission presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EluateSubmissionViewPresenter {
  public static final String HEADER_LABEL_ID = "header";
  public static final String SERVICE_LABEL_ID = "service";
  public static final String INFORMATION_LABEL_ID = "information";
  public static final String SAMPLE_COUNT_PANEL_ID = "sampleCountPanel";
  public static final String SAMPLE_COUNT_PROPERTY = "sampleCount";
  public static final String SAMPLE_NAMES_PANEL_ID = "sampleNamesPanel";
  public static final String SAMPLE_NAMES_FORM_ID = "sampleNamesForm";
  public static final String SAMPLE_NAMES_PROPERTY = eluateSample.name.getMetadata().getName();
  public static final String FIRST_SAMPLE_NAME_PROPERTY = "sampleName1";
  public static final String FILL_SAMPLE_NAMES_PROPERTY = "fillSampleNames";
  public static final String EXPERIENCE_PANEL_ID = "experiencePanel";
  public static final String PROJECT_PROPERTY = eluateSample.project.getMetadata().getName();
  public static final String EXPERIENCE_PROPERTY = eluateSample.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL_PROPERTY = eluateSample.goal.getMetadata().getName();
  public static final String SAMPLE_DETAILS_PANEL_ID = "sampleDetailsPanel";
  public static final String TAXONOMY_PROPERTY = eluateSample.taxonomy.getMetadata().getName();
  public static final String PROTEIN_NAME_PROPERTY = eluateSample.protein.getMetadata().getName();
  public static final String PROTEIN_WEIGHT_PROPERTY =
      eluateSample.molecularWeight.getMetadata().getName();
  public static final String POST_TRANSLATION_MODIFICATION_PROPERTY =
      eluateSample.postTranslationModification.getMetadata().getName();
  public static final String SAMPLE_VOLUME_PROPERTY = eluateSample.volume.getMetadata().getName();
  public static final String SAMPLE_QUANTITY_PROPERTY =
      eluateSample.quantity.getMetadata().getName();
  public static final String STANDARDS_PANEL_ID = "standardsPanel";
  public static final String CONTAMINANTS_PANEL_ID = "contaminantsPanel";
  public static final String DIGESTION_PANEL_ID = "digestionPanel";
  public static final String DIGESTION_PROPERTY =
      eluateSample.proteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT_PANEL_ID = "enrichmentPanel";
  public static final String EXCLUSIONS_PANEL_ID = "exclusionsPanel";
  public static final String INSTRUMENT_PANEL_ID = "instrumentPanel";
  public static final String INSTRUMENT_PROPERTY =
      eluateSample.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_PANEL_ID = "proteinIdentificationPanel";
  public static final String PROTEIN_IDENTIFICATION_PROPERTY =
      eluateSample.proteinIdentification.getMetadata().getName();
  public static final String COMMENTS_PANEL_ID = "commentsPanel";
  public static final String COMMENTS_PROPERTY = eluateSample.comments.getMetadata().getName();
  private static final ProteolyticDigestion[] digestions =
      new ProteolyticDigestion[] { TRYPSIN, DIGESTED, ProteolyticDigestion.OTHER };
  private static final MassDetectionInstrument[] instruments = new MassDetectionInstrument[] {
      VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION, LTQ_ORBI_TRAP };
  private static final ProteinIdentification[] proteinIdentifications =
      new ProteinIdentification[] { REFSEQ, UNIPROT, ProteinIdentification.OTHER };
  private static final Logger logger = LoggerFactory.getLogger(EluateSubmissionViewPresenter.class);
  private EluateSubmissionView view;
  private BeanFieldGroup<EluateSample> firstSampleFieldGroup =
      new BeanFieldGroup<>(EluateSample.class);
  private Label headerLabel;
  private Label serviceLabel;
  private Label informationLabel;
  private Panel sampleCountPanel;
  private TextField sampleCountField;
  private Panel sampleNamesPanel;
  private FormLayout sampleNamesForm;
  private TextField sampleNameField;
  private Button fillSampleNameButton;
  private Panel experiencePanel;
  private ComboBox projectField;
  private TextField experienceField;
  private TextField experienceGoalField;
  private Panel sampleDetailsPanel;
  private TextField taxonomyField;
  private TextField proteinNameField;
  private TextField proteinWeightField;
  private TextField postTranslationModificationField;
  private TextField sampleVolumeField;
  private TextField sampleQuantityField;
  private Panel standardsPanel;
  private Panel contaminantsPanel;
  private Panel digestionPanel;
  private OptionGroup digestionOptions;
  private Panel enrichmentPanel;
  private Panel exclusionsPanel;
  private Panel instrumentPanel;
  private OptionGroup instrumentOptions;
  private Panel proteinIdentificationPanel;
  private OptionGroup proteinIdentificationOptions;
  private Panel commentsPanel;
  private TextArea commentsField;

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(EluateSubmissionView view) {
    this.view = view;
    setFields();
  }

  private void setFields() {
    headerLabel = view.getHeaderLabel();
    serviceLabel = view.getServiceLabel();
    informationLabel = view.getInformationLabel();
    sampleCountPanel = view.getSampleCountPanel();
    sampleCountField = view.getSampleCountField();
    sampleNamesPanel = view.getSampleNamesPanel();
    sampleNamesForm = view.getSampleNamesForm();
    sampleNameField = view.getSampleNameField();
    fillSampleNameButton = view.getFillSampleNameButton();
    experiencePanel = view.getExperiencePanel();
    projectField = view.getProjectField();
    experienceField = view.getExperienceField();
    experienceGoalField = view.getExperienceGoalField();
    sampleDetailsPanel = view.getSampleDetailsPanel();
    taxonomyField = view.getTaxonomyField();
    proteinNameField = view.getProteinNameField();
    proteinWeightField = view.getProteinWeightField();
    postTranslationModificationField = view.getPostTranslationModificationField();
    sampleVolumeField = view.getSampleVolumeField();
    sampleQuantityField = view.getSampleQuantityField();
    standardsPanel = view.getStandardsPanel();
    contaminantsPanel = view.getContaminantsPanel();
    digestionPanel = view.getDigestionPanel();
    digestionOptions = view.getDigestionOptions();
    enrichmentPanel = view.getEnrichmentPanel();
    exclusionsPanel = view.getExclusionsPanel();
    instrumentPanel = view.getInstrumentPanel();
    instrumentOptions = view.getInstrumentOptions();
    proteinIdentificationPanel = view.getProteinIdentificationPanel();
    proteinIdentificationOptions = view.getProteinIdentificationOptions();
    commentsPanel = view.getCommentsPanel();
    commentsField = view.getCommentsField();
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    logger.debug("Eluate submission view");
    setIds();
    bindFields();
    addFieldListeners();
    setCaptions();
  }

  private void setIds() {
    headerLabel.setId(HEADER_LABEL_ID);
    serviceLabel.setId(SERVICE_LABEL_ID);
    informationLabel.setId(INFORMATION_LABEL_ID);
    sampleCountPanel.setId(SAMPLE_COUNT_PANEL_ID);
    sampleCountField.setId(SAMPLE_COUNT_PROPERTY);
    sampleNamesPanel.setId(SAMPLE_NAMES_PANEL_ID);
    sampleNamesForm.setId(SAMPLE_NAMES_FORM_ID);
    sampleNameField.setId(FIRST_SAMPLE_NAME_PROPERTY);
    fillSampleNameButton.setId(FILL_SAMPLE_NAMES_PROPERTY);
    experiencePanel.setId(EXPERIENCE_PANEL_ID);
    projectField.setId(PROJECT_PROPERTY);
    experienceField.setId(EXPERIENCE_PROPERTY);
    experienceGoalField.setId(EXPERIENCE_GOAL_PROPERTY);
    sampleDetailsPanel.setId(SAMPLE_DETAILS_PANEL_ID);
    taxonomyField.setId(TAXONOMY_PROPERTY);
    proteinNameField.setId(PROTEIN_NAME_PROPERTY);
    proteinWeightField.setId(PROTEIN_WEIGHT_PROPERTY);
    postTranslationModificationField.setId(POST_TRANSLATION_MODIFICATION_PROPERTY);
    sampleVolumeField.setId(SAMPLE_VOLUME_PROPERTY);
    sampleQuantityField.setId(SAMPLE_QUANTITY_PROPERTY);
    standardsPanel.setId(STANDARDS_PANEL_ID);
    contaminantsPanel.setId(CONTAMINANTS_PANEL_ID);
    digestionPanel.setId(DIGESTION_PANEL_ID);
    digestionOptions.setId(DIGESTION_PROPERTY);
    enrichmentPanel.setId(ENRICHEMENT_PANEL_ID);
    exclusionsPanel.setId(EXCLUSIONS_PANEL_ID);
    instrumentPanel.setId(INSTRUMENT_PANEL_ID);
    instrumentOptions.setId(INSTRUMENT_PROPERTY);
    proteinIdentificationPanel.setId(PROTEIN_IDENTIFICATION_PANEL_ID);
    proteinIdentificationOptions.setId(PROTEIN_IDENTIFICATION_PROPERTY);
    commentsPanel.setId(COMMENTS_PANEL_ID);
    commentsField.setId(COMMENTS_PROPERTY);
  }

  private void bindFields() {
    firstSampleFieldGroup.bind(sampleNameField, SAMPLE_NAMES_PROPERTY);
    firstSampleFieldGroup.bind(projectField, PROJECT_PROPERTY);
    firstSampleFieldGroup.bind(experienceField, EXPERIENCE_PROPERTY);
    firstSampleFieldGroup.bind(experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    firstSampleFieldGroup.bind(taxonomyField, TAXONOMY_PROPERTY);
    firstSampleFieldGroup.bind(proteinNameField, PROTEIN_NAME_PROPERTY);
    firstSampleFieldGroup.bind(proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    firstSampleFieldGroup.bind(postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    firstSampleFieldGroup.bind(sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    firstSampleFieldGroup.bind(digestionOptions, DIGESTION_PROPERTY);
    firstSampleFieldGroup.bind(instrumentOptions, INSTRUMENT_PROPERTY);
    firstSampleFieldGroup.bind(proteinIdentificationOptions, PROTEIN_IDENTIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(commentsField, COMMENTS_PROPERTY);
  }

  private void addFieldListeners() {
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    serviceLabel.setValue(resources.message(SERVICE_LABEL_ID));
    informationLabel.setValue(resources.message(INFORMATION_LABEL_ID));
    sampleCountPanel.setCaption(resources.message(SAMPLE_COUNT_PANEL_ID));
    sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    sampleNamesPanel.setCaption(resources.message(SAMPLE_NAMES_PANEL_ID));
    sampleNameField.setCaption(resources.message(SAMPLE_NAMES_PROPERTY, 1));
    fillSampleNameButton.setCaption(resources.message(FILL_SAMPLE_NAMES_PROPERTY));
    experiencePanel.setCaption(resources.message(EXPERIENCE_PANEL_ID));
    projectField.setCaption(resources.message(PROJECT_PROPERTY));
    experienceField.setCaption(resources.message(EXPERIENCE_PROPERTY));
    experienceGoalField.setCaption(resources.message(EXPERIENCE_GOAL_PROPERTY));
    sampleDetailsPanel.setCaption(resources.message(SAMPLE_DETAILS_PANEL_ID));
    taxonomyField.setCaption(resources.message(TAXONOMY_PROPERTY));
    proteinNameField.setCaption(resources.message(PROTEIN_NAME_PROPERTY));
    proteinWeightField.setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY));
    postTranslationModificationField
        .setCaption(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY));
    sampleVolumeField.setCaption(resources.message(SAMPLE_VOLUME_PROPERTY));
    sampleQuantityField.setCaption(resources.message(SAMPLE_QUANTITY_PROPERTY));
    standardsPanel.setCaption(resources.message(STANDARDS_PANEL_ID));
    contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL_ID));
    digestionPanel.setCaption(resources.message(DIGESTION_PANEL_ID));
    digestionOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    digestionOptions.removeAllItems();
    for (ProteolyticDigestion digestion : digestions) {
      digestionOptions.addItem(digestion);
      digestionOptions.setItemCaption(digestion, digestion.getLabel(view.getLocale()));
    }
    enrichmentPanel.setCaption(resources.message(ENRICHEMENT_PANEL_ID));
    exclusionsPanel.setCaption(resources.message(EXCLUSIONS_PANEL_ID));
    instrumentPanel.setCaption(resources.message(INSTRUMENT_PANEL_ID));
    instrumentOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    instrumentOptions.removeAllItems();
    for (MassDetectionInstrument instrument : instruments) {
      instrumentOptions.addItem(instrument);
      instrumentOptions.setItemCaption(instrument, instrument.getLabel(view.getLocale()));
    }
    proteinIdentificationPanel.setCaption(resources.message(PROTEIN_IDENTIFICATION_PANEL_ID));
    proteinIdentificationOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    proteinIdentificationOptions.removeAllItems();
    for (ProteinIdentification proteinIdentification : proteinIdentifications) {
      proteinIdentificationOptions.addItem(proteinIdentification);
      proteinIdentificationOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(view.getLocale()));
    }
    commentsPanel.setCaption(resources.message(COMMENTS_PANEL_ID));
  }
}
