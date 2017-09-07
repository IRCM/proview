package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.digestion.QDigestedSample.digestedSample;
import static ca.qc.ircm.proview.digestion.QDigestion.digestion;
import static ca.qc.ircm.proview.dilution.QDilutedSample.dilutedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichedSample.enrichedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichment.enrichment;
import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.fractionation.QFractionationDetail.fractionationDetail;
import static ca.qc.ircm.proview.msanalysis.QAcquisition.acquisition;
import static ca.qc.ircm.proview.msanalysis.QMsAnalysis.msAnalysis;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;

import ca.qc.ircm.proview.digestion.DigestionService;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToIntegerConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Sample form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SampleFormPresenter {
  public static final String SAMPLE_PANEL = "samplePanel";
  public static final String NAME = submissionSample.name.getMetadata().getName();
  public static final String SUPPORT = submissionSample.support.getMetadata().getName();
  public static final String STATUS = submissionSample.status.getMetadata().getName();
  public static final String QUANTITY = submissionSample.quantity.getMetadata().getName();
  public static final String VOLUME = submissionSample.volume.getMetadata().getName();
  public static final String NUMBER_PROTEINS =
      submissionSample.numberProtein.getMetadata().getName();
  public static final String MOLECULAR_WEIGHT =
      submissionSample.molecularWeight.getMetadata().getName();
  public static final String CONTAINER = "container";
  public static final String CONTAINER_TYPE = "type";
  public static final String CONTAINER_NAME = "name";
  public static final String DIGESTIONS_PANEL = "digestionsPanel";
  public static final String DIGESTIONS = "digestions";
  public static final String DIGESTION_PROTOCOL = digestion.protocol.getMetadata().getName();
  public static final String DIGESTION_CONTAINER = digestedSample.container.getMetadata().getName();
  public static final String DIGESTION_COMMENTS = digestedSample.comments.getMetadata().getName();
  public static final String ENRICHMENTS_PANEL = "enrichmentsPanel";
  public static final String ENRICHMENTS = "enrichments";
  public static final String ENRICHMENT_PROTOCOL = enrichment.protocol.getMetadata().getName();
  public static final String ENRICHMENT_CONTAINER =
      enrichedSample.container.getMetadata().getName();
  public static final String ENRICHMENT_COMMENTS = enrichedSample.comments.getMetadata().getName();
  public static final String DILUTIONS_PANEL = "dilutionsPanel";
  public static final String DILUTIONS = "dilutions";
  public static final String DILUTION_SOURCE_VOLUME =
      dilutedSample.sourceVolume.getMetadata().getName();
  public static final String DILUTION_SOLVENT = dilutedSample.solvent.getMetadata().getName();
  public static final String DILUTION_SOLVENT_VOLUME =
      dilutedSample.solventVolume.getMetadata().getName();
  public static final String DILUTION_CONTAINER = dilutedSample.container.getMetadata().getName();
  public static final String DILUTION_COMMENTS = dilutedSample.comments.getMetadata().getName();
  public static final String FRACTIONATIONS_PANEL = "fractionationsPanel";
  public static final String FRACTIONATIONS = "fractionations";
  public static final String FRACTIONATION_TYPE =
      fractionation.fractionationType.getMetadata().getName();
  public static final String FRACTIONATION_TYPE_VALUE = "typeValue";
  public static final String FRACTIONATION_CONTAINER =
      fractionationDetail.container.getMetadata().getName();
  public static final String FRACTIONATION_DESTINATION_CONTAINER =
      fractionationDetail.destinationContainer.getMetadata().getName();
  public static final String FRACTIONATION_COMMENTS =
      fractionationDetail.comments.getMetadata().getName();
  public static final String TRANSFERS_PANEL = "transfersPanel";
  public static final String TRANSFERS = "transfers";
  public static final String TRANSFER_CONTAINER = sampleTransfer.container.getMetadata().getName();
  public static final String TRANSFER_DESTINATION_CONTAINER =
      sampleTransfer.destinationContainer.getMetadata().getName();
  public static final String TRANSFER_COMMENTS = sampleTransfer.comments.getMetadata().getName();
  public static final String MS_ANALYSES_PANEL = "msAnalysesPanel";
  public static final String MS_ANALYSES = "msAnalyses";
  public static final String MS_ANALYSES_MASS_DETECTION_INSTRUMENT =
      msAnalysis.massDetectionInstrument.getMetadata().getName();
  public static final String MS_ANALYSES_SOURCE = msAnalysis.source.getMetadata().getName();
  public static final String MS_ANALYSES_NUMBER_OF_ACQUISITION =
      acquisition.numberOfAcquisition.getMetadata().getName();
  public static final String MS_ANALYSES_ACQUISITION_FILE =
      acquisition.acquisitionFile.getMetadata().getName();
  public static final String MS_ANALYSES_SAMPLE_LIST_NAME =
      acquisition.sampleListName.getMetadata().getName();
  public static final String MS_ANALYSES_CONTAINER = acquisition.container.getMetadata().getName();
  public static final String MS_ANALYSES_COMMENTS = acquisition.comments.getMetadata().getName();
  public static final String DATA_ANALYSES_PANEL = "dataAnalysesPanel";
  public static final String DATA_ANALYSES = "dataAnalyses";
  private SampleForm view;
  private SubmissionSample sample;
  @Inject
  private DigestionService digestionService;
  @Inject
  private EnrichmentService enrichmentService;
  @Inject
  private DilutionService dilutionService;
  @Inject
  private FractionationService fractionationService;
  @Inject
  private TransferService transferService;
  @Inject
  private MsAnalysisService msAnalysisService;

  protected SampleFormPresenter() {
  }

  protected SampleFormPresenter(DigestionService digestionService,
      EnrichmentService enrichmentService, DilutionService dilutionService,
      FractionationService fractionationService, TransferService transferService,
      MsAnalysisService msAnalysisService) {
    this.digestionService = digestionService;
    this.enrichmentService = enrichmentService;
    this.dilutionService = dilutionService;
    this.fractionationService = fractionationService;
    this.transferService = transferService;
    this.msAnalysisService = msAnalysisService;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SampleForm view) {
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.samplePanel.addStyleName(SAMPLE_PANEL);
    view.samplePanel.setCaption(resources.message(SAMPLE_PANEL));
    view.name.addStyleName(NAME);
    view.name.setCaption(resources.message(NAME));
    view.support.addStyleName(SUPPORT);
    view.support.setCaption(resources.message(SUPPORT));
    view.status.addStyleName(STATUS);
    view.status.setCaption(resources.message(STATUS));
    view.quantity.addStyleName(QUANTITY);
    view.quantity.setCaption(resources.message(QUANTITY));
    view.volume.addStyleName(VOLUME);
    view.volume.setCaption(resources.message(VOLUME));
    view.numberProteins.addStyleName(NUMBER_PROTEINS);
    view.numberProteins.setCaption(resources.message(NUMBER_PROTEINS));
    view.molecularWeight.addStyleName(MOLECULAR_WEIGHT);
    view.molecularWeight.setCaption(resources.message(MOLECULAR_WEIGHT));
    view.containerPanel.addStyleName(CONTAINER);
    view.containerPanel.setCaption(resources.message(CONTAINER));
    view.containerType.addStyleName(CONTAINER + "-" + CONTAINER_TYPE);
    view.containerType.setCaption(resources.message(CONTAINER + "." + CONTAINER_TYPE));
    view.containerName.addStyleName(CONTAINER + "-" + CONTAINER_NAME);
    view.containerName.setCaption(resources.message(CONTAINER + "." + CONTAINER_NAME));
    view.digestionsPanel.addStyleName(DIGESTIONS_PANEL);
    view.digestionsPanel.setCaption(resources.message(DIGESTIONS_PANEL));
    view.digestions.addStyleName(DIGESTIONS);
    prepareDigestionsGrid();
    view.enrichmentsPanel.addStyleName(ENRICHMENTS_PANEL);
    view.enrichmentsPanel.setCaption(resources.message(ENRICHMENTS_PANEL));
    view.enrichments.addStyleName(ENRICHMENTS);
    prepareEnrichmentsGrid();
    view.dilutionsPanel.addStyleName(DILUTIONS_PANEL);
    view.dilutionsPanel.setCaption(resources.message(DILUTIONS_PANEL));
    view.dilutions.addStyleName(DILUTIONS);
    prepareDilutionsGrid();
    view.fractionationsPanel.addStyleName(FRACTIONATIONS_PANEL);
    view.fractionationsPanel.setCaption(resources.message(FRACTIONATIONS_PANEL));
    view.fractionations.addStyleName(FRACTIONATIONS);
    prepareFractionationsGrid();
    view.transfersPanel.addStyleName(TRANSFERS_PANEL);
    view.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    view.transfers.addStyleName(TRANSFERS);
    prepareTransfersGrid();
    view.msAnalysesPanel.addStyleName(MS_ANALYSES_PANEL);
    view.msAnalysesPanel.setCaption(resources.message(MS_ANALYSES_PANEL));
    view.msAnalyses.addStyleName(MS_ANALYSES);
    prepareMsAnalysesGrid();
    view.dataAnalysesPanel.addStyleName(DATA_ANALYSES_PANEL);
    view.dataAnalysesPanel.setCaption(resources.message(DATA_ANALYSES_PANEL));
    view.dataAnalyses.addStyleName(DATA_ANALYSES);
    prepareDataAnalysesGrid();
    updateSample();
  }

  private void prepareDigestionsGrid() {
    MessageResource resources = view.getResources();
    view.digestions.addColumn(ts -> ts.getDigestion().getProtocol().getName())
        .setId(DIGESTION_PROTOCOL)
        .setCaption(resources.message(DIGESTIONS + "." + DIGESTION_PROTOCOL));
    view.digestions.addColumn(ts -> ts.getContainer().getFullName()).setId(DIGESTION_CONTAINER)
        .setCaption(resources.message(DIGESTIONS + "." + DIGESTION_CONTAINER));
    view.digestions.addColumn(ts -> ts.getComments()).setId(DIGESTION_COMMENTS)
        .setCaption(resources.message(DIGESTIONS + "." + DIGESTION_COMMENTS));
  }

  private void prepareEnrichmentsGrid() {
    MessageResource resources = view.getResources();
    view.enrichments.addColumn(ts -> ts.getEnrichment().getProtocol().getName())
        .setId(ENRICHMENT_PROTOCOL)
        .setCaption(resources.message(ENRICHMENTS + "." + ENRICHMENT_PROTOCOL));
    view.enrichments.addColumn(ts -> ts.getContainer().getFullName()).setId(ENRICHMENT_CONTAINER)
        .setCaption(resources.message(ENRICHMENTS + "." + ENRICHMENT_CONTAINER));
    view.enrichments.addColumn(ts -> ts.getComments()).setId(ENRICHMENT_COMMENTS)
        .setCaption(resources.message(ENRICHMENTS + "." + ENRICHMENT_COMMENTS));
  }

  private void prepareDilutionsGrid() {
    MessageResource resources = view.getResources();
    view.dilutions.addColumn(ts -> ts.getSourceVolume()).setId(DILUTION_SOURCE_VOLUME)
        .setCaption(resources.message(DILUTIONS + "." + DILUTION_SOURCE_VOLUME));
    view.dilutions.addColumn(ts -> ts.getSolvent()).setId(DILUTION_SOLVENT)
        .setCaption(resources.message(DILUTIONS + "." + DILUTION_SOLVENT));
    view.dilutions.addColumn(ts -> ts.getSolventVolume()).setId(DILUTION_SOLVENT_VOLUME)
        .setCaption(resources.message(DILUTIONS + "." + DILUTION_SOLVENT_VOLUME));
    view.dilutions.addColumn(ts -> ts.getContainer().getFullName()).setId(DILUTION_CONTAINER)
        .setCaption(resources.message(DILUTIONS + "." + DILUTION_CONTAINER));
    view.dilutions.addColumn(ts -> ts.getComments()).setId(DILUTION_COMMENTS)
        .setCaption(resources.message(DILUTIONS + "." + DILUTION_COMMENTS));
  }

  private void prepareFractionationsGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    view.fractionations
        .addColumn(ts -> ts.getFractionation().getFractionationType().getLabel(locale))
        .setId(FRACTIONATION_TYPE)
        .setCaption(resources.message(FRACTIONATIONS + "." + FRACTIONATION_TYPE));
    view.fractionations.addColumn(ts -> {
      switch (ts.getFractionation().getFractionationType()) {
        case MUDPIT:
          return ts.getNumber();
        case PI:
          return ts.getPiInterval();
        default:
          return FractionationType.getNullLabel(locale);
      }
    }).setId(FRACTIONATION_TYPE_VALUE)
        .setCaption(resources.message(FRACTIONATIONS + "." + FRACTIONATION_TYPE_VALUE));
    view.fractionations.addColumn(ts -> ts.getContainer().getFullName())
        .setId(FRACTIONATION_CONTAINER)
        .setCaption(resources.message(FRACTIONATIONS + "." + FRACTIONATION_CONTAINER));
    view.fractionations.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(FRACTIONATION_DESTINATION_CONTAINER)
        .setCaption(resources.message(FRACTIONATIONS + "." + FRACTIONATION_DESTINATION_CONTAINER));
    view.fractionations.addColumn(ts -> ts.getComments()).setId(FRACTIONATION_COMMENTS)
        .setCaption(resources.message(FRACTIONATIONS + "." + FRACTIONATION_COMMENTS));
  }

  private void prepareTransfersGrid() {
    MessageResource resources = view.getResources();
    view.transfers.addColumn(ts -> ts.getContainer().getFullName()).setId(TRANSFER_CONTAINER)
        .setCaption(resources.message(TRANSFERS + "." + TRANSFER_CONTAINER));
    view.transfers.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(TRANSFER_DESTINATION_CONTAINER)
        .setCaption(resources.message(TRANSFERS + "." + TRANSFER_DESTINATION_CONTAINER));
    view.transfers.addColumn(ts -> ts.getComments()).setId(TRANSFER_COMMENTS)
        .setCaption(resources.message(TRANSFERS + "." + TRANSFER_COMMENTS));
  }

  private void prepareMsAnalysesGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    view.msAnalyses
        .addColumn(aq -> aq.getMsAnalysis().getMassDetectionInstrument().getLabel(locale))
        .setId(MS_ANALYSES_MASS_DETECTION_INSTRUMENT)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_MASS_DETECTION_INSTRUMENT));
    view.msAnalyses.addColumn(aq -> aq.getMsAnalysis().getSource().getLabel(locale))
        .setId(MS_ANALYSES_SOURCE)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_SOURCE));
    view.msAnalyses.addColumn(aq -> aq.getNumberOfAcquisition())
        .setId(MS_ANALYSES_NUMBER_OF_ACQUISITION)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_NUMBER_OF_ACQUISITION));
    view.msAnalyses.addColumn(aq -> aq.getAcquisitionFile()).setId(MS_ANALYSES_ACQUISITION_FILE)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_ACQUISITION_FILE));
    view.msAnalyses.addColumn(aq -> aq.getSampleListName()).setId(MS_ANALYSES_SAMPLE_LIST_NAME)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_SAMPLE_LIST_NAME));
    view.msAnalyses.addColumn(aq -> aq.getContainer().getFullName()).setId(MS_ANALYSES_CONTAINER)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_CONTAINER));
    view.msAnalyses.addColumn(aq -> aq.getComments()).setId(MS_ANALYSES_COMMENTS)
        .setCaption(resources.message(MS_ANALYSES + "." + MS_ANALYSES_COMMENTS));
  }

  private void prepareDataAnalysesGrid() {
  }

  private void updateSample() {
    if (sample != null) {
      Locale locale = view.getLocale();
      view.name.setValue(sample.getName());
      view.support.setValue(sample.getSupport().getLabel(locale));
      view.status.setValue(sample.getStatus().getLabel(locale));
      view.quantity.setValue(sample.getQuantity());
      view.volume.setValue(new StringToDoubleConverter("").convertToPresentation(sample.getVolume(),
          new ValueContext(view.volume)));
      view.numberProteins.setValue(new StringToIntegerConverter("")
          .convertToPresentation(sample.getNumberProtein(), new ValueContext(view.numberProteins)));
      view.molecularWeight.setValue(new StringToDoubleConverter("").convertToPresentation(
          sample.getMolecularWeight(), new ValueContext(view.molecularWeight)));
      view.containerType.setValue(sample.getOriginalContainer().getType().getLabel(locale));
      view.containerName.setValue(sample.getOriginalContainer().getName());
    } else {
      view.name.setValue("");
      view.support.setValue("");
      view.status.setValue("");
      view.quantity.setValue("");
      view.volume.setValue("");
      view.numberProteins.setValue("");
      view.molecularWeight.setValue("");
      view.containerType.setValue("");
      view.containerName.setValue("");
    }
    view.digestions.setItems(
        digestionService.all(getBean()).stream().flatMap(d -> d.getTreatmentSamples().stream()));
    view.enrichments.setItems(
        enrichmentService.all(getBean()).stream().flatMap(d -> d.getTreatmentSamples().stream()));
    view.dilutions.setItems(
        dilutionService.all(getBean()).stream().flatMap(d -> d.getTreatmentSamples().stream()));
    view.fractionations.setItems(fractionationService.all(getBean()).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()));
    view.transfers.setItems(
        transferService.all(getBean()).stream().flatMap(d -> d.getTreatmentSamples().stream()));
    view.msAnalyses.setItems(
        msAnalysisService.all(getBean()).stream().flatMap(d -> d.getAcquisitions().stream()));
  }

  SubmissionSample getBean() {
    return sample;
  }

  void setBean(SubmissionSample sample) {
    this.sample = sample;
    updateSample();
  }
}
