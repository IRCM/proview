package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.Constants.ENGLISH;
import static ca.qc.ircm.proview.Constants.FRENCH;
import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.files.web.GuidelinesViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.web.ProfileViewElement;
import ca.qc.ircm.proview.user.web.UsersViewElement;
import java.util.Locale;
import org.junit.jupiter.api.Test;
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
  public void security_Anonymous() {
    open();

    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void fieldsExistence_User() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(view::applicationName).isPresent());
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::drawerToggle).isPresent());
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::profile).isPresent());
    assertFalse(optional(view::users).isPresent());
    assertFalse(optional(view::exitSwitchUser).isPresent());
    assertTrue(optional(view::signout).isPresent());
    assertTrue(optional(view::changeLanguage).isPresent());
    assertTrue(optional(view::contact).isPresent());
    assertTrue(optional(view::guidelines).isPresent());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void fieldsExistence_Manager() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(view::applicationName).isPresent());
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::drawerToggle).isPresent());
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::profile).isPresent());
    assertTrue(optional(view::users).isPresent());
    assertFalse(optional(view::exitSwitchUser).isPresent());
    assertTrue(optional(view::signout).isPresent());
    assertTrue(optional(view::changeLanguage).isPresent());
    assertTrue(optional(view::contact).isPresent());
    assertTrue(optional(view::guidelines).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(view::applicationName).isPresent());
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::drawerToggle).isPresent());
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::profile).isPresent());
    assertTrue(optional(view::users).isPresent());
    assertFalse(optional(view::exitSwitchUser).isPresent());
    assertTrue(optional(view::signout).isPresent());
    assertTrue(optional(view::changeLanguage).isPresent());
    assertTrue(optional(view::contact).isPresent());
    assertTrue(optional(view::guidelines).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Runas() {
    open();
    $(ViewLayoutElement.class).waitForFirst().users().click();
    UsersViewElement usersView = $(UsersViewElement.class).waitForFirst();
    usersView.users().select(1);
    usersView.switchUser().click();
    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(view::applicationName).isPresent());
    assertTrue(optional(view::header).isPresent());
    assertTrue(optional(view::drawerToggle).isPresent());
    assertTrue(optional(view::submissions).isPresent());
    assertTrue(optional(view::profile).isPresent());
    assertTrue(optional(view::users).isPresent());
    assertTrue(optional(view::exitSwitchUser).isPresent());
    assertTrue(optional(view::signout).isPresent());
    assertTrue(optional(view::changeLanguage).isPresent());
    assertTrue(optional(view::contact).isPresent());
    assertTrue(optional(view::guidelines).isPresent());
  }

  @Test
  public void submissions() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.submissions().click();
    $(SubmissionsViewElement.class).waitForFirst();
  }

  @Test
  public void profile() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.profile().click();
    $(ProfileViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void users() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.users().click();
    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void exitSwitchUser() {
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
    assertFalse(optional(viewAfterExitSwitchUser::exitSwitchUser).isPresent());
    assertTrue(optional(viewAfterExitSwitchUser::users).isPresent());
  }

  @Test
  public void signout() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.signout().click();
    $(SigninViewElement.class).waitForFirst();
  }

  @Test
  public void changeLanguage() {
    open();
    final Locale before = currentLocale();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.changeLanguage().click();
    $(ContactViewElement.class).waitForFirst();
    assertEquals(ENGLISH.equals(before) ? FRENCH : ENGLISH, currentLocale());
    assertEquals(viewUrl(VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void contact() {
    openView(GuidelinesView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.contact().click();
    $(ContactViewElement.class).waitForFirst();
  }

  @Test
  public void guidelines() {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).waitForFirst();
    view.guidelines().click();
    $(GuidelinesViewElement.class).waitForFirst();
  }
}
