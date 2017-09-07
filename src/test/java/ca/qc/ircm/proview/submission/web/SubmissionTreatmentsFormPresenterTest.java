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
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOLVENT;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOLVENT_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.DILUTION_SOURCE_VOLUME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.ENRICHMENT_PROTOCOL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATIONS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.FRACTIONATION_TYPE_VALUE;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFERS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_COMMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionTreatmentsFormPresenter.TRANSFER_DESTINATION_CONTAINER;
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
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
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
  private DigestionService digestionService;
  @Mock
  private EnrichmentService enrichmentService;
  @Mock
  private DilutionService dilutionService;
  @Mock
  private FractionationService fractionationService;
  @Mock
  private TransferService transferService;
  @Mock
  private MsAnalysisService msAnalysisService;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionTreatmentsForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private SubmissionSample sample1;
  private SubmissionSample sample2;
  private Submission submission;
  private Tube tube1;
  private Tube tube2;
  private Digestion tubeDigestion;
  private DigestedSample tubeDigestedSample;
  private Digestion plateDigestion;
  private DigestedSample plateDigestedSample;
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
  private SampleTransfer tubeTransferedSample;
  private Transfer plateTransfer;
  private SampleTransfer plateTransferedSample;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionTreatmentsFormPresenter(digestionService, enrichmentService,
        dilutionService, fractionationService, transferService);
    view.samplesPanel = new Panel();
    view.samples = new Grid<>();
    view.digestionsPanel = new Panel();
    view.digestions = new Grid<>();
    view.enrichmentsPanel = new Panel();
    view.enrichments = new Grid<>();
    view.dilutionsPanel = new Panel();
    view.dilutions = new Grid<>();
    view.fractionationsPanel = new Panel();
    view.fractionations = new Grid<>();
    view.transfersPanel = new Panel();
    view.transfers = new Grid<>();
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
    plateDigestedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateDigestedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateDigestedSample.getContainer()).getPlate().setName("plate_1");
    plateDigestion.setTreatmentSamples(Arrays.asList(plateDigestedSample));
    when(digestionService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeDigestion, plateDigestion));
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
    plateEnrichedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateEnrichedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateEnrichedSample.getContainer()).getPlate().setName("plate_1");
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
    plateDilutedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateDilutedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateDilutedSample.getContainer()).getPlate().setName("plate_1");
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
    plateFractionatedSample.setContainer(new PlateSpot(1, 2));
    plateFractionatedSample.setDestinationContainer(new PlateSpot(2, 2));
    ((PlateSpot) plateFractionatedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateFractionatedSample.getContainer()).getPlate().setName("plate_1");
    ((PlateSpot) plateFractionatedSample.getDestinationContainer()).setPlate(new Plate());
    ((PlateSpot) plateFractionatedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateFractionation.setTreatmentSamples(Arrays.asList(plateFractionatedSample));
    when(fractionationService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeFractionation, plateFractionation));
    tubeTransfer = new Transfer();
    tubeTransferedSample = new SampleTransfer();
    tubeTransferedSample.setTransfer(tubeTransfer);
    tubeTransferedSample.setSample(sample1);
    tubeTransferedSample.setComments("tube_transfer_comment_1");
    tubeTransferedSample.setContainer(new Tube());
    tubeTransferedSample.setDestinationContainer(new Tube());
    ((Tube) tubeTransferedSample.getContainer()).setName("tube_1");
    ((Tube) tubeTransferedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeTransfer.setTreatmentSamples(Arrays.asList(tubeTransferedSample));
    plateTransfer = new Transfer();
    plateTransferedSample = new SampleTransfer();
    plateTransferedSample.setTransfer(plateTransfer);
    plateTransferedSample.setSample(sample2);
    plateTransferedSample.setComments("plate_transfer_comment_2");
    plateTransferedSample.setContainer(new PlateSpot(1, 2));
    plateTransferedSample.setDestinationContainer(new PlateSpot(2, 2));
    ((PlateSpot) plateTransferedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateTransferedSample.getContainer()).getPlate().setName("plate_1");
    ((PlateSpot) plateTransferedSample.getDestinationContainer()).setPlate(new Plate());
    ((PlateSpot) plateTransferedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateTransfer.setTreatmentSamples(Arrays.asList(plateTransferedSample));
    when(transferService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(tubeTransfer, plateTransfer));
  }

  @SuppressWarnings("unchecked")
  private <T> ListDataProvider<T> dataProvider(Grid<T> grid) {
    return (ListDataProvider<T>) grid.getDataProvider();
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(view.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(view.samples.getStyleName().contains(SAMPLES));
    assertTrue(view.digestionsPanel.getStyleName().contains(DIGESTIONS_PANEL));
    assertTrue(view.digestions.getStyleName().contains(DIGESTIONS));
    assertTrue(view.enrichmentsPanel.getStyleName().contains(ENRICHMENTS_PANEL));
    assertTrue(view.enrichments.getStyleName().contains(ENRICHMENTS));
    assertTrue(view.dilutionsPanel.getStyleName().contains(DILUTIONS_PANEL));
    assertTrue(view.dilutions.getStyleName().contains(DILUTIONS));
    assertTrue(view.fractionationsPanel.getStyleName().contains(FRACTIONATIONS_PANEL));
    assertTrue(view.fractionations.getStyleName().contains(FRACTIONATIONS));
    assertTrue(view.transfersPanel.getStyleName().contains(TRANSFERS_PANEL));
    assertTrue(view.transfers.getStyleName().contains(TRANSFERS));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    assertEquals(resources.message(DIGESTIONS_PANEL), view.digestionsPanel.getCaption());
    assertEquals(resources.message(ENRICHMENTS_PANEL), view.enrichmentsPanel.getCaption());
    assertEquals(resources.message(DILUTIONS_PANEL), view.dilutionsPanel.getCaption());
    assertEquals(resources.message(FRACTIONATIONS_PANEL), view.fractionationsPanel.getCaption());
    assertEquals(resources.message(TRANSFERS_PANEL), view.transfersPanel.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(1, view.samples.getColumns().size());
    assertEquals(SAMPLES_NAME, view.samples.getColumns().get(0).getId());
    assertEquals(resources.message(SAMPLES + "." + SAMPLES_NAME),
        view.samples.getColumn(SAMPLES_NAME).getCaption());
    assertEquals(sample1.getName(),
        view.samples.getColumn(SAMPLES_NAME).getValueProvider().apply(sample1));
    assertEquals(sample2.getName(),
        view.samples.getColumn(SAMPLES_NAME).getValueProvider().apply(sample2));

    Collection<SubmissionSample> samples = dataProvider(view.samples).getItems();
    assertEquals(2, samples.size());
    assertTrue(samples.contains(sample1));
    assertTrue(samples.contains(sample2));
  }

  @Test
  public void digestionsGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(3, view.digestions.getColumns().size());
    assertEquals(DIGESTION_PROTOCOL, view.digestions.getColumns().get(0).getId());
    assertEquals(DIGESTION_CONTAINER, view.digestions.getColumns().get(1).getId());
    assertEquals(DIGESTION_COMMENTS, view.digestions.getColumns().get(2).getId());
    assertEquals(resources.message(DIGESTIONS + "." + DIGESTION_PROTOCOL),
        view.digestions.getColumn(DIGESTION_PROTOCOL).getCaption());
    assertEquals(tubeDigestion.getProtocol().getName(),
        view.digestions.getColumn(DIGESTION_PROTOCOL).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestion.getProtocol().getName(), view.digestions
        .getColumn(DIGESTION_PROTOCOL).getValueProvider().apply(plateDigestedSample));
    assertEquals(resources.message(DIGESTIONS + "." + DIGESTION_CONTAINER),
        view.digestions.getColumn(DIGESTION_CONTAINER).getCaption());
    assertEquals(tubeDigestedSample.getContainer().getFullName(), view.digestions
        .getColumn(DIGESTION_CONTAINER).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestedSample.getContainer().getFullName(), view.digestions
        .getColumn(DIGESTION_CONTAINER).getValueProvider().apply(plateDigestedSample));
    assertEquals(resources.message(DIGESTIONS + "." + DIGESTION_COMMENTS),
        view.digestions.getColumn(DIGESTION_COMMENTS).getCaption());
    assertEquals(tubeDigestedSample.getComments(),
        view.digestions.getColumn(DIGESTION_COMMENTS).getValueProvider().apply(tubeDigestedSample));
    assertEquals(plateDigestedSample.getComments(), view.digestions.getColumn(DIGESTION_COMMENTS)
        .getValueProvider().apply(plateDigestedSample));

    assertTrue(view.digestionsPanel.isVisible());
    Collection<DigestedSample> digestedSamples = dataProvider(view.digestions).getItems();
    assertEquals(2, digestedSamples.size());
    assertTrue(digestedSamples.contains(tubeDigestedSample));
    assertTrue(digestedSamples.contains(plateDigestedSample));
  }

  @Test
  public void digestionsGrid_Empty() {
    when(digestionService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.digestionsPanel.isVisible());
  }

  @Test
  public void enrichmentsGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(3, view.enrichments.getColumns().size());
    assertEquals(ENRICHMENT_PROTOCOL, view.enrichments.getColumns().get(0).getId());
    assertEquals(ENRICHMENT_CONTAINER, view.enrichments.getColumns().get(1).getId());
    assertEquals(ENRICHMENT_COMMENTS, view.enrichments.getColumns().get(2).getId());
    assertEquals(resources.message(ENRICHMENTS + "." + ENRICHMENT_PROTOCOL),
        view.enrichments.getColumn(ENRICHMENT_PROTOCOL).getCaption());
    assertEquals(tubeEnrichment.getProtocol().getName(), view.enrichments
        .getColumn(ENRICHMENT_PROTOCOL).getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichment.getProtocol().getName(), view.enrichments
        .getColumn(ENRICHMENT_PROTOCOL).getValueProvider().apply(plateEnrichedSample));
    assertEquals(resources.message(ENRICHMENTS + "." + ENRICHMENT_CONTAINER),
        view.enrichments.getColumn(ENRICHMENT_CONTAINER).getCaption());
    assertEquals(tubeEnrichedSample.getContainer().getFullName(), view.enrichments
        .getColumn(ENRICHMENT_CONTAINER).getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichedSample.getContainer().getFullName(), view.enrichments
        .getColumn(ENRICHMENT_CONTAINER).getValueProvider().apply(plateEnrichedSample));
    assertEquals(resources.message(ENRICHMENTS + "." + ENRICHMENT_COMMENTS),
        view.enrichments.getColumn(ENRICHMENT_COMMENTS).getCaption());
    assertEquals(tubeEnrichedSample.getComments(), view.enrichments.getColumn(ENRICHMENT_COMMENTS)
        .getValueProvider().apply(tubeEnrichedSample));
    assertEquals(plateEnrichedSample.getComments(), view.enrichments.getColumn(ENRICHMENT_COMMENTS)
        .getValueProvider().apply(plateEnrichedSample));

    assertTrue(view.enrichmentsPanel.isVisible());
    Collection<EnrichedSample> enrichedSamples = dataProvider(view.enrichments).getItems();
    assertEquals(2, enrichedSamples.size());
    assertTrue(enrichedSamples.contains(tubeEnrichedSample));
    assertTrue(enrichedSamples.contains(plateEnrichedSample));
  }

  @Test
  public void enrichmentsGrid_Empty() {
    when(enrichmentService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.enrichmentsPanel.isVisible());
  }

  @Test
  public void dilutionsGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(5, view.dilutions.getColumns().size());
    assertEquals(DILUTION_SOURCE_VOLUME, view.dilutions.getColumns().get(0).getId());
    assertEquals(DILUTION_SOLVENT, view.dilutions.getColumns().get(1).getId());
    assertEquals(DILUTION_SOLVENT_VOLUME, view.dilutions.getColumns().get(2).getId());
    assertEquals(DILUTION_CONTAINER, view.dilutions.getColumns().get(3).getId());
    assertEquals(DILUTION_COMMENTS, view.dilutions.getColumns().get(4).getId());
    assertEquals(resources.message(DILUTIONS + "." + DILUTION_SOURCE_VOLUME),
        view.dilutions.getColumn(DILUTION_SOURCE_VOLUME).getCaption());
    assertEquals(tubeDilutedSample.getSourceVolume(), view.dilutions
        .getColumn(DILUTION_SOURCE_VOLUME).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSourceVolume(), view.dilutions
        .getColumn(DILUTION_SOURCE_VOLUME).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTIONS + "." + DILUTION_SOLVENT),
        view.dilutions.getColumn(DILUTION_SOLVENT).getCaption());
    assertEquals(tubeDilutedSample.getSolvent(),
        view.dilutions.getColumn(DILUTION_SOLVENT).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSolvent(),
        view.dilutions.getColumn(DILUTION_SOLVENT).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTIONS + "." + DILUTION_SOLVENT_VOLUME),
        view.dilutions.getColumn(DILUTION_SOLVENT_VOLUME).getCaption());
    assertEquals(tubeDilutedSample.getSolventVolume(), view.dilutions
        .getColumn(DILUTION_SOLVENT_VOLUME).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getSolventVolume(), view.dilutions
        .getColumn(DILUTION_SOLVENT_VOLUME).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTIONS + "." + DILUTION_CONTAINER),
        view.dilutions.getColumn(DILUTION_CONTAINER).getCaption());
    assertEquals(tubeDilutedSample.getContainer().getFullName(),
        view.dilutions.getColumn(DILUTION_CONTAINER).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getContainer().getFullName(),
        view.dilutions.getColumn(DILUTION_CONTAINER).getValueProvider().apply(plateDilutedSample));
    assertEquals(resources.message(DILUTIONS + "." + DILUTION_COMMENTS),
        view.dilutions.getColumn(DILUTION_COMMENTS).getCaption());
    assertEquals(tubeDilutedSample.getComments(),
        view.dilutions.getColumn(DILUTION_COMMENTS).getValueProvider().apply(tubeDilutedSample));
    assertEquals(plateDilutedSample.getComments(),
        view.dilutions.getColumn(DILUTION_COMMENTS).getValueProvider().apply(plateDilutedSample));

    assertTrue(view.dilutionsPanel.isVisible());
    Collection<DilutedSample> dilutedSamples = dataProvider(view.dilutions).getItems();
    assertEquals(2, dilutedSamples.size());
    assertTrue(dilutedSamples.contains(tubeDilutedSample));
    assertTrue(dilutedSamples.contains(plateDilutedSample));
  }

  @Test
  public void dilutionsGrid_Empty() {
    when(dilutionService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.dilutionsPanel.isVisible());
  }

  @Test
  public void fractionationsGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(5, view.fractionations.getColumns().size());
    assertEquals(FRACTIONATION_TYPE, view.fractionations.getColumns().get(0).getId());
    assertEquals(FRACTIONATION_TYPE_VALUE, view.fractionations.getColumns().get(1).getId());
    assertEquals(FRACTIONATION_CONTAINER, view.fractionations.getColumns().get(2).getId());
    assertEquals(FRACTIONATION_DESTINATION_CONTAINER,
        view.fractionations.getColumns().get(3).getId());
    assertEquals(FRACTIONATION_COMMENTS, view.fractionations.getColumns().get(4).getId());
    assertEquals(resources.message(FRACTIONATIONS + "." + FRACTIONATION_TYPE),
        view.fractionations.getColumn(FRACTIONATION_TYPE).getCaption());
    assertEquals(tubeFractionatedSample.getFractionation().getFractionationType().getLabel(locale),
        view.fractionations.getColumn(FRACTIONATION_TYPE).getValueProvider()
            .apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getFractionation().getFractionationType().getLabel(locale),
        view.fractionations.getColumn(FRACTIONATION_TYPE).getValueProvider()
            .apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATIONS + "." + FRACTIONATION_TYPE_VALUE),
        view.fractionations.getColumn(FRACTIONATION_TYPE_VALUE).getCaption());
    assertEquals(tubeFractionatedSample.getNumber(), view.fractionations
        .getColumn(FRACTIONATION_TYPE_VALUE).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getPiInterval(), view.fractionations
        .getColumn(FRACTIONATION_TYPE_VALUE).getValueProvider().apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATIONS + "." + FRACTIONATION_CONTAINER),
        view.fractionations.getColumn(FRACTIONATION_CONTAINER).getCaption());
    assertEquals(tubeFractionatedSample.getContainer().getFullName(), view.fractionations
        .getColumn(FRACTIONATION_CONTAINER).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getContainer().getFullName(), view.fractionations
        .getColumn(FRACTIONATION_CONTAINER).getValueProvider().apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATIONS + "." + FRACTIONATION_DESTINATION_CONTAINER),
        view.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getCaption());
    assertEquals(tubeFractionatedSample.getDestinationContainer().getFullName(),
        view.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getValueProvider()
            .apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getDestinationContainer().getFullName(),
        view.fractionations.getColumn(FRACTIONATION_DESTINATION_CONTAINER).getValueProvider()
            .apply(plateFractionatedSample));
    assertEquals(resources.message(FRACTIONATIONS + "." + FRACTIONATION_COMMENTS),
        view.fractionations.getColumn(FRACTIONATION_COMMENTS).getCaption());
    assertEquals(tubeFractionatedSample.getComments(), view.fractionations
        .getColumn(FRACTIONATION_COMMENTS).getValueProvider().apply(tubeFractionatedSample));
    assertEquals(plateFractionatedSample.getComments(), view.fractionations
        .getColumn(FRACTIONATION_COMMENTS).getValueProvider().apply(plateFractionatedSample));

    assertTrue(view.fractionationsPanel.isVisible());
    Collection<FractionationDetail> fractionatedSamples =
        dataProvider(view.fractionations).getItems();
    assertEquals(2, fractionatedSamples.size());
    assertTrue(fractionatedSamples.contains(tubeFractionatedSample));
    assertTrue(fractionatedSamples.contains(plateFractionatedSample));
  }

  @Test
  public void fractionationsGrid_Empty() {
    when(fractionationService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.fractionationsPanel.isVisible());
  }

  @Test
  public void transfersGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(3, view.transfers.getColumns().size());
    assertEquals(TRANSFER_CONTAINER, view.transfers.getColumns().get(0).getId());
    assertEquals(TRANSFER_DESTINATION_CONTAINER, view.transfers.getColumns().get(1).getId());
    assertEquals(TRANSFER_COMMENTS, view.transfers.getColumns().get(2).getId());
    assertEquals(resources.message(TRANSFERS + "." + TRANSFER_CONTAINER),
        view.transfers.getColumn(TRANSFER_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getContainer().getFullName(), view.transfers
        .getColumn(TRANSFER_CONTAINER).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getContainer().getFullName(), view.transfers
        .getColumn(TRANSFER_CONTAINER).getValueProvider().apply(plateTransferedSample));
    assertEquals(resources.message(TRANSFERS + "." + TRANSFER_DESTINATION_CONTAINER),
        view.transfers.getColumn(TRANSFER_DESTINATION_CONTAINER).getCaption());
    assertEquals(tubeTransferedSample.getDestinationContainer().getFullName(), view.transfers
        .getColumn(TRANSFER_DESTINATION_CONTAINER).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getDestinationContainer().getFullName(), view.transfers
        .getColumn(TRANSFER_DESTINATION_CONTAINER).getValueProvider().apply(plateTransferedSample));
    assertEquals(resources.message(TRANSFERS + "." + TRANSFER_COMMENTS),
        view.transfers.getColumn(TRANSFER_COMMENTS).getCaption());
    assertEquals(tubeTransferedSample.getComments(),
        view.transfers.getColumn(TRANSFER_COMMENTS).getValueProvider().apply(tubeTransferedSample));
    assertEquals(plateTransferedSample.getComments(), view.transfers.getColumn(TRANSFER_COMMENTS)
        .getValueProvider().apply(plateTransferedSample));

    assertTrue(view.transfersPanel.isVisible());
    Collection<SampleTransfer> transferedSamples = dataProvider(view.transfers).getItems();
    assertEquals(2, transferedSamples.size());
    assertTrue(transferedSamples.contains(tubeTransferedSample));
    assertTrue(transferedSamples.contains(plateTransferedSample));
  }

  @Test
  public void transfersGrid_Empty() {
    when(transferService.all(any(Submission.class))).thenReturn(Arrays.asList());
    presenter.init(view);
    presenter.setBean(submission);

    assertFalse(view.transfersPanel.isVisible());
  }
}
