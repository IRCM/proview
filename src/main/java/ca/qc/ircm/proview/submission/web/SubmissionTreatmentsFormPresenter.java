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
import static ca.qc.ircm.proview.solubilisation.QSolubilisedSample.solubilisedSample;
import static ca.qc.ircm.proview.standard.QAddedStandard.addedStandard;
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
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.solubilisation.SolubilisationService;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAdditionService;
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
  public static final String SAMPLES_NAME =
      SAMPLES + "." + submissionSample.name.getMetadata().getName();
  public static final String SAMPLES_LAST_CONTAINER = SAMPLES + "." + "lastContainer";
  public static final String TRANSFERS_PANEL = "transfersPanel";
  public static final String TRANSFERS = "transfers";
  public static final String TRANSFER_SAMPLE =
      TRANSFERS + "." + sampleTransfer.sample.getMetadata().getName();
  public static final String TRANSFER_CONTAINER =
      TRANSFERS + "." + sampleTransfer.container.getMetadata().getName();
  public static final String TRANSFER_DESTINATION_CONTAINER =
      TRANSFERS + "." + sampleTransfer.destinationContainer.getMetadata().getName();
  public static final String TRANSFER_COMMENTS =
      TRANSFERS + "." + sampleTransfer.comments.getMetadata().getName();
  public static final String SOLUBILISATIONS_PANEL = "solubilisationsPanel";
  public static final String SOLUBILISATIONS = "solubilisations";
  public static final String SOLUBILISATION_SAMPLE =
      SOLUBILISATIONS + "." + solubilisedSample.sample.getMetadata().getName();
  public static final String SOLUBILISATION_SOLVENT =
      SOLUBILISATIONS + "." + solubilisedSample.solvent.getMetadata().getName();
  public static final String SOLUBILISATION_SOLVENT_VOLUME =
      SOLUBILISATIONS + "." + solubilisedSample.solventVolume.getMetadata().getName();
  public static final String SOLUBILISATION_CONTAINER =
      SOLUBILISATIONS + "." + solubilisedSample.container.getMetadata().getName();
  public static final String SOLUBILISATION_COMMENTS =
      SOLUBILISATIONS + "." + solubilisedSample.comments.getMetadata().getName();
  public static final String DIGESTIONS_PANEL = "digestionsPanel";
  public static final String DIGESTIONS = "digestions";
  public static final String DIGESTION_SAMPLE =
      DIGESTIONS + "." + digestedSample.sample.getMetadata().getName();
  public static final String DIGESTION_PROTOCOL =
      DIGESTIONS + "." + digestion.protocol.getMetadata().getName();
  public static final String DIGESTION_CONTAINER =
      DIGESTIONS + "." + digestedSample.container.getMetadata().getName();
  public static final String DIGESTION_COMMENTS =
      DIGESTIONS + "." + digestedSample.comments.getMetadata().getName();
  public static final String STANDARD_ADDITIONS_PANEL = "standardAdditionsPanel";
  public static final String STANDARD_ADDITIONS = "standardAdditions";
  public static final String STANDARD_ADDITION_SAMPLE =
      STANDARD_ADDITIONS + "." + addedStandard.sample.getMetadata().getName();
  public static final String STANDARD_ADDITION_NAME =
      STANDARD_ADDITIONS + "." + addedStandard.name.getMetadata().getName();
  public static final String STANDARD_ADDITION_QUANTITY =
      STANDARD_ADDITIONS + "." + addedStandard.quantity.getMetadata().getName();
  public static final String STANDARD_ADDITION_CONTAINER =
      STANDARD_ADDITIONS + "." + addedStandard.container.getMetadata().getName();
  public static final String STANDARD_ADDITION_COMMENTS =
      STANDARD_ADDITIONS + "." + addedStandard.comments.getMetadata().getName();
  public static final String ENRICHMENTS_PANEL = "enrichmentsPanel";
  public static final String ENRICHMENTS = "enrichments";
  public static final String ENRICHMENT_SAMPLE =
      ENRICHMENTS + "." + enrichedSample.sample.getMetadata().getName();
  public static final String ENRICHMENT_PROTOCOL =
      ENRICHMENTS + "." + enrichment.protocol.getMetadata().getName();
  public static final String ENRICHMENT_CONTAINER =
      ENRICHMENTS + "." + enrichedSample.container.getMetadata().getName();
  public static final String ENRICHMENT_COMMENTS =
      ENRICHMENTS + "." + enrichedSample.comments.getMetadata().getName();
  public static final String DILUTIONS_PANEL = "dilutionsPanel";
  public static final String DILUTIONS = "dilutions";
  public static final String DILUTION_SAMPLE =
      DILUTIONS + "." + dilutedSample.sample.getMetadata().getName();
  public static final String DILUTION_SOURCE_VOLUME =
      DILUTIONS + "." + dilutedSample.sourceVolume.getMetadata().getName();
  public static final String DILUTION_SOLVENT =
      DILUTIONS + "." + dilutedSample.solvent.getMetadata().getName();
  public static final String DILUTION_SOLVENT_VOLUME =
      DILUTIONS + "." + dilutedSample.solventVolume.getMetadata().getName();
  public static final String DILUTION_CONTAINER =
      DILUTIONS + "." + dilutedSample.container.getMetadata().getName();
  public static final String DILUTION_COMMENTS =
      DILUTIONS + "." + dilutedSample.comments.getMetadata().getName();
  public static final String FRACTIONATIONS_PANEL = "fractionationsPanel";
  public static final String FRACTIONATIONS = "fractionations";
  public static final String FRACTIONATION_SAMPLE =
      FRACTIONATIONS + "." + fractionationDetail.sample.getMetadata().getName();
  public static final String FRACTIONATION_TYPE =
      FRACTIONATIONS + "." + fractionation.fractionationType.getMetadata().getName();
  public static final String FRACTIONATION_TYPE_VALUE = FRACTIONATIONS + "." + "typeValue";
  public static final String FRACTIONATION_CONTAINER =
      FRACTIONATIONS + "." + fractionationDetail.container.getMetadata().getName();
  public static final String FRACTIONATION_DESTINATION_CONTAINER =
      FRACTIONATIONS + "." + fractionationDetail.destinationContainer.getMetadata().getName();
  public static final String FRACTIONATION_COMMENTS =
      FRACTIONATIONS + "." + fractionationDetail.comments.getMetadata().getName();
  private SubmissionTreatmentsForm view;
  private Submission submission;
  @Inject
  private SampleContainerService sampleContainerService;
  @Inject
  private SolubilisationService solubilisationService;
  @Inject
  private DigestionService digestionService;
  @Inject
  private EnrichmentService enrichmentService;
  @Inject
  private DilutionService dilutionService;
  @Inject
  private StandardAdditionService standardAdditionService;
  @Inject
  private FractionationService fractionationService;
  @Inject
  private TransferService transferService;

  protected SubmissionTreatmentsFormPresenter() {
  }

  protected SubmissionTreatmentsFormPresenter(SampleContainerService sampleContainerService,
      SolubilisationService solubilisationService, DigestionService digestionService,
      EnrichmentService enrichmentService, DilutionService dilutionService,
      StandardAdditionService standardAdditionService, FractionationService fractionationService,
      TransferService transferService) {
    this.sampleContainerService = sampleContainerService;
    this.solubilisationService = solubilisationService;
    this.digestionService = digestionService;
    this.enrichmentService = enrichmentService;
    this.dilutionService = dilutionService;
    this.standardAdditionService = standardAdditionService;
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
    view.transfersPanel.addStyleName(TRANSFERS_PANEL);
    view.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    view.transfers.addStyleName(TRANSFERS);
    prepareTransfersGrid();
    view.solubilisationsPanel.addStyleName(SOLUBILISATIONS_PANEL);
    view.solubilisationsPanel.setCaption(resources.message(SOLUBILISATIONS_PANEL));
    view.solubilisations.addStyleName(SOLUBILISATIONS);
    prepareSolubilisationGrid();
    view.digestionsPanel.addStyleName(DIGESTIONS_PANEL);
    view.digestionsPanel.setCaption(resources.message(DIGESTIONS_PANEL));
    view.digestions.addStyleName(DIGESTIONS);
    prepareDigestionsGrid();
    view.standardAdditionsPanel.addStyleName(STANDARD_ADDITIONS_PANEL);
    view.standardAdditionsPanel.setCaption(resources.message(STANDARD_ADDITIONS_PANEL));
    view.standardAdditions.addStyleName(STANDARD_ADDITIONS);
    prepareStandardAdditionsGrid();
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
    updateSubmission();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    view.samples.addColumn(sa -> sa.getName()).setId(SAMPLES_NAME)
        .setCaption(resources.message(SAMPLES_NAME));
    view.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(SAMPLES_LAST_CONTAINER).setCaption(resources.message(SAMPLES_LAST_CONTAINER));
  }

  private void prepareTransfersGrid() {
    MessageResource resources = view.getResources();
    view.transfers.addColumn(ts -> ts.getSample().getName()).setId(TRANSFER_SAMPLE)
        .setCaption(resources.message(TRANSFER_SAMPLE));
    view.transfers.addColumn(ts -> ts.getContainer().getFullName()).setId(TRANSFER_CONTAINER)
        .setCaption(resources.message(TRANSFER_CONTAINER));
    view.transfers.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(TRANSFER_DESTINATION_CONTAINER)
        .setCaption(resources.message(TRANSFER_DESTINATION_CONTAINER));
    view.transfers.addColumn(ts -> ts.getComments()).setId(TRANSFER_COMMENTS)
        .setCaption(resources.message(TRANSFER_COMMENTS));
  }

  private void prepareSolubilisationGrid() {
    MessageResource resources = view.getResources();
    view.solubilisations.addColumn(ts -> ts.getSample().getName()).setId(SOLUBILISATION_SAMPLE)
        .setCaption(resources.message(SOLUBILISATION_SAMPLE));
    view.solubilisations.addColumn(ts -> ts.getSolvent()).setId(SOLUBILISATION_SOLVENT)
        .setCaption(resources.message(SOLUBILISATION_SOLVENT));
    view.solubilisations.addColumn(ts -> ts.getSolventVolume()).setId(SOLUBILISATION_SOLVENT_VOLUME)
        .setCaption(resources.message(SOLUBILISATION_SOLVENT_VOLUME));
    view.solubilisations.addColumn(ts -> ts.getContainer().getFullName())
        .setId(SOLUBILISATION_CONTAINER).setCaption(resources.message(SOLUBILISATION_CONTAINER));
    view.solubilisations.addColumn(ts -> ts.getComments()).setId(SOLUBILISATION_COMMENTS)
        .setCaption(resources.message(SOLUBILISATION_COMMENTS));
  }

  private void prepareDigestionsGrid() {
    MessageResource resources = view.getResources();
    view.digestions.addColumn(ts -> ts.getSample().getName()).setId(DIGESTION_SAMPLE)
        .setCaption(resources.message(DIGESTION_SAMPLE));
    view.digestions.addColumn(ts -> ts.getDigestion().getProtocol().getName())
        .setId(DIGESTION_PROTOCOL).setCaption(resources.message(DIGESTION_PROTOCOL));
    view.digestions.addColumn(ts -> ts.getContainer().getFullName()).setId(DIGESTION_CONTAINER)
        .setCaption(resources.message(DIGESTION_CONTAINER));
    view.digestions.addColumn(ts -> ts.getComments()).setId(DIGESTION_COMMENTS)
        .setCaption(resources.message(DIGESTION_COMMENTS));
  }

  private void prepareStandardAdditionsGrid() {
    MessageResource resources = view.getResources();
    view.standardAdditions.addColumn(ts -> ts.getSample().getName()).setId(STANDARD_ADDITION_SAMPLE)
        .setCaption(resources.message(STANDARD_ADDITION_SAMPLE));
    view.standardAdditions.addColumn(ts -> ts.getName()).setId(STANDARD_ADDITION_NAME)
        .setCaption(resources.message(STANDARD_ADDITION_NAME));
    view.standardAdditions.addColumn(ts -> ts.getQuantity()).setId(STANDARD_ADDITION_QUANTITY)
        .setCaption(resources.message(STANDARD_ADDITION_QUANTITY));
    view.standardAdditions.addColumn(ts -> ts.getContainer().getFullName())
        .setId(STANDARD_ADDITION_CONTAINER)
        .setCaption(resources.message(STANDARD_ADDITION_CONTAINER));
    view.standardAdditions.addColumn(ts -> ts.getComments()).setId(STANDARD_ADDITION_COMMENTS)
        .setCaption(resources.message(STANDARD_ADDITION_COMMENTS));
  }

  private void prepareEnrichmentsGrid() {
    MessageResource resources = view.getResources();
    view.enrichments.addColumn(ts -> ts.getSample().getName()).setId(ENRICHMENT_SAMPLE)
        .setCaption(resources.message(ENRICHMENT_SAMPLE));
    view.enrichments.addColumn(ts -> ts.getEnrichment().getProtocol().getName())
        .setId(ENRICHMENT_PROTOCOL).setCaption(resources.message(ENRICHMENT_PROTOCOL));
    view.enrichments.addColumn(ts -> ts.getContainer().getFullName()).setId(ENRICHMENT_CONTAINER)
        .setCaption(resources.message(ENRICHMENT_CONTAINER));
    view.enrichments.addColumn(ts -> ts.getComments()).setId(ENRICHMENT_COMMENTS)
        .setCaption(resources.message(ENRICHMENT_COMMENTS));
  }

  private void prepareDilutionsGrid() {
    MessageResource resources = view.getResources();
    view.dilutions.addColumn(ts -> ts.getSample().getName()).setId(DILUTION_SAMPLE)
        .setCaption(resources.message(DILUTION_SAMPLE));
    view.dilutions.addColumn(ts -> ts.getSourceVolume()).setId(DILUTION_SOURCE_VOLUME)
        .setCaption(resources.message(DILUTION_SOURCE_VOLUME));
    view.dilutions.addColumn(ts -> ts.getSolvent()).setId(DILUTION_SOLVENT)
        .setCaption(resources.message(DILUTION_SOLVENT));
    view.dilutions.addColumn(ts -> ts.getSolventVolume()).setId(DILUTION_SOLVENT_VOLUME)
        .setCaption(resources.message(DILUTION_SOLVENT_VOLUME));
    view.dilutions.addColumn(ts -> ts.getContainer().getFullName()).setId(DILUTION_CONTAINER)
        .setCaption(resources.message(DILUTION_CONTAINER));
    view.dilutions.addColumn(ts -> ts.getComments()).setId(DILUTION_COMMENTS)
        .setCaption(resources.message(DILUTION_COMMENTS));
  }

  private void prepareFractionationsGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    view.fractionations.addColumn(ts -> ts.getSample().getName()).setId(FRACTIONATION_SAMPLE)
        .setCaption(resources.message(FRACTIONATION_SAMPLE));
    view.fractionations
        .addColumn(ts -> ts.getFractionation().getFractionationType().getLabel(locale))
        .setId(FRACTIONATION_TYPE).setCaption(resources.message(FRACTIONATION_TYPE));
    view.fractionations.addColumn(ts -> {
      switch (ts.getFractionation().getFractionationType()) {
        case MUDPIT:
          return ts.getNumber();
        case PI:
          return ts.getPiInterval();
        default:
          return FractionationType.getNullLabel(locale);
      }
    }).setId(FRACTIONATION_TYPE_VALUE).setCaption(resources.message(FRACTIONATION_TYPE_VALUE));
    view.fractionations.addColumn(ts -> ts.getContainer().getFullName())
        .setId(FRACTIONATION_CONTAINER).setCaption(resources.message(FRACTIONATION_CONTAINER));
    view.fractionations.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(FRACTIONATION_DESTINATION_CONTAINER)
        .setCaption(resources.message(FRACTIONATION_DESTINATION_CONTAINER));
    view.fractionations.addColumn(ts -> ts.getComments()).setId(FRACTIONATION_COMMENTS)
        .setCaption(resources.message(FRACTIONATION_COMMENTS));
  }

  private void updateSubmission() {
    if (submission != null) {
      view.samples.setItems(submission.getSamples());
    } else {
      view.samples.setItems(new ArrayList<>());
    }
    List<SampleTransfer> transfers = transferService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.transfers.setItems(transfers);
    view.transfersPanel.setVisible(!transfers.isEmpty());
    List<SolubilisedSample> solubilisations = solubilisationService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.solubilisations.setItems(solubilisations);
    view.solubilisationsPanel.setVisible(!solubilisations.isEmpty());
    List<DigestedSample> digestions = digestionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.digestions.setItems(digestions);
    view.digestionsPanel.setVisible(!digestions.isEmpty());
    List<AddedStandard> standardAdditions = standardAdditionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    view.standardAdditions.setItems(standardAdditions);
    view.standardAdditionsPanel.setVisible(!standardAdditions.isEmpty());
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
  }

  Submission getBean() {
    return submission;
  }

  void setBean(Submission submission) {
    this.submission = submission;
    updateSubmission();
  }
}
