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

import static ca.qc.ircm.proview.digestion.QDigestedSample.digestedSample;
import static ca.qc.ircm.proview.digestion.QDigestion.digestion;
import static ca.qc.ircm.proview.dilution.QDilutedSample.dilutedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichedSample.enrichedSample;
import static ca.qc.ircm.proview.enrichment.QEnrichment.enrichment;
import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.fractionation.QFractionationDetail.fractionationDetail;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.transfer.QSampleTransfer.sampleTransfer;

import ca.qc.ircm.proview.digestion.DigestedSample;
import ca.qc.ircm.proview.digestion.DigestionService;
import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.enrichment.EnrichedSample;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.fractionation.FractionationDetail;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.transfer.SampleTransfer;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * Submission treatments form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionTreatmentsFormPresenter implements BinderValidator {
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLES = QSubmission.submission.samples.getMetadata().getName();
  public static final String SAMPLES_NAME = submissionSample.name.getMetadata().getName();
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
  private SubmissionTreatmentsForm view;
  private Submission submission;
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

  protected SubmissionTreatmentsFormPresenter() {
  }

  protected SubmissionTreatmentsFormPresenter(DigestionService digestionService,
      EnrichmentService enrichmentService, DilutionService dilutionService,
      FractionationService fractionationService, TransferService transferService) {
    this.digestionService = digestionService;
    this.enrichmentService = enrichmentService;
    this.dilutionService = dilutionService;
    this.fractionationService = fractionationService;
    this.transferService = transferService;
  }

  /**
   * Called by view when view is initialized.
   *
   * @param view
   *          view
   */
  public void init(SubmissionTreatmentsForm view) {
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    view.samples.addStyleName(SAMPLES);
    prepareSamplesGrid();
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
    updateSubmission();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    view.samples.addColumn(ts -> ts.getName()).setId(SAMPLES_NAME)
        .setCaption(resources.message(SAMPLES + "." + SAMPLES_NAME));
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

  private void updateSubmission() {
    if (submission != null) {
      view.samples.setItems(submission.getSamples());
    } else {
      view.samples.setItems(new ArrayList<>());
    }
    List<DigestedSample> digestions = digestionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.digestions.setItems(digestions);
    view.digestionsPanel.setVisible(!digestions.isEmpty());
    List<EnrichedSample> enrichments = enrichmentService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.enrichments.setItems(enrichments);
    view.enrichmentsPanel.setVisible(!enrichments.isEmpty());
    List<DilutedSample> dilutions = dilutionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.dilutions.setItems(dilutions);
    view.dilutionsPanel.setVisible(!dilutions.isEmpty());
    List<FractionationDetail> fractionations = fractionationService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.fractionations.setItems(fractionations);
    view.fractionationsPanel.setVisible(!fractionations.isEmpty());
    List<SampleTransfer> transfers = transferService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.transfers.setItems(transfers);
    view.transfersPanel.setVisible(!transfers.isEmpty());
  }

  Submission getBean() {
    return submission;
  }

  void setBean(Submission submission) {
    this.submission = submission;
    updateSubmission();
  }
}
