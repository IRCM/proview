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
  public void fieldsPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = header().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = sampleTypeLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = inactiveLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = servicePanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = serviceOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = samplesPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = sampleSupportOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    setService(SMALL_MOLECULE);
    current = solutionSolventField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = sampleNameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = formulaField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    uploadStructure(Paths.get(getClass().getResource("/structure1").toURI()));
    current = structureButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = structureUploader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = monoisotopicMassField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = averageMassField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = toxicityField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = lightSensitiveField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = storageTemperatureOptions().getLocation().y;
    assertTrue(previous < current);
    setService(LC_MS_MS);
    previous = sampleSupportOptions().getLocation().y;
    current = sampleCountSolvent().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = sampleContainerTypeOptions().getLocation().y;
    assertTrue(previous < current);
    setSampleContainerType(SPOT);
    previous = current;
    current = plateNameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = samplesLabel().getLocation().y;
    assertTrue(previous < current);
    setSampleContainerType(TUBE);
    previous = current;
    current = samplesTable().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = fillSamplesButton().getLocation().y;
    assertTrue(previous < current);
    setSampleContainerType(SPOT);
    previous = samplesLabel().getLocation().y;
    current = samplesPlate().getLocation().y;
    assertTrue(previous < current);
    setSampleContainerType(TUBE);
    previous = current;
    current = experiencePanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = experienceField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = experienceGoalField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = taxonomyField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = proteinNameField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = proteinWeightField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = postTranslationModificationField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = volumeField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = quantityField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = standardsPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = standardCountField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = standardsTable().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = fillStandardsButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = contaminantsPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = contaminantCountField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = contaminantsTable().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = fillContaminantsButton().getLocation().y;
    assertTrue(previous < current);
    setSampleSupport(GEL);
    previous = samplesTable().getLocation().y;
    current = gelPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = separationField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = thicknessField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = colorationField().getLocation().y;
    assertTrue(previous < current);
    setColoration(GelColoration.OTHER);
    previous = current;
    current = otherColorationField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = developmentTimeField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = decolorationField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = weightMarkerQuantityField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = proteinQuantityField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = gelImagesUploader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = gelImagesTable().getLocation().y;
    assertTrue(previous < current);
    setSampleSupport(SOLUTION);
    previous = contaminantsTable().getLocation().y;
    current = servicesPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = digestionOptions().getLocation().y;
    assertTrue(previous < current);
    setDigestion(ProteolyticDigestion.DIGESTED);
    previous = current;
    current = usedDigestionField().getLocation().y;
    assertTrue(previous < current);
    setDigestion(ProteolyticDigestion.OTHER);
    current = otherDigestionField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = enrichmentLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = exclusionsLabel().getLocation().y;
    assertTrue(previous < current);
    setService(INTACT_PROTEIN);
    previous = servicesPanel().getLocation().y;
    current = injectionTypeOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = sourceOptions().getLocation().y;
    assertTrue(previous < current);
    setService(LC_MS_MS);
    previous = exclusionsLabel().getLocation().y;
    current = proteinContentOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = instrumentOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = proteinIdentificationOptions().getLocation().y;
    assertTrue(previous < current);
    setProteinIdentification(ProteinIdentification.OTHER);
    previous = current;
    current = proteinIdentificationLinkField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = quantificationOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = quantificationLabelsField().getLocation().y;
    assertTrue(previous < current);
    setService(SMALL_MOLECULE);
    previous = servicesPanel().getLocation().y;
    current = highResolutionOptions().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = acetonitrileField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = methanolField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = chclField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = otherSolventsField().getLocation().y;
    assertTrue(previous < current);
    setOtherSolvents(true);
    previous = current;
    current = otherSolventField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = otherSolventNoteLabel().getLocation().y;
    assertTrue(previous < current);
    setService(LC_MS_MS);
    previous = quantificationLabelsField().getLocation().y;
    current = commentsPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = commentsField().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = filesPanel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = filesUploader().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = filesTable().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = submitButton().getLocation().y;
    assertTrue(previous < current);
    previous = current;
  }
}
