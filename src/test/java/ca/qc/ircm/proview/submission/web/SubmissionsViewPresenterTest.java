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

import static ca.qc.ircm.proview.submission.QSubmission.submission;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.COLUMN_ORDER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.CONDITION_FALSE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE_GOAL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HISTORY;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES_SEPARATOR;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_CONTAINERS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_CONTAINERS_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_CONTAINERS_NO_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TITLE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TRANSFER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TRANSFER_NO_CONTAINERS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TREATMENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.USER;
import static ca.qc.ircm.proview.test.utils.SearchUtils.containsInstanceOf;
import static ca.qc.ircm.proview.test.utils.SearchUtils.find;
import static ca.qc.ircm.proview.test.utils.TestBenchUtils.dataProvider;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleContainer;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.ContainerSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.transfer.web.TransferView;
import ca.qc.ircm.proview.tube.Tube;
import ca.qc.ircm.proview.user.UserPreferenceService;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.filter.LocalDateFilterComponent;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.data.SelectionModel;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionsViewPresenterTest {
  private SubmissionsViewPresenter presenter;
  @PersistenceContext
  private EntityManager entityManager;
  @Inject
  private JPAQueryFactory queryFactory;
  @Mock
  private SubmissionService submissionService;
  @Mock
  private AuthorizationService authorizationService;
  @Mock
  private UserPreferenceService userPreferenceService;
  @Mock
  private Provider<LocalDateFilterComponent> localDateFilterComponentProvider;
  @Mock
  private Provider<SubmissionWindow> submissionWindowProvider;
  @Mock
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;
  @Mock
  private Provider<SubmissionTreatmentsWindow> submissionTreatmentsWindowProvider;
  @Mock
  private Provider<SubmissionHistoryWindow> submissionHistoryWindowProvider;
  @Mock
  private Provider<SampleSelectionWindow> sampleSelectionWindowProvider;
  @Mock
  private Provider<ContainerSelectionWindow> containerSelectionWindowProvider;
  @Mock
  private SubmissionsView view;
  @Mock
  private SubmissionWindow submissionWindow;
  @Mock
  private SubmissionAnalysesWindow submissionAnalysesWindow;
  @Mock
  private SubmissionTreatmentsWindow submissionTreatmentsWindow;
  @Mock
  private SubmissionHistoryWindow submissionHistoryWindow;
  @Mock
  private SampleSelectionWindow sampleSelectionWindow;
  @Mock
  private ContainerSelectionWindow containerSelectionWindow;
  @Mock
  private ListDataProvider<Submission> submissionsDataProvider;
  @Mock
  private LocalDateFilterComponent localDateFilterComponent;
  @Captor
  private ArgumentCaptor<Collection<Sample>> samplesCaptor;
  @Captor
  private ArgumentCaptor<List<Sample>> samplesListCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<List<Sample>>> samplesSaveListenerCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<List<SampleContainer>>> containersSaveListenerCaptor;
  @Captor
  private ArgumentCaptor<SaveListener<Range<LocalDate>>> localDateRangeSaveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private SubmissionsViewDesign design;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionsView.class, locale);
  private List<Submission> submissions;
  private Map<Submission, Boolean> linkedToResults;

  /**
   * Before test.
   */
  @Before
  public void beforeTest() {
    presenter = new SubmissionsViewPresenter(submissionService, authorizationService,
        userPreferenceService, localDateFilterComponentProvider, submissionWindowProvider,
        submissionAnalysesWindowProvider, submissionTreatmentsWindowProvider,
        submissionHistoryWindowProvider, sampleSelectionWindowProvider,
        containerSelectionWindowProvider, applicationName);
    design = new SubmissionsViewDesign();
    view.design = design;
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    submissions = queryFactory.select(submission).from(submission).fetch();
    linkedToResults = new HashMap<>();
    for (Submission submission : submissions.subList(0, 2)) {
      linkedToResults.put(submission, true);
    }
    for (Submission submission : submissions.subList(2, submissions.size())) {
      linkedToResults.put(submission, false);
    }
    when(submissionService.all()).thenReturn(submissions);
    when(userPreferenceService.get(any(), any(), any())).thenAnswer(i -> i.getArguments()[2]);
    when(localDateFilterComponentProvider.get()).thenReturn(localDateFilterComponent);
    when(submissionWindowProvider.get()).thenReturn(submissionWindow);
    when(submissionAnalysesWindowProvider.get()).thenReturn(submissionAnalysesWindow);
    when(submissionTreatmentsWindowProvider.get()).thenReturn(submissionTreatmentsWindow);
    when(submissionHistoryWindowProvider.get()).thenReturn(submissionHistoryWindow);
    when(sampleSelectionWindowProvider.get()).thenReturn(sampleSelectionWindow);
    when(containerSelectionWindowProvider.get()).thenReturn(containerSelectionWindow);
  }

  private String statusesValue(Submission submission) {
    return submission.getSamples().stream().map(s -> s.getStatus()).distinct().sorted()
        .map(s -> s.getLabel(locale))
        .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR)));
  }

  @Test
  public void submissionsGrid() {
    presenter.init(view);
    final Submission submission = submissions.get(0);

    final List<Column<Submission, ?>> columns = design.submissionsGrid.getColumns();
    final List<GridSortOrder<Submission>> sortOrders = design.submissionsGrid.getSortOrder();

    assertEquals(EXPERIENCE, columns.get(0).getId());
    assertTrue(containsInstanceOf(columns.get(0).getExtensions(), ComponentRenderer.class));
    assertEquals(resources.message(EXPERIENCE),
        design.submissionsGrid.getColumn(EXPERIENCE).getCaption());
    Button experienceButton =
        (Button) design.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertTrue(experienceButton.getStyleName().contains(EXPERIENCE));
    assertEquals(submission.getExperience(), experienceButton.getCaption());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidden());
    assertEquals(USER, columns.get(1).getId());
    assertEquals(resources.message(USER), design.submissionsGrid.getColumn(USER).getCaption());
    assertEquals(submission.getUser().getName(),
        design.submissionsGrid.getColumn(USER).getValueProvider().apply(submission));
    assertEquals(submission.getUser().getEmail(),
        design.submissionsGrid.getColumn(USER).getDescriptionGenerator().apply(submission));
    assertFalse(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidden());
    assertEquals(SAMPLE_COUNT, columns.get(2).getId());
    assertEquals(resources.message(SAMPLE_COUNT),
        design.submissionsGrid.getColumn(SAMPLE_COUNT).getCaption());
    assertEquals(submission.getSamples().size(),
        design.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertEquals(SAMPLE_NAME, columns.get(3).getId());
    assertEquals(resources.message(SAMPLE_NAME),
        design.submissionsGrid.getColumn(SAMPLE_NAME).getCaption());
    assertEquals(submission.getSamples().get(0).getName(),
        design.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertEquals(EXPERIENCE_GOAL, columns.get(4).getId());
    assertEquals(resources.message(EXPERIENCE_GOAL),
        design.submissionsGrid.getColumn(EXPERIENCE_GOAL).getCaption());
    assertEquals(submission.getGoal(),
        design.submissionsGrid.getColumn(EXPERIENCE_GOAL).getValueProvider().apply(submission));
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidable());
    assertEquals(SAMPLE_STATUSES, columns.get(5).getId());
    assertEquals(resources.message(SAMPLE_STATUSES),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getCaption());
    assertEquals(
        submission.getSamples().stream().map(sample -> sample.getStatus()).distinct()
            .map(status -> status.getLabel(locale))
            .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR))),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertEquals(DATE, columns.get(6).getId());
    assertEquals(resources.message(DATE), design.submissionsGrid.getColumn(DATE).getCaption());
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    assertEquals(dateFormatter.format(submission.getSubmissionDate()),
        design.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertEquals(LINKED_TO_RESULTS, columns.get(7).getId());
    assertTrue(containsInstanceOf(columns.get(7).getExtensions(), ComponentRenderer.class));
    assertEquals(resources.message(LINKED_TO_RESULTS),
        design.submissionsGrid.getColumn(LINKED_TO_RESULTS).getCaption());
    boolean results = submission.getSamples().stream().filter(sample -> sample.getStatus() != null)
        .filter(sample -> SampleStatus.ANALYSED.compareTo(sample.getStatus()) <= 0).count() > 0;
    Button resultsButton = (Button) design.submissionsGrid.getColumn(LINKED_TO_RESULTS)
        .getValueProvider().apply(submission);
    assertTrue(resultsButton.getStyleName().contains(LINKED_TO_RESULTS));
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + results), resultsButton.getCaption());
    assertTrue(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidable());
    assertEquals(TREATMENTS, columns.get(8).getId());
    assertTrue(containsInstanceOf(columns.get(8).getExtensions(), ComponentRenderer.class));
    assertEquals(resources.message(TREATMENTS),
        design.submissionsGrid.getColumn(TREATMENTS).getCaption());
    Button treatmentsButton =
        (Button) design.submissionsGrid.getColumn(TREATMENTS).getValueProvider().apply(submission);
    assertTrue(treatmentsButton.getStyleName().contains(TREATMENTS));
    assertEquals(resources.message(TREATMENTS), treatmentsButton.getCaption());
    assertFalse(design.submissionsGrid.getColumn(TREATMENTS).isHidable());
    assertTrue(design.submissionsGrid.getColumn(TREATMENTS).isHidden());
    assertEquals(HISTORY, columns.get(9).getId());
    assertTrue(containsInstanceOf(columns.get(9).getExtensions(), ComponentRenderer.class));
    assertEquals(resources.message(HISTORY),
        design.submissionsGrid.getColumn(HISTORY).getCaption());
    Button historyButton =
        (Button) design.submissionsGrid.getColumn(HISTORY).getValueProvider().apply(submission);
    assertTrue(historyButton.getStyleName().contains(HISTORY));
    assertEquals(resources.message(HISTORY), historyButton.getCaption());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
    assertEquals(1, design.submissionsGrid.getFrozenColumnCount());
    assertFalse(sortOrders.isEmpty());
    GridSortOrder<Submission> sortOrder = sortOrders.get(0);
    assertEquals(DATE, sortOrder.getSorted().getId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  public void submissionsGrid_Manager() {
    when(authorizationService.hasManagerRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidable());
    assertFalse(design.submissionsGrid.getColumn(TREATMENTS).isHidable());
    assertTrue(design.submissionsGrid.getColumn(TREATMENTS).isHidden());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertTrue(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidable());
    assertTrue(design.submissionsGrid.getColumn(TREATMENTS).isHidable());
    assertFalse(design.submissionsGrid.getColumn(TREATMENTS).isHidden());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_HiddenColumn() {
    when(userPreferenceService.get(any(), eq(SAMPLE_COUNT), any())).thenReturn(true);
    when(userPreferenceService.get(any(), eq(EXPERIENCE_GOAL), any())).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidden());
    assertFalse(design.submissionsGrid.getColumn(USER).isHidable());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidden());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_COUNT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_NAME, false);
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidable());
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidden());
    verify(userPreferenceService).get(presenter, EXPERIENCE_GOAL, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_STATUSES, false);
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATE, false);
    assertTrue(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidable());
    assertFalse(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidden());
    verify(userPreferenceService).get(presenter, LINKED_TO_RESULTS, false);
    assertFalse(design.submissionsGrid.getColumn(TREATMENTS).isHidable());
    assertTrue(design.submissionsGrid.getColumn(TREATMENTS).isHidden());
    assertFalse(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
  }

  @Test
  public void submissionsGrid_HiddenColumnAdmin() {
    when(userPreferenceService.get(any(), eq(SAMPLE_COUNT), any())).thenReturn(true);
    when(userPreferenceService.get(any(), eq(EXPERIENCE_GOAL), any())).thenReturn(true);
    when(userPreferenceService.get(any(), eq(HISTORY), any())).thenReturn(true);
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(EXPERIENCE).isHidden());
    assertTrue(design.submissionsGrid.getColumn(USER).isHidable());
    assertFalse(design.submissionsGrid.getColumn(USER).isHidden());
    verify(userPreferenceService).get(presenter, USER, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidable());
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_COUNT).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_COUNT, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_NAME).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_NAME, false);
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidable());
    assertTrue(design.submissionsGrid.getColumn(EXPERIENCE_GOAL).isHidden());
    verify(userPreferenceService).get(presenter, EXPERIENCE_GOAL, false);
    assertTrue(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidable());
    assertFalse(design.submissionsGrid.getColumn(SAMPLE_STATUSES).isHidden());
    verify(userPreferenceService).get(presenter, SAMPLE_STATUSES, false);
    assertTrue(design.submissionsGrid.getColumn(DATE).isHidable());
    assertFalse(design.submissionsGrid.getColumn(DATE).isHidden());
    verify(userPreferenceService).get(presenter, DATE, false);
    assertTrue(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidable());
    assertFalse(design.submissionsGrid.getColumn(LINKED_TO_RESULTS).isHidden());
    verify(userPreferenceService).get(presenter, LINKED_TO_RESULTS, false);
    assertTrue(design.submissionsGrid.getColumn(TREATMENTS).isHidable());
    assertFalse(design.submissionsGrid.getColumn(TREATMENTS).isHidden());
    verify(userPreferenceService).get(presenter, TREATMENTS, false);
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidable());
    assertTrue(design.submissionsGrid.getColumn(HISTORY).isHidden());
    verify(userPreferenceService).get(presenter, HISTORY, false);
  }

  @Test
  public void submissionsGrid_HideColumn() {
    presenter.init(view);
    design.submissionsGrid.getColumn(SAMPLE_COUNT).setHidden(true);

    verify(userPreferenceService).save(presenter, SAMPLE_COUNT, true);
  }

  @Test
  public void submissionsGrid_ColumnOrder() {
    String[] columnOrder = new String[] { EXPERIENCE, USER, SAMPLE_NAME, SAMPLE_COUNT,
        EXPERIENCE_GOAL, SAMPLE_STATUSES, DATE, LINKED_TO_RESULTS, TREATMENTS, HISTORY };
    when(userPreferenceService.get(any(), eq(COLUMN_ORDER), any())).thenReturn(columnOrder);
    presenter.init(view);

    assertEquals(EXPERIENCE, design.submissionsGrid.getColumns().get(0).getId());
    assertEquals(USER, design.submissionsGrid.getColumns().get(1).getId());
    assertEquals(SAMPLE_NAME, design.submissionsGrid.getColumns().get(2).getId());
    assertEquals(SAMPLE_COUNT, design.submissionsGrid.getColumns().get(3).getId());
    assertEquals(EXPERIENCE_GOAL, design.submissionsGrid.getColumns().get(4).getId());
    assertEquals(SAMPLE_STATUSES, design.submissionsGrid.getColumns().get(5).getId());
    assertEquals(DATE, design.submissionsGrid.getColumns().get(6).getId());
    assertEquals(LINKED_TO_RESULTS, design.submissionsGrid.getColumns().get(7).getId());
    assertEquals(TREATMENTS, design.submissionsGrid.getColumns().get(8).getId());
    assertEquals(HISTORY, design.submissionsGrid.getColumns().get(9).getId());
    String[] defaultColumnOrder = new String[] { EXPERIENCE, USER, SAMPLE_COUNT, SAMPLE_NAME,
        EXPERIENCE_GOAL, SAMPLE_STATUSES, DATE, LINKED_TO_RESULTS, TREATMENTS, HISTORY };
    verify(userPreferenceService).get(presenter, COLUMN_ORDER, defaultColumnOrder);
  }

  @Test
  public void submissionsGrid_ChangeColumnOrder() {
    presenter.init(view);
    design.submissionsGrid.setColumnOrder(EXPERIENCE, USER, SAMPLE_NAME, SAMPLE_COUNT,
        EXPERIENCE_GOAL, SAMPLE_STATUSES, DATE, LINKED_TO_RESULTS, TREATMENTS, HISTORY);

    String[] columnOrder =
        design.submissionsGrid.getColumns().stream().map(col -> col.getId()).toArray(String[]::new);
    verify(userPreferenceService).save(presenter, COLUMN_ORDER, columnOrder);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void experienceFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.experienceContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void userFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(USER);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.userContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleNameFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.anySampleNameContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void experienceGoalFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE_GOAL);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener<String> listener = (ValueChangeListener<String>) textField
        .getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent<String> event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.goalContains);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleStatusFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_STATUSES);
    ComboBox<SampleStatus> comboBox = (ComboBox<SampleStatus>) cell.getComponent();
    SampleStatus filterValue = SampleStatus.ANALYSED;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.anySampleStatus);
  }

  @Test
  public void dateFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);

    verify(localDateFilterComponentProvider).get();
    verify(localDateFilterComponent).addSaveListener(localDateRangeSaveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(DATE);
    assertTrue(cell.getComponent() instanceof LocalDateFilterComponent);

    Range<LocalDate> range = Range.open(LocalDate.now().minusDays(2), LocalDate.now());
    SaveListener<Range<LocalDate>> listener = localDateRangeSaveListenerCaptor.getValue();
    listener.saved(new SaveEvent<>(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(range, filter.dateRange);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void resultsFilter() {
    presenter.init(view);
    design.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = design.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(LINKED_TO_RESULTS);
    ComboBox<Boolean> comboBox = (ComboBox<Boolean>) cell.getComponent();
    Boolean filterValue = true;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.results);
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(design.headerLabel.getStyleName().contains(HEADER));
    assertTrue(design.submissionsGrid.getStyleName().contains(SUBMISSIONS));
    assertTrue(design.submissionsGrid.getStyleName().contains(COMPONENTS));
    final Submission submission = submissions.get(0);
    Button designButton =
        (Button) design.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertTrue(designButton.getStyleName().contains(EXPERIENCE));
    Button designResultsButton = (Button) design.submissionsGrid.getColumn(LINKED_TO_RESULTS)
        .getValueProvider().apply(submission);
    assertTrue(designResultsButton.getStyleName().contains(LINKED_TO_RESULTS));
    assertTrue(design.selectSamplesButton.getStyleName().contains(SELECT_SAMPLES));
    assertTrue(design.selectedSamplesLabel.getStyleName().contains(SELECT_SAMPLES_LABEL));
    assertTrue(design.selectContainers.getStyleName().contains(SELECT_CONTAINERS));
    assertTrue(design.selectedContainersLabel.getStyleName().contains(SELECT_CONTAINERS_LABEL));
    assertTrue(design.updateStatusButton.getStyleName().contains(UPDATE_STATUS));
    assertTrue(design.transfer.getStyleName().contains(TRANSFER));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), design.headerLabel.getValue());
    for (Column<Submission, ?> column : design.submissionsGrid.getColumns()) {
      assertEquals(resources.message(column.getId()), column.getCaption());
    }
    Submission manyStatuses = entityManager.find(Submission.class, 153L);
    assertEquals(statusesValue(manyStatuses),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(manyStatuses));
    assertEquals(resources.message(SELECT_SAMPLES), design.selectSamplesButton.getCaption());
    assertEquals(resources.message(SELECT_SAMPLES_LABEL, 0),
        design.selectedSamplesLabel.getValue());
    assertEquals(resources.message(SELECT_CONTAINERS), design.selectContainers.getCaption());
    assertEquals(resources.message(SELECT_CONTAINERS_LABEL, 0),
        design.selectedContainersLabel.getValue());
    assertEquals(resources.message(UPDATE_STATUS), design.updateStatusButton.getCaption());
    assertEquals(resources.message(TRANSFER), design.transfer.getCaption());
  }

  @Test
  public void visible() {
    presenter.init(view);

    assertTrue(design.submissionsGrid.getSelectionModel() instanceof SelectionModel.Single);
    assertFalse(design.sampleSelectionLayout.isVisible());
    assertFalse(design.containerSelectionLayout.isVisible());
    assertFalse(design.updateStatusButton.isVisible());
    assertFalse(design.transfer.isVisible());
  }

  @Test
  public void visible_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(design.submissionsGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertTrue(design.sampleSelectionLayout.isVisible());
    assertTrue(design.containerSelectionLayout.isVisible());
    assertTrue(design.updateStatusButton.isVisible());
    assertTrue(design.transfer.isVisible());
  }

  @Test
  public void defaultSubmissions() {
    presenter.init(view);
    Collection<Submission> gridSubmissions = dataProvider(design.submissionsGrid).getItems();

    Set<Long> expectedSubmissionIds =
        submissions.stream().map(s -> s.getId()).collect(Collectors.toSet());
    Set<Long> submissionIds =
        gridSubmissions.stream().map(s -> s.getId()).collect(Collectors.toSet());
    assertTrue(expectedSubmissionIds.containsAll(submissionIds));
    assertTrue(submissionIds.containsAll(expectedSubmissionIds));
  }

  @Test
  public void containerProperties_Analysed() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    final SubmissionSample sample = submission.getSamples().get(0);

    Button button =
        (Button) design.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        design.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    assertEquals(submission.getSamples().get(0).getName(),
        design.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
    assertEquals(submission.getGoal(),
        design.submissionsGrid.getColumn(EXPERIENCE_GOAL).getValueProvider().apply(submission));
    assertEquals(sample.getStatus().getLabel(locale),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    assertEquals(dateFormatter.format(submission.getSubmissionDate()),
        design.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    button = (Button) design.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
        .apply(submission);
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + true), button.getCaption());
    assertFalse(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertFalse(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void containerProperties_NotAnalysed() {
    presenter.init(view);
    final Submission submission = submissions.get(6);
    final SubmissionSample sample = submission.getSamples().get(0);

    Button button =
        (Button) design.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        design.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    assertEquals(sample.getName(),
        design.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
    assertEquals(submission.getGoal(),
        design.submissionsGrid.getColumn(EXPERIENCE_GOAL).getValueProvider().apply(submission));
    assertEquals(statusesValue(submission),
        design.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    assertEquals(dateFormatter.format(submission.getSubmissionDate()),
        design.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    button = (Button) design.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
        .apply(submission);
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + false), button.getCaption());
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertTrue(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void designSubmission() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);

    button.click();

    verify(submissionWindowProvider).get();
    verify(submissionWindow).setValue(submission);
    verify(submissionWindow).center();
    verify(view).addWindow(submissionWindow);
  }

  @Test
  public void designSubmissionResults() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button = (Button) design.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
        .apply(submission);

    button.click();

    verify(submissionAnalysesWindowProvider).get();
    verify(submissionAnalysesWindow).setValue(submission);
    verify(submissionAnalysesWindow).center();
    verify(view).addWindow(submissionAnalysesWindow);
  }

  @Test
  public void designSubmissionTreatments() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(TREATMENTS).getValueProvider().apply(submission);

    button.click();

    verify(submissionTreatmentsWindowProvider).get();
    verify(submissionTreatmentsWindow).setValue(submission);
    verify(submissionTreatmentsWindow).center();
    verify(view).addWindow(submissionTreatmentsWindow);
  }

  @Test
  public void designSubmissionHistory() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) design.submissionsGrid.getColumn(HISTORY).getValueProvider().apply(submission);

    button.click();

    verify(submissionHistoryWindowProvider).get();
    verify(submissionHistoryWindow).setValue(submission);
    verify(submissionHistoryWindow).center();
    verify(view).addWindow(submissionHistoryWindow);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void selectSamples() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    design.submissionsGrid.select(submission1);
    design.submissionsGrid.select(submission2);
    when(sampleSelectionWindow.getItems())
        .thenReturn(new ArrayList<Sample>(submission2.getSamples()));

    design.selectSamplesButton.click();

    verify(sampleSelectionWindowProvider).get();
    verify(sampleSelectionWindow).setItems(samplesListCaptor.capture());
    verify(sampleSelectionWindow).addSaveListener(samplesSaveListenerCaptor.capture());
    List<Sample> samples = samplesListCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(view).addWindow(sampleSelectionWindow);
    SaveEvent<List<Sample>> saveEvent = mock(SaveEvent.class);
    samples = new ArrayList<>(submission2.getSamples());
    when(saveEvent.getSavedObject()).thenReturn(samples);
    samplesSaveListenerCaptor.getValue().saved(saveEvent);
    verify(saveEvent).getSavedObject();
    verify(view).saveSamples(samples);
    assertTrue(design.submissionsGrid.getSelectedItems().isEmpty());
    assertEquals(resources.message(SELECT_SAMPLES_LABEL, submission2.getSamples().size()),
        design.selectedSamplesLabel.getValue());
  }

  @Test
  public void selectContainers_NoSamples() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    design.selectContainers.click();

    verify(containerSelectionWindowProvider, never()).get();
    view.showError(resources.message(SELECT_CONTAINERS_NO_SAMPLES));
  }

  @Test
  @SuppressWarnings("unchecked")
  public void selectContainers() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    when(view.savedSamples()).thenReturn(new ArrayList<>(submission1.getSamples()));
    presenter.init(view);

    design.selectContainers.click();

    verify(containerSelectionWindowProvider).get();
    verify(containerSelectionWindow).setSamples(new ArrayList<>(submission1.getSamples()));
    verify(containerSelectionWindow).addSaveListener(containersSaveListenerCaptor.capture());
    verify(view).addWindow(containerSelectionWindow);
    SaveEvent<List<SampleContainer>> saveEvent = mock(SaveEvent.class);
    List<SampleContainer> containers = Arrays.asList(new Tube(), new Tube());
    when(saveEvent.getSavedObject()).thenReturn(containers);
    containersSaveListenerCaptor.getValue().saved(saveEvent);
    verify(saveEvent).getSavedObject();
    verify(view).saveContainers(containers);
    assertEquals(resources.message(SELECT_CONTAINERS_LABEL, containers.size()),
        design.selectedContainersLabel.getValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void selectContainers_Selection() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    design.submissionsGrid.select(submission1);
    design.submissionsGrid.select(submission2);
    when(view.savedSamples()).thenReturn(new ArrayList<>(submission1.getSamples()));

    design.selectContainers.click();

    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> samples = samplesCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(containerSelectionWindowProvider).get();
    verify(containerSelectionWindow).setSamples(new ArrayList<>(submission1.getSamples()));
    verify(containerSelectionWindow).addSaveListener(containersSaveListenerCaptor.capture());
    verify(view).addWindow(containerSelectionWindow);
    SaveEvent<List<SampleContainer>> saveEvent = mock(SaveEvent.class);
    List<SampleContainer> containers = Arrays.asList(new Tube(), new Tube());
    when(saveEvent.getSavedObject()).thenReturn(containers);
    containersSaveListenerCaptor.getValue().saved(saveEvent);
    verify(saveEvent).getSavedObject();
    verify(view).saveContainers(containers);
    assertEquals(
        resources.message(SELECT_SAMPLES_LABEL,
            submission1.getSamples().size() + submission2.getSamples().size()),
        design.selectedSamplesLabel.getValue());
    assertEquals(resources.message(SELECT_CONTAINERS_LABEL, containers.size()),
        design.selectedContainersLabel.getValue());
  }

  @Test
  public void updateStatus() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    design.updateStatusButton.click();

    verify(view, never()).saveSamples(any());
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }

  @Test
  public void updateStatus_Selection() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    design.submissionsGrid.select(submission1);
    design.submissionsGrid.select(submission2);

    design.updateStatusButton.click();

    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> samples = samplesCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    assertEquals(
        resources.message(SELECT_SAMPLES_LABEL,
            submission1.getSamples().size() + submission2.getSamples().size()),
        design.selectedSamplesLabel.getValue());
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }

  @Test
  public void transfer() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    List<SampleContainer> containers = Arrays.asList(new Tube(), new Tube());
    when(view.savedContainers()).thenReturn(containers);

    design.transfer.click();

    verify(view, never()).saveSamples(any());
    verify(view, never()).saveContainers(any());
    verify(view).navigateTo(TransferView.VIEW_NAME);
  }

  @Test
  public void transfer_NoContainers() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    design.transfer.click();

    verify(view).showError(resources.message(TRANSFER_NO_CONTAINERS));
    verify(view, never()).navigateTo(TransferView.VIEW_NAME);
  }
}
