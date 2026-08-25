package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.msanalysis.web.MsAnalysisDialog;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import ca.qc.ircm.proview.treatment.TreatmentType;
import ca.qc.ircm.proview.treatment.web.TreatmentDialog;
import ca.qc.ircm.proview.web.SigninView;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link HistoryView}.
 */
@ServiceTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class HistoryViewIT extends SpringBrowserlessTest {

  private static final String TREATMENT_TYPE_PREFIX = messagePrefix(TreatmentType.class);
  private static final String MS_ANALYSIS_DIALOG_PREFIX = messagePrefix(MsAnalysisDialog.class);

  @Test
  @WithAnonymousUser
  public void security_Anonymous() {
    navigate(VIEW_NAME + "/1", SigninView.class);
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() {
    navigate(VIEW_NAME + "/1", AccessDeniedView.class);
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() {
    navigate(VIEW_NAME + "/1", AccessDeniedView.class);
  }

  @Test
  public void dialog() {
    HistoryView view = navigate(HistoryView.class, 1L);
    test(view.activities).select(6);
    test(view.view).click();
    SubmissionDialog dialog = $(SubmissionDialog.class).single();
    assertTrue(dialog.isOpened());
    Assertions.assertEquals("G100429", dialog.getHeaderTitle());
  }

  @Test
  public void msAnalysisDialog() {
    HistoryView view = navigate(HistoryView.class, 1L);
    test(view.activities).select(5);
    test(view.view).click();
    MsAnalysisDialog dialog = $(MsAnalysisDialog.class).single();
    assertTrue(dialog.isOpened());
    Assertions.assertEquals(
        dialog.getTranslation(MS_ANALYSIS_DIALOG_PREFIX + MsAnalysisDialog.HEADER),
        dialog.getHeaderTitle());
  }

  @Test
  public void treatmentDialog() {
    HistoryView view = navigate(HistoryView.class, 1L);
    test(view.activities).select(0);
    test(view.view).click();
    TreatmentDialog dialog = $(TreatmentDialog.class).single();
    assertTrue(dialog.isOpened());
    Assertions.assertEquals(
        dialog.getTranslation(TREATMENT_TYPE_PREFIX + TreatmentType.TRANSFER.name()),
        dialog.getHeaderTitle());
  }
}
