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

package ca.qc.ircm.proview.submission;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.Activity.ActionType;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.laboratory.Laboratory;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleSolvent;
import ca.qc.ircm.proview.sample.Structure;
import ca.qc.ircm.proview.security.AuthorizationService;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionActivityServiceImplTest {
  private SubmissionActivityServiceImpl submissionActivityServiceImpl;
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
    submissionActivityServiceImpl =
        new SubmissionActivityServiceImpl(entityManager, authorizationService);
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
  public void insert() {
    Submission submission = new Submission();
    submission.setId(123456L);
    submission.setSubmissionDate(Instant.now());

    Activity activity = submissionActivityServiceImpl.insert(submission);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals(null, activity.getJustification());
    assertEquals(user, activity.getUser());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Submission oldSubmission = entityManager.find(Submission.class, 1L);
    entityManager.detach(oldSubmission);
    Submission newSubmission = entityManager.find(Submission.class, 1L);
    entityManager.detach(newSubmission);
    final User oldUser = new User(3L);
    final Laboratory oldLaboratory = new Laboratory(2L);
    User newUser = new User(4L);
    Laboratory newLaboratory = new Laboratory(1L);
    newSubmission.setService(Service.MALDI_MS);
    newSubmission.setTaxonomy("mouse");
    newSubmission.setProject("new_project");
    newSubmission.setExperience("new_experience");
    newSubmission.setGoal("new_goal");
    newSubmission.setMassDetectionInstrument(MassDetectionInstrument.TOF);
    newSubmission.setSource(MassDetectionInstrumentSource.LDTD);
    newSubmission.setSampleNumberProtein(2);
    newSubmission.setProteolyticDigestionMethod(ProteolyticDigestion.DIGESTED);
    newSubmission.setUsedProteolyticDigestionMethod("Trypsine");
    newSubmission.setOtherProteolyticDigestionMethod("None");
    newSubmission.setProteinIdentification(ProteinIdentification.OTHER);
    newSubmission.setProteinIdentificationLink("http://cou24/my_database");
    newSubmission.setEnrichmentType(EnrichmentType.PHOSPHOPEPTIDES);
    newSubmission.setOtherEnrichmentType("Phosphopeptides");
    newSubmission.setLowResolution(true);
    newSubmission.setHighResolution(true);
    newSubmission.setMsms(true);
    newSubmission.setExactMsms(true);
    newSubmission.setMudPitFraction(MudPitFraction.TWELVE);
    newSubmission.setProteinContent(ProteinContent.LARGE);
    newSubmission.setProtein("my_protein");
    newSubmission.setMolecularWeight(20.0);
    newSubmission.setPostTranslationModification("my_modification");
    newSubmission.setSeparation(GelSeparation.TWO_DIMENSION);
    newSubmission.setThickness(GelThickness.ONE_HALF);
    newSubmission.setColoration(GelColoration.OTHER);
    newSubmission.setOtherColoration("my_coloration");
    newSubmission.setDevelopmentTime("2.0 min");
    newSubmission.setDecoloration(true);
    newSubmission.setWeightMarkerQuantity(2.5);
    newSubmission.setProteinQuantity("12.0 pmol");
    newSubmission.setFormula("h2o");
    newSubmission.setMonoisotopicMass(18.0);
    newSubmission.setAverageMass(18.0);
    newSubmission.setSolutionSolvent("ethanol");
    newSubmission.setOtherSolvent("ch3oh");
    newSubmission.setToxicity("die");
    newSubmission.setLightSensitive(true);
    newSubmission.setStorageTemperature(StorageTemperature.LOW);
    newSubmission.setQuantification(Quantification.LABEL_FREE);
    newSubmission.setQuantificationLabels("Heavy:Lys8,Arg10\nMedium:Lys4,Arg6\nLight:None");
    newSubmission.setComments("new_comment");
    newSubmission.setLaboratory(newLaboratory);
    newSubmission.setUser(newUser);
    newSubmission.setSubmissionDate(Instant.now());
    newSubmission.setAdditionalPrice(new BigDecimal("21.50"));

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(newSubmission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(newSubmission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity serviceActivity = new UpdateActivity();
    serviceActivity.setActionType(ActionType.UPDATE);
    serviceActivity.setTableName("submission");
    serviceActivity.setRecordId(newSubmission.getId());
    serviceActivity.setColumn("service");
    serviceActivity.setOldValue(Service.LC_MS_MS.name());
    serviceActivity.setNewValue(Service.MALDI_MS.name());
    expectedUpdateActivities.add(serviceActivity);
    UpdateActivity taxonomyActivity = new UpdateActivity();
    taxonomyActivity.setActionType(ActionType.UPDATE);
    taxonomyActivity.setTableName("submission");
    taxonomyActivity.setRecordId(newSubmission.getId());
    taxonomyActivity.setColumn("taxonomy");
    taxonomyActivity.setOldValue("Human");
    taxonomyActivity.setNewValue("mouse");
    expectedUpdateActivities.add(taxonomyActivity);
    UpdateActivity projectActivity = new UpdateActivity();
    projectActivity.setActionType(ActionType.UPDATE);
    projectActivity.setTableName("submission");
    projectActivity.setRecordId(newSubmission.getId());
    projectActivity.setColumn("project");
    projectActivity.setOldValue(oldSubmission.getProject());
    projectActivity.setNewValue(newSubmission.getProject());
    expectedUpdateActivities.add(projectActivity);
    UpdateActivity experienceActivity = new UpdateActivity();
    experienceActivity.setActionType(ActionType.UPDATE);
    experienceActivity.setTableName("submission");
    experienceActivity.setRecordId(newSubmission.getId());
    experienceActivity.setColumn("experience");
    experienceActivity.setOldValue(oldSubmission.getExperience());
    experienceActivity.setNewValue(newSubmission.getExperience());
    expectedUpdateActivities.add(experienceActivity);
    UpdateActivity goalActivity = new UpdateActivity();
    goalActivity.setActionType(ActionType.UPDATE);
    goalActivity.setTableName("submission");
    goalActivity.setRecordId(newSubmission.getId());
    goalActivity.setColumn("goal");
    goalActivity.setOldValue(oldSubmission.getGoal());
    goalActivity.setNewValue(newSubmission.getGoal());
    expectedUpdateActivities.add(goalActivity);
    UpdateActivity massDetectionInstrumentActivity = new UpdateActivity();
    massDetectionInstrumentActivity.setActionType(ActionType.UPDATE);
    massDetectionInstrumentActivity.setTableName("submission");
    massDetectionInstrumentActivity.setRecordId(newSubmission.getId());
    massDetectionInstrumentActivity.setColumn("massDetectionInstrument");
    massDetectionInstrumentActivity.setOldValue("LTQ_ORBI_TRAP");
    massDetectionInstrumentActivity.setNewValue(MassDetectionInstrument.TOF.name());
    expectedUpdateActivities.add(massDetectionInstrumentActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName("submission");
    sourceActivity.setRecordId(newSubmission.getId());
    sourceActivity.setColumn("source");
    sourceActivity.setOldValue(null);
    sourceActivity.setNewValue(MassDetectionInstrumentSource.LDTD.name());
    expectedUpdateActivities.add(sourceActivity);
    UpdateActivity sampleNumberProteinActivity = new UpdateActivity();
    sampleNumberProteinActivity.setActionType(ActionType.UPDATE);
    sampleNumberProteinActivity.setTableName("submission");
    sampleNumberProteinActivity.setRecordId(newSubmission.getId());
    sampleNumberProteinActivity.setColumn("sampleNumberProtein");
    sampleNumberProteinActivity.setOldValue(null);
    sampleNumberProteinActivity.setNewValue("2");
    expectedUpdateActivities.add(sampleNumberProteinActivity);
    UpdateActivity proteolyticDigestionMethodActivity = new UpdateActivity();
    proteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    proteolyticDigestionMethodActivity.setTableName("submission");
    proteolyticDigestionMethodActivity.setRecordId(newSubmission.getId());
    proteolyticDigestionMethodActivity.setColumn("proteolyticDigestionMethod");
    proteolyticDigestionMethodActivity.setOldValue("TRYPSIN");
    proteolyticDigestionMethodActivity.setNewValue(ProteolyticDigestion.DIGESTED.name());
    expectedUpdateActivities.add(proteolyticDigestionMethodActivity);
    UpdateActivity usedProteolyticDigestionMethodActivity = new UpdateActivity();
    usedProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    usedProteolyticDigestionMethodActivity.setTableName("submission");
    usedProteolyticDigestionMethodActivity.setRecordId(newSubmission.getId());
    usedProteolyticDigestionMethodActivity.setColumn("usedProteolyticDigestionMethod");
    usedProteolyticDigestionMethodActivity.setOldValue(null);
    usedProteolyticDigestionMethodActivity.setNewValue("Trypsine");
    expectedUpdateActivities.add(usedProteolyticDigestionMethodActivity);
    UpdateActivity otherProteolyticDigestionMethodActivity = new UpdateActivity();
    otherProteolyticDigestionMethodActivity.setActionType(ActionType.UPDATE);
    otherProteolyticDigestionMethodActivity.setTableName("submission");
    otherProteolyticDigestionMethodActivity.setRecordId(newSubmission.getId());
    otherProteolyticDigestionMethodActivity.setColumn("otherProteolyticDigestionMethod");
    otherProteolyticDigestionMethodActivity.setOldValue(null);
    otherProteolyticDigestionMethodActivity.setNewValue("None");
    expectedUpdateActivities.add(otherProteolyticDigestionMethodActivity);
    UpdateActivity proteinIdentificationActivity = new UpdateActivity();
    proteinIdentificationActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationActivity.setTableName("submission");
    proteinIdentificationActivity.setRecordId(newSubmission.getId());
    proteinIdentificationActivity.setColumn("proteinIdentification");
    proteinIdentificationActivity.setOldValue("NCBINR");
    proteinIdentificationActivity.setNewValue(ProteinIdentification.OTHER.name());
    expectedUpdateActivities.add(proteinIdentificationActivity);
    UpdateActivity proteinIdentificationLinkActivity = new UpdateActivity();
    proteinIdentificationLinkActivity.setActionType(ActionType.UPDATE);
    proteinIdentificationLinkActivity.setTableName("submission");
    proteinIdentificationLinkActivity.setRecordId(newSubmission.getId());
    proteinIdentificationLinkActivity.setColumn("proteinIdentificationLink");
    proteinIdentificationLinkActivity.setOldValue(null);
    proteinIdentificationLinkActivity.setNewValue("http://cou24/my_database");
    expectedUpdateActivities.add(proteinIdentificationLinkActivity);
    UpdateActivity enrichmentTypeActivity = new UpdateActivity();
    enrichmentTypeActivity.setActionType(ActionType.UPDATE);
    enrichmentTypeActivity.setTableName("submission");
    enrichmentTypeActivity.setRecordId(newSubmission.getId());
    enrichmentTypeActivity.setColumn("enrichmentType");
    enrichmentTypeActivity.setOldValue(null);
    enrichmentTypeActivity.setNewValue(EnrichmentType.PHOSPHOPEPTIDES.name());
    expectedUpdateActivities.add(enrichmentTypeActivity);
    UpdateActivity otherEnrichmentTypeActivity = new UpdateActivity();
    otherEnrichmentTypeActivity.setActionType(ActionType.UPDATE);
    otherEnrichmentTypeActivity.setTableName("submission");
    otherEnrichmentTypeActivity.setRecordId(newSubmission.getId());
    otherEnrichmentTypeActivity.setColumn("otherEnrichmentType");
    otherEnrichmentTypeActivity.setOldValue(null);
    otherEnrichmentTypeActivity.setNewValue("Phosphopeptides");
    expectedUpdateActivities.add(otherEnrichmentTypeActivity);
    UpdateActivity lowResolutionActivity = new UpdateActivity();
    lowResolutionActivity.setActionType(ActionType.UPDATE);
    lowResolutionActivity.setTableName("submission");
    lowResolutionActivity.setRecordId(newSubmission.getId());
    lowResolutionActivity.setColumn("lowResolution");
    lowResolutionActivity.setOldValue("0");
    lowResolutionActivity.setNewValue("1");
    expectedUpdateActivities.add(lowResolutionActivity);
    UpdateActivity highResolutionActivity = new UpdateActivity();
    highResolutionActivity.setActionType(ActionType.UPDATE);
    highResolutionActivity.setTableName("submission");
    highResolutionActivity.setRecordId(newSubmission.getId());
    highResolutionActivity.setColumn("highResolution");
    highResolutionActivity.setOldValue("0");
    highResolutionActivity.setNewValue("1");
    expectedUpdateActivities.add(highResolutionActivity);
    UpdateActivity msmsActivity = new UpdateActivity();
    msmsActivity.setActionType(ActionType.UPDATE);
    msmsActivity.setTableName("submission");
    msmsActivity.setRecordId(newSubmission.getId());
    msmsActivity.setColumn("msms");
    msmsActivity.setOldValue("0");
    msmsActivity.setNewValue("1");
    expectedUpdateActivities.add(msmsActivity);
    UpdateActivity exactMsmsActivity = new UpdateActivity();
    exactMsmsActivity.setActionType(ActionType.UPDATE);
    exactMsmsActivity.setTableName("submission");
    exactMsmsActivity.setRecordId(newSubmission.getId());
    exactMsmsActivity.setColumn("exactMsms");
    exactMsmsActivity.setOldValue("0");
    exactMsmsActivity.setNewValue("1");
    expectedUpdateActivities.add(exactMsmsActivity);
    UpdateActivity mudPitFractionActivity = new UpdateActivity();
    mudPitFractionActivity.setActionType(ActionType.UPDATE);
    mudPitFractionActivity.setTableName("submission");
    mudPitFractionActivity.setRecordId(newSubmission.getId());
    mudPitFractionActivity.setColumn("mudPitFraction");
    mudPitFractionActivity.setOldValue(null);
    mudPitFractionActivity.setNewValue(MudPitFraction.TWELVE.name());
    expectedUpdateActivities.add(mudPitFractionActivity);
    UpdateActivity proteinContentActivity = new UpdateActivity();
    proteinContentActivity.setActionType(ActionType.UPDATE);
    proteinContentActivity.setTableName("submission");
    proteinContentActivity.setRecordId(newSubmission.getId());
    proteinContentActivity.setColumn("proteinContent");
    proteinContentActivity.setOldValue("XLARGE");
    proteinContentActivity.setNewValue(ProteinContent.LARGE.name());
    expectedUpdateActivities.add(proteinContentActivity);
    UpdateActivity proteinActivity = new UpdateActivity();
    proteinActivity.setActionType(ActionType.UPDATE);
    proteinActivity.setTableName("submission");
    proteinActivity.setRecordId(newSubmission.getId());
    proteinActivity.setColumn("protein");
    proteinActivity.setOldValue(null);
    proteinActivity.setNewValue("my_protein");
    expectedUpdateActivities.add(proteinActivity);
    UpdateActivity molecularWeightActivity = new UpdateActivity();
    molecularWeightActivity.setActionType(ActionType.UPDATE);
    molecularWeightActivity.setTableName("submission");
    molecularWeightActivity.setRecordId(newSubmission.getId());
    molecularWeightActivity.setColumn("molecularWeight");
    molecularWeightActivity.setOldValue(null);
    molecularWeightActivity.setNewValue("20.0");
    expectedUpdateActivities.add(molecularWeightActivity);
    UpdateActivity postTranslationModificationActivity = new UpdateActivity();
    postTranslationModificationActivity.setActionType(ActionType.UPDATE);
    postTranslationModificationActivity.setTableName("submission");
    postTranslationModificationActivity.setRecordId(newSubmission.getId());
    postTranslationModificationActivity.setColumn("postTranslationModification");
    postTranslationModificationActivity.setOldValue(null);
    postTranslationModificationActivity.setNewValue("my_modification");
    expectedUpdateActivities.add(postTranslationModificationActivity);
    UpdateActivity separationActivity = new UpdateActivity();
    separationActivity.setActionType(ActionType.UPDATE);
    separationActivity.setTableName("submission");
    separationActivity.setRecordId(newSubmission.getId());
    separationActivity.setColumn("separation");
    separationActivity.setOldValue("ONE_DIMENSION");
    separationActivity.setNewValue(GelSeparation.TWO_DIMENSION.name());
    expectedUpdateActivities.add(separationActivity);
    UpdateActivity thicknessActivity = new UpdateActivity();
    thicknessActivity.setActionType(ActionType.UPDATE);
    thicknessActivity.setTableName("submission");
    thicknessActivity.setRecordId(newSubmission.getId());
    thicknessActivity.setColumn("thickness");
    thicknessActivity.setOldValue("ONE");
    thicknessActivity.setNewValue(GelThickness.ONE_HALF.name());
    expectedUpdateActivities.add(thicknessActivity);
    UpdateActivity colorationActivity = new UpdateActivity();
    colorationActivity.setActionType(ActionType.UPDATE);
    colorationActivity.setTableName("submission");
    colorationActivity.setRecordId(newSubmission.getId());
    colorationActivity.setColumn("coloration");
    colorationActivity.setOldValue("SILVER");
    colorationActivity.setNewValue(GelColoration.OTHER.name());
    expectedUpdateActivities.add(colorationActivity);
    UpdateActivity otherColorationActivity = new UpdateActivity();
    otherColorationActivity.setActionType(ActionType.UPDATE);
    otherColorationActivity.setTableName("submission");
    otherColorationActivity.setRecordId(newSubmission.getId());
    otherColorationActivity.setColumn("otherColoration");
    otherColorationActivity.setOldValue(null);
    otherColorationActivity.setNewValue("my_coloration");
    expectedUpdateActivities.add(otherColorationActivity);
    UpdateActivity developmentTimeActivity = new UpdateActivity();
    developmentTimeActivity.setActionType(ActionType.UPDATE);
    developmentTimeActivity.setTableName("submission");
    developmentTimeActivity.setRecordId(newSubmission.getId());
    developmentTimeActivity.setColumn("developmentTime");
    developmentTimeActivity.setOldValue(null);
    developmentTimeActivity.setNewValue("2.0 min");
    expectedUpdateActivities.add(developmentTimeActivity);
    UpdateActivity decolorationActivity = new UpdateActivity();
    decolorationActivity.setActionType(ActionType.UPDATE);
    decolorationActivity.setTableName("submission");
    decolorationActivity.setRecordId(newSubmission.getId());
    decolorationActivity.setColumn("decoloration");
    decolorationActivity.setOldValue("0");
    decolorationActivity.setNewValue("1");
    expectedUpdateActivities.add(decolorationActivity);
    UpdateActivity weightMarkerQuantityActivity = new UpdateActivity();
    weightMarkerQuantityActivity.setActionType(ActionType.UPDATE);
    weightMarkerQuantityActivity.setTableName("submission");
    weightMarkerQuantityActivity.setRecordId(newSubmission.getId());
    weightMarkerQuantityActivity.setColumn("weightMarkerQuantity");
    weightMarkerQuantityActivity.setOldValue(null);
    weightMarkerQuantityActivity.setNewValue("2.5");
    expectedUpdateActivities.add(weightMarkerQuantityActivity);
    UpdateActivity proteinQuantityActivity = new UpdateActivity();
    proteinQuantityActivity.setActionType(ActionType.UPDATE);
    proteinQuantityActivity.setTableName("submission");
    proteinQuantityActivity.setRecordId(newSubmission.getId());
    proteinQuantityActivity.setColumn("proteinQuantity");
    proteinQuantityActivity.setOldValue(null);
    proteinQuantityActivity.setNewValue("12.0 pmol");
    expectedUpdateActivities.add(proteinQuantityActivity);
    UpdateActivity formulaActivity = new UpdateActivity();
    formulaActivity.setActionType(ActionType.UPDATE);
    formulaActivity.setTableName("submission");
    formulaActivity.setRecordId(newSubmission.getId());
    formulaActivity.setColumn("formula");
    formulaActivity.setOldValue(null);
    formulaActivity.setNewValue("h2o");
    expectedUpdateActivities.add(formulaActivity);
    UpdateActivity monoisotopicMassActivity = new UpdateActivity();
    monoisotopicMassActivity.setActionType(ActionType.UPDATE);
    monoisotopicMassActivity.setTableName("submission");
    monoisotopicMassActivity.setRecordId(newSubmission.getId());
    monoisotopicMassActivity.setColumn("monoisotopicMass");
    monoisotopicMassActivity.setOldValue(null);
    monoisotopicMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(monoisotopicMassActivity);
    UpdateActivity averageMassActivity = new UpdateActivity();
    averageMassActivity.setActionType(ActionType.UPDATE);
    averageMassActivity.setTableName("submission");
    averageMassActivity.setRecordId(newSubmission.getId());
    averageMassActivity.setColumn("averageMass");
    averageMassActivity.setOldValue(null);
    averageMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(averageMassActivity);
    UpdateActivity solutionSolventActivity = new UpdateActivity();
    solutionSolventActivity.setActionType(ActionType.UPDATE);
    solutionSolventActivity.setTableName("submission");
    solutionSolventActivity.setRecordId(newSubmission.getId());
    solutionSolventActivity.setColumn("solutionSolvent");
    solutionSolventActivity.setOldValue(null);
    solutionSolventActivity.setNewValue("ethanol");
    expectedUpdateActivities.add(solutionSolventActivity);
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName("submission");
    otherSolventActivity.setRecordId(newSubmission.getId());
    otherSolventActivity.setColumn("otherSolvent");
    otherSolventActivity.setOldValue(null);
    otherSolventActivity.setNewValue("ch3oh");
    expectedUpdateActivities.add(otherSolventActivity);
    UpdateActivity toxicityActivity = new UpdateActivity();
    toxicityActivity.setActionType(ActionType.UPDATE);
    toxicityActivity.setTableName("submission");
    toxicityActivity.setRecordId(newSubmission.getId());
    toxicityActivity.setColumn("toxicity");
    toxicityActivity.setOldValue(null);
    toxicityActivity.setNewValue("die");
    expectedUpdateActivities.add(toxicityActivity);
    UpdateActivity lightSensitiveActivity = new UpdateActivity();
    lightSensitiveActivity.setActionType(ActionType.UPDATE);
    lightSensitiveActivity.setTableName("submission");
    lightSensitiveActivity.setRecordId(newSubmission.getId());
    lightSensitiveActivity.setColumn("lightSensitive");
    lightSensitiveActivity.setOldValue("0");
    lightSensitiveActivity.setNewValue("1");
    expectedUpdateActivities.add(lightSensitiveActivity);
    UpdateActivity storageTemperatureActivity = new UpdateActivity();
    storageTemperatureActivity.setActionType(ActionType.UPDATE);
    storageTemperatureActivity.setTableName("submission");
    storageTemperatureActivity.setRecordId(newSubmission.getId());
    storageTemperatureActivity.setColumn("storageTemperature");
    storageTemperatureActivity.setOldValue(null);
    storageTemperatureActivity.setNewValue(StorageTemperature.LOW.name());
    expectedUpdateActivities.add(storageTemperatureActivity);
    UpdateActivity quantificationActivity = new UpdateActivity();
    quantificationActivity.setActionType(ActionType.UPDATE);
    quantificationActivity.setTableName("submission");
    quantificationActivity.setRecordId(newSubmission.getId());
    quantificationActivity.setColumn("quantification");
    quantificationActivity.setOldValue(null);
    quantificationActivity.setNewValue(Quantification.LABEL_FREE.name());
    expectedUpdateActivities.add(quantificationActivity);
    UpdateActivity quantificationLabelsActivity = new UpdateActivity();
    quantificationLabelsActivity.setActionType(ActionType.UPDATE);
    quantificationLabelsActivity.setTableName("submission");
    quantificationLabelsActivity.setRecordId(newSubmission.getId());
    quantificationLabelsActivity.setColumn("quantificationLabels");
    quantificationLabelsActivity.setOldValue(null);
    quantificationLabelsActivity.setNewValue("Heavy:Lys8,Arg10\nMedium:Lys4,Arg6\nLight:None");
    expectedUpdateActivities.add(quantificationLabelsActivity);
    UpdateActivity commentsActivity = new UpdateActivity();
    commentsActivity.setActionType(ActionType.UPDATE);
    commentsActivity.setTableName("submission");
    commentsActivity.setRecordId(newSubmission.getId());
    commentsActivity.setColumn("comments");
    commentsActivity.setOldValue(oldSubmission.getComments());
    commentsActivity.setNewValue(newSubmission.getComments());
    expectedUpdateActivities.add(commentsActivity);
    UpdateActivity userActivity = new UpdateActivity();
    userActivity.setActionType(ActionType.UPDATE);
    userActivity.setTableName("submission");
    userActivity.setRecordId(newSubmission.getId());
    userActivity.setColumn("userId");
    userActivity.setOldValue(String.valueOf(oldUser.getId()));
    userActivity.setNewValue(String.valueOf(newUser.getId()));
    expectedUpdateActivities.add(userActivity);
    UpdateActivity laboratoryActivity = new UpdateActivity();
    laboratoryActivity.setActionType(ActionType.UPDATE);
    laboratoryActivity.setTableName("submission");
    laboratoryActivity.setRecordId(newSubmission.getId());
    laboratoryActivity.setColumn("laboratoryId");
    laboratoryActivity.setOldValue(String.valueOf(oldLaboratory.getId()));
    laboratoryActivity.setNewValue(String.valueOf(newLaboratory.getId()));
    expectedUpdateActivities.add(laboratoryActivity);
    UpdateActivity submissionDateActivity = new UpdateActivity();
    submissionDateActivity.setActionType(ActionType.UPDATE);
    submissionDateActivity.setTableName("submission");
    submissionDateActivity.setRecordId(newSubmission.getId());
    submissionDateActivity.setColumn("submissionDate");
    DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    submissionDateActivity.setOldValue(instantFormatter.format(
        LocalDateTime.ofInstant(oldSubmission.getSubmissionDate(), ZoneId.systemDefault())));
    submissionDateActivity.setNewValue(instantFormatter.format(
        LocalDateTime.ofInstant(newSubmission.getSubmissionDate(), ZoneId.systemDefault())));
    expectedUpdateActivities.add(submissionDateActivity);
    UpdateActivity additionalPriceActivity = new UpdateActivity();
    additionalPriceActivity.setActionType(ActionType.UPDATE);
    additionalPriceActivity.setTableName("submission");
    additionalPriceActivity.setRecordId(newSubmission.getId());
    additionalPriceActivity.setColumn("additionalPrice");
    additionalPriceActivity.setOldValue(null);
    additionalPriceActivity.setNewValue("21.50");
    expectedUpdateActivities.add(additionalPriceActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_MoleculeSample_AddSolvent() throws Throwable {
    Submission submission = entityManager.find(Submission.class, 33L);
    entityManager.detach(submission);
    SampleSolvent solvent = new SampleSolvent(203L, Solvent.OTHER);
    submission.getSolvents().add(solvent);
    submission.setOtherSolvent("ch3oh");
    entityManager.flush();
    Long solventId = solvent.getId();

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(submission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertNotNull(solventId);
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName("submission");
    otherSolventActivity.setRecordId(submission.getId());
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
    Submission submission = entityManager.find(Submission.class, 33L);
    entityManager.detach(submission);
    SampleSolvent solvent = find(submission.getSolvents(), Solvent.METHANOL);
    submission.getSolvents().remove(solvent);
    entityManager.flush();
    final Long solventId = solvent.getId();

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(submission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
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
    Submission submission = entityManager.find(Submission.class, 33L);
    entityManager.detach(submission);
    Structure structure = submission.getStructure();
    entityManager.detach(structure);
    structure.setFilename("unit_test_structure_new.gif");
    Random random = new Random();
    byte[] bytes = new byte[1024];
    random.nextBytes(bytes);
    structure.setContent(bytes);

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(submission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals("submission", activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getJustification());
    assertEquals(user, activity.getUser());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<UpdateActivity>();
    UpdateActivity removeSolventActivity = new UpdateActivity();
    removeSolventActivity.setActionType(ActionType.UPDATE);
    removeSolventActivity.setTableName("submission");
    removeSolventActivity.setRecordId(submission.getId());
    removeSolventActivity.setColumn("structure");
    removeSolventActivity.setOldValue("glucose.png");
    removeSolventActivity.setNewValue(structure.getFilename());
    expectedUpdateActivities.add(removeSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Submission submission = entityManager.find(Submission.class, 1L);
    entityManager.detach(submission);

    Optional<Activity> optionalActivity =
        submissionActivityServiceImpl.update(submission, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
