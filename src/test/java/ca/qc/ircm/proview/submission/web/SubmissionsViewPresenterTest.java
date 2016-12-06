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
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.COMPONENTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.CONDITION_FALSE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.DATE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.EXPERIENCE_GOAL;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.HIDE_SELECTION;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.LINKED_TO_RESULTS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_COUNT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_NAME;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SAMPLE_STATUS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SELECT;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.SUBMISSIONS;
import static ca.qc.ircm.proview.submission.web.SubmissionsViewPresenter.UPDATE_STATUS;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.HEADER_LABEL_ID;
import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Range;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.submission.SubmissionService.Report;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.utils.web.FilterInstantComponent;
import ca.qc.ircm.proview.utils.web.FilterInstantComponentPresenter;
import ca.qc.ircm.proview.utils.web.FunctionFilter;
import ca.qc.ircm.proview.utils.web.RangeFilter;
import ca.qc.ircm.proview.utils.web.StringToInstantConverter;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
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
import java.util.Set;
import java.util.function.BiFunction;

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
  private SubmissionsView view;
  @Mock
  private Report report;
  @Mock
  private SubmissionWindow submissionWindow;
  @Mock
  private SubmissionAnalysesWindow submissionAnalysesWindow;
  @Captor
  private ArgumentCaptor<Collection<Submission>> submissionsCaptor;
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
        submissionAnalysesWindowProvider, applicationName);
    view.headerLabel = new Label();
    view.submissionsGrid = new Grid();
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
    presenter.init(view);
  }

  @Test
  public void submissionsGridColumns() {
    List<Column> columns = view.submissionsGrid.getColumns();

    assertEquals(SELECT, columns.get(0).getPropertyId());
    assertTrue(columns.get(0).getRenderer() instanceof ComponentRenderer);
    assertEquals(EXPERIENCE, columns.get(1).getPropertyId());
    assertTrue(columns.get(1).getRenderer() instanceof ComponentRenderer);
    assertEquals(SAMPLE_COUNT, columns.get(2).getPropertyId());
    assertEquals(SAMPLE_NAME, columns.get(3).getPropertyId());
    assertEquals(EXPERIENCE_GOAL, columns.get(4).getPropertyId());
    assertEquals(SAMPLE_STATUS, columns.get(5).getPropertyId());
    assertEquals(DATE, columns.get(6).getPropertyId());
    assertTrue(columns.get(6).getConverter() instanceof StringToInstantConverter);
    assertEquals(LINKED_TO_RESULTS, columns.get(7).getPropertyId());
    assertTrue(columns.get(7).getRenderer() instanceof ComponentRenderer);
    assertEquals(2, view.submissionsGrid.getFrozenColumnCount());
  }

  @Test
  public void submissionsGridSelection() {
    SelectionModel selectionModel = view.submissionsGrid.getSelectionModel();

    assertTrue(selectionModel instanceof SelectionModel.Multi);
  }

  @Test
  public void submissionsGridOrder() {
    List<SortOrder> sortOrders = view.submissionsGrid.getSortOrder();

    assertFalse(sortOrders.isEmpty());
    SortOrder sortOrder = sortOrders.get(0);
    assertEquals(DATE, sortOrder.getPropertyId());
    assertEquals(SortDirection.DESCENDING, sortOrder.getDirection());
  }

  @Test
  public void experienceFilter() {
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof SimpleStringFilter);
    SimpleStringFilter stringFilter = (SimpleStringFilter) filter;
    assertEquals(filterValue, stringFilter.getFilterString());
    assertEquals(EXPERIENCE, stringFilter.getPropertyId());
  }

  @Test
  public void sampleNameFilter() {
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_NAME);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

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
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(EXPERIENCE_GOAL);
    TextField textField = (TextField) cell.getComponent();
    String filterValue = "test";
    TextChangeListener listener =
        (TextChangeListener) textField.getListeners(TextChangeEvent.class).iterator().next();
    TextChangeEvent event = mock(TextChangeEvent.class);
    when(event.getText()).thenReturn(filterValue);

    listener.textChange(event);

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
    HeaderRow filterRow = view.submissionsGrid.getHeaderRow(1);
    HeaderCell cell = filterRow.getCell(SAMPLE_STATUS);
    ComboBox comboBox = (ComboBox) cell.getComponent();
    SampleStatus filterValue = SampleStatus.ANALYSED;

    comboBox.setValue(filterValue);

    GeneratedPropertyContainer container =
        (GeneratedPropertyContainer) view.submissionsGrid.getContainerDataSource();
    Collection<Filter> filters = container.getContainerFilters();
    assertEquals(1, filters.size());
    Filter filter = filters.iterator().next();
    assertTrue(filter instanceof Compare.Equal);
    Compare.Equal equalFilter = (Compare.Equal) filter;
    assertEquals(filterValue, equalFilter.getValue());
    assertEquals(SAMPLE_STATUS, equalFilter.getPropertyId());
  }

  @Test
  public void dateFilter() {
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
    assertTrue(filter instanceof FunctionFilter);
    FunctionFilter functionFilter = (FunctionFilter) filter;
    assertEquals(filterValue, functionFilter.getValue());
    assertEquals(LINKED_TO_RESULTS, functionFilter.getPropertyId());
    BiFunction<Object, Item, Object> itemValueFunction = functionFilter.getItemValueFunction();
    SubmissionSample sample = submissions.get(0).getSamples().get(0);
    assertEquals(true, itemValueFunction.apply(sample, new BeanItem<>(sample)));
    sample = submissions.get(3).getSamples().get(0);
    assertEquals(false, itemValueFunction.apply(sample, new BeanItem<>(sample)));
    BiFunction<Object, Object, Boolean> compareFunction = functionFilter.getComparisonFunction();
    assertTrue(compareFunction.apply(true, true));
    assertFalse(compareFunction.apply(true, false));
    assertFalse(compareFunction.apply(false, true));
    assertTrue(compareFunction.apply(false, false));
    assertTrue(compareFunction.apply(null, null));
    assertFalse(compareFunction.apply(null, true));
    assertFalse(compareFunction.apply(true, null));
  }

  @Test
  public void styles() {
    assertTrue(view.headerLabel.getStyleName().contains(HEADER));
    assertTrue(view.submissionsGrid.getStyleName().contains(SUBMISSIONS));
    assertTrue(view.submissionsGrid.getStyleName().contains(HIDE_SELECTION));
    assertTrue(view.submissionsGrid.getStyleName().contains(COMPONENTS));
    assertTrue(view.updateStatusButton.getStyleName().contains(UPDATE_STATUS));
  }

  @Test
  public void captions() {
    verify(view).setTitle(resources.message(TITLE, applicationName));
    assertEquals(resources.message(HEADER_LABEL_ID), view.headerLabel.getValue());
    for (Column column : view.submissionsGrid.getColumns()) {
      assertEquals(resources.message((String) column.getPropertyId()), column.getHeaderCaption());
    }
    assertEquals(resources.message(UPDATE_STATUS), view.updateStatusButton.getCaption());
  }

  @Test
  public void visible() {
    assertFalse(view.updateStatusButton.isVisible());
  }

  @Test
  public void visible_Admin() {
    when(authorizationService.hasAdminRole()).thenReturn(true);
    presenter.init(view);

    assertTrue(view.updateStatusButton.isVisible());
  }

  @Test
  public void defaultSubmissions() {
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();

    Collection<?> itemIds = container.getItemIds();

    Set<Submission> submissions = new HashSet<>();
    for (Object itemId : itemIds) {
      assertTrue(itemId instanceof SubmissionSample);
      SubmissionSample sample = (SubmissionSample) itemId;
      assertTrue(submissions.add(sample.getSubmission()));
    }
    this.submissions.containsAll(submissions);
    submissions.containsAll(this.submissions);
  }

  @Test
  public void containerProperties_Analysed() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();

    CheckBox checkBox = (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    assertEquals(true, checkBox.isVisible());
    Button button = (Button) container.getItem(sample).getItemProperty(EXPERIENCE).getValue();
    assertEquals(sample.getSubmission().getExperience(), button.getCaption());
    assertEquals(sample.getSubmission().getSamples().size(),
        container.getItem(sample).getItemProperty(SAMPLE_COUNT).getValue());
    assertEquals(sample.getName(),
        container.getItem(sample).getItemProperty(SAMPLE_NAME).getValue());
    assertEquals(sample.getSubmission().getGoal(),
        container.getItem(sample).getItemProperty(EXPERIENCE_GOAL).getValue());
    assertEquals(sample.getStatus(),
        container.getItem(sample).getItemProperty(SAMPLE_STATUS).getValue());
    assertEquals(sample.getSubmission().getSubmissionDate(),
        container.getItem(sample).getItemProperty(DATE).getValue());
    button = (Button) container.getItem(sample).getItemProperty(LINKED_TO_RESULTS).getValue();
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + true), button.getCaption());
    assertFalse(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertFalse(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void containerProperties_NotAnalysed() {
    final SubmissionSample sample = submissions.get(3).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();

    CheckBox checkBox = (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    assertEquals(false, checkBox.isVisible());
    Button button = (Button) container.getItem(sample).getItemProperty(EXPERIENCE).getValue();
    assertEquals(sample.getSubmission().getExperience(), button.getCaption());
    assertEquals(sample.getSubmission().getSamples().size(),
        container.getItem(sample).getItemProperty(SAMPLE_COUNT).getValue());
    assertEquals(sample.getName(),
        container.getItem(sample).getItemProperty(SAMPLE_NAME).getValue());
    assertEquals(sample.getSubmission().getGoal(),
        container.getItem(sample).getItemProperty(EXPERIENCE_GOAL).getValue());
    assertEquals(sample.getStatus(),
        container.getItem(sample).getItemProperty(SAMPLE_STATUS).getValue());
    assertEquals(sample.getSubmission().getSubmissionDate(),
        container.getItem(sample).getItemProperty(DATE).getValue());
    button = (Button) container.getItem(sample).getItemProperty(LINKED_TO_RESULTS).getValue();
    assertEquals(resources.message(LINKED_TO_RESULTS + "." + false), button.getCaption());
    assertTrue(button.getStyleName().contains(ValoTheme.BUTTON_BORDERLESS));
    assertTrue(button.getStyleName().contains(CONDITION_FALSE));
  }

  @Test
  public void selectSubmission_CheckBox() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    CheckBox checkBox = (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));

    checkBox.setValue(true);

    assertEquals(1, view.submissionsGrid.getSelectedRows().size());
    assertTrue(view.submissionsGrid.getSelectedRows().contains(sample));
    assertTrue(checkBox.getValue());
  }

  @Test
  public void selectSubmission_Grid() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    final CheckBox checkBox =
        (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));

    view.submissionsGrid.select(sample);

    assertEquals(1, view.submissionsGrid.getSelectedRows().size());
    assertTrue(view.submissionsGrid.getSelectedRows().contains(sample));
    assertTrue(checkBox.getValue());
  }

  @Test
  public void deselectSubmission_CheckBox() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    CheckBox checkBox = (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));
    view.submissionsGrid.select(sample);
    assertTrue(checkBox.getValue());

    checkBox.setValue(false);

    assertEquals(0, view.submissionsGrid.getSelectedRows().size());
    assertFalse(view.submissionsGrid.getSelectedRows().contains(sample));
    assertFalse(checkBox.getValue());
  }

  @Test
  public void deselectSubmission_Grid() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    CheckBox checkBox = (CheckBox) container.getItem(sample).getItemProperty(SELECT).getValue();
    checkBox.getListeners(AttachEvent.class)
        .forEach(l -> ((AttachListener) l).attach(mock(AttachEvent.class)));
    checkBox.setValue(true);

    view.submissionsGrid.deselect(sample);

    assertEquals(0, view.submissionsGrid.getSelectedRows().size());
    assertFalse(view.submissionsGrid.getSelectedRows().contains(sample));
    assertFalse(checkBox.getValue());
  }

  @Test
  public void viewSubmission() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    Button button = (Button) container.getItem(sample).getItemProperty(EXPERIENCE).getValue();

    button.click();

    verify(submissionWindowProvider).get();
    verify(submissionWindow).setSubmission(sample.getSubmission());
    verify(submissionWindow).center();
    verify(view).addWindow(submissionWindow);
  }

  @Test
  public void viewSubmissionResults() {
    final SubmissionSample sample = submissions.get(0).getSamples().get(0);
    Container.Indexed container = view.submissionsGrid.getContainerDataSource();
    Button button =
        (Button) container.getItem(sample).getItemProperty(LINKED_TO_RESULTS).getValue();

    button.click();

    verify(submissionAnalysesWindowProvider).get();
    verify(submissionAnalysesWindow).setSubmission(sample.getSubmission());
    verify(submissionAnalysesWindow).center();
    verify(view).addWindow(submissionAnalysesWindow);
  }

  @Test
  public void updateStatus() {
    final SubmissionSample sample1 = submissions.get(0).getSamples().get(0);
    final SubmissionSample sample2 = submissions.get(1).getSamples().get(0);
    view.submissionsGrid.select(sample1);
    view.submissionsGrid.select(sample2);

    view.updateStatusButton.click();

    verify(view).saveSubmissions(submissionsCaptor.capture());
    Collection<Submission> submissions = submissionsCaptor.getValue();
    assertEquals(2, submissions.size());
    assertTrue(submissions.contains(sample1.getSubmission()));
    assertTrue(submissions.contains(sample2.getSubmission()));
    verify(view).navigateTo(SampleStatusView.VIEW_NAME);
  }
}
