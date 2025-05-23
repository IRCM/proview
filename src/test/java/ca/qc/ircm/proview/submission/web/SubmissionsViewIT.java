package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.Constants.messagePrefix;
import static ca.qc.ircm.proview.submission.web.SubmissionsView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialog;
import ca.qc.ircm.proview.sample.web.SamplesStatusDialogElement;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.AbstractBrowserTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.testbench.BrowserTest;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
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
public class SubmissionsViewIT extends AbstractBrowserTestCase {

  private static final String MESSAGES_PREFIX = messagePrefix(SubmissionsView.class);
  private static final String SAMPLES_STATUS_DIALOG_PREFIX = messagePrefix(
      SamplesStatusDialog.class);
  private static final String CONSTANTS_PREFIX = messagePrefix(Constants.class);
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SubmissionsViewIT.class);
  @Autowired
  private SubmissionRepository repository;
  @Autowired
  private MessageSource messageSource;
  @Value("${spring.application.name}")
  private String applicationName;

  private void open() {
    openView(VIEW_NAME);
  }

  @BrowserTest
  @WithAnonymousUser
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
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
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::add).isPresent());
    assertFalse(optional(view::editStatus).isPresent());
    assertFalse(optional(view::history).isPresent());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::add).isPresent());
    assertTrue(optional(view::editStatus).isPresent());
    assertTrue(optional(view::history).isPresent());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void hide() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getDomAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    Submission submission = repository.findById(164L).orElseThrow();
    assertTrue(submission.isHidden());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void show() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();
    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getDomAttribute("theme")
        .equals(ButtonVariant.LUMO_ERROR.getVariantName()));

    view.submissions().visible(0).click();
    waitUntil(driver -> view.submissions().visible(0).getDomAttribute("theme")
        .equals(ButtonVariant.LUMO_SUCCESS.getVariantName()));

    Submission submission = repository.findById(164L).orElseThrow();
    assertFalse(submission.isHidden());
  }

  @BrowserTest
  public void view() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().select(0);
    view.view().click();

    SubmissionDialogElement dialog = view.dialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals("POLR3B-Flag", dialog.header().getText());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void statusDialog() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.SHIFT);

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals(
        messageSource.getMessage(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
            new Object[]{"POLR3B-Flag"}, currentLocale()), dialog.header().getText());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void history_Grid() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click(0, 0, Keys.ALT);

    $(HistoryViewElement.class).waitForFirst();
    Assertions.assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }

  @BrowserTest
  public void add() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.add().click();

    $(SubmissionViewElement.class).waitForFirst();
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void editStatus() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.editStatus().click();

    SamplesStatusDialogElement dialog = view.statusDialog();
    assertTrue(dialog.isOpen());
    Assertions.assertEquals(
        messageSource.getMessage(SAMPLES_STATUS_DIALOG_PREFIX + SamplesStatusDialog.HEADER,
            new Object[]{"POLR3B-Flag"}, currentLocale()), dialog.header().getText());
  }

  @BrowserTest
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() {
    open();
    SubmissionsViewElement view = $(SubmissionsViewElement.class).waitForFirst();

    view.submissions().experimentCell(0).click();
    view.history().click();

    $(HistoryViewElement.class).waitForFirst();
    Assertions.assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
  }
}
