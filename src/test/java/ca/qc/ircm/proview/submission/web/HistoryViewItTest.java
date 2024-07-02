package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialogElement;
import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.treatment.web.TreatmentDialogElement;
import ca.qc.ircm.proview.web.SigninViewElement;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link HistoryView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewItTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(VIEW_NAME, "1");
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(HistoryView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.activities()).isPresent());
  }

  @Test
  public void dialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(6).click();
    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    assertEquals("G100429", dialog.header().getText());
  }

  @Test
  public void msAnalysisDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(5).click();
    MsAnalysisDialogElement dialog = view.msAnalysisDialog();
    assertTrue(dialog.isOpen());
    assertEquals(resources(MsAnalysisDialog.class).message(MsAnalysisDialog.HEADER),
        dialog.header().getText());
  }

  @Test
  public void treatmentDialog() throws Throwable {
    open();
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().view(0).click();
    TreatmentDialogElement dialog = view.treatmentDialog();
    assertTrue(dialog.isOpen());
    assertEquals(TreatmentType.TRANSFER.getLabel(currentLocale()), dialog.header().getText());
  }
}
