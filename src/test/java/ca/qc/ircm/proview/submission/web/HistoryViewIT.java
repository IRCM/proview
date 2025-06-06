package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.testbench.BrowserTest;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link HistoryView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewIT extends AbstractBrowserTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(HistoryView.class);
  private static final String TREATMENT_TYPE_PREFIX = messagePrefix(TreatmentType.class);
  private static final String MS_ANALYSIS_DIALOG_PREFIX = messagePrefix(MsAnalysisDialog.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @Autowired
  private MessageSource messageSource;

  private void open() {
    openView(VIEW_NAME, "1");
  }

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @BrowserTest
  public void title() {
    open();

    Locale locale = currentLocale();
    String applicationName = messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null,
        locale);
    Assertions.assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[]{applicationName}, locale),
        getDriver().getTitle());
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    assertTrue(optional(view::activities).isPresent());
  }

  @BrowserTest
  public void dialog() {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().select(6);
    view.view().click();
    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals("G100429", dialog.header().getText());
  }

  @BrowserTest
  public void msAnalysisDialog() {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().select(5);
    view.view().click();
    MsAnalysisDialogElement dialog = view.msAnalysisDialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals(
        messageSource.getMessage(MS_ANALYSIS_DIALOG_PREFIX + MsAnalysisDialog.HEADER, null,
            currentLocale()), dialog.header().getText());
  }

  @BrowserTest
  public void treatmentDialog() {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().select(0);
    view.view().click();
    TreatmentDialogElement dialog = view.treatmentDialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals(
        messageSource.getMessage(TREATMENT_TYPE_PREFIX + TreatmentType.TRANSFER.name(), null,
            currentLocale()), dialog.header().getText());
  }
}
