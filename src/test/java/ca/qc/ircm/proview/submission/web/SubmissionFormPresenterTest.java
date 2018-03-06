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
import static ca.qc.ircm.proview.sample.SampleType.AGAROSE_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.BIOID_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.DRY;
import static ca.qc.ircm.proview.sample.SampleType.GEL;
import static ca.qc.ircm.proview.sample.SampleType.MAGNETIC_BEADS;
import static ca.qc.ircm.proview.sample.SampleType.SOLUTION;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.AVERAGE_MASS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COLORATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.COMMENT_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.CONTAMINANT_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DECOLORATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.ENRICHEMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXCLUSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_GOAL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPERIENCE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPLANATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.EXPLANATION_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILES_UPLOADER;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILE_FILENAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_CONTAMINANTS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FILL_STANDARDS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.FORMULA;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_IMAGE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GEL_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.GUIDELINES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.HIGH_RESOLUTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INACTIVE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INJECTION_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.INSTRUMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.LIGHT_SENSITIVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.MONOISOTOPIC_MASS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.OTHER_SOLVENT_NOTE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PLATE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.PROTEIN_WEIGHT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.REMOVE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_CONTAINER_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLES_PLATE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_NUMBER_PROTEIN;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_TYPE_WARNING;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAMPLE_VOLUME_BEADS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SAVE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SEPARATION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOLVENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SOURCE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARDS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COMMENT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STANDARD_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STORAGE_TEMPERATURE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.STRUCTURE_FILE;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TAXONOMY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.THICKNESS;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.TOXICITY;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.UPDATE_ERROR;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.errorMessage;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDate;
import static ca.qc.ircm.proview.web.WebConstants.ALREADY_EXISTS;
import static ca.qc.ircm.proview.web.WebConstants.BUTTON_SKIP_ROW;
import static ca.qc.ircm.proview.web.WebConstants.FIELD_NOTIFICATION;
import static ca.qc.ircm.proview.web.WebConstants.INVALID;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_INTEGER;
import static ca.qc.ircm.proview.web.WebConstants.INVALID_NUMBER;
import static ca.qc.ircm.proview.web.WebConstants.OUT_OF_RANGE;
import static ca.qc.ircm.proview.web.WebConstants.REQUIRED;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.files.web.GuidelinesWindow;
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
import ca.qc.ircm.proview.sample.SampleType;
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
import com.vaadin.server.CompositeErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.SerializableFunction;
import com.vaadin.server.StreamResource;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Provider;
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
  private Provider<GuidelinesWindow> guidelinesWindowProvider;
  @Mock
  private GuidelinesWindow guidelinesWindow;
  @Mock
  private DefaultMultiFileUpload filesUploader;
  @Captor
  private ArgumentCaptor<String> stringCaptor;
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
  private SampleType type = SOLUTION;
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
  private String sampleVolume = "21.5 μl";
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
  private String quantificationComment = "Heavy: Lys8, Arg10\nLight: None";
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
        plateService, authorizationService, guidelinesWindowProvider);
    design = new SubmissionFormDesign();
    view.design = design;
    view.plateComponent = mock(PlateComponent.class);
    view.filesUploader = filesUploader;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    when(guidelinesWindowProvider.get()).thenReturn(guidelinesWindow);
    when(plateService.nameAvailable(any())).thenReturn(true);
    plate = new Plate();
    plate.initWells();
    when(view.plateComponent.getValue()).thenReturn(plate);
  }

  private void setFields() {
    design.solutionSolvent.setValue(solutionSolvent);
    design.sampleName.setValue(sampleName);
    design.formula.setValue(formula);
    design.monoisotopicMass.setValue(String.valueOf(monoisotopicMass));
    design.averageMass.setValue(String.valueOf(averageMass));
    design.toxicity.setValue(toxicity);
    design.lightSensitive.setValue(lightSensitive);
    design.storageTemperature.setValue(storageTemperature);
    design.sampleContainerType.setValue(sampleContainerType);
    design.plateName.setValue(plateName);
    design.sampleCount.setValue(String.valueOf(sampleCount));
    setValuesInSamplesTable();
    plate.well(0, 0).setSample(new SubmissionSample(null, sampleName1));
    plate.well(1, 0).setSample(new SubmissionSample(null, sampleName2));
    design.experience.setValue(experience);
    design.experienceGoal.setValue(experienceGoal);
    design.taxonomy.setValue(taxonomy);
    design.proteinName.setValue(proteinName);
    design.proteinWeight.setValue(String.valueOf(proteinWeight));
    design.postTranslationModification.setValue(postTranslationModification);
    design.sampleQuantity.setValue(sampleQuantity);
    design.sampleVolume.setValue(String.valueOf(sampleVolume));
    design.standardCount.setValue(String.valueOf(standardsCount));
    setValuesInStandardsTable();
    design.contaminantCount.setValue(String.valueOf(contaminantsCount));
    setValuesInContaminantsTable();
    design.separation.setValue(gelSeparation);
    design.thickness.setValue(gelThickness);
    design.coloration.setValue(gelColoration);
    design.otherColoration.setValue(otherColoration);
    design.developmentTime.setValue(developmentTime);
    design.decoloration.setValue(decoloration);
    design.weightMarkerQuantity.setValue(String.valueOf(weightMarkerQuantity));
    design.proteinQuantity.setValue(proteinQuantity);
    design.digestion.setValue(digestion);
    design.usedProteolyticDigestionMethod.setValue(usedDigestion);
    design.otherProteolyticDigestionMethod.setValue(otherDigestion);
    design.injectionType.setValue(injectionType);
    design.source.setValue(source);
    design.proteinContent.setValue(proteinContent);
    design.instrument.setValue(instrument);
    design.proteinIdentification.setValue(proteinIdentification);
    design.proteinIdentificationLink.setValue(proteinIdentificationLink);
    design.quantification.setValue(quantification);
    design.quantificationComment.setValue(quantificationComment);
    design.highResolution.setValue(highResolution);
    design.acetonitrileSolvents.setValue(acetonitrileSolvents);
    design.methanolSolvents.setValue(methanolSolvents);
    design.chclSolvents.setValue(chclSolvents);
    design.otherSolvents.setValue(otherSolvents);
    design.otherSolvent.setValue(otherSolvent);
    design.comment.setValue(comment);
    design.explanation.setValue(explanation);
  }

  private <R extends Number> R convert(AbstractStringToNumberConverter<R> converter,
      TextField component) throws Exception {
    return converter.convertToModel(component.getValue(), new ValueContext(component))
        .getOrThrow(throwableFunction);
  }

  private void setValuesInSamplesTable() {
    List<SubmissionSample> samples = new ArrayList<>(dataProvider(design.samples).getItems());
    SubmissionSample sample = samples.get(0);
    sampleNameField1 = setValueInSamplesGrid(sample, sampleName1, SAMPLE_NAME);
    sampleNumberProteinField1 =
        setValueInSamplesGrid(sample, String.valueOf(sampleNumberProtein1), SAMPLE_NUMBER_PROTEIN);
    sampleProteinWeightField1 =
        setValueInSamplesGrid(sample, String.valueOf(proteinWeight1), PROTEIN_WEIGHT);
    sample = samples.get(1);
    sampleNameField2 = setValueInSamplesGrid(sample, sampleName2, SAMPLE_NAME);
    sampleNumberProteinField2 =
        setValueInSamplesGrid(sample, String.valueOf(sampleNumberProtein2), SAMPLE_NUMBER_PROTEIN);
    sampleProteinWeightField2 =
        setValueInSamplesGrid(sample, String.valueOf(proteinWeight2), PROTEIN_WEIGHT);
  }

  private TextField setValueInSamplesGrid(SubmissionSample sample, String value, String columnId) {
    TextField field =
        (TextField) design.samples.getColumn(columnId).getValueProvider().apply(sample);
    field.setValue(value);
    return field;
  }

  private void setValuesInStandardsTable() {
    List<Standard> standards = new ArrayList<>(dataProvider(design.standards).getItems());
    Standard standard = standards.get(0);
    standardNameField1 = setValueInStandardsGrid(standard, standardName1, STANDARD_NAME);
    standardQuantityField1 =
        setValueInStandardsGrid(standard, standardQuantity1, STANDARD_QUANTITY);
    setValueInStandardsGrid(standard, standardComment1, STANDARD_COMMENT);
    standard = standards.get(1);
    standardNameField2 = setValueInStandardsGrid(standard, standardName2, STANDARD_NAME);
    standardQuantityField2 =
        setValueInStandardsGrid(standard, standardQuantity2, STANDARD_QUANTITY);
    setValueInStandardsGrid(standard, standardComment2, STANDARD_COMMENT);
  }

  private TextField setValueInStandardsGrid(Standard standard, String value, String columnId) {
    TextField field =
        (TextField) design.standards.getColumn(columnId).getValueProvider().apply(standard);
    field.setValue(value);
    return field;
  }

  private void setValuesInContaminantsTable() {
    List<Contaminant> contaminants = new ArrayList<>(dataProvider(design.contaminants).getItems());
    Contaminant contaminant = contaminants.get(0);
    contaminantNameField1 =
        setValueInContaminantsGrid(contaminant, contaminantName1, CONTAMINANT_NAME);
    contaminantQuantityField1 =
        setValueInContaminantsGrid(contaminant, contaminantQuantity1, CONTAMINANT_QUANTITY);
    setValueInContaminantsGrid(contaminant, contaminantComment1, CONTAMINANT_COMMENT);
    contaminant = contaminants.get(1);
    contaminantNameField2 =
        setValueInContaminantsGrid(contaminant, contaminantName2, CONTAMINANT_NAME);
    contaminantQuantityField2 =
        setValueInContaminantsGrid(contaminant, contaminantQuantity2, CONTAMINANT_QUANTITY);
    setValueInContaminantsGrid(contaminant, contaminantComment2, CONTAMINANT_COMMENT);
  }

  private TextField setValueInContaminantsGrid(Contaminant contaminant, String value,
      String columnId) {
    TextField field =
        (TextField) design.contaminants.getColumn(columnId).getValueProvider().apply(contaminant);
    field.setValue(value);
    return field;
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
    submission.setQuantificationComment(quantificationComment);
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
    sample.setType(type);
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
    sample.setType(type);
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

    assertEquals(3, design.samples.getColumns().size());
    assertEquals(SAMPLE_NAME, design.samples.getColumns().get(0).getId());
    assertFalse(design.samples.getColumn(SAMPLE_NAME).isHidden());
    assertFalse(design.samples.getColumn(SAMPLE_NAME).isSortable());
    assertEquals(SAMPLE_NUMBER_PROTEIN, design.samples.getColumns().get(1).getId());
    assertTrue(design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).isHidden());
    assertFalse(design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).isSortable());
    assertEquals(PROTEIN_WEIGHT, design.samples.getColumns().get(2).getId());
    assertTrue(design.samples.getColumn(PROTEIN_WEIGHT).isHidden());
    assertFalse(design.samples.getColumn(PROTEIN_WEIGHT).isSortable());
  }

  @Test
  public void samplesTableColumns_IntactProtein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);

    assertEquals(3, design.samples.getColumns().size());
    assertEquals(SAMPLE_NAME, design.samples.getColumns().get(0).getId());
    assertFalse(design.samples.getColumn(SAMPLE_NAME).isHidden());
    assertFalse(design.samples.getColumn(SAMPLE_NAME).isSortable());
    assertEquals(SAMPLE_NUMBER_PROTEIN, design.samples.getColumns().get(1).getId());
    assertFalse(design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).isHidden());
    assertFalse(design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).isSortable());
    assertEquals(PROTEIN_WEIGHT, design.samples.getColumns().get(2).getId());
    assertFalse(design.samples.getColumn(PROTEIN_WEIGHT).isHidden());
    assertFalse(design.samples.getColumn(PROTEIN_WEIGHT).isSortable());
  }

  @Test
  public void standardsColumns() {
    presenter.init(view);

    assertEquals(3, design.standards.getColumns().size());
    assertEquals(STANDARD_NAME, design.standards.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(STANDARD_NAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(STANDARD_NAME).isSortable());
    {
      TextField field = (TextField) design.standards.getColumn(STANDARD_NAME).getValueProvider()
          .apply(new Standard());
      assertTrue(field.getStyleName().contains(STANDARD_NAME));
    }
    assertEquals(STANDARD_QUANTITY, design.standards.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(STANDARD_QUANTITY).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(STANDARD_QUANTITY).isSortable());
    {
      TextField field = (TextField) design.standards.getColumn(STANDARD_QUANTITY).getValueProvider()
          .apply(new Standard());
      assertTrue(field.getStyleName().contains(STANDARD_QUANTITY));
    }
    assertEquals(STANDARD_COMMENT, design.standards.getColumns().get(2).getId());
    assertTrue(containsInstanceOf(design.standards.getColumn(STANDARD_COMMENT).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.standards.getColumn(STANDARD_COMMENT).isSortable());
    {
      TextField field = (TextField) design.standards.getColumn(STANDARD_COMMENT).getValueProvider()
          .apply(new Standard());
      assertTrue(field.getStyleName().contains(STANDARD_COMMENT));
    }
  }

  @Test
  public void contaminantsColumns() {
    presenter.init(view);

    assertEquals(3, design.contaminants.getColumns().size());
    assertEquals(CONTAMINANT_NAME, design.contaminants.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.contaminants.getColumn(CONTAMINANT_NAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(CONTAMINANT_NAME).isSortable());
    {
      TextField field = (TextField) design.contaminants.getColumn(CONTAMINANT_NAME)
          .getValueProvider().apply(new Contaminant());
      assertTrue(field.getStyleName().contains(CONTAMINANT_NAME));
    }
    assertEquals(CONTAMINANT_QUANTITY, design.contaminants.getColumns().get(1).getId());
    assertTrue(
        containsInstanceOf(design.contaminants.getColumn(CONTAMINANT_QUANTITY).getExtensions(),
            ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(CONTAMINANT_QUANTITY).isSortable());
    {
      TextField field = (TextField) design.contaminants.getColumn(CONTAMINANT_QUANTITY)
          .getValueProvider().apply(new Contaminant());
      assertTrue(field.getStyleName().contains(CONTAMINANT_QUANTITY));
    }
    assertEquals(CONTAMINANT_COMMENT, design.contaminants.getColumns().get(2).getId());
    assertTrue(
        containsInstanceOf(design.contaminants.getColumn(CONTAMINANT_COMMENT).getExtensions(),
            ComponentRenderer.class));
    assertFalse(design.contaminants.getColumn(CONTAMINANT_COMMENT).isSortable());
    {
      TextField field = (TextField) design.contaminants.getColumn(CONTAMINANT_COMMENT)
          .getValueProvider().apply(new Contaminant());
      assertTrue(field.getStyleName().contains(CONTAMINANT_COMMENT));
    }
  }

  @Test
  public void filesColumns_ReadOnly() throws Throwable {
    presenter.init(view);
    presenter.setReadOnly(true);

    SubmissionFile file = new SubmissionFile("test.xlsx");
    file.setContent(filesContent1);
    assertEquals(2, design.files.getColumns().size());
    assertEquals(FILE_FILENAME, design.files.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.files.getColumn(FILE_FILENAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.files.getColumn(FILE_FILENAME).isHidden());
    assertFalse(design.files.getColumn(FILE_FILENAME).isSortable());
    Button download = (Button) design.files.getColumn(FILE_FILENAME).getValueProvider().apply(file);
    assertTrue(download.getStyleName().contains(FILE_FILENAME));
    assertEquals(file.getFilename(), download.getCaption());
    assertEquals(VaadinIcons.DOWNLOAD, download.getIcon());
    assertTrue(containsInstanceOf(download.getExtensions(), FileDownloader.class));
    FileDownloader fileDownloader = (FileDownloader) download.getExtensions().iterator().next();
    StreamResource fileResource = (StreamResource) fileDownloader.getFileDownloadResource();
    assertEquals(file.getFilename(), fileResource.getFilename());
    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    IOUtils.copy(fileResource.getStream().getStream(), fileOutput);
    assertArrayEquals(file.getContent(), fileOutput.toByteArray());
    assertEquals(REMOVE_FILE, design.files.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.files.getColumn(REMOVE_FILE).getExtensions(),
        ComponentRenderer.class));
    assertTrue(design.files.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.files.getColumn(REMOVE_FILE).isSortable());
    Button remove = (Button) design.files.getColumn(REMOVE_FILE).getValueProvider().apply(file);
    assertTrue(remove.getStyleName().contains(REMOVE_FILE));
    assertEquals(resources.message(FILES + "." + REMOVE_FILE), remove.getCaption());
  }

  @Test
  public void filesColumns() throws Throwable {
    presenter.init(view);

    SubmissionFile file = new SubmissionFile("test.xlsx");
    file.setContent(filesContent1);
    dataProvider(design.files).getItems().add(file);
    assertEquals(2, design.files.getColumns().size());
    assertEquals(FILE_FILENAME, design.files.getColumns().get(0).getId());
    assertTrue(containsInstanceOf(design.files.getColumn(FILE_FILENAME).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.files.getColumn(FILE_FILENAME).isHidden());
    assertFalse(design.files.getColumn(FILE_FILENAME).isSortable());
    Button download = (Button) design.files.getColumn(FILE_FILENAME).getValueProvider().apply(file);
    assertTrue(download.getStyleName().contains(FILE_FILENAME));
    assertEquals(file.getFilename(), download.getCaption());
    assertEquals(VaadinIcons.DOWNLOAD, download.getIcon());
    assertTrue(containsInstanceOf(download.getExtensions(), FileDownloader.class));
    FileDownloader fileDownloader = (FileDownloader) download.getExtensions().iterator().next();
    StreamResource fileResource = (StreamResource) fileDownloader.getFileDownloadResource();
    assertEquals(file.getFilename(), fileResource.getFilename());
    ByteArrayOutputStream fileOutput = new ByteArrayOutputStream();
    IOUtils.copy(fileResource.getStream().getStream(), fileOutput);
    assertArrayEquals(file.getContent(), fileOutput.toByteArray());
    assertEquals(REMOVE_FILE, design.files.getColumns().get(1).getId());
    assertTrue(containsInstanceOf(design.files.getColumn(REMOVE_FILE).getExtensions(),
        ComponentRenderer.class));
    assertFalse(design.files.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.files.getColumn(REMOVE_FILE).isSortable());
    Button remove = (Button) design.files.getColumn(REMOVE_FILE).getValueProvider().apply(file);
    assertTrue(remove.getStyleName().contains(REMOVE_FILE));
    assertEquals(resources.message(FILES + "." + REMOVE_FILE), remove.getCaption());
    remove.click();
    assertEquals(0, items(design.files).size());
  }

  @Test
  public void requiredFields() {
    presenter.init(view);

    assertTrue(design.service.isRequiredIndicatorVisible());
    assertTrue(design.sampleType.isRequiredIndicatorVisible());
    assertTrue(design.solutionSolvent.isRequiredIndicatorVisible());
    assertTrue(design.sampleCount.isRequiredIndicatorVisible());
    assertTrue(design.sampleName.isRequiredIndicatorVisible());
    assertTrue(design.formula.isRequiredIndicatorVisible());
    assertTrue(design.monoisotopicMass.isRequiredIndicatorVisible());
    assertFalse(design.averageMass.isRequiredIndicatorVisible());
    assertFalse(design.toxicity.isRequiredIndicatorVisible());
    assertFalse(design.lightSensitive.isRequiredIndicatorVisible());
    assertTrue(design.storageTemperature.isRequiredIndicatorVisible());
    assertTrue(design.sampleContainerType.isRequiredIndicatorVisible());
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    assertTrue(design.plateName.isRequiredIndicatorVisible());
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleNameTableField =
        (TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(firstSample);
    assertTrue(sampleNameTableField.isRequiredIndicatorVisible());
    TextField sampleNumberProteinTableField = (TextField) design.samples
        .getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider().apply(firstSample);
    assertTrue(sampleNumberProteinTableField.isRequiredIndicatorVisible());
    TextField sampleProteinWeightTableField =
        (TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(firstSample);
    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(design.experience.isRequiredIndicatorVisible());
    assertFalse(design.experienceGoal.isRequiredIndicatorVisible());
    assertTrue(design.taxonomy.isRequiredIndicatorVisible());
    assertFalse(design.proteinName.isRequiredIndicatorVisible());
    assertFalse(design.proteinWeight.isRequiredIndicatorVisible());
    assertFalse(design.postTranslationModification.isRequiredIndicatorVisible());
    assertTrue(design.sampleQuantity.isRequiredIndicatorVisible());
    assertTrue(design.sampleVolume.isRequiredIndicatorVisible());
    assertFalse(design.standardCount.isRequiredIndicatorVisible());
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standards);
    if (standardsDataProvider.getItems().size() < 1) {
      standardsDataProvider.getItems().add(new Standard());
    }
    Standard firstStandard = standardsDataProvider.getItems().iterator().next();
    TextField standardNameTableField = (TextField) design.standards.getColumn(STANDARD_NAME)
        .getValueProvider().apply(firstStandard);
    assertTrue(standardNameTableField.isRequiredIndicatorVisible());
    TextField standardQuantityTableField = (TextField) design.standards.getColumn(STANDARD_QUANTITY)
        .getValueProvider().apply(firstStandard);
    assertTrue(standardQuantityTableField.isRequiredIndicatorVisible());
    TextField standardCommentTableField = (TextField) design.standards.getColumn(STANDARD_COMMENT)
        .getValueProvider().apply(firstStandard);
    assertFalse(standardCommentTableField.isRequiredIndicatorVisible());
    assertFalse(design.contaminantCount.isRequiredIndicatorVisible());
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminants);
    if (contaminantsDataProvider.getItems().size() < 1) {
      contaminantsDataProvider.getItems().add(new Contaminant());
    }
    Contaminant firstContaminant = contaminantsDataProvider.getItems().iterator().next();
    TextField contaminantNameTableField = (TextField) design.contaminants
        .getColumn(CONTAMINANT_NAME).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantNameTableField.isRequiredIndicatorVisible());
    TextField contaminantQuantityTableField = (TextField) design.contaminants
        .getColumn(CONTAMINANT_QUANTITY).getValueProvider().apply(firstContaminant);
    assertTrue(contaminantQuantityTableField.isRequiredIndicatorVisible());
    TextField contaminantCommentTableField = (TextField) design.contaminants
        .getColumn(CONTAMINANT_COMMENT).getValueProvider().apply(firstContaminant);
    assertFalse(contaminantCommentTableField.isRequiredIndicatorVisible());
    assertTrue(design.separation.isRequiredIndicatorVisible());
    assertTrue(design.thickness.isRequiredIndicatorVisible());
    assertFalse(design.coloration.isRequiredIndicatorVisible());
    assertTrue(design.otherColoration.isRequiredIndicatorVisible());
    assertFalse(design.developmentTime.isRequiredIndicatorVisible());
    assertFalse(design.decoloration.isRequiredIndicatorVisible());
    assertFalse(design.weightMarkerQuantity.isRequiredIndicatorVisible());
    assertFalse(design.proteinQuantity.isRequiredIndicatorVisible());
    assertTrue(design.digestion.isRequiredIndicatorVisible());
    assertTrue(design.usedProteolyticDigestionMethod.isRequiredIndicatorVisible());
    assertTrue(design.otherProteolyticDigestionMethod.isRequiredIndicatorVisible());
    assertTrue(design.injectionType.isRequiredIndicatorVisible());
    assertTrue(design.source.isRequiredIndicatorVisible());
    assertTrue(design.proteinContent.isRequiredIndicatorVisible());
    assertFalse(design.instrument.isRequiredIndicatorVisible());
    assertTrue(design.proteinIdentification.isRequiredIndicatorVisible());
    assertTrue(design.proteinIdentificationLink.isRequiredIndicatorVisible());
    assertFalse(design.quantification.isRequiredIndicatorVisible());
    assertTrue(design.quantificationComment.isRequiredIndicatorVisible());
    assertTrue(design.highResolution.isRequiredIndicatorVisible());
    assertFalse(design.acetonitrileSolvents.isRequiredIndicatorVisible());
    assertFalse(design.methanolSolvents.isRequiredIndicatorVisible());
    assertFalse(design.chclSolvents.isRequiredIndicatorVisible());
    assertFalse(design.otherSolvents.isRequiredIndicatorVisible());
    assertTrue(design.otherSolvent.isRequiredIndicatorVisible());
  }

  @Test
  public void required_ProteinWeight_Lcmsms() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField =
        (TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(firstSample);
    design.service.setValue(LC_MS_MS); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertTrue(design.samples.getColumn(PROTEIN_WEIGHT).isHidden());
  }

  @Test
  public void required_ProteinWeight_IntactProtein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
    if (samplesDataProvider.getItems().size() < 1) {
      samplesDataProvider.getItems().add(new SubmissionSample());
    }
    SubmissionSample firstSample = samplesDataProvider.getItems().iterator().next();
    TextField sampleProteinWeightTableField =
        (TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(firstSample);
    design.service.setValue(INTACT_PROTEIN); // Force field update.

    assertTrue(sampleProteinWeightTableField.isRequiredIndicatorVisible());
    assertFalse(design.samples.getColumn(PROTEIN_WEIGHT).isHidden());
  }

  @Test
  public void service_Options() {
    presenter.init(view);

    assertEquals(Service.availables().size(), dataProvider(design.service).getItems().size());
    for (Service service : Service.availables()) {
      assertTrue(service.name(), dataProvider(design.service).getItems().contains(service));
    }
  }

  @Test
  public void service_DisabledOption() {
    Service service = Service.MALDI_MS;
    Submission submission = new Submission();
    submission.setService(service);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.service).getItems().contains(service));
    assertFalse(design.service.getItemEnabledProvider().test(service));
  }

  @Test
  public void gelTypeDisabled_Smallmolecule() {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);

    assertFalse(design.sampleType.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void gelTypeDisabled_Intactprotein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);

    assertFalse(design.sampleType.getItemEnabledProvider().test(GEL));
  }

  @Test
  public void bioidBeadsDisabled_Smallmolecule() {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);

    assertFalse(design.sampleType.getItemEnabledProvider().test(BIOID_BEADS));
  }

  @Test
  public void bioidBeadsDisabled_Intactprotein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);

    assertFalse(design.sampleType.getItemEnabledProvider().test(BIOID_BEADS));
  }

  @Test
  public void magneticBeadsDisabled_Smallmolecule() {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);

    assertFalse(design.sampleType.getItemEnabledProvider().test(MAGNETIC_BEADS));
  }

  @Test
  public void magneticBeadsDisabled_Intactprotein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);

    assertFalse(design.sampleType.getItemEnabledProvider().test(MAGNETIC_BEADS));
  }

  @Test
  public void agaroseBeadsDisabled_Smallmolecule() {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);

    assertFalse(design.sampleType.getItemEnabledProvider().test(AGAROSE_BEADS));
  }

  @Test
  public void agaroseBeadsDisabled_Intactprotein() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);

    assertFalse(design.sampleType.getItemEnabledProvider().test(AGAROSE_BEADS));
  }

  @Test
  public void digestion_RequiredText() {
    presenter.init(view);

    design.digestion.setValue(TRYPSIN);
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    design.digestion.setValue(DIGESTED);
    assertTrue(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    design.digestion.setValue(ProteolyticDigestion.OTHER);
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertTrue(design.otherProteolyticDigestionMethod.isVisible());
    assertTrue(design.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void source_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrumentSource.availables().size(),
        dataProvider(design.source).getItems().size());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.availables()) {
      assertTrue(source.name(), dataProvider(design.source).getItems().contains(source));
    }
  }

  @Test
  public void source_DisabledOption() {
    source = MassDetectionInstrumentSource.LDTD;
    Submission submission = new Submission();
    submission.setSource(source);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.source).getItems().contains(source));
    assertFalse(design.source.getItemEnabledProvider().test(source));
  }

  @Test
  public void instrument_Options() {
    presenter.init(view);

    assertEquals(MassDetectionInstrument.userChoices().size(),
        dataProvider(design.instrument).getItems().size());
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertTrue(instrument.name(),
          dataProvider(design.instrument).getItems().contains(instrument));
    }
  }

  @Test
  public void instrument_DisabledOption() {
    instrument = MassDetectionInstrument.TOF;
    Submission submission = new Submission();
    submission.setMassDetectionInstrument(instrument);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(dataProvider(design.instrument).getItems().contains(instrument));
    assertFalse(design.instrument.getItemEnabledProvider().test(instrument));
  }

  @Test
  public void proteinIdentification_Options() {
    presenter.init(view);

    assertEquals(ProteinIdentification.availables().size(),
        dataProvider(design.proteinIdentification).getItems().size());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertTrue(proteinIdentification.name(),
          dataProvider(design.proteinIdentification).getItems().contains(proteinIdentification));
    }
  }

  @Test
  public void proteinIdentification_DisabledOption() {
    proteinIdentification = ProteinIdentification.NCBINR;
    Submission submission = new Submission();
    submission.setProteinIdentification(proteinIdentification);

    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(
        dataProvider(design.proteinIdentification).getItems().contains(proteinIdentification));
    assertFalse(design.proteinIdentification.getItemEnabledProvider().test(proteinIdentification));
  }

  @Test
  public void proteinIdentification_RequiredText() {
    presenter.init(view);

    design.proteinIdentification.setValue(REFSEQ);
    assertFalse(design.proteinIdentificationLink.isVisible());
    design.proteinIdentification.setValue(UNIPROT);
    assertFalse(design.proteinIdentificationLink.isVisible());
    design.proteinIdentification.setValue(ProteinIdentification.OTHER);
    assertTrue(design.proteinIdentificationLink.isVisible());
  }

  @Test
  public void quantification_Options() {
    presenter.init(view);

    assertEquals(Quantification.values().length,
        dataProvider(design.quantification).getItems().size());
    for (Quantification quantification : Quantification.values()) {
      assertTrue(quantification.name(),
          dataProvider(design.quantification).getItems().contains(quantification));
    }
  }

  @Test
  public void quantificationComment_Visible() {
    presenter.init(view);

    design.quantification.setValue(null);
    assertFalse(design.quantificationComment.isVisible());
    design.quantification.setValue(Quantification.LABEL_FREE);
    assertFalse(design.quantificationComment.isVisible());
    design.quantification.setValue(Quantification.SILAC);
    assertTrue(design.quantificationComment.isVisible());
    design.quantification.setValue(Quantification.TMT);
    assertTrue(design.quantificationComment.isVisible());
  }

  @Test
  public void quantificationComment_SilacCaptions() {
    presenter.init(view);

    design.quantification.setValue(Quantification.SILAC);
    assertEquals(resources.message(QUANTIFICATION_COMMENT),
        design.quantificationComment.getCaption());
    assertEquals(resources.message(QUANTIFICATION_COMMENT + "." + EXAMPLE),
        design.quantificationComment.getPlaceholder());
  }

  @Test
  public void quantificationComment_TmtCaptions() {
    presenter.init(view);

    design.quantification.setValue(Quantification.TMT);
    assertEquals(resources.message(QUANTIFICATION_COMMENT + "." + Quantification.TMT.name()),
        design.quantificationComment.getCaption());
    assertEquals(
        resources.message(QUANTIFICATION_COMMENT + "." + EXAMPLE + "." + Quantification.TMT.name()),
        design.quantificationComment.getPlaceholder());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.sampleTypeWarning.getStyleName().contains(SAMPLE_TYPE_WARNING));
    assertTrue(design.inactiveWarning.getStyleName().contains(INACTIVE_WARNING));
    assertTrue(design.guidelines.getStyleName().contains(ValoTheme.BUTTON_FRIENDLY));
    assertTrue(design.guidelines.getStyleName().contains(GUIDELINES));
    assertTrue(design.servicePanel.getStyleName().contains(SERVICE_PANEL));
    assertTrue(design.servicePanel.getStyleName().contains(REQUIRED));
    assertTrue(design.service.getStyleName().contains(SERVICE));
    assertTrue(design.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(design.sampleType.getStyleName().contains(SAMPLE_TYPE));
    assertTrue(design.solutionSolvent.getStyleName().contains(SOLUTION_SOLVENT));
    assertTrue(design.sampleCount.getStyleName().contains(SAMPLE_COUNT));
    assertTrue(design.sampleName.getStyleName().contains(SAMPLE_NAME));
    assertTrue(design.formula.getStyleName().contains(FORMULA));
    assertTrue(design.monoisotopicMass.getStyleName().contains(MONOISOTOPIC_MASS));
    assertTrue(design.averageMass.getStyleName().contains(AVERAGE_MASS));
    assertTrue(design.toxicity.getStyleName().contains(TOXICITY));
    assertTrue(design.lightSensitive.getStyleName().contains(LIGHT_SENSITIVE));
    assertTrue(design.storageTemperature.getStyleName().contains(STORAGE_TEMPERATURE));
    assertTrue(design.sampleContainerType.getStyleName().contains(SAMPLES_CONTAINER_TYPE));
    assertTrue(design.plateName.getStyleName().contains(PLATE + "-" + PLATE_NAME));
    assertTrue(design.samplesLabel.getStyleName().contains(SAMPLES_LABEL));
    assertTrue(design.samples.getStyleName().contains(SAMPLES));
    assertTrue(design.fillSamples.getStyleName().contains(FILL_SAMPLES));
    assertTrue(design.fillSamples.getStyleName().contains(BUTTON_SKIP_ROW));
    verify(view.plateComponent).addStyleName(SAMPLES_PLATE);
    assertTrue(design.experiencePanel.getStyleName().contains(EXPERIENCE_PANEL));
    assertTrue(design.experience.getStyleName().contains(EXPERIENCE));
    assertTrue(design.experienceGoal.getStyleName().contains(EXPERIENCE_GOAL));
    assertTrue(design.taxonomy.getStyleName().contains(TAXONOMY));
    assertTrue(design.proteinName.getStyleName().contains(PROTEIN_NAME));
    assertTrue(design.proteinWeight.getStyleName().contains(PROTEIN_WEIGHT));
    assertTrue(
        design.postTranslationModification.getStyleName().contains(POST_TRANSLATION_MODIFICATION));
    assertTrue(design.sampleQuantity.getStyleName().contains(SAMPLE_QUANTITY));
    assertTrue(design.sampleVolume.getStyleName().contains(SAMPLE_VOLUME));
    assertTrue(design.standardsPanel.getStyleName().contains(STANDARDS_PANEL));
    assertTrue(design.standardCount.getStyleName().contains(STANDARD_COUNT));
    assertTrue(design.standards.getStyleName().contains(STANDARDS));
    assertTrue(design.fillStandards.getStyleName().contains(FILL_STANDARDS));
    assertTrue(design.fillStandards.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.contaminantsPanel.getStyleName().contains(CONTAMINANTS_PANEL));
    assertTrue(design.contaminantCount.getStyleName().contains(CONTAMINANT_COUNT));
    assertTrue(design.contaminants.getStyleName().contains(CONTAMINANTS));
    assertTrue(design.fillContaminants.getStyleName().contains(FILL_CONTAMINANTS));
    assertTrue(design.fillContaminants.getStyleName().contains(BUTTON_SKIP_ROW));
    assertTrue(design.gelPanel.getStyleName().contains(GEL_PANEL));
    assertTrue(design.separation.getStyleName().contains(SEPARATION));
    assertTrue(design.thickness.getStyleName().contains(THICKNESS));
    assertTrue(design.coloration.getStyleName().contains(COLORATION));
    assertTrue(design.otherColoration.getStyleName().contains(OTHER_COLORATION));
    assertTrue(design.developmentTime.getStyleName().contains(DEVELOPMENT_TIME));
    assertTrue(design.decoloration.getStyleName().contains(DECOLORATION));
    assertTrue(design.weightMarkerQuantity.getStyleName().contains(WEIGHT_MARKER_QUANTITY));
    assertTrue(design.proteinQuantity.getStyleName().contains(PROTEIN_QUANTITY));
    assertTrue(design.servicesPanel.getStyleName().contains(SERVICES_PANEL));
    assertTrue(design.digestion.getStyleName().contains(DIGESTION));
    assertTrue(design.usedProteolyticDigestionMethod.getStyleName().contains(USED_DIGESTION));
    assertTrue(design.otherProteolyticDigestionMethod.getStyleName().contains(OTHER_DIGESTION));
    assertTrue(design.enrichment.getStyleName().contains(ENRICHEMENT));
    assertTrue(design.exclusions.getStyleName().contains(EXCLUSIONS));
    assertTrue(design.injectionType.getStyleName().contains(INJECTION_TYPE));
    assertTrue(design.source.getStyleName().contains(SOURCE));
    assertTrue(design.proteinContent.getStyleName().contains(PROTEIN_CONTENT));
    assertTrue(design.instrument.getStyleName().contains(INSTRUMENT));
    assertTrue(design.proteinIdentification.getStyleName().contains(PROTEIN_IDENTIFICATION));
    assertTrue(
        design.proteinIdentificationLink.getStyleName().contains(PROTEIN_IDENTIFICATION_LINK));
    assertTrue(design.quantification.getStyleName().contains(QUANTIFICATION));
    assertTrue(design.quantificationComment.getStyleName().contains(QUANTIFICATION_COMMENT));
    assertTrue(design.highResolution.getStyleName().contains(HIGH_RESOLUTION));
    assertTrue(design.solventsLayout.getStyleName().contains(REQUIRED));
    assertTrue(design.acetonitrileSolvents.getStyleName()
        .contains(SOLVENTS + "-" + Solvent.ACETONITRILE.name()));
    assertTrue(
        design.methanolSolvents.getStyleName().contains(SOLVENTS + "-" + Solvent.METHANOL.name()));
    assertTrue(design.chclSolvents.getStyleName().contains(SOLVENTS + "-" + Solvent.CHCL3.name()));
    assertTrue(design.otherSolvents.getStyleName().contains(SOLVENTS + "-" + Solvent.OTHER.name()));
    assertTrue(design.otherSolvent.getStyleName().contains(OTHER_SOLVENT));
    assertTrue(design.otherSolvent.getStyleName().contains(ValoTheme.TEXTFIELD_SMALL));
    assertTrue(design.otherSolventNote.getStyleName().contains(OTHER_SOLVENT_NOTE));
    assertTrue(design.commentPanel.getStyleName().contains(COMMENT_PANEL));
    assertTrue(design.comment.getStyleName().contains(COMMENT));
    assertTrue(design.structureFile.getStyleName().contains(STRUCTURE_FILE));
    assertTrue(design.gelImageFile.getStyleName().contains(GEL_IMAGE_FILE));
    assertTrue(design.filesPanel.getStyleName().contains(FILES_PANEL));
    verify(view.filesUploader).addStyleName(FILES_UPLOADER);
    assertTrue(design.files.getStyleName().contains(FILES));
    assertTrue(design.explanationPanel.getStyleName().contains(EXPLANATION_PANEL));
    assertTrue(design.explanationPanel.getStyleName().contains(REQUIRED));
    assertTrue(design.explanation.getStyleName().contains(EXPLANATION));
    assertTrue(design.save.getStyleName().contains(SAVE));
    assertTrue(design.save.getStyleName().contains(ValoTheme.BUTTON_PRIMARY));
  }

  @Test
  public void captions() {
    presenter.init(view);

    assertEquals(resources.message(SAMPLE_TYPE_WARNING), design.sampleTypeWarning.getValue());
    assertEquals(resources.message(INACTIVE_WARNING), design.inactiveWarning.getValue());
    assertEquals(resources.message(GUIDELINES), design.guidelines.getCaption());
    assertEquals(resources.message(SERVICE), design.servicePanel.getCaption());
    assertEquals(null, design.service.getCaption());
    for (Service service : Service.availables()) {
      assertEquals(service.getLabel(locale),
          design.service.getItemCaptionGenerator().apply(service));
    }
    assertEquals(resources.message(SAMPLES_PANEL), design.samplesPanel.getCaption());
    assertEquals(resources.message(SAMPLE_TYPE), design.sampleType.getCaption());
    for (SampleType type : SampleType.values()) {
      assertEquals(type.getLabel(locale), design.sampleType.getItemCaptionGenerator().apply(type));
    }
    assertEquals(resources.message(SOLUTION_SOLVENT), design.solutionSolvent.getCaption());
    assertEquals(resources.message(SAMPLE_COUNT), design.sampleCount.getCaption());
    assertEquals(resources.message(SAMPLE_NAME), design.sampleName.getCaption());
    assertEquals(resources.message(FORMULA), design.formula.getCaption());
    assertEquals(resources.message(MONOISOTOPIC_MASS), design.monoisotopicMass.getCaption());
    assertEquals(resources.message(AVERAGE_MASS), design.averageMass.getCaption());
    assertEquals(resources.message(TOXICITY), design.toxicity.getCaption());
    assertEquals(resources.message(LIGHT_SENSITIVE), design.lightSensitive.getCaption());
    assertEquals(resources.message(STORAGE_TEMPERATURE), design.storageTemperature.getCaption());
    for (StorageTemperature storageTemperature : StorageTemperature.values()) {
      assertEquals(storageTemperature.getLabel(locale),
          design.storageTemperature.getItemCaptionGenerator().apply(storageTemperature));
    }
    assertEquals(resources.message(SAMPLES_CONTAINER_TYPE),
        design.sampleContainerType.getCaption());
    for (SampleContainerType containerType : SampleContainerType.values()) {
      assertEquals(containerType.getLabel(locale),
          design.sampleContainerType.getItemCaptionGenerator().apply(containerType));
    }
    assertEquals(resources.message(PLATE + "." + PLATE_NAME), design.plateName.getCaption());
    assertEquals(resources.message(SAMPLES_LABEL), design.samplesLabel.getCaption());
    assertEquals(null, design.samples.getCaption());
    assertEquals(resources.message(SAMPLE_NAME),
        design.samples.getColumn(SAMPLE_NAME).getCaption());
    assertEquals(resources.message(SAMPLE_NUMBER_PROTEIN),
        design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT),
        design.samples.getColumn(PROTEIN_WEIGHT).getCaption());
    assertEquals(resources.message(FILL_SAMPLES), design.fillSamples.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillSamples.getIcon());
    assertEquals(null, view.plateComponent.getCaption());
    assertEquals(resources.message(EXPERIENCE_PANEL), design.experiencePanel.getCaption());
    assertEquals(resources.message(EXPERIENCE), design.experience.getCaption());
    assertEquals(resources.message(EXPERIENCE_GOAL), design.experienceGoal.getCaption());
    assertEquals(resources.message(TAXONOMY), design.taxonomy.getCaption());
    assertEquals(resources.message(PROTEIN_NAME), design.proteinName.getCaption());
    assertEquals(resources.message(PROTEIN_WEIGHT), design.proteinWeight.getCaption());
    assertEquals(resources.message(POST_TRANSLATION_MODIFICATION),
        design.postTranslationModification.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY), design.sampleQuantity.getCaption());
    assertEquals(resources.message(SAMPLE_QUANTITY + "." + EXAMPLE),
        design.sampleQuantity.getPlaceholder());
    assertEquals(resources.message(SAMPLE_VOLUME), design.sampleVolume.getCaption());
    assertEquals(resources.message(SAMPLE_VOLUME + "." + EXAMPLE),
        design.sampleVolume.getPlaceholder());
    assertEquals(resources.message(STANDARDS_PANEL), design.standardsPanel.getCaption());
    assertEquals(resources.message(STANDARD_COUNT), design.standardCount.getCaption());
    assertEquals(null, design.standards.getCaption());
    assertEquals(resources.message(STANDARDS + "." + STANDARD_NAME),
        design.standards.getColumn(STANDARD_NAME).getCaption());
    assertEquals(resources.message(STANDARDS + "." + STANDARD_QUANTITY),
        design.standards.getColumn(STANDARD_QUANTITY).getCaption());
    assertEquals(resources.message(STANDARDS + "." + STANDARD_COMMENT),
        design.standards.getColumn(STANDARD_COMMENT).getCaption());
    assertEquals(resources.message(FILL_STANDARDS), design.fillStandards.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillStandards.getIcon());
    assertEquals(resources.message(CONTAMINANTS_PANEL), design.contaminantsPanel.getCaption());
    assertEquals(resources.message(CONTAMINANT_COUNT), design.contaminantCount.getCaption());
    assertEquals(null, design.contaminants.getCaption());
    assertEquals(resources.message(CONTAMINANTS + "." + CONTAMINANT_NAME),
        design.contaminants.getColumn(CONTAMINANT_NAME).getCaption());
    assertEquals(resources.message(CONTAMINANTS + "." + CONTAMINANT_QUANTITY),
        design.contaminants.getColumn(CONTAMINANT_QUANTITY).getCaption());
    assertEquals(resources.message(CONTAMINANTS + "." + CONTAMINANT_COMMENT),
        design.contaminants.getColumn(CONTAMINANT_COMMENT).getCaption());
    assertEquals(resources.message(FILL_CONTAMINANTS), design.fillContaminants.getCaption());
    assertEquals(VaadinIcons.ARROW_DOWN, design.fillContaminants.getIcon());
    assertEquals(resources.message(GEL_PANEL), design.gelPanel.getCaption());
    assertEquals(resources.message(SEPARATION), design.separation.getCaption());
    for (GelSeparation separation : GelSeparation.values()) {
      assertEquals(separation.getLabel(locale),
          design.separation.getItemCaptionGenerator().apply(separation));
    }
    assertEquals(resources.message(THICKNESS), design.thickness.getCaption());
    for (GelThickness thickness : GelThickness.values()) {
      assertEquals(thickness.getLabel(locale),
          design.thickness.getItemCaptionGenerator().apply(thickness));
    }
    assertEquals(resources.message(COLORATION), design.coloration.getCaption());
    assertEquals(GelColoration.getNullLabel(locale), design.coloration.getEmptySelectionCaption());
    for (GelColoration coloration : GelColoration.values()) {
      assertEquals(coloration.getLabel(locale),
          design.coloration.getItemCaptionGenerator().apply(coloration));
    }
    assertEquals(resources.message(OTHER_COLORATION), design.otherColoration.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME), design.developmentTime.getCaption());
    assertEquals(resources.message(DEVELOPMENT_TIME + "." + EXAMPLE),
        design.developmentTime.getPlaceholder());
    assertEquals(resources.message(DECOLORATION), design.decoloration.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY),
        design.weightMarkerQuantity.getCaption());
    assertEquals(resources.message(WEIGHT_MARKER_QUANTITY + "." + EXAMPLE),
        design.weightMarkerQuantity.getPlaceholder());
    assertEquals(resources.message(PROTEIN_QUANTITY), design.proteinQuantity.getCaption());
    assertEquals(resources.message(PROTEIN_QUANTITY + "." + EXAMPLE),
        design.proteinQuantity.getPlaceholder());
    assertEquals(resources.message(SERVICES_PANEL), design.servicesPanel.getCaption());
    assertEquals(resources.message(DIGESTION), design.digestion.getCaption());
    for (ProteolyticDigestion digestion : ProteolyticDigestion.values()) {
      assertEquals(digestion.getLabel(locale),
          design.digestion.getItemCaptionGenerator().apply(digestion));
    }
    assertEquals(resources.message(USED_DIGESTION),
        design.usedProteolyticDigestionMethod.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION),
        design.otherProteolyticDigestionMethod.getCaption());
    assertEquals(resources.message(OTHER_DIGESTION + ".note"),
        design.otherProteolyticDigestionMethodNote.getValue());
    assertEquals(resources.message(ENRICHEMENT), design.enrichment.getCaption());
    assertEquals(resources.message(ENRICHEMENT + ".value"), design.enrichment.getValue());
    assertEquals(resources.message(EXCLUSIONS), design.exclusions.getCaption());
    assertEquals(resources.message(EXCLUSIONS + ".value"), design.exclusions.getValue());
    assertEquals(resources.message(INJECTION_TYPE), design.injectionType.getCaption());
    for (InjectionType injectionType : InjectionType.values()) {
      assertEquals(injectionType.getLabel(locale),
          design.injectionType.getItemCaptionGenerator().apply(injectionType));
    }
    assertEquals(resources.message(SOURCE), design.source.getCaption());
    for (MassDetectionInstrumentSource source : MassDetectionInstrumentSource.values()) {
      assertEquals(source.getLabel(locale), design.source.getItemCaptionGenerator().apply(source));
    }
    assertEquals(resources.message(PROTEIN_CONTENT), design.proteinContent.getCaption());
    assertTrue(design.proteinContent.isCaptionAsHtml());
    for (ProteinContent proteinContent : ProteinContent.values()) {
      assertEquals(proteinContent.getLabel(locale),
          design.proteinContent.getItemCaptionGenerator().apply(proteinContent));
    }
    assertEquals(resources.message(INSTRUMENT), design.instrument.getCaption());
    assertEquals(MassDetectionInstrument.getNullLabel(locale),
        design.instrument.getItemCaptionGenerator().apply(null));
    for (MassDetectionInstrument instrument : MassDetectionInstrument.userChoices()) {
      assertEquals(instrument.getLabel(locale),
          design.instrument.getItemCaptionGenerator().apply(instrument));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION),
        design.proteinIdentification.getCaption());
    for (ProteinIdentification proteinIdentification : ProteinIdentification.availables()) {
      assertEquals(proteinIdentification.getLabel(locale),
          design.proteinIdentification.getItemCaptionGenerator().apply(proteinIdentification));
    }
    assertEquals(resources.message(PROTEIN_IDENTIFICATION_LINK),
        design.proteinIdentificationLink.getCaption());
    assertEquals(resources.message(QUANTIFICATION), design.quantification.getCaption());
    assertEquals(Quantification.getNullLabel(locale),
        design.quantification.getItemCaptionGenerator().apply(null));
    for (Quantification quantification : Quantification.values()) {
      assertEquals(quantification.getLabel(locale),
          design.quantification.getItemCaptionGenerator().apply(quantification));
    }
    assertEquals(resources.message(QUANTIFICATION_COMMENT),
        design.quantificationComment.getCaption());
    assertEquals(resources.message(QUANTIFICATION_COMMENT + "." + EXAMPLE),
        design.quantificationComment.getPlaceholder());
    assertEquals(resources.message(HIGH_RESOLUTION), design.highResolution.getCaption());
    for (boolean value : new boolean[] { false, true }) {
      assertEquals(resources.message(HIGH_RESOLUTION + "." + value),
          design.highResolution.getItemCaptionGenerator().apply(value));
    }
    assertEquals(resources.message(SOLVENTS), design.solventsLayout.getCaption());
    assertEquals(Solvent.ACETONITRILE.getLabel(locale), design.acetonitrileSolvents.getCaption());
    assertEquals(Solvent.METHANOL.getLabel(locale), design.methanolSolvents.getCaption());
    assertEquals(Solvent.CHCL3.getLabel(locale), design.chclSolvents.getCaption());
    assertEquals(Solvent.OTHER.getLabel(locale), design.otherSolvents.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT), design.otherSolvent.getCaption());
    assertEquals(resources.message(OTHER_SOLVENT_NOTE), design.otherSolventNote.getValue());
    assertEquals(resources.message(COMMENT_PANEL), design.commentPanel.getCaption());
    assertEquals(null, design.comment.getCaption());
    assertEquals(resources.message(STRUCTURE_FILE), design.structureFile.getValue());
    assertEquals(resources.message(GEL_IMAGE_FILE), design.gelImageFile.getValue());
    assertEquals(resources.message(FILES_PANEL), design.filesPanel.getCaption());
    verify(view.filesUploader).setUploadButtonCaption(resources.message(FILES_UPLOADER));
    assertEquals(null, design.files.getCaption());
    assertEquals(resources.message(FILES + "." + FILE_FILENAME),
        design.files.getColumn(FILE_FILENAME).getCaption());
    assertEquals(resources.message(FILES + "." + REMOVE_FILE),
        design.files.getColumn(REMOVE_FILE).getCaption());
    assertEquals(resources.message(EXPLANATION_PANEL), design.explanationPanel.getCaption());
    assertEquals(resources.message(SAVE), design.save.getCaption());
  }

  @Test
  public void readOnly_True() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(type);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertTrue(design.service.isReadOnly());
    assertTrue(design.sampleType.isReadOnly());
    assertTrue(design.solutionSolvent.isReadOnly());
    assertTrue(design.sampleCount.isReadOnly());
    assertTrue(design.sampleName.isReadOnly());
    assertTrue(design.formula.isReadOnly());
    assertTrue(design.monoisotopicMass.isReadOnly());
    assertTrue(design.averageMass.isReadOnly());
    assertTrue(design.toxicity.isReadOnly());
    assertTrue(design.lightSensitive.isReadOnly());
    assertTrue(design.storageTemperature.isReadOnly());
    assertTrue(design.sampleContainerType.isReadOnly());
    assertTrue(design.plateName.isReadOnly());
    SubmissionSample firstSample = dataProvider(design.samples).getItems().iterator().next();
    assertTrue(
        ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(firstSample))
            .isReadOnly());
    assertTrue(((TextField) design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertTrue(
        ((TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(firstSample))
            .isReadOnly());
    assertTrue(design.experience.isReadOnly());
    assertTrue(design.experienceGoal.isReadOnly());
    assertTrue(design.taxonomy.isReadOnly());
    assertTrue(design.proteinName.isReadOnly());
    assertTrue(design.proteinWeight.isReadOnly());
    assertTrue(design.postTranslationModification.isReadOnly());
    assertTrue(design.sampleQuantity.isReadOnly());
    assertTrue(design.sampleVolume.isReadOnly());
    assertTrue(design.standardCount.isReadOnly());
    Standard firstStandard = dataProvider(design.standards).getItems().iterator().next();
    assertTrue(((TextField) design.standards.getColumn(STANDARD_NAME).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) design.standards.getColumn(STANDARD_QUANTITY).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertTrue(((TextField) design.standards.getColumn(STANDARD_COMMENT).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertTrue(design.contaminantCount.isReadOnly());
    Contaminant firstContaminant = dataProvider(design.contaminants).getItems().iterator().next();
    assertTrue(((TextField) design.contaminants.getColumn(CONTAMINANT_NAME).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) design.contaminants.getColumn(CONTAMINANT_QUANTITY).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertTrue(((TextField) design.contaminants.getColumn(CONTAMINANT_COMMENT).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertTrue(design.separation.isReadOnly());
    assertTrue(design.thickness.isReadOnly());
    assertTrue(design.coloration.isReadOnly());
    assertTrue(design.otherColoration.isReadOnly());
    assertTrue(design.developmentTime.isReadOnly());
    assertTrue(design.decoloration.isReadOnly());
    assertTrue(design.weightMarkerQuantity.isReadOnly());
    assertTrue(design.proteinQuantity.isReadOnly());
    assertTrue(design.digestion.isReadOnly());
    assertTrue(design.usedProteolyticDigestionMethod.isReadOnly());
    assertTrue(design.otherProteolyticDigestionMethod.isReadOnly());
    assertTrue(design.injectionType.isReadOnly());
    assertTrue(design.source.isReadOnly());
    assertTrue(design.proteinContent.isReadOnly());
    assertTrue(design.instrument.isReadOnly());
    assertTrue(design.proteinIdentification.isReadOnly());
    assertTrue(design.proteinIdentificationLink.isReadOnly());
    assertTrue(design.quantification.isReadOnly());
    assertTrue(design.quantificationComment.isReadOnly());
    assertTrue(design.highResolution.isReadOnly());
    assertTrue(design.acetonitrileSolvents.isReadOnly());
    assertTrue(design.methanolSolvents.isReadOnly());
    assertTrue(design.chclSolvents.isReadOnly());
    assertTrue(design.otherSolvents.isReadOnly());
    assertTrue(design.otherSolvent.isReadOnly());
    assertTrue(design.comment.isReadOnly());
    assertTrue(design.files.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
  }

  @Test
  public void readOnly_False() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(type);
    sample.setOriginalContainer(new Tube());
    sample.setStandards(Arrays.asList(new Standard()));
    sample.setContaminants(Arrays.asList(new Contaminant()));
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.service.isReadOnly());
    assertFalse(design.sampleType.isReadOnly());
    assertFalse(design.solutionSolvent.isReadOnly());
    assertFalse(design.sampleCount.isReadOnly());
    assertFalse(design.sampleName.isReadOnly());
    assertFalse(design.formula.isReadOnly());
    assertFalse(design.monoisotopicMass.isReadOnly());
    assertFalse(design.averageMass.isReadOnly());
    assertFalse(design.toxicity.isReadOnly());
    assertFalse(design.lightSensitive.isReadOnly());
    assertFalse(design.storageTemperature.isReadOnly());
    assertFalse(design.sampleContainerType.isReadOnly());
    assertFalse(design.plateName.isReadOnly());
    SubmissionSample firstSample = dataProvider(design.samples).getItems().iterator().next();
    assertFalse(
        ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(firstSample))
            .isReadOnly());
    assertFalse(((TextField) design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider()
        .apply(firstSample)).isReadOnly());
    assertFalse(
        ((TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(firstSample))
            .isReadOnly());
    assertFalse(design.experience.isReadOnly());
    assertFalse(design.experienceGoal.isReadOnly());
    assertFalse(design.taxonomy.isReadOnly());
    assertFalse(design.proteinName.isReadOnly());
    assertFalse(design.proteinWeight.isReadOnly());
    assertFalse(design.postTranslationModification.isReadOnly());
    assertFalse(design.sampleQuantity.isReadOnly());
    assertFalse(design.sampleVolume.isReadOnly());
    assertFalse(design.standardCount.isReadOnly());
    Standard firstStandard = dataProvider(design.standards).getItems().iterator().next();
    assertFalse(((TextField) design.standards.getColumn(STANDARD_NAME).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) design.standards.getColumn(STANDARD_QUANTITY).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertFalse(((TextField) design.standards.getColumn(STANDARD_COMMENT).getValueProvider()
        .apply(firstStandard)).isReadOnly());
    assertFalse(design.contaminantCount.isReadOnly());
    Contaminant firstContaminant = dataProvider(design.contaminants).getItems().iterator().next();
    assertFalse(((TextField) design.contaminants.getColumn(CONTAMINANT_NAME).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) design.contaminants.getColumn(CONTAMINANT_QUANTITY).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertFalse(((TextField) design.contaminants.getColumn(CONTAMINANT_COMMENT).getValueProvider()
        .apply(firstContaminant)).isReadOnly());
    assertFalse(design.separation.isReadOnly());
    assertFalse(design.thickness.isReadOnly());
    assertFalse(design.coloration.isReadOnly());
    assertFalse(design.otherColoration.isReadOnly());
    assertFalse(design.developmentTime.isReadOnly());
    assertFalse(design.decoloration.isReadOnly());
    assertFalse(design.weightMarkerQuantity.isReadOnly());
    assertFalse(design.proteinQuantity.isReadOnly());
    assertFalse(design.digestion.isReadOnly());
    assertFalse(design.usedProteolyticDigestionMethod.isReadOnly());
    assertFalse(design.otherProteolyticDigestionMethod.isReadOnly());
    assertFalse(design.injectionType.isReadOnly());
    assertFalse(design.source.isReadOnly());
    assertFalse(design.proteinContent.isReadOnly());
    assertFalse(design.instrument.isReadOnly());
    assertFalse(design.proteinIdentification.isReadOnly());
    assertFalse(design.proteinIdentificationLink.isReadOnly());
    assertFalse(design.quantification.isReadOnly());
    assertFalse(design.quantificationComment.isReadOnly());
    assertFalse(design.highResolution.isReadOnly());
    assertFalse(design.acetonitrileSolvents.isReadOnly());
    assertFalse(design.methanolSolvents.isReadOnly());
    assertFalse(design.chclSolvents.isReadOnly());
    assertFalse(design.otherSolvents.isReadOnly());
    assertFalse(design.otherSolvent.isReadOnly());
    assertFalse(design.comment.isReadOnly());
    assertFalse(design.files.getColumn(REMOVE_FILE).isHidden());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
  }

  @Test
  public void readOnly_False_ForceUpdate() {
    Submission submission = new Submission();
    submission.setService(Service.LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(type);
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
    sample.setType(type);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_Solution() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_BioidBeads_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(BIOID_BEADS);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_BioidBeads() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(BIOID_BEADS);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_MagneticBeads_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(MAGNETIC_BEADS);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_MagneticBeads() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(MAGNETIC_BEADS);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_AgaroseBeads_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(AGAROSE_BEADS);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_AgaroseBeads() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(AGAROSE_BEADS);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_UsedDigestion() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);

    design.digestion.setValue(DIGESTED);

    assertTrue(design.usedProteolyticDigestionMethod.isVisible());
  }

  @Test
  public void visible_Lcmsms_OtherDigestion() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);

    design.digestion.setValue(ProteolyticDigestion.OTHER);

    assertTrue(design.otherProteolyticDigestionMethod.isVisible());
    assertTrue(design.otherProteolyticDigestionMethodNote.isVisible());
  }

  @Test
  public void visible_Lcmsms_Plate() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);

    design.sampleContainerType.setValue(WELL);

    assertTrue(design.plateName.isVisible());
    assertFalse(design.samplesLayout.isVisible());
    assertFalse(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertTrue(design.samplesPlateContainer.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_Dry() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(DRY);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(LC_MS_MS);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(GEL);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertTrue(design.separation.isVisible());
    assertTrue(design.thickness.isVisible());
    assertTrue(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertTrue(design.developmentTime.isVisible());
    assertTrue(design.decoloration.isVisible());
    assertTrue(design.weightMarkerQuantity.isVisible());
    assertTrue(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertTrue(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertTrue(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertTrue(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertTrue(design.separation.isVisible());
    assertTrue(design.thickness.isVisible());
    assertTrue(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertTrue(design.developmentTime.isVisible());
    assertTrue(design.decoloration.isVisible());
    assertTrue(design.weightMarkerQuantity.isVisible());
    assertTrue(design.proteinQuantity.isVisible());
    assertTrue(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertTrue(design.enrichment.isVisible());
    assertTrue(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertTrue(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertTrue(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertTrue(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertTrue(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Lcmsms_Gel_OtherColoration() {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);

    design.coloration.setValue(GelColoration.OTHER);

    assertTrue(design.otherColoration.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertTrue(design.solutionSolvent.isVisible());
    assertFalse(design.sampleCount.isVisible());
    assertTrue(design.sampleName.isVisible());
    assertTrue(design.formula.isVisible());
    assertTrue(design.monoisotopicMass.isVisible());
    assertTrue(design.averageMass.isVisible());
    assertTrue(design.toxicity.isVisible());
    assertTrue(design.lightSensitive.isVisible());
    assertTrue(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesLayout.isVisible());
    assertFalse(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experience.isVisible());
    assertFalse(design.experienceGoal.isVisible());
    assertFalse(design.taxonomy.isVisible());
    assertFalse(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertFalse(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertFalse(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertTrue(design.highResolution.isVisible());
    assertTrue(design.acetonitrileSolvents.isVisible());
    assertTrue(design.methanolSolvents.isVisible());
    assertTrue(design.chclSolvents.isVisible());
    assertTrue(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertTrue(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertTrue(design.solutionSolvent.isVisible());
    assertFalse(design.sampleCount.isVisible());
    assertTrue(design.sampleName.isVisible());
    assertTrue(design.formula.isVisible());
    assertTrue(design.monoisotopicMass.isVisible());
    assertTrue(design.averageMass.isVisible());
    assertTrue(design.toxicity.isVisible());
    assertTrue(design.lightSensitive.isVisible());
    assertTrue(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesLayout.isVisible());
    assertFalse(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experience.isVisible());
    assertFalse(design.experienceGoal.isVisible());
    assertFalse(design.taxonomy.isVisible());
    assertFalse(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertFalse(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertFalse(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertTrue(design.highResolution.isVisible());
    assertTrue(design.acetonitrileSolvents.isVisible());
    assertTrue(design.methanolSolvents.isVisible());
    assertTrue(design.chclSolvents.isVisible());
    assertTrue(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertTrue(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Solution_OtherSolvents() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    design.otherSolvents.setValue(true);

    assertTrue(design.otherSolvent.isVisible());
    assertTrue(design.otherSolventNote.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(SMALL_MOLECULE);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertFalse(design.sampleCount.isVisible());
    assertTrue(design.sampleName.isVisible());
    assertTrue(design.formula.isVisible());
    assertTrue(design.monoisotopicMass.isVisible());
    assertTrue(design.averageMass.isVisible());
    assertTrue(design.toxicity.isVisible());
    assertTrue(design.lightSensitive.isVisible());
    assertTrue(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesLayout.isVisible());
    assertFalse(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experience.isVisible());
    assertFalse(design.experienceGoal.isVisible());
    assertFalse(design.taxonomy.isVisible());
    assertFalse(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertFalse(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertFalse(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertTrue(design.highResolution.isVisible());
    assertTrue(design.acetonitrileSolvents.isVisible());
    assertTrue(design.methanolSolvents.isVisible());
    assertTrue(design.chclSolvents.isVisible());
    assertTrue(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertTrue(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Smallmolecule_Dry() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(DRY);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertFalse(design.sampleCount.isVisible());
    assertTrue(design.sampleName.isVisible());
    assertTrue(design.formula.isVisible());
    assertTrue(design.monoisotopicMass.isVisible());
    assertTrue(design.averageMass.isVisible());
    assertTrue(design.toxicity.isVisible());
    assertTrue(design.lightSensitive.isVisible());
    assertTrue(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertFalse(design.samplesLabel.isVisible());
    assertFalse(design.samplesLayout.isVisible());
    assertFalse(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertFalse(design.experience.isVisible());
    assertFalse(design.experienceGoal.isVisible());
    assertFalse(design.taxonomy.isVisible());
    assertFalse(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertFalse(design.postTranslationModification.isVisible());
    assertFalse(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertFalse(design.standardsPanel.isVisible());
    assertFalse(design.standardCount.isVisible());
    assertFalse(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertFalse(design.contaminantsPanel.isVisible());
    assertFalse(design.contaminantCount.isVisible());
    assertFalse(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertFalse(design.injectionType.isVisible());
    assertFalse(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertFalse(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertTrue(design.highResolution.isVisible());
    assertTrue(design.acetonitrileSolvents.isVisible());
    assertTrue(design.methanolSolvents.isVisible());
    assertTrue(design.chclSolvents.isVisible());
    assertTrue(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertTrue(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(SOLUTION);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertTrue(design.injectionType.isVisible());
    assertTrue(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Intactprotein_Solution() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertTrue(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertTrue(design.injectionType.isVisible());
    assertTrue(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry_ReadOnly() {
    Submission submission = new Submission();
    submission.setService(INTACT_PROTEIN);
    SubmissionSample sample = new SubmissionSample();
    sample.setType(DRY);
    sample.setOriginalContainer(new Tube());
    submission.setSamples(Arrays.asList(sample));
    presenter.init(view);
    presenter.setReadOnly(true);
    presenter.setValue(submission);

    assertFalse(design.sampleTypeWarning.isVisible());
    assertFalse(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertFalse(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertFalse(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertFalse(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertTrue(design.injectionType.isVisible());
    assertTrue(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertFalse(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void visible_Intactprotein_Dry() {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(DRY);

    assertTrue(design.sampleTypeWarning.isVisible());
    assertTrue(design.inactiveWarning.isVisible());
    assertTrue(design.service.isVisible());
    assertTrue(design.sampleType.isVisible());
    assertFalse(design.solutionSolvent.isVisible());
    assertTrue(design.sampleCount.isVisible());
    assertFalse(design.sampleName.isVisible());
    assertFalse(design.formula.isVisible());
    assertFalse(design.monoisotopicMass.isVisible());
    assertFalse(design.averageMass.isVisible());
    assertFalse(design.toxicity.isVisible());
    assertFalse(design.lightSensitive.isVisible());
    assertFalse(design.storageTemperature.isVisible());
    assertFalse(design.sampleContainerType.isVisible());
    assertFalse(design.plateName.isVisible());
    assertTrue(design.samplesLabel.isVisible());
    assertTrue(design.samplesLayout.isVisible());
    assertTrue(design.samples.isVisible());
    assertTrue(design.fillSamples.isVisible());
    assertFalse(design.samplesPlateContainer.isVisible());
    assertTrue(design.experience.isVisible());
    assertTrue(design.experienceGoal.isVisible());
    assertTrue(design.taxonomy.isVisible());
    assertTrue(design.proteinName.isVisible());
    assertFalse(design.proteinWeight.isVisible());
    assertTrue(design.postTranslationModification.isVisible());
    assertTrue(design.sampleQuantity.isVisible());
    assertFalse(design.sampleVolume.isVisible());
    assertTrue(design.standardsPanel.isVisible());
    assertTrue(design.standardCount.isVisible());
    assertTrue(design.standards.isVisible());
    assertTrue(design.fillStandards.isVisible());
    assertTrue(design.contaminantsPanel.isVisible());
    assertTrue(design.contaminantCount.isVisible());
    assertTrue(design.contaminants.isVisible());
    assertTrue(design.fillContaminants.isVisible());
    assertFalse(design.separation.isVisible());
    assertFalse(design.thickness.isVisible());
    assertFalse(design.coloration.isVisible());
    assertFalse(design.otherColoration.isVisible());
    assertFalse(design.developmentTime.isVisible());
    assertFalse(design.decoloration.isVisible());
    assertFalse(design.weightMarkerQuantity.isVisible());
    assertFalse(design.proteinQuantity.isVisible());
    assertFalse(design.digestion.isVisible());
    assertFalse(design.usedProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethod.isVisible());
    assertFalse(design.otherProteolyticDigestionMethodNote.isVisible());
    assertFalse(design.enrichment.isVisible());
    assertFalse(design.exclusions.isVisible());
    assertTrue(design.injectionType.isVisible());
    assertTrue(design.source.isVisible());
    assertFalse(design.proteinContent.isVisible());
    assertTrue(design.instrument.isVisible());
    assertFalse(design.proteinIdentification.isVisible());
    assertFalse(design.proteinIdentificationLink.isVisible());
    assertFalse(design.quantification.isVisible());
    assertFalse(design.quantificationComment.isVisible());
    assertFalse(design.highResolution.isVisible());
    assertFalse(design.acetonitrileSolvents.isVisible());
    assertFalse(design.methanolSolvents.isVisible());
    assertFalse(design.chclSolvents.isVisible());
    assertFalse(design.otherSolvents.isVisible());
    assertFalse(design.otherSolvent.isVisible());
    assertFalse(design.otherSolventNote.isVisible());
    assertFalse(design.explanationPanel.isVisible());
    assertTrue(design.buttons.isVisible());
    assertFalse(design.structureFile.isVisible());
    assertFalse(design.gelImageFile.isVisible());
  }

  @Test
  public void guidelines() throws Throwable {
    presenter.init(view);

    design.guidelines.click();

    verify(guidelinesWindowProvider).get();
    verify(guidelinesWindow).center();
    verify(view).addWindow(guidelinesWindow);
  }

  @Test
  public void sampleVolume_BioidBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(BIOID_BEADS);

    assertTrue(design.sampleVolume.isReadOnly());
    assertEquals(resources.message(SAMPLE_VOLUME_BEADS), design.sampleVolume.getValue());
  }

  @Test
  public void fillSamples_Lcmsms() throws Throwable {
    presenter.init(view);
    design.sampleCount.setValue("3");
    List<SubmissionSample> samples = items(design.samples);
    for (SubmissionSample sample : samples) {
      design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample);
      design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider().apply(sample);
      design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(sample);
    }
    ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(samples.get(0)))
        .setValue("sample-09");

    design.fillSamples.click();

    int count = 9;
    DecimalFormat format = new DecimalFormat("00");
    for (SubmissionSample sample : samples) {
      assertEquals("sample-" + format.format(count++),
          ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample))
              .getValue());
    }
  }

  @Test
  public void fillSamples_IntactProtein() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleCount.setValue("3");
    List<SubmissionSample> samples = items(design.samples);
    for (SubmissionSample sample : samples) {
      design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample);
      design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider().apply(sample);
      design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(sample);
    }
    ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(samples.get(0)))
        .setValue("sample-09");
    ((TextField) design.samples.getColumn(SAMPLE_NUMBER_PROTEIN).getValueProvider()
        .apply(samples.get(0))).setValue("10 proteins");
    ((TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(samples.get(0)))
        .setValue("200 MW");

    design.fillSamples.click();

    int count = 9;
    DecimalFormat format = new DecimalFormat("00");
    for (SubmissionSample sample : samples) {
      assertEquals("sample-" + format.format(count++),
          ((TextField) design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample))
              .getValue());
      assertEquals("10 proteins", ((TextField) design.samples.getColumn(SAMPLE_NUMBER_PROTEIN)
          .getValueProvider().apply(sample)).getValue());
      assertEquals("200 MW",
          ((TextField) design.samples.getColumn(PROTEIN_WEIGHT).getValueProvider().apply(sample))
              .getValue());
    }
  }

  @Test
  public void fillStandards() throws Throwable {
    presenter.init(view);
    design.standardCount.setValue("3");
    List<Standard> standards = items(design.standards);
    for (Standard standard : standards) {
      design.standards.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
      design.standards.getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
      design.standards.getColumn(STANDARD_COMMENT).getValueProvider().apply(standard);
    }
    ((TextField) design.standards.getColumn(STANDARD_NAME).getValueProvider()
        .apply(standards.get(0))).setValue("std-09");
    ((TextField) design.standards.getColumn(STANDARD_QUANTITY).getValueProvider()
        .apply(standards.get(0))).setValue("10 ug");
    ((TextField) design.standards.getColumn(STANDARD_COMMENT).getValueProvider()
        .apply(standards.get(0))).setValue("test_comment");

    design.fillStandards.click();

    for (Standard standard : standards) {
      assertEquals("std-09",
          ((TextField) design.standards.getColumn(STANDARD_NAME).getValueProvider().apply(standard))
              .getValue());
      assertEquals("10 ug", ((TextField) design.standards.getColumn(STANDARD_QUANTITY)
          .getValueProvider().apply(standard)).getValue());
      assertEquals("test_comment", ((TextField) design.standards.getColumn(STANDARD_COMMENT)
          .getValueProvider().apply(standard)).getValue());
    }
  }

  @Test
  public void fillContaminants() throws Throwable {
    presenter.init(view);
    design.contaminantCount.setValue("3");
    List<Contaminant> contaminants = items(design.contaminants);
    for (Contaminant contaminant : contaminants) {
      design.contaminants.getColumn(CONTAMINANT_NAME).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(CONTAMINANT_QUANTITY).getValueProvider().apply(contaminant);
      design.contaminants.getColumn(CONTAMINANT_COMMENT).getValueProvider().apply(contaminant);
    }
    ((TextField) design.contaminants.getColumn(CONTAMINANT_NAME).getValueProvider()
        .apply(contaminants.get(0))).setValue("cont-09");
    ((TextField) design.contaminants.getColumn(CONTAMINANT_QUANTITY).getValueProvider()
        .apply(contaminants.get(0))).setValue("10 ug");
    ((TextField) design.contaminants.getColumn(CONTAMINANT_COMMENT).getValueProvider()
        .apply(contaminants.get(0))).setValue("test_comment");

    design.fillContaminants.click();

    for (Contaminant contaminant : contaminants) {
      assertEquals("cont-09", ((TextField) design.contaminants.getColumn(CONTAMINANT_NAME)
          .getValueProvider().apply(contaminant)).getValue());
      assertEquals("10 ug", ((TextField) design.contaminants.getColumn(CONTAMINANT_QUANTITY)
          .getValueProvider().apply(contaminant)).getValue());
      assertEquals("test_comment", ((TextField) design.contaminants.getColumn(CONTAMINANT_COMMENT)
          .getValueProvider().apply(contaminant)).getValue());
    }
  }

  @Test
  public void save_MissingService() throws Throwable {
    presenter.init(view);
    design.service.setValue(null);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.service.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingType() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(null);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_SmallMoleculeGel() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    uploadFiles();
    design.service.setValue(SMALL_MOLECULE);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_SmallMoleculeBioidBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(BIOID_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(SMALL_MOLECULE);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_SmallMoleculeMagneticBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(MAGNETIC_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(SMALL_MOLECULE);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_SmallMoleculeAgaroseBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(AGAROSE_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(SMALL_MOLECULE);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_IntactProteinGel() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    uploadFiles();
    design.service.setValue(INTACT_PROTEIN);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_IntactProteinBioidBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(BIOID_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(INTACT_PROTEIN);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_IntactProteinMagneticBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(MAGNETIC_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(INTACT_PROTEIN);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_IntactProteinAgaroseBeads() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(AGAROSE_BEADS);
    setFields();
    uploadFiles();
    design.service.setValue(INTACT_PROTEIN);

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID)),
        design.sampleType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSolutionSolvent() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.solutionSolvent.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.solutionSolvent.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleCount.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleCount.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.sampleCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowOneSampleCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleCount.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        design.sampleCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxSampleCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleCount.setValue("200000");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 1, 200)),
        design.sampleCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleCount.setValue("1.3");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.sampleCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleName() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.sampleName.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleName.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleName() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();
    when(submissionSampleService.exists(any())).thenReturn(true);

    design.save.click();

    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS, sampleName)),
        design.sampleName.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingFormula() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.formula.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.formula.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.monoisotopicMass.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.monoisotopicMass.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.monoisotopicMass.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.monoisotopicMass.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroMonoisotopicMass() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.monoisotopicMass.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.monoisotopicMass.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidAverageMass() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.averageMass.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.averageMass.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroAverageMass() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.averageMass.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.averageMass.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStorageTemperature() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.storageTemperature.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.storageTemperature.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingPlateName() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    design.plateName.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.plateName.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateName() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();
    when(plateService.nameAvailable(any())).thenReturn(false);

    design.save.click();

    verify(plateService, atLeastOnce()).nameAvailable(plateName);
    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(ALREADY_EXISTS)),
        design.plateName.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNames_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    sampleNameField1.setValue("");
    uploadFiles();
    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleNames_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);

    design.save.click();

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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    sampleNameField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsSampleNames_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);

    design.save.click();

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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    sampleNameField2.setValue(sampleName1);
    uploadFiles();

    design.save.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(SAMPLE_NAME + ".duplicate", sampleName1)),
        sampleNameField2.getComponentError().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    plate.well(0, 0).setSample(new SubmissionSample(null, ""));
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLES + ".missing", sampleCount)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateSampleNames_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    when(submissionSampleService.exists(sampleName1)).thenReturn(true);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(
        new CompositeErrorMessage(
            new UserError(resources.message(SAMPLE_NAME + ".exists", sampleName1)))
                .getFormattedHtmlMessage(),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    plate.well(1, 0).setSample(new SubmissionSample(null, ""));
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLES + ".missing", sampleCount)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_ExistsPlateSampleNames_2() throws Throwable {
    presenter.init(view);
    when(submissionSampleService.exists(sampleName2)).thenReturn(true);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(
        new CompositeErrorMessage(
            new UserError(resources.message(SAMPLE_NAME + ".exists", sampleName2)))
                .getFormattedHtmlMessage(),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DuplicatePlateSampleNames() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    plate.well(1, 0).setSample(new SubmissionSample(null, sampleName1));
    uploadFiles();

    design.save.click();

    verify(view, atLeastOnce()).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    verify(view.plateComponent, atLeastOnce()).setComponentError(errorMessageCaptor.capture());
    assertEquals(errorMessage(resources.message(SAMPLE_NAME + ".duplicate", sampleName1)),
        errorMessageCaptor.getValue().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField1.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField1.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleNumberProtein1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField1.setValue("1.2");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField2.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField2.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleSampleNumberProtein2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleNumberProteinField2.setValue("1.2");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        sampleNumberProteinField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinWeight1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField1.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight1() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField1.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinWeight2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField2.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight2() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    sampleProteinWeightField2.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(sampleProteinWeightField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingExperience() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.experience.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.experience.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingTaxonomy() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.taxonomy.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.taxonomy.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidProteinWeight() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.proteinWeight.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.proteinWeight.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroProteinWeight() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.proteinWeight.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertNotNull(design.proteinWeight.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleQuantity() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleQuantity.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleQuantity.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSampleVolume() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleVolume.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.sampleVolume.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.standardCount.setValue("");
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_InvalidStandardCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.standardCount.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroStandardCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.standardCount.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxStandardCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.standardCount.setValue("200");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.standardCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleStandardCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.standardCount.setValue("1.2");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.standardCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    standardNameField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardName_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    standardNameField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    standardQuantityField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingStandardQuantity_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    standardQuantityField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        standardQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.contaminantCount.setValue("");
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_InvalidContaminantCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.contaminantCount.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.contaminantCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_BelowZeroContaminantCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.contaminantCount.setValue("-1");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.contaminantCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_AboveMaxContaminantCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.contaminantCount.setValue("200");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(OUT_OF_RANGE, 0, 10)),
        design.contaminantCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_DoubleContaminantCount() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.contaminantCount.setValue("1.2");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_INTEGER)),
        design.contaminantCount.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantName_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    contaminantNameField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantName_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    contaminantNameField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantNameField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantQuantity_1() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    contaminantQuantityField1.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField1.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingContaminantQuantity_2() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    contaminantQuantityField2.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        contaminantQuantityField2.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingGelSeparation() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    design.separation.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.separation.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingGelThickness() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    design.thickness.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.thickness.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherGelColoration() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    design.coloration.setValue(GelColoration.OTHER);
    design.otherColoration.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherColoration.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_InvalidWeightMarkerQuantity() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    design.weightMarkerQuantity.setValue("a");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(INVALID_NUMBER)),
        design.weightMarkerQuantity.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingDigestion() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.digestion.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.digestion.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingUsedDigestion() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.digestion.setValue(DIGESTED);
    design.usedProteolyticDigestionMethod.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.usedProteolyticDigestionMethod.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherDigestion() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.digestion.setValue(ProteolyticDigestion.OTHER);
    design.otherProteolyticDigestionMethod.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherProteolyticDigestionMethod.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingInjectionType() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    design.injectionType.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.injectionType.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSource() throws Throwable {
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    design.source.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.source.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinContent() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.proteinContent.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinContent.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingInstrument() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.instrument.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(stringCaptor.capture());
    verify(submissionService).insert(any());
  }

  @Test
  public void save_MissingProteinIdentification() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.proteinIdentification.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinIdentification.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingProteinIdentificationLink() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.proteinIdentification.setValue(ProteinIdentification.OTHER);
    design.proteinIdentificationLink.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.proteinIdentificationLink.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingQuantificationComment_Silac() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.quantification.setValue(Quantification.SILAC);
    design.quantificationComment.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.quantificationComment.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingQuantificationComment_Tmt() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.quantification.setValue(Quantification.TMT);
    design.quantificationComment.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.quantificationComment.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingHighResolution() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.highResolution.setValue(null);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.highResolution.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingSolvents() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.acetonitrileSolvents.setValue(false);
    design.methanolSolvents.setValue(false);
    design.chclSolvents.setValue(false);
    design.otherSolvents.setValue(false);
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(resources.message(SOLVENTS + "." + REQUIRED)),
        design.solventsLayout.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingOtherSolvent() throws Throwable {
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.otherSolvents.setValue(true);
    design.otherSolvent.setValue("");
    uploadFiles();

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.otherSolvent.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).insert(any());
  }

  @Test
  public void save_MissingExplanation() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 147L);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    List<SubmissionSample> samples = new ArrayList<>(dataProvider(design.samples).getItems());
    samples.forEach(sample -> {
      design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample);
    });
    design.explanation.setValue("");

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(generalResources.message(FIELD_NOTIFICATION), stringCaptor.getValue());
    assertEquals(errorMessage(generalResources.message(REQUIRED)),
        design.explanation.getErrorMessage().getFormattedHtmlMessage());
    verify(submissionService, never()).forceUpdate(any(), any());
  }

  @Test
  public void save_Lcmsms_Solution() throws Throwable {
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.digestion.setValue(digestion);
    design.otherProteolyticDigestionMethod.setValue(otherDigestion);
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    final SampleType type = DRY;
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
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
    assertEquals(type, sample.getType());
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
    final SampleType type = DRY;
    presenter.init(view);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
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
    assertEquals(type, sample.getType());
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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(GEL, sample.getType());
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
    assertEquals(GEL, sample.getType());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(proteinWeight, sample.getMolecularWeight(), 0.0001);
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(GEL);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(GEL, sample.getType());
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
    assertEquals(GEL, sample.getType());
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
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
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
    assertEquals(null, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
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
    final SampleType type = DRY;
    presenter.init(view);
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(SMALL_MOLECULE, submission.getService());
    assertEquals(null, submission.getTaxonomy());
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
    assertEquals(null, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(null, sample.getVolume());
    assertEquals(null, sample.getQuantity());
    assertEquals(null, sample.getNumberProtein());
    assertEquals(null, sample.getMolecularWeight());
    assertEquals(null, sample.getOriginalContainer());
    assertTrue(sample.getStandards() == null || sample.getStandards().isEmpty());
    assertEquals(null, sample.getStatus());
    assertEquals(null, sample.getSubmission());
    assertTrue(sample.getContaminants() == null || sample.getContaminants().isEmpty());
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
    design.service.setValue(SMALL_MOLECULE);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

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
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(INTACT_PROTEIN, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(null, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    final SampleType type = DRY;
    presenter.init(view);
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).insert(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals(null, submission.getId());
    assertEquals(INTACT_PROTEIN, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(null, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
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
    assertEquals(type, sample.getType());
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
    design.service.setValue(INTACT_PROTEIN);
    design.sampleType.setValue(type);
    setFields();
    design.sampleContainerType.setValue(WELL);
    uploadFiles();

    design.save.click();

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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).update(submissionCaptor.capture());
    submission = submissionCaptor.getValue();
    assertEquals((Long) 36L, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
  public void save_UpdateNoChange() throws Throwable {
    Submission database = entityManager.find(Submission.class, 36L);
    database.setProteinIdentification(REFSEQ);
    when(submissionSampleService.exists(any())).thenReturn(true);
    when(submissionService.get(any())).thenReturn(database);
    presenter.init(view);
    presenter.setValue(database);
    database.getSamples()
        .forEach(sample -> design.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample));
    database.getSamples().stream().flatMap(sample -> sample.getStandards().stream())
        .forEach(standard -> {
          design.standards.getColumn(STANDARD_NAME).getValueProvider().apply(standard);
          design.standards.getColumn(STANDARD_QUANTITY).getValueProvider().apply(standard);
          design.standards.getColumn(STANDARD_COMMENT).getValueProvider().apply(standard);
        });
    database.getSamples().stream().flatMap(sample -> sample.getContaminants().stream())
        .forEach(contaminant -> {
          design.contaminants.getColumn(CONTAMINANT_NAME).getValueProvider().apply(contaminant);
          design.contaminants.getColumn(CONTAMINANT_QUANTITY).getValueProvider().apply(contaminant);
          design.contaminants.getColumn(CONTAMINANT_COMMENT).getValueProvider().apply(contaminant);
        });

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionService).update(submissionCaptor.capture());
    Submission submission = submissionCaptor.getValue();
    assertEquals((Long) 36L, submission.getId());
    assertEquals(database.getService(), submission.getService());
    assertEquals(database.getTaxonomy(), submission.getTaxonomy());
    assertEquals(database.getExperience(), submission.getExperience());
    assertEquals(database.getGoal(), submission.getGoal());
    assertEquals(database.getMassDetectionInstrument(), submission.getMassDetectionInstrument());
    assertEquals(database.getSource(), submission.getSource());
    assertEquals(database.getProteolyticDigestionMethod(),
        submission.getProteolyticDigestionMethod());
    assertEquals(database.getUsedProteolyticDigestionMethod(),
        submission.getUsedProteolyticDigestionMethod());
    assertEquals(database.getOtherProteolyticDigestionMethod(),
        submission.getOtherProteolyticDigestionMethod());
    assertEquals(database.getProteinIdentification(), submission.getProteinIdentification());
    assertEquals(database.getProteinIdentificationLink(),
        submission.getProteinIdentificationLink());
    assertEquals(database.getEnrichmentType(), submission.getEnrichmentType());
    assertEquals(database.isLowResolution(), submission.isLowResolution());
    assertEquals(database.isHighResolution(), submission.isHighResolution());
    assertEquals(database.isMsms(), submission.isMsms());
    assertEquals(database.isExactMsms(), submission.isExactMsms());
    assertEquals(database.getMudPitFraction(), submission.getMudPitFraction());
    assertEquals(database.getProteinContent(), submission.getProteinContent());
    assertEquals(database.getProtein(), submission.getProtein());
    assertEquals(database.getPostTranslationModification(),
        submission.getPostTranslationModification());
    assertEquals(database.getSeparation(), submission.getSeparation());
    assertEquals(database.getThickness(), submission.getThickness());
    assertEquals(database.getColoration(), submission.getColoration());
    assertEquals(database.getOtherColoration(), submission.getOtherColoration());
    assertEquals(database.getDevelopmentTime(), submission.getDevelopmentTime());
    assertEquals(database.isDecoloration(), submission.isDecoloration());
    assertEquals(database.getWeightMarkerQuantity(), submission.getWeightMarkerQuantity());
    assertEquals(database.getProteinQuantity(), submission.getProteinQuantity());
    assertEquals(database.getFormula(), submission.getFormula());
    assertEquals(database.getMonoisotopicMass(), submission.getMonoisotopicMass());
    assertEquals(database.getAverageMass(), submission.getAverageMass());
    assertEquals(database.getSolutionSolvent(), submission.getSolutionSolvent());
    assertEquals(database.getSolvents(), submission.getSolvents());
    assertEquals(database.getOtherSolvent(), submission.getOtherSolvent());
    assertEquals(database.getToxicity(), submission.getToxicity());
    assertEquals(database.isLightSensitive(), submission.isLightSensitive());
    assertEquals(database.getStorageTemperature(), submission.getStorageTemperature());
    assertEquals(database.getQuantification(), submission.getQuantification());
    assertEquals(database.getQuantificationComment(), submission.getQuantificationComment());
    assertEquals(database.getComment(), submission.getComment());
    assertEquals(database.getSubmissionDate(), submission.getSubmissionDate());
    assertEquals(database.getPrice(), submission.getPrice());
    assertEquals(database.getAdditionalPrice(), submission.getAdditionalPrice());
    assertEquals(database.getUser().getId(), submission.getUser().getId());
    assertEquals(database.getLaboratory().getId(), submission.getLaboratory().getId());
    assertNotNull(submission.getSamples());
    assertEquals(database.getSamples().size(), submission.getSamples().size());
    for (int i = 0; i < database.getSamples().size(); i++) {
      SubmissionSample dsample = database.getSamples().get(i);
      SubmissionSample sample = submission.getSamples().get(i);
      assertEquals(dsample.getId(), sample.getId());
      assertEquals(dsample.getName(), sample.getName());
      assertEquals(dsample.getType(), sample.getType());
      assertEquals(dsample.getVolume(), sample.getVolume());
      assertEquals(dsample.getQuantity(), sample.getQuantity());
      assertEquals(dsample.getNumberProtein(), sample.getNumberProtein());
      assertEquals(dsample.getMolecularWeight(), sample.getMolecularWeight());
      assertNotNull(sample.getOriginalContainer());
      assertEquals(dsample.getOriginalContainer().getId(), sample.getOriginalContainer().getId());
      assertEquals(dsample.getOriginalContainer().getType(),
          sample.getOriginalContainer().getType());
      assertEquals(dsample.getOriginalContainer().getName(),
          sample.getOriginalContainer().getName());
      assertEquals(dsample.getStandards().size(), sample.getStandards().size());
      assertEquals(dsample.getStatus(), sample.getStatus());
      assertEquals(submission, sample.getSubmission());
      for (int j = 0; j < dsample.getStandards().size(); j++) {
        Standard dstandard = dsample.getStandards().get(j);
        Standard standard = sample.getStandards().get(j);
        assertEquals(dstandard.getName(), standard.getName());
        assertEquals(dstandard.getQuantity(), standard.getQuantity());
        assertEquals(dstandard.getComment(), standard.getComment());
      }
      for (int j = 0; j < dsample.getContaminants().size(); j++) {
        Contaminant dcontaminant = dsample.getContaminants().get(j);
        Contaminant contaminant = sample.getContaminants().get(j);
        assertEquals(dcontaminant.getName(), contaminant.getName());
        assertEquals(dcontaminant.getQuantity(), contaminant.getQuantity());
        assertEquals(dcontaminant.getComment(), contaminant.getComment());
      }
    }
    assertEquals(database.getFiles().size(), submission.getFiles().size());
    for (int i = 0; i < database.getFiles().size(); i++) {
      SubmissionFile dfile = database.getFiles().get(i);
      SubmissionFile file = submission.getFiles().get(i);
      assertEquals(dfile.getFilename(), file.getFilename());
      assertArrayEquals(dfile.getContent(), file.getContent());
    }
    verify(view).showTrayNotification(resources.message(SAVE + ".done", database.getExperience()));
    verify(view).navigateTo(SubmissionsView.VIEW_NAME);
  }

  @Test
  public void save_ForceUpdate() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 147L);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    presenter.setValue(submission);
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();

    design.save.click();

    verify(view, never()).showError(any());
    verify(view, never()).showWarning(any());
    verify(submissionSampleService, atLeastOnce()).exists(sampleName1);
    verify(submissionSampleService, atLeastOnce()).exists(sampleName2);
    verify(submissionService).forceUpdate(submissionCaptor.capture(), eq(explanation));
    submission = submissionCaptor.getValue();
    assertEquals((Long) 147L, submission.getId());
    assertEquals(LC_MS_MS, submission.getService());
    assertEquals(taxonomy, submission.getTaxonomy());
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
    assertEquals(quantificationComment, submission.getQuantificationComment());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    assertEquals(type, sample.getType());
    assertEquals(sampleVolume, sample.getVolume());
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
    design.service.setValue(LC_MS_MS);
    design.sampleType.setValue(type);
    setFields();
    uploadFiles();
    doThrow(new PersistenceException("Could not update submission")).when(submissionService)
        .forceUpdate(any(), any());

    design.save.click();

    verify(view).showError(stringCaptor.capture());
    assertEquals(resources.message(UPDATE_ERROR, experience), stringCaptor.getValue());
  }

  @Test
  public void setValue_Lcmsms() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();

    presenter.setValue(submission);

    assertEquals(LC_MS_MS, design.service.getValue());
    assertEquals(SOLUTION, design.sampleType.getValue());
    assertEquals(solutionSolvent, design.solutionSolvent.getValue());
    assertEquals(sampleName1, design.sampleName.getValue());
    assertEquals(formula, design.formula.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMass), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMass), 0.001);
    assertEquals(toxicity, design.toxicity.getValue());
    assertEquals(lightSensitive, design.lightSensitive.getValue());
    assertEquals(storageTemperature, design.storageTemperature.getValue());
    assertEquals(sampleContainerType, design.sampleContainerType.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCount));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
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
    assertEquals(experience, design.experience.getValue());
    assertEquals(experienceGoal, design.experienceGoal.getValue());
    assertEquals(taxonomy, design.taxonomy.getValue());
    assertEquals(proteinName, design.proteinName.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeight), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModification.getValue());
    assertEquals(sampleQuantity, design.sampleQuantity.getValue());
    assertEquals(sampleVolume, design.sampleVolume.getValue());
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCount));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standards);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount, convert(integerConverter, design.contaminantCount));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminants);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separation.getValue());
    assertEquals(gelThickness, design.thickness.getValue());
    assertEquals(gelColoration, design.coloration.getValue());
    assertEquals(otherColoration, design.otherColoration.getValue());
    assertEquals(developmentTime, design.developmentTime.getValue());
    assertEquals(decoloration, design.decoloration.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantity),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantity.getValue());
    assertEquals(digestion, design.digestion.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethod.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethod.getValue());
    assertEquals(injectionType, design.injectionType.getValue());
    assertEquals(source, design.source.getValue());
    assertEquals(proteinContent, design.proteinContent.getValue());
    assertEquals(instrument, design.instrument.getValue());
    assertEquals(proteinIdentification, design.proteinIdentification.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLink.getValue());
    assertEquals(quantification, design.quantification.getValue());
    assertEquals(quantificationComment, design.quantificationComment.getValue());
    assertEquals(highResolution, design.highResolution.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolvents.getValue());
    assertEquals(methanolSolvents, design.methanolSolvents.getValue());
    assertEquals(chclSolvents, design.chclSolvents.getValue());
    assertEquals(otherSolvents, design.otherSolvents.getValue());
    assertEquals(otherSolvent, design.otherSolvent.getValue());
    assertEquals(comment, design.comment.getValue());
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

    assertEquals(LC_MS_MS, design.service.getValue());
    assertEquals(SOLUTION, design.sampleType.getValue());
    assertEquals(solutionSolvent, design.solutionSolvent.getValue());
    assertEquals(sampleName1, design.sampleName.getValue());
    assertEquals(formula, design.formula.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMass), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMass), 0.001);
    assertEquals(toxicity, design.toxicity.getValue());
    assertEquals(lightSensitive, design.lightSensitive.getValue());
    assertEquals(storageTemperature, design.storageTemperature.getValue());
    assertEquals(SampleContainerType.WELL, design.sampleContainerType.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCount));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
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
    assertEquals(plateName, design.plateName.getValue());
    verify(view.plateComponent).setValue(plate);
    assertEquals(experience, design.experience.getValue());
    assertEquals(experienceGoal, design.experienceGoal.getValue());
    assertEquals(taxonomy, design.taxonomy.getValue());
    assertEquals(proteinName, design.proteinName.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeight), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModification.getValue());
    assertEquals(sampleQuantity, design.sampleQuantity.getValue());
    assertEquals(sampleVolume, design.sampleVolume.getValue());
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCount));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standards);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount, convert(integerConverter, design.contaminantCount));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminants);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separation.getValue());
    assertEquals(gelThickness, design.thickness.getValue());
    assertEquals(gelColoration, design.coloration.getValue());
    assertEquals(otherColoration, design.otherColoration.getValue());
    assertEquals(developmentTime, design.developmentTime.getValue());
    assertEquals(decoloration, design.decoloration.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantity),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantity.getValue());
    assertEquals(digestion, design.digestion.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethod.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethod.getValue());
    assertEquals(injectionType, design.injectionType.getValue());
    assertEquals(source, design.source.getValue());
    assertEquals(proteinContent, design.proteinContent.getValue());
    assertEquals(instrument, design.instrument.getValue());
    assertEquals(proteinIdentification, design.proteinIdentification.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLink.getValue());
    assertEquals(quantification, design.quantification.getValue());
    assertEquals(quantificationComment, design.quantificationComment.getValue());
    assertEquals(highResolution, design.highResolution.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolvents.getValue());
    assertEquals(methanolSolvents, design.methanolSolvents.getValue());
    assertEquals(chclSolvents, design.chclSolvents.getValue());
    assertEquals(otherSolvents, design.otherSolvents.getValue());
    assertEquals(otherSolvent, design.otherSolvent.getValue());
    assertEquals(comment, design.comment.getValue());
  }

  @Test
  public void setValue_SmallMolecule() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();
    submission.setService(SMALL_MOLECULE);

    presenter.setValue(submission);

    assertEquals(SMALL_MOLECULE, design.service.getValue());
    assertEquals(SOLUTION, design.sampleType.getValue());
    assertEquals(solutionSolvent, design.solutionSolvent.getValue());
    assertEquals(sampleName1, design.sampleName.getValue());
    assertEquals(formula, design.formula.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMass), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMass), 0.001);
    assertEquals(toxicity, design.toxicity.getValue());
    assertEquals(lightSensitive, design.lightSensitive.getValue());
    assertEquals(storageTemperature, design.storageTemperature.getValue());
    assertEquals(sampleContainerType, design.sampleContainerType.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCount));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
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
    assertEquals(experience, design.experience.getValue());
    assertEquals(experienceGoal, design.experienceGoal.getValue());
    assertEquals(taxonomy, design.taxonomy.getValue());
    assertEquals(proteinName, design.proteinName.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeight), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModification.getValue());
    assertEquals(sampleQuantity, design.sampleQuantity.getValue());
    assertEquals(sampleVolume, design.sampleVolume.getValue());
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCount));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standards);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount, convert(integerConverter, design.contaminantCount));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminants);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separation.getValue());
    assertEquals(gelThickness, design.thickness.getValue());
    assertEquals(gelColoration, design.coloration.getValue());
    assertEquals(otherColoration, design.otherColoration.getValue());
    assertEquals(developmentTime, design.developmentTime.getValue());
    assertEquals(decoloration, design.decoloration.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantity),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantity.getValue());
    assertEquals(digestion, design.digestion.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethod.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethod.getValue());
    assertEquals(injectionType, design.injectionType.getValue());
    assertEquals(source, design.source.getValue());
    assertEquals(proteinContent, design.proteinContent.getValue());
    assertEquals(instrument, design.instrument.getValue());
    assertEquals(proteinIdentification, design.proteinIdentification.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLink.getValue());
    assertEquals(quantification, design.quantification.getValue());
    assertEquals(quantificationComment, design.quantificationComment.getValue());
    assertEquals(highResolution, design.highResolution.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolvents.getValue());
    assertEquals(methanolSolvents, design.methanolSolvents.getValue());
    assertEquals(chclSolvents, design.chclSolvents.getValue());
    assertEquals(otherSolvents, design.otherSolvents.getValue());
    assertEquals(otherSolvent, design.otherSolvent.getValue());
    assertEquals(comment, design.comment.getValue());
  }

  @Test
  public void setValue_IntactProtein() throws Throwable {
    presenter.init(view);
    Submission submission = createSubmission();
    submission.setService(INTACT_PROTEIN);

    presenter.setValue(submission);

    assertEquals(INTACT_PROTEIN, design.service.getValue());
    assertEquals(SOLUTION, design.sampleType.getValue());
    assertEquals(solutionSolvent, design.solutionSolvent.getValue());
    assertEquals(sampleName1, design.sampleName.getValue());
    assertEquals(formula, design.formula.getValue());
    assertEquals(monoisotopicMass, convert(doubleConverter, design.monoisotopicMass), 0.001);
    assertEquals(averageMass, convert(doubleConverter, design.averageMass), 0.001);
    assertEquals(toxicity, design.toxicity.getValue());
    assertEquals(lightSensitive, design.lightSensitive.getValue());
    assertEquals(storageTemperature, design.storageTemperature.getValue());
    assertEquals(sampleContainerType, design.sampleContainerType.getValue());
    assertEquals((Integer) sampleCount, convert(integerConverter, design.sampleCount));
    ListDataProvider<SubmissionSample> samplesDataProvider = dataProvider(design.samples);
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
    assertEquals(experience, design.experience.getValue());
    assertEquals(experienceGoal, design.experienceGoal.getValue());
    assertEquals(taxonomy, design.taxonomy.getValue());
    assertEquals(proteinName, design.proteinName.getValue());
    assertEquals(proteinWeight1, convert(doubleConverter, design.proteinWeight), 0.001);
    assertEquals(postTranslationModification, design.postTranslationModification.getValue());
    assertEquals(sampleQuantity, design.sampleQuantity.getValue());
    assertEquals(sampleVolume, design.sampleVolume.getValue());
    assertEquals((Integer) standardsCount, convert(integerConverter, design.standardCount));
    ListDataProvider<Standard> standardsDataProvider = dataProvider(design.standards);
    List<Standard> standards = new ArrayList<>(standardsDataProvider.getItems());
    assertEquals(2, standards.size());
    Standard standard = standards.get(0);
    assertEquals(standardName1, standard.getName());
    assertEquals(standardQuantity1, standard.getQuantity());
    standard = standards.get(1);
    assertEquals(standardName2, standard.getName());
    assertEquals(standardQuantity2, standard.getQuantity());
    assertEquals((Integer) contaminantsCount, convert(integerConverter, design.contaminantCount));
    ListDataProvider<Contaminant> contaminantsDataProvider = dataProvider(design.contaminants);
    List<Contaminant> contaminants = new ArrayList<>(contaminantsDataProvider.getItems());
    assertEquals(2, contaminants.size());
    Contaminant contaminant = contaminants.get(0);
    assertEquals(contaminantName1, contaminant.getName());
    assertEquals(contaminantQuantity1, contaminant.getQuantity());
    contaminant = contaminants.get(1);
    assertEquals(contaminantName2, contaminant.getName());
    assertEquals(contaminantQuantity2, contaminant.getQuantity());
    assertEquals(gelSeparation, design.separation.getValue());
    assertEquals(gelThickness, design.thickness.getValue());
    assertEquals(gelColoration, design.coloration.getValue());
    assertEquals(otherColoration, design.otherColoration.getValue());
    assertEquals(developmentTime, design.developmentTime.getValue());
    assertEquals(decoloration, design.decoloration.getValue());
    assertEquals(weightMarkerQuantity, convert(doubleConverter, design.weightMarkerQuantity),
        0.001);
    assertEquals(proteinQuantity, design.proteinQuantity.getValue());
    assertEquals(digestion, design.digestion.getValue());
    assertEquals(usedDigestion, design.usedProteolyticDigestionMethod.getValue());
    assertEquals(otherDigestion, design.otherProteolyticDigestionMethod.getValue());
    assertEquals(injectionType, design.injectionType.getValue());
    assertEquals(source, design.source.getValue());
    assertEquals(proteinContent, design.proteinContent.getValue());
    assertEquals(instrument, design.instrument.getValue());
    assertEquals(proteinIdentification, design.proteinIdentification.getValue());
    assertEquals(proteinIdentificationLink, design.proteinIdentificationLink.getValue());
    assertEquals(quantification, design.quantification.getValue());
    assertEquals(quantificationComment, design.quantificationComment.getValue());
    assertEquals(highResolution, design.highResolution.getValue());
    assertEquals(acetonitrileSolvents, design.acetonitrileSolvents.getValue());
    assertEquals(methanolSolvents, design.methanolSolvents.getValue());
    assertEquals(chclSolvents, design.chclSolvents.getValue());
    assertEquals(otherSolvents, design.otherSolvents.getValue());
    assertEquals(otherSolvent, design.otherSolvent.getValue());
    assertEquals(comment, design.comment.getValue());
  }
}
