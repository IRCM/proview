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

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.SUBMISSIONS;
import static ca.qc.ircm.proview.text.Strings.property;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.security.AuthorizationService;
import ca.qc.ircm.proview.submission.QSubmission;
import ca.qc.ircm.proview.submission.Service;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionFilter;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.SavedEvent;
import com.google.common.collect.Range;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link SubmissionsViewPresenter}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionsViewPresenterTest extends AbstractViewTestCase {
  @Autowired
  private SubmissionsViewPresenter presenter;
  @Mock
  private SubmissionsView view;
  @MockBean
  private SubmissionService service;
  @MockBean
  private AuthorizationService authorizationService;
  @Autowired
  private SubmissionRepository repository;
  @Captor
  private ArgumentCaptor<Submission> submissionCaptor;
  @Captor
  private ArgumentCaptor<SubmissionFilter> filterCaptor;
  @Captor
  private ArgumentCaptor<DataProvider<Submission, ?>> dataProviderCaptor;
  @Captor
  @SuppressWarnings("checkstyle:linelength")
  private ArgumentCaptor<
      ComponentEventListener<SavedEvent<SubmissionDialog>>> submissionSavedListenerCaptor;
  @Captor
  @SuppressWarnings("checkstyle:linelength")
  private ArgumentCaptor<
      ComponentEventListener<SavedEvent<SamplesStatusDialog>>> statusSavedListenerCaptor;
  private List<Submission> submissions;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SubmissionsView.class, locale);

  /**
   * Before test.
   */
  @Before
  @SuppressWarnings("unchecked")
  public void beforeTest() {
    view.header = new H2();
    view.submissions = mock(Grid.class);
    when(view.submissions.getDataProvider()).thenReturn(mock(DataProvider.class));
    view.experiment = mock(Column.class);
    view.user = mock(Column.class);
    view.director = mock(Column.class);
    view.service = mock(Column.class);
    view.dataAvailableDate = mock(Column.class);
    view.date = mock(Column.class);
    view.instrument = mock(Column.class);
    view.samplesCount = mock(Column.class);
    view.samples = mock(Column.class);
    view.status = mock(Column.class);
    view.hidden = mock(Column.class);
    view.experimentFilter = new TextField();
    view.userFilter = new TextField();
    view.directorFilter = new TextField();
    view.instrumentFilter = new ComboBox<>();
    view.samplesFilter = new TextField();
    view.statusFilter = new ComboBox<>();
    view.hiddenFilter = new ComboBox<>();
    view.add = new Button();
    view.editStatus = new Button();
    view.dialog = mock(SubmissionDialog.class);
    view.statusDialog = mock(SamplesStatusDialog.class);
    submissions = repository.findAll();
    when(service.all(any())).thenReturn(submissions);
  }

  @Test
  public void init_User() {
    presenter.init(view);
    verify(view.submissions).setDataProvider(any());
    verify(view.user).setVisible(false);
    verify(view.director).setVisible(false);
    verify(view.service).setVisible(false);
    verify(view.instrument).setVisible(false);
    verify(view.hidden).setVisible(false);
    assertFalse(view.editStatus.isVisible());
  }

  @Test
  public void init_Manager() {
    when(authorizationService.hasAnyRole(MANAGER, ADMIN)).thenReturn(true);
    presenter.init(view);
    verify(view.submissions).setDataProvider(any());
    verify(view.user).setVisible(true);
    verify(view.director).setVisible(false);
    verify(view.service).setVisible(false);
    verify(view.instrument).setVisible(false);
    verify(view.hidden).setVisible(false);
    assertFalse(view.editStatus.isVisible());
  }

  @Test
  public void init_Admin() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    verify(view.submissions).setDataProvider(any());
    verify(view.user).setVisible(true);
    verify(view.director).setVisible(true);
    verify(view.service).setVisible(true);
    verify(view.instrument).setVisible(true);
    verify(view.hidden).setVisible(true);
    assertTrue(view.editStatus.isVisible());
  }

  @Test
  public void submissions() {
    presenter.init(view);
    verify(view.submissions).setDataProvider(dataProviderCaptor.capture());
    List<Submission> submissions = dataProviderCaptor.getValue()
        .fetch(new Query<>(0, Integer.MAX_VALUE, null, null, null)).collect(Collectors.toList());
    assertEquals(this.submissions, submissions);
    verify(service).all(filterCaptor.capture());
    SubmissionFilter filter = filterCaptor.getValue();
    assertEquals(1, filter.sortOrders.size());
    assertEquals(QSubmission.submission.id.desc(), filter.sortOrders.get(0));
  }

  @Test
  public void submissions_SortOrder() {
    presenter.init(view);
    verify(view.submissions).setDataProvider(dataProviderCaptor.capture());
    List<QuerySortOrder> sortOrders =
        Arrays.asList(new QuerySortOrder(EXPERIMENT, SortDirection.ASCENDING),
            new QuerySortOrder(USER, SortDirection.DESCENDING));
    List<Submission> submissions = dataProviderCaptor.getValue()
        .fetch(new Query<>(0, Integer.MAX_VALUE, sortOrders, null, null))
        .collect(Collectors.toList());
    assertEquals(this.submissions, submissions);
    verify(service).all(filterCaptor.capture());
    SubmissionFilter filter = filterCaptor.getValue();
    assertEquals(2, filter.sortOrders.size());
    assertEquals(QSubmission.submission.experiment.asc(), filter.sortOrders.get(0));
    assertEquals(QSubmission.submission.user.name.desc(), filter.sortOrders.get(1));
  }

  @Test
  public void filterExperiment() {
    presenter.init(view);
    presenter.filterExperiment("test");
    assertEquals("test", presenter.filter().experimentContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterUser() {
    presenter.init(view);
    presenter.filterUser("test");
    assertEquals("test", presenter.filter().userContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterDirector() {
    presenter.init(view);
    presenter.filterDirector("test");
    assertEquals("test", presenter.filter().directorContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterDataAvailableDate() {
    Range<LocalDate> range = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
    presenter.init(view);
    presenter.filterDataAvailableDate(range);
    assertEquals(range, presenter.filter().dataAvailableDateRange);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterDate() {
    Range<LocalDate> range = Range.closed(LocalDate.now().minusDays(1), LocalDate.now());
    presenter.init(view);
    presenter.filterDate(range);
    assertEquals(range, presenter.filter().dateRange);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterService() {
    presenter.init(view);
    presenter.filterService(Service.INTACT_PROTEIN);
    assertEquals(Service.INTACT_PROTEIN, presenter.filter().service);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterInstrument() {
    presenter.init(view);
    presenter.filterInstrument(MassDetectionInstrument.VELOS);
    assertEquals(MassDetectionInstrument.VELOS, presenter.filter().instrument);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterSamples() {
    presenter.init(view);
    presenter.filterSamples("test");
    assertEquals("test", presenter.filter().anySampleNameContains);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterStatus() {
    presenter.init(view);
    presenter.filterStatus(SampleStatus.ANALYSED);
    assertEquals(SampleStatus.ANALYSED, presenter.filter().anySampleStatus);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void filterHidden() {
    presenter.init(view);
    presenter.filterHidden(true);
    assertEquals(true, presenter.filter().hidden);
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void view() {
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(32L);
    Submission databaseSubmission = repository.findById(32L).get();
    when(service.get(any(Long.class))).thenReturn(Optional.of(databaseSubmission));
    presenter.view(submission);
    verify(service).get(32L);
    verify(view.dialog).setSubmission(databaseSubmission);
    verify(view.dialog).open();
  }

  @Test
  public void view_Empty() {
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(2L);
    when(service.get(any(Long.class))).thenReturn(Optional.empty());
    presenter.view(submission);
    verify(service).get(2L);
    verify(view.dialog).setSubmission(null);
    verify(view.dialog).open();
  }

  @Test
  public void editStatus_User() {
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(32L);
    Submission databaseSubmission = repository.findById(32L).get();
    when(service.get(any(Long.class))).thenReturn(Optional.of(databaseSubmission));
    presenter.editStatus(submission);
    verify(service, never()).get(any());
    verify(view.statusDialog, never()).setSubmission(any());
    verify(view.statusDialog, never()).open();
  }

  @Test
  public void editStatus_Admin() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(32L);
    Submission databaseSubmission = repository.findById(32L).get();
    when(service.get(any(Long.class))).thenReturn(Optional.of(databaseSubmission));
    presenter.editStatus(submission);
    verify(service).get(32L);
    verify(view.statusDialog).setSubmission(databaseSubmission);
    verify(view.statusDialog).open();
  }

  @Test
  public void editStatus_AdminEmpty() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(2L);
    when(service.get(any(Long.class))).thenReturn(Optional.empty());
    presenter.editStatus(submission);
    verify(service).get(2L);
    verify(view.statusDialog).setSubmission(null);
    verify(view.statusDialog).open();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void history_User() {
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(2L);
    presenter.history(submission);
    verify(ui, never()).navigate(any(Class.class), any());
  }

  @Test
  public void history_Admin() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(2L);
    presenter.history(submission);
    verify(ui).navigate(HistoryView.class, 2L);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void refreshOnSaved_Dialog() {
    presenter.init(view);
    verify(view.dialog).addSavedListener(submissionSavedListenerCaptor.capture());
    @SuppressWarnings("checkstyle:linelength")
    ComponentEventListener<SavedEvent<SubmissionDialog>> savedListener =
        submissionSavedListenerCaptor.getValue();
    savedListener.onComponentEvent(mock(SavedEvent.class));
    verify(view.submissions, times(2)).setDataProvider(any());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void refreshOnSaved_StatusDialog() {
    presenter.init(view);
    verify(view.statusDialog).addSavedListener(statusSavedListenerCaptor.capture());
    @SuppressWarnings("checkstyle:linelength")
    ComponentEventListener<SavedEvent<SamplesStatusDialog>> savedListener =
        statusSavedListenerCaptor.getValue();
    savedListener.onComponentEvent(mock(SavedEvent.class));
    verify(view.submissions, times(2)).setDataProvider(any());
  }

  @Test
  public void add() {
    presenter.init(view);
    presenter.add();
    verify(ui).navigate(SubmissionView.class);
  }

  @Test
  public void editSelectedStatus() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(32L);
    when(view.submissions.getSelectedItems()).thenReturn(Collections.singleton(submission));
    Submission databaseSubmission = repository.findById(32L).get();
    when(service.get(any(Long.class))).thenReturn(Optional.of(databaseSubmission));
    presenter.editSelectedStatus(locale);
    verify(service).get(32L);
    verify(view.statusDialog).setSubmission(databaseSubmission);
    verify(view.statusDialog).open();
    verify(view, never()).showNotification(any());
  }

  @Test
  public void editSelectedStatus_Empty() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    Submission submission = mock(Submission.class);
    when(submission.getId()).thenReturn(2L);
    when(view.submissions.getSelectedItems()).thenReturn(Collections.singleton(submission));
    when(service.get(any(Long.class))).thenReturn(Optional.empty());
    presenter.editSelectedStatus(locale);
    verify(service).get(2L);
    verify(view.statusDialog).setSubmission(null);
    verify(view.statusDialog).open();
    verify(view, never()).showNotification(any());
  }

  @Test
  public void editSelectedStatus_NoSelection() {
    when(authorizationService.hasRole(any())).thenReturn(true);
    when(authorizationService.hasAnyRole(any())).thenReturn(true);
    presenter.init(view);
    when(view.submissions.getSelectedItems()).thenReturn(Collections.emptySet());
    presenter.editSelectedStatus(locale);
    verify(service, never()).get(any());
    verify(view.statusDialog, never()).setSubmission(any());
    verify(view.statusDialog, never()).open();
    verify(view).showNotification(resources.message(property(SUBMISSIONS, REQUIRED)));
  }

  @Test
  public void toggleHidden_False() {
    Submission submission = submissions.get(0);
    submission.setHidden(true);
    presenter.init(view);
    presenter.toggleHidden(submission);
    verify(service).update(submission, null);
    assertFalse(submission.isHidden());
    verify(view.submissions.getDataProvider()).refreshAll();
  }

  @Test
  public void toggleHidden_True() {
    Submission submission = submissions.get(0);
    presenter.init(view);
    presenter.toggleHidden(submission);
    verify(service).update(submission, null);
    assertTrue(submission.isHidden());
    verify(view.submissions.getDataProvider()).refreshAll();
  }
}
