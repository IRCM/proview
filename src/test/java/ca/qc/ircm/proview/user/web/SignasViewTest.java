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

import static ca.qc.ircm.proview.user.web.ValidateViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.submission.web.SubmissionsView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.WindowElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject
public class SignasViewTest extends SignasPageObject {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SignasViewTest.class);
  @PersistenceContext
  private EntityManager entityManager;
  @Value("${spring.application.name}")
  private String applicationName;

  @Test
  @WithSubject(anonymous = true)
  public void security_Anonymous() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 10L)
  public void security_RegularUser() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 3L)
  public void security_Manager() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  public void title() throws Throwable {
    open();

    assertTrue(resources(SignasView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> usersGrid()).isPresent());
  }

  @Test
  public void defaultUsers() throws Throwable {
    open();

    List<String> emails = getUserEmails();

    assertEquals(4, emails.size());
    assertTrue(emails.contains("benoit.coulombe@ircm.qc.ca"));
    assertTrue(emails.contains("christopher.anderson@ircm.qc.ca"));
    assertTrue(emails.contains("patricia.jones@ircm.qc.ca"));
    assertTrue(emails.contains("lucas.martin@ircm.qc.ca"));
  }

  @Test
  public void viewUser() throws Throwable {
    open();
    String email = "christopher.anderson@ircm.qc.ca";
    String name = "Christopher Anderson";

    clickViewUser(email);

    assertNotNull(findElement(className(UserWindow.WINDOW_STYLE)));
    WindowElement userWindow =
        wrap(WindowElement.class, findElement(className(UserWindow.WINDOW_STYLE)));
    assertTrue(resources(UserWindow.class).message(UserWindow.TITLE, name)
        .contains(userWindow.getCaption()));
    assertNotNull(userWindow.findElement(className(UserFormPresenter.USER)));
  }

  @Test
  public void signas() throws Throwable {
    open();
    String email = "christopher.anderson@ircm.qc.ca";

    clickSignas(email);

    assertEquals(viewUrl(SubmissionsView.VIEW_NAME), getDriver().getCurrentUrl());
    assertTrue(optional(() -> managerMenuItem()).isPresent());
    clickManager();
    assertTrue(optional(() -> stopSignasMenuItem()).isPresent());
  }
}
