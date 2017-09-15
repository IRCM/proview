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

import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITIES;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITIES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_ACTION_TYPE;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_DESCRIPTION_LONG;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_JUSTIFICATION;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_TIMESTAMP;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.ACTIVITY_USER;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLE_LAST_CONTAINER;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.plate.PlateSpot;
import ca.qc.ircm.proview.sample.SampleContainerService;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SampleSupport;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.time.TimeConverter;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.renderers.ComponentRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionHistoryFormPresenterTest implements TimeConverter {
  private SubmissionHistoryFormPresenter presenter;
  @Mock
  private SubmissionHistoryForm view;
  @Mock
  private ActivityService activityService;
  @Mock
  private SampleContainerService sampleContainerService;
  private Locale locale = Locale.FRENCH;
  private MessageResource resources = new MessageResource(SubmissionHistoryForm.class, locale);
  private MessageResource generalResources =
      new MessageResource(WebConstants.GENERAL_MESSAGES, locale);
  private SubmissionSample sample1;
  private SubmissionSample sample2;
  private Submission submission;
  private Tube tube1;
  private Tube tube2;
  private Tube last1;
  private PlateSpot last2;
  private Activity activity1;
  private Activity activity2;
  private String activityDescription1;
  private String activityDescription2;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionHistoryFormPresenter(activityService, sampleContainerService);
    view.samplesPanel = new Panel();
    view.samples = new Grid<>();
    view.activitiesPanel = new Panel();
    view.activities = new Grid<>();
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
    last1 = new Tube();
    last1.setName("sample1 last tube");
    last2 = new PlateSpot(1, 2);
    last2.setPlate(new Plate(10L, "sample2 last plate"));
    when(sampleContainerService.last(sample1)).thenReturn(last1);
    when(sampleContainerService.last(sample2)).thenReturn(last2);
    activity1 = new Activity();
    activity1.setUser(new User(1L, "test@ircm.qc.ca"));
    activity1.setActionType(ActionType.INSERT);
    activity1.setTimestamp(Instant.now().minus(2, ChronoUnit.DAYS).minus(1, ChronoUnit.HOURS));
    activity2 = new Activity();
    activity2.setUser(new User(1L, "test@ircm.qc.ca"));
    activity2.setActionType(ActionType.UPDATE);
    activity2.setTimestamp(Instant.now());
    activity2.setJustification("test_justification");
    when(activityService.all(any(Submission.class)))
        .thenReturn(Arrays.asList(activity1, activity2));
    activityDescription1 = "description 1";
    activityDescription2 = "description 2\nAnother line";
    when(activityService.description(activity1, submission, locale))
        .thenReturn(activityDescription1);
    when(activityService.description(activity2, submission, locale))
        .thenReturn(activityDescription2);
  }

  @SuppressWarnings("unchecked")
  private <T> ListDataProvider<T> dataProvider(Grid<T> grid) {
    return (ListDataProvider<T>) grid.getDataProvider();
  }

  private boolean containsInstanceOf(Collection<?> objects, Class<?> clazz) {
    return objects.stream().filter(o -> clazz.isAssignableFrom(o.getClass())).findAny().isPresent();
  }

  @Test
  public void styles() {
    presenter.init(view);
    presenter.setBean(submission);

    assertTrue(view.samplesPanel.getStyleName().contains(SAMPLES_PANEL));
    assertTrue(view.samples.getStyleName().contains(SAMPLES));
    assertTrue(view.activitiesPanel.getStyleName().contains(ACTIVITIES_PANEL));
    assertTrue(view.activities.getStyleName().contains(ACTIVITIES));
  }

  @Test
  public void captions() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(resources.message(SAMPLES_PANEL), view.samplesPanel.getCaption());
    assertEquals(resources.message(ACTIVITIES_PANEL), view.activitiesPanel.getCaption());
  }

  @Test
  public void samplesGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    assertEquals(2, view.samples.getColumns().size());
    assertEquals(SAMPLE_NAME, view.samples.getColumns().get(0).getId());
    assertEquals(SAMPLE_LAST_CONTAINER, view.samples.getColumns().get(1).getId());
    assertEquals(resources.message(SAMPLE_NAME), view.samples.getColumn(SAMPLE_NAME).getCaption());
    assertEquals(sample1.getName(),
        view.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample1));
    assertEquals(sample2.getName(),
        view.samples.getColumn(SAMPLE_NAME).getValueProvider().apply(sample2));
    assertEquals(resources.message(SAMPLE_LAST_CONTAINER),
        view.samples.getColumn(SAMPLE_LAST_CONTAINER).getCaption());
    assertEquals(last1.getFullName(),
        view.samples.getColumn(SAMPLE_LAST_CONTAINER).getValueProvider().apply(sample1));
    assertEquals(last2.getFullName(),
        view.samples.getColumn(SAMPLE_LAST_CONTAINER).getValueProvider().apply(sample2));

    Collection<SubmissionSample> samples = dataProvider(view.samples).getItems();
    assertEquals(2, samples.size());
    assertTrue(samples.contains(sample1));
    assertTrue(samples.contains(sample2));
  }

  @Test
  public void activitiesGrid() {
    presenter.init(view);
    presenter.setBean(submission);

    final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    assertEquals(5, view.activities.getColumns().size());
    assertEquals(ACTIVITY_USER, view.activities.getColumns().get(0).getId());
    assertEquals(ACTIVITY_ACTION_TYPE, view.activities.getColumns().get(1).getId());
    assertEquals(ACTIVITY_TIMESTAMP, view.activities.getColumns().get(2).getId());
    assertEquals(ACTIVITY_DESCRIPTION, view.activities.getColumns().get(3).getId());
    assertEquals(ACTIVITY_JUSTIFICATION, view.activities.getColumns().get(4).getId());
    assertEquals(resources.message(ACTIVITY_USER),
        view.activities.getColumn(ACTIVITY_USER).getCaption());
    assertEquals(activity1.getUser().getEmail(),
        view.activities.getColumn(ACTIVITY_USER).getValueProvider().apply(activity1));
    assertEquals(activity2.getUser().getEmail(),
        view.activities.getColumn(ACTIVITY_USER).getValueProvider().apply(activity2));
    assertEquals(resources.message(ACTIVITY_ACTION_TYPE),
        view.activities.getColumn(ACTIVITY_ACTION_TYPE).getCaption());
    assertEquals(activity1.getActionType().getLabel(locale),
        view.activities.getColumn(ACTIVITY_ACTION_TYPE).getValueProvider().apply(activity1));
    assertEquals(activity2.getActionType().getLabel(locale),
        view.activities.getColumn(ACTIVITY_ACTION_TYPE).getValueProvider().apply(activity2));
    assertEquals(resources.message(ACTIVITY_TIMESTAMP),
        view.activities.getColumn(ACTIVITY_TIMESTAMP).getCaption());
    assertEquals(dateFormatter.format(toLocalDateTime(activity1.getTimestamp())),
        view.activities.getColumn(ACTIVITY_TIMESTAMP).getValueProvider().apply(activity1));
    assertEquals(dateFormatter.format(toLocalDateTime(activity2.getTimestamp())),
        view.activities.getColumn(ACTIVITY_TIMESTAMP).getValueProvider().apply(activity2));
    assertEquals(resources.message(ACTIVITY_DESCRIPTION),
        view.activities.getColumn(ACTIVITY_DESCRIPTION).getCaption());
    assertTrue(containsInstanceOf(view.activities.getColumn(ACTIVITY_DESCRIPTION).getExtensions(),
        ComponentRenderer.class));
    Label descriptionLabel =
        (Label) view.activities.getColumn(ACTIVITY_DESCRIPTION).getValueProvider().apply(activity1);
    assertEquals(activityDescription1, descriptionLabel.getValue());
    assertEquals(activityDescription1, descriptionLabel.getDescription());
    descriptionLabel =
        (Label) view.activities.getColumn(ACTIVITY_DESCRIPTION).getValueProvider().apply(activity2);
    assertEquals(
        resources.message(ACTIVITY_DESCRIPTION_LONG,
            activityDescription2.substring(0, activityDescription2.indexOf("\n"))),
        descriptionLabel.getValue());
    assertEquals(activityDescription2, descriptionLabel.getDescription());
    assertEquals(resources.message(ACTIVITY_JUSTIFICATION),
        view.activities.getColumn(ACTIVITY_JUSTIFICATION).getCaption());
    assertEquals(activity1.getJustification(),
        view.activities.getColumn(ACTIVITY_JUSTIFICATION).getValueProvider().apply(activity1));
    assertEquals(activity2.getJustification(),
        view.activities.getColumn(ACTIVITY_JUSTIFICATION).getValueProvider().apply(activity2));

    verify(activityService).all(submission);
    Collection<Activity> activities = dataProvider(view.activities).getItems();
    assertEquals(2, activities.size());
    assertTrue(activities.contains(activity1));
    assertTrue(activities.contains(activity2));
    List<GridSortOrder<Activity>> sortOrders = view.activities.getSortOrder();
    assertEquals(1, sortOrders.size());
    GridSortOrder<Activity> sortOrder = sortOrders.get(0);
    assertEquals(ACTIVITY_TIMESTAMP, sortOrder.getSorted().getId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }
}
