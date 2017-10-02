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
import static ca.qc.ircm.proview.time.TimeConverter.toLocalDateTime;

import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.shared.data.sort.SortDirection;
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
public class SubmissionHistoryFormPresenter {
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
  private SubmissionHistoryFormDesign design;
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
    prepareActivitiesGrid();
  }

  private void prepareSamplesGrid() {
    MessageResource resources = view.getResources();
    design.samples.addColumn(sa -> sa.getName()).setId(SAMPLE_NAME)
        .setCaption(resources.message(SAMPLE_NAME));
    design.samples.addColumn(sa -> sampleContainerService.last(sa).getFullName())
        .setId(SAMPLE_LAST_CONTAINER).setCaption(resources.message(SAMPLE_LAST_CONTAINER));
  }

  private void prepareActivitiesGrid() {
    MessageResource resources = view.getResources();
    Locale locale = view.getLocale();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    design.activities.addColumn(ac -> ac.getUser().getName()).setId(ACTIVITY_USER)
        .setCaption(resources.message(ACTIVITY_USER))
        .setDescriptionGenerator(ac -> ac.getUser().getEmail());
    design.activities.addColumn(ac -> ac.getActionType().getLabel(locale))
        .setId(ACTIVITY_ACTION_TYPE).setCaption(resources.message(ACTIVITY_ACTION_TYPE));
    design.activities.addColumn(ac -> formatter.format(toLocalDateTime(ac.getTimestamp())))
        .setId(ACTIVITY_TIMESTAMP).setCaption(resources.message(ACTIVITY_TIMESTAMP));
    design.activities
        .addColumn(ac -> activityDescription(activityService.description(ac, submission, locale),
            resources))
        .setId(ACTIVITY_DESCRIPTION).setCaption(resources.message(ACTIVITY_DESCRIPTION))
        .setDescriptionGenerator(ac -> activityService.description(ac, submission, locale));
    design.activities.addColumn(ac -> ac.getExplanation()).setId(ACTIVITY_EXPLANATION)
        .setCaption(resources.message(ACTIVITY_EXPLANATION));
    design.activities.sort(ACTIVITY_TIMESTAMP, SortDirection.DESCENDING);
  }

  private String activityDescription(String description, MessageResource resources) {
    String firstLine =
        Pattern.compile("\\n.*", Pattern.DOTALL).matcher(description).replaceFirst("");
    if (!description.equals(firstLine)) {
      return resources.message(ACTIVITY_DESCRIPTION_LONG, firstLine);
    } else {
      return description;
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
