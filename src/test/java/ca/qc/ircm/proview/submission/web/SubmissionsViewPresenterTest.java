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
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT_SAMPLES_LABEL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSION;
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
import ca.qc.ircm.proview.web.v7.converter.StringToInstantConverter;
import ca.qc.ircm.proview.web.v7.filter.FilterInstantComponent;
import ca.qc.ircm.proview.web.v7.filter.FilterInstantComponentPresenter;
import ca.qc.ircm.proview.web.v7.filter.RangeFilter;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.sort.SortOrder;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.filter.Compare;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.Grid.Column;
import com.vaadin.v7.ui.Grid.HeaderCell;
import com.vaadin.v7.ui.Grid.HeaderRow;
import com.vaadin.v7.ui.Grid.SelectionModel;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
  private Provider<FilterInstantComponentPresenter> filterInstantComponentPresenterProvider;
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
  private ObjectProperty<List<Sample>> selectedSamplesProperty;
  @Captor
  private ArgumentCaptor<Collection<Sample>> samplesCaptor;
  @Captor
  private ArgumentCaptor<List<Sample>> samplesListCaptor;
  @Captor
  private ArgumentCaptor<com.vaadin.v7.data.Property.ValueChangeListener> listenerCaptor;
  @Value("${spring.application.name}")
  private String applicationName;
  private FilterInstantComponentPresenter filterInstantComponentPresenter =
      new FilterInstantComponentPresenter();
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
        filterInstantComponentPresenterProvider, submissionWindowProvider,
        submissionAnalysesWindowProvider, sampleSelectionWindowProvider, applicationName);
    view.headerLabel = new Label();
    view.submissionsGrid = new Grid();
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
    when(filterInstantComponentPresenterProvider.get()).thenReturn(filterInstantComponentPresenter);
    when(submissionWindowProvider.get()).thenReturn(submissionWindow);
    when(submissionAnalysesWindowProvider.get()).thenReturn(submissionAnalysesWindow);
    when(sampleSelectionWindowProvider.get()).thenReturn(sampleSelectionWindow);
  }

  private <D extends Data> Optional<D> find(Collection<D> datas, long id) {
    return datas.stream().filter(d -> d.getId() == id).findAny();
  }

  private Object containerItemId(Submission submission) {
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    return container.getItemIds().stream().filter(o -> container.getItem(o)
        .getItemProperty(SUBMISSION + ".id").getValue().equals(submission.getId())).findFirst()
        .orElse(null);
  }

  private Item containerItem(Submission submission) {
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    Object itemId = containerItemId(submission);
    return itemId != null ? container.getItem(itemId) : null;
  }

  @Test
  public void submissionsGridColumns() {
    presenter.init(view);

    List<Column> columns = view.submissionsGrid.getColumns();

    assertEquals(EXPERIENCE, columns.get(0).getPropertyId());
    assertTrue(columns.get(0).getRenderer() instanceof ComponentRenderer);
    assertEquals(SAMPLE_COUNT, columns.get(1).getPropertyId());
    assertEquals(SAMPLE_NAME, columns.get(2).getPropertyId());
    assertEquals(EXPERIENCE_GOAL, columns.get(3).getPropertyId());
    assertEquals(SAMPLE_STATUSES, columns.get(4).getPropertyId());
    assertEquals(DATE, columns.get(5).getPropertyId());
    assertTrue(columns.get(5).getConverter() instanceof StringToInstantConverter);
    assertEquals(LINKED_TO_RESULTS, columns.get(6).getPropertyId());
    assertTrue(columns.get(6).getRenderer() instanceof ComponentRenderer);
    assertEquals(1, view.submissionsGrid.getFrozenColumnCount());
  }

  @Test
  public void submissionsGridOrder() {
    presenter.init(view);

    List<SortOrder> sortOrders = view.submissionsGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    SortOrder sortOrder = sortOrders.get(0);
    assertEquals(DATE, sortOrder.getPropertyId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  public void experienceFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener listener =
        (ValueChangeListener) textField.getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue.toLowerCase(locale), stringFilter.getFilterString());
    assertEquals(EXPERIENCE, stringFilter.getPropertyId());
  }

  @Test
  public void sampleNameFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener listener =
        (ValueChangeListener) textField.getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(SAMPLE_NAME, stringFilter.getPropertyId());
  }

  @Test
  public void experienceGoalFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE_GOAL);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    ValueChangeListener listener =
        (ValueChangeListener) textField.getListeners(ValueChangeEvent.class).iterator().next();
    ValueChangeEvent event = mock(ValueChangeEvent.class);
    when(event.getValue()).thenReturn(filterValue);

    listener.valueChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(EXPERIENCE_GOAL, stringFilter.getPropertyId());
  }

  @Test
  public void sampleStatusFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_STATUSES);
    ComboBox comboBox = (ComboBox) cell.getComponent();
    String filterValue = SampleStatus.ANALYSED.getLabel(locale);

    comboBox.setValue(filterValue);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue.toLowerCase(locale), stringFilter.getFilterString());
    assertEquals(SAMPLE_STATUSES, stringFilter.getPropertyId());
  }

  @Test
  public void dateFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(DATE);
    FilterInstantComponent filterInstantComponent = (FilterInstantComponent) cell.getComponent();
    Range<Instant> filterValue = Range.all();

    filterInstantComponent.setRange(filterValue);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof RangeFilter);
    RangeFilter<?> rangeFilter = (RangeFilter<?>) filter;
    assertEquals(filterValue, rangeFilter.getValue());
    assertEquals(DATE, rangeFilter.getPropertyId());
  }

  @Test
  public void resultsFilter() {
    presenter.init(view);
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(LINKED_TO_RESULTS);
    ComboBox comboBox = (ComboBox) cell.getComponent();
    Boolean filterValue = true;

    comboBox.setValue(filterValue);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof Compare.Equal);
    Compare.Equal equalsFilter = (Compare.Equal) filter;
    assertEquals(filterValue, equalsFilter.getValue());
    assertEquals(LINKED_TO_RESULTS, equalsFilter.getPropertyId());
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
    for (Column column : view.submissionsGrid.getColumns()) {
      assertEquals(resources.message((String) column.getPropertyId()), column.getHeaderCaption());
    }
    Submission submission = submissions.get(0);
    String statuses = submission.getSamples().stream().map(s -> s.getStatus()).distinct().sorted()
        .map(s -> s.getLabel(locale))
        .collect(Collectors.joining(resources.message(SAMPLE_STATUSES)));
    Object statusValue = containerItem(submission).getItemProperty(SAMPLE_STATUSES).getValue();
    assertEquals(statuses, statusValue);
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
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();

    Collection<?> itemIds = container.getItemIds();

    Set<Long> expectedSubmissionIds =
        submissions.stream().map(s -> s.getId()).collect(Collectors.toSet());
    Set<Long> submissionIds = new HashSet<>();
    for (Object itemId : itemIds) {
      Item item = container.getItem(itemId);
      Long id = (Long) item.getItemProperty(SUBMISSION + ".id").getValue();
      submissionIds.add(id);
    }
    assertTrue(expectedSubmissionIds.containsAll(submissionIds));
    assertTrue(submissionIds.containsAll(expectedSubmissionIds));
  }

  @Test
  public void containerProperties_Analysed() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    final SubmissionSample sample = submission.getSamples().get(0);

    Button button = (Button) containerItem(submission).getItemProperty(EXPERIENCE).getValue();
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        containerItem(submission).getItemProperty(SAMPLE_COUNT).getValue());
    assertEquals(submission.getSamples().get(0).getName(),
        containerItem(submission).getItemProperty(SAMPLE_NAME).getValue());
    assertEquals(submission.getGoal(),
        containerItem(submission).getItemProperty(EXPERIENCE_GOAL).getValue());
    assertEquals(sample.getStatus().getLabel(locale),
        containerItem(submission).getItemProperty(SAMPLE_STATUSES).getValue());
    assertEquals(submission.getSubmissionDate(),
        containerItem(submission).getItemProperty(DATE).getValue());
    button = (Button) containerItem(submission).getItemProperty(LINKED_TO_RESULTS).getValue();
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + true), button.getCaption());
    assertFalse(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertFalse(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void containerProperties_NotAnalysed() {
    presenter.init(view);
    final Submission submission = submissions.get(3);
    final SubmissionSample sample = submission.getSamples().get(0);

    Button button = (Button) containerItem(submission).getItemProperty(EXPERIENCE).getValue();
    assertEquals(submission.getExperience(), button.getCaption());
    assertEquals(submission.getSamples().size(),
        containerItem(submission).getItemProperty(SAMPLE_COUNT).getValue());
    assertEquals(sample.getName(),
        containerItem(submission).getItemProperty(SAMPLE_NAME).getValue());
    assertEquals(submission.getGoal(),
        containerItem(submission).getItemProperty(EXPERIENCE_GOAL).getValue());
    assertEquals(sample.getStatus().getLabel(locale),
        containerItem(submission).getItemProperty(SAMPLE_STATUSES).getValue());
    assertEquals(submission.getSubmissionDate(),
        containerItem(submission).getItemProperty(DATE).getValue());
    button = (Button) containerItem(submission).getItemProperty(LINKED_TO_RESULTS).getValue();
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + false), button.getCaption());
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertTrue(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void viewSubmission() {
    presenter.init(view);
    final Submission submission = submissions.get(0);
    Button button = (Button) containerItem(submission).getItemProperty(EXPERIENCE).getValue();

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
    Button button =
        (Button) containerItem(submission).getItemProperty(LINKED_TO_RESULTS).getValue();

    button.click();

    verify(submissionAnalysesWindowProvider).get();
    verify(submissionAnalysesWindow).setSubmission(submission);
    verify(submissionAnalysesWindow).center();
    verify(view).addWindow(submissionAnalysesWindow);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void selectSamples() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    view.submissionsGrid.select(containerItemId(submission1));
    view.submissionsGrid.select(containerItemId(submission2));
    when(sampleSelectionWindow.selectedSamplesProperty()).thenReturn(selectedSamplesProperty);

    view.selectSamplesButton.click();

    verify(sampleSelectionWindowProvider).get();
    verify(sampleSelectionWindow).setSelectedSamples(samplesListCaptor.capture());
    List<Sample> samples = samplesListCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(sampleSelectionWindow).selectedSamplesProperty();
    verify(selectedSamplesProperty).addValueChangeListener(listenerCaptor.capture());
    verify(view).addWindow(sampleSelectionWindow);
    com.vaadin.v7.data.Property.ValueChangeListener listener = listenerCaptor.getValue();
    Property.ValueChangeEvent event = mock(Property.ValueChangeEvent.class);
    Property<Object> eventProperty = mock(Property.class);
    when(event.getProperty()).thenReturn(eventProperty);
    when(eventProperty.getValue()).thenReturn(submission2.getSamples());
    listener.valueChange(event);
    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> savedSamples = samplesCaptor.getValue();
    assertEquals(submission2.getSamples().size(), savedSamples.size());
    assertTrue(savedSamples.containsAll(submission2.getSamples()));
    assertTrue(view.submissionsGrid.getSelectedRows().isEmpty());
    assertEquals(resources.message(SELECT_SAMPLES_LABEL, submission2.getSamples().size()),
        view.selectedSamplesLabel.getValue());
  }

  @Test
  public void updateStatus() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);
    final Submission submission1 = find(submissions, 32L).orElse(null);
    final Submission submission2 = find(submissions, 156L).orElse(null);
    view.submissionsGrid.select(containerItemId(submission1));
    view.submissionsGrid.select(containerItemId(submission2));

    view.updateStatusButton.click();

    verify(view).saveSamples(samplesCaptor.capture());
    Collection<Sample> samples = samplesCaptor.getValue();
    assertEquals(submission1.getSamples().size() + submission2.getSamples().size(), samples.size());
    assertTrue(samples.containsAll(submission1.getSamples()));
    assertTrue(samples.containsAll(submission2.getSamples()));
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }
}
