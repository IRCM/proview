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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.CONTAINER_NAME;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.CONTAINER_TYPE;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DATA_ANALYSES;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DATA_ANALYSES_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DIGESTIONS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DIGESTIONS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DIGESTION_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DIGESTION_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DIGESTION_PROTOCOL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTIONS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTIONS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTION_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTION_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTION_SOLVENT;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTION_SOLVENT_VOLUME;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.DILUTION_SOURCE_VOLUME;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.ENRICHMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.ENRICHMENTS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.ENRICHMENT_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.ENRICHMENT_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.ENRICHMENT_PROTOCOL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATIONS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATIONS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATION_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATION_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATION_DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATION_TYPE;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.FRACTIONATION_TYPE_VALUE;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MOLECULAR_WEIGHT;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_ACQUISITION_FILE;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_MASS_DETECTION_INSTRUMENT;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_NUMBER_OF_ACQUISITION;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_SAMPLE_LIST_NAME;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.MS_ANALYSES_SOURCE;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.NAME;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.NUMBER_PROTEINS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.QUANTITY;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.SAMPLE_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.STATUS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.SUPPORT;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.TRANSFERS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.TRANSFERS_PANEL;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.TRANSFER_COMMENTS;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.TRANSFER_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.TRANSFER_DESTINATION_CONTAINER;
import static ca.qc.ircm.proview.sample.web.SampleFormPresenter.VOLUME;
import static org.junit.Assert.assertEquals;
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
import ca.qc.ircm.proview.msanalysis.Acquisition;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrumentSource;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.Sample;
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
import com.vaadin.ui.Label;
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
public class SampleFormPresenterTest {
  private SampleFormPresenter presenter;
  @Mock
  private SampleForm view;
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
  private MessageResource resources = new MessageResource(SampleForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private SubmissionSample sample;
  private Submission submission;
  private Tube tube;
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
  private MsAnalysis tubeMsAnalysis;
  private Acquisition tubeAcquisition;
  private MsAnalysis plateMsAnalysis;
  private Acquisition plateAcquisition;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SampleFormPresenter(digestionService, enrichmentService, dilutionService,
        fractionationService, transferService, msAnalysisService);
    view.samplePanel = new Panel();
    view.name = new Label();
    view.support = new Label();
    view.status = new Label();
    view.quantity = new Label();
    view.volume = new Label();
    view.numberProteins = new Label();
    view.molecularWeight = new Label();
    view.containerPanel = new Panel();
    view.containerType = new Label();
    view.containerName = new Label();
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
    view.msAnalysesPanel = new Panel();
    view.msAnalyses = new Grid<>();
    view.dataAnalysesPanel = new Panel();
    view.dataAnalyses = new Grid<>();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(view.getGeneralResources()).thenReturn(generalResources);
    sample = new SubmissionSample();
    sample.setName("sample_name");
    sample.setSupport(SampleSupport.SOLUTION);
    sample.setStatus(SampleStatus.ANALYSED);
    sample.setQuantity("10.4 ug");
    sample.setVolume(10.3);
    sample.setNumberProtein(4);
    sample.setMolecularWeight(5.6);
    submission = new Submission();
    sample.setSubmission(submission);
    tube = new Tube();
    tube.setName("tube_name");
    sample.setOriginalContainer(tube);
    tubeDigestion = new Digestion();
    tubeDigestion.setProtocol(new DigestionProtocol());
    tubeDigestion.getProtocol().setName("digestion_protocol_1");
    tubeDigestedSample = new DigestedSample();
    tubeDigestedSample.setDigestion(tubeDigestion);
    tubeDigestedSample.setSample(sample);
    tubeDigestedSample.setComments("tube_digestion_comment_1");
    tubeDigestedSample.setContainer(new Tube());
    ((Tube) tubeDigestedSample.getContainer()).setName("tube_1");
    tubeDigestion.setTreatmentSamples(Arrays.asList(tubeDigestedSample));
    plateDigestion = new Digestion();
    plateDigestion.setProtocol(new DigestionProtocol());
    plateDigestion.getProtocol().setName("digestion_protocol_2");
    plateDigestedSample = new DigestedSample();
    plateDigestedSample.setDigestion(plateDigestion);
    plateDigestedSample.setSample(sample);
    plateDigestedSample.setComments("plate_digestion_comment_1");
    plateDigestedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateDigestedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateDigestedSample.getContainer()).getPlate().setName("plate_1");
    plateDigestion.setTreatmentSamples(Arrays.asList(plateDigestedSample));
    when(digestionService.all(any())).thenReturn(Arrays.asList(tubeDigestion, plateDigestion));
    tubeEnrichment = new Enrichment();
    tubeEnrichment.setProtocol(new EnrichmentProtocol());
    tubeEnrichment.getProtocol().setName("enrichment_protocol_1");
    tubeEnrichedSample = new EnrichedSample();
    tubeEnrichedSample.setEnrichment(tubeEnrichment);
    tubeEnrichedSample.setSample(sample);
    tubeEnrichedSample.setComments("tube_enrichment_comment_1");
    tubeEnrichedSample.setContainer(new Tube());
    ((Tube) tubeEnrichedSample.getContainer()).setName("tube_1");
    tubeEnrichment.setTreatmentSamples(Arrays.asList(tubeEnrichedSample));
    plateEnrichment = new Enrichment();
    plateEnrichment.setProtocol(new EnrichmentProtocol());
    plateEnrichment.getProtocol().setName("enrichment_protocol_2");
    plateEnrichedSample = new EnrichedSample();
    plateEnrichedSample.setEnrichment(plateEnrichment);
    plateEnrichedSample.setSample(sample);
    plateEnrichedSample.setComments("plate_enrichment_comment_1");
    plateEnrichedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateEnrichedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateEnrichedSample.getContainer()).getPlate().setName("plate_1");
    plateEnrichment.setTreatmentSamples(Arrays.asList(plateEnrichedSample));
    when(enrichmentService.all(any())).thenReturn(Arrays.asList(tubeEnrichment, plateEnrichment));
    tubeDilution = new Dilution();
    tubeDilutedSample = new DilutedSample();
    tubeDilutedSample.setDilution(tubeDilution);
    tubeDilutedSample.setSample(sample);
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
    plateDilutedSample.setSample(sample);
    plateDilutedSample.setComments("plate_dilution_comment_1");
    plateDilutedSample.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateDilutedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateDilutedSample.getContainer()).getPlate().setName("plate_1");
    plateDilution.setTreatmentSamples(Arrays.asList(plateDilutedSample));
    when(dilutionService.all(any())).thenReturn(Arrays.asList(tubeDilution, plateDilution));
    tubeFractionation = new Fractionation();
    tubeFractionation.setFractionationType(FractionationType.MUDPIT);
    tubeFractionatedSample = new FractionationDetail();
    tubeFractionatedSample.setFractionation(tubeFractionation);
    tubeFractionatedSample.setSample(sample);
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
    plateFractionatedSample.setSample(sample);
    plateFractionatedSample.setComments("plate_fractionation_comment_1");
    plateFractionatedSample.setContainer(new PlateSpot(1, 2));
    plateFractionatedSample.setDestinationContainer(new PlateSpot(2, 2));
    ((PlateSpot) plateFractionatedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateFractionatedSample.getContainer()).getPlate().setName("plate_1");
    ((PlateSpot) plateFractionatedSample.getDestinationContainer()).setPlate(new Plate());
    ((PlateSpot) plateFractionatedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateFractionation.setTreatmentSamples(Arrays.asList(plateFractionatedSample));
    when(fractionationService.all(any()))
        .thenReturn(Arrays.asList(tubeFractionation, plateFractionation));
    tubeTransfer = new Transfer();
    tubeTransferedSample = new SampleTransfer();
    tubeTransferedSample.setTransfer(tubeTransfer);
    tubeTransferedSample.setSample(sample);
    tubeTransferedSample.setComments("tube_transfer_comment_1");
    tubeTransferedSample.setContainer(new Tube());
    tubeTransferedSample.setDestinationContainer(new Tube());
    ((Tube) tubeTransferedSample.getContainer()).setName("tube_1");
    ((Tube) tubeTransferedSample.getDestinationContainer()).setName("destination_tube_1");
    tubeTransfer.setTreatmentSamples(Arrays.asList(tubeTransferedSample));
    plateTransfer = new Transfer();
    plateTransferedSample = new SampleTransfer();
    plateTransferedSample.setTransfer(plateTransfer);
    plateTransferedSample.setSample(sample);
    plateTransferedSample.setComments("plate_transfer_comment_2");
    plateTransferedSample.setContainer(new PlateSpot(1, 2));
    plateTransferedSample.setDestinationContainer(new PlateSpot(2, 2));
    ((PlateSpot) plateTransferedSample.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateTransferedSample.getContainer()).getPlate().setName("plate_1");
    ((PlateSpot) plateTransferedSample.getDestinationContainer()).setPlate(new Plate());
    ((PlateSpot) plateTransferedSample.getDestinationContainer()).getPlate().setName("plate_2");
    plateTransfer.setTreatmentSamples(Arrays.asList(plateTransferedSample));
    when(transferService.all(any())).thenReturn(Arrays.asList(tubeTransfer, plateTransfer));
    tubeMsAnalysis = new MsAnalysis();
    tubeMsAnalysis.setMassDetectionInstrument(MassDetectionInstrument.VELOS);
    tubeMsAnalysis.setSource(MassDetectionInstrumentSource.LDTD);
    tubeAcquisition = new Acquisition();
    tubeAcquisition.setMsAnalysis(tubeMsAnalysis);
    tubeAcquisition.setSample(sample);
    tubeAcquisition.setAcquisitionFile("acquisition_file_1");
    tubeAcquisition.setListIndex(1);
    tubeAcquisition.setNumberOfAcquisition(2);
    tubeAcquisition.setPosition(1);
    tubeAcquisition.setSampleListName("sample_list_1");
    tubeAcquisition.setComments("tube_msanalysis_comment_1");
    tubeAcquisition.setContainer(new Tube());
    ((Tube) tubeAcquisition.getContainer()).setName("tube_1");
    tubeMsAnalysis.setAcquisitions(Arrays.asList(tubeAcquisition));
    plateMsAnalysis = new MsAnalysis();
    plateMsAnalysis.setMassDetectionInstrument(MassDetectionInstrument.LTQ_ORBI_TRAP);
    plateMsAnalysis.setSource(MassDetectionInstrumentSource.ESI);
    plateAcquisition = new Acquisition();
    plateAcquisition.setMsAnalysis(plateMsAnalysis);
    plateAcquisition.setSample(sample);
    plateAcquisition.setAcquisitionFile("acquisition_file_2");
    plateAcquisition.setListIndex(2);
    plateAcquisition.setNumberOfAcquisition(2);
    plateAcquisition.setPosition(2);
    plateAcquisition.setSampleListName("sample_list_2");
    plateAcquisition.setComments("plate_msanalysis_comment_2");
    plateAcquisition.setContainer(new PlateSpot(1, 2));
    ((PlateSpot) plateAcquisition.getContainer()).setPlate(new Plate());
    ((PlateSpot) plateAcquisition.getContainer()).getPlate().setName("plate_1");
    plateMsAnalysis.setAcquisitions(Arrays.asList(plateAcquisition));
    when(msAnalysisService.all(any(Sample.class)))
        .thenReturn(Arrays.asList(tubeMsAnalysis, plateMsAnalysis));
  }

  @SuppressWarnings("unchecked")
  private <T> ListDataProvider<T> dataProvider(Grid<T> grid) {
    return (ListDataProvider<T>) grid.getDataProvider();
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setBean(sample);

    assertTrue(view.samplePanel.getStyleName().contains(SAMPLE_PANEL));
    assertTrue(view.name.getStyleName().contains(NAME));
    assertTrue(view.support.getStyleName().contains(SUPPORT));
    assertTrue(view.status.getStyleName().contains(STATUS));
    assertTrue(view.quantity.getStyleName().contains(QUANTITY));
    assertTrue(view.volume.getStyleName().contains(VOLUME));
    assertTrue(view.numberProteins.getStyleName().contains(NUMBER_PROTEINS));
    assertTrue(view.molecularWeight.getStyleName().contains(MOLECULAR_WEIGHT));
    assertTrue(view.containerPanel.getStyleName().contains(CONTAINER));
    assertTrue(view.containerType.getStyleName().contains(CONTAINER_TYPE));
    assertTrue(view.containerName.getStyleName().contains(CONTAINER_NAME));
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
    assertTrue(view.msAnalysesPanel.getStyleName().contains(MS_ANALYSES_PANEL));
    assertTrue(view.msAnalyses.getStyleName().contains(MS_ANALYSES));
    assertTrue(view.dataAnalysesPanel.getStyleName().contains(DATA_ANALYSES_PANEL));
    assertTrue(view.dataAnalyses.getStyleName().contains(DATA_ANALYSES));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setBean(sample);

    assertEquals(resources.message(SAMPLE_PANEL), view.samplePanel.getCaption());
    assertEquals(resources.message(NAME), view.name.getCaption());
    assertEquals(sample.getName(), view.name.getValue());
    assertEquals(resources.message(SUPPORT), view.support.getCaption());
    assertEquals(sample.getSupport().getLabel(locale), view.support.getValue());
    assertEquals(resources.message(STATUS), view.status.getCaption());
    assertEquals(sample.getStatus().getLabel(locale), view.status.getValue());
    assertEquals(resources.message(QUANTITY), view.quantity.getCaption());
    assertEquals(sample.getQuantity(), view.quantity.getValue());
    assertEquals(resources.message(VOLUME), view.volume.getCaption());
    assertEquals(String.valueOf(sample.getVolume()), view.volume.getValue());
    assertEquals(resources.message(NUMBER_PROTEINS), view.numberProteins.getCaption());
    assertEquals(String.valueOf(sample.getNumberProtein()), view.numberProteins.getValue());
    assertEquals(resources.message(MOLECULAR_WEIGHT), view.molecularWeight.getCaption());
    assertEquals(String.valueOf(sample.getMolecularWeight()), view.molecularWeight.getValue());
    assertEquals(resources.message(CONTAINER), view.containerPanel.getCaption());
    assertEquals(resources.message(CONTAINER + "." + CONTAINER_TYPE),
        view.containerType.getCaption());
    assertEquals(tube.getType().getLabel(locale), view.containerType.getValue());
    assertEquals(resources.message(CONTAINER + "." + CONTAINER_NAME),
        view.containerName.getCaption());
    assertEquals(tube.getName(), view.containerName.getValue());
    assertEquals(resources.message(DIGESTIONS_PANEL), view.digestionsPanel.getCaption());
    assertEquals(resources.message(ENRICHMENTS_PANEL), view.enrichmentsPanel.getCaption());
    assertEquals(resources.message(DILUTIONS_PANEL), view.dilutionsPanel.getCaption());
    assertEquals(resources.message(FRACTIONATIONS_PANEL), view.fractionationsPanel.getCaption());
    assertEquals(resources.message(TRANSFERS_PANEL), view.transfersPanel.getCaption());
    assertEquals(resources.message(MS_ANALYSES_PANEL), view.msAnalysesPanel.getCaption());
    assertEquals(resources.message(DATA_ANALYSES_PANEL), view.dataAnalysesPanel.getCaption());
  }

  @Test
  public void digestionsGrid() {
    presenter.init(view);
    presenter.setBean(sample);

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

    Collection<DigestedSample> digestedSamples = dataProvider(view.digestions).getItems();
    assertEquals(2, digestedSamples.size());
    assertTrue(digestedSamples.contains(tubeDigestedSample));
    assertTrue(digestedSamples.contains(plateDigestedSample));
  }

  @Test
  public void enrichmentsGrid() {
    presenter.init(view);
    presenter.setBean(sample);

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

    Collection<EnrichedSample> enrichedSamples = dataProvider(view.enrichments).getItems();
    assertEquals(2, enrichedSamples.size());
    assertTrue(enrichedSamples.contains(tubeEnrichedSample));
    assertTrue(enrichedSamples.contains(plateEnrichedSample));
  }

  @Test
  public void dilutionsGrid() {
    presenter.init(view);
    presenter.setBean(sample);

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

    Collection<DilutedSample> dilutedSamples = dataProvider(view.dilutions).getItems();
    assertEquals(2, dilutedSamples.size());
    assertTrue(dilutedSamples.contains(tubeDilutedSample));
    assertTrue(dilutedSamples.contains(plateDilutedSample));
  }

  @Test
  public void fractionationsGrid() {
    presenter.init(view);
    presenter.setBean(sample);

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

    Collection<FractionationDetail> fractionatedSamples =
        dataProvider(view.fractionations).getItems();
    assertEquals(2, fractionatedSamples.size());
    assertTrue(fractionatedSamples.contains(tubeFractionatedSample));
    assertTrue(fractionatedSamples.contains(plateFractionatedSample));
  }

  @Test
  public void transfersGrid() {
    presenter.init(view);
    presenter.setBean(sample);

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

    Collection<SampleTransfer> transferedSamples = dataProvider(view.transfers).getItems();
    assertEquals(2, transferedSamples.size());
    assertTrue(transferedSamples.contains(tubeTransferedSample));
    assertTrue(transferedSamples.contains(plateTransferedSample));
  }

  @Test
  public void msAnalysisGrid() {
    presenter.init(view);
    presenter.setBean(sample);

    assertEquals(7, view.msAnalyses.getColumns().size());
    assertEquals(MS_ANALYSES_MASS_DETECTION_INSTRUMENT,
        view.msAnalyses.getColumns().get(0).getId());
    assertEquals(MS_ANALYSES_SOURCE, view.msAnalyses.getColumns().get(1).getId());
    assertEquals(MS_ANALYSES_NUMBER_OF_ACQUISITION, view.msAnalyses.getColumns().get(2).getId());
    assertEquals(MS_ANALYSES_ACQUISITION_FILE, view.msAnalyses.getColumns().get(3).getId());
    assertEquals(MS_ANALYSES_SAMPLE_LIST_NAME, view.msAnalyses.getColumns().get(4).getId());
    assertEquals(MS_ANALYSES_CONTAINER, view.msAnalyses.getColumns().get(5).getId());
    assertEquals(MS_ANALYSES_COMMENTS, view.msAnalyses.getColumns().get(6).getId());
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_MASS_DETECTION_INSTRUMENT),
        view.msAnalyses.getColumn(MS_ANALYSES_MASS_DETECTION_INSTRUMENT).getCaption());
    assertEquals(tubeAcquisition.getMsAnalysis().getMassDetectionInstrument().getLabel(locale),
        view.msAnalyses.getColumn(MS_ANALYSES_MASS_DETECTION_INSTRUMENT).getValueProvider()
            .apply(tubeAcquisition));
    assertEquals(plateAcquisition.getMsAnalysis().getMassDetectionInstrument().getLabel(locale),
        view.msAnalyses.getColumn(MS_ANALYSES_MASS_DETECTION_INSTRUMENT).getValueProvider()
            .apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_SOURCE),
        view.msAnalyses.getColumn(MS_ANALYSES_SOURCE).getCaption());
    assertEquals(tubeAcquisition.getMsAnalysis().getSource().getLabel(locale),
        view.msAnalyses.getColumn(MS_ANALYSES_SOURCE).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getMsAnalysis().getSource().getLabel(locale),
        view.msAnalyses.getColumn(MS_ANALYSES_SOURCE).getValueProvider().apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_NUMBER_OF_ACQUISITION),
        view.msAnalyses.getColumn(MS_ANALYSES_NUMBER_OF_ACQUISITION).getCaption());
    assertEquals(tubeAcquisition.getNumberOfAcquisition(), view.msAnalyses
        .getColumn(MS_ANALYSES_NUMBER_OF_ACQUISITION).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getNumberOfAcquisition(), view.msAnalyses
        .getColumn(MS_ANALYSES_NUMBER_OF_ACQUISITION).getValueProvider().apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_ACQUISITION_FILE),
        view.msAnalyses.getColumn(MS_ANALYSES_ACQUISITION_FILE).getCaption());
    assertEquals(tubeAcquisition.getAcquisitionFile(), view.msAnalyses
        .getColumn(MS_ANALYSES_ACQUISITION_FILE).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getAcquisitionFile(), view.msAnalyses
        .getColumn(MS_ANALYSES_ACQUISITION_FILE).getValueProvider().apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_SAMPLE_LIST_NAME),
        view.msAnalyses.getColumn(MS_ANALYSES_SAMPLE_LIST_NAME).getCaption());
    assertEquals(tubeAcquisition.getSampleListName(), view.msAnalyses
        .getColumn(MS_ANALYSES_SAMPLE_LIST_NAME).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getSampleListName(), view.msAnalyses
        .getColumn(MS_ANALYSES_SAMPLE_LIST_NAME).getValueProvider().apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_CONTAINER),
        view.msAnalyses.getColumn(MS_ANALYSES_CONTAINER).getCaption());
    assertEquals(tubeAcquisition.getContainer().getFullName(),
        view.msAnalyses.getColumn(MS_ANALYSES_CONTAINER).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getContainer().getFullName(), view.msAnalyses
        .getColumn(MS_ANALYSES_CONTAINER).getValueProvider().apply(plateAcquisition));
    assertEquals(resources.message(MS_ANALYSES + "." + MS_ANALYSES_COMMENTS),
        view.msAnalyses.getColumn(MS_ANALYSES_COMMENTS).getCaption());
    assertEquals(tubeAcquisition.getComments(),
        view.msAnalyses.getColumn(MS_ANALYSES_COMMENTS).getValueProvider().apply(tubeAcquisition));
    assertEquals(plateAcquisition.getComments(),
        view.msAnalyses.getColumn(MS_ANALYSES_COMMENTS).getValueProvider().apply(plateAcquisition));

    Collection<Acquisition> acquisitions = dataProvider(view.msAnalyses).getItems();
    assertEquals(2, acquisitions.size());
    assertTrue(acquisitions.contains(tubeAcquisition));
    assertTrue(acquisitions.contains(plateAcquisition));
  }
}
