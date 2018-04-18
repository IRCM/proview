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
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
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
import static ca.qc.ircm.proview.vaadin.VaadinUtils.styleName;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.ONLY_WORDS;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;

import ca.qc.ircm.proview.Named;
import ca.qc.ircm.proview.files.web.GuidelinesWindow;
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
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.Standard;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthorizationService;
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
import com.vaadin.server.BrowserWindowOpener;
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
import java.nio.charset.StandardCharsets;
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
import javax.inject.Provider;
import javax.persistence.PersistenceException;

/**
 * Submission form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionFormPresenter implements BinderValidator {
  public static final String SAMPLE_TYPE_WARNING = "sampleTypeWarning";
  public static final String INACTIVE_WARNING = "inactive";
  public static final String GUIDELINES = "guidelines";
  public static final String SERVICE_PANEL = "servicePanel";
  public static final String SERVICE = "service";
  public static final String SAMPLES = submission.samples.getMetadata().getName();
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLE_TYPE = submissionSample.type.getMetadata().getName();
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
  public static final String EXPERIMENT_PANEL = "experimentPanel";
  public static final String EXPERIMENT = submission.experiment.getMetadata().getName();
  public static final String EXPERIMENT_GOAL = submission.goal.getMetadata().getName();
  public static final String TAXONOMY = submission.taxonomy.getMetadata().getName();
  public static final String PROTEIN_NAME = submission.protein.getMetadata().getName();
  public static final String PROTEIN_WEIGHT =
      submissionSample.molecularWeight.getMetadata().getName();
  public static final String POST_TRANSLATION_MODIFICATION =
      submission.postTranslationModification.getMetadata().getName();
  public static final String SAMPLE_QUANTITY = submissionSample.quantity.getMetadata().getName();
  public static final String SAMPLE_VOLUME = submissionSample.volume.getMetadata().getName();
  public static final String SAMPLE_VOLUME_BEADS = property(SAMPLE_VOLUME, "beads");
  public static final String STANDARDS_PANEL = "standardsPanel";
  public static final String CONTAMINANTS_PANEL = "contaminantsPanel";
  public static final String GEL_PANEL = "gelPanel";
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
      property(submission.otherSolvent.getMetadata().getName(), "note");
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
  public static final String PRINT = "print";
  public static final String PRINT_FILENAME = "submission-print-%s.html";
  public static final String PRINT_MIME = "text/html";
  public static final String UPDATE_ERROR = "updateError";
  public static final String EXAMPLE = "example";
  public static final String HIDE_REQUIRED_STYLE = "hide-required";
  private static final int MAX_SAMPLE_COUNT = 200;
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenter.class);
  private SubmissionForm view;
  private SubmissionFormDesign design;
  private boolean readOnly = false;
  private Binder<Submission> submissionBinder = new BeanValidationBinder<>(Submission.class);
  private Binder<SubmissionSample> firstSampleBinder =
      new BeanValidationBinder<>(SubmissionSample.class);
  private Binder<Plate> plateBinder = new BeanValidationBinder<>(Plate.class);
  private Binder<ItemCount> sampleCountBinder = new Binder<>(ItemCount.class);
  private ListDataProvider<SubmissionSample> samplesDataProvider =
      DataProvider.ofCollection(new ArrayList<>());
  private Map<SubmissionSample, Binder<SubmissionSample>> sampleBinders = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleNameFields = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleNumberProteinFields = new HashMap<>();
  private Map<SubmissionSample, TextField> sampleMolecularWeightFields = new HashMap<>();
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
  @Inject
  private Provider<GuidelinesWindow> guidelinesWindowProvider;

  protected SubmissionFormPresenter() {
  }

  protected SubmissionFormPresenter(SubmissionService submissionService,
      SubmissionSampleService submissionSampleService, PlateService plateService,
      AuthorizationService authorizationService,
      Provider<GuidelinesWindow> guidelinesWindowProvider) {
    this.submissionService = submissionService;
    this.submissionSampleService = submissionSampleService;
    this.plateService = plateService;
    this.authorizationService = authorizationService;
    this.guidelinesWindowProvider = guidelinesWindowProvider;
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
  }

  private void prepareComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.sampleTypeWarning.addStyleName(SAMPLE_TYPE_WARNING);
    design.sampleTypeWarning.setValue(resources.message(SAMPLE_TYPE_WARNING));
    design.inactiveWarning.addStyleName(INACTIVE_WARNING);
    design.inactiveWarning.setValue(resources.message(INACTIVE_WARNING));
    design.guidelines.addStyleName(GUIDELINES);
    design.guidelines.setCaption(resources.message(GUIDELINES));
    design.guidelines.addClickListener(e -> {
      GuidelinesWindow window = guidelinesWindowProvider.get();
      window.center();
      view.addWindow(window);
    });
    design.servicePanel.addStyleName(SERVICE_PANEL);
    design.servicePanel.addStyleName(REQUIRED);
    design.servicePanel.setCaption(resources.message(SERVICE));
    design.service.addStyleName(SERVICE);
    design.service.addStyleName(HIDE_REQUIRED_STYLE);
    design.service.setItems(Service.availables());
    design.service.setItemCaptionGenerator(service -> service.getLabel(locale));
    design.service.setItemEnabledProvider(service -> service.available);
    design.service.addValueChangeListener(e -> updateVisible());
    design.service.addValueChangeListener(e -> design.sampleType.getDataProvider().refreshAll());
    submissionBinder.forField(design.service).asRequired(generalResources.message(REQUIRED))
        .bind(SERVICE);
    prepareSamplesComponents();
    prepareExperimentComponents();
    design.standardsPanel.addStyleName(STANDARDS_PANEL);
    design.standardsPanel.setCaption(resources.message(STANDARDS_PANEL));
    design.contaminantsPanel.addStyleName(CONTAMINANTS_PANEL);
    design.contaminantsPanel.setCaption(resources.message(CONTAMINANTS_PANEL));
    design.gelPanel.addStyleName(GEL_PANEL);
    design.gelPanel.setCaption(resources.message(GEL_PANEL));
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
        .setId(FILE_FILENAME).setCaption(resources.message(property(FILES, FILE_FILENAME)))
        .setSortable(false);
    design.files.addColumn(file -> removeFileButton(file), new ComponentRenderer())
        .setId(REMOVE_FILE).setCaption(resources.message(property(FILES, REMOVE_FILE)))
        .setSortable(false);
    design.explanationPanel.addStyleName(EXPLANATION_PANEL);
    design.explanationPanel.addStyleName(REQUIRED);
    design.explanationPanel.setCaption(resources.message(EXPLANATION_PANEL));
    design.explanation.addStyleName(EXPLANATION);
    design.save.addStyleName(SAVE);
    design.save.setCaption(resources.message(SAVE));
    design.save.addClickListener(e -> save());
    design.print.addStyleName(PRINT);
    design.print.setCaption(resources.message(PRINT));
    preparePrint(submissionBinder.getBean());
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
      button.setCaption(resources.message(property(FILES, REMOVE_FILE)));
      button.addClickListener(e -> {
        filesDataProvider.getItems().remove(file);
        filesDataProvider.refreshAll();
      });
      fileRemoves.put(file, button);
      return button;
    }
  }

  private void preparePrint(Submission submission) {
    final Locale locale = view.getLocale();
    String content = submissionService.print(submission, locale);
    String filename = String.format(PRINT_FILENAME, submission != null ? submission.getId() : "");
    new ArrayList<>(design.print.getExtensions()).stream().forEach(ext -> ext.remove());
    StreamResource printResource = new StreamResource(
        () -> new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), filename);
    printResource.setMIMEType(PRINT_MIME);
    printResource.setCacheTime(0);
    BrowserWindowOpener opener = new BrowserWindowOpener(printResource);
    opener.extend(design.print);
  }

  private void prepareSamplesComponents() {
    final Locale locale = view.getLocale();
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.sampleType.addStyleName(SAMPLE_TYPE);
    design.sampleType.setCaption(resources.message(SAMPLE_TYPE));
    design.sampleType.setItemEnabledProvider(
        value -> design.service.getValue() == LC_MS_MS || (!value.isGel() && !value.isBeads()));
    design.sampleType.setItems(SampleType.values());
    design.sampleType.setItemCaptionGenerator(type -> type.getLabel(locale));
    design.sampleType.addValueChangeListener(e -> updateSampleType());
    firstSampleBinder.forField(design.sampleType).asRequired(generalResources.message(REQUIRED))
        .withValidator(validateSampleType()).bind(SAMPLE_TYPE);
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
    design.plateName.addStyleName(styleName(PLATE, PLATE_NAME));
    design.plateName.setCaption(resources.message(property(PLATE, PLATE_NAME)));
    design.plateName.setRequiredIndicatorVisible(true);
    plateBinder.forField(design.plateName).withValidator(requiredTextIfVisible(design.plateName))
        .withNullRepresentation("").withValidator(validatePlateName()).bind(PLATE_NAME);
    design.samplesLabel.addStyleName(SAMPLES_LABEL);
    design.samplesLabel.setCaption(resources.message(SAMPLES_LABEL));
    design.samples.addStyleName(SAMPLES);
    design.samples.addStyleName(COMPONENTS);
    design.samples.setDataProvider(samplesDataProvider);
    design.samples.addColumn(sample -> sampleNameField(sample), new ComponentRenderer())
        .setId(SAMPLE_NAME).setCaption(resources.message(SAMPLE_NAME)).setWidth(350)
        .setSortable(false);
    design.samples.addColumn(sample -> sampleNumberProteinField(sample), new ComponentRenderer())
        .setId(SAMPLE_NUMBER_PROTEIN).setCaption(resources.message(SAMPLE_NUMBER_PROTEIN))
        .setWidth(170).setSortable(false);
    design.samples.addColumn(sample -> proteinWeightField(sample), new ComponentRenderer())
        .setId(PROTEIN_WEIGHT).setCaption(resources.message(PROTEIN_WEIGHT)).setWidth(170)
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
      field.setWidth("100%");
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
      field.setWidth("100%");
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
      field.setWidth("100%");
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

  private void prepareExperimentComponents() {
    final MessageResource resources = view.getResources();
    final MessageResource generalResources = view.getGeneralResources();
    design.experimentPanel.addStyleName(EXPERIMENT_PANEL);
    design.experimentPanel.setCaption(resources.message(EXPERIMENT_PANEL));
    design.experiment.addStyleName(EXPERIMENT);
    design.experiment.setCaption(resources.message(EXPERIMENT));
    design.experiment.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.experiment)
        .withValidator(requiredTextIfVisible(design.experiment)).withNullRepresentation("")
        .bind(EXPERIMENT);
    design.experimentGoal.addStyleName(EXPERIMENT_GOAL);
    design.experimentGoal.setCaption(resources.message(EXPERIMENT_GOAL));
    submissionBinder.forField(design.experimentGoal).withNullRepresentation("")
        .bind(EXPERIMENT_GOAL);
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
    design.sampleQuantity.setPlaceholder(resources.message(property(SAMPLE_QUANTITY, EXAMPLE)));
    design.sampleQuantity.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleQuantity)
        .withValidator(requiredTextIfVisible(design.sampleQuantity)).withNullRepresentation("")
        .bind(SAMPLE_QUANTITY);
    design.sampleVolume.addStyleName(SAMPLE_VOLUME);
    design.sampleVolume.setCaption(resources.message(SAMPLE_VOLUME));
    design.sampleVolume.setPlaceholder(resources.message(property(SAMPLE_VOLUME, EXAMPLE)));
    design.sampleVolume.setRequiredIndicatorVisible(true);
    firstSampleBinder.forField(design.sampleVolume)
        .withValidator(requiredTextIfVisible(design.sampleVolume)).withNullRepresentation("")
        .bind(SAMPLE_VOLUME);
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
        .setValue(resources.message(property(OTHER_DIGESTION, "note")));
    design.enrichment.addStyleName(ENRICHEMENT);
    design.enrichment.setCaption(resources.message(ENRICHEMENT));
    design.enrichment.setValue(resources.message(property(ENRICHEMENT, "value")));
    design.exclusions.addStyleName(EXCLUSIONS);
    design.exclusions.setCaption(resources.message(EXCLUSIONS));
    design.exclusions.setValue(resources.message(property(EXCLUSIONS, "value")));
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
        .setPlaceholder(resources.message(property(QUANTIFICATION_COMMENT, EXAMPLE)));
    submissionBinder.forField(design.quantificationComment).withValidator((value, context) -> {
      if (value.isEmpty() && (design.quantification.getValue() == SILAC
          || design.quantification.getValue() == TMT)) {
        String error = generalResources.message(REQUIRED);
        logger.debug("validation error on {}: {}", QUANTIFICATION_COMMENT, error);
        return ValidationResult.error(error);
      }
      return ValidationResult.ok();
    }).withNullRepresentation("").bind(QUANTIFICATION_COMMENT);
    design.highResolution.addStyleName(HIGH_RESOLUTION);
    design.highResolution.setCaption(resources.message(HIGH_RESOLUTION));
    design.highResolution.setItems(false, true);
    design.highResolution
        .setItemCaptionGenerator(value -> resources.message(property(HIGH_RESOLUTION, value)));
    design.highResolution.setRequiredIndicatorVisible(true);
    submissionBinder.forField(design.highResolution)
        .withValidator(requiredIfVisible(design.highResolution)).bind(HIGH_RESOLUTION);
    design.solventsLayout.addStyleName(REQUIRED);
    design.solventsLayout.setCaption(resources.message(SOLVENTS));
    design.acetonitrileSolvents.addStyleName(styleName(SOLVENTS, Solvent.ACETONITRILE.name()));
    design.acetonitrileSolvents.setCaption(Solvent.ACETONITRILE.getLabel(locale));
    design.methanolSolvents.addStyleName(styleName(SOLVENTS, Solvent.METHANOL.name()));
    design.methanolSolvents.setCaption(Solvent.METHANOL.getLabel(locale));
    design.chclSolvents.addStyleName(styleName(SOLVENTS, Solvent.CHCL3.name()));
    design.chclSolvents.setCaption(Solvent.CHCL3.getLabel(locale));
    design.chclSolvents.setCaptionAsHtml(true);
    design.otherSolvents.addStyleName(styleName(SOLVENTS, Solvent.OTHER.name()));
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
        String error = generalResources.message(REQUIRED);
        logger.debug("validation error on {}: {}", field.getStyleName(), error);
        return ValidationResult.error(error);
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> requiredTextIfVisible(AbstractTextField field) {
    final MessageResource generalResources = view.getGeneralResources();
    return (value, context) -> {
      if (field.isVisible() && value.isEmpty()) {
        String error = generalResources.message(REQUIRED);
        logger.debug("validation error on {}: {}", field.getStyleName(), error);
        return ValidationResult.error(error);
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> requiredTextIf(Predicate<Void> predicate) {
    final MessageResource generalResources = view.getGeneralResources();
    return (value, context) -> {
      if (predicate.test(null) && value.isEmpty()) {
        String error = generalResources.message(REQUIRED);
        logger.debug("validation error on {}: {}",
            context.getComponent().map(com -> com.getStyleName()).orElse(null), error);
        return ValidationResult.error(error);
      } else {
        return ValidationResult.ok();
      }
    };
  }

  private void updateSampleType() {
    MessageResource resources = view.getResources();
    final SampleType type = design.sampleType.getValue();
    if (type == null) {
      return;
    }
    if (type.isBeads()) {
      design.sampleVolume.setPlaceholder(resources.message(property(SAMPLE_VOLUME_BEADS, EXAMPLE)));
    } else {
      design.sampleVolume.setPlaceholder(resources.message(property(SAMPLE_VOLUME, EXAMPLE)));
    }
    updateVisible();
  }

  private void updateVisible() {
    final Service service = design.service.getValue();
    final SampleType type = design.sampleType.getValue();
    if (type == null) {
      return;
    }
    design.sampleTypeWarning.setVisible(!readOnly);
    design.inactiveWarning.setVisible(!readOnly);
    design.solutionSolvent.setVisible(service == SMALL_MOLECULE && type.isSolution());
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
    design.experimentPanel.setVisible(service != SMALL_MOLECULE);
    design.experiment.setVisible(service != SMALL_MOLECULE);
    design.experimentGoal.setVisible(service != SMALL_MOLECULE);
    design.taxonomy.setVisible(service != SMALL_MOLECULE);
    design.proteinName.setVisible(service != SMALL_MOLECULE);
    design.proteinWeight.setVisible(service == LC_MS_MS);
    design.postTranslationModification.setVisible(service != SMALL_MOLECULE);
    design.sampleQuantity
        .setVisible(service != SMALL_MOLECULE && (type.isSolution() || type.isDry()));
    design.sampleVolume.setVisible(service != SMALL_MOLECULE && type.isSolution());
    design.standardsPanel
        .setVisible(service != SMALL_MOLECULE && (type.isSolution() || type.isDry()));
    design.contaminantsPanel
        .setVisible(service != SMALL_MOLECULE && (type.isSolution() || type.isDry()));
    design.gelPanel.setVisible(service == LC_MS_MS && type.isGel());
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
    design.gelImageFile.setVisible(service == LC_MS_MS && type.isGel());
    view.filesUploader.setVisible(!readOnly);
    design.save.setVisible(!readOnly);
    design.print.setVisible(submissionBinder.getBean().getId() != null);
  }

  private void updateReadOnly() {
    design.service.setReadOnly(readOnly);
    design.sampleType.setReadOnly(readOnly);
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
    design.experiment.setReadOnly(readOnly);
    design.experimentGoal.setReadOnly(readOnly);
    design.taxonomy.setReadOnly(readOnly);
    design.proteinName.setReadOnly(readOnly);
    design.proteinWeight.setReadOnly(readOnly);
    design.postTranslationModification.setReadOnly(readOnly);
    design.sampleQuantity.setReadOnly(readOnly);
    design.sampleVolume.setReadOnly(readOnly);
    view.standardsForm.setReadOnly(readOnly);
    view.contaminantsForm.setReadOnly(readOnly);
    view.gelForm.setReadOnly(readOnly);
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
        view.showError(resources.message(property(FILES, "error"), fileName));
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
      view.showWarning(resources.message(property(FILES, "overMaximumCount"), MAXIMUM_FILES_COUNT));
    }
  }

  private Validator<SampleType> validateSampleType() {
    return (value, context) -> {
      Service service = design.service.getValue();
      if ((service == SMALL_MOLECULE || service == INTACT_PROTEIN)
          && (value.isGel() || value.isBeads())) {
        MessageResource generalResources = view.getGeneralResources();
        String error = generalResources.message(INVALID);
        logger.debug("validation error on {}: {}", SAMPLE_TYPE, error);
        return ValidationResult.error(error);
      }
      return ValidationResult.ok();
    };
  }

  private Validator<String> validateSampleName(boolean includeSampleNameInError) {
    return (value, context) -> {
      if (value == null || value.isEmpty()) {
        return ValidationResult.ok();
      }
      MessageResource generalResources = view.getGeneralResources();
      if (!Pattern.matches("\\w*", value)) {
        String error = generalResources.message(ONLY_WORDS);
        logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
        return ValidationResult.error(error);
      }
      if (submissionSampleService.exists(value)) {
        if (submissionBinder.getBean().getId() == null
            || !submissionService.get(submissionBinder.getBean().getId()).getSamples().stream()
                .filter(sample -> sample.getName().equalsIgnoreCase(value)).findAny().isPresent()) {
          if (includeSampleNameInError) {
            MessageResource resources = view.getResources();
            String error = resources.message(property(SAMPLE_NAME, "exists"), value);
            logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
            return ValidationResult.error(error);
          } else {
            String error = generalResources.message(ALREADY_EXISTS);
            logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
            return ValidationResult.error(error);
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
        if (plateBinder.getBean().getId() == null
            || !plateService.get(plateBinder.getBean().getId()).getName().equalsIgnoreCase(value)) {
          String error = generalResources.message(ALREADY_EXISTS);
          logger.debug("validation error on {}: {}", PLATE_NAME, error);
          return ValidationResult.error(error);
        }
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
      if (sample.getType().isDry() || sample.getType().isSolution()) {
        valid &= view.standardsForm.validate();
        valid &= view.contaminantsForm.validate();
      }
      if (sample.getType().isGel()) {
        valid &= view.gelForm.validate();
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
          String error = resources.message(property(SAMPLE_NAME, "duplicate"), sample.getName());
          sampleNameFields.get(sample).setComponentError(new UserError(error));
          logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
          return ValidationResult.error(error);
        }
      }
    } else {
      int count = 0;
      List<String> plateSampleNames = plateSampleNames();
      for (String name : plateSampleNames) {
        count++;
        if (!names.add(name)) {
          String error = resources.message(property(SAMPLE_NAME, "duplicate"), name);
          addError(new UserError(error), view.plateComponent);
          logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
          return ValidationResult.error(error);
        }
      }
      if (count < sampleCountBinder.getBean().getCount()) {
        String error =
            resources.message(property(SAMPLES, "missing"), sampleCountBinder.getBean().getCount());
        addError(new UserError(error), view.plateComponent);
        logger.debug("validation error on {}: {}", SAMPLE_NAME, error);
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
      String error = resources.message(property(SOLVENTS, REQUIRED));
      design.solventsLayout.setComponentError(new UserError(error));
      logger.debug("validation error on {}: {}", SOLVENTS, error);
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
      logger.debug("validation error on {}: {}", EXPLANATION, error);
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
    clearInvisibleField(design.experiment);
    clearInvisibleField(design.experimentGoal);
    clearInvisibleField(design.taxonomy);
    clearInvisibleField(design.proteinName);
    clearInvisibleField(design.proteinWeight);
    clearInvisibleField(design.postTranslationModification);
    clearInvisibleField(design.sampleQuantity);
    clearInvisibleField(design.sampleVolume);
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
        submission.setExperiment(firstSample.getName());
        submission.setSamples(Arrays.asList(firstSample));
        submission.setSolvents(new ArrayList<>());
        firstSample.setOriginalContainer(null);
        firstSample.setNumberProtein(null);
        if (design.acetonitrileSolvents.getValue()) {
          submission.getSolvents().add(ACETONITRILE);
        }
        if (design.methanolSolvents.getValue()) {
          submission.getSolvents().add(METHANOL);
        }
        if (design.chclSolvents.getValue()) {
          submission.getSolvents().add(CHCL3);
        }
        if (design.otherSolvents.getValue()) {
          submission.getSolvents().add(Solvent.OTHER);
        }
      } else {
        submission.setStorageTemperature(null);
      }
      if (submission.getService() == INTACT_PROTEIN) {
        copySamplesToSubmission(submission);
      } else {
        submission.setSource(null);
      }
      if (!firstSample.getType().isGel()) {
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
          view.showError(resources.message(UPDATE_ERROR, submission.getExperiment()));
        }
      } else {
        submissionService.insert(submission);
      }
      view.showTrayNotification(
          resources.message(property(SAVE, "done"), submission.getExperiment()));
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
      sample.setType(firstSample.getType());
      sample.setQuantity(firstSample.getQuantity());
      sample.setVolume(firstSample.getVolume());
      if (submission.getService() != INTACT_PROTEIN) {
        sample.setNumberProtein(null);
        sample.setMolecularWeight(firstSample.getMolecularWeight());
      }
      if (!firstSample.getType().isGel()) {
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
        sample.setType(firstSample.getType());
        sample.setQuantity(firstSample.getQuantity());
        sample.setVolume(firstSample.getVolume());
        sample.setNumberProtein(null);
        sample.setMolecularWeight(firstSample.getMolecularWeight());
        if (!firstSample.getType().isGel()) {
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
    for (Standard standard : view.standardsForm.getValue()) {
      Standard copy = new Standard();
      copy.setName(standard.getName());
      copy.setQuantity(standard.getQuantity());
      copy.setComment(standard.getComment());
      sample.getStandards().add(copy);
    }
  }

  private void copyContaminantsFromTableToSample(SubmissionSample sample) {
    sample.setContaminants(new ArrayList<>());
    for (Contaminant contaminant : view.contaminantsForm.getValue()) {
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
      firstSample.setType(SOLUTION);
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
      firstSample.setType(original.getType());
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
    view.standardsForm.setValue(firstSample.getStandards());
    view.contaminantsForm.setValue(firstSample.getContaminants());
    view.gelForm.setValue(submission);
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
    Set<Solvent> solvents =
        submission.getSolvents() != null ? new HashSet<>(submission.getSolvents())
            : new HashSet<>();
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
    preparePrint(submissionBinder.getBean());
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
