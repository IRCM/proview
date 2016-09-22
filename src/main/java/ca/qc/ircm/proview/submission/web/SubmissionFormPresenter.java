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
import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QEluateSample.eluateSample;
import static ca.qc.ircm.proview.sample.QStandard.standard;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.EluateSample;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.utils.web.EmptyNullTableFieldFactory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
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
public class SubmissionFormPresenter {
  public static final String HEADER_LABEL_ID = "header";
  public static final String SERVICE_LABEL_ID = "service";
  public static final String SAMPLE_TYPE_LABEL_ID = "sampleType";
  public static final String INACTIVE_LABEL_ID = "inactive";
  public static final String SAMPLE_COUNT_PANEL_ID = "sampleCountPanel";
  public static final String SAMPLE_COUNT_PROPERTY = "sampleCount";
  public static final String SAMPLE_NAMES_PANEL_ID = "sampleNamesPanel";
  public static final String SAMPLE_NAMES_FORM_ID = "sampleNamesForm";
  public static final String SAMPLE_NAMES_PROPERTY = eluateSample.name.getMetadata().getName();
  public static final String FIRST_SAMPLE_NAME_PROPERTY = "sampleName1";
  public static final String FILL_SAMPLE_NAMES_PROPERTY = "fillSampleNames";
  public static final String EXPERIENCE_PANEL_ID = "experiencePanel";
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
  public static final String STANDARD_COUNT_PROPERTY = "standardCount";
  public static final String STANDARD_PROPERTY = standard.getMetadata().getName();
  public static final String STANDARD_NAME_PROPERTY = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY_PROPERTY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENTS_PROPERTY = standard.comments.getMetadata().getName();
  public static final String FILL_STANDARDS_PROPERTY = "fillStandards";
  public static final String CONTAMINANTS_PANEL_ID = "contaminantsPanel";
  public static final String CONTAMINANT_COUNT_PROPERTY = "contaminantCount";
  public static final String CONTAMINANT_PROPERTY = contaminant.getMetadata().getName();
  public static final String CONTAMINANT_NAME_PROPERTY = contaminant.name.getMetadata().getName();
  public static final String CONTAMINANT_QUANTITY_PROPERTY =
      contaminant.quantity.getMetadata().getName();
  public static final String CONTAMINANT_COMMENTS_PROPERTY =
      contaminant.comments.getMetadata().getName();
  public static final String FILL_CONTAMINANTS_PROPERTY = "fillContaminants";
  public static final String SERVICES_PANEL_ID = "servicesPanel";
  public static final String DIGESTION_PROPERTY =
      eluateSample.proteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT_PROPERTY = "enrichment";
  public static final String EXCLUSIONS_PROPERTY = "exclusions";
  public static final String INSTRUMENT_PROPERTY =
      eluateSample.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_PROPERTY =
      eluateSample.proteinIdentification.getMetadata().getName();
  public static final String COMMENTS_PANEL_ID = "commentsPanel";
  public static final String COMMENTS_PROPERTY = eluateSample.comments.getMetadata().getName();
  public static final String SUBMIT_ID = "submit";
  private static final Object[] standardsColumns = new Object[] { STANDARD_NAME_PROPERTY,
      STANDARD_QUANTITY_PROPERTY, STANDARD_COMMENTS_PROPERTY };
  private static final Object[] contaminantsColumns = new Object[] { CONTAMINANT_NAME_PROPERTY,
      CONTAMINANT_QUANTITY_PROPERTY, CONTAMINANT_COMMENTS_PROPERTY };
  private static final ProteolyticDigestion[] digestions =
      new ProteolyticDigestion[] { TRYPSIN, DIGESTED, ProteolyticDigestion.OTHER };
  private static final MassDetectionInstrument[] instruments = new MassDetectionInstrument[] {
      VELOS, Q_EXACTIVE, TSQ_VANTAGE, ORBITRAP_FUSION, LTQ_ORBI_TRAP };
  private static final ProteinIdentification[] proteinIdentifications =
      new ProteinIdentification[] { REFSEQ, UNIPROT, ProteinIdentification.OTHER };
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenter.class);
  private SubmissionForm view;
  private BeanFieldGroup<EluateSample> firstSampleFieldGroup =
      new BeanFieldGroup<>(EluateSample.class);
  private BeanItemContainer<Standard> standardsContainer = new BeanItemContainer<>(Standard.class);
  private BeanItemContainer<Contaminant> contaminantsContainer =
      new BeanItemContainer<>(Contaminant.class);

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionForm view) {
    this.view = view;
  }

  /**
   * Called when view gets attached.
   */
  public void attach() {
    logger.debug("Eluate submission view");
    setIds();
    setConverters();
    bindFields();
    addFieldListeners();
    setCaptions();
    setDefaults();
  }

  private void setIds() {
    view.headerLabel.setId(HEADER_LABEL_ID);
    view.serviceLabel.setId(SERVICE_LABEL_ID);
    view.sampleTypeLabel.setId(SAMPLE_TYPE_LABEL_ID);
    view.inactiveLabel.setId(INACTIVE_LABEL_ID);
    view.sampleCountPanel.setId(SAMPLE_COUNT_PANEL_ID);
    view.sampleCountField.setId(SAMPLE_COUNT_PROPERTY);
    view.sampleNamesPanel.setId(SAMPLE_NAMES_PANEL_ID);
    view.sampleNamesForm.setId(SAMPLE_NAMES_FORM_ID);
    view.sampleNameField.setId(FIRST_SAMPLE_NAME_PROPERTY);
    view.fillSampleNamesButton.setId(FILL_SAMPLE_NAMES_PROPERTY);
    view.experiencePanel.setId(EXPERIENCE_PANEL_ID);
    view.experienceField.setId(EXPERIENCE_PROPERTY);
    view.experienceGoalField.setId(EXPERIENCE_GOAL_PROPERTY);
    view.taxonomyField.setId(TAXONOMY_PROPERTY);
    view.proteinNameField.setId(PROTEIN_NAME_PROPERTY);
    view.proteinWeightField.setId(PROTEIN_WEIGHT_PROPERTY);
    view.postTranslationModificationField.setId(POST_TRANSLATION_MODIFICATION_PROPERTY);
    view.sampleVolumeField.setId(SAMPLE_VOLUME_PROPERTY);
    view.sampleQuantityField.setId(SAMPLE_QUANTITY_PROPERTY);
    view.standardsPanel.setId(STANDARDS_PANEL_ID);
    view.standardCountField.setId(STANDARD_COUNT_PROPERTY);
    view.standardsTable.setId(STANDARD_PROPERTY);
    view.fillStandardsButton.setId(FILL_STANDARDS_PROPERTY);
    view.contaminantsPanel.setId(CONTAMINANTS_PANEL_ID);
    view.contaminantCountField.setId(CONTAMINANT_COUNT_PROPERTY);
    view.contaminantsTable.setId(CONTAMINANT_PROPERTY);
    view.fillContaminantsButton.setId(FILL_CONTAMINANTS_PROPERTY);
    view.servicesPanel.setId(SERVICES_PANEL_ID);
    view.digestionOptionsLayout.setId(DIGESTION_PROPERTY);
    view.enrichmentLabel.setId(ENRICHEMENT_PROPERTY);
    view.exclusionsLabel.setId(EXCLUSIONS_PROPERTY);
    view.instrumentOptions.setId(INSTRUMENT_PROPERTY);
    view.proteinIdentificationOptionsLayout.setId(PROTEIN_IDENTIFICATION_PROPERTY);
    view.commentsPanel.setId(COMMENTS_PANEL_ID);
    view.commentsField.setId(COMMENTS_PROPERTY);
    view.submitButton.setId(SUBMIT_ID);
  }

  private void setConverters() {
    view.sampleCountField.setConverter(new StringToIntegerConverter());
    view.sampleVolumeField.setConverter(new StringToDoubleConverter());
    view.standardCountField.setConverter(new StringToIntegerConverter());
    view.contaminantCountField.setConverter(new StringToIntegerConverter());
  }

  private void bindFields() {
    firstSampleFieldGroup.bind(view.sampleNameField, SAMPLE_NAMES_PROPERTY);
    firstSampleFieldGroup.bind(view.experienceField, EXPERIENCE_PROPERTY);
    firstSampleFieldGroup.bind(view.experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    firstSampleFieldGroup.bind(view.taxonomyField, TAXONOMY_PROPERTY);
    firstSampleFieldGroup.bind(view.proteinNameField, PROTEIN_NAME_PROPERTY);
    firstSampleFieldGroup.bind(view.proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    firstSampleFieldGroup.bind(view.postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    firstSampleFieldGroup.bind(view.digestionFlexibleOptions, DIGESTION_PROPERTY);
    firstSampleFieldGroup.bind(view.instrumentOptions, INSTRUMENT_PROPERTY);
    firstSampleFieldGroup.bind(view.proteinIdentificationFlexibleOptions,
        PROTEIN_IDENTIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(view.commentsField, COMMENTS_PROPERTY);
    view.standardsTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.standardsTable.setContainerDataSource(standardsContainer);
    view.standardsTable.setEditable(true);
    view.contaminantsTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.contaminantsTable.setContainerDataSource(standardsContainer);
    view.contaminantsTable.setEditable(true);
  }

  private void addFieldListeners() {
    view.standardCountField.addValueChangeListener(e -> updateStandardsTable());
    view.contaminantCountField.addValueChangeListener(e -> updateContaminantsTable());
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.setTitle(resources.message("title"));
    view.headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    view.serviceLabel.setValue(resources.message(SERVICE_LABEL_ID));
    view.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL_ID));
    view.inactiveLabel.setValue(resources.message(INACTIVE_LABEL_ID));
    view.sampleCountPanel.setCaption(resources.message(SAMPLE_COUNT_PANEL_ID));
    view.sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    view.sampleNamesPanel.setCaption(resources.message(SAMPLE_NAMES_PANEL_ID));
    view.sampleNameField.setCaption(resources.message(SAMPLE_NAMES_PROPERTY, 1));
    view.fillSampleNamesButton.setCaption(resources.message(FILL_SAMPLE_NAMES_PROPERTY));
    view.experiencePanel.setCaption(resources.message(EXPERIENCE_PANEL_ID));
    view.experienceField.setCaption(resources.message(EXPERIENCE_PROPERTY));
    view.experienceGoalField.setCaption(resources.message(EXPERIENCE_GOAL_PROPERTY));
    view.taxonomyField.setCaption(resources.message(TAXONOMY_PROPERTY));
    view.proteinNameField.setCaption(resources.message(PROTEIN_NAME_PROPERTY));
    view.proteinWeightField.setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY));
    view.postTranslationModificationField
        .setCaption(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY));
    view.sampleVolumeField.setCaption(resources.message(SAMPLE_VOLUME_PROPERTY));
    view.sampleQuantityField.setCaption(resources.message(SAMPLE_QUANTITY_PROPERTY));
    view.standardsPanel.setCaption(resources.message(STANDARDS_PANEL_ID));
    view.standardCountField.setCaption(resources.message(STANDARD_COUNT_PROPERTY));
    view.standardsTable.setVisibleColumns(standardsColumns);
    for (Object column : standardsColumns) {
      view.standardsTable.setColumnHeader(column,
          resources.message(STANDARD_PROPERTY + "." + column));
    }
    view.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS_PROPERTY));
    view.contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL_ID));
    view.contaminantCountField.setCaption(resources.message(CONTAMINANT_COUNT_PROPERTY));
    view.contaminantsTable.setVisibleColumns(contaminantsColumns);
    for (Object column : contaminantsColumns) {
      view.contaminantsTable.setColumnHeader(column,
          resources.message(CONTAMINANT_PROPERTY + "." + column));
    }
    view.fillStandardsButton.setCaption(resources.message(FILL_CONTAMINANTS_PROPERTY));
    view.servicesPanel.setCaption(resources.message(SERVICES_PANEL_ID));
    view.digestionOptionsLayout.setCaption(resources.message(DIGESTION_PROPERTY));
    for (ProteolyticDigestion digestion : digestions) {
      view.digestionFlexibleOptions.addItem(digestion);
      view.digestionFlexibleOptions.setItemCaption(digestion, digestion.getLabel(view.getLocale()));
      view.createDigestionOptionLayout(digestion);
      TextField text = view.getDigestionOptionTextField(digestion);
      if (digestion == DIGESTED || digestion == ProteolyticDigestion.OTHER) {
        text.setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".value"));
      } else {
        view.getDigestionOptionTextLayout(digestion).setVisible(false);
      }
      if (digestion == ProteolyticDigestion.OTHER) {
        view.getDigestionOptionNoteLabel(digestion)
            .setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".note"));
      }
    }
    view.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    view.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    view.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    view.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    view.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.instrumentOptions.removeAllItems();
    for (MassDetectionInstrument instrument : instruments) {
      view.instrumentOptions.addItem(instrument);
      view.instrumentOptions.setItemCaption(instrument, instrument.getLabel(view.getLocale()));
    }
    view.proteinIdentificationOptionsLayout
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    for (ProteinIdentification proteinIdentification : proteinIdentifications) {
      view.proteinIdentificationFlexibleOptions.addItem(proteinIdentification);
      view.proteinIdentificationFlexibleOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(view.getLocale()));
      view.createProteinIdentificationOptionLayout(proteinIdentification);
      TextField text = view.getProteinIdentificationOptionTextField(proteinIdentification);
      if (proteinIdentification != ProteinIdentification.OTHER) {
        view.getProteinIdentificationOptionTextLayout(proteinIdentification).setVisible(false);
      } else {
        text.setCaption(resources.message(
            PROTEIN_IDENTIFICATION_PROPERTY + "." + proteinIdentification.name() + ".value"));
      }
    }
    view.commentsPanel.setCaption(resources.message(COMMENTS_PANEL_ID));
    view.submitButton.setCaption(resources.message(SUBMIT_ID));
  }

  private void setDefaults() {
    view.sampleCountField.setConvertedValue(1);
    view.standardCountField.setConvertedValue(0);
    view.standardsTableLayout.setVisible(false);
    view.contaminantCountField.setConvertedValue(0);
    view.contaminantsTableLayout.setVisible(false);
    view.digestionFlexibleOptions.setConvertedValue(TRYPSIN);
    view.instrumentOptions.setConvertedValue(VELOS);
    view.proteinIdentificationFlexibleOptions.setConvertedValue(REFSEQ);
  }

  private void updateStandardsTable() {
    Integer count = (Integer) view.standardCountField.getConvertedValue();
    if (count == null) {
      count = 0;
    }
    while (standardsContainer.size() > count) {
      standardsContainer.removeItem(standardsContainer.lastItemId());
    }
    while (standardsContainer.size() < count) {
      standardsContainer.addBean(new Standard());
    }
    view.standardsTableLayout.setVisible(count > 0);
  }

  private void updateContaminantsTable() {
    Integer count = (Integer) view.contaminantCountField.getConvertedValue();
    if (count == null) {
      count = 0;
    }
    while (contaminantsContainer.size() > count) {
      contaminantsContainer.removeItem(contaminantsContainer.lastItemId());
    }
    while (contaminantsContainer.size() < count) {
      contaminantsContainer.addBean(new Contaminant());
    }
    view.contaminantsTableLayout.setVisible(count > 0);
  }
}
