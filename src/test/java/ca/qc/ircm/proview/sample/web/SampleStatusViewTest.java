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

package ca.qc.ircm.proview.sample.web;

import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.SAVE;
import static ca.qc.ircm.proview.sample.web.SampleStatusViewPresenter.TITLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ca.qc.ircm.proview.sample.SampleStatus;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.submission.Submission;
import ca.qc.ircm.proview.submission.SubmissionRepository;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.NotificationElement;
import java.util.Locale;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 1)
public class SampleStatusViewTest extends SampleStatusPageObject {
  @Inject
  private SubmissionRepository submissionRepository;
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
  @WithSubject(userId = 10)
  public void security_RegularUser() throws Throwable {
    openView(ContactView.VIEW_NAME);
    Locale locale = currentLocale();

    open();

    assertTrue(new MessageResource(AccessDeniedView.class, locale)
        .message(AccessDeniedView.TITLE, applicationName).contains(getDriver().getTitle()));
  }

  @Test
  @WithSubject(userId = 3)
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

    assertEquals(resources(SampleStatusView.class).message(TITLE, applicationName),
        getDriver().getTitle());
  }

  @Test
  public void fieldsExistence() throws Throwable {
    open();

    assertTrue(optional(() -> headerLabel()).isPresent());
    assertTrue(optional(() -> samplesGrid()).isPresent());
    assertTrue(optional(() -> saveButton()).isPresent());
  }

  @Test
  public void save_Regress() throws Throwable {
    openWithSubmissions();
    setSampleStatus(0, SampleStatus.WAITING);

    clickSave();

    assertTrue(optional(() -> confirmWindow()).isPresent());
  }

  @Test
  public void save() throws Throwable {
    openWithSubmissions();
    setSampleStatus(0, SampleStatus.ANALYSED);
    setSampleStatus(1, SampleStatus.CANCELLED);

    clickSave();

    NotificationElement notification = $(NotificationElement.class).first();
    assertEquals("tray_notification", notification.getType());
    assertNotNull(notification.getCaption());
    assertEquals(resources(SampleStatusView.class).message(SAVE + ".done", 2),
        notification.getCaption());
    Submission submission = submissionRepository.findById(32L).orElse(null);
    assertEquals(SampleStatus.ANALYSED, submission.getSamples().get(0).getStatus());
    submission = submissionRepository.findById(147L).orElse(null);
    assertEquals(SampleStatus.CANCELLED, submission.getSamples().get(0).getStatus());
  }
}
