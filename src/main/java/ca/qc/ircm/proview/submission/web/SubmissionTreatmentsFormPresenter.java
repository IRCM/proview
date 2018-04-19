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

import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;
import static ca.qc.ircm.proview.treatment.QTreatment.treatment;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.fractionation.web.FractionationView;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.proview.web.validator.BinderValidator;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
      property(SAMPLES, submissionSample.name.getMetadata().getName());
  public static final String SAMPLES_STATUS =
      property(SAMPLES, submissionSample.status.getMetadata().getName());
  public static final String SAMPLES_LAST_CONTAINER = property(SAMPLES, "lastContainer");
  public static final String TREATMENTS_PANEL = "treatmentsPanel";
  public static final String TREATMENTS = "treatments";
  public static final String TREATMENT_TYPE = property(TREATMENTS, "type");
  public static final String TREATMENT_TIME =
      property(TREATMENTS, treatment.insertTime.getMetadata().getName());
  public static final String TREATMENT_SAMPLES = property(TREATMENTS, "samples");
  private SubmissionTreatmentsForm view;
  private SubmissionTreatmentsFormDesign design;
  private Submission submission;
  @Inject
  private SampleContainerService sampleContainerService;
  @Inject
  private TreatmentService treatmentService;

  protected SubmissionTreatmentsFormPresenter() {
  }

  protected SubmissionTreatmentsFormPresenter(SampleContainerService sampleContainerService,
      TreatmentService treatmentService) {
    this.sampleContainerService = sampleContainerService;
    this.treatmentService = treatmentService;
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
    final Locale locale = view.getLocale();
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault());
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.samples.addStyleName(SAMPLES);
    design.samples.addColumn(sa -> sa.getName()).setId(SAMPLES_NAME)
        .setCaption(resources.message(SAMPLES_NAME));
    design.samples.addColumn(sa -> sa.getStatus().getLabel(locale)).setId(SAMPLES_STATUS)
        .setCaption(resources.message(SAMPLES_STATUS));
    design.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(SAMPLES_LAST_CONTAINER).setCaption(resources.message(SAMPLES_LAST_CONTAINER));
    design.treatmentsPanel.addStyleName(TREATMENTS_PANEL);
    design.treatmentsPanel.setCaption(resources.message(TREATMENTS_PANEL));
    design.treatments.addStyleName(TREATMENTS);
    design.treatments.addStyleName(COMPONENTS);
    design.treatments
        .addColumn(treatment -> viewTreatmentButton(treatment), new ComponentRenderer())
        .setId(TREATMENT_TYPE).setCaption(resources.message(TREATMENT_TYPE));
    design.treatments.addColumn(treatment -> dateFormatter.format(treatment.getInsertTime()))
        .setId(TREATMENT_TIME).setCaption(resources.message(TREATMENT_TIME));
    design.treatments.addColumn(treatment -> treatmentSampleCount(treatment))
        .setId(TREATMENT_SAMPLES).setCaption(resources.message(TREATMENT_SAMPLES))
        .setDescriptionGenerator(treatment -> treatmentSampleDescription(treatment));
    design.treatments.sort(TREATMENT_TIME);
    updateSubmission();
  }

  private long treatmentSampleCount(Treatment treatment) {
    Set<Long> sampleIds =
        submission.getSamples().stream().map(sa -> sa.getId()).collect(Collectors.toSet());
    return treatment.getTreatmentSamples().stream().map(ts -> ts.getSample().getId())
        .filter(id -> sampleIds.contains(id)).distinct().count();
  }

  private String treatmentSampleDescription(Treatment treatment) {
    Map<Long, SubmissionSample> sampleIds =
        submission.getSamples().stream().collect(Collectors.toMap(sa -> sa.getId(), sa -> sa));
    return treatment.getTreatmentSamples().stream().map(ts -> ts.getSample().getId())
        .filter(id -> sampleIds.containsKey(id)).distinct().map(id -> sampleIds.get(id).getName())
        .collect(Collectors.joining("\n"));
  }

  private Button viewTreatmentButton(Treatment treatment) {
    Locale locale = view.getLocale();
    Button view = new Button();
    view.addStyleName(TREATMENT_TYPE);
    view.setCaption(treatment.getType().getLabel(locale));
    view.addClickListener(e -> viewTreatment(treatment));
    return view;
  }

  private void viewTreatment(Treatment treatment) {
    switch (treatment.getType()) {
      case DIGESTION:
        view.navigateTo(DigestionView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case DILUTION:
        view.navigateTo(DilutionView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case ENRICHMENT:
        view.navigateTo(EnrichmentView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case FRACTIONATION:
        view.navigateTo(FractionationView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case STANDARD_ADDITION:
        view.navigateTo(StandardAdditionView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case SOLUBILISATION:
        view.navigateTo(SolubilisationView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      case TRANSFER:
        view.navigateTo(TransferView.VIEW_NAME, String.valueOf(treatment.getId()));
        break;
      default:
        throw new AssertionError("No view for " + treatment.getType() + " yet");
    }
  }

  private void updateSubmission() {
    if (submission != null) {
      design.samples.setItems(submission.getSamples());
    } else {
      design.samples.setItems(new ArrayList<>());
    }
    design.treatments.setItems(new ArrayList<>(treatmentService.all(submission)));
  }

  Submission getValue() {
    return submission;
  }

  void setValue(Submission submission) {
    this.submission = submission;
    updateSubmission();
  }
}
