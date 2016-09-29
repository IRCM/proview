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

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.VELOS;
import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.utils.web.EmptyNullTableFieldFactory;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
  public static final String SAMPLE_TYPE_LABEL_ID = "sampleTypeLabel";
  public static final String INACTIVE_LABEL_ID = "inactive";
  public static final String SERVICE_LABEL_ID = "serviceLabel";
  public static final String SERVICE_PROPERTY = "service";
  public static final String SAMPLES_PROPERTY = submission.samples.getMetadata().getName();
  public static final String SAMPLES_PANEL_ID = "samplesPanel";
  public static final String SAMPLE_SUPPORT_TEXT =
      submissionSample.support.getMetadata().getName() + "Text";
  public static final String SAMPLE_SUPPORT_PROPERTY =
      submissionSample.support.getMetadata().getName();
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
  public static final String STANDARD_PROPERTY = submissionSample.standards.getMetadata().getName();
  public static final int STANDARDS_TABLE_LENGTH = 4;
  public static final String STANDARD_NAME_PROPERTY = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY_PROPERTY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENTS_PROPERTY = standard.comments.getMetadata().getName();
  public static final String FILL_STANDARDS_PROPERTY = "fillStandards";
  public static final String CONTAMINANTS_PANEL_ID = "contaminantsPanel";
  public static final String CONTAMINANT_COUNT_PROPERTY = "contaminantCount";
  public static final String CONTAMINANT_PROPERTY =
      submissionSample.contaminants.getMetadata().getName();
  public static final int CONTAMINANTS_TABLE_LENGTH = 4;
  public static final String CONTAMINANT_NAME_PROPERTY = contaminant.name.getMetadata().getName();
  public static final String CONTAMINANT_QUANTITY_PROPERTY =
      contaminant.quantity.getMetadata().getName();
  public static final String CONTAMINANT_COMMENTS_PROPERTY =
      contaminant.comments.getMetadata().getName();
  public static final String FILL_CONTAMINANTS_PROPERTY = "fillContaminants";
  public static final String GEL_PANEL_ID = "gelPanel";
  public static final String SEPARATION_PROPERTY = submission.separation.getMetadata().getName();
  public static final String THICKNESS_PROPERTY = submission.thickness.getMetadata().getName();
  public static final String COLORATION_PROPERTY = submission.coloration.getMetadata().getName();
  public static final String DEVELOPMENT_TIME_PROPERTY =
      submission.developmentTime.getMetadata().getName();
  public static final String DECOLORATION_PROPERTY =
      submission.decoloration.getMetadata().getName();
  public static final String WEIGHT_MARKER_QUANTITY_PROPERTY =
      submission.weightMarkerQuantity.getMetadata().getName();
  public static final String PROTEIN_QUANTITY_PROPERTY =
      submission.proteinQuantity.getMetadata().getName();
  public static final String GEL_IMAGE_PROPERTY = submission.gelImages.getMetadata().getName();
  public static final String SERVICES_PANEL_ID = "servicesPanel";
  public static final String DIGESTION_TEXT =
      submission.proteolyticDigestionMethod.getMetadata().getName() + "Label";
  public static final String DIGESTION_PROPERTY =
      submission.proteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT_PROPERTY = "enrichment";
  public static final String EXCLUSIONS_PROPERTY = "exclusions";
  public static final String SAMPLE_NUMBER_PROTEIN_PROPERTY =
      submission.sampleNumberProtein.getMetadata().getName();
  public static final String SOURCE_TEXT = submission.source.getMetadata().getName() + "Label";
  public static final String SOURCE_PROPERTY = submission.source.getMetadata().getName();
  public static final String INSTRUMENT_TEXT =
      submission.massDetectionInstrument.getMetadata().getName() + "Label";
  public static final String INSTRUMENT_PROPERTY =
      submission.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_TEXT =
      submission.proteinIdentification.getMetadata().getName() + "Label";
  public static final String PROTEIN_IDENTIFICATION_PROPERTY =
      submission.proteinIdentification.getMetadata().getName();
  public static final String COMMENTS_PANEL_ID = "commentsPanel";
  public static final String COMMENTS_PROPERTY = submission.comments.getMetadata().getName();
  public static final String SUBMIT_ID = "submit";
  public static final int NULL_ID = -1;
  public static final String EXAMPLE = "example";
  public static final String FILL_BUTTON_STYLE = "skip-row";
  private static final Object[] standardsColumns = new Object[] { STANDARD_NAME_PROPERTY,
      STANDARD_QUANTITY_PROPERTY, STANDARD_COMMENTS_PROPERTY };
  private static final Object[] contaminantsColumns = new Object[] { CONTAMINANT_NAME_PROPERTY,
      CONTAMINANT_QUANTITY_PROPERTY, CONTAMINANT_COMMENTS_PROPERTY };
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
    setOptions();
    setConverters();
    bindFields();
    addFieldListeners();
    setCaptions();
    setDefaults();
    updateVisibleAndEditable();
  }

  private void setIds() {
    view.headerLabel.setId(HEADER_LABEL_ID);
    view.sampleTypeLabel.setId(SAMPLE_TYPE_LABEL_ID);
    view.inactiveLabel.setId(INACTIVE_LABEL_ID);
    view.serviceLabel.setId(SERVICE_LABEL_ID);
    view.serviceOptions.setId(SERVICE_PROPERTY);
    view.samplesPanel.setId(SAMPLES_PANEL_ID);
    view.sampleSupportText.setId(SAMPLE_SUPPORT_TEXT);
    view.sampleSupportOptions.setId(SAMPLE_SUPPORT_PROPERTY);
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
    view.gelPanel.setId(GEL_PANEL_ID);
    view.separationField.setId(SEPARATION_PROPERTY);
    view.thicknessField.setId(THICKNESS_PROPERTY);
    view.colorationField.setId(COLORATION_PROPERTY);
    view.developmentTimeField.setId(DEVELOPMENT_TIME_PROPERTY);
    view.decolorationField.setId(DECOLORATION_PROPERTY);
    view.weightMarkerQuantityField.setId(WEIGHT_MARKER_QUANTITY_PROPERTY);
    view.proteinQuantityField.setId(PROTEIN_QUANTITY_PROPERTY);
    view.servicesPanel.setId(SERVICES_PANEL_ID);
    view.digestionText.setId(DIGESTION_TEXT);
    view.digestionOptionsLayout.setId(DIGESTION_PROPERTY);
    view.enrichmentLabel.setId(ENRICHEMENT_PROPERTY);
    view.exclusionsLabel.setId(EXCLUSIONS_PROPERTY);
    view.sampleNumberProteinField.setId(SAMPLE_NUMBER_PROTEIN_PROPERTY);
    view.sourceText.setId(SOURCE_TEXT);
    view.sourceOptions.setId(SOURCE_PROPERTY);
    view.instrumentText.setId(INSTRUMENT_TEXT);
    view.instrumentOptions.setId(INSTRUMENT_PROPERTY);
    view.proteinIdentificationText.setId(PROTEIN_IDENTIFICATION_TEXT);
    view.proteinIdentificationOptionsLayout.setId(PROTEIN_IDENTIFICATION_PROPERTY);
    view.commentsPanel.setId(COMMENTS_PANEL_ID);
    view.commentsField.setId(COMMENTS_PROPERTY);
    view.submitButton.setId(SUBMIT_ID);
  }

  private void setOptions() {
    Locale locale = view.getLocale();
    view.serviceOptions.removeAllItems();
    for (Service service : SubmissionForm.SERVICES) {
      view.serviceOptions.addItem(service);
      view.serviceOptions.setItemCaption(service, service.getLabel(locale));
    }
    view.sampleSupportOptions.removeAllItems();
    for (SampleSupport support : SubmissionForm.SUPPORT) {
      view.sampleSupportOptions.addItem(support);
      view.sampleSupportOptions.setItemCaption(support, support.getLabel(locale));
    }
    view.separationField.removeAllItems();
    view.separationField.setNullSelectionAllowed(false);
    view.separationField.setNewItemsAllowed(false);
    for (GelSeparation separation : SubmissionForm.SEPARATION) {
      view.separationField.addItem(separation);
      view.separationField.setItemCaption(separation, separation.getLabel(locale));
    }
    view.thicknessField.removeAllItems();
    view.thicknessField.setNullSelectionAllowed(false);
    view.thicknessField.setNewItemsAllowed(false);
    for (GelThickness thickness : SubmissionForm.THICKNESS) {
      view.thicknessField.addItem(thickness);
      view.thicknessField.setItemCaption(thickness, thickness.getLabel(locale));
    }
    view.colorationField.removeAllItems();
    view.colorationField.setNullSelectionItemId(NULL_ID);
    view.colorationField.setItemCaption(NULL_ID, GelColoration.getNullLabel(locale));
    view.colorationField.addItem(view.colorationField.getNullSelectionItemId());
    view.colorationField.setNullSelectionAllowed(true);
    view.colorationField.setNewItemsAllowed(false);
    for (GelColoration coloration : SubmissionForm.COLORATION) {
      view.colorationField.addItem(coloration);
      view.colorationField.setItemCaption(coloration, coloration.getLabel(locale));
    }
    view.sourceOptions.removeAllItems();
    for (MassDetectionInstrumentSource source : SubmissionForm.SOURCES) {
      view.sourceOptions.addItem(source);
      view.sourceOptions.setItemCaption(source, source.getLabel(locale));
    }
    view.instrumentOptions.removeAllItems();
    for (MassDetectionInstrument instrument : SubmissionForm.INSTRUMENTS) {
      view.instrumentOptions.addItem(instrument);
      view.instrumentOptions.setItemCaption(instrument, instrument.getLabel(locale));
    }
  }

  private void setConverters() {
    view.sampleCountField.setConverter(new StringToIntegerConverter());
    view.sampleVolumeField.setConverter(new StringToDoubleConverter());
    view.standardCountField.setConverter(new StringToIntegerConverter());
    view.contaminantCountField.setConverter(new StringToIntegerConverter());
    view.sampleNumberProteinField.setConverter(new StringToIntegerConverter());
  }

  @SuppressWarnings("serial")
  private void bindFields() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    submissionFieldGroup.bind(view.serviceOptions, SERVICE_PROPERTY);
    view.sampleNamesTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.sampleNamesTable.setContainerDataSource(samplesContainer);
    view.sampleNamesTable.setPageLength(SAMPLES_NAMES_TABLE_LENGTH);
    submissionFieldGroup.bind(view.experienceField, EXPERIENCE_PROPERTY);
    view.thicknessField.setRequired(true);
    view.thicknessField
        .setRequiredError(generalResources.message("required", view.separationField.getCaption()));
    submissionFieldGroup.bind(view.experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    submissionFieldGroup.bind(view.taxonomyField, TAXONOMY_PROPERTY);
    view.thicknessField.setRequired(true);
    view.thicknessField
        .setRequiredError(generalResources.message("required", view.separationField.getCaption()));
    submissionFieldGroup.bind(view.proteinNameField, PROTEIN_NAME_PROPERTY);
    submissionFieldGroup.bind(view.proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    submissionFieldGroup.bind(view.postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleSupportOptions, SAMPLE_SUPPORT_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.separationField, SEPARATION_PROPERTY);
    view.separationField.setRequired(true);
    view.separationField
        .setRequiredError(generalResources.message("required", view.separationField.getCaption()));
    submissionFieldGroup.bind(view.thicknessField, THICKNESS_PROPERTY);
    view.thicknessField.setRequired(true);
    view.thicknessField
        .setRequiredError(generalResources.message("required", view.separationField.getCaption()));
    submissionFieldGroup.bind(view.colorationField, COLORATION_PROPERTY);
    view.thicknessField.setRequired(true);
    view.thicknessField
        .setRequiredError(generalResources.message("required", view.separationField.getCaption()));
    submissionFieldGroup.bind(view.developmentTimeField, DEVELOPMENT_TIME_PROPERTY);
    submissionFieldGroup.bind(view.decolorationField, DECOLORATION_PROPERTY);
    submissionFieldGroup.bind(view.weightMarkerQuantityField, WEIGHT_MARKER_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.proteinQuantityField, PROTEIN_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.digestionFlexibleOptions, DIGESTION_PROPERTY);
    submissionFieldGroup.bind(view.instrumentOptions, INSTRUMENT_PROPERTY);
    submissionFieldGroup.bind(view.proteinIdentificationFlexibleOptions,
        PROTEIN_IDENTIFICATION_PROPERTY);
    submissionFieldGroup.bind(view.commentsField, COMMENTS_PROPERTY);
    view.standardsTable.setTableFieldFactory(new EmptyNullTableFieldFactory() {
      @Override
      public Field<?> createField(Container container, Object itemId, Object propertyId,
          Component uiContext) {
        Field<?> field = super.createField(container, itemId, propertyId, uiContext);
        if (propertyId.equals(STANDARD_QUANTITY_PROPERTY)) {
          ((TextField) field).setInputPrompt(resources
              .message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY + "." + EXAMPLE));
        }
        return field;
      }
    });
    view.standardsTable.setContainerDataSource(standardsContainer);
    view.standardsTable.setPageLength(STANDARDS_TABLE_LENGTH);
    view.contaminantsTable.setTableFieldFactory(new EmptyNullTableFieldFactory() {
      @Override
      public Field<?> createField(Container container, Object itemId, Object propertyId,
          Component uiContext) {
        Field<?> field = super.createField(container, itemId, propertyId, uiContext);
        if (propertyId.equals(CONTAMINANT_QUANTITY_PROPERTY)) {
          ((TextField) field).setInputPrompt(resources
              .message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY + "." + EXAMPLE));
        }
        return field;
      }
    });
    view.contaminantsTable.setContainerDataSource(contaminantsContainer);
    view.contaminantsTable.setPageLength(CONTAMINANTS_TABLE_LENGTH);
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> updateVisibleAndEditable());
    view.serviceOptions.addValueChangeListener(e -> updateVisibleAndEditable());
    view.sampleSupportOptions.addValueChangeListener(e -> updateVisibleAndEditable());
    view.sampleCountField
        .addValueChangeListener(e -> updateSampleCount(view.sampleCountField.getValue()));
    view.sampleCountField.addTextChangeListener(e -> updateSampleCount(e.getText()));
    view.fillSampleNamesButton.addClickListener(e -> fillSampleNames());
    view.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(view.standardCountField.getValue()));
    view.standardCountField.addTextChangeListener(e -> updateStandardsTable(e.getText()));
    view.fillStandardsButton.addClickListener(e -> fillStandards());
    view.contaminantCountField.addValueChangeListener(
        e -> updateContaminantsTable(view.contaminantCountField.getValue()));
    view.contaminantCountField.addTextChangeListener(e -> updateContaminantsTable(e.getText()));
    view.fillContaminantsButton.addClickListener(e -> fillContaminants());
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      view.digestionOptionTextField.get(digestion)
          .addValueChangeListener(e -> selectDigestion(digestion));
      view.digestionOptionTextField.get(digestion)
          .addTextChangeListener(e -> selectDigestion(digestion));
    }
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      view.proteinIdentificationOptionTextField.get(proteinIdentification)
          .addValueChangeListener(e -> selectProteinIdentification(proteinIdentification));
      view.proteinIdentificationOptionTextField.get(proteinIdentification)
          .addTextChangeListener(e -> selectProteinIdentification(proteinIdentification));
    }
  }

  private void setCaptions() {
    Locale locale = view.getLocale();
    MessageResource resources = view.getResources();
    view.headerLabel.setValue(resources.message(HEADER_LABEL_ID));
    view.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL_ID));
    view.inactiveLabel.setValue(resources.message(INACTIVE_LABEL_ID));
    view.servicePanel.setCaption(resources.message(SERVICE_PROPERTY));
    view.serviceOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL_ID));
    view.sampleSupportText.setCaption(resources.message(SAMPLE_SUPPORT_PROPERTY));
    view.sampleSupportOptions.setCaption(resources.message(SAMPLE_SUPPORT_PROPERTY));
    view.sampleSupportOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
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
    view.sampleQuantityField
        .setInputPrompt(resources.message(SAMPLE_QUANTITY_PROPERTY + "." + EXAMPLE));
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
    view.fillContaminantsButton.setCaption(resources.message(FILL_CONTAMINANTS_PROPERTY));
    view.gelPanel.setCaption(resources.message(GEL_PANEL_ID));
    view.separationField.setCaption(resources.message(SEPARATION_PROPERTY));
    view.separationField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.thicknessField.setCaption(resources.message(THICKNESS_PROPERTY));
    view.thicknessField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.colorationField.setCaption(resources.message(COLORATION_PROPERTY));
    view.colorationField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.developmentTimeField.setCaption(resources.message(DEVELOPMENT_TIME_PROPERTY));
    view.developmentTimeField
        .setInputPrompt(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE));
    view.decolorationField.setCaption(resources.message(DECOLORATION_PROPERTY));
    view.weightMarkerQuantityField.setCaption(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY));
    view.weightMarkerQuantityField
        .setInputPrompt(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE));
    view.proteinQuantityField.setCaption(resources.message(PROTEIN_QUANTITY_PROPERTY));
    view.proteinQuantityField
        .setInputPrompt(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE));
    view.servicesPanel.setCaption(resources.message(SERVICES_PANEL_ID));
    view.digestionText.setCaption(resources.message(DIGESTION_PROPERTY));
    view.digestionOptionsLayout.setCaption(resources.message(DIGESTION_PROPERTY));
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      view.digestionFlexibleOptions.setItemCaption(digestion, digestion.getLabel(locale));
      TextField text = view.digestionOptionTextField.get(digestion);
      if (digestion == DIGESTED || digestion == ProteolyticDigestion.OTHER) {
        text.setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".value"));
      } else {
        view.digestionOptionTextLayout.get(digestion).setVisible(false);
      }
      if (digestion == ProteolyticDigestion.OTHER) {
        view.digestionOptionNoteLabel.get(digestion)
            .setCaption(resources.message(DIGESTION_PROPERTY + "." + digestion.name() + ".note"));
      }
    }
    view.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    view.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    view.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    view.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    view.sampleNumberProteinField.setCaption(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY));
    view.sourceText.setCaption(resources.message(SOURCE_PROPERTY));
    view.sourceOptions.setCaption(resources.message(SOURCE_PROPERTY));
    view.sourceOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.instrumentText.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.proteinIdentificationText.setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    view.proteinIdentificationOptionsLayout
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      view.proteinIdentificationFlexibleOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(locale));
      TextField text = view.proteinIdentificationOptionTextField.get(proteinIdentification);
      if (proteinIdentification != ProteinIdentification.OTHER) {
        view.proteinIdentificationOptionTextLayout.get(proteinIdentification).setVisible(false);
      } else {
        text.setCaption(resources.message(
            PROTEIN_IDENTIFICATION_PROPERTY + "." + proteinIdentification.name() + ".value"));
      }
    }
    view.commentsPanel.setCaption(resources.message(COMMENTS_PANEL_ID));
    view.submitButton.setCaption(resources.message(SUBMIT_ID));
  }

  @SuppressWarnings("unchecked")
  private void setDefaults() {
    if (submissionFieldGroup.getItemDataSource() == null) {
      Submission submission = new Submission();
      submission.setService(LC_MS_MS);
      submission.setSamples(new ArrayList<>());
      SubmissionSample sample = new SubmissionSample();
      sample.setSupport(SOLUTION);
      submission.getSamples().add(sample);
      submission.setSeparation(ONE_DIMENSION);
      submission.setThickness(ONE);
      submission.setProteolyticDigestionMethod(TRYPSIN);
      submission.setSource(ESI);
      submission.setMassDetectionInstrument(VELOS);
      submission.setProteinIdentification(REFSEQ);
      submissionFieldGroup.setItemDataSource(new BeanItem<>(submission));
    }
    // Refresh fields.
    submissionFieldGroup.setItemDataSource(submissionFieldGroup.getItemDataSource());

    Locale locale = view.getLocale();
    Item item = submissionFieldGroup.getItemDataSource();
    Service service = (Service) item.getItemProperty(SERVICE_PROPERTY).getValue();
    view.serviceLabel
        .setValue(service == null ? Service.getNullLabel(locale) : service.getLabel(locale));
    List<SubmissionSample> samples =
        (List<SubmissionSample>) item.getItemProperty(SAMPLES_PROPERTY).getValue();
    if (samples == null) {
      samples = new ArrayList<>();
    }
    firstSampleFieldGroup.setItemDataSource(
        new BeanItem<>(samples.isEmpty() ? new SubmissionSample() : samples.get(0)));
    samplesContainer.removeAllItems();
    samplesContainer.addAll(samples);
    view.sampleCountField.setReadOnly(false);
    view.sampleCountField.setConvertedValue(samples.size());
    view.sampleNamesTable.sort(new Object[] { SAMPLE_NAMES_PROPERTY }, new boolean[] { true });
    Item sampleItem = firstSampleFieldGroup.getItemDataSource();
    SampleSupport support =
        (SampleSupport) sampleItem.getItemProperty(SAMPLE_SUPPORT_PROPERTY).getValue();
    view.sampleSupportText.setReadOnly(false);
    view.sampleSupportText
        .setValue(support == null ? SampleSupport.getNullLabel(locale) : support.getLabel(locale));
    List<Standard> standards =
        (List<Standard>) sampleItem.getItemProperty(STANDARD_PROPERTY).getValue();
    if (standards == null) {
      standards = new ArrayList<>();
    }
    standardsContainer.removeAllItems();
    standardsContainer.addAll(standards);
    view.standardCountField.setReadOnly(false);
    view.standardCountField.setConvertedValue(standards.size());
    List<Contaminant> contaminants =
        (List<Contaminant>) sampleItem.getItemProperty(CONTAMINANT_PROPERTY).getValue();
    if (contaminants == null) {
      contaminants = new ArrayList<>();
    }
    contaminantsContainer.removeAllItems();
    contaminantsContainer.addAll(contaminants);
    view.contaminantCountField.setReadOnly(false);
    view.contaminantCountField.setConvertedValue(contaminants.size());
    ProteolyticDigestion digestion =
        (ProteolyticDigestion) item.getItemProperty(DIGESTION_PROPERTY).getValue();
    view.digestionText.setReadOnly(false);
    view.digestionText.setValue(
        digestion == null ? ProteolyticDigestion.getNullLabel(locale) : digestion.getLabel(locale));
    MassDetectionInstrumentSource source =
        (MassDetectionInstrumentSource) item.getItemProperty(SOURCE_PROPERTY).getValue();
    view.sourceText.setReadOnly(false);
    view.sourceText.setValue(source == null ? MassDetectionInstrumentSource.getNullLabel(locale)
        : source.getLabel(locale));
    MassDetectionInstrument instrument =
        (MassDetectionInstrument) item.getItemProperty(INSTRUMENT_PROPERTY).getValue();
    view.instrumentText.setReadOnly(false);
    view.instrumentText.setValue(instrument == null ? MassDetectionInstrument.getNullLabel(locale)
        : instrument.getLabel(locale));
    ProteinIdentification proteinIdentification =
        (ProteinIdentification) item.getItemProperty(PROTEIN_IDENTIFICATION_PROPERTY).getValue();
    view.proteinIdentificationText.setReadOnly(false);
    view.proteinIdentificationText.setValue(proteinIdentification == null
        ? ProteinIdentification.getNullLabel(locale) : proteinIdentification.getLabel(locale));
    updateVisibleAndEditable();
  }

  private void updateVisibleAndEditable() {
    final boolean editable = editableProperty.getValue();
    final Service service = (Service) view.serviceOptions.getValue();
    final SampleSupport support = (SampleSupport) view.sampleSupportOptions.getValue();
    view.serviceLabel.setVisible(!editable);
    view.serviceOptions.setVisible(editable);
    view.sampleTypeLabel.setVisible(editable);
    view.inactiveLabel.setVisible(editable);
    view.sampleSupportText.setVisible(!editable);
    view.sampleSupportOptions.setVisible(editable);
    view.fillSampleNamesButton.setVisible(editable);
    view.sampleQuantityField.setVisible(support == SOLUTION || support == DRY);
    view.sampleVolumeField.setVisible(support == SOLUTION);
    view.standardsPanel.setVisible(editable && (support == SOLUTION || support == DRY));
    view.contaminantsPanel.setVisible(editable && (support == SOLUTION || support == DRY));
    view.gelPanel.setVisible(support == GEL);
    view.digestionText.setVisible(!editable && service == LC_MS_MS);
    view.digestionOptionsLayout.setVisible(editable && service == LC_MS_MS);
    view.enrichmentLabel.setVisible(editable && service == LC_MS_MS);
    view.exclusionsLabel.setVisible(editable && service == LC_MS_MS);
    view.sampleNumberProteinField.setVisible(service == INTACT_PROTEIN);
    view.sourceText.setVisible(!editable && service == INTACT_PROTEIN);
    view.sourceOptions.setVisible(editable && service == INTACT_PROTEIN);
    view.instrumentText.setVisible(!editable);
    view.instrumentOptions.setVisible(editable);
    view.proteinIdentificationText.setVisible(!editable && service == LC_MS_MS);
    view.proteinIdentificationOptionsLayout.setVisible(editable && service == LC_MS_MS);
    view.buttonsLayout.setVisible(editable);
    view.sampleSupportText.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleCountField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.experienceField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.experienceGoalField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.taxonomyField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.proteinNameField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.proteinWeightField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.postTranslationModificationField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleQuantityField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleVolumeField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sampleNumberProteinField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.digestionText.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.sourceText.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.instrumentText.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.proteinIdentificationText.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    view.commentsField.removeStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    if (!editable) {
      view.sampleSupportText.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleCountField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.experienceField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.experienceGoalField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.taxonomyField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.proteinNameField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.proteinWeightField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.postTranslationModificationField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleQuantityField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleVolumeField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sampleNumberProteinField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.digestionText.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.sourceText.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.instrumentText.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.proteinIdentificationText.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
      view.commentsField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
    }
    view.sampleSupportText.setReadOnly(!editable);
    view.sampleCountField.setReadOnly(!editable);
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
    view.sampleNumberProteinField.setReadOnly(!editable);
    view.digestionText.setReadOnly(!editable);
    view.sourceText.setReadOnly(!editable);
    view.instrumentText.setReadOnly(!editable);
    view.proteinIdentificationText.setReadOnly(!editable);
    view.commentsField.setReadOnly(!editable);
  }

  private void updateSampleCount(String countValue) {
    int count;
    try {
      count = Math.max(Integer.parseInt(countValue), 1);
    } catch (NumberFormatException e) {
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

  private void updateStandardsTable(String countValue) {
    int count;
    try {
      count = Math.max(Integer.parseInt(countValue), 0);
    } catch (NumberFormatException e) {
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

  private void updateContaminantsTable(String countValue) {
    int count;
    try {
      count = Math.max(Integer.parseInt(countValue), 0);
    } catch (NumberFormatException e) {
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
    for (Object itemId : standardsContainer.getItemIds().subList(1, standardsContainer.size())) {
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
    for (Object itemId : contaminantsContainer.getItemIds().subList(1,
        contaminantsContainer.size())) {
      name = incrementLastNumber(name);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_NAME_PROPERTY).setValue(name);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_QUANTITY_PROPERTY)
          .setValue(quantity);
      contaminantsContainer.getContainerProperty(itemId, CONTAMINANT_COMMENTS_PROPERTY)
          .setValue(comments);
    }
  }

  private String incrementLastNumber(String value) {
    Pattern pattern = Pattern.compile("(.*\\D)?(\\d+)(\\D*)");
    Matcher matcher = pattern.matcher(value);
    if (matcher.matches()) {
      try {
        StringBuilder builder = new StringBuilder();
        builder.append(matcher.group(1) != null ? matcher.group(1) : "");
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

  private void selectDigestion(ProteolyticDigestion digestion) {
    view.digestionFlexibleOptions.select(digestion);
  }

  private void selectProteinIdentification(ProteinIdentification proteinIdentification) {
    view.proteinIdentificationFlexibleOptions.select(proteinIdentification);
  }

  public Item getItemDataSource() {
    return submissionFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    submissionFieldGroup.setItemDataSource(item != null ? item : new BeanItem<>(new Submission()));
    setDefaults();
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
