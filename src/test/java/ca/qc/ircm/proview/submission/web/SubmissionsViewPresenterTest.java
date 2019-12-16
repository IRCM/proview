/*
 * Copyright (c) 2018 Institut de recherches cliniques de Montreal (IRCM)
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

import static ca.qc.ircm.proview.submission.SubmissionProperties.EXPERIMENT;
import static ca.qc.ircm.proview.submission.SubmissionProperties.USER;
import static ca.qc.ircm.proview.user.UserRole.ADMIN;
import static ca.qc.ircm.proview.user.UserRole.MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.sample.SampleStatus;
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
import java.util.Arrays;
import java.util.List;
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
  private ArgumentCaptor<ComponentEventListener<SavedEvent<SubmissionDialog>>> submissionSavedListenerCaptor;
  private List<Submission> submissions;

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
    view.dialog = mock(SubmissionDialog.class);
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
    when(submission.getId()).thenReturn(2L);
    Submission databaseSubmission = repository.findById(2L).orElse(null);
    when(service.get(any(Long.class))).thenReturn(databaseSubmission);
    presenter.view(submission);
    verify(service).get(2L);
    verify(view.dialog).setSubmission(databaseSubmission);
    verify(view.dialog).open();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void refreshOnSaved() {
    presenter.init(view);
    verify(view.dialog).addSavedListener(submissionSavedListenerCaptor.capture());
    @SuppressWarnings("checkstyle:linelength")
    ComponentEventListener<SavedEvent<SubmissionDialog>> savedListener =
        submissionSavedListenerCaptor.getValue();
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
