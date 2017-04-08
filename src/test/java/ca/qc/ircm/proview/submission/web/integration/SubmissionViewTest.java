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

package ca.qc.ircm.proview.submission.web.integration;

import static ca.qc.ircm.proview.msanalysis.MassDetectionInstrument.ORBITRAP_FUSION;
import static ca.qc.ircm.proview.sample.ProteinIdentification.REFSEQ;
import static ca.qc.ircm.proview.sample.ProteolyticDigestion.TRYPSIN;
import static ca.qc.ircm.proview.sample.SampleContainerType.SPOT;
import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.ProteinContent.LARGE;
import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.TITLE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.sample.SampleContainerType;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.ProteinContent;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFile;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.MainView;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionViewTest extends SubmissionViewPageObject {
  @Inject
  private JPAQueryFactory jpaQueryFactory;
  @Value("${spring.application.name}")
  private String applicationName;
  private Service service = LC_MS_MS;
  private SampleSupport support = SOLUTION;
  private int sampleCount = 2;
  private String sampleName1 = "sample_01";
  private String sampleName2 = "sample_02";
  private SampleContainerType sampleContainerType = TUBE;
  private String experience = "my experience";
  private String experienceGoal = "my experience goal";
  private String taxonomy = "human";
  private String quantity = "20 ug";
  private double volume = 10.0;
  private int standardCount = 0;
  private int contaminantCount = 0;
  private ProteolyticDigestion digestion = TRYPSIN;
  private ProteinContent proteinContent = LARGE;
  private MassDetectionInstrument instrument = ORBITRAP_FUSION;
  private ProteinIdentification proteinIdentification = REFSEQ;
  private String comments = "my comment\ntest";
  private Path additionalFile1;

  @Before
  public void beforeTest() throws Throwable {
    additionalFile1 = Paths.get(getClass().getResource("/submissionfile1.txt").toURI());
  }

  private Submission getSubmission(String experience) {
    JPAQuery<Submission> query = jpaQueryFactory.select(submission);
    query.from(submission);
    query.where(submission.experience.eq(experience));
    return query.fetchOne();
  }

  private void setFields() {
    setService(service);
    setSampleSupport(support);
    setSampleContainerType(sampleContainerType);
    setSampleCount(sampleCount);
    setSampleNameInGrid(0, sampleName1);
    setSampleNameInGrid(1, sampleName2);
    setExperience(experience);
    setExperienceGoal(experienceGoal);
    setTaxonomy(taxonomy);
    setQuantity(quantity);
    setVolume(volume);
    setStandardCount(standardCount);
    setContaminantCount(contaminantCount);
    setDigestion(digestion);
    setProteinContent(proteinContent);
    setInstrument(instrument);
    setProteinIdentification(proteinIdentification);
    setComments(comments);
    uploadFile(additionalFile1);
    waitForPageLoad();
  }

  @Test
  @WithSubject(anonymous = true)
  public void security_Anonymous() throws Throwable {
    openView(MainView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(SubmissionView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertNotNull(header());
    assertNotNull(sampleTypeLabel());
    assertNotNull(inactiveLabel());
    assertNotNull(servicePanel());
    assertNotNull(serviceOptions());
    assertNotNull(samplesPanel());
    assertNotNull(sampleSupportOptions());
    setService(SMALL_MOLECULE);
    assertNotNull(solutionSolventField());
    assertNotNull(sampleNameField());
    assertNotNull(formulaField());
    uploadStructure(Paths.get(getClass().getResource("/structure1").toURI()));
    waitForPageLoad();
    assertNotNull(structureButton());
    assertNotNull(structureUploader());
    assertNotNull(monoisotopicMassField());
    assertNotNull(averageMassField());
    assertNotNull(toxicityField());
    assertNotNull(lightSensitiveField());
    assertNotNull(storageTemperatureOptions());
    setService(LC_MS_MS);
    assertNotNull(sampleCountField());
    assertNotNull(sampleContainerTypeOptions());
    setSampleContainerType(SPOT);
    assertNotNull(plateNameField());
    assertNotNull(samplesLabel());
    setSampleContainerType(TUBE);
    assertNotNull(samplesGrid());
    assertNotNull(fillSamplesButton());
    setSampleContainerType(SPOT);
    assertNotNull(samplesPlate());
    setSampleContainerType(TUBE);
    assertNotNull(experiencePanel());
    assertNotNull(experienceField());
    assertNotNull(experienceGoalField());
    assertNotNull(taxonomyField());
    assertNotNull(proteinNameField());
    assertNotNull(proteinWeightField());
    assertNotNull(postTranslationModificationField());
    assertNotNull(quantityField());
    assertNotNull(volumeField());
    assertNotNull(standardsPanel());
    assertNotNull(standardCountField());
    setStandardCount(1);
    assertNotNull(standardsGrid());
    assertNotNull(fillStandardsButton());
    assertNotNull(contaminantsPanel());
    assertNotNull(contaminantCountField());
    setContaminantCount(1);
    assertNotNull(contaminantsGrid());
    assertNotNull(fillContaminantsButton());
    setSampleSupport(GEL);
    assertNotNull(gelPanel());
    assertNotNull(separationField());
    assertNotNull(thicknessField());
    assertNotNull(colorationField());
    setColoration(GelColoration.OTHER);
    assertNotNull(otherColorationField());
    assertNotNull(developmentTimeField());
    assertNotNull(decolorationField());
    assertNotNull(weightMarkerQuantityField());
    assertNotNull(proteinQuantityField());
    assertNotNull(gelImagesUploader());
    assertNotNull(gelImagesGrid());
    setSampleSupport(SOLUTION);
    assertNotNull(servicesPanel());
    assertNotNull(digestionOptions());
    setDigestion(ProteolyticDigestion.DIGESTED);
    assertNotNull(usedDigestionField());
    setDigestion(ProteolyticDigestion.OTHER);
    assertNotNull(otherDigestionField());
    assertNotNull(enrichmentLabel());
    assertNotNull(exclusionsLabel());
    setService(INTACT_PROTEIN);
    assertNotNull(injectionTypeOptions());
    assertNotNull(sourceOptions());
    setService(LC_MS_MS);
    assertNotNull(proteinContentOptions());
    assertNotNull(instrumentOptions());
    assertNotNull(proteinIdentificationOptions());
    setProteinIdentification(ProteinIdentification.OTHER);
    assertNotNull(proteinIdentificationLinkField());
    assertNotNull(quantificationOptions());
    assertNotNull(quantificationLabelsField());
    setService(SMALL_MOLECULE);
    assertNotNull(highResolutionOptions());
    assertNotNull(acetonitrileField());
    assertNotNull(methanolField());
    assertNotNull(chclField());
    assertNotNull(otherSolventsField());
    setOtherSolvents(true);
    assertNotNull(otherSolventField());
    assertNotNull(otherSolventNoteLabel());
    setService(LC_MS_MS);
    assertNotNull(commentsPanel());
    assertNotNull(commentsField());
    assertNotNull(filesPanel());
    assertNotNull(filesUploader());
    assertNotNull(filesGrid());
    assertNotNull(saveButton());
  }

  @Test
  public void submission_Error() {
    open();

    clickSaveButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void submission() throws Throwable {
    open();
    setFields();

    clickSaveButton();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
    Submission submission = getSubmission(experience);
    assertEquals(service, submission.getService());
    assertEquals(experience, submission.getExperience());
    assertEquals(experienceGoal, submission.getGoal());
    assertEquals(taxonomy, submission.getTaxonomy());
    assertEquals(digestion, submission.getProteolyticDigestionMethod());
    assertEquals(proteinContent, submission.getProteinContent());
    assertEquals(instrument, submission.getMassDetectionInstrument());
    assertEquals(proteinIdentification, submission.getProteinIdentification());
    assertEquals(null, submission.getQuantification());
    assertEquals(null, submission.getQuantificationLabels());
    assertEquals(comments, submission.getComments());
    assertEquals(2, submission.getSamples().size());
    SubmissionSample sample = submission.getSamples().get(0);
    assertEquals(support, sample.getSupport());
    assertEquals(1, submission.getFiles().size());
    SubmissionFile file = submission.getFiles().get(0);
    assertEquals(additionalFile1.getFileName().toString(), file.getFilename());
    assertArrayEquals(Files.readAllBytes(additionalFile1), file.getContent());
  }
}
