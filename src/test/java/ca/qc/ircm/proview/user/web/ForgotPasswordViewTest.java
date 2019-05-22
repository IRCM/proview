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

import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.SAVED;
import static ca.qc.ircm.proview.user.web.ForgotPasswordViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.UserRepository;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
public class ForgotPasswordViewTest extends ForgotPasswordPageObject {
  @Inject
  private UserRepository userRepository;
  @Inject
  private PasswordEncoder passwordEncoder;
  @Value("${spring.application.name}")
  private String applicationName;
  private String password = "unittestpassword";

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(ForgotPasswordView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> menu()).isPresent());
    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> passwordPanel()).isPresent());
    assertTrue(optional(() -> passwordField()).isPresent());
    assertTrue(optional(() -> confirmPasswordField()).isPresent());
    assertTrue(optional(() -> saveButton()).isPresent());
  }

  private void setFields() {
    setPassword(password);
    setConfirmPassword(password);
  }

  @Test
  public void save_Error() throws Throwable {
    open();

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void save() throws Throwable {
    open();
    setFields();

    clickSave();

    assertEquals(viewUrl(SigninView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = userRepository.findOne(10L);
    assertEquals(passwordEncoder.encode(password), user.getHashedPassword());
    assertNull(user.getSalt());
    assertNull(user.getPasswordVersion());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertEquals(resources(ForgotPasswordView.class).message(SAVED), notification.getCaption());
  }
}
