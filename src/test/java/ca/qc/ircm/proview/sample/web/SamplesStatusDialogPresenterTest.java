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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.REQUIRED;
import static ca.qc.ircm.proview.sample.web.SamplesStatusDialog.SAVED;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findValidationStatusByField;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.AppResources;
import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSample;
import ca.qc.ircm.proview.sample.SubmissionSampleService;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Tests for {@link SamplesStatusDialogPresenter}.
 */
@ServiceTestAnnotations
public class SamplesStatusDialogPresenterTest extends AbstractViewTestCase {
  @Autowired
  private SamplesStatusDialogPresenter presenter;
  @MockBean
  private SubmissionSampleService service;
  @Mock
  private SamplesStatusDialog dialog;
  @Captor
  private ArgumentCaptor<ComponentEventListener<OpenedChangeEvent<Dialog>>> openedListener;
  @Autowired
  private SubmissionRepository submissionRepository;
  private Locale locale = ENGLISH;
  private AppResources resources = new AppResources(SamplesStatusDialog.class, locale);
  private AppResources webResources = new AppResources(Constants.class, locale);
  private Submission submission;
  private SampleStatus status1 = SampleStatus.ANALYSED;
  private SampleStatus status2 = SampleStatus.DIGESTED;
  private Map<SubmissionSample, ComboBox<SampleStatus>> statusFields = new HashMap<>();

  /**
   * Before tests.
   */
  @BeforeEach
  @SuppressWarnings("unchecked")
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog.header = new H3();
    dialog.samples = mock(Grid.class);
    dialog.name = mock(Column.class);
    dialog.status = mock(Column.class);
    dialog.allStatus = new ComboBox<>();
    dialog.allStatus.setItems(SampleStatus.values());
    dialog.save = new Button();
    dialog.cancel = new Button();
    when(dialog.status(any())).then(i -> statusFields.get(i.getArgument(0)));
    submission = submissionRepository.findById(163L).get();
    submission.getSamples().forEach(sample -> {
      ComboBox<SampleStatus> comboBox = new ComboBox<>();
      comboBox.setItems(SampleStatus.values());
      statusFields.put(sample, comboBox);
    });
    presenter.init(dialog);
  }

  private void setFields() {
    statusFields.get(submission.getSamples().get(0)).setValue(status1);
    submission.getSamples().stream().skip(1)
        .forEach(sample -> statusFields.get(sample).setValue(status2));
  }

  @Test
  public void localeChange() {
    presenter.setSubmission(submission);
    presenter.localeChange(locale);
    dialog.samples.setItems(submission.getSamples());
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(sample.getStatus(), statusFields.get(sample).getValue());
      assertTrue(statusFields.get(sample).isRequiredIndicatorVisible());
    }
  }

  @Test
  @SuppressWarnings("unchecked")
  public void allStatus_ResetOnOpen() {
    dialog.allStatus.setValue(SampleStatus.ANALYSED);
    verify(dialog).addOpenedChangeListener(openedListener.capture());
    openedListener.getValue().onComponentEvent(mock(OpenedChangeEvent.class));
    assertNull(dialog.allStatus.getValue());
  }

  @Test
  public void setAllStatus() {
    presenter.localeChange(locale);
    presenter.setSubmission(submission);
    presenter.setAllStatus(SampleStatus.ANALYSED);
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(SampleStatus.ANALYSED, statusFields.get(sample).getValue());
    }
  }

  @Test
  public void setAllStatus_Null() {
    presenter.localeChange(locale);
    presenter.setSubmission(submission);
    presenter.setAllStatus(null);
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(SampleStatus.WAITING, statusFields.get(sample).getValue());
    }
  }

  @Test
  public void save_EmptySamples() {
    presenter.localeChange(locale);
    presenter.save();
    verify(service, never()).updateStatus(any());
  }

  @Test
  public void save_EmptyStatus_First() {
    presenter.setSubmission(submission);
    presenter.localeChange(locale);
    setFields();
    SubmissionSample sample = submission.getSamples().get(0);
    statusFields.get(sample).setValue(null);
    presenter.save();
    verify(service, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = presenter.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(0);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, statusFields.get(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void save_EmptyStatus_Second() {
    presenter.setSubmission(submission);
    presenter.localeChange(locale);
    setFields();
    SubmissionSample sample = submission.getSamples().get(1);
    statusFields.get(sample).setValue(null);
    presenter.save();
    verify(service, never()).updateStatus(any());
    List<BinderValidationStatus<SubmissionSample>> statuses = presenter.validateSamples();
    BinderValidationStatus<SubmissionSample> status = statuses.get(1);
    assertFalse(status.isOk());
    Optional<BindingValidationStatus<?>> optionalError =
        findValidationStatusByField(status, statusFields.get(sample));
    assertTrue(optionalError.isPresent());
    BindingValidationStatus<?> error = optionalError.get();
    assertEquals(Optional.of(webResources.message(REQUIRED)), error.getMessage());
  }

  @Test
  public void save() {
    presenter.setSubmission(submission);
    presenter.localeChange(locale);
    setFields();
    presenter.save();
    verify(service).updateStatus(submission.getSamples());
    verify(dialog).showNotification(resources.message(SAVED, submission.getExperiment()));
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void cancel() {
    presenter.cancel();
    verify(dialog).close();
  }

  @Test
  public void getSubmission() {
    presenter.setSubmission(submission);
    assertEquals(submission, presenter.getSubmission());
  }

  @Test
  public void setSubmission() {
    presenter.localeChange(locale);
    presenter.setSubmission(submission);
    dialog.samples.setItems(submission.getSamples());
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(sample.getStatus(), statusFields.get(sample).getValue());
      assertTrue(statusFields.get(sample).isRequiredIndicatorVisible());
    }
  }

  @Test
  public void setSubmission_BeforeLocalChange() {
    presenter.setSubmission(submission);
    presenter.localeChange(locale);
    dialog.samples.setItems(submission.getSamples());
    for (SubmissionSample sample : submission.getSamples()) {
      assertEquals(sample.getStatus(), statusFields.get(sample).getValue());
      assertTrue(statusFields.get(sample).isRequiredIndicatorVisible());
    }
  }
}
