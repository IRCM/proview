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

package ca.qc.ircm.proview.user.web.integration;

import static ca.qc.ircm.proview.user.QUser.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ValidateView;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class ValidateViewTest extends ValidatePageObject {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ValidateViewTest.class);
  @PersistenceContext
  private EntityManager entityManager;

  private User getUser(String email) {
    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    return query.fetchOne();
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(ValidateView.class).message("title").contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertNotNull(headerLabel());
    assertNotNull(usersGrid());
    assertNotNull(validateSelectedButton());
  }

  @Test
  public void defaultUsers() throws Throwable {
    open();

    List<String> emails = getUserEmails();

    assertEquals(2, emails.size());
    assertTrue(emails.contains("francois.robert@ircm.qc.ca"));
    assertTrue(emails.contains("michel.tremblay@ircm.qc.ca"));
  }

  @Test
  public void validate() throws Throwable {
    open();
    String email = "francois.robert@ircm.qc.ca";

    clickValidateUser(email);

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = getUser(email);
    assertNotNull(user);
    assertEquals(email, user.getEmail());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isActive());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(email));
  }

  @Test
  public void validateSelected_Error() throws Throwable {
    open();

    clickValidateSelected();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals(Notification.Type.ERROR_MESSAGE.getStyle(), notification.getType());
    assertNotNull(notification.getCaption());
  }

  @Test
  public void validateSelected_One() throws Throwable {
    open();
    String email = "francois.robert@ircm.qc.ca";
    selectUsers(email);

    clickValidateSelected();

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
    User user = getUser(email);
    assertNotNull(user);
    assertEquals(email, user.getEmail());
    assertEquals(true, user.isValid());
    assertEquals(true, user.isActive());
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    waitForNotificationCaption(notification);
    assertNotNull(notification.getCaption());
    assertTrue(notification.getCaption().contains(email));
  }

  @Test
  public void validateSelected_Many() throws Throwable {
    open();
    String[] emails = new String[] { "francois.robert@ircm.qc.ca", "michel.tremblay@ircm.qc.ca" };
    selectUsers(emails);

    clickValidateSelected();

    assertEquals(viewUrl(ValidateView.VIEW_NAME), getDriver().getCurrentUrl());
    for (String email : emails) {
      User user = getUser(email);
      assertNotNull(user);
      assertEquals(email, user.getEmail());
      assertEquals(true, user.isValid());
      assertEquals(true, user.isActive());
    }
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    waitForNotificationCaption(notification);
    assertNotNull(notification.getCaption());
    for (String email : emails) {
      assertTrue(notification.getCaption().contains(email));
    }
  }
}
