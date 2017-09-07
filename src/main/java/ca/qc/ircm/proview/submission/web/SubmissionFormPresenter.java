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
import static ca.qc.ircm.proview.sample.SampleContainerType.SPOT;
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
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Contaminant;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleContainerType;
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
import ca.qc.ircm.proview.web.data.NullableListDataProvider;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.addon.spreadsheet.Spreadsheet;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.inject.Inject;

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
  public static final String COMMENTS_PANEL = "commentsPanel";
  public static final String COMMENTS_PROPERTY = submission.comments.getMetadata().getName();
  public static final String FILES_PROPERTY = submission.files.getMetadata().getName();
  public static final String FILES_UPLOADER = submission.files.getMetadata().getName() + "Uploader";
  public static final String FILES_GRID = FILES_PROPERTY + "Grid";
  public static final int MAXIMUM_FILES_SIZE = 10 * 1024 * 1024; // 10MB
  public static final int MAXIMUM_FILES_COUNT = 4;
  public static final int FILES_TABLE_LENGTH = 3;
  public static final String FILE_FILENAME_PROPERTY =
      submissionFile.filename.getMetadata().getName();
  public static final String REMOVE_FILE = "removeFile";
  public static final String SUBMIT_ID = "submit";
  public static final int NULL_ID = -1;
  public static final String EXAMPLE = "example";
  public static final String FILL_BUTTON_STYLE = "skip-row";
  public static final String FORM_CAPTION_STYLE = "formcaption";
  public static final String CLICKABLE_STYLE = "clickable";
  public static final String HIDE_REQUIRED_STYLE = "hide-required";
  private static final int MAX_SAMPLE_COUNT = 200;
  private static final int MAX_STANDARD_COUNT = 10;
  private static final int MAX_CONTAMINANT_COUNT = 10;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenter.class);
  private SubmissionForm view;
  private boolean editable = false;
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
  private Map<Standard, TextField> standardCommentsFields = new HashMap<>();
  private ListDataProvider<Contaminant> contaminantsDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<Contaminant, Binder<Contaminant>> contaminantBinders = new HashMap<>();
  private Map<Contaminant, TextField> contaminantNameFields = new HashMap<>();
  private Map<Contaminant, TextField> contaminantQuantityFields = new HashMap<>();
  private Map<Contaminant, TextField> contaminantCommentsFields = new HashMap<>();
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

  protected SubmissionFormPresenter() {
  }

  protected SubmissionFormPresenter(SubmissionService submissionService,
      SubmissionSampleService submissionSampleService, PlateService plateService) {
    this.submissionService = submissionService;
    this.submissionSampleService = submissionSampleService;
    this.plateService = plateService;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionForm view) {
    this.view = view;
    view.createStructureUploader();
    StructureUploaderReceiver structureUploaderReceiver = new StructureUploaderReceiver();
    view.structureUploader.setReceiver(structureUploaderReceiver);
    view.structureUploader.addSucceededListener(structureUploaderReceiver);
    view.structureUploader.addProgressListener(structureUploaderReceiver);
    view.createGelImagesUploader(gelImageFileHandler());
    view.createFilesUploader(fileHandler());
    prepareComponents();
    setBean(null);
    addFieldListeners();
    updateVisible();
    updateEditable();
    updateSampleCount(view.sampleCountField.getValue());
    updateStandardsTable(view.standardCountField.getValue());
    updateContaminantsTable(view.contaminantCountField.getValue());
  }

  private void prepareComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.sampleTypeLabel.addStyleName(SAMPLE_TYPE_LABEL);
    view.sampleTypeLabel.setValue(resources.message(SAMPLE_TYPE_LABEL));
    view.inactiveLabel.addStyleName(INACTIVE_LABEL);
    view.inactiveLabel.setValue(resources.message(INACTIVE_LABEL));
    view.servicePanel.addStyleName(SERVICE_PANEL);
    view.servicePanel.addStyleName(REQUIRED);
    view.servicePanel.setCaption(resources.message(SERVICE_PROPERTY));
    view.serviceOptions.addStyleName(SERVICE_PROPERTY);
    view.serviceOptions.addStyleName(HIDE_REQUIRED_STYLE);
    view.serviceOptions.setItems(Service.availables());
    view.serviceOptions.setItemCaptionGenerator(service -> service.getLabel(locale));
    view.serviceOptions.setItemEnabledProvider(service -> service.available);
    view.serviceOptions
        .addValueChangeListener(e -> view.sampleSupportOptions.getDataProvider().refreshItem(GEL));
    submissionBinder.forField(view.serviceOptions).asRequired(generalResources.message(REQUIRED))
        .bind(SERVICE_PROPERTY);
    prepareSamplesComponents();
    prepareExperienceComponents();
    prepareStandardsComponents();
    prepareContaminantsComponents();
    prepareGelComponents();
    prepareServicesComponents();
    view.commentsPanel.setCaption(resources.message(COMMENTS_PANEL));
    view.commentsPanel.addStyleName(COMMENTS_PANEL);
    view.commentsField.addStyleName(COMMENTS_PROPERTY);
    submissionBinder.forField(view.commentsField).withNullRepresentation("")
        .bind(COMMENTS_PROPERTY);
    view.filesPanel.addStyleName(FILES_PROPERTY);
    view.filesPanel.setCaption(resources.message(FILES_PROPERTY));
    view.filesUploader.addStyleName(FILES_UPLOADER);
    view.filesUploader.setUploadButtonCaption(resources.message(FILES_UPLOADER));
    view.filesUploader.setMaxFileCount(1000000); // Count is required if size is set.
    view.filesUploader.setMaxFileSize(MAXIMUM_FILES_SIZE);
    view.filesGrid.addStyleName(FILES_GRID);
    view.filesGrid.addStyleName(COMPONENTS);
    view.filesGrid.addColumn(file -> downloadFileButton(file), new ComponentRenderer())
        .setId(FILE_FILENAME_PROPERTY)
        .setCaption(resources.message(FILES_PROPERTY + "." + FILE_FILENAME_PROPERTY));
    view.filesGrid.addColumn(file -> removeFileButton(file), new ComponentRenderer())
        .setId(REMOVE_FILE).setCaption(resources.message(FILES_PROPERTY + "." + REMOVE_FILE));
    view.filesGrid.setDataProvider(filesDataProvider);
    view.submitButton.addStyleName(SUBMIT_ID);
    view.submitButton.setCaption(resources.message(SUBMIT_ID));
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
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    view.sampleSupportOptions.addStyleName(SAMPLE_SUPPORT_PROPERTY);
    view.sampleSupportOptions.setCaption(resources.message(SAMPLE_SUPPORT_PROPERTY));
    view.sampleSupportOptions.setItems(SampleSupport.values());
    view.sampleSupportOptions.setItemCaptionGenerator(support -> support.getLabel(locale));
    firstSampleBinder.forField(view.sampleSupportOptions)
        .asRequired(generalResources.message(REQUIRED)).bind(SAMPLE_SUPPORT_PROPERTY);
    view.solutionSolventField.addStyleName(SOLUTION_SOLVENT_PROPERTY);
    view.solutionSolventField.setCaption(resources.message(SOLUTION_SOLVENT_PROPERTY));
    view.solutionSolventField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.solutionSolventField)
        .withValidator(requiredTextIfVisible(view.solutionSolventField)).withNullRepresentation("")
        .bind(SOLUTION_SOLVENT_PROPERTY);
    view.sampleCountField.addStyleName(SAMPLE_COUNT_PROPERTY);
    view.sampleCountField.setCaption(resources.message(SAMPLE_COUNT_PROPERTY));
    sampleCountBinder.forField(view.sampleCountField).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 1, MAX_SAMPLE_COUNT), 1, MAX_SAMPLE_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    view.sampleNameField.addStyleName(SAMPLE_NAME_PROPERTY);
    view.sampleNameField.setCaption(resources.message(SAMPLE_NAME_PROPERTY));
    view.sampleNameField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(view.sampleNameField)
        .withValidator(requiredTextIfVisible(view.sampleNameField)).withNullRepresentation("")
        .withValidator(validateSampleName(true)).bind(SAMPLE_NAME_PROPERTY);
    view.formulaField.addStyleName(FORMULA_PROPERTY);
    view.formulaField.setCaption(resources.message(FORMULA_PROPERTY));
    view.formulaField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.formulaField)
        .withValidator(requiredTextIfVisible(view.formulaField)).withNullRepresentation("")
        .bind(FORMULA_PROPERTY);
    view.structureLayout.addStyleName(REQUIRED);
    view.structureLayout.setCaption(resources.message(STRUCTURE_PROPERTY));
    view.structureButton.addStyleName(STRUCTURE_PROPERTY);
    view.structureButton.setVisible(false);
    view.structureUploader.addStyleName(STRUCTURE_UPLOADER);
    view.structureUploader.setButtonCaption(resources.message(STRUCTURE_UPLOADER));
    view.structureUploader.setImmediateMode(true);
    view.monoisotopicMassField.addStyleName(MONOISOTOPIC_MASS_PROPERTY);
    view.monoisotopicMassField.setCaption(resources.message(MONOISOTOPIC_MASS_PROPERTY));
    view.monoisotopicMassField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.monoisotopicMassField)
        .withValidator(requiredTextIfVisible(view.monoisotopicMassField)).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS_PROPERTY);
    view.averageMassField.addStyleName(AVERAGE_MASS_PROPERTY);
    view.averageMassField.setCaption(resources.message(AVERAGE_MASS_PROPERTY));
    submissionBinder.forField(view.averageMassField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(AVERAGE_MASS_PROPERTY);
    view.toxicityField.addStyleName(TOXICITY_PROPERTY);
    view.toxicityField.setCaption(resources.message(TOXICITY_PROPERTY));
    submissionBinder.forField(view.toxicityField).withNullRepresentation("")
        .bind(TOXICITY_PROPERTY);
    view.lightSensitiveField.addStyleName(LIGHT_SENSITIVE_PROPERTY);
    view.lightSensitiveField.setCaption(resources.message(LIGHT_SENSITIVE_PROPERTY));
    submissionBinder.forField(view.lightSensitiveField).bind(LIGHT_SENSITIVE_PROPERTY);
    view.storageTemperatureOptions.addStyleName(STORAGE_TEMPERATURE_PROPERTY);
    view.storageTemperatureOptions.setCaption(resources.message(STORAGE_TEMPERATURE_PROPERTY));
    view.storageTemperatureOptions.setItems(StorageTemperature.values());
    view.storageTemperatureOptions
        .setItemCaptionGenerator(storageTemperature -> storageTemperature.getLabel(locale));
    view.storageTemperatureOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.storageTemperatureOptions)
        .withValidator(requiredIfVisible(view.storageTemperatureOptions))
        .bind(STORAGE_TEMPERATURE_PROPERTY);
    view.sampleContainerTypeOptions.addStyleName(SAMPLES_CONTAINER_TYPE_PROPERTY);
    view.sampleContainerTypeOptions.setCaption(resources.message(SAMPLES_CONTAINER_TYPE_PROPERTY));
    view.sampleContainerTypeOptions.setItems(SampleContainerType.values());
    view.sampleContainerTypeOptions
        .setItemCaptionGenerator(sampleContainerType -> sampleContainerType.getLabel(locale));
    view.sampleContainerTypeOptions.setRequiredIndicatorVisible(true);
    view.plateNameField.addStyleName(PLATE_PROPERTY + "-" + PLATE_NAME_PROPERTY);
    view.plateNameField.setCaption(resources.message(PLATE_PROPERTY + "." + PLATE_NAME_PROPERTY));
    view.plateNameField.setRequiredIndicatorVisible(true);
    plateBinder.forField(view.plateNameField)
        .withValidator(requiredTextIfVisible(view.plateNameField)).withNullRepresentation("")
        .withValidator(validatePlateName()).bind(PLATE_NAME_PROPERTY);
    view.samplesLabel.addStyleName(SAMPLES_PROPERTY);
    view.samplesLabel.setCaption(resources.message(SAMPLES_PROPERTY));
    view.samplesGrid.addStyleName(SAMPLES_TABLE);
    view.samplesGrid.addStyleName(COMPONENTS);
    view.samplesGrid.setDataProvider(samplesDataProvider);
    view.samplesGrid.addColumn(sample -> sampleNameTextField(sample), new ComponentRenderer())
        .setId(SAMPLE_NAME_PROPERTY).setCaption(resources.message(SAMPLE_NAME_PROPERTY))
        .setWidth(230);
    view.samplesGrid
        .addColumn(sample -> sampleNumberProteinTextField(sample), new ComponentRenderer())
        .setId(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .setCaption(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY)).setWidth(230);
    view.samplesGrid.addColumn(sample -> proteinWeightTextField(sample), new ComponentRenderer())
        .setId(PROTEIN_WEIGHT_PROPERTY).setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY))
        .setWidth(230);
    view.fillSamplesButton.addStyleName(FILL_SAMPLES_PROPERTY);
    view.fillSamplesButton.addStyleName(FILL_BUTTON_STYLE);
    view.fillSamplesButton.setCaption(resources.message(FILL_SAMPLES_PROPERTY));
    view.samplesSpreadsheet.addStyleName(SAMPLES_PLATE);
    view.samplesSpreadsheet.setMaxColumns(13);
    view.samplesSpreadsheet.setMaxRows(9);
    IntStream.range(0, view.samplesSpreadsheet.getRows()).forEach(row -> {
      IntStream.range(0, view.samplesSpreadsheet.getColumns()).forEach(column -> {
        if (view.samplesSpreadsheet.getCell(row, column) == null) {
          view.samplesSpreadsheet.createCell(row, column, "");
        }
      });
    });
    view.samplesSpreadsheet.setSelection(1, 1);
    view.samplesSpreadsheet.getActiveSheet().getRow(0).getCell(0)
        .setCellValue(resources.message(SAMPLES_PLATE));
    view.samplesSpreadsheet.setFunctionBarVisible(false);
    view.samplesSpreadsheet.setSheetSelectionBarVisible(false);
    logger.debug("Sample name at plate {}-{} is {}", 0, 0,
        view.samplesSpreadsheet.getCellValue(view.samplesSpreadsheet.getCell(1, 1)));
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
      field.setReadOnly(!editable);
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
      field.setReadOnly(!editable);
      field.setRequiredIndicatorVisible(true);
      binder.forField(field)
          .withValidator(requiredTextIf(n -> view.serviceOptions.getValue() == INTACT_PROTEIN))
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
      field.setReadOnly(!editable);
      field.setRequiredIndicatorVisible(true);
      binder.forField(field)
          .withValidator(requiredTextIf(n -> view.serviceOptions.getValue() == INTACT_PROTEIN))
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
    view.experiencePanel.addStyleName(EXPERIENCE_PANEL);
    view.experiencePanel.setCaption(resources.message(EXPERIENCE_PANEL));
    view.experienceField.addStyleName(EXPERIENCE_PROPERTY);
    view.experienceField.setCaption(resources.message(EXPERIENCE_PROPERTY));
    view.experienceField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.experienceField)
        .withValidator(requiredTextIfVisible(view.experienceField)).withNullRepresentation("")
        .bind(EXPERIENCE_PROPERTY);
    view.experienceGoalField.addStyleName(EXPERIENCE_GOAL_PROPERTY);
    view.experienceGoalField.setCaption(resources.message(EXPERIENCE_GOAL_PROPERTY));
    submissionBinder.forField(view.experienceGoalField).withNullRepresentation("")
        .bind(EXPERIENCE_GOAL_PROPERTY);
    view.taxonomyField.addStyleName(TAXONOMY_PROPERTY);
    view.taxonomyField.setCaption(resources.message(TAXONOMY_PROPERTY));
    view.taxonomyField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.taxonomyField)
        .withValidator(requiredTextIfVisible(view.taxonomyField)).withNullRepresentation("")
        .bind(TAXONOMY_PROPERTY);
    view.proteinNameField.addStyleName(PROTEIN_NAME_PROPERTY);
    view.proteinNameField.setCaption(resources.message(PROTEIN_NAME_PROPERTY));
    submissionBinder.forField(view.proteinNameField).withNullRepresentation("")
        .bind(PROTEIN_NAME_PROPERTY);
    view.proteinWeightField.addStyleName(PROTEIN_WEIGHT_PROPERTY);
    view.proteinWeightField.setCaption(resources.message(PROTEIN_WEIGHT_PROPERTY));
    firstSampleBinder.forField(view.proteinWeightField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(PROTEIN_WEIGHT_PROPERTY);
    view.postTranslationModificationField.addStyleName(POST_TRANSLATION_MODIFICATION_PROPERTY);
    view.postTranslationModificationField
        .setCaption(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY));
    submissionBinder.forField(view.postTranslationModificationField).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION_PROPERTY);
    view.sampleQuantityField.addStyleName(SAMPLE_QUANTITY_PROPERTY);
    view.sampleQuantityField.setCaption(resources.message(SAMPLE_QUANTITY_PROPERTY));
    view.sampleQuantityField
        .setPlaceholder(resources.message(SAMPLE_QUANTITY_PROPERTY + "." + EXAMPLE));
    view.sampleQuantityField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(view.sampleQuantityField)
        .withValidator(requiredTextIfVisible(view.sampleQuantityField)).withNullRepresentation("")
        .bind(SAMPLE_QUANTITY_PROPERTY);
    view.sampleVolumeField.addStyleName(SAMPLE_VOLUME_PROPERTY);
    view.sampleVolumeField.setCaption(resources.message(SAMPLE_VOLUME_PROPERTY));
    view.sampleVolumeField.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(view.sampleVolumeField)
        .withValidator(requiredTextIfVisible(view.sampleVolumeField)).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(SAMPLE_VOLUME_PROPERTY);
  }

  private void prepareStandardsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.standardsPanel.addStyleName(STANDARDS_PANEL);
    view.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    view.standardCountField.addStyleName(STANDARD_COUNT_PROPERTY);
    view.standardCountField.setCaption(resources.message(STANDARD_COUNT_PROPERTY));
    standardCountBinder.forField(view.standardCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_STANDARD_COUNT), 0, MAX_STANDARD_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    view.standardsGrid.addStyleName(STANDARD_PROPERTY);
    view.standardsGrid.addStyleName(COMPONENTS);
    view.standardsGrid.setDataProvider(standardsDataProvider);
    view.standardsGrid
        .addColumn(standard -> standardNameTextField(standard), new ComponentRenderer())
        .setId(STANDARD_NAME_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_NAME_PROPERTY));
    view.standardsGrid
        .addColumn(standard -> standardQuantityTextField(standard), new ComponentRenderer())
        .setId(STANDARD_QUANTITY_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY));
    view.standardsGrid
        .addColumn(standard -> standardCommentsTextField(standard), new ComponentRenderer())
        .setId(STANDARD_COMMENTS_PROPERTY)
        .setCaption(resources.message(STANDARD_PROPERTY + "." + STANDARD_COMMENTS_PROPERTY));
    view.fillStandardsButton.addStyleName(FILL_STANDARDS_PROPERTY);
    view.fillStandardsButton.addStyleName(FILL_BUTTON_STYLE);
    view.fillStandardsButton.setCaption(resources.message(FILL_STANDARDS_PROPERTY));
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
      field.setReadOnly(!editable);
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
      field.setReadOnly(!editable);
      field.setPlaceholder(
          resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_QUANTITY_PROPERTY);
      standardBinders.put(standard, binder);
      standardQuantityFields.put(standard, field);
      return field;
    }
  }

  private TextField standardCommentsTextField(Standard standard) {
    if (standardCommentsFields.containsKey(standard)) {
      return standardCommentsFields.get(standard);
    } else {
      Binder<Standard> binder = standardBinders.get(standard);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Standard.class);
        binder.setBean(standard);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(!editable);
      binder.forField(field).withNullRepresentation("").bind(STANDARD_COMMENTS_PROPERTY);
      standardBinders.put(standard, binder);
      standardCommentsFields.put(standard, field);
      return field;
    }
  }

  private void prepareContaminantsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.contaminantsPanel.addStyleName(CONTAMINANTS_PANEL);
    view.contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL));
    view.contaminantCountField.addStyleName(CONTAMINANT_COUNT_PROPERTY);
    view.contaminantCountField.setCaption(resources.message(CONTAMINANT_COUNT_PROPERTY));
    contaminantCountBinder.forField(view.contaminantCountField).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_CONTAMINANT_COUNT), 0,
            MAX_CONTAMINANT_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    view.contaminantsGrid.addStyleName(CONTAMINANT_PROPERTY);
    view.contaminantsGrid.addStyleName(COMPONENTS);
    view.contaminantsGrid.setDataProvider(contaminantsDataProvider);
    view.contaminantsGrid.addColumn(contaminant -> contaminantNameTextField(contaminant))
        .setId(CONTAMINANT_NAME_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_NAME_PROPERTY));
    view.contaminantsGrid.addColumn(contaminant -> contaminantQuantityTextField(contaminant))
        .setId(CONTAMINANT_QUANTITY_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY));
    view.contaminantsGrid.addColumn(contaminant -> contaminantCommentsTextField(contaminant))
        .setId(CONTAMINANT_COMMENTS_PROPERTY)
        .setCaption(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_COMMENTS_PROPERTY));
    view.fillContaminantsButton.addStyleName(FILL_CONTAMINANTS_PROPERTY);
    view.fillContaminantsButton.addStyleName(FILL_BUTTON_STYLE);
    view.fillContaminantsButton.setCaption(resources.message(FILL_CONTAMINANTS_PROPERTY));
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
      field.setReadOnly(!editable);
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
      field.setReadOnly(!editable);
      field.setPlaceholder(resources
          .message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(CONTAMINANT_QUANTITY_PROPERTY);
      contaminantBinders.put(contaminant, binder);
      contaminantQuantityFields.put(contaminant, field);
      return field;
    }
  }

  private TextField contaminantCommentsTextField(Contaminant contaminant) {
    if (contaminantCommentsFields.containsKey(contaminant)) {
      return contaminantCommentsFields.get(contaminant);
    } else {
      Binder<Contaminant> binder = contaminantBinders.get(contaminant);
      if (binder == null) {
        binder = new BeanValidationBinder<>(Contaminant.class);
        binder.setBean(contaminant);
      }
      TextField field = new TextField();
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(!editable);
      binder.forField(field).withNullRepresentation("").bind(CONTAMINANT_COMMENTS_PROPERTY);
      contaminantBinders.put(contaminant, binder);
      contaminantCommentsFields.put(contaminant, field);
      return field;
    }
  }

  private void prepareGelComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    view.gelPanel.addStyleName(GEL_PANEL);
    view.gelPanel.setCaption(resources.message(GEL_PANEL));
    view.separationField.addStyleName(SEPARATION_PROPERTY);
    view.separationField.setCaption(resources.message(SEPARATION_PROPERTY));
    view.separationField.setEmptySelectionAllowed(false);
    view.separationField.setItems(GelSeparation.values());
    view.separationField.setItemCaptionGenerator(separation -> separation.getLabel(locale));
    view.separationField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.separationField)
        .withValidator(requiredIfVisible(view.separationField)).bind(SEPARATION_PROPERTY);
    view.thicknessField.addStyleName(THICKNESS_PROPERTY);
    view.thicknessField.setCaption(resources.message(THICKNESS_PROPERTY));
    view.thicknessField.setEmptySelectionAllowed(false);
    view.thicknessField.setItems(GelThickness.values());
    view.thicknessField.setItemCaptionGenerator(thickness -> thickness.getLabel(locale));
    view.thicknessField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.thicknessField)
        .withValidator(requiredIfVisible(view.thicknessField)).bind(THICKNESS_PROPERTY);
    view.colorationField.addStyleName(COLORATION_PROPERTY);
    view.colorationField.setCaption(resources.message(COLORATION_PROPERTY));
    view.colorationField.setEmptySelectionAllowed(true);
    view.colorationField.setEmptySelectionCaption(GelColoration.getNullLabel(locale));
    view.colorationField.setItems(GelColoration.values());
    view.colorationField.setItemCaptionGenerator(coloration -> coloration.getLabel(locale));
    submissionBinder.forField(view.colorationField).bind(COLORATION_PROPERTY);
    view.otherColorationField.addStyleName(OTHER_COLORATION_PROPERTY);
    view.otherColorationField.setCaption(resources.message(OTHER_COLORATION_PROPERTY));
    view.otherColorationField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.otherColorationField)
        .withValidator(requiredTextIfVisible(view.otherColorationField)).withNullRepresentation("")
        .bind(OTHER_COLORATION_PROPERTY);
    view.developmentTimeField.addStyleName(DEVELOPMENT_TIME_PROPERTY);
    view.developmentTimeField.setCaption(resources.message(DEVELOPMENT_TIME_PROPERTY));
    view.developmentTimeField
        .setPlaceholder(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(view.developmentTimeField).withNullRepresentation("")
        .bind(DEVELOPMENT_TIME_PROPERTY);
    view.decolorationField.addStyleName(DECOLORATION_PROPERTY);
    view.decolorationField.setCaption(resources.message(DECOLORATION_PROPERTY));
    submissionBinder.forField(view.decolorationField).bind(DECOLORATION_PROPERTY);
    view.weightMarkerQuantityField.addStyleName(WEIGHT_MARKER_QUANTITY_PROPERTY);
    view.weightMarkerQuantityField.setCaption(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY));
    view.weightMarkerQuantityField
        .setPlaceholder(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(view.weightMarkerQuantityField).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY_PROPERTY);
    view.proteinQuantityField.addStyleName(PROTEIN_QUANTITY_PROPERTY);
    view.proteinQuantityField.setCaption(resources.message(PROTEIN_QUANTITY_PROPERTY));
    view.proteinQuantityField
        .setPlaceholder(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(view.proteinQuantityField).withNullRepresentation("")
        .bind(PROTEIN_QUANTITY_PROPERTY);
    view.gelImagesLayout.addStyleName(REQUIRED);
    view.gelImagesLayout.setCaption(resources.message(GEL_IMAGES_PROPERTY));
    view.gelImagesUploader.addStyleName(GEL_IMAGES_PROPERTY);
    view.gelImagesUploader.setUploadButtonCaption(resources.message(GEL_IMAGES_UPLOADER));
    view.gelImagesUploader.setMaxFileCount(1000000); // Count is required if size is set.
    view.gelImagesUploader.setMaxFileSize(MAXIMUM_GEL_IMAGES_SIZE);
    view.gelImagesGrid.addStyleName(GEL_IMAGES_TABLE);
    view.gelImagesGrid.addStyleName(COMPONENTS);
    view.gelImagesGrid.addColumn(image -> downloadGelImageButton(image))
        .setId(GEL_IMAGE_FILENAME_PROPERTY)
        .setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + GEL_IMAGE_FILENAME_PROPERTY));
    view.gelImagesGrid.addColumn(image -> removeGelImageButton(image)).setId(REMOVE_GEL_IMAGE)
        .setCaption(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE));
    view.gelImagesGrid.setDataProvider(gelImagesDataProvider);
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
    view.servicesPanel.addStyleName(SERVICES_PANEL);
    view.servicesPanel.setCaption(resources.message(SERVICES_PANEL));
    view.digestionOptions.addStyleName(DIGESTION_PROPERTY);
    view.digestionOptions.setCaption(resources.message(DIGESTION_PROPERTY));
    view.digestionOptions.setItemCaptionGenerator(digestion -> digestion.getLabel(locale));
    view.digestionOptions.setItems(ProteolyticDigestion.values());
    view.digestionOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.digestionOptions)
        .withValidator(requiredIfVisible(view.digestionOptions)).bind(DIGESTION_PROPERTY);
    view.usedProteolyticDigestionMethodField.addStyleName(USED_DIGESTION_PROPERTY);
    view.usedProteolyticDigestionMethodField.setCaption(resources.message(USED_DIGESTION_PROPERTY));
    view.usedProteolyticDigestionMethodField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.usedProteolyticDigestionMethodField)
        .withValidator(requiredTextIfVisible(view.usedProteolyticDigestionMethodField))
        .withNullRepresentation("").bind(USED_DIGESTION_PROPERTY);
    view.otherProteolyticDigestionMethodField.addStyleName(OTHER_DIGESTION_PROPERTY);
    view.otherProteolyticDigestionMethodField
        .setCaption(resources.message(OTHER_DIGESTION_PROPERTY));
    view.otherProteolyticDigestionMethodField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.otherProteolyticDigestionMethodField)
        .withValidator(requiredTextIfVisible(view.otherProteolyticDigestionMethodField))
        .withNullRepresentation("").bind(OTHER_DIGESTION_PROPERTY);
    view.otherProteolyticDigestionMethodNote
        .setValue(resources.message(OTHER_DIGESTION_PROPERTY + ".note"));
    view.enrichmentLabel.addStyleName(ENRICHEMENT_PROPERTY);
    view.enrichmentLabel.setCaption(resources.message(ENRICHEMENT_PROPERTY));
    view.enrichmentLabel.setValue(resources.message(ENRICHEMENT_PROPERTY + ".value"));
    view.exclusionsLabel.addStyleName(EXCLUSIONS_PROPERTY);
    view.exclusionsLabel.setCaption(resources.message(EXCLUSIONS_PROPERTY));
    view.exclusionsLabel.setValue(resources.message(EXCLUSIONS_PROPERTY + ".value"));
    view.injectionTypeOptions.addStyleName(INJECTION_TYPE_PROPERTY);
    view.injectionTypeOptions.setCaption(resources.message(INJECTION_TYPE_PROPERTY));
    view.injectionTypeOptions.setItems(InjectionType.values());
    view.injectionTypeOptions
        .setItemCaptionGenerator(injectionType -> injectionType.getLabel(locale));
    view.injectionTypeOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.injectionTypeOptions)
        .withValidator(requiredIfVisible(view.injectionTypeOptions)).bind(INJECTION_TYPE_PROPERTY);
    view.sourceOptions.addStyleName(SOURCE_PROPERTY);
    view.sourceOptions.setCaption(resources.message(SOURCE_PROPERTY));
    view.sourceOptions.setItems(MassDetectionInstrumentSource.availables());
    view.sourceOptions.setItemCaptionGenerator(source -> source.getLabel(locale));
    view.sourceOptions.setItemEnabledProvider(source -> source.available);
    view.sourceOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.sourceOptions)
        .withValidator(requiredIfVisible(view.sourceOptions)).bind(SOURCE_PROPERTY);
    view.proteinContentOptions.addStyleName(PROTEIN_CONTENT_PROPERTY);
    view.proteinContentOptions.setCaption(resources.message(PROTEIN_CONTENT_PROPERTY));
    view.proteinContentOptions.setItems(ProteinContent.values());
    view.proteinContentOptions
        .setItemCaptionGenerator(proteinContent -> proteinContent.getLabel(locale));
    view.proteinContentOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.proteinContentOptions)
        .withValidator(requiredIfVisible(view.proteinContentOptions))
        .bind(PROTEIN_CONTENT_PROPERTY);
    view.instrumentOptions.addStyleName(INSTRUMENT_PROPERTY);
    view.instrumentOptions.setCaption(resources.message(INSTRUMENT_PROPERTY));
    view.instrumentOptions.setDataProvider(new NullableListDataProvider<>(instrumentValues()));
    view.instrumentOptions.setItemCaptionGenerator(instrument -> instrument != null
        ? instrument.getLabel(locale) : MassDetectionInstrument.getNullLabel(locale));
    view.instrumentOptions
        .setItemEnabledProvider(instrument -> instrument != null ? instrument.available : true);
    submissionBinder.forField(view.instrumentOptions).bind(INSTRUMENT_PROPERTY);
    view.proteinIdentificationOptions.addStyleName(PROTEIN_IDENTIFICATION_PROPERTY);
    view.proteinIdentificationOptions
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_PROPERTY));
    view.proteinIdentificationOptions.setItems(ProteinIdentification.availables());
    view.proteinIdentificationOptions
        .setItemCaptionGenerator(proteinIdentification -> proteinIdentification.getLabel(locale));
    view.proteinIdentificationOptions
        .setItemEnabledProvider(proteinIdentification -> proteinIdentification.available);
    view.proteinIdentificationOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.proteinIdentificationOptions)
        .withValidator(requiredIfVisible(view.proteinIdentificationOptions))
        .bind(PROTEIN_IDENTIFICATION_PROPERTY);
    view.proteinIdentificationLinkField.addStyleName(PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    view.proteinIdentificationLinkField
        .setCaption(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY));
    view.proteinIdentificationLinkField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.proteinIdentificationLinkField)
        .withValidator(requiredTextIfVisible(view.proteinIdentificationLinkField))
        .withNullRepresentation("").bind(PROTEIN_IDENTIFICATION_LINK_PROPERTY);
    view.quantificationOptions.addStyleName(QUANTIFICATION_PROPERTY);
    view.quantificationOptions.setCaption(resources.message(QUANTIFICATION_PROPERTY));
    view.quantificationOptions
        .setDataProvider(new NullableListDataProvider<>(quantificationValues()));
    view.quantificationOptions.setItemCaptionGenerator(quantification -> quantification != null
        ? quantification.getLabel(locale) : Quantification.getNullLabel(locale));
    submissionBinder.forField(view.quantificationOptions).bind(QUANTIFICATION_PROPERTY);
    view.quantificationLabelsField.addStyleName(QUANTIFICATION_LABELS_PROPERTY);
    view.quantificationLabelsField.setCaption(resources.message(QUANTIFICATION_LABELS_PROPERTY));
    view.quantificationLabelsField
        .setPlaceholder(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE));
    submissionBinder.forField(view.quantificationLabelsField).withValidator((value, context) -> {
      if (view.quantificationOptions.getValue() == SILAC && value.isEmpty()) {
        return ValidationResult.error(generalResources.message(REQUIRED));
      }
      return ValidationResult.ok();
    }).withNullRepresentation("").bind(QUANTIFICATION_LABELS_PROPERTY);
    view.highResolutionOptions.addStyleName(HIGH_RESOLUTION_PROPERTY);
    view.highResolutionOptions.setCaption(resources.message(HIGH_RESOLUTION_PROPERTY));
    view.highResolutionOptions.setItems(false, true);
    view.highResolutionOptions.setItemCaptionGenerator(
        value -> resources.message(HIGH_RESOLUTION_PROPERTY + "." + value));
    view.highResolutionOptions.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.highResolutionOptions)
        .withValidator(requiredIfVisible(view.highResolutionOptions))
        .bind(HIGH_RESOLUTION_PROPERTY);
    view.solventsLayout.addStyleName(REQUIRED);
    view.solventsLayout.setCaption(resources.message(SOLVENTS_PROPERTY));
    view.acetonitrileSolventsField
        .addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name());
    view.acetonitrileSolventsField.setCaption(Solvent.ACETONITRILE.getLabel(locale));
    view.methanolSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name());
    view.methanolSolventsField.setCaption(Solvent.METHANOL.getLabel(locale));
    view.chclSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name());
    view.chclSolventsField.setCaption(Solvent.CHCL3.getLabel(locale));
    view.chclSolventsField.setCaptionAsHtml(true);
    view.otherSolventsField.addStyleName(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name());
    view.otherSolventsField.setCaption(Solvent.OTHER.getLabel(locale));
    view.otherSolventField.setCaption(resources.message(OTHER_SOLVENT_PROPERTY));
    view.otherSolventField.addStyleName(OTHER_SOLVENT_PROPERTY);
    view.otherSolventField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    view.otherSolventField.setRequiredIndicatorVisible(true);
    submissionBinder.forField(view.otherSolventField)
        .withValidator(requiredTextIfVisible(view.otherSolventField)).withNullRepresentation("")
        .bind(OTHER_SOLVENT_PROPERTY);
    view.otherSolventNoteLabel.addStyleName(OTHER_SOLVENT_NOTE);
    view.otherSolventNoteLabel.addStyleName(FORM_CAPTION_STYLE);
    view.otherSolventNoteLabel.setValue(resources.message(OTHER_SOLVENT_NOTE));
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
    view.serviceOptions.addValueChangeListener(e -> updateVisible());
    view.sampleSupportOptions.addValueChangeListener(e -> updateVisible());
    view.sampleCountField
        .addValueChangeListener(e -> updateSampleCount(view.sampleCountField.getValue()));
    view.sampleCountField.addValueChangeListener(e -> updateSampleCount(e.getValue()));
    view.sampleContainerTypeOptions.addValueChangeListener(e -> updateVisible());
    view.fillSamplesButton.addClickListener(e -> fillSamples());
    view.standardCountField
        .addValueChangeListener(e -> updateStandardsTable(view.standardCountField.getValue()));
    view.standardCountField.addValueChangeListener(e -> updateStandardsTable(e.getValue()));
    view.fillStandardsButton.addClickListener(e -> fillStandards());
    view.contaminantCountField.addValueChangeListener(
        e -> updateContaminantsTable(view.contaminantCountField.getValue()));
    view.contaminantCountField.addValueChangeListener(e -> updateContaminantsTable(e.getValue()));
    view.fillContaminantsButton.addClickListener(e -> fillContaminants());
    view.colorationField.addValueChangeListener(e -> updateVisible());
    view.digestionOptions.addValueChangeListener(e -> updateVisible());
    view.proteinIdentificationOptions.addValueChangeListener(e -> updateVisible());
    view.quantificationOptions.addValueChangeListener(e -> view.quantificationLabelsField
        .setRequiredIndicatorVisible(view.quantificationOptions.getValue() == SILAC));
    view.otherSolventsField.addValueChangeListener(e -> updateVisible());
    view.submitButton.addClickListener(e -> saveSubmission());
  }

  private void updateVisible() {
    final Service service = view.serviceOptions.getValue();
    final SampleSupport support = view.sampleSupportOptions.getValue();
    view.sampleTypeLabel.setVisible(editable);
    view.inactiveLabel.setVisible(editable);
    view.sampleSupportOptions.setItemEnabledProvider(value -> value != GEL || service == LC_MS_MS);
    view.solutionSolventField
        .setVisible(service == SMALL_MOLECULE && support == SampleSupport.SOLUTION);
    view.sampleNameField.setVisible(service == SMALL_MOLECULE);
    view.formulaField.setVisible(service == SMALL_MOLECULE);
    view.structureLayout.setVisible(service == SMALL_MOLECULE);
    view.structureUploader.setVisible(service == SMALL_MOLECULE && editable);
    view.structureButton
        .setVisible(service == SMALL_MOLECULE && view.structureButton.getCaption() != null
            && !view.structureButton.getCaption().isEmpty());
    view.monoisotopicMassField.setVisible(service == SMALL_MOLECULE);
    view.averageMassField.setVisible(service == SMALL_MOLECULE);
    view.toxicityField.setVisible(service == SMALL_MOLECULE);
    view.lightSensitiveField.setVisible(service == SMALL_MOLECULE);
    view.storageTemperatureOptions.setVisible(service == SMALL_MOLECULE);
    view.sampleCountField.setVisible(service != SMALL_MOLECULE);
    view.sampleContainerTypeOptions.setVisible(service == LC_MS_MS);
    view.plateNameField
        .setVisible(service == LC_MS_MS && view.sampleContainerTypeOptions.getValue() == SPOT);
    view.samplesLabel.setVisible(service != SMALL_MOLECULE);
    view.samplesGridLayout.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && view.sampleContainerTypeOptions.getValue() != SPOT));
    view.samplesGrid.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && view.sampleContainerTypeOptions.getValue() != SPOT));
    view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).setHidden(service != INTACT_PROTEIN);
    view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).setHidden(service != INTACT_PROTEIN);
    view.samplesGrid.setWidth((float) view.samplesGrid.getColumns().stream()
        .filter(column -> !column.isHidden()).mapToDouble(column -> column.getWidth()).sum(),
        Unit.PIXELS);
    view.fillSamplesButton.setVisible((service == INTACT_PROTEIN
        || (service == LC_MS_MS && view.sampleContainerTypeOptions.getValue() != SPOT))
        && editable);
    view.samplesPlateContainer
        .setVisible(service == LC_MS_MS && view.sampleContainerTypeOptions.getValue() == SPOT);
    view.experiencePanel.setVisible(service != SMALL_MOLECULE);
    view.experienceField.setVisible(service != SMALL_MOLECULE);
    view.experienceGoalField.setVisible(service != SMALL_MOLECULE);
    view.taxonomyField.setVisible(service != SMALL_MOLECULE);
    view.proteinNameField.setVisible(service != SMALL_MOLECULE);
    view.proteinWeightField.setVisible(service == LC_MS_MS);
    view.postTranslationModificationField.setVisible(service != SMALL_MOLECULE);
    view.sampleQuantityField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.sampleVolumeField.setVisible(service != SMALL_MOLECULE && support == SOLUTION);
    view.standardsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.standardCountField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.standardsGrid
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.fillStandardsButton.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && editable);
    view.contaminantsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.contaminantCountField
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.contaminantsGrid
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    view.fillContaminantsButton.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && editable);
    view.gelPanel.setVisible(service == LC_MS_MS && support == GEL);
    view.separationField.setVisible(service == LC_MS_MS && support == GEL);
    view.thicknessField.setVisible(service == LC_MS_MS && support == GEL);
    view.colorationField.setVisible(service == LC_MS_MS && support == GEL);
    view.otherColorationField.setVisible(service == LC_MS_MS && support == GEL
        && view.colorationField.getValue() == GelColoration.OTHER);
    view.developmentTimeField.setVisible(service == LC_MS_MS && support == GEL);
    view.decolorationField.setVisible(service == LC_MS_MS && support == GEL);
    view.weightMarkerQuantityField.setVisible(service == LC_MS_MS && support == GEL);
    view.proteinQuantityField.setVisible(service == LC_MS_MS && support == GEL);
    view.gelImagesLayout.setVisible(service == LC_MS_MS && support == GEL);
    view.gelImagesUploader.setVisible(service == LC_MS_MS && support == GEL && editable);
    view.gelImagesGrid.setVisible(service == LC_MS_MS && support == GEL);
    view.digestionOptions.setVisible(service == LC_MS_MS);
    view.usedProteolyticDigestionMethodField.setVisible(
        view.digestionOptions.isVisible() && view.digestionOptions.getValue() == DIGESTED);
    view.otherProteolyticDigestionMethodField.setVisible(view.digestionOptions.isVisible()
        && view.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    view.otherProteolyticDigestionMethodNote.setVisible(view.digestionOptions.isVisible()
        && view.digestionOptions.getValue() == ProteolyticDigestion.OTHER);
    view.enrichmentLabel.setVisible(editable && service == LC_MS_MS);
    view.exclusionsLabel.setVisible(editable && service == LC_MS_MS);
    view.injectionTypeOptions.setVisible(service == INTACT_PROTEIN);
    view.sourceOptions.setVisible(service == INTACT_PROTEIN);
    view.proteinContentOptions.setVisible(service == LC_MS_MS);
    view.instrumentOptions.setVisible(service != SMALL_MOLECULE);
    view.proteinIdentificationOptions.setVisible(service == LC_MS_MS);
    view.proteinIdentificationLinkField.setVisible(view.proteinIdentificationOptions.isVisible()
        && view.proteinIdentificationOptions.getValue() == ProteinIdentification.OTHER);
    view.quantificationOptions.setVisible(service == LC_MS_MS);
    view.quantificationLabelsField.setVisible(service == LC_MS_MS);
    view.highResolutionOptions.setVisible(service == SMALL_MOLECULE);
    view.solventsLayout.setVisible(service == SMALL_MOLECULE);
    view.acetonitrileSolventsField.setVisible(service == SMALL_MOLECULE);
    view.methanolSolventsField.setVisible(service == SMALL_MOLECULE);
    view.chclSolventsField.setVisible(service == SMALL_MOLECULE);
    view.otherSolventsField.setVisible(service == SMALL_MOLECULE);
    view.otherSolventField
        .setVisible(service == SMALL_MOLECULE && view.otherSolventsField.getValue());
    view.otherSolventNoteLabel
        .setVisible(service == SMALL_MOLECULE && view.otherSolventsField.getValue());
    view.filesUploader.setVisible(editable);
    view.buttonsLayout.setVisible(editable);
  }

  private void updateEditable() {
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
    view.sampleContainerTypeOptions.setReadOnly(!editable);
    view.plateNameField.setReadOnly(!editable);
    view.sampleCountField.setReadOnly(!editable);
    sampleBinders.values().forEach(binder -> binder.setReadOnly(!editable));
    view.fillSamplesButton.setVisible(editable);
    view.samplesSpreadsheet.setReportStyle(!editable);
    view.samplesSpreadsheet.setFunctionBarVisible(false);
    view.samplesSpreadsheet.setSheetSelectionBarVisible(false);
    view.samplesSpreadsheet.setRowColHeadingsVisible(false);
    CellStyle locked = view.samplesSpreadsheet.getWorkbook().createCellStyle();
    locked.setLocked(!editable);
    cells(view.samplesSpreadsheet).forEach(cell -> cell.setCellStyle(locked));
    view.experienceField.setReadOnly(!editable);
    view.experienceGoalField.setReadOnly(!editable);
    view.taxonomyField.setReadOnly(!editable);
    view.proteinNameField.setReadOnly(!editable);
    view.proteinWeightField.setReadOnly(!editable);
    view.postTranslationModificationField.setReadOnly(!editable);
    view.sampleQuantityField.setReadOnly(!editable);
    view.sampleVolumeField.setReadOnly(!editable);
    view.standardCountField.setReadOnly(!editable);
    standardBinders.values().forEach(binder -> binder.setReadOnly(!editable));
    view.contaminantCountField.setReadOnly(!editable);
    contaminantBinders.values().forEach(binder -> binder.setReadOnly(!editable));
    view.separationField.setReadOnly(!editable);
    view.thicknessField.setReadOnly(!editable);
    view.colorationField.setReadOnly(!editable);
    view.otherColorationField.setReadOnly(!editable);
    view.developmentTimeField.setReadOnly(!editable);
    view.decolorationField.setReadOnly(!editable);
    view.weightMarkerQuantityField.setReadOnly(!editable);
    view.proteinQuantityField.setReadOnly(!editable);
    view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).setHidden(!editable);
    view.digestionOptions.setReadOnly(!editable);
    view.usedProteolyticDigestionMethodField.setReadOnly(!editable);
    view.otherProteolyticDigestionMethodField.setReadOnly(!editable);
    view.injectionTypeOptions.setReadOnly(!editable);
    view.sourceOptions.setReadOnly(!editable);
    view.proteinContentOptions.setReadOnly(!editable);
    view.instrumentOptions.setReadOnly(!editable);
    view.proteinIdentificationOptions.setReadOnly(!editable);
    view.proteinIdentificationLinkField.setReadOnly(!editable);
    view.quantificationOptions.setReadOnly(!editable);
    view.quantificationLabelsField.setReadOnly(!editable);
    view.highResolutionOptions.setReadOnly(!editable);
    view.acetonitrileSolventsField.setReadOnly(!editable);
    view.methanolSolventsField.setReadOnly(!editable);
    view.chclSolventsField.setReadOnly(!editable);
    view.otherSolventsField.setReadOnly(!editable);
    view.otherSolventField.setReadOnly(!editable);
    view.commentsField.setReadOnly(!editable);
    view.filesGrid.getColumn(REMOVE_FILE).setHidden(!editable);
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
      view.standardsTableLayout.setVisible(count > 0);
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
      view.contaminantsTableLayout.setVisible(count > 0);
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
    String comments = first.getComments();
    standardsDataProvider.getItems().forEach(standard -> {
      standard.setName(name);
      standard.setQuantity(quantity);
      standard.setComments(comments);
    });
    standardsDataProvider.refreshAll();
  }

  private void fillContaminants() {
    Contaminant first = contaminantsDataProvider.getItems().iterator().next();
    String name = first.getName();
    String quantity = first.getQuantity();
    String comments = first.getComments();
    contaminantsDataProvider.getItems().forEach(contaminant -> {
      contaminant.setName(name);
      contaminant.setQuantity(quantity);
      contaminant.setComments(comments);
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

  private Stream<Cell> cells(Spreadsheet spreadsheet) {
    List<Cell> cells = new ArrayList<>();
    for (int column = 1; column < spreadsheet.getColumns(); column++) {
      for (int row = 1; row < spreadsheet.getRows(); row++) {
        cells.add(spreadsheet.getCell(row, column));
      }
    }
    return cells.stream().filter(c -> c != null);
  }

  private boolean validate() {
    logger.trace("Validate submission");
    boolean valid = true;
    valid &= validate(submissionBinder);
    valid &= validate(firstSampleBinder);
    Submission submission = submissionBinder.getBean();
    SubmissionSample sample = firstSampleBinder.getBean();
    if (submission.getService() == LC_MS_MS || submission.getService() == INTACT_PROTEIN) {
      valid &= validate(sampleCountBinder);
      if (view.sampleContainerTypeOptions.getValue() != SPOT) {
        for (SubmissionSample samp : samplesDataProvider.getItems()) {
          valid &= validate(sampleBinders.get(samp));
        }
      } else {
        valid &= validate(plateBinder);
        List<String> excelNames =
            cells(view.samplesSpreadsheet).map(cell -> view.samplesSpreadsheet.getCellValue(cell))
                .filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());
        valid &= validate(validateSampleName(false), view.samplesSpreadsheet, excelNames);
      }
      if (sample.getSupport() == DRY || sample.getSupport() == SOLUTION) {
        valid &= validate(standardCountBinder);
        for (Standard standard : standardsDataProvider.getItems()) {
          valid &= validate(standardBinders.get(standard));
        }
        valid &= validate(contaminantCountBinder);
        for (Contaminant contaminant : contaminantsDataProvider.getItems()) {
          valid &= validate(contaminantBinders.get(contaminant));
        }
      }
    }
    if (!valid) {
      final MessageResource generalResources = view.getGeneralResources();
      logger.trace("Submission field validation failed");
      view.showError(generalResources.message(FIELD_NOTIFICATION));
    }
    if (valid) {
      if (submission.getService() == LC_MS_MS || submission.getService() == INTACT_PROTEIN) {
        valid &= validate(() -> validateSampleNames());
        if (sample.getSupport() == GEL) {
          valid &= validate(() -> validateGelImages(submission));
        }
      }
      if (submission.getService() == Service.SMALL_MOLECULE) {
        valid &= validate(() -> validateStructure(submission));
        valid &= validate(() -> validateSolvents());
      }
    }
    return valid;
  }

  private boolean validate(Supplier<ValidationResult> validator) {
    ValidationResult result = validator.get();
    if (result.isError()) {
      logger.trace("Validation error {}", result.getErrorMessage());
      view.showError(result.getErrorMessage());
      return false;
    } else {
      return true;
    }
  }

  private ValidationResult validateSampleNames() {
    MessageResource resources = view.getResources();
    Set<String> names = new HashSet<>();
    if (view.sampleContainerTypeOptions.getValue() != SPOT) {
      for (SubmissionSample sample : samplesDataProvider.getItems()) {
        if (!names.add(sample.getName())) {
          return ValidationResult
              .error(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sample.getName()));
        }
      }
    } else {
      int count = 0;
      List<String> excelNames =
          cells(view.samplesSpreadsheet).map(cell -> view.samplesSpreadsheet.getCellValue(cell))
              .filter(name -> name != null && !name.isEmpty()).collect(Collectors.toList());
      for (String name : excelNames) {
        count++;
        if (!names.add(name)) {
          return ValidationResult
              .error(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", name));
        }
      }
      if (count < sampleCountBinder.getBean().getCount()) {
        return ValidationResult.error(resources.message(SAMPLES_PROPERTY + ".missing",
            sampleCountBinder.getBean().getCount()));
      }
    }
    return ValidationResult.ok();

  }

  private ValidationResult validateStructure(Submission submission) {
    if (submission.getStructure() == null || submission.getStructure().getFilename() == null) {
      MessageResource resources = view.getResources();
      return ValidationResult.error(resources.message(STRUCTURE_PROPERTY + "." + REQUIRED));
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateGelImages(Submission submission) {
    if (gelImagesDataProvider.getItems().size() == 0) {
      MessageResource resources = view.getResources();
      return ValidationResult.error(resources.message(GEL_IMAGES_PROPERTY + "." + REQUIRED));
    }
    return ValidationResult.ok();
  }

  private ValidationResult validateSolvents() {
    if (!view.acetonitrileSolventsField.getValue() && !view.methanolSolventsField.getValue()
        && !view.chclSolventsField.getValue() && !view.otherSolventsField.getValue()) {
      MessageResource resources = view.getResources();
      return ValidationResult.error(resources.message(SOLVENTS_PROPERTY + "." + REQUIRED));
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
    clearInvisibleField(view.solutionSolventField);
    clearInvisibleField(view.sampleNameField);
    clearInvisibleField(view.formulaField);
    if (!view.structureLayout.isVisible()) {
      submissionBinder.getBean().setStructure(null);
    }
    clearInvisibleField(view.monoisotopicMassField);
    clearInvisibleField(view.averageMassField);
    clearInvisibleField(view.toxicityField);
    clearInvisibleField(view.lightSensitiveField);
    clearInvisibleField(view.storageTemperatureOptions);
    clearInvisibleField(view.sampleCountField);
    clearInvisibleField(view.sampleContainerTypeOptions);
    clearInvisibleField(view.plateNameField);
    if (!view.samplesGrid.isVisible()) {
      sampleNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleNumberProteinFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleMolecularWeightFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(view.experienceField);
    clearInvisibleField(view.experienceGoalField);
    clearInvisibleField(view.taxonomyField);
    clearInvisibleField(view.proteinNameField);
    clearInvisibleField(view.proteinWeightField);
    clearInvisibleField(view.postTranslationModificationField);
    clearInvisibleField(view.sampleQuantityField);
    clearInvisibleField(view.sampleVolumeField);
    clearInvisibleField(view.standardCountField);
    if (!view.standardsGrid.isVisible()) {
      standardNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardCommentsFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(view.contaminantCountField);
    if (!view.contaminantsGrid.isVisible()) {
      contaminantNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantCommentsFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(view.separationField);
    clearInvisibleField(view.thicknessField);
    clearInvisibleField(view.colorationField);
    clearInvisibleField(view.otherColorationField);
    clearInvisibleField(view.developmentTimeField);
    clearInvisibleField(view.decolorationField);
    clearInvisibleField(view.weightMarkerQuantityField);
    clearInvisibleField(view.proteinQuantityField);
    if (!view.gelImagesLayout.isVisible()) {
      gelImagesDataProvider.getItems().clear();
      gelImagesDataProvider.refreshAll();
    }
    clearInvisibleField(view.digestionOptions);
    clearInvisibleField(view.usedProteolyticDigestionMethodField);
    clearInvisibleField(view.otherProteolyticDigestionMethodField);
    clearInvisibleField(view.injectionTypeOptions);
    clearInvisibleField(view.sourceOptions);
    clearInvisibleField(view.proteinContentOptions);
    clearInvisibleField(view.instrumentOptions);
    clearInvisibleField(view.proteinIdentificationOptions);
    clearInvisibleField(view.proteinIdentificationLinkField);
    clearInvisibleField(view.quantificationOptions);
    clearInvisibleField(view.quantificationLabelsField);
    setValueIfInvisible(view.highResolutionOptions, false);
    clearInvisibleField(view.acetonitrileSolventsField);
    clearInvisibleField(view.methanolSolventsField);
    clearInvisibleField(view.chclSolventsField);
    clearInvisibleField(view.otherSolventsField);
    clearInvisibleField(view.otherSolventField);
  }

  private void saveSubmission() {
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
      submissionService.insert(submission);
      MessageResource resources = view.getResources();
      view.showTrayNotification(resources.message("save", submission.getExperience()));
      view.navigateTo(SubmissionsView.VIEW_NAME);
    }
  }

  private void copySamplesToSubmission(Submission submission) {
    if (submission.getService() == LC_MS_MS && view.sampleContainerTypeOptions.getValue() == SPOT) {
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
    Plate plate = plateBinder.getBean();
    cells(view.samplesSpreadsheet).forEach(cell -> {
      String name = view.samplesSpreadsheet.getCellValue(cell);
      if (name != null && !name.isEmpty()) {
        SubmissionSample sample = new SubmissionSample();
        sample.setName(name);
        sample.setSupport(firstSample.getSupport());
        sample.setQuantity(firstSample.getQuantity());
        sample.setVolume(firstSample.getVolume());
        sample.setNumberProtein(null);
        sample.setMolecularWeight(firstSample.getMolecularWeight());
        if (firstSample.getSupport() != GEL) {
          copyStandardsFromTableToSample(sample);
          copyContaminantsFromTableToSample(sample);
        }
        PlateSpot container = new PlateSpot(cell.getRowIndex() - 1, cell.getColumnIndex() - 1);
        container.setPlate(plate);
        sample.setOriginalContainer(container);
        samples.add(sample);
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
      copy.setComments(standard.getComments());
      sample.getStandards().add(copy);
    }
  }

  private void copyContaminantsFromTableToSample(SubmissionSample sample) {
    sample.setContaminants(new ArrayList<>());
    for (Contaminant contaminant : contaminantsDataProvider.getItems()) {
      Contaminant copy = new Contaminant();
      copy.setName(contaminant.getName());
      copy.setQuantity(contaminant.getQuantity());
      copy.setComments(contaminant.getComments());
      sample.getContaminants().add(copy);
    }
  }

  public Submission getBean() {
    return submissionBinder.getBean();
  }

  /**
   * Sets submission.
   *
   * @param submission
   *          submission
   */
  public void setBean(Submission submission) {
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
      firstSample.setNumberProtein(1);
      firstSample.setOriginalContainer(new Tube());
      samples.add(firstSample);
    } else {
      firstSample = samples.get(0);
    }
    SubmissionSample firstSampleCopy =
        new SubmissionSample(firstSample.getId(), firstSample.getName());
    firstSampleCopy.setNumberProtein(firstSample.getNumberProtein());
    firstSampleCopy.setMolecularWeight(firstSample.getMolecularWeight());
    samples.set(0, firstSampleCopy);
    SampleContainer container = firstSample.getOriginalContainer();

    submissionBinder.setBean(submission);
    firstSampleBinder.setBean(firstSample);
    if (container instanceof PlateSpot) {
      PlateSpot containerAsWell = (PlateSpot) container;
      plateBinder.setBean(containerAsWell.getPlate());
      view.samplesSpreadsheet.createCell(containerAsWell.getRow() + 1,
          containerAsWell.getColumn() + 1, firstSample.getName());
      logger.debug("Sample name at plate {}-{} is {} and should be {}", containerAsWell.getRow(),
          containerAsWell.getColumn(), firstSample.getName(),
          view.samplesSpreadsheet.getCellValue(view.samplesSpreadsheet
              .getCell(containerAsWell.getRow() + 1, containerAsWell.getColumn() + 1)));
      samples.stream().skip(1).forEach(sample -> {
        PlateSpot well = (PlateSpot) sample.getOriginalContainer();
        view.samplesSpreadsheet.createCell(well.getRow() + 1, well.getColumn() + 1,
            sample.getName());
      });
    } else {
      plateBinder.setBean(new Plate());
    }
    Service service = submission.getService();
    if (service != null && !service.available) {
      view.serviceOptions
          .setItems(Stream.concat(Service.availables().stream(), Stream.of(service)));
    }
    samplesDataProvider.getItems().clear();
    samplesDataProvider.getItems().addAll(samples);
    samplesDataProvider.refreshAll();
    view.sampleCountField.setReadOnly(false);
    sampleCountBinder.setBean(new ItemCount(samples.size()));
    view.sampleContainerTypeOptions.setReadOnly(false);
    view.sampleContainerTypeOptions.setValue(firstSample.getOriginalContainer().getType());
    view.samplesGrid.sort(SAMPLE_NAME_PROPERTY);
    Structure structure = submission.getStructure();
    updateStructureButton(structure);
    List<Standard> standards = firstSample.getStandards();
    if (standards == null) {
      standards = new ArrayList<>();
    }
    standardsDataProvider.getItems().clear();
    standardsDataProvider.getItems().addAll(standards);
    standardsDataProvider.refreshAll();
    view.standardCountField.setReadOnly(false);
    standardCountBinder.setBean(new ItemCount(standards.size()));
    List<Contaminant> contaminants = firstSample.getContaminants();
    if (contaminants == null) {
      contaminants = new ArrayList<>();
    }
    contaminantsDataProvider.getItems().clear();
    contaminantsDataProvider.getItems().addAll(contaminants);
    contaminantsDataProvider.refreshAll();
    view.contaminantCountField.setReadOnly(false);
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
      view.sourceOptions.setItems(
          Stream.concat(MassDetectionInstrumentSource.availables().stream(), Stream.of(source)));
    }
    MassDetectionInstrument instrument = submission.getMassDetectionInstrument();
    if (instrument != null && !instrument.available) {
      view.instrumentOptions
          .setItems(Stream.concat(instrumentValues().stream(), Stream.of(instrument)));
    }
    ProteinIdentification proteinIdentification = submission.getProteinIdentification();
    if (proteinIdentification != null && !proteinIdentification.available) {
      view.proteinIdentificationOptions.setItems(Stream
          .concat(ProteinIdentification.availables().stream(), Stream.of(proteinIdentification)));
    }
    List<SampleSolvent> sampleSolvents = submission.getSolvents();
    if (sampleSolvents == null) {
      sampleSolvents = new ArrayList<>();
    }
    Set<Solvent> solvents =
        sampleSolvents.stream().map(ss -> ss.getSolvent()).collect(Collectors.toSet());
    view.acetonitrileSolventsField.setReadOnly(false);
    view.acetonitrileSolventsField.setValue(solvents.contains(Solvent.ACETONITRILE));
    view.acetonitrileSolventsField.setReadOnly(false);
    view.methanolSolventsField.setValue(solvents.contains(Solvent.METHANOL));
    view.acetonitrileSolventsField.setReadOnly(false);
    view.chclSolventsField.setValue(solvents.contains(Solvent.CHCL3));
    view.acetonitrileSolventsField.setReadOnly(false);
    view.otherSolventsField.setValue(solvents.contains(Solvent.OTHER));
    List<SubmissionFile> files = submission.getFiles();
    if (files == null) {
      files = new ArrayList<>();
    }
    filesDataProvider.getItems().clear();
    filesDataProvider.getItems().addAll(files);
    filesDataProvider.refreshAll();
    updateVisible();
    updateEditable();
  }

  public boolean isEditable() {
    return editable;
  }

  /**
   * Sets if form is editable.
   *
   * @param editable
   *          editable
   */
  public void setEditable(boolean editable) {
    this.editable = editable;
    updateVisible();
    updateEditable();
  }

  private List<MassDetectionInstrument> instrumentValues() {
    List<MassDetectionInstrument> values = new ArrayList<>(MassDetectionInstrument.availables());
    values.add(0, null);
    return values;
  }

  private List<Quantification> quantificationValues() {
    List<Quantification> values = new ArrayList<>(Arrays.asList(Quantification.values()));
    values.add(0, null);
    return values;
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
      view.structureButton.setVisible(true);
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
