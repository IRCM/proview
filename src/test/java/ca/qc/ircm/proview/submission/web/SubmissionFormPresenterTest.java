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
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_BUTTON_STYLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_CONTAMINANTS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_SAMPLE_NAMES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_STANDARDS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FORMULA_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_TABLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGES_UPLOADER_PROGRESS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGE_FILENAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.HIGH_RESOLUTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INACTIVE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INSTRUMENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.LIGHT_SENSITIVE_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.MONOISOTOPIC_MASS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.NULL_ID;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_COLORATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_NOTE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.POST_TRANSLATION_MODIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_LINK_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_WEIGHT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_LABELS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.REMOVE_GEL_IMAGE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_COUNT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAMES_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NUMBER_PROTEIN_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_SUPPORT_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SEPARATION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
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
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_UPLOADER_PROGRESS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TAXONOMY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.THICKNESS_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TOXICITY_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION_PROPERTY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.WEIGHT_MARKER_QUANTITY_PROPERTY;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER_LABEL_ID;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.utils.web.VaadinUtils;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.Container;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.Plupload.FileUploadedListener;
import pl.exsio.plupload.PluploadFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionFormPresenterTest {
  private static final Logger logger = LoggerFactory.getLogger(SubmissionFormPresenterTest.class);
  private SubmissionFormPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private SubmissionSampleService submissionSampleService;
  @Mock
  private VaadinUtils vaadinUtils;
  @Mock
  private SubmissionForm view;
  @Mock
  private Plupload structureUploader;
  @Mock
  private Plupload gelImagesUploader;
  @Mock
  private PluploadFile pluploadFile;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
  @Captor
  private ArgumentCaptor<Boolean> booleanCaptor;
  @Captor
  private ArgumentCaptor<FileUploadedListener> fileUploadedListenerCaptor;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  private Label headerLabel = new Label();
  private Label sampleTypeLabel = new Label();
  private Label inactiveLabel = new Label();
  private Panel servicePanel = new Panel();
  private OptionGroup serviceOptions = new OptionGroup();
  private Panel samplesPanel = new Panel();
  private OptionGroup sampleSupportOptions = new OptionGroup();
  private TextField solutionSolventField = new TextField();
  private TextField sampleCountField = new TextField();
  private TextField sampleNameField = new TextField();
  private TextField formulaField = new TextField();
  private VerticalLayout structureLayout = new VerticalLayout();
  private Button structureButton = new Button();
  private ProgressBar structureProgress = new ProgressBar();
  private TextField monoisotopicMassField = new TextField();
  private TextField averageMassField = new TextField();
  private TextField toxicityField = new TextField();
  private CheckBox lightSensitiveField = new CheckBox();
  private OptionGroup storageTemperatureOptions = new OptionGroup();
  private HorizontalLayout sampleNamesLayout = new HorizontalLayout();
  private Table sampleNamesTable = new Table();
  private Button fillSampleNamesButton = new Button();
  private Panel experiencePanel = new Panel();
  private TextField experienceField = new TextField();
  private TextField experienceGoalField = new TextField();
  private TextField taxonomyField = new TextField();
  private TextField proteinNameField = new TextField();
  private TextField proteinWeightField = new TextField();
  private TextField postTranslationModificationField = new TextField();
  private TextField sampleQuantityField = new TextField();
  private TextField sampleVolumeField = new TextField();
  private Panel standardsPanel = new Panel();
  private TextField standardCountField = new TextField();
  private HorizontalLayout standardsTableLayout = new HorizontalLayout();
  private Table standardsTable = new Table();
  private Button fillStandardsButton = new Button();
  private Panel contaminantsPanel = new Panel();
  private TextField contaminantCountField = new TextField();
  private HorizontalLayout contaminantsTableLayout = new HorizontalLayout();
  private Table contaminantsTable = new Table();
  private Button fillContaminantsButton = new Button();
  private Panel gelPanel = new Panel();
  private ComboBox separationField = new ComboBox();
  private ComboBox thicknessField = new ComboBox();
  private ComboBox colorationField = new ComboBox();
  private TextField otherColorationField = new TextField();
  private TextField developmentTimeField = new TextField();
  private CheckBox decolorationField = new CheckBox();
  private TextField weightMarkerQuantityField = new TextField();
  private TextField proteinQuantityField = new TextField();
  private HorizontalLayout gelImagesLayout = new HorizontalLayout();
  private ProgressBar gelImageProgress = new ProgressBar();
  private Table gelImagesTable = new Table();
  private Panel servicesPanel = new Panel();
  private OptionGroup digestionOptions = new OptionGroup();
  private TextField usedProteolyticDigestionMethodField = new TextField();
  private TextField otherProteolyticDigestionMethodField = new TextField();
  private Label otherProteolyticDigestionMethodNote = new Label();
  private Label enrichmentLabel = new Label();
  private Label exclusionsLabel = new Label();
  private TextField sampleNumberProteinField = new TextField();
  private OptionGroup sourceOptions = new OptionGroup();
  private OptionGroup instrumentOptions = new OptionGroup();
  private OptionGroup proteinIdentificationOptions = new OptionGroup();
  private TextField proteinIdentificationLinkField = new TextField();
  private OptionGroup quantificationOptions = new OptionGroup();
  private TextArea quantificationLabelsField = new TextArea();
  private OptionGroup highResolutionOptions = new OptionGroup();
  private VerticalLayout solventsLayout = new VerticalLayout();
  private CheckBox acetonitrileSolventsField = new CheckBox();
  private CheckBox methanolSolventsField = new CheckBox();
  private CheckBox chclSolventsField = new CheckBox();
  private HorizontalLayout otherSolventLayout = new HorizontalLayout();
  private CheckBox otherSolventsField = new CheckBox();
  private TextField otherSolventField = new TextField();
  private Label otherSolventNoteLabel = new Label();
  private Panel commentsPanel = new Panel();
  private TextArea commentsField = new TextArea();
  private HorizontalLayout buttonsLayout = new HorizontalLayout();
  private Button submitButton = new Button();
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
  private String formula = "ch3oh";
  private String structureFilename = "ch3oh.png";
  private byte[] structureContent = new byte[2048];
  private double monoisotopicMass = 32.04;
  private double averageMass = 32.08;
  private String toxicity = "non-toxic";
  private boolean lightSensitive = true;
  private StorageTemperature storageTemperature = StorageTemperature.LOW;
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
  private byte[] gelImageContent1 = new byte[3072];
  private String gelImageFilename2 = "gel2.png";
  private byte[] gelImageContent2 = new byte[4096];
  private ProteolyticDigestion digestion = DIGESTED;
  private String usedDigestion = "typsinP";
  private String otherDigestion = "typsinP/Y";
  private int sampleNumberProtein = 2;
  private MassDetectionInstrumentSource source = MassDetectionInstrumentSource.NSI;
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
  private TextField sampleNameField1;
  private TextField sampleNameField2;
  private TextField standardNameField1;
  private TextField standardNameField2;
  private TextField standardQuantityField1;
  private TextField standardQuantityField2;
  private TextField contaminantNameField1;
  private TextField contaminantNameField2;
  private TextField contaminantQuantityField1;
  private TextField contaminantQuantityField2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter =
        new SubmissionFormPresenter(submissionService, submissionSampleService, vaadinUtils);
    view.headerLabel = headerLabel;
    view.sampleTypeLabel = sampleTypeLabel;
    view.inactiveLabel = inactiveLabel;
    view.servicePanel = servicePanel;
    view.serviceOptions = serviceOptions;
    view.samplesPanel = samplesPanel;
    view.sampleSupportOptions = sampleSupportOptions;
    view.solutionSolventField = solutionSolventField;
    view.sampleCountField = sampleCountField;
    view.sampleNameField = sampleNameField;
    view.formulaField = formulaField;
    view.structureLayout = structureLayout;
    view.structureButton = structureButton;
    view.structureUploader = structureUploader;
    view.structureProgress = structureProgress;
    view.monoisotopicMassField = monoisotopicMassField;
    view.averageMassField = averageMassField;
    view.toxicityField = toxicityField;
    view.lightSensitiveField = lightSensitiveField;
    view.storageTemperatureOptions = storageTemperatureOptions;
    view.sampleNamesLayout = sampleNamesLayout;
    view.sampleNamesTable = sampleNamesTable;
    view.fillSampleNamesButton = fillSampleNamesButton;
    view.experiencePanel = experiencePanel;
    view.experienceField = experienceField;
    view.experienceGoalField = experienceGoalField;
    view.taxonomyField = taxonomyField;
    view.proteinNameField = proteinNameField;
    view.proteinWeightField = proteinWeightField;
    view.postTranslationModificationField = postTranslationModificationField;
    view.sampleQuantityField = sampleQuantityField;
    view.sampleVolumeField = sampleVolumeField;
    view.standardsPanel = standardsPanel;
    view.standardCountField = standardCountField;
    view.standardsTableLayout = standardsTableLayout;
    view.standardsTable = standardsTable;
    view.fillStandardsButton = fillStandardsButton;
    view.contaminantsPanel = contaminantsPanel;
    view.contaminantCountField = contaminantCountField;
    view.contaminantsTableLayout = contaminantsTableLayout;
    view.contaminantsTable = contaminantsTable;
    view.fillContaminantsButton = fillContaminantsButton;
    view.gelPanel = gelPanel;
    view.separationField = separationField;
    view.thicknessField = thicknessField;
    view.colorationField = colorationField;
    view.otherColorationField = otherColorationField;
    view.developmentTimeField = developmentTimeField;
    view.decolorationField = decolorationField;
    view.weightMarkerQuantityField = weightMarkerQuantityField;
    view.proteinQuantityField = proteinQuantityField;
    view.gelImagesLayout = gelImagesLayout;
    view.gelImagesUploader = gelImagesUploader;
    view.gelImageProgress = gelImageProgress;
    view.gelImagesTable = gelImagesTable;
    view.servicesPanel = servicesPanel;
    view.digestionOptions = digestionOptions;
    view.usedProteolyticDigestionMethodField = usedProteolyticDigestionMethodField;
    view.otherProteolyticDigestionMethodField = otherProteolyticDigestionMethodField;
    view.otherProteolyticDigestionMethodNote = otherProteolyticDigestionMethodNote;
    view.enrichmentLabel = enrichmentLabel;
    view.exclusionsLabel = exclusionsLabel;
    view.sampleNumberProteinField = sampleNumberProteinField;
    view.sourceOptions = sourceOptions;
    view.instrumentOptions = instrumentOptions;
    view.proteinIdentificationOptions = proteinIdentificationOptions;
    view.proteinIdentificationLinkField = proteinIdentificationLinkField;
    view.quantificationOptions = quantificationOptions;
    view.quantificationLabelsField = quantificationLabelsField;
    view.highResolutionOptions = highResolutionOptions;
    view.solventsLayout = solventsLayout;
    view.acetonitrileSolventsField = acetonitrileSolventsField;
    view.methanolSolventsField = methanolSolventsField;
    view.chclSolventsField = chclSolventsField;
    view.otherSolventLayout = otherSolventLayout;
    view.otherSolventsField = otherSolventsField;
    view.otherSolventField = otherSolventField;
    view.solventsFields = new HashMap<>();
    view.solventsFields.put(Solvent.ACETONITRILE, acetonitrileSolventsField);
    view.solventsFields.put(Solvent.METHANOL, methanolSolventsField);
    view.solventsFields.put(Solvent.CHCL3, chclSolventsField);
    view.solventsFields.put(Solvent.OTHER, otherSolventsField);
    view.otherSolventNoteLabel = otherSolventNoteLabel;
    view.commentsPanel = commentsPanel;
    view.commentsField = commentsField;
    view.buttonsLayout = buttonsLayout;
    view.submitButton = submitButton;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    VaadinUtils realVaadinUtils = new VaadinUtils();
    when(vaadinUtils.getFieldMessage(any(), any(Locale.class))).thenAnswer(i -> {
      CommitException exception = (CommitException) i.getArguments()[0];
      Locale locale = (Locale) i.getArguments()[1];
      Map<Field<?>, InvalidValueException> fieldExceptions = exception.getInvalidFields();
      fieldExceptions.forEach((field, value) -> logger.debug("field {} in error: {}",
          field.getCaption(), value.getMessage()));
      return realVaadinUtils.getFieldMessage(exception, locale);
    });
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
    view.sampleCountField.setValue(String.valueOf(sampleCount));
    setValuesInSamplesTable();
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
    view.sampleNumberProteinField.setValue(String.valueOf(sampleNumberProtein));
    view.sourceOptions.setValue(source);
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

  private void setValuesInSamplesTable() {
    Container samplesContainer = view.sampleNamesTable.getContainerDataSource();
    List<?> samples = new ArrayList<>(samplesContainer.getItemIds());
    SubmissionSample sample = (SubmissionSample) samples.get(0);
    sampleNameField1 = setValuesInSamplesTable(samplesContainer, sample, sampleName1);
    sample = (SubmissionSample) samples.get(1);
    sampleNameField2 = setValuesInSamplesTable(samplesContainer, sample, sampleName2);
  }

  private TextField setValuesInSamplesTable(Container samplesContainer, SubmissionSample sample,
      String name) {
    TextField sampleNameTableField = (TextField) view.sampleNamesTable.getTableFieldFactory()
        .createField(samplesContainer, sample, SAMPLE_NAME_PROPERTY, view.sampleNamesTable);
    sampleNameTableField
        .setPropertyDataSource(samplesContainer.getContainerProperty(sample, SAMPLE_NAME_PROPERTY));
    sampleNameTableField.setValue(name);
    return sampleNameTableField;
  }

  private void setValuesInStandardsTable() {
    Container standardsContainer = view.standardsTable.getContainerDataSource();
    List<?> standards = new ArrayList<>(standardsContainer.getItemIds());
    Standard standard = (Standard) standards.get(0);
    standardNameField1 = setValueInStandardsTable(standardsContainer, standard, standardName1,
        STANDARD_NAME_PROPERTY);
    standardQuantityField1 = setValueInStandardsTable(standardsContainer, standard,
        standardQuantity1, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsTable(standardsContainer, standard, standardComment1,
        STANDARD_COMMENTS_PROPERTY);
    standard = (Standard) standards.get(1);
    standardNameField2 = setValueInStandardsTable(standardsContainer, standard, standardName2,
        STANDARD_NAME_PROPERTY);
    standardQuantityField2 = setValueInStandardsTable(standardsContainer, standard,
        standardQuantity2, STANDARD_QUANTITY_PROPERTY);
    setValueInStandardsTable(standardsContainer, standard, standardComment2,
        STANDARD_COMMENTS_PROPERTY);
  }

  private TextField setValueInStandardsTable(Container standardsContainer, Standard standard,
      String value, String property) {
    TextField field = (TextField) view.standardsTable.getTableFieldFactory()
        .createField(standardsContainer, standard, property, view.standardsTable);
    field.setPropertyDataSource(standardsContainer.getContainerProperty(standard, property));
    field.setValue(value);
    return field;
  }

  private void setValuesInContaminantsTable() {
    Container contaminantsContainer = view.contaminantsTable.getContainerDataSource();
    List<?> contaminants = new ArrayList<>(contaminantsContainer.getItemIds());
    Contaminant contaminant = (Contaminant) contaminants.get(0);
    contaminantNameField1 = setValueInContaminantsTable(contaminantsContainer, contaminant,
        contaminantName1, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField1 = setValueInContaminantsTable(contaminantsContainer, contaminant,
        contaminantQuantity1, CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsTable(contaminantsContainer, contaminant, contaminantComment1,
        CONTAMINANT_COMMENTS_PROPERTY);
    contaminant = (Contaminant) contaminants.get(1);
    contaminantNameField2 = setValueInContaminantsTable(contaminantsContainer, contaminant,
        contaminantName2, CONTAMINANT_NAME_PROPERTY);
    contaminantQuantityField2 = setValueInContaminantsTable(contaminantsContainer, contaminant,
        contaminantQuantity2, CONTAMINANT_QUANTITY_PROPERTY);
    setValueInContaminantsTable(contaminantsContainer, contaminant, contaminantComment2,
        CONTAMINANT_COMMENTS_PROPERTY);
  }

  private TextField setValueInContaminantsTable(Container contaminantsContainer,
      Contaminant contaminant, String value, String property) {
    TextField field = (TextField) view.contaminantsTable.getTableFieldFactory()
        .createField(contaminantsContainer, contaminant, property, view.contaminantsTable);
    field.setPropertyDataSource(contaminantsContainer.getContainerProperty(contaminant, property));
    field.setValue(value);
    return field;
  }

  private void uploadStructure() {
    when(pluploadFile.getName()).thenReturn(structureFilename);
    random.nextBytes(structureContent);
    when(pluploadFile.getUploadedFile()).thenReturn(structureContent);
    verify(structureUploader).addFileUploadedListener(fileUploadedListenerCaptor.capture());
    fileUploadedListenerCaptor.getValue().onFileUploaded(pluploadFile);
  }

  private void uploadGelImages() {
    verify(gelImagesUploader).addFileUploadedListener(fileUploadedListenerCaptor.capture());
    when(pluploadFile.getName()).thenReturn(gelImageFilename1);
    random.nextBytes(gelImageContent1);
    when(pluploadFile.getUploadedFile()).thenReturn(gelImageContent1);
    fileUploadedListenerCaptor.getValue().onFileUploaded(pluploadFile);
    when(pluploadFile.getName()).thenReturn(gelImageFilename2);
    random.nextBytes(gelImageContent2);
    when(pluploadFile.getUploadedFile()).thenReturn(gelImageContent2);
    fileUploadedListenerCaptor.getValue().onFileUploaded(pluploadFile);
  }

  @Test
  public void samplesTableColumns() {
    presenter.init(view);

    Object[] columns = view.sampleNamesTable.getVisibleColumns();

    assertEquals(1, columns.length);
    assertEquals(SAMPLE_NAME_PROPERTY, columns[0]);
  }

  @Test
  public void standardsColumns() {
    presenter.init(view);

    Object[] columns = view.standardsTable.getVisibleColumns();

    assertEquals(3, columns.length);
    assertEquals(STANDARD_NAME_PROPERTY, columns[0]);
    assertEquals(STANDARD_QUANTITY_PROPERTY, columns[1]);
    assertEquals(STANDARD_COMMENTS_PROPERTY, columns[2]);
  }

  @Test
  public void contaminantsColumns() {
    presenter.init(view);

    Object[] columns = view.contaminantsTable.getVisibleColumns();

    assertEquals(3, columns.length);
    assertEquals(CONTAMINANT_NAME_PROPERTY, columns[0]);
    assertEquals(CONTAMINANT_QUANTITY_PROPERTY, columns[1]);
    assertEquals(CONTAMINANT_COMMENTS_PROPERTY, columns[2]);
  }

  @Test
  public void gelImagesColumns() {
    presenter.init(view);

    Object[] columns = view.gelImagesTable.getVisibleColumns();

    assertEquals(1, columns.length);
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, columns[0]);
  }

  @Test
  public void gelImagesColumns_Editable() {
    presenter.init(view);
    presenter.setEditable(true);

    Object[] columns = view.gelImagesTable.getVisibleColumns();

    assertEquals(2, columns.length);
    assertEquals(GEL_IMAGE_FILENAME_PROPERTY, columns[0]);
    assertEquals(REMOVE_GEL_IMAGE, columns[1]);
  }

  @Test
  public void requiredFields() {
    presenter.init(view);

    assertTrue(view.serviceOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.serviceOptions.getRequiredError());
    assertTrue(view.sampleSupportOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sampleSupportOptions.getRequiredError());
    assertTrue(view.solutionSolventField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.solutionSolventField.getRequiredError());
    assertTrue(view.sampleCountField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sampleCountField.getRequiredError());
    assertTrue(view.sampleNameField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sampleNameField.getRequiredError());
    assertTrue(view.formulaField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.formulaField.getRequiredError());
    assertTrue(view.monoisotopicMassField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.monoisotopicMassField.getRequiredError());
    assertFalse(view.averageMassField.isRequired());
    assertFalse(view.toxicityField.isRequired());
    assertFalse(view.lightSensitiveField.isRequired());
    assertTrue(view.storageTemperatureOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.storageTemperatureOptions.getRequiredError());
    Container samplesContainer = view.sampleNamesTable.getContainerDataSource();
    if (samplesContainer.size() < 1) {
      samplesContainer.addItem(new SubmissionSample());
    }
    SubmissionSample firstSample =
        (SubmissionSample) samplesContainer.getItemIds().iterator().next();
    TextField sampleNameTableField = (TextField) view.sampleNamesTable.getTableFieldFactory()
        .createField(samplesContainer, firstSample, SAMPLE_NAME_PROPERTY, view.sampleNamesTable);
    assertTrue(sampleNameTableField.isRequired());
    assertEquals(generalResources.message(REQUIRED), sampleNameTableField.getRequiredError());
    assertTrue(view.experienceField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.experienceField.getRequiredError());
    assertFalse(view.experienceGoalField.isRequired());
    assertTrue(view.taxonomyField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.taxonomyField.getRequiredError());
    assertFalse(view.proteinNameField.isRequired());
    assertFalse(view.proteinWeightField.isRequired());
    assertFalse(view.postTranslationModificationField.isRequired());
    assertTrue(view.sampleQuantityField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sampleQuantityField.getRequiredError());
    assertTrue(view.sampleVolumeField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sampleVolumeField.getRequiredError());
    assertFalse(view.standardCountField.isRequired());
    Container standardsContainer = view.standardsTable.getContainerDataSource();
    if (standardsContainer.size() < 1) {
      standardsContainer.addItem(new Standard());
    }
    Standard firstStandard = (Standard) standardsContainer.getItemIds().iterator().next();
    TextField standardNameTableField =
        (TextField) view.standardsTable.getTableFieldFactory().createField(standardsContainer,
            firstStandard, STANDARD_NAME_PROPERTY, view.standardsTable);
    assertTrue(standardNameTableField.isRequired());
    assertEquals(generalResources.message(REQUIRED), standardNameTableField.getRequiredError());
    TextField standardQuantityTableField =
        (TextField) view.standardsTable.getTableFieldFactory().createField(standardsContainer,
            firstStandard, STANDARD_QUANTITY_PROPERTY, view.standardsTable);
    assertTrue(standardQuantityTableField.isRequired());
    assertEquals(generalResources.message(REQUIRED), standardQuantityTableField.getRequiredError());
    TextField standardCommentsTableField =
        (TextField) view.standardsTable.getTableFieldFactory().createField(standardsContainer,
            firstStandard, STANDARD_COMMENTS_PROPERTY, view.standardsTable);
    assertFalse(standardCommentsTableField.isRequired());
    assertFalse(view.contaminantCountField.isRequired());
    Container contaminantsContainer = view.contaminantsTable.getContainerDataSource();
    if (contaminantsContainer.size() < 1) {
      contaminantsContainer.addItem(new Contaminant());
    }
    Contaminant firstContaminant =
        (Contaminant) contaminantsContainer.getItemIds().iterator().next();
    TextField contaminantNameTableField =
        (TextField) view.contaminantsTable.getTableFieldFactory().createField(contaminantsContainer,
            firstContaminant, CONTAMINANT_NAME_PROPERTY, view.contaminantsTable);
    assertTrue(contaminantNameTableField.isRequired());
    assertEquals(generalResources.message(REQUIRED), contaminantNameTableField.getRequiredError());
    TextField contaminantQuantityTableField =
        (TextField) view.contaminantsTable.getTableFieldFactory().createField(contaminantsContainer,
            firstContaminant, CONTAMINANT_QUANTITY_PROPERTY, view.contaminantsTable);
    assertTrue(contaminantQuantityTableField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        contaminantQuantityTableField.getRequiredError());
    TextField contaminantCommentsTableField =
        (TextField) view.contaminantsTable.getTableFieldFactory().createField(contaminantsContainer,
            firstContaminant, CONTAMINANT_COMMENTS_PROPERTY, view.contaminantsTable);
    assertFalse(contaminantCommentsTableField.isRequired());
    assertTrue(view.separationField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.separationField.getRequiredError());
    assertTrue(view.thicknessField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.thicknessField.getRequiredError());
    assertFalse(view.colorationField.isRequired());
    assertTrue(view.otherColorationField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.otherColorationField.getRequiredError());
    assertFalse(view.developmentTimeField.isRequired());
    assertFalse(view.decolorationField.isRequired());
    assertFalse(view.weightMarkerQuantityField.isRequired());
    assertFalse(view.proteinQuantityField.isRequired());
    assertTrue(view.digestionOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.digestionOptions.getRequiredError());
    assertTrue(view.usedProteolyticDigestionMethodField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.usedProteolyticDigestionMethodField.getRequiredError());
    assertTrue(view.otherProteolyticDigestionMethodField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.otherProteolyticDigestionMethodField.getRequiredError());
    assertTrue(view.sampleNumberProteinField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.sampleNumberProteinField.getRequiredError());
    assertTrue(view.sourceOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.sourceOptions.getRequiredError());
    assertTrue(view.instrumentOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.instrumentOptions.getRequiredError());
    assertTrue(view.proteinIdentificationOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.proteinIdentificationOptions.getRequiredError());
    assertTrue(view.proteinIdentificationLinkField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.proteinIdentificationLinkField.getRequiredError());
    assertFalse(view.quantificationOptions.isRequired());
    assertFalse(view.quantificationLabelsField.isRequired());
    assertEquals(generalResources.message(REQUIRED),
        view.quantificationLabelsField.getRequiredError());
    assertTrue(view.highResolutionOptions.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.highResolutionOptions.getRequiredError());
    assertFalse(view.acetonitrileSolventsField.isRequired());
    assertFalse(view.methanolSolventsField.isRequired());
    assertFalse(view.chclSolventsField.isRequired());
    assertFalse(view.otherSolventsField.isRequired());
    assertFalse(view.otherSolventField.isRequired());
    assertEquals(generalResources.message(REQUIRED), view.otherSolventField.getRequiredError());
  }

  @Test
  public void converters() {
    presenter.init(view);

    assertNotNull(view.sampleCountField.getConverter());
    assertTrue(StringToIntegerConverter.class
        .isAssignableFrom(view.sampleCountField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_INTEGER),
        view.sampleCountField.getConversionError());
    assertNotNull(view.monoisotopicMassField.getConverter());
    assertTrue(StringToDoubleConverter.class
        .isAssignableFrom(view.monoisotopicMassField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_NUMBER),
        view.monoisotopicMassField.getConversionError());
    assertNotNull(view.averageMassField.getConverter());
    assertTrue(StringToDoubleConverter.class
        .isAssignableFrom(view.averageMassField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_NUMBER),
        view.averageMassField.getConversionError());
    assertNotNull(view.proteinWeightField.getConverter());
    assertTrue(StringToDoubleConverter.class
        .isAssignableFrom(view.proteinWeightField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_NUMBER),
        view.proteinWeightField.getConversionError());
    assertNotNull(view.sampleVolumeField.getConverter());
    assertTrue(StringToDoubleConverter.class
        .isAssignableFrom(view.sampleVolumeField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_NUMBER),
        view.sampleVolumeField.getConversionError());
    assertNotNull(view.standardCountField.getConverter());
    assertTrue(StringToIntegerConverter.class
        .isAssignableFrom(view.standardCountField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_INTEGER),
        view.standardCountField.getConversionError());
    assertNotNull(view.contaminantCountField.getConverter());
    assertTrue(StringToIntegerConverter.class
        .isAssignableFrom(view.contaminantCountField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_INTEGER),
        view.contaminantCountField.getConversionError());
    assertNotNull(view.weightMarkerQuantityField.getConverter());
    assertTrue(StringToDoubleConverter.class
        .isAssignableFrom(view.weightMarkerQuantityField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_NUMBER),
        view.weightMarkerQuantityField.getConversionError());
    assertNotNull(view.sampleNumberProteinField.getConverter());
    assertTrue(StringToIntegerConverter.class
        .isAssignableFrom(view.sampleNumberProteinField.getConverter().getClass()));
    assertEquals(generalResources.message(INVALID_INTEGER),
        view.sampleNumberProteinField.getConversionError());
  }

  @Test
  public void gelSupportDisabled_Smallmolecule() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);

    assertFalse(view.sampleSupportOptions.isItemEnabled(GEL));
  }

  @Test
  public void gelSupportDisabled_Intactprotein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);

    assertFalse(view.sampleSupportOptions.isItemEnabled(GEL));
  }

  @Test
  public void digestion_Options() {
    presenter.init(view);

    assertEquals(SubmissionForm.DIGESTIONS.length, view.digestionOptions.getItemIds().size());
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      assertTrue(digestion.name(), view.digestionOptions.getItemIds().contains(digestion));
    }
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
  public void instrument_DisabledOption() {
    instrument = MassDetectionInstrument.TOF;
    Submission submission = new Submission();
    submission.setMassDetectionInstrument(instrument);

    presenter.setItemDataSource(new BeanItem<>(submission));
    presenter.init(view);

    assertTrue(view.instrumentOptions.getItemIds().contains(instrument));
    assertFalse(view.instrumentOptions.isItemEnabled(instrument));
  }

  @Test
  public void proteinIdentification_Options() {
    presenter.init(view);

    assertEquals(SubmissionForm.PROTEIN_IDENTIFICATIONS.length,
        view.proteinIdentificationOptions.getItemIds().size());
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      assertTrue(proteinIdentification.name(),
          view.proteinIdentificationOptions.getItemIds().contains(proteinIdentification));
    }
  }

  @Test
  public void proteinIdentification_DisabledOption() {
    proteinIdentification = ProteinIdentification.NCBINR;
    Submission submission = new Submission();
    submission.setProteinIdentification(proteinIdentification);

    presenter.setItemDataSource(new BeanItem<>(submission));
    presenter.init(view);

    assertTrue(view.proteinIdentificationOptions.getItemIds().contains(proteinIdentification));
    assertFalse(view.proteinIdentificationOptions.isItemEnabled(proteinIdentification));
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
    assertFalse(view.quantificationLabelsField.isRequired());
    view.quantificationOptions.setValue(Quantification.LABEL_FREE);
    assertFalse(view.quantificationLabelsField.isRequired());
    view.quantificationOptions.setValue(Quantification.SILAC);
    assertTrue(view.quantificationLabelsField.isRequired());
  }

  @Test
  public void solvents_RequiredText() {
    presenter.init(view);
    presenter.setEditable(true);

    view.otherSolventsField.setValue(false);
    assertFalse(view.otherSolventField.isRequired());
    view.otherSolventsField.setValue(true);
    assertTrue(view.otherSolventField.isRequired());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER_LABEL_ID));
    assertTrue(view.headerLabel.getStyleName().contains("h1"));
    assertTrue(view.sampleTypeLabel.getStyleName().contains(SAMPLE_TYPE_LABEL));
    assertTrue(view.inactiveLabel.getStyleName().contains(INACTIVE_LABEL));
    assertTrue(view.servicePanel.getStyleName().contains(SERVICES_PANEL));
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
    assertTrue(view.structureProgress.getStyleName().contains(STRUCTURE_UPLOADER_PROGRESS));
    assertTrue(view.monoisotopicMassField.getStyleName().contains(MONOISOTOPIC_MASS_PROPERTY));
    assertTrue(view.averageMassField.getStyleName().contains(AVERAGE_MASS_PROPERTY));
    assertTrue(view.toxicityField.getStyleName().contains(TOXICITY_PROPERTY));
    assertTrue(view.lightSensitiveField.getStyleName().contains(LIGHT_SENSITIVE_PROPERTY));
    assertTrue(
        view.storageTemperatureOptions.getStyleName().contains(STORAGE_TEMPERATURE_PROPERTY));
    assertTrue(view.sampleNamesTable.getStyleName().contains(SAMPLE_NAMES_PROPERTY));
    assertTrue(view.fillSampleNamesButton.getStyleName().contains(FILL_SAMPLE_NAMES_PROPERTY));
    assertTrue(view.fillSampleNamesButton.getStyleName().contains(FILL_BUTTON_STYLE));
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
    assertTrue(view.standardsTable.getStyleName().contains(STANDARD_PROPERTY));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_STANDARDS_PROPERTY));
    assertTrue(view.fillStandardsButton.getStyleName().contains(FILL_BUTTON_STYLE));
    assertTrue(view.contaminantsPanel.getStyleName().contains(CONTAMINANTS_PANEL));
    assertTrue(view.contaminantCountField.getStyleName().contains(CONTAMINANT_COUNT_PROPERTY));
    assertTrue(view.contaminantsTable.getStyleName().contains(CONTAMINANT_PROPERTY));
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
    assertTrue(view.gelImageProgress.getStyleName().contains(GEL_IMAGES_UPLOADER_PROGRESS));
    assertTrue(view.gelImagesTable.getStyleName().contains(GEL_IMAGES_TABLE));
    assertTrue(view.servicesPanel.getStyleName().contains(SERVICES_PANEL));
    assertTrue(view.digestionOptions.getStyleName().contains(DIGESTION_PROPERTY));
    assertTrue(
        view.usedProteolyticDigestionMethodField.getStyleName().contains(USED_DIGESTION_PROPERTY));
    assertTrue(view.otherProteolyticDigestionMethodField.getStyleName()
        .contains(OTHER_DIGESTION_PROPERTY));
    assertTrue(view.enrichmentLabel.getStyleName().contains(ENRICHEMENT_PROPERTY));
    assertTrue(view.exclusionsLabel.getStyleName().contains(EXCLUSIONS_PROPERTY));
    assertTrue(
        view.sampleNumberProteinField.getStyleName().contains(SAMPLE_NUMBER_PROTEIN_PROPERTY));
    assertTrue(view.sourceOptions.getStyleName().contains(SOURCE_PROPERTY));
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
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(HEADER_LABEL_ID), view.headerLabel.getValue());
    assertEquals(resources.message(SAMPLE_TYPE_LABEL), view.sampleTypeLabel.getValue());
    assertEquals(resources.message(INACTIVE_LABEL), view.inactiveLabel.getValue());
    assertEquals(resources.message(SERVICE_PROPERTY), view.servicePanel.getCaption());
    assertEquals(null, view.serviceOptions.getCaption());
    for (Service service : SubmissionForm.SERVICES) {
      assertEquals(service.getLabel(locale), view.serviceOptions.getItemCaption(service));
    }
    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    assertEquals(resources.message(SAMPLE_SUPPORT_PROPERTY),
        view.sampleSupportOptions.getCaption());
    for (SampleSupport support : SubmissionForm.SUPPORT) {
      assertEquals(support.getLabel(locale), view.sampleSupportOptions.getItemCaption(support));
    }
    assertEquals(resources.message(SOLUTION_SOLVENT_PROPERTY),
        view.solutionSolventField.getCaption());
    assertEquals(resources.message(SAMPLE_COUNT_PROPERTY), view.sampleCountField.getCaption());
    assertEquals(resources.message(SAMPLE_NAMES_PROPERTY),
        view.sampleNamesTable.getColumnHeader(SAMPLE_NAME_PROPERTY));
    assertEquals(resources.message(SAMPLE_NAME_PROPERTY), view.sampleNameField.getCaption());
    assertEquals(resources.message(FORMULA_PROPERTY), view.formulaField.getCaption());
    assertEquals(resources.message(STRUCTURE_PROPERTY), view.structureLayout.getCaption());
    assertEquals("", view.structureButton.getCaption());
    verify(view.structureUploader).setCaption(resources.message(STRUCTURE_UPLOADER));
    verify(view.structureUploader).setIcon(FontAwesome.FILE_O);
    assertEquals(null, view.structureProgress.getCaption());
    assertEquals(FontAwesome.CLOUD_DOWNLOAD, view.structureProgress.getIcon());
    assertEquals(resources.message(MONOISOTOPIC_MASS_PROPERTY),
        view.monoisotopicMassField.getCaption());
    assertEquals(resources.message(AVERAGE_MASS_PROPERTY), view.averageMassField.getCaption());
    assertEquals(resources.message(TOXICITY_PROPERTY), view.toxicityField.getCaption());
    assertEquals(resources.message(LIGHT_SENSITIVE_PROPERTY),
        view.lightSensitiveField.getCaption());
    assertEquals(resources.message(STORAGE_TEMPERATURE_PROPERTY),
        view.storageTemperatureOptions.getCaption());
    for (StorageTemperature storageTemperature : SubmissionForm.STORAGE_TEMPERATURES) {
      assertEquals(storageTemperature.getLabel(locale),
          view.storageTemperatureOptions.getItemCaption(storageTemperature));
    }
    assertEquals(null, view.sampleNamesTable.getCaption());
    assertEquals(resources.message(FILL_SAMPLE_NAMES_PROPERTY),
        view.fillSampleNamesButton.getCaption());
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
        view.sampleQuantityField.getInputPrompt());
    assertEquals(resources.message(SAMPLE_VOLUME_PROPERTY), view.sampleVolumeField.getCaption());
    assertEquals(resources.message(STANDARDS_PANEL), view.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT_PROPERTY), view.standardCountField.getCaption());
    assertEquals(null, view.standardsTable.getCaption());
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_NAME_PROPERTY),
        view.standardsTable.getColumnHeader(STANDARD_NAME_PROPERTY));
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_QUANTITY_PROPERTY),
        view.standardsTable.getColumnHeader(STANDARD_QUANTITY_PROPERTY));
    assertEquals(resources.message(STANDARD_PROPERTY + "." + STANDARD_COMMENTS_PROPERTY),
        view.standardsTable.getColumnHeader(STANDARD_COMMENTS_PROPERTY));
    assertEquals(resources.message(FILL_STANDARDS_PROPERTY), view.fillStandardsButton.getCaption());
    assertEquals(resources.message(CONTAMINANTS_PANEL), view.contaminantsPanel.getCaption());
    assertEquals(resources.message(CONTAMINANT_COUNT_PROPERTY),
        view.contaminantCountField.getCaption());
    assertEquals(null, view.contaminantsTable.getCaption());
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_NAME_PROPERTY),
        view.contaminantsTable.getColumnHeader(CONTAMINANT_NAME_PROPERTY));
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_QUANTITY_PROPERTY),
        view.contaminantsTable.getColumnHeader(CONTAMINANT_QUANTITY_PROPERTY));
    assertEquals(resources.message(CONTAMINANT_PROPERTY + "." + CONTAMINANT_COMMENTS_PROPERTY),
        view.contaminantsTable.getColumnHeader(CONTAMINANT_COMMENTS_PROPERTY));
    assertEquals(resources.message(FILL_CONTAMINANTS_PROPERTY),
        view.fillContaminantsButton.getCaption());
    assertEquals(resources.message(GEL_PANEL), view.gelPanel.getCaption());
    assertEquals(resources.message(SEPARATION_PROPERTY), view.separationField.getCaption());
    for (GelSeparation separation : SubmissionForm.SEPARATION) {
      assertEquals(separation.getLabel(locale), view.separationField.getItemCaption(separation));
    }
    assertEquals(resources.message(THICKNESS_PROPERTY), view.thicknessField.getCaption());
    for (GelThickness thickness : SubmissionForm.THICKNESS) {
      assertEquals(thickness.getLabel(locale), view.thicknessField.getItemCaption(thickness));
    }
    assertEquals(resources.message(COLORATION_PROPERTY), view.colorationField.getCaption());
    assertEquals(GelColoration.getNullLabel(locale), view.colorationField.getItemCaption(NULL_ID));
    for (GelColoration coloration : SubmissionForm.COLORATION) {
      assertEquals(coloration.getLabel(locale), view.colorationField.getItemCaption(coloration));
    }
    assertEquals(resources.message(OTHER_COLORATION_PROPERTY),
        view.otherColorationField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY),
        view.developmentTimeField.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME_PROPERTY + "." + EXAMPLE),
        view.developmentTimeField.getInputPrompt());
    assertEquals(resources.message(DECOLORATION_PROPERTY), view.decolorationField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY),
        view.weightMarkerQuantityField.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY_PROPERTY + "." + EXAMPLE),
        view.weightMarkerQuantityField.getInputPrompt());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY),
        view.proteinQuantityField.getCaption());
    assertEquals(resources.message(PROTEIN_QUANTITY_PROPERTY + "." + EXAMPLE),
        view.proteinQuantityField.getInputPrompt());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY), view.gelImagesLayout.getCaption());
    verify(view.gelImagesUploader).setCaption(resources.message(GEL_IMAGES_UPLOADER));
    verify(view.gelImagesUploader).setIcon(FontAwesome.FILES_O);
    assertEquals(null, view.gelImageProgress.getCaption());
    assertEquals(FontAwesome.CLOUD_DOWNLOAD, view.gelImageProgress.getIcon());
    assertEquals(null, view.gelImagesTable.getCaption());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + GEL_IMAGE_FILENAME_PROPERTY),
        view.gelImagesTable.getColumnHeader(GEL_IMAGE_FILENAME_PROPERTY));
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + REMOVE_GEL_IMAGE),
        view.gelImagesTable.getColumnHeader(REMOVE_GEL_IMAGE));
    assertEquals(resources.message(SERVICES_PANEL), view.servicesPanel.getCaption());
    assertEquals(resources.message(DIGESTION_PROPERTY), view.digestionOptions.getCaption());
    for (ProteolyticDigestion digestion : SubmissionForm.DIGESTIONS) {
      assertEquals(digestion.getLabel(locale), view.digestionOptions.getItemCaption(digestion));
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
    assertEquals(resources.message(SAMPLE_NUMBER_PROTEIN_PROPERTY),
        view.sampleNumberProteinField.getCaption());
    assertEquals(resources.message(SOURCE_PROPERTY), view.sourceOptions.getCaption());
    for (MassDetectionInstrumentSource source : SubmissionForm.SOURCES) {
      assertEquals(source.getLabel(locale), view.sourceOptions.getItemCaption(source));
    }
    assertEquals(resources.message(INSTRUMENT_PROPERTY), view.instrumentOptions.getCaption());
    for (MassDetectionInstrument instrument : SubmissionForm.INSTRUMENTS) {
      assertEquals(instrument.getLabel(locale), view.instrumentOptions.getItemCaption(instrument));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_PROPERTY),
        view.proteinIdentificationOptions.getCaption());
    for (ProteinIdentification proteinIdentification : SubmissionForm.PROTEIN_IDENTIFICATIONS) {
      assertEquals(proteinIdentification.getLabel(locale),
          view.proteinIdentificationOptions.getItemCaption(proteinIdentification));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_LINK_PROPERTY),
        view.proteinIdentificationLinkField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_PROPERTY),
        view.quantificationOptions.getCaption());
    assertEquals(Quantification.getNullLabel(locale),
        view.quantificationOptions.getItemCaption(NULL_ID));
    for (Quantification quantification : SubmissionForm.QUANTIFICATION) {
      assertEquals(quantification.getLabel(locale),
          view.quantificationOptions.getItemCaption(quantification));
    }
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY),
        view.quantificationLabelsField.getCaption());
    assertEquals(resources.message(QUANTIFICATION_LABELS_PROPERTY + "." + EXAMPLE),
        view.quantificationLabelsField.getInputPrompt());
    assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY),
        view.highResolutionOptions.getCaption());
    for (boolean value : new boolean[] { false, true }) {
      assertEquals(resources.message(HIGH_RESOLUTION_PROPERTY + "." + value),
          view.highResolutionOptions.getItemCaption(value));
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
  }

  @Test
  public void visible_Lcmsms_Solution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);

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
    assertTrue(view.sampleNamesLayout.isVisible());
    assertTrue(view.sampleNamesTable.isVisible());
    assertTrue(view.fillSampleNamesButton.isVisible());
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
    assertTrue(view.standardsTable.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.sampleNumberProteinField.isVisible());
    assertFalse(view.sourceOptions.isVisible());
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
  public void visible_Lcmsms_Dry() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(DRY);

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
    assertTrue(view.sampleNamesLayout.isVisible());
    assertTrue(view.sampleNamesTable.isVisible());
    assertTrue(view.fillSampleNamesButton.isVisible());
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
    assertTrue(view.standardsTable.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.sampleNumberProteinField.isVisible());
    assertFalse(view.sourceOptions.isVisible());
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
  }

  @Test
  public void visible_Lcmsms_Gel() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);

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
    assertTrue(view.sampleNamesLayout.isVisible());
    assertTrue(view.sampleNamesTable.isVisible());
    assertTrue(view.fillSampleNamesButton.isVisible());
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
    assertFalse(view.standardsTable.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsTable.isVisible());
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
    assertTrue(view.gelImagesTable.isVisible());
    assertTrue(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(view.enrichmentLabel.isVisible());
    assertTrue(view.exclusionsLabel.isVisible());
    assertFalse(view.sampleNumberProteinField.isVisible());
    assertFalse(view.sourceOptions.isVisible());
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
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);

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
    assertFalse(view.sampleNamesLayout.isVisible());
    assertFalse(view.sampleNamesTable.isVisible());
    assertFalse(view.fillSampleNamesButton.isVisible());
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
    assertFalse(view.standardsTable.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.sampleNumberProteinField.isVisible());
    assertFalse(view.sourceOptions.isVisible());
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
    assertTrue(view.otherSolventField.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(DRY);

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
    assertFalse(view.sampleNamesLayout.isVisible());
    assertFalse(view.sampleNamesTable.isVisible());
    assertFalse(view.fillSampleNamesButton.isVisible());
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
    assertFalse(view.standardsTable.isVisible());
    assertFalse(view.fillStandardsButton.isVisible());
    assertFalse(view.contaminantsPanel.isVisible());
    assertFalse(view.contaminantCountField.isVisible());
    assertFalse(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertFalse(view.sampleNumberProteinField.isVisible());
    assertFalse(view.sourceOptions.isVisible());
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
    assertTrue(view.otherSolventField.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);

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
    assertTrue(view.sampleNamesLayout.isVisible());
    assertTrue(view.sampleNamesTable.isVisible());
    assertTrue(view.fillSampleNamesButton.isVisible());
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
    assertTrue(view.standardsTable.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.sampleNumberProteinField.isVisible());
    assertTrue(view.sourceOptions.isVisible());
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
  }

  @Test
  public void visible_Intactprotein_Dry() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(DRY);

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
    assertTrue(view.sampleNamesLayout.isVisible());
    assertTrue(view.sampleNamesTable.isVisible());
    assertTrue(view.fillSampleNamesButton.isVisible());
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
    assertTrue(view.standardsTable.isVisible());
    assertTrue(view.fillStandardsButton.isVisible());
    assertTrue(view.contaminantsPanel.isVisible());
    assertTrue(view.contaminantCountField.isVisible());
    assertTrue(view.contaminantsTable.isVisible());
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
    assertFalse(view.gelImagesTable.isVisible());
    assertFalse(view.digestionOptions.isVisible());
    assertFalse(view.usedProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodField.isVisible());
    assertFalse(view.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(view.enrichmentLabel.isVisible());
    assertFalse(view.exclusionsLabel.isVisible());
    assertTrue(view.sampleNumberProteinField.isVisible());
    assertTrue(view.sourceOptions.isVisible());
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
  }

  @Test
  public void submit_MissingService() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(null);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.serviceOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSupport() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(null);
    setFields();
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleSupportOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSolutionSolvent() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.solutionSolventField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.solutionSolventField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.sampleCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowOneSampleCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 1, 200), stringCaptor.getValue());
    assertNotNull(view.sampleCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxSampleCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("200000");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 1, 200), stringCaptor.getValue());
    assertNotNull(view.sampleCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleSampleCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleCountField.setValue("1.3");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.sampleCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleName() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNameField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleNameField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleName() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    when(submissionSampleService.exists(any())).thenReturn(true);

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(ALREADY_EXISTS), stringCaptor.getValue());
    assertNotNull(view.sampleNameField.getErrorMessage());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingFormula() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.formulaField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.formulaField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStructure() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(STRUCTURE_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingMonoisotopicMass() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.monoisotopicMassField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidMonoisotopicMass() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_NUMBER), stringCaptor.getValue());
    assertNotNull(view.monoisotopicMassField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroMonoisotopicMass() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.monoisotopicMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertNotNull(stringCaptor.getValue());
    assertNotNull(view.monoisotopicMassField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidAverageMass() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.averageMassField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_NUMBER), stringCaptor.getValue());
    assertNotNull(view.averageMassField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroAverageMass() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.averageMassField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertNotNull(stringCaptor.getValue());
    assertNotNull(view.averageMassField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStorageTemperature() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.storageTemperatureOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.storageTemperatureOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNames_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField1.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(sampleNameField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleNames_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(ALREADY_EXISTS), stringCaptor.getValue());
    assertNotNull(sampleNameField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNames_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(sampleNameField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_ExistsSampleNames_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(ALREADY_EXISTS), stringCaptor.getValue());
    assertNotNull(sampleNameField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DuplicateSampleNames() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    sampleNameField2.setValue(sampleName1);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(resources.message(SAMPLE_NAMES_PROPERTY + ".duplicate"), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingExperience() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.experienceField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.experienceField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingTaxonomy() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.taxonomyField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.taxonomyField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidProteinWeight() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinWeightField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_NUMBER), stringCaptor.getValue());
    assertNotNull(view.proteinWeightField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroProteinWeight() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinWeightField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertNotNull(stringCaptor.getValue());
    assertNotNull(view.proteinWeightField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleQuantity() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleQuantityField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleQuantityField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleVolume() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleVolumeField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleVolume() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_NUMBER), stringCaptor.getValue());
    assertNotNull(view.sampleVolumeField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroSampleVolume() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleVolumeField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertNotNull(stringCaptor.getValue());
    assertNotNull(view.sampleVolumeField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_InvalidStandardCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.standardCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroStandardCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 0, 10), stringCaptor.getValue());
    assertNotNull(view.standardCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxStandardCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("200");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 0, 10), stringCaptor.getValue());
    assertNotNull(view.standardCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleStandardCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.standardCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.standardCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardName_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField1.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(standardNameField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardName_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardNameField2.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(standardNameField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardQuantity_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(standardQuantityField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingStandardQuantity_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    standardQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(standardQuantityField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void submit_InvalidContaminantCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.contaminantCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroContaminantCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 0, 10), stringCaptor.getValue());
    assertNotNull(view.contaminantCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_AboveMaxContaminantCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("200");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(OUT_OF_RANGE, 0, 10), stringCaptor.getValue());
    assertNotNull(view.contaminantCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleContaminantCount() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.contaminantCountField.setValue("1.2");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.contaminantCountField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantName_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField1.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(contaminantNameField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantName_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantNameField2.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(contaminantNameField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantQuantity_1() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField1.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(contaminantQuantityField1.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingContaminantQuantity_2() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    contaminantQuantityField2.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(contaminantQuantityField2.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingGelSeparation() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.separationField.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.separationField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidGelSeparation() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();

    view.separationField.setValue("abc");

    assertEquals(gelSeparation, view.separationField.getValue());
  }

  @Test
  public void submit_MissingGelThickness() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.thicknessField.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.thicknessField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidGelThickness() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();

    view.thicknessField.setValue("a");

    assertEquals(gelThickness, view.thicknessField.getValue());
  }

  @Test
  public void submit_InvalidGelColoration() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();

    view.colorationField.setValue("a");

    assertEquals(gelColoration, view.colorationField.getValue());
  }

  @Test
  public void submit_MissingOtherGelColoration() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.colorationField.setValue(GelColoration.OTHER);
    view.otherColorationField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.otherColorationField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidWeightMarkerQuantity() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    view.weightMarkerQuantityField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_NUMBER), stringCaptor.getValue());
    assertNotNull(view.weightMarkerQuantityField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingGelImages() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(GEL_IMAGES_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingDigestion() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.digestionOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingUsedDigestion() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(DIGESTED);
    view.usedProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.usedProteolyticDigestionMethodField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingOtherDigestion() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.digestionOptions.setValue(ProteolyticDigestion.OTHER);
    view.otherProteolyticDigestionMethodField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.otherProteolyticDigestionMethodField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSampleNumberProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNumberProteinField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sampleNumberProteinField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_InvalidSampleNumberProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNumberProteinField.setValue("a");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.sampleNumberProteinField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_BelowZeroSampleNumberProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNumberProteinField.setValue("-1");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertNotNull(stringCaptor.getValue());
    assertNotNull(view.sampleNumberProteinField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_DoubleSampleNumberProtein() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sampleNumberProteinField.setValue("1.2");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(INVALID_INTEGER), stringCaptor.getValue());
    assertNotNull(view.sampleNumberProteinField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSource() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.sourceOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.sourceOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingInstrument() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.instrumentOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.instrumentOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinIdentification() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinIdentificationOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.proteinIdentificationOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingProteinIdentificationLink() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.proteinIdentificationOptions.setValue(ProteinIdentification.OTHER);
    view.proteinIdentificationLinkField.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.proteinIdentificationLinkField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingQuantificationLabels() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.quantificationOptions.setValue(Quantification.SILAC);
    view.quantificationLabelsField.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.quantificationLabelsField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingHighResolution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.highResolutionOptions.setValue(null);
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.highResolutionOptions.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingSolvents() {
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

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(SOLVENTS_PROPERTY + "." + REQUIRED), stringCaptor.getValue());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_MissingOtherSolvent() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    view.otherSolventsField.setValue(true);
    view.otherSolventField.setValue("");
    uploadStructure();
    uploadGelImages();

    view.submitButton.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(REQUIRED), stringCaptor.getValue());
    assertNotNull(view.otherSolventField.getErrorMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void submit_Lcmsms_Solution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();

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
    assertEquals(null, submission.getSampleNumberProtein());
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
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(proteinWeight, submission.getMolecularWeight(), 0.0001);
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
  }

  @Test
  public void submit_Lcmsms_Solution_OtherDigestion() {
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
    assertEquals(null, submission.getSampleNumberProtein());
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
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(proteinWeight, submission.getMolecularWeight(), 0.0001);
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
  }

  @Test
  public void submit_Lcmsms_Dry() {
    final SampleSupport support = DRY;
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();

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
    assertEquals(null, submission.getSampleNumberProtein());
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
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(proteinWeight, submission.getMolecularWeight(), 0.0001);
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
  }

  @Test
  public void submit_Lcmsms_Gel() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(LC_MS_MS);
    view.sampleSupportOptions.setValue(GEL);
    setFields();
    uploadStructure();
    uploadGelImages();

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
    assertEquals(null, submission.getSampleNumberProtein());
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
    assertEquals(null, submission.getProteinContent());
    assertEquals(proteinName, submission.getProtein());
    assertEquals(proteinWeight, submission.getMolecularWeight(), 0.0001);
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
  }

  @Test
  public void submit_SmallMolecule_Solution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(SMALL_MOLECULE);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();

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
    assertEquals(null, submission.getSampleNumberProtein());
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
    assertEquals(null, submission.getMolecularWeight());
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
    verify(view).afterSuccessfulSave(resources.message("save", sampleName));
  }

  @Test
  public void submit_Intactprotein_Solution() {
    presenter.init(view);
    presenter.setEditable(true);
    view.serviceOptions.setValue(INTACT_PROTEIN);
    view.sampleSupportOptions.setValue(support);
    setFields();
    uploadStructure();
    uploadGelImages();

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
    assertEquals((Integer) sampleNumberProtein, submission.getSampleNumberProtein());
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
    assertEquals(proteinWeight, submission.getMolecularWeight(), 0.0001);
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
  }
}
