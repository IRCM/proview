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
import static ca.qc.ircm.proview.fractionation.QFraction.fraction;
import static ca.qc.ircm.proview.fractionation.QFractionation.fractionation;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.solubilisation.QSolubilisedSample.solubilisedSample;
import static ca.qc.ircm.proview.standard.QAddedStandard.addedStandard;
import static ca.qc.ircm.proview.transfer.QTransferedSample.transferedSample;

import ca.qc.ircm.proview.digestion.DigestedSample;
import ca.qc.ircm.proview.digestion.DigestionService;
import ca.qc.ircm.proview.dilution.DilutedSample;
import ca.qc.ircm.proview.dilution.DilutionService;
import ca.qc.ircm.proview.enrichment.EnrichedSample;
import ca.qc.ircm.proview.enrichment.EnrichmentService;
import ca.qc.ircm.proview.fractionation.Fraction;
import ca.qc.ircm.proview.fractionation.FractionationService;
import ca.qc.ircm.proview.fractionation.FractionationType;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.solubilisation.SolubilisationService;
import ca.qc.ircm.proview.solubilisation.SolubilisedSample;
import ca.qc.ircm.proview.standard.AddedStandard;
import ca.qc.ircm.proview.standard.StandardAdditionService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.transfer.TransferService;
import ca.qc.ircm.proview.transfer.TransferedSample;
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
      TRANSFERS + "." + transferedSample.sample.getMetadata().getName();
  public static final String TRANSFER_CONTAINER =
      TRANSFERS + "." + transferedSample.container.getMetadata().getName();
  public static final String TRANSFER_DESTINATION_CONTAINER =
      TRANSFERS + "." + transferedSample.destinationContainer.getMetadata().getName();
  public static final String TRANSFER_COMMENT =
      TRANSFERS + "." + transferedSample.comment.getMetadata().getName();
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
  public static final String SOLUBILISATION_COMMENT =
      SOLUBILISATIONS + "." + solubilisedSample.comment.getMetadata().getName();
  public static final String DIGESTIONS_PANEL = "digestionsPanel";
  public static final String DIGESTIONS = "digestions";
  public static final String DIGESTION_SAMPLE =
      DIGESTIONS + "." + digestedSample.sample.getMetadata().getName();
  public static final String DIGESTION_PROTOCOL =
      DIGESTIONS + "." + digestion.protocol.getMetadata().getName();
  public static final String DIGESTION_CONTAINER =
      DIGESTIONS + "." + digestedSample.container.getMetadata().getName();
  public static final String DIGESTION_COMMENT =
      DIGESTIONS + "." + digestedSample.comment.getMetadata().getName();
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
  public static final String STANDARD_ADDITION_COMMENT =
      STANDARD_ADDITIONS + "." + addedStandard.comment.getMetadata().getName();
  public static final String ENRICHMENTS_PANEL = "enrichmentsPanel";
  public static final String ENRICHMENTS = "enrichments";
  public static final String ENRICHMENT_SAMPLE =
      ENRICHMENTS + "." + enrichedSample.sample.getMetadata().getName();
  public static final String ENRICHMENT_PROTOCOL =
      ENRICHMENTS + "." + enrichment.protocol.getMetadata().getName();
  public static final String ENRICHMENT_CONTAINER =
      ENRICHMENTS + "." + enrichedSample.container.getMetadata().getName();
  public static final String ENRICHMENT_COMMENT =
      ENRICHMENTS + "." + enrichedSample.comment.getMetadata().getName();
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
  public static final String DILUTION_COMMENT =
      DILUTIONS + "." + dilutedSample.comment.getMetadata().getName();
  public static final String FRACTIONATIONS_PANEL = "fractionationsPanel";
  public static final String FRACTIONATIONS = "fractionations";
  public static final String FRACTIONATION_SAMPLE =
      FRACTIONATIONS + "." + fraction.sample.getMetadata().getName();
  public static final String FRACTIONATION_TYPE =
      FRACTIONATIONS + "." + fractionation.fractionationType.getMetadata().getName();
  public static final String FRACTIONATION_TYPE_VALUE = FRACTIONATIONS + "." + "typeValue";
  public static final String FRACTIONATION_CONTAINER =
      FRACTIONATIONS + "." + fraction.container.getMetadata().getName();
  public static final String FRACTIONATION_DESTINATION_CONTAINER =
      FRACTIONATIONS + "." + fraction.destinationContainer.getMetadata().getName();
  public static final String FRACTIONATION_COMMENT =
      FRACTIONATIONS + "." + fraction.comment.getMetadata().getName();
  private SubmissionTreatmentsForm view;
  private SubmissionTreatmentsFormDesign design;
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
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    final MessageResource resources = view.getResources();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.samples.addStyleName(SAMPLES);
    prepareSamplesGrid();
    design.transfersPanel.addStyleName(TRANSFERS_PANEL);
    design.transfersPanel.setCaption(resources.message(TRANSFERS_PANEL));
    design.transfers.addStyleName(TRANSFERS);
    prepareTransfersGrid();
    design.solubilisationsPanel.addStyleName(SOLUBILISATIONS_PANEL);
    design.solubilisationsPanel.setCaption(resources.message(SOLUBILISATIONS_PANEL));
    design.solubilisations.addStyleName(SOLUBILISATIONS);
    prepareSolubilisationGrid();
    design.digestionsPanel.addStyleName(DIGESTIONS_PANEL);
    design.digestionsPanel.setCaption(resources.message(DIGESTIONS_PANEL));
    design.digestions.addStyleName(DIGESTIONS);
    prepareDigestionsGrid();
    design.standardAdditionsPanel.addStyleName(STANDARD_ADDITIONS_PANEL);
    design.standardAdditionsPanel.setCaption(resources.message(STANDARD_ADDITIONS_PANEL));
    design.standardAdditions.addStyleName(STANDARD_ADDITIONS);
    prepareStandardAdditionsGrid();
    design.enrichmentsPanel.addStyleName(ENRICHMENTS_PANEL);
    design.enrichmentsPanel.setCaption(resources.message(ENRICHMENTS_PANEL));
    design.enrichments.addStyleName(ENRICHMENTS);
    prepareEnrichmentsGrid();
    design.dilutionsPanel.addStyleName(DILUTIONS_PANEL);
    design.dilutionsPanel.setCaption(resources.message(DILUTIONS_PANEL));
    design.dilutions.addStyleName(DILUTIONS);
    prepareDilutionsGrid();
    design.fractionationsPanel.addStyleName(FRACTIONATIONS_PANEL);
    design.fractionationsPanel.setCaption(resources.message(FRACTIONATIONS_PANEL));
    design.fractionations.addStyleName(FRACTIONATIONS);
    prepareFractionationsGrid();
    updateSubmission();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    design.samples.addColumn(sa -> sa.getName()).setId(SAMPLES_NAME)
        .setCaption(resources.message(SAMPLES_NAME));
    design.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(SAMPLES_LAST_CONTAINER).setCaption(resources.message(SAMPLES_LAST_CONTAINER));
  }

  private void prepareTransfersGrid() {
    MessageResource resources = view.getResources();
    design.transfers.addColumn(ts -> ts.getSample().getName()).setId(TRANSFER_SAMPLE)
        .setCaption(resources.message(TRANSFER_SAMPLE));
    design.transfers.addColumn(ts -> ts.getContainer().getFullName()).setId(TRANSFER_CONTAINER)
        .setCaption(resources.message(TRANSFER_CONTAINER));
    design.transfers.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(TRANSFER_DESTINATION_CONTAINER)
        .setCaption(resources.message(TRANSFER_DESTINATION_CONTAINER));
    design.transfers.addColumn(ts -> ts.getComment()).setId(TRANSFER_COMMENT)
        .setCaption(resources.message(TRANSFER_COMMENT));
  }

  private void prepareSolubilisationGrid() {
    MessageResource resources = view.getResources();
    design.solubilisations.addColumn(ts -> ts.getSample().getName()).setId(SOLUBILISATION_SAMPLE)
        .setCaption(resources.message(SOLUBILISATION_SAMPLE));
    design.solubilisations.addColumn(ts -> ts.getSolvent()).setId(SOLUBILISATION_SOLVENT)
        .setCaption(resources.message(SOLUBILISATION_SOLVENT));
    design.solubilisations.addColumn(ts -> ts.getSolventVolume())
        .setId(SOLUBILISATION_SOLVENT_VOLUME)
        .setCaption(resources.message(SOLUBILISATION_SOLVENT_VOLUME));
    design.solubilisations.addColumn(ts -> ts.getContainer().getFullName())
        .setId(SOLUBILISATION_CONTAINER).setCaption(resources.message(SOLUBILISATION_CONTAINER));
    design.solubilisations.addColumn(ts -> ts.getComment()).setId(SOLUBILISATION_COMMENT)
        .setCaption(resources.message(SOLUBILISATION_COMMENT));
  }

  private void prepareDigestionsGrid() {
    MessageResource resources = view.getResources();
    design.digestions.addColumn(ts -> ts.getSample().getName()).setId(DIGESTION_SAMPLE)
        .setCaption(resources.message(DIGESTION_SAMPLE));
    design.digestions.addColumn(ts -> ts.getDigestion().getProtocol().getName())
        .setId(DIGESTION_PROTOCOL).setCaption(resources.message(DIGESTION_PROTOCOL));
    design.digestions.addColumn(ts -> ts.getContainer().getFullName()).setId(DIGESTION_CONTAINER)
        .setCaption(resources.message(DIGESTION_CONTAINER));
    design.digestions.addColumn(ts -> ts.getComment()).setId(DIGESTION_COMMENT)
        .setCaption(resources.message(DIGESTION_COMMENT));
  }

  private void prepareStandardAdditionsGrid() {
    MessageResource resources = view.getResources();
    design.standardAdditions.addColumn(ts -> ts.getSample().getName())
        .setId(STANDARD_ADDITION_SAMPLE).setCaption(resources.message(STANDARD_ADDITION_SAMPLE));
    design.standardAdditions.addColumn(ts -> ts.getName()).setId(STANDARD_ADDITION_NAME)
        .setCaption(resources.message(STANDARD_ADDITION_NAME));
    design.standardAdditions.addColumn(ts -> ts.getQuantity()).setId(STANDARD_ADDITION_QUANTITY)
        .setCaption(resources.message(STANDARD_ADDITION_QUANTITY));
    design.standardAdditions.addColumn(ts -> ts.getContainer().getFullName())
        .setId(STANDARD_ADDITION_CONTAINER)
        .setCaption(resources.message(STANDARD_ADDITION_CONTAINER));
    design.standardAdditions.addColumn(ts -> ts.getComment()).setId(STANDARD_ADDITION_COMMENT)
        .setCaption(resources.message(STANDARD_ADDITION_COMMENT));
  }

  private void prepareEnrichmentsGrid() {
    MessageResource resources = view.getResources();
    design.enrichments.addColumn(ts -> ts.getSample().getName()).setId(ENRICHMENT_SAMPLE)
        .setCaption(resources.message(ENRICHMENT_SAMPLE));
    design.enrichments.addColumn(ts -> ts.getEnrichment().getProtocol().getName())
        .setId(ENRICHMENT_PROTOCOL).setCaption(resources.message(ENRICHMENT_PROTOCOL));
    design.enrichments.addColumn(ts -> ts.getContainer().getFullName()).setId(ENRICHMENT_CONTAINER)
        .setCaption(resources.message(ENRICHMENT_CONTAINER));
    design.enrichments.addColumn(ts -> ts.getComment()).setId(ENRICHMENT_COMMENT)
        .setCaption(resources.message(ENRICHMENT_COMMENT));
  }

  private void prepareDilutionsGrid() {
    MessageResource resources = view.getResources();
    design.dilutions.addColumn(ts -> ts.getSample().getName()).setId(DILUTION_SAMPLE)
        .setCaption(resources.message(DILUTION_SAMPLE));
    design.dilutions.addColumn(ts -> ts.getSourceVolume()).setId(DILUTION_SOURCE_VOLUME)
        .setCaption(resources.message(DILUTION_SOURCE_VOLUME));
    design.dilutions.addColumn(ts -> ts.getSolvent()).setId(DILUTION_SOLVENT)
        .setCaption(resources.message(DILUTION_SOLVENT));
    design.dilutions.addColumn(ts -> ts.getSolventVolume()).setId(DILUTION_SOLVENT_VOLUME)
        .setCaption(resources.message(DILUTION_SOLVENT_VOLUME));
    design.dilutions.addColumn(ts -> ts.getContainer().getFullName()).setId(DILUTION_CONTAINER)
        .setCaption(resources.message(DILUTION_CONTAINER));
    design.dilutions.addColumn(ts -> ts.getComment()).setId(DILUTION_COMMENT)
        .setCaption(resources.message(DILUTION_COMMENT));
  }

  private void prepareFractionationsGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    design.fractionations.addColumn(ts -> ts.getSample().getName()).setId(FRACTIONATION_SAMPLE)
        .setCaption(resources.message(FRACTIONATION_SAMPLE));
    design.fractionations
        .addColumn(ts -> ts.getFractionation().getFractionationType().getLabel(locale))
        .setId(FRACTIONATION_TYPE).setCaption(resources.message(FRACTIONATION_TYPE));
    design.fractionations.addColumn(ts -> {
      switch (ts.getFractionation().getFractionationType()) {
        case MUDPIT:
          return ts.getNumber();
        case PI:
          return ts.getPiInterval();
        default:
          return FractionationType.getNullLabel(locale);
      }
    }).setId(FRACTIONATION_TYPE_VALUE).setCaption(resources.message(FRACTIONATION_TYPE_VALUE));
    design.fractionations.addColumn(ts -> ts.getContainer().getFullName())
        .setId(FRACTIONATION_CONTAINER).setCaption(resources.message(FRACTIONATION_CONTAINER));
    design.fractionations.addColumn(ts -> ts.getDestinationContainer().getFullName())
        .setId(FRACTIONATION_DESTINATION_CONTAINER)
        .setCaption(resources.message(FRACTIONATION_DESTINATION_CONTAINER));
    design.fractionations.addColumn(ts -> ts.getComment()).setId(FRACTIONATION_COMMENT)
        .setCaption(resources.message(FRACTIONATION_COMMENT));
  }

  private void updateSubmission() {
    if (submission != null) {
      design.samples.setItems(submission.getSamples());
    } else {
      design.samples.setItems(new ArrayList<>());
    }
    List<TransferedSample> transfers = transferService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.transfers.setItems(transfers);
    design.transfersPanel.setVisible(!transfers.isEmpty());
    List<SolubilisedSample> solubilisations = solubilisationService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.solubilisations.setItems(solubilisations);
    design.solubilisationsPanel.setVisible(!solubilisations.isEmpty());
    List<DigestedSample> digestions = digestionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.digestions.setItems(digestions);
    design.digestionsPanel.setVisible(!digestions.isEmpty());
    List<AddedStandard> standardAdditions = standardAdditionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.standardAdditions.setItems(standardAdditions);
    design.standardAdditionsPanel.setVisible(!standardAdditions.isEmpty());
    List<EnrichedSample> enrichments = enrichmentService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.enrichments.setItems(enrichments);
    design.enrichmentsPanel.setVisible(!enrichments.isEmpty());
    List<DilutedSample> dilutions = dilutionService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.dilutions.setItems(dilutions);
    design.dilutionsPanel.setVisible(!dilutions.isEmpty());
    List<Fraction> fractionations = fractionationService.all(submission).stream()
        .flatMap(d -> d.getTreatmentSamples().stream()).collect(Collectors.toList());
    design.fractionations.setItems(fractionations);
    design.fractionationsPanel.setVisible(!fractionations.isEmpty());
  }

  Submission getValue() {
    return submission;
  }

  void setValue(Submission submission) {
    this.submission = submission;
    updateSubmission();
  }
}
