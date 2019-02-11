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

import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;
import static ca.qc.ircm.proview.sample.SampleProperties.NAME;
import static ca.qc.ircm.proview.sample.SubmissionSampleProperties.STATUS;
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;
import static ca.qc.ircm.proview.vaadin.VaadinUtils.property;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;

import ca.qc.ircm.proview.dataanalysis.DataAnalysis;
import ca.qc.ircm.proview.digestion.Digestion;
import ca.qc.ircm.proview.digestion.web.DigestionView;
import ca.qc.ircm.proview.dilution.Dilution;
import ca.qc.ircm.proview.dilution.web.DilutionView;
import ca.qc.ircm.proview.enrichment.Enrichment;
import ca.qc.ircm.proview.enrichment.web.EnrichmentView;
import ca.qc.ircm.proview.fractionation.Fractionation;
import ca.qc.ircm.proview.fractionation.web.FractionationView;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisView;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.web.PlateView;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.web.SampleView;
import ca.qc.ircm.proview.solubilisation.Solubilisation;
import ca.qc.ircm.proview.solubilisation.web.SolubilisationView;
import ca.qc.ircm.proview.standard.StandardAddition;
import ca.qc.ircm.proview.standard.web.StandardAdditionView;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.transfer.Transfer;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.renderers.ComponentRenderer;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Provider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

/**
 * Submission history form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionHistoryFormPresenter {
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLES = "samples";
  public static final String SAMPLE_NAME = NAME;
  public static final String SAMPLE_STATUS = STATUS;
  public static final String LAST_CONTAINER = "lastContainer";
  public static final String ACTIVITIES_PANEL = "activitiesPanel";
  public static final String ACTIVITIES = "activities";
  public static final String DESCRIPTION = "description";
  public static final String DESCRIPTION_LONG = property(DESCRIPTION, "long");
  public static final String VIEW = "view";
  public static final String VIEW_ERROR = "view.error";
  private SubmissionHistoryForm view;
  private SubmissionHistoryFormDesign design;
  private Submission submission;
  @Inject
  private ActivityService activityService;
  @Inject
  private SampleContainerService sampleContainerService;
  @Inject
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;

  protected SubmissionHistoryFormPresenter() {
  }

  protected SubmissionHistoryFormPresenter(ActivityService activityService,
      SampleContainerService sampleContainerService,
      Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider) {
    this.activityService = activityService;
    this.sampleContainerService = sampleContainerService;
    this.submissionAnalysesWindowProvider = submissionAnalysesWindowProvider;
  }

  /**
   * Initializes presenter.
   *
   * @param view
   *          view
   */
  public void init(SubmissionHistoryForm view) {
    this.view = view;
    design = view.design;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    design.samplesPanel.addStyleName(SAMPLES_PANEL);
    design.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    design.samples.addStyleName(SAMPLES);
    prepareSamplesGrid();
    design.activitiesPanel.addStyleName(ACTIVITIES_PANEL);
    design.activitiesPanel.setCaption(resources.message(ACTIVITIES_PANEL));
    design.activities.addStyleName(ACTIVITIES);
    design.activities.addStyleName(COMPONENTS);
    prepareActivitiesGrid();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    design.samples.addColumn(sa -> sa.getName()).setId(SAMPLE_NAME)
        .setCaption(resources.message(SAMPLE_NAME));
    design.samples.addColumn(sa -> sa.getStatus().getLabel(locale)).setId(SAMPLE_STATUS)
        .setCaption(resources.message(SAMPLE_STATUS));
    design.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(LAST_CONTAINER).setCaption(resources.message(LAST_CONTAINER));
  }

  private void prepareActivitiesGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    design.activities.addColumn(ac -> ac.getUser().getName()).setId(USER)
        .setCaption(resources.message(USER)).setDescriptionGenerator(ac -> ac.getUser().getEmail());
    design.activities.addColumn(ac -> ac.getActionType().getLabel(locale)).setId(ACTION_TYPE)
        .setCaption(resources.message(ACTION_TYPE));
    design.activities.addColumn(ac -> formatter.format(toLocalDateTime(ac.getTimestamp())))
        .setId(TIMESTAMP).setCaption(resources.message(TIMESTAMP));
    design.activities
        .addColumn(ac -> activityDescription(activityService.description(ac, locale), resources))
        .setId(DESCRIPTION).setCaption(resources.message(DESCRIPTION))
        .setDescriptionGenerator(ac -> activityService.description(ac, locale));
    design.activities.addColumn(ac -> ac.getExplanation()).setId(EXPLANATION)
        .setCaption(resources.message(EXPLANATION));
    design.activities.addColumn(ac -> viewButton(ac), new ComponentRenderer()).setId(VIEW)
        .setCaption(resources.message(VIEW));
    design.activities.sort(TIMESTAMP, SortDirection.DESCENDING);
  }

  private String activityDescription(String description, MessageResource resources) {
    String firstLine =
        Pattern.compile("\\n.*", Pattern.DOTALL).matcher(description).replaceFirst("");
    if (!description.equals(firstLine)) {
      return resources.message(DESCRIPTION_LONG, firstLine);
    } else {
      return description;
    }
  }

  private Button viewButton(Activity ac) {
    MessageResource resources = view.getResources();
    Button button = new Button();
    button.addStyleName(VIEW);
    button.setCaption(resources.message(VIEW));
    button.addClickListener(e -> view(ac));
    return button;
  }

  private void view(Activity ac) {
    MessageResource resources = view.getResources();
    Object record = activityService.record(ac);
    if (record instanceof Submission) {
      view.navigateTo(SubmissionView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Sample) {
      view.navigateTo(SampleView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Plate) {
      view.navigateTo(PlateView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Digestion) {
      view.navigateTo(DigestionView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Dilution) {
      view.navigateTo(DilutionView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Enrichment) {
      view.navigateTo(EnrichmentView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Fractionation) {
      view.navigateTo(FractionationView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Solubilisation) {
      view.navigateTo(SolubilisationView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof StandardAddition) {
      view.navigateTo(StandardAdditionView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof Transfer) {
      view.navigateTo(TransferView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof MsAnalysis) {
      view.navigateTo(MsAnalysisView.VIEW_NAME, Objects.toString(ac.getRecordId()));
    } else if (record instanceof DataAnalysis) {
      SubmissionAnalysesWindow window = submissionAnalysesWindowProvider.get();
      window.setValue(submission);
      window.center();
      view.addWindow(window);
    } else {
      view.showWarning(resources.message(VIEW_ERROR, record.getClass().getSimpleName()));
    }
  }

  Submission getValue() {
    return submission;
  }

  void setValue(Submission submission) {
    this.submission = submission;
    design.samples.setItems(submission != null ? submission.getSamples() : Collections.emptyList());
    design.activities.setItems(activityService.all(submission));
  }
}
