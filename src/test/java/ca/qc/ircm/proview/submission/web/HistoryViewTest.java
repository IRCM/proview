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

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.history.ActivityProperties.ACTION_TYPE;
import static ca.qc.ircm.proview.history.ActivityProperties.EXPLANATION;
import static ca.qc.ircm.proview.history.ActivityProperties.TIMESTAMP;
import static ca.qc.ircm.proview.history.ActivityProperties.USER;
import static ca.qc.ircm.proview.submission.web.HistoryView.ACTIVITIES;
import static ca.qc.ircm.proview.submission.web.HistoryView.DESCRIPTION;
import static ca.qc.ircm.proview.submission.web.HistoryView.DESCRIPTION_SPAN;
import static ca.qc.ircm.proview.submission.web.HistoryView.EXPLANATION_SPAN;
import static ca.qc.ircm.proview.submission.web.HistoryView.HEADER;
import static ca.qc.ircm.proview.submission.web.HistoryView.ID;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_BUTTON;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_ERROR;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.functions;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.rendererTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityRepository;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link HistoryView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewTest extends SpringUIUnitTest {
  private HistoryView view;
  @MockBean
  private ActivityService service;
  @MockBean
  private SubmissionService submissionService;
  @Mock
  private BeforeEvent beforeEvent;
  @MockBean
  private SubmissionDialog dialog;
  @MockBean
  private MsAnalysisDialog msAnalysisDialog;
  @MockBean
  private TreatmentDialog treatmentDialog;
  @Autowired
  private ActivityRepository repository;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Captor
  private ArgumentCaptor<ValueProvider<Activity, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<LitRenderer<Activity>> litRendererCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(HistoryView.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private AppResources activityResources = new AppResources(Activity.class, locale);
  private List<Activity> activities;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    activities = repository.findAll();
    when(service.all(any())).thenReturn(activities);
    when(submissionService.get(any())).thenAnswer(i -> Optional.ofNullable((Long) i.getArgument(0))
        .map(id -> submissionRepository.findById(id)).orElse(Optional.empty()));
    view = navigate(HistoryView.class, 1L);
  }

  private int indexOfColumn(String property) {
    return test(view.activities).getColumnPosition(property);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(HEADER, view.header.getId().orElse(""));
    assertEquals(ACTIVITIES, view.activities.getId().orElse(""));
  }

  @Test
  public void labels() {
    Submission submission = submissionRepository.findById(1L).get();
    assertEquals(resources.message(HEADER, submission.getExperiment()), view.header.getText());
    HeaderRow header = view.activities.getHeaderRows().get(0);
    FooterRow footer = view.activities.getFooterRows().get(0);
    assertEquals(webResources.message(VIEW), header.getCell(view.view).getText());
    assertEquals(webResources.message(VIEW), footer.getCell(view.view).getText());
    assertEquals(activityResources.message(USER), header.getCell(view.user).getText());
    assertEquals(activityResources.message(USER), footer.getCell(view.user).getText());
    assertEquals(activityResources.message(ACTION_TYPE), header.getCell(view.type).getText());
    assertEquals(activityResources.message(ACTION_TYPE), footer.getCell(view.type).getText());
    assertEquals(activityResources.message(TIMESTAMP), header.getCell(view.date).getText());
    assertEquals(activityResources.message(TIMESTAMP), footer.getCell(view.date).getText());
    assertEquals(resources.message(DESCRIPTION), header.getCell(view.description).getText());
    assertEquals(resources.message(DESCRIPTION), footer.getCell(view.description).getText());
    assertEquals(activityResources.message(EXPLANATION),
        header.getCell(view.explanation).getText());
    assertEquals(activityResources.message(EXPLANATION),
        footer.getCell(view.explanation).getText());
  }

  @Test
  public void localeChange() {
    Submission submission = submissionRepository.findById(1L).get();
    Locale locale = FRENCH;
    final AppResources resources = new AppResources(HistoryView.class, locale);
    final AppResources activityResources = new AppResources(Activity.class, locale);
    final AppResources webResources = new AppResources(Constants.class, locale);
    UI.getCurrent().setLocale(locale);
    assertEquals(resources.message(HEADER, submission.getExperiment()), view.header.getText());
    HeaderRow header = view.activities.getHeaderRows().get(0);
    FooterRow footer = view.activities.getFooterRows().get(0);
    assertEquals(webResources.message(VIEW), header.getCell(view.view).getText());
    assertEquals(webResources.message(VIEW), footer.getCell(view.view).getText());
    assertEquals(activityResources.message(USER), header.getCell(view.user).getText());
    assertEquals(activityResources.message(USER), footer.getCell(view.user).getText());
    assertEquals(activityResources.message(ACTION_TYPE), header.getCell(view.type).getText());
    assertEquals(activityResources.message(ACTION_TYPE), footer.getCell(view.type).getText());
    assertEquals(activityResources.message(TIMESTAMP), header.getCell(view.date).getText());
    assertEquals(activityResources.message(TIMESTAMP), footer.getCell(view.date).getText());
    assertEquals(resources.message(DESCRIPTION), header.getCell(view.description).getText());
    assertEquals(resources.message(DESCRIPTION), footer.getCell(view.description).getText());
    assertEquals(activityResources.message(EXPLANATION),
        header.getCell(view.explanation).getText());
    assertEquals(activityResources.message(EXPLANATION),
        footer.getCell(view.explanation).getText());
  }

  @Test
  public void activities_Columns() {
    assertEquals(6, view.activities.getColumns().size());
    assertNotNull(view.activities.getColumnByKey(VIEW));
    assertFalse(view.activities.getColumnByKey(VIEW).isSortable());
    assertNotNull(view.activities.getColumnByKey(USER));
    assertTrue(view.activities.getColumnByKey(USER).isSortable());
    assertNotNull(view.activities.getColumnByKey(ACTION_TYPE));
    assertTrue(view.activities.getColumnByKey(ACTION_TYPE).isSortable());
    assertNotNull(view.activities.getColumnByKey(TIMESTAMP));
    assertTrue(view.activities.getColumnByKey(TIMESTAMP).isSortable());
    assertNotNull(view.activities.getColumnByKey(DESCRIPTION));
    assertFalse(view.activities.getColumnByKey(DESCRIPTION).isSortable());
    assertNotNull(view.activities.getColumnByKey(EXPLANATION));
    assertFalse(view.activities.getColumnByKey(EXPLANATION).isSortable());
  }

  @Test
  public void activities_ColumnsValueProvider() {
    Map<Activity, String> descriptions = IntStream.range(0, activities.size()).boxed()
        .collect(Collectors.toMap(in -> activities.get(in), in -> "description " + in));
    when(service.description(any(), any()))
        .thenAnswer(i -> Optional.of(descriptions.get(i.getArgument(0))));
    view.setParameter(mock(BeforeEvent.class), 1L);
    for (int i = 0; i < activities.size(); i++) {
      Activity activity = activities.get(i);
      Renderer<Activity> viewRawRenderer = test(view.activities).getColumn(VIEW).getRenderer();
      assertTrue(viewRawRenderer instanceof LitRenderer<Activity>);
      LitRenderer<Activity> viewRenderer = (LitRenderer<Activity>) viewRawRenderer;
      assertEquals(VIEW_BUTTON, rendererTemplate(viewRenderer));
      assertTrue(functions(viewRenderer).containsKey("view"));
      functions(viewRenderer).get("view").accept(activity, null);
      // TODO Test view
      assertEquals(activity.getUser().getName(),
          test(view.activities).getCellText(i, indexOfColumn(USER)));
      assertEquals(activity.getActionType().getLabel(locale),
          test(view.activities).getCellText(i, indexOfColumn(ACTION_TYPE)));
      assertEquals(DateTimeFormatter.ISO_DATE_TIME.format(activity.getTimestamp()),
          test(view.activities).getCellText(i, indexOfColumn(TIMESTAMP)));
      Renderer<Activity> descriptionRawRenderer =
          test(view.activities).getColumn(DESCRIPTION).getRenderer();
      assertTrue(descriptionRawRenderer instanceof LitRenderer<Activity>);
      LitRenderer<Activity> descriptionRenderer = (LitRenderer<Activity>) descriptionRawRenderer;
      assertEquals(DESCRIPTION_SPAN, rendererTemplate(descriptionRenderer));
      assertTrue(descriptionRenderer.getValueProviders().containsKey("descriptionValue"));
      assertEquals(descriptions.get(activity),
          descriptionRenderer.getValueProviders().get("descriptionValue").apply(activity));
      assertTrue(descriptionRenderer.getValueProviders().containsKey("descriptionTitle"));
      assertEquals(descriptions.get(activity),
          descriptionRenderer.getValueProviders().get("descriptionTitle").apply(activity));
      verify(service, times(2)).description(activity, locale);
      Renderer<Activity> explanationRawRenderer =
          test(view.activities).getColumn(EXPLANATION).getRenderer();
      assertTrue(explanationRawRenderer instanceof LitRenderer<Activity>);
      LitRenderer<Activity> explanationRenderer = (LitRenderer<Activity>) explanationRawRenderer;
      assertEquals(EXPLANATION_SPAN, rendererTemplate(explanationRenderer));
      assertTrue(explanationRenderer.getValueProviders().containsKey("explanationValue"));
      assertEquals(activity.getExplanation(),
          explanationRenderer.getValueProviders().get("explanationValue").apply(activity));
      assertTrue(explanationRenderer.getValueProviders().containsKey("explanationTitle"));
      assertEquals(activity.getExplanation(),
          explanationRenderer.getValueProviders().get("explanationTitle").apply(activity));
    }
  }

  @Test
  public void view_Submission() {
    Submission submission = mock(Submission.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(submission));
    test(view.activities).doubleClickRow(0);

    verify(dialog).setSubmissionId(submission.getId());
    verify(dialog).open();
  }

  @Test
  public void view_SubmissionSample() {
    Submission submission = mock(Submission.class);
    SubmissionSample sample = mock(SubmissionSample.class);
    when(sample.getSubmission()).thenReturn(submission);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(sample));
    test(view.activities).doubleClickRow(0);

    verify(dialog).setSubmissionId(submission.getId());
    verify(dialog).open();
  }

  @Test
  public void view_MsAnalysis() {
    MsAnalysis msAnalysis = mock(MsAnalysis.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(msAnalysis));
    test(view.activities).doubleClickRow(0);

    verify(msAnalysisDialog).setMsAnalysisId(msAnalysis.getId());
    verify(msAnalysisDialog).open();
  }

  @Test
  public void view_Treatment() {
    Treatment treatment = mock(Treatment.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(treatment));
    test(view.activities).doubleClickRow(0);

    verify(treatmentDialog).setTreatmentId(treatment.getId());
    verify(treatmentDialog).open();
  }

  @Test
  public void view_Plate() {
    Plate plate = mock(Plate.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(plate));
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(VIEW_ERROR, Plate.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void view_Other() {
    Object object = mock(Object.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(object));
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(VIEW_ERROR, Object.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void view_Empty() {
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.empty());
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(resources.message(VIEW_ERROR, Object.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(resources.message(TITLE, webResources.message(APPLICATION_NAME)),
        view.getPageTitle());
  }

  @Test
  public void setParameter() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(submissionService.get(any())).thenReturn(Optional.of(submission));
    view.setParameter(beforeEvent, 12L);
    verify(submissionService).get(12L);
    assertEquals(1L, view.getSubmissionId());
    assertEquals(resources.message(HEADER, experiment), view.header.getText());
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(submissionService.get(any())).thenReturn(Optional.empty());
    view.setParameter(beforeEvent, 12L);
    verify(submissionService).get(12L);
    assertNull(view.getSubmissionId());
    assertEquals(resources.message(HEADER, ""), view.header.getText());
  }

  @Test
  public void setParameter_Null() {
    view.setParameter(beforeEvent, null);
    Submission submission = submissionRepository.findById(1L).get();
    assertEquals(1L, view.getSubmissionId());
    assertEquals(resources.message(HEADER, submission.getExperiment()), view.header.getText());
  }
}
