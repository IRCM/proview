/*
 * Copyright (c) 2006 Institut de recherches cliniques de Montreal (IRCM)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.qc.ircm.proview.web;

import static ca.qc.ircm.proview.web.ContactView.VIEW_NAME;
import static ca.qc.ircm.proview.web.ViewLayout.ID;
import static ca.qc.ircm.proview.web.WebConstants.ENGLISH;
import static ca.qc.ircm.proview.web.WebConstants.FRENCH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.files.web.GuidelinesView;
import ca.qc.ircm.proview.submission.web.PrintSubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionDialog;
import ca.qc.ircm.proview.submission.web.SubmissionDialogElement;
import ca.qc.ircm.proview.submission.web.SubmissionView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.web.ProfileView;
import ca.qc.ircm.proview.user.web.UsersView;
import ca.qc.ircm.proview.user.web.UsersViewElement;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class ViewLayoutItTest extends AbstractTestBenchTestCase {
  private void open() {
    openView(VIEW_NAME);
  }

  @Test
  @WithAnonymousUser
  public void security_Anonymous() throws Throwable {
    open();

    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void fieldsExistence_User() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertFalse(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void fieldsExistence_Manager() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void fieldsExistence_Admin() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
  }

  @Test
  public void fieldsExistence_Runas() throws Throwable {
    openView(UsersView.VIEW_NAME);
    SigninViewElement signinView = $(SigninViewElement.class).id(SigninView.ID);
    signinView.getUsernameField().setValue("proview@ircm.qc.ca");
    signinView.getPasswordField().setValue("password");
    signinView.getSubmitButton().click();
    UsersViewElement usersView = $(UsersViewElement.class).id(UsersView.ID);
    usersView.clickUser(1);
    usersView.clickSwitchUser();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    assertTrue(optional(() -> view.submissions()).isPresent());
    assertTrue(optional(() -> view.profile()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertTrue(optional(() -> view.exitSwitchUser()).isPresent());
    assertTrue(optional(() -> view.signout()).isPresent());
    assertTrue(optional(() -> view.changeLanguage()).isPresent());
    assertTrue(optional(() -> view.contact()).isPresent());
    assertTrue(optional(() -> view.guidelines()).isPresent());
    assertFalse(optional(() -> view.print()).isPresent());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void submissions() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.submissions().click();
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void profile() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.profile().click();
    assertEquals(viewUrl(ProfileView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("proview@ircm.qc.ca")
  public void users() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.users().click();
    assertEquals(viewUrl(UsersView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void exitSwitchUser() throws Throwable {
    openView(UsersView.VIEW_NAME);
    SigninViewElement signinView = $(SigninViewElement.class).id(SigninView.ID);
    signinView.getUsernameField().setValue("proview@ircm.qc.ca");
    signinView.getPasswordField().setValue("password");
    signinView.getSubmitButton().click();
    UsersViewElement usersView = $(UsersViewElement.class).id(UsersView.ID);
    usersView.clickUser(1);
    usersView.clickSwitchUser();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.exitSwitchUser().click();
    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
    assertFalse(optional(() -> view.exitSwitchUser()).isPresent());
  }

  @Test
  public void signout() throws Throwable {
    open();
    SigninViewElement signinView = $(SigninViewElement.class).id(SigninView.ID);
    signinView.getUsernameField().setValue("christopher.anderson@ircm.qc.ca");
    signinView.getPasswordField().setValue("password");
    signinView.getSubmitButton().click();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.signout().click();
    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void changeLanguage() throws Throwable {
    open();
    final Locale before = currentLocale();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.changeLanguage().click();
    assertEquals(viewUrl(ContactView.VIEW_NAME), getDriver().getCurrentUrl());
    assertEquals(ENGLISH.equals(before) ? FRENCH : ENGLISH, currentLocale());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void contact() throws Throwable {
    openView(GuidelinesView.VIEW_NAME);
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.contact().click();
    assertEquals(viewUrl(ContactView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void guidelines() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.guidelines().click();
    assertEquals(viewUrl(GuidelinesView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void add() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).id(SubmissionsView.ID);
    submissionsView.add().click();
    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.add()).isPresent());
    view.guidelines().click();
    assertEquals(viewUrl(GuidelinesView.VIEW_NAME), getDriver().getCurrentUrl());
    assertEquals("true", view.add().getAttribute("hidden"));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void edit() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).id(SubmissionsView.ID);
    submissionsView.doubleClickSubmission(0);
    SubmissionDialogElement submissionDialog =
        $(SubmissionDialogElement.class).id(SubmissionDialog.ID);
    submissionDialog.clickEdit();
    assertEquals(viewUrl(SubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.edit()).isPresent());
    view.guidelines().click();
    assertEquals(viewUrl(GuidelinesView.VIEW_NAME), getDriver().getCurrentUrl());
    assertEquals("true", view.edit().getAttribute("hidden"));
  }

  @Test
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void print() throws Throwable {
    open();
    ViewLayoutElement view = $(ViewLayoutElement.class).id(ID);
    view.submissions().click();
    SubmissionsViewElement submissionsView = $(SubmissionsViewElement.class).id(SubmissionsView.ID);
    submissionsView.doubleClickSubmission(0);
    SubmissionDialogElement submissionDialog =
        $(SubmissionDialogElement.class).id(SubmissionDialog.ID);
    submissionDialog.clickPrint();
    assertEquals(viewUrl(PrintSubmissionView.VIEW_NAME, "164"), getDriver().getCurrentUrl());
    assertTrue(optional(() -> view.print()).isPresent());
    view.guidelines().click();
    assertEquals(viewUrl(GuidelinesView.VIEW_NAME), getDriver().getCurrentUrl());
    assertEquals("true", view.print().getAttribute("hidden"));
  }
}
