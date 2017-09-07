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

import static ca.qc.ircm.proview.web.MainViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.web.RegisterView;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class MainViewTest extends MainPageObject {
  @Value("${spring.application.name}")
  private String applicationName;

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(
        resources(MainView.class).message(TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertNotNull(header());
    assertNotNull(signPanel());
    assertNotNull(signUsernameField());
    assertNotNull(signPasswordField());
    assertNotNull(signButton());
    assertNotNull(forgotPasswordPanel());
    assertNotNull(forgotPasswordEmailField());
    assertNotNull(forgotPasswordButton());
    assertNotNull(registerButton());
  }

  @Test
  public void sign_Error() throws Throwable {
    open();
    setSignUsername("unit.test@ircm.qc.ca");
    setSignPassword("password");

    clickSignButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  @Ignore("not programmed yet")
  public void sign_Admin() throws Throwable {
    open();
    setSignUsername("proview@ircm.qc.ca");
    setSignPassword("password");

    clickSignButton();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void sign_User() throws Throwable {
    open();
    setSignUsername("benoit.coulombe@ircm.qc.ca");
    setSignPassword("password");

    clickSignButton();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void forgotPassword_Error() throws Throwable {
    open();
    setForgotPasswordEmail("unit.test@ircm.qc.ca");

    clickForgotPasswordButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void forgotPassword() throws Throwable {
    open();
    setForgotPasswordEmail("benoit.coulombe@ircm.qc.ca");

    clickForgotPasswordButton();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.WARNING_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void register() throws Throwable {
    open();

    clickRegisterButton();

    assertEquals(viewUrl(RegisterView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  public void enter_NotSigned() throws Throwable {
    open();

    assertEquals(viewUrl(MainView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject(userId = 10)
  public void enter_User() throws Throwable {
    open();

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}