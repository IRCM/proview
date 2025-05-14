package ca.qc.ircm.proview.msanalysis.web;

import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.HistoryViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link MsAnalysisDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class MsAnalysisDialogIT extends AbstractBrowserTestCase {

  private void open() {
    openView(VIEW_NAME, "1");
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().select(5);
    view.view().click();
    $(MsAnalysisDialogElement.class).waitForFirst();
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    MsAnalysisDialogElement dialog = $(MsAnalysisDialogElement.class).waitForFirst();
    assertTrue(optional(dialog::header).isPresent());
    assertFalse(optional(dialog::deleted).isPresent());
    assertTrue(optional(dialog::instrument).isPresent());
    assertTrue(optional(dialog::source).isPresent());
    assertTrue(optional(dialog::date).isPresent());
    assertTrue(optional(dialog::acquisitionsHeader).isPresent());
    assertTrue(optional(dialog::acquisitions).isPresent());
  }
}
