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

import static ca.qc.ircm.proview.history.QActivity.activity;
import static ca.qc.ircm.proview.sample.QSubmissionSample.submissionSample;

import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.time.TimeConverter;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Submission history form presenter.
 */
@Controller
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SubmissionHistoryFormPresenter implements TimeConverter {
  public static final String SAMPLES_PANEL = "samplesPanel";
  public static final String SAMPLES = "samples";
  public static final String SAMPLE_NAME =
      SAMPLES + "." + submissionSample.name.getMetadata().getName();
  public static final String SAMPLE_LAST_CONTAINER = SAMPLES + "." + "lastContainer";
  public static final String ACTIVITIES_PANEL = "activitiesPanel";
  public static final String ACTIVITIES = "activities";
  public static final String ACTIVITY_USER =
      ACTIVITIES + "." + activity.user.getMetadata().getName();
  public static final String ACTIVITY_ACTION_TYPE =
      ACTIVITIES + "." + activity.actionType.getMetadata().getName();
  public static final String ACTIVITY_TIMESTAMP =
      ACTIVITIES + "." + activity.timestamp.getMetadata().getName();
  public static final String ACTIVITY_DESCRIPTION = ACTIVITIES + "." + "description";
  public static final String ACTIVITY_DESCRIPTION_LONG = ACTIVITY_DESCRIPTION + ".long";
  public static final String ACTIVITY_EXPLANATION =
      ACTIVITIES + "." + activity.explanation.getMetadata().getName();
  private SubmissionHistoryForm view;
  private Submission submission;
  @Inject
  private ActivityService activityService;
  @Inject
  private SampleContainerService sampleContainerService;

  protected SubmissionHistoryFormPresenter() {
  }

  protected SubmissionHistoryFormPresenter(ActivityService activityService,
      SampleContainerService sampleContainerService) {
    this.activityService = activityService;
    this.sampleContainerService = sampleContainerService;
  }

  public void init(SubmissionHistoryForm view) {
    this.view = view;
    prepareComponents();
  }

  private void prepareComponents() {
    MessageResource resources = view.getResources();
    view.samplesPanel.addStyleName(SAMPLES_PANEL);
    view.samplesPanel.setCaption(resources.message(SAMPLES_PANEL));
    view.samples.addStyleName(SAMPLES);
    prepareSamplesGrid();
    view.activitiesPanel.addStyleName(ACTIVITIES_PANEL);
    view.activitiesPanel.setCaption(resources.message(ACTIVITIES_PANEL));
    view.activities.addStyleName(ACTIVITIES);
    prepareActivitiesGrid();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    view.samples.addColumn(sa -> sa.getName()).setId(SAMPLE_NAME)
        .setCaption(resources.message(SAMPLE_NAME));
    view.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(SAMPLE_LAST_CONTAINER).setCaption(resources.message(SAMPLE_LAST_CONTAINER));
  }

  private void prepareActivitiesGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    view.activities.addColumn(ac -> ac.getUser().getEmail()).setId(ACTIVITY_USER)
        .setCaption(resources.message(ACTIVITY_USER));
    view.activities.addColumn(ac -> ac.getActionType().getLabel(locale)).setId(ACTIVITY_ACTION_TYPE)
        .setCaption(resources.message(ACTIVITY_ACTION_TYPE));
    view.activities.addColumn(ac -> formatter.format(toLocalDateTime(ac.getTimestamp())))
        .setId(ACTIVITY_TIMESTAMP).setCaption(resources.message(ACTIVITY_TIMESTAMP));
    view.activities
        .addColumn(
            ac -> descriptionLabel(activityService.description(ac, submission, locale), resources),
            new ComponentRenderer())
        .setId(ACTIVITY_DESCRIPTION).setCaption(resources.message(ACTIVITY_DESCRIPTION));
    view.activities.addColumn(ac -> ac.getExplanation()).setId(ACTIVITY_EXPLANATION)
        .setCaption(resources.message(ACTIVITY_EXPLANATION));
    view.activities.sort(ACTIVITY_TIMESTAMP, SortDirection.DESCENDING);
  }

  private Label descriptionLabel(String description, MessageResource resources) {
    String firstLine =
        Pattern.compile("\\n.*", Pattern.DOTALL).matcher(description).replaceFirst("");
    Label label = new Label(firstLine);
    if (!description.equals(firstLine)) {
      label.setValue(resources.message(ACTIVITY_DESCRIPTION_LONG, firstLine));
    }
    label.setDescription(description);
    return label;
  }

  Submission getValue() {
    return submission;
  }

  void setValue(Submission submission) {
    this.submission = submission;
    view.samples.setItems(submission != null ? submission.getSamples() : Collections.emptyList());
    view.activities.setItems(activityService.all(submission));
  }
}
