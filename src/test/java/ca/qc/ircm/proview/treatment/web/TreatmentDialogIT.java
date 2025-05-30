package ca.qc.ircm.proview.treatment.web;

import static ca.qc.ircm.proview.submission.web.HistoryView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.submission.web.HistoryViewElement;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link TreatmentDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class TreatmentDialogIT extends AbstractBrowserTestCase {

  private void open() {
    openView(VIEW_NAME, "1");
    HistoryViewElement view = $(HistoryViewElement.class).waitForFirst();
    view.activities().select(3);
    view.view().click();
    $(TreatmentDialogElement.class).waitForFirst();
  }

  @BrowserTest
  public void fieldsExistence() {
    open();
    TreatmentDialogElement dialog = $(TreatmentDialogElement.class).waitForFirst();
    assertTrue(optional(dialog::header).isPresent());
    assertFalse(optional(dialog::deleted).isPresent());
    assertFalse(optional(dialog::protocol).isPresent());
    assertTrue(optional(dialog::fractionationType).isPresent());
    assertTrue(optional(dialog::date).isPresent());
    assertTrue(optional(dialog::samplesHeader).isPresent());
    assertTrue(optional(dialog::samples).isPresent());
  }
}
