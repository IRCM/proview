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

import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTION_PROTOCOL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DIGESTION_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOLVENT_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOURCE_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_PROTOCOL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_TYPE_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_LAST_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATION_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATION_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SOLUBILISATION_SOLVENT_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITION_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITION_QUANTITY;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.STANDARD_ADDITION_SAMPLE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFERS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_SAMPLE;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.digestion.DigestedSample;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.DigestionProtocol;
import ca.qc.ircm.proview.digestion.DigestionService;
import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.enrichment.EnrichedSample;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.EnrichmentProtocol;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.Well;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.solubilisation.SolubilisationService;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.standard.StandardAdditionService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.transfer.TransferedSample;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionTreatmentsFormPresenterTest {
  private SubmissionTreatmentsFormPresenter presenter;
  @Mock
  private SubmissionTreatmentsForm view;
  @Mock
  private SampleContainerService sampleContainerService;
  @Mock
  private SolubilisationService solubilisationService;
  @Mock
  private DigestionService digestionService;
  @Mock
  private EnrichmentService enrichmentService;
  @Mock
  private DilutionService dilutionService;
  @Mock
  private StandardAdditionService standardAdditionService;
  @Mock
  private FractionationService fractionationService;
  @Mock
  private TransferService transferService;
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
  private SolubilisedSample tubeSolubilisedSample;
  private Solubilisation plateSolubilisation;
  private SolubilisedSample plateSolubilisedSample;
  private Digestion tubeDigestion;
  private DigestedSample tubeDigestedSample;
  private Digestion plateDigestion;
  private DigestedSample plateDigestedSample;
  private StandardAddition tubeStandardAddition;
  private AddedStandard tubeAddedStandard;
  private StandardAddition plateStandardAddition;
  private AddedStandard plateAddedStandard;
  private Enrichment tubeEnrichment;
  private EnrichedSample tubeEnrichedSample;
  private Enrichment plateEnrichment;
  private EnrichedSample plateEnrichedSample;
  private Dilution tubeDilution;
  private DilutedSample tubeDilutedSample;
  private Dilution plateDilution;
  private DilutedSample plateDilutedSample;
  private Fractionation tubeFractionation;
  private FractionationDetail tubeFractionatedSample;
  private Fractionation plateFractionation;
  private FractionationDetail plateFractionatedSample;
  private Transfer tubeTransfer;
  private TransferedSample tubeTransferedSample;
  private Transfer plateTransfer;
  private TransferedSample plateTransferedSample;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionTreatmentsFormPresenter(sampleContainerService, solubilisationService,
        digestionService, enrichmentService, dilutionService, standardAdditionService,
        fractionationService, transferService);
    design = new SubmissionTreatmentsFormDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    submission = new Submission();
    sample1 = new SubmissionSample();
    sample1.setName("sample_name");
    sample1.setSupport(SampleSupport.SOLUTION);
    sample1.setStatus(SampleStatus.ANALYSED);
    sample1.setQuantity("10.4 ug");
    sample1.setVolume(10.3);
    sample1.setNumberProtein(4);
    sample1.setMolecularWeight(5.6);
    sample1.setSubmission(submission);
    tube1 = new Tube();
    tube1.setName("tube_name");
    sample1.setOriginalContainer(tube1);
    sample2 = new SubmissionSample();
    sample2.setName("sample_name");
    sample2.setSupport(SampleSupport.SOLUTION);
    sample2.setStatus(SampleStatus.ANALYSED);
    sample2.setQuantity("10.4 ug");
    sample2.setVolume(10.3);
    sample2.setNumberProtein(4);
    sample2.setMolecularWeight(5.6);
    sample2.setSubmission(submission);
    tube2 = new Tube();
    tube2.setName("tube_name");
    sample2.setOriginalContainer(tube1);
    submission.setSamples(Arrays.asList(sample1, sample2));
    tubeSolubilisation = new Solubilisation();
    tubeSolubilisedSample = new SolubilisedSample();
    tubeSolubilisedSample.setSolubilisation(tubeSolubilisation);
    tubeSolubilisedSample.setSample(sample1);
    tubeSolubilisedSample.setSolvent("ch3oh");
    tubeSolubilisedSample.setSolventVolume(10.0);
    tubeSolubilisedSample.setComments("tube_digestion_comment_1");
    tubeSolubilisedSample.setContainer(new Tube());
    ((Tube) tubeSolubilisedSample.getContainer()).setName("tube_1");
    tubeSolubilisation.setTreatmentSamples(Arrays.asList(tubeSolubilisedSample));
    plateSolubilisation = new Solubilisation();
    plateSolubilisedSample = new SolubilisedSample();
    plateSolubilisedSample.setSolubilisation(plateSolubilisation);
    plateSolubilisedSample.setSample(sample2);
    tubeSolubilisedSample.setSolvent("ch2oh2");
    tubeSolubilisedSample.setSolventVolume(20.0);
    plateSolubilisedSample.setComments("plate_digestion_comment_1");
    plateSolubilisedSample.setContainer(new Well(1, 2));
    ((Well) plateSolubilisedSample.getContainer()).setPlate(new Plate());
    ((Well) plateSolubilisedSample.getContainer()).getPlate().setName("plate_1");
    plateSolubilisation.setTreatmentSamples(Arrays.asList(plateSolubilisedSample));
    when(solubilisationService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeSolubilisation, plateSolubilisation));
    tubeDigestion = new Digestion();
    tubeDigestion.setProtocol(new DigestionProtocol());
    tubeDigestion.getProtocol().setName("digestion_protocol_1");
    tubeDigestedSample = new DigestedSample();
    tubeDigestedSample.setDigestion(tubeDigestion);
    tubeDigestedSample.setSample(sample1);
    tubeDigestedSample.setComments("tube_digestion_comment_1");
    tubeDigestedSample.setContainer(new Tube());
    ((Tube) tubeDigestedSample.getContainer()).setName("tube_1");
    tubeDigestion.setTreatmentSamples(Arrays.asList(tubeDigestedSample));
    plateDigestion = new Digestion();
    plateDigestion.setProtocol(new DigestionProtocol());
    plateDigestion.getProtocol().setName("digestion_protocol_2");
    plateDigestedSample = new DigestedSample();
    plateDigestedSample.setDigestion(plateDigestion);
    plateDigestedSample.setSample(sample2);
    plateDigestedSample.setComments("plate_digestion_comment_1");
    plateDigestedSample.setContainer(new Well(1, 2));
    ((Well) plateDigestedSample.getContainer()).setPlate(new Plate());
    ((Well) plateDigestedSample.getContainer()).getPlate().setName("plate_1");
    plateDigestion.setTreatmentSamples(Arrays.asList(plateDigestedSample));
    when(digestionService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeDigestion, plateDigestion));
    tubeStandardAddition = new StandardAddition();
    tubeAddedStandard = new AddedStandard();
    tubeAddedStandard.setStandardAddition(tubeStandardAddition);
    tubeAddedStandard.setSample(sample1);
    tubeAddedStandard.setName("std1");
    tubeAddedStandard.setQuantity("1 ug");
    tubeAddedStandard.setComments("tube_digestion_comment_1");
    tubeAddedStandard.setContainer(new Tube());
    ((Tube) tubeAddedStandard.getContainer()).setName("tube_1");
    tubeStandardAddition.setTreatmentSamples(Arrays.asList(tubeAddedStandard));
    plateStandardAddition = new StandardAddition();
    plateAddedStandard = new AddedStandard();
    plateAddedStandard.setStandardAddition(plateStandardAddition);
    plateAddedStandard.setSample(sample2);
    tubeAddedStandard.setName("std2");
    tubeAddedStandard.setQuantity("2 ug");
    plateAddedStandard.setComments("plate_digestion_comment_1");
    plateAddedStandard.setContainer(new Well(1, 2));
    ((Well) plateAddedStandard.getContainer()).setPlate(new Plate());
    ((Well) plateAddedStandard.getContainer()).getPlate().setName("plate_1");
    plateStandardAddition.setTreatmentSamples(Arrays.asList(plateAddedStandard));
    when(standardAdditionService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeStandardAddition, plateStandardAddition));
    tubeEnrichment = new Enrichment();
    tubeEnrichment.setProtocol(new EnrichmentProtocol());
    tubeEnrichment.getProtocol().setName("enrichment_protocol_1");
    tubeEnrichedSample = new EnrichedSample();
    tubeEnrichedSample.setEnrichment(tubeEnrichment);
    tubeEnrichedSample.setSample(sample1);
    tubeEnrichedSample.setComments("tube_enrichment_comment_1");
    tubeEnrichedSample.setContainer(new Tube());
    ((Tube) tubeEnrichedSample.getContainer()).setName("tube_1");
    tubeEnrichment.setTreatmentSamples(Arrays.asList(tubeEnrichedSample));
    plateEnrichment = new Enrichment();
    plateEnrichment.setProtocol(new EnrichmentProtocol());
    plateEnrichment.getProtocol().setName("enrichment_protocol_2");
    plateEnrichedSample = new EnrichedSample();
    plateEnrichedSample.setEnrichment(plateEnrichment);
    plateEnrichedSample.setSample(sample2);
    plateEnrichedSample.setComments("plate_enrichment_comment_1");
    plateEnrichedSample.setContainer(new Well(1, 2));
    ((Well) plateEnrichedSample.getContainer()).setPlate(new Plate());
    ((Well) plateEnrichedSample.getContainer()).getPlate().setName("plate_1");
    plateEnrichment.setTreatmentSamples(Arrays.asList(plateEnrichedSample));
    when(enrichmentService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeEnrichment, plateEnrichment));
    tubeDilution = new Dilution();
    tubeDilutedSample = new DilutedSample();
    tubeDilutedSample.setDilution(tubeDilution);
    tubeDilutedSample.setSample(sample1);
    tubeDilutedSample.setSolventVolume(2.0);
    tubeDilutedSample.setSolvent("CH3OH");
    tubeDilutedSample.setSolventVolume(18.0);
    tubeDilutedSample.setComments("tube_dilution_comment_1");
    tubeDilutedSample.setContainer(new Tube());
    ((Tube) tubeDilutedSample.getContainer()).setName("tube_1");
    tubeDilution.setTreatmentSamples(Arrays.asList(tubeDilutedSample));
    plateDilution = new Dilution();
    plateDilutedSample = new DilutedSample();
    plateDilutedSample.setDilution(plateDilution);
    plateDilutedSample.setSolventVolume(1.0);
    plateDilutedSample.setSolvent("H2O");
    plateDilutedSample.setSolventVolume(19.0);
    plateDilutedSample.setSample(sample2);
    plateDilutedSample.setComments("plate_dilution_comment_1");
    plateDilutedSample.setContainer(new Well(1, 2));
    ((Well) plateDilutedSample.getContainer()).setPlate(new Plate());
    ((Well) plateDilutedSample.getContainer()).getPlate().setName("plate_1");
    plateDilution.setTreatmentSamples(Arrays.asList(plateDilutedSample));
    when(dilutionService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeDilution, plateDilution));
    tubeFractionation = new Fractionation();
    tubeFractionation.setFractionationType(FractionationType.MUDPIT);
    tubeFractionatedSample = new FractionationDetail();
    tubeFractionatedSample.setFractionation(tubeFractionation);
    tubeFractionatedSample.setSample(sample1);
    tubeFractionatedSample.setPosition(1);
    tubeFractionatedSample.setNumber(1);
    tubeFractionatedSample.setComments("tube_fractionation_comment_1");
    tubeFractionatedSample.setContainer(new Tube());
    tubeFractionatedSample.setDestinationContainer(new Tube());
    ((Tube) tubeFractionatedSample.getContainer()).setName("tube_1");
    ((Tube) tubeFractionatedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeFractionation.setTreatmentSamples(Arrays.asList(tubeFractionatedSample));
    plateFractionation = new Fractionation();
    plateFractionation.setFractionationType(FractionationType.PI);
    plateFractionatedSample = new FractionationDetail();
    plateFractionatedSample.setFractionation(plateFractionation);
    plateFractionatedSample.setPosition(0);
    plateFractionatedSample.setPiInterval("2-3");
    plateFractionatedSample.setSample(sample2);
    plateFractionatedSample.setComments("plate_fractionation_comment_1");
    plateFractionatedSample.setContainer(new Well(1, 2));
    plateFractionatedSample.setDestinationContainer(new Well(2, 2));
    ((Well) plateFractionatedSample.getContainer()).setPlate(new Plate());
    ((Well) plateFractionatedSample.getContainer()).getPlate().setName("plate_1");
    ((Well) plateFractionatedSample.getDestinationContainer()).setPlate(new Plate());
    ((Well) plateFractionatedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateFractionation.setTreatmentSamples(Arrays.asList(plateFractionatedSample));
    when(fractionationService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeFractionation, plateFractionation));
    tubeTransfer = new Transfer();
    tubeTransferedSample = new TransferedSample();
    tubeTransferedSample.setTransfer(tubeTransfer);
    tubeTransferedSample.setSample(sample1);
    tubeTransferedSample.setComments("tube_transfer_comment_1");
    tubeTransferedSample.setContainer(new Tube());
    tubeTransferedSample.setDestinationContainer(new Tube());
    ((Tube) tubeTransferedSample.getContainer()).setName("tube_1");
    ((Tube) tubeTransferedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeTransfer.setTreatmentSamples(Arrays.asList(tubeTransferedSample));
    plateTransfer = new Transfer();
    plateTransferedSample = new TransferedSample();
    plateTransferedSample.setTransfer(plateTransfer);
    plateTransferedSample.setSample(sample2);
    plateTransferedSample.setComments("plate_transfer_comment_2");
    plateTransferedSample.setContainer(new Well(1, 2));
    plateTransferedSample.setDestinationContainer(new Well(2, 2));
    ((Well) plateTransferedSample.getContainer()).setPlate(new Plate());
    ((Well) plateTransferedSample.getContainer()).getPlate().setName("plate_1");
    ((Well) plateTransferedSample.getDestinationContainer()).setPlate(new Plate());
    ((Well) plateTransferedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateTransfer.setTreatmentSamples(Arrays.asList(plateTransferedSample));
    when(transferService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeTransfer, plateTransfer));
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
    assertTrue(design.solubilisationsPanel.getStyleName().contains(SOLUBILISATIONS_PANEL));
    assertTrue(design.solubilisations.getStyleName().contains(SOLUBILISATIONS));
    assertTrue(design.transfersPanel.getStyleName().contains(TRANSFERS_PANEL));
    assertTrue(design.transfers.getStyleName().contains(TRANSFERS));
    assertTrue(design.digestionsPanel.getStyleName().contains(DIGESTIONS_PANEL));
    assertTrue(design.digestions.getStyleName().contains(DIGESTIONS));
    assertTrue(design.standardAdditionsPanel.getStyleName().contains(STANDARD_ADDITIONS_PANEL));
    assertTrue(design.standardAdditions.getStyleName().contains(STANDARD_ADDITIONS));
    assertTrue(design.enrichmentsPanel.getStyleName().contains(ENRICHMENTS_PANEL));
    assertTrue(design.enrichments.getStyleName().contains(ENRICHMENTS));
    assertTrue(design.dilutionsPanel.getStyleName().contains(DILUTIONS_PANEL));
    assertTrue(design.dilutions.getStyleName().contains(DILUTIONS));
    assertTrue(design.fractionationsPanel.getStyleName().contains(FRACTIONATIONS_PANEL));
    assertTrue(design.fractionations.getStyleName().contains(FRACTIONATIONS));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(resources.message(SAMPLES_PANEL), design.samplesPanel.getCaption());
    assertEquals(resources.message(SOLUBILISATIONS_PANEL),
        design.solubilisationsPanel.getCaption());
    assertEquals(resources.message(TRANSFERS_PANEL), design.transfersPanel.getCaption());
    assertEquals(resources.message(DIGESTIONS_PANEL), design.digestionsPanel.getCaption());
    assertEquals(resources.message(STANDARD_ADDITIONS_PANEL),
        design.standardAdditionsPanel.getCaption());
    assertEquals(resources.message(ENRICHMENTS_PANEL), design.enrichmentsPanel.getCaption());
    assertEquals(resources.message(DILUTIONS_PANEL), design.dilutionsPanel.getCaption());
    assertEquals(resources.message(FRACTIONATIONS_PANEL), design.fractionationsPanel.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(2, design.samples.getColumns().size());
    assertEquals(SAMPLES_NAME, design.samples.getColumns().get(0).getId());
    assertEquals(SAMPLES_LAST_CONTAINER, design.samples.getColumns().get(1).getId());
    assertEquals(resources.message(SAMPLES_NAME),
        design.samples.getColumn(SAMPLES_NAME).getCaption());
    assertEquals(sample1.getName(),
        design.samples.getColumn(SAMPLES_NAME).getValueProvider().apply(sample1));
    assertEquals(sample2.getName(),
        design.samples.getColumn(SAMPLES_NAME).getValueProvider().apply(sample2));
    assertEquals(resources.message(SAMPLES_LAST_CONTAINER),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getDestinationContainer().getFullName(),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample1));
    assertEquals(plateTransferedSample.getDestinationContainer().getFullName(),
        design.samples.getColumn(SAMPLES_LAST_CONTAINER).getValueProvider().apply(sample2));

    Collection<SubmissionSample> samples = dataProvider(design.samples).getItems();
    assertEquals(2, samples.size());
    assertTrue(samples.contains(sample1));
    assertTrue(samples.contains(sample2));
  }

  @Test
  public void solubilisationsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(5, design.solubilisations.getColumns().size());
    assertEquals(SOLUBILISATION_SAMPLE, design.solubilisations.getColumns().get(0).getId());
    assertEquals(SOLUBILISATION_SOLVENT, design.solubilisations.getColumns().get(1).getId());
    assertEquals(SOLUBILISATION_SOLVENT_VOLUME, design.solubilisations.getColumns().get(2).getId());
    assertEquals(SOLUBILISATION_CONTAINER, design.solubilisations.getColumns().get(3).getId());
    assertEquals(SOLUBILISATION_COMMENTS, design.solubilisations.getColumns().get(4).getId());
    assertEquals(resources.message(SOLUBILISATION_SAMPLE),
        design.solubilisations.getColumn(SOLUBILISATION_SAMPLE).getCaption());
    assertEquals(tubeSolubilisedSample.getSample().getName(), design.solubilisations
        .getColumn(SOLUBILISATION_SAMPLE).getValueProvider().apply(tubeSolubilisedSample));
    assertEquals(plateSolubilisedSample.getSample().getName(), design.solubilisations
        .getColumn(SOLUBILISATION_SAMPLE).getValueProvider().apply(plateSolubilisedSample));
    assertEquals(resources.message(SOLUBILISATION_SOLVENT),
        design.solubilisations.getColumn(SOLUBILISATION_SOLVENT).getCaption());
    assertEquals(tubeSolubilisedSample.getSolvent(), design.solubilisations
        .getColumn(SOLUBILISATION_SOLVENT).getValueProvider().apply(tubeSolubilisedSample));
    assertEquals(plateSolubilisedSample.getSolvent(), design.solubilisations
        .getColumn(SOLUBILISATION_SOLVENT).getValueProvider().apply(plateSolubilisedSample));
    assertEquals(resources.message(SOLUBILISATION_SOLVENT_VOLUME),
        design.solubilisations.getColumn(SOLUBILISATION_SOLVENT_VOLUME).getCaption());
    assertEquals(tubeSolubilisedSample.getSolventVolume(), design.solubilisations
        .getColumn(SOLUBILISATION_SOLVENT_VOLUME).getValueProvider().apply(tubeSolubilisedSample));
    assertEquals(plateSolubilisedSample.getSolventVolume(), design.solubilisations
        .getColumn(SOLUBILISATION_SOLVENT_VOLUME).getValueProvider().apply(plateSolubilisedSample));
    assertEquals(resources.message(SOLUBILISATION_CONTAINER),
        design.solubilisations.getColumn(SOLUBILISATION_CONTAINER).getCaption());
    assertEquals(tubeSolubilisedSample.getContainer().getFullName(), design.solubilisations
        .getColumn(SOLUBILISATION_CONTAINER).getValueProvider().apply(tubeSolubilisedSample));
    assertEquals(plateSolubilisedSample.getContainer().getFullName(), design.solubilisations
        .getColumn(SOLUBILISATION_CONTAINER).getValueProvider().apply(plateSolubilisedSample));
    assertEquals(resources.message(SOLUBILISATION_COMMENTS),
        design.solubilisations.getColumn(SOLUBILISATION_COMMENTS).getCaption());
    assertEquals(tubeSolubilisedSample.getComments(), design.solubilisations
        .getColumn(SOLUBILISATION_COMMENTS).getValueProvider().apply(tubeSolubilisedSample));
    assertEquals(plateSolubilisedSample.getComments(), design.solubilisations
        .getColumn(SOLUBILISATION_COMMENTS).getValueProvider().apply(plateSolubilisedSample));

    assertTrue(design.solubilisationsPanel.isVisible());
    Collection<SolubilisedSample> solubilisedSamples =
        dataProvider(design.solubilisations).getItems();
    assertEquals(2, solubilisedSamples.size());
    assertTrue(solubilisedSamples.contains(tubeSolubilisedSample));
    assertTrue(solubilisedSamples.contains(plateSolubilisedSample));
  }

  @Test
  public void solubilisationsGrid_Empty() {
    when(solubilisationService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.solubilisationsPanel.isVisible());
  }

  @Test
  public void transfersGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(4, design.transfers.getColumns().size());
    assertEquals(TRANSFER_SAMPLE, design.transfers.getColumns().get(0).getId());
    assertEquals(TRANSFER_CONTAINER, design.transfers.getColumns().get(1).getId());
    assertEquals(TRANSFER_DESTINATION_CONTAINER, design.transfers.getColumns().get(2).getId());
    assertEquals(TRANSFER_COMMENTS, design.transfers.getColumns().get(3).getId());
    assertEquals(resources.message(TRANSFER_SAMPLE),
        design.transfers.getColumn(TRANSFER_SAMPLE).getCaption());
    assertEquals(tubeTransferedSample.getSample().getName(),
        design.transfers.getColumn(TRANSFER_SAMPLE).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getSample().getName(), design.transfers
        .getColumn(TRANSFER_SAMPLE).getValueProvider().apply(plateTransferedSample));
    assertEquals(resources.message(TRANSFER_CONTAINER),
        design.transfers.getColumn(TRANSFER_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getContainer().getFullName(), design.transfers
        .getColumn(TRANSFER_CONTAINER).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getContainer().getFullName(), design.transfers
        .getColumn(TRANSFER_CONTAINER).getValueProvider().apply(plateTransferedSample));
    assertEquals(resources.message(TRANSFER_DESTINATION_CONTAINER),
        design.transfers.getColumn(TRANSFER_DESTINATION_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getDestinationContainer().getFullName(), design.transfers
        .getColumn(TRANSFER_DESTINATION_CONTAINER).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getDestinationContainer().getFullName(), design.transfers
        .getColumn(TRANSFER_DESTINATION_CONTAINER).getValueProvider().apply(plateTransferedSample));
    assertEquals(resources.message(TRANSFER_COMMENTS),
        design.transfers.getColumn(TRANSFER_COMMENTS).getCaption());
    assertEquals(tubeTransferedSample.getComments(), design.transfers.getColumn(TRANSFER_COMMENTS)
        .getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getComments(), design.transfers.getColumn(TRANSFER_COMMENTS)
        .getValueProvider().apply(plateTransferedSample));

    assertTrue(design.transfersPanel.isVisible());
    Collection<TransferedSample> transferedSamples = dataProvider(design.transfers).getItems();
    assertEquals(2, transferedSamples.size());
    assertTrue(transferedSamples.contains(tubeTransferedSample));
    assertTrue(transferedSamples.contains(plateTransferedSample));
  }

  @Test
  public void transfersGrid_Empty() {
    when(transferService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.transfersPanel.isVisible());
  }

  @Test
  public void digestionsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(4, design.digestions.getColumns().size());
    assertEquals(DIGESTION_SAMPLE, design.digestions.getColumns().get(0).getId());
    assertEquals(DIGESTION_PROTOCOL, design.digestions.getColumns().get(1).getId());
    assertEquals(DIGESTION_CONTAINER, design.digestions.getColumns().get(2).getId());
    assertEquals(DIGESTION_COMMENTS, design.digestions.getColumns().get(3).getId());
    assertEquals(resources.message(DIGESTION_SAMPLE),
        design.digestions.getColumn(DIGESTION_SAMPLE).getCaption());
    assertEquals(tubeDigestedSample.getSample().getName(),
        design.digestions.getColumn(DIGESTION_SAMPLE).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestedSample.getSample().getName(), design.digestions
        .getColumn(DIGESTION_SAMPLE).getValueProvider().apply(plateDigestedSample));
    assertEquals(resources.message(DIGESTION_PROTOCOL),
        design.digestions.getColumn(DIGESTION_PROTOCOL).getCaption());
    assertEquals(tubeDigestion.getProtocol().getName(), design.digestions
        .getColumn(DIGESTION_PROTOCOL).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestion.getProtocol().getName(), design.digestions
        .getColumn(DIGESTION_PROTOCOL).getValueProvider().apply(plateDigestedSample));
    assertEquals(resources.message(DIGESTION_CONTAINER),
        design.digestions.getColumn(DIGESTION_CONTAINER).getCaption());
    assertEquals(tubeDigestedSample.getContainer().getFullName(), design.digestions
        .getColumn(DIGESTION_CONTAINER).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestedSample.getContainer().getFullName(), design.digestions
        .getColumn(DIGESTION_CONTAINER).getValueProvider().apply(plateDigestedSample));
    assertEquals(resources.message(DIGESTION_COMMENTS),
        design.digestions.getColumn(DIGESTION_COMMENTS).getCaption());
    assertEquals(tubeDigestedSample.getComments(), design.digestions.getColumn(DIGESTION_COMMENTS)
        .getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestedSample.getComments(), design.digestions.getColumn(DIGESTION_COMMENTS)
        .getValueProvider().apply(plateDigestedSample));

    assertTrue(design.digestionsPanel.isVisible());
    Collection<DigestedSample> digestedSamples = dataProvider(design.digestions).getItems();
    assertEquals(2, digestedSamples.size());
    assertTrue(digestedSamples.contains(tubeDigestedSample));
    assertTrue(digestedSamples.contains(plateDigestedSample));
  }

  @Test
  public void digestionsGrid_Empty() {
    when(digestionService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.digestionsPanel.isVisible());
  }

  @Test
  public void standardAdditionsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(5, design.standardAdditions.getColumns().size());
    assertEquals(STANDARD_ADDITION_SAMPLE, design.standardAdditions.getColumns().get(0).getId());
    assertEquals(STANDARD_ADDITION_NAME, design.standardAdditions.getColumns().get(1).getId());
    assertEquals(STANDARD_ADDITION_QUANTITY, design.standardAdditions.getColumns().get(2).getId());
    assertEquals(STANDARD_ADDITION_CONTAINER, design.standardAdditions.getColumns().get(3).getId());
    assertEquals(STANDARD_ADDITION_COMMENTS, design.standardAdditions.getColumns().get(4).getId());
    assertEquals(resources.message(STANDARD_ADDITION_SAMPLE),
        design.standardAdditions.getColumn(STANDARD_ADDITION_SAMPLE).getCaption());
    assertEquals(tubeAddedStandard.getSample().getName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_SAMPLE).getValueProvider().apply(tubeAddedStandard));
    assertEquals(plateAddedStandard.getSample().getName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_SAMPLE).getValueProvider().apply(plateAddedStandard));
    assertEquals(resources.message(STANDARD_ADDITION_NAME),
        design.standardAdditions.getColumn(STANDARD_ADDITION_NAME).getCaption());
    assertEquals(tubeAddedStandard.getName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_NAME).getValueProvider().apply(tubeAddedStandard));
    assertEquals(plateAddedStandard.getName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_NAME).getValueProvider().apply(plateAddedStandard));
    assertEquals(resources.message(STANDARD_ADDITION_QUANTITY),
        design.standardAdditions.getColumn(STANDARD_ADDITION_QUANTITY).getCaption());
    assertEquals(tubeAddedStandard.getQuantity(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_QUANTITY).getValueProvider().apply(tubeAddedStandard));
    assertEquals(plateAddedStandard.getQuantity(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_QUANTITY).getValueProvider().apply(plateAddedStandard));
    assertEquals(resources.message(STANDARD_ADDITION_CONTAINER),
        design.standardAdditions.getColumn(STANDARD_ADDITION_CONTAINER).getCaption());
    assertEquals(tubeAddedStandard.getContainer().getFullName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_CONTAINER).getValueProvider().apply(tubeAddedStandard));
    assertEquals(plateAddedStandard.getContainer().getFullName(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_CONTAINER).getValueProvider().apply(plateAddedStandard));
    assertEquals(resources.message(STANDARD_ADDITION_COMMENTS),
        design.standardAdditions.getColumn(STANDARD_ADDITION_COMMENTS).getCaption());
    assertEquals(tubeAddedStandard.getComments(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_COMMENTS).getValueProvider().apply(tubeAddedStandard));
    assertEquals(plateAddedStandard.getComments(), design.standardAdditions
        .getColumn(STANDARD_ADDITION_COMMENTS).getValueProvider().apply(plateAddedStandard));

    assertTrue(design.standardAdditionsPanel.isVisible());
    Collection<AddedStandard> addedStandards = dataProvider(design.standardAdditions).getItems();
    assertEquals(2, addedStandards.size());
    assertTrue(addedStandards.contains(tubeAddedStandard));
    assertTrue(addedStandards.contains(plateAddedStandard));
  }

  @Test
  public void standardAdditionsGrid_Empty() {
    when(standardAdditionService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.standardAdditionsPanel.isVisible());
  }

  @Test
  public void enrichmentsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(4, design.enrichments.getColumns().size());
    assertEquals(ENRICHMENT_SAMPLE, design.enrichments.getColumns().get(0).getId());
    assertEquals(ENRICHMENT_PROTOCOL, design.enrichments.getColumns().get(1).getId());
    assertEquals(ENRICHMENT_CONTAINER, design.enrichments.getColumns().get(2).getId());
    assertEquals(ENRICHMENT_COMMENTS, design.enrichments.getColumns().get(3).getId());
    assertEquals(resources.message(ENRICHMENT_SAMPLE),
        design.enrichments.getColumn(ENRICHMENT_SAMPLE).getCaption());
    assertEquals(tubeEnrichedSample.getSample().getName(), design.enrichments
        .getColumn(ENRICHMENT_SAMPLE).getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichedSample.getSample().getName(), design.enrichments
        .getColumn(ENRICHMENT_SAMPLE).getValueProvider().apply(plateEnrichedSample));
    assertEquals(resources.message(ENRICHMENT_PROTOCOL),
        design.enrichments.getColumn(ENRICHMENT_PROTOCOL).getCaption());
    assertEquals(tubeEnrichment.getProtocol().getName(), design.enrichments
        .getColumn(ENRICHMENT_PROTOCOL).getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichment.getProtocol().getName(), design.enrichments
        .getColumn(ENRICHMENT_PROTOCOL).getValueProvider().apply(plateEnrichedSample));
    assertEquals(resources.message(ENRICHMENT_CONTAINER),
        design.enrichments.getColumn(ENRICHMENT_CONTAINER).getCaption());
    assertEquals(tubeEnrichedSample.getContainer().getFullName(), design.enrichments
        .getColumn(ENRICHMENT_CONTAINER).getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichedSample.getContainer().getFullName(), design.enrichments
        .getColumn(ENRICHMENT_CONTAINER).getValueProvider().apply(plateEnrichedSample));
    assertEquals(resources.message(ENRICHMENT_COMMENTS),
        design.enrichments.getColumn(ENRICHMENT_COMMENTS).getCaption());
    assertEquals(tubeEnrichedSample.getComments(), design.enrichments.getColumn(ENRICHMENT_COMMENTS)
        .getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichedSample.getComments(), design.enrichments
        .getColumn(ENRICHMENT_COMMENTS).getValueProvider().apply(plateEnrichedSample));

    assertTrue(design.enrichmentsPanel.isVisible());
    Collection<EnrichedSample> enrichedSamples = dataProvider(design.enrichments).getItems();
    assertEquals(2, enrichedSamples.size());
    assertTrue(enrichedSamples.contains(tubeEnrichedSample));
    assertTrue(enrichedSamples.contains(plateEnrichedSample));
  }

  @Test
  public void enrichmentsGrid_Empty() {
    when(enrichmentService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.enrichmentsPanel.isVisible());
  }

  @Test
  public void dilutionsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(6, design.dilutions.getColumns().size());
    assertEquals(DILUTION_SAMPLE, design.dilutions.getColumns().get(0).getId());
    assertEquals(DILUTION_SOURCE_VOLUME, design.dilutions.getColumns().get(1).getId());
    assertEquals(DILUTION_SOLVENT, design.dilutions.getColumns().get(2).getId());
    assertEquals(DILUTION_SOLVENT_VOLUME, design.dilutions.getColumns().get(3).getId());
    assertEquals(DILUTION_CONTAINER, design.dilutions.getColumns().get(4).getId());
    assertEquals(DILUTION_COMMENTS, design.dilutions.getColumns().get(5).getId());
    assertEquals(resources.message(DILUTION_SAMPLE),
        design.dilutions.getColumn(DILUTION_SAMPLE).getCaption());
    assertEquals(tubeDilutedSample.getSample().getName(),
        design.dilutions.getColumn(DILUTION_SAMPLE).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSample().getName(),
        design.dilutions.getColumn(DILUTION_SAMPLE).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTION_SOURCE_VOLUME),
        design.dilutions.getColumn(DILUTION_SOURCE_VOLUME).getCaption());
    assertEquals(tubeDilutedSample.getSourceVolume(), design.dilutions
        .getColumn(DILUTION_SOURCE_VOLUME).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSourceVolume(), design.dilutions
        .getColumn(DILUTION_SOURCE_VOLUME).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTION_SOLVENT),
        design.dilutions.getColumn(DILUTION_SOLVENT).getCaption());
    assertEquals(tubeDilutedSample.getSolvent(),
        design.dilutions.getColumn(DILUTION_SOLVENT).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSolvent(),
        design.dilutions.getColumn(DILUTION_SOLVENT).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTION_SOLVENT_VOLUME),
        design.dilutions.getColumn(DILUTION_SOLVENT_VOLUME).getCaption());
    assertEquals(tubeDilutedSample.getSolventVolume(), design.dilutions
        .getColumn(DILUTION_SOLVENT_VOLUME).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSolventVolume(), design.dilutions
        .getColumn(DILUTION_SOLVENT_VOLUME).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTION_CONTAINER),
        design.dilutions.getColumn(DILUTION_CONTAINER).getCaption());
    assertEquals(tubeDilutedSample.getContainer().getFullName(),
        design.dilutions.getColumn(DILUTION_CONTAINER).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getContainer().getFullName(), design.dilutions
        .getColumn(DILUTION_CONTAINER).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTION_COMMENTS),
        design.dilutions.getColumn(DILUTION_COMMENTS).getCaption());
    assertEquals(tubeDilutedSample.getComments(),
        design.dilutions.getColumn(DILUTION_COMMENTS).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getComments(),
        design.dilutions.getColumn(DILUTION_COMMENTS).getValueProvider().apply(plateDilutedSample));

    assertTrue(design.dilutionsPanel.isVisible());
    Collection<DilutedSample> dilutedSamples = dataProvider(design.dilutions).getItems();
    assertEquals(2, dilutedSamples.size());
    assertTrue(dilutedSamples.contains(tubeDilutedSample));
    assertTrue(dilutedSamples.contains(plateDilutedSample));
  }

  @Test
  public void dilutionsGrid_Empty() {
    when(dilutionService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.dilutionsPanel.isVisible());
  }

  @Test
  public void fractionationsGrid() {
    presenter.init(view);
    presenter.setValue(submission);

    assertEquals(6, design.fractionations.getColumns().size());
    assertEquals(FRACTIONATION_SAMPLE, design.fractionations.getColumns().get(0).getId());
    assertEquals(FRACTIONATION_TYPE, design.fractionations.getColumns().get(1).getId());
    assertEquals(FRACTIONATION_TYPE_VALUE, design.fractionations.getColumns().get(2).getId());
    assertEquals(FRACTIONATION_CONTAINER, design.fractionations.getColumns().get(3).getId());
    assertEquals(FRACTIONATION_DESTINATION_CONTAINER,
        design.fractionations.getColumns().get(4).getId());
    assertEquals(FRACTIONATION_COMMENTS, design.fractionations.getColumns().get(5).getId());
    assertEquals(resources.message(FRACTIONATION_SAMPLE),
        design.fractionations.getColumn(FRACTIONATION_SAMPLE).getCaption());
    assertEquals(tubeFractionatedSample.getSample().getName(), design.fractionations
        .getColumn(FRACTIONATION_SAMPLE).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getSample().getName(), design.fractionations
        .getColumn(FRACTIONATION_SAMPLE).getValueProvider().apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATION_TYPE),
        design.fractionations.getColumn(FRACTIONATION_TYPE).getCaption());
    assertEquals(tubeFractionatedSample.getFractionation().getFractionationType().getLabel(locale),
        design.fractionations.getColumn(FRACTIONATION_TYPE).getValueProvider()
            .apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getFractionation().getFractionationType().getLabel(locale),
        design.fractionations.getColumn(FRACTIONATION_TYPE).getValueProvider()
            .apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATION_TYPE_VALUE),
        design.fractionations.getColumn(FRACTIONATION_TYPE_VALUE).getCaption());
    assertEquals(tubeFractionatedSample.getNumber(), design.fractionations
        .getColumn(FRACTIONATION_TYPE_VALUE).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getPiInterval(), design.fractionations
        .getColumn(FRACTIONATION_TYPE_VALUE).getValueProvider().apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATION_CONTAINER),
        design.fractionations.getColumn(FRACTIONATION_CONTAINER).getCaption());
    assertEquals(tubeFractionatedSample.getContainer().getFullName(), design.fractionations
        .getColumn(FRACTIONATION_CONTAINER).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getContainer().getFullName(), design.fractionations
        .getColumn(FRACTIONATION_CONTAINER).getValueProvider().apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATION_DESTINATION_CONTAINER),
        design.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getCaption());
    assertEquals(tubeFractionatedSample.getDestinationContainer().getFullName(),
        design.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getValueProvider()
            .apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getDestinationContainer().getFullName(),
        design.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getValueProvider()
            .apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATION_COMMENTS),
        design.fractionations.getColumn(FRACTIONATION_COMMENTS).getCaption());
    assertEquals(tubeFractionatedSample.getComments(), design.fractionations
        .getColumn(FRACTIONATION_COMMENTS).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getComments(), design.fractionations
        .getColumn(FRACTIONATION_COMMENTS).getValueProvider().apply(plateFractionatedSample));

    assertTrue(design.fractionationsPanel.isVisible());
    Collection<FractionationDetail> fractionatedSamples =
        dataProvider(design.fractionations).getItems();
    assertEquals(2, fractionatedSamples.size());
    assertTrue(fractionatedSamples.contains(tubeFractionatedSample));
    assertTrue(fractionatedSamples.contains(plateFractionatedSample));
  }

  @Test
  public void fractionationsGrid_Empty() {
    when(fractionationService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setValue(submission);

    assertFalse(design.fractionationsPanel.isVisible());
  }
}
