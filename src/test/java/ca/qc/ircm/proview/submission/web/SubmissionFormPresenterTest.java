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

import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteinIdentification.UNIPROT;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.DIGESTED;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.SampleContainerType.WELL;
import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.AVERAGE_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COMMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DECOLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DEVELOPMENT_TIME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.ENRICHEMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXCLUSIONS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_GOAL_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPLANATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_GRID;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILE_FILENAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_CONTAMINANTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_SAMPLES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_STANDARDS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FORMULA_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_TABLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGE_FILENAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.HIGH_RESOLUTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INACTIVE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INJECTION_TYPE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INSTRUMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.LIGHT_SENSITIVE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.MONOISOTOPIC_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_NOTE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.POST_TRANSLATION_MODIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_CONTENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_LINK_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_WEIGHT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_LABELS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.REMOVE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.REMOVE_GEL_IMAGE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_CONTAINER_TYPE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_TABLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NUMBER_PROTEIN_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_SUPPORT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SEPARATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLUTION_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLVENTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOURCE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COMMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STORAGE_TEMPERATURE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TAXONOMY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.THICKNESS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TOXICITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.UPDATE_ERROR;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.WEIGHT_MARKER_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.errorMessage;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.io.ByteStreams;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.plate.web.PlateComponent;
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
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.DefaultMultiFileUpload;
import ca.qc.ircm.proview.web.MultiFileUploadFileHandler;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.AbstractStringToNumberConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.SerializableFunction;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionFormPresenterTest {
  private SubmissionFormPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private SubmissionSampleService submissionSampleService;
  @Mock
  private PlateService plateService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private SubmissionForm view;
  @Mock
  private Upload structureUploader;
  @Mock
  private DefaultMultiFileUpload gelImagesUploader;
  @Mock
  private DefaultMultiFileUpload filesUploader;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Captor
  private ArgumentCaptor<Receiver> receiverCaptor;
  @Captor
  private ArgumentCaptor<SucceededListener> succeededListenerCaptor;
  @Captor
  private ArgumentCaptor<MultiFileUploadFileHandler> uploadFinishedHandlerCaptor;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  @Captor
  private ArgumentCaptor<ErrorMessage> errorMessageCaptor;
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private SubmissionFormDesign design;
  private final Locale locale = Locale.FRENCH;
  private final MessageResource resources = new MessageResource(SubmissionForm.class, locale);
  private final MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private final Random random = new Random();
  private SampleSupport support = SOLUTION;
  private int sampleCount = 2;
  private String solutionSolvent = "h2o";
  private String sampleName = "my_sample";
  private String sampleName1 = "my_sample_1";
  private String sampleName2 = "my_sample_2";
  private int sampleNumberProtein1 = 1;
  private int sampleNumberProtein2 = 2;
  private double proteinWeight1 = 207076;
  private double proteinWeight2 = 219076;
  private String formula = "ch3oh";
  private String structureFilename = "ch3oh.png";
  private String structureMimeType = "png";
  private byte[] structureContent = new byte[2048];
  private double monoisotopicMass = 32.04;
  private double averageMass = 32.08;
  private String toxicity = "non-toxic";
  private boolean lightSensitive = true;
  private StorageTemperature storageTemperature = StorageTemperature.LOW;
  private SampleContainerType sampleContainerType = SampleContainerType.TUBE;
  private String plateName = "my_plate";
  private Plate plate;
  private String experience = "my_experience";
  private String experienceGoal = "to succeed!";
  private String taxonomy = "human";
  private String proteinName = "PORL2A";
  private double proteinWeight = 217076;
  private String postTranslationModification = "Methylation on A75";
  private String sampleQuantity = "150 ug";
  private double sampleVolume = 21.5;
  private int standardsCount = 2;
  private String standardName1 = "ADH";
  private String standardQuantity1 = "5 ug";
  private String standardComment1 = "standard 1 comment";
  private String standardName2 = "CBS";
  private String standardQuantity2 = "8 ug";
  private String standardComment2 = "standard 2 comment";
  private int contaminantsCount = 2;
  private String contaminantName1 = "KRT1";
  private String contaminantQuantity1 = "3 ug";
  private String contaminantComment1 = "contaminant 1 comment";
  private String contaminantName2 = "KRT8";
  private String contaminantQuantity2 = "4 ug";
  private String contaminantComment2 = "contaminant 2 comment";
  private GelSeparation gelSeparation = GelSeparation.TWO_DIMENSION;
  private GelThickness gelThickness = GelThickness.ONE_HALF;
  private GelColoration gelColoration = GelColoration.OTHER;
  private String otherColoration = "my coloration";
  private String developmentTime = "300 seconds";
  private boolean decoloration = true;
  private double weightMarkerQuantity = 300;
  private String proteinQuantity = "30 ug";
  private String gelImageFilename1 = "gel1.png";
  private String gelImageMimeType1 = "png";
  private byte[] gelImageContent1 = new byte[3072];
  private String gelImageFilename2 = "gel2.png";
  private String gelImageMimeType2 = "png";
  private byte[] gelImageContent2 = new byte[4096];
  private ProteolyticDigestion digestion = DIGESTED;
  private String usedDigestion = "typsinP";
  private String otherDigestion = "typsinP/Y";
  private InjectionType injectionType = InjectionType.DIRECT_INFUSION;
  private MassDetectionInstrumentSource source = MassDetectionInstrumentSource.NSI;
  private ProteinContent proteinContent = ProteinContent.LARGE;
  private MassDetectionInstrument instrument = MassDetectionInstrument.ORBITRAP_FUSION;
  private ProteinIdentification proteinIdentification = ProteinIdentification.OTHER;
  private String proteinIdentificationLink = "NR at ftp://ftp.ncbi.nlm.nih.gov/blast/db/";
  private Quantification quantification = Quantification.SILAC;
  private String quantificationLabels = "Heavy: Lys8, Arg10\nLight: None";
  private boolean highResolution = true;
  private boolean acetonitrileSolvents = true;
  private boolean methanolSolvents = true;
  private boolean chclSolvents = true;
  private boolean otherSolvents = true;
  private String otherSolvent = "acetone";
  private String comment = "my comment\nmy comment second line";
  private String filesFilename1 = "protocol.txt";
  private String filesMimeType1 = "txt";
  private byte[] filesContent1 = new byte[1024];
  private String filesFilename2 = "samples.xlsx";
  private String filesMimeType2 = "xlsx";
  private byte[] filesContent2 = new byte[10240];
  private String explanation = "my explanation";
  private TextField sampleNameField1;
  private TextField sampleNameField2;
  private TextField sampleNumberProteinField1;
  private TextField sampleNumberProteinField2;
  private TextField sampleProteinWeightField1;
  private TextField sampleProteinWeightField2;
  private TextField standardNameField1;
  private TextField standardNameField2;
  private TextField standardQuantityField1;
  private TextField standardQuantityField2;
  private TextField contaminantNameField1;
  private TextField contaminantNameField2;
  private TextField contaminantQuantityField1;
  private TextField contaminantQuantityField2;
  private StringToIntegerConverter integerConverter = new StringToIntegerConverter("");
  private StringToDoubleConverter doubleConverter = new StringToDoubleConverter("");
  private SerializableFunction<String, Exception> throwableFunction = s -> new Exception();

  /**
   * Before test.
   */
  @Before
  public void beforeTest() throws Throwable {
    presenter = new SubmissionFormPresenter(submissionService, submissionSampleService,
        plateService, authorizationService);
    design = new SubmissionFormDesign();
    view.design = design;
    view.structureUploader = structureUploader;
    view.plateComponent = mock(PlateComponent.class);
    view.gelImagesUploader = gelImagesUploader;
    view.filesUploader = filesUploader;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(plateService.nameAvailable(any())).thenReturn(true);
    plate = new Plate();
    plate.initWells();
    when(view.plateComponent.getValue()).thenReturn(plate);
  }

  private void setFields() {
    design.solutionSolventField.setValue(solutionSolvent);
    design.sampleNameField.setValue(sampleName);
    design.formulaField.setValue(formula);
    design.monoisotopicMassField.setValue(String.valueOf(monoisotopicMass));
    design.averageMassField.setValue(String.valueOf(averageMass));
    design.toxicityField.setValue(toxicity);
    design.lightSensitiveField.setValue(lightSensitive);
    design.storageTemperatureOptions.setValue(storageTemperature);
    design.sampleContainerTypeOptions.setValue(sampleContainerType);
    design.plateNameField.setValue(plateName);
    design.sampleCountField.setValue(String.valueOf(sampleCount));
    setValuesInSamplesTable();
    plate.well(0, 0).setSample(new SubmissionSample(null, sampleName1));
    plate.well(1, 0).setSample(new SubmissionSample(null, sampleName2));
    design.experienceField.setValue(experience);
    design.experienceGoalField.setValue(experienceGoal);
    design.taxonomyField.setValue(taxonomy);
    design.proteinNameField.setValue(proteinName);
    design.proteinWeightField.setValue(String.valueOf(proteinWeight));
    design.postTranslationModificationField.setValue(postTranslationModification);
    design.sampleQuantityField.setValue(sampleQuantity);
    design.sampleVolumeField.setValue(String.valueOf(sampleVolume));
    design.standardCountField.setValue(String.valueOf(standardsCount));
    setValuesInStandardsTable();
    design.contaminantCountField.setValue(String.valueOf(contaminantsCount));
    setValuesInContaminantsTable();
    design.separationField.setValue(gelSeparation);
    design.thicknessField.setValue(gelThickness);
    design.colorationField.setValue(gelColoration);
    design.otherColorationField.setValue(otherColoration);
    design.developmentTimeField.setValue(developmentTime);
    design.decolorationField.setValue(decoloration);
    design.weightMarkerQuantityField.setValue(String.valueOf(weightMarkerQuantity));
    design.proteinQuantityField.setValue(proteinQuantity);
    design.digestionOptions.setValue(digestion);
    design.usedProteolyticDigestionMethodField.setValue(usedDigestion);
    design.otherProteolyticDigestionMethodField.setValue(otherDigestion);
    design.injectionTypeOptions.setValue(injectionType);
    design.sourceOptions.setValue(source);
    design.proteinContentOptions.setValue(proteinContent);
    design.instrumentOptions.setValue(instrument);
    design.proteinIdentificationOptions.setValue(proteinIdentification);
    design.proteinIdentificationLinkField.setValue(proteinIdentificationLink);
    design.quantificationOptions.setValue(quantification);
    design.quantificationLabelsField.setValue(quantificationLabels);
    design.highResolutionOptions.setValue(highResolution);
    design.acetonitrileSolventsField.setValue(acetonitrileSolvents);
    design.methanolSolventsField.setValue(methanolSolvents);
    design.chclSolventsField.setValue(chclSolvents);
    design.otherSolventsField.setValue(otherSolvents);
    design.otherSolventField.setValue(otherSolvent);
    design.commentField.setValue(comment);
    design.explanation.setValue(explanation);
  }

  private <R extends Number> R convert(AbstractStringToNumberConverter<R> converter,
      TextField component) throws Exception {
    return converter.convertToModel(component.getValue(), new ValueContext(component))
        .getOrThrow(throwableFunction);
  }

  private void setValuesInSamplesTable() {
    List<SubmissionSample> samples = new ArrayList<>(dataProvider(design.samplesGrid).getItems());
    SubmissionSample sample = samples.get(0);
    sampleNameField1 = setValueInSamplesGrid(sample, sampleName1, SAMPLE_NAME_PROPERTY);
    sampleNumberProteinField1 = setValueInSamplesGrid(sample, String.valueOf(sampleNumberProtein1),
        SAMPLE_NUMBER_PROTEIN_PROPERTY);
    sampleProteinWeightField1 =
        setValueInSamplesGrid(sample, String.valueOf(proteinWeight1), PROTEIN_WEIGHT_PROPERTY);
    sample = samples.get(1);
    sampleNameField2 = setValueInSamplesGrid(sample, sampleName2, SAMPLE_NAME_PROPERTY);
    sampleNumberProteinField2 = setValueInSamplesGrid(sample, String.valueOf(sampleNumberProtein2),
        SAMPLE_NUMBER_PROTEIN_PROPERTY);
    sampleProteinWeightField2 =
        setValueInSamplesGrid(sample, String.valueOf(proteinWeight2), PROTEIN_WEIGHT_PROPERTY);
  }

  private TextField setValueInSamplesGrid(SubmissionSample sample, String value, String columnId) {
    TextField field =
        (TextField) design.samplesGrid.getColumn(columnId).getValueProvider().apply(sample);
    field.setValue(value);
    return field;
  }

  private void setValuesInStandardsTable() {
    List<Standard> standards = new ArrayList<>(dataProvider(design.standardsGrid).getItems());
    Standard standard = standards.get(0);
    standardNameField1 = setValueInStandardsGrid(standard, standardName1, STANDARD_NAME_PROPERTY);
    standardQuantityField1 =
        setValueInStandardsGrid(standard, standardQuantity1, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsGrid(standard, standardComment1, STANDARD_COMMENT_PROPERTY);
    standard = standards.get(1);
    standardNameField2 = setValueInStandardsGrid(standard, standardName2, STANDARD_NAME_PROPERTY);
    standardQuantityField2 =
        setValueInStandardsGrid(standard, standardQuantity2, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsGrid(standard, standardComment2, STANDARD_COMMENT_PROPERTY);
  }

  private TextField setValueInStandardsGrid(Standard standard, String value, String columnId) {
    TextField field =
        (TextField) design.standardsGrid.getColumn(columnId).getValueProvider().apply(standard);
    field.setValue(value);
    return field;
  }

  private void setValuesInContaminantsTable() {
    List<Contaminant> contaminants =
        new ArrayList<>(dataProvider(design.contaminantsGrid).getItems());
    Contaminant contaminant = contaminants.get(0);
    contaminantNameField1 =
        setValueInContaminantsGrid(contaminant, contaminantName1, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField1 = setValueInContaminantsGrid(contaminant, contaminantQuantity1,
        CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsGrid(contaminant, contaminantComment1, CONTAMINANT_COMMENT_PROPERTY);
    contaminant = contaminants.get(1);
    contaminantNameField2 =
        setValueInContaminantsGrid(contaminant, contaminantName2, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField2 = setValueInContaminantsGrid(contaminant, contaminantQuantity2,
        CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsGrid(contaminant, contaminantComment2, CONTAMINANT_COMMENT_PROPERTY);
  }

  private TextField setValueInContaminantsGrid(Contaminant contaminant, String value,
      String columnId) {
    TextField field = (TextField) design.contaminantsGrid.getColumn(columnId).getValueProvider()
        .apply(contaminant);
    field.setValue(value);
    return field;
  }

  private void uploadStructure() throws IOException {
    verify(view.structureUploader).setReceiver(receiverCaptor.capture());
    verify(view.structureUploader).addSucceededListener(succeededListenerCaptor.capture());
    Receiver receiver = receiverCaptor.getValue();
    SucceededListener listener = succeededListenerCaptor.getValue();
    random.nextBytes(structureContent);
    OutputStream output = receiver.receiveUpload(structureFilename, structureMimeType);
    ByteStreams.copy(new ByteArrayInputStream(structureContent), output);
    listener.uploadSucceeded(new SucceededEvent(view.structureUploader, structureFilename,
        structureMimeType, structureContent.length));
  }

  private void uploadGelImages() throws IOException {
    verify(view).createGelImagesUploader(uploadFinishedHandlerCaptor.capture());
    MultiFileUploadFileHandler handler = uploadFinishedHandlerCaptor.getValue();
    random.nextBytes(gelImageContent1);
    Path gelImage1Path = temporaryFolder.getRoot().toPath().resolve("gelimage1.tmp");
    Files.copy(new ByteArrayInputStream(gelImageContent1), gelImage1Path);
    handler.handleFile(gelImage1Path.toFile(), gelImageFilename1, gelImageMimeType1,
        gelImageContent1.length);
    random.nextBytes(gelImageContent2);
    Path gelImage2Path = temporaryFolder.getRoot().toPath().resolve("gelimage2.tmp");
    Files.copy(new ByteArrayInputStream(gelImageContent2), gelImage2Path);
    handler.handleFile(gelImage2Path.toFile(), gelImageFilename2, gelImageMimeType2,
        gelImageContent2.length);
  }

  private void uploadFiles() throws IOException {
    verify(view).createFilesUploader(uploadFinishedHandlerCaptor.capture());
    MultiFileUploadFileHandler handler = uploadFinishedHandlerCaptor.getValue();
    random.nextBytes(filesContent1);
    Path file1Path = temporaryFolder.getRoot().toPath().resolve("file1.tmp");
    Files.copy(new ByteArrayInputStream(filesContent1), file1Path);
    handler.handleFile(file1Path.toFile(), filesFilename1, filesMimeType1, filesContent1.length);
    random.nextBytes(filesContent2);
    Path file2Path = temporaryFolder.getRoot().toPath().resolve("file2.tmp");
    Files.copy(new ByteArrayInputStream(filesContent2), file2Path);
    handler.handleFile(file2Path.toFile(), filesFilename2, filesMimeType2, filesContent2.length);
  }

  private Submission createSubmission() {
    Submission submission = new Submission();
    submission.setId(20L);
    submission.setService(LC_MS_MS);
    submission.setTaxonomy(taxonomy);
    submission.setExperience(experience);
    submission.setGoal(experienceGoal);
    submission.setMassDetectionInstrument(instrument);
    submission.setProteolyticDigestionMethod(digestion);
    submission.setUsedProteolyticDigestionMethod(usedDigestion);
    submission.setOtherProteolyticDigestionMethod(otherDigestion);
    submission.setProteinIdentification(proteinIdentification);
    submission.setProteinIdentificationLink(proteinIdentificationLink);
    submission.setInjectionType(injectionType);
    submission.setSource(source);
    submission.setHighResolution(true);
    submission.setMsms(true);
    submission.setExactMsms(true);
    submission.setMudPitFraction(null);
    submission.setProteinContent(proteinContent);
    submission.setProtein(proteinName);
    submission.setPostTranslationModification(postTranslationModification);
    submission.setSeparation(gelSeparation);
    submission.setThickness(gelThickness);
    submission.setColoration(gelColoration);
    submission.setOtherColoration(otherColoration);
    submission.setDevelopmentTime(developmentTime);
    submission.setDecoloration(true);
    submission.setWeightMarkerQuantity(weightMarkerQuantity);
    submission.setProteinQuantity(proteinQuantity);
    submission.setFormula(formula);
    submission.setMonoisotopicMass(monoisotopicMass);
    submission.setAverageMass(averageMass);
    submission.setSolutionSolvent(solutionSolvent);
    List<SampleSolvent> sampleSolvents = new ArrayList<>();
    if (acetonitrileSolvents) {
      sampleSolvents.add(new SampleSolvent(Solvent.ACETONITRILE));
    }
    if (methanolSolvents) {
      sampleSolvents.add(new SampleSolvent(Solvent.METHANOL));
    }
    if (chclSolvents) {
      sampleSolvents.add(new SampleSolvent(Solvent.CHCL3));
    }
    if (otherSolvents) {
      sampleSolvents.add(new SampleSolvent(Solvent.OTHER));
    }
    submission.setSolvents(sampleSolvents);
    submission.setOtherSolvent(otherSolvent);
    submission.setToxicity(toxicity);
    submission.setLightSensitive(true);
    submission.setStorageTemperature(storageTemperature);
    submission.setQuantification(quantification);
    submission.setQuantificationLabels(quantificationLabels);
    submission.setComment(comment);
    submission.setSubmissionDate(Instant.now());
    User user = entityManager.find(User.class, 3L);
    submission.setUser(user);
    submission.setLaboratory(user.getLaboratory());
    List<SubmissionSample> samples = new ArrayList<>();
    submission.setSamples(samples);
    SubmissionSample sample = new SubmissionSample();
    sample.setId(21L);
    sample.setName(sampleName1);
    sample.setSupport(support);
    sample.setVolume(sampleVolume);
    sample.setQuantity(sampleQuantity);
    sample.setNumberProtein(sampleNumberProtein1);
    sample.setMolecularWeight(proteinWeight1);
    sample.setOriginalContainer(new Tube(21L, sampleName1));
    List<Standard> standards = new ArrayList<>();
    sample.setStandards(standards);
    Standard standard = new Standard();
    standard.setId(30L);
    standard.setName(standardName1);
    standard.setQuantity(standardQuantity1);
    standard.setComment(standardComment1);
    standards.add(standard);
    standard = new Standard();
    standard.setId(31L);
    standard.setName(standardName2);
    standard.setQuantity(standardQuantity2);
    standard.setComment(standardComment2);
    standards.add(standard);
    List<Contaminant> contaminants = new ArrayList<>();
    sample.setContaminants(contaminants);
    Contaminant contaminant = new Contaminant();
    contaminant.setId(30L);
    contaminant.setName(contaminantName1);
    contaminant.setQuantity(contaminantQuantity1);
    contaminant.setComment(contaminantComment1);
    contaminants.add(contaminant);
    contaminant = new Contaminant();
    contaminant.setId(31L);
    contaminant.setName(contaminantName2);
    contaminant.setQuantity(contaminantQuantity2);
    contaminant.setComment(contaminantComment2);
    contaminants.add(contaminant);
    sample.setStatus(SampleStatus.TO_APPROVE);
    sample.setSubmission(submission);
    samples.add(sample);
    sample = new SubmissionSample();
    sample.setId(22L);
    sample.setName(sampleName2);
    sample.setSupport(support);
    sample.setVolume(sampleVolume);
    sample.setQuantity(sampleQuantity);
    sample.setNumberProtein(sampleNumberProtein2);
    sample.setMolecularWeight(proteinWeight2);
    sample.setOriginalContainer(new Tube(22L, sampleName2));
    standards = new ArrayList<>();
    sample.setStandards(standards);
    standard = new Standard();
    standard.setId(32L);
    standard.setName(standardName1);
    standard.setQuantity(standardQuantity1);
    standard.setComment(standardComment1);
    standards.add(standard);
    standard = new Standard();
    standard.setId(33L);
    standard.setName(standardName2);
    standard.setQuantity(standardQuantity2);
    standard.setComment(standardComment2);
    standards.add(standard);
    contaminants = new ArrayList<>();
    sample.setContaminants(contaminants);
    contaminant = new Contaminant();
    contaminant.setId(32L);
    contaminant.setName(contaminantName1);
    contaminant.setQuantity(contaminantQuantity1);
    contaminant.setComment(contaminantComment1);
    contaminants.add(contaminant);
    contaminant = new Contaminant();
    contaminant.setId(33L);
    contaminant.setName(contaminantName2);
    contaminant.setQuantity(contaminantQuantity2);
    contaminant.setComment(contaminantComment2);
    contaminants.add(contaminant);
    List<GelImage> gelImages = new ArrayList<>();
    submission.setGelImages(gelImages);
    GelImage gelImage = new GelImage();
    gelImage.setFilename(gelImageFilename1);
    gelImage.setContent(gelImageContent1);
    gelImages.add(gelImage);
    gelImage = new GelImage();
    gelImage.setFilename(gelImageFilename2);
    gelImage.setContent(gelImageContent2);
    gelImages.add(gelImage);
    Structure structure = new Structure();
    structure.setFilename(structureFilename);
    structure.setContent(structureContent);
    submission.setStructure(structure);
    List<SubmissionFile> files = new ArrayList<>();
    submission.setFiles(files);
    SubmissionFile file = new SubmissionFile();
    file.setFilename(filesFilename1);
    file.setContent(filesContent1);
    files.add(file);
    file = new SubmissionFile();
    file.setFilename(filesFilename2);
    file.setContent(filesContent2);
    files.add(file);
    sample.setStatus(SampleStatus.TO_APPROVE);
    sample.setSubmission(submission);
    samples.add(sample);
    return submission;
  }

  @Test
  public void samplesTableColumns() {
    presenter.init(view);

    assertEquals(3, design.samplesGrid.getColumns().size());
    assertEquals(SAMPLE_NAME_PROPERTY, design.samplesGrid.getColumns().get(0).getId());
    assertFalse(design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).isHidden());
    assertEquals(SAMPLE_NUMBER_PROTEIN_PROPERTY, design.samplesGrid.getColumns().get(1).getId());
    assertTrue(design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).isHidden());
    assertEquals(PROTEIN_WEIGHT_PROPERTY, design.samplesGrid.getColumns().get(2).getId());
    assertTrue(design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void samplesTableColumns_IntactProtein() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);

    assertEquals(3, design.samplesGrid.getColumns().size());
    assertEquals(SAMPLE_NAME_PROPERTY, design.samplesGrid.getColumns().get(0).getId());
    assertFalse(design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).isHidden());
    assertEquals(SAMPLE_NUMBER_PROTEIN_PROPERTY, design.samplesGrid.getColumns().get(1).getId());
    assertFalse(design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).isHidden());
    assertEquals(PROTEIN_WEIGHT_PROPERTY, design.samplesGrid.getColumns().get(2).getId());
    assertFalse(design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void standardsColumns() {
    presenter.init(view);

    assertEquals(3, design.standardsGrid.getColumns().size());
    assertEquals(STANDARD_NAME_PROPERTY, design.standardsGrid.getColumns().get(0).getId());
    assertEquals(STANDARD_QUANTITY_PROPERTY, design.standardsGrid.getColumns().get(1).getId());
    assertEquals(STANDARD_COMMENT_PROPERTY, design.standardsGrid.getColumns().get(2).getId());
  }

  @Test
  public void contaminantsColumns() {
    presenter.init(view);

    assertEquals(3, design.contaminantsGrid.getColumns().size());
    assertEquals(CONTAMINANT_NAME_PROPERTY, design.contaminantsGrid.getColumns().get(0).getId());
    assertEquals(CONTAMINANT_QUANTITY_PROPERTY,
        design.contaminantsGrid.getColumns().get(1).getId());
    assertEquals(CONTAMINANT_COMMENT_PROPERTY, design.contaminantsGrid.getColumns().get(2).getId());
  }

  @Test
  public void gelImagesColumns_ReadOnly() {
    presenter.init(view);
    presenter.setReadOnly(true);

    assertEquals(2, design.gelImagesGrid.getColumns().size());
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, design.gelImagesGrid.getColumns().get(0).getId());
    assertFalse(design.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_GEL_IMAGE, design.gelImagesGrid.getColumns().get(1).getId());
    assertTrue(design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
  }

  @Test
  public void gelImagesColumns() {
    presenter.init(view);

    assertEquals(2, design.gelImagesGrid.getColumns().size());
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, design.gelImagesGrid.getColumns().get(0).getId());
    assertFalse(design.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_GEL_IMAGE, design.gelImagesGrid.getColumns().get(1).getId());
    assertFalse(design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
  }

  @Test
  public void filesColumns_ReadOnly() {
    presenter.init(view);
    presenter.setReadOnly(true);

    assertEquals(2, design.filesGrid.getColumns().size());
    assertEquals(FILE_FILENAME_PROPERTY, design.filesGrid.getColumns().get(0).getId());
    assertFalse(design.filesGrid.getColumn(FILE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_FILE, design.filesGrid.getColumns().get(1).getId());
    assertTrue(design.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void filesColumns() {
    presenter.init(view);

    assertEquals(2, design.filesGrid.getColumns().size());
    assertEquals(FILE_FILENAME_PROPERTY, design.filesGrid.getColumns().get(0).getId());
    assertFalse(design.filesGrid.getColumn(FILE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_FILE, design.filesGrid.getColumns().get(1).getId());
    assertFalse(design.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void requiredFields() {
    presenter.init(view);

    assertTrue(design.serviceOptions.isRequiredIndicatorVisible());
    assertTrue(design.sampleSupportOptions.isRequiredIndicatorVisible());
    assertTrue(design.solutionSolventField.isRequiredIndicatorVisible());
    assertTrue(design.sampleCountField.isRequiredIndicatorVisible());
    assertTrue(design.sampleNameField.isRequiredIndicatorVisible());
    assertTrue(design.formulaField.isRequiredIndicatorVisible());
    assertTrue(design.monoisotopicMassField.isRequiredIndicatorVisible());
    assertFalse(design.averageMassField.isRequiredIndicatorVisible());
    assertFalse(design.toxicityField.isRequiredIndicatorVisible());
    assertFalse(design.lightSensitiveField.isRequiredIndicatorVisible());
    assertTrue(design.storageTemperatureOptions.isRequiredIndicatorVisible());
    assertTrue(design.sampleContainerTypeOptions.isRequiredIndicatorVisible());
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    assertTrue(design.plateNameField.isRequiredIndicatorVisible());
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleNameTableField = (TextField) design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY)
        .getValueProvider().apply(firstSample);
    assertTrue(sampleNameTableField.isRequiredIndicatorVisible());
    TextField sampleNumberProteinTableField = (TextField) design.samplesGrid
        .getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).getValueProvider().apply(firstSample);
    assertTrue(sampleNumberProteinTableField.isRequiredIndicatorVisible());
    TextField sampleProteinWeightTableField = (TextField) design.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(design.experienceField.isRequiredIndicatorVisible());
    assertFalse(design.experienceGoalField.isRequiredIndicatorVisible());
    assertTrue(design.taxonomyField.isRequiredIndicatorVisible());
    assertFalse(design.proteinNameField.isRequiredIndicatorVisible());
    assertFalse(design.proteinWeightField.isRequiredIndicatorVisible());
    assertFalse(design.postTranslationModificationField.isRequiredIndicatorVisible());
    assertTrue(design.sampleQuantityField.isRequiredIndicatorVisible());
    assertTrue(design.sampleVolumeField.isRequiredIndicatorVisible());
    assertFalse(design.standardCountField.isRequiredIndicatorVisible());
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standardsGrid);
    if (standardsDataProvider.getItems().size() < 1) {
      standardsDataProvider.getItems().add(new Standard());
    }
    Standard firstStandard = standardsDataProvider.getItems().iterator().next();
    TextField standardNameTableField = (TextField) design.standardsGrid
        .getColumn(STANDARD_NAME_PROPERTY).getValueProvider().apply(firstStandard);
    assertTrue(standardNameTableField.isRequiredIndicatorVisible());
    TextField standardQuantityTableField = (TextField) design.standardsGrid
        .getColumn(STANDARD_QUANTITY_PROPERTY).getValueProvider().apply(firstStandard);
    assertTrue(standardQuantityTableField.isRequiredIndicatorVisible());
    TextField standardCommentTableField = (TextField) design.standardsGrid
        .getColumn(STANDARD_COMMENT_PROPERTY).getValueProvider().apply(firstStandard);
    assertFalse(standardCommentTableField.isRequiredIndicatorVisible());
    assertFalse(design.contaminantCountField.isRequiredIndicatorVisible());
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminantsGrid);
    if (contaminantsDataProvider.getItems().size() < 1) {
      contaminantsDataProvider.getItems().add(new Contaminant());
    }
    Contaminant firstContaminant = contaminantsDataProvider.getItems().iterator().next();
    TextField contaminantNameTableField = (TextField) design.contaminantsGrid
        .getColumn(CONTAMINANT_NAME_PROPERTY).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantNameTableField.isRequiredIndicatorVisible());
    TextField contaminantQuantityTableField = (TextField) design.contaminantsGrid
        .getColumn(CONTAMINANT_QUANTITY_PROPERTY).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantQuantityTableField.isRequiredIndicatorVisible());
    TextField contaminantCommentTableField = (TextField) design.contaminantsGrid
        .getColumn(CONTAMINANT_COMMENT_PROPERTY).getValueProvider().apply(firstContaminant);
    assertFalse(contaminantCommentTableField.isRequiredIndicatorVisible());
    assertTrue(design.separationField.isRequiredIndicatorVisible());
    assertTrue(design.thicknessField.isRequiredIndicatorVisible());
    assertFalse(design.colorationField.isRequiredIndicatorVisible());
    assertTrue(design.otherColorationField.isRequiredIndicatorVisible());
    assertFalse(design.developmentTimeField.isRequiredIndicatorVisible());
    assertFalse(design.decolorationField.isRequiredIndicatorVisible());
    assertFalse(design.weightMarkerQuantityField.isRequiredIndicatorVisible());
    assertFalse(design.proteinQuantityField.isRequiredIndicatorVisible());
    assertTrue(design.digestionOptions.isRequiredIndicatorVisible());
    assertTrue(design.usedProteolyticDigestionMethodField.isRequiredIndicatorVisible());
    assertTrue(design.otherProteolyticDigestionMethodField.isRequiredIndicatorVisible());
    assertTrue(design.injectionTypeOptions.isRequiredIndicatorVisible());
    assertTrue(design.sourceOptions.isRequiredIndicatorVisible());
    assertTrue(design.proteinContentOptions.isRequiredIndicatorVisible());
    assertFalse(design.instrumentOptions.isRequiredIndicatorVisible());
    assertTrue(design.proteinIdentificationOptions.isRequiredIndicatorVisible());
    assertTrue(design.proteinIdentificationLinkField.isRequiredIndicatorVisible());
    assertFalse(design.quantificationOptions.isRequiredIndicatorVisible());
    assertFalse(design.quantificationLabelsField.isRequiredIndicatorVisible());
    assertTrue(design.highResolutionOptions.isRequiredIndicatorVisible());
    assertFalse(design.acetonitrileSolventsField.isRequiredIndicatorVisible());
    assertFalse(design.methanolSolventsField.isRequiredIndicatorVisible());
    assertFalse(design.chclSolventsField.isRequiredIndicatorVisible());
    assertFalse(design.otherSolventsField.isRequiredIndicatorVisible());
    assertTrue(design.otherSolventField.isRequiredIndicatorVisible());
  }

  @Test
  public void required_ProteinWeight_Lcmsms() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField = (TextField) design.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    design.serviceOptions.setValue(LC_MS_MS); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void required_ProteinWeight_IntactProtein() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField = (TextField) design.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    design.serviceOptions.setValue(INTACT_PROTEIN); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertFalse(design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void service_Options() {
    presenter.init(view);

    assertEquals(Service.availables().size(),
        dataProvider(design.serviceOptions).getItems().size());
    for (Service service : Service.availables()) {
      assertTrue(service.name(), dataProvider(design.serviceOptions).getItems().contains(service));
    }
  }

  @Test
  public void service_DisabledOption() {
    Service service = Service.MALDI_MS;
    Submission submission = new Submission();
    submission.setService(service);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.serviceOptions).getItems().contains(service));
    assertFalse(design.serviceOptions.getItemEnabledProvider().test(service));
  }

  @Test
  public void gelSupportDisabled_Smallmolecule() {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);

    assertFalse(design.sampleSupportOptions.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void gelSupportDisabled_Intactprotein() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);

    assertFalse(design.sampleSupportOptions.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void digestion_RequiredText() {
    presenter.init(view);

    design.digestionOptions.setValue(TRYPSIN);
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    design.digestionOptions.setValue(DIGESTED);
    assertTrue(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    design.digestionOptions.setValue(ProteolyticDigestion.OTHER);
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertTrue(design.otherProteolyticDigestionMethodField.isVisible());
    assertTrue(design.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void source_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrumentSource.availables().size(),
        dataProvider(design.sourceOptions).getItems().size());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertTrue(source.name(), dataProvider(design.sourceOptions).getItems().contains(source));
    }
  }

  @Test
  public void source_DisabledOption() {
    source = MassDetectionInstrumentSource.LDTD;
    Submission submission = new Submission();
    submission.setSource(source);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.sourceOptions).getItems().contains(source));
    assertFalse(design.sourceOptions.getItemEnabledProvider().test(source));
  }

  @Test
  public void instrument_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrument.availables().size(),
        dataProvider(design.instrumentOptions).getItems().size());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.availables()) {
      assertTrue(instrument.name(),
          dataProvider(design.instrumentOptions).getItems().contains(instrument));
    }
  }

  @Test
  public void instrument_DisabledOption() {
    instrument = MassDetectionInstrument.TOF;
    Submission submission = new Submission();
    submission.setMassDetectionInstrument(instrument);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.instrumentOptions).getItems().contains(instrument));
    assertFalse(design.instrumentOptions.getItemEnabledProvider().test(instrument));
  }

  @Test
  public void proteinIdentification_Options() {
    presenter.init(view);

    assertEquals(ProteinIdentification.availables().size(),
        dataProvider(design.proteinIdentificationOptions).getItems().size());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertTrue(proteinIdentification.name(), dataProvider(design.proteinIdentificationOptions)
          .getItems().contains(proteinIdentification));
    }
  }

  @Test
  public void proteinIdentification_DisabledOption() {
    proteinIdentification = ProteinIdentification.NCBINR;
    Submission submission = new Submission();
    submission.setProteinIdentification(proteinIdentification);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.proteinIdentificationOptions).getItems()
        .contains(proteinIdentification));
    assertFalse(
        design.proteinIdentificationOptions.getItemEnabledProvider().test(proteinIdentification));
  }

  @Test
  public void proteinIdentification_RequiredText() {
    presenter.init(view);

    design.proteinIdentificationOptions.setValue(REFSEQ);
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    design.proteinIdentificationOptions.setValue(UNIPROT);
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    design.proteinIdentificationOptions.setValue(ProteinIdentification.OTHER);
    assertTrue(design.proteinIdentificationLinkField.isVisible());
  }

  @Test
  public void quantification_RequiredText() {
    presenter.init(view);

    design.quantificationOptions.setValue(null);
    assertFalse(design.quantificationLabelsField.isRequiredIndicatorVisible());
    design.quantificationOptions.setValue(Quantification.LABEL_FREE);
    assertFalse(design.quantificationLabelsField.isRequiredIndicatorVisible());
    design.quantificationOptions.setValue(Quantification.SILAC);
    assertTrue(design.quantificationLabelsField.isRequiredIndicatorVisible());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.sampleTypeLabel.getStyleName().contains(SAMPLE_TYPE_LABEL));
    assertTrue(design.inactiveLabel.getStyleName().contains(INACTIVE_LABEL));
    assertTrue(design.servicePanel.getStyleName().contains(SERVICE_PANEL));
    assertTrue(design.servicePanel.getStyleName().contains(REQUIRED));
    assertTrue(design.serviceOptions.getStyleName().contains(SERVICE_PROPERTY));
    assertTrue(design.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(design.sampleSupportOptions.getStyleName().contains(SAMPLE_SUPPORT_PROPERTY));
    assertTrue(design.solutionSolventField.getStyleName().contains(SOLUTION_SOLVENT_PROPERTY));
    assertTrue(design.sampleCountField.getStyleName().contains(SAMPLE_COUNT_PROPERTY));
    assertTrue(design.sampleNameField.getStyleName().contains(SAMPLE_NAME_PROPERTY));
    assertTrue(design.formulaField.getStyleName().contains(FORMULA_PROPERTY));
    assertTrue(design.structureLayout.getStyleName().contains(REQUIRED));
    assertTrue(design.structureButton.getStyleName().contains(STRUCTURE_PROPERTY));
    verify(view.structureUploader).addStyleName(STRUCTURE_UPLOADER);
    assertTrue(design.monoisotopicMassField.getStyleName().contains(MONOISOTOPIC_MASS_PROPERTY));
    assertTrue(design.averageMassField.getStyleName().contains(AVERAGE_MASS_PROPERTY));
    assertTrue(design.toxicityField.getStyleName().contains(TOXICITY_PROPERTY));
    assertTrue(design.lightSensitiveField.getStyleName().contains(LIGHT_SENSITIVE_PROPERTY));
    assertTrue(
        design.storageTemperatureOptions.getStyleName().contains(STORAGE_TEMPERATURE_PROPERTY));
    assertTrue(
        design.sampleContainerTypeOptions.getStyleName().contains(SAMPLES_CONTAINER_TYPE_PROPERTY));
    assertTrue(
        design.plateNameField.getStyleName().contains(PLATE_PROPERTY + "-" + PLATE_NAME_PROPERTY));
    assertTrue(design.samplesLabel.getStyleName().contains(SAMPLES_PROPERTY));
    assertTrue(design.samplesGrid.getStyleName().contains(SAMPLES_TABLE));
    assertTrue(design.fillSamplesButton.getStyleName().contains(FILL_SAMPLES_PROPERTY));
    assertTrue(design.fillSamplesButton.getStyleName().contains(BUTTON_SKIP_ROW));
    verify(view.plateComponent).addStyleName(SAMPLES_PLATE);
    assertTrue(design.experiencePanel.getStyleName().contains(EXPERIENCE_PANEL));
    assertTrue(design.experienceField.getStyleName().contains(EXPERIENCE_PROPERTY));
    assertTrue(design.experienceGoalField.getStyleName().contains(EXPERIENCE_GOAL_PROPERTY));
    assertTrue(design.taxonomyField.getStyleName().contains(TAXONOMY_PROPERTY));
    assertTrue(design.proteinNameField.getStyleName().contains(PROTEIN_NAME_PROPERTY));
    assertTrue(design.proteinWeightField.getStyleName().contains(PROTEIN_WEIGHT_PROPERTY));
    assertTrue(design.postTranslationModificationField.getStyleName()
        .contains(POST_TRANSLATION_MODIFICATION_PROPERTY));
    assertTrue(design.sampleQuantityField.getStyleName().contains(SAMPLE_QUANTITY_PROPERTY));
    assertTrue(design.sampleVolumeField.getStyleName().contains(SAMPLE_VOLUME_PROPERTY));
    assertTrue(design.standardsPanel.getStyleName().contains(STANDARDS_PANEL));
    assertTrue(design.standardCountField.getStyleName().contains(STANDARD_COUNT_PROPERTY));
    assertTrue(design.standardsGrid.getStyleName().contains(STANDARD_PROPERTY));
    assertTrue(design.fillStandardsButton.getStyleName().contains(FILL_STANDARDS_PROPERTY));
    assertTrue(design.fillStandardsButton.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.contaminantsPanel.getStyleName().contains(CONTAMINANTS_PANEL));
    assertTrue(design.contaminantCountField.getStyleName().contains(CONTAMINANT_COUNT_PROPERTY));
    assertTrue(design.contaminantsGrid.getStyleName().contains(CONTAMINANT_PROPERTY));
    assertTrue(design.fillContaminantsButton.getStyleName().contains(FILL_CONTAMINANTS_PROPERTY));
    assertTrue(design.fillContaminantsButton.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.gelPanel.getStyleName().contains(GEL_PANEL));
    assertTrue(design.separationField.getStyleName().contains(SEPARATION_PROPERTY));
    assertTrue(design.thicknessField.getStyleName().contains(THICKNESS_PROPERTY));
    assertTrue(design.colorationField.getStyleName().contains(COLORATION_PROPERTY));
    assertTrue(design.otherColorationField.getStyleName().contains(OTHER_COLORATION_PROPERTY));
    assertTrue(design.developmentTimeField.getStyleName().contains(DEVELOPMENT_TIME_PROPERTY));
    assertTrue(design.decolorationField.getStyleName().contains(DECOLORATION_PROPERTY));
    assertTrue(
        design.weightMarkerQuantityField.getStyleName().contains(WEIGHT_MARKER_QUANTITY_PROPERTY));
    assertTrue(design.proteinQuantityField.getStyleName().contains(PROTEIN_QUANTITY_PROPERTY));
    assertTrue(design.gelImagesLayout.getStyleName().contains(REQUIRED));
    verify(view.gelImagesUploader).addStyleName(GEL_IMAGES_PROPERTY);
    assertTrue(design.gelImagesGrid.getStyleName().contains(GEL_IMAGES_TABLE));
    assertTrue(design.servicesPanel.getStyleName().contains(SERVICES_PANEL));
    assertTrue(design.digestionOptions.getStyleName().contains(DIGESTION_PROPERTY));
    assertTrue(design.usedProteolyticDigestionMethodField.getStyleName()
        .contains(USED_DIGESTION_PROPERTY));
    assertTrue(design.otherProteolyticDigestionMethodField.getStyleName()
        .contains(OTHER_DIGESTION_PROPERTY));
    assertTrue(design.enrichmentLabel.getStyleName().contains(ENRICHEMENT_PROPERTY));
    assertTrue(design.exclusionsLabel.getStyleName().contains(EXCLUSIONS_PROPERTY));
    assertTrue(design.injectionTypeOptions.getStyleName().contains(INJECTION_TYPE_PROPERTY));
    assertTrue(design.sourceOptions.getStyleName().contains(SOURCE_PROPERTY));
    assertTrue(design.proteinContentOptions.getStyleName().contains(PROTEIN_CONTENT_PROPERTY));
    assertTrue(design.instrumentOptions.getStyleName().contains(INSTRUMENT_PROPERTY));
    assertTrue(design.proteinIdentificationOptions.getStyleName()
        .contains(PROTEIN_IDENTIFICATION_PROPERTY));
    assertTrue(design.proteinIdentificationLinkField.getStyleName()
        .contains(PROTEIN_IDENTIFICATION_LINK_PROPERTY));
    assertTrue(design.quantificationOptions.getStyleName().contains(QUANTIFICATION_PROPERTY));
    assertTrue(
        design.quantificationLabelsField.getStyleName().contains(QUANTIFICATION_LABELS_PROPERTY));
    assertTrue(design.highResolutionOptions.getStyleName().contains(HIGH_RESOLUTION_PROPERTY));
    assertTrue(design.solventsLayout.getStyleName().contains(REQUIRED));
    assertTrue(design.acetonitrileSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name()));
    assertTrue(design.methanolSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name()));
    assertTrue(design.chclSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name()));
    assertTrue(design.otherSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name()));
    assertTrue(design.otherSolventField.getStyleName().contains(OTHER_SOLVENT_PROPERTY));
    assertTrue(design.otherSolventField.getStyleName().contains(ValoTheme.TEXTFIELD_SMALL));
    assertTrue(design.otherSolventNoteLabel.getStyleName().contains(OTHER_SOLVENT_NOTE));
    assertTrue(design.commentPanel.getStyleName().contains(COMMENT_PANEL));
    assertTrue(design.commentField.getStyleName().contains(COMMENT_PROPERTY));
    assertTrue(design.filesPanel.getStyleName().contains(FILES_PROPERTY));
    verify(view.filesUploader).addStyleName(FILES_UPLOADER);
    assertTrue(design.filesGrid.getStyleName().contains(FILES_GRID));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanationPanel.getStyleName().contains(REQUIRED));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
    assertTrue(design.saveButton.getStyleName().contains(SAVE));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLE_TYPE_LABEL), design.sampleTypeLabel.getValue());
    assertEquals(resources.message(INACTIVE_LABEL), design.inactiveLabel.getValue());
    assertEquals(resources.message(SERVICE_PROPERTY), design.servicePanel.getCaption());
    assertEquals(null, design.serviceOptions.getCaption());
    for (Service service : Service.availables()) {
      assertEquals(service.getLabel(locale),
          design.serviceOptions.getItemCaptionGenerator().apply(service));
    }
    assertEquals(resources.message(SAMPLES_PANEL), design.samplesPanel.getCaption());
    assertEquals(resources.message(SAMPLE_SUPPORT_PROPERTY),
        design.sampleSupportOptions.getCaption());
    for (SampleSupport support : SampleSupport.values()) {
      assertEquals(support.getLabel(locale),
          design.sampleSupportOptions.getItemCaptionGenerator().apply(support));
    }
    assertEquals(resources.message(SOLUTION_SOLVENT_PROPERTY),
        design.solutionSolventField.getCaption());
    assertEquals(resources.message(SAMPLE_COUNT_PROPERTY), design.sampleCountField.getCaption());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY), design.sampleNameField.getCaption());
    assertEquals(resources.message(FORMULA_PROPERTY), design.formulaField.getCaption());
    assertEquals(resources.message(STRUCTURE_PROPERTY), design.structureLayout.getCaption());
    assertEquals("", design.structureButton.getCaption());
    verify(view.structureUploader).setButtonCaption(resources.message(STRUCTURE_UPLOADER));
    verify(view.structureUploader).setImmediateMode(true);
    assertEquals(resources.message(MONOISOTOPIC_MASS_PROPERTY),
        design.monoisotopicMassField.getCaption());
    assertEquals(resources.message(AVERAGE_MASS_PROPERTY), design.averageMassField.getCaption());
    assertEquals(resources.message(TOXICITY_PROPERTY), design.toxicityField.getCaption());
    assertEquals(resources.message(LIGHT_SENSITIVE_PROPERTY),
        design.lightSensitiveField.getCaption());
    assertEquals(resources.message(STORAGE_TEMPERATURE_PROPERTY),
        design.storageTemperatureOptions.getCaption());
    for (StorageTemperature storageTemperature : StorageTemperature.values()) {
      assertEquals(storageTemperature.getLabel(locale),
          design.storageTemperatureOptions.getItemCaptionGenerator().apply(storageTemperature));
    }
    assertEquals(resources.message(SAMPLES_CONTAINER_TYPE_PROPERTY),
        design.sampleContainerTypeOptions.getCaption());
    for (SampleContainerType containerType : SampleContainerType.values()) {
      assertEquals(containerType.getLabel(locale),
          design.sampleContainerTypeOptions.getItemCaptionGenerator().apply(containerType));
    }
    assertEquals(resources.message(PLATE_PROPERTY + "." + PLATE_NAME_PROPERTY),
        design.plateNameField.getCaption());
    assertEquals(resources.message(SAMPLES_PROPERTY), design.samplesLabel.getCaption());
    assertEquals(null, design.samplesGrid.getCaption());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY),
        design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY),
        design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT_PROPERTY),
        design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_SAMPLES_PROPERTY), design.fillSamplesButton.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillSamplesButton.getIcon());
    assertEquals(null, view.plateComponent.getCaption());
    assertEquals(resources.message(EXPERIENCE_PANEL), design.experiencePanel.getCaption());
    assertEquals(resources.message(EXPERIENCE_PROPERTY), design.experienceField.getCaption());
    assertEquals(resources.message(EXPERIENCE_GOAL_PROPERTY),
        design.experienceGoalField.getCaption());
    assertEquals(resources.message(TAXONOMY_PROPERTY), design.taxonomyField.getCaption());
    assertEquals(resources.message(PROTEIN_NAME_PROPERTY), design.proteinNameField.getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT_PROPERTY),
        design.proteinWeightField.getCaption());
    assertEquals(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY),
        design.postTranslationModificationField.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY_PROPERTY),
        design.sampleQuantityField.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY_PROPERTY + "." + EXAMPLE),
        design.sampleQuantityField.getPlaceholder());
    assertEquals(resources.message(SAMPLE_VOLUME_PROPERTY), design.sampleVolumeField.getCaption());
    assertEquals(resources.message(STANDARDS_PANEL), design.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT_PROPERTY),
        design.standardCountField.getCaption());
    assertEquals(null, design.standardsGrid.getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_NAME_PROPERTY),
        design.standardsGrid.getColumn(STANDARD_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY),
        design.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY).getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_COMMENT_PROPERTY),
        design.standardsGrid.getColumn(STANDARD_COMMENT_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_STANDARDS_PROPERTY),
        design.fillStandardsButton.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillStandardsButton.getIcon());
    assertEquals(resources.message(CONTAMINANTS_PANEL), design.contaminantsPanel.getCaption());
    assertEquals(resources.message(CONTAMINANT_COUNT_PROPERTY),
        design.contaminantCountField.getCaption());
    assertEquals(null, design.contaminantsGrid.getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_NAME_PROPERTY),
        design.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY),
        design.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY).getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_COMMENT_PROPERTY),
        design.contaminantsGrid.getColumn(CONTAMINANT_COMMENT_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_CONTAMINANTS_PROPERTY),
        design.fillContaminantsButton.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillContaminantsButton.getIcon());
    assertEquals(resources.message(GEL_PANEL), design.gelPanel.getCaption());
    assertEquals(resources.message(SEPARATION_PROPERTY), design.separationField.getCaption());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          design.separationField.getItemCaptionGenerator().apply(separation));
    }
    assertEquals(resources.message(THICKNESS_PROPERTY), design.thicknessField.getCaption());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          design.thicknessField.getItemCaptionGenerator().apply(thickness));
    }
    assertEquals(resources.message(COLORATION_PROPERTY), design.colorationField.getCaption());
    assertEquals(GelColoration.getNullLabel(locale),
        design.colorationField.getEmptySelectionCaption());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          design.colorationField.getItemCaptionGenerator().apply(coloration));
    }
    assertEquals(resources.message(OTHER_COLORATION_PROPERTY),
        design.otherColorationField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY),
        design.developmentTimeField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE),
        design.developmentTimeField.getPlaceholder());
    assertEquals(resources.message(DECOLORATION_PROPERTY), design.decolorationField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY),
        design.weightMarkerQuantityField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE),
        design.weightMarkerQuantityField.getPlaceholder());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY),
        design.proteinQuantityField.getCaption());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE),
        design.proteinQuantityField.getPlaceholder());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY), design.gelImagesLayout.getCaption());
    verify(view.gelImagesUploader).setUploadButtonCaption(resources.message(GEL_IMAGES_UPLOADER));
    assertEquals(null, design.gelImagesGrid.getCaption());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + GEL_IMAGE_FILENAME_PROPERTY),
        design.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).getCaption());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE),
        design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).getCaption());
    assertEquals(resources.message(SERVICES_PANEL), design.servicesPanel.getCaption());
    assertEquals(resources.message(DIGESTION_PROPERTY), design.digestionOptions.getCaption());
    for (ProteolyticDigestion digestion : ProteolyticDigestion.values()) {
      assertEquals(digestion.getLabel(locale),
          design.digestionOptions.getItemCaptionGenerator().apply(digestion));
    }
    assertEquals(resources.message(USED_DIGESTION_PROPERTY),
        design.usedProteolyticDigestionMethodField.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION_PROPERTY),
        design.otherProteolyticDigestionMethodField.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION_PROPERTY + ".note"),
        design.otherProteolyticDigestionMethodNote.getValue());
    assertEquals(resources.message(ENRICHEMENT_PROPERTY), design.enrichmentLabel.getCaption());
    assertEquals(resources.message(ENRICHEMENT_PROPERTY + ".value"),
        design.enrichmentLabel.getValue());
    assertEquals(resources.message(EXCLUSIONS_PROPERTY), design.exclusionsLabel.getCaption());
    assertEquals(resources.message(EXCLUSIONS_PROPERTY + ".value"),
        design.exclusionsLabel.getValue());
    assertEquals(resources.message(INJECTION_TYPE_PROPERTY),
        design.injectionTypeOptions.getCaption());
    for (InjectionType injectionType : InjectionType.values()) {
      assertEquals(injectionType.getLabel(locale),
          design.injectionTypeOptions.getItemCaptionGenerator().apply(injectionType));
    }
    assertEquals(resources.message(SOURCE_PROPERTY), design.sourceOptions.getCaption());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.values()) {
      assertEquals(source.getLabel(locale),
          design.sourceOptions.getItemCaptionGenerator().apply(source));
    }
    assertEquals(resources.message(PROTEIN_CONTENT_PROPERTY),
        design.proteinContentOptions.getCaption());
    for (ProteinContent proteinContent : ProteinContent.values()) {
      assertEquals(proteinContent.getLabel(locale),
          design.proteinContentOptions.getItemCaptionGenerator().apply(proteinContent));
    }
    assertEquals(resources.message(INSTRUMENT_PROPERTY), design.instrumentOptions.getCaption());
    assertEquals(MassDetectionInstrument.getNullLabel(locale),
        design.instrumentOptions.getItemCaptionGenerator().apply(null));
    for (MassDetectionInstrument instrument : MassDetectionInstrument.availables()) {
      assertEquals(instrument.getLabel(locale),
          design.instrumentOptions.getItemCaptionGenerator().apply(instrument));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_PROPERTY),
        design.proteinIdentificationOptions.getCaption());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertEquals(proteinIdentification.getLabel(locale), design.proteinIdentificationOptions
          .getItemCaptionGenerator().apply(proteinIdentification));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY),
        design.proteinIdentificationLinkField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_PROPERTY),
        design.quantificationOptions.getCaption());
    assertEquals(Quantification.getNullLabel(locale),
        design.quantificationOptions.getItemCaptionGenerator().apply(null));
    for (Quantification quantification : Quantification.values()) {
      assertEquals(quantification.getLabel(locale),
          design.quantificationOptions.getItemCaptionGenerator().apply(quantification));
    }
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY),
        design.quantificationLabelsField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE),
        design.quantificationLabelsField.getPlaceholder());
    assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY),
        design.highResolutionOptions.getCaption());
    for (boolean value : new boolean[] { false, true }) {
      assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY + "." + value),
          design.highResolutionOptions.getItemCaptionGenerator().apply(value));
    }
    assertEquals(resources.message(SOLVENTS_PROPERTY), design.solventsLayout.getCaption());
    assertEquals(Solvent.ACETONITRILE.getLabel(locale),
        design.acetonitrileSolventsField.getCaption());
    assertEquals(Solvent.METHANOL.getLabel(locale), design.methanolSolventsField.getCaption());
    assertEquals(Solvent.CHCL3.getLabel(locale), design.chclSolventsField.getCaption());
    assertEquals(Solvent.OTHER.getLabel(locale), design.otherSolventsField.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT_PROPERTY), design.otherSolventField.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT_NOTE), design.otherSolventNoteLabel.getValue());
    assertEquals(resources.message(COMMENT_PANEL), design.commentPanel.getCaption());
    assertEquals(null, design.commentField.getCaption());
    assertEquals(resources.message(FILES_PROPERTY), design.filesPanel.getCaption());
    verify(view.filesUploader).setUploadButtonCaption(resources.message(FILES_UPLOADER));
    assertEquals(null, design.filesGrid.getCaption());
    assertEquals(resources.message(FILES_PROPERTY + "." + FILE_FILENAME_PROPERTY),
        design.filesGrid.getColumn(FILE_FILENAME_PROPERTY).getCaption());
    assertEquals(resources.message(FILES_PROPERTY + "." + REMOVE_FILE),
        design.filesGrid.getColumn(REMOVE_FILE).getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(SAVE), design.saveButton.getCaption());
  }

  @Test
  public void readOnly_True() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertTrue(design.serviceOptions.isReadOnly());
    assertTrue(design.sampleSupportOptions.isReadOnly());
    assertTrue(design.solutionSolventField.isReadOnly());
    assertTrue(design.sampleCountField.isReadOnly());
    assertTrue(design.sampleNameField.isReadOnly());
    assertTrue(design.formulaField.isReadOnly());
    assertTrue(design.monoisotopicMassField.isReadOnly());
    assertTrue(design.averageMassField.isReadOnly());
    assertTrue(design.toxicityField.isReadOnly());
    assertTrue(design.lightSensitiveField.isReadOnly());
    assertTrue(design.storageTemperatureOptions.isReadOnly());
    assertTrue(design.sampleContainerTypeOptions.isReadOnly());
    assertTrue(design.plateNameField.isReadOnly());
    SubmissionSample firstSample = dataProvider(design.samplesGrid).getItems().iterator().next();
    assertTrue(((TextField) design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertTrue(((TextField) design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .getValueProvider().apply(firstSample)).isReadOnly());
    assertTrue(((TextField) design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertTrue(design.experienceField.isReadOnly());
    assertTrue(design.experienceGoalField.isReadOnly());
    assertTrue(design.taxonomyField.isReadOnly());
    assertTrue(design.proteinNameField.isReadOnly());
    assertTrue(design.proteinWeightField.isReadOnly());
    assertTrue(design.postTranslationModificationField.isReadOnly());
    assertTrue(design.sampleQuantityField.isReadOnly());
    assertTrue(design.sampleVolumeField.isReadOnly());
    assertTrue(design.standardCountField.isReadOnly());
    Standard firstStandard = dataProvider(design.standardsGrid).getItems().iterator().next();
    assertTrue(((TextField) design.standardsGrid.getColumn(STANDARD_NAME_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertTrue(design.contaminantCountField.isReadOnly());
    Contaminant firstContaminant =
        dataProvider(design.contaminantsGrid).getItems().iterator().next();
    assertTrue(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_COMMENT_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(design.separationField.isReadOnly());
    assertTrue(design.thicknessField.isReadOnly());
    assertTrue(design.colorationField.isReadOnly());
    assertTrue(design.otherColorationField.isReadOnly());
    assertTrue(design.developmentTimeField.isReadOnly());
    assertTrue(design.decolorationField.isReadOnly());
    assertTrue(design.weightMarkerQuantityField.isReadOnly());
    assertTrue(design.proteinQuantityField.isReadOnly());
    assertTrue(design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
    assertTrue(design.digestionOptions.isReadOnly());
    assertTrue(design.usedProteolyticDigestionMethodField.isReadOnly());
    assertTrue(design.otherProteolyticDigestionMethodField.isReadOnly());
    assertTrue(design.injectionTypeOptions.isReadOnly());
    assertTrue(design.sourceOptions.isReadOnly());
    assertTrue(design.proteinContentOptions.isReadOnly());
    assertTrue(design.instrumentOptions.isReadOnly());
    assertTrue(design.proteinIdentificationOptions.isReadOnly());
    assertTrue(design.proteinIdentificationLinkField.isReadOnly());
    assertTrue(design.quantificationOptions.isReadOnly());
    assertTrue(design.quantificationLabelsField.isReadOnly());
    assertTrue(design.highResolutionOptions.isReadOnly());
    assertTrue(design.acetonitrileSolventsField.isReadOnly());
    assertTrue(design.methanolSolventsField.isReadOnly());
    assertTrue(design.chclSolventsField.isReadOnly());
    assertTrue(design.otherSolventsField.isReadOnly());
    assertTrue(design.otherSolventField.isReadOnly());
    assertTrue(design.commentField.isReadOnly());
    assertTrue(design.filesGrid.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void readOnly_False() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.serviceOptions.isReadOnly());
    assertFalse(design.sampleSupportOptions.isReadOnly());
    assertFalse(design.solutionSolventField.isReadOnly());
    assertFalse(design.sampleCountField.isReadOnly());
    assertFalse(design.sampleNameField.isReadOnly());
    assertFalse(design.formulaField.isReadOnly());
    assertFalse(design.monoisotopicMassField.isReadOnly());
    assertFalse(design.averageMassField.isReadOnly());
    assertFalse(design.toxicityField.isReadOnly());
    assertFalse(design.lightSensitiveField.isReadOnly());
    assertFalse(design.storageTemperatureOptions.isReadOnly());
    assertFalse(design.sampleContainerTypeOptions.isReadOnly());
    assertFalse(design.plateNameField.isReadOnly());
    SubmissionSample firstSample = dataProvider(design.samplesGrid).getItems().iterator().next();
    assertFalse(((TextField) design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertFalse(((TextField) design.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .getValueProvider().apply(firstSample)).isReadOnly());
    assertFalse(((TextField) design.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY)
        .getValueProvider().apply(firstSample)).isReadOnly());
    assertFalse(design.experienceField.isReadOnly());
    assertFalse(design.experienceGoalField.isReadOnly());
    assertFalse(design.taxonomyField.isReadOnly());
    assertFalse(design.proteinNameField.isReadOnly());
    assertFalse(design.proteinWeightField.isReadOnly());
    assertFalse(design.postTranslationModificationField.isReadOnly());
    assertFalse(design.sampleQuantityField.isReadOnly());
    assertFalse(design.sampleVolumeField.isReadOnly());
    assertFalse(design.standardCountField.isReadOnly());
    Standard firstStandard = dataProvider(design.standardsGrid).getItems().iterator().next();
    assertFalse(((TextField) design.standardsGrid.getColumn(STANDARD_NAME_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) design.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) design.standardsGrid.getColumn(STANDARD_COMMENT_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertFalse(design.contaminantCountField.isReadOnly());
    Contaminant firstContaminant =
        dataProvider(design.contaminantsGrid).getItems().iterator().next();
    assertFalse(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) design.contaminantsGrid.getColumn(CONTAMINANT_COMMENT_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(design.separationField.isReadOnly());
    assertFalse(design.thicknessField.isReadOnly());
    assertFalse(design.colorationField.isReadOnly());
    assertFalse(design.otherColorationField.isReadOnly());
    assertFalse(design.developmentTimeField.isReadOnly());
    assertFalse(design.decolorationField.isReadOnly());
    assertFalse(design.weightMarkerQuantityField.isReadOnly());
    assertFalse(design.proteinQuantityField.isReadOnly());
    assertFalse(design.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
    assertFalse(design.digestionOptions.isReadOnly());
    assertFalse(design.usedProteolyticDigestionMethodField.isReadOnly());
    assertFalse(design.otherProteolyticDigestionMethodField.isReadOnly());
    assertFalse(design.injectionTypeOptions.isReadOnly());
    assertFalse(design.sourceOptions.isReadOnly());
    assertFalse(design.proteinContentOptions.isReadOnly());
    assertFalse(design.instrumentOptions.isReadOnly());
    assertFalse(design.proteinIdentificationOptions.isReadOnly());
    assertFalse(design.proteinIdentificationLinkField.isReadOnly());
    assertFalse(design.quantificationOptions.isReadOnly());
    assertFalse(design.quantificationLabelsField.isReadOnly());
    assertFalse(design.highResolutionOptions.isReadOnly());
    assertFalse(design.acetonitrileSolventsField.isReadOnly());
    assertFalse(design.methanolSolventsField.isReadOnly());
    assertFalse(design.chclSolventsField.isReadOnly());
    assertFalse(design.otherSolventsField.isReadOnly());
    assertFalse(design.otherSolventField.isReadOnly());
    assertFalse(design.commentField.isReadOnly());
    assertFalse(design.filesGrid.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void readOnly_False_ForceUpdate() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    sample.setStatus(SampleStatus.RECEIVED);
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(design.explanationPanel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Solution_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertTrue(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_Solution() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertTrue(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertTrue(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertTrue(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertTrue(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichmentLabel.isVisible());
    assertTrue(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_UsedDigestion() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);

    design.digestionOptions.setValue(DIGESTED);

    assertTrue(design.usedProteolyticDigestionMethodField.isVisible());
  }

  @Test
  public void visible_Lcmsms_OtherDigestion() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);

    design.digestionOptions.setValue(ProteolyticDigestion.OTHER);

    assertTrue(design.otherProteolyticDigestionMethodField.isVisible());
    assertTrue(design.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void visible_Lcmsms_Plate() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);

    design.sampleContainerTypeOptions.setValue(WELL);

    assertTrue(design.plateNameField.isVisible());
    assertFalse(design.samplesGridLayout.isVisible());
    assertFalse(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertTrue(design.samplesPlateContainer.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(DRY);

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertTrue(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertTrue(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertTrue(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichmentLabel.isVisible());
    assertTrue(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(GEL);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertTrue(design.separationField.isVisible());
    assertTrue(design.thicknessField.isVisible());
    assertTrue(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertTrue(design.developmentTimeField.isVisible());
    assertTrue(design.decolorationField.isVisible());
    assertTrue(design.weightMarkerQuantityField.isVisible());
    assertTrue(design.proteinQuantityField.isVisible());
    assertTrue(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertTrue(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertTrue(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertTrue(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertTrue(design.separationField.isVisible());
    assertTrue(design.thicknessField.isVisible());
    assertTrue(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertTrue(design.developmentTimeField.isVisible());
    assertTrue(design.decolorationField.isVisible());
    assertTrue(design.weightMarkerQuantityField.isVisible());
    assertTrue(design.proteinQuantityField.isVisible());
    assertTrue(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(design.gelImagesGrid.isVisible());
    assertTrue(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichmentLabel.isVisible());
    assertTrue(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertTrue(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertTrue(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertTrue(design.quantificationOptions.isVisible());
    assertTrue(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_OtherColoration() {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);

    design.colorationField.setValue(GelColoration.OTHER);

    assertTrue(design.otherColorationField.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    submission.setStructure(new Structure());
    submission.getStructure().setFilename("structure.png");
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertTrue(design.solutionSolventField.isVisible());
    assertFalse(design.sampleCountField.isVisible());
    assertTrue(design.sampleNameField.isVisible());
    assertTrue(design.formulaField.isVisible());
    assertTrue(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(design.structureButton.isVisible());
    assertTrue(design.monoisotopicMassField.isVisible());
    assertTrue(design.averageMassField.isVisible());
    assertTrue(design.toxicityField.isVisible());
    assertTrue(design.lightSensitiveField.isVisible());
    assertTrue(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesGridLayout.isVisible());
    assertFalse(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experienceField.isVisible());
    assertFalse(design.experienceGoalField.isVisible());
    assertFalse(design.taxonomyField.isVisible());
    assertFalse(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertFalse(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertFalse(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertTrue(design.highResolutionOptions.isVisible());
    assertTrue(design.acetonitrileSolventsField.isVisible());
    assertTrue(design.methanolSolventsField.isVisible());
    assertTrue(design.chclSolventsField.isVisible());
    assertTrue(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    uploadStructure();

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertTrue(design.solutionSolventField.isVisible());
    assertFalse(design.sampleCountField.isVisible());
    assertTrue(design.sampleNameField.isVisible());
    assertTrue(design.formulaField.isVisible());
    assertTrue(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(design.structureButton.isVisible());
    assertTrue(design.monoisotopicMassField.isVisible());
    assertTrue(design.averageMassField.isVisible());
    assertTrue(design.toxicityField.isVisible());
    assertTrue(design.lightSensitiveField.isVisible());
    assertTrue(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesGridLayout.isVisible());
    assertFalse(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experienceField.isVisible());
    assertFalse(design.experienceGoalField.isVisible());
    assertFalse(design.taxonomyField.isVisible());
    assertFalse(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertFalse(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertFalse(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertTrue(design.highResolutionOptions.isVisible());
    assertTrue(design.acetonitrileSolventsField.isVisible());
    assertTrue(design.methanolSolventsField.isVisible());
    assertTrue(design.chclSolventsField.isVisible());
    assertTrue(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_NoStructure() {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    design.otherSolventsField.setValue(true);

    assertFalse(design.structureButton.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_OtherSolvents() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    uploadStructure();
    design.otherSolventsField.setValue(true);

    assertTrue(design.otherSolventField.isVisible());
    assertTrue(design.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    submission.setStructure(new Structure());
    submission.getStructure().setFilename("structure.png");
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertFalse(design.sampleCountField.isVisible());
    assertTrue(design.sampleNameField.isVisible());
    assertTrue(design.formulaField.isVisible());
    assertTrue(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(design.structureButton.isVisible());
    assertTrue(design.monoisotopicMassField.isVisible());
    assertTrue(design.averageMassField.isVisible());
    assertTrue(design.toxicityField.isVisible());
    assertTrue(design.lightSensitiveField.isVisible());
    assertTrue(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesGridLayout.isVisible());
    assertFalse(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experienceField.isVisible());
    assertFalse(design.experienceGoalField.isVisible());
    assertFalse(design.taxonomyField.isVisible());
    assertFalse(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertFalse(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertFalse(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertTrue(design.highResolutionOptions.isVisible());
    assertTrue(design.acetonitrileSolventsField.isVisible());
    assertTrue(design.methanolSolventsField.isVisible());
    assertTrue(design.chclSolventsField.isVisible());
    assertTrue(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(DRY);
    uploadStructure();

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertFalse(design.sampleCountField.isVisible());
    assertTrue(design.sampleNameField.isVisible());
    assertTrue(design.formulaField.isVisible());
    assertTrue(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(design.structureButton.isVisible());
    assertTrue(design.monoisotopicMassField.isVisible());
    assertTrue(design.averageMassField.isVisible());
    assertTrue(design.toxicityField.isVisible());
    assertTrue(design.lightSensitiveField.isVisible());
    assertTrue(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesGridLayout.isVisible());
    assertFalse(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experienceField.isVisible());
    assertFalse(design.experienceGoalField.isVisible());
    assertFalse(design.taxonomyField.isVisible());
    assertFalse(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertFalse(design.postTranslationModificationField.isVisible());
    assertFalse(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCountField.isVisible());
    assertFalse(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCountField.isVisible());
    assertFalse(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertFalse(design.injectionTypeOptions.isVisible());
    assertFalse(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertFalse(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertTrue(design.highResolutionOptions.isVisible());
    assertTrue(design.acetonitrileSolventsField.isVisible());
    assertTrue(design.methanolSolventsField.isVisible());
    assertTrue(design.chclSolventsField.isVisible());
    assertTrue(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertTrue(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertTrue(design.injectionTypeOptions.isVisible());
    assertTrue(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertTrue(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertTrue(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertTrue(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertTrue(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertTrue(design.injectionTypeOptions.isVisible());
    assertTrue(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeLabel.isVisible());
    assertFalse(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertFalse(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertFalse(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertFalse(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertTrue(design.injectionTypeOptions.isVisible());
    assertTrue(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttonsLayout.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry() {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(DRY);

    assertTrue(design.sampleTypeLabel.isVisible());
    assertTrue(design.inactiveLabel.isVisible());
    assertTrue(design.serviceOptions.isVisible());
    assertTrue(design.sampleSupportOptions.isVisible());
    assertFalse(design.solutionSolventField.isVisible());
    assertTrue(design.sampleCountField.isVisible());
    assertFalse(design.sampleNameField.isVisible());
    assertFalse(design.formulaField.isVisible());
    assertFalse(design.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.structureButton.isVisible());
    assertFalse(design.monoisotopicMassField.isVisible());
    assertFalse(design.averageMassField.isVisible());
    assertFalse(design.toxicityField.isVisible());
    assertFalse(design.lightSensitiveField.isVisible());
    assertFalse(design.storageTemperatureOptions.isVisible());
    assertFalse(design.sampleContainerTypeOptions.isVisible());
    assertFalse(design.plateNameField.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesGridLayout.isVisible());
    assertTrue(design.samplesGrid.isVisible());
    assertTrue(design.fillSamplesButton.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experienceField.isVisible());
    assertTrue(design.experienceGoalField.isVisible());
    assertTrue(design.taxonomyField.isVisible());
    assertTrue(design.proteinNameField.isVisible());
    assertFalse(design.proteinWeightField.isVisible());
    assertTrue(design.postTranslationModificationField.isVisible());
    assertTrue(design.sampleQuantityField.isVisible());
    assertFalse(design.sampleVolumeField.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCountField.isVisible());
    assertTrue(design.standardsGrid.isVisible());
    assertTrue(design.fillStandardsButton.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCountField.isVisible());
    assertTrue(design.contaminantsGrid.isVisible());
    assertTrue(design.fillContaminantsButton.isVisible());
    assertFalse(design.separationField.isVisible());
    assertFalse(design.thicknessField.isVisible());
    assertFalse(design.colorationField.isVisible());
    assertFalse(design.otherColorationField.isVisible());
    assertFalse(design.developmentTimeField.isVisible());
    assertFalse(design.decolorationField.isVisible());
    assertFalse(design.weightMarkerQuantityField.isVisible());
    assertFalse(design.proteinQuantityField.isVisible());
    assertFalse(design.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(design.gelImagesGrid.isVisible());
    assertFalse(design.digestionOptions.isVisible());
    assertFalse(design.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichmentLabel.isVisible());
    assertFalse(design.exclusionsLabel.isVisible());
    assertTrue(design.injectionTypeOptions.isVisible());
    assertTrue(design.sourceOptions.isVisible());
    assertFalse(design.proteinContentOptions.isVisible());
    assertTrue(design.instrumentOptions.isVisible());
    assertFalse(design.proteinIdentificationOptions.isVisible());
    assertFalse(design.proteinIdentificationLinkField.isVisible());
    assertFalse(design.quantificationOptions.isVisible());
    assertFalse(design.quantificationLabelsField.isVisible());
    assertFalse(design.highResolutionOptions.isVisible());
    assertFalse(design.acetonitrileSolventsField.isVisible());
    assertFalse(design.methanolSolventsField.isVisible());
    assertFalse(design.chclSolventsField.isVisible());
    assertFalse(design.otherSolventsField.isVisible());
    assertFalse(design.otherSolventField.isVisible());
    assertFalse(design.otherSolventNoteLabel.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttonsLayout.isVisible());
  }

  @Test
  public void save_MissingService() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(null);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.serviceOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSupport() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(null);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleSupportOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSolutionSolvent() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.solutionSolventField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.solutionSolventField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowOneSampleCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        design.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxSampleCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleCountField.setValue("200000");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        design.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleCountField.setValue("1.3");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleName() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleNameField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleName() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(any())).thenReturn(true);

    design.saveButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName)),
        design.sampleNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingFormula() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.formulaField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.formulaField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStructure() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(STRUCTURE_PROPERTY + "." + REQUIRED)),
        design.structureLayout.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.monoisotopicMassField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.monoisotopicMassField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.monoisotopicMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidAverageMass() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.averageMassField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.averageMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroAverageMass() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.averageMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.averageMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStorageTemperature() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.storageTemperatureOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.storageTemperatureOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingPlateName() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    design.plateNameField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.plateNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateName() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(plateService.nameAvailable(any())).thenReturn(false);

    design.saveButton.click();

    verify(plateService, atLeastOnce()).nameAvailable(plateName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        design.plateNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNames_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleNames_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    design.saveButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName1)),
        sampleNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNames_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleNames_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    design.saveButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName2)),
        sampleNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DuplicateSampleNames() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue(sampleName1);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sampleName1)),
        sampleNameField2.getComponentError().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    plate.well(0, 0).setSample(new SubmissionSample(null, ""));
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLES_PROPERTY + ".missing", sampleCount)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_MissingPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    plate.well(1, 0).setSample(new SubmissionSample(null, ""));
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLES_PROPERTY + ".missing", sampleCount)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_DuplicatePlateSampleNames() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    plate.well(1, 0).setSample(new SubmissionSample(null, sampleName1));
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sampleName1)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinWeight1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinWeight2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingExperience() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.experienceField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.experienceField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingTaxonomy() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.taxonomyField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.taxonomyField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.proteinWeightField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.proteinWeightField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.proteinWeightField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.proteinWeightField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleQuantity() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleQuantityField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleQuantityField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleVolume() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleVolumeField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleVolume() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleVolumeField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleVolume() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleVolumeField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.standardCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_InvalidStandardCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.standardCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroStandardCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.standardCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxStandardCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.standardCountField.setValue("200");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleStandardCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.standardCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.contaminantCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_InvalidContaminantCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.contaminantCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroContaminantCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.contaminantCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxContaminantCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.contaminantCountField.setValue("200");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleContaminantCount() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.contaminantCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantName_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantName_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantQuantity_1() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantQuantity_2() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingGelSeparation() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    design.separationField.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.separationField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingGelThickness() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    design.thicknessField.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.thicknessField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherGelColoration() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    design.colorationField.setValue(GelColoration.OTHER);
    design.otherColorationField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherColorationField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidWeightMarkerQuantity() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    design.weightMarkerQuantityField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.weightMarkerQuantityField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingGelImages() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(GEL_IMAGES_PROPERTY + "." + REQUIRED)),
        design.gelImagesLayout.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingDigestion() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.digestionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.digestionOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingUsedDigestion() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.digestionOptions.setValue(DIGESTED);
    design.usedProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.usedProteolyticDigestionMethodField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherDigestion() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.digestionOptions.setValue(ProteolyticDigestion.OTHER);
    design.otherProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherProteolyticDigestionMethodField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingInjectionType() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.injectionTypeOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.injectionTypeOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSource() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sourceOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sourceOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinContent() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.proteinContentOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinContentOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingInstrument() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.instrumentOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_MissingProteinIdentification() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.proteinIdentificationOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinIdentificationOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinIdentificationLink() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.proteinIdentificationOptions.setValue(ProteinIdentification.OTHER);
    design.proteinIdentificationLinkField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinIdentificationLinkField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingQuantificationLabels() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.quantificationOptions.setValue(Quantification.SILAC);
    design.quantificationLabelsField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.quantificationLabelsField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingHighResolution() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.highResolutionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.highResolutionOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSolvents() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.acetonitrileSolventsField.setValue(false);
    design.methanolSolventsField.setValue(false);
    design.chclSolventsField.setValue(false);
    design.otherSolventsField.setValue(false);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(SOLVENTS_PROPERTY + "." + REQUIRED)),
        design.solventsLayout.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherSolvent() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.otherSolventsField.setValue(true);
    design.otherSolventField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherSolventField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingExplanation() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 147L);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    List<SubmissionSample> samples = new ArrayList<>(dataProvider(design.samplesGrid).getItems());
    samples.forEach(sample -> {
      design.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getValueProvider().apply(sample);
    });
    design.explanation.setValue("");

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).forceUpdate(any(), any());
  }

  @Test
  public void save_Lcmsms_Solution() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Solution_Plate() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    Well well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(0, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(1, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Solution_OtherDigestion() throws Throwable {
    final ProteolyticDigestion digestion = ProteolyticDigestion.OTHER;
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.digestionOptions.setValue(digestion);
    design.otherProteolyticDigestionMethodField.setValue(otherDigestion);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(otherDigestion, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Dry_Plate() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    Well well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(0, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(1, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Gel() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(gelSeparation, submission.getSeparation());
    assertEquals(gelThickness, submission.getThickness());
    assertEquals(gelColoration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    assertEquals(2, submission.getGelImages().size());
    GelImage gelImage = submission.getGelImages().get(0);
    assertEquals(gelImageFilename1, gelImage.getFilename());
    assertArrayEquals(gelImageContent1, gelImage.getContent());
    gelImage = submission.getGelImages().get(1);
    assertEquals(gelImageFilename2, gelImage.getFilename());
    assertArrayEquals(gelImageContent2, gelImage.getContent());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Lcmsms_Gel_Plate() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(GEL);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(gelSeparation, submission.getSeparation());
    assertEquals(gelThickness, submission.getThickness());
    assertEquals(gelColoration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(decoloration, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity(), 0.0001);
    assertEquals(proteinQuantity, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    Well well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(0, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(WELL, container.getType());
    well = (Well) container;
    assertEquals(0, well.getColumn());
    assertEquals(1, well.getRow());
    assertEquals(plateName, well.getPlate().getName());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    assertEquals(2, submission.getGelImages().size());
    GelImage gelImage = submission.getGelImages().get(0);
    assertEquals(gelImageFilename1, gelImage.getFilename());
    assertArrayEquals(gelImageContent1, gelImage.getContent());
    gelImage = submission.getGelImages().get(1);
    assertEquals(gelImageFilename2, gelImage.getFilename());
    assertArrayEquals(gelImageContent2, gelImage.getContent());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_SmallMolecule_Solution() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(sampleName, submission.getExperience());
    assertEquals(null, submission.getGoal());
    assertEquals(null, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(null, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(null, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(highResolution, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(null, submission.getProteinContent());
    assertEquals(null, submission.getProtein());
    assertEquals(null, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(formula, submission.getFormula());
    assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
    assertEquals(averageMass, submission.getAverageMass(), 0.0001);
    assertEquals(solutionSolvent, submission.getSolutionSolvent());
    assertEquals(4, submission.getSolvents().size());
    SampleSolvent sampleSolvent = submission.getSolvents().get(0);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.ACETONITRILE, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(1);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.METHANOL, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(2);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.CHCL3, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(3);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.OTHER, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    assertEquals(otherSolvent, submission.getOtherSolvent());
    assertEquals(toxicity, submission.getToxicity());
    assertEquals(lightSensitive, submission.isLightSensitive());
    assertEquals(storageTemperature, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(1, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNotNull(submission.getStructure());
    Structure structure = submission.getStructure();
    assertEquals(structureFilename, structure.getFilename());
    assertArrayEquals(structureContent, structure.getContent());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", sampleName));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_SmallMolecule_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(sampleName, submission.getExperience());
    assertEquals(null, submission.getGoal());
    assertEquals(null, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(null, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(null, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(highResolution, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(null, submission.getProteinContent());
    assertEquals(null, submission.getProtein());
    assertEquals(null, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(formula, submission.getFormula());
    assertEquals(monoisotopicMass, submission.getMonoisotopicMass(), 0.0001);
    assertEquals(averageMass, submission.getAverageMass(), 0.0001);
    assertEquals(null, submission.getSolutionSolvent());
    assertEquals(4, submission.getSolvents().size());
    SampleSolvent sampleSolvent = submission.getSolvents().get(0);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.ACETONITRILE, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(1);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.METHANOL, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(2);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.CHCL3, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    sampleSolvent = submission.getSolvents().get(3);
    assertEquals(null, sampleSolvent.getId());
    assertEquals(Solvent.OTHER, sampleSolvent.getSolvent());
    assertEquals(false, sampleSolvent.isDeleted());
    assertEquals(otherSolvent, submission.getOtherSolvent());
    assertEquals(toxicity, submission.getToxicity());
    assertEquals(lightSensitive, submission.isLightSensitive());
    assertEquals(storageTemperature, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(1, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNotNull(submission.getStructure());
    Structure structure = submission.getStructure();
    assertEquals(structureFilename, structure.getFilename());
    assertArrayEquals(structureContent, structure.getContent());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", sampleName));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_SmallMolecule_Plate() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(SMALL_MOLECULE);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getOriginalContainer());
  }

  @Test
  public void save_Intactprotein_Solution() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(INTACT_PROTEIN, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(source, submission.getSource());
    assertEquals(null, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(null, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_Intactprotein_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(INTACT_PROTEIN, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(null, submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(source, submission.getSource());
    assertEquals(null, submission.getProteolyticDigestionMethod());
    assertEquals(null, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(null, submission.getProteinIdentification());
    assertEquals(null, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_IntactProtein_Plate() throws Throwable {
    presenter.init(view);
    design.serviceOptions.setValue(INTACT_PROTEIN);
    design.sampleSupportOptions.setValue(support);
    setFields();
    design.sampleContainerTypeOptions.setValue(WELL);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getOriginalContainer());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
  }

  @Test
  public void save_Update() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 36L);
    presenter.init(view);
    presenter.setValue(submission);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).update(submissionCaptor.capture());
    submission = submissionCaptor.getValue();
    assertEquals((Long) 36L, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals("cap_project", submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(LocalDate.of(2011, 11, 16), toLocalDate(submission.getSubmissionDate()));
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 447L, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    assertEquals((Long) 9L, sample.getOriginalContainer().getId());
    assertEquals(SampleContainerType.TUBE, sample.getOriginalContainer().getType());
    assertEquals("CAP_20111116_01", sample.getOriginalContainer().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(SampleStatus.TO_APPROVE, sample.getStatus());
    assertEquals(submission, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_ForceUpdate() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 147L);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    design.saveButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).forceUpdate(submissionCaptor.capture(), eq(explanation));
    submission = submissionCaptor.getValue();
    assertEquals((Long) 147L, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals("Flag", submission.getProject());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(null, submission.getSource());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(usedDigestion, submission.getUsedProteolyticDigestionMethod());
    assertEquals(null, submission.getOtherProteolyticDigestionMethod());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(proteinIdentificationLink, submission.getProteinIdentificationLink());
    assertEquals(null, submission.getEnrichmentType());
    assertEquals(false, submission.isLowResolution());
    assertEquals(false, submission.isHighResolution());
    assertEquals(false, submission.isMsms());
    assertEquals(false, submission.isExactMsms());
    assertEquals(null, submission.getMudPitFraction());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(null, submission.getSeparation());
    assertEquals(null, submission.getThickness());
    assertEquals(null, submission.getColoration());
    assertEquals(null, submission.getOtherColoration());
    assertEquals(null, submission.getDevelopmentTime());
    assertEquals(false, submission.isDecoloration());
    assertEquals(null, submission.getWeightMarkerQuantity());
    assertEquals(null, submission.getProteinQuantity());
    assertEquals(null, submission.getFormula());
    assertEquals(null, submission.getMonoisotopicMass());
    assertEquals(null, submission.getAverageMass());
    assertEquals(null, submission.getSolutionSolvent());
    assertTrue(submission.getSolvents() == null || submission.getSolvents().isEmpty());
    assertEquals(null, submission.getOtherSolvent());
    assertEquals(null, submission.getToxicity());
    assertEquals(false, submission.isLightSensitive());
    assertEquals(null, submission.getStorageTemperature());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationLabels, submission.getQuantificationLabels());
    assertEquals(comment, submission.getComment());
    assertEquals(LocalDate.of(2014, 10, 8), toLocalDate(submission.getSubmissionDate()));
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals((Long) 10L, submission.getUser().getId());
    assertEquals((Long) 2L, submission.getLaboratory().getId());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals((Long) 559L, sample.getId());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    assertEquals((Long) 11L, sample.getOriginalContainer().getId());
    assertEquals(SampleContainerType.TUBE, sample.getOriginalContainer().getType());
    assertEquals("POLR2A_20141008_1", sample.getOriginalContainer().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
    assertEquals(submission, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    sample = submission.getSamples().get(1);
    assertEquals((Long) 560L, sample.getId());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    assertEquals((Long) 12L, sample.getOriginalContainer().getId());
    assertEquals(SampleContainerType.TUBE, sample.getOriginalContainer().getType());
    assertEquals("POLR2A_20141008_2", sample.getOriginalContainer().getName());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComment());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComment());
    assertEquals(SampleStatus.DIGESTED, sample.getStatus());
    assertEquals(submission, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComment());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComment());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message(SAVE + ".done", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_ForceUpdateError() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 147L);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    design.serviceOptions.setValue(LC_MS_MS);
    design.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    doThrow(new PersistenceException("Could not update submission")).when(submissionService)
        .forceUpdate(any(), any());

    design.saveButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(UPDATE_ERROR, experience), stringCaptor.getValue());
  }

  @Test
  public void setValue_Lcmsms() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setValue(submission);

    assertEquals(LC_MS_MS, design.serviceOptions.getValue());
    assertEquals(SOLUTION, design.sampleSupportOptions.getValue());
    assertEquals(solutionSolvent, design.solutionSolventField.getValue());
    assertEquals(sampleName1, design.sampleNameField.getValue());
    assertEquals(formula, design.formulaField.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMassField), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMassField), 0.001);
    assertEquals(toxicity, design.toxicityField.getValue());
    assertEquals(lightSensitive, design.lightSensitiveField.getValue());
    assertEquals(storageTemperature, design.storageTemperatureOptions.getValue());
    assertEquals(sampleContainerType, design.sampleContainerTypeOptions.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCountField));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    List<SubmissionSample> samples = new ArrayList<>(samplesDataProvider.getItems());
    assertEquals(2, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals(sampleName1, sample.getName());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.001);
    sample = samples.get(1);
    assertEquals(sampleName2, sample.getName());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.001);
    assertEquals(experience, design.experienceField.getValue());
    assertEquals(experienceGoal, design.experienceGoalField.getValue());
    assertEquals(taxonomy, design.taxonomyField.getValue());
    assertEquals(proteinName, design.proteinNameField.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeightField), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModificationField.getValue());
    assertEquals(sampleQuantity, design.sampleQuantityField.getValue());
    assertEquals(sampleVolume, convert(doubleConverter, design.sampleVolumeField), 0.001);
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCountField));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standardsGrid);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount,
        convert(integerConverter, design.contaminantCountField));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminantsGrid);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separationField.getValue());
    assertEquals(gelThickness, design.thicknessField.getValue());
    assertEquals(gelColoration, design.colorationField.getValue());
    assertEquals(otherColoration, design.otherColorationField.getValue());
    assertEquals(developmentTime, design.developmentTimeField.getValue());
    assertEquals(decoloration, design.decolorationField.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantityField),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantityField.getValue());
    assertEquals(digestion, design.digestionOptions.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethodField.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethodField.getValue());
    assertEquals(injectionType, design.injectionTypeOptions.getValue());
    assertEquals(source, design.sourceOptions.getValue());
    assertEquals(proteinContent, design.proteinContentOptions.getValue());
    assertEquals(instrument, design.instrumentOptions.getValue());
    assertEquals(proteinIdentification, design.proteinIdentificationOptions.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLinkField.getValue());
    assertEquals(quantification, design.quantificationOptions.getValue());
    assertEquals(quantificationLabels, design.quantificationLabelsField.getValue());
    assertEquals(highResolution, design.highResolutionOptions.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolventsField.getValue());
    assertEquals(methanolSolvents, design.methanolSolventsField.getValue());
    assertEquals(chclSolvents, design.chclSolventsField.getValue());
    assertEquals(otherSolvents, design.otherSolventsField.getValue());
    assertEquals(otherSolvent, design.otherSolventField.getValue());
    assertEquals(comment, design.commentField.getValue());
  }

  @Test
  public void setValue_Lcmsms_Plate() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();
    Plate plate = new Plate(2L, plateName);
    plate.initWells();
    submission.getSamples().get(0).setOriginalContainer(plate.well(0, 0));
    submission.getSamples().get(1).setOriginalContainer(plate.well(1, 0));

    presenter.setValue(submission);

    assertEquals(LC_MS_MS, design.serviceOptions.getValue());
    assertEquals(SOLUTION, design.sampleSupportOptions.getValue());
    assertEquals(solutionSolvent, design.solutionSolventField.getValue());
    assertEquals(sampleName1, design.sampleNameField.getValue());
    assertEquals(formula, design.formulaField.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMassField), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMassField), 0.001);
    assertEquals(toxicity, design.toxicityField.getValue());
    assertEquals(lightSensitive, design.lightSensitiveField.getValue());
    assertEquals(storageTemperature, design.storageTemperatureOptions.getValue());
    assertEquals(SampleContainerType.WELL, design.sampleContainerTypeOptions.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCountField));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    List<SubmissionSample> samples = new ArrayList<>(samplesDataProvider.getItems());
    assertEquals(2, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals(sampleName1, sample.getName());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.001);
    sample = samples.get(1);
    assertEquals(sampleName2, sample.getName());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.001);
    assertEquals(plateName, design.plateNameField.getValue());
    verify(view.plateComponent).setValue(plate);
    assertEquals(experience, design.experienceField.getValue());
    assertEquals(experienceGoal, design.experienceGoalField.getValue());
    assertEquals(taxonomy, design.taxonomyField.getValue());
    assertEquals(proteinName, design.proteinNameField.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeightField), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModificationField.getValue());
    assertEquals(sampleQuantity, design.sampleQuantityField.getValue());
    assertEquals(sampleVolume, convert(doubleConverter, design.sampleVolumeField), 0.001);
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCountField));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standardsGrid);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount,
        convert(integerConverter, design.contaminantCountField));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminantsGrid);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separationField.getValue());
    assertEquals(gelThickness, design.thicknessField.getValue());
    assertEquals(gelColoration, design.colorationField.getValue());
    assertEquals(otherColoration, design.otherColorationField.getValue());
    assertEquals(developmentTime, design.developmentTimeField.getValue());
    assertEquals(decoloration, design.decolorationField.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantityField),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantityField.getValue());
    assertEquals(digestion, design.digestionOptions.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethodField.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethodField.getValue());
    assertEquals(injectionType, design.injectionTypeOptions.getValue());
    assertEquals(source, design.sourceOptions.getValue());
    assertEquals(proteinContent, design.proteinContentOptions.getValue());
    assertEquals(instrument, design.instrumentOptions.getValue());
    assertEquals(proteinIdentification, design.proteinIdentificationOptions.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLinkField.getValue());
    assertEquals(quantification, design.quantificationOptions.getValue());
    assertEquals(quantificationLabels, design.quantificationLabelsField.getValue());
    assertEquals(highResolution, design.highResolutionOptions.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolventsField.getValue());
    assertEquals(methanolSolvents, design.methanolSolventsField.getValue());
    assertEquals(chclSolvents, design.chclSolventsField.getValue());
    assertEquals(otherSolvents, design.otherSolventsField.getValue());
    assertEquals(otherSolvent, design.otherSolventField.getValue());
    assertEquals(comment, design.commentField.getValue());
  }

  @Test
  public void setValue_SmallMolecule() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();
    submission.setService(SMALL_MOLECULE);

    presenter.setValue(submission);

    assertEquals(SMALL_MOLECULE, design.serviceOptions.getValue());
    assertEquals(SOLUTION, design.sampleSupportOptions.getValue());
    assertEquals(solutionSolvent, design.solutionSolventField.getValue());
    assertEquals(sampleName1, design.sampleNameField.getValue());
    assertEquals(formula, design.formulaField.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMassField), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMassField), 0.001);
    assertEquals(toxicity, design.toxicityField.getValue());
    assertEquals(lightSensitive, design.lightSensitiveField.getValue());
    assertEquals(storageTemperature, design.storageTemperatureOptions.getValue());
    assertEquals(sampleContainerType, design.sampleContainerTypeOptions.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCountField));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    List<SubmissionSample> samples = new ArrayList<>(samplesDataProvider.getItems());
    assertEquals(2, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals(sampleName1, sample.getName());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.001);
    sample = samples.get(1);
    assertEquals(sampleName2, sample.getName());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.001);
    assertEquals(experience, design.experienceField.getValue());
    assertEquals(experienceGoal, design.experienceGoalField.getValue());
    assertEquals(taxonomy, design.taxonomyField.getValue());
    assertEquals(proteinName, design.proteinNameField.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeightField), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModificationField.getValue());
    assertEquals(sampleQuantity, design.sampleQuantityField.getValue());
    assertEquals(sampleVolume, convert(doubleConverter, design.sampleVolumeField), 0.001);
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCountField));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standardsGrid);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount,
        convert(integerConverter, design.contaminantCountField));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminantsGrid);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separationField.getValue());
    assertEquals(gelThickness, design.thicknessField.getValue());
    assertEquals(gelColoration, design.colorationField.getValue());
    assertEquals(otherColoration, design.otherColorationField.getValue());
    assertEquals(developmentTime, design.developmentTimeField.getValue());
    assertEquals(decoloration, design.decolorationField.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantityField),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantityField.getValue());
    assertEquals(digestion, design.digestionOptions.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethodField.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethodField.getValue());
    assertEquals(injectionType, design.injectionTypeOptions.getValue());
    assertEquals(source, design.sourceOptions.getValue());
    assertEquals(proteinContent, design.proteinContentOptions.getValue());
    assertEquals(instrument, design.instrumentOptions.getValue());
    assertEquals(proteinIdentification, design.proteinIdentificationOptions.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLinkField.getValue());
    assertEquals(quantification, design.quantificationOptions.getValue());
    assertEquals(quantificationLabels, design.quantificationLabelsField.getValue());
    assertEquals(highResolution, design.highResolutionOptions.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolventsField.getValue());
    assertEquals(methanolSolvents, design.methanolSolventsField.getValue());
    assertEquals(chclSolvents, design.chclSolventsField.getValue());
    assertEquals(otherSolvents, design.otherSolventsField.getValue());
    assertEquals(otherSolvent, design.otherSolventField.getValue());
    assertEquals(comment, design.commentField.getValue());
  }

  @Test
  public void setValue_IntactProtein() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();
    submission.setService(INTACT_PROTEIN);

    presenter.setValue(submission);

    assertEquals(INTACT_PROTEIN, design.serviceOptions.getValue());
    assertEquals(SOLUTION, design.sampleSupportOptions.getValue());
    assertEquals(solutionSolvent, design.solutionSolventField.getValue());
    assertEquals(sampleName1, design.sampleNameField.getValue());
    assertEquals(formula, design.formulaField.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMassField), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMassField), 0.001);
    assertEquals(toxicity, design.toxicityField.getValue());
    assertEquals(lightSensitive, design.lightSensitiveField.getValue());
    assertEquals(storageTemperature, design.storageTemperatureOptions.getValue());
    assertEquals(sampleContainerType, design.sampleContainerTypeOptions.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCountField));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samplesGrid);
    List<SubmissionSample> samples = new ArrayList<>(samplesDataProvider.getItems());
    assertEquals(2, samples.size());
    SubmissionSample sample = samples.get(0);
    assertEquals(sampleName1, sample.getName());
    assertEquals((Integer) sampleNumberProtein1, sample.getNumberProtein());
    assertEquals(proteinWeight1, sample.getMolecularWeight(), 0.001);
    sample = samples.get(1);
    assertEquals(sampleName2, sample.getName());
    assertEquals((Integer) sampleNumberProtein2, sample.getNumberProtein());
    assertEquals(proteinWeight2, sample.getMolecularWeight(), 0.001);
    assertEquals(experience, design.experienceField.getValue());
    assertEquals(experienceGoal, design.experienceGoalField.getValue());
    assertEquals(taxonomy, design.taxonomyField.getValue());
    assertEquals(proteinName, design.proteinNameField.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeightField), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModificationField.getValue());
    assertEquals(sampleQuantity, design.sampleQuantityField.getValue());
    assertEquals(sampleVolume, convert(doubleConverter, design.sampleVolumeField), 0.001);
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCountField));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standardsGrid);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount,
        convert(integerConverter, design.contaminantCountField));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminantsGrid);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separationField.getValue());
    assertEquals(gelThickness, design.thicknessField.getValue());
    assertEquals(gelColoration, design.colorationField.getValue());
    assertEquals(otherColoration, design.otherColorationField.getValue());
    assertEquals(developmentTime, design.developmentTimeField.getValue());
    assertEquals(decoloration, design.decolorationField.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantityField),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantityField.getValue());
    assertEquals(digestion, design.digestionOptions.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethodField.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethodField.getValue());
    assertEquals(injectionType, design.injectionTypeOptions.getValue());
    assertEquals(source, design.sourceOptions.getValue());
    assertEquals(proteinContent, design.proteinContentOptions.getValue());
    assertEquals(instrument, design.instrumentOptions.getValue());
    assertEquals(proteinIdentification, design.proteinIdentificationOptions.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLinkField.getValue());
    assertEquals(quantification, design.quantificationOptions.getValue());
    assertEquals(quantificationLabels, design.quantificationLabelsField.getValue());
    assertEquals(highResolution, design.highResolutionOptions.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolventsField.getValue());
    assertEquals(methanolSolvents, design.methanolSolventsField.getValue());
    assertEquals(chclSolvents, design.chclSolventsField.getValue());
    assertEquals(otherSolvents, design.otherSolventsField.getValue());
    assertEquals(otherSolvent, design.otherSolventField.getValue());
    assertEquals(comment, design.commentField.getValue());
  }
}
