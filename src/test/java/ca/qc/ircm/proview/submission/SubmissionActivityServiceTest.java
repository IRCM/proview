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

import static ca.qc.ircm.proview.persistence.QueryDsl.qname;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.UpdateActivity;
import ca.qc.ircm.proview.msanalysis.InjectionType;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.QSubmissionSample;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleActivityService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.test.config.AbstractServiceTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.test.utils.LogTestUtils;
import ca.qc.ircm.proview.treatment.Solvent;
import ca.qc.ircm.proview.user.Laboratory;
import ca.qc.ircm.proview.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionActivityServiceTest extends AbstractServiceTestCase {
  private static final QSubmission qsubmission = QSubmission.submission;
  private static final QSubmissionSample qsubmissionSample = QSubmissionSample.submissionSample;
  @Inject
  private SubmissionActivityService submissionActivityService;
  @Inject
  private SubmissionRepository repository;
  @MockBean
  private SampleActivityService sampleActivityService;
  @MockBean
  private AuthorizationService authorizationService;
  private User user;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    user = new User(4L);
    when(authorizationService.getCurrentUser()).thenReturn(user);
    when(sampleActivityService.update(any(), any())).thenReturn(Optional.empty());
  }

  @Test
  public void insert() {
    Submission submission = new Submission();
    submission.setId(123456L);
    submission.setSubmissionDate(LocalDateTime.now());

    Activity activity = submissionActivityService.insert(submission);

    assertEquals(ActionType.INSERT, activity.getActionType());
    assertEquals(Submission.TABLE_NAME, activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals(null, activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    LogTestUtils.validateUpdateActivities(null, activity.getUpdates());
  }

  @Test
  public void update() {
    Submission newSubmission = repository.findById(1L).orElse(null);
    detach(newSubmission);
    final User oldUser = new User(3L);
    final Laboratory oldLaboratory = new Laboratory(2L);
    User newUser = new User(4L);
    Laboratory newLaboratory = new Laboratory(1L);
    newSubmission.setService(Service.MALDI_MS);
    newSubmission.setTaxonomy("mouse");
    newSubmission.setExperiment("new_experiment");
    newSubmission.setGoal("new_goal");
    newSubmission.setInstrument(MassDetectionInstrument.TOF);
    newSubmission.setSource(MassDetectionInstrumentSource.LDTD);
    newSubmission.setInjectionType(InjectionType.LC_MS);
    newSubmission.setDigestion(ProteolyticDigestion.DIGESTED);
    newSubmission.setUsedDigestion("Trypsine");
    newSubmission.setOtherDigestion("None");
    newSubmission.setIdentification(ProteinIdentification.OTHER);
    newSubmission.setIdentificationLink("http://cou24/my_database");
    newSubmission.setHighResolution(true);
    newSubmission.setProteinContent(ProteinContent.LARGE);
    newSubmission.setProtein("my_protein");
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
    newSubmission.setQuantificationComment("Heavy:Lys8,Arg10\nMedium:Lys4,Arg6\nLight:None");
    newSubmission.setComment("new_comment");
    newSubmission.setLaboratory(newLaboratory);
    newSubmission.setUser(newUser);
    newSubmission.setSubmissionDate(LocalDateTime.now());
    newSubmission.setSampleDeliveryDate(LocalDate.now().minusDays(3));
    newSubmission.setDigestionDate(LocalDate.now().minusDays(2));
    newSubmission.setAnalysisDate(LocalDate.now().minusDays(1));
    newSubmission.setDataAvailableDate(LocalDate.now());
    newSubmission.setHidden(true);
    newSubmission.setFiles(new ArrayList<>());
    newSubmission.getFiles().add(new SubmissionFile("my_file.xlsx"));
    newSubmission.getFiles().add(new SubmissionFile("protocol.docx"));

    Optional<Activity> optionalActivity =
        submissionActivityService.update(newSubmission, "unit_test");

    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_DATE;
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Submission.TABLE_NAME, activity.getTableName());
    assertEquals(newSubmission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity serviceActivity = new UpdateActivity();
    serviceActivity.setActionType(ActionType.UPDATE);
    serviceActivity.setTableName(Submission.TABLE_NAME);
    serviceActivity.setRecordId(newSubmission.getId());
    serviceActivity.setColumn(qname(qsubmission.service));
    serviceActivity.setOldValue(Service.LC_MS_MS.name());
    serviceActivity.setNewValue(Service.MALDI_MS.name());
    expectedUpdateActivities.add(serviceActivity);
    UpdateActivity taxonomyActivity = new UpdateActivity();
    taxonomyActivity.setActionType(ActionType.UPDATE);
    taxonomyActivity.setTableName(Submission.TABLE_NAME);
    taxonomyActivity.setRecordId(newSubmission.getId());
    taxonomyActivity.setColumn(qname(qsubmission.taxonomy));
    taxonomyActivity.setOldValue("Human");
    taxonomyActivity.setNewValue("mouse");
    expectedUpdateActivities.add(taxonomyActivity);
    UpdateActivity experimentActivity = new UpdateActivity();
    experimentActivity.setActionType(ActionType.UPDATE);
    experimentActivity.setTableName(Submission.TABLE_NAME);
    experimentActivity.setRecordId(newSubmission.getId());
    experimentActivity.setColumn(qname(qsubmission.experiment));
    experimentActivity.setOldValue("G100429");
    experimentActivity.setNewValue(newSubmission.getExperiment());
    expectedUpdateActivities.add(experimentActivity);
    UpdateActivity goalActivity = new UpdateActivity();
    goalActivity.setActionType(ActionType.UPDATE);
    goalActivity.setTableName(Submission.TABLE_NAME);
    goalActivity.setRecordId(newSubmission.getId());
    goalActivity.setColumn(qname(qsubmission.goal));
    goalActivity.setOldValue(null);
    goalActivity.setNewValue(newSubmission.getGoal());
    expectedUpdateActivities.add(goalActivity);
    UpdateActivity instrumentActivity = new UpdateActivity();
    instrumentActivity.setActionType(ActionType.UPDATE);
    instrumentActivity.setTableName(Submission.TABLE_NAME);
    instrumentActivity.setRecordId(newSubmission.getId());
    instrumentActivity.setColumn(qname(qsubmission.instrument));
    instrumentActivity.setOldValue("LTQ_ORBI_TRAP");
    instrumentActivity.setNewValue(MassDetectionInstrument.TOF.name());
    expectedUpdateActivities.add(instrumentActivity);
    UpdateActivity sourceActivity = new UpdateActivity();
    sourceActivity.setActionType(ActionType.UPDATE);
    sourceActivity.setTableName(Submission.TABLE_NAME);
    sourceActivity.setRecordId(newSubmission.getId());
    sourceActivity.setColumn(qname(qsubmission.source));
    sourceActivity.setOldValue(null);
    sourceActivity.setNewValue(MassDetectionInstrumentSource.LDTD.name());
    expectedUpdateActivities.add(sourceActivity);
    UpdateActivity injectionTypeActivity = new UpdateActivity();
    injectionTypeActivity.setActionType(ActionType.UPDATE);
    injectionTypeActivity.setTableName(Submission.TABLE_NAME);
    injectionTypeActivity.setRecordId(newSubmission.getId());
    injectionTypeActivity.setColumn(qname(qsubmission.injectionType));
    injectionTypeActivity.setOldValue(null);
    injectionTypeActivity.setNewValue(InjectionType.LC_MS.name());
    expectedUpdateActivities.add(injectionTypeActivity);
    UpdateActivity digestionActivity = new UpdateActivity();
    digestionActivity.setActionType(ActionType.UPDATE);
    digestionActivity.setTableName(Submission.TABLE_NAME);
    digestionActivity.setRecordId(newSubmission.getId());
    digestionActivity.setColumn(qname(qsubmission.digestion));
    digestionActivity.setOldValue("TRYPSIN");
    digestionActivity.setNewValue(ProteolyticDigestion.DIGESTED.name());
    expectedUpdateActivities.add(digestionActivity);
    UpdateActivity usedDigestionActivity = new UpdateActivity();
    usedDigestionActivity.setActionType(ActionType.UPDATE);
    usedDigestionActivity.setTableName(Submission.TABLE_NAME);
    usedDigestionActivity.setRecordId(newSubmission.getId());
    usedDigestionActivity
        .setColumn(qname(qsubmission.usedDigestion));
    usedDigestionActivity.setOldValue(null);
    usedDigestionActivity.setNewValue("Trypsine");
    expectedUpdateActivities.add(usedDigestionActivity);
    UpdateActivity otherDigestionActivity = new UpdateActivity();
    otherDigestionActivity.setActionType(ActionType.UPDATE);
    otherDigestionActivity.setTableName(Submission.TABLE_NAME);
    otherDigestionActivity.setRecordId(newSubmission.getId());
    otherDigestionActivity
        .setColumn(qname(qsubmission.otherDigestion));
    otherDigestionActivity.setOldValue(null);
    otherDigestionActivity.setNewValue("None");
    expectedUpdateActivities.add(otherDigestionActivity);
    UpdateActivity identificationActivity = new UpdateActivity();
    identificationActivity.setActionType(ActionType.UPDATE);
    identificationActivity.setTableName(Submission.TABLE_NAME);
    identificationActivity.setRecordId(newSubmission.getId());
    identificationActivity.setColumn(qname(qsubmission.identification));
    identificationActivity.setOldValue("NCBINR");
    identificationActivity.setNewValue(ProteinIdentification.OTHER.name());
    expectedUpdateActivities.add(identificationActivity);
    UpdateActivity identificationLinkActivity = new UpdateActivity();
    identificationLinkActivity.setActionType(ActionType.UPDATE);
    identificationLinkActivity.setTableName(Submission.TABLE_NAME);
    identificationLinkActivity.setRecordId(newSubmission.getId());
    identificationLinkActivity.setColumn(qname(qsubmission.identificationLink));
    identificationLinkActivity.setOldValue(null);
    identificationLinkActivity.setNewValue("http://cou24/my_database");
    expectedUpdateActivities.add(identificationLinkActivity);
    UpdateActivity highResolutionActivity = new UpdateActivity();
    highResolutionActivity.setActionType(ActionType.UPDATE);
    highResolutionActivity.setTableName(Submission.TABLE_NAME);
    highResolutionActivity.setRecordId(newSubmission.getId());
    highResolutionActivity.setColumn(qname(qsubmission.highResolution));
    highResolutionActivity.setOldValue("0");
    highResolutionActivity.setNewValue("1");
    expectedUpdateActivities.add(highResolutionActivity);
    UpdateActivity proteinContentActivity = new UpdateActivity();
    proteinContentActivity.setActionType(ActionType.UPDATE);
    proteinContentActivity.setTableName(Submission.TABLE_NAME);
    proteinContentActivity.setRecordId(newSubmission.getId());
    proteinContentActivity.setColumn(qname(qsubmission.proteinContent));
    proteinContentActivity.setOldValue("XLARGE");
    proteinContentActivity.setNewValue(ProteinContent.LARGE.name());
    expectedUpdateActivities.add(proteinContentActivity);
    UpdateActivity proteinActivity = new UpdateActivity();
    proteinActivity.setActionType(ActionType.UPDATE);
    proteinActivity.setTableName(Submission.TABLE_NAME);
    proteinActivity.setRecordId(newSubmission.getId());
    proteinActivity.setColumn(qname(qsubmission.protein));
    proteinActivity.setOldValue(null);
    proteinActivity.setNewValue("my_protein");
    expectedUpdateActivities.add(proteinActivity);
    UpdateActivity postTranslationModificationActivity = new UpdateActivity();
    postTranslationModificationActivity.setActionType(ActionType.UPDATE);
    postTranslationModificationActivity.setTableName(Submission.TABLE_NAME);
    postTranslationModificationActivity.setRecordId(newSubmission.getId());
    postTranslationModificationActivity.setColumn(qname(qsubmission.postTranslationModification));
    postTranslationModificationActivity.setOldValue(null);
    postTranslationModificationActivity.setNewValue("my_modification");
    expectedUpdateActivities.add(postTranslationModificationActivity);
    UpdateActivity separationActivity = new UpdateActivity();
    separationActivity.setActionType(ActionType.UPDATE);
    separationActivity.setTableName(Submission.TABLE_NAME);
    separationActivity.setRecordId(newSubmission.getId());
    separationActivity.setColumn(qname(qsubmission.separation));
    separationActivity.setOldValue("ONE_DIMENSION");
    separationActivity.setNewValue(GelSeparation.TWO_DIMENSION.name());
    expectedUpdateActivities.add(separationActivity);
    UpdateActivity thicknessActivity = new UpdateActivity();
    thicknessActivity.setActionType(ActionType.UPDATE);
    thicknessActivity.setTableName(Submission.TABLE_NAME);
    thicknessActivity.setRecordId(newSubmission.getId());
    thicknessActivity.setColumn(qname(qsubmission.thickness));
    thicknessActivity.setOldValue("ONE");
    thicknessActivity.setNewValue(GelThickness.ONE_HALF.name());
    expectedUpdateActivities.add(thicknessActivity);
    UpdateActivity colorationActivity = new UpdateActivity();
    colorationActivity.setActionType(ActionType.UPDATE);
    colorationActivity.setTableName(Submission.TABLE_NAME);
    colorationActivity.setRecordId(newSubmission.getId());
    colorationActivity.setColumn(qname(qsubmission.coloration));
    colorationActivity.setOldValue("SILVER");
    colorationActivity.setNewValue(GelColoration.OTHER.name());
    expectedUpdateActivities.add(colorationActivity);
    UpdateActivity otherColorationActivity = new UpdateActivity();
    otherColorationActivity.setActionType(ActionType.UPDATE);
    otherColorationActivity.setTableName(Submission.TABLE_NAME);
    otherColorationActivity.setRecordId(newSubmission.getId());
    otherColorationActivity.setColumn(qname(qsubmission.otherColoration));
    otherColorationActivity.setOldValue(null);
    otherColorationActivity.setNewValue("my_coloration");
    expectedUpdateActivities.add(otherColorationActivity);
    UpdateActivity developmentTimeActivity = new UpdateActivity();
    developmentTimeActivity.setActionType(ActionType.UPDATE);
    developmentTimeActivity.setTableName(Submission.TABLE_NAME);
    developmentTimeActivity.setRecordId(newSubmission.getId());
    developmentTimeActivity.setColumn(qname(qsubmission.developmentTime));
    developmentTimeActivity.setOldValue(null);
    developmentTimeActivity.setNewValue("2.0 min");
    expectedUpdateActivities.add(developmentTimeActivity);
    UpdateActivity decolorationActivity = new UpdateActivity();
    decolorationActivity.setActionType(ActionType.UPDATE);
    decolorationActivity.setTableName(Submission.TABLE_NAME);
    decolorationActivity.setRecordId(newSubmission.getId());
    decolorationActivity.setColumn(qname(qsubmission.decoloration));
    decolorationActivity.setOldValue("0");
    decolorationActivity.setNewValue("1");
    expectedUpdateActivities.add(decolorationActivity);
    UpdateActivity weightMarkerQuantityActivity = new UpdateActivity();
    weightMarkerQuantityActivity.setActionType(ActionType.UPDATE);
    weightMarkerQuantityActivity.setTableName(Submission.TABLE_NAME);
    weightMarkerQuantityActivity.setRecordId(newSubmission.getId());
    weightMarkerQuantityActivity.setColumn(qname(qsubmission.weightMarkerQuantity));
    weightMarkerQuantityActivity.setOldValue(null);
    weightMarkerQuantityActivity.setNewValue("2.5");
    expectedUpdateActivities.add(weightMarkerQuantityActivity);
    UpdateActivity proteinQuantityActivity = new UpdateActivity();
    proteinQuantityActivity.setActionType(ActionType.UPDATE);
    proteinQuantityActivity.setTableName(Submission.TABLE_NAME);
    proteinQuantityActivity.setRecordId(newSubmission.getId());
    proteinQuantityActivity.setColumn(qname(qsubmission.proteinQuantity));
    proteinQuantityActivity.setOldValue(null);
    proteinQuantityActivity.setNewValue("12.0 pmol");
    expectedUpdateActivities.add(proteinQuantityActivity);
    UpdateActivity formulaActivity = new UpdateActivity();
    formulaActivity.setActionType(ActionType.UPDATE);
    formulaActivity.setTableName(Submission.TABLE_NAME);
    formulaActivity.setRecordId(newSubmission.getId());
    formulaActivity.setColumn(qname(qsubmission.formula));
    formulaActivity.setOldValue(null);
    formulaActivity.setNewValue("h2o");
    expectedUpdateActivities.add(formulaActivity);
    UpdateActivity monoisotopicMassActivity = new UpdateActivity();
    monoisotopicMassActivity.setActionType(ActionType.UPDATE);
    monoisotopicMassActivity.setTableName(Submission.TABLE_NAME);
    monoisotopicMassActivity.setRecordId(newSubmission.getId());
    monoisotopicMassActivity.setColumn(qname(qsubmission.monoisotopicMass));
    monoisotopicMassActivity.setOldValue(null);
    monoisotopicMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(monoisotopicMassActivity);
    UpdateActivity averageMassActivity = new UpdateActivity();
    averageMassActivity.setActionType(ActionType.UPDATE);
    averageMassActivity.setTableName(Submission.TABLE_NAME);
    averageMassActivity.setRecordId(newSubmission.getId());
    averageMassActivity.setColumn(qname(qsubmission.averageMass));
    averageMassActivity.setOldValue(null);
    averageMassActivity.setNewValue("18.0");
    expectedUpdateActivities.add(averageMassActivity);
    UpdateActivity solutionSolventActivity = new UpdateActivity();
    solutionSolventActivity.setActionType(ActionType.UPDATE);
    solutionSolventActivity.setTableName(Submission.TABLE_NAME);
    solutionSolventActivity.setRecordId(newSubmission.getId());
    solutionSolventActivity.setColumn(qname(qsubmission.solutionSolvent));
    solutionSolventActivity.setOldValue(null);
    solutionSolventActivity.setNewValue("ethanol");
    expectedUpdateActivities.add(solutionSolventActivity);
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName(Submission.TABLE_NAME);
    otherSolventActivity.setRecordId(newSubmission.getId());
    otherSolventActivity.setColumn(qname(qsubmission.otherSolvent));
    otherSolventActivity.setOldValue(null);
    otherSolventActivity.setNewValue("ch3oh");
    expectedUpdateActivities.add(otherSolventActivity);
    UpdateActivity toxicityActivity = new UpdateActivity();
    toxicityActivity.setActionType(ActionType.UPDATE);
    toxicityActivity.setTableName(Submission.TABLE_NAME);
    toxicityActivity.setRecordId(newSubmission.getId());
    toxicityActivity.setColumn(qname(qsubmission.toxicity));
    toxicityActivity.setOldValue(null);
    toxicityActivity.setNewValue("die");
    expectedUpdateActivities.add(toxicityActivity);
    UpdateActivity lightSensitiveActivity = new UpdateActivity();
    lightSensitiveActivity.setActionType(ActionType.UPDATE);
    lightSensitiveActivity.setTableName(Submission.TABLE_NAME);
    lightSensitiveActivity.setRecordId(newSubmission.getId());
    lightSensitiveActivity.setColumn(qname(qsubmission.lightSensitive));
    lightSensitiveActivity.setOldValue("0");
    lightSensitiveActivity.setNewValue("1");
    expectedUpdateActivities.add(lightSensitiveActivity);
    UpdateActivity storageTemperatureActivity = new UpdateActivity();
    storageTemperatureActivity.setActionType(ActionType.UPDATE);
    storageTemperatureActivity.setTableName(Submission.TABLE_NAME);
    storageTemperatureActivity.setRecordId(newSubmission.getId());
    storageTemperatureActivity.setColumn(qname(qsubmission.storageTemperature));
    storageTemperatureActivity.setOldValue(null);
    storageTemperatureActivity.setNewValue(StorageTemperature.LOW.name());
    expectedUpdateActivities.add(storageTemperatureActivity);
    UpdateActivity quantificationActivity = new UpdateActivity();
    quantificationActivity.setActionType(ActionType.UPDATE);
    quantificationActivity.setTableName(Submission.TABLE_NAME);
    quantificationActivity.setRecordId(newSubmission.getId());
    quantificationActivity.setColumn(qname(qsubmission.quantification));
    quantificationActivity.setOldValue(null);
    quantificationActivity.setNewValue(Quantification.LABEL_FREE.name());
    expectedUpdateActivities.add(quantificationActivity);
    UpdateActivity quantificationCommentActivity = new UpdateActivity();
    quantificationCommentActivity.setActionType(ActionType.UPDATE);
    quantificationCommentActivity.setTableName(Submission.TABLE_NAME);
    quantificationCommentActivity.setRecordId(newSubmission.getId());
    quantificationCommentActivity.setColumn(qname(qsubmission.quantificationComment));
    quantificationCommentActivity.setOldValue(null);
    quantificationCommentActivity.setNewValue("Heavy:Lys8,Arg10\nMedium:Lys4,Arg6\nLight:None");
    expectedUpdateActivities.add(quantificationCommentActivity);
    UpdateActivity commentActivity = new UpdateActivity();
    commentActivity.setActionType(ActionType.UPDATE);
    commentActivity.setTableName(Submission.TABLE_NAME);
    commentActivity.setRecordId(newSubmission.getId());
    commentActivity.setColumn(qname(qsubmission.comment));
    commentActivity.setOldValue("Philippe");
    commentActivity.setNewValue(newSubmission.getComment());
    expectedUpdateActivities.add(commentActivity);
    UpdateActivity userActivity = new UpdateActivity();
    userActivity.setActionType(ActionType.UPDATE);
    userActivity.setTableName(Submission.TABLE_NAME);
    userActivity.setRecordId(newSubmission.getId());
    userActivity.setColumn(qname(qsubmission.user) + "Id");
    userActivity.setOldValue(String.valueOf(oldUser.getId()));
    userActivity.setNewValue(String.valueOf(newUser.getId()));
    expectedUpdateActivities.add(userActivity);
    UpdateActivity laboratoryActivity = new UpdateActivity();
    laboratoryActivity.setActionType(ActionType.UPDATE);
    laboratoryActivity.setTableName(Submission.TABLE_NAME);
    laboratoryActivity.setRecordId(newSubmission.getId());
    laboratoryActivity.setColumn(qname(qsubmission.laboratory) + "Id");
    laboratoryActivity.setOldValue(String.valueOf(oldLaboratory.getId()));
    laboratoryActivity.setNewValue(String.valueOf(newLaboratory.getId()));
    expectedUpdateActivities.add(laboratoryActivity);
    UpdateActivity submissionDateActivity = new UpdateActivity();
    submissionDateActivity.setActionType(ActionType.UPDATE);
    submissionDateActivity.setTableName(Submission.TABLE_NAME);
    submissionDateActivity.setRecordId(newSubmission.getId());
    submissionDateActivity.setColumn(qname(qsubmission.submissionDate));
    DateTimeFormatter instantFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    submissionDateActivity.setOldValue("2010-10-15T00:00:00");
    submissionDateActivity.setNewValue(instantFormatter.format(newSubmission.getSubmissionDate()));
    expectedUpdateActivities.add(submissionDateActivity);
    UpdateActivity sampleDeliveryDateActivity = new UpdateActivity();
    sampleDeliveryDateActivity.setActionType(ActionType.UPDATE);
    sampleDeliveryDateActivity.setTableName(Submission.TABLE_NAME);
    sampleDeliveryDateActivity.setRecordId(newSubmission.getId());
    sampleDeliveryDateActivity.setColumn(qname(qsubmission.sampleDeliveryDate));
    sampleDeliveryDateActivity.setOldValue("2010-12-09");
    sampleDeliveryDateActivity
        .setNewValue(dateFormatter.format(newSubmission.getSampleDeliveryDate()));
    expectedUpdateActivities.add(sampleDeliveryDateActivity);
    UpdateActivity digestionDateActivity = new UpdateActivity();
    digestionDateActivity.setActionType(ActionType.UPDATE);
    digestionDateActivity.setTableName(Submission.TABLE_NAME);
    digestionDateActivity.setRecordId(newSubmission.getId());
    digestionDateActivity.setColumn(qname(qsubmission.digestionDate));
    digestionDateActivity.setOldValue("2010-12-11");
    digestionDateActivity.setNewValue(dateFormatter.format(newSubmission.getDigestionDate()));
    expectedUpdateActivities.add(digestionDateActivity);
    UpdateActivity analysisDateActivity = new UpdateActivity();
    analysisDateActivity.setActionType(ActionType.UPDATE);
    analysisDateActivity.setTableName(Submission.TABLE_NAME);
    analysisDateActivity.setRecordId(newSubmission.getId());
    analysisDateActivity.setColumn(qname(qsubmission.analysisDate));
    analysisDateActivity.setOldValue("2010-12-13");
    analysisDateActivity.setNewValue(dateFormatter.format(newSubmission.getAnalysisDate()));
    expectedUpdateActivities.add(analysisDateActivity);
    UpdateActivity dataAvailableDateActivity = new UpdateActivity();
    dataAvailableDateActivity.setActionType(ActionType.UPDATE);
    dataAvailableDateActivity.setTableName(Submission.TABLE_NAME);
    dataAvailableDateActivity.setRecordId(newSubmission.getId());
    dataAvailableDateActivity.setColumn(qname(qsubmission.dataAvailableDate));
    dataAvailableDateActivity.setOldValue("2010-12-15");
    dataAvailableDateActivity
        .setNewValue(dateFormatter.format(newSubmission.getDataAvailableDate()));
    expectedUpdateActivities.add(dataAvailableDateActivity);
    UpdateActivity hiddenActivity = new UpdateActivity();
    hiddenActivity.setActionType(ActionType.UPDATE);
    hiddenActivity.setTableName(Submission.TABLE_NAME);
    hiddenActivity.setRecordId(newSubmission.getId());
    hiddenActivity.setColumn(qname(qsubmission.hidden));
    hiddenActivity.setOldValue("0");
    hiddenActivity.setNewValue("1");
    expectedUpdateActivities.add(hiddenActivity);
    UpdateActivity filesActivity = new UpdateActivity();
    filesActivity.setActionType(ActionType.UPDATE);
    filesActivity.setTableName(Submission.TABLE_NAME);
    filesActivity.setRecordId(newSubmission.getId());
    filesActivity.setColumn("submissionfiles");
    filesActivity.setOldValue("protocol.txt,frag.jpg");
    filesActivity.setNewValue(newSubmission.getFiles().stream().map(file -> file.getFilename())
        .collect(Collectors.joining(",")));
    expectedUpdateActivities.add(filesActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_ChangeSamples() {
    Submission submission = repository.findById(147L).orElse(null);
    detach(submission);
    submission.getSamples().remove(1);
    submission.getSamples().add(new SubmissionSample(640L, "new_sample"));
    Activity sampleUpdate = new Activity();
    sampleUpdate.setUpdates(new ArrayList<>());
    UpdateActivity updateSampleNameActivity = new UpdateActivity();
    updateSampleNameActivity.setActionType(ActionType.UPDATE);
    updateSampleNameActivity.setTableName(Sample.TABLE_NAME);
    updateSampleNameActivity.setRecordId(559L);
    updateSampleNameActivity.setColumn(qname(qsubmissionSample.name));
    updateSampleNameActivity.setOldValue("POLR2A_20141008_1");
    updateSampleNameActivity.setNewValue("POLR2A_20141008_1_New");
    sampleUpdate.getUpdates().add(updateSampleNameActivity);
    when(sampleActivityService.update(any(), any())).thenReturn(Optional.of(sampleUpdate));

    Optional<Activity> optionalActivity = submissionActivityService.update(submission, "unit_test");

    verify(sampleActivityService).update(eq(submission.getSamples().get(0)), any());
    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Submission.TABLE_NAME, activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity removeSampleActivity = new UpdateActivity();
    removeSampleActivity.setActionType(ActionType.DELETE);
    removeSampleActivity.setTableName(Sample.TABLE_NAME);
    removeSampleActivity.setRecordId(560L);
    expectedUpdateActivities.add(removeSampleActivity);
    UpdateActivity addSampleActivity = new UpdateActivity();
    addSampleActivity.setActionType(ActionType.INSERT);
    addSampleActivity.setTableName(Sample.TABLE_NAME);
    addSampleActivity.setRecordId(640L);
    expectedUpdateActivities.add(addSampleActivity);
    expectedUpdateActivities.add(updateSampleNameActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_AddSolvent() throws Throwable {
    Submission submission = repository.findById(33L).orElse(null);
    Solvent solvent = Solvent.OTHER;
    submission.getSolvents().add(solvent);
    submission.setOtherSolvent("ch3oh");
    detach(submission);

    Optional<Activity> optionalActivity = submissionActivityService.update(submission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Submission.TABLE_NAME, activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity otherSolventActivity = new UpdateActivity();
    otherSolventActivity.setActionType(ActionType.UPDATE);
    otherSolventActivity.setTableName(Submission.TABLE_NAME);
    otherSolventActivity.setRecordId(submission.getId());
    otherSolventActivity.setColumn(qname(qsubmission.otherSolvent));
    otherSolventActivity.setOldValue(null);
    otherSolventActivity.setNewValue("ch3oh");
    expectedUpdateActivities.add(otherSolventActivity);
    UpdateActivity addSolventActivity = new UpdateActivity();
    addSolventActivity.setActionType(ActionType.UPDATE);
    addSolventActivity.setTableName(Submission.TABLE_NAME);
    addSolventActivity.setRecordId(submission.getId());
    addSolventActivity.setColumn("solvent");
    addSolventActivity.setOldValue("METHANOL");
    addSolventActivity.setNewValue("METHANOL,OTHER");
    expectedUpdateActivities.add(addSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_RemoveSolvent() throws Throwable {
    Submission submission = repository.findById(33L).orElse(null);
    Solvent solvent = Solvent.METHANOL;
    submission.getSolvents().remove(solvent);
    detach(submission);

    Optional<Activity> optionalActivity = submissionActivityService.update(submission, "unit_test");

    assertEquals(true, optionalActivity.isPresent());
    Activity activity = optionalActivity.get();
    assertEquals(ActionType.UPDATE, activity.getActionType());
    assertEquals(Submission.TABLE_NAME, activity.getTableName());
    assertEquals(submission.getId(), activity.getRecordId());
    assertEquals("unit_test", activity.getExplanation());
    assertEquals(user.getId(), activity.getUser().getId());
    final Collection<UpdateActivity> expectedUpdateActivities = new ArrayList<>();
    UpdateActivity removeSolventActivity = new UpdateActivity();
    removeSolventActivity.setActionType(ActionType.UPDATE);
    removeSolventActivity.setTableName(Submission.TABLE_NAME);
    removeSolventActivity.setRecordId(submission.getId());
    removeSolventActivity.setColumn("solvent");
    removeSolventActivity.setOldValue("METHANOL");
    removeSolventActivity.setNewValue(null);
    expectedUpdateActivities.add(removeSolventActivity);
    LogTestUtils.validateUpdateActivities(expectedUpdateActivities, activity.getUpdates());
  }

  @Test
  public void update_NoChange() {
    Submission submission = repository.findById(1L).orElse(null);
    detach(submission);

    Optional<Activity> optionalActivity = submissionActivityService.update(submission, "unit_test");

    assertEquals(false, optionalActivity.isPresent());
  }
}
