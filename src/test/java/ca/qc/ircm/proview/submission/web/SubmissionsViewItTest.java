package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialogElement;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.button.ButtonVariant;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link SubmissionsView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class SubmissionsViewItTest extends AbstractTestBenchTestCase {
  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionsView.class);
  private static final String SAMPLES_STATUS_DIALOG_PREFIX =
      messagePrefix(SamplesStatusDialog.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewItTest.class);
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void title() throws Throwable {
    open();

    Locale locale = currentLocale();
    String applicationName =
        messageSource.getMessage(CONSTANTS_PREFIX + APPLICATION_NAME, null, locale);
    assertEquals(
        messageSource.getMessage(MESSAGES_PREFIX + TITLE, new Object[] { applicationName }, locale),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
    assertFalse(optional(() -> view.editStatus()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
    assertTrue(optional(() -> view.editStatus()).isPresent());
    assertTrue(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void hide() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    Submission submission = repository.findById(164L).orElseThrow();
    assertTrue(submission.isHidden());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void show() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getAttribute("theme")
        .equals(ButtonVariant.LUMO_SUCCESS.getVariantName()));

    Submission submission = repository.findById(164L).orElseThrow();
    assertFalse(submission.isHidden());
  }

  @Test
  public void view() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().select(0);
    view.view().click();

    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    assertEquals("POLR3B-Flag", dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void statusDialog() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.SHIFT);

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    assertEquals(messageSource.getMessage(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
        new Object[] { "POLR3B-Flag" }, currentLocale()), dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_Grid() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.ALT);

    $(HistoryViewElement.class).waitForFirst();
    assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @Test
  public void add() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.add().click();

    $(SubmissionViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.editStatus().click();

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    assertEquals(messageSource.getMessage(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
        new Object[] { "POLR3B-Flag" }, currentLocale()), dialog.header().getText());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() throws Throwable {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.history().click();

    $(HistoryViewElement.class).waitForFirst();
    assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
