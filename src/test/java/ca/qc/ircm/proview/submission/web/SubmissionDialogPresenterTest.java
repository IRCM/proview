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
import static ca.qc.ircm.proview.security.Permission.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.security.AuthenticatedUser;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractKaribuTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Tests for {@link SubmissionDialogPresenter}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionDialogPresenterTest extends AbstractKaribuTestCase {
  @Autowired
  private SubmissionDialogPresenter presenter;
  @MockBean
  private SubmissionService service;
  @MockBean
  private AuthenticatedUser authenticatedUser;
  @Mock
  private SubmissionDialog dialog;
  @Mock
  private Submission submission;
  private Locale locale = ENGLISH;
  private MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private LocalDate dataAvailableDate = LocalDate.of(2019, 8, 2);

  /**
   * Before tests.
   */
  @BeforeEach
  public void beforeTest() {
    ui.setLocale(locale);
    dialog.printContent = mock(PrintSubmission.class);
    dialog.instrument = new ComboBox<>();
    dialog.instrument.setItems(MassDetectionInstrument.platformChoices());
    dialog.dataAvailableDate = new DatePicker();
    dialog.edit = new Button();
    dialog.print = new Button();
    presenter.init(dialog);
  }

  private void setFields() {
    dialog.instrument.setValue(instrument);
    dialog.dataAvailableDate.setValue(dataAvailableDate);
  }

  @Test
  public void save() {
    Submission submission = new Submission();
    presenter.localeChange(locale);
    presenter.setSubmission(submission);
    setFields();

    presenter.save();

    verify(service).update(submission, null);
    assertEquals(instrument, submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void save_NullInstrument() {
    Submission submission = new Submission();
    presenter.localeChange(locale);
    presenter.setSubmission(submission);
    setFields();
    dialog.instrument.setValue(MassDetectionInstrument.NULL);

    presenter.save();

    verify(service).update(submission, null);
    assertNull(submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
    verify(dialog).close();
    verify(dialog).fireSavedEvent();
  }

  @Test
  public void edit() {
    long id = 12;
    when(submission.getId()).thenReturn(id);
    presenter.setSubmission(submission);
    presenter.edit();
    assertCurrentView(SubmissionView.class, id);
    verify(dialog).close();
  }

  @Test
  public void edit_New() {
    presenter.edit();
    assertCurrentView(SubmissionView.class);
    verify(dialog).close();
  }

  @Test
  public void print() {
    long id = 12;
    when(submission.getId()).thenReturn(id);
    presenter.setSubmission(submission);
    presenter.print();
    assertCurrentView(PrintSubmissionView.class, id);
    verify(dialog).close();
  }

  @Test
  public void print_New() {
    assertThrows(IllegalArgumentException.class, () -> presenter.print());
    assertCurrentView(SubmissionsView.class);
    verify(dialog, never()).close();
  }

  @Test
  public void getSubmission() {
    presenter.setSubmission(submission);
    assertSame(submission, presenter.getSubmission());
  }

  @Test
  public void setSubmission() {
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);
    Submission submission = new Submission();
    submission.setId(1L);
    submission.setInstrument(instrument);
    submission.setDataAvailableDate(dataAvailableDate);
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    verify(authenticatedUser).hasPermission(submission, WRITE);
    assertEquals(instrument, dialog.instrument.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
    assertTrue(dialog.edit.isEnabled());
  }

  @Test
  public void setSubmission_ReadOnly() {
    Submission submission = new Submission();
    submission.setId(1L);
    submission.setInstrument(instrument);
    submission.setDataAvailableDate(dataAvailableDate);
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    verify(authenticatedUser).hasPermission(submission, WRITE);
    assertEquals(instrument, dialog.instrument.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
    assertFalse(dialog.edit.isEnabled());
  }

  @Test
  public void setSubmission_BeforeLocalChange() {
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);
    Submission submission = new Submission();
    submission.setId(1L);
    submission.setInstrument(instrument);
    submission.setDataAvailableDate(dataAvailableDate);

    presenter.setSubmission(submission);
    presenter.localeChange(locale);

    verify(authenticatedUser).hasPermission(submission, WRITE);
    assertEquals(instrument, dialog.instrument.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
    assertTrue(dialog.edit.isEnabled());
  }

  @Test
  public void setSubmission_Null() {
    when(authenticatedUser.hasPermission(any(), any())).thenReturn(true);
    presenter.localeChange(locale);

    presenter.setSubmission(null);

    verify(authenticatedUser, never()).hasPermission(submission, WRITE);
    assertEquals(MassDetectionInstrument.NULL, dialog.instrument.getValue());
    assertEquals(null, dialog.dataAvailableDate.getValue());
    assertTrue(dialog.edit.isEnabled());
  }
}
