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

import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_LAST_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TREATMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TREATMENTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TREATMENT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TREATMENT_TIME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TREATMENT_TYPE;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.dataProvider;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.fractionation.web.FractionationView;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleType;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.treatment.Protocol;
import ca.qc.ircm.proview.treatment.TreatedSample;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionTreatmentsFormPresenterTest {
  private SubmissionTreatmentsFormPresenter presenter;
  @Mock
  private SubmissionTreatmentsForm view;
  @Mock
  private SampleContainerService sampleContainerService;
  @Mock
  private TreatmentService treatmentService;
  @Mock
  private MsAnalysisService msAnalysisService;
  private SubmissionTreatmentsFormDesign design;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionTreatmentsForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private SubmissionSample sample1;
  private SubmissionSample sample2;
  private Submission submission;
  private Tube tube1;
  private Tube tube2;
  private Solubilisation tubeSolubilisation;
  private TreatedSample tubeSolubilisedSample;
  private Solubilisation plateSolubilisation;
  private TreatedSample plateSolubilisedSample;
  private Digestion tubeDigestion;
  private TreatedSample tubeDigestedSample;
  private TreatedSample tubeDigestedSample2;
  private Digestion plateDigestion;
  private TreatedSample plateDigestedSample;
  private StandardAddition tubeStandardAddition;
  private TreatedSample tubeAddedStandard;
  private StandardAddition plateStandardAddition;
  private TreatedSample plateAddedStandard;
  private Enrichment tubeEnrichment;
  private TreatedSample tubeEnrichedSample;
  private Enrichment plateEnrichment;
  private TreatedSample plateEnrichedSample;
  private Dilution tubeDilution;
  private TreatedSample tubeDilutedSample;
  private Dilution plateDilution;
  private TreatedSample plateDilutedSample;
  private Fractionation tubeFractionation;
  private TreatedSample tubeFractionatedSample;
  private TreatedSample tubeFractionatedSample2;
  private Fractionation plateFractionation;
  private TreatedSample plateFractionatedSample;
  private Transfer tubeTransfer;
  private TreatedSample tubeTransferedSample;
  private Transfer plateTransfer;
  private TreatedSample plateTransferedSample;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionTreatmentsFormPresenter(sampleContainerService, treatmentService);
    design = new SubmissionTreatmentsFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    submission = new Submission();
    sample1 = new SubmissionSample(1L);
    sample1.setName("sample_name");
    sample1.setType(SampleType.SOLUTION);
    sample1.setStatus(SampleStatus.ANALYSED);
    sample1.setQuantity("10.4 ug");
    sample1.setVolume("10.3 ul");
    sample1.setNumberProtein(4);
    sample1.setMolecularWeight(5.6);
    sample1.setSubmission(submission);
    tube1 = new Tube();
    tube1.setName("tube_name");
    sample1.setOriginalContainer(tube1);
    sample2 = new SubmissionSample(2L);
    sample2.setName("sample_name");
    sample2.setType(SampleType.SOLUTION);
    sample2.setStatus(SampleStatus.ANALYSED);
    sample2.setQuantity("10.4 ug");
    sample2.setVolume("10.3 ul");
    sample2.setNumberProtein(4);
    sample2.setMolecularWeight(5.6);
    sample2.setSubmission(submission);
    tube2 = new Tube();
    tube2.setName("tube_name");
    sample2.setOriginalContainer(tube1);
    submission.setSamples(Arrays.asList(sample1, sample2));
    tubeSolubilisation = new Solubilisation(1L);
    tubeSolubilisation
        .setInsertTime(Instant.now().minus(tubeSolubilisation.getId(), ChronoUnit.HOURS));
    tubeSolubilisedSample = new TreatedSample();
    tubeSolubilisedSample.setTreatment(tubeSolubilisation);
    tubeSolubilisedSample.setSample(sample1);
    tubeSolubilisedSample.setSolvent("ch3oh");
    tubeSolubilisedSample.setSolventVolume(10.0);
    tubeSolubilisedSample.setComment("tube_digestion_comment_1");
    tubeSolubilisedSample.setContainer(new Tube());
    ((Tube) tubeSolubilisedSample.getContainer()).setName("tube_1");
    tubeSolubilisation.setTreatedSamples(Arrays.asList(tubeSolubilisedSample));
    plateSolubilisation = new Solubilisation(2L);
    plateSolubilisation
        .setInsertTime(Instant.now().minus(plateSolubilisation.getId(), ChronoUnit.HOURS));
    plateSolubilisedSample = new TreatedSample();
    plateSolubilisedSample.setTreatment(plateSolubilisation);
    plateSolubilisedSample.setSample(sample2);
    plateSolubilisedSample.setSolvent("ch2oh2");
    plateSolubilisedSample.setSolventVolume(20.0);
    plateSolubilisedSample.setComment("plate_digestion_comment_1");
    plateSolubilisedSample.setContainer(new Well(1, 2));
    ((Well) plateSolubilisedSample.getContainer()).setPlate(new Plate());
    ((Well) plateSolubilisedSample.getContainer()).getPlate().setName("plate_1");
    plateSolubilisation.setTreatedSamples(Arrays.asList(plateSolubilisedSample));
    tubeDigestion = new Digestion(3L);
    tubeDigestion.setProtocol(new Protocol());
    tubeDigestion.getProtocol().setName("digestion_protocol_1");
    tubeDigestion.setInsertTime(Instant.now().minus(tubeDigestion.getId(), ChronoUnit.HOURS));
    tubeDigestedSample = new TreatedSample();
    tubeDigestedSample.setTreatment(tubeDigestion);
    tubeDigestedSample.setSample(sample1);
    tubeDigestedSample.setComment("tube_digestion_comment_1");
    tubeDigestedSample.setContainer(new Tube());
    ((Tube) tubeDigestedSample.getContainer()).setName("tube_1");
    tubeDigestedSample2 = new TreatedSample();
    tubeDigestedSample2.setTreatment(tubeDigestion);
    tubeDigestedSample2.setSample(sample2);
    tubeDigestedSample2.setComment("tube_digestion_comment_2");
    tubeDigestedSample2.setContainer(new Tube());
    ((Tube) tubeDigestedSample2.getContainer()).setName("tube_2");
    tubeDigestion.setTreatedSamples(Arrays.asList(tubeDigestedSample, tubeDigestedSample2));
    plateDigestion = new Digestion(4L);
    plateDigestion.setProtocol(new Protocol());
    plateDigestion.getProtocol().setName("digestion_protocol_2");
    plateDigestion.setInsertTime(Instant.now().minus(plateDigestion.getId(), ChronoUnit.HOURS));
    plateDigestedSample = new TreatedSample();
    plateDigestedSample.setTreatment(plateDigestion);
    plateDigestedSample.setSample(sample2);
    plateDigestedSample.setComment("plate_digestion_comment_1");
    plateDigestedSample.setContainer(new Well(1, 2));
    ((Well) plateDigestedSample.getContainer()).setPlate(new Plate());
    ((Well) plateDigestedSample.getContainer()).getPlate().setName("plate_1");
    plateDigestion.setTreatedSamples(Arrays.asList(plateDigestedSample));
    tubeStandardAddition = new StandardAddition(5L);
    tubeStandardAddition
        .setInsertTime(Instant.now().minus(tubeStandardAddition.getId(), ChronoUnit.HOURS));
    tubeAddedStandard = new TreatedSample();
    tubeAddedStandard.setTreatment(tubeStandardAddition);
    tubeAddedStandard.setSample(sample1);
    tubeAddedStandard.setName("std1");
    tubeAddedStandard.setQuantity("1 ug");
    tubeAddedStandard.setComment("tube_digestion_comment_1");
    tubeAddedStandard.setContainer(new Tube());
    ((Tube) tubeAddedStandard.getContainer()).setName("tube_1");
    tubeStandardAddition.setTreatedSamples(Arrays.asList(tubeAddedStandard));
    plateStandardAddition = new StandardAddition(6L);
    plateStandardAddition
        .setInsertTime(Instant.now().minus(plateStandardAddition.getId(), ChronoUnit.HOURS));
    plateAddedStandard = new TreatedSample();
    plateAddedStandard.setTreatment(plateStandardAddition);
    plateAddedStandard.setSample(sample2);
    plateAddedStandard.setName("std2");
    plateAddedStandard.setQuantity("2 ug");
    plateAddedStandard.setComment("plate_digestion_comment_1");
    plateAddedStandard.setContainer(new Well(1, 2));
    ((Well) plateAddedStandard.getContainer()).setPlate(new Plate());
    ((Well) plateAddedStandard.getContainer()).getPlate().setName("plate_1");
    plateStandardAddition.setTreatedSamples(Arrays.asList(plateAddedStandard));
    tubeEnrichment = new Enrichment(7L);
    tubeEnrichment.setProtocol(new Protocol());
    tubeEnrichment.getProtocol().setName("enrichment_protocol_1");
    tubeEnrichment.setInsertTime(Instant.now().minus(tubeEnrichment.getId(), ChronoUnit.HOURS));
    tubeEnrichedSample = new TreatedSample();
    tubeEnrichedSample.setTreatment(tubeEnrichment);
    tubeEnrichedSample.setSample(sample1);
    tubeEnrichedSample.setComment("tube_enrichment_comment_1");
    tubeEnrichedSample.setContainer(new Tube());
    ((Tube) tubeEnrichedSample.getContainer()).setName("tube_1");
    tubeEnrichment.setTreatedSamples(Arrays.asList(tubeEnrichedSample));
    plateEnrichment = new Enrichment(8L);
    plateEnrichment.setProtocol(new Protocol());
    plateEnrichment.getProtocol().setName("enrichment_protocol_2");
    plateEnrichment.setInsertTime(Instant.now().minus(plateEnrichment.getId(), ChronoUnit.HOURS));
    plateEnrichedSample = new TreatedSample();
    plateEnrichedSample.setTreatment(plateEnrichment);
    plateEnrichedSample.setSample(sample2);
    plateEnrichedSample.setComment("plate_enrichment_comment_1");
    plateEnrichedSample.setContainer(new Well(1, 2));
    ((Well) plateEnrichedSample.getContainer()).setPlate(new Plate());
    ((Well) plateEnrichedSample.getContainer()).getPlate().setName("plate_1");
    plateEnrichment.setTreatedSamples(Arrays.asList(plateEnrichedSample));
    tubeDilution = new Dilution(9L);
    tubeDilution.setInsertTime(Instant.now().minus(tubeDilution.getId(), ChronoUnit.HOURS));
    tubeDilutedSample = new TreatedSample();
    tubeDilutedSample.setTreatment(tubeDilution);
    tubeDilutedSample.setSample(sample1);
    tubeDilutedSample.setSolventVolume(2.0);
    tubeDilutedSample.setSolvent("CH3OH");
    tubeDilutedSample.setSolventVolume(18.0);
    tubeDilutedSample.setComment("tube_dilution_comment_1");
    tubeDilutedSample.setContainer(new Tube());
    ((Tube) tubeDilutedSample.getContainer()).setName("tube_1");
    tubeDilution.setTreatedSamples(Arrays.asList(tubeDilutedSample));
    plateDilution = new Dilution(10L);
    plateDilution.setInsertTime(Instant.now().minus(plateDilution.getId(), ChronoUnit.HOURS));
    plateDilutedSample = new TreatedSample();
    plateDilutedSample.setTreatment(plateDilution);
    plateDilutedSample.setSolventVolume(1.0);
    plateDilutedSample.setSolvent("H2O");
    plateDilutedSample.setSolventVolume(19.0);
    plateDilutedSample.setSample(sample2);
    plateDilutedSample.setComment("plate_dilution_comment_1");
    plateDilutedSample.setContainer(new Well(1, 2));
    ((Well) plateDilutedSample.getContainer()).setPlate(new Plate());
    ((Well) plateDilutedSample.getContainer()).getPlate().setName("plate_1");
    plateDilution.setTreatedSamples(Arrays.asList(plateDilutedSample));
    tubeFractionation = new Fractionation(11L);
    tubeFractionation.setFractionationType(FractionationType.MUDPIT);
    tubeFractionation
        .setInsertTime(Instant.now().minus(tubeFractionation.getId(), ChronoUnit.HOURS));
    tubeFractionatedSample = new TreatedSample();
    tubeFractionatedSample.setTreatment(tubeFractionation);
    tubeFractionatedSample.setSample(sample1);
    tubeFractionatedSample.setPosition(1);
    tubeFractionatedSample.setNumber(1);
    tubeFractionatedSample.setComment("tube_fractionation_comment_1");
    tubeFractionatedSample.setContainer(new Tube());
    tubeFractionatedSample.setDestinationContainer(new Tube());
    ((Tube) tubeFractionatedSample.getContainer()).setName("tube_1");
    ((Tube) tubeFractionatedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeFractionatedSample2 = new TreatedSample();
    tubeFractionatedSample2.setTreatment(tubeFractionation);
    tubeFractionatedSample2.setSample(sample1);
    tubeFractionatedSample2.setPosition(2);
    tubeFractionatedSample2.setNumber(2);
    tubeFractionatedSample2.setComment("tube_fractionation_comment_1");
    tubeFractionatedSample2.setContainer(new Tube());
    tubeFractionatedSample2.setDestinationContainer(new Tube());
    ((Tube) tubeFractionatedSample2.getContainer()).setName("tube_1");
    ((Tube) tubeFractionatedSample2.getDestinationContainer()).setName("destination_tube_1");
    tubeFractionation
        .setTreatedSamples(Arrays.asList(tubeFractionatedSample, tubeFractionatedSample2));
    plateFractionation = new Fractionation(12L);
    plateFractionation.setFractionationType(FractionationType.PI);
    plateFractionation
        .setInsertTime(Instant.now().minus(plateFractionation.getId(), ChronoUnit.HOURS));
    plateFractionatedSample = new TreatedSample();
    plateFractionatedSample.setTreatment(plateFractionation);
    plateFractionatedSample.setPosition(0);
    plateFractionatedSample.setPiInterval("2-3");
    plateFractionatedSample.setSample(sample2);
    plateFractionatedSample.setComment("plate_fractionation_comment_1");
    plateFractionatedSample.setContainer(new Well(1, 2));
    plateFractionatedSample.setDestinationContainer(new Well(2, 2));
    ((Well) plateFractionatedSample.getContainer()).setPlate(new Plate());
    ((Well) plateFractionatedSample.getContainer()).getPlate().setName("plate_1");
    ((Well) plateFractionatedSample.getDestinationContainer()).setPlate(new Plate());
    ((Well) plateFractionatedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateFractionation.setTreatedSamples(Arrays.asList(plateFractionatedSample));
    tubeTransfer = new Transfer(13L);
    tubeTransfer.setInsertTime(Instant.now().minus(tubeTransfer.getId(), ChronoUnit.HOURS));
    tubeTransferedSample = new TreatedSample();
    tubeTransferedSample.setTreatment(tubeTransfer);
    tubeTransferedSample.setSample(sample1);
    tubeTransferedSample.setComment("tube_transfer_comment_1");
    tubeTransferedSample.setContainer(new Tube());
    tubeTransferedSample.setDestinationContainer(new Tube());
    ((Tube) tubeTransferedSample.getContainer()).setName("tube_1");
    ((Tube) tubeTransferedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeTransfer.setTreatedSamples(Arrays.asList(tubeTransferedSample));
    plateTransfer = new Transfer(14L);
    plateTransfer.setInsertTime(Instant.now().minus(plateTransfer.getId(), ChronoUnit.HOURS));
    plateTransferedSample = new TreatedSample();
    plateTransferedSample.setTreatment(plateTransfer);
    plateTransferedSample.setSample(sample2);
    plateTransferedSample.setComment("plate_transfer_comment_2");
    plateTransferedSample.setContainer(new Well(1, 2));
    plateTransferedSample.setDestinationContainer(new Well(2, 2));
    ((Well) plateTransferedSample.getContainer()).setPlate(new Plate());
    ((Well) plateTransferedSample.getContainer()).getPlate().setName("plate_1");
    ((Well) plateTransferedSample.getDestinationContainer()).setPlate(new Plate());
    ((Well) plateTransferedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateTransfer.setTreatedSamples(Arrays.asList(plateTransferedSample));
    when(treatmentService.all(any(Submission.class))).thenReturn(Arrays.asList(tubeSolubilisation,
        plateSolubilisation, tubeDigestion, plateDigestion, tubeStandardAddition,
        plateStandardAddition, tubeEnrichment, plateEnrichment, tubeDilution, plateDilution,
        tubeFractionation, plateFractionation, tubeTransfer, plateTransfer));
    when(sampleContainerService.last(sample1))
        .thenReturn(tubeTransferedSample.getDestinationContainer());
    when(sampleContainerService.last(sample2))
        .thenReturn(plateTransferedSample.getDestinationContainer());
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setValue(submission);

    assertTrue(design.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(design.samples.getStyleName().contains(SAMPLES));
    assertTrue(design.treatmentsPanel.getStyleName().contains(TREATMENTS_PANEL));
    assertTrue(design.treatments.getStyleName().contains(TREATMENTS));
    assertTrue(design.treatments.getStyleName().contains(COMPONENTS));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(resources.message(SAMPLES_PANEL), design.samplesPanel.getCaption());
    assertEquals(resources.message(TREATMENTS_PANEL), design.treatmentsPanel.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    Collection<SubmissionSample> samples = dataProvider(design.samples).getItems();
    assertEquals(3, design.samples.getColumns().size());
    assertEquals(SAMPLES_NAME, design.samples.getColumns().get(0).getId());
    assertEquals(SAMPLES_STATUS, design.samples.getColumns().get(1).getId());
    assertEquals(SAMPLES_LAST_CONTAINER, design.samples.getColumns().get(2).getId());
    assertEquals(resources.message(SAMPLES_NAME),
        design.samples.getColumn(SAMPLES_NAME).getCaption());
    for (SubmissionSample sample : samples) {
      assertEquals(sample.getName(),
          design.samples.getColumn(SAMPLES_NAME).getValueProvider().apply(sample));
    }
    assertEquals(resources.message(SAMPLES_STATUS),
        design.samples.getColumn(SAMPLES_STATUS).getCaption());
    for (SubmissionSample sample : samples) {
      assertEquals(sample.getStatus().getLabel(locale),
          design.samples.getColumn(SAMPLES_STATUS).getValueProvider().apply(sample));
    }
    assertEquals(resources.message(SAMPLES_LAST_CONTAINER),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getDestinationContainer().getFullName(),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample1));
    assertEquals(plateTransferedSample.getDestinationContainer().getFullName(),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample2));
    assertEquals(2, samples.size());
    assertTrue(samples.contains(sample1));
    assertTrue(samples.contains(sample2));
  }

  @Test
  @SuppressWarnings("rawtypes")
  public void treatmentsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    Collection<Treatment> treatments = new ArrayList<>(dataProvider(design.treatments).getItems());
    assertEquals(3, design.treatments.getColumns().size());
    assertEquals(TREATMENT_TYPE, design.treatments.getColumns().get(0).getId());
    assertEquals(TREATMENT_TIME, design.treatments.getColumns().get(1).getId());
    assertEquals(TREATMENT_SAMPLES, design.treatments.getColumns().get(2).getId());
    assertEquals(resources.message(TREATMENT_TYPE),
        design.treatments.getColumn(TREATMENT_TYPE).getCaption());
    for (Treatment treatment : treatments) {
      Button button =
          (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider().apply(treatment);
      assertTrue(button.getStyleName().contains(TREATMENT_TYPE));
      assertEquals(treatment.getType().getLabel(locale), button.getCaption());
    }
    assertEquals(resources.message(TREATMENT_TIME),
        design.treatments.getColumn(TREATMENT_TIME).getCaption());
    DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());
    for (Treatment treatment : treatments) {
      assertEquals(dateFormatter.format(treatment.getInsertTime()),
          design.treatments.getColumn(TREATMENT_TIME).getValueProvider().apply(treatment));
    }
    assertEquals(resources.message(TREATMENT_SAMPLES),
        design.treatments.getColumn(TREATMENT_SAMPLES).getCaption());
    assertEquals(2L,
        design.treatments.getColumn(TREATMENT_SAMPLES).getValueProvider().apply(tubeDigestion));
    assertEquals(sample1.getName() + "\n" + sample2.getName(), design.treatments
        .getColumn(TREATMENT_SAMPLES).getDescriptionGenerator().apply(tubeDigestion));
    for (Treatment treatment : Arrays.asList(tubeSolubilisation, tubeStandardAddition,
        tubeEnrichment, tubeDilution, tubeFractionation, tubeTransfer)) {
      assertEquals(1L,
          design.treatments.getColumn(TREATMENT_SAMPLES).getValueProvider().apply(treatment));
      assertEquals(sample1.getName(), design.treatments.getColumn(TREATMENT_SAMPLES)
          .getDescriptionGenerator().apply(treatment));
    }
    for (Treatment treatment : Arrays.asList(plateSolubilisation, plateDigestion,
        plateStandardAddition, plateEnrichment, plateDilution, plateFractionation, plateTransfer)) {
      assertEquals(1L,
          design.treatments.getColumn(TREATMENT_SAMPLES).getValueProvider().apply(treatment));
      assertEquals(sample2.getName(), design.treatments.getColumn(TREATMENT_SAMPLES)
          .getDescriptionGenerator().apply(treatment));
    }
    List<GridSortOrder<Treatment>> sortOrders = new ArrayList<>(design.treatments.getSortOrder());
    assertEquals(1, sortOrders.size());
    GridSortOrder<Treatment> sortOrder = sortOrders.get(0);
    assertEquals(TREATMENT_TIME, sortOrder.getSorted().getId());
    assertEquals(SortDirection.ASCENDING, sortOrder.getDirection());
    assertEquals(14, treatments.size());
    assertTrue(treatments.contains(tubeSolubilisation));
    assertTrue(treatments.contains(plateSolubilisation));
    assertTrue(treatments.contains(tubeDigestion));
    assertTrue(treatments.contains(plateDigestion));
    assertTrue(treatments.contains(tubeStandardAddition));
    assertTrue(treatments.contains(plateStandardAddition));
    assertTrue(treatments.contains(tubeEnrichment));
    assertTrue(treatments.contains(plateEnrichment));
    assertTrue(treatments.contains(tubeDilution));
    assertTrue(treatments.contains(plateDilution));
    assertTrue(treatments.contains(tubeFractionation));
    assertTrue(treatments.contains(plateFractionation));
    assertTrue(treatments.contains(tubeTransfer));
    assertTrue(treatments.contains(plateTransfer));
  }

  @Test
  public void viewTreatmentDigestion() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider()
        .apply(tubeDigestion);

    button.click();

    verify(view).navigateTo(DigestionView.VIEW_NAME, String.valueOf(tubeDigestion.getId()));
  }

  @Test
  public void viewTreatmentDilution() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button =
        (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider().apply(tubeDilution);

    button.click();

    verify(view).navigateTo(DilutionView.VIEW_NAME, String.valueOf(tubeDilution.getId()));
  }

  @Test
  public void viewTreatmentEnrichment() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider()
        .apply(tubeEnrichment);

    button.click();

    verify(view).navigateTo(EnrichmentView.VIEW_NAME, String.valueOf(tubeEnrichment.getId()));
  }

  @Test
  public void viewTreatmentFractionation() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider()
        .apply(tubeFractionation);

    button.click();

    verify(view).navigateTo(FractionationView.VIEW_NAME, String.valueOf(tubeFractionation.getId()));
  }

  @Test
  public void viewTreatmentSolubilisation() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider()
        .apply(tubeSolubilisation);

    button.click();

    verify(view).navigateTo(SolubilisationView.VIEW_NAME,
        String.valueOf(tubeSolubilisation.getId()));
  }

  @Test
  public void viewTreatmentStandardAddition() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button = (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider()
        .apply(tubeStandardAddition);

    button.click();

    verify(view).navigateTo(StandardAdditionView.VIEW_NAME,
        String.valueOf(tubeStandardAddition.getId()));
  }

  @Test
  public void viewTreatmentTransfer() {
    presenter.init(view);
    presenter.setValue(submission);
    Button button =
        (Button) design.treatments.getColumn(TREATMENT_TYPE).getValueProvider().apply(tubeTransfer);

    button.click();

    verify(view).navigateTo(TransferView.VIEW_NAME, String.valueOf(tubeTransfer.getId()));
  }
}
