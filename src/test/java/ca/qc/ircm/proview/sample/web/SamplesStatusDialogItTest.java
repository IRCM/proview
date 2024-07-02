package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.sample.SubmissionSampleRepository;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SamplesStatusDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class SamplesStatusDialogItTest extends AbstractTestBenchTestCase {
  @Autowired
  private SubmissionSampleRepository repository;

  private SamplesStatusDialogElement open() {
    openView(VIEW_NAME);
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    view.submissions().experimentCell(1).click(0, 0, Keys.SHIFT);
    return view.statusDialog();
  }

  @Test
  public void fieldsExistence() throws Throwable {
    SamplesStatusDialogElement dialog = open();
    assertTrue(optional(() -> dialog.header()).isPresent());
    assertTrue(optional(() -> dialog.samples()).isPresent());
    assertTrue(optional(() -> dialog.samples().allStatus()).isPresent());
    assertTrue(optional(() -> dialog.save()).isPresent());
    assertTrue(optional(() -> dialog.cancel()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    SamplesStatusDialogElement dialog = open();
    Locale locale = currentLocale();
    dialog.samples().status(0).selectByText(SampleStatus.ANALYSED.getLabel(locale));
    dialog.samples().status(1).selectByText(SampleStatus.DIGESTED.getLabel(locale));
    dialog.save().click();
    assertFalse(dialog.isOpen());
    assertEquals(SampleStatus.ANALYSED, repository.findById(640L).get().getStatus());
    assertEquals(SampleStatus.DIGESTED, repository.findById(641L).get().getStatus());
    assertEquals(SampleStatus.WAITING, repository.findById(642L).get().getStatus());
  }

  @Test
  public void cancel() throws Throwable {
    SamplesStatusDialogElement dialog = open();
    dialog.cancel().click();
    assertFalse(dialog.isOpen());
  }
}
