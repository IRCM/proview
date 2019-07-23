package ca.qc.ircm.proview.submission.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import java.util.Locale;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionDialogPresenterTest extends AbstractViewTestCase {
  @Autowired
  private SubmissionDialogPresenter presenter;
  @Mock
  private SubmissionDialog dialog;
  @Mock
  private Submission submission;
  private Locale locale = Locale.ENGLISH;
  private MessageResource resources = new MessageResource(SubmissionDialog.class, locale);
  private MessageResource webResources = new MessageResource(WebConstants.class, locale);

  /**
   * Before tests.
   */
  @Before
  public void beforeTest() {
    when(ui.getLocale()).thenReturn(locale);
    dialog.header = new H2();
    dialog.printContent = mock(PrintSubmission.class);
    dialog.edit = new Button();
    dialog.print = new Button();
    presenter.init(dialog);
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
    String name = "test submission";
    when(submission.getName()).thenReturn(name);
    presenter.setSubmission(submission);
    assertEquals(name, dialog.header.getText());
  }
}
