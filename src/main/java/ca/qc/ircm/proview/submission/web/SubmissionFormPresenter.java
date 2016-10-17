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
import static ca.qc.ircm.proview.submission.QGelImage.gelImage;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static ca.qc.ircm.proview.web.WebConstants.VALUE_TOO_SHORT;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.Structure;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelImage;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.utils.web.EmptyNullTableFieldFactory;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.utils.web.ValidatableTableFieldFactory;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;
import pl.exsio.plupload.handler.memory.ByteArrayChunkHandlerFactory;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Submission form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionFormPresenter {
  public static final String HEADER_LABEL = "header";
  public static final String SAMPLE_TYPE_LABEL = "sampleTypeLabel";
  public static final String INACTIVE_LABEL = "inactive";
  public static final String SERVICE_PROPERTY = "service";
  public static final String SAMPLES_PROPERTY = submission.samples.getMetadata().getName();
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLE_SUPPORT_PROPERTY =
      submissionSample.support.getMetadata().getName();
  public static final String SOLUTION_SOLVENT_PROPERTY =
      submission.solutionSolvent.getMetadata().getName();
  public static final String SAMPLE_COUNT_PROPERTY = "sampleCount";
  public static final int SAMPLES_NAMES_TABLE_LENGTH = 8;
  public static final String SAMPLE_NAME_PROPERTY = submissionSample.name.getMetadata().getName();
  public static final String FORMULA_PROPERTY = submission.formula.getMetadata().getName();
  public static final String STRUCTURE_PROPERTY = submission.structure.getMetadata().getName();
  public static final String STRUCTURE_UPLOADER =
      submission.structure.getMetadata().getName() + "Uploader";
  public static final String STRUCTURE_UPLOADER_PROGRESS =
      submission.structure.getMetadata().getName() + "UploaderProgress";
  public static final String MONOISOTOPIC_MASS_PROPERTY =
      submission.monoisotopicMass.getMetadata().getName();
  public static final String MAXIMUM_STRUCTURE_SIZE = "10MB";
  public static final String AVERAGE_MASS_PROPERTY = submission.averageMass.getMetadata().getName();
  public static final String TOXICITY_PROPERTY = submission.toxicity.getMetadata().getName();
  public static final String LIGHT_SENSITIVE_PROPERTY =
      submission.lightSensitive.getMetadata().getName();
  public static final String STORAGE_TEMPERATURE_PROPERTY =
      submission.storageTemperature.getMetadata().getName();
  public static final String SAMPLE_NAMES_PROPERTY =
      submissionSample.name.getMetadata().getName() + "s";
  public static final String FILL_SAMPLE_NAMES_PROPERTY = "fillSampleNames";
  public static final String EXPERIENCE_PANEL = "experiencePanel";
  public static final String EXPERIENCE_PROPERTY = submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL_PROPERTY = submission.goal.getMetadata().getName();
  public static final String SAMPLE_DETAILS_PANEL = "sampleDetailsPanel";
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
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String STANDARD_COUNT_PROPERTY = "standardCount";
  public static final String STANDARD_PROPERTY = submissionSample.standards.getMetadata().getName();
  public static final int STANDARDS_TABLE_LENGTH = 4;
  public static final String STANDARD_NAME_PROPERTY = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY_PROPERTY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENTS_PROPERTY = standard.comments.getMetadata().getName();
  public static final String FILL_STANDARDS_PROPERTY = "fillStandards";
  public static final String CONTAMINANTS_PANEL = "contaminantsPanel";
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
  public static final String GEL_PANEL = "gelPanel";
  public static final String SEPARATION_PROPERTY = submission.separation.getMetadata().getName();
  public static final String THICKNESS_PROPERTY = submission.thickness.getMetadata().getName();
  public static final String COLORATION_PROPERTY = submission.coloration.getMetadata().getName();
  public static final String OTHER_COLORATION_PROPERTY =
      submission.otherColoration.getMetadata().getName();
  public static final String DEVELOPMENT_TIME_PROPERTY =
      submission.developmentTime.getMetadata().getName();
  public static final String DECOLORATION_PROPERTY =
      submission.decoloration.getMetadata().getName();
  public static final String WEIGHT_MARKER_QUANTITY_PROPERTY =
      submission.weightMarkerQuantity.getMetadata().getName();
  public static final String PROTEIN_QUANTITY_PROPERTY =
      submission.proteinQuantity.getMetadata().getName();
  public static final String GEL_IMAGES_PROPERTY = submission.gelImages.getMetadata().getName();
  public static final String GEL_IMAGES_UPLOADER_PROGRESS =
      submission.gelImages.getMetadata().getName() + "UploaderProgress";
  public static final String GEL_IMAGES_TABLE = GEL_IMAGES_PROPERTY + "Table";
  public static final String MAXIMUM_GEL_IMAGES_SIZE = "10MB";
  public static final int MAXIMUM_GEL_IMAGES_COUNT = 4;
  public static final int GEL_IMAGES_TABLE_LENGTH = 3;
  public static final String GEL_IMAGE_FILENAME_PROPERTY =
      gelImage.filename.getMetadata().getName();
  public static final String REMOVE_GEL_IMAGE = "removeGelImage";
  public static final String SERVICES_PANEL = "servicesPanel";
  public static final String DIGESTION_PROPERTY =
      submission.proteolyticDigestionMethod.getMetadata().getName();
  public static final String USED_DIGESTION_PROPERTY =
      submission.usedProteolyticDigestionMethod.getMetadata().getName();
  public static final String OTHER_DIGESTION_PROPERTY =
      submission.otherProteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT_PROPERTY = "enrichment";
  public static final String EXCLUSIONS_PROPERTY = "exclusions";
  public static final String SAMPLE_NUMBER_PROTEIN_PROPERTY =
      submission.sampleNumberProtein.getMetadata().getName();
  public static final String SOURCE_PROPERTY = submission.source.getMetadata().getName();
  public static final String INSTRUMENT_PROPERTY =
      submission.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_PROPERTY =
      submission.proteinIdentification.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_LINK_PROPERTY =
      submission.proteinIdentificationLink.getMetadata().getName();
  public static final String QUANTIFICATION_PROPERTY =
      submission.quantification.getMetadata().getName();
  public static final String QUANTIFICATION_LABELS_PROPERTY =
      submission.quantificationLabels.getMetadata().getName();
  public static final String HIGH_RESOLUTION_PROPERTY =
      submission.highResolution.getMetadata().getName();
  public static final String SOLVENTS_PROPERTY = submission.solvents.getMetadata().getName();
  public static final String OTHER_SOLVENT_PROPERTY =
      submission.otherSolvent.getMetadata().getName();
  public static final String OTHER_SOLVENT_NOTE =
      submission.otherSolvent.getMetadata().getName() + ".note";
  public static final String COMMENTS_PANEL = "commentsPanel";
  public static final String COMMENTS_PROPERTY = submission.comments.getMetadata().getName();
  public static final String SUBMIT_ID = "submit";
  public static final int NULL_ID = -1;
  public static final String EXAMPLE = "example";
  public static final String FILL_BUTTON_STYLE = "skip-row";
  public static final String FLEXIBLE_OPTION_STYLE = "flexible-option";
  public static final String FORM_CAPTION_STYLE = "formcaption";
  public static final String CLICKABLE_STYLE = "clickable";
  public static final String HIDE_REQUIRED_STYLE = "hide-required";
  private static final Object[] standardsColumns = new Object[] { STANDARD_NAME_PROPERTY,
      STANDARD_QUANTITY_PROPERTY, STANDARD_COMMENTS_PROPERTY };
  private static final Object[] contaminantsColumns = new Object[] { CONTAMINANT_NAME_PROPERTY,
      CONTAMINANT_QUANTITY_PROPERTY, CONTAMINANT_COMMENTS_PROPERTY };
  private static final Object[] gelImagesColumns = new Object[] { GEL_IMAGE_FILENAME_PROPERTY };
  private static final Object[] editableGelImagesColumns =
      new Object[] { GEL_IMAGE_FILENAME_PROPERTY, REMOVE_GEL_IMAGE };
  private static final int MAX_SAMPLE_COUNT = 200;
  private static final double MAX_MASS = 1000000;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenter.class);
  private SubmissionForm view;
  private ObjectProperty<Boolean> editableProperty = new ObjectProperty<>(false);
  private BeanFieldGroup<Submission> submissionFieldGroup = new BeanFieldGroup<>(Submission.class);
  private BeanFieldGroup<SubmissionSample> firstSampleFieldGroup =
      new BeanFieldGroup<>(SubmissionSample.class);
  private BeanItemContainer<SubmissionSample> samplesContainer =
      new BeanItemContainer<>(SubmissionSample.class);
  private ValidatableTableFieldFactory sampleTableFieldFactory;
  private BeanItemContainer<Standard> standardsContainer = new BeanItemContainer<>(Standard.class);
  private ValidatableTableFieldFactory standardsTableFieldFactory;
  private BeanItemContainer<Contaminant> contaminantsContainer =
      new BeanItemContainer<>(Contaminant.class);
  private ValidatableTableFieldFactory contaminantsTableFieldFactory;
  private BeanItemContainer<GelImage> gelImagesContainer = new BeanItemContainer<>(GelImage.class);
  private GeneratedPropertyContainer gelImagesGeneratedContainer =
      new GeneratedPropertyContainer(gelImagesContainer);
  @Inject
  private SubmissionService submissionService;
  @Inject
  private SubmissionSampleService submissionSampleService;
  @Inject
  private VaadinUtils vaadinUtils;

  protected SubmissionFormPresenter() {
  }

  protected SubmissionFormPresenter(SubmissionService submissionService,
      SubmissionSampleService submissionSampleService, VaadinUtils vaadinUtils) {
    this.submissionService = submissionService;
    this.submissionSampleService = submissionSampleService;
    this.vaadinUtils = vaadinUtils;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionForm view) {
    this.view = view;
    initFlexibleOptions();
    setStyles();
    setCaptions();
    prepareFields();
    addFieldListeners();
    setDefaults();
    bindFields();
    updateVisible();
    updateEditable();
  }

  private void initFlexibleOptions() {
  }

  private void setStyles() {
    view.headerLabel.addStyleName(HEADER_LABEL);
    view.headerLabel.addStyleName("h1");
    view.sampleTypeLabel.addStyleName(SAMPLE_TYPE_LABEL);
    view.inactiveLabel.addStyleName(INACTIVE_LABEL);
    view.servicePanel.addStyleName(SERVICES_PANEL);
    view.servicePanel.addStyleName(REQUIRED);
    view.serviceOptions.addStyleName(SERVICE_PROPERTY);
    view.serviceOptions.addStyleName(HIDE_REQUIRED_STYLE);
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.sampleSupportOptions.addStyleName(SAMPLE_SUPPORT_PROPERTY);
    view.solutionSolventField.addStyleName(SOLUTION_SOLVENT_PROPERTY);
    view.sampleCountField.addStyleName(SAMPLE_COUNT_PROPERTY);
    view.sampleNameField.addStyleName(SAMPLE_NAME_PROPERTY);
    view.formulaField.addStyleName(FORMULA_PROPERTY);
    view.structureLayout.addStyleName(REQUIRED);
    view.structureButton.addStyleName(STRUCTURE_PROPERTY);
    view.structureUploader.addStyleName(STRUCTURE_UPLOADER);
    view.structureProgress.addStyleName(STRUCTURE_UPLOADER_PROGRESS);
    view.monoisotopicMassField.addStyleName(MONOISOTOPIC_MASS_PROPERTY);
    view.averageMassField.addStyleName(AVERAGE_MASS_PROPERTY);
    view.toxicityField.addStyleName(TOXICITY_PROPERTY);
    view.lightSensitiveField.addStyleName(LIGHT_SENSITIVE_PROPERTY);
    view.storageTemperatureOptions.addStyleName(STORAGE_TEMPERATURE_PROPERTY);
    view.sampleNamesTable.addStyleName(SAMPLE_NAMES_PROPERTY);
    view.fillSampleNamesButton.addStyleName(FILL_SAMPLE_NAMES_PROPERTY);
    view.fillSampleNamesButton.addStyleName(FILL_BUTTON_STYLE);
    view.experiencePanel.addStyleName(EXPERIENCE_PANEL);
    view.experienceField.addStyleName(EXPERIENCE_PROPERTY);
    view.experienceGoalField.addStyleName(EXPERIENCE_GOAL_PROPERTY);
    view.taxonomyField.addStyleName(TAXONOMY_PROPERTY);
    view.proteinNameField.addStyleName(PROTEIN_NAME_PROPERTY);
    view.proteinWeightField.addStyleName(PROTEIN_WEIGHT_PROPERTY);
    view.postTranslationModificationField.addStyleName(POST_TRANSLATION_MODIFICATION_PROPERTY);
    view.sampleVolumeField.addStyleName(SAMPLE_VOLUME_PROPERTY);
    view.sampleQuantityField.addStyleName(SAMPLE_QUANTITY_PROPERTY);
    view.standardsPanel.addStyleName(STANDARDS_PANEL);
    view.standardCountField.addStyleName(STANDARD_COUNT_PROPERTY);
    view.standardsTable.addStyleName(STANDARD_PROPERTY);
    view.fillStandardsButton.addStyleName(FILL_STANDARDS_PROPERTY);
    view.fillStandardsButton.addStyleName(FILL_BUTTON_STYLE);
    view.contaminantsPanel.addStyleName(CONTAMINANTS_PANEL);
    view.contaminantCountField.addStyleName(CONTAMINANT_COUNT_PROPERTY);
    view.contaminantsTable.addStyleName(CONTAMINANT_PROPERTY);
    view.fillContaminantsButton.addStyleName(FILL_CONTAMINANTS_PROPERTY);
    view.fillContaminantsButton.addStyleName(FILL_BUTTON_STYLE);
    view.gelPanel.addStyleName(GEL_PANEL);
    view.separationField.addStyleName(SEPARATION_PROPERTY);
    view.thicknessField.addStyleName(THICKNESS_PROPERTY);
    view.colorationField.addStyleName(COLORATION_PROPERTY);
    view.otherColorationField.addStyleName(OTHER_COLORATION_PROPERTY);
    view.developmentTimeField.addStyleName(DEVELOPMENT_TIME_PROPERTY);
    view.decolorationField.addStyleName(DECOLORATION_PROPERTY);
    view.weightMarkerQuantityField.addStyleName(WEIGHT_MARKER_QUANTITY_PROPERTY);
    view.proteinQuantityField.addStyleName(PROTEIN_QUANTITY_PROPERTY);
    view.gelImagesUploader.addStyleName(GEL_IMAGES_PROPERTY);
    view.gelImagesUploader.addStyleName(REQUIRED);
    view.gelImagesTable.addStyleName(GEL_IMAGES_TABLE);
    view.gelImageProgress.addStyleName(GEL_IMAGES_UPLOADER_PROGRESS);
    view.servicesPanel.addStyleName(SERVICES_PANEL);
    view.digestionOptions.addStyleName(DIGESTION_PROPERTY);
    view.usedProteolyticDigestionMethodField.addStyleName(USED_DIGESTION_PROPERTY);
    view.otherProteolyticDigestionMethodField.addStyleName(OTHER_DIGESTION_PROPERTY);
    view.enrichmentLabel.addStyleName(ENRICHEMENT_PROPERTY);
    view.exclusionsLabel.addStyleName(EXCLUSIONS_PROPERTY);
    view.sampleNumberProteinField.addStyleName(SAMPLE_NUMBER_PROTEIN_PROPERTY);
    view.sourceOptions.addStyleName(SOURCE_PROPERTY);
    view.instrumentOptions.addStyleName(INSTRUMENT_PROPERTY);
    view.proteinIdentificationOptions.addStyleName(PROTEIN_IDENTIFICATION_PROPERTY);
    view.proteinIdentificationLinkField.addStyleName(PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    view.quantificationOptions.addStyleName(QUANTIFICATION_PROPERTY);
    view.quantificationLabelsField.addStyleName(QUANTIFICATION_LABELS_PROPERTY);
    view.highResolutionOptions.addStyleName(HIGH_RESOLUTION_PROPERTY);
    view.solventsLayout.addStyleName(REQUIRED);
    view.acetonitrileSolventsField
        .addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name());
    view.acetonitrileSolventsField.addStyleName(FLEXIBLE_OPTION_STYLE);
    view.methanolSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name());
    view.methanolSolventsField.addStyleName(FLEXIBLE_OPTION_STYLE);
    view.chclSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name());
    view.chclSolventsField.addStyleName(FLEXIBLE_OPTION_STYLE);
    view.otherSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name());
    view.otherSolventsField.addStyleName(FLEXIBLE_OPTION_STYLE);
    view.otherSolventField.addStyleName(OTHER_SOLVENT_PROPERTY);
    view.otherSolventField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    view.otherSolventNoteLabel.addStyleName(OTHER_SOLVENT_NOTE);
    view.otherSolventNoteLabel.addStyleName(FORM_CAPTION_STYLE);
    view.commentsPanel.addStyleName(COMMENTS_PANEL);
    view.commentsField.addStyleName(COMMENTS_PROPERTY);
    view.submitButton.addStyleName(SUBMIT_ID);
  }

  private void setCaptions() {
    Locale locale = view.getLocale();
    MessageResource resources = view.getResources();
    view.headerLabel.setValue(resources.message(HEADER_LABEL));
    view.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL));
    view.inactiveLabel.setValue(resources.message(INACTIVE_LABEL));
    view.servicePanel.setCaption(resources.message(SERVICE_PROPERTY));
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    view.sampleSupportOptions.setCaption(resources.message(SAMPLE_SUPPORT_PROPERTY));
    view.solutionSolventField.setCaption(resources.message(SOLUTION_SOLVENT_PROPERTY));
    view.sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    view.sampleNameField.setCaption(resources.message(SAMPLE_NAME_PROPERTY));
    view.formulaField.setCaption(resources.message(FORMULA_PROPERTY));
    view.structureLayout.setCaption(resources.message(STRUCTURE_PROPERTY));
    view.structureUploader.setCaption(resources.message(STRUCTURE_UPLOADER));
    view.structureUploader.setIcon(FontAwesome.FILE_O);
    view.structureProgress.setIcon(FontAwesome.CLOUD_DOWNLOAD);
    view.monoisotopicMassField.setCaption(resources.message(MONOISOTOPIC_MASS_PROPERTY));
    view.averageMassField.setCaption(resources.message(AVERAGE_MASS_PROPERTY));
    view.toxicityField.setCaption(resources.message(TOXICITY_PROPERTY));
    view.lightSensitiveField.setCaption(resources.message(LIGHT_SENSITIVE_PROPERTY));
    view.storageTemperatureOptions.setCaption(resources.message(STORAGE_TEMPERATURE_PROPERTY));
    view.sampleNamesTable.setColumnHeader(SAMPLE_NAME_PROPERTY,
        resources.message(SAMPLE_NAMES_PROPERTY));
    view.fillSampleNamesButton.setCaption(resources.message(FILL_SAMPLE_NAMES_PROPERTY));
    view.experiencePanel.setCaption(resources.message(EXPERIENCE_PANEL));
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
    view.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    view.standardCountField.setCaption(resources.message(STANDARD_COUNT_PROPERTY));
    for (Object column : standardsColumns) {
      view.standardsTable.setColumnHeader(column,
          resources.message(STANDARD_PROPERTY + "." + column));
    }
    view.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS_PROPERTY));
    view.contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL));
    view.contaminantCountField.setCaption(resources.message(CONTAMINANT_COUNT_PROPERTY));
    for (Object column : contaminantsColumns) {
      view.contaminantsTable.setColumnHeader(column,
          resources.message(CONTAMINANT_PROPERTY + "." + column));
    }
    view.fillContaminantsButton.setCaption(resources.message(FILL_CONTAMINANTS_PROPERTY));
    view.gelPanel.setCaption(resources.message(GEL_PANEL));
    view.separationField.setCaption(resources.message(SEPARATION_PROPERTY));
    view.thicknessField.setCaption(resources.message(THICKNESS_PROPERTY));
    view.colorationField.setCaption(resources.message(COLORATION_PROPERTY));
    view.otherColorationField.setCaption(resources.message(OTHER_COLORATION_PROPERTY));
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
    view.gelImagesUploader.setCaption(resources.message(GEL_IMAGES_PROPERTY));
    view.gelImagesUploader.setIcon(FontAwesome.FILES_O);
    view.gelImageProgress.setIcon(FontAwesome.CLOUD_DOWNLOAD);
    for (Object column : editableGelImagesColumns) {
      view.gelImagesTable.setColumnHeader(column,
          resources.message(GEL_IMAGES_PROPERTY + "." + column));
    }
    view.servicesPanel.setCaption(resources.message(SERVICES_PANEL));
    view.digestionOptions.setCaption(resources.message(DIGESTION_PROPERTY));
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      view.digestionOptions.setItemCaption(digestion, digestion.getLabel(locale));
    }
    view.usedProteolyticDigestionMethodField.setCaption(resources.message(USED_DIGESTION_PROPERTY));
    view.otherProteolyticDigestionMethodField
        .setCaption(resources.message(OTHER_DIGESTION_PROPERTY));
    view.otherProteolyticDigestionMethodNote
        .setValue(resources.message(OTHER_DIGESTION_PROPERTY + ".note"));
    view.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    view.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    view.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    view.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    view.sampleNumberProteinField.setCaption(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY));
    view.sourceOptions.setCaption(resources.message(SOURCE_PROPERTY));
    view.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.proteinIdentificationOptions
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      view.proteinIdentificationOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(locale));
    }
    view.proteinIdentificationLinkField
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY));
    view.quantificationOptions.setCaption(resources.message(QUANTIFICATION_PROPERTY));
    view.quantificationLabelsField.setCaption(resources.message(QUANTIFICATION_LABELS_PROPERTY));
    view.quantificationLabelsField
        .setInputPrompt(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE));
    view.highResolutionOptions.setCaption(resources.message(HIGH_RESOLUTION_PROPERTY));
    view.solventsLayout.setCaption(resources.message(SOLVENTS_PROPERTY));
    view.acetonitrileSolventsField.setCaption(Solvent.ACETONITRILE.getLabel(locale));
    view.methanolSolventsField.setCaption(Solvent.METHANOL.getLabel(locale));
    view.chclSolventsField.setCaption(Solvent.CHCL3.getLabel(locale));
    view.chclSolventsField.setCaptionAsHtml(true);
    view.otherSolventsField.setCaption(Solvent.OTHER.getLabel(locale));
    view.otherSolventField.setCaption(resources.message(OTHER_SOLVENT_PROPERTY));
    view.otherSolventNoteLabel.setValue(resources.message(OTHER_SOLVENT_NOTE));
    view.commentsPanel.setCaption(resources.message(COMMENTS_PANEL));
    view.submitButton.setCaption(resources.message(SUBMIT_ID));
  }

  @SuppressWarnings("serial")
  private void prepareFields() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    view.serviceOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.serviceOptions.removeAllItems();
    for (Service service : SubmissionForm.SERVICES) {
      view.serviceOptions.addItem(service);
      view.serviceOptions.setItemCaption(service, service.getLabel(locale));
    }
    view.serviceOptions.setRequired(true);
    view.serviceOptions
        .setRequiredError(generalResources.message(REQUIRED, view.servicePanel.getCaption()));
    view.sampleSupportOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.sampleSupportOptions.removeAllItems();
    for (SampleSupport support : SubmissionForm.SUPPORT) {
      view.sampleSupportOptions.addItem(support);
      view.sampleSupportOptions.setItemCaption(support, support.getLabel(locale));
    }
    view.sampleSupportOptions.setRequired(true);
    view.sampleSupportOptions.setRequiredError(
        generalResources.message(REQUIRED, view.sampleSupportOptions.getCaption()));
    view.solutionSolventField.setRequired(true);
    view.solutionSolventField.setRequiredError(
        generalResources.message(REQUIRED, view.solutionSolventField.getCaption()));
    view.sampleCountField.setConverter(new StringToIntegerConverter());
    view.sampleCountField.setConversionError(
        generalResources.message(INVALID_INTEGER, view.sampleCountField.getCaption()));
    view.sampleCountField
        .addValidator(new IntegerRangeValidator(generalResources.message(OUT_OF_RANGE,
            view.sampleCountField.getCaption(), 1, MAX_SAMPLE_COUNT), 1, MAX_SAMPLE_COUNT));
    view.sampleCountField.setRequired(true);
    view.sampleCountField
        .setRequiredError(generalResources.message(REQUIRED, view.sampleCountField.getCaption()));
    view.sampleNameField.setRequired(true);
    view.sampleNameField
        .setRequiredError(generalResources.message(REQUIRED, view.sampleNameField.getCaption()));
    view.sampleNameField
        .addValidator(v -> validateSampleName((String) v, view.sampleNameField.getCaption()));
    view.formulaField.setRequired(true);
    view.formulaField
        .setRequiredError(generalResources.message(REQUIRED, view.formulaField.getCaption()));
    view.structureUploader.setMaxFileSize(MAXIMUM_STRUCTURE_SIZE);
    view.structureUploader.setChunkHandlerFactory(new ByteArrayChunkHandlerFactory());
    view.monoisotopicMassField.setConverter(new StringToDoubleConverter());
    view.monoisotopicMassField.setConversionError(
        generalResources.message(INVALID_NUMBER, view.monoisotopicMassField.getCaption()));
    view.monoisotopicMassField.setRequired(true);
    view.monoisotopicMassField.setRequiredError(
        generalResources.message(REQUIRED, view.monoisotopicMassField.getCaption()));
    /*
    view.monoisotopicMassField
        .addValidator(new DoubleRangeValidator(generalResources.message(OUT_OF_RANGE,
            view.monoisotopicMassField.getCaption(), 0.0, MAX_MASS), 0.0, MAX_MASS));
            */
    view.averageMassField.setConverter(new StringToDoubleConverter());
    view.averageMassField.setConversionError(
        generalResources.message(INVALID_NUMBER, view.averageMassField.getCaption()));
    view.averageMassField.addValidator(new DoubleRangeValidator(
        generalResources.message(OUT_OF_RANGE, view.averageMassField.getCaption(), 0.0, MAX_MASS),
        0.0, MAX_MASS));
    view.storageTemperatureOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.storageTemperatureOptions.removeAllItems();
    for (StorageTemperature storageTemperature : SubmissionForm.STORAGE_TEMPERATURES) {
      view.storageTemperatureOptions.addItem(storageTemperature);
      view.storageTemperatureOptions.setItemCaption(storageTemperature,
          storageTemperature.getLabel(locale));
    }
    view.storageTemperatureOptions.setRequired(true);
    view.storageTemperatureOptions.setRequiredError(
        generalResources.message(REQUIRED, view.storageTemperatureOptions.getCaption()));
    sampleTableFieldFactory = new ValidatableTableFieldFactory(new EmptyNullTableFieldFactory() {
      @Override
      public Field<?> createField(Container container, Object itemId, Object propertyId,
          Component uiContext) {
        TextField field = (TextField) super.createField(container, itemId, propertyId, uiContext);
        field.setCaption(resources.message(SAMPLE_NAMES_PROPERTY));
        field.setRequired(true);
        field.setRequiredError(generalResources.message(REQUIRED, field.getCaption()));
        field.addValidator(v -> validateSampleName((String) v, field.getCaption()));
        return field;
      }
    });
    view.sampleNamesTable.setTableFieldFactory(sampleTableFieldFactory);
    view.sampleNamesTable.setContainerDataSource(samplesContainer);
    view.sampleNamesTable.setPageLength(SAMPLES_NAMES_TABLE_LENGTH);
    view.sampleNamesTable.setVisibleColumns(SAMPLE_NAME_PROPERTY);
    view.experienceField.setRequired(true);
    view.experienceField
        .setRequiredError(generalResources.message(REQUIRED, view.experienceField.getCaption()));
    view.taxonomyField.setRequired(true);
    view.taxonomyField
        .setRequiredError(generalResources.message(REQUIRED, view.taxonomyField.getCaption()));
    view.proteinWeightField.setConverter(new StringToDoubleConverter());
    view.proteinWeightField.setConversionError(
        generalResources.message(INVALID_NUMBER, view.proteinWeightField.getCaption()));
    view.sampleVolumeField.setConverter(new StringToDoubleConverter());
    view.sampleVolumeField.setConversionError(
        generalResources.message(INVALID_NUMBER, view.sampleVolumeField.getCaption()));
    view.sampleVolumeField.setRequired(true);
    view.sampleVolumeField
        .setRequiredError(generalResources.message(REQUIRED, view.sampleVolumeField.getCaption()));
    view.sampleQuantityField.setRequired(true);
    view.sampleQuantityField.setRequiredError(
        generalResources.message(REQUIRED, view.sampleQuantityField.getCaption()));
    view.standardCountField.setConverter(new StringToIntegerConverter());
    view.standardCountField.setConversionError(
        generalResources.message(INVALID_INTEGER, view.standardCountField.getCaption()));
    standardsTableFieldFactory = new ValidatableTableFieldFactory(new EmptyNullTableFieldFactory() {
      @Override
      public Field<?> createField(Container container, Object itemId, Object propertyId,
          Component uiContext) {
        Field<?> field = super.createField(container, itemId, propertyId, uiContext);
        field.setCaption(resources.message(STANDARD_PROPERTY + "." + propertyId));
        if (propertyId.equals(STANDARD_QUANTITY_PROPERTY)) {
          ((TextField) field).setInputPrompt(resources
              .message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY + "." + EXAMPLE));
        }
        if (propertyId.equals(STANDARD_NAME_PROPERTY)
            || propertyId.equals(STANDARD_QUANTITY_PROPERTY)) {
          field.setRequired(true);
          field.setRequiredError(
              resources.message(STANDARD_PROPERTY + "." + propertyId + "." + REQUIRED));
        }
        return field;
      }
    });
    view.standardsTable.setTableFieldFactory(standardsTableFieldFactory);
    view.standardsTable.setContainerDataSource(standardsContainer);
    view.standardsTable.setPageLength(STANDARDS_TABLE_LENGTH);
    view.standardsTable.setVisibleColumns(standardsColumns);
    view.contaminantCountField.setConverter(new StringToIntegerConverter());
    view.contaminantCountField.setConversionError(
        generalResources.message(INVALID_INTEGER, view.contaminantCountField.getCaption()));
    contaminantsTableFieldFactory =
        new ValidatableTableFieldFactory(new EmptyNullTableFieldFactory() {
          @Override
          public Field<?> createField(Container container, Object itemId, Object propertyId,
              Component uiContext) {
            Field<?> field = super.createField(container, itemId, propertyId, uiContext);
            field.setCaption(resources.message(CONTAMINANT_PROPERTY + "." + propertyId));
            if (propertyId.equals(CONTAMINANT_QUANTITY_PROPERTY)) {
              ((TextField) field).setInputPrompt(resources.message(
                  CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY + "." + EXAMPLE));
            }
            if (propertyId.equals(CONTAMINANT_NAME_PROPERTY)
                || propertyId.equals(CONTAMINANT_QUANTITY_PROPERTY)) {
              field.setRequired(true);
              field.setRequiredError(
                  resources.message(CONTAMINANT_PROPERTY + "." + propertyId + "." + REQUIRED));
            }
            return field;
          }
        });
    view.contaminantsTable.setTableFieldFactory(contaminantsTableFieldFactory);
    view.contaminantsTable.setContainerDataSource(contaminantsContainer);
    view.contaminantsTable.setPageLength(CONTAMINANTS_TABLE_LENGTH);
    view.contaminantsTable.setVisibleColumns(contaminantsColumns);
    view.separationField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.separationField.removeAllItems();
    view.separationField.setNullSelectionAllowed(false);
    view.separationField.setNewItemsAllowed(false);
    for (GelSeparation separation : SubmissionForm.SEPARATION) {
      view.separationField.addItem(separation);
      view.separationField.setItemCaption(separation, separation.getLabel(locale));
    }
    view.separationField.setRequired(true);
    view.separationField
        .setRequiredError(generalResources.message(REQUIRED, view.separationField.getCaption()));
    view.thicknessField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.thicknessField.removeAllItems();
    view.thicknessField.setNullSelectionAllowed(false);
    view.thicknessField.setNewItemsAllowed(false);
    for (GelThickness thickness : SubmissionForm.THICKNESS) {
      view.thicknessField.addItem(thickness);
      view.thicknessField.setItemCaption(thickness, thickness.getLabel(locale));
    }
    view.thicknessField.setRequired(true);
    view.thicknessField
        .setRequiredError(generalResources.message(REQUIRED, view.thicknessField.getCaption()));
    view.colorationField.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
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
    view.otherColorationField.setRequired(true);
    view.otherColorationField.setRequiredError(
        generalResources.message(REQUIRED, view.otherColorationField.getCaption()));
    view.weightMarkerQuantityField.setConverter(new StringToDoubleConverter());
    view.weightMarkerQuantityField.setConversionError(
        generalResources.message(INVALID_NUMBER, view.weightMarkerQuantityField.getCaption()));
    view.gelImagesUploader.setMaxFileSize(MAXIMUM_GEL_IMAGES_SIZE);
    view.gelImagesUploader.setChunkHandlerFactory(new ByteArrayChunkHandlerFactory());
    gelImagesGeneratedContainer.addGeneratedProperty(GEL_IMAGE_FILENAME_PROPERTY,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            GelImage gelImage = (GelImage) itemId;
            Button button = new Button();
            button.setCaption(gelImage.getFilename());
            button.setIcon(FontAwesome.DOWNLOAD);
            StreamResource resource = new StreamResource(
                () -> new ByteArrayInputStream(gelImage.getContent()), gelImage.getFilename());
            FileDownloader fileDownloader = new FileDownloader(resource);
            fileDownloader.extend(button);
            return button;
          }

          @Override
          public Class<Button> getType() {
            return Button.class;
          }
        });
    gelImagesGeneratedContainer.addGeneratedProperty(REMOVE_GEL_IMAGE,
        new PropertyValueGenerator<Button>() {
          @Override
          public Button getValue(Item item, Object itemId, Object propertyId) {
            MessageResource resources = view.getResources();
            GelImage gelImage = (GelImage) itemId;
            Button button = new Button();
            button.setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE));
            button.addClickListener(e -> gelImagesContainer.removeItem(gelImage));
            return button;
          }

          @Override
          public Class<Button> getType() {
            return Button.class;
          }
        });
    view.gelImagesTable.setTableFieldFactory(new EmptyNullTableFieldFactory());
    view.gelImagesTable.setContainerDataSource(gelImagesGeneratedContainer);
    view.gelImagesTable.setPageLength(GEL_IMAGES_TABLE_LENGTH);
    view.digestionOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.digestionOptions.removeAllItems();
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      view.digestionOptions.addItem(digestion);
      view.digestionOptions.setItemCaption(digestion, digestion.getLabel(locale));
    }
    view.digestionOptions.setRequired(true);
    view.digestionOptions
        .setRequiredError(generalResources.message(REQUIRED, view.digestionOptions.getCaption()));
    view.usedProteolyticDigestionMethodField.setRequired(true);
    view.usedProteolyticDigestionMethodField.setRequiredError(
        generalResources.message(REQUIRED, view.usedProteolyticDigestionMethodField.getCaption()));
    view.otherProteolyticDigestionMethodField.setRequired(true);
    view.otherProteolyticDigestionMethodField.setRequiredError(
        generalResources.message(REQUIRED, view.otherProteolyticDigestionMethodField.getCaption()));
    view.sampleNumberProteinField.setConverter(new StringToIntegerConverter());
    view.sampleNumberProteinField.setConversionError(
        generalResources.message(INVALID_INTEGER, view.sampleNumberProteinField.getCaption()));
    view.sampleNumberProteinField.setRequired(true);
    view.sampleNumberProteinField.setRequiredError(
        generalResources.message(REQUIRED, view.sampleNumberProteinField.getCaption()));
    view.sourceOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.sourceOptions.removeAllItems();
    for (MassDetectionInstrumentSource source : SubmissionForm.SOURCES) {
      view.sourceOptions.addItem(source);
      view.sourceOptions.setItemCaption(source, source.getLabel(locale));
    }
    view.sourceOptions.setRequired(true);
    view.sourceOptions
        .setRequiredError(generalResources.message(REQUIRED, view.sourceOptions.getCaption()));
    view.instrumentOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.instrumentOptions.removeAllItems();
    for (MassDetectionInstrument instrument : SubmissionForm.INSTRUMENTS) {
      view.instrumentOptions.addItem(instrument);
      view.instrumentOptions.setItemCaption(instrument, instrument.getLabel(locale));
    }
    view.instrumentOptions.setRequired(true);
    view.instrumentOptions
        .setRequiredError(generalResources.message(REQUIRED, view.instrumentOptions.getCaption()));
    view.proteinIdentificationOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.proteinIdentificationOptions.removeAllItems();
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      view.proteinIdentificationOptions.addItem(proteinIdentification);
    }
    view.proteinIdentificationOptions.setRequired(true);
    view.proteinIdentificationOptions.setRequiredError(
        generalResources.message(REQUIRED, view.proteinIdentificationOptions.getCaption()));
    view.proteinIdentificationLinkField.setRequired(true);
    view.proteinIdentificationLinkField.setRequiredError(
        generalResources.message(REQUIRED, view.proteinIdentificationLinkField.getCaption()));
    view.quantificationOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.quantificationOptions.removeAllItems();
    view.quantificationOptions.setNullSelectionItemId(NULL_ID);
    view.quantificationOptions.setItemCaption(NULL_ID, Quantification.getNullLabel(locale));
    view.quantificationOptions.addItem(view.quantificationOptions.getNullSelectionItemId());
    view.quantificationOptions.setNullSelectionAllowed(true);
    for (Quantification quantification : SubmissionForm.QUANTIFICATION) {
      view.quantificationOptions.addItem(quantification);
      view.quantificationOptions.setItemCaption(quantification, quantification.getLabel(locale));
    }
    view.quantificationLabelsField.setRequiredError(
        generalResources.message(REQUIRED, view.quantificationLabelsField.getCaption()));
    view.highResolutionOptions.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
    view.highResolutionOptions.removeAllItems();
    for (boolean value : new boolean[] { false, true }) {
      view.highResolutionOptions.addItem(value);
      view.highResolutionOptions.setItemCaption(value,
          resources.message(HIGH_RESOLUTION_PROPERTY + "." + value));
    }
    view.highResolutionOptions.setRequired(true);
    view.highResolutionOptions.setRequiredError(
        generalResources.message(REQUIRED, view.highResolutionOptions.getCaption()));
    view.otherSolventField
        .setRequiredError(generalResources.message(REQUIRED, view.otherSolventField.getCaption()));
  }

  private void addFieldListeners() {
    editableProperty.addValueChangeListener(e -> {
      updateVisible();
      updateEditable();
    });
    view.serviceOptions.addValueChangeListener(e -> updateVisible());
    view.sampleSupportOptions.addValueChangeListener(e -> updateVisible());
    view.sampleCountField
        .addValueChangeListener(e -> updateSampleCount(view.sampleCountField.getValue()));
    view.sampleCountField.addTextChangeListener(e -> updateSampleCount(e.getText()));
    view.structureUploader.addFilesAddedListener(files -> view.structureUploader.start());
    view.structureUploader.addFileUploadedListener(file -> structureReceived(file));
    view.structureUploader.addUploadStartListener(() -> view.structureProgress.setVisible(true));
    view.structureUploader
        .addUploadCompleteListener(() -> view.structureProgress.setVisible(false));
    view.structureUploader.addUploadProgressListener(
        file -> view.structureProgress.setValue((float) file.getSize() / file.getOrigSize()));
    view.structureUploader.addErrorListener(error -> structureError(error));
    view.fillSampleNamesButton.addClickListener(e -> fillSampleNames());
    view.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(view.standardCountField.getValue()));
    view.standardCountField.addTextChangeListener(e -> updateStandardsTable(e.getText()));
    view.fillStandardsButton.addClickListener(e -> fillStandards());
    view.contaminantCountField.addValueChangeListener(
        e -> updateContaminantsTable(view.contaminantCountField.getValue()));
    view.contaminantCountField.addTextChangeListener(e -> updateContaminantsTable(e.getText()));
    view.fillContaminantsButton.addClickListener(e -> fillContaminants());
    view.colorationField.addValueChangeListener(e -> updateVisible());
    view.gelImagesUploader.addFilesAddedListener(files -> view.gelImagesUploader.start());
    view.gelImagesUploader.addFileUploadedListener(file -> gelImageReceived(file));
    view.gelImagesUploader.addUploadStartListener(() -> view.gelImageProgress.setVisible(true));
    view.gelImagesUploader.addUploadCompleteListener(() -> view.gelImageProgress.setVisible(false));
    view.gelImagesUploader.addUploadCompleteListener(() -> warnIfGelImageAtMaximum());
    view.gelImagesUploader.addUploadProgressListener(
        file -> view.gelImageProgress.setValue((float) file.getSize() / file.getOrigSize()));
    view.gelImagesUploader.addErrorListener(error -> gelImageError(error));
    view.digestionOptions.addValueChangeListener(e -> updateVisible());
    view.proteinIdentificationOptions.addValueChangeListener(e -> updateVisible());
    view.quantificationOptions.addValueChangeListener(e -> view.quantificationLabelsField
        .setRequired(view.quantificationOptions.getValue() == SILAC));
    view.otherSolventsField.addValueChangeListener(
        e -> view.otherSolventField.setRequired(view.otherSolventsField.getValue()));
    view.submitButton.addClickListener(e -> saveSubmission());
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
      submission.setStorageTemperature(StorageTemperature.MEDIUM);
      submission.setSeparation(ONE_DIMENSION);
      submission.setThickness(ONE);
      submission.setProteolyticDigestionMethod(TRYPSIN);
      submission.setSource(ESI);
      submission.setMassDetectionInstrument(VELOS);
      submission.setProteinIdentification(REFSEQ);
      submissionFieldGroup.setItemDataSource(new BeanItem<>(submission));
    }

    final Locale locale = view.getLocale();
    Item item = submissionFieldGroup.getItemDataSource();
    List<SubmissionSample> samples =
        (List<SubmissionSample>) item.getItemProperty(SAMPLES_PROPERTY).getValue();
    if (samples == null) {
      samples = new ArrayList<>();
    }
    SubmissionSample firstSample = samples.isEmpty() ? new SubmissionSample() : samples.get(0);
    if (samples.size() > 0) {
      samples.set(0, new SubmissionSample(firstSample.getId(), firstSample.getName()));
    } else {
      samples.add(new SubmissionSample(firstSample.getId(), firstSample.getName()));
    }
    firstSampleFieldGroup.setItemDataSource(new BeanItem<>(firstSample));
    samplesContainer.removeAllItems();
    samplesContainer.addAll(samples);
    view.sampleCountField.setReadOnly(false);
    view.sampleCountField.setConvertedValue(samples.size());
    view.sampleNamesTable.sort(new Object[] { SAMPLE_NAME_PROPERTY }, new boolean[] { true });
    Item sampleItem = firstSampleFieldGroup.getItemDataSource();
    Structure structure = (Structure) item.getItemProperty(STRUCTURE_PROPERTY).getValue();
    updateStructureButton(structure);
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
    List<GelImage> gelImages =
        (List<GelImage>) item.getItemProperty(GEL_IMAGES_PROPERTY).getValue();
    if (gelImages == null) {
      gelImages = new ArrayList<>();
    }
    gelImagesContainer.removeAllItems();
    gelImagesContainer.addAll(gelImages);
    MassDetectionInstrumentSource source =
        (MassDetectionInstrumentSource) item.getItemProperty(SOURCE_PROPERTY).getValue();
    if (source != null && !view.sourceOptions.containsId(source)) {
      view.sourceOptions.addItem(source);
      view.sourceOptions.setItemCaption(source, source.getLabel(locale));
      view.sourceOptions.setItemEnabled(source, false);
    }
    MassDetectionInstrument instrument =
        (MassDetectionInstrument) item.getItemProperty(INSTRUMENT_PROPERTY).getValue();
    if (instrument != null && !view.instrumentOptions.containsId(instrument)) {
      view.instrumentOptions.addItem(instrument);
      view.instrumentOptions.setItemCaption(instrument, instrument.getLabel(locale));
      view.instrumentOptions.setItemEnabled(instrument, false);
    }
    ProteinIdentification proteinIdentification =
        (ProteinIdentification) item.getItemProperty(PROTEIN_IDENTIFICATION_PROPERTY).getValue();
    if (proteinIdentification != null
        && !view.proteinIdentificationOptions.containsId(proteinIdentification)) {
      view.proteinIdentificationOptions.addItem(proteinIdentification);
      view.proteinIdentificationOptions.setItemCaption(proteinIdentification,
          proteinIdentification.getLabel(locale));
      view.proteinIdentificationOptions.setItemEnabled(proteinIdentification, false);
    }
    List<SampleSolvent> solvents =
        (List<SampleSolvent>) item.getItemProperty(SOLVENTS_PROPERTY).getValue();
    if (solvents == null) {
      solvents = new ArrayList<>();
    }
    view.solventsFields.values().forEach(c -> c.setValue(false));
    solvents.stream().filter(s -> !s.isDeleted()).map(s -> s.getSolvent())
        .forEach(s -> view.solventsFields.get(s).setValue(true));
  }

  private void updateVisible() {
    final boolean editable = editableProperty.getValue();
    final Service service = (Service) view.serviceOptions.getValue();
    final SampleSupport support = (SampleSupport) view.sampleSupportOptions.getValue();
    view.sampleTypeLabel.setVisible(editable);
    view.inactiveLabel.setVisible(editable);
    view.sampleSupportOptions.setItemEnabled(GEL, service != SMALL_MOLECULE);
    view.solutionSolventField
        .setVisible(service == SMALL_MOLECULE && support == SampleSupport.SOLUTION);
    view.sampleNameField.setVisible(service == SMALL_MOLECULE);
    view.formulaField.setVisible(service == SMALL_MOLECULE);
    view.structureLayout.setVisible(service == SMALL_MOLECULE);
    view.structureUploader.setVisible(editable);
    view.monoisotopicMassField.setVisible(service == SMALL_MOLECULE);
    view.averageMassField.setVisible(service == SMALL_MOLECULE);
    view.toxicityField.setVisible(service == SMALL_MOLECULE);
    view.lightSensitiveField.setVisible(service == SMALL_MOLECULE);
    view.storageTemperatureOptions.setVisible(service == SMALL_MOLECULE);
    view.sampleCountField.setVisible(service != SMALL_MOLECULE);
    view.sampleNamesLayout.setVisible(service != SMALL_MOLECULE);
    view.fillSampleNamesButton.setVisible(editable);
    view.experiencePanel.setVisible(service != SMALL_MOLECULE);
    view.experienceField.setVisible(service != SMALL_MOLECULE);
    view.experienceGoalField.setVisible(service != SMALL_MOLECULE);
    view.taxonomyField.setVisible(service != SMALL_MOLECULE);
    view.proteinNameField.setVisible(service != SMALL_MOLECULE);
    view.proteinWeightField.setVisible(service != SMALL_MOLECULE);
    view.postTranslationModificationField.setVisible(service != SMALL_MOLECULE);
    view.sampleQuantityField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.sampleVolumeField.setVisible(service != SMALL_MOLECULE && support == SOLUTION);
    view.standardsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.fillStandardsButton.setVisible(editable);
    view.contaminantsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.fillContaminantsButton.setVisible(editable);
    view.gelPanel.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.separationField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.thicknessField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.colorationField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.otherColorationField.setVisible(
        view.colorationField.isVisible() && view.colorationField.getValue() == GelColoration.OTHER);
    view.developmentTimeField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.decolorationField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.weightMarkerQuantityField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.proteinQuantityField.setVisible(service != SMALL_MOLECULE && support == GEL);
    view.gelImagesUploader.setVisible(service != SMALL_MOLECULE && support == GEL && editable);
    view.gelImagesTable.setVisibleColumns(editable ? editableGelImagesColumns : gelImagesColumns);
    view.digestionOptions.setVisible(service == LC_MS_MS);
    view.usedProteolyticDigestionMethodField.setVisible(
        view.digestionOptions.isVisible() && view.digestionOptions.getValue() == DIGESTED);
    view.otherProteolyticDigestionMethodField.setVisible(view.digestionOptions.isVisible()
        && view.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    view.otherProteolyticDigestionMethodNote.setVisible(view.digestionOptions.isVisible()
        && view.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    view.enrichmentLabel.setVisible(editable && service == LC_MS_MS);
    view.exclusionsLabel.setVisible(editable && service == LC_MS_MS);
    view.sampleNumberProteinField.setVisible(service == INTACT_PROTEIN);
    view.sourceOptions.setVisible(service == INTACT_PROTEIN);
    view.instrumentOptions.setVisible(service != SMALL_MOLECULE);
    view.proteinIdentificationOptions.setVisible(service == LC_MS_MS);
    view.proteinIdentificationLinkField.setVisible(view.proteinIdentificationOptions.isVisible()
        && view.proteinIdentificationOptions.getValue() == ProteinIdentification.OTHER);
    view.quantificationOptions.setVisible(service == LC_MS_MS);
    view.quantificationLabelsField.setVisible(service == LC_MS_MS);
    view.highResolutionOptions.setVisible(service == SMALL_MOLECULE);
    view.solventsLayout.setVisible(service == SMALL_MOLECULE);
    view.otherSolventField.setVisible(service == SMALL_MOLECULE);
    view.buttonsLayout.setVisible(editable);
  }

  private void updateEditable() {
    final boolean editable = editableProperty.getValue();
    view.serviceOptions.setReadOnly(!editable);
    view.sampleSupportOptions.setReadOnly(!editable);
    view.solutionSolventField.setReadOnly(!editable);
    view.sampleNameField.setReadOnly(!editable);
    view.formulaField.setReadOnly(!editable);
    view.monoisotopicMassField.setReadOnly(!editable);
    view.averageMassField.setReadOnly(!editable);
    view.toxicityField.setReadOnly(!editable);
    view.lightSensitiveField.setReadOnly(!editable);
    view.storageTemperatureOptions.setReadOnly(!editable);
    view.sampleCountField.setReadOnly(!editable);
    view.sampleNamesTable.setEditable(editable);
    view.fillSampleNamesButton.setVisible(editable);
    view.experienceField.setReadOnly(!editable);
    view.experienceGoalField.setReadOnly(!editable);
    view.taxonomyField.setReadOnly(!editable);
    view.proteinNameField.setReadOnly(!editable);
    view.proteinWeightField.setReadOnly(!editable);
    view.postTranslationModificationField.setReadOnly(!editable);
    view.sampleQuantityField.setReadOnly(!editable);
    view.sampleVolumeField.setReadOnly(!editable);
    view.standardsTable.setEditable(editable);
    view.contaminantsTable.setEditable(editable);
    view.separationField.setReadOnly(!editable);
    view.thicknessField.setReadOnly(!editable);
    view.colorationField.setReadOnly(!editable);
    view.otherColorationField.setReadOnly(!editable);
    view.developmentTimeField.setReadOnly(!editable);
    view.decolorationField.setReadOnly(!editable);
    view.weightMarkerQuantityField.setReadOnly(!editable);
    view.proteinQuantityField.setReadOnly(!editable);
    view.digestionOptions.setReadOnly(!editable);
    view.usedProteolyticDigestionMethodField.setReadOnly(!editable);
    view.otherProteolyticDigestionMethodField.setReadOnly(!editable);
    view.sampleNumberProteinField.setReadOnly(!editable);
    view.sourceOptions.setReadOnly(!editable);
    view.instrumentOptions.setReadOnly(!editable);
    view.proteinIdentificationOptions.setReadOnly(!editable);
    view.proteinIdentificationLinkField.setReadOnly(!editable);
    view.highResolutionOptions.setReadOnly(!editable);
    view.solventsFields.values().forEach(c -> c.setReadOnly(!editable));
    view.commentsField.setReadOnly(!editable);
  }

  private void bindFields() {
    submissionFieldGroup.bind(view.serviceOptions, SERVICE_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleSupportOptions, SAMPLE_SUPPORT_PROPERTY);
    submissionFieldGroup.bind(view.solutionSolventField, SOLUTION_SOLVENT_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleNameField, SAMPLE_NAME_PROPERTY);
    submissionFieldGroup.bind(view.formulaField, FORMULA_PROPERTY);
    submissionFieldGroup.bind(view.monoisotopicMassField, MONOISOTOPIC_MASS_PROPERTY);
    submissionFieldGroup.bind(view.averageMassField, AVERAGE_MASS_PROPERTY);
    submissionFieldGroup.bind(view.toxicityField, TOXICITY_PROPERTY);
    submissionFieldGroup.bind(view.lightSensitiveField, LIGHT_SENSITIVE_PROPERTY);
    submissionFieldGroup.bind(view.storageTemperatureOptions, STORAGE_TEMPERATURE_PROPERTY);
    submissionFieldGroup.bind(view.experienceField, EXPERIENCE_PROPERTY);
    submissionFieldGroup.bind(view.experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    submissionFieldGroup.bind(view.taxonomyField, TAXONOMY_PROPERTY);
    submissionFieldGroup.bind(view.proteinNameField, PROTEIN_NAME_PROPERTY);
    submissionFieldGroup.bind(view.proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    submissionFieldGroup.bind(view.postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    firstSampleFieldGroup.bind(view.sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.separationField, SEPARATION_PROPERTY);
    submissionFieldGroup.bind(view.thicknessField, THICKNESS_PROPERTY);
    submissionFieldGroup.bind(view.colorationField, COLORATION_PROPERTY);
    submissionFieldGroup.bind(view.otherColorationField, OTHER_COLORATION_PROPERTY);
    submissionFieldGroup.bind(view.developmentTimeField, DEVELOPMENT_TIME_PROPERTY);
    submissionFieldGroup.bind(view.decolorationField, DECOLORATION_PROPERTY);
    submissionFieldGroup.bind(view.weightMarkerQuantityField, WEIGHT_MARKER_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.proteinQuantityField, PROTEIN_QUANTITY_PROPERTY);
    submissionFieldGroup.bind(view.digestionOptions, DIGESTION_PROPERTY);
    submissionFieldGroup.bind(view.usedProteolyticDigestionMethodField, USED_DIGESTION_PROPERTY);
    submissionFieldGroup.bind(view.otherProteolyticDigestionMethodField, OTHER_DIGESTION_PROPERTY);
    submissionFieldGroup.bind(view.sampleNumberProteinField, SAMPLE_NUMBER_PROTEIN_PROPERTY);
    submissionFieldGroup.bind(view.sourceOptions, SOURCE_PROPERTY);
    submissionFieldGroup.bind(view.instrumentOptions, INSTRUMENT_PROPERTY);
    submissionFieldGroup.bind(view.proteinIdentificationLinkField,
        PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    submissionFieldGroup.bind(view.proteinIdentificationOptions, PROTEIN_IDENTIFICATION_PROPERTY);
    submissionFieldGroup.bind(view.otherSolventField, OTHER_SOLVENT_PROPERTY);
    submissionFieldGroup.bind(view.quantificationOptions, QUANTIFICATION_PROPERTY);
    submissionFieldGroup.bind(view.quantificationLabelsField, QUANTIFICATION_LABELS_PROPERTY);
    submissionFieldGroup.bind(view.highResolutionOptions, HIGH_RESOLUTION_PROPERTY);
    submissionFieldGroup.bind(view.commentsField, COMMENTS_PROPERTY);
  }

  private void bindVisibleFields() {
    bindVisible(submissionFieldGroup, view.solutionSolventField, SOLUTION_SOLVENT_PROPERTY);
    bindVisible(firstSampleFieldGroup, view.sampleNameField, SAMPLE_NAME_PROPERTY);
    bindVisible(submissionFieldGroup, view.formulaField, FORMULA_PROPERTY);
    bindVisible(submissionFieldGroup, view.monoisotopicMassField, MONOISOTOPIC_MASS_PROPERTY);
    bindVisible(submissionFieldGroup, view.averageMassField, AVERAGE_MASS_PROPERTY);
    bindVisible(submissionFieldGroup, view.toxicityField, TOXICITY_PROPERTY);
    bindVisible(submissionFieldGroup, view.lightSensitiveField, LIGHT_SENSITIVE_PROPERTY);
    bindVisible(submissionFieldGroup, view.storageTemperatureOptions, STORAGE_TEMPERATURE_PROPERTY);
    bindVisible(submissionFieldGroup, view.experienceField, EXPERIENCE_PROPERTY);
    bindVisible(submissionFieldGroup, view.experienceGoalField, EXPERIENCE_GOAL_PROPERTY);
    bindVisible(submissionFieldGroup, view.taxonomyField, TAXONOMY_PROPERTY);
    bindVisible(submissionFieldGroup, view.proteinNameField, PROTEIN_NAME_PROPERTY);
    bindVisible(submissionFieldGroup, view.proteinWeightField, PROTEIN_WEIGHT_PROPERTY);
    bindVisible(submissionFieldGroup, view.postTranslationModificationField,
        POST_TRANSLATION_MODIFICATION_PROPERTY);
    bindVisible(firstSampleFieldGroup, view.sampleQuantityField, SAMPLE_QUANTITY_PROPERTY);
    bindVisible(firstSampleFieldGroup, view.sampleVolumeField, SAMPLE_VOLUME_PROPERTY);
    bindVisible(submissionFieldGroup, view.separationField, SEPARATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.thicknessField, THICKNESS_PROPERTY);
    bindVisible(submissionFieldGroup, view.colorationField, COLORATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.otherColorationField, OTHER_COLORATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.developmentTimeField, DEVELOPMENT_TIME_PROPERTY);
    bindVisible(submissionFieldGroup, view.decolorationField, DECOLORATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.weightMarkerQuantityField,
        WEIGHT_MARKER_QUANTITY_PROPERTY);
    bindVisible(submissionFieldGroup, view.proteinQuantityField, PROTEIN_QUANTITY_PROPERTY);
    bindVisible(submissionFieldGroup, view.digestionOptions, DIGESTION_PROPERTY);
    bindVisible(submissionFieldGroup, view.usedProteolyticDigestionMethodField,
        USED_DIGESTION_PROPERTY);
    bindVisible(submissionFieldGroup, view.otherProteolyticDigestionMethodField,
        OTHER_DIGESTION_PROPERTY);
    bindVisible(submissionFieldGroup, view.sampleNumberProteinField,
        SAMPLE_NUMBER_PROTEIN_PROPERTY);
    bindVisible(submissionFieldGroup, view.sourceOptions, SOURCE_PROPERTY);
    bindVisible(submissionFieldGroup, view.instrumentOptions, INSTRUMENT_PROPERTY);
    bindVisible(submissionFieldGroup, view.proteinIdentificationLinkField,
        PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    bindVisible(submissionFieldGroup, view.proteinIdentificationOptions,
        PROTEIN_IDENTIFICATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.quantificationOptions, QUANTIFICATION_PROPERTY);
    bindVisible(submissionFieldGroup, view.quantificationLabelsField,
        QUANTIFICATION_LABELS_PROPERTY);
    bindVisible(submissionFieldGroup, view.highResolutionOptions, HIGH_RESOLUTION_PROPERTY);
    bindVisible(submissionFieldGroup, view.otherSolventField, OTHER_SOLVENT_PROPERTY);
  }

  private void bindVisible(FieldGroup fieldGroup, Field<?> field, Object propertyId) {
    if (field.isVisible()) {
      if (fieldGroup.getField(propertyId) == null) {
        fieldGroup.bind(field, propertyId);
      }
    } else {
      if (fieldGroup.getField(propertyId) != null) {
        fieldGroup.unbind(field);
      }
    }
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
        (String) samplesContainer.getContainerProperty(first, SAMPLE_NAME_PROPERTY).getValue();
    for (Object itemId : samplesContainer.getItemIds().subList(1, samplesContainer.size())) {
      name = incrementLastNumber(name);
      samplesContainer.getContainerProperty(itemId, SAMPLE_NAME_PROPERTY).setValue(name);
    }
    view.sampleNamesTable.refreshRowCache();
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
    view.standardsTable.refreshRowCache();
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
    view.contaminantsTable.refreshRowCache();
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

  @SuppressWarnings("unchecked")
  private void structureReceived(PluploadFile file) {
    Property<Structure> structureProperty =
        submissionFieldGroup.getItemDataSource().getItemProperty(STRUCTURE_PROPERTY);
    if (structureProperty.getValue() == null) {
      structureProperty.setValue(new Structure());
    }
    Structure structure = structureProperty.getValue();
    structure.setFilename(file.getName());
    structure.setContent((byte[]) file.getUploadedFile());
    view.structureButton.setVisible(true);
    updateStructureButton(structure);
  }

  private void updateStructureButton(Structure structure) {
    view.structureButton.getExtensions().stream().collect(Collectors.toList())
        .forEach(e -> e.remove());
    if (structure != null) {
      view.structureButton.setCaption(structure.getFilename());
      StreamResource resource = new StreamResource(
          () -> new ByteArrayInputStream(structure.getContent()), structure.getFilename());
      FileDownloader fileDownloader = new FileDownloader(resource);
      fileDownloader.extend(view.structureButton);
    } else {
      view.structureButton.setCaption("");
    }
  }

  private void structureError(PluploadError error) {
    logger.debug("structureUploader error {} of type {}", error.getMessage(), error.getType());
    MessageResource resources = view.getResources();
    if (error.getType() == PluploadError.Type.FILE_SIZE_ERROR) {
      view.showError(resources.message(STRUCTURE_PROPERTY + ".overMaximumSize",
          error.getFile().getName(), MAXIMUM_STRUCTURE_SIZE));
    } else {
      view.showError(resources.message(STRUCTURE_PROPERTY + ".error", error.getFile().getName()));
    }
  }

  private void gelImageReceived(PluploadFile file) {
    if (gelImagesContainer.size() < MAXIMUM_GEL_IMAGES_COUNT) {
      GelImage gelImage = new GelImage();
      gelImage.setFilename(file.getName());
      gelImage.setContent((byte[]) file.getUploadedFile());
      gelImagesContainer.addBean(gelImage);
    }
  }

  private void gelImageError(PluploadError error) {
    logger.debug("gelImagesUploader error {} of type {}", error.getMessage(), error.getType());
    MessageResource resources = view.getResources();
    if (error.getType() == PluploadError.Type.FILE_SIZE_ERROR) {
      view.showError(resources.message(GEL_IMAGES_PROPERTY + ".overMaximumSize",
          error.getFile().getName(), MAXIMUM_GEL_IMAGES_SIZE));
    } else {
      view.showError(resources.message(GEL_IMAGES_PROPERTY + ".error", error.getFile().getName()));
    }
  }

  private void warnIfGelImageAtMaximum() {
    if (gelImagesContainer.size() >= MAXIMUM_GEL_IMAGES_COUNT) {
      MessageResource resources = view.getResources();
      view.showWarning(
          resources.message(GEL_IMAGES_PROPERTY + ".overMaximumCount", MAXIMUM_GEL_IMAGES_COUNT));
    }
  }

  private void validateSampleName(String name, String caption) {
    if (name == null) {
      return;
    }
    MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    if (name.length() < 3) {
      throw new InvalidValueException(generalResources.message(VALUE_TOO_SHORT, caption, 3));
    }
    if (!Pattern.matches("\\w*", name)) {
      throw new InvalidValueException(generalResources.message(ONLY_WORDS, caption));
    }
    if (submissionSampleService.exists(name)) {
      throw new InvalidValueException(generalResources.message(ALREADY_EXISTS, caption, name));
    }
  }

  private boolean validate() {
    logger.trace("Validate user");
    boolean valid = true;
    try {
      submissionFieldGroup.commit();
      firstSampleFieldGroup.commit();
      Submission submission = submissionFieldGroup.getItemDataSource().getBean();
      SubmissionSample sample = firstSampleFieldGroup.getItemDataSource().getBean();
      if (submission.getService() == LC_MS_MS || submission.getService() == INTACT_PROTEIN) {
        view.sampleCountField.validate();
        sampleTableFieldFactory.commit();
        validateSampleNames();
        if (sample.getSupport() == DRY || sample.getSupport() == SOLUTION) {
          view.standardCountField.validate();
          standardsTableFieldFactory.commit();
          view.contaminantCountField.validate();
          contaminantsTableFieldFactory.commit();
        } else if (sample.getSupport() == GEL) {
          validateGelImages(submission);
        }
      }
      if (submission.getService() == Service.SMALL_MOLECULE) {
        validateStructure(submission);
        validateSolvents();
      }
    } catch (InvalidValueException e) {
      logger.debug("Validation value failed with message {}", e.getMessage(), e);
      view.showError(e.getMessage());
      valid = false;
    } catch (CommitException e) {
      String message = vaadinUtils.getFieldMessage(e, view.getLocale());
      logger.debug("Validation commit failed with message {}", message, e);
      view.showError(message);
      valid = false;
    }
    return valid;
  }

  private void validateSampleNames() {
    MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    MessageResource resources = view.getResources();
    String nameCaption = resources.message(SAMPLE_NAME_PROPERTY);
    Set<String> names = new HashSet<>();
    for (SubmissionSample sample : samplesContainer.getItemIds()) {
      if (!names.add(sample.getName())) {
        throw new InvalidValueException(
            generalResources.message(ALREADY_EXISTS, nameCaption, sample.getName()));
      }
    }
  }

  private void validateStructure(Submission submission) {
    MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    if (submission.getStructure() == null || submission.getStructure().getFilename() == null) {
      throw new InvalidValueException(
          generalResources.message(REQUIRED, view.structureLayout.getCaption()));
    }
  }

  private void validateGelImages(Submission submission) {
    MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    if (gelImagesContainer.size() == 0) {
      throw new InvalidValueException(
          generalResources.message(REQUIRED, view.gelImagesUploader.getCaption()));
    }
  }

  private void validateSolvents() {
    MessageResource generalResources =
        new MessageResource(WebConstants.GENERAL_MESSAGES, view.getLocale());
    if (!view.acetonitrileSolventsField.getValue() && !view.methanolSolventsField.getValue()
        && !view.chclSolventsField.getValue() && !view.otherSolventsField.getValue()) {
      throw new InvalidValueException(
          generalResources.message(REQUIRED, view.solventsLayout.getCaption()));
    }
  }

  private void saveSubmission() {
    try {
      bindVisibleFields();
      if (validate()) {
        Submission submission = submissionFieldGroup.getItemDataSource().getBean();
        SubmissionSample firstSample = firstSampleFieldGroup.getItemDataSource().getBean();
        if (submission.getService() == LC_MS_MS) {
          copySamplesFromTableToSubmission(submission);
        } else {
          submission.setProteolyticDigestionMethod(null);
          submission.setProteinIdentification(null);
        }
        if (submission.getService() == SMALL_MOLECULE) {
          submission.setMassDetectionInstrument(null);
          submission.setExperience(firstSample.getName());
          submission.setSamples(Arrays.asList(firstSample));
          submission.setSolvents(new ArrayList<>());
          if (view.acetonitrileSolventsField.getValue()) {
            submission.getSolvents().add(new SampleSolvent(ACETONITRILE));
          }
          if (view.methanolSolventsField.getValue()) {
            submission.getSolvents().add(new SampleSolvent(METHANOL));
          }
          if (view.chclSolventsField.getValue()) {
            submission.getSolvents().add(new SampleSolvent(CHCL3));
          }
          if (view.otherSolventsField.getValue()) {
            submission.getSolvents().add(new SampleSolvent(Solvent.OTHER));
          }
        } else {
          submission.setStructure(null);
          submission.setStorageTemperature(null);
        }
        if (submission.getService() == INTACT_PROTEIN) {
          copySamplesFromTableToSubmission(submission);
        } else {
          submission.setSource(null);
        }
        if (firstSample.getSupport() == GEL) {
          submission.setGelImages(gelImagesContainer.getItemIds());
        } else {
          submission.setSeparation(null);
          submission.setThickness(null);
        }
        logger.debug("Save submission {}", submission);
        submissionService.insert(submission);
        MessageResource resources = view.getResources();
        view.afterSuccessfulSave(resources.message("save", submission.getExperience()));
      }
    } finally {
      bindFields();
    }
  }

  private void copySamplesFromTableToSubmission(Submission submission) {
    SubmissionSample firstSample = firstSampleFieldGroup.getItemDataSource().getBean();
    submission.setSamples(new ArrayList<>());
    for (SubmissionSample sample : samplesContainer.getItemIds()) {
      sample.setSupport(firstSample.getSupport());
      sample.setQuantity(firstSample.getQuantity());
      sample.setVolume(firstSample.getVolume());
      if (firstSample.getSupport() != GEL) {
        copyStandardsFromTableToSample(sample);
        copyContaminantsFromTableToSample(sample);
      }
      submission.getSamples().add(sample);
    }
  }

  private void copyStandardsFromTableToSample(SubmissionSample sample) {
    sample.setStandards(new ArrayList<>());
    for (Standard standard : standardsContainer.getItemIds()) {
      Standard copy = new Standard();
      copy.setName(standard.getName());
      copy.setQuantity(standard.getQuantity());
      copy.setComments(standard.getComments());
      sample.getStandards().add(copy);
    }
  }

  private void copyContaminantsFromTableToSample(SubmissionSample sample) {
    sample.setContaminants(new ArrayList<>());
    for (Contaminant contaminant : contaminantsContainer.getItemIds()) {
      Contaminant copy = new Contaminant();
      copy.setName(contaminant.getName());
      copy.setQuantity(contaminant.getQuantity());
      copy.setComments(contaminant.getComments());
      sample.getContaminants().add(copy);
    }
  }

  public Item getItemDataSource() {
    return submissionFieldGroup.getItemDataSource();
  }

  public void setItemDataSource(Item item) {
    submissionFieldGroup.setItemDataSource(item != null ? item : new BeanItem<>(new Submission()));
    setDefaults();
    bindFields();
    updateVisible();
  }

  public boolean isEditable() {
    return editableProperty.getValue();
  }

  public void setEditable(boolean editable) {
    editableProperty.setValue(editable);
  }
}
