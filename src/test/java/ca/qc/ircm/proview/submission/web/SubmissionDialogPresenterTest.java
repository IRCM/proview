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

import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionDialogPresenterTest extends AbstractViewTestCase {
  @Autowired
  private SubmissionDialogPresenter presenter;
  @MockBean
  private SubmissionService service;
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
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog.header = new H2();
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
    verify(ui).navigate(SubmissionView.class, id);
    verify(dialog).close();
  }

  @Test
  public void edit_New() {
    presenter.edit();
    verify(ui).navigate(SubmissionView.class, null);
    verify(dialog).close();
  }

  @Test
  public void print() {
    long id = 12;
    when(submission.getId()).thenReturn(id);
    presenter.setSubmission(submission);
    presenter.print();
    verify(ui).navigate(PrintSubmissionView.class, id);
    verify(dialog).close();
  }

  @Test
  public void print_New() {
    presenter.print();
    verify(ui).navigate(PrintSubmissionView.class, null);
    verify(dialog).close();
  }

  @Test
  public void getSubmission() {
    presenter.setSubmission(submission);
    assertSame(submission, presenter.getSubmission());
  }

  @Test
  public void setSubmission() {
    Submission submission = new Submission();
    submission.setInstrument(instrument);
    submission.setDataAvailableDate(dataAvailableDate);
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    assertEquals(instrument, dialog.instrument.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
  }

  @Test
  public void setSubmission_BeforeLocalChange() {
    Submission submission = new Submission();
    submission.setInstrument(instrument);
    submission.setDataAvailableDate(dataAvailableDate);

    presenter.setSubmission(submission);
    presenter.localeChange(locale);

    assertEquals(instrument, dialog.instrument.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
  }

  @Test
  public void setSubmission_NullFields() {
    Submission submission = new Submission();
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    assertEquals(MassDetectionInstrument.NULL, dialog.instrument.getValue());
    assertEquals(null, dialog.dataAvailableDate.getValue());
  }
}
