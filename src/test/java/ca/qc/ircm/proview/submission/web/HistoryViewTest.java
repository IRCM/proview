package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.VIEW;
import static ca.qc.ircm.proview.Constants.messagePrefix;
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
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_ERROR;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.items;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.rendererTemplate;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.text.Strings.property;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.history.ActionType;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityRepository;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.MsAnalysisRepository;
import ca.qc.ircm.proview.msanalysis.MsAnalysisService;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.TreatmentRepository;
import ca.qc.ircm.proview.treatment.TreatmentService;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.web.ErrorNotification;
import ca.qc.ircm.proview.web.ViewLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.FooterRow;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.NotFoundException;
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
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Tests for {@link HistoryView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewTest extends SpringUIUnitTest {
  private static final String MESSAGES_PREFIX = messagePrefix(HistoryView.class);
  private static final String ACTIVITY_PREFIX = messagePrefix(Activity.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  private static final String ACTION_TYPE_PREFIX = messagePrefix(ActionType.class);
  private HistoryView view;
  @MockitoBean
  private ActivityService service;
  @MockitoBean
  private SubmissionService submissionService;
  @MockitoBean
  private MsAnalysisService msAnalysisService;
  @MockitoBean
  private TreatmentService treatmentService;
  @Mock
  private BeforeEvent beforeEvent;
  @Autowired
  private ActivityRepository repository;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Autowired
  private MsAnalysisRepository msAnalysisRepository;
  @Autowired
  private TreatmentRepository treatmentRepository;
  @Autowired
  private MessageSource messageSource;
  @Captor
  private ArgumentCaptor<ValueProvider<Activity, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<LitRenderer<Activity>> litRendererCaptor;
  private Locale locale = ENGLISH;
  private List<Activity> activities;

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    UI.getCurrent().setLocale(locale);
    activities = repository.findAll();
    when(service.all(any())).thenReturn(activities);
    when(submissionService.get(anyLong()))
        .thenAnswer(i -> submissionRepository.findById(i.getArgument(0)));
    view = navigate(HistoryView.class, 1L);
  }

  private int indexOfColumn(String property) {
    return test(view.activities).getColumnPosition(property);
  }

  @Test
  public void styles() {
    assertEquals(ID, view.getId().orElse(""));
    assertEquals(ACTIVITIES, view.activities.getId().orElse(""));
    assertEquals(VIEW, view.view.getId().orElse(""));
    validateIcon(VaadinIcon.EYE.create(), view.view.getIcon());
  }

  @Test
  public void labels() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment()),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    HeaderRow header = view.activities.getHeaderRows().get(0);
    FooterRow footer = view.activities.getFooterRows().get(0);
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + USER), header.getCell(view.user).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + USER), footer.getCell(view.user).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + ACTION_TYPE),
        header.getCell(view.type).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + ACTION_TYPE),
        footer.getCell(view.type).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + TIMESTAMP),
        header.getCell(view.date).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + TIMESTAMP),
        footer.getCell(view.date).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        header.getCell(view.description).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        footer.getCell(view.description).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + EXPLANATION),
        header.getCell(view.explanation).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + EXPLANATION),
        footer.getCell(view.explanation).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + VIEW), view.view.getText());
  }

  @Test
  public void localeChange() {
    Submission submission = submissionRepository.findById(1L).orElseThrow();
    Locale locale = FRENCH;
    UI.getCurrent().setLocale(locale);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, submission.getExperiment()),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
    HeaderRow header = view.activities.getHeaderRows().get(0);
    FooterRow footer = view.activities.getFooterRows().get(0);
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + USER), header.getCell(view.user).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + USER), footer.getCell(view.user).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + ACTION_TYPE),
        header.getCell(view.type).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + ACTION_TYPE),
        footer.getCell(view.type).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + TIMESTAMP),
        header.getCell(view.date).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + TIMESTAMP),
        footer.getCell(view.date).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        header.getCell(view.description).getText());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + DESCRIPTION),
        footer.getCell(view.description).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + EXPLANATION),
        header.getCell(view.explanation).getText());
    assertEquals(view.getTranslation(ACTIVITY_PREFIX + EXPLANATION),
        footer.getCell(view.explanation).getText());
    assertEquals(view.getTranslation(CONSTANTS_PREFIX + VIEW), view.view.getText());
  }

  @Test
  public void activities_Columns() {
    assertEquals(5, view.activities.getColumns().size());
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
        .thenAnswer(i -> Optional.of(descriptions.get(i.<Activity>getArgument(0))));
    view.setParameter(mock(BeforeEvent.class), 1L);
    for (int i = 0; i < activities.size(); i++) {
      Activity activity = activities.get(i);
      assertEquals(activity.getUser().getName(),
          test(view.activities).getCellText(i, indexOfColumn(USER)));
      assertEquals(view.getTranslation(ACTION_TYPE_PREFIX + activity.getActionType().name()),
          test(view.activities).getCellText(i, indexOfColumn(ACTION_TYPE)));
      assertEquals(DateTimeFormatter.ISO_DATE_TIME.format(activity.getTimestamp()),
          test(view.activities).getCellText(i, indexOfColumn(TIMESTAMP)));
      Renderer<Activity> descriptionRawRenderer =
          test(view.activities).getColumn(DESCRIPTION).getRenderer();
      assertInstanceOf(LitRenderer<Activity>.class, descriptionRawRenderer);
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
      assertInstanceOf(LitRenderer<Activity>.class, explanationRawRenderer);
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
  public void view_Enabled() {
    assertFalse(view.view.isEnabled());
    test(view.activities).select(0);
    assertTrue(view.view.isEnabled());
    view.activities.deselectAll();
    assertFalse(view.view.isEnabled());
  }

  @Test
  public void view() {
    Submission submission = submissionRepository.findById(32L).orElseThrow();
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(submission));
    when(submissionService.get(anyLong())).thenReturn(Optional.of(submission));
    when(submissionService.print(any(), any())).thenReturn("");
    test(view.activities).select(0);
    test(view.view).click();

    verify(submissionService).get(32L);
    SubmissionDialog dialog = $(SubmissionDialog.class).first();
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  public void view_NoSelection() {
    view.view();

    Notification error = $(Notification.class).first();
    assertInstanceOf(ErrorNotification.class, error);
    assertEquals(view.getTranslation(MESSAGES_PREFIX + property(ACTIVITIES, REQUIRED)),
        ((ErrorNotification) error).getText());
  }

  @Test
  public void view_Submission() {
    Submission submission = submissionRepository.findById(32L).orElseThrow();
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(submission));
    when(submissionService.get(anyLong())).thenReturn(Optional.of(submission));
    when(submissionService.print(any(), any())).thenReturn("");
    test(view.activities).doubleClickRow(0);

    verify(submissionService).get(32L);
    SubmissionDialog dialog = $(SubmissionDialog.class).first();
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  public void view_SubmissionSample() {
    Submission submission = submissionRepository.findById(32L).orElseThrow();
    SubmissionSample sample = submission.getSamples().get(0);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(sample));
    when(submissionService.get(anyLong())).thenReturn(Optional.of(submission));
    when(submissionService.print(any(), any())).thenReturn("");
    test(view.activities).doubleClickRow(0);

    verify(submissionService).get(32L);
    SubmissionDialog dialog = $(SubmissionDialog.class).first();
    assertEquals(32L, dialog.getSubmissionId());
  }

  @Test
  public void view_MsAnalysis() {
    MsAnalysis msAnalysis = msAnalysisRepository.findById(12L).orElseThrow();
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(msAnalysis));
    when(msAnalysisService.get(anyLong())).thenReturn(Optional.of(msAnalysis));
    test(view.activities).doubleClickRow(0);

    verify(msAnalysisService).get(12L);
    MsAnalysisDialog dialog = $(MsAnalysisDialog.class).first();
    assertEquals(12L, dialog.getMsAnalysisId());
  }

  @Test
  public void view_Treatment() {
    Treatment treatment = treatmentRepository.findById(6L).orElseThrow();
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(treatment));
    when(treatmentService.get(anyLong())).thenReturn(Optional.of(treatment));
    test(view.activities).doubleClickRow(0);

    verify(treatmentService).get(6L);
    TreatmentDialog dialog = $(TreatmentDialog.class).first();
    assertEquals(6L, dialog.getTreatmentId());
  }

  @Test
  public void view_Plate() {
    Plate plate = mock(Plate.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(plate));
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + VIEW_ERROR, Plate.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void view_Other() {
    Object object = mock(Object.class);
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.of(object));
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + VIEW_ERROR, Object.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void view_Empty() {
    Activity activity = items(view.activities).get(0);
    when(service.record(activity)).thenReturn(Optional.empty());
    test(view.activities).doubleClickRow(0);

    Notification notification = $(Notification.class).first();
    assertEquals(view.getTranslation(MESSAGES_PREFIX + VIEW_ERROR, Object.class.getSimpleName()),
        test(notification).getText());
  }

  @Test
  public void getPageTitle() {
    assertEquals(view.getTranslation(MESSAGES_PREFIX + TITLE,
        view.getTranslation(CONSTANTS_PREFIX + APPLICATION_NAME)), view.getPageTitle());
  }

  @Test
  public void setParameter() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    when(submissionService.get(anyLong())).thenReturn(Optional.of(submission));
    view.setParameter(beforeEvent, 12L);
    verify(submissionService).get(12L);
    assertEquals(1L, view.getSubmissionId());
    assertEquals(view.getTranslation(MESSAGES_PREFIX + HEADER, experiment),
        view.viewLayout().map(ViewLayout::getHeaderText).orElseThrow());
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(submissionService.get(anyLong())).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> view.setParameter(beforeEvent, 12L));
    verify(submissionService).get(12L);
  }
}
