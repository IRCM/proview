package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.INVALID_INTEGER;
import static ca.qc.ircm.proview.Constants.INVALID_NUMBER;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.sample.SampleProperties.QUANTITY;
import static ca.qc.ircm.proview.sample.SampleProperties.VOLUME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.CONTAMINANTS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DECOLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DEVELOPMENT_TIME;
import static ca.qc.ircm.proview.submission.SubmissionProperties.DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.GOAL;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.IDENTIFICATION_LINK;
import static ca.qc.ircm.proview.submission.SubmissionProperties.INSTRUMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_COLORATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.OTHER_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.POST_TRANSLATION_MODIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_CONTENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.PROTEIN_QUANTITY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.QUANTIFICATION_COMMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SEPARATION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.SERVICE;
import static ca.qc.ircm.proview.submission.SubmissionProperties.STANDARDS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.TAXONOMY;
import static ca.qc.ircm.proview.submission.SubmissionProperties.THICKNESS;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USED_DIGESTION;
import static ca.qc.ircm.proview.submission.SubmissionProperties.WEIGHT_MARKER_QUANTITY;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.CONTAMINANTS_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.DEVELOPMENT_TIME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.ID;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.PROTEIN_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTIFICATION_COMMENT_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTIFICATION_COMMENT_PLACEHOLDER_TMT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_COUNT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_DUPLICATES;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_EXISTS;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_TITLE;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_NAMES_WRONG_COUNT;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.SAMPLES_TYPE;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.STANDARDS_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.VOLUME_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.WEIGHT_MARKER_QUANTITY_PLACEHOLDER;
import static ca.qc.ircm.proview.submission.web.LcmsmsSubmissionForm.id;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.GelSeparation;
import ca.qc.ircm.proview.submission.GelThickness;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Quantification;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link LcmsmsSubmissionForm}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class LcmsmsSubmissionFormTest extends SpringUIUnitTest {

  private static final String MESSAGES_PREFIX = messagePrefix(LcmsmsSubmissionForm.class);
  private static final String SAMPLE_PREFIX = messagePrefix(Sample.class);
  private static final String SUBMISSION_PREFIX = messagePrefix(Submission.class);
  private static final String SUBMISSION_SAMPLE_PREFIX = messagePrefix(SubmissionSample.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String MASS_DETECTION_INSTRUMENT_PREFIX =
      messagePrefix(MassDetectionInstrument.class);
  private static final String PROTEIN_IDENTIFICATION_PREFIX =
      messagePrefix(ProteinIdentification.class);
  private static final String PROTEOLYTIC_DIGESTION_PREFIX =
      messagePrefix(ProteolyticDigestion.class);
  private static final String SAMPLE_TYPE_PREFIX = messagePrefix(SampleType.class);
  private static final String GEL_COLORATION_PREFIX = messagePrefix(GelColoration.class);
  private static final String GEL_SEPARATION_PREFIX = messagePrefix(GelSeparation.class);
  private static final String GEL_THICKNESS_PREFIX = messagePrefix(GelThickness.class);
  private static final String PROTEIN_CONTENT_PREFIX = messagePrefix(ProteinContent.class);
  private static final String QUANTIFICATION_PREFIX = messagePrefix(Quantification.class);
  private static final String SERVICE_PREFIX = messagePrefix(Service.class);
  private LcmsmsSubmissionForm form;
  @MockitoBean
  private SubmissionSampleService sampleService;
  @Autowired
  private AuthenticatedUser authenticatedUser;
  @Autowired
  private SubmissionRepository repository;
  private Submission newSubmission;
  private String experiment = "my experiment";
  private String goal = "my goal";
  private String taxonomy = "my taxon";
  private String protein = "my protein";
  private Double molecularWeight = 12.3;
  private String postTranslationModification = "glyco";
  private SampleType sampleType = SampleType.SOLUTION;
  private int samplesCount = 2;
  private String sampleName1 = "my sample 1";
  private String sampleName2 = "my sample 2";
  private String samplesNames = sampleName1 + ", " + sampleName2;
  private String quantity = "13g";
  private String volume = "9 ml";
  private GelSeparation separation = GelSeparation.TWO_DIMENSION;
  private GelThickness thickness = GelThickness.TWO;
  private GelColoration coloration = GelColoration.SYPRO;
  private String otherColoration = "my coloration";
  private String developmentTime = "20s";
  private boolean destained = true;
  private Double weightMarkerQuantity = 5.1;
  private String proteinQuantity = "11g";
  private ProteolyticDigestion digestion = ProteolyticDigestion.DIGESTED;
  private String usedDigestion = "my used digestion";
  private String otherDigestion = "my other digestion";
  private ProteinContent proteinContent = ProteinContent.LARGE;
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private ProteinIdentification identification = ProteinIdentification.UNIPROT;
  private String identificationLink = "http://www.unitprot.org/mydatabase";
  private Quantification quantification = Quantification.SILAC;
  private String quantificationComment = "Heavy: Lys8, Arg10\nMedium: Lys4, Arg6";
  private Locale locale = ENGLISH;

  /**
   * Before test.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    newSubmission = new Submission();
    newSubmission.setSamples(new ArrayList<>());
    SubmissionSample sample = new SubmissionSample();
    sample.setType(SampleType.DRY);
    newSubmission.getSamples().add(sample);
    SubmissionView view = navigate(SubmissionView.class);
    test(test(view).find(Tabs.class).id(SERVICE))
        .select(view.getTranslation(SERVICE_PREFIX + LC_MS_MS.name()));
    form = test(view).find(LcmsmsSubmissionForm.class).id(LcmsmsSubmissionForm.ID);
  }

  private Submission submission() {
    Submission submission = new Submission();
    submission.setExperiment(experiment);
    submission.setGoal(goal);
    submission.setTaxonomy(taxonomy);
    submission.setProtein(protein);
    submission.setSamples(new ArrayList<>());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().add(new SubmissionSample());
    submission.getSamples().get(0).setMolecularWeight(molecularWeight);
    submission.setPostTranslationModification(postTranslationModification);
    submission.getSamples().get(0).setType(sampleType);
    submission.getSamples().get(0).setName(sampleName1);
    submission.getSamples().get(1).setName(sampleName2);
    submission.getSamples().get(0).setQuantity(quantity);
    submission.getSamples().get(0).setVolume(volume);
    submission.setSeparation(separation);
    submission.setThickness(thickness);
    submission.setColoration(coloration);
    submission.setOtherColoration(otherColoration);
    submission.setDevelopmentTime(developmentTime);
    submission.setDecoloration(destained);
    submission.setWeightMarkerQuantity(weightMarkerQuantity);
    submission.setProteinQuantity(proteinQuantity);
    submission.setDigestion(digestion);
    submission.setUsedDigestion(usedDigestion);
    submission.setOtherDigestion(otherDigestion);
    submission.setProteinContent(proteinContent);
    submission.setInstrument(instrument);
    submission.setIdentification(identification);
    submission.setIdentificationLink(identificationLink);
    submission.setQuantification(quantification);
    submission.setQuantificationComment(quantificationComment);
    return submission;
  }

  private void setFields() {
    form.experiment.setValue(experiment);
    form.goal.setValue(goal);
    form.taxonomy.setValue(taxonomy);
    form.protein.setValue(protein);
    form.molecularWeight.setValue(String.valueOf(molecularWeight));
    form.postTranslationModification.setValue(postTranslationModification);
    form.sampleType.setValue(sampleType);
    form.samplesCount.setValue(String.valueOf(samplesCount));
    form.samplesNames.setValue(samplesNames);
    form.quantity.setValue(quantity);
    form.volume.setValue(volume);
    form.separation.setValue(separation);
    form.thickness.setValue(thickness);
    form.coloration.setValue(coloration);
    form.otherColoration.setValue(otherColoration);
    form.developmentTime.setValue(developmentTime);
    form.destained.setValue(destained);
    form.weightMarkerQuantity.setValue(String.valueOf(weightMarkerQuantity));
    form.proteinQuantity.setValue(proteinQuantity);
    form.digestion.setValue(digestion);
    form.usedDigestion.setValue(usedDigestion);
    form.otherDigestion.setValue(otherDigestion);
    form.proteinContent.setValue(proteinContent);
    form.instrument.setValue(instrument);
    form.identification.setValue(identification);
    form.identificationLink.setValue(identificationLink);
    form.quantification.setValue(quantification);
    form.quantificationComment.setValue(quantificationComment);
  }

  @Test
  public void styles() {
    assertEquals(ID, form.getId().orElse(""));
    assertEquals(id(GOAL), form.goal.getId().orElse(""));
    assertEquals(id(TAXONOMY), form.taxonomy.getId().orElse(""));
    assertEquals(id(PROTEIN), form.protein.getId().orElse(""));
    assertEquals(id(MOLECULAR_WEIGHT), form.molecularWeight.getId().orElse(""));
    assertEquals(id(POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getId().orElse(""));
    assertEquals(id(SAMPLES_TYPE), form.sampleType.getId().orElse(""));
    assertEquals(id(SAMPLES_COUNT), form.samplesCount.getId().orElse(""));
    assertEquals(id(SAMPLES_NAMES), form.samplesNames.getId().orElse(""));
    assertEquals(id(QUANTITY), form.quantity.getId().orElse(""));
    assertEquals(id(VOLUME), form.volume.getId().orElse(""));
    assertEquals(id(CONTAMINANTS), form.contaminants.getId().orElse(""));
    assertEquals(id(STANDARDS), form.standards.getId().orElse(""));
    assertEquals(id(SEPARATION), form.separation.getId().orElse(""));
    assertEquals(id(THICKNESS), form.thickness.getId().orElse(""));
    assertEquals(id(COLORATION), form.coloration.getId().orElse(""));
    assertEquals(id(OTHER_COLORATION), form.otherColoration.getId().orElse(""));
    assertEquals(id(DEVELOPMENT_TIME), form.developmentTime.getId().orElse(""));
    assertEquals(id(DECOLORATION), form.destained.getId().orElse(""));
    assertEquals(id(WEIGHT_MARKER_QUANTITY), form.weightMarkerQuantity.getId().orElse(""));
    assertEquals(id(PROTEIN_QUANTITY), form.proteinQuantity.getId().orElse(""));
    assertEquals(id(DIGESTION), form.digestion.getId().orElse(""));
    assertEquals(id(USED_DIGESTION), form.usedDigestion.getId().orElse(""));
    assertEquals(id(OTHER_DIGESTION), form.otherDigestion.getId().orElse(""));
    assertEquals(id(PROTEIN_CONTENT), form.proteinContent.getId().orElse(""));
    assertEquals(id(INSTRUMENT), form.instrument.getId().orElse(""));
    assertEquals(id(IDENTIFICATION), form.identification.getId().orElse(""));
    assertEquals(id(IDENTIFICATION_LINK), form.identificationLink.getId().orElse(""));
    assertEquals(id(QUANTIFICATION), form.quantification.getId().orElse(""));
    assertEquals(id(QUANTIFICATION_COMMENT), form.quantificationComment.getId().orElse(""));
  }

  @Test
  public void labels() {
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + GOAL), form.goal.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + TAXONOMY), form.taxonomy.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN), form.protein.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_SAMPLE_PREFIX + MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        form.samplesCount.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES),
        form.samplesNames.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_PLACEHOLDER),
        form.samplesNames.getPlaceholder());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_TITLE),
        form.samplesNames.getElement().getAttribute(TITLE));
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES),
        form.samplesNames.getLabel());
    assertEquals(form.getTranslation(SAMPLE_PREFIX + QUANTITY), form.quantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTITY_PLACEHOLDER),
        form.quantity.getPlaceholder());
    assertEquals(form.getTranslation(SAMPLE_PREFIX + VOLUME), form.volume.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + VOLUME_PLACEHOLDER),
        form.volume.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + CONTAMINANTS),
        form.contaminants.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + CONTAMINANTS_PLACEHOLDER),
        form.contaminants.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + STANDARDS), form.standards.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + STANDARDS_PLACEHOLDER),
        form.standards.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SEPARATION), form.separation.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + THICKNESS), form.thickness.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + COLORATION), form.coloration.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_COLORATION),
        form.otherColoration.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DEVELOPMENT_TIME),
        form.developmentTime.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + DEVELOPMENT_TIME_PLACEHOLDER),
        form.developmentTime.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DECOLORATION), form.destained.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + WEIGHT_MARKER_QUANTITY),
        form.weightMarkerQuantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        form.weightMarkerQuantity.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN_QUANTITY),
        form.proteinQuantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + PROTEIN_QUANTITY_PLACEHOLDER),
        form.proteinQuantity.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DIGESTION), form.digestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + USED_DIGESTION),
        form.usedDigestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_DIGESTION),
        form.otherDigestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN_CONTENT),
        form.proteinContent.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + INSTRUMENT), form.instrument.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + IDENTIFICATION),
        form.identification.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + IDENTIFICATION_LINK),
        form.identificationLink.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + QUANTIFICATION),
        form.quantification.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + QUANTIFICATION_COMMENT),
        form.quantificationComment.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
  }

  @Test
  public void localeChange() {
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + GOAL), form.goal.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + TAXONOMY), form.taxonomy.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN), form.protein.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_SAMPLE_PREFIX + MOLECULAR_WEIGHT),
        form.molecularWeight.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + POST_TRANSLATION_MODIFICATION),
        form.postTranslationModification.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_TYPE), form.sampleType.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_COUNT),
        form.samplesCount.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES),
        form.samplesNames.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_PLACEHOLDER),
        form.samplesNames.getPlaceholder());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_TITLE),
        form.samplesNames.getElement().getAttribute(TITLE));
    assertEquals(form.getTranslation(SAMPLE_PREFIX + QUANTITY), form.quantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTITY_PLACEHOLDER),
        form.quantity.getPlaceholder());
    assertEquals(form.getTranslation(SAMPLE_PREFIX + VOLUME), form.volume.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + VOLUME_PLACEHOLDER),
        form.volume.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + CONTAMINANTS),
        form.contaminants.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + CONTAMINANTS_PLACEHOLDER),
        form.contaminants.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + STANDARDS), form.standards.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + STANDARDS_PLACEHOLDER),
        form.standards.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + SEPARATION), form.separation.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + THICKNESS), form.thickness.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + COLORATION), form.coloration.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_COLORATION),
        form.otherColoration.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DEVELOPMENT_TIME),
        form.developmentTime.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + DEVELOPMENT_TIME_PLACEHOLDER),
        form.developmentTime.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DECOLORATION), form.destained.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + WEIGHT_MARKER_QUANTITY),
        form.weightMarkerQuantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + WEIGHT_MARKER_QUANTITY_PLACEHOLDER),
        form.weightMarkerQuantity.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN_QUANTITY),
        form.proteinQuantity.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + PROTEIN_QUANTITY_PLACEHOLDER),
        form.proteinQuantity.getPlaceholder());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + DIGESTION), form.digestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + USED_DIGESTION),
        form.usedDigestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + OTHER_DIGESTION),
        form.otherDigestion.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + PROTEIN_CONTENT),
        form.proteinContent.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + INSTRUMENT), form.instrument.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + IDENTIFICATION),
        form.identification.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + IDENTIFICATION_LINK),
        form.identificationLink.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + QUANTIFICATION),
        form.quantification.getLabel());
    assertEquals(form.getTranslation(SUBMISSION_PREFIX + QUANTIFICATION_COMMENT),
        form.quantificationComment.getLabel());
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
  }

  @Test
  public void sampleTypes() {
    List<SampleType> items = items(form.sampleType);
    assertEquals(SampleType.values().length, items.size());
    for (SampleType value : SampleType.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(SAMPLE_TYPE_PREFIX + value.name()),
          form.sampleType.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void separations() {
    List<GelSeparation> items = items(form.separation);
    assertEquals(GelSeparation.values().length, items.size());
    for (GelSeparation value : GelSeparation.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(GEL_SEPARATION_PREFIX + value.name()),
          form.separation.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void thicknesses() {
    List<GelThickness> items = items(form.thickness);
    assertEquals(GelThickness.values().length, items.size());
    for (GelThickness value : GelThickness.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(GEL_THICKNESS_PREFIX + value.name()),
          form.thickness.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void colorations() {
    List<GelColoration> items = items(form.coloration);
    assertEquals(GelColoration.values().length, items.size());
    for (GelColoration value : GelColoration.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(GEL_COLORATION_PREFIX + value.name()),
          form.coloration.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void digestions() {
    List<ProteolyticDigestion> items = items(form.digestion);
    assertEquals(ProteolyticDigestion.values().length, items.size());
    for (ProteolyticDigestion value : ProteolyticDigestion.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(PROTEOLYTIC_DIGESTION_PREFIX + value.name()),
          form.digestion.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void proteinContents() {
    List<ProteinContent> items = items(form.proteinContent);
    assertEquals(ProteinContent.values().length, items.size());
    for (ProteinContent value : ProteinContent.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(PROTEIN_CONTENT_PREFIX + value.name()),
          form.proteinContent.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void instruments() {
    List<MassDetectionInstrument> items = items(form.instrument);
    assertEquals(MassDetectionInstrument.userChoices().size(), items.size());
    for (MassDetectionInstrument value : MassDetectionInstrument.userChoices()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + value.name()),
          form.instrument.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void identifications() {
    List<ProteinIdentification> items = items(form.identification);
    assertEquals(ProteinIdentification.availables().size(), items.size());
    for (ProteinIdentification value : ProteinIdentification.availables()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(PROTEIN_IDENTIFICATION_PREFIX + value.name()),
          form.identification.getItemRenderer().createComponent(value).getElement().getText());
    }
  }

  @Test
  public void quantifications() {
    List<Quantification> items = items(form.quantification);
    assertEquals(Quantification.values().length, items.size());
    for (Quantification value : Quantification.values()) {
      assertTrue(items.contains(value));
      assertEquals(form.getTranslation(QUANTIFICATION_PREFIX + value.name()),
          form.quantification.getItemLabelGenerator().apply(value));
    }
  }

  @Test
  public void quantificationComment() {
    form.quantification.setValue(Quantification.TMT);
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER_TMT),
        form.quantificationComment.getPlaceholder());
    form.quantification.setValue(Quantification.SILAC);
    assertEquals(form.getTranslation(MESSAGES_PREFIX + QUANTIFICATION_COMMENT_PLACEHOLDER),
        form.quantificationComment.getPlaceholder());
  }

  @Test
  public void required() {
    assertTrue(form.experiment.isRequiredIndicatorVisible());
    assertFalse(form.goal.isRequiredIndicatorVisible());
    assertTrue(form.taxonomy.isRequiredIndicatorVisible());
    assertFalse(form.protein.isRequiredIndicatorVisible());
    assertFalse(form.molecularWeight.isRequiredIndicatorVisible());
    assertFalse(form.postTranslationModification.isRequiredIndicatorVisible());
    assertTrue(form.sampleType.isRequiredIndicatorVisible());
    assertTrue(form.samplesCount.isRequiredIndicatorVisible());
    assertTrue(form.samplesNames.isRequiredIndicatorVisible());
    assertTrue(form.quantity.isRequiredIndicatorVisible());
    assertTrue(form.volume.isRequiredIndicatorVisible());
    assertTrue(form.separation.isRequiredIndicatorVisible());
    assertTrue(form.thickness.isRequiredIndicatorVisible());
    assertTrue(form.coloration.isRequiredIndicatorVisible());
    assertTrue(form.otherColoration.isRequiredIndicatorVisible());
    assertFalse(form.developmentTime.isRequiredIndicatorVisible());
    assertFalse(form.destained.isRequiredIndicatorVisible());
    assertFalse(form.weightMarkerQuantity.isRequiredIndicatorVisible());
    assertFalse(form.proteinQuantity.isRequiredIndicatorVisible());
    assertTrue(form.digestion.isRequiredIndicatorVisible());
    assertTrue(form.usedDigestion.isRequiredIndicatorVisible());
    assertTrue(form.otherDigestion.isRequiredIndicatorVisible());
    assertTrue(form.proteinContent.isRequiredIndicatorVisible());
    assertFalse(form.instrument.isRequiredIndicatorVisible());
    assertTrue(form.identification.isRequiredIndicatorVisible());
    assertTrue(form.identificationLink.isRequiredIndicatorVisible());
    assertFalse(form.quantification.isRequiredIndicatorVisible());
    assertTrue(form.quantificationComment.isRequiredIndicatorVisible());
  }

  @Test
  public void enabled() {
    assertTrue(form.experiment.isEnabled());
    assertTrue(form.goal.isEnabled());
    assertTrue(form.taxonomy.isEnabled());
    assertTrue(form.protein.isEnabled());
    assertTrue(form.molecularWeight.isEnabled());
    assertTrue(form.postTranslationModification.isEnabled());
    assertTrue(form.sampleType.isEnabled());
    assertTrue(form.samplesCount.isEnabled());
    assertTrue(form.samplesNames.isEnabled());
    assertTrue(form.quantity.isEnabled());
    assertTrue(form.digestion.isEnabled());
    assertFalse(form.usedDigestion.isEnabled());
    assertFalse(form.otherDigestion.isEnabled());
    assertTrue(form.proteinContent.isEnabled());
    assertTrue(form.instrument.isEnabled());
    assertTrue(form.identification.isEnabled());
    assertFalse(form.identificationLink.isEnabled());
    assertTrue(form.quantification.isEnabled());
    assertFalse(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_Solution() {
    form.sampleType.setValue(SampleType.SOLUTION);
    assertTrue(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Dry() {
    form.sampleType.setValue(SampleType.DRY);
    assertFalse(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Gel() {
    form.sampleType.setValue(SampleType.GEL);
    assertFalse(form.volume.isEnabled());
    assertTrue(form.separation.isEnabled());
    assertTrue(form.thickness.isEnabled());
    assertTrue(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertTrue(form.developmentTime.isEnabled());
    assertTrue(form.destained.isEnabled());
    assertTrue(form.weightMarkerQuantity.isEnabled());
    assertTrue(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_Beads() {
    form.sampleType.setValue(SampleType.AGAROSE_BEADS);
    assertTrue(form.volume.isEnabled());
    assertFalse(form.separation.isEnabled());
    assertFalse(form.thickness.isEnabled());
    assertFalse(form.coloration.isEnabled());
    assertFalse(form.otherColoration.isEnabled());
    assertFalse(form.developmentTime.isEnabled());
    assertFalse(form.destained.isEnabled());
    assertFalse(form.weightMarkerQuantity.isEnabled());
    assertFalse(form.proteinQuantity.isEnabled());
  }

  @Test
  public void enabled_UsedDigestion() {
    form.digestion.setValue(ProteolyticDigestion.DIGESTED);
    assertTrue(form.usedDigestion.isEnabled());
    assertFalse(form.otherDigestion.isEnabled());
  }

  @Test
  public void enabled_OtherDigestion() {
    form.digestion.setValue(ProteolyticDigestion.OTHER);
    assertFalse(form.usedDigestion.isEnabled());
    assertTrue(form.otherDigestion.isEnabled());
  }

  @Test
  public void enabled_RefseqIdentification() {
    form.identification.setValue(ProteinIdentification.REFSEQ);
    assertFalse(form.identificationLink.isEnabled());
  }

  @Test
  public void enabled_OtherIdentification() {
    form.identification.setValue(ProteinIdentification.OTHER);
    assertTrue(form.identificationLink.isEnabled());
  }

  @Test
  public void enabled_LabelFreeQuantification() {
    form.quantification.setValue(Quantification.LABEL_FREE);
    assertFalse(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_SilacQuantification() {
    form.quantification.setValue(Quantification.SILAC);
    assertTrue(form.quantificationComment.isEnabled());
  }

  @Test
  public void enabled_TmtQuantification() {
    form.quantification.setValue(Quantification.TMT);
    assertTrue(form.quantificationComment.isEnabled());
  }

  @Test
  public void isValid_EmptyExperiment() {
    form.setSubmission(newSubmission);
    setFields();
    form.experiment.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.experiment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyTaxonomy() {
    form.setSubmission(newSubmission);
    setFields();
    form.taxonomy.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.taxonomy);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyMolecularWeight() {
    form.setSubmission(newSubmission);
    setFields();
    form.molecularWeight.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_InvalidMolecularWeight() {
    form.setSubmission(newSubmission);
    setFields();
    form.molecularWeight.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.molecularWeight);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)),
        error.getMessage());
  }

  @Test
  public void isValid_EmptySampleType() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.sampleType);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySamplesCount() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_InvalidSamplesCount() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesCount);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_INTEGER)),
        error.getMessage());
  }

  @Test
  public void isValid_SamplesNamesCommaSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesCommaSpaceSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + ", " + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + ";" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesSemicolonSpaceSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "; " + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesTabSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "\t" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_SamplesNamesNewlineSeparator() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "\n" + sampleName2);

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_EmptySamplesNames() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyFirstSampleName() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(", abc");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional
            .of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_EmptySecondSampleName() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue("abc, ");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional
            .of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_LastEmptySamplesNames() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",");

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
    Submission submission = form.getSubmission();
    assertEquals(2, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
  }

  @Test
  public void isValid_DuplicatedSamplesNames() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesCount.setValue("3");
    form.samplesNames.setValue(String.join(", ", sampleName1, sampleName2, sampleName2));

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional.of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_DUPLICATES, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_WrongNumberOfSamplesNames_Below() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1);

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional
            .of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT, 1, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_WrongNumberOfSamplesNames_Above() {
    form.setSubmission(newSubmission);
    setFields();
    form.samplesNames.setValue(sampleName1 + "," + sampleName2 + ",other_sample_name");

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional
            .of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_WRONG_COUNT, 3, samplesCount)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNames() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    form.setSubmission(newSubmission);
    setFields();

    assertFalse(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.samplesNames);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(
        Optional.of(form.getTranslation(MESSAGES_PREFIX + SAMPLES_NAMES_EXISTS, sampleName2)),
        error.getMessage());
  }

  @Test
  public void isValid_AlreadyExistsSamplesNamesInSubmission() {
    when(sampleService.exists(sampleName2)).thenReturn(true);
    SubmissionSample sample = new SubmissionSample();
    sample.setName(sampleName2);
    newSubmission.getSamples().add(sample);
    form.setSubmission(newSubmission);
    setFields();

    assertTrue(form.isValid());
    BinderValidationStatus<LcmsmsSubmissionForm.Samples> status = form.validateSamples();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyQuantity() {
    form.setSubmission(newSubmission);
    setFields();
    form.quantity.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantity);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyQuantity_Gel() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.quantity.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyVolume() {
    form.setSubmission(newSubmission);
    setFields();
    form.volume.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.volume);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyVolume_Dry() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.DRY);
    form.volume.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyVolume_Beads() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.AGAROSE_BEADS);
    form.volume.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<SubmissionSample> status = form.validateFirstSample();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.volume);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySeparation_Gel() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.separation.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.separation);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptySeparation_Solution() {
    form.setSubmission(newSubmission);
    setFields();
    form.separation.setValue(null);

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyThickness_Gel() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.thickness.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.thickness);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyThickness_Solution() {
    form.setSubmission(newSubmission);
    setFields();
    form.thickness.setValue(null);

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyColoration_Gel() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.coloration);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyColoration_Solution() {
    form.setSubmission(newSubmission);
    setFields();
    form.coloration.setValue(null);

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherColoration_GelAndOtherColoration() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(GelColoration.OTHER);
    form.otherColoration.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.otherColoration);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyOtherColoration_GelAndAnyColoration() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.coloration.setValue(GelColoration.COOMASSIE);
    form.otherColoration.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherColoration_SolutionAndOtherColoration() {
    form.setSubmission(newSubmission);
    setFields();
    form.coloration.setValue(GelColoration.OTHER);
    form.otherColoration.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_InvalidWeightMarker_Gel() {
    form.setSubmission(newSubmission);
    setFields();
    form.sampleType.setValue(SampleType.GEL);
    form.weightMarkerQuantity.setValue("a");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.weightMarkerQuantity);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + INVALID_NUMBER)),
        error.getMessage());
  }

  @Test
  public void isValid_InvalidWeightMarker_Solution() {
    form.setSubmission(newSubmission);
    setFields();
    form.weightMarkerQuantity.setValue("a");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyDigestion() {
    form.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.digestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyUsedDigestion_UsedDigestion() {
    form.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.DIGESTED);
    form.usedDigestion.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.usedDigestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyUsedDigestion_AnyDigestion() {
    form.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.TRYPSIN);
    form.usedDigestion.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyOtherDigestion_OtherDigestion() {
    form.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.OTHER);
    form.otherDigestion.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.otherDigestion);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyOtherDigestion_AnyDigestion() {
    form.setSubmission(newSubmission);
    setFields();
    form.digestion.setValue(ProteolyticDigestion.TRYPSIN);
    form.otherDigestion.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyProteinContent() {
    form.setSubmission(newSubmission);
    setFields();
    form.proteinContent.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.proteinContent);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentification() {
    form.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(null);

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.identification);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentificationLink_OtherIdentification() {
    form.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(ProteinIdentification.OTHER);
    form.identificationLink.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.identificationLink);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyIdentificationLink_AnyIdentification() {
    form.setSubmission(newSubmission);
    setFields();
    form.identification.setValue(ProteinIdentification.REFSEQ);
    form.identificationLink.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Silac() {
    form.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.SILAC);
    form.quantificationComment.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantificationComment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Tmt() {
    form.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.TMT);
    form.quantificationComment.setValue("");

    assertFalse(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, form.quantificationComment);
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(form.getTranslation(CONSTANTS_PREFIX + REQUIRED)), error.getMessage());
  }

  @Test
  public void isValid_EmptyQuantificationComment_Any() {
    form.setSubmission(newSubmission);
    setFields();
    form.quantification.setValue(Quantification.LABEL_FREE);
    form.quantificationComment.setValue("");

    assertTrue(form.isValid());
    BinderValidationStatus<Submission> status = form.validateSubmission();
    assertTrue(status.isOk());
  }

  @Test
  public void isValid() {
    form.setSubmission(newSubmission);
    setFields();

    assertTrue(form.isValid());
    assertTrue(form.validateSubmission().isOk());
    assertTrue(form.validateFirstSample().isOk());
    assertTrue(form.validateSamples().isOk());
  }

  @Test
  public void getSubmission_NoChanges() {
    Submission database = repository.findById(34L).orElseThrow();
    form.setSubmission(database);

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
    assertEquals(database.getId(), submission.getId());
    assertEquals(database.getExperiment(), submission.getExperiment());
    assertEquals(database.getGoal(), submission.getGoal());
    assertEquals(database.getTaxonomy(), submission.getTaxonomy());
    assertEquals(database.getProtein(), submission.getProtein());
    assertEquals(database.getSamples().get(0).getMolecularWeight(),
        submission.getSamples().get(0).getMolecularWeight());
    assertEquals(database.getPostTranslationModification(),
        submission.getPostTranslationModification());
    assertEquals(database.getSamples().get(0).getType(), submission.getSamples().get(0).getType());
    assertEquals(database.getSamples().size(), submission.getSamples().size());
    for (int i = 0; i < database.getSamples().size(); i++) {
      assertEquals(database.getSamples().get(i).getId(), submission.getSamples().get(i).getId());
      assertEquals(database.getSamples().get(i).getName(),
          submission.getSamples().get(i).getName());
    }
    assertEquals(database.getSamples().get(0).getQuantity(),
        submission.getSamples().get(0).getQuantity());
    assertEquals(database.getSamples().get(0).getVolume(),
        submission.getSamples().get(0).getVolume());
    assertEquals(database.getSeparation(), submission.getSeparation());
    assertEquals(database.getThickness(), submission.getThickness());
    assertEquals(database.getColoration(), submission.getColoration());
    assertEquals(database.getOtherColoration(), submission.getOtherColoration());
    assertEquals(database.getDevelopmentTime(), submission.getDevelopmentTime());
    assertEquals(database.isDecoloration(), submission.isDecoloration());
    assertEquals(database.getWeightMarkerQuantity(), submission.getWeightMarkerQuantity());
    assertEquals(database.getProteinQuantity(), submission.getProteinQuantity());
    assertEquals(database.getDigestion(), submission.getDigestion());
    assertEquals(database.getUsedDigestion(), submission.getUsedDigestion());
    assertEquals(database.getOtherDigestion(), submission.getOtherDigestion());
    assertEquals(database.getProteinContent(), submission.getProteinContent());
    assertEquals(database.getInstrument(), submission.getInstrument());
    assertEquals(database.getIdentification(), submission.getIdentification());
    assertEquals(database.getIdentificationLink(), submission.getIdentificationLink());
    assertEquals(database.getQuantification(), submission.getQuantification());
    assertEquals(database.getQuantificationComment(), submission.getQuantificationComment());
  }

  @Test
  public void getSubmission_ModifiedFields() {
    form.setSubmission(newSubmission);
    setFields();

    assertTrue(form.isValid());
    Submission submission = form.getSubmission();
    assertEquals(experiment, submission.getExperiment());
    assertEquals(goal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(protein, submission.getProtein());
    assertEquals(postTranslationModification, submission.getPostTranslationModification());
    assertEquals(samplesCount, submission.getSamples().size());
    assertEquals(sampleName1, submission.getSamples().get(0).getName());
    assertEquals(sampleName2, submission.getSamples().get(1).getName());
    for (int i = 0; i < samplesCount; i++) {
      assertEquals(sampleType, submission.getSamples().get(i).getType());
      assertEquals(molecularWeight, submission.getSamples().get(i).getMolecularWeight());
      assertEquals(quantity, submission.getSamples().get(0).getQuantity());
      assertEquals(volume, submission.getSamples().get(0).getVolume());
    }
    assertEquals(separation, submission.getSeparation());
    assertEquals(thickness, submission.getThickness());
    assertEquals(coloration, submission.getColoration());
    assertEquals(otherColoration, submission.getOtherColoration());
    assertEquals(developmentTime, submission.getDevelopmentTime());
    assertEquals(destained, submission.isDecoloration());
    assertEquals(weightMarkerQuantity, submission.getWeightMarkerQuantity());
    assertEquals(proteinQuantity, submission.getProteinQuantity());
    assertEquals(digestion, submission.getDigestion());
    assertEquals(usedDigestion, submission.getUsedDigestion());
    assertEquals(otherDigestion, submission.getOtherDigestion());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(instrument, submission.getInstrument());
    assertEquals(identification, submission.getIdentification());
    assertEquals(identificationLink, submission.getIdentificationLink());
    assertEquals(quantification, submission.getQuantification());
    assertEquals(quantificationComment, submission.getQuantificationComment());
  }

  @Test
  public void setSubmission() {
    form.setSubmission(submission());

    assertEquals(experiment, form.experiment.getValue());
    assertFalse(form.experiment.isReadOnly());
    assertEquals(goal, form.goal.getValue());
    assertFalse(form.goal.isReadOnly());
    assertEquals(taxonomy, form.taxonomy.getValue());
    assertFalse(form.taxonomy.isReadOnly());
    assertEquals(protein, form.protein.getValue());
    assertFalse(form.protein.isReadOnly());
    assertEquals(molecularWeight, Double.parseDouble(form.molecularWeight.getValue()), 0.00001);
    assertFalse(form.molecularWeight.isReadOnly());
    assertEquals(postTranslationModification, form.postTranslationModification.getValue());
    assertFalse(form.postTranslationModification.isReadOnly());
    assertEquals(sampleType, form.sampleType.getValue());
    assertFalse(form.sampleType.isReadOnly());
    assertEquals(samplesCount, Integer.parseInt(form.samplesCount.getValue()));
    assertFalse(form.samplesCount.isReadOnly());
    assertEquals(samplesNames, form.samplesNames.getValue());
    assertFalse(form.samplesNames.isReadOnly());
    assertEquals(quantity, form.quantity.getValue());
    assertFalse(form.quantity.isReadOnly());
    assertEquals(volume, form.volume.getValue());
    assertFalse(form.volume.isReadOnly());
    assertEquals(separation, form.separation.getValue());
    assertFalse(form.separation.isReadOnly());
    assertEquals(thickness, form.thickness.getValue());
    assertFalse(form.thickness.isReadOnly());
    assertEquals(coloration, form.coloration.getValue());
    assertFalse(form.coloration.isReadOnly());
    assertEquals(otherColoration, form.otherColoration.getValue());
    assertFalse(form.otherColoration.isReadOnly());
    assertEquals(developmentTime, form.developmentTime.getValue());
    assertFalse(form.developmentTime.isReadOnly());
    assertEquals(destained, form.destained.getValue());
    assertFalse(form.destained.isReadOnly());
    assertEquals(weightMarkerQuantity, Double.parseDouble(form.weightMarkerQuantity.getValue()),
        0.00001);
    assertFalse(form.weightMarkerQuantity.isReadOnly());
    assertEquals(proteinQuantity, form.proteinQuantity.getValue());
    assertFalse(form.proteinQuantity.isReadOnly());
    assertEquals(digestion, form.digestion.getValue());
    assertFalse(form.digestion.isReadOnly());
    assertEquals(usedDigestion, form.usedDigestion.getValue());
    assertFalse(form.usedDigestion.isReadOnly());
    assertEquals(otherDigestion, form.otherDigestion.getValue());
    assertFalse(form.otherDigestion.isReadOnly());
    assertEquals(proteinContent, form.proteinContent.getValue());
    assertFalse(form.proteinContent.isReadOnly());
    assertEquals(instrument, form.instrument.getValue());
    assertFalse(form.instrument.isReadOnly());
    assertEquals(identification, form.identification.getValue());
    assertFalse(form.identification.isReadOnly());
    assertEquals(identificationLink, form.identificationLink.getValue());
    assertFalse(form.identificationLink.isReadOnly());
    assertEquals(quantification, form.quantification.getValue());
    assertFalse(form.quantification.isReadOnly());
    assertEquals(quantificationComment, form.quantificationComment.getValue());
    assertFalse(form.quantificationComment.isReadOnly());
  }

  @Test
  public void setSubmission_ReadOnly() {
    Submission submission = repository.findById(32L).orElseThrow();

    form.setSubmission(submission);

    assertEquals("cap_experiment", form.experiment.getValue());
    assertTrue(form.experiment.isReadOnly());
    assertEquals("cap_goal", form.goal.getValue());
    assertTrue(form.goal.isReadOnly());
    assertEquals("human", form.taxonomy.getValue());
    assertTrue(form.taxonomy.isReadOnly());
    assertEquals("", form.protein.getValue());
    assertTrue(form.protein.isReadOnly());
    assertEquals("", form.molecularWeight.getValue());
    assertTrue(form.molecularWeight.isReadOnly());
    assertEquals("", form.postTranslationModification.getValue());
    assertTrue(form.postTranslationModification.isReadOnly());
    assertEquals(SampleType.SOLUTION, form.sampleType.getValue());
    assertTrue(form.sampleType.isReadOnly());
    assertEquals(1, Integer.parseInt(form.samplesCount.getValue()));
    assertTrue(form.samplesCount.isReadOnly());
    assertEquals("CAP_20111013_01", form.samplesNames.getValue());
    assertTrue(form.samplesNames.isReadOnly());
    assertEquals("1.5 μg", form.quantity.getValue());
    assertTrue(form.quantity.isReadOnly());
    assertEquals("50 μl", form.volume.getValue());
    assertTrue(form.volume.isReadOnly());
    assertNull(form.separation.getValue());
    assertTrue(form.separation.isReadOnly());
    assertNull(form.thickness.getValue());
    assertTrue(form.thickness.isReadOnly());
    assertNull(form.coloration.getValue());
    assertTrue(form.coloration.isReadOnly());
    assertEquals("", form.otherColoration.getValue());
    assertTrue(form.otherColoration.isReadOnly());
    assertEquals("", form.developmentTime.getValue());
    assertTrue(form.developmentTime.isReadOnly());
    assertEquals(false, form.destained.getValue());
    assertTrue(form.destained.isReadOnly());
    assertEquals("", form.weightMarkerQuantity.getValue());
    assertTrue(form.weightMarkerQuantity.isReadOnly());
    assertEquals("", form.proteinQuantity.getValue());
    assertTrue(form.proteinQuantity.isReadOnly());
    assertEquals(ProteolyticDigestion.TRYPSIN, form.digestion.getValue());
    assertTrue(form.digestion.isReadOnly());
    assertEquals("", form.usedDigestion.getValue());
    assertTrue(form.usedDigestion.isReadOnly());
    assertEquals("", form.otherDigestion.getValue());
    assertTrue(form.otherDigestion.isReadOnly());
    assertEquals(ProteinContent.MEDIUM, form.proteinContent.getValue());
    assertTrue(form.proteinContent.isReadOnly());
    assertEquals(MassDetectionInstrument.LTQ_ORBI_TRAP, form.instrument.getValue());
    assertTrue(form.instrument.isReadOnly());
    assertEquals(ProteinIdentification.NCBINR, form.identification.getValue());
    assertTrue(form.identification.isReadOnly());
    assertEquals("", form.identificationLink.getValue());
    assertTrue(form.identificationLink.isReadOnly());
    assertEquals(Quantification.NULL, form.quantification.getValue());
    assertTrue(form.quantification.isReadOnly());
    assertEquals("", form.quantificationComment.getValue());
    assertTrue(form.quantificationComment.isReadOnly());
  }

  @Test
  public void setSubmission_NullInstrument() {
    Submission submission = submission();
    submission.setInstrument(null);
    form.setSubmission(submission);

    assertEquals(MassDetectionInstrument.NULL, form.instrument.getValue());
  }

  @Test
  public void setSubmission_NullQuantification() {
    Submission submission = submission();
    submission.setQuantification(null);
    form.setSubmission(submission);

    assertEquals(Quantification.NULL, form.quantification.getValue());
  }
}
