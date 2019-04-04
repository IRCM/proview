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

package ca.qc.ircm.proview.submission.web;

import static ca.qc.ircm.proview.submission.web.SubmissionFormPresenter.SERVICE;
import static ca.qc.ircm.proview.submission.web.SubmissionHistoryFormPresenter.SAMPLES_PANEL;
import static ca.qc.ircm.proview.submission.web.SubmissionViewPresenter.TITLE;
import static ca.qc.ircm.proview.web.HelpWindow.WINDOW_STYLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.By.className;

import ca.qc.ircm.proview.sample.web.SampleStatusView;
import ca.qc.ircm.proview.security.web.AccessDeniedView;
import ca.qc.ircm.proview.test.config.TestBenchTestAnnotations;
import ca.qc.ircm.proview.test.config.WithSubject;
import ca.qc.ircm.proview.web.ContactView;
import ca.qc.ircm.utils.MessageResource;
import com.vaadin.testbench.elements.WindowElement;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestBenchTestAnnotations
@WithSubject(userId = 10)
public class SubmissionsViewTest extends SubmissionsViewPageObject {
  @Value("${spring.application.name}")
  private String applicationName;
  private boolean admin;
  private boolean manager;

  @Override
  protected boolean isAdmin() {
    return admin;
  }

  @Override
  protected boolean isManager() {
    return manager;
  }

  @Before
  public void beforeTest() {
    admin = false;
  }

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
  public void title() throws Throwable {
    open();

    assertTrue(resources(SubmissionsView.class).message(TITLE, applicationName)
        .contains(getDriver().getTitle()));
  }

  @Test
  public void fieldsExistence_User() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> help()).isPresent());
    assertTrue(optional(() -> submissionsGrid()).isPresent());
    assertTrue(optional(() -> addSubmissionButton()).isPresent());
    assertFalse(optional(() -> updateStatusButton()).isPresent());
  }

  @Test
  @WithSubject
  public void fieldsExistence_Admin() throws Throwable {
    open();

    assertTrue(optional(() -> header()).isPresent());
    assertTrue(optional(() -> help()).isPresent());
    assertTrue(optional(() -> submissionsGrid()).isPresent());
    assertFalse(optional(() -> addSubmissionButton()).isPresent());
    assertTrue(optional(() -> updateStatusButton()).isPresent());
  }

  @Test
  public void helpWindow() throws Throwable {
    open();

    clickHelp();

    assertTrue(optional(() -> findElement(className(WINDOW_STYLE))).isPresent());
  }

  @Test
  public void viewSubmission() throws Throwable {
    open();

    clickViewSubmissionByRow(2);

    assertNotNull(findElement(className(SubmissionWindowPresenter.WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(SubmissionWindowPresenter.WINDOW_STYLE)));
    String experiment = experimentByRow(2);
    assertTrue(
        resources(SubmissionWindow.class).message(SubmissionWindowPresenter.TITLE, experiment)
            .contains(submissionWindow.getCaption()));
    assertTrue(optional(() -> submissionWindow.findElement(className(SERVICE))).isPresent());
  }

  @Test
  @WithSubject
  public void viewSubmissionHistory() throws Throwable {
    admin = true;
    open();

    clickViewSubmissionHistoryByRow(4);

    assertNotNull(findElement(className(SubmissionHistoryWindow.WINDOW_STYLE)));
    WindowElement submissionWindow =
        wrap(WindowElement.class, findElement(className(SubmissionHistoryWindow.WINDOW_STYLE)));
    String experiment = experimentByRow(4);
    assertTrue(
        resources(SubmissionHistoryWindow.class).message(SubmissionHistoryWindow.TITLE, experiment)
            .contains(submissionWindow.getCaption()));
    assertTrue(optional(() -> submissionWindow.findElement(className(SAMPLES_PANEL))).isPresent());
  }

  @Test
  public void addSubmission() throws Throwable {
    open();

    clickAddSubmissionButton();

    assertEquals(viewUrl(SubmissionView.VIEW_NAME), getDriver().getCurrentUrl());
  }

  @Test
  @WithSubject
  public void updateStatus() throws Throwable {
    admin = true;
    open();
    selectSubmission(5);

    clickUpdateStatusButton();

    assertEquals(viewUrl(SampleStatusView.VIEW_NAME), getDriver().getCurrentUrl());
  }
}
