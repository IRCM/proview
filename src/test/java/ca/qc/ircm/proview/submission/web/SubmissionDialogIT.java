package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.msanalysis.MassDetectionInstrument;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import com.vaadin.testbench.BrowserTest;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionDialog}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionDialogIT extends AbstractBrowserTestCase {

  private static final String MASS_DETECTION_INSTRUMENT_PREFIX = messagePrefix(
      MassDetectionInstrument.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionDialogIT.class);
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;
  private final MassDetectionInstrument instrument = MassDetectionInstrument.Q_EXACTIVE;
  private final LocalDate dataAvailableDate = LocalDate.now().minusDays(1);

  private SubmissionDialogElement openDialog(int row) {
    openView(VIEW_NAME);
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    view.submissions().select(row);
    view.view().click();
    return view.dialog();
  }

  private void setFields(SubmissionDialogElement dialog) {
    Locale locale = this.currentLocale();
    dialog.instrument().selectByText(
        messageSource.getMessage(MASS_DETECTION_INSTRUMENT_PREFIX + instrument.name(), null,
            locale));
    dialog.dataAvailableDate().setDate(dataAvailableDate);
  }

  @BrowserTest
  public void fieldsExistence_User() {
    SubmissionDialogElement dialog = openDialog(0);
    assertTrue(optional(dialog::header).isPresent());
    assertTrue(optional(dialog::printSubmission).isPresent());
    assertFalse(optional(dialog::instrument).isPresent());
    assertFalse(optional(dialog::dataAvailableDate).isPresent());
    assertFalse(optional(dialog::save).isPresent());
    assertTrue(optional(dialog::print).isPresent());
    assertTrue(optional(dialog::edit).isPresent());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() {
    SubmissionDialogElement dialog = openDialog(0);
    assertTrue(optional(dialog::header).isPresent());
    assertTrue(optional(dialog::printSubmission).isPresent());
    assertTrue(optional(dialog::instrument).isPresent());
    assertTrue(optional(dialog::dataAvailableDate).isPresent());
    assertTrue(optional(dialog::save).isPresent());
    assertTrue(optional(dialog::print).isPresent());
    assertTrue(optional(dialog::edit).isPresent());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void update() {
    SubmissionDialogElement dialog = openDialog(0);

    setFields(dialog);

    dialog.clickSave();
    assertFalse(dialog.isOpen());
    Submission submission = repository.findById(164L).orElseThrow();
    Assertions.assertEquals(instrument, submission.getInstrument());
    Assertions.assertEquals(dataAvailableDate, submission.getDataAvailableDate());
  }

  @BrowserTest
  public void print() {
    SubmissionDialogElement dialog = openDialog(0);

    dialog.clickPrint();

    $(PrintSubmissionViewElement.class).waitForFirst();
    Assertions.assertEquals(viewUrl(PrintSubmissionView.VIEW_NAME, "164"),
        getDriver().getCurrentUrl());
  }

  @BrowserTest
  public void edit() {
    SubmissionDialogElement dialog = openDialog(0);

    dialog.clickEdit();

    $(SubmissionViewElement.class).waitForFirst();
    Assertions.assertEquals(viewUrl(SubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
