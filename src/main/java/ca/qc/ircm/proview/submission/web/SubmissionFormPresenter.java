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
import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.submission.QSubmission.submission;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.utils.web.EmptyNullTableFieldFactory;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.vaadin.hene.flexibleoptiongroup.FlexibleOptionGroupItemComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Submission form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionFormPresenter {
  public static final String HEADER_LABEL_ID = "header";
  public static final String SERVICE_LABEL_ID = "service";
  public static final String SAMPLE_TYPE_LABEL_ID = "sampleType";
  public static final String INACTIVE_LABEL_ID = "inactive";
  public static final String SAMPLES_PROPERTY = submission.samples.getMetadata().getName();
  public static final String SAMPLE_COUNT_PANEL_ID = "sampleCountPanel";
  public static final String SAMPLE_COUNT_PROPERTY = "sampleCount";
  public static final int SAMPLES_NAMES_TABLE_LENGTH = 8;
  public static final String SAMPLE_NAMES_PROPERTY = submissionSample.name.getMetadata().getName();
  public static final String FILL_SAMPLE_NAMES_PROPERTY = "fillSampleNames";
  public static final String EXPERIENCE_PANEL_ID = "experiencePanel";
  public static final String EXPERIENCE_PROPERTY = submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL_PROPERTY = submission.goal.getMetadata().getName();
  public static final String SAMPLE_DETAILS_PANEL_ID = "sampleDetailsPanel";
  public static final String TAXONOMY_PROPERTY = submission.taxonomy.getMetadata().getName();
  public static final String PROTEIN_NAME_PROPERTY = submission.protein.getMetadata().getName();
  public static final String PROTEIN_WEIGHT_PROPERTY =
      submission.molecularWeight.getMetadata().getName();
  public static final String POST_TRANSLATION_MODIFICATION_PROPERTY =
      submission.postTranslationModification.getMetadata().getName();
  public static final String SAMPLE_VOLUME_PROPERTY =
      submissionSample.volume.getMetadata().getName();
  public static final String SAMPLE_QUANTITY_PROPERTY =
      submissionSample.quantity.getMetadata().getName();
  public static final String STANDARDS_PANEL_ID = "standardsPanel";
  public static final String STANDARD_COUNT_PROPERTY = "standardCount";
  public static final String STANDARD_PROPERTY = standard.getMetadata().getName();
  public static final int STANDARDS_TABLE_LENGTH = 4;
  public static final String STANDARD_NAME_PROPERTY = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY_PROPERTY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENTS_PROPERTY = standard.comments.getMetadata().getName();
  public static final String FILL_STANDARDS_PROPERTY = "fillStandards";
  public static final String CONTAMINANTS_PANEL_ID = "contaminantsPanel";
  public static final String CONTAMINANT_COUNT_PROPERTY = "contaminantCount";
  public static final String CONTAMINANT_PROPERTY = contaminant.getMetadata().getName();
  public static final int CONTAMINANTS_TABLE_LENGTH = 4;
  public static final String CONTAMINANT_NAME_PROPERTY = contaminant.name.getMetadata().getName();
  public static final String CONTAMINANT_QUANTITY_PROPERTY =
      contaminant.quantity.getMetadata().getName();
  public static final String CONTAMINANT_COMMENTS_PROPERTY =
      contaminant.comments.getMetadata().getName();
  public static final String FILL_CONTAMINANTS_PROPERTY = "fillContaminants";
  public static final String SERVICES_PANEL_ID = "servicesPanel";
  public static final String DIGESTION_PROPERTY =
      submission.proteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT_PROPERTY = "enrichment";
  public static final String EXCLUSIONS_PROPERTY = "exclusions";
  public static final String INSTRUMENT_PROPERTY =
      submission.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_PROPERTY =
      submission.proteinIdentification.getMetadata().getName();
  public static final String COMMENTS_PANEL_ID = "commentsPanel";
  public static final String COMMENTS_PROPERTY = submission.comments.getMetadata().getName();
  public static final String SUBMIT_ID = "submit";
  public static final String FILL_BUTTON_STYLE = "skip-row";
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
  private SubmissionForm view;
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Submission> submissionFieldGroup = new BeanFieldGroup<>(Submission.class);
  private BeanFieldGroup<SubmissionSample> firstSampleFieldGroup =
      new BeanFieldGroup<>(SubmissionSample.class);
  private BeanItemContainer<SubmissionSample> samplesContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private BeanItemContainer<Standard> standardsContainer = new BeanItemContainer<>(Standard.class);
  private BeanItemContainer<Contaminant> contaminantsContainer =
      new BeanItemContainer<>(Contaminant.class);
  private Map<Object, TextField> digestionOptionTextField = new HashMap<>();
  private Map<Object, Label> digestionOptionNoteLabel = new HashMap<>();
  private Map<Object, TextField> proteinIdentificationOptionTextField = new HashMap<>();
  @Inject
  private SubmissionService submissionService;

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionForm view) {
    this.view = view;
    setIds();
    setConverters();
    bindFields();
    addFieldListeners();
    updateEditable();
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
    view.sampleNamesTable.setId(SAMPLE_NAMES_PROPERTY);
    view.fillSampleNamesButton.setId(FILL_SAMPLE_NAMES_PROPERTY);
    view.fillSampleNamesButton.addStyleName(FILL_BUTTON_STYLE);
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
    view.fillStandardsButton.addStyleName(FILL_BUTTON_STYLE);
    view.contaminantsPanel.setId(CONTAMINANTS_PANEL_ID);
    view.contaminantCountField.setId(CONTAMINANT_COUNT_PROPERTY);
    view.contaminantsTable.setId(CONTAMINANT_PROPERTY);
    view.fillContaminantsButton.setId(FILL_CONTAMINANTS_PROPERTY);
    view.fillContaminantsButton.addStyleName(FILL_BUTTON_STYLE);
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
    view.sampleNamesTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.sampleNamesTable.setContainerDataSource(samplesContainer);
    view.sampleNamesTable.setPageLength(SAMPLES_NAMES_TABLE_LENGTH);
    submissionFieldGroup.bind(view.experienceField, EXPERIENCE_PROPERTY);
    submissionFieldGroup.bind(view.experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    submissionFieldGroup.bind(view.taxonomyField, TAXONOMY_PROPERTY);
    submissionFieldGroup.bind(view.proteinNameField, PROTEIN_NAME_PROPERTY);
    submissionFieldGroup.bind(view.proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    submissionFieldGroup.bind(view.postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.digestionFlexibleOptions, DIGESTION_PROPERTY);
    submissionFieldGroup.bind(view.instrumentOptions, INSTRUMENT_PROPERTY);
    submissionFieldGroup.bind(view.proteinIdentificationFlexibleOptions,
        PROTEIN_IDENTIFICATION_PROPERTY);
    submissionFieldGroup.bind(view.commentsField, COMMENTS_PROPERTY);
    view.standardsTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.standardsTable.setContainerDataSource(standardsContainer);
    view.standardsTable.setPageLength(STANDARDS_TABLE_LENGTH);
    view.contaminantsTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.contaminantsTable.setContainerDataSource(standardsContainer);
    view.standardsTable.setPageLength(CONTAMINANTS_TABLE_LENGTH);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateEditable());
    view.sampleCountField.addValueChangeListener(e -> updateSampleCount());
    view.fillSampleNamesButton.addClickListener(e -> fillSampleNames());
    view.standardCountField.addValueChangeListener(e -> updateStandardsTable());
    view.fillStandardsButton.addClickListener(e -> fillStandards());
    view.contaminantCountField.addValueChangeListener(e -> updateContaminantsTable());
    view.fillContaminantsButton.addClickListener(e -> fillContaminants());
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    view.serviceLabel.setVisible(editable);
    view.sampleTypeLabel.setVisible(editable);
    view.inactiveLabel.setVisible(editable);
    view.sampleCountPanel.setVisible(editable);
    view.fillSampleNamesButton.setVisible(editable);
    view.standardsPanel.setVisible(editable);
    view.contaminantsPanel.setVisible(editable);
    view.digestionLabel.setVisible(!editable);
    view.digestionOptionsLayout.setVisible(editable);
    view.enrichmentLabel.setVisible(editable);
    view.exclusionsLabel.setVisible(editable);
    view.instrumentLabel.setVisible(!editable);
    view.instrumentOptions.setVisible(editable);
    view.proteinIdentificationLabel.setVisible(!editable);
    view.proteinIdentificationOptionsLayout.setVisible(editable);
    view.buttonsLayout.setVisible(editable);
    view.experienceField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.experienceGoalField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.taxonomyField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.proteinNameField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.proteinWeightField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.postTranslationModificationField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleQuantityField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleVolumeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.commentsField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      view.experienceField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.experienceGoalField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.taxonomyField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.proteinNameField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.proteinWeightField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.postTranslationModificationField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleQuantityField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleVolumeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.commentsField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    view.sampleNamesTable.setEditable(editable);
    view.experienceField.setReadOnly(!editable);
    view.experienceGoalField.setReadOnly(!editable);
    view.taxonomyField.setReadOnly(!editable);
    view.proteinNameField.setReadOnly(!editable);
    view.proteinWeightField.setReadOnly(!editable);
    view.postTranslationModificationField.setReadOnly(!editable);
    view.standardsTable.setEditable(editable);
    view.contaminantsTable.setEditable(editable);
    view.sampleQuantityField.setReadOnly(!editable);
    view.sampleVolumeField.setReadOnly(!editable);
    view.commentsField.setReadOnly(!editable);
  }

  private void setCaptions() {
    MessageResource resources = view.getResources();
    view.headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    view.serviceLabel.setValue(resources.message(SERVICE_LABEL_ID));
    view.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL_ID));
    view.inactiveLabel.setValue(resources.message(INACTIVE_LABEL_ID));
    view.sampleCountPanel.setCaption(resources.message(SAMPLE_COUNT_PANEL_ID));
    view.sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    view.sampleNamesTable.setVisibleColumns(SAMPLE_NAMES_PROPERTY);
    view.sampleNamesTable.setColumnHeader(SAMPLE_NAMES_PROPERTY,
        resources.message(SAMPLE_NAMES_PROPERTY));
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
    view.digestionLabel.setCaption(resources.message(DIGESTION_PROPERTY));
    view.digestionOptionsLayout.setCaption(resources.message(DIGESTION_PROPERTY));
    for (ProteolyticDigestion digestion : digestions) {
      view.digestionFlexibleOptions.addItem(digestion);
      view.digestionFlexibleOptions.setItemCaption(digestion, digestion.getLabel(view.getLocale()));
      createDigestionOptionLayout(digestion);
      TextField text = getDigestionOptionTextField(digestion);
      if (digestion == DIGESTED || digestion == ProteolyticDigestion.OTHER) {
        text.setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".value"));
      } else {
        getDigestionOptionTextLayout(digestion).setVisible(false);
      }
      if (digestion == ProteolyticDigestion.OTHER) {
        getDigestionOptionNoteLabel(digestion)
            .setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".note"));
      }
    }
    view.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    view.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    view.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    view.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    view.instrumentLabel.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.instrumentOptions.removeAllItems();
    for (MassDetectionInstrument instrument : instruments) {
      view.instrumentOptions.addItem(instrument);
      view.instrumentOptions.setItemCaption(instrument, instrument.getLabel(view.getLocale()));
    }
    view.proteinIdentificationLabel.setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    view.proteinIdentificationOptionsLayout
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    for (ProteinIdentification proteinIdentification : proteinIdentifications) {
      view.proteinIdentificationFlexibleOptions.addItem(proteinIdentification);
      view.proteinIdentificationFlexibleOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(view.getLocale()));
      createProteinIdentificationOptionLayout(proteinIdentification);
      TextField text = getProteinIdentificationOptionTextField(proteinIdentification);
      if (proteinIdentification != ProteinIdentification.OTHER) {
        getProteinIdentificationOptionTextLayout(proteinIdentification).setVisible(false);
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

  private void updateSampleCount() {
    Integer count = (Integer) view.sampleCountField.getConvertedValue();
    if (count == null || count < 1) {
      count = 1;
    }
    while (count > samplesContainer.size()) {
      samplesContainer.addBean(new SubmissionSample());
    }
    while (count < samplesContainer.size()) {
      samplesContainer.removeItem(samplesContainer.getIdByIndex(samplesContainer.size() - 1));
    }
    view.sampleNamesTable.setPageLength(Math.min(count, SAMPLES_NAMES_TABLE_LENGTH));
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

  private AbstractLayout createDigestionOptionLayout(Object itemId) {
    VerticalLayout optionTextLayout = new VerticalLayout();
    view.digestionOptionsLayout.addComponent(optionTextLayout);
    HorizontalLayout optionLayout = new HorizontalLayout();
    optionTextLayout.addComponent(optionLayout);
    FlexibleOptionGroupItemComponent comp = view.digestionFlexibleOptions.getItemComponent(itemId);
    optionLayout.addComponent(comp);
    Label captionLabel = new Label();
    captionLabel.setStyleName("formcaption");
    captionLabel.setValue(comp.getCaption());
    optionLayout.addComponent(captionLabel);
    HorizontalLayout textAndNoteLayout = new HorizontalLayout();
    textAndNoteLayout.setMargin(new MarginInfo(false, false, false, true));
    textAndNoteLayout.setSpacing(true);
    optionTextLayout.addComponent(textAndNoteLayout);
    FormLayout textLayout = new FormLayout();
    textLayout.setMargin(false);
    textAndNoteLayout.addComponent(textLayout);
    TextField text = new TextField();
    textLayout.addComponent(text);
    digestionOptionTextField.put(itemId, text);
    FormLayout noteLayout = new FormLayout();
    noteLayout.setMargin(false);
    textAndNoteLayout.addComponent(noteLayout);
    Label note = new Label();
    note.setStyleName("formcaption");
    noteLayout.addComponent(note);
    digestionOptionNoteLabel.put(itemId, note);
    return optionTextLayout;
  }

  private TextField getDigestionOptionTextField(Object itemId) {
    return digestionOptionTextField.get(itemId);
  }

  private AbstractLayout getDigestionOptionTextLayout(Object itemId) {
    return digestionOptionTextField.get(itemId).findAncestor(HorizontalLayout.class);
  }

  private Label getDigestionOptionNoteLabel(Object itemId) {
    return digestionOptionNoteLabel.get(itemId);
  }

  private AbstractLayout createProteinIdentificationOptionLayout(Object itemId) {
    VerticalLayout optionTextLayout = new VerticalLayout();
    view.proteinIdentificationOptionsLayout.addComponent(optionTextLayout);
    HorizontalLayout optionLayout = new HorizontalLayout();
    optionTextLayout.addComponent(optionLayout);
    FlexibleOptionGroupItemComponent comp =
        view.proteinIdentificationFlexibleOptions.getItemComponent(itemId);
    optionLayout.addComponent(comp);
    Label captionLabel = new Label();
    captionLabel.setStyleName("formcaption");
    captionLabel.setValue(comp.getCaption());
    optionLayout.addComponent(captionLabel);
    FormLayout textLayout = new FormLayout();
    textLayout.setMargin(new MarginInfo(false, false, false, true));
    optionTextLayout.addComponent(textLayout);
    TextField text = new TextField();
    textLayout.addComponent(text);
    proteinIdentificationOptionTextField.put(itemId, text);
    return optionTextLayout;
  }

  private TextField getProteinIdentificationOptionTextField(Object itemId) {
    return proteinIdentificationOptionTextField.get(itemId);
  }

  private AbstractLayout getProteinIdentificationOptionTextLayout(Object itemId) {
    return proteinIdentificationOptionTextField.get(itemId).findAncestor(FormLayout.class);
  }

  @SuppressWarnings("unchecked")
  private void fillSampleNames() {
    Object first = samplesContainer.getIdByIndex(0);
    String name =
        (String) samplesContainer.getContainerProperty(first, SAMPLE_NAMES_PROPERTY).getValue();
    for (Object itemId : samplesContainer.getItemIds().subList(1, samplesContainer.size())) {
      name = incrementLastNumber(name);
      samplesContainer.getContainerProperty(itemId, SAMPLE_NAMES_PROPERTY).setValue(name);
    }
  }

  @SuppressWarnings("unchecked")
  private void fillStandards() {
    Object first = standardsContainer.getIdByIndex(0);
    String name =
        (String) standardsContainer.getContainerProperty(first, STANDARD_NAME_PROPERTY).getValue();
    String quantity = (String) standardsContainer
        .getContainerProperty(first, STANDARD_QUANTITY_PROPERTY).getValue();
    String comments = (String) standardsContainer
        .getContainerProperty(first, STANDARD_COMMENTS_PROPERTY).getValue();
    for (Object itemId : standardsContainer.getItemIds().subList(1, samplesContainer.size())) {
      name = incrementLastNumber(name);
      standardsContainer.getContainerProperty(itemId, STANDARD_NAME_PROPERTY).setValue(name);
      standardsContainer.getContainerProperty(itemId, STANDARD_QUANTITY_PROPERTY)
          .setValue(quantity);
      standardsContainer.getContainerProperty(itemId, STANDARD_COMMENTS_PROPERTY)
          .setValue(comments);
    }
  }

  @SuppressWarnings("unchecked")
  private void fillContaminants() {
    Object first = contaminantsContainer.getIdByIndex(0);
    String name = (String) contaminantsContainer
        .getContainerProperty(first, CONTAMINANT_NAME_PROPERTY).getValue();
    String quantity = (String) contaminantsContainer
        .getContainerProperty(first, CONTAMINANT_QUANTITY_PROPERTY).getValue();
    String comments = (String) contaminantsContainer
        .getContainerProperty(first, CONTAMINANT_COMMENTS_PROPERTY).getValue();
    for (Object itemId : contaminantsContainer.getItemIds().subList(1, samplesContainer.size())) {
      name = incrementLastNumber(name);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_NAME_PROPERTY).setValue(name);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_QUANTITY_PROPERTY)
          .setValue(quantity);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_COMMENTS_PROPERTY)
          .setValue(comments);
    }
  }

  private String incrementLastNumber(String value) {
    Pattern pattern = Pattern.compile("(.*\\D)(\\d+)(\\D*)");
    Matcher matcher = pattern.matcher(value);
    if (matcher.matches()) {
      try {
        StringBuilder builder = new StringBuilder();
        builder.append(matcher.group(1));
        int number = Integer.parseInt(matcher.group(2));
        int length = matcher.group(2).length();
        String newNumber = String.valueOf(number + 1);
        while (newNumber.length() < length) {
          newNumber = "0" + newNumber;
        }
        builder.append(newNumber);
        builder.append(matcher.group(3));
        return builder.toString();
      } catch (NumberFormatException e) {
        return value;
      }
    } else {
      return value;
    }
  }

  public Item getItemDataSource() {
    return submissionFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    submissionFieldGroup.setItemDataSource(item);
    if (item != null && item.getItemProperty(SAMPLES_PROPERTY) != null) {
      @SuppressWarnings("unchecked")
      List<SubmissionSample> samples =
          (List<SubmissionSample>) item.getItemProperty(SAMPLES_PROPERTY).getValue();
      if (!samples.isEmpty()) {
        firstSampleFieldGroup.setItemDataSource(new BeanItem<>(samples.get(0)));
      }
      samplesContainer.removeAllItems();
      samplesContainer.addAll(samples);
      view.sampleCountField.setConvertedValue(samples.size());
      updateEditable();
    }
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
