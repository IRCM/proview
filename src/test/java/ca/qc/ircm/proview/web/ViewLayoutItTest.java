package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.files.web.GuidelinesViewElement;
import ca.qc.ircm.proview.submission.web.HistoryView;
import ca.qc.ircm.proview.submission.web.HistoryViewElement;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.PrintSubmissionViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionDialogElement;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.web.ProfileViewElement;
import ca.qc.ircm.proview.user.web.UsersViewElement;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link ViewLayout}.
 */
@TestBenchTestAnnotations
@WithUserDetails("christopher.anderson@ircm.qc.ca")
public class ViewLayoutItTest extends AbstractTestBenchTestCase {
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
  public void fieldsExistence_User() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertFalse(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void fieldsExistence_Manager() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Runas() throws Throwable {
    open();
    $(ViewLayoutElement.class).waitForFirst().users().click();
    UsersViewElement usersView = $(UsersViewElement.class).waitForFirst();
    usersView.users().select(1);
    usersView.switchUser().click();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertTrue(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
    assertFalse(optional(() -> view.history()).isPresent());
  }

  @Test
  public void submissions() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @Test
  public void profile() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.profile().click();
    $(ProfileViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void users() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.users().click();
    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void exitSwitchUser() throws Throwable {
    open();
    $(ViewLayoutElement.class).waitForFirst().users().click();
    UsersViewElement usersView = $(UsersViewElement.class).waitForFirst();
    usersView.users().select(1);
    usersView.switchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement view = $(ViewLayoutElement.class).first();
    view.contact().click();
    view.exitSwitchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewAfterExitSwitchUser = $(ViewLayoutElement.class).first();
    assertFalse(optional(() -> viewAfterExitSwitchUser.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> viewAfterExitSwitchUser.users()).isPresent());
  }

  @Test
  public void signout() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.signout().click();
    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void changeLanguage() throws Throwable {
    open();
    final Locale before = currentLocale();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.changeLanguage().click();
    $(ContactViewElement.class).waitForFirst();
    assertEquals(ENGLISH.equals(before) ? FRENCH : ENGLISH, currentLocale());
  }

  @Test
  public void contact() throws Throwable {
    openView(GuidelinesView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.contact().click();
    $(ContactViewElement.class).waitForFirst();
  }

  @Test
  public void guidelines() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
  }

  @Test
  public void add() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).waitForFirst();
    submissionsView.add().click();
    $(SubmissionViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.add()).isPresent());
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
    assertEquals("true", view.add().getAttribute("hidden"));
  }

  @Test
  public void edit() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).waitForFirst();
    submissionsView.submissions().experimentCell(0).doubleClick();
    SubmissionDialogElement submissionDialog = $(SubmissionDialogElement.class).waitForFirst();
    submissionDialog.clickEdit();
    $(SubmissionViewElement.class).waitForFirst();
    assertEquals(viewUrl(SubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.edit()).isPresent());
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
    assertEquals("true", view.edit().getAttribute("hidden"));
  }

  @Test
  public void print() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).waitForFirst();
    submissionsView.submissions().experimentCell(0).doubleClick();
    SubmissionDialogElement submissionDialog = $(SubmissionDialogElement.class).waitForFirst();
    submissionDialog.clickPrint();
    $(PrintSubmissionViewElement.class).waitForFirst();
    assertEquals(viewUrl(PrintSubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.print()).isPresent());
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
    assertEquals("true", view.print().getAttribute("hidden"));
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void history() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).waitForFirst();
    submissionsView.submissions().experimentCell(0).click(0, 0, Keys.ALT);
    $(HistoryViewElement.class).waitForFirst();
    assertEquals(viewUrl(HistoryView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.history()).isPresent());
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
    assertEquals("true", view.history().getAttribute("hidden"));
  }
}
