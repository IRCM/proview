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
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.CONDITION_FALSE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE_GOAL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUSES_SEPARATOR;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.TITLE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static ca.qc.ircm.proview.web.WebConstants.COMPONENTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.Data;
import ca.qc.ircm.proview.sample.Sample;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleSelectionWindow;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SaveEvent;
import ca.qc.ircm.proview.web.SaveListener;
import ca.qc.ircm.proview.web.filter.InstantFilterComponent;
import ca.qc.ircm.proview.web.filter.InstantFilterComponentPresenter;
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
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
  private Provider<InstantFilterComponent> instantFilterComponentProvider;
  @Mock
  private Provider<SubmissionWindow> submissionWindowProvider;
  @Mock
  private Provider<SubmissionAnalysesWindow> submissionAnalysesWindowProvider;
  @Mock
  private Provider<SampleSelectionWindow> sampleSelectionWindowProvider;
  @Mock
  private SubmissionsView view;
  @Mock
  private Report report;
  @Mock
  private SubmissionWindow submissionWindow;
  @Mock
  private SubmissionAnalysesWindow submissionAnalysesWindow;
  @Mock
  private SampleSelectionWindow sampleSelectionWindow;
  @Mock
  private ListDataProvider<Submission> submissionsDataProvider;
  @Mock
  private InstantFilterComponent instantFilterComponent;
  @Mock
  private InstantFilterComponentPresenter instantFilterComponentPresenter;
  @Captor
  private ArgumentCaptor<Collection<Sample>> samplesCaptor;
  @Captor
  private ArgumentCaptor<List<Sample>> samplesListCaptor;
  @Captor
  private ArgumentCaptor<SaveListener> saveListenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
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
        instantFilterComponentProvider, submissionWindowProvider, submissionAnalysesWindowProvider,
        sampleSelectionWindowProvider, applicationName);
    view.headerLabel = new Label();
    view.submissionsGrid = new Grid<>();
    view.selectSamplesButton = new Button();
    view.selectedSamplesLabel = new Label();
    view.updateStatusButton = new Button();
    when(view.getLocale()).thenReturn(locale);
    when(view.getResources()).thenReturn(resources);
    when(submissionService.report()).thenReturn(report);
    submissions = queryFactory.select(submission).from(submission).fetch();
    linkedToResults = new HashMap<>();
    for (Submission submission : submissions.subList(0, 2)) {
      linkedToResults.put(submission, true);
    }
    for (Submission submission : submissions.subList(2, submissions.size())) {
      linkedToResults.put(submission, false);
    }
    when(report.getSubmissions()).thenReturn(submissions);
    when(report.getLinkedToResults()).thenReturn(linkedToResults);
    when(instantFilterComponentProvider.get()).thenReturn(instantFilterComponent);
    when(instantFilterComponent.getPresenter()).thenReturn(instantFilterComponentPresenter);
    when(submissionWindowProvider.get()).thenReturn(submissionWindow);
    when(submissionAnalysesWindowProvider.get()).thenReturn(submissionAnalysesWindow);
    when(sampleSelectionWindowProvider.get()).thenReturn(sampleSelectionWindow);
  }

  private <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(d -> d.getId() == id).findAny();
  }

  private <V> boolean containsInstanceOf(Collection<V> extensions, Class<? extends V> clazz) {
    return extensions.stream().filter(extension -> clazz.isInstance(extension)).findAny()
        .isPresent();
  }

  @SuppressWarnings("unchecked")
  private ListDataProvider<Submission> dataProvider() {
    return (ListDataProvider<Submission>) view.submissionsGrid.getDataProvider();
  }

  private String statusesValue(Submission submission) {
    return submission.getSamples().stream().map(s -> s.getStatus()).distinct().sorted()
        .map(s -> s.getLabel(locale))
        .collect(Collectors.joining(resources.message(SAMPLE_STATUSES_SEPARATOR)));
  }

  @Test
  public void submissionsGridColumns() {
    presenter.init(view);

    List<Column<Submission, ?>> columns = view.submissionsGrid.getColumns();

    assertEquals(EXPERIENCE, columns.get(0).getId());
    assertTrue(containsInstanceOf(columns.get(0).getExtensions(), ComponentRenderer.class));
    assertEquals(SAMPLE_COUNT, columns.get(1).getId());
    assertEquals(SAMPLE_NAME, columns.get(2).getId());
    assertEquals(EXPERIENCE_GOAL, columns.get(3).getId());
    assertEquals(SAMPLE_STATUSES, columns.get(4).getId());
    assertEquals(DATE, columns.get(5).getId());
    assertEquals(LINKED_TO_RESULTS, columns.get(6).getId());
    assertTrue(containsInstanceOf(columns.get(6).getExtensions(), ComponentRenderer.class));
    assertEquals(1, view.submissionsGrid.getFrozenColumnCount());
  }

  @Test
  public void submissionsGridOrder() {
    presenter.init(view);

    List<GridSortOrder<Submission>> sortOrders = view.submissionsGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    GridSortOrder<Submission> sortOrder = sortOrders.get(0);
    assertEquals(DATE, sortOrder.getSorted().getId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void experienceFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getExperienceContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleNameFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getAnySampleNameContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void experienceGoalFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
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
    assertEquals(filterValue, filter.getGoalContains());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void sampleStatusFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_STATUSES);
    ComboBox<SampleStatus> comboBox = (ComboBox<SampleStatus>) cell.getComponent();
    SampleStatus filterValue = SampleStatus.ANALYSED;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.getAnySampleStatus());
  }

  @Test
  @Ignore("Date filter does not work")
  public void dateFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);

    verify(instantFilterComponentProvider).get();
    verify(instantFilterComponent).getPresenter();
    verify(instantFilterComponentPresenter).addSaveListener(saveListenerCaptor.capture());
    HeaderCell cell = filterRow.getCell(DATE);
    assertTrue(cell.getComponent() instanceof InstantFilterComponent);

    Range<Instant> range = Range.open(Instant.now(), Instant.now());
    SaveListener listener = saveListenerCaptor.getValue();
    listener.saved(new SaveEvent(cell.getComponent(), range));

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(range, filter.getDateRange());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void resultsFilter() {
    presenter.init(view);
    view.submissionsGrid.setDataProvider(submissionsDataProvider);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(LINKED_TO_RESULTS);
    ComboBox<Boolean> comboBox = (ComboBox<Boolean>) cell.getComponent();
    Boolean filterValue = true;

    comboBox.setValue(filterValue);

    verify(submissionsDataProvider).refreshAll();
    SubmissionWebFilter filter = presenter.getFilter();
    assertEquals(filterValue, filter.getResults());
  }

  @Test
  public void styles() {
    presenter.init(view);

    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.submissionsGrid.getStyleName().contains(SUBMISSIONS));
    assertTrue(view.submissionsGrid.getStyleName().contains(COMPONENTS));
    assertTrue(view.selectSamplesButton.getStyleName().contains(SELECT_SAMPLES));
    assertTrue(view.selectedSamplesLabel.getStyleName().contains(SELECT_SAMPLES_LABEL));
    assertTrue(view.updateStatusButton.getStyleName().contains(UPDATE_STATUS));
  }

  @Test
  public void captions() {
    presenter.init(view);

    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER), view.headerLabel.getValue());
    for (Column<Submission, ?> column : view.submissionsGrid.getColumns()) {
      assertEquals(resources.message(column.getId()), column.getCaption());
    }
    Submission manyStatuses = entityManager.find(Submission.class, 153L);
    assertEquals(statusesValue(manyStatuses),
        view.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(manyStatuses));
    assertEquals(resources.message(SELECT_SAMPLES), view.selectSamplesButton.getCaption());
    assertEquals(resources.message(SELECT_SAMPLES_LABEL, 0), view.selectedSamplesLabel.getValue());
    assertEquals(resources.message(UPDATE_STATUS), view.updateStatusButton.getCaption());
  }

  @Test
  public void visible() {
    presenter.init(view);

    assertTrue(view.submissionsGrid.getSelectionModel() instanceof SelectionModel.Single);
    assertFalse(view.selectSamplesButton.isVisible());
    assertFalse(view.updateStatusButton.isVisible());
  }

  @Test
  public void visible_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(view.submissionsGrid.getSelectionModel() instanceof SelectionModel.Multi);
    assertTrue(view.selectSamplesButton.isVisible());
    assertTrue(view.updateStatusButton.isVisible());
  }

  @Test
  public void defaultSubmissions() {
    presenter.init(view);
    Collection<Submission> gridSubmissions = dataProvider().getItems();

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
        (Button) view.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        view.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    assertEquals(submission.getSamples().get(0).getName(),
        view.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
    assertEquals(submission.getGoal(),
        view.submissionsGrid.getColumn(EXPERIENCE_GOAL).getValueProvider().apply(submission));
    assertEquals(sample.getStatus().getLabel(locale),
        view.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    assertEquals(dateFormatter.format(submission.getSubmissionDate()),
        view.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    button = (Button) view.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
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
        (Button) view.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        view.submissionsGrid.getColumn(SAMPLE_COUNT).getValueProvider().apply(submission));
    assertEquals(sample.getName(),
        view.submissionsGrid.getColumn(SAMPLE_NAME).getValueProvider().apply(submission));
    assertEquals(submission.getGoal(),
        view.submissionsGrid.getColumn(EXPERIENCE_GOAL).getValueProvider().apply(submission));
    assertEquals(statusesValue(submission),
        view.submissionsGrid.getColumn(SAMPLE_STATUSES).getValueProvider().apply(submission));
    final DateTimeFormatter dateFormatter =
        DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.systemDefault());
    assertEquals(dateFormatter.format(submission.getSubmissionDate()),
        view.submissionsGrid.getColumn(DATE).getValueProvider().apply(submission));
    button = (Button) view.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
        .apply(submission);
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + false), button.getCaption());
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertTrue(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void viewSubmission() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button =
        (Button) view.submissionsGrid.getColumn(EXPERIENCE).getValueProvider().apply(submission);

    button.click();

    verify(submissionWindowProvider).get();
    verify(submissionWindow).setSubmission(submission);
    verify(submissionWindow).center();
    verify(view).addWindow(submissionWindow);
  }

  @Test
  public void viewSubmissionResults() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button = (Button) view.submissionsGrid.getColumn(LINKED_TO_RESULTS).getValueProvider()
        .apply(submission);

    button.click();

    verify(submissionAnalysesWindowProvider).get();
    verify(submissionAnalysesWindow).setSubmission(submission);
    verify(submissionAnalysesWindow).center();
    verify(view).addWindow(submissionAnalysesWindow);
  }

  @Test
  public void selectSamples() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    view.submissionsGrid.select(submission1);
    view.submissionsGrid.select(submission2);
    when(sampleSelectionWindow.getSelectedSamples())
        .thenReturn(new ArrayList<Sample>(submission2.getSamples()));

    view.selectSamplesButton.click();

    verify(sampleSelectionWindowProvider).get();
    verify(sampleSelectionWindow).setSelectedSamples(samplesListCaptor.capture());
    verify(sampleSelectionWindow).addSaveListener(saveListenerCaptor.capture());
    List<Sample> samples = samplesListCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(view).addWindow(sampleSelectionWindow);
    saveListenerCaptor.getValue().saved(mock(SaveEvent.class));
    verify(sampleSelectionWindow).getSelectedSamples();
    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> savedSamples = samplesCaptor.getValue();
    assertEquals(submission2.getSamples().size(), savedSamples.size());
    assertTrue(savedSamples.containsAll(submission2.getSamples()));
    assertTrue(view.submissionsGrid.getSelectedItems().isEmpty());
    assertEquals(resources.message(SELECT_SAMPLES_LABEL, submission2.getSamples().size()),
        view.selectedSamplesLabel.getValue());
  }

  @Test
  public void updateStatus() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    view.submissionsGrid.select(submission1);
    view.submissionsGrid.select(submission2);

    view.updateStatusButton.click();

    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> samples = samplesCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }
}
