package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionDialog.HEADER;
import static ca.qc.ircm.proview.submission.web.SubmissionDialog.ID;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.findChild;
import static ca.qc.ircm.proview.test.utils.VaadinTestUtils.validateIcon;
import static ca.qc.ircm.proview.web.WebConstants.EDIT;
import static ca.qc.ircm.proview.web.WebConstants.PRIMARY;
import static ca.qc.ircm.proview.web.WebConstants.PRINT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.test.config.AbstractViewTestCase;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.web.WebConstants;
import ca.qc.ircm.text.MessageResource;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ServiceTestAnnotations
public class SubmissionDialogTest extends AbstractViewTestCase {
  private SubmissionDialog dialog;
  @Mock
  private SubmissionDialogPresenter presenter;
  @Autowired
  private PrintSubmission printContent;
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
    dialog = new SubmissionDialog(presenter, printContent);
    dialog.init();
  }

  @Test
  public void init() {
    verify(presenter).init(dialog);
  }

  @Test
  public void printContent() {
    assertSame(printContent, findChild(dialog, PrintSubmission.class).orElse(null));
  }

  @Test
  public void styles() {
    assertEquals(ID, dialog.getId().orElse(""));
    assertTrue(dialog.header.getClassName().contains(HEADER));
    assertTrue(dialog.edit.getClassName().contains(EDIT));
    assertTrue(dialog.edit.getThemeName().contains(PRIMARY));
    validateIcon(VaadinIcon.EDIT.create(), dialog.edit.getIcon());
    assertTrue(dialog.print.getClassName().contains(PRINT));
    validateIcon(VaadinIcon.PRINT.create(), dialog.print.getIcon());
  }

  @Test
  public void labels() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void localeChange() {
    dialog.localeChange(mock(LocaleChangeEvent.class));
    Locale locale = Locale.FRENCH;
    final MessageResource resources = new MessageResource(SubmissionDialog.class, locale);
    final MessageResource webResources = new MessageResource(WebConstants.class, locale);
    when(ui.getLocale()).thenReturn(locale);
    dialog.localeChange(mock(LocaleChangeEvent.class));
    assertEquals(resources.message(HEADER), dialog.header.getText());
    assertEquals(webResources.message(EDIT), dialog.edit.getText());
    assertEquals(webResources.message(PRINT), dialog.print.getText());
    verify(presenter).localeChange(locale);
  }

  @Test
  public void edit() {
    dialog.edit.click();
    verify(presenter).edit();
  }

  @Test
  public void print() {
    dialog.print.click();
    verify(presenter).print();
  }

  @Test
  public void getSubmission() {
    when(presenter.getSubmission()).thenReturn(submission);
    Submission submission = dialog.getSubmission();
    verify(presenter).getSubmission();
    assertEquals(this.submission, submission);
  }

  @Test
  public void setSubmission() {
    dialog.setSubmission(submission);
    verify(presenter).setSubmission(submission);
  }
}