package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.ServiceTestAnnotations;
import com.vaadin.testbench.unit.SpringUIUnitTest;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionDialog}.
 */
@ServiceTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionDialogIT extends SpringUIUnitTest {

  private static final String MASS_DETECTION_INSTRUMENT_PREFIX = messagePrefix(
      MassDetectionInstrument.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogIT.class);
  @Autowired
  private SubmissionRepository repository;
  private final MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private final LocalDate dataAvailableDate = LocalDate.now().minusDays(1);

  private SubmissionDialog openDialog(int row) {
    SubmissionsView view = navigate(SubmissionsView.class);
    test(view.submissions).select(row);
    test(view.view).click();
    return $(SubmissionDialog.class).single();
  }

  private void setFields(SubmissionDialog dialog) {
    test(dialog.instrument).selectItem(
        dialog.getTranslation(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name()));
    test(dialog.dataAvailableDate).setValue(dataAvailableDate);
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void update() {
    SubmissionDialog dialog = openDialog(0);

    setFields(dialog);

    test(dialog.save).click();
    assertFalse(dialog.isOpened());
    Submission submission = repository.findById(164L).orElseThrow();
    assertEquals(instrument, submission.getInstrument());
    assertEquals(dataAvailableDate, submission.getDataAvailableDate());
  }

  @Test
  public void print() {
    SubmissionDialog dialog = openDialog(0);

    test(dialog.print).click();

    PrintSubmissionView printView = $(PrintSubmissionView.class).single();
    assertEquals(164, printView.printContent.getSubmission().getId());
  }

  @Test
  public void edit() {
    SubmissionDialog dialog = openDialog(0);

    test(dialog.edit).click();

    SubmissionView submissionView = $(SubmissionView.class).single();
    assertEquals(164, submissionView.getSubmission().getId());
  }
}
