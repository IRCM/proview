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
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.history.Activity;
import ca.qc.ircm.proview.history.ActivityRepository;
import ca.qc.ircm.proview.history.ActivityService;
import ca.qc.ircm.proview.msanalysis.MsAnalysis;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.plate.Plate;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.Treatment;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.web.SavedEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.function.ValueProvider;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link HistoryViewPresenter}.
 */
@ServiceTestAnnotations
public class HistoryViewPresenterTest extends AbstractKaribuTestCase {
  private HistoryViewPresenter presenter;
  @MockBean
  private ActivityService service;
  @MockBean
  private SubmissionService submissionService;
  @MockBean
  private SubmissionDialog dialog;
  @Autowired
  private ObjectFactory<SubmissionDialog> dialogFactory;
  @MockBean
  private MsAnalysisDialog msAnalysisDialog;
  @Autowired
  private ObjectFactory<MsAnalysisDialog> msAnalysisDialogFactory;
  @MockBean
  private TreatmentDialog treatmentDialog;
  @Autowired
  private ObjectFactory<TreatmentDialog> treatmentDialogFactory;
  @Mock
  private HistoryView view;
  @Mock
  private Activity activity;
  @Mock
  private MsAnalysis msAnalysis;
  @Mock
  private Treatment treatment;
  @Autowired
  private ActivityRepository repository;
  @Autowired
  private SubmissionRepository submissionRepository;
  @Captor
  private ArgumentCaptor<ValueProvider<Activity, String>> valueProviderCaptor;
  @Captor
  private ArgumentCaptor<
      ComponentEventListener<SavedEvent<SubmissionDialog>>> submissionSavedListenerCaptor;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(HistoryView.class, locale);
  private Submission submission;
  private List<Activity> activities;
  private String description = "test description";

  /**
   * Before tests.
   */
  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeTest() {
    ui.setLocale(locale);
    presenter = new HistoryViewPresenter(service, submissionService);
    view.header = new H2();
    view.activities = mock(Grid.class);
    view.user = mock(Grid.Column.class);
    view.type = mock(Grid.Column.class);
    view.date = mock(Grid.Column.class);
    view.description = mock(Grid.Column.class);
    view.explanation = mock(Grid.Column.class);
    view.dialogFactory = dialogFactory;
    view.treatmentDialogFactory = treatmentDialogFactory;
    view.msAnalysisDialogFactory = msAnalysisDialogFactory;
    submission = submissionRepository.findById(163L).get();
    activities = repository.findAll();
    presenter.init(view);
    when(service.all(any())).thenReturn(activities);
  }

  @Test
  public void description() {
    when(service.description(any(), any())).thenReturn(Optional.of(description));
    String description = presenter.description(activities.get(1), locale);
    verify(service).description(activities.get(1), locale);
    assertEquals(this.description, description);
  }

  @Test
  public void description_Empty() {
    when(service.description(any(), any())).thenReturn(Optional.empty());
    String description = presenter.description(activities.get(1), locale);
    verify(service).description(activities.get(1), locale);
    assertEquals("", description);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void view_Submission() {
    when(submissionService.get(any())).thenReturn(Optional.of(submission));
    presenter.setParameter(34L);
    Submission submission = mock(Submission.class);
    when(service.record(any())).thenReturn(Optional.of(submission));
    presenter.view(activity, locale);
    verify(service).record(activity);
    verify(dialog).setSubmission(submission);
    verify(dialog).open();
    verify(dialog).addSavedListener(submissionSavedListenerCaptor.capture());
    ComponentEventListener<SavedEvent<SubmissionDialog>> savedListener =
        submissionSavedListenerCaptor.getValue();
    savedListener.onComponentEvent(mock(SavedEvent.class));
    verify(service, times(2)).all(this.submission);
    verify(view.activities, times(2)).setItems(any(Collection.class));
  }

  @Test
  public void view_MsAnalysis() {
    when(service.record(any())).thenReturn(Optional.of(msAnalysis));
    presenter.view(activity, locale);
    verify(msAnalysisDialog).setMsAnalysis(msAnalysis);
    verify(msAnalysisDialog).open();
  }

  @Test
  public void view_Treatment() {
    when(service.record(any())).thenReturn(Optional.of(treatment));
    presenter.view(activity, locale);
    verify(treatmentDialog).setTreatment(treatment);
    verify(treatmentDialog).open();
  }

  @Test
  public void view_Plate() {
    when(service.record(any())).thenReturn(Optional.of(mock(Plate.class)));
    presenter.view(activity, locale);
    verify(view).showNotification(resources.message(VIEW_ERROR, Plate.class.getSimpleName()));
  }

  @Test
  public void view_Other() {
    when(service.record(any())).thenReturn(Optional.of(mock(Object.class)));
    presenter.view(activity, locale);
    verify(view).showNotification(resources.message(VIEW_ERROR, Object.class.getSimpleName()));
  }

  @Test
  public void view_Empty() {
    when(service.record(any())).thenReturn(Optional.empty());
    presenter.view(activity, locale);
    verify(view).showNotification(resources.message(VIEW_ERROR, Object.class.getSimpleName()));
  }

  @Test
  public void getSubmission() {
    when(submissionService.get(any())).thenReturn(Optional.of(submission));
    assertNull(presenter.getSubmission());
    presenter.setParameter(34L);
    assertEquals(submission, presenter.getSubmission());
  }

  @Test
  public void setParameter() {
    when(submissionService.get(any())).thenReturn(Optional.of(submission));

    presenter.setParameter(34L);

    verify(submissionService).get(34L);
    verify(service).all(submission);
    verify(view.activities).setItems(activities);
    assertEquals(submission, presenter.getSubmission());
  }

  @Test
  public void setParameter_EmptySubmission() {
    when(submissionService.get(any())).thenReturn(Optional.empty());

    presenter.setParameter(34L);

    verify(submissionService).get(34L);
    verifyNoInteractions(service, view.activities);
    assertNull(presenter.getSubmission());
  }

  @Test
  public void setParameter_Null() {
    presenter.setParameter(null);

    verifyNoInteractions(submissionService, service, view.activities);
    assertNull(presenter.getSubmission());
  }
}
