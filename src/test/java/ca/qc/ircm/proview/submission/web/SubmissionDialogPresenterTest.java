package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionService;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.Before;
import org.junit.Ignore;
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
  private LocalDate sampleDeliveryDate = LocalDate.of(2019, 7, 28);
  private LocalDate digestionDate = LocalDate.of(2019, 7, 30);
  private LocalDate analysisDate = LocalDate.of(2019, 8, 1);
  private LocalDate dataAvailableDate = LocalDate.of(2019, 8, 2);

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog.header = new H2();
    dialog.printContent = mock(PrintSubmission.class);
    dialog.sampleDeliveryDate = new DatePicker();
    dialog.digestionDate = new DatePicker();
    dialog.analysisDate = new DatePicker();
    dialog.dataAvailableDate = new DatePicker();
    dialog.edit = new Button();
    dialog.print = new Button();
    presenter.init(dialog);
  }

  private void setFields() {
    dialog.sampleDeliveryDate.setValue(sampleDeliveryDate);
    dialog.digestionDate.setValue(digestionDate);
    dialog.analysisDate.setValue(analysisDate);
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
    assertEquals(sampleDeliveryDate, submission.getSampleDeliveryDate());
    assertEquals(digestionDate, submission.getDigestionDate());
    assertEquals(analysisDate, submission.getAnalysisDate());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
  }

  @Test
  public void edit() {
    long id = 12;
    when(submission.getId()).thenReturn(id);
    presenter.setSubmission(submission);
    presenter.edit();
    verify(ui).navigate(SubmissionView.class, id);
  }

  @Test
  public void edit_New() {
    presenter.edit();
    verify(ui).navigate(SubmissionView.class, null);
  }

  @Test
  @Ignore("Does nothing right now")
  public void print() {
  }

  @Test
  public void getSubmission() {
    presenter.setSubmission(submission);
    assertSame(submission, presenter.getSubmission());
  }

  @Test
  public void setSubmission() {
    Submission submission = new Submission(1L);
    String experiment = "test submission";
    submission.setExperiment(experiment);
    submission.setSampleDeliveryDate(sampleDeliveryDate);
    submission.setDigestionDate(digestionDate);
    submission.setAnalysisDate(analysisDate);
    submission.setDataAvailableDate(dataAvailableDate);
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    assertEquals(experiment, dialog.header.getText());
    assertEquals(sampleDeliveryDate, dialog.sampleDeliveryDate.getValue());
    assertEquals(digestionDate, dialog.digestionDate.getValue());
    assertEquals(analysisDate, dialog.analysisDate.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
  }

  @Test
  public void setSubmission_BeforeLocalChange() {
    Submission submission = new Submission(1L);
    String name = "test submission";
    submission.setExperiment(name);
    submission.setSampleDeliveryDate(sampleDeliveryDate);
    submission.setDigestionDate(digestionDate);
    submission.setAnalysisDate(analysisDate);
    submission.setDataAvailableDate(dataAvailableDate);

    presenter.setSubmission(submission);
    presenter.localeChange(locale);

    assertEquals(name, dialog.header.getText());
    assertEquals(sampleDeliveryDate, dialog.sampleDeliveryDate.getValue());
    assertEquals(digestionDate, dialog.digestionDate.getValue());
    assertEquals(analysisDate, dialog.analysisDate.getValue());
    assertEquals(dataAvailableDate, dialog.dataAvailableDate.getValue());
  }

  @Test
  public void setSubmission_NullFields() {
    Submission submission = new Submission();
    presenter.localeChange(locale);

    presenter.setSubmission(submission);

    assertEquals(null, dialog.sampleDeliveryDate.getValue());
    assertEquals(null, dialog.digestionDate.getValue());
    assertEquals(null, dialog.analysisDate.getValue());
    assertEquals(null, dialog.dataAvailableDate.getValue());
  }
}
