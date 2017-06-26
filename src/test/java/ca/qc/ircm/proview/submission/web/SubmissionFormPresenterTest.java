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
import static ca.qc.ircm.proview.sample.SampleContainerType.SPOT;
import static ca.qc.ircm.proview.sample.SampleSupport.DRY;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.AVERAGE_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COMMENTS_PROPERTY;
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
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_GRID;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILE_FILENAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_BUTTON_STYLE;
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
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SEPARATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLUTION_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLVENTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOURCE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COMMENTS_PROPERTY;
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
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.WEIGHT_MARKER_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.io.ByteStreams;

import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.plate.PlateService;
import ca.qc.ircm.proview.plate.PlateSpot;
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
import com.vaadin.addon.spreadsheet.Spreadsheet;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.AbstractStringToNumberConverter;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Before;
import org.junit.Ignore;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private Random random = new Random();
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
  private String comments = "my comment\nmy comment second line";
  private String filesFilename1 = "protocol.txt";
  private String filesMimeType1 = "txt";
  private byte[] filesContent1 = new byte[1024];
  private String filesFilename2 = "samples.xlsx";
  private String filesMimeType2 = "xlsx";
  private byte[] filesContent2 = new byte[10240];
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
  public void beforeTest() {
    presenter =
        new SubmissionFormPresenter(submissionService, submissionSampleService, plateService);
    view.sampleTypeLabel = new Label();
    view.inactiveLabel = new Label();
    view.servicePanel = new Panel();
    view.serviceOptions = new RadioButtonGroup<>();
    view.samplesPanel = new Panel();
    view.sampleSupportOptions = new RadioButtonGroup<>();
    view.solutionSolventField = new TextField();
    view.sampleCountField = new TextField();
    view.sampleNameField = new TextField();
    view.formulaField = new TextField();
    view.structureLayout = new VerticalLayout();
    view.structureButton = new Button();
    view.structureUploader = structureUploader;
    view.monoisotopicMassField = new TextField();
    view.averageMassField = new TextField();
    view.toxicityField = new TextField();
    view.lightSensitiveField = new CheckBox();
    view.storageTemperatureOptions = new RadioButtonGroup<>();
    view.sampleContainerTypeOptions = new RadioButtonGroup<>();
    view.plateNameField = new TextField();
    view.samplesLabel = new Label();
    view.samplesGridLayout = new HorizontalLayout();
    view.samplesGrid = new Grid<>();
    view.fillSamplesButton = new Button();
    view.samplesPlateContainer = new VerticalLayout();
    view.samplesSpreadsheet = new Spreadsheet();
    view.experiencePanel = new Panel();
    view.experienceField = new TextField();
    view.experienceGoalField = new TextField();
    view.taxonomyField = new TextField();
    view.proteinNameField = new TextField();
    view.proteinWeightField = new TextField();
    view.postTranslationModificationField = new TextField();
    view.sampleQuantityField = new TextField();
    view.sampleVolumeField = new TextField();
    view.standardsPanel = new Panel();
    view.standardCountField = new TextField();
    view.standardsTableLayout = new HorizontalLayout();
    view.standardsGrid = new Grid<>();
    view.fillStandardsButton = new Button();
    view.contaminantsPanel = new Panel();
    view.contaminantCountField = new TextField();
    view.contaminantsTableLayout = new HorizontalLayout();
    view.contaminantsGrid = new Grid<>();
    view.fillContaminantsButton = new Button();
    view.gelPanel = new Panel();
    view.separationField = new ComboBox<>();
    view.thicknessField = new ComboBox<>();
    view.colorationField = new ComboBox<>();
    view.otherColorationField = new TextField();
    view.developmentTimeField = new TextField();
    view.decolorationField = new CheckBox();
    view.weightMarkerQuantityField = new TextField();
    view.proteinQuantityField = new TextField();
    view.gelImagesLayout = new HorizontalLayout();
    view.gelImagesUploader = gelImagesUploader;
    view.gelImagesGrid = new Grid<>();
    view.servicesPanel = new Panel();
    view.digestionOptions = new RadioButtonGroup<>();
    view.usedProteolyticDigestionMethodField = new TextField();
    view.otherProteolyticDigestionMethodField = new TextField();
    view.otherProteolyticDigestionMethodNote = new Label();
    view.enrichmentLabel = new Label();
    view.exclusionsLabel = new Label();
    view.injectionTypeOptions = new RadioButtonGroup<>();
    view.sourceOptions = new RadioButtonGroup<>();
    view.proteinContentOptions = new RadioButtonGroup<>();
    view.instrumentOptions = new RadioButtonGroup<>();
    view.proteinIdentificationOptions = new RadioButtonGroup<>();
    view.proteinIdentificationLinkField = new TextField();
    view.quantificationOptions = new RadioButtonGroup<>();
    view.quantificationLabelsField = new TextArea();
    view.highResolutionOptions = new RadioButtonGroup<>();
    view.solventsLayout = new VerticalLayout();
    view.acetonitrileSolventsField = new CheckBox();
    view.methanolSolventsField = new CheckBox();
    view.chclSolventsField = new CheckBox();
    view.otherSolventsField = new CheckBox();
    view.otherSolventField = new TextField();
    view.otherSolventNoteLabel = new Label();
    view.commentsPanel = new Panel();
    view.commentsField = new TextArea();
    view.filesPanel = new Panel();
    view.filesUploaderLayout = new VerticalLayout();
    view.filesUploader = filesUploader;
    view.filesGrid = new Grid<>();
    view.buttonsLayout = new HorizontalLayout();
    view.submitButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(plateService.nameAvailable(any())).thenReturn(true);
  }

  private void setFields() {
    view.solutionSolventField.setValue(solutionSolvent);
    view.sampleNameField.setValue(sampleName);
    view.formulaField.setValue(formula);
    view.monoisotopicMassField.setValue(String.valueOf(monoisotopicMass));
    view.averageMassField.setValue(String.valueOf(averageMass));
    view.toxicityField.setValue(toxicity);
    view.lightSensitiveField.setValue(lightSensitive);
    view.storageTemperatureOptions.setValue(storageTemperature);
    view.sampleContainerTypeOptions.setValue(sampleContainerType);
    view.plateNameField.setValue(plateName);
    view.sampleCountField.setValue(String.valueOf(sampleCount));
    setValuesInSamplesTable();
    plateSampleNameCell(0, 0).setCellValue(sampleName1);
    plateSampleNameCell(0, 1).setCellValue(sampleName2);
    view.experienceField.setValue(experience);
    view.experienceGoalField.setValue(experienceGoal);
    view.taxonomyField.setValue(taxonomy);
    view.proteinNameField.setValue(proteinName);
    view.proteinWeightField.setValue(String.valueOf(proteinWeight));
    view.postTranslationModificationField.setValue(postTranslationModification);
    view.sampleQuantityField.setValue(sampleQuantity);
    view.sampleVolumeField.setValue(String.valueOf(sampleVolume));
    view.standardCountField.setValue(String.valueOf(standardsCount));
    setValuesInStandardsTable();
    view.contaminantCountField.setValue(String.valueOf(contaminantsCount));
    setValuesInContaminantsTable();
    view.separationField.setValue(gelSeparation);
    view.thicknessField.setValue(gelThickness);
    view.colorationField.setValue(gelColoration);
    view.otherColorationField.setValue(otherColoration);
    view.developmentTimeField.setValue(developmentTime);
    view.decolorationField.setValue(decoloration);
    view.weightMarkerQuantityField.setValue(String.valueOf(weightMarkerQuantity));
    view.proteinQuantityField.setValue(proteinQuantity);
    view.digestionOptions.setValue(digestion);
    view.usedProteolyticDigestionMethodField.setValue(usedDigestion);
    view.otherProteolyticDigestionMethodField.setValue(otherDigestion);
    view.injectionTypeOptions.setValue(injectionType);
    view.sourceOptions.setValue(source);
    view.proteinContentOptions.setValue(proteinContent);
    view.instrumentOptions.setValue(instrument);
    view.proteinIdentificationOptions.setValue(proteinIdentification);
    view.proteinIdentificationLinkField.setValue(proteinIdentificationLink);
    view.quantificationOptions.setValue(quantification);
    view.quantificationLabelsField.setValue(quantificationLabels);
    view.highResolutionOptions.setValue(highResolution);
    view.acetonitrileSolventsField.setValue(acetonitrileSolvents);
    view.methanolSolventsField.setValue(methanolSolvents);
    view.chclSolventsField.setValue(chclSolvents);
    view.otherSolventsField.setValue(otherSolvents);
    view.otherSolventField.setValue(otherSolvent);
    view.commentsField.setValue(comments);
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(Grid<V> grid) {
    return (ListDataProvider<V>) grid.getDataProvider();
  }

  @SuppressWarnings("unchecked")
  private <V> ListDataProvider<V> dataProvider(RadioButtonGroup<V> radios) {
    return (ListDataProvider<V>) radios.getDataProvider();
  }

  private <R extends Number> R convert(AbstractStringToNumberConverter<R> converter,
      TextField component) throws Exception {
    return converter.convertToModel(component.getValue(), new ValueContext(component))
        .getOrThrow(throwableFunction);
  }

  private void setValuesInSamplesTable() {
    List<SubmissionSample> samples = new ArrayList<>(dataProvider(view.samplesGrid).getItems());
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
        (TextField) view.samplesGrid.getColumn(columnId).getValueProvider().apply(sample);
    field.setValue(value);
    return field;
  }

  private void setValuesInStandardsTable() {
    List<Standard> standards = new ArrayList<>(dataProvider(view.standardsGrid).getItems());
    Standard standard = standards.get(0);
    standardNameField1 = setValueInStandardsGrid(standard, standardName1, STANDARD_NAME_PROPERTY);
    standardQuantityField1 =
        setValueInStandardsGrid(standard, standardQuantity1, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsGrid(standard, standardComment1, STANDARD_COMMENTS_PROPERTY);
    standard = standards.get(1);
    standardNameField2 = setValueInStandardsGrid(standard, standardName2, STANDARD_NAME_PROPERTY);
    standardQuantityField2 =
        setValueInStandardsGrid(standard, standardQuantity2, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsGrid(standard, standardComment2, STANDARD_COMMENTS_PROPERTY);
  }

  private TextField setValueInStandardsGrid(Standard standard, String value, String columnId) {
    TextField field =
        (TextField) view.standardsGrid.getColumn(columnId).getValueProvider().apply(standard);
    field.setValue(value);
    return field;
  }

  private void setValuesInContaminantsTable() {
    List<Contaminant> contaminants =
        new ArrayList<>(dataProvider(view.contaminantsGrid).getItems());
    Contaminant contaminant = contaminants.get(0);
    contaminantNameField1 =
        setValueInContaminantsGrid(contaminant, contaminantName1, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField1 = setValueInContaminantsGrid(contaminant, contaminantQuantity1,
        CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsGrid(contaminant, contaminantComment1, CONTAMINANT_COMMENTS_PROPERTY);
    contaminant = contaminants.get(1);
    contaminantNameField2 =
        setValueInContaminantsGrid(contaminant, contaminantName2, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField2 = setValueInContaminantsGrid(contaminant, contaminantQuantity2,
        CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsGrid(contaminant, contaminantComment2, CONTAMINANT_COMMENTS_PROPERTY);
  }

  private TextField setValueInContaminantsGrid(Contaminant contaminant, String value,
      String columnId) {
    TextField field =
        (TextField) view.contaminantsGrid.getColumn(columnId).getValueProvider().apply(contaminant);
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

  private Cell plateSampleNameCell(int column, int row) {
    if (view.samplesSpreadsheet.getCell(row, column) == null) {
      view.samplesSpreadsheet.createCell(row, column, "");
    }
    return view.samplesSpreadsheet.getCell(row, column);
  }

  private String errorMessage(String message) {
    return new UserError(message).getFormattedHtmlMessage();
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
    submission.setComments(comments);
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
    standard.setComments(standardComment1);
    standards.add(standard);
    standard = new Standard();
    standard.setId(31L);
    standard.setName(standardName2);
    standard.setQuantity(standardQuantity2);
    standard.setComments(standardComment2);
    standards.add(standard);
    List<Contaminant> contaminants = new ArrayList<>();
    sample.setContaminants(contaminants);
    Contaminant contaminant = new Contaminant();
    contaminant.setId(30L);
    contaminant.setName(contaminantName1);
    contaminant.setQuantity(contaminantQuantity1);
    contaminant.setComments(contaminantComment1);
    contaminants.add(contaminant);
    contaminant = new Contaminant();
    contaminant.setId(31L);
    contaminant.setName(contaminantName2);
    contaminant.setQuantity(contaminantQuantity2);
    contaminant.setComments(contaminantComment2);
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
    standard.setComments(standardComment1);
    standards.add(standard);
    standard = new Standard();
    standard.setId(33L);
    standard.setName(standardName2);
    standard.setQuantity(standardQuantity2);
    standard.setComments(standardComment2);
    standards.add(standard);
    contaminants = new ArrayList<>();
    sample.setContaminants(contaminants);
    contaminant = new Contaminant();
    contaminant.setId(32L);
    contaminant.setName(contaminantName1);
    contaminant.setQuantity(contaminantQuantity1);
    contaminant.setComments(contaminantComment1);
    contaminants.add(contaminant);
    contaminant = new Contaminant();
    contaminant.setId(33L);
    contaminant.setName(contaminantName2);
    contaminant.setQuantity(contaminantQuantity2);
    contaminant.setComments(contaminantComment2);
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

    assertEquals(3, view.samplesGrid.getColumns().size());
    assertEquals(SAMPLE_NAME_PROPERTY, view.samplesGrid.getColumns().get(0).getId());
    assertFalse(view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).isHidden());
    assertEquals(SAMPLE_NUMBER_PROTEIN_PROPERTY, view.samplesGrid.getColumns().get(1).getId());
    assertTrue(view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).isHidden());
    assertEquals(PROTEIN_WEIGHT_PROPERTY, view.samplesGrid.getColumns().get(2).getId());
    assertTrue(view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void samplesTableColumns_IntactProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);

    assertEquals(3, view.samplesGrid.getColumns().size());
    assertEquals(SAMPLE_NAME_PROPERTY, view.samplesGrid.getColumns().get(0).getId());
    assertFalse(view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).isHidden());
    assertEquals(SAMPLE_NUMBER_PROTEIN_PROPERTY, view.samplesGrid.getColumns().get(1).getId());
    assertFalse(view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).isHidden());
    assertEquals(PROTEIN_WEIGHT_PROPERTY, view.samplesGrid.getColumns().get(2).getId());
    assertFalse(view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void standardsColumns() {
    presenter.init(view);

    assertEquals(3, view.standardsGrid.getColumns().size());
    assertEquals(STANDARD_NAME_PROPERTY, view.standardsGrid.getColumns().get(0).getId());
    assertEquals(STANDARD_QUANTITY_PROPERTY, view.standardsGrid.getColumns().get(1).getId());
    assertEquals(STANDARD_COMMENTS_PROPERTY, view.standardsGrid.getColumns().get(2).getId());
  }

  @Test
  public void contaminantsColumns() {
    presenter.init(view);

    assertEquals(3, view.contaminantsGrid.getColumns().size());
    assertEquals(CONTAMINANT_NAME_PROPERTY, view.contaminantsGrid.getColumns().get(0).getId());
    assertEquals(CONTAMINANT_QUANTITY_PROPERTY, view.contaminantsGrid.getColumns().get(1).getId());
    assertEquals(CONTAMINANT_COMMENTS_PROPERTY, view.contaminantsGrid.getColumns().get(2).getId());
  }

  @Test
  public void gelImagesColumns() {
    presenter.init(view);

    assertEquals(2, view.gelImagesGrid.getColumns().size());
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, view.gelImagesGrid.getColumns().get(0).getId());
    assertFalse(view.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_GEL_IMAGE, view.gelImagesGrid.getColumns().get(1).getId());
    assertTrue(view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
  }

  @Test
  public void gelImagesColumns_Editable() {
    presenter.init(view);
    presenter.setEditable(true);

    assertEquals(2, view.gelImagesGrid.getColumns().size());
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, view.gelImagesGrid.getColumns().get(0).getId());
    assertFalse(view.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_GEL_IMAGE, view.gelImagesGrid.getColumns().get(1).getId());
    assertFalse(view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
  }

  @Test
  public void filesColumns() {
    presenter.init(view);

    assertEquals(2, view.filesGrid.getColumns().size());
    assertEquals(FILE_FILENAME_PROPERTY, view.filesGrid.getColumns().get(0).getId());
    assertFalse(view.filesGrid.getColumn(FILE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_FILE, view.filesGrid.getColumns().get(1).getId());
    assertTrue(view.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void filesColumns_Editable() {
    presenter.init(view);
    presenter.setEditable(true);

    assertEquals(2, view.filesGrid.getColumns().size());
    assertEquals(FILE_FILENAME_PROPERTY, view.filesGrid.getColumns().get(0).getId());
    assertFalse(view.filesGrid.getColumn(FILE_FILENAME_PROPERTY).isHidden());
    assertEquals(REMOVE_FILE, view.filesGrid.getColumns().get(1).getId());
    assertFalse(view.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void requiredFields() {
    presenter.init(view);

    assertTrue(view.serviceOptions.isRequiredIndicatorVisible());
    assertTrue(view.sampleSupportOptions.isRequiredIndicatorVisible());
    assertTrue(view.solutionSolventField.isRequiredIndicatorVisible());
    assertTrue(view.sampleCountField.isRequiredIndicatorVisible());
    assertTrue(view.sampleNameField.isRequiredIndicatorVisible());
    assertTrue(view.formulaField.isRequiredIndicatorVisible());
    assertTrue(view.monoisotopicMassField.isRequiredIndicatorVisible());
    assertFalse(view.averageMassField.isRequiredIndicatorVisible());
    assertFalse(view.toxicityField.isRequiredIndicatorVisible());
    assertFalse(view.lightSensitiveField.isRequiredIndicatorVisible());
    assertTrue(view.storageTemperatureOptions.isRequiredIndicatorVisible());
    assertTrue(view.sampleContainerTypeOptions.isRequiredIndicatorVisible());
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(view.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    assertTrue(view.plateNameField.isRequiredIndicatorVisible());
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleNameTableField = (TextField) view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY)
        .getValueProvider().apply(firstSample);
    assertTrue(sampleNameTableField.isRequiredIndicatorVisible());
    TextField sampleNumberProteinTableField = (TextField) view.samplesGrid
        .getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).getValueProvider().apply(firstSample);
    assertTrue(sampleNumberProteinTableField.isRequiredIndicatorVisible());
    TextField sampleProteinWeightTableField = (TextField) view.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(view.experienceField.isRequiredIndicatorVisible());
    assertFalse(view.experienceGoalField.isRequiredIndicatorVisible());
    assertTrue(view.taxonomyField.isRequiredIndicatorVisible());
    assertFalse(view.proteinNameField.isRequiredIndicatorVisible());
    assertFalse(view.proteinWeightField.isRequiredIndicatorVisible());
    assertFalse(view.postTranslationModificationField.isRequiredIndicatorVisible());
    assertTrue(view.sampleQuantityField.isRequiredIndicatorVisible());
    assertTrue(view.sampleVolumeField.isRequiredIndicatorVisible());
    assertFalse(view.standardCountField.isRequiredIndicatorVisible());
    ListDataProvider<Standard> standardsDataProvider = dataProvider(view.standardsGrid);
    if (standardsDataProvider.getItems().size() < 1) {
      standardsDataProvider.getItems().add(new Standard());
    }
    Standard firstStandard = standardsDataProvider.getItems().iterator().next();
    TextField standardNameTableField = (TextField) view.standardsGrid
        .getColumn(STANDARD_NAME_PROPERTY).getValueProvider().apply(firstStandard);
    assertTrue(standardNameTableField.isRequiredIndicatorVisible());
    TextField standardQuantityTableField = (TextField) view.standardsGrid
        .getColumn(STANDARD_QUANTITY_PROPERTY).getValueProvider().apply(firstStandard);
    assertTrue(standardQuantityTableField.isRequiredIndicatorVisible());
    TextField standardCommentsTableField = (TextField) view.standardsGrid
        .getColumn(STANDARD_COMMENTS_PROPERTY).getValueProvider().apply(firstStandard);
    assertFalse(standardCommentsTableField.isRequiredIndicatorVisible());
    assertFalse(view.contaminantCountField.isRequiredIndicatorVisible());
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(view.contaminantsGrid);
    if (contaminantsDataProvider.getItems().size() < 1) {
      contaminantsDataProvider.getItems().add(new Contaminant());
    }
    Contaminant firstContaminant = contaminantsDataProvider.getItems().iterator().next();
    TextField contaminantNameTableField = (TextField) view.contaminantsGrid
        .getColumn(CONTAMINANT_NAME_PROPERTY).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantNameTableField.isRequiredIndicatorVisible());
    TextField contaminantQuantityTableField = (TextField) view.contaminantsGrid
        .getColumn(CONTAMINANT_QUANTITY_PROPERTY).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantQuantityTableField.isRequiredIndicatorVisible());
    TextField contaminantCommentsTableField = (TextField) view.contaminantsGrid
        .getColumn(CONTAMINANT_COMMENTS_PROPERTY).getValueProvider().apply(firstContaminant);
    assertFalse(contaminantCommentsTableField.isRequiredIndicatorVisible());
    assertTrue(view.separationField.isRequiredIndicatorVisible());
    assertTrue(view.thicknessField.isRequiredIndicatorVisible());
    assertFalse(view.colorationField.isRequiredIndicatorVisible());
    assertTrue(view.otherColorationField.isRequiredIndicatorVisible());
    assertFalse(view.developmentTimeField.isRequiredIndicatorVisible());
    assertFalse(view.decolorationField.isRequiredIndicatorVisible());
    assertFalse(view.weightMarkerQuantityField.isRequiredIndicatorVisible());
    assertFalse(view.proteinQuantityField.isRequiredIndicatorVisible());
    assertTrue(view.digestionOptions.isRequiredIndicatorVisible());
    assertTrue(view.usedProteolyticDigestionMethodField.isRequiredIndicatorVisible());
    assertTrue(view.otherProteolyticDigestionMethodField.isRequiredIndicatorVisible());
    assertTrue(view.injectionTypeOptions.isRequiredIndicatorVisible());
    assertTrue(view.sourceOptions.isRequiredIndicatorVisible());
    assertTrue(view.proteinContentOptions.isRequiredIndicatorVisible());
    assertFalse(view.instrumentOptions.isRequiredIndicatorVisible());
    assertTrue(view.proteinIdentificationOptions.isRequiredIndicatorVisible());
    assertTrue(view.proteinIdentificationLinkField.isRequiredIndicatorVisible());
    assertFalse(view.quantificationOptions.isRequiredIndicatorVisible());
    assertFalse(view.quantificationLabelsField.isRequiredIndicatorVisible());
    assertTrue(view.highResolutionOptions.isRequiredIndicatorVisible());
    assertFalse(view.acetonitrileSolventsField.isRequiredIndicatorVisible());
    assertFalse(view.methanolSolventsField.isRequiredIndicatorVisible());
    assertFalse(view.chclSolventsField.isRequiredIndicatorVisible());
    assertFalse(view.otherSolventsField.isRequiredIndicatorVisible());
    assertTrue(view.otherSolventField.isRequiredIndicatorVisible());
  }

  @Test
  public void required_ProteinWeight_Lcmsms() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(view.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField = (TextField) view.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    view.serviceOptions.setValue(LC_MS_MS); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void required_ProteinWeight_IntactProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(view.samplesGrid);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField = (TextField) view.samplesGrid
        .getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider().apply(firstSample);
    view.serviceOptions.setValue(INTACT_PROTEIN); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertFalse(view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).isHidden());
  }

  @Test
  public void service_Options() {
    presenter.init(view);

    assertEquals(Service.availables().size(), dataProvider(view.serviceOptions).getItems().size());
    for (Service service : Service.availables()) {
      assertTrue(service.name(), dataProvider(view.serviceOptions).getItems().contains(service));
    }
  }

  @Test
  public void service_DisabledOption() {
    Service service = Service.MALDI_MS;
    Submission submission = new Submission();
    submission.setService(service);

    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(dataProvider(view.serviceOptions).getItems().contains(service));
    assertFalse(view.serviceOptions.getItemEnabledProvider().test(service));
  }

  @Test
  public void gelSupportDisabled_Smallmolecule() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);

    assertFalse(view.sampleSupportOptions.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void gelSupportDisabled_Intactprotein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);

    assertFalse(view.sampleSupportOptions.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void digestion_RequiredText() {
    presenter.init(view);
    presenter.setEditable(true);

    view.digestionOptions.setValue(TRYPSIN);
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    view.digestionOptions.setValue(DIGESTED);
    assertTrue(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    view.digestionOptions.setValue(ProteolyticDigestion.OTHER);
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertTrue(view.otherProteolyticDigestionMethodField.isVisible());
    assertTrue(view.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void source_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrumentSource.availables().size(),
        dataProvider(view.sourceOptions).getItems().size());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertTrue(source.name(), dataProvider(view.sourceOptions).getItems().contains(source));
    }
  }

  @Test
  public void source_DisabledOption() {
    source = MassDetectionInstrumentSource.LDTD;
    Submission submission = new Submission();
    submission.setSource(source);

    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(dataProvider(view.sourceOptions).getItems().contains(source));
    assertFalse(view.sourceOptions.getItemEnabledProvider().test(source));
  }

  @Test
  public void instrument_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrument.availables().size() + 1,
        dataProvider(view.instrumentOptions).getItems().size());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.availables()) {
      assertTrue(instrument.name(),
          dataProvider(view.instrumentOptions).getItems().contains(instrument));
    }
  }

  @Test
  public void instrument_DisabledOption() {
    instrument = MassDetectionInstrument.TOF;
    Submission submission = new Submission();
    submission.setMassDetectionInstrument(instrument);

    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(dataProvider(view.instrumentOptions).getItems().contains(instrument));
    assertFalse(view.instrumentOptions.getItemEnabledProvider().test(instrument));
  }

  @Test
  public void proteinIdentification_Options() {
    presenter.init(view);

    assertEquals(ProteinIdentification.availables().size(),
        dataProvider(view.proteinIdentificationOptions).getItems().size());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertTrue(proteinIdentification.name(), dataProvider(view.proteinIdentificationOptions)
          .getItems().contains(proteinIdentification));
    }
  }

  @Test
  public void proteinIdentification_DisabledOption() {
    proteinIdentification = ProteinIdentification.NCBINR;
    Submission submission = new Submission();
    submission.setProteinIdentification(proteinIdentification);

    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(
        dataProvider(view.proteinIdentificationOptions).getItems().contains(proteinIdentification));
    assertFalse(
        view.proteinIdentificationOptions.getItemEnabledProvider().test(proteinIdentification));
  }

  @Test
  public void proteinIdentification_RequiredText() {
    presenter.init(view);
    presenter.setEditable(true);

    view.proteinIdentificationOptions.setValue(REFSEQ);
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    view.proteinIdentificationOptions.setValue(UNIPROT);
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    view.proteinIdentificationOptions.setValue(ProteinIdentification.OTHER);
    assertTrue(view.proteinIdentificationLinkField.isVisible());
  }

  @Test
  public void quantification_RequiredText() {
    presenter.init(view);
    presenter.setEditable(true);

    view.quantificationOptions.setValue(null);
    assertFalse(view.quantificationLabelsField.isRequiredIndicatorVisible());
    view.quantificationOptions.setValue(Quantification.LABEL_FREE);
    assertFalse(view.quantificationLabelsField.isRequiredIndicatorVisible());
    view.quantificationOptions.setValue(Quantification.SILAC);
    assertTrue(view.quantificationLabelsField.isRequiredIndicatorVisible());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.sampleTypeLabel.getStyleName().contains(SAMPLE_TYPE_LABEL));
    assertTrue(view.inactiveLabel.getStyleName().contains(INACTIVE_LABEL));
    assertTrue(view.servicePanel.getStyleName().contains(SERVICE_PANEL));
    assertTrue(view.servicePanel.getStyleName().contains(REQUIRED));
    assertTrue(view.serviceOptions.getStyleName().contains(SERVICE_PROPERTY));
    assertTrue(view.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(view.sampleSupportOptions.getStyleName().contains(SAMPLE_SUPPORT_PROPERTY));
    assertTrue(view.solutionSolventField.getStyleName().contains(SOLUTION_SOLVENT_PROPERTY));
    assertTrue(view.sampleCountField.getStyleName().contains(SAMPLE_COUNT_PROPERTY));
    assertTrue(view.sampleNameField.getStyleName().contains(SAMPLE_NAME_PROPERTY));
    assertTrue(view.formulaField.getStyleName().contains(FORMULA_PROPERTY));
    assertTrue(view.structureLayout.getStyleName().contains(REQUIRED));
    assertTrue(view.structureButton.getStyleName().contains(STRUCTURE_PROPERTY));
    verify(view.structureUploader).addStyleName(STRUCTURE_UPLOADER);
    assertTrue(view.monoisotopicMassField.getStyleName().contains(MONOISOTOPIC_MASS_PROPERTY));
    assertTrue(view.averageMassField.getStyleName().contains(AVERAGE_MASS_PROPERTY));
    assertTrue(view.toxicityField.getStyleName().contains(TOXICITY_PROPERTY));
    assertTrue(view.lightSensitiveField.getStyleName().contains(LIGHT_SENSITIVE_PROPERTY));
    assertTrue(
        view.storageTemperatureOptions.getStyleName().contains(STORAGE_TEMPERATURE_PROPERTY));
    assertTrue(
        view.sampleContainerTypeOptions.getStyleName().contains(SAMPLES_CONTAINER_TYPE_PROPERTY));
    assertTrue(
        view.plateNameField.getStyleName().contains(PLATE_PROPERTY + "-" + PLATE_NAME_PROPERTY));
    assertTrue(view.samplesLabel.getStyleName().contains(SAMPLES_PROPERTY));
    assertTrue(view.samplesGrid.getStyleName().contains(SAMPLES_TABLE));
    assertTrue(view.fillSamplesButton.getStyleName().contains(FILL_SAMPLES_PROPERTY));
    assertTrue(view.fillSamplesButton.getStyleName().contains(FILL_BUTTON_STYLE));
    assertTrue(view.samplesSpreadsheet.getStyleName().contains(SAMPLES_PLATE));
    assertTrue(view.experiencePanel.getStyleName().contains(EXPERIENCE_PANEL));
    assertTrue(view.experienceField.getStyleName().contains(EXPERIENCE_PROPERTY));
    assertTrue(view.experienceGoalField.getStyleName().contains(EXPERIENCE_GOAL_PROPERTY));
    assertTrue(view.taxonomyField.getStyleName().contains(TAXONOMY_PROPERTY));
    assertTrue(view.proteinNameField.getStyleName().contains(PROTEIN_NAME_PROPERTY));
    assertTrue(view.proteinWeightField.getStyleName().contains(PROTEIN_WEIGHT_PROPERTY));
    assertTrue(view.postTranslationModificationField.getStyleName()
        .contains(POST_TRANSLATION_MODIFICATION_PROPERTY));
    assertTrue(view.sampleQuantityField.getStyleName().contains(SAMPLE_QUANTITY_PROPERTY));
    assertTrue(view.sampleVolumeField.getStyleName().contains(SAMPLE_VOLUME_PROPERTY));
    assertTrue(view.standardsPanel.getStyleName().contains(STANDARDS_PANEL));
    assertTrue(view.standardCountField.getStyleName().contains(STANDARD_COUNT_PROPERTY));
    assertTrue(view.standardsGrid.getStyleName().contains(STANDARD_PROPERTY));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_STANDARDS_PROPERTY));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_BUTTON_STYLE));
    assertTrue(view.contaminantsPanel.getStyleName().contains(CONTAMINANTS_PANEL));
    assertTrue(view.contaminantCountField.getStyleName().contains(CONTAMINANT_COUNT_PROPERTY));
    assertTrue(view.contaminantsGrid.getStyleName().contains(CONTAMINANT_PROPERTY));
    assertTrue(view.fillContaminantsButton.getStyleName().contains(FILL_CONTAMINANTS_PROPERTY));
    assertTrue(view.fillContaminantsButton.getStyleName().contains(FILL_BUTTON_STYLE));
    assertTrue(view.gelPanel.getStyleName().contains(GEL_PANEL));
    assertTrue(view.separationField.getStyleName().contains(SEPARATION_PROPERTY));
    assertTrue(view.thicknessField.getStyleName().contains(THICKNESS_PROPERTY));
    assertTrue(view.colorationField.getStyleName().contains(COLORATION_PROPERTY));
    assertTrue(view.otherColorationField.getStyleName().contains(OTHER_COLORATION_PROPERTY));
    assertTrue(view.developmentTimeField.getStyleName().contains(DEVELOPMENT_TIME_PROPERTY));
    assertTrue(view.decolorationField.getStyleName().contains(DECOLORATION_PROPERTY));
    assertTrue(
        view.weightMarkerQuantityField.getStyleName().contains(WEIGHT_MARKER_QUANTITY_PROPERTY));
    assertTrue(view.proteinQuantityField.getStyleName().contains(PROTEIN_QUANTITY_PROPERTY));
    assertTrue(view.gelImagesLayout.getStyleName().contains(REQUIRED));
    verify(view.gelImagesUploader).addStyleName(GEL_IMAGES_PROPERTY);
    assertTrue(view.gelImagesGrid.getStyleName().contains(GEL_IMAGES_TABLE));
    assertTrue(view.servicesPanel.getStyleName().contains(SERVICES_PANEL));
    assertTrue(view.digestionOptions.getStyleName().contains(DIGESTION_PROPERTY));
    assertTrue(
        view.usedProteolyticDigestionMethodField.getStyleName().contains(USED_DIGESTION_PROPERTY));
    assertTrue(view.otherProteolyticDigestionMethodField.getStyleName()
        .contains(OTHER_DIGESTION_PROPERTY));
    assertTrue(view.enrichmentLabel.getStyleName().contains(ENRICHEMENT_PROPERTY));
    assertTrue(view.exclusionsLabel.getStyleName().contains(EXCLUSIONS_PROPERTY));
    assertTrue(view.injectionTypeOptions.getStyleName().contains(INJECTION_TYPE_PROPERTY));
    assertTrue(view.sourceOptions.getStyleName().contains(SOURCE_PROPERTY));
    assertTrue(view.proteinContentOptions.getStyleName().contains(PROTEIN_CONTENT_PROPERTY));
    assertTrue(view.instrumentOptions.getStyleName().contains(INSTRUMENT_PROPERTY));
    assertTrue(
        view.proteinIdentificationOptions.getStyleName().contains(PROTEIN_IDENTIFICATION_PROPERTY));
    assertTrue(view.proteinIdentificationLinkField.getStyleName()
        .contains(PROTEIN_IDENTIFICATION_LINK_PROPERTY));
    assertTrue(view.quantificationOptions.getStyleName().contains(QUANTIFICATION_PROPERTY));
    assertTrue(
        view.quantificationLabelsField.getStyleName().contains(QUANTIFICATION_LABELS_PROPERTY));
    assertTrue(view.highResolutionOptions.getStyleName().contains(HIGH_RESOLUTION_PROPERTY));
    assertTrue(view.solventsLayout.getStyleName().contains(REQUIRED));
    assertTrue(view.acetonitrileSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.ACETONITRILE.name()));
    assertTrue(view.methanolSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.METHANOL.name()));
    assertTrue(view.chclSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.CHCL3.name()));
    assertTrue(view.otherSolventsField.getStyleName()
        .contains(SOLVENTS_PROPERTY + "-" + Solvent.OTHER.name()));
    assertTrue(view.otherSolventField.getStyleName().contains(OTHER_SOLVENT_PROPERTY));
    assertTrue(view.otherSolventField.getStyleName().contains(ValoTheme.TEXTFIELD_SMALL));
    assertTrue(view.otherSolventNoteLabel.getStyleName().contains(OTHER_SOLVENT_NOTE));
    assertTrue(view.commentsPanel.getStyleName().contains(COMMENTS_PANEL));
    assertTrue(view.commentsField.getStyleName().contains(COMMENTS_PROPERTY));
    assertTrue(view.filesPanel.getStyleName().contains(FILES_PROPERTY));
    verify(view.filesUploader).addStyleName(FILES_UPLOADER);
    assertTrue(view.filesGrid.getStyleName().contains(FILES_GRID));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLE_TYPE_LABEL), view.sampleTypeLabel.getValue());
    assertEquals(resources.message(INACTIVE_LABEL), view.inactiveLabel.getValue());
    assertEquals(resources.message(SERVICE_PROPERTY), view.servicePanel.getCaption());
    assertEquals(null, view.serviceOptions.getCaption());
    for (Service service : Service.availables()) {
      assertEquals(service.getLabel(locale),
          view.serviceOptions.getItemCaptionGenerator().apply(service));
    }
    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    assertEquals(resources.message(SAMPLE_SUPPORT_PROPERTY),
        view.sampleSupportOptions.getCaption());
    for (SampleSupport support : SampleSupport.values()) {
      assertEquals(support.getLabel(locale),
          view.sampleSupportOptions.getItemCaptionGenerator().apply(support));
    }
    assertEquals(resources.message(SOLUTION_SOLVENT_PROPERTY),
        view.solutionSolventField.getCaption());
    assertEquals(resources.message(SAMPLE_COUNT_PROPERTY), view.sampleCountField.getCaption());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY), view.sampleNameField.getCaption());
    assertEquals(resources.message(FORMULA_PROPERTY), view.formulaField.getCaption());
    assertEquals(resources.message(STRUCTURE_PROPERTY), view.structureLayout.getCaption());
    assertEquals("", view.structureButton.getCaption());
    verify(view.structureUploader).setButtonCaption(resources.message(STRUCTURE_UPLOADER));
    verify(view.structureUploader).setImmediateMode(true);
    assertEquals(resources.message(MONOISOTOPIC_MASS_PROPERTY),
        view.monoisotopicMassField.getCaption());
    assertEquals(resources.message(AVERAGE_MASS_PROPERTY), view.averageMassField.getCaption());
    assertEquals(resources.message(TOXICITY_PROPERTY), view.toxicityField.getCaption());
    assertEquals(resources.message(LIGHT_SENSITIVE_PROPERTY),
        view.lightSensitiveField.getCaption());
    assertEquals(resources.message(STORAGE_TEMPERATURE_PROPERTY),
        view.storageTemperatureOptions.getCaption());
    for (StorageTemperature storageTemperature : StorageTemperature.values()) {
      assertEquals(storageTemperature.getLabel(locale),
          view.storageTemperatureOptions.getItemCaptionGenerator().apply(storageTemperature));
    }
    assertEquals(resources.message(SAMPLES_CONTAINER_TYPE_PROPERTY),
        view.sampleContainerTypeOptions.getCaption());
    for (SampleContainerType containerType : SampleContainerType.values()) {
      assertEquals(containerType.getLabel(locale),
          view.sampleContainerTypeOptions.getItemCaptionGenerator().apply(containerType));
    }
    assertEquals(resources.message(PLATE_PROPERTY + "." + PLATE_NAME_PROPERTY),
        view.plateNameField.getCaption());
    assertEquals(resources.message(SAMPLES_PROPERTY), view.samplesLabel.getCaption());
    assertEquals(null, view.samplesGrid.getCaption());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY),
        view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY),
        view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY).getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT_PROPERTY),
        view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_SAMPLES_PROPERTY), view.fillSamplesButton.getCaption());
    assertEquals(null, view.samplesSpreadsheet.getCaption());
    for (int column = 0; column < view.samplesSpreadsheet.getColumns(); column++) {
      for (int row = 0; row < view.samplesSpreadsheet.getRows(); row++) {
        assertEquals("", plateSampleNameCell(row, column).getStringCellValue());
      }
    }
    assertEquals(resources.message(EXPERIENCE_PANEL), view.experiencePanel.getCaption());
    assertEquals(resources.message(EXPERIENCE_PROPERTY), view.experienceField.getCaption());
    assertEquals(resources.message(EXPERIENCE_GOAL_PROPERTY),
        view.experienceGoalField.getCaption());
    assertEquals(resources.message(TAXONOMY_PROPERTY), view.taxonomyField.getCaption());
    assertEquals(resources.message(PROTEIN_NAME_PROPERTY), view.proteinNameField.getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT_PROPERTY), view.proteinWeightField.getCaption());
    assertEquals(resources.message(POST_TRANSLATION_MODIFICATION_PROPERTY),
        view.postTranslationModificationField.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY_PROPERTY),
        view.sampleQuantityField.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY_PROPERTY + "." + EXAMPLE),
        view.sampleQuantityField.getPlaceholder());
    assertEquals(resources.message(SAMPLE_VOLUME_PROPERTY), view.sampleVolumeField.getCaption());
    assertEquals(resources.message(STANDARDS_PANEL), view.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT_PROPERTY), view.standardCountField.getCaption());
    assertEquals(null, view.standardsGrid.getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_NAME_PROPERTY),
        view.standardsGrid.getColumn(STANDARD_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY),
        view.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY).getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_COMMENTS_PROPERTY),
        view.standardsGrid.getColumn(STANDARD_COMMENTS_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_STANDARDS_PROPERTY), view.fillStandardsButton.getCaption());
    assertEquals(resources.message(CONTAMINANTS_PANEL), view.contaminantsPanel.getCaption());
    assertEquals(resources.message(CONTAMINANT_COUNT_PROPERTY),
        view.contaminantCountField.getCaption());
    assertEquals(null, view.contaminantsGrid.getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_NAME_PROPERTY),
        view.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY).getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY),
        view.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY).getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_COMMENTS_PROPERTY),
        view.contaminantsGrid.getColumn(CONTAMINANT_COMMENTS_PROPERTY).getCaption());
    assertEquals(resources.message(FILL_CONTAMINANTS_PROPERTY),
        view.fillContaminantsButton.getCaption());
    assertEquals(resources.message(GEL_PANEL), view.gelPanel.getCaption());
    assertEquals(resources.message(SEPARATION_PROPERTY), view.separationField.getCaption());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          view.separationField.getItemCaptionGenerator().apply(separation));
    }
    assertEquals(resources.message(THICKNESS_PROPERTY), view.thicknessField.getCaption());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          view.thicknessField.getItemCaptionGenerator().apply(thickness));
    }
    assertEquals(resources.message(COLORATION_PROPERTY), view.colorationField.getCaption());
    assertEquals(GelColoration.getNullLabel(locale),
        view.colorationField.getEmptySelectionCaption());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          view.colorationField.getItemCaptionGenerator().apply(coloration));
    }
    assertEquals(resources.message(OTHER_COLORATION_PROPERTY),
        view.otherColorationField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY),
        view.developmentTimeField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE),
        view.developmentTimeField.getPlaceholder());
    assertEquals(resources.message(DECOLORATION_PROPERTY), view.decolorationField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY),
        view.weightMarkerQuantityField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE),
        view.weightMarkerQuantityField.getPlaceholder());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY),
        view.proteinQuantityField.getCaption());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE),
        view.proteinQuantityField.getPlaceholder());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY), view.gelImagesLayout.getCaption());
    verify(view.gelImagesUploader).setUploadButtonCaption(resources.message(GEL_IMAGES_UPLOADER));
    assertEquals(null, view.gelImagesGrid.getCaption());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + GEL_IMAGE_FILENAME_PROPERTY),
        view.gelImagesGrid.getColumn(GEL_IMAGE_FILENAME_PROPERTY).getCaption());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE),
        view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).getCaption());
    assertEquals(resources.message(SERVICES_PANEL), view.servicesPanel.getCaption());
    assertEquals(resources.message(DIGESTION_PROPERTY), view.digestionOptions.getCaption());
    for (ProteolyticDigestion digestion : ProteolyticDigestion.values()) {
      assertEquals(digestion.getLabel(locale),
          view.digestionOptions.getItemCaptionGenerator().apply(digestion));
    }
    assertEquals(resources.message(USED_DIGESTION_PROPERTY),
        view.usedProteolyticDigestionMethodField.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION_PROPERTY),
        view.otherProteolyticDigestionMethodField.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION_PROPERTY + ".note"),
        view.otherProteolyticDigestionMethodNote.getValue());
    assertEquals(resources.message(ENRICHEMENT_PROPERTY), view.enrichmentLabel.getCaption());
    assertEquals(resources.message(ENRICHEMENT_PROPERTY + ".value"),
        view.enrichmentLabel.getValue());
    assertEquals(resources.message(EXCLUSIONS_PROPERTY), view.exclusionsLabel.getCaption());
    assertEquals(resources.message(EXCLUSIONS_PROPERTY + ".value"),
        view.exclusionsLabel.getValue());
    assertEquals(resources.message(INJECTION_TYPE_PROPERTY),
        view.injectionTypeOptions.getCaption());
    for (InjectionType injectionType : InjectionType.values()) {
      assertEquals(injectionType.getLabel(locale),
          view.injectionTypeOptions.getItemCaptionGenerator().apply(injectionType));
    }
    assertEquals(resources.message(SOURCE_PROPERTY), view.sourceOptions.getCaption());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.values()) {
      assertEquals(source.getLabel(locale),
          view.sourceOptions.getItemCaptionGenerator().apply(source));
    }
    assertEquals(resources.message(PROTEIN_CONTENT_PROPERTY),
        view.proteinContentOptions.getCaption());
    for (ProteinContent proteinContent : ProteinContent.values()) {
      assertEquals(proteinContent.getLabel(locale),
          view.proteinContentOptions.getItemCaptionGenerator().apply(proteinContent));
    }
    assertEquals(resources.message(INSTRUMENT_PROPERTY), view.instrumentOptions.getCaption());
    assertEquals(MassDetectionInstrument.getNullLabel(locale),
        view.instrumentOptions.getItemCaptionGenerator().apply(null));
    for (MassDetectionInstrument instrument : MassDetectionInstrument.availables()) {
      assertEquals(instrument.getLabel(locale),
          view.instrumentOptions.getItemCaptionGenerator().apply(instrument));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_PROPERTY),
        view.proteinIdentificationOptions.getCaption());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertEquals(proteinIdentification.getLabel(locale),
          view.proteinIdentificationOptions.getItemCaptionGenerator().apply(proteinIdentification));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY),
        view.proteinIdentificationLinkField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_PROPERTY),
        view.quantificationOptions.getCaption());
    assertEquals(Quantification.getNullLabel(locale),
        view.quantificationOptions.getItemCaptionGenerator().apply(null));
    for (Quantification quantification : Quantification.values()) {
      assertEquals(quantification.getLabel(locale),
          view.quantificationOptions.getItemCaptionGenerator().apply(quantification));
    }
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY),
        view.quantificationLabelsField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE),
        view.quantificationLabelsField.getPlaceholder());
    assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY),
        view.highResolutionOptions.getCaption());
    for (boolean value : new boolean[] { false, true }) {
      assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY + "." + value),
          view.highResolutionOptions.getItemCaptionGenerator().apply(value));
    }
    assertEquals(resources.message(SOLVENTS_PROPERTY), view.solventsLayout.getCaption());
    assertEquals(Solvent.ACETONITRILE.getLabel(locale),
        view.acetonitrileSolventsField.getCaption());
    assertEquals(Solvent.METHANOL.getLabel(locale), view.methanolSolventsField.getCaption());
    assertEquals(Solvent.CHCL3.getLabel(locale), view.chclSolventsField.getCaption());
    assertEquals(Solvent.OTHER.getLabel(locale), view.otherSolventsField.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT_PROPERTY), view.otherSolventField.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT_NOTE), view.otherSolventNoteLabel.getValue());
    assertEquals(resources.message(COMMENTS_PANEL), view.commentsPanel.getCaption());
    assertEquals(null, view.commentsField.getCaption());
    assertEquals(resources.message(FILES_PROPERTY), view.filesPanel.getCaption());
    verify(view.filesUploader).setUploadButtonCaption(resources.message(FILES_UPLOADER));
    assertEquals(null, view.filesGrid.getCaption());
    assertEquals(resources.message(FILES_PROPERTY + "." + FILE_FILENAME_PROPERTY),
        view.filesGrid.getColumn(FILE_FILENAME_PROPERTY).getCaption());
    assertEquals(resources.message(FILES_PROPERTY + "." + REMOVE_FILE),
        view.filesGrid.getColumn(REMOVE_FILE).getCaption());
  }

  @Test
  public void editable_False() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(view.serviceOptions.isReadOnly());
    assertTrue(view.sampleSupportOptions.isReadOnly());
    assertTrue(view.solutionSolventField.isReadOnly());
    assertTrue(view.sampleCountField.isReadOnly());
    assertTrue(view.sampleNameField.isReadOnly());
    assertTrue(view.formulaField.isReadOnly());
    assertTrue(view.monoisotopicMassField.isReadOnly());
    assertTrue(view.averageMassField.isReadOnly());
    assertTrue(view.toxicityField.isReadOnly());
    assertTrue(view.lightSensitiveField.isReadOnly());
    assertTrue(view.storageTemperatureOptions.isReadOnly());
    assertTrue(view.sampleContainerTypeOptions.isReadOnly());
    assertTrue(view.plateNameField.isReadOnly());
    SubmissionSample firstSample = dataProvider(view.samplesGrid).getItems().iterator().next();
    assertTrue(((TextField) view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertTrue(((TextField) view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .getValueProvider().apply(firstSample)).isReadOnly());
    assertTrue(((TextField) view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertTrue(view.experienceField.isReadOnly());
    assertTrue(view.experienceGoalField.isReadOnly());
    assertTrue(view.taxonomyField.isReadOnly());
    assertTrue(view.proteinNameField.isReadOnly());
    assertTrue(view.proteinWeightField.isReadOnly());
    assertTrue(view.postTranslationModificationField.isReadOnly());
    assertTrue(view.sampleQuantityField.isReadOnly());
    assertTrue(view.sampleVolumeField.isReadOnly());
    assertTrue(view.standardCountField.isReadOnly());
    Standard firstStandard = dataProvider(view.standardsGrid).getItems().iterator().next();
    assertTrue(((TextField) view.standardsGrid.getColumn(STANDARD_NAME_PROPERTY).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertTrue(view.contaminantCountField.isReadOnly());
    Contaminant firstContaminant = dataProvider(view.contaminantsGrid).getItems().iterator().next();
    assertTrue(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_COMMENTS_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertTrue(view.separationField.isReadOnly());
    assertTrue(view.thicknessField.isReadOnly());
    assertTrue(view.colorationField.isReadOnly());
    assertTrue(view.otherColorationField.isReadOnly());
    assertTrue(view.developmentTimeField.isReadOnly());
    assertTrue(view.decolorationField.isReadOnly());
    assertTrue(view.weightMarkerQuantityField.isReadOnly());
    assertTrue(view.proteinQuantityField.isReadOnly());
    assertTrue(view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
    assertTrue(view.digestionOptions.isReadOnly());
    assertTrue(view.usedProteolyticDigestionMethodField.isReadOnly());
    assertTrue(view.otherProteolyticDigestionMethodField.isReadOnly());
    assertTrue(view.injectionTypeOptions.isReadOnly());
    assertTrue(view.sourceOptions.isReadOnly());
    assertTrue(view.proteinContentOptions.isReadOnly());
    assertTrue(view.instrumentOptions.isReadOnly());
    assertTrue(view.proteinIdentificationOptions.isReadOnly());
    assertTrue(view.proteinIdentificationLinkField.isReadOnly());
    assertTrue(view.quantificationOptions.isReadOnly());
    assertTrue(view.quantificationLabelsField.isReadOnly());
    assertTrue(view.highResolutionOptions.isReadOnly());
    assertTrue(view.acetonitrileSolventsField.isReadOnly());
    assertTrue(view.methanolSolventsField.isReadOnly());
    assertTrue(view.chclSolventsField.isReadOnly());
    assertTrue(view.otherSolventsField.isReadOnly());
    assertTrue(view.otherSolventField.isReadOnly());
    assertTrue(view.commentsField.isReadOnly());
    assertTrue(view.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void editable_True() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setEditable(true);
    presenter.setBean(submission);

    assertFalse(view.serviceOptions.isReadOnly());
    assertFalse(view.sampleSupportOptions.isReadOnly());
    assertFalse(view.solutionSolventField.isReadOnly());
    assertFalse(view.sampleCountField.isReadOnly());
    assertFalse(view.sampleNameField.isReadOnly());
    assertFalse(view.formulaField.isReadOnly());
    assertFalse(view.monoisotopicMassField.isReadOnly());
    assertFalse(view.averageMassField.isReadOnly());
    assertFalse(view.toxicityField.isReadOnly());
    assertFalse(view.lightSensitiveField.isReadOnly());
    assertFalse(view.storageTemperatureOptions.isReadOnly());
    assertFalse(view.sampleContainerTypeOptions.isReadOnly());
    assertFalse(view.plateNameField.isReadOnly());
    SubmissionSample firstSample = dataProvider(view.samplesGrid).getItems().iterator().next();
    assertFalse(((TextField) view.samplesGrid.getColumn(SAMPLE_NAME_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertFalse(((TextField) view.samplesGrid.getColumn(SAMPLE_NUMBER_PROTEIN_PROPERTY)
        .getValueProvider().apply(firstSample)).isReadOnly());
    assertFalse(((TextField) view.samplesGrid.getColumn(PROTEIN_WEIGHT_PROPERTY).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertFalse(view.experienceField.isReadOnly());
    assertFalse(view.experienceGoalField.isReadOnly());
    assertFalse(view.taxonomyField.isReadOnly());
    assertFalse(view.proteinNameField.isReadOnly());
    assertFalse(view.proteinWeightField.isReadOnly());
    assertFalse(view.postTranslationModificationField.isReadOnly());
    assertFalse(view.sampleQuantityField.isReadOnly());
    assertFalse(view.sampleVolumeField.isReadOnly());
    assertFalse(view.standardCountField.isReadOnly());
    Standard firstStandard = dataProvider(view.standardsGrid).getItems().iterator().next();
    assertFalse(((TextField) view.standardsGrid.getColumn(STANDARD_NAME_PROPERTY).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) view.standardsGrid.getColumn(STANDARD_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) view.standardsGrid.getColumn(STANDARD_COMMENTS_PROPERTY)
        .getValueProvider().apply(firstStandard)).isReadOnly());
    assertFalse(view.contaminantCountField.isReadOnly());
    Contaminant firstContaminant = dataProvider(view.contaminantsGrid).getItems().iterator().next();
    assertFalse(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_NAME_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_QUANTITY_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) view.contaminantsGrid.getColumn(CONTAMINANT_COMMENTS_PROPERTY)
        .getValueProvider().apply(firstContaminant)).isReadOnly());
    assertFalse(view.separationField.isReadOnly());
    assertFalse(view.thicknessField.isReadOnly());
    assertFalse(view.colorationField.isReadOnly());
    assertFalse(view.otherColorationField.isReadOnly());
    assertFalse(view.developmentTimeField.isReadOnly());
    assertFalse(view.decolorationField.isReadOnly());
    assertFalse(view.weightMarkerQuantityField.isReadOnly());
    assertFalse(view.proteinQuantityField.isReadOnly());
    assertFalse(view.gelImagesGrid.getColumn(REMOVE_GEL_IMAGE).isHidden());
    assertFalse(view.digestionOptions.isReadOnly());
    assertFalse(view.usedProteolyticDigestionMethodField.isReadOnly());
    assertFalse(view.otherProteolyticDigestionMethodField.isReadOnly());
    assertFalse(view.injectionTypeOptions.isReadOnly());
    assertFalse(view.sourceOptions.isReadOnly());
    assertFalse(view.proteinContentOptions.isReadOnly());
    assertFalse(view.instrumentOptions.isReadOnly());
    assertFalse(view.proteinIdentificationOptions.isReadOnly());
    assertFalse(view.proteinIdentificationLinkField.isReadOnly());
    assertFalse(view.quantificationOptions.isReadOnly());
    assertFalse(view.quantificationLabelsField.isReadOnly());
    assertFalse(view.highResolutionOptions.isReadOnly());
    assertFalse(view.acetonitrileSolventsField.isReadOnly());
    assertFalse(view.methanolSolventsField.isReadOnly());
    assertFalse(view.chclSolventsField.isReadOnly());
    assertFalse(view.otherSolventsField.isReadOnly());
    assertFalse(view.otherSolventField.isReadOnly());
    assertFalse(view.commentsField.isReadOnly());
    assertFalse(view.filesGrid.getColumn(REMOVE_FILE).isHidden());
  }

  @Test
  public void visible_Lcmsms_Solution() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(support);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertTrue(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Solution_Editable() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertTrue(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertTrue(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertTrue(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_UsedDigestion() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);

    view.digestionOptions.setValue(DIGESTED);

    assertTrue(view.usedProteolyticDigestionMethodField.isVisible());
  }

  @Test
  public void visible_Lcmsms_OtherDigestion() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);

    view.digestionOptions.setValue(ProteolyticDigestion.OTHER);

    assertTrue(view.otherProteolyticDigestionMethodField.isVisible());
    assertTrue(view.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void visible_Lcmsms_Plate() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);

    view.sampleContainerTypeOptions.setValue(SPOT);

    assertTrue(view.plateNameField.isVisible());
    assertFalse(view.samplesGridLayout.isVisible());
    assertFalse(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertTrue(view.samplesPlateContainer.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry_Editable() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(DRY);

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertTrue(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertTrue(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(GEL);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertTrue(view.separationField.isVisible());
    assertTrue(view.thicknessField.isVisible());
    assertTrue(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertTrue(view.developmentTimeField.isVisible());
    assertTrue(view.decolorationField.isVisible());
    assertTrue(view.weightMarkerQuantityField.isVisible());
    assertTrue(view.proteinQuantityField.isVisible());
    assertTrue(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_Editable() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertTrue(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertTrue(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertTrue(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertTrue(view.separationField.isVisible());
    assertTrue(view.thicknessField.isVisible());
    assertTrue(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertTrue(view.developmentTimeField.isVisible());
    assertTrue(view.decolorationField.isVisible());
    assertTrue(view.weightMarkerQuantityField.isVisible());
    assertTrue(view.proteinQuantityField.isVisible());
    assertTrue(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(view.gelImagesGrid.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertTrue(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertTrue(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertTrue(view.quantificationOptions.isVisible());
    assertTrue(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_OtherColoration() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);

    view.colorationField.setValue(GelColoration.OTHER);

    assertTrue(view.otherColorationField.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    submission.setStructure(new Structure());
    submission.getStructure().setFilename("structure.png");
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertTrue(view.solutionSolventField.isVisible());
    assertFalse(view.sampleCountField.isVisible());
    assertTrue(view.sampleNameField.isVisible());
    assertTrue(view.formulaField.isVisible());
    assertTrue(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(view.structureButton.isVisible());
    assertTrue(view.monoisotopicMassField.isVisible());
    assertTrue(view.averageMassField.isVisible());
    assertTrue(view.toxicityField.isVisible());
    assertTrue(view.lightSensitiveField.isVisible());
    assertTrue(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertFalse(view.samplesLabel.isVisible());
    assertFalse(view.samplesGridLayout.isVisible());
    assertFalse(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertFalse(view.experienceField.isVisible());
    assertFalse(view.experienceGoalField.isVisible());
    assertFalse(view.taxonomyField.isVisible());
    assertFalse(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertFalse(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertFalse(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertTrue(view.highResolutionOptions.isVisible());
    assertTrue(view.acetonitrileSolventsField.isVisible());
    assertTrue(view.methanolSolventsField.isVisible());
    assertTrue(view.chclSolventsField.isVisible());
    assertTrue(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_Editable() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    uploadStructure();

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertTrue(view.solutionSolventField.isVisible());
    assertFalse(view.sampleCountField.isVisible());
    assertTrue(view.sampleNameField.isVisible());
    assertTrue(view.formulaField.isVisible());
    assertTrue(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(view.structureButton.isVisible());
    assertTrue(view.monoisotopicMassField.isVisible());
    assertTrue(view.averageMassField.isVisible());
    assertTrue(view.toxicityField.isVisible());
    assertTrue(view.lightSensitiveField.isVisible());
    assertTrue(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertFalse(view.samplesLabel.isVisible());
    assertFalse(view.samplesGridLayout.isVisible());
    assertFalse(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertFalse(view.experienceField.isVisible());
    assertFalse(view.experienceGoalField.isVisible());
    assertFalse(view.taxonomyField.isVisible());
    assertFalse(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertFalse(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertFalse(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertTrue(view.highResolutionOptions.isVisible());
    assertTrue(view.acetonitrileSolventsField.isVisible());
    assertTrue(view.methanolSolventsField.isVisible());
    assertTrue(view.chclSolventsField.isVisible());
    assertTrue(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_NoStructure() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    view.otherSolventsField.setValue(true);

    assertFalse(view.structureButton.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_OtherSolvents() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    uploadStructure();
    view.otherSolventsField.setValue(true);

    assertTrue(view.otherSolventField.isVisible());
    assertTrue(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    submission.setStructure(new Structure());
    submission.getStructure().setFilename("structure.png");
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertFalse(view.sampleCountField.isVisible());
    assertTrue(view.sampleNameField.isVisible());
    assertTrue(view.formulaField.isVisible());
    assertTrue(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertTrue(view.structureButton.isVisible());
    assertTrue(view.monoisotopicMassField.isVisible());
    assertTrue(view.averageMassField.isVisible());
    assertTrue(view.toxicityField.isVisible());
    assertTrue(view.lightSensitiveField.isVisible());
    assertTrue(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertFalse(view.samplesLabel.isVisible());
    assertFalse(view.samplesGridLayout.isVisible());
    assertFalse(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertFalse(view.experienceField.isVisible());
    assertFalse(view.experienceGoalField.isVisible());
    assertFalse(view.taxonomyField.isVisible());
    assertFalse(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertFalse(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertFalse(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertTrue(view.highResolutionOptions.isVisible());
    assertTrue(view.acetonitrileSolventsField.isVisible());
    assertTrue(view.methanolSolventsField.isVisible());
    assertTrue(view.chclSolventsField.isVisible());
    assertTrue(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry_Editable() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(DRY);
    uploadStructure();

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertFalse(view.sampleCountField.isVisible());
    assertTrue(view.sampleNameField.isVisible());
    assertTrue(view.formulaField.isVisible());
    assertTrue(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertTrue(booleanCaptor.getValue());
    assertTrue(view.structureButton.isVisible());
    assertTrue(view.monoisotopicMassField.isVisible());
    assertTrue(view.averageMassField.isVisible());
    assertTrue(view.toxicityField.isVisible());
    assertTrue(view.lightSensitiveField.isVisible());
    assertTrue(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertFalse(view.samplesLabel.isVisible());
    assertFalse(view.samplesGridLayout.isVisible());
    assertFalse(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertFalse(view.experienceField.isVisible());
    assertFalse(view.experienceGoalField.isVisible());
    assertFalse(view.taxonomyField.isVisible());
    assertFalse(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertFalse(view.postTranslationModificationField.isVisible());
    assertFalse(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertFalse(view.standardsPanel.isVisible());
    assertFalse(view.standardCountField.isVisible());
    assertFalse(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.injectionTypeOptions.isVisible());
    assertFalse(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertFalse(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertTrue(view.highResolutionOptions.isVisible());
    assertTrue(view.acetonitrileSolventsField.isVisible());
    assertTrue(view.methanolSolventsField.isVisible());
    assertTrue(view.chclSolventsField.isVisible());
    assertTrue(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertTrue(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.injectionTypeOptions.isVisible());
    assertTrue(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution_Editable() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertTrue(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertTrue(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertTrue(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.injectionTypeOptions.isVisible());
    assertTrue(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setSupport(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.sampleTypeLabel.isVisible());
    assertFalse(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertFalse(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertFalse(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.injectionTypeOptions.isVisible());
    assertTrue(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry_Editable() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(DRY);

    assertTrue(view.sampleTypeLabel.isVisible());
    assertTrue(view.inactiveLabel.isVisible());
    assertTrue(view.serviceOptions.isVisible());
    assertTrue(view.sampleSupportOptions.isVisible());
    assertFalse(view.solutionSolventField.isVisible());
    assertTrue(view.sampleCountField.isVisible());
    assertFalse(view.sampleNameField.isVisible());
    assertFalse(view.formulaField.isVisible());
    assertFalse(view.structureLayout.isVisible());
    verify(view.structureUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.structureButton.isVisible());
    assertFalse(view.monoisotopicMassField.isVisible());
    assertFalse(view.averageMassField.isVisible());
    assertFalse(view.toxicityField.isVisible());
    assertFalse(view.lightSensitiveField.isVisible());
    assertFalse(view.storageTemperatureOptions.isVisible());
    assertFalse(view.sampleContainerTypeOptions.isVisible());
    assertFalse(view.plateNameField.isVisible());
    assertTrue(view.samplesLabel.isVisible());
    assertTrue(view.samplesGridLayout.isVisible());
    assertTrue(view.samplesGrid.isVisible());
    assertTrue(view.fillSamplesButton.isVisible());
    assertFalse(view.samplesPlateContainer.isVisible());
    assertTrue(view.experienceField.isVisible());
    assertTrue(view.experienceGoalField.isVisible());
    assertTrue(view.taxonomyField.isVisible());
    assertTrue(view.proteinNameField.isVisible());
    assertFalse(view.proteinWeightField.isVisible());
    assertTrue(view.postTranslationModificationField.isVisible());
    assertTrue(view.sampleQuantityField.isVisible());
    assertFalse(view.sampleVolumeField.isVisible());
    assertTrue(view.standardsPanel.isVisible());
    assertTrue(view.standardCountField.isVisible());
    assertTrue(view.standardsGrid.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsGrid.isVisible());
    assertTrue(view.fillContaminantsButton.isVisible());
    assertFalse(view.separationField.isVisible());
    assertFalse(view.thicknessField.isVisible());
    assertFalse(view.colorationField.isVisible());
    assertFalse(view.otherColorationField.isVisible());
    assertFalse(view.developmentTimeField.isVisible());
    assertFalse(view.decolorationField.isVisible());
    assertFalse(view.weightMarkerQuantityField.isVisible());
    assertFalse(view.proteinQuantityField.isVisible());
    assertFalse(view.gelImagesLayout.isVisible());
    verify(view.gelImagesUploader, atLeastOnce()).setVisible(booleanCaptor.capture());
    assertFalse(booleanCaptor.getValue());
    assertFalse(view.gelImagesGrid.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.injectionTypeOptions.isVisible());
    assertTrue(view.sourceOptions.isVisible());
    assertFalse(view.proteinContentOptions.isVisible());
    assertTrue(view.instrumentOptions.isVisible());
    assertFalse(view.proteinIdentificationOptions.isVisible());
    assertFalse(view.proteinIdentificationLinkField.isVisible());
    assertFalse(view.quantificationOptions.isVisible());
    assertFalse(view.quantificationLabelsField.isVisible());
    assertFalse(view.highResolutionOptions.isVisible());
    assertFalse(view.acetonitrileSolventsField.isVisible());
    assertFalse(view.methanolSolventsField.isVisible());
    assertFalse(view.chclSolventsField.isVisible());
    assertFalse(view.otherSolventsField.isVisible());
    assertFalse(view.otherSolventField.isVisible());
    assertFalse(view.otherSolventNoteLabel.isVisible());
  }

  @Test
  public void submit_MissingService() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(null);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.serviceOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSupport() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(null);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sampleSupportOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSolutionSolvent() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.solutionSolventField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.solutionSolventField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowOneSampleCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        view.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxSampleCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("200000");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        view.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleSampleCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("1.3");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.sampleCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleName() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNameField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sampleNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleName() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(any())).thenReturn(true);

    view.submitButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName)),
        view.sampleNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingFormula() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.formulaField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.formulaField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStructure() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(STRUCTURE_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingMonoisotopicMass() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidMonoisotopicMass() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroMonoisotopicMass() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(view.monoisotopicMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidAverageMass() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.averageMassField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.averageMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroAverageMass() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.averageMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(view.averageMassField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStorageTemperature() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.storageTemperatureOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.storageTemperatureOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingPlateName() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    view.plateNameField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.plateNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsPlateName() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(plateService.nameAvailable(any())).thenReturn(false);

    view.submitButton.click();

    verify(plateService, atLeastOnce()).nameAvailable(plateName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        view.plateNameField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNames_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleNames_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    view.submitButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName1)),
        sampleNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNames_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleNames_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    view.submitButton.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName2)),
        sampleNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DuplicateSampleNames() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue(sampleName1);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sampleName1),
        stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    plateSampleNameCell(0, 0).setCellValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(SAMPLES_PROPERTY + ".missing", sampleCount),
        stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_MissingPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    plateSampleNameCell(0, 1).setCellValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(SAMPLES_PROPERTY + ".missing", sampleCount),
        stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_DuplicatePlateSampleNames() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    plateSampleNameCell(0, 1).setCellValue(sampleName1);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY + ".duplicate", sampleName1),
        stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField1.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNumberProteinField2.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinWeight1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidProteinWeight1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroProteinWeight1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField1.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinWeight2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidProteinWeight2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroProteinWeight2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleProteinWeightField2.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingExperience() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.experienceField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.experienceField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingTaxonomy() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.taxonomyField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.taxonomyField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidProteinWeight() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinWeightField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.proteinWeightField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroProteinWeight() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinWeightField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(view.proteinWeightField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleQuantity() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleQuantityField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sampleQuantityField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleVolume() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleVolume() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroSampleVolume() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(view.sampleVolumeField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_InvalidStandardCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroStandardCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxStandardCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("200");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleStandardCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.standardCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardName_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardName_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardQuantity_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardQuantity_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_InvalidContaminantCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroContaminantCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxContaminantCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("200");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        view.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleContaminantCount() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        view.contaminantCountField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantName_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantName_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantQuantity_1() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantQuantity_2() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingGelSeparation() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.separationField.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.separationField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingGelThickness() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.thicknessField.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.thicknessField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingOtherGelColoration() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.colorationField.setValue(GelColoration.OTHER);
    view.otherColorationField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.otherColorationField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidWeightMarkerQuantity() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.weightMarkerQuantityField.setValue("a");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        view.weightMarkerQuantityField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingGelImages() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingDigestion() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.digestionOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingUsedDigestion() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(DIGESTED);
    view.usedProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.usedProteolyticDigestionMethodField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingOtherDigestion() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(ProteolyticDigestion.OTHER);
    view.otherProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.otherProteolyticDigestionMethodField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingInjectionType() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.injectionTypeOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.injectionTypeOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSource() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sourceOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.sourceOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinContent() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinContentOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.proteinContentOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingInstrument() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.instrumentOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_MissingProteinIdentification() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinIdentificationOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.proteinIdentificationOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinIdentificationLink() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinIdentificationOptions.setValue(ProteinIdentification.OTHER);
    view.proteinIdentificationLinkField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.proteinIdentificationLinkField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingQuantificationLabels() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.quantificationOptions.setValue(Quantification.SILAC);
    view.quantificationLabelsField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.quantificationLabelsField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingHighResolution() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.highResolutionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.highResolutionOptions.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSolvents() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.acetonitrileSolventsField.setValue(false);
    view.methanolSolventsField.setValue(false);
    view.chclSolventsField.setValue(false);
    view.otherSolventsField.setValue(false);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(SOLVENTS_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingOtherSolvent() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.otherSolventsField.setValue(true);
    view.otherSolventField.setValue("");
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        view.otherSolventField.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_Lcmsms_Solution() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Solution_Plate() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    PlateSpot spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(0, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(sampleVolume, sample.getVolume(), 0.00001);
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(1, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Solution_OtherDigestion() throws Throwable {
    final ProteolyticDigestion digestion = ProteolyticDigestion.OTHER;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(digestion);
    view.otherProteolyticDigestionMethodField.setValue(otherDigestion);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Dry_Plate() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName1, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    PlateSpot spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(0, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    Standard standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName2, sample.getName());
    assertEquals(support, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(sampleQuantity, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(1, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
    assertEquals(2, sample.getStandards().size());
    standard = sample.getStandards().get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Gel() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(null, sample.getLims());
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
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Lcmsms_Gel_Plate() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName1, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    SampleContainer container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    PlateSpot spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(0, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
    assertEquals(sampleName2, sample.getName());
    assertEquals(GEL, sample.getSupport());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertNotNull(sample.getOriginalContainer());
    container = sample.getOriginalContainer();
    assertEquals(SPOT, container.getType());
    spot = (PlateSpot) container;
    assertEquals(0, spot.getColumn());
    assertEquals(1, spot.getRow());
    assertEquals(plateName, spot.getPlate().getName());
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
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_SmallMolecule_Solution() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(1, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    verify(view).showTrayNotification(resources.message("save", sampleName));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_SmallMolecule_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(1, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    verify(view).showTrayNotification(resources.message("save", sampleName));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_SmallMolecule_Plate() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getOriginalContainer());
  }

  @Test
  public void submit_Intactprotein_Solution() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_Intactprotein_Dry() throws Throwable {
    final SampleSupport support = DRY;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
    assertEquals(comments, submission.getComments());
    assertEquals(null, submission.getSubmissionDate());
    assertEquals(null, submission.getPrice());
    assertEquals(null, submission.getAdditionalPrice());
    assertEquals(null, submission.getUser());
    assertEquals(null, submission.getLaboratory());
    assertNotNull(submission.getSamples());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    Contaminant contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    sample = submission.getSamples().get(1);
    assertEquals(null, sample.getId());
    assertEquals(null, sample.getLims());
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
    assertEquals(standardComment1, standard.getComments());
    standard = sample.getStandards().get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals(standardComment2, standard.getComments());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertEquals(2, sample.getContaminants().size());
    contaminant = sample.getContaminants().get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    assertEquals(contaminantComment1, contaminant.getComments());
    contaminant = sample.getContaminants().get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(contaminantComment2, contaminant.getComments());
    assertTrue(submission.getGelImages() == null || submission.getGelImages().isEmpty());
    assertNull(submission.getStructure());
    assertEquals(2, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(filesFilename1, file.getFilename());
    assertArrayEquals(filesContent1, file.getContent());
    file = submission.getFiles().get(1);
    assertEquals(filesFilename2, file.getFilename());
    assertArrayEquals(filesContent2, file.getContent());
    verify(view).showTrayNotification(resources.message("save", experience));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void submit_IntactProtein_Plate() throws Throwable {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleContainerTypeOptions.setValue(SPOT);
    uploadStructure();
    uploadGelImages();
    uploadFiles();

    view.submitButton.click();

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
  public void setBean_Lcmsms() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setBean(submission);

    assertEquals(LC_MS_MS, view.serviceOptions.getValue());
    assertEquals(SOLUTION, view.sampleSupportOptions.getValue());
    assertEquals(solutionSolvent, view.solutionSolventField.getValue());
    assertEquals(sampleName1, view.sampleNameField.getValue());
    assertEquals(formula, view.formulaField.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, view.monoisotopicMassField), 0.001);
    assertEquals(averageMass, convert(doubleConverter, view.averageMassField), 0.001);
    assertEquals(toxicity, view.toxicityField.getValue());
    assertEquals(lightSensitive, view.lightSensitiveField.getValue());
    assertEquals(storageTemperature, view.storageTemperatureOptions.getValue());
    assertEquals(sampleContainerType, view.sampleContainerTypeOptions.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, view.sampleCountField));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(view.samplesGrid);
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
    assertEquals(experience, view.experienceField.getValue());
    assertEquals(experienceGoal, view.experienceGoalField.getValue());
    assertEquals(taxonomy, view.taxonomyField.getValue());
    assertEquals(proteinName, view.proteinNameField.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, view.proteinWeightField), 0.001);
    assertEquals(postTranslationModification, view.postTranslationModificationField.getValue());
    assertEquals(sampleQuantity, view.sampleQuantityField.getValue());
    assertEquals(sampleVolume, convert(doubleConverter, view.sampleVolumeField), 0.001);
    assertEquals((Integer) standardsCount, convert(integerConverter, view.standardCountField));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(view.standardsGrid);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount,
        convert(integerConverter, view.contaminantCountField));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(view.contaminantsGrid);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, view.separationField.getValue());
    assertEquals(gelThickness, view.thicknessField.getValue());
    assertEquals(gelColoration, view.colorationField.getValue());
    assertEquals(otherColoration, view.otherColorationField.getValue());
    assertEquals(developmentTime, view.developmentTimeField.getValue());
    assertEquals(decoloration, view.decolorationField.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, view.weightMarkerQuantityField),
        0.001);
    assertEquals(proteinQuantity, view.proteinQuantityField.getValue());
    assertEquals(digestion, view.digestionOptions.getValue());
    assertEquals(usedDigestion, view.usedProteolyticDigestionMethodField.getValue());
    assertEquals(otherDigestion, view.otherProteolyticDigestionMethodField.getValue());
    assertEquals(injectionType, view.injectionTypeOptions.getValue());
    assertEquals(source, view.sourceOptions.getValue());
    assertEquals(proteinContent, view.proteinContentOptions.getValue());
    assertEquals(instrument, view.instrumentOptions.getValue());
    assertEquals(proteinIdentification, view.proteinIdentificationOptions.getValue());
    assertEquals(proteinIdentificationLink, view.proteinIdentificationLinkField.getValue());
    assertEquals(quantification, view.quantificationOptions.getValue());
    assertEquals(quantificationLabels, view.quantificationLabelsField.getValue());
    assertEquals(highResolution, view.highResolutionOptions.getValue());
    assertEquals(acetonitrileSolvents, view.acetonitrileSolventsField.getValue());
    assertEquals(methanolSolvents, view.methanolSolventsField.getValue());
    assertEquals(chclSolvents, view.chclSolventsField.getValue());
    assertEquals(otherSolvents, view.otherSolventsField.getValue());
    assertEquals(otherSolvent, view.otherSolventField.getValue());
    assertEquals(comments, view.commentsField.getValue());
  }

  @Test
  @Ignore
  public void setBean_Lcmsms_Plate() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setBean(submission);

    // TODO assertEquals(plateName, view.plateNameField.getValue());
    // TODO plateSampleNameCell(0, 0).setCellValue(sampleName1);
    // TODO plateSampleNameCell(0, 1).setCellValue(sampleName2);
    fail("Program test");
  }

  @Test
  @Ignore
  public void setBean_SmallMolecule() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setBean(submission);

    fail("Program test");
  }

  @Test
  @Ignore
  public void setBean_IntactProtein() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setBean(submission);

    fail("Program test");
  }
}
