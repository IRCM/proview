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

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource.ESI;
import static ca.qc.ircm.proview.plate.QPlate.plate;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.QContaminant.contaminant;
import static ca.qc.ircm.proview.sample.QStandard.standard;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.GelSeparation.ONE_DIMENSION;
import static ca.qc.ircm.proview.submission.GelThickness.ONE;
import static ca.qc.ircm.proview.submission.QGelImage.gelImage;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.QSubmissionFile.submissionFile;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.Structure;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelImage;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.StorageTemperature;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractListing;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

/**
 * Submission form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionFormPresenter implements BinderValidator {
  public static final String SAMPLE_TYPE_LABEL = "sampleTypeLabel";
  public static final String INACTIVE_LABEL = "inactive";
  public static final String SERVICE_PANEL = "servicePanel";
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
  public static final int MAXIMUM_STRUCTURE_SIZE = 10 * 1024 * 1024; // 10MB
  public static final String MONOISOTOPIC_MASS_PROPERTY =
      submission.monoisotopicMass.getMetadata().getName();
  public static final String AVERAGE_MASS_PROPERTY = submission.averageMass.getMetadata().getName();
  public static final String TOXICITY_PROPERTY = submission.toxicity.getMetadata().getName();
  public static final String LIGHT_SENSITIVE_PROPERTY =
      submission.lightSensitive.getMetadata().getName();
  public static final String STORAGE_TEMPERATURE_PROPERTY =
      submission.storageTemperature.getMetadata().getName();
  public static final String SAMPLES_CONTAINER_TYPE_PROPERTY = SAMPLES_PROPERTY + "ContainerType";
  public static final String PLATE_PROPERTY = plate.getMetadata().getName();
  public static final String PLATE_NAME_PROPERTY = plate.name.getMetadata().getName();
  public static final String SAMPLES_TABLE = SAMPLES_PROPERTY + "Table";
  public static final String SAMPLE_NUMBER_PROTEIN_PROPERTY =
      submissionSample.numberProtein.getMetadata().getName();
  public static final String FILL_SAMPLES_PROPERTY = "fillSamples";
  public static final String SAMPLES_PLATE = SAMPLES_PROPERTY + "Plate";
  public static final String EXPERIENCE_PANEL = "experiencePanel";
  public static final String EXPERIENCE_PROPERTY = submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL_PROPERTY = submission.goal.getMetadata().getName();
  public static final String TAXONOMY_PROPERTY = submission.taxonomy.getMetadata().getName();
  public static final String PROTEIN_NAME_PROPERTY = submission.protein.getMetadata().getName();
  public static final String PROTEIN_WEIGHT_PROPERTY =
      submissionSample.molecularWeight.getMetadata().getName();
  public static final String POST_TRANSLATION_MODIFICATION_PROPERTY =
      submission.postTranslationModification.getMetadata().getName();
  public static final String SAMPLE_QUANTITY_PROPERTY =
      submissionSample.quantity.getMetadata().getName();
  public static final String SAMPLE_VOLUME_PROPERTY =
      submissionSample.volume.getMetadata().getName();
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String STANDARD_COUNT_PROPERTY = "standardCount";
  public static final String STANDARD_PROPERTY = submissionSample.standards.getMetadata().getName();
  public static final int STANDARDS_TABLE_LENGTH = 4;
  public static final String STANDARD_NAME_PROPERTY = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY_PROPERTY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENT_PROPERTY = standard.comment.getMetadata().getName();
  public static final String FILL_STANDARDS_PROPERTY = "fillStandards";
  public static final String CONTAMINANTS_PANEL = "contaminantsPanel";
  public static final String CONTAMINANT_COUNT_PROPERTY = "contaminantCount";
  public static final String CONTAMINANT_PROPERTY =
      submissionSample.contaminants.getMetadata().getName();
  public static final int CONTAMINANTS_TABLE_LENGTH = 4;
  public static final String CONTAMINANT_NAME_PROPERTY = contaminant.name.getMetadata().getName();
  public static final String CONTAMINANT_QUANTITY_PROPERTY =
      contaminant.quantity.getMetadata().getName();
  public static final String CONTAMINANT_COMMENT_PROPERTY =
      contaminant.comment.getMetadata().getName();
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
  public static final String GEL_IMAGES_UPLOADER =
      submission.gelImages.getMetadata().getName() + "Uploader";
  public static final String GEL_IMAGES_TABLE = GEL_IMAGES_PROPERTY + "Table";
  public static final int MAXIMUM_GEL_IMAGES_SIZE = 10 * 1024 * 1024; // 10MB;
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
  public static final String INJECTION_TYPE_PROPERTY =
      submission.injectionType.getMetadata().getName();
  public static final String SOURCE_PROPERTY = submission.source.getMetadata().getName();
  public static final String PROTEIN_CONTENT_PROPERTY =
      submission.proteinContent.getMetadata().getName();
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
  public static final String COMMENT_PANEL = "commentPanel";
  public static final String COMMENT_PROPERTY = submission.comment.getMetadata().getName();
  public static final String FILES_PROPERTY = submission.files.getMetadata().getName();
  public static final String FILES_UPLOADER = submission.files.getMetadata().getName() + "Uploader";
  public static final String FILES_GRID = FILES_PROPERTY + "Grid";
  public static final String EXPLANATION_PANEL = "explanationPanel";
  public static final String EXPLANATION = "explanation";
  public static final int MAXIMUM_FILES_SIZE = 10 * 1024 * 1024; // 10MB
  public static final int MAXIMUM_FILES_COUNT = 4;
  public static final int FILES_TABLE_LENGTH = 3;
  public static final String FILE_FILENAME_PROPERTY =
      submissionFile.filename.getMetadata().getName();
  public static final String REMOVE_FILE = "removeFile";
  public static final String SAVE = "save";
  public static final String UPDATE_ERROR = "updateError";
  public static final int NULL_ID = -1;
  public static final String EXAMPLE = "example";
  public static final String HIDE_REQUIRED_STYLE = "hide-required";
  private static final int MAX_SAMPLE_COUNT = 200;
  private static final int MAX_STANDARD_COUNT = 10;
  private static final int MAX_CONTAMINANT_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenter.class);
  private SubmissionForm view;
  private SubmissionFormDesign design;
  private boolean readOnly = false;
  private Binder<Submission> submissionBinder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Plate> plateBinder = new BeanValidationBinder<>(Plate.class);
  private Binder<ItemCount> sampleCountBinder = new Binder<>(ItemCount.class);
  private Binder<ItemCount> standardCountBinder = new Binder<>(ItemCount.class);
  private Binder<ItemCount> contaminantCountBinder = new Binder<>(ItemCount.class);
  private ListDataProvider<SubmissionSample> samplesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<SubmissionSample, Binder<SubmissionSample>> sampleBinders = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleNameFields = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleNumberProteinFields = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleMolecularWeightFields = new HashMap<>();
  private ListDataProvider<Standard> standardsDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<Standard, Binder<Standard>> standardBinders = new HashMap<>();
  private Map<Standard, TextField> standardNameFields = new HashMap<>();
  private Map<Standard, TextField> standardQuantityFields = new HashMap<>();
  private Map<Standard, TextField> standardCommentFields = new HashMap<>();
  private ListDataProvider<Contaminant> contaminantsDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<Contaminant, Binder<Contaminant>> contaminantBinders = new HashMap<>();
  private Map<Contaminant, TextField> contaminantNameFields = new HashMap<>();
  private Map<Contaminant, TextField> contaminantQuantityFields = new HashMap<>();
  private Map<Contaminant, TextField> contaminantCommentFields = new HashMap<>();
  private ListDataProvider<GelImage> gelImagesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private ListDataProvider<SubmissionFile> filesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  @Inject
  private SubmissionService submissionService;
  @Inject
  private SubmissionSampleService submissionSampleService;
  @Inject
  private PlateService plateService;
  @Inject
  private AuthorizationService authorizationService;

  protected SubmissionFormPresenter() {
  }

  protected SubmissionFormPresenter(SubmissionService submissionService,
      SubmissionSampleService submissionSampleService, PlateService plateService,
      AuthorizationService authorizationService) {
    this.submissionService = submissionService;
    this.submissionSampleService = submissionSampleService;
    this.plateService = plateService;
    this.authorizationService = authorizationService;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionForm view) {
    this.view = view;
    design = view.design;
    view.createStructureUploader();
    StructureUploaderReceiver structureUploaderReceiver = new StructureUploaderReceiver();
    view.structureUploader.setReceiver(structureUploaderReceiver);
    view.structureUploader.addSucceededListener(structureUploaderReceiver);
    view.structureUploader.addProgressListener(structureUploaderReceiver);
    view.createGelImagesUploader(gelImageFileHandler());
    view.createFilesUploader(fileHandler());
    prepareComponents();
    setValue(null);
    addFieldListeners();
    updateVisible();
    updateReadOnly();
    updateSampleCount(design.sampleCountField.getValue());
    updateStandardsTable(design.standardCountField.getValue());
    updateContaminantsTable(design.contaminantCountField.getValue());
  }

  private void prepareComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.sampleTypeLabel.addStyleName(SAMPLE_TYPE_LABEL);
    design.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL));
    design.inactiveLabel.addStyleName(INACTIVE_LABEL);
    design.inactiveLabel.setValue(resources.message(INACTIVE_LABEL));
    design.servicePanel.addStyleName(SERVICE_PANEL);
    design.servicePanel.addStyleName(REQUIRED);
    design.servicePanel.setCaption(resources.message(SERVICE_PROPERTY));
    design.serviceOptions.addStyleName(SERVICE_PROPERTY);
    design.serviceOptions.addStyleName(HIDE_REQUIRED_STYLE);
    design.serviceOptions.setItems(Service.availables());
    design.serviceOptions.setItemCaptionGenerator(service -> service.getLabel(locale));
    design.serviceOptions.setItemEnabledProvider(service -> service.available);
    design.serviceOptions.addValueChangeListener(
        e -> design.sampleSupportOptions.getDataProvider().refreshItem(GEL));
    submissionBinder.forField(design.serviceOptions).asRequired(generalResources.message(REQUIRED))
        .bind(SERVICE_PROPERTY);
    prepareSamplesComponents();
    prepareExperienceComponents();
    prepareStandardsComponents();
    prepareContaminantsComponents();
    prepareGelComponents();
    prepareServicesComponents();
    design.commentPanel.setCaption(resources.message(COMMENT_PANEL));
    design.commentPanel.addStyleName(COMMENT_PANEL);
    design.commentField.addStyleName(COMMENT_PROPERTY);
    submissionBinder.forField(design.commentField).withNullRepresentation("")
        .bind(COMMENT_PROPERTY);
    design.filesPanel.addStyleName(FILES_PROPERTY);
    design.filesPanel.setCaption(resources.message(FILES_PROPERTY));
    view.filesUploader.addStyleName(FILES_UPLOADER);
    view.filesUploader.setUploadButtonCaption(resources.message(FILES_UPLOADER));
    view.filesUploader.setMaxFileCount(1000000); // Count is required if size is set.
    view.filesUploader.setMaxFileSize(MAXIMUM_FILES_SIZE);
    design.filesGrid.addStyleName(FILES_GRID);
    design.filesGrid.addStyleName(COMPONENTS);
    design.filesGrid.addColumn(file -> downloadFileButton(file), new ComponentRenderer())
        .setId(FILE_FILENAME_PROPERTY)
        .setCaption(resources.message(FILES_PROPERTY + "." + FILE_FILENAME_PROPERTY))
        .setSortable(false);
    design.filesGrid.addColumn(file -> removeFileButton(file), new ComponentRenderer())
        .setId(REMOVE_FILE).setCaption(resources.message(FILES_PROPERTY + "." + REMOVE_FILE))
        .setSortable(false);
    design.filesGrid.setDataProvider(filesDataProvider);
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.addStyleName(REQUIRED);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanation.addStyleName(EXPLANATION);
    design.saveButton.addStyleName(SAVE);
    design.saveButton.setCaption(resources.message(SAVE));
  }

  private Button downloadFileButton(SubmissionFile file) {
    Button button = new Button();
    button.setCaption(file.getFilename());
    button.setIcon(VaadinIcons.DOWNLOAD);
    StreamResource resource =
        new StreamResource(() -> new ByteArrayInputStream(file.getContent()), file.getFilename());
    FileDownloader fileDownloader = new FileDownloader(resource);
    fileDownloader.extend(button);
    return button;
  }

  private Button removeFileButton(SubmissionFile file) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.setCaption(resources.message(FILES_PROPERTY + "." + REMOVE_FILE));
    button.addClickListener(e -> filesDataProvider.getItems().remove(file));
    filesDataProvider.refreshAll();
    return button;
  }

  private void prepareSamplesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.sampleSupportOptions.addStyleName(SAMPLE_SUPPORT_PROPERTY);
    design.sampleSupportOptions.setCaption(resources.message(SAMPLE_SUPPORT_PROPERTY));
    design.sampleSupportOptions.setItems(SampleSupport.values());
    design.sampleSupportOptions.setItemCaptionGenerator(support -> support.getLabel(locale));
    firstSampleBinder.forField(design.sampleSupportOptions)
        .asRequired(generalResources.message(REQUIRED)).bind(SAMPLE_SUPPORT_PROPERTY);
    design.solutionSolventField.addStyleName(SOLUTION_SOLVENT_PROPERTY);
    design.solutionSolventField.setCaption(resources.message(SOLUTION_SOLVENT_PROPERTY));
    design.solutionSolventField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.solutionSolventField)
        .withValidator(requiredTextIfVisible(design.solutionSolventField))
        .withNullRepresentation("").bind(SOLUTION_SOLVENT_PROPERTY);
    design.sampleCountField.addStyleName(SAMPLE_COUNT_PROPERTY);
    design.sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    sampleCountBinder.forField(design.sampleCountField)
        .asRequired(generalResources.message(REQUIRED)).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 1, MAX_SAMPLE_COUNT), 1, MAX_SAMPLE_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.sampleNameField.addStyleName(SAMPLE_NAME_PROPERTY);
    design.sampleNameField.setCaption(resources.message(SAMPLE_NAME_PROPERTY));
    design.sampleNameField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleNameField)
        .withValidator(requiredTextIfVisible(design.sampleNameField)).withNullRepresentation("")
        .withValidator(validateSampleName(true)).bind(SAMPLE_NAME_PROPERTY);
    design.formulaField.addStyleName(FORMULA_PROPERTY);
    design.formulaField.setCaption(resources.message(FORMULA_PROPERTY));
    design.formulaField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.formulaField)
        .withValidator(requiredTextIfVisible(design.formulaField)).withNullRepresentation("")
        .bind(FORMULA_PROPERTY);
    design.structureLayout.addStyleName(REQUIRED);
    design.structureLayout.setCaption(resources.message(STRUCTURE_PROPERTY));
    design.structureButton.addStyleName(STRUCTURE_PROPERTY);
    design.structureButton.setVisible(false);
    view.structureUploader.addStyleName(STRUCTURE_UPLOADER);
    view.structureUploader.setButtonCaption(resources.message(STRUCTURE_UPLOADER));
    view.structureUploader.setImmediateMode(true);
    design.monoisotopicMassField.addStyleName(MONOISOTOPIC_MASS_PROPERTY);
    design.monoisotopicMassField.setCaption(resources.message(MONOISOTOPIC_MASS_PROPERTY));
    design.monoisotopicMassField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.monoisotopicMassField)
        .withValidator(requiredTextIfVisible(design.monoisotopicMassField))
        .withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS_PROPERTY);
    design.averageMassField.addStyleName(AVERAGE_MASS_PROPERTY);
    design.averageMassField.setCaption(resources.message(AVERAGE_MASS_PROPERTY));
    submissionBinder.forField(design.averageMassField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(AVERAGE_MASS_PROPERTY);
    design.toxicityField.addStyleName(TOXICITY_PROPERTY);
    design.toxicityField.setCaption(resources.message(TOXICITY_PROPERTY));
    submissionBinder.forField(design.toxicityField).withNullRepresentation("")
        .bind(TOXICITY_PROPERTY);
    design.lightSensitiveField.addStyleName(LIGHT_SENSITIVE_PROPERTY);
    design.lightSensitiveField.setCaption(resources.message(LIGHT_SENSITIVE_PROPERTY));
    submissionBinder.forField(design.lightSensitiveField).bind(LIGHT_SENSITIVE_PROPERTY);
    design.storageTemperatureOptions.addStyleName(STORAGE_TEMPERATURE_PROPERTY);
    design.storageTemperatureOptions.setCaption(resources.message(STORAGE_TEMPERATURE_PROPERTY));
    design.storageTemperatureOptions.setItems(StorageTemperature.values());
    design.storageTemperatureOptions
        .setItemCaptionGenerator(storageTemperature -> storageTemperature.getLabel(locale));
    design.storageTemperatureOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.storageTemperatureOptions)
        .withValidator(requiredIfVisible(design.storageTemperatureOptions))
        .bind(STORAGE_TEMPERATURE_PROPERTY);
    design.sampleContainerTypeOptions.addStyleName(SAMPLES_CONTAINER_TYPE_PROPERTY);
    design.sampleContainerTypeOptions
        .setCaption(resources.message(SAMPLES_CONTAINER_TYPE_PROPERTY));
    design.sampleContainerTypeOptions.setItems(SampleContainerType.values());
    design.sampleContainerTypeOptions
        .setItemCaptionGenerator(sampleContainerType -> sampleContainerType.getLabel(locale));
    design.sampleContainerTypeOptions.setRequiredIndicatorVisible(true);
    design.plateNameField.addStyleName(PLATE_PROPERTY + "-" + PLATE_NAME_PROPERTY);
    design.plateNameField.setCaption(resources.message(PLATE_PROPERTY + "." + PLATE_NAME_PROPERTY));
    design.plateNameField.setRequiredIndicatorVisible(true);
    plateBinder.forField(design.plateNameField)
        .withValidator(requiredTextIfVisible(design.plateNameField)).withNullRepresentation("")
        .withValidator(validatePlateName()).bind(PLATE_NAME_PROPERTY);
    design.samplesLabel.addStyleName(SAMPLES_PROPERTY);
    design.samplesLabel.setCaption(resources.message(SAMPLES_PROPERTY));
    design.samplesGrid.addStyleName(SAMPLES_TABLE);
    design.samplesGrid.addStyleName(COMPONENTS);
    design.samplesGrid.setDataProvider(samplesDataProvider);
    design.samplesGrid.addColumn(sample -> sampleNameTextField(sample), new ComponentRenderer())
        .setId(SAMPLE_NAME_PROPERTY).setCaption(resources.message(SAMPLE_NAME_PROPERTY))
        .setWidth(230).setSortable(false);
    design.samplesGrid
        .addColumn(sample -> sampleNumberProteinTextField(sample), new ComponentRenderer())
        .setId(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .setCaption(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY)).setWidth(230)
        .setSortable(false);
    design.samplesGrid.addColumn(sample -> proteinWeightTextField(sample), new ComponentRenderer())
        .setId(PROTEIN_WEIGHT_PROPERTY).setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY))
        .setWidth(230).setSortable(false);
    design.fillSamplesButton.addStyleName(FILL_SAMPLES_PROPERTY);
    design.fillSamplesButton.addStyleName(BUTTON_SKIP_ROW);
    design.fillSamplesButton.setCaption(resources.message(FILL_SAMPLES_PROPERTY));
    design.fillSamplesButton.setIcon(VaadinIcons.ARROW_DOWN);
    view.plateComponent.addStyleName(SAMPLES_PLATE);
  }

  private TextField sampleNameTextField(SubmissionSample sample) {
    if (sampleNameFields.containsKey(sample)) {
      return sampleNameFields.get(sample);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<SubmissionSample> binder = sampleBinders.get(sample);
      if (binder == null) {
        binder = new BeanValidationBinder<>(SubmissionSample.class);
        binder.setBean(sample);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").withValidator(validateSampleName(true))
          .bind(SAMPLE_NAME_PROPERTY);
      sampleBinders.put(sample, binder);
      sampleNameFields.put(sample, field);
      return field;
    }
  }

  private TextField sampleNumberProteinTextField(SubmissionSample sample) {
    if (sampleNumberProteinFields.containsKey(sample)) {
      return sampleNumberProteinFields.get(sample);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<SubmissionSample> binder = sampleBinders.get(sample);
      if (binder == null) {
        binder = new BeanValidationBinder<>(SubmissionSample.class);
        binder.setBean(sample);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setRequiredIndicatorVisible(true);
      binder.forField(field)
          .withValidator(requiredTextIf(n -> design.serviceOptions.getValue() == INTACT_PROTEIN))
          .withNullRepresentation("")
          .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
          .bind(SAMPLE_NUMBER_PROTEIN_PROPERTY);
      sampleBinders.put(sample, binder);
      sampleNumberProteinFields.put(sample, field);
      return field;
    }
  }

  private TextField proteinWeightTextField(SubmissionSample sample) {
    if (sampleMolecularWeightFields.containsKey(sample)) {
      return sampleMolecularWeightFields.get(sample);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<SubmissionSample> binder = sampleBinders.get(sample);
      if (binder == null) {
        binder = new BeanValidationBinder<>(SubmissionSample.class);
        binder.setBean(sample);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setRequiredIndicatorVisible(true);
      binder.forField(field)
          .withValidator(requiredTextIf(n -> design.serviceOptions.getValue() == INTACT_PROTEIN))
          .withNullRepresentation("")
          .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
          .bind(PROTEIN_WEIGHT_PROPERTY);
      sampleBinders.put(sample, binder);
      sampleMolecularWeightFields.put(sample, field);
      return field;
    }
  }

  private void prepareExperienceComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.experiencePanel.addStyleName(EXPERIENCE_PANEL);
    design.experiencePanel.setCaption(resources.message(EXPERIENCE_PANEL));
    design.experienceField.addStyleName(EXPERIENCE_PROPERTY);
    design.experienceField.setCaption(resources.message(EXPERIENCE_PROPERTY));
    design.experienceField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.experienceField)
        .withValidator(requiredTextIfVisible(design.experienceField)).withNullRepresentation("")
        .bind(EXPERIENCE_PROPERTY);
    design.experienceGoalField.addStyleName(EXPERIENCE_GOAL_PROPERTY);
    design.experienceGoalField.setCaption(resources.message(EXPERIENCE_GOAL_PROPERTY));
    submissionBinder.forField(design.experienceGoalField).withNullRepresentation("")
        .bind(EXPERIENCE_GOAL_PROPERTY);
    design.taxonomyField.addStyleName(TAXONOMY_PROPERTY);
    design.taxonomyField.setCaption(resources.message(TAXONOMY_PROPERTY));
    design.taxonomyField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.taxonomyField)
        .withValidator(requiredTextIfVisible(design.taxonomyField)).withNullRepresentation("")
        .bind(TAXONOMY_PROPERTY);
    design.proteinNameField.addStyleName(PROTEIN_NAME_PROPERTY);
    design.proteinNameField.setCaption(resources.message(PROTEIN_NAME_PROPERTY));
    submissionBinder.forField(design.proteinNameField).withNullRepresentation("")
        .bind(PROTEIN_NAME_PROPERTY);
    design.proteinWeightField.addStyleName(PROTEIN_WEIGHT_PROPERTY);
    design.proteinWeightField.setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY));
    firstSampleBinder.forField(design.proteinWeightField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(PROTEIN_WEIGHT_PROPERTY);
    design.postTranslationModificationField.addStyleName(POST_TRANSLATION_MODIFICATION_PROPERTY);
    design.postTranslationModificationField
        .setCaption(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY));
    submissionBinder.forField(design.postTranslationModificationField).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION_PROPERTY);
    design.sampleQuantityField.addStyleName(SAMPLE_QUANTITY_PROPERTY);
    design.sampleQuantityField.setCaption(resources.message(SAMPLE_QUANTITY_PROPERTY));
    design.sampleQuantityField
        .setPlaceholder(resources.message(SAMPLE_QUANTITY_PROPERTY + "." + EXAMPLE));
    design.sampleQuantityField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleQuantityField)
        .withValidator(requiredTextIfVisible(design.sampleQuantityField)).withNullRepresentation("")
        .bind(SAMPLE_QUANTITY_PROPERTY);
    design.sampleVolumeField.addStyleName(SAMPLE_VOLUME_PROPERTY);
    design.sampleVolumeField.setCaption(resources.message(SAMPLE_VOLUME_PROPERTY));
    design.sampleVolumeField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleVolumeField)
        .withValidator(requiredTextIfVisible(design.sampleVolumeField)).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SAMPLE_VOLUME_PROPERTY);
  }

  private void prepareStandardsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.standardsPanel.addStyleName(STANDARDS_PANEL);
    design.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    design.standardCountField.addStyleName(STANDARD_COUNT_PROPERTY);
    design.standardCountField.setCaption(resources.message(STANDARD_COUNT_PROPERTY));
    standardCountBinder.forField(design.standardCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_STANDARD_COUNT), 0, MAX_STANDARD_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.standardsGrid.addStyleName(STANDARD_PROPERTY);
    design.standardsGrid.addStyleName(COMPONENTS);
    design.standardsGrid.setDataProvider(standardsDataProvider);
    design.standardsGrid
        .addColumn(standard -> standardNameTextField(standard), new ComponentRenderer())
        .setId(STANDARD_NAME_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_NAME_PROPERTY))
        .setSortable(false);
    design.standardsGrid
        .addColumn(standard -> standardQuantityTextField(standard), new ComponentRenderer())
        .setId(STANDARD_QUANTITY_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY))
        .setSortable(false);
    design.standardsGrid
        .addColumn(standard -> standardCommentTextField(standard), new ComponentRenderer())
        .setId(STANDARD_COMMENT_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_COMMENT_PROPERTY))
        .setSortable(false);
    design.fillStandardsButton.addStyleName(FILL_STANDARDS_PROPERTY);
    design.fillStandardsButton.addStyleName(BUTTON_SKIP_ROW);
    design.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS_PROPERTY));
    design.fillStandardsButton.setIcon(VaadinIcons.ARROW_DOWN);
  }

  private TextField standardNameTextField(Standard standard) {
    if (standardNameFields.containsKey(standard)) {
      return standardNameFields.get(standard);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_NAME_PROPERTY);
      standardBinders.put(standard, binder);
      standardNameFields.put(standard, field);
      return field;
    }
  }

  private TextField standardQuantityTextField(Standard standard) {
    if (standardQuantityFields.containsKey(standard)) {
      return standardQuantityFields.get(standard);
    } else {
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setPlaceholder(
          resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_QUANTITY_PROPERTY);
      standardBinders.put(standard, binder);
      standardQuantityFields.put(standard, field);
      return field;
    }
  }

  private TextField standardCommentTextField(Standard standard) {
    if (standardCommentFields.containsKey(standard)) {
      return standardCommentFields.get(standard);
    } else {
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).withNullRepresentation("").bind(STANDARD_COMMENT_PROPERTY);
      standardBinders.put(standard, binder);
      standardCommentFields.put(standard, field);
      return field;
    }
  }

  private void prepareContaminantsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.contaminantsPanel.addStyleName(CONTAMINANTS_PANEL);
    design.contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL));
    design.contaminantCountField.addStyleName(CONTAMINANT_COUNT_PROPERTY);
    design.contaminantCountField.setCaption(resources.message(CONTAMINANT_COUNT_PROPERTY));
    contaminantCountBinder.forField(design.contaminantCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_CONTAMINANT_COUNT), 0,
            MAX_CONTAMINANT_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.contaminantsGrid.addStyleName(CONTAMINANT_PROPERTY);
    design.contaminantsGrid.addStyleName(COMPONENTS);
    design.contaminantsGrid.setDataProvider(contaminantsDataProvider);
    design.contaminantsGrid.addColumn(contaminant -> contaminantNameTextField(contaminant))
        .setId(CONTAMINANT_NAME_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_NAME_PROPERTY))
        .setSortable(false);
    design.contaminantsGrid.addColumn(contaminant -> contaminantQuantityTextField(contaminant))
        .setId(CONTAMINANT_QUANTITY_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY))
        .setSortable(false);
    design.contaminantsGrid.addColumn(contaminant -> contaminantCommentTextField(contaminant))
        .setId(CONTAMINANT_COMMENT_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_COMMENT_PROPERTY))
        .setSortable(false);
    design.fillContaminantsButton.addStyleName(FILL_CONTAMINANTS_PROPERTY);
    design.fillContaminantsButton.addStyleName(BUTTON_SKIP_ROW);
    design.fillContaminantsButton.setCaption(resources.message(FILL_CONTAMINANTS_PROPERTY));
    design.fillContaminantsButton.setIcon(VaadinIcons.ARROW_DOWN);
  }

  private TextField contaminantNameTextField(Contaminant contaminant) {
    if (contaminantNameFields.containsKey(contaminant)) {
      return contaminantNameFields.get(contaminant);
    } else {
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Contaminant> binder = contaminantBinders.get(contaminant);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Contaminant.class);
        binder.setBean(contaminant);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(CONTAMINANT_NAME_PROPERTY);
      contaminantBinders.put(contaminant, binder);
      contaminantNameFields.put(contaminant, field);
      return field;
    }
  }

  private TextField contaminantQuantityTextField(Contaminant contaminant) {
    if (contaminantQuantityFields.containsKey(contaminant)) {
      return contaminantQuantityFields.get(contaminant);
    } else {
      final MessageResource resources = view.getResources();
      final MessageResource generalResources = view.getGeneralResources();
      Binder<Contaminant> binder = contaminantBinders.get(contaminant);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Contaminant.class);
        binder.setBean(contaminant);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setPlaceholder(resources
          .message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(CONTAMINANT_QUANTITY_PROPERTY);
      contaminantBinders.put(contaminant, binder);
      contaminantQuantityFields.put(contaminant, field);
      return field;
    }
  }

  private TextField contaminantCommentTextField(Contaminant contaminant) {
    if (contaminantCommentFields.containsKey(contaminant)) {
      return contaminantCommentFields.get(contaminant);
    } else {
      Binder<Contaminant> binder = contaminantBinders.get(contaminant);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Contaminant.class);
        binder.setBean(contaminant);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).withNullRepresentation("").bind(CONTAMINANT_COMMENT_PROPERTY);
      contaminantBinders.put(contaminant, binder);
      contaminantCommentFields.put(contaminant, field);
      return field;
    }
  }

  private void prepareGelComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.gelPanel.addStyleName(GEL_PANEL);
    design.gelPanel.setCaption(resources.message(GEL_PANEL));
    design.separationField.addStyleName(SEPARATION_PROPERTY);
    design.separationField.setCaption(resources.message(SEPARATION_PROPERTY));
    design.separationField.setEmptySelectionAllowed(false);
    design.separationField.setItems(GelSeparation.values());
    design.separationField.setItemCaptionGenerator(separation -> separation.getLabel(locale));
    design.separationField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.separationField)
        .withValidator(requiredIfVisible(design.separationField)).bind(SEPARATION_PROPERTY);
    design.thicknessField.addStyleName(THICKNESS_PROPERTY);
    design.thicknessField.setCaption(resources.message(THICKNESS_PROPERTY));
    design.thicknessField.setEmptySelectionAllowed(false);
    design.thicknessField.setItems(GelThickness.values());
    design.thicknessField.setItemCaptionGenerator(thickness -> thickness.getLabel(locale));
    design.thicknessField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.thicknessField)
        .withValidator(requiredIfVisible(design.thicknessField)).bind(THICKNESS_PROPERTY);
    design.colorationField.addStyleName(COLORATION_PROPERTY);
    design.colorationField.setCaption(resources.message(COLORATION_PROPERTY));
    design.colorationField.setEmptySelectionAllowed(true);
    design.colorationField.setEmptySelectionCaption(GelColoration.getNullLabel(locale));
    design.colorationField.setItems(GelColoration.values());
    design.colorationField.setItemCaptionGenerator(coloration -> coloration.getLabel(locale));
    submissionBinder.forField(design.colorationField).bind(COLORATION_PROPERTY);
    design.otherColorationField.addStyleName(OTHER_COLORATION_PROPERTY);
    design.otherColorationField.setCaption(resources.message(OTHER_COLORATION_PROPERTY));
    design.otherColorationField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.otherColorationField)
        .withValidator(requiredTextIfVisible(design.otherColorationField))
        .withNullRepresentation("").bind(OTHER_COLORATION_PROPERTY);
    design.developmentTimeField.addStyleName(DEVELOPMENT_TIME_PROPERTY);
    design.developmentTimeField.setCaption(resources.message(DEVELOPMENT_TIME_PROPERTY));
    design.developmentTimeField
        .setPlaceholder(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(design.developmentTimeField).withNullRepresentation("")
        .bind(DEVELOPMENT_TIME_PROPERTY);
    design.decolorationField.addStyleName(DECOLORATION_PROPERTY);
    design.decolorationField.setCaption(resources.message(DECOLORATION_PROPERTY));
    submissionBinder.forField(design.decolorationField).bind(DECOLORATION_PROPERTY);
    design.weightMarkerQuantityField.addStyleName(WEIGHT_MARKER_QUANTITY_PROPERTY);
    design.weightMarkerQuantityField.setCaption(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY));
    design.weightMarkerQuantityField
        .setPlaceholder(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(design.weightMarkerQuantityField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY_PROPERTY);
    design.proteinQuantityField.addStyleName(PROTEIN_QUANTITY_PROPERTY);
    design.proteinQuantityField.setCaption(resources.message(PROTEIN_QUANTITY_PROPERTY));
    design.proteinQuantityField
        .setPlaceholder(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(design.proteinQuantityField).withNullRepresentation("")
        .bind(PROTEIN_QUANTITY_PROPERTY);
    design.gelImagesLayout.addStyleName(REQUIRED);
    design.gelImagesLayout.setCaption(resources.message(GEL_IMAGES_PROPERTY));
    view.gelImagesUploader.addStyleName(GEL_IMAGES_PROPERTY);
    view.gelImagesUploader.setUploadButtonCaption(resources.message(GEL_IMAGES_UPLOADER));
    view.gelImagesUploader.setMaxFileCount(1000000); // Count is required if size is set.
    view.gelImagesUploader.setMaxFileSize(MAXIMUM_GEL_IMAGES_SIZE);
    design.gelImagesGrid.addStyleName(GEL_IMAGES_TABLE);
    design.gelImagesGrid.addStyleName(COMPONENTS);
    design.gelImagesGrid.addColumn(image -> downloadGelImageButton(image))
        .setId(GEL_IMAGE_FILENAME_PROPERTY)
        .setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + GEL_IMAGE_FILENAME_PROPERTY))
        .setSortable(false);
    design.gelImagesGrid.addColumn(image -> removeGelImageButton(image)).setId(REMOVE_GEL_IMAGE)
        .setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE))
        .setSortable(false);
    design.gelImagesGrid.setDataProvider(gelImagesDataProvider);
  }

  private Button downloadGelImageButton(GelImage file) {
    Button button = new Button();
    button.setCaption(file.getFilename());
    button.setIcon(VaadinIcons.DOWNLOAD);
    StreamResource resource =
        new StreamResource(() -> new ByteArrayInputStream(file.getContent()), file.getFilename());
    FileDownloader fileDownloader = new FileDownloader(resource);
    fileDownloader.extend(button);
    return button;
  }

  private Button removeGelImageButton(GelImage file) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE));
    button.addClickListener(e -> gelImagesDataProvider.getItems().remove(file));
    gelImagesDataProvider.refreshAll();
    return button;
  }

  private void prepareServicesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.servicesPanel.addStyleName(SERVICES_PANEL);
    design.servicesPanel.setCaption(resources.message(SERVICES_PANEL));
    design.digestionOptions.addStyleName(DIGESTION_PROPERTY);
    design.digestionOptions.setCaption(resources.message(DIGESTION_PROPERTY));
    design.digestionOptions.setItemCaptionGenerator(digestion -> digestion.getLabel(locale));
    design.digestionOptions.setItems(ProteolyticDigestion.values());
    design.digestionOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.digestionOptions)
        .withValidator(requiredIfVisible(design.digestionOptions)).bind(DIGESTION_PROPERTY);
    design.usedProteolyticDigestionMethodField.addStyleName(USED_DIGESTION_PROPERTY);
    design.usedProteolyticDigestionMethodField
        .setCaption(resources.message(USED_DIGESTION_PROPERTY));
    design.usedProteolyticDigestionMethodField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.usedProteolyticDigestionMethodField)
        .withValidator(requiredTextIfVisible(design.usedProteolyticDigestionMethodField))
        .withNullRepresentation("").bind(USED_DIGESTION_PROPERTY);
    design.otherProteolyticDigestionMethodField.addStyleName(OTHER_DIGESTION_PROPERTY);
    design.otherProteolyticDigestionMethodField
        .setCaption(resources.message(OTHER_DIGESTION_PROPERTY));
    design.otherProteolyticDigestionMethodField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.otherProteolyticDigestionMethodField)
        .withValidator(requiredTextIfVisible(design.otherProteolyticDigestionMethodField))
        .withNullRepresentation("").bind(OTHER_DIGESTION_PROPERTY);
    design.otherProteolyticDigestionMethodNote
        .setValue(resources.message(OTHER_DIGESTION_PROPERTY + ".note"));
    design.enrichmentLabel.addStyleName(ENRICHEMENT_PROPERTY);
    design.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    design.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    design.exclusionsLabel.addStyleName(EXCLUSIONS_PROPERTY);
    design.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    design.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    design.injectionTypeOptions.addStyleName(INJECTION_TYPE_PROPERTY);
    design.injectionTypeOptions.setCaption(resources.message(INJECTION_TYPE_PROPERTY));
    design.injectionTypeOptions.setItems(InjectionType.values());
    design.injectionTypeOptions
        .setItemCaptionGenerator(injectionType -> injectionType.getLabel(locale));
    design.injectionTypeOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.injectionTypeOptions)
        .withValidator(requiredIfVisible(design.injectionTypeOptions))
        .bind(INJECTION_TYPE_PROPERTY);
    design.sourceOptions.addStyleName(SOURCE_PROPERTY);
    design.sourceOptions.setCaption(resources.message(SOURCE_PROPERTY));
    design.sourceOptions.setItems(MassDetectionInstrumentSource.availables());
    design.sourceOptions.setItemCaptionGenerator(source -> source.getLabel(locale));
    design.sourceOptions.setItemEnabledProvider(source -> source.available);
    design.sourceOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.sourceOptions)
        .withValidator(requiredIfVisible(design.sourceOptions)).bind(SOURCE_PROPERTY);
    design.proteinContentOptions.addStyleName(PROTEIN_CONTENT_PROPERTY);
    design.proteinContentOptions.setCaption(resources.message(PROTEIN_CONTENT_PROPERTY));
    design.proteinContentOptions.setItems(ProteinContent.values());
    design.proteinContentOptions
        .setItemCaptionGenerator(proteinContent -> proteinContent.getLabel(locale));
    design.proteinContentOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.proteinContentOptions)
        .withValidator(requiredIfVisible(design.proteinContentOptions))
        .bind(PROTEIN_CONTENT_PROPERTY);
    design.instrumentOptions.addStyleName(INSTRUMENT_PROPERTY);
    design.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    design.instrumentOptions.setItems(instrumentValues());
    design.instrumentOptions
        .setItemCaptionGenerator(instrument -> instrument != null ? instrument.getLabel(locale)
            : MassDetectionInstrument.getNullLabel(locale));
    design.instrumentOptions
        .setItemEnabledProvider(instrument -> instrument != null ? instrument.userChoice : true);
    submissionBinder.forField(design.instrumentOptions)
        .withNullRepresentation(MassDetectionInstrument.NULL).bind(INSTRUMENT_PROPERTY);
    design.proteinIdentificationOptions.addStyleName(PROTEIN_IDENTIFICATION_PROPERTY);
    design.proteinIdentificationOptions
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    design.proteinIdentificationOptions.setItems(ProteinIdentification.availables());
    design.proteinIdentificationOptions
        .setItemCaptionGenerator(proteinIdentification -> proteinIdentification.getLabel(locale));
    design.proteinIdentificationOptions
        .setItemEnabledProvider(proteinIdentification -> proteinIdentification.available);
    design.proteinIdentificationOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.proteinIdentificationOptions)
        .withValidator(requiredIfVisible(design.proteinIdentificationOptions))
        .bind(PROTEIN_IDENTIFICATION_PROPERTY);
    design.proteinIdentificationLinkField.addStyleName(PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    design.proteinIdentificationLinkField
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY));
    design.proteinIdentificationLinkField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.proteinIdentificationLinkField)
        .withValidator(requiredTextIfVisible(design.proteinIdentificationLinkField))
        .withNullRepresentation("").bind(PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    design.quantificationOptions.addStyleName(QUANTIFICATION_PROPERTY);
    design.quantificationOptions.setCaption(resources.message(QUANTIFICATION_PROPERTY));
    design.quantificationOptions.setItems(quantificationValues());
    design.quantificationOptions.setItemCaptionGenerator(
        quantification -> quantification != null ? quantification.getLabel(locale)
            : Quantification.getNullLabel(locale));
    submissionBinder.forField(design.quantificationOptions)
        .withNullRepresentation(Quantification.NULL).bind(QUANTIFICATION_PROPERTY);
    design.quantificationLabelsField.addStyleName(QUANTIFICATION_LABELS_PROPERTY);
    design.quantificationLabelsField.setCaption(resources.message(QUANTIFICATION_LABELS_PROPERTY));
    design.quantificationLabelsField
        .setPlaceholder(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(design.quantificationLabelsField).withValidator((value, context) -> {
      if (design.quantificationOptions.getValue() == SILAC && value.isEmpty()) {
        return ValidationResult.error(generalResources.message(REQUIRED));
      }
      return ValidationResult.ok();
    }).withNullRepresentation("").bind(QUANTIFICATION_LABELS_PROPERTY);
    design.highResolutionOptions.addStyleName(HIGH_RESOLUTION_PROPERTY);
    design.highResolutionOptions.setCaption(resources.message(HIGH_RESOLUTION_PROPERTY));
    design.highResolutionOptions.setItems(false, true);
    design.highResolutionOptions.setItemCaptionGenerator(
        value -> resources.message(HIGH_RESOLUTION_PROPERTY + "." + value));
    design.highResolutionOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.highResolutionOptions)
        .withValidator(requiredIfVisible(design.highResolutionOptions))
        .bind(HIGH_RESOLUTION_PROPERTY);
    design.solventsLayout.addStyleName(REQUIRED);
    design.solventsLayout.setCaption(resources.message(SOLVENTS_PROPERTY));
    design.acetonitrileSolventsField
        .addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name());
    design.acetonitrileSolventsField.setCaption(Solvent.ACETONITRILE.getLabel(locale));
    design.methanolSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name());
    design.methanolSolventsField.setCaption(Solvent.METHANOL.getLabel(locale));
    design.chclSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name());
    design.chclSolventsField.setCaption(Solvent.CHCL3.getLabel(locale));
    design.chclSolventsField.setCaptionAsHtml(true);
    design.otherSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name());
    design.otherSolventsField.setCaption(Solvent.OTHER.getLabel(locale));
    design.otherSolventField.setCaption(resources.message(OTHER_SOLVENT_PROPERTY));
    design.otherSolventField.addStyleName(OTHER_SOLVENT_PROPERTY);
    design.otherSolventField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    design.otherSolventField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.otherSolventField)
        .withValidator(requiredTextIfVisible(design.otherSolventField)).withNullRepresentation("")
        .bind(OTHER_SOLVENT_PROPERTY);
    design.otherSolventNoteLabel.addStyleName(OTHER_SOLVENT_NOTE);
    design.otherSolventNoteLabel.setValue(resources.message(OTHER_SOLVENT_NOTE));
  }

  private <V> Validator<V> requiredIfVisible(AbstractListing<V> field) {
    final MessageResource generalResources = view.getGeneralResources();
    return (value, context) -> {
      if (field.isVisible() && value == null) {
        return ValidationResult.error(generalResources.message(REQUIRED));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> requiredTextIfVisible(AbstractTextField field) {
    final MessageResource generalResources = view.getGeneralResources();
    return (value, context) -> {
      if (field.isVisible() && value.isEmpty()) {
        return ValidationResult.error(generalResources.message(REQUIRED));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> requiredTextIf(Predicate<Void> predicate) {
    return (value, context) -> {
      if (predicate.test(null) && value.isEmpty()) {
        final MessageResource generalResources = view.getGeneralResources();
        return ValidationResult.error(generalResources.message(REQUIRED));
      } else {
        return ValidationResult.ok();
      }
    };
  }

  private void addFieldListeners() {
    design.serviceOptions.addValueChangeListener(e -> updateVisible());
    design.sampleSupportOptions.addValueChangeListener(e -> updateVisible());
    design.sampleCountField
        .addValueChangeListener(e -> updateSampleCount(design.sampleCountField.getValue()));
    design.sampleCountField.addValueChangeListener(e -> updateSampleCount(e.getValue()));
    design.sampleContainerTypeOptions.addValueChangeListener(e -> updateVisible());
    design.fillSamplesButton.addClickListener(e -> fillSamples());
    design.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(design.standardCountField.getValue()));
    design.standardCountField.addValueChangeListener(e -> updateStandardsTable(e.getValue()));
    design.fillStandardsButton.addClickListener(e -> fillStandards());
    design.contaminantCountField.addValueChangeListener(
        e -> updateContaminantsTable(design.contaminantCountField.getValue()));
    design.contaminantCountField.addValueChangeListener(e -> updateContaminantsTable(e.getValue()));
    design.fillContaminantsButton.addClickListener(e -> fillContaminants());
    design.colorationField.addValueChangeListener(e -> updateVisible());
    design.digestionOptions.addValueChangeListener(e -> updateVisible());
    design.proteinIdentificationOptions.addValueChangeListener(e -> updateVisible());
    design.quantificationOptions.addValueChangeListener(e -> design.quantificationLabelsField
        .setRequiredIndicatorVisible(design.quantificationOptions.getValue() == SILAC));
    design.otherSolventsField.addValueChangeListener(e -> updateVisible());
    design.saveButton.addClickListener(e -> save());
  }

  private void updateVisible() {
    final Service service = design.serviceOptions.getValue();
    final SampleSupport support = design.sampleSupportOptions.getValue();
    design.sampleTypeLabel.setVisible(!readOnly);
    design.inactiveLabel.setVisible(!readOnly);
    design.sampleSupportOptions
        .setItemEnabledProvider(value -> value != GEL || service == LC_MS_MS);
    design.solutionSolventField
        .setVisible(service == SMALL_MOLECULE && support == SampleSupport.SOLUTION);
    design.sampleNameField.setVisible(service == SMALL_MOLECULE);
    design.formulaField.setVisible(service == SMALL_MOLECULE);
    design.structureLayout.setVisible(service == SMALL_MOLECULE);
    view.structureUploader.setVisible(service == SMALL_MOLECULE && !readOnly);
    design.structureButton
        .setVisible(service == SMALL_MOLECULE && design.structureButton.getCaption() != null
            && !design.structureButton.getCaption().isEmpty());
    design.monoisotopicMassField.setVisible(service == SMALL_MOLECULE);
    design.averageMassField.setVisible(service == SMALL_MOLECULE);
    design.toxicityField.setVisible(service == SMALL_MOLECULE);
    design.lightSensitiveField.setVisible(service == SMALL_MOLECULE);
    design.storageTemperatureOptions.setVisible(service == SMALL_MOLECULE);
    design.sampleCountField.setVisible(service != SMALL_MOLECULE);
    design.sampleContainerTypeOptions.setVisible(service == LC_MS_MS);
    design.plateNameField
        .setVisible(service == LC_MS_MS && design.sampleContainerTypeOptions.getValue() == WELL);
    design.samplesLabel.setVisible(service != SMALL_MOLECULE);
    design.samplesGridLayout.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerTypeOptions.getValue() != WELL));
    design.samplesGrid.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerTypeOptions.getValue() != WELL));
    design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .setHidden(service != INTACT_PROTEIN);
    design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).setHidden(service != INTACT_PROTEIN);
    design.samplesGrid.setWidth((float) design.samplesGrid.getColumns().stream()
        .filter(column -> !column.isHidden()).mapToDouble(column -> column.getWidth()).sum(),
        Unit.PIXELS);
    design.fillSamplesButton.setVisible((service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerTypeOptions.getValue() != WELL))
        && !readOnly);
    design.samplesPlateContainer
        .setVisible(service == LC_MS_MS && design.sampleContainerTypeOptions.getValue() == WELL);
    design.experiencePanel.setVisible(service != SMALL_MOLECULE);
    design.experienceField.setVisible(service != SMALL_MOLECULE);
    design.experienceGoalField.setVisible(service != SMALL_MOLECULE);
    design.taxonomyField.setVisible(service != SMALL_MOLECULE);
    design.proteinNameField.setVisible(service != SMALL_MOLECULE);
    design.proteinWeightField.setVisible(service == LC_MS_MS);
    design.postTranslationModificationField.setVisible(service != SMALL_MOLECULE);
    design.sampleQuantityField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.sampleVolumeField.setVisible(service != SMALL_MOLECULE && support == SOLUTION);
    design.standardsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.standardCountField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.standardsGrid
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.fillStandardsButton.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && !readOnly);
    design.contaminantsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.contaminantCountField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.contaminantsGrid
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.fillContaminantsButton.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && !readOnly);
    design.gelPanel.setVisible(service == LC_MS_MS && support == GEL);
    design.separationField.setVisible(service == LC_MS_MS && support == GEL);
    design.thicknessField.setVisible(service == LC_MS_MS && support == GEL);
    design.colorationField.setVisible(service == LC_MS_MS && support == GEL);
    design.otherColorationField.setVisible(service == LC_MS_MS && support == GEL
        && design.colorationField.getValue() == GelColoration.OTHER);
    design.developmentTimeField.setVisible(service == LC_MS_MS && support == GEL);
    design.decolorationField.setVisible(service == LC_MS_MS && support == GEL);
    design.weightMarkerQuantityField.setVisible(service == LC_MS_MS && support == GEL);
    design.proteinQuantityField.setVisible(service == LC_MS_MS && support == GEL);
    design.gelImagesLayout.setVisible(service == LC_MS_MS && support == GEL);
    view.gelImagesUploader.setVisible(service == LC_MS_MS && support == GEL && !readOnly);
    design.gelImagesGrid.setVisible(service == LC_MS_MS && support == GEL);
    design.digestionOptions.setVisible(service == LC_MS_MS);
    design.usedProteolyticDigestionMethodField.setVisible(
        design.digestionOptions.isVisible() && design.digestionOptions.getValue() == DIGESTED);
    design.otherProteolyticDigestionMethodField.setVisible(design.digestionOptions.isVisible()
        && design.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    design.otherProteolyticDigestionMethodNote.setVisible(design.digestionOptions.isVisible()
        && design.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    design.enrichmentLabel.setVisible(!readOnly && service == LC_MS_MS);
    design.exclusionsLabel.setVisible(!readOnly && service == LC_MS_MS);
    design.injectionTypeOptions.setVisible(service == INTACT_PROTEIN);
    design.sourceOptions.setVisible(service == INTACT_PROTEIN);
    design.proteinContentOptions.setVisible(service == LC_MS_MS);
    design.instrumentOptions.setVisible(service != SMALL_MOLECULE);
    design.proteinIdentificationOptions.setVisible(service == LC_MS_MS);
    design.proteinIdentificationLinkField.setVisible(design.proteinIdentificationOptions.isVisible()
        && design.proteinIdentificationOptions.getValue() == ProteinIdentification.OTHER);
    design.quantificationOptions.setVisible(service == LC_MS_MS);
    design.quantificationLabelsField.setVisible(service == LC_MS_MS);
    design.highResolutionOptions.setVisible(service == SMALL_MOLECULE);
    design.solventsLayout.setVisible(service == SMALL_MOLECULE);
    design.acetonitrileSolventsField.setVisible(service == SMALL_MOLECULE);
    design.methanolSolventsField.setVisible(service == SMALL_MOLECULE);
    design.chclSolventsField.setVisible(service == SMALL_MOLECULE);
    design.otherSolventsField.setVisible(service == SMALL_MOLECULE);
    design.otherSolventField
        .setVisible(service == SMALL_MOLECULE && design.otherSolventsField.getValue());
    design.otherSolventNoteLabel
        .setVisible(service == SMALL_MOLECULE && design.otherSolventsField.getValue());
    view.filesUploader.setVisible(!readOnly);
    design.buttonsLayout.setVisible(!readOnly);
  }

  private void updateReadOnly() {
    design.serviceOptions.setReadOnly(readOnly);
    design.sampleSupportOptions.setReadOnly(readOnly);
    design.solutionSolventField.setReadOnly(readOnly);
    design.sampleNameField.setReadOnly(readOnly);
    design.formulaField.setReadOnly(readOnly);
    design.monoisotopicMassField.setReadOnly(readOnly);
    design.averageMassField.setReadOnly(readOnly);
    design.toxicityField.setReadOnly(readOnly);
    design.lightSensitiveField.setReadOnly(readOnly);
    design.storageTemperatureOptions.setReadOnly(readOnly);
    design.sampleContainerTypeOptions.setReadOnly(readOnly);
    design.plateNameField.setReadOnly(readOnly);
    design.sampleCountField.setReadOnly(readOnly);
    sampleBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.fillSamplesButton.setVisible(!readOnly);
    view.plateComponent.setReadOnly(readOnly);
    design.experienceField.setReadOnly(readOnly);
    design.experienceGoalField.setReadOnly(readOnly);
    design.taxonomyField.setReadOnly(readOnly);
    design.proteinNameField.setReadOnly(readOnly);
    design.proteinWeightField.setReadOnly(readOnly);
    design.postTranslationModificationField.setReadOnly(readOnly);
    design.sampleQuantityField.setReadOnly(readOnly);
    design.sampleVolumeField.setReadOnly(readOnly);
    design.standardCountField.setReadOnly(readOnly);
    standardBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.contaminantCountField.setReadOnly(readOnly);
    contaminantBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.separationField.setReadOnly(readOnly);
    design.thicknessField.setReadOnly(readOnly);
    design.colorationField.setReadOnly(readOnly);
    design.otherColorationField.setReadOnly(readOnly);
    design.developmentTimeField.setReadOnly(readOnly);
    design.decolorationField.setReadOnly(readOnly);
    design.weightMarkerQuantityField.setReadOnly(readOnly);
    design.proteinQuantityField.setReadOnly(readOnly);
    design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).setHidden(readOnly);
    design.digestionOptions.setReadOnly(readOnly);
    design.usedProteolyticDigestionMethodField.setReadOnly(readOnly);
    design.otherProteolyticDigestionMethodField.setReadOnly(readOnly);
    design.injectionTypeOptions.setReadOnly(readOnly);
    design.sourceOptions.setReadOnly(readOnly);
    design.proteinContentOptions.setReadOnly(readOnly);
    design.instrumentOptions.setReadOnly(readOnly);
    design.proteinIdentificationOptions.setReadOnly(readOnly);
    design.proteinIdentificationLinkField.setReadOnly(readOnly);
    design.quantificationOptions.setReadOnly(readOnly);
    design.quantificationLabelsField.setReadOnly(readOnly);
    design.highResolutionOptions.setReadOnly(readOnly);
    design.acetonitrileSolventsField.setReadOnly(readOnly);
    design.methanolSolventsField.setReadOnly(readOnly);
    design.chclSolventsField.setReadOnly(readOnly);
    design.otherSolventsField.setReadOnly(readOnly);
    design.otherSolventField.setReadOnly(readOnly);
    design.commentField.setReadOnly(readOnly);
    design.filesGrid.getColumn(REMOVE_FILE).setHidden(readOnly);
  }

  private void updateSampleCount(String countValue) {
    if (sampleCountBinder.isValid()) {
      int count;
      try {
        count = Math.max(Integer.parseInt(countValue), 1);
      } catch (NumberFormatException e) {
        count = 1;
      }
      while (count > samplesDataProvider.getItems().size()) {
        SubmissionSample sample = new SubmissionSample();
        sample.setNumberProtein(1);
        samplesDataProvider.getItems().add(sample);
      }
      while (count < samplesDataProvider.getItems().size()) {
        SubmissionSample remove = samplesDataProvider.getItems().stream()
            .skip(samplesDataProvider.getItems().size() - 1).findFirst().orElse(null);
        samplesDataProvider.getItems().remove(remove);
      }
      samplesDataProvider.refreshAll();
    }
  }

  private void updateStandardsTable(String countValue) {
    if (standardCountBinder.isValid()) {
      int count;
      try {
        count = Math.max(Integer.parseInt(countValue), 0);
      } catch (NumberFormatException e) {
        count = 0;
      }
      while (standardsDataProvider.getItems().size() > count) {
        Standard remove = standardsDataProvider.getItems().stream()
            .skip(standardsDataProvider.getItems().size() - 1).findFirst().orElse(null);
        standardsDataProvider.getItems().remove(remove);
      }
      while (standardsDataProvider.getItems().size() < count) {
        standardsDataProvider.getItems().add(new Standard());
      }
      design.standardsTableLayout.setVisible(count > 0);
      standardsDataProvider.refreshAll();
    }
  }

  private void updateContaminantsTable(String countValue) {
    if (contaminantCountBinder.isValid()) {
      int count;
      try {
        count = Math.max(Integer.parseInt(countValue), 0);
      } catch (NumberFormatException e) {
        count = 0;
      }
      while (contaminantsDataProvider.getItems().size() > count) {
        Contaminant remove = contaminantsDataProvider.getItems().stream()
            .skip(contaminantsDataProvider.getItems().size() - 1).findFirst().orElse(null);
        contaminantsDataProvider.getItems().remove(remove);
      }
      while (contaminantsDataProvider.getItems().size() < count) {
        contaminantsDataProvider.getItems().add(new Contaminant());
      }
      design.contaminantsTableLayout.setVisible(count > 0);
      contaminantsDataProvider.refreshAll();
    }
  }

  private void fillSamples() {
    SubmissionSample first = samplesDataProvider.getItems().iterator().next();
    String name = first.getName();
    samplesDataProvider.getItems().forEach(sample -> sample.setName(incrementLastNumber(name)));
    samplesDataProvider.refreshAll();
  }

  private void fillStandards() {
    Standard first = standardsDataProvider.getItems().iterator().next();
    String name = first.getName();
    String quantity = first.getQuantity();
    String comment = first.getComment();
    standardsDataProvider.getItems().forEach(standard -> {
      standard.setName(name);
      standard.setQuantity(quantity);
      standard.setComment(comment);
    });
    standardsDataProvider.refreshAll();
  }

  private void fillContaminants() {
    Contaminant first = contaminantsDataProvider.getItems().iterator().next();
    String name = first.getName();
    String quantity = first.getQuantity();
    String comment = first.getComment();
    contaminantsDataProvider.getItems().forEach(contaminant -> {
      contaminant.setName(name);
      contaminant.setQuantity(quantity);
      contaminant.setComment(comment);
    });
    contaminantsDataProvider.refreshAll();
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

  private void updateStructureButton(Structure structure) {
    design.structureButton.getExtensions().stream().collect(Collectors.toList())
        .forEach(e -> e.remove());
    if (structure != null) {
      design.structureButton.setCaption(structure.getFilename());
      StreamResource resource = new StreamResource(
          () -> new ByteArrayInputStream(structure.getContent()), structure.getFilename());
      FileDownloader fileDownloader = new FileDownloader(resource);
      fileDownloader.extend(design.structureButton);
    } else {
      design.structureButton.setCaption("");
    }
  }

  private MultiFileUploadFileHandler gelImageFileHandler() {
    return (file, fileName, mimetype, length) -> {
      if (gelImagesDataProvider.getItems().size() >= MAXIMUM_GEL_IMAGES_COUNT) {
        return;
      }

      ByteArrayOutputStream content = new ByteArrayOutputStream();
      try {
        Files.copy(file.toPath(), content);
      } catch (IOException e) {
        MessageResource resources = view.getResources();
        view.showError(resources.message(GEL_IMAGES_PROPERTY + ".error", fileName));
        return;
      }

      GelImage gelImage = new GelImage();
      gelImage.setFilename(fileName);
      gelImage.setContent(content.toByteArray());
      gelImagesDataProvider.getItems().add(gelImage);
      gelImagesDataProvider.refreshAll();
      warnIfGelImageAtMaximum();
    };
  }

  private void warnIfGelImageAtMaximum() {
    if (gelImagesDataProvider.getItems().size() >= MAXIMUM_GEL_IMAGES_COUNT) {
      MessageResource resources = view.getResources();
      view.showWarning(
          resources.message(GEL_IMAGES_PROPERTY + ".overMaximumCount", MAXIMUM_GEL_IMAGES_COUNT));
    }
  }

  private MultiFileUploadFileHandler fileHandler() {
    return (file, fileName, mimetype, length) -> {
      if (filesDataProvider.getItems().size() >= MAXIMUM_GEL_IMAGES_COUNT) {
        return;
      }

      ByteArrayOutputStream content = new ByteArrayOutputStream();
      try {
        Files.copy(file.toPath(), content);
      } catch (IOException e) {
        MessageResource resources = view.getResources();
        view.showError(resources.message(FILES_PROPERTY + ".error", fileName));
        return;
      }

      SubmissionFile submissionFile = new SubmissionFile();
      submissionFile.setFilename(fileName);
      submissionFile.setContent(content.toByteArray());
      filesDataProvider.getItems().add(submissionFile);
      filesDataProvider.refreshAll();
      warnIfFilesAtMaximum();
    };
  }

  private void warnIfFilesAtMaximum() {
    if (filesDataProvider.getItems().size() >= MAXIMUM_FILES_COUNT) {
      MessageResource resources = view.getResources();
      view.showWarning(
          resources.message(FILES_PROPERTY + ".overMaximumCount", MAXIMUM_FILES_COUNT));
    }
  }

  private Validator<String> validateSampleName(boolean testExists) {
    return (value, context) -> {
      if (value == null || value.isEmpty()) {
        return ValidationResult.ok();
      }
      MessageResource generalResources = view.getGeneralResources();
      if (!Pattern.matches("\\w*", value)) {
        return ValidationResult.error(generalResources.message(ONLY_WORDS));
      }
      if (testExists && submissionSampleService.exists(value)) {
        return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> validatePlateName() {
    return (value, context) -> {
      if (value == null || value.isEmpty()) {
        return ValidationResult.ok();
      }
      MessageResource generalResources = view.getGeneralResources();
      if (!plateService.nameAvailable(value)) {
        return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
      }
      return ValidationResult.ok();
    };
  }

  private List<String> plateSampleNames() {
    return view.plateComponent.getValue().getWells().stream()
        .filter(well -> well.getSample() != null).map(well -> well.getSample().getName())
        .filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());
  }

  private boolean validate() {
    logger.trace("Validate submission");
    boolean valid = true;
    valid &= validate(submissionBinder);
    valid &= validate(firstSampleBinder);
    valid &= validate(() -> validateExplanation());
    Submission submission = submissionBinder.getBean();
    SubmissionSample sample = firstSampleBinder.getBean();
    if (submission.getService() == LC_MS_MS || submission.getService() == INTACT_PROTEIN) {
      valid &= validate(sampleCountBinder);
      if (design.sampleContainerTypeOptions.getValue() != WELL) {
        for (SubmissionSample samp : samplesDataProvider.getItems()) {
          valid &= validate(sampleBinders.get(samp));
        }
      } else {
        valid &= validate(plateBinder);
        valid &= validate(validateSampleName(false), view.plateComponent, plateSampleNames());
      }
      valid &= validate(() -> validateSampleNames());
      if (sample.getSupport() == DRY || sample.getSupport() == SOLUTION) {
        valid &= validate(standardCountBinder);
        for (Standard standard : standardsDataProvider.getItems()) {
          valid &= validate(standardBinders.get(standard));
        }
        valid &= validate(contaminantCountBinder);
        for (Contaminant contaminant : contaminantsDataProvider.getItems()) {
          valid &= validate(contaminantBinders.get(contaminant));
        }
      } else if (sample.getSupport() == GEL) {
        valid &= validate(() -> validateGelImages(submission));
      }
    } else if (submission.getService() == Service.SMALL_MOLECULE) {
      valid &= validate(() -> validateStructure(submission));
      valid &= validate(() -> validateSolvents());
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Submission field validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    return valid;
  }

  private boolean validate(Supplier<ValidationResult> validator) {
    ValidationResult result = validator.get();
    if (result.isError()) {
      logger.trace("Validation error {}", result.getErrorMessage());
      return false;
    } else {
      return true;
    }
  }

  private ValidationResult validateSampleNames() {
    view.plateComponent.setComponentError(null);
    MessageResource resources = view.getResources();
    Set<String> names = new HashSet<>();
    if (design.sampleContainerTypeOptions.getValue() != WELL) {
      for (SubmissionSample sample : samplesDataProvider.getItems()) {
        if (!names.add(sample.getName())) {
          String error = resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sample.getName());
          sampleNameFields.get(sample).setComponentError(new UserError(error));
          return ValidationResult.error(error);
        }
      }
    } else {
      int count = 0;
      List<String> plateSampleNames = plateSampleNames();
      for (String name : plateSampleNames) {
        count++;
        if (!names.add(name)) {
          String error = resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", name);
          view.plateComponent.setComponentError(new UserError(error));
          return ValidationResult.error(error);
        }
      }
      if (count < sampleCountBinder.getBean().getCount()) {
        String error = resources.message(SAMPLES_PROPERTY + ".missing",
            sampleCountBinder.getBean().getCount());
        view.plateComponent.setComponentError(new UserError(error));
        return ValidationResult.error(error);
      }
    }
    return ValidationResult.ok();

  }

  private ValidationResult validateStructure(Submission submission) {
    design.structureLayout.setComponentError(null);
    if (submission.getStructure() == null || submission.getStructure().getFilename() == null) {
      MessageResource resources = view.getResources();
      String error = resources.message(STRUCTURE_PROPERTY + "." + REQUIRED);
      design.structureLayout.setComponentError(new UserError(error));
      return ValidationResult.error(error);
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateGelImages(Submission submission) {
    design.gelImagesLayout.setComponentError(null);
    if (gelImagesDataProvider.getItems().size() == 0) {
      MessageResource resources = view.getResources();
      String error = resources.message(GEL_IMAGES_PROPERTY + "." + REQUIRED);
      design.gelImagesLayout.setComponentError(new UserError(error));
      return ValidationResult.error(error);
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateSolvents() {
    design.solventsLayout.setComponentError(null);
    if (!design.acetonitrileSolventsField.getValue() && !design.methanolSolventsField.getValue()
        && !design.chclSolventsField.getValue() && !design.otherSolventsField.getValue()) {
      MessageResource resources = view.getResources();
      String error = resources.message(SOLVENTS_PROPERTY + "." + REQUIRED);
      design.solventsLayout.setComponentError(new UserError(error));
      return ValidationResult.error(error);
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateExplanation() {
    design.explanation.setComponentError(null);
    if (design.explanationPanel.isVisible() && design.explanation.getValue().isEmpty()) {
      MessageResource generalResources = view.getGeneralResources();
      String error = generalResources.message(REQUIRED);
      design.explanation.setComponentError(new UserError(error));
      return ValidationResult.error(error);
    }
    return ValidationResult.ok();
  }

  private <V> void setValueIfInvisible(HasValue<V> field, V value) {
    if (!((Component) field).isVisible()) {
      field.setValue(value);
    }
  }

  private <V> void clearInvisibleField(HasValue<V> field) {
    setValueIfInvisible(field, field.getEmptyValue());
  }

  private void clearInvisibleFields() {
    clearInvisibleField(design.solutionSolventField);
    clearInvisibleField(design.sampleNameField);
    clearInvisibleField(design.formulaField);
    if (!design.structureLayout.isVisible()) {
      submissionBinder.getBean().setStructure(null);
    }
    clearInvisibleField(design.monoisotopicMassField);
    clearInvisibleField(design.averageMassField);
    clearInvisibleField(design.toxicityField);
    clearInvisibleField(design.lightSensitiveField);
    clearInvisibleField(design.storageTemperatureOptions);
    clearInvisibleField(design.sampleCountField);
    clearInvisibleField(design.sampleContainerTypeOptions);
    clearInvisibleField(design.plateNameField);
    if (!design.samplesGrid.isVisible()) {
      sampleNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleNumberProteinFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleMolecularWeightFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.experienceField);
    clearInvisibleField(design.experienceGoalField);
    clearInvisibleField(design.taxonomyField);
    clearInvisibleField(design.proteinNameField);
    clearInvisibleField(design.proteinWeightField);
    clearInvisibleField(design.postTranslationModificationField);
    clearInvisibleField(design.sampleQuantityField);
    clearInvisibleField(design.sampleVolumeField);
    clearInvisibleField(design.standardCountField);
    if (!design.standardsGrid.isVisible()) {
      standardNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardCommentFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.contaminantCountField);
    if (!design.contaminantsGrid.isVisible()) {
      contaminantNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantCommentFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.separationField);
    clearInvisibleField(design.thicknessField);
    clearInvisibleField(design.colorationField);
    clearInvisibleField(design.otherColorationField);
    clearInvisibleField(design.developmentTimeField);
    clearInvisibleField(design.decolorationField);
    clearInvisibleField(design.weightMarkerQuantityField);
    clearInvisibleField(design.proteinQuantityField);
    if (!design.gelImagesLayout.isVisible()) {
      gelImagesDataProvider.getItems().clear();
      gelImagesDataProvider.refreshAll();
    }
    clearInvisibleField(design.digestionOptions);
    clearInvisibleField(design.usedProteolyticDigestionMethodField);
    clearInvisibleField(design.otherProteolyticDigestionMethodField);
    clearInvisibleField(design.injectionTypeOptions);
    clearInvisibleField(design.sourceOptions);
    clearInvisibleField(design.proteinContentOptions);
    clearInvisibleField(design.instrumentOptions);
    clearInvisibleField(design.proteinIdentificationOptions);
    clearInvisibleField(design.proteinIdentificationLinkField);
    clearInvisibleField(design.quantificationOptions);
    clearInvisibleField(design.quantificationLabelsField);
    setValueIfInvisible(design.highResolutionOptions, false);
    clearInvisibleField(design.acetonitrileSolventsField);
    clearInvisibleField(design.methanolSolventsField);
    clearInvisibleField(design.chclSolventsField);
    clearInvisibleField(design.otherSolventsField);
    clearInvisibleField(design.otherSolventField);
  }

  private void save() {
    clearInvisibleFields();
    if (validate()) {
      Submission submission = submissionBinder.getBean();
      SubmissionSample firstSample = firstSampleBinder.getBean();
      if (submission.getService() == LC_MS_MS) {
        copySamplesToSubmission(submission);
      } else {
        submission.setProteolyticDigestionMethod(null);
        submission.setProteinContent(null);
        submission.setProteinIdentification(null);
      }
      if (submission.getService() == SMALL_MOLECULE) {
        submission.setMassDetectionInstrument(null);
        submission.setExperience(firstSample.getName());
        submission.setSamples(Arrays.asList(firstSample));
        submission.setSolvents(new ArrayList<>());
        firstSample.setOriginalContainer(null);
        firstSample.setNumberProtein(null);
        if (design.acetonitrileSolventsField.getValue()) {
          submission.getSolvents().add(new SampleSolvent(ACETONITRILE));
        }
        if (design.methanolSolventsField.getValue()) {
          submission.getSolvents().add(new SampleSolvent(METHANOL));
        }
        if (design.chclSolventsField.getValue()) {
          submission.getSolvents().add(new SampleSolvent(CHCL3));
        }
        if (design.otherSolventsField.getValue()) {
          submission.getSolvents().add(new SampleSolvent(Solvent.OTHER));
        }
      } else {
        submission.setStructure(null);
        submission.setStorageTemperature(null);
      }
      if (submission.getService() == INTACT_PROTEIN) {
        copySamplesToSubmission(submission);
      } else {
        submission.setSource(null);
      }
      if (firstSample.getSupport() == GEL) {
        submission.setGelImages(new ArrayList<>(gelImagesDataProvider.getItems()));
      } else {
        submission.setSeparation(null);
        submission.setThickness(null);
      }
      submission.setFiles(new ArrayList<>(filesDataProvider.getItems()));
      logger.debug("Save submission {}", submission);
      final MessageResource resources = view.getResources();
      if (submission.getId() != null) {
        try {
          if (design.explanationPanel.isVisible()) {
            submissionService.forceUpdate(submission, design.explanation.getValue());
          } else {
            submissionService.update(submission);
          }
        } catch (PersistenceException e) {
          view.showError(resources.message(UPDATE_ERROR, submission.getExperience()));
        }
      } else {
        submissionService.insert(submission);
      }
      view.showTrayNotification(resources.message(SAVE + ".done", submission.getExperience()));
      view.navigateTo(SubmissionsView.VIEW_NAME);
    }
  }

  private void copySamplesToSubmission(Submission submission) {
    if (submission.getService() == LC_MS_MS
        && design.sampleContainerTypeOptions.getValue() == WELL) {
      submission.setSamples(samplesFromPlate(submission));
    } else {
      submission.setSamples(samplesFromTable(submission));
    }
  }

  private List<SubmissionSample> samplesFromTable(Submission submission) {
    List<SubmissionSample> samples = new ArrayList<>();
    SubmissionSample firstSample = firstSampleBinder.getBean();
    for (SubmissionSample sample : samplesDataProvider.getItems()) {
      sample.setSupport(firstSample.getSupport());
      sample.setQuantity(firstSample.getQuantity());
      sample.setVolume(firstSample.getVolume());
      if (submission.getService() != INTACT_PROTEIN) {
        sample.setNumberProtein(null);
        sample.setMolecularWeight(firstSample.getMolecularWeight());
      }
      if (firstSample.getSupport() != GEL) {
        copyStandardsFromTableToSample(sample);
        copyContaminantsFromTableToSample(sample);
      }
      samples.add(sample);
    }
    return samples;
  }

  private List<SubmissionSample> samplesFromPlate(Submission submission) {
    List<SubmissionSample> samples = new ArrayList<>();
    SubmissionSample firstSample = firstSampleBinder.getBean();
    view.plateComponent.getValue().getWells().forEach(well -> {
      if (well.getSample() != null && well.getSample() instanceof SubmissionSample
          && well.getSample().getName() != null && !well.getSample().getName().isEmpty()) {
        SubmissionSample sample = (SubmissionSample) well.getSample();
        sample.setSupport(firstSample.getSupport());
        sample.setQuantity(firstSample.getQuantity());
        sample.setVolume(firstSample.getVolume());
        sample.setNumberProtein(null);
        sample.setMolecularWeight(firstSample.getMolecularWeight());
        if (firstSample.getSupport() != GEL) {
          copyStandardsFromTableToSample(sample);
          copyContaminantsFromTableToSample(sample);
        }
        sample.setOriginalContainer(well);
        samples.add(sample);
        well.getPlate().setName(plateBinder.getBean().getName());
      }
    });
    return samples;
  }

  private void copyStandardsFromTableToSample(SubmissionSample sample) {
    sample.setStandards(new ArrayList<>());
    for (Standard standard : standardsDataProvider.getItems()) {
      Standard copy = new Standard();
      copy.setName(standard.getName());
      copy.setQuantity(standard.getQuantity());
      copy.setComment(standard.getComment());
      sample.getStandards().add(copy);
    }
  }

  private void copyContaminantsFromTableToSample(SubmissionSample sample) {
    sample.setContaminants(new ArrayList<>());
    for (Contaminant contaminant : contaminantsDataProvider.getItems()) {
      Contaminant copy = new Contaminant();
      copy.setName(contaminant.getName());
      copy.setQuantity(contaminant.getQuantity());
      copy.setComment(contaminant.getComment());
      sample.getContaminants().add(copy);
    }
  }

  Submission getValue() {
    return submissionBinder.getBean();
  }

  void setValue(Submission submission) {
    if (submission == null) {
      submission = new Submission();
      submission.setService(LC_MS_MS);
      submission.setSamples(new ArrayList<>());
      submission.setStorageTemperature(StorageTemperature.MEDIUM);
      submission.setSeparation(ONE_DIMENSION);
      submission.setThickness(ONE);
      submission.setProteolyticDigestionMethod(TRYPSIN);
      submission.setInjectionType(InjectionType.LC_MS);
      submission.setSource(ESI);
      submission.setProteinContent(ProteinContent.SMALL);
      submission.setProteinIdentification(REFSEQ);
    }
    List<SubmissionSample> samples = submission.getSamples();
    if (samples == null) {
      samples = new ArrayList<>();
    }
    SubmissionSample firstSample;
    if (samples.isEmpty()) {
      firstSample = new SubmissionSample();
      firstSample.setSupport(SOLUTION);
      firstSample.setOriginalContainer(new Tube());
      samples.add(new SubmissionSample());
      samples.get(0).setNumberProtein(1);
    } else {
      SubmissionSample original = submission.getSamples().get(0);
      firstSample = new SubmissionSample();
      firstSample.setContaminants(original.getContaminants());
      firstSample.setId(original.getId());
      firstSample.setMolecularWeight(original.getMolecularWeight());
      firstSample.setName(original.getName());
      firstSample.setNumberProtein(original.getNumberProtein());
      firstSample.setOriginalContainer(original.getOriginalContainer());
      firstSample.setQuantity(original.getQuantity());
      firstSample.setStandards(original.getStandards());
      firstSample.setSupport(original.getSupport());
      firstSample.setVolume(original.getVolume());
    }
    SampleContainer container = firstSample.getOriginalContainer();

    submissionBinder.setBean(submission);
    firstSampleBinder.setBean(firstSample);
    if (container instanceof Well) {
      Well containerAsWell = (Well) container;
      plateBinder.setBean(containerAsWell.getPlate());
      view.plateComponent.setValue(containerAsWell.getPlate());
    } else {
      Plate plate = new Plate();
      plate.initWells();
      plateBinder.setBean(plate);
      view.plateComponent.setValue(plate);
    }
    Service service = submission.getService();
    if (service != null && !service.available) {
      design.serviceOptions
          .setItems(Stream.concat(Service.availables().stream(), Stream.of(service)));
    }
    samplesDataProvider.getItems().clear();
    samplesDataProvider.getItems().addAll(samples);
    samplesDataProvider.refreshAll();
    design.sampleCountField.setReadOnly(false);
    sampleCountBinder.setBean(new ItemCount(samples.size()));
    design.sampleContainerTypeOptions.setReadOnly(false);
    design.sampleContainerTypeOptions.setValue(firstSample.getOriginalContainer().getType());
    Structure structure = submission.getStructure();
    updateStructureButton(structure);
    List<Standard> standards = firstSample.getStandards();
    if (standards == null) {
      standards = new ArrayList<>();
    }
    standardsDataProvider.getItems().clear();
    standardsDataProvider.getItems().addAll(standards);
    standardsDataProvider.refreshAll();
    design.standardCountField.setReadOnly(false);
    standardCountBinder.setBean(new ItemCount(standards.size()));
    List<Contaminant> contaminants = firstSample.getContaminants();
    if (contaminants == null) {
      contaminants = new ArrayList<>();
    }
    contaminantsDataProvider.getItems().clear();
    contaminantsDataProvider.getItems().addAll(contaminants);
    contaminantsDataProvider.refreshAll();
    design.contaminantCountField.setReadOnly(false);
    contaminantCountBinder.setBean(new ItemCount(contaminants.size()));
    List<GelImage> gelImages = submission.getGelImages();
    if (gelImages == null) {
      gelImages = new ArrayList<>();
    }
    gelImagesDataProvider.getItems().clear();
    gelImagesDataProvider.getItems().addAll(gelImages);
    gelImagesDataProvider.refreshAll();
    MassDetectionInstrumentSource source = submission.getSource();
    if (source != null && !source.available) {
      design.sourceOptions.setItems(
          Stream.concat(MassDetectionInstrumentSource.availables().stream(), Stream.of(source)));
    }
    MassDetectionInstrument instrument = submission.getMassDetectionInstrument();
    if (instrument != null && !instrument.userChoice) {
      design.instrumentOptions
          .setItems(Stream.concat(instrumentValues().stream(), Stream.of(instrument)));
    }
    ProteinIdentification proteinIdentification = submission.getProteinIdentification();
    if (proteinIdentification != null && !proteinIdentification.available) {
      design.proteinIdentificationOptions.setItems(Stream
          .concat(ProteinIdentification.availables().stream(), Stream.of(proteinIdentification)));
    }
    List<SampleSolvent> sampleSolvents = submission.getSolvents();
    if (sampleSolvents == null) {
      sampleSolvents = new ArrayList<>();
    }
    Set<Solvent> solvents =
        sampleSolvents.stream().map(ss -> ss.getSolvent()).collect(Collectors.toSet());
    design.acetonitrileSolventsField.setReadOnly(false);
    design.acetonitrileSolventsField.setValue(solvents.contains(Solvent.ACETONITRILE));
    design.acetonitrileSolventsField.setReadOnly(false);
    design.methanolSolventsField.setValue(solvents.contains(Solvent.METHANOL));
    design.acetonitrileSolventsField.setReadOnly(false);
    design.chclSolventsField.setValue(solvents.contains(Solvent.CHCL3));
    design.acetonitrileSolventsField.setReadOnly(false);
    design.otherSolventsField.setValue(solvents.contains(Solvent.OTHER));
    List<SubmissionFile> files = submission.getFiles();
    if (files == null) {
      files = new ArrayList<>();
    }
    filesDataProvider.getItems().clear();
    filesDataProvider.getItems().addAll(files);
    filesDataProvider.refreshAll();
    design.explanationPanel.setVisible(authorizationService.hasAdminRole() && samples.stream()
        .filter(
            sample -> sample.getStatus() != null && sample.getStatus() != SampleStatus.TO_APPROVE)
        .findAny().isPresent());
    updateVisible();
    updateReadOnly();
  }

  boolean isReadOnly() {
    return readOnly;
  }

  void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
    updateVisible();
    updateReadOnly();
  }

  private List<MassDetectionInstrument> instrumentValues() {
    return MassDetectionInstrument.userChoices();
  }

  private List<Quantification> quantificationValues() {
    return Arrays.asList(Quantification.values());
  }

  @SuppressWarnings("serial")
  private class StructureUploaderReceiver implements Receiver, SucceededListener, ProgressListener {
    private ByteArrayOutputStream output;

    @Override
    public void uploadSucceeded(SucceededEvent event) {
      String fileName = event.getFilename();
      logger.debug("Received structure file {}", fileName);

      Structure structure = submissionBinder.getBean().getStructure();
      if (structure == null) {
        structure = new Structure();
        submissionBinder.getBean().setStructure(structure);
      }
      structure.setFilename(fileName);
      structure.setContent(output.toByteArray());
      design.structureButton.setVisible(true);
      updateStructureButton(structure);
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
      output = new ByteArrayOutputStream();
      return output;
    }

    @Override
    public void updateProgress(long readBytes, long contentLength) {
      if (output != null && output.size() > MAXIMUM_STRUCTURE_SIZE) {
        view.structureUploader.interruptUpload();
        MessageResource generalResources = view.getGeneralResources();
        view.showError(
            generalResources.message(WebConstants.OVER_MAXIMUM_SIZE, MAXIMUM_STRUCTURE_SIZE));
      }
    }
  }

  private static class ItemCount {
    private Integer count;

    private ItemCount() {
    }

    private ItemCount(Integer count) {
      this.count = count;
    }

    public Integer getCount() {
      return count;
    }

    public void setCount(Integer count) {
      this.count = count;
    }
  }
}
