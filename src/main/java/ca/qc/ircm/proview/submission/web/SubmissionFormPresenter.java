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
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.QSubmissionFile.submissionFile;
import static ca.qc.ircm.proview.submission.Quantification.SILAC;
import static ca.qc.ircm.proview.submission.Quantification.TMT;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.treatment.Solvent.ACETONITRILE;
import static ca.qc.ircm.proview.treatment.Solvent.CHCL3;
import static ca.qc.ircm.proview.treatment.Solvent.METHANOL;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.Named;
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
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.GelColoration;
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
import ca.qc.ircm.proview.vaadin.VaadinUtils;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
  public static final String SAMPLE_TYPE_WARNING = "sampleTypeWarning";
  public static final String INACTIVE_WARNING = "inactive";
  public static final String SERVICE_PANEL = "servicePanel";
  public static final String SERVICE = "service";
  public static final String SAMPLES = submission.samples.getMetadata().getName();
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLE_SUPPORT = submissionSample.support.getMetadata().getName();
  public static final String SOLUTION_SOLVENT = submission.solutionSolvent.getMetadata().getName();
  public static final String SAMPLE_COUNT = "sampleCount";
  public static final String SAMPLE_NAME = submissionSample.name.getMetadata().getName();
  public static final String FORMULA = submission.formula.getMetadata().getName();
  public static final String MONOISOTOPIC_MASS =
      submission.monoisotopicMass.getMetadata().getName();
  public static final String AVERAGE_MASS = submission.averageMass.getMetadata().getName();
  public static final String TOXICITY = submission.toxicity.getMetadata().getName();
  public static final String LIGHT_SENSITIVE = submission.lightSensitive.getMetadata().getName();
  public static final String STORAGE_TEMPERATURE =
      submission.storageTemperature.getMetadata().getName();
  public static final String SAMPLES_CONTAINER_TYPE = SAMPLES + "ContainerType";
  public static final String PLATE = plate.getMetadata().getName();
  public static final String PLATE_NAME = plate.name.getMetadata().getName();
  public static final String SAMPLES_LABEL = SAMPLES + "Label";
  public static final String SAMPLE_NUMBER_PROTEIN =
      submissionSample.numberProtein.getMetadata().getName();
  public static final String FILL_SAMPLES = "fillSamples";
  public static final String SAMPLES_PLATE = SAMPLES + "Plate";
  public static final String EXPERIENCE_PANEL = "experiencePanel";
  public static final String EXPERIENCE = submission.experience.getMetadata().getName();
  public static final String EXPERIENCE_GOAL = submission.goal.getMetadata().getName();
  public static final String TAXONOMY = submission.taxonomy.getMetadata().getName();
  public static final String PROTEIN_NAME = submission.protein.getMetadata().getName();
  public static final String PROTEIN_WEIGHT =
      submissionSample.molecularWeight.getMetadata().getName();
  public static final String POST_TRANSLATION_MODIFICATION =
      submission.postTranslationModification.getMetadata().getName();
  public static final String SAMPLE_QUANTITY = submissionSample.quantity.getMetadata().getName();
  public static final String SAMPLE_VOLUME = submissionSample.volume.getMetadata().getName();
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String STANDARD_COUNT = "standardCount";
  public static final String STANDARDS = submissionSample.standards.getMetadata().getName();
  public static final String STANDARD_NAME = standard.name.getMetadata().getName();
  public static final String STANDARD_QUANTITY = standard.quantity.getMetadata().getName();
  public static final String STANDARD_COMMENT = standard.comment.getMetadata().getName();
  public static final String FILL_STANDARDS = "fillStandards";
  public static final String CONTAMINANTS_PANEL = "contaminantsPanel";
  public static final String CONTAMINANT_COUNT = "contaminantCount";
  public static final String CONTAMINANTS = submissionSample.contaminants.getMetadata().getName();
  public static final String CONTAMINANT_NAME = contaminant.name.getMetadata().getName();
  public static final String CONTAMINANT_QUANTITY = contaminant.quantity.getMetadata().getName();
  public static final String CONTAMINANT_COMMENT = contaminant.comment.getMetadata().getName();
  public static final String FILL_CONTAMINANTS = "fillContaminants";
  public static final String GEL_PANEL = "gelPanel";
  public static final String SEPARATION = submission.separation.getMetadata().getName();
  public static final String THICKNESS = submission.thickness.getMetadata().getName();
  public static final String COLORATION = submission.coloration.getMetadata().getName();
  public static final String OTHER_COLORATION = submission.otherColoration.getMetadata().getName();
  public static final String DEVELOPMENT_TIME = submission.developmentTime.getMetadata().getName();
  public static final String DECOLORATION = submission.decoloration.getMetadata().getName();
  public static final String WEIGHT_MARKER_QUANTITY =
      submission.weightMarkerQuantity.getMetadata().getName();
  public static final String PROTEIN_QUANTITY = submission.proteinQuantity.getMetadata().getName();
  public static final String SERVICES_PANEL = "servicesPanel";
  public static final String DIGESTION =
      submission.proteolyticDigestionMethod.getMetadata().getName();
  public static final String USED_DIGESTION =
      submission.usedProteolyticDigestionMethod.getMetadata().getName();
  public static final String OTHER_DIGESTION =
      submission.otherProteolyticDigestionMethod.getMetadata().getName();
  public static final String ENRICHEMENT = "enrichment";
  public static final String EXCLUSIONS = "exclusions";
  public static final String INJECTION_TYPE = submission.injectionType.getMetadata().getName();
  public static final String SOURCE = submission.source.getMetadata().getName();
  public static final String PROTEIN_CONTENT = submission.proteinContent.getMetadata().getName();
  public static final String INSTRUMENT =
      submission.massDetectionInstrument.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION =
      submission.proteinIdentification.getMetadata().getName();
  public static final String PROTEIN_IDENTIFICATION_LINK =
      submission.proteinIdentificationLink.getMetadata().getName();
  public static final String QUANTIFICATION = submission.quantification.getMetadata().getName();
  public static final String QUANTIFICATION_COMMENT =
      submission.quantificationComment.getMetadata().getName();
  public static final String HIGH_RESOLUTION = submission.highResolution.getMetadata().getName();
  public static final String SOLVENTS = submission.solvents.getMetadata().getName();
  public static final String OTHER_SOLVENT = submission.otherSolvent.getMetadata().getName();
  public static final String OTHER_SOLVENT_NOTE =
      submission.otherSolvent.getMetadata().getName() + ".note";
  public static final String COMMENT_PANEL = "commentPanel";
  public static final String COMMENT = submission.comment.getMetadata().getName();
  public static final String STRUCTURE_FILE = "structureFile";
  public static final String GEL_IMAGE_FILE = "gelImageFile";
  public static final String FILES = submission.files.getMetadata().getName();
  public static final String FILES_PANEL = FILES + "Panel";
  public static final String FILES_UPLOADER = FILES + "Uploader";
  public static final String EXPLANATION_PANEL = "explanationPanel";
  public static final String EXPLANATION = "explanation";
  public static final int MAXIMUM_FILES_SIZE = 50 * 1024 * 1024; // 50MB
  public static final int MAXIMUM_FILES_COUNT = 6;
  public static final String FILE_FILENAME = submissionFile.filename.getMetadata().getName();
  public static final String REMOVE_FILE = "removeFile";
  public static final String SAVE = "save";
  public static final String UPDATE_ERROR = "updateError";
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
  private ListDataProvider<SubmissionFile> filesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<SubmissionFile, Button> fileDownloads = new HashMap<>();
  private Map<SubmissionFile, Button> fileRemoves = new HashMap<>();
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
    view.createFilesUploader(fileHandler());
    prepareComponents();
    setValue(null);
    updateVisible();
    updateReadOnly();
    updateSampleCount(design.sampleCount.getValue());
    updateStandardsTable(design.standardCount.getValue());
    updateContaminantsTable(design.contaminantCount.getValue());
  }

  private void prepareComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.sampleTypeWarning.addStyleName(SAMPLE_TYPE_WARNING);
    design.sampleTypeWarning.setValue(resources.message(SAMPLE_TYPE_WARNING));
    design.inactiveWarning.addStyleName(INACTIVE_WARNING);
    design.inactiveWarning.setValue(resources.message(INACTIVE_WARNING));
    design.servicePanel.addStyleName(SERVICE_PANEL);
    design.servicePanel.addStyleName(REQUIRED);
    design.servicePanel.setCaption(resources.message(SERVICE));
    design.service.addStyleName(SERVICE);
    design.service.addStyleName(HIDE_REQUIRED_STYLE);
    design.service.setItems(Service.availables());
    design.service.setItemCaptionGenerator(service -> service.getLabel(locale));
    design.service.setItemEnabledProvider(service -> service.available);
    design.service.addValueChangeListener(e -> updateVisible());
    design.service
        .addValueChangeListener(e -> design.sampleSupport.getDataProvider().refreshItem(GEL));
    submissionBinder.forField(design.service).asRequired(generalResources.message(REQUIRED))
        .bind(SERVICE);
    prepareSamplesComponents();
    prepareExperienceComponents();
    prepareStandardsComponents();
    prepareContaminantsComponents();
    prepareGelComponents();
    prepareServicesComponents();
    design.commentPanel.setCaption(resources.message(COMMENT_PANEL));
    design.commentPanel.addStyleName(COMMENT_PANEL);
    design.comment.addStyleName(COMMENT);
    submissionBinder.forField(design.comment).withNullRepresentation("").bind(COMMENT);
    design.structureFile.addStyleName(STRUCTURE_FILE);
    design.structureFile.setValue(resources.message(STRUCTURE_FILE));
    design.gelImageFile.addStyleName(GEL_IMAGE_FILE);
    design.gelImageFile.setValue(resources.message(GEL_IMAGE_FILE));
    design.filesPanel.addStyleName(FILES_PANEL);
    design.filesPanel.setCaption(resources.message(FILES_PANEL));
    view.filesUploader.addStyleName(FILES_UPLOADER);
    view.filesUploader.setUploadButtonCaption(resources.message(FILES_UPLOADER));
    view.filesUploader.setMaxFileCount(1000000); // Count is required if size is set.
    view.filesUploader.setMaxFileSize(MAXIMUM_FILES_SIZE);
    design.files.addStyleName(FILES);
    design.files.addStyleName(COMPONENTS);
    design.files.setDataProvider(filesDataProvider);
    design.files.addColumn(file -> downloadFileButton(file), new ComponentRenderer())
        .setId(FILE_FILENAME).setCaption(resources.message(FILES + "." + FILE_FILENAME))
        .setSortable(false);
    design.files.addColumn(file -> removeFileButton(file), new ComponentRenderer())
        .setId(REMOVE_FILE).setCaption(resources.message(FILES + "." + REMOVE_FILE))
        .setSortable(false);
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.addStyleName(REQUIRED);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanation.addStyleName(EXPLANATION);
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
  }

  private Button downloadFileButton(SubmissionFile file) {
    if (fileDownloads.containsKey(file)) {
      return fileDownloads.get(file);
    } else {
      Button button = new Button();
      button.addStyleName(FILE_FILENAME);
      button.setCaption(file.getFilename());
      button.setIcon(VaadinIcons.DOWNLOAD);
      StreamResource resource =
          new StreamResource(() -> new ByteArrayInputStream(file.getContent()), file.getFilename());
      FileDownloader fileDownloader = new FileDownloader(resource);
      fileDownloader.extend(button);
      fileDownloads.put(file, button);
      return button;
    }
  }

  private Button removeFileButton(SubmissionFile file) {
    if (fileRemoves.containsKey(file)) {
      return fileRemoves.get(file);
    } else {
      MessageResource resources = view.getResources();
      Button button = new Button();
      button.addStyleName(REMOVE_FILE);
      button.setCaption(resources.message(FILES + "." + REMOVE_FILE));
      button.addClickListener(e -> {
        filesDataProvider.getItems().remove(file);
        filesDataProvider.refreshAll();
      });
      fileRemoves.put(file, button);
      return button;
    }
  }

  private void prepareSamplesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.sampleSupport.addStyleName(SAMPLE_SUPPORT);
    design.sampleSupport.setCaption(resources.message(SAMPLE_SUPPORT));
    design.sampleSupport.setItems(SampleSupport.values());
    design.sampleSupport.setItemCaptionGenerator(support -> support.getLabel(locale));
    design.sampleSupport.addValueChangeListener(e -> updateVisible());
    firstSampleBinder.forField(design.sampleSupport).asRequired(generalResources.message(REQUIRED))
        .bind(SAMPLE_SUPPORT);
    design.solutionSolvent.addStyleName(SOLUTION_SOLVENT);
    design.solutionSolvent.setCaption(resources.message(SOLUTION_SOLVENT));
    design.solutionSolvent.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.solutionSolvent)
        .withValidator(requiredTextIfVisible(design.solutionSolvent)).withNullRepresentation("")
        .bind(SOLUTION_SOLVENT);
    design.sampleCount.addStyleName(SAMPLE_COUNT);
    design.sampleCount.setCaption(resources.message(SAMPLE_COUNT));
    sampleCountBinder.forField(design.sampleCount).asRequired(generalResources.message(REQUIRED))
        .withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 1, MAX_SAMPLE_COUNT), 1, MAX_SAMPLE_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.sampleCount
        .addValueChangeListener(e -> updateSampleCount(design.sampleCount.getValue()));
    design.sampleCount.addValueChangeListener(e -> updateSampleCount(e.getValue()));
    design.sampleName.addStyleName(SAMPLE_NAME);
    design.sampleName.setCaption(resources.message(SAMPLE_NAME));
    design.sampleName.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleName)
        .withValidator(requiredTextIfVisible(design.sampleName)).withNullRepresentation("")
        .withValidator(validateSampleName(false)).bind(SAMPLE_NAME);
    design.formula.addStyleName(FORMULA);
    design.formula.setCaption(resources.message(FORMULA));
    design.formula.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.formula).withValidator(requiredTextIfVisible(design.formula))
        .withNullRepresentation("").bind(FORMULA);
    design.monoisotopicMass.addStyleName(MONOISOTOPIC_MASS);
    design.monoisotopicMass.setCaption(resources.message(MONOISOTOPIC_MASS));
    design.monoisotopicMass.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.monoisotopicMass)
        .withValidator(requiredTextIfVisible(design.monoisotopicMass)).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(MONOISOTOPIC_MASS);
    design.averageMass.addStyleName(AVERAGE_MASS);
    design.averageMass.setCaption(resources.message(AVERAGE_MASS));
    submissionBinder.forField(design.averageMass).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(AVERAGE_MASS);
    design.toxicity.addStyleName(TOXICITY);
    design.toxicity.setCaption(resources.message(TOXICITY));
    submissionBinder.forField(design.toxicity).withNullRepresentation("").bind(TOXICITY);
    design.lightSensitive.addStyleName(LIGHT_SENSITIVE);
    design.lightSensitive.setCaption(resources.message(LIGHT_SENSITIVE));
    submissionBinder.forField(design.lightSensitive).bind(LIGHT_SENSITIVE);
    design.storageTemperature.addStyleName(STORAGE_TEMPERATURE);
    design.storageTemperature.setCaption(resources.message(STORAGE_TEMPERATURE));
    design.storageTemperature.setItems(StorageTemperature.values());
    design.storageTemperature
        .setItemCaptionGenerator(storageTemperature -> storageTemperature.getLabel(locale));
    design.storageTemperature.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.storageTemperature)
        .withValidator(requiredIfVisible(design.storageTemperature)).bind(STORAGE_TEMPERATURE);
    design.sampleContainerType.addStyleName(SAMPLES_CONTAINER_TYPE);
    design.sampleContainerType.setCaption(resources.message(SAMPLES_CONTAINER_TYPE));
    design.sampleContainerType.setItems(SampleContainerType.values());
    design.sampleContainerType
        .setItemCaptionGenerator(sampleContainerType -> sampleContainerType.getLabel(locale));
    design.sampleContainerType.setRequiredIndicatorVisible(true);
    design.sampleContainerType.addValueChangeListener(e -> updateVisible());
    design.plateName.addStyleName(PLATE + "-" + PLATE_NAME);
    design.plateName.setCaption(resources.message(PLATE + "." + PLATE_NAME));
    design.plateName.setRequiredIndicatorVisible(true);
    plateBinder.forField(design.plateName).withValidator(requiredTextIfVisible(design.plateName))
        .withNullRepresentation("").withValidator(validatePlateName()).bind(PLATE_NAME);
    design.samplesLabel.addStyleName(SAMPLES_LABEL);
    design.samplesLabel.setCaption(resources.message(SAMPLES_LABEL));
    design.samples.addStyleName(SAMPLES);
    design.samples.addStyleName(COMPONENTS);
    design.samples.setDataProvider(samplesDataProvider);
    design.samples.addColumn(sample -> sampleNameField(sample), new ComponentRenderer())
        .setId(SAMPLE_NAME).setCaption(resources.message(SAMPLE_NAME)).setWidth(230)
        .setSortable(false);
    design.samples.addColumn(sample -> sampleNumberProteinField(sample), new ComponentRenderer())
        .setId(SAMPLE_NUMBER_PROTEIN).setCaption(resources.message(SAMPLE_NUMBER_PROTEIN))
        .setWidth(230).setSortable(false);
    design.samples.addColumn(sample -> proteinWeightField(sample), new ComponentRenderer())
        .setId(PROTEIN_WEIGHT).setCaption(resources.message(PROTEIN_WEIGHT)).setWidth(230)
        .setSortable(false);
    design.fillSamples.addStyleName(FILL_SAMPLES);
    design.fillSamples.addStyleName(BUTTON_SKIP_ROW);
    design.fillSamples.setCaption(resources.message(FILL_SAMPLES));
    design.fillSamples.setIcon(VaadinIcons.ARROW_DOWN);
    design.fillSamples.addClickListener(e -> fillSamples());
    view.plateComponent.addStyleName(SAMPLES_PLATE);
  }

  private TextField sampleNameField(SubmissionSample sample) {
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
          .withNullRepresentation("").withValidator(validateSampleName(false)).bind(SAMPLE_NAME);
      sampleBinders.put(sample, binder);
      sampleNameFields.put(sample, field);
      return field;
    }
  }

  private TextField sampleNumberProteinField(SubmissionSample sample) {
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
          .withValidator(requiredTextIf(n -> design.service.getValue() == INTACT_PROTEIN))
          .withNullRepresentation("")
          .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
          .bind(SAMPLE_NUMBER_PROTEIN);
      sampleBinders.put(sample, binder);
      sampleNumberProteinFields.put(sample, field);
      return field;
    }
  }

  private TextField proteinWeightField(SubmissionSample sample) {
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
          .withValidator(requiredTextIf(n -> design.service.getValue() == INTACT_PROTEIN))
          .withNullRepresentation("")
          .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
          .bind(PROTEIN_WEIGHT);
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
    design.experience.addStyleName(EXPERIENCE);
    design.experience.setCaption(resources.message(EXPERIENCE));
    design.experience.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.experience)
        .withValidator(requiredTextIfVisible(design.experience)).withNullRepresentation("")
        .bind(EXPERIENCE);
    design.experienceGoal.addStyleName(EXPERIENCE_GOAL);
    design.experienceGoal.setCaption(resources.message(EXPERIENCE_GOAL));
    submissionBinder.forField(design.experienceGoal).withNullRepresentation("")
        .bind(EXPERIENCE_GOAL);
    design.taxonomy.addStyleName(TAXONOMY);
    design.taxonomy.setCaption(resources.message(TAXONOMY));
    design.taxonomy.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.taxonomy).withValidator(requiredTextIfVisible(design.taxonomy))
        .withNullRepresentation("").bind(TAXONOMY);
    design.proteinName.addStyleName(PROTEIN_NAME);
    design.proteinName.setCaption(resources.message(PROTEIN_NAME));
    submissionBinder.forField(design.proteinName).withNullRepresentation("").bind(PROTEIN_NAME);
    design.proteinWeight.addStyleName(PROTEIN_WEIGHT);
    design.proteinWeight.setCaption(resources.message(PROTEIN_WEIGHT));
    firstSampleBinder.forField(design.proteinWeight).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(PROTEIN_WEIGHT);
    design.postTranslationModification.addStyleName(POST_TRANSLATION_MODIFICATION);
    design.postTranslationModification.setCaption(resources.message(POST_TRANSLATION_MODIFICATION));
    submissionBinder.forField(design.postTranslationModification).withNullRepresentation("")
        .bind(POST_TRANSLATION_MODIFICATION);
    design.sampleQuantity.addStyleName(SAMPLE_QUANTITY);
    design.sampleQuantity.setCaption(resources.message(SAMPLE_QUANTITY));
    design.sampleQuantity.setPlaceholder(resources.message(SAMPLE_QUANTITY + "." + EXAMPLE));
    design.sampleQuantity.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleQuantity)
        .withValidator(requiredTextIfVisible(design.sampleQuantity)).withNullRepresentation("")
        .bind(SAMPLE_QUANTITY);
    design.sampleVolume.addStyleName(SAMPLE_VOLUME);
    design.sampleVolume.setCaption(resources.message(SAMPLE_VOLUME));
    design.sampleVolume.setPlaceholder(resources.message(SAMPLE_VOLUME + "." + EXAMPLE));
    design.sampleVolume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleVolume)
        .withValidator(requiredTextIfVisible(design.sampleVolume)).withNullRepresentation("")
        .bind(SAMPLE_VOLUME);
  }

  private void prepareStandardsComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.standardsPanel.addStyleName(STANDARDS_PANEL);
    design.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    design.standardCount.addStyleName(STANDARD_COUNT);
    design.standardCount.setCaption(resources.message(STANDARD_COUNT));
    standardCountBinder.forField(design.standardCount).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_STANDARD_COUNT), 0, MAX_STANDARD_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.standardCount
        .addValueChangeListener(e -> updateStandardsTable(design.standardCount.getValue()));
    design.standardCount.addValueChangeListener(e -> updateStandardsTable(e.getValue()));
    design.standards.addStyleName(STANDARDS);
    design.standards.addStyleName(COMPONENTS);
    design.standards.setDataProvider(standardsDataProvider);
    design.standards.addColumn(standard -> standardNameTextField(standard), new ComponentRenderer())
        .setId(STANDARD_NAME).setCaption(resources.message(STANDARDS + "." + STANDARD_NAME))
        .setSortable(false);
    design.standards
        .addColumn(standard -> standardQuantityTextField(standard), new ComponentRenderer())
        .setId(STANDARD_QUANTITY).setCaption(resources.message(STANDARDS + "." + STANDARD_QUANTITY))
        .setSortable(false);
    design.standards
        .addColumn(standard -> standardCommentTextField(standard), new ComponentRenderer())
        .setId(STANDARD_COMMENT).setCaption(resources.message(STANDARDS + "." + STANDARD_COMMENT))
        .setSortable(false);
    design.fillStandards.addStyleName(FILL_STANDARDS);
    design.fillStandards.addStyleName(BUTTON_SKIP_ROW);
    design.fillStandards.setCaption(resources.message(FILL_STANDARDS));
    design.fillStandards.setIcon(VaadinIcons.ARROW_DOWN);
    design.fillStandards.addClickListener(e -> fillStandards());
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
      field.addStyleName(STANDARD_NAME);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_NAME);
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
      field.addStyleName(STANDARD_QUANTITY);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setPlaceholder(resources.message(STANDARDS + "." + STANDARD_QUANTITY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(STANDARD_QUANTITY);
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
      field.addStyleName(STANDARD_COMMENT);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).withNullRepresentation("").bind(STANDARD_COMMENT);
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
    design.contaminantCount.addStyleName(CONTAMINANT_COUNT);
    design.contaminantCount.setCaption(resources.message(CONTAMINANT_COUNT));
    contaminantCountBinder.forField(design.contaminantCount).withNullRepresentation("0")
        .withConverter(new StringToIntegerConverter(generalResources.message(INVALID_INTEGER)))
        .withValidator(new IntegerRangeValidator(
            generalResources.message(OUT_OF_RANGE, 0, MAX_CONTAMINANT_COUNT), 0,
            MAX_CONTAMINANT_COUNT))
        .bind(ItemCount::getCount, ItemCount::setCount);
    design.contaminantCount
        .addValueChangeListener(e -> updateContaminantsTable(design.contaminantCount.getValue()));
    design.contaminantCount.addValueChangeListener(e -> updateContaminantsTable(e.getValue()));
    design.contaminants.addStyleName(CONTAMINANTS);
    design.contaminants.addStyleName(COMPONENTS);
    design.contaminants.setDataProvider(contaminantsDataProvider);
    design.contaminants
        .addColumn(contaminant -> contaminantNameTextField(contaminant), new ComponentRenderer())
        .setId(CONTAMINANT_NAME)
        .setCaption(resources.message(CONTAMINANTS + "." + CONTAMINANT_NAME)).setSortable(false);
    design.contaminants
        .addColumn(contaminant -> contaminantQuantityTextField(contaminant),
            new ComponentRenderer())
        .setId(CONTAMINANT_QUANTITY)
        .setCaption(resources.message(CONTAMINANTS + "." + CONTAMINANT_QUANTITY))
        .setSortable(false);
    design.contaminants
        .addColumn(contaminant -> contaminantCommentTextField(contaminant), new ComponentRenderer())
        .setId(CONTAMINANT_COMMENT)
        .setCaption(resources.message(CONTAMINANTS + "." + CONTAMINANT_COMMENT)).setSortable(false);
    design.fillContaminants.addStyleName(FILL_CONTAMINANTS);
    design.fillContaminants.addStyleName(BUTTON_SKIP_ROW);
    design.fillContaminants.setCaption(resources.message(FILL_CONTAMINANTS));
    design.fillContaminants.setIcon(VaadinIcons.ARROW_DOWN);
    design.fillContaminants.addClickListener(e -> fillContaminants());
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
      field.addStyleName(CONTAMINANT_NAME);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(CONTAMINANT_NAME);
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
      field.addStyleName(CONTAMINANT_QUANTITY);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      field.setPlaceholder(
          resources.message(CONTAMINANTS + "." + CONTAMINANT_QUANTITY + "." + EXAMPLE));
      binder.forField(field).asRequired(generalResources.message(REQUIRED))
          .withNullRepresentation("").bind(CONTAMINANT_QUANTITY);
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
      field.addStyleName(CONTAMINANT_COMMENT);
      field.addStyleName(ValoTheme.TEXTFIELD_TINY);
      field.setReadOnly(readOnly);
      binder.forField(field).withNullRepresentation("").bind(CONTAMINANT_COMMENT);
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
    design.separation.addStyleName(SEPARATION);
    design.separation.setCaption(resources.message(SEPARATION));
    design.separation.setEmptySelectionAllowed(false);
    design.separation.setItems(GelSeparation.values());
    design.separation.setItemCaptionGenerator(separation -> separation.getLabel(locale));
    design.separation.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.separation).withValidator(requiredIfVisible(design.separation))
        .bind(SEPARATION);
    design.thickness.addStyleName(THICKNESS);
    design.thickness.setCaption(resources.message(THICKNESS));
    design.thickness.setEmptySelectionAllowed(false);
    design.thickness.setItems(GelThickness.values());
    design.thickness.setItemCaptionGenerator(thickness -> thickness.getLabel(locale));
    design.thickness.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.thickness).withValidator(requiredIfVisible(design.thickness))
        .bind(THICKNESS);
    design.coloration.addStyleName(COLORATION);
    design.coloration.setCaption(resources.message(COLORATION));
    design.coloration.setEmptySelectionAllowed(true);
    design.coloration.setEmptySelectionCaption(GelColoration.getNullLabel(locale));
    design.coloration.setItems(GelColoration.values());
    design.coloration.setItemCaptionGenerator(coloration -> coloration.getLabel(locale));
    design.coloration.addValueChangeListener(e -> updateVisible());
    submissionBinder.forField(design.coloration).bind(COLORATION);
    design.otherColoration.addStyleName(OTHER_COLORATION);
    design.otherColoration.setCaption(resources.message(OTHER_COLORATION));
    design.otherColoration.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.otherColoration)
        .withValidator(requiredTextIfVisible(design.otherColoration)).withNullRepresentation("")
        .bind(OTHER_COLORATION);
    design.developmentTime.addStyleName(DEVELOPMENT_TIME);
    design.developmentTime.setCaption(resources.message(DEVELOPMENT_TIME));
    design.developmentTime.setPlaceholder(resources.message(DEVELOPMENT_TIME + "." + EXAMPLE));
    submissionBinder.forField(design.developmentTime).withNullRepresentation("")
        .bind(DEVELOPMENT_TIME);
    design.decoloration.addStyleName(DECOLORATION);
    design.decoloration.setCaption(resources.message(DECOLORATION));
    submissionBinder.forField(design.decoloration).bind(DECOLORATION);
    design.weightMarkerQuantity.addStyleName(WEIGHT_MARKER_QUANTITY);
    design.weightMarkerQuantity.setCaption(resources.message(WEIGHT_MARKER_QUANTITY));
    design.weightMarkerQuantity
        .setPlaceholder(resources.message(WEIGHT_MARKER_QUANTITY + "." + EXAMPLE));
    submissionBinder.forField(design.weightMarkerQuantity).withNullRepresentation("")
        .withConverter(new StringToDoubleConverter(generalResources.message(INVALID_NUMBER)))
        .bind(WEIGHT_MARKER_QUANTITY);
    design.proteinQuantity.addStyleName(PROTEIN_QUANTITY);
    design.proteinQuantity.setCaption(resources.message(PROTEIN_QUANTITY));
    design.proteinQuantity.setPlaceholder(resources.message(PROTEIN_QUANTITY + "." + EXAMPLE));
    submissionBinder.forField(design.proteinQuantity).withNullRepresentation("")
        .bind(PROTEIN_QUANTITY);
  }

  private void prepareServicesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.servicesPanel.addStyleName(SERVICES_PANEL);
    design.servicesPanel.setCaption(resources.message(SERVICES_PANEL));
    design.digestion.addStyleName(DIGESTION);
    design.digestion.setCaption(resources.message(DIGESTION));
    design.digestion.setItemCaptionGenerator(digestion -> digestion.getLabel(locale));
    design.digestion.setItems(ProteolyticDigestion.values());
    design.digestion.setRequiredIndicatorVisible(true);
    design.digestion.addValueChangeListener(e -> updateVisible());
    submissionBinder.forField(design.digestion).withValidator(requiredIfVisible(design.digestion))
        .bind(DIGESTION);
    design.usedProteolyticDigestionMethod.addStyleName(USED_DIGESTION);
    design.usedProteolyticDigestionMethod.setCaption(resources.message(USED_DIGESTION));
    design.usedProteolyticDigestionMethod.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.usedProteolyticDigestionMethod)
        .withValidator(requiredTextIfVisible(design.usedProteolyticDigestionMethod))
        .withNullRepresentation("").bind(USED_DIGESTION);
    design.otherProteolyticDigestionMethod.addStyleName(OTHER_DIGESTION);
    design.otherProteolyticDigestionMethod.setCaption(resources.message(OTHER_DIGESTION));
    design.otherProteolyticDigestionMethod.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.otherProteolyticDigestionMethod)
        .withValidator(requiredTextIfVisible(design.otherProteolyticDigestionMethod))
        .withNullRepresentation("").bind(OTHER_DIGESTION);
    design.otherProteolyticDigestionMethodNote
        .setValue(resources.message(OTHER_DIGESTION + ".note"));
    design.enrichment.addStyleName(ENRICHEMENT);
    design.enrichment.setCaption(resources.message(ENRICHEMENT));
    design.enrichment.setValue(resources.message(ENRICHEMENT + ".value"));
    design.exclusions.addStyleName(EXCLUSIONS);
    design.exclusions.setCaption(resources.message(EXCLUSIONS));
    design.exclusions.setValue(resources.message(EXCLUSIONS + ".value"));
    design.injectionType.addStyleName(INJECTION_TYPE);
    design.injectionType.setCaption(resources.message(INJECTION_TYPE));
    design.injectionType.setItems(InjectionType.values());
    design.injectionType.setItemCaptionGenerator(injectionType -> injectionType.getLabel(locale));
    design.injectionType.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.injectionType)
        .withValidator(requiredIfVisible(design.injectionType)).bind(INJECTION_TYPE);
    design.source.addStyleName(SOURCE);
    design.source.setCaption(resources.message(SOURCE));
    design.source.setItems(MassDetectionInstrumentSource.availables());
    design.source.setItemCaptionGenerator(source -> source.getLabel(locale));
    design.source.setItemEnabledProvider(source -> source.available);
    design.source.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.source).withValidator(requiredIfVisible(design.source))
        .bind(SOURCE);
    design.proteinContent.addStyleName(PROTEIN_CONTENT);
    design.proteinContent.setCaption(resources.message(PROTEIN_CONTENT));
    design.proteinContent.setItems(ProteinContent.values());
    design.proteinContent
        .setItemCaptionGenerator(proteinContent -> proteinContent.getLabel(locale));
    design.proteinContent.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.proteinContent)
        .withValidator(requiredIfVisible(design.proteinContent)).bind(PROTEIN_CONTENT);
    design.instrument.addStyleName(INSTRUMENT);
    design.instrument.setCaption(resources.message(INSTRUMENT));
    design.instrument.setItems(instrumentValues());
    design.instrument
        .setItemCaptionGenerator(instrument -> instrument != null ? instrument.getLabel(locale)
            : MassDetectionInstrument.getNullLabel(locale));
    design.instrument
        .setItemEnabledProvider(instrument -> instrument != null ? instrument.userChoice : true);
    submissionBinder.forField(design.instrument)
        .withNullRepresentation(MassDetectionInstrument.NULL).bind(INSTRUMENT);
    design.proteinIdentification.addStyleName(PROTEIN_IDENTIFICATION);
    design.proteinIdentification.setCaption(resources.message(PROTEIN_IDENTIFICATION));
    design.proteinIdentification.setItems(ProteinIdentification.availables());
    design.proteinIdentification
        .setItemCaptionGenerator(proteinIdentification -> proteinIdentification.getLabel(locale));
    design.proteinIdentification
        .setItemEnabledProvider(proteinIdentification -> proteinIdentification.available);
    design.proteinIdentification.setRequiredIndicatorVisible(true);
    design.proteinIdentification.addValueChangeListener(e -> updateVisible());
    submissionBinder.forField(design.proteinIdentification)
        .withValidator(requiredIfVisible(design.proteinIdentification))
        .bind(PROTEIN_IDENTIFICATION);
    design.proteinIdentificationLink.addStyleName(PROTEIN_IDENTIFICATION_LINK);
    design.proteinIdentificationLink.setCaption(resources.message(PROTEIN_IDENTIFICATION_LINK));
    design.proteinIdentificationLink.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.proteinIdentificationLink)
        .withValidator(requiredTextIfVisible(design.proteinIdentificationLink))
        .withNullRepresentation("").bind(PROTEIN_IDENTIFICATION_LINK);
    design.quantification.addStyleName(QUANTIFICATION);
    design.quantification.setCaption(resources.message(QUANTIFICATION));
    design.quantification.setItems(quantificationValues());
    design.quantification.setItemCaptionGenerator(
        quantification -> quantification != null ? quantification.getLabel(locale)
            : Quantification.getNullLabel(locale));
    design.quantificationComment.setRequiredIndicatorVisible(true);
    design.quantification.addValueChangeListener(e -> updateVisible());
    design.quantification.addValueChangeListener(e -> {
      if (e.getValue() != null) {
        try {
          design.quantificationComment
              .setCaption(resources.message(property(QUANTIFICATION_COMMENT, e.getValue().name())));
        } catch (MissingResourceException exception) {
          design.quantificationComment.setCaption(resources.message(QUANTIFICATION_COMMENT));
        }
        try {
          design.quantificationComment.setPlaceholder(
              resources.message(property(QUANTIFICATION_COMMENT, EXAMPLE, e.getValue().name())));
        } catch (MissingResourceException exception) {
          design.quantificationComment
              .setPlaceholder(resources.message(property(QUANTIFICATION_COMMENT, EXAMPLE)));
        }
      }
    });
    submissionBinder.forField(design.quantification).withNullRepresentation(Quantification.NULL)
        .bind(QUANTIFICATION);
    design.quantificationComment.addStyleName(QUANTIFICATION_COMMENT);
    design.quantificationComment.setCaption(resources.message(QUANTIFICATION_COMMENT));
    design.quantificationComment
        .setPlaceholder(resources.message(QUANTIFICATION_COMMENT + "." + EXAMPLE));
    submissionBinder.forField(design.quantificationComment).withValidator((value, context) -> {
      if (value.isEmpty() && (design.quantification.getValue() == SILAC
          || design.quantification.getValue() == TMT)) {
        return ValidationResult.error(generalResources.message(REQUIRED));
      }
      return ValidationResult.ok();
    }).withNullRepresentation("").bind(QUANTIFICATION_COMMENT);
    design.highResolution.addStyleName(HIGH_RESOLUTION);
    design.highResolution.setCaption(resources.message(HIGH_RESOLUTION));
    design.highResolution.setItems(false, true);
    design.highResolution
        .setItemCaptionGenerator(value -> resources.message(HIGH_RESOLUTION + "." + value));
    design.highResolution.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.highResolution)
        .withValidator(requiredIfVisible(design.highResolution)).bind(HIGH_RESOLUTION);
    design.solventsLayout.addStyleName(REQUIRED);
    design.solventsLayout.setCaption(resources.message(SOLVENTS));
    design.acetonitrileSolvents.addStyleName(SOLVENTS + "-" + Solvent.ACETONITRILE.name());
    design.acetonitrileSolvents.setCaption(Solvent.ACETONITRILE.getLabel(locale));
    design.methanolSolvents.addStyleName(SOLVENTS + "-" + Solvent.METHANOL.name());
    design.methanolSolvents.setCaption(Solvent.METHANOL.getLabel(locale));
    design.chclSolvents.addStyleName(SOLVENTS + "-" + Solvent.CHCL3.name());
    design.chclSolvents.setCaption(Solvent.CHCL3.getLabel(locale));
    design.chclSolvents.setCaptionAsHtml(true);
    design.otherSolvents.addStyleName(SOLVENTS + "-" + Solvent.OTHER.name());
    design.otherSolvents.setCaption(Solvent.OTHER.getLabel(locale));
    design.otherSolvent.setCaption(resources.message(OTHER_SOLVENT));
    design.otherSolvent.addStyleName(OTHER_SOLVENT);
    design.otherSolvent.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    design.otherSolvent.setRequiredIndicatorVisible(true);
    design.otherSolvents.addValueChangeListener(e -> updateVisible());
    submissionBinder.forField(design.otherSolvent)
        .withValidator(requiredTextIfVisible(design.otherSolvent)).withNullRepresentation("")
        .bind(OTHER_SOLVENT);
    design.otherSolventNote.addStyleName(OTHER_SOLVENT_NOTE);
    design.otherSolventNote.setValue(resources.message(OTHER_SOLVENT_NOTE));
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

  private void updateVisible() {
    final Service service = design.service.getValue();
    final SampleSupport support = design.sampleSupport.getValue();
    design.sampleTypeWarning.setVisible(!readOnly);
    design.inactiveWarning.setVisible(!readOnly);
    design.sampleSupport.setItemEnabledProvider(value -> value != GEL || service == LC_MS_MS);
    design.solutionSolvent
        .setVisible(service == SMALL_MOLECULE && support == SampleSupport.SOLUTION);
    design.sampleName.setVisible(service == SMALL_MOLECULE);
    design.formula.setVisible(service == SMALL_MOLECULE);
    design.monoisotopicMass.setVisible(service == SMALL_MOLECULE);
    design.averageMass.setVisible(service == SMALL_MOLECULE);
    design.toxicity.setVisible(service == SMALL_MOLECULE);
    design.lightSensitive.setVisible(service == SMALL_MOLECULE);
    design.storageTemperature.setVisible(service == SMALL_MOLECULE);
    design.sampleCount.setVisible(service != SMALL_MOLECULE);
    design.sampleContainerType.setVisible(service == LC_MS_MS);
    design.plateName
        .setVisible(service == LC_MS_MS && design.sampleContainerType.getValue() == WELL);
    design.samplesLabel.setVisible(service != SMALL_MOLECULE);
    design.samplesLayout.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerType.getValue() != WELL));
    design.samples.setVisible(service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerType.getValue() != WELL));
    design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).setHidden(service != INTACT_PROTEIN);
    design.samples.getColumn(PROTEIN_WEIGHT).setHidden(service != INTACT_PROTEIN);
    design.samples.setWidth((float) design.samples.getColumns().stream()
        .filter(column -> !column.isHidden()).mapToDouble(column -> column.getWidth()).sum(),
        Unit.PIXELS);
    design.fillSamples.setVisible((service == INTACT_PROTEIN
        || (service == LC_MS_MS && design.sampleContainerType.getValue() != WELL)) && !readOnly);
    design.samplesPlateContainer
        .setVisible(service == LC_MS_MS && design.sampleContainerType.getValue() == WELL);
    design.experiencePanel.setVisible(service != SMALL_MOLECULE);
    design.experience.setVisible(service != SMALL_MOLECULE);
    design.experienceGoal.setVisible(service != SMALL_MOLECULE);
    design.taxonomy.setVisible(service != SMALL_MOLECULE);
    design.proteinName.setVisible(service != SMALL_MOLECULE);
    design.proteinWeight.setVisible(service == LC_MS_MS);
    design.postTranslationModification.setVisible(service != SMALL_MOLECULE);
    design.sampleQuantity
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.sampleVolume.setVisible(service != SMALL_MOLECULE && support == SOLUTION);
    design.standardsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.standardCount
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.standards
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.fillStandards.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && !readOnly);
    design.contaminantsPanel
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.contaminantCount
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.contaminants
        .setVisible(service != SMALL_MOLECULE && (support == SOLUTION || support == DRY));
    design.fillContaminants.setVisible(
        service != SMALL_MOLECULE && (support == SOLUTION || support == DRY) && !readOnly);
    design.gelPanel.setVisible(service == LC_MS_MS && support == GEL);
    design.separation.setVisible(service == LC_MS_MS && support == GEL);
    design.thickness.setVisible(service == LC_MS_MS && support == GEL);
    design.coloration.setVisible(service == LC_MS_MS && support == GEL);
    design.otherColoration.setVisible(service == LC_MS_MS && support == GEL
        && design.coloration.getValue() == GelColoration.OTHER);
    design.developmentTime.setVisible(service == LC_MS_MS && support == GEL);
    design.decoloration.setVisible(service == LC_MS_MS && support == GEL);
    design.weightMarkerQuantity.setVisible(service == LC_MS_MS && support == GEL);
    design.proteinQuantity.setVisible(service == LC_MS_MS && support == GEL);
    design.digestion.setVisible(service == LC_MS_MS);
    design.usedProteolyticDigestionMethod
        .setVisible(design.digestion.isVisible() && design.digestion.getValue() == DIGESTED);
    design.otherProteolyticDigestionMethod.setVisible(
        design.digestion.isVisible() && design.digestion.getValue() == ProteolyticDigestion.OTHER);
    design.otherProteolyticDigestionMethodNote.setVisible(
        design.digestion.isVisible() && design.digestion.getValue() == ProteolyticDigestion.OTHER);
    design.enrichment.setVisible(!readOnly && service == LC_MS_MS);
    design.exclusions.setVisible(!readOnly && service == LC_MS_MS);
    design.injectionType.setVisible(service == INTACT_PROTEIN);
    design.source.setVisible(service == INTACT_PROTEIN);
    design.proteinContent.setVisible(service == LC_MS_MS);
    design.instrument.setVisible(service != SMALL_MOLECULE);
    design.proteinIdentification.setVisible(service == LC_MS_MS);
    design.proteinIdentificationLink.setVisible(design.proteinIdentification.isVisible()
        && design.proteinIdentification.getValue() == ProteinIdentification.OTHER);
    design.quantification.setVisible(service == LC_MS_MS);
    design.quantificationComment
        .setVisible(service == LC_MS_MS && (design.quantification.getValue() == Quantification.SILAC
            || design.quantification.getValue() == Quantification.TMT));
    design.highResolution.setVisible(service == SMALL_MOLECULE);
    design.solventsLayout.setVisible(service == SMALL_MOLECULE);
    design.acetonitrileSolvents.setVisible(service == SMALL_MOLECULE);
    design.methanolSolvents.setVisible(service == SMALL_MOLECULE);
    design.chclSolvents.setVisible(service == SMALL_MOLECULE);
    design.otherSolvents.setVisible(service == SMALL_MOLECULE);
    design.otherSolvent.setVisible(service == SMALL_MOLECULE && design.otherSolvents.getValue());
    design.otherSolventNote
        .setVisible(service == SMALL_MOLECULE && design.otherSolvents.getValue());
    design.structureFile.setVisible(service == SMALL_MOLECULE);
    design.gelImageFile.setVisible(service == LC_MS_MS && support == GEL);
    view.filesUploader.setVisible(!readOnly);
    design.buttons.setVisible(!readOnly);
  }

  private void updateReadOnly() {
    design.service.setReadOnly(readOnly);
    design.sampleSupport.setReadOnly(readOnly);
    design.solutionSolvent.setReadOnly(readOnly);
    design.sampleName.setReadOnly(readOnly);
    design.formula.setReadOnly(readOnly);
    design.monoisotopicMass.setReadOnly(readOnly);
    design.averageMass.setReadOnly(readOnly);
    design.toxicity.setReadOnly(readOnly);
    design.lightSensitive.setReadOnly(readOnly);
    design.storageTemperature.setReadOnly(readOnly);
    design.sampleContainerType.setReadOnly(readOnly);
    design.plateName.setReadOnly(readOnly);
    design.sampleCount.setReadOnly(readOnly);
    sampleBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.fillSamples.setVisible(!readOnly);
    view.plateComponent.setReadOnly(readOnly);
    design.experience.setReadOnly(readOnly);
    design.experienceGoal.setReadOnly(readOnly);
    design.taxonomy.setReadOnly(readOnly);
    design.proteinName.setReadOnly(readOnly);
    design.proteinWeight.setReadOnly(readOnly);
    design.postTranslationModification.setReadOnly(readOnly);
    design.sampleQuantity.setReadOnly(readOnly);
    design.sampleVolume.setReadOnly(readOnly);
    design.standardCount.setReadOnly(readOnly);
    standardBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.contaminantCount.setReadOnly(readOnly);
    contaminantBinders.values().forEach(binder -> binder.setReadOnly(readOnly));
    design.separation.setReadOnly(readOnly);
    design.thickness.setReadOnly(readOnly);
    design.coloration.setReadOnly(readOnly);
    design.otherColoration.setReadOnly(readOnly);
    design.developmentTime.setReadOnly(readOnly);
    design.decoloration.setReadOnly(readOnly);
    design.weightMarkerQuantity.setReadOnly(readOnly);
    design.proteinQuantity.setReadOnly(readOnly);
    design.digestion.setReadOnly(readOnly);
    design.usedProteolyticDigestionMethod.setReadOnly(readOnly);
    design.otherProteolyticDigestionMethod.setReadOnly(readOnly);
    design.injectionType.setReadOnly(readOnly);
    design.source.setReadOnly(readOnly);
    design.proteinContent.setReadOnly(readOnly);
    design.instrument.setReadOnly(readOnly);
    design.proteinIdentification.setReadOnly(readOnly);
    design.proteinIdentificationLink.setReadOnly(readOnly);
    design.quantification.setReadOnly(readOnly);
    design.quantificationComment.setReadOnly(readOnly);
    design.highResolution.setReadOnly(readOnly);
    design.acetonitrileSolvents.setReadOnly(readOnly);
    design.methanolSolvents.setReadOnly(readOnly);
    design.chclSolvents.setReadOnly(readOnly);
    design.otherSolvents.setReadOnly(readOnly);
    design.otherSolvent.setReadOnly(readOnly);
    design.comment.setReadOnly(readOnly);
    design.files.getColumn(REMOVE_FILE).setHidden(readOnly);
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
      design.standardsLayout.setVisible(count > 0);
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
      design.contaminantsLayout.setVisible(count > 0);
      contaminantsDataProvider.refreshAll();
    }
  }

  private void fillSamples() {
    List<SubmissionSample> samples =
        VaadinUtils.gridItems(design.samples).collect(Collectors.toList());
    if (!samples.isEmpty()) {
      SubmissionSample first = samples.get(0);
      String name = sampleNameFields.get(first).getValue();
      String numberProtein = sampleNumberProteinFields.get(first).getValue();
      String molecularWeight = sampleMolecularWeightFields.get(first).getValue();
      for (SubmissionSample sample : samples.subList(1, samples.size())) {
        name = Named.incrementLastNumber(name);
        sampleNameFields.get(sample).setValue(name);
        if (design.service.getValue() == INTACT_PROTEIN) {
          sampleNumberProteinFields.get(sample).setValue(numberProtein);
          sampleMolecularWeightFields.get(sample).setValue(molecularWeight);
        }
      }
    }
  }

  private void fillStandards() {
    List<Standard> standards = VaadinUtils.gridItems(design.standards).collect(Collectors.toList());
    if (!standards.isEmpty()) {
      Standard first = standards.get(0);
      String name = standardNameFields.get(first).getValue();
      String quantity = standardQuantityFields.get(first).getValue();
      String comment = standardCommentFields.get(first).getValue();
      for (Standard standard : standards.subList(1, standards.size())) {
        standardNameFields.get(standard).setValue(name);
        standardQuantityFields.get(standard).setValue(quantity);
        standardCommentFields.get(standard).setValue(comment);
      }
    }
  }

  private void fillContaminants() {
    List<Contaminant> contaminants =
        VaadinUtils.gridItems(design.contaminants).collect(Collectors.toList());
    if (!contaminants.isEmpty()) {
      Contaminant first = contaminants.get(0);
      String name = contaminantNameFields.get(first).getValue();
      String quantity = contaminantQuantityFields.get(first).getValue();
      String comment = contaminantCommentFields.get(first).getValue();
      for (Contaminant contaminant : contaminants.subList(1, contaminants.size())) {
        contaminantNameFields.get(contaminant).setValue(name);
        contaminantQuantityFields.get(contaminant).setValue(quantity);
        contaminantCommentFields.get(contaminant).setValue(comment);
      }
    }
  }

  private MultiFileUploadFileHandler fileHandler() {
    return (file, fileName, mimetype, length) -> {
      if (filesDataProvider.getItems().size() >= MAXIMUM_FILES_COUNT) {
        return;
      }

      ByteArrayOutputStream content = new ByteArrayOutputStream();
      try {
        Files.copy(file.toPath(), content);
      } catch (IOException e) {
        MessageResource resources = view.getResources();
        view.showError(resources.message(FILES + ".error", fileName));
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
      view.showWarning(resources.message(FILES + ".overMaximumCount", MAXIMUM_FILES_COUNT));
    }
  }

  private Validator<String> validateSampleName(boolean includeSampleNameInError) {
    return (value, context) -> {
      if (value == null || value.isEmpty()) {
        return ValidationResult.ok();
      }
      MessageResource generalResources = view.getGeneralResources();
      if (!Pattern.matches("\\w*", value)) {
        return ValidationResult.error(generalResources.message(ONLY_WORDS));
      }
      if (submissionSampleService.exists(value)) {
        if (submissionBinder.getBean().getId() == null
            || !submissionService.get(submissionBinder.getBean().getId()).getSamples().stream()
                .filter(sample -> sample.getName().equalsIgnoreCase(value)).findAny().isPresent()) {
          if (includeSampleNameInError) {
            MessageResource resources = view.getResources();
            return ValidationResult.error(resources.message(SAMPLE_NAME + ".exists", value));
          } else {
            return ValidationResult.error(generalResources.message(ALREADY_EXISTS));
          }
        }
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
      if (design.sampleContainerType.getValue() != WELL) {
        for (SubmissionSample samp : samplesDataProvider.getItems()) {
          valid &= validate(sampleBinders.get(samp));
        }
      } else {
        valid &= validate(plateBinder);
        valid &= validate(validateSampleName(true), view.plateComponent, plateSampleNames());
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
      }
    } else if (submission.getService() == Service.SMALL_MOLECULE) {
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
    MessageResource resources = view.getResources();
    Set<String> names = new HashSet<>();
    if (design.sampleContainerType.getValue() != WELL) {
      for (SubmissionSample sample : samplesDataProvider.getItems()) {
        if (!names.add(sample.getName())) {
          String error = resources.message(SAMPLE_NAME + ".duplicate", sample.getName());
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
          String error = resources.message(SAMPLE_NAME + ".duplicate", name);
          addError(new UserError(error), view.plateComponent);
          return ValidationResult.error(error);
        }
      }
      if (count < sampleCountBinder.getBean().getCount()) {
        String error =
            resources.message(SAMPLES + ".missing", sampleCountBinder.getBean().getCount());
        addError(new UserError(error), view.plateComponent);
        return ValidationResult.error(error);
      }
    }
    return ValidationResult.ok();

  }

  private ValidationResult validateSolvents() {
    design.solventsLayout.setComponentError(null);
    if (!design.acetonitrileSolvents.getValue() && !design.methanolSolvents.getValue()
        && !design.chclSolvents.getValue() && !design.otherSolvents.getValue()) {
      MessageResource resources = view.getResources();
      String error = resources.message(SOLVENTS + "." + REQUIRED);
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
    clearInvisibleField(design.solutionSolvent);
    clearInvisibleField(design.sampleName);
    clearInvisibleField(design.formula);
    clearInvisibleField(design.monoisotopicMass);
    clearInvisibleField(design.averageMass);
    clearInvisibleField(design.toxicity);
    clearInvisibleField(design.lightSensitive);
    clearInvisibleField(design.storageTemperature);
    clearInvisibleField(design.sampleCount);
    clearInvisibleField(design.sampleContainerType);
    clearInvisibleField(design.plateName);
    if (!design.samples.isVisible()) {
      sampleNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleNumberProteinFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      sampleMolecularWeightFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.experience);
    clearInvisibleField(design.experienceGoal);
    clearInvisibleField(design.taxonomy);
    clearInvisibleField(design.proteinName);
    clearInvisibleField(design.proteinWeight);
    clearInvisibleField(design.postTranslationModification);
    clearInvisibleField(design.sampleQuantity);
    clearInvisibleField(design.sampleVolume);
    clearInvisibleField(design.standardCount);
    if (!design.standards.isVisible()) {
      standardNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      standardCommentFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.contaminantCount);
    if (!design.contaminants.isVisible()) {
      contaminantNameFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantQuantityFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
      contaminantCommentFields.values().forEach(field -> field.setValue(field.getEmptyValue()));
    }
    clearInvisibleField(design.separation);
    clearInvisibleField(design.thickness);
    clearInvisibleField(design.coloration);
    clearInvisibleField(design.otherColoration);
    clearInvisibleField(design.developmentTime);
    clearInvisibleField(design.decoloration);
    clearInvisibleField(design.weightMarkerQuantity);
    clearInvisibleField(design.proteinQuantity);
    clearInvisibleField(design.digestion);
    clearInvisibleField(design.usedProteolyticDigestionMethod);
    clearInvisibleField(design.otherProteolyticDigestionMethod);
    clearInvisibleField(design.injectionType);
    clearInvisibleField(design.source);
    clearInvisibleField(design.proteinContent);
    clearInvisibleField(design.instrument);
    clearInvisibleField(design.proteinIdentification);
    clearInvisibleField(design.proteinIdentificationLink);
    clearInvisibleField(design.quantification);
    clearInvisibleField(design.quantificationComment);
    setValueIfInvisible(design.highResolution, false);
    clearInvisibleField(design.acetonitrileSolvents);
    clearInvisibleField(design.methanolSolvents);
    clearInvisibleField(design.chclSolvents);
    clearInvisibleField(design.otherSolvents);
    clearInvisibleField(design.otherSolvent);
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
        if (design.acetonitrileSolvents.getValue()) {
          submission.getSolvents().add(new SampleSolvent(ACETONITRILE));
        }
        if (design.methanolSolvents.getValue()) {
          submission.getSolvents().add(new SampleSolvent(METHANOL));
        }
        if (design.chclSolvents.getValue()) {
          submission.getSolvents().add(new SampleSolvent(CHCL3));
        }
        if (design.otherSolvents.getValue()) {
          submission.getSolvents().add(new SampleSolvent(Solvent.OTHER));
        }
      } else {
        submission.setStorageTemperature(null);
      }
      if (submission.getService() == INTACT_PROTEIN) {
        copySamplesToSubmission(submission);
      } else {
        submission.setSource(null);
      }
      if (firstSample.getSupport() != GEL) {
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
    if (submission.getService() == LC_MS_MS && design.sampleContainerType.getValue() == WELL) {
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
      design.service.setItems(Stream.concat(Service.availables().stream(), Stream.of(service)));
    }
    samplesDataProvider.getItems().clear();
    samplesDataProvider.getItems().addAll(samples);
    samplesDataProvider.refreshAll();
    design.sampleCount.setReadOnly(false);
    sampleCountBinder.setBean(new ItemCount(samples.size()));
    design.sampleContainerType.setReadOnly(false);
    design.sampleContainerType.setValue(firstSample.getOriginalContainer().getType());
    List<Standard> standards = firstSample.getStandards();
    if (standards == null) {
      standards = new ArrayList<>();
    }
    standardsDataProvider.getItems().clear();
    standardsDataProvider.getItems().addAll(standards);
    standardsDataProvider.refreshAll();
    design.standardCount.setReadOnly(false);
    standardCountBinder.setBean(new ItemCount(standards.size()));
    List<Contaminant> contaminants = firstSample.getContaminants();
    if (contaminants == null) {
      contaminants = new ArrayList<>();
    }
    contaminantsDataProvider.getItems().clear();
    contaminantsDataProvider.getItems().addAll(contaminants);
    contaminantsDataProvider.refreshAll();
    design.contaminantCount.setReadOnly(false);
    contaminantCountBinder.setBean(new ItemCount(contaminants.size()));
    MassDetectionInstrumentSource source = submission.getSource();
    if (source != null && !source.available) {
      design.source.setItems(
          Stream.concat(MassDetectionInstrumentSource.availables().stream(), Stream.of(source)));
    }
    MassDetectionInstrument instrument = submission.getMassDetectionInstrument();
    if (instrument != null && !instrument.userChoice) {
      design.instrument.setItems(Stream.concat(instrumentValues().stream(), Stream.of(instrument)));
    }
    ProteinIdentification proteinIdentification = submission.getProteinIdentification();
    if (proteinIdentification != null && !proteinIdentification.available) {
      design.proteinIdentification.setItems(Stream
          .concat(ProteinIdentification.availables().stream(), Stream.of(proteinIdentification)));
    }
    List<SampleSolvent> sampleSolvents = submission.getSolvents();
    if (sampleSolvents == null) {
      sampleSolvents = new ArrayList<>();
    }
    Set<Solvent> solvents =
        sampleSolvents.stream().map(ss -> ss.getSolvent()).collect(Collectors.toSet());
    design.acetonitrileSolvents.setReadOnly(false);
    design.acetonitrileSolvents.setValue(solvents.contains(Solvent.ACETONITRILE));
    design.acetonitrileSolvents.setReadOnly(false);
    design.methanolSolvents.setValue(solvents.contains(Solvent.METHANOL));
    design.acetonitrileSolvents.setReadOnly(false);
    design.chclSolvents.setValue(solvents.contains(Solvent.CHCL3));
    design.acetonitrileSolvents.setReadOnly(false);
    design.otherSolvents.setValue(solvents.contains(Solvent.OTHER));
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
