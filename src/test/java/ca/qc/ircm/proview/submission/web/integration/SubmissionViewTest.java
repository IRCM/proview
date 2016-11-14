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

import static ca.qc.ircm.proview.sample.SampleContainerType.SPOT;
import static ca.qc.ircm.proview.sample.SampleContainerType.TUBE;
import static ca.qc.ircm.proview.sample.SampleSupport.GEL;
import static ca.qc.ircm.proview.sample.SampleSupport.SOLUTION;
import static ca.qc.ircm.proview.submission.Service.INTACT_PROTEIN;
import static ca.qc.ircm.proview.submission.Service.LC_MS_MS;
import static ca.qc.ircm.proview.submission.Service.SMALL_MOLECULE;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.TITLE;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.sample.ProteinIdentification;
import ca.qc.ircm.proview.sample.ProteolyticDigestion;
import ca.qc.ircm.proview.submission.GelColoration;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Paths;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionViewTest extends SubmissionViewPageObject {
  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(SubmissionView.class).message(TITLE).contains(getDriver().getTitle()));
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
    assertNotNull(sampleCountSolvent());
    assertNotNull(sampleContainerTypeOptions());
    setSampleContainerType(SPOT);
    assertNotNull(plateNameField());
    assertNotNull(samplesLabel());
    setSampleContainerType(TUBE);
    assertNotNull(samplesTable());
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
    assertNotNull(standardsTable());
    assertNotNull(fillStandardsButton());
    assertNotNull(contaminantsPanel());
    assertNotNull(contaminantCountField());
    setContaminantCount(1);
    assertNotNull(contaminantsTable());
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
    assertNotNull(gelImagesTable());
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
    assertNotNull(filesTable());
    assertNotNull(submitButton());
  }
}
