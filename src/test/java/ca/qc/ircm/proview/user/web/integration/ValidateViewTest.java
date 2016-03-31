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

import ca.qc.ircm.proview.test.config.IntegrationTestDatabaseRule;
import ca.qc.ircm.proview.test.config.Rules;
import ca.qc.ircm.proview.test.config.Slow;
import ca.qc.ircm.proview.test.config.TestBenchLicenseRunner;
import ca.qc.ircm.proview.test.config.TestBenchRule;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.user.User;
import ca.qc.ircm.proview.user.web.ValidateView;
import ca.qc.ircm.utils.MessageResource;
import com.querydsl.jpa.impl.JPAQuery;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.ui.Notification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.EntityManager;

@RunWith(TestBenchLicenseRunner.class)
@Slow
@WithSubject
public class ValidateViewTest extends ValidatePageObject {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ValidateViewTest.class);
  public TestBenchRule testBenchRule = new TestBenchRule(this);
  public IntegrationTestDatabaseRule integrationTestDatabaseRule =
      new IntegrationTestDatabaseRule();
  @Rule
  public RuleChain rules =
      Rules.defaultRules(this).around(testBenchRule).around(integrationTestDatabaseRule);
  private EntityManager entityManager;

  @Before
  public void beforeTest() throws Throwable {
    entityManager = integrationTestDatabaseRule.getEntityManager();
  }

  @Override
  protected String getBaseUrl() {
    return testBenchRule.getBaseUrl();
  }

  private User getUser(String email) {
    JPAQuery<User> query = new JPAQuery<>(entityManager);
    query.from(user);
    query.where(user.email.eq(email));
    return query.fetchOne();
  }

  @Test
  public void title() throws Throwable {
    open();

    Set<Locale> locales = Rules.getLocales();
    Set<String> titles = new HashSet<>();
    for (Locale locale : locales) {
      titles.add(new MessageResource(ValidateView.class, locale).message("title"));
    }
    assertTrue(titles.contains(getDriver().getTitle()));
  }

  @Test
  public void fieldPositions() throws Throwable {
    open();

    int previous = 0;
    int current;
    current = headerLabel().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = usersGrid().getLocation().y;
    assertTrue(previous < current);
    previous = current;
    current = validateSelectedButton().getLocation().y;
    assertTrue(previous < current);
  }

  @Test
  public void defaultUsers() throws Throwable {
    open();

    List<String> emails = getUserEmails();

    assertEquals(2, emails.size());
    assertTrue(emails.contains("robert.stlouis@ircm.qc.ca"));
    assertTrue(emails.contains("nicole.francis@ircm.qc.ca"));
  }

  @Test
  public void validate() throws Throwable {
    open();
    String email = "robert.stlouis@ircm.qc.ca";

    clickValidateUser(email);

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + ValidateView.VIEW_NAME,
        getDriver().getCurrentUrl());
    entityManager.getTransaction().commit();
    entityManager.getTransaction().begin();
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
    String email = "robert.stlouis@ircm.qc.ca";
    selectUsers(email);

    clickValidateSelected();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + ValidateView.VIEW_NAME,
        getDriver().getCurrentUrl());
    entityManager.getTransaction().commit();
    entityManager.getTransaction().begin();
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
  public void validateSelected_Many() throws Throwable {
    open();
    String[] emails = new String[] { "robert.stlouis@ircm.qc.ca", "nicole.francis@ircm.qc.ca" };
    selectUsers(emails);

    clickValidateSelected();

    assertEquals(testBenchRule.getBaseUrl() + "/#!" + ValidateView.VIEW_NAME,
        getDriver().getCurrentUrl());
    entityManager.getTransaction().commit();
    entityManager.getTransaction().begin();
    for (String email : emails) {
      User user = getUser(email);
      assertNotNull(user);
      assertEquals(email, user.getEmail());
      assertEquals(true, user.isValid());
      assertEquals(true, user.isActive());
    }
    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    for (String email : emails) {
      assertTrue(notification.getCaption().contains(email));
    }
  }
}
