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

package ca.qc.ircm.proview.user.web;

import static ca.qc.ircm.proview.Constants.APPLICATION_NAME;
import static ca.qc.ircm.proview.Constants.TITLE;
import static ca.qc.ircm.proview.user.web.UsersView.VIEW_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.qc.ircm.proview.Constants;
import ca.qc.ircm.proview.security.web.AccessDeniedViewElement;
import ca.qc.ircm.proview.submission.web.SubmissionsViewElement;
import ca.qc.ircm.proview.test.config.AbstractTestBenchTestCase;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.web.SigninViewElement;
import ca.qc.ircm.proview.web.ViewLayoutElement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

/**
 * Integration tests for {@link UsersView}.
 */
@TestBenchTestAnnotations
@WithUserDetails("proview@ircm.qc.ca")
public class UsersViewItTest extends AbstractTestBenchTestCase {
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
  @WithUserDetails("christopher.anderson@ircm.qc.ca")
  public void security_User() throws Throwable {
    open();

    $(AccessDeniedViewElement.class).waitForFirst();
  }

  @Test
  @WithUserDetails("benoit.coulombe@ircm.qc.ca")
  public void security_Manager() throws Throwable {
    open();

    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  public void security_Admin() throws Throwable {
    open();

    $(UsersViewElement.class).waitForFirst();
  }

  @Test
  public void title() throws Throwable {
    open();

    assertEquals(resources(UsersView.class).message(TITLE,
        resources(Constants.class).message(APPLICATION_NAME)), getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    assertTrue(optional(() -> view.header()).isPresent());
    assertTrue(optional(() -> view.users()).isPresent());
    assertFalse(optional(() -> view.switchFailed()).isPresent());
    assertTrue(optional(() -> view.add()).isPresent());
    assertTrue(optional(() -> view.switchUser()).isPresent());
  }

  @Test
  public void edit() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();

    view.users().edit(0).click();

    assertTrue(view.dialog().isOpen());
  }

  @Test
  public void add() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();

    view.add().click();

    assertTrue(view.dialog().isOpen());
  }

  @Test
  public void switchUser() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(4);

    view.switchUser().click();

    $(SubmissionsViewElement.class).waitForFirst();
    ViewLayoutElement viewLayout = $(ViewLayoutElement.class).waitForFirst();
    assertTrue(optional(() -> viewLayout.exitSwitchUser()).isPresent());
    assertFalse(optional(() -> viewLayout.users()).isPresent());
  }

  @Test
  @Disabled("Admins are allowed to switch to another admin right now")
  public void switchUser_Fail() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);

    view.switchUser().click();

    assertTrue(optional(() -> view.switchFailed()).isPresent());
  }

  @Test
  public void view_Laboratory() throws Throwable {
    open();
    UsersViewElement view = $(UsersViewElement.class).waitForFirst();
    view.users().select(0);
    view.viewLaboratory().click();
    assertTrue(view.laboratoryDialog().isOpen());
  }
}
