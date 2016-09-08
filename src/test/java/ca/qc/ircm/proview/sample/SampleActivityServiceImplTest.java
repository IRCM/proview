package ca.qc.ircm.proview.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MsAnalysis.Source;
import ca.qc.ircm.proview.sample.GelSample.Coloration;
import ca.qc.ircm.proview.sample.GelSample.Separation;
import ca.qc.ircm.proview.sample.GelSample.Thickness;
import ca.qc.ircm.proview.sample.MoleculeSample.StorageTemperature;
import ca.qc.ircm.proview.sample.ProteicSample.EnrichmentType;
import ca.qc.ircm.proview.sample.ProteicSample.MudPitFraction;
import ca.qc.ircm.proview.sample.ProteicSample.ProteinContent;
import ca.qc.ircm.proview.sample.Sample.Support;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SampleActivityServiceImplTest {
  private SampleActivityServiceImpl sampleActivityServiceImpl;
  @PersistenceContext
  private EntityManager entityManager;
  @Mock
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    sampleActivityServiceImpl = new SampleActivityServiceImpl(entityManager, authorizationService);
    user = new User(4L, "sylvain.tessier@ircm.qc.ca");
    when(authorizationService.getCurrentUser()).thenReturn(user);
  }

  private SampleSolvent find(Collection<SampleSolvent> solvents, Solvent solvent) {
    for (SampleSolvent ssolvent : solvents) {
      if (ssolvent.getSolvent() == solvent) {
        return ssolvent;
      }
    }
    return null;
  }

  @Test
  public void insertControl() {
    Control control = new Control();
    control.setId(123456L);
    control.setName("unit_test_control");
    control.setQuantity("200.0 μg");
    control.setSupport(Sample.Support.SOLUTION);
    control.setControlType(Control.ControlType.NEGATIVE_CONTROL);
    control.setVolume(300.0);

    Activity activity = sampleActivityServiceImpl.insertControl(control);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update_GelSample() {
    GelSample gelSample = entityManager.find(GelSample.class, 1L);
    entityManager.detach(gelSample);
    gelSample.setComments("my_new_comment");
    gelSample.setName("new_gel_tag_0001");
    gelSample.setProject("my_project");
    gelSample.setExperience("my_experience");
    gelSample.setGoal("my_goal");
    gelSample.setSource(Source.LDTD);
    gelSample.setSampleNumberProtein(2);
    gelSample.setProteolyticDigestionMethod(ProteolyticDigestion.DIGESTED);
    gelSample.setUsedProteolyticDigestionMethod("Trypsine");
    gelSample.setOtherProteolyticDigestionMethod("None");
    gelSample.setProteinIdentification(ProteinIdentification.OTHER);
    gelSample.setProteinIdentificationLink("http://cou24/my_database");
    gelSample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    gelSample.setOtherEnrichmentType("Phosphopeptides");
    gelSample.setMudPitFraction(MudPitFraction.TWELVE);
    gelSample.setProteinContent(ProteinContent.LARGE);
    gelSample.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    gelSample.setService(Service.MALDI_MS);
    gelSample.setTaxonomy("mouse");
    gelSample.setProtein("my_protein");
    gelSample.setMolecularWeight(20.0);
    gelSample.setPostTranslationModification("my_modification");
    gelSample.setSeparation(Separation.TWO_DIMENSION);
    gelSample.setThickness(Thickness.ONE_HALF);
    gelSample.setColoration(Coloration.OTHER);
    gelSample.setOtherColoration("my_coloration");
    gelSample.setDevelopmentTime("2.0 min");
    gelSample.setDecoloration(true);
    gelSample.setWeightMarkerQuantity(2.5);
    gelSample.setProteinQuantity("12.0 pmol");
    gelSample.setAdditionalPrice(new BigDecimal("21.50"));

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(gelSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(gelSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("sample");
    commentsActivity.setRecordId(gelSample.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("Philippe");
    commentsActivity.setNewValue("my_new_comment");
    expectedUpdateActivities.add(commentsActivity);
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(gelSample.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("FAM119A_band_01");
    nameActivity.setNewValue("new_gel_tag_0001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity projectActivity = new UpdateActivity();
    projectActivity.setActionType(ActionType.UPDATE);
    projectActivity.setTableName("sample");
    projectActivity.setRecordId(gelSample.getId());
    projectActivity.setColumn("project");
    projectActivity.setOldValue("Coulombe");
    projectActivity.setNewValue("my_project");
    expectedUpdateActivities.add(projectActivity);
    UpdateActivity experienceActivity = new UpdateActivity();
    experienceActivity.setActionType(ActionType.UPDATE);
    experienceActivity.setTableName("sample");
    experienceActivity.setRecordId(gelSample.getId());
    experienceActivity.setColumn("experience");
    experienceActivity.setOldValue("G100429");
    experienceActivity.setNewValue("my_experience");
    expectedUpdateActivities.add(experienceActivity);
    UpdateActivity goalActivity = new UpdateActivity();
    goalActivity.setActionType(ActionType.UPDATE);
    goalActivity.setTableName("sample");
    goalActivity.setRecordId(gelSample.getId());
    goalActivity.setColumn("goal");
    goalActivity.setOldValue(null);
    goalActivity.setNewValue("my_goal");
    expectedUpdateActivities.add(goalActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName("sample");
    sourceActivity.setRecordId(gelSample.getId());
    sourceActivity.setColumn("source");
    sourceActivity.setOldValue(null);
    sourceActivity.setNewValue(Source.LDTD.name());
    expectedUpdateActivities.add(sourceActivity);
    UpdateActivity sampleNumberProteinActivity = new UpdateActivity();
    sampleNumberProteinActivity.setActionType(ActionType.UPDATE);
    sampleNumberProteinActivity.setTableName("sample");
    sampleNumberProteinActivity.setRecordId(gelSample.getId());
    sampleNumberProteinActivity.setColumn("sampleNumberProtein");
    sampleNumberProteinActivity.setOldValue(null);
    sampleNumberProteinActivity.setNewValue("2");
    expectedUpdateActivities.add(sampleNumberProteinActivity);
    UpdateActivity proteolyticDigestionMethodActivity = new UpdateActivity();
    proteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    proteolyticDigestionMethodActivity.setTableName("sample");
    proteolyticDigestionMethodActivity.setRecordId(gelSample.getId());
    proteolyticDigestionMethodActivity.setColumn("proteolyticDigestionMethod");
    proteolyticDigestionMethodActivity.setOldValue("TRYPSIN");
    proteolyticDigestionMethodActivity.setNewValue(ProteolyticDigestion.DIGESTED.name());
    expectedUpdateActivities.add(proteolyticDigestionMethodActivity);
    UpdateActivity usedProteolyticDigestionMethodActivity = new UpdateActivity();
    usedProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    usedProteolyticDigestionMethodActivity.setTableName("sample");
    usedProteolyticDigestionMethodActivity.setRecordId(gelSample.getId());
    usedProteolyticDigestionMethodActivity.setColumn("usedProteolyticDigestionMethod");
    usedProteolyticDigestionMethodActivity.setOldValue(null);
    usedProteolyticDigestionMethodActivity.setNewValue("Trypsine");
    expectedUpdateActivities.add(usedProteolyticDigestionMethodActivity);
    UpdateActivity otherProteolyticDigestionMethodActivity = new UpdateActivity();
    otherProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    otherProteolyticDigestionMethodActivity.setTableName("sample");
    otherProteolyticDigestionMethodActivity.setRecordId(gelSample.getId());
    otherProteolyticDigestionMethodActivity.setColumn("otherProteolyticDigestionMethod");
    otherProteolyticDigestionMethodActivity.setOldValue(null);
    otherProteolyticDigestionMethodActivity.setNewValue("None");
    expectedUpdateActivities.add(otherProteolyticDigestionMethodActivity);
    UpdateActivity proteinIdentificationActivity = new UpdateActivity();
    proteinIdentificationActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationActivity.setTableName("sample");
    proteinIdentificationActivity.setRecordId(gelSample.getId());
    proteinIdentificationActivity.setColumn("proteinIdentification");
    proteinIdentificationActivity.setOldValue("NCBINR");
    proteinIdentificationActivity.setNewValue(ProteinIdentification.OTHER.name());
    expectedUpdateActivities.add(proteinIdentificationActivity);
    UpdateActivity proteinIdentificationLinkActivity = new UpdateActivity();
    proteinIdentificationLinkActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationLinkActivity.setTableName("sample");
    proteinIdentificationLinkActivity.setRecordId(gelSample.getId());
    proteinIdentificationLinkActivity.setColumn("proteinIdentificationLink");
    proteinIdentificationLinkActivity.setOldValue(null);
    proteinIdentificationLinkActivity.setNewValue("http://cou24/my_database");
    expectedUpdateActivities.add(proteinIdentificationLinkActivity);
    UpdateActivity enrichmentTypeActivity = new UpdateActivity();
    enrichmentTypeActivity.setActionType(ActionType.UPDATE);
    enrichmentTypeActivity.setTableName("sample");
    enrichmentTypeActivity.setRecordId(gelSample.getId());
    enrichmentTypeActivity.setColumn("enrichmentType");
    enrichmentTypeActivity.setOldValue(null);
    enrichmentTypeActivity.setNewValue(EnrichmentType.PHOSPHOPEPTIDES.name());
    expectedUpdateActivities.add(enrichmentTypeActivity);
    UpdateActivity otherEnrichmentTypeActivity = new UpdateActivity();
    otherEnrichmentTypeActivity.setActionType(ActionType.UPDATE);
    otherEnrichmentTypeActivity.setTableName("sample");
    otherEnrichmentTypeActivity.setRecordId(gelSample.getId());
    otherEnrichmentTypeActivity.setColumn("otherEnrichmentType");
    otherEnrichmentTypeActivity.setOldValue(null);
    otherEnrichmentTypeActivity.setNewValue("Phosphopeptides");
    expectedUpdateActivities.add(otherEnrichmentTypeActivity);
    UpdateActivity mudPitFractionActivity = new UpdateActivity();
    mudPitFractionActivity.setActionType(ActionType.UPDATE);
    mudPitFractionActivity.setTableName("sample");
    mudPitFractionActivity.setRecordId(gelSample.getId());
    mudPitFractionActivity.setColumn("mudPitFraction");
    mudPitFractionActivity.setOldValue(null);
    mudPitFractionActivity.setNewValue(MudPitFraction.TWELVE.name());
    expectedUpdateActivities.add(mudPitFractionActivity);
    UpdateActivity proteinContentActivity = new UpdateActivity();
    proteinContentActivity.setActionType(ActionType.UPDATE);
    proteinContentActivity.setTableName("sample");
    proteinContentActivity.setRecordId(gelSample.getId());
    proteinContentActivity.setColumn("proteinContent");
    proteinContentActivity.setOldValue("XLARGE");
    proteinContentActivity.setNewValue(ProteinContent.LARGE.name());
    expectedUpdateActivities.add(proteinContentActivity);
    UpdateActivity massDetectionInstrumentActivity = new UpdateActivity();
    massDetectionInstrumentActivity.setActionType(ActionType.UPDATE);
    massDetectionInstrumentActivity.setTableName("sample");
    massDetectionInstrumentActivity.setRecordId(gelSample.getId());
    massDetectionInstrumentActivity.setColumn("massDetectionInstrument");
    massDetectionInstrumentActivity.setOldValue("LTQ_ORBI_TRAP");
    massDetectionInstrumentActivity.setNewValue(MassDetectionInstrument.TOF.name());
    expectedUpdateActivities.add(massDetectionInstrumentActivity);
    UpdateActivity serviceActivity = new UpdateActivity();
    serviceActivity.setActionType(ActionType.UPDATE);
    serviceActivity.setTableName("sample");
    serviceActivity.setRecordId(gelSample.getId());
    serviceActivity.setColumn("service");
    serviceActivity.setOldValue(Service.LC_MS_MS.name());
    serviceActivity.setNewValue(Service.MALDI_MS.name());
    expectedUpdateActivities.add(serviceActivity);
    UpdateActivity taxonomyActivity = new UpdateActivity();
    taxonomyActivity.setActionType(ActionType.UPDATE);
    taxonomyActivity.setTableName("sample");
    taxonomyActivity.setRecordId(gelSample.getId());
    taxonomyActivity.setColumn("taxonomy");
    taxonomyActivity.setOldValue("Human");
    taxonomyActivity.setNewValue("mouse");
    expectedUpdateActivities.add(taxonomyActivity);
    UpdateActivity proteinActivity = new UpdateActivity();
    proteinActivity.setActionType(ActionType.UPDATE);
    proteinActivity.setTableName("sample");
    proteinActivity.setRecordId(gelSample.getId());
    proteinActivity.setColumn("protein");
    proteinActivity.setOldValue(null);
    proteinActivity.setNewValue("my_protein");
    expectedUpdateActivities.add(proteinActivity);
    UpdateActivity molecularWeightActivity = new UpdateActivity();
    molecularWeightActivity.setActionType(ActionType.UPDATE);
    molecularWeightActivity.setTableName("sample");
    molecularWeightActivity.setRecordId(gelSample.getId());
    molecularWeightActivity.setColumn("molecularWeight");
    molecularWeightActivity.setOldValue(null);
    molecularWeightActivity.setNewValue("20.0");
    expectedUpdateActivities.add(molecularWeightActivity);
    UpdateActivity postTranslationModificationActivity = new UpdateActivity();
    postTranslationModificationActivity.setActionType(ActionType.UPDATE);
    postTranslationModificationActivity.setTableName("sample");
    postTranslationModificationActivity.setRecordId(gelSample.getId());
    postTranslationModificationActivity.setColumn("postTranslationModification");
    postTranslationModificationActivity.setOldValue(null);
    postTranslationModificationActivity.setNewValue("my_modification");
    expectedUpdateActivities.add(postTranslationModificationActivity);
    UpdateActivity separationActivity = new UpdateActivity();
    separationActivity.setActionType(ActionType.UPDATE);
    separationActivity.setTableName("sample");
    separationActivity.setRecordId(gelSample.getId());
    separationActivity.setColumn("separation");
    separationActivity.setOldValue("ONE_DIMENSION");
    separationActivity.setNewValue(Separation.TWO_DIMENSION.name());
    expectedUpdateActivities.add(separationActivity);
    UpdateActivity thicknessActivity = new UpdateActivity();
    thicknessActivity.setActionType(ActionType.UPDATE);
    thicknessActivity.setTableName("sample");
    thicknessActivity.setRecordId(gelSample.getId());
    thicknessActivity.setColumn("thickness");
    thicknessActivity.setOldValue("ONE");
    thicknessActivity.setNewValue(Thickness.ONE_HALF.name());
    expectedUpdateActivities.add(thicknessActivity);
    UpdateActivity colorationActivity = new UpdateActivity();
    colorationActivity.setActionType(ActionType.UPDATE);
    colorationActivity.setTableName("sample");
    colorationActivity.setRecordId(gelSample.getId());
    colorationActivity.setColumn("coloration");
    colorationActivity.setOldValue("SILVER");
    colorationActivity.setNewValue(Coloration.OTHER.name());
    expectedUpdateActivities.add(colorationActivity);
    UpdateActivity otherColorationActivity = new UpdateActivity();
    otherColorationActivity.setActionType(ActionType.UPDATE);
    otherColorationActivity.setTableName("sample");
    otherColorationActivity.setRecordId(gelSample.getId());
    otherColorationActivity.setColumn("otherColoration");
    otherColorationActivity.setOldValue(null);
    otherColorationActivity.setNewValue("my_coloration");
    expectedUpdateActivities.add(otherColorationActivity);
    UpdateActivity developmentTimeActivity = new UpdateActivity();
    developmentTimeActivity.setActionType(ActionType.UPDATE);
    developmentTimeActivity.setTableName("sample");
    developmentTimeActivity.setRecordId(gelSample.getId());
    developmentTimeActivity.setColumn("developmentTime");
    developmentTimeActivity.setOldValue(null);
    developmentTimeActivity.setNewValue("2.0 min");
    expectedUpdateActivities.add(developmentTimeActivity);
    UpdateActivity decolorationActivity = new UpdateActivity();
    decolorationActivity.setActionType(ActionType.UPDATE);
    decolorationActivity.setTableName("sample");
    decolorationActivity.setRecordId(gelSample.getId());
    decolorationActivity.setColumn("decoloration");
    decolorationActivity.setOldValue("0");
    decolorationActivity.setNewValue("1");
    expectedUpdateActivities.add(decolorationActivity);
    UpdateActivity weightMarkerQuantityActivity = new UpdateActivity();
    weightMarkerQuantityActivity.setActionType(ActionType.UPDATE);
    weightMarkerQuantityActivity.setTableName("sample");
    weightMarkerQuantityActivity.setRecordId(gelSample.getId());
    weightMarkerQuantityActivity.setColumn("weightMarkerQuantity");
    weightMarkerQuantityActivity.setOldValue(null);
    weightMarkerQuantityActivity.setNewValue("2.5");
    expectedUpdateActivities.add(weightMarkerQuantityActivity);
    UpdateActivity proteinQuantityActivity = new UpdateActivity();
    proteinQuantityActivity.setActionType(ActionType.UPDATE);
    proteinQuantityActivity.setTableName("sample");
    proteinQuantityActivity.setRecordId(gelSample.getId());
    proteinQuantityActivity.setColumn("proteinQuantity");
    proteinQuantityActivity.setOldValue(null);
    proteinQuantityActivity.setNewValue("12.0 pmol");
    expectedUpdateActivities.add(proteinQuantityActivity);
    UpdateActivity additionalPriceActivity = new UpdateActivity();
    additionalPriceActivity.setActionType(ActionType.UPDATE);
    additionalPriceActivity.setTableName("sample");
    additionalPriceActivity.setRecordId(gelSample.getId());
    additionalPriceActivity.setColumn("additionalPrice");
    additionalPriceActivity.setOldValue(null);
    additionalPriceActivity.setNewValue("21.50");
    expectedUpdateActivities.add(additionalPriceActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(eluateSample);
    eluateSample.setComments("my_new_comment");
    eluateSample.setName("new_solution_tag_0001");
    eluateSample.setProject("my_project");
    eluateSample.setExperience("my_experience");
    eluateSample.setGoal("my_goal");
    eluateSample.setSource(Source.LDTD);
    eluateSample.setSampleNumberProtein(2);
    eluateSample.setProteolyticDigestionMethod(ProteolyticDigestion.DIGESTED);
    eluateSample.setUsedProteolyticDigestionMethod("Trypsine");
    eluateSample.setOtherProteolyticDigestionMethod("None");
    eluateSample.setProteinIdentification(ProteinIdentification.OTHER);
    eluateSample.setProteinIdentificationLink("http://cou24/my_database");
    eluateSample.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    eluateSample.setOtherEnrichmentType("Phosphopeptides");
    eluateSample.setMudPitFraction(MudPitFraction.TWELVE);
    eluateSample.setProteinContent(ProteinContent.LARGE);
    eluateSample.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    eluateSample.setService(Service.MALDI_MS);
    eluateSample.setTaxonomy("mouse");
    eluateSample.setProtein("my_protein");
    eluateSample.setMolecularWeight(20.0);
    eluateSample.setPostTranslationModification("my_modification");
    eluateSample.setSupport(Support.DRY);
    eluateSample.setQuantity("12 pmol");
    eluateSample.setVolume(70.0);
    eluateSample.setAdditionalPrice(new BigDecimal("21.50"));

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("sample");
    commentsActivity.setRecordId(eluateSample.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue(null);
    commentsActivity.setNewValue("my_new_comment");
    expectedUpdateActivities.add(commentsActivity);
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(eluateSample.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("CAP_20111013_01");
    nameActivity.setNewValue("new_solution_tag_0001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity projectActivity = new UpdateActivity();
    projectActivity.setActionType(ActionType.UPDATE);
    projectActivity.setTableName("sample");
    projectActivity.setRecordId(eluateSample.getId());
    projectActivity.setColumn("project");
    projectActivity.setOldValue("cap_project");
    projectActivity.setNewValue("my_project");
    expectedUpdateActivities.add(projectActivity);
    UpdateActivity experienceActivity = new UpdateActivity();
    experienceActivity.setActionType(ActionType.UPDATE);
    experienceActivity.setTableName("sample");
    experienceActivity.setRecordId(eluateSample.getId());
    experienceActivity.setColumn("experience");
    experienceActivity.setOldValue("cap_experience");
    experienceActivity.setNewValue("my_experience");
    expectedUpdateActivities.add(experienceActivity);
    UpdateActivity goalActivity = new UpdateActivity();
    goalActivity.setActionType(ActionType.UPDATE);
    goalActivity.setTableName("sample");
    goalActivity.setRecordId(eluateSample.getId());
    goalActivity.setColumn("goal");
    goalActivity.setOldValue("cap_goal");
    goalActivity.setNewValue("my_goal");
    expectedUpdateActivities.add(goalActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName("sample");
    sourceActivity.setRecordId(eluateSample.getId());
    sourceActivity.setColumn("source");
    sourceActivity.setOldValue(null);
    sourceActivity.setNewValue(Source.LDTD.name());
    expectedUpdateActivities.add(sourceActivity);
    UpdateActivity sampleNumberProteinActivity = new UpdateActivity();
    sampleNumberProteinActivity.setActionType(ActionType.UPDATE);
    sampleNumberProteinActivity.setTableName("sample");
    sampleNumberProteinActivity.setRecordId(eluateSample.getId());
    sampleNumberProteinActivity.setColumn("sampleNumberProtein");
    sampleNumberProteinActivity.setOldValue(null);
    sampleNumberProteinActivity.setNewValue("2");
    expectedUpdateActivities.add(sampleNumberProteinActivity);
    UpdateActivity proteolyticDigestionMethodActivity = new UpdateActivity();
    proteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    proteolyticDigestionMethodActivity.setTableName("sample");
    proteolyticDigestionMethodActivity.setRecordId(eluateSample.getId());
    proteolyticDigestionMethodActivity.setColumn("proteolyticDigestionMethod");
    proteolyticDigestionMethodActivity.setOldValue("TRYPSIN");
    proteolyticDigestionMethodActivity.setNewValue(ProteolyticDigestion.DIGESTED.name());
    expectedUpdateActivities.add(proteolyticDigestionMethodActivity);
    UpdateActivity usedProteolyticDigestionMethodActivity = new UpdateActivity();
    usedProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    usedProteolyticDigestionMethodActivity.setTableName("sample");
    usedProteolyticDigestionMethodActivity.setRecordId(eluateSample.getId());
    usedProteolyticDigestionMethodActivity.setColumn("usedProteolyticDigestionMethod");
    usedProteolyticDigestionMethodActivity.setOldValue(null);
    usedProteolyticDigestionMethodActivity.setNewValue("Trypsine");
    expectedUpdateActivities.add(usedProteolyticDigestionMethodActivity);
    UpdateActivity otherProteolyticDigestionMethodActivity = new UpdateActivity();
    otherProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    otherProteolyticDigestionMethodActivity.setTableName("sample");
    otherProteolyticDigestionMethodActivity.setRecordId(eluateSample.getId());
    otherProteolyticDigestionMethodActivity.setColumn("otherProteolyticDigestionMethod");
    otherProteolyticDigestionMethodActivity.setOldValue(null);
    otherProteolyticDigestionMethodActivity.setNewValue("None");
    expectedUpdateActivities.add(otherProteolyticDigestionMethodActivity);
    UpdateActivity proteinIdentificationActivity = new UpdateActivity();
    proteinIdentificationActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationActivity.setTableName("sample");
    proteinIdentificationActivity.setRecordId(eluateSample.getId());
    proteinIdentificationActivity.setColumn("proteinIdentification");
    proteinIdentificationActivity.setOldValue("NCBINR");
    proteinIdentificationActivity.setNewValue(ProteinIdentification.OTHER.name());
    expectedUpdateActivities.add(proteinIdentificationActivity);
    UpdateActivity proteinIdentificationLinkActivity = new UpdateActivity();
    proteinIdentificationLinkActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationLinkActivity.setTableName("sample");
    proteinIdentificationLinkActivity.setRecordId(eluateSample.getId());
    proteinIdentificationLinkActivity.setColumn("proteinIdentificationLink");
    proteinIdentificationLinkActivity.setOldValue(null);
    proteinIdentificationLinkActivity.setNewValue("http://cou24/my_database");
    expectedUpdateActivities.add(proteinIdentificationLinkActivity);
    UpdateActivity enrichmentTypeActivity = new UpdateActivity();
    enrichmentTypeActivity.setActionType(ActionType.UPDATE);
    enrichmentTypeActivity.setTableName("sample");
    enrichmentTypeActivity.setRecordId(eluateSample.getId());
    enrichmentTypeActivity.setColumn("enrichmentType");
    enrichmentTypeActivity.setOldValue(null);
    enrichmentTypeActivity.setNewValue(EnrichmentType.PHOSPHOPEPTIDES.name());
    expectedUpdateActivities.add(enrichmentTypeActivity);
    UpdateActivity otherEnrichmentTypeActivity = new UpdateActivity();
    otherEnrichmentTypeActivity.setActionType(ActionType.UPDATE);
    otherEnrichmentTypeActivity.setTableName("sample");
    otherEnrichmentTypeActivity.setRecordId(eluateSample.getId());
    otherEnrichmentTypeActivity.setColumn("otherEnrichmentType");
    otherEnrichmentTypeActivity.setOldValue(null);
    otherEnrichmentTypeActivity.setNewValue("Phosphopeptides");
    expectedUpdateActivities.add(otherEnrichmentTypeActivity);
    UpdateActivity mudPitFractionActivity = new UpdateActivity();
    mudPitFractionActivity.setActionType(ActionType.UPDATE);
    mudPitFractionActivity.setTableName("sample");
    mudPitFractionActivity.setRecordId(eluateSample.getId());
    mudPitFractionActivity.setColumn("mudPitFraction");
    mudPitFractionActivity.setOldValue(null);
    mudPitFractionActivity.setNewValue(MudPitFraction.TWELVE.name());
    expectedUpdateActivities.add(mudPitFractionActivity);
    UpdateActivity proteinContentActivity = new UpdateActivity();
    proteinContentActivity.setActionType(ActionType.UPDATE);
    proteinContentActivity.setTableName("sample");
    proteinContentActivity.setRecordId(eluateSample.getId());
    proteinContentActivity.setColumn("proteinContent");
    proteinContentActivity.setOldValue("MEDIUM");
    proteinContentActivity.setNewValue(ProteinContent.LARGE.name());
    expectedUpdateActivities.add(proteinContentActivity);
    UpdateActivity massDetectionInstrumentActivity = new UpdateActivity();
    massDetectionInstrumentActivity.setActionType(ActionType.UPDATE);
    massDetectionInstrumentActivity.setTableName("sample");
    massDetectionInstrumentActivity.setRecordId(eluateSample.getId());
    massDetectionInstrumentActivity.setColumn("massDetectionInstrument");
    massDetectionInstrumentActivity.setOldValue("LTQ_ORBI_TRAP");
    massDetectionInstrumentActivity.setNewValue(MassDetectionInstrument.TOF.name());
    expectedUpdateActivities.add(massDetectionInstrumentActivity);
    UpdateActivity serviceActivity = new UpdateActivity();
    serviceActivity.setActionType(ActionType.UPDATE);
    serviceActivity.setTableName("sample");
    serviceActivity.setRecordId(eluateSample.getId());
    serviceActivity.setColumn("service");
    serviceActivity.setOldValue(Service.LC_MS_MS.name());
    serviceActivity.setNewValue(Service.MALDI_MS.name());
    expectedUpdateActivities.add(serviceActivity);
    UpdateActivity taxonomyActivity = new UpdateActivity();
    taxonomyActivity.setActionType(ActionType.UPDATE);
    taxonomyActivity.setTableName("sample");
    taxonomyActivity.setRecordId(eluateSample.getId());
    taxonomyActivity.setColumn("taxonomy");
    taxonomyActivity.setOldValue("human");
    taxonomyActivity.setNewValue("mouse");
    expectedUpdateActivities.add(taxonomyActivity);
    UpdateActivity proteinActivity = new UpdateActivity();
    proteinActivity.setActionType(ActionType.UPDATE);
    proteinActivity.setTableName("sample");
    proteinActivity.setRecordId(eluateSample.getId());
    proteinActivity.setColumn("protein");
    proteinActivity.setOldValue(null);
    proteinActivity.setNewValue("my_protein");
    expectedUpdateActivities.add(proteinActivity);
    UpdateActivity molecularWeightActivity = new UpdateActivity();
    molecularWeightActivity.setActionType(ActionType.UPDATE);
    molecularWeightActivity.setTableName("sample");
    molecularWeightActivity.setRecordId(eluateSample.getId());
    molecularWeightActivity.setColumn("molecularWeight");
    molecularWeightActivity.setOldValue(null);
    molecularWeightActivity.setNewValue("20.0");
    expectedUpdateActivities.add(molecularWeightActivity);
    UpdateActivity postTranslationModificationActivity = new UpdateActivity();
    postTranslationModificationActivity.setActionType(ActionType.UPDATE);
    postTranslationModificationActivity.setTableName("sample");
    postTranslationModificationActivity.setRecordId(eluateSample.getId());
    postTranslationModificationActivity.setColumn("postTranslationModification");
    postTranslationModificationActivity.setOldValue(null);
    postTranslationModificationActivity.setNewValue("my_modification");
    expectedUpdateActivities.add(postTranslationModificationActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName("sample");
    supportActivity.setRecordId(eluateSample.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(Sample.Support.SOLUTION.name());
    supportActivity.setNewValue(Support.DRY.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("sample");
    quantityActivity.setRecordId(eluateSample.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("1.5 μg");
    quantityActivity.setNewValue("12 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName("sample");
    volumeActivity.setRecordId(eluateSample.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue("50.0");
    volumeActivity.setNewValue("70.0");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity additionalPriceActivity = new UpdateActivity();
    additionalPriceActivity.setActionType(ActionType.UPDATE);
    additionalPriceActivity.setTableName("sample");
    additionalPriceActivity.setRecordId(eluateSample.getId());
    additionalPriceActivity.setColumn("additionalPrice");
    additionalPriceActivity.setOldValue(null);
    additionalPriceActivity.setNewValue("21.50");
    expectedUpdateActivities.add(additionalPriceActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_AddContaminants() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(eluateSample);
    Contaminant contaminant = new Contaminant();
    contaminant.setId(57894121L);
    contaminant.setName("my_new_contaminant");
    contaminant.setQuantity("3 μg");
    contaminant.setComments("some_comments");
    eluateSample.getContaminants().add(contaminant);

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity addContaminantActivity = new UpdateActivity();
    addContaminantActivity.setActionType(ActionType.INSERT);
    addContaminantActivity.setTableName("contaminant");
    addContaminantActivity.setRecordId(contaminant.getId());
    expectedUpdateActivities.add(addContaminantActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_UpdateContaminants() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 447L);
    entityManager.detach(eluateSample);
    for (Contaminant contaminant : eluateSample.getContaminants()) {
      entityManager.detach(contaminant);
    }
    Contaminant contaminant = eluateSample.getContaminants().get(0);
    contaminant.setName("new_contaminant_name");
    contaminant.setQuantity("1 pmol");
    contaminant.setComments("new_comments");

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("contaminant");
    nameActivity.setRecordId(contaminant.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_contaminant");
    nameActivity.setNewValue("new_contaminant_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("contaminant");
    quantityActivity.setRecordId(contaminant.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("contaminant");
    commentsActivity.setRecordId(contaminant.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_RemoveContaminant() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 447L);
    entityManager.detach(eluateSample);
    final Contaminant contaminant = eluateSample.getContaminants().get(0);
    eluateSample.getContaminants().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("contaminant");
    removeActivity.setRecordId(contaminant.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_AddStandard() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 442L);
    entityManager.detach(eluateSample);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComments("some_comments");
    eluateSample.getStandards().add(standard);

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity addStandardActivity = new UpdateActivity();
    addStandardActivity.setActionType(ActionType.INSERT);
    addStandardActivity.setTableName("standard");
    addStandardActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(addStandardActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_UpdateStandard() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 447L);
    entityManager.detach(eluateSample);
    for (Standard standard : eluateSample.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = eluateSample.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComments("new_comments");

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("standard");
    nameActivity.setRecordId(standard.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_standard");
    nameActivity.setNewValue("new_standard_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("standard");
    quantityActivity.setRecordId(standard.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("standard");
    commentsActivity.setRecordId(standard.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_EluateSample_RemoveStandard() {
    EluateSample eluateSample = entityManager.find(EluateSample.class, 447L);
    entityManager.detach(eluateSample);
    final Standard standard = eluateSample.getStandards().get(0);
    eluateSample.getStandards().remove(0);

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(eluateSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(eluateSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("standard");
    removeActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_MoleculeSample() {
    MoleculeSample moleculeSample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(moleculeSample);
    moleculeSample.setName("new_molecule_tag_0001");
    moleculeSample.setComments("my_new_comment");
    moleculeSample.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    moleculeSample.setSource(Source.LDTD);
    moleculeSample.setSupport(Support.DRY);
    moleculeSample.setFormula("h2o");
    moleculeSample.setMonoisotopicMass(18.0);
    moleculeSample.setAverageMass(18.0);
    moleculeSample.setSolutionSolvent("ethanol");
    moleculeSample.setOtherSolvent("ch3oh");
    moleculeSample.setToxicity("die");
    moleculeSample.setLightSensitive(true);
    moleculeSample.setStorageTemperature(StorageTemperature.LOW);
    moleculeSample.setLowResolution(false);
    moleculeSample.setHighResolution(true);
    moleculeSample.setMsms(true);
    moleculeSample.setExactMsms(true);
    moleculeSample.setAdditionalPrice(new BigDecimal("21.50"));

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(moleculeSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(moleculeSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(moleculeSample.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("CAP_20111013_05");
    nameActivity.setNewValue("new_molecule_tag_0001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("sample");
    commentsActivity.setRecordId(moleculeSample.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue(null);
    commentsActivity.setNewValue("my_new_comment");
    expectedUpdateActivities.add(commentsActivity);
    UpdateActivity massDetectionInstrumentActivity = new UpdateActivity();
    massDetectionInstrumentActivity.setActionType(ActionType.UPDATE);
    massDetectionInstrumentActivity.setTableName("sample");
    massDetectionInstrumentActivity.setRecordId(moleculeSample.getId());
    massDetectionInstrumentActivity.setColumn("massDetectionInstrument");
    massDetectionInstrumentActivity.setOldValue(null);
    massDetectionInstrumentActivity.setNewValue(MassDetectionInstrument.TOF.name());
    expectedUpdateActivities.add(massDetectionInstrumentActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName("sample");
    sourceActivity.setRecordId(moleculeSample.getId());
    sourceActivity.setColumn("source");
    sourceActivity.setOldValue("ESI");
    sourceActivity.setNewValue(Source.LDTD.name());
    expectedUpdateActivities.add(sourceActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName("sample");
    supportActivity.setRecordId(moleculeSample.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(Support.SOLUTION.name());
    supportActivity.setNewValue(Support.DRY.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity formulaActivity = new UpdateActivity();
    formulaActivity.setActionType(ActionType.UPDATE);
    formulaActivity.setTableName("sample");
    formulaActivity.setRecordId(moleculeSample.getId());
    formulaActivity.setColumn("formula");
    formulaActivity.setOldValue("C100H100O100");
    formulaActivity.setNewValue("h2o");
    expectedUpdateActivities.add(formulaActivity);
    UpdateActivity monoisotopicMassActivity = new UpdateActivity();
    monoisotopicMassActivity.setActionType(ActionType.UPDATE);
    monoisotopicMassActivity.setTableName("sample");
    monoisotopicMassActivity.setRecordId(moleculeSample.getId());
    monoisotopicMassActivity.setColumn("monoisotopicMass");
    monoisotopicMassActivity.setOldValue("654.654");
    monoisotopicMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(monoisotopicMassActivity);
    UpdateActivity averageMassActivity = new UpdateActivity();
    averageMassActivity.setActionType(ActionType.UPDATE);
    averageMassActivity.setTableName("sample");
    averageMassActivity.setRecordId(moleculeSample.getId());
    averageMassActivity.setColumn("averageMass");
    averageMassActivity.setOldValue("654.654");
    averageMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(averageMassActivity);
    UpdateActivity solutionSolventActivity = new UpdateActivity();
    solutionSolventActivity.setActionType(ActionType.UPDATE);
    solutionSolventActivity.setTableName("sample");
    solutionSolventActivity.setRecordId(moleculeSample.getId());
    solutionSolventActivity.setColumn("solutionSolvent");
    solutionSolventActivity.setOldValue("MeOH/TFA 0.1%");
    solutionSolventActivity.setNewValue("ethanol");
    expectedUpdateActivities.add(solutionSolventActivity);
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName("sample");
    otherSolventActivity.setRecordId(moleculeSample.getId());
    otherSolventActivity.setColumn("otherSolvent");
    otherSolventActivity.setOldValue(null);
    otherSolventActivity.setNewValue("ch3oh");
    expectedUpdateActivities.add(otherSolventActivity);
    UpdateActivity toxicityActivity = new UpdateActivity();
    toxicityActivity.setActionType(ActionType.UPDATE);
    toxicityActivity.setTableName("sample");
    toxicityActivity.setRecordId(moleculeSample.getId());
    toxicityActivity.setColumn("toxicity");
    toxicityActivity.setOldValue(null);
    toxicityActivity.setNewValue("die");
    expectedUpdateActivities.add(toxicityActivity);
    UpdateActivity lightSensitiveActivity = new UpdateActivity();
    lightSensitiveActivity.setActionType(ActionType.UPDATE);
    lightSensitiveActivity.setTableName("sample");
    lightSensitiveActivity.setRecordId(moleculeSample.getId());
    lightSensitiveActivity.setColumn("lightSensitive");
    lightSensitiveActivity.setOldValue("0");
    lightSensitiveActivity.setNewValue("1");
    expectedUpdateActivities.add(lightSensitiveActivity);
    UpdateActivity storageTemperatureActivity = new UpdateActivity();
    storageTemperatureActivity.setActionType(ActionType.UPDATE);
    storageTemperatureActivity.setTableName("sample");
    storageTemperatureActivity.setRecordId(moleculeSample.getId());
    storageTemperatureActivity.setColumn("storageTemperature");
    storageTemperatureActivity.setOldValue("MEDIUM");
    storageTemperatureActivity.setNewValue(StorageTemperature.LOW.name());
    expectedUpdateActivities.add(storageTemperatureActivity);
    UpdateActivity lowResolutionActivity = new UpdateActivity();
    lowResolutionActivity.setActionType(ActionType.UPDATE);
    lowResolutionActivity.setTableName("sample");
    lowResolutionActivity.setRecordId(moleculeSample.getId());
    lowResolutionActivity.setColumn("lowResolution");
    lowResolutionActivity.setOldValue("1");
    lowResolutionActivity.setNewValue("0");
    expectedUpdateActivities.add(lowResolutionActivity);
    UpdateActivity highResolutionActivity = new UpdateActivity();
    highResolutionActivity.setActionType(ActionType.UPDATE);
    highResolutionActivity.setTableName("sample");
    highResolutionActivity.setRecordId(moleculeSample.getId());
    highResolutionActivity.setColumn("highResolution");
    highResolutionActivity.setOldValue("0");
    highResolutionActivity.setNewValue("1");
    expectedUpdateActivities.add(highResolutionActivity);
    UpdateActivity msmsActivity = new UpdateActivity();
    msmsActivity.setActionType(ActionType.UPDATE);
    msmsActivity.setTableName("sample");
    msmsActivity.setRecordId(moleculeSample.getId());
    msmsActivity.setColumn("msms");
    msmsActivity.setOldValue("0");
    msmsActivity.setNewValue("1");
    expectedUpdateActivities.add(msmsActivity);
    UpdateActivity exactMsmsActivity = new UpdateActivity();
    exactMsmsActivity.setActionType(ActionType.UPDATE);
    exactMsmsActivity.setTableName("sample");
    exactMsmsActivity.setRecordId(moleculeSample.getId());
    exactMsmsActivity.setColumn("exactMsms");
    exactMsmsActivity.setOldValue("0");
    exactMsmsActivity.setNewValue("1");
    expectedUpdateActivities.add(exactMsmsActivity);
    UpdateActivity additionalPriceActivity = new UpdateActivity();
    additionalPriceActivity.setActionType(ActionType.UPDATE);
    additionalPriceActivity.setTableName("sample");
    additionalPriceActivity.setRecordId(moleculeSample.getId());
    additionalPriceActivity.setColumn("additionalPrice");
    additionalPriceActivity.setOldValue(null);
    additionalPriceActivity.setNewValue("21.50");
    expectedUpdateActivities.add(additionalPriceActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_MoleculeSample_AddSolvent() throws Throwable {
    MoleculeSample moleculeSample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(moleculeSample);
    SampleSolvent solvent = new SampleSolvent(203L, Solvent.OTHER);
    moleculeSample.getSolventList().add(solvent);
    moleculeSample.setOtherSolvent("ch3oh");
    entityManager.flush();
    Long solventId = solvent.getId();

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(moleculeSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertNotNull(solventId);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(moleculeSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName("sample");
    otherSolventActivity.setRecordId(moleculeSample.getId());
    otherSolventActivity.setColumn("otherSolvent");
    otherSolventActivity.setOldValue(null);
    otherSolventActivity.setNewValue("ch3oh");
    expectedUpdateActivities.add(otherSolventActivity);
    UpdateActivity addSolventActivity = new UpdateActivity();
    addSolventActivity.setActionType(ActionType.INSERT);
    addSolventActivity.setTableName("solvent");
    addSolventActivity.setRecordId(solventId);
    expectedUpdateActivities.add(addSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_MoleculeSample_RemoveSolvent() throws Throwable {
    MoleculeSample moleculeSample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(moleculeSample);
    SampleSolvent solvent = find(moleculeSample.getSolventList(), Solvent.METHANOL);
    moleculeSample.getSolventList().remove(solvent);
    entityManager.flush();
    final Long solventId = solvent.getId();

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(moleculeSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(moleculeSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeSolventActivity = new UpdateActivity();
    removeSolventActivity.setActionType(ActionType.DELETE);
    removeSolventActivity.setTableName("solvent");
    removeSolventActivity.setRecordId(solventId);
    expectedUpdateActivities.add(removeSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_MoleculeSample_UpdateStructure() throws Throwable {
    MoleculeSample moleculeSample = entityManager.find(MoleculeSample.class, 443L);
    entityManager.detach(moleculeSample);
    Structure structure = moleculeSample.getStructure();
    entityManager.detach(structure);
    structure.setFilename("unit_test_structure_new.gif");
    Random random = new Random();
    byte[] bytes = new byte[1024];
    random.nextBytes(bytes);
    structure.setContent(bytes);

    Optional<Activity> optionalActivity =
        sampleActivityServiceImpl.update(moleculeSample, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(moleculeSample.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeSolventActivity = new UpdateActivity();
    removeSolventActivity.setActionType(ActionType.UPDATE);
    removeSolventActivity.setTableName("sample");
    removeSolventActivity.setRecordId(moleculeSample.getId());
    removeSolventActivity.setColumn("structure");
    removeSolventActivity.setOldValue("glucose.png");
    removeSolventActivity.setNewValue(structure.getFilename());
    expectedUpdateActivities.add(removeSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    control.setName("nc_test_000001");
    control.setControlType(Control.ControlType.POSITIVE_CONTROL);
    control.setComments("my_new_comment");
    control.setSupport(Support.SOLUTION);
    control.setVolume(2.0);
    control.setQuantity("40 μg");

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("sample");
    nameActivity.setRecordId(control.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("control_01");
    nameActivity.setNewValue("nc_test_000001");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity controlTypeActivity = new UpdateActivity();
    controlTypeActivity.setActionType(ActionType.UPDATE);
    controlTypeActivity.setTableName("sample");
    controlTypeActivity.setRecordId(control.getId());
    controlTypeActivity.setColumn("controlType");
    controlTypeActivity.setOldValue("NEGATIVE_CONTROL");
    controlTypeActivity.setNewValue("POSITIVE_CONTROL");
    expectedUpdateActivities.add(controlTypeActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("sample");
    commentsActivity.setRecordId(control.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue(null);
    commentsActivity.setNewValue("my_new_comment");
    expectedUpdateActivities.add(commentsActivity);
    UpdateActivity supportActivity = new UpdateActivity();
    supportActivity.setActionType(ActionType.UPDATE);
    supportActivity.setTableName("sample");
    supportActivity.setRecordId(control.getId());
    supportActivity.setColumn("support");
    supportActivity.setOldValue(Support.GEL.name());
    supportActivity.setNewValue(Support.SOLUTION.name());
    expectedUpdateActivities.add(supportActivity);
    UpdateActivity volumeActivity = new UpdateActivity();
    volumeActivity.setActionType(ActionType.UPDATE);
    volumeActivity.setTableName("sample");
    volumeActivity.setRecordId(control.getId());
    volumeActivity.setColumn("volume");
    volumeActivity.setOldValue(null);
    volumeActivity.setNewValue("2.0");
    expectedUpdateActivities.add(volumeActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("sample");
    quantityActivity.setRecordId(control.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue(null);
    quantityActivity.setNewValue("40 μg");
    expectedUpdateActivities.add(quantityActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_AddStandard() {
    Control control = entityManager.find(Control.class, 444L);
    entityManager.detach(control);
    Standard standard = new Standard();
    standard.setId(57894121L);
    standard.setName("my_new_standard");
    standard.setQuantity("3 μg");
    standard.setComments("some_comments");
    control.getStandards().add(standard);

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity addStandardActivity = new UpdateActivity();
    addStandardActivity.setActionType(ActionType.INSERT);
    addStandardActivity.setTableName("standard");
    addStandardActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(addStandardActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_UpdateStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    for (Standard standard : control.getStandards()) {
      entityManager.detach(standard);
    }
    Standard standard = control.getStandards().get(0);
    standard.setName("new_standard_name");
    standard.setQuantity("1 pmol");
    standard.setComments("new_comments");

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity nameActivity = new UpdateActivity();
    nameActivity.setActionType(ActionType.UPDATE);
    nameActivity.setTableName("standard");
    nameActivity.setRecordId(standard.getId());
    nameActivity.setColumn("name");
    nameActivity.setOldValue("cap_standard");
    nameActivity.setNewValue("new_standard_name");
    expectedUpdateActivities.add(nameActivity);
    UpdateActivity quantityActivity = new UpdateActivity();
    quantityActivity.setActionType(ActionType.UPDATE);
    quantityActivity.setTableName("standard");
    quantityActivity.setRecordId(standard.getId());
    quantityActivity.setColumn("quantity");
    quantityActivity.setOldValue("3 μg");
    quantityActivity.setNewValue("1 pmol");
    expectedUpdateActivities.add(quantityActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("standard");
    commentsActivity.setRecordId(standard.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue("some_comments");
    commentsActivity.setNewValue("new_comments");
    expectedUpdateActivities.add(commentsActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_Control_RemoveStandard() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);
    final Standard standard = control.getStandards().get(0);
    control.getStandards().remove(0);

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(control, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("sample", activity.getTableName());
    assertEquals(control.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeActivity = new UpdateActivity();
    removeActivity.setActionType(ActionType.DELETE);
    removeActivity.setTableName("standard");
    removeActivity.setRecordId(standard.getId());
    expectedUpdateActivities.add(removeActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Control control = entityManager.find(Control.class, 448L);
    entityManager.detach(control);

    Optional<Activity> optionalActivity = sampleActivityServiceImpl.update(control, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
